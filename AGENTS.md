# AGENTS.md - FaceLogAI

Base operacional alinhada ao Rheyder Method v1.2 e ao Superpowers Cursor Playbook: triagem por risco, plano antes de tarefas relevantes, fatias pequenas, validacao objetiva e gate humano em seguranca, dados escolares e mudancas de contrato.

## Visao Geral

- Produto: FaceLogAI, API backend para apoio a monitoramento escolar e organizacao de dados de escolas, turmas, alunos, cameras e eventos.
- Objetivo atual: manter um MVP backend profissional para portfolio, com autenticacao por perfis e base para evoluir monitoramento, eventos e presenca.
- Stack: Java 21, Spring Boot 3.2, Maven, Spring Web, Spring Security, JWT, Spring Data JPA, Flyway, MySQL em desenvolvimento, H2 em testes e springdoc OpenAPI.
- Arquitetura atual: camadas `controller`, `service`, `repository`, `domain`, `dto`, `config` e `api`.
- Repositorio publico declarado no README: `https://github.com/HelderAbud/Camera-Escolar`.

## Comandos

| Objetivo | Comando |
|----------|---------|
| Subir MySQL local | `docker compose up -d` |
| Rodar testes | `mvn clean test` |
| Subir API | `mvn spring-boot:run` |
| Swagger local | `http://localhost:8080/swagger-ui.html` |
| Health local | `http://localhost:8080/health` |

Antes de considerar o servico pronto, valide pelo menos testes ou smoke check coerente com a mudanca.

## Triagem Rheyder

- `Simple`: ajuste pequeno, localizado, sem contrato publico novo e sem dado sensivel.
- `Normal`: documentacao relevante, regra de negocio, endpoint, fluxo, plano ou ambiguidade moderada.
- `Complex`: autenticacao/autorizacao, dados escolares sensiveis, imagem/video, integracao externa, banco, arquitetura ou alto custo de erro.
- `Hotfix`: operacao real quebrada; aplicar patch minimo e provar o sintoma corrigido.

Se durante a execucao aparecer risco maior, contrato novo, dado sensivel ou mudanca arquitetural, suba a trilha e pare para realinhar.

## Regras De Arquitetura

- Controllers devem ser finos: validar entrada, chamar service e devolver DTO/resposta HTTP.
- Regras de negocio ficam em `service` e modelo de dominio; repositorios ficam apenas para acesso a dados.
- Nao expor entidades JPA diretamente em novos contratos; preferir DTOs de request/response.
- Preservar contratos REST existentes salvo aprovacao explicita.
- Mudancas de schema devem usar nova migration Flyway em `src/main/resources/db/migration/`; nunca editar migration antiga ja aplicada.
- Endpoints novos ou alterados devem aparecer corretamente no Swagger via springdoc.
- Evitar refactors amplos sem necessidade direta para a tarefa.

## Testes E Validacao

- Nova regra de negocio pede teste unitario ou de integracao.
- Bugfix tecnico pede teste de regressao quando viavel.
- Mudanca em controller/security deve considerar teste de integracao com Spring Security.
- Mudanca em service deve considerar teste unitario com cenarios principais e erro.
- Nao concluir com testes quebrados; se nao puder rodar testes, explicar motivo e risco.

## Workflow

- Tarefa nao trivial: brainstorming curto, Plan Mode, plano aprovado e execucao em passos pequenos.
- Planos relevantes ficam em `.cursor/plans/` com nome `plan-YYYY-MM-DD-assunto.md`.
- Cada fatia deve entregar comportamento verificavel ou documentacao operacional clara.
- Antes de mudar API, banco, seguranca, imagem/video ou dados escolares, pedir aprovacao explicita.
- Antes de finalizar: revisar diff, escopo, dados sensiveis, aderencia ao plano e validacao executada.
- Nunca commitar, pushar ou abrir PR sem pedido explicito do usuario.

## Seguranca E Dados Escolares

- Tratar dados de alunos, usuarios, escolas, imagens, videos e eventos como sensiveis.
- Nao commitar `.env`, secrets JWT, dumps de banco, arquivos reais de alunos, imagens reais, videos reais, exports privados ou uploads sensiveis.
- Usar dados fake/seed de desenvolvimento em exemplos publicos.
- Nao enviar dados reais de criancas/adolescentes para IA; mascarar nomes, documentos, emails e identificadores quando necessario.
- Qualquer evolucao com reconhecimento facial, biometria, camera real, armazenamento de imagem/video ou integracao externa e trilha `Complex` com gate humano.

## Caminhos Importantes

| Caminho | Conteudo |
|---------|----------|
| `README.md` | Visao de produto, stack, como rodar e resumo para portfolio |
| `docs/BACKLOG.md` | Marcos e tarefas do MVP |
| `docs/WORKFLOW.md` | Fluxo atual de trabalho pequeno e testavel |
| `src/main/java/com/faceblogai/` | Codigo principal da API |
| `src/main/resources/db/migration/` | Migrations Flyway |
| `src/main/resources/application.properties` | Configuracao local da aplicacao |
| `src/test/` | Testes automatizados e dados de teste |
| `.cursor/rules/` | Regras persistentes do Cursor |
| `.cursor/plans/` | Planos aprovados por fatia |
