# Plano - Security Cleanup Credentials

Data: 2026-07-02

## Triagem

Classificacao: `Complex`.

Motivo: envolve autenticacao, senha publicada em repositorio publico, seed de usuario admin, testes de login e politica de dados sensiveis.

## Objetivo

Remover credenciais padrao publicadas do FaceLogAI, trocar o seed admin fixo por seed configuravel e revisar o repositorio para evitar que dados sensiveis locais sejam adicionados por acidente.

## Decisao

Editar a migration antiga `V2__seed_admin_usuario.sql` para remover a senha fixa do estado atual do GitHub.

Impacto: bancos locais que ja executaram a migration antiga podem precisar ser recriados, receber `flyway repair` ou ter a senha rotacionada manualmente.

## Escopo

- Remover senhas padrao publicadas e hashes conhecidos de docs/codigo quando forem credenciais reutilizaveis.
- Criar seed admin configuravel e desabilitado por padrao.
- Habilitar seed apenas em testes com senha test-only.
- Atualizar README e docs para nao publicar senha real/padrao.
- Inspecionar artefato local `tatus --short`.

## Fora De Escopo

- Reescrever historico Git remoto.
- Corrigir a pasta local nao rastreada `Gestao Financeira/`.
- Fazer commit, push ou PR.
- Criar deploy ou ambiente de producao.

## Passos

1. Atualizar teste de login para senha via configuracao.
2. Implementar seed admin configuravel.
3. Atualizar properties seguras.
4. Limpar migration `V2`.
5. Sanitizar docs.
6. Remover artefato acidental se confirmado.
7. Rodar varredura de seguranca.
8. Rodar testes Maven.
9. Revisar status e diff final.

## Validacao

- Varredura sem senhas padrao publicadas ou comentarios que revelem credenciais.
- Varredura de tokens/private keys sem achados.
- `mvn test` verde.
- Diff revisado sem dados sensiveis.

## Risco Residual

A senha antiga ja existiu no historico publico. Se algum ambiente real usou essa senha, ela deve ser trocada imediatamente fora do Git.
