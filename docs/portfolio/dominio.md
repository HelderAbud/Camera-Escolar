# Domínio — FaceLogAI

Modelo de domínio do MVP (persistência JPA + Flyway). Diagrama para portfólio / entrevista.

```mermaid
erDiagram
    Escola ||--o{ Turma : "tem"
    Escola ||--o{ Camera : "tem"
    Turma ||--o{ TurmaAluno : "vincula"
    Aluno ||--o{ TurmaAluno : "pertence"
    Camera ||--o{ CameraTurma : "cobre"
    Turma ||--o{ CameraTurma : "monitorada"
    Camera ||--o{ EventoMonitoramento : "gera"
    Turma ||--o| EventoMonitoramento : "opcional"
    Aluno ||--o| EventoMonitoramento : "opcional"

    Escola {
        Long id PK
        string nome
    }
    Turma {
        Long id PK
        Long escola_id FK
        string nome
        string serie
    }
    Aluno {
        Long id PK
        string nome
        string matricula
    }
    Camera {
        Long id PK
        Long escola_id FK
        string nome
        string endpoint_url
        boolean ativo
    }
    EventoMonitoramento {
        Long id PK
        Long camera_id FK
        Long turma_id FK
        Long aluno_id FK
        string tipo_evento
        string detalhes
    }
```

## Leitura rápida

| Relação | Significado |
|---------|-------------|
| Escola → Turma / Camera | Contexto institucional |
| Turma ↔ Aluno | Vínculo N:N (`turma_aluno`) |
| Camera ↔ Turma | Cobertura N:N (`camera_turma`) |
| Camera → Evento | Evento de monitoramento (obrigatório) |
| Turma / Aluno → Evento | Contexto opcional no evento |

Auth (`Usuario` + JWT) fica fora deste diagrama — ver matriz de permissões no [`README.md`](../../README.md).
