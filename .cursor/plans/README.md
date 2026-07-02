# Planos Aprovados - FaceLogAI

Use esta pasta para registrar planos antes de executar tarefas relevantes no projeto.

## Quando Criar Plano

Crie um plano para:

- feature nova;
- bugfix com risco ou causa incerta;
- mudanca em endpoint, contrato REST ou Swagger;
- mudanca em banco ou migration Flyway;
- mudanca em autenticacao/autorizacao;
- qualquer trabalho com dados escolares, imagem, video, camera real ou integracao externa;
- refatoracao que toque mais de uma area.

Mudancas `Simple` podem usar fluxo curto, desde que sejam pequenas, localizadas e sem contrato novo.

## Padrao De Nome

Use:

```text
plan-YYYY-MM-DD-assunto.md
```

Exemplos:

```text
plan-2026-07-02-camera-escolar-superpowers.md
plan-2026-07-03-eventos-monitoramento-filtros.md
plan-2026-07-04-hardening-jwt.md
```

## Estrutura Minima

Cada plano deve responder:

- classificacao: `Simple`, `Normal`, `Complex` ou `Hotfix`;
- objetivo da fatia;
- arquivos ou modulos impactados;
- fora de escopo;
- riscos e gates humanos;
- passos pequenos de execucao;
- validacao esperada;
- criterio de pronto.

## Regra De Execucao

Depois de aprovado, siga o plano em passos pequenos. Se a realidade divergir do plano, pare e atualize o plano ou peca realinhamento antes de continuar.
