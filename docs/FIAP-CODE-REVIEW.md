# FIAP — IA no Pipeline: Code Review

Laboratório da disciplina AI-Assisted Development, com entrega em 03/11/2026.
Fonte: [enunciado do professor](https://github.com/lgsreal/ai-driven-dev/blob/main/Aula_3/04_Laboratorio_Pratico_AI_Code_Reviewer.md).

## Entrega

O entregável é o link de um pull request **neste fork**, da branch
`feature/search-update` para `main`, com o comentário real do agente de revisão.
A vulnerabilidade é proposital para avaliação: **não fazer merge nem publicar a aplicação**.

## Pipeline

- Testes Maven em Java 25, separados do job que recebe a chave da IA.
- Revisor `anc95/ChatGPT-CodeReview`, como no roteiro, fixado em commit imutável.
- Gemini 3.5 Flash-Lite pela API compatível com OpenAI; o modelo é Gemini apesar do
  nome da action e do título padrão “Code review by ChatGPT” que ela publica.
- Chave somente no secret `GEMINI_API_KEY`. Usar um projeto sem faturamento;
  assinatura ChatGPT/Claude não substitui crédito ou acesso à API.
- Revisão apenas de Java em `src/main`, limitada a patches de 16.000 caracteres.
  A cobertura é parcial se algum arquivo ultrapassar esse limite.
- Revisão disparada ao abrir/atualizar o PR. Para repetir uma execução após
  cadastrar o secret, usar “Re-run failed jobs” no GitHub Actions.
- A action de revisão não faz checkout nem executa código do PR. Segredo restrito
  a PRs do dono no próprio fork; branches de terceiros não recebem revisão com a chave.
- Não há merge automático nem regra de proteção instalada: comentários não bloqueiam
  integração por si só. O PR de laboratório deve permanecer aberto.

## Adaptação do exercício

O GNews original armazena artigos em memória, sem JDBC. A demonstração adiciona
um adaptador experimental de busca JDBC isolado e testes com banco H2 em memória.
Isso permite reproduzir a injeção sem criar serviço público vulnerável nem mudar
o endpoint original. O H2 é dependência apenas de teste.

## Critério de pronto

- [x] PR no próprio fork com SQL Injection reproduzível: [PR #1](https://github.com/viiihbastos/gnews/pull/1).
- [x] Testes Java 25 aprovados localmente e no GitHub Actions: 4 testes, zero falhas (`mvn verify`).
- [x] Comentário real do bot com análise da falha.
- [x] Análise crítica: precisão, explicação, correção sugerida e falsos positivos.
- [ ] Link do PR entregue no portal FIAP por Victor.

Registrar os links e resultados reais após a execução; não tratar ausência de
comentário ou falha de autenticação como revisão aprovada.

## Evidência técnica já obtida

`JdbcArticleSearchTest` usa H2 em memória com somente dois registros sintéticos.
Entrada normal `Java 25`: um resultado. Entrada inexistente: nenhum resultado.
Entrada `' OR '1'='1`: ambos os resultados; a entrada alterou a estrutura da consulta.
O controle com `PreparedStatement` e `setString(1, payload)` retorna zero linhas,
pois trata o payload como valor, sem interpretar sua sintaxe SQL.

Esses testes caracterizam a falha intencional; testes verdes **não significam**
que a branch é segura. A classe não está conectada ao endpoint HTTP. O revisor
deve identificar o risco no adaptador sem afirmar que há exploração remota ativa.

## Resultado da revisão automática — 03/11/2026

- [PR de entrega](https://github.com/viiihbastos/gnews/pull/1).
- [Comentário inline do agente](https://github.com/viiihbastos/gnews/pull/1#discussion_r4455127798).
- [Revisão publicada pelo github-actions[bot]](https://github.com/viiihbastos/gnews/pull/1#pullrequestreview-5752769116).
- [Execução com revisão e testes aprovados](https://github.com/viiihbastos/gnews/actions/runs/41642188072).
- Commit de código analisado: `a768aaab16c77a95f4dd4649cd8a643e5648ace5`.
- Modelo efetivo: `gemini-3.5-flash-lite`; projeto Google Free tier, sem faturamento vinculado.
- Resultado: um comentário com identificação de SQL Injection e exemplo corrigido.

### Precisão

A IA identificou corretamente a concatenação de `userInput` na consulta executada
por `Statement`. O teste com H2 comprova que o payload altera a condição de busca e
retorna ambos os artigos. Portanto, o achado central é um verdadeiro positivo.
O teste adicional valida que uma conexão ausente é rejeitada no construtor.

### Didática e correção

O comentário descreve causa e risco, reproduz a linha vulnerável e mostra uma
correção completa com `connection.prepareStatement(query)`, placeholder `?` e
`setString(1, userInput)`, preservando o fechamento de recursos. O controle
parametrizado do teste confirma que esse mecanismo trata o payload como texto.

A action posicionou o comentário no final do hunk do arquivo adicionado, e não
exatamente na linha da concatenação. O trecho vulnerável está identificado no
texto, mas a localização inline poderia ser mais precisa.

### Falsos positivos e calibração de impacto

Não houve outro achado além da injeção. A IA reconheceu que a exposição a dados de
usuário é condicional; o adaptador continua sem conexão ao endpoint HTTP.
Classificou a vulnerabilidade como crítica e citou leitura, modificação e exclusão
de dados. Neste experimento foi comprovada **apenas a leitura de linhas indevidas**.
Escrita/exclusão dependeriam do banco, das permissões e do suporte a comandos
adicionais; não foram testadas. Logo, a falha é real, mas a gravidade operacional
e todos os impactos mencionados não podem ser inferidos deste exemplo isolado.

### Limites e problemas encontrados

- Só o diff de Java em `src/main` foi enviado. Não houve auditoria integral do
  repositório, teste de exploração remota ou medição de precisão em vários casos.
- A action lê arquivos alterados desde a última revisão/atualização. Uma execução
  anterior, restrita a documentação, terminou verde sem comentário: foi necessário
  conferir o comentário real, não apenas o status do workflow.
- A primeira chamada com `gemini-2.5-flash` retornou HTTP 404 no projeto novo.
  A [documentação de descontinuação](https://aistudio.google.com/docs/deprecations)
  limita esse modelo a usuários anteriores. Foi substituído por uma versão estável
  atual, [Gemini 3.5 Flash-Lite](https://aistudio.google.com/docs/models/gemini-3.5-flash-lite),
  com [faixa gratuita documentada](https://aistudio.google.com/docs/pricing#gemini-3.5-flash-lite).
- Falta de secret e indisponibilidade do modelo foram falhas de infraestrutura,
  não evidências de que a IA aprovou ou deixou passar a vulnerabilidade.
- O desafio extra de prompt injection é opcional e não foi executado. Há instrução
  defensiva no prompt, mas sua eficácia não foi comprovada por este teste.
- O review é do tipo comentário: nenhuma regra de proteção de branch foi instalada.
  Revisão humana continua necessária; não há integração ou implantação automática.

## Entrega no portal

O requisito técnico foi obtido. Revisar este material e enviar **somente o link do
PR** na atividade IA no Pipeline - Code Review (ID 63653), com prazo 03/11/2026.
O horário limite não estava informado na consulta ao portal. Não houve submissão
acadêmica automática. Manter este PR aberto, com a falha proposital, como evidência.
