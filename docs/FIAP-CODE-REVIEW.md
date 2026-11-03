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
- [x] Testes locais Java 25 aprovados: 3 testes, zero falhas (`mvn verify`).
- [ ] Comentário real do bot com análise da falha.
- [ ] Análise crítica: precisão, explicação, correção sugerida e falsos positivos.
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

## Análise crítica — preencher após o bot

- Precisão: pendente de comentário real do agente.
- Didática: conferir se explica a concatenação e sugere `PreparedStatement`.
- Falsos positivos: conferir se evita afirmar endpoint exposto ou dados reais.
- Limitações: apenas diff Java de produção; testes e documentação não entram na
  chamada da IA. Uma detecção correta neste exemplo não mede cobertura geral.
- Prompt injection extra: não executado; é desafio opcional do enunciado.
- Se a revisão falhar: distinguir credencial/quota/formato de resposta de erro
  de análise; não apresentar erro de infraestrutura como aprovação de segurança.

O projeto Google `FIAP Code Review` foi criado na faixa gratuita, sem vincular
faturamento. A criação da chave e seu cadastro no GitHub aguardam confirmação.
Não houve submissão no portal FIAP.
