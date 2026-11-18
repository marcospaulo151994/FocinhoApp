# FocinhoApp

App Android para tutores acompanharem a saúde dos pets: cadastro dos animais,
vacinas, vermífugos e consultas. Projeto final de Desenvolvimento Mobile II (UPF).

Integrantes: Marcos Paulo de Medeiros e Emerson Rosado Scalcon.

Status dos requisitos em `docs/REQUISITOS.md`. Plano de trabalho em `docs/SPRINTS.md`.

## Regras da disciplina

- Kotlin.
- Layouts em XML. Sem Jetpack Compose.
- Activities e Fragments (pelo menos um Fragment).
- Persistência com Room.
- Mínimo de 4 Activities.
- Login funcional, CRUD e formulários com validações.

## Padrões do código atual

- Toda tela herda de `BaseActivity` (log de ciclo de vida, toolbar, insets, `navegarPara`).
- Views acessadas por ViewBinding. Sem `findViewById`.
- Fluxo de dados: Activity → Repository → DAO. A tela nunca chama o DAO direto.
  Os repositórios vêm de `FocinhoApplication` (ex.: `app.petRepository`).
- Todo texto visível fica em `res/values/strings.xml`.
- Nomes e comentários em português, como no código existente.

## Git

- Branches `nome/funcionalidade` (ex.: `marcos/detalhe-pet`).
- Pull Request para `main`. Não existe `develop`.
- Commits pequenos, com mensagem descritiva em português.

## Fora de escopo

Não fazer, mesmo que pareça melhoria:

- Atualizar dependências.
- Novos testes.
- Refatorações.
- Firebase, API ou notificações.
- Corrigir avisos de lint.

## Como trabalhar com o Marcos

- Uma tarefa por vez.
- Explicar em até 3 linhas antes de criar cada arquivo.
- Respostas curtas.
- Terminar com um único próximo passo.
- Nunca ampliar o escopo.
