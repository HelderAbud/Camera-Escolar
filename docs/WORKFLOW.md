# Workflow — Como trabalhar no FaceLogAI

## 0) Regra de ouro
Pequeno, testável e sempre com build passando.

## 1) Escolha uma tarefa
- Abra `docs/BACKLOG.md`.
- Pegue a próxima tarefa (ex.: **B0-01**).
- Confirme o objetivo e o impacto.

## 2) Crie uma branch (se estiver usando GitFlow)
```bash
git checkout main
git pull
git checkout -b feature/B0-01-setup-backend
```

## 3) Implemente com apoio do Cursor
- Abra os arquivos de contexto (este `WORKFLOW.md` e o `BACKLOG.md`).
- Peça para o Cursor implementar **apenas a próxima tarefa**, mantendo o escopo pequeno.

## 4) Valide localmente
Backend:
```bash
mvn clean test
mvn spring-boot:run
```

## 5) PR (quando estiver usando repositório remoto)
- Crie um Pull Request descrevendo:
  - O que mudou
  - Como testar
  - Riscos / pontos de atenção

## 6) Finalizar
- Build/testes verdes.
- Checklist do item do backlog validado.
- Merge e escolha o próximo item em `docs/BACKLOG.md`.

