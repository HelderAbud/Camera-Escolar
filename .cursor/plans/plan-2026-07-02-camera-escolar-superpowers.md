# Plano - Camera Escolar Superpowers

Data: 2026-07-02

## Triagem

Classificacao: `Normal`.

Motivo: a tarefa cria base operacional persistente para o projeto, sem alterar codigo da aplicacao, banco, endpoints ou testes.

## Objetivo

Organizar o FaceLogAI para trabalhar com Helder Method v1.2 e Superpowers Cursor Playbook, deixando contexto, regras e fluxo de planos disponiveis dentro do repositorio.

## Escopo

Arquivos previstos:

- `AGENTS.md`
- `.cursor/rules/helder-method.mdc`
- `.cursor/rules/backend-java-spring.mdc`
- `.cursor/rules/testing.mdc`
- `.cursor/rules/security-school-data.mdc`
- `.cursor/plans/README.md`
- `.cursor/plans/plan-2026-07-02-camera-escolar-superpowers.md`

## Fora De Escopo

- Alterar `src/`, `pom.xml`, migrations, testes ou configuracoes da aplicacao.
- Rodar migrations, modificar banco ou Docker.
- Apagar artefatos existentes.
- Commit, push ou PR.

## Passos

1. Criar estrutura `.cursor/rules/` e `.cursor/plans/`.
2. Criar `AGENTS.md` com contexto operacional do FaceLogAI.
3. Criar regras persistentes para metodo, backend, testes e seguranca escolar.
4. Criar README da pasta de planos.
5. Registrar este plano no repositorio.
6. Validar diff e escopo.

## Validacao

- Conferir que os arquivos existem nos caminhos esperados.
- Conferir frontmatter das regras `.mdc`.
- Rodar `git status --short`.
- Revisar `git diff -- AGENTS.md .cursor`.
- Confirmar que nao houve alteracao em codigo, migrations, `pom.xml` ou testes.

## Criterio De Pronto

A base Superpowers/Helder estara pronta quando o projeto tiver `AGENTS.md`, regras persistentes, pasta de planos documentada e diff limitado aos artefatos operacionais aprovados.
