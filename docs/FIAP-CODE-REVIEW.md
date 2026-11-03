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

O GNews original armazena artigos em memória, sem JDBC. A demonstração adicionará
um adaptador experimental de busca JDBC isolado e testes com banco H2 em memória.
Isso permite reproduzir a injeção sem criar serviço público vulnerável nem mudar
o endpoint original. O H2 será dependência apenas de teste.

## Critério de pronto

- [ ] PR no próprio fork com SQL Injection reproduzível.
- [ ] Testes Java 25 aprovados.
- [ ] Comentário real do bot com análise da falha.
- [ ] Análise crítica: precisão, explicação, correção sugerida e falsos positivos.
- [ ] Link do PR entregue no portal FIAP por Victor.

Registrar os links e resultados reais após a execução; não tratar ausência de
comentário ou falha de autenticação como revisão aprovada.
