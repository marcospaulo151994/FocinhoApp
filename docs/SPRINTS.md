# Sprints

Entrega: **25/11/2026 (quarta)**. Plano feito em 18/11/2026.

Cada tarefa traz: responsável · branch · prazo · critério de pronto.
Uma tarefa só conta como pronta com o Pull Request mesclado na `main`.

## Sprint 1 — Cuidados e Fragment (18/11 a 22/11)

- [ ] **PetDetalheActivity com 3 abas + CuidadoListFragment** · Marcos · `marcos/detalhe-pet` · até 22/11 (dom) · Pronto quando: tocar num pet abre o detalhe e cada aba (Vacinas, Vermífugos, Consultas) lista os cuidados daquele tipo; editar o pet continua acessível.
- [ ] **CuidadoFormActivity (criar, editar, excluir cuidado)** · Emerson · `emerson/form-cuidado` · até 21/11 (sáb) · Pronto quando: dá para criar, editar e excluir um cuidado, com validação dos campos.

Dependência: o formulário entra na `main` primeiro (21/11). No dia 22/11 o detalhe liga o botão "Novo" e o toque no item da lista ao `CuidadoFormActivity`.

## Sprint 2 — Fechamento (23/11 e 24/11)

- [ ] **Prints de todas as telas em `docs/prints`** · Marcos · `marcos/prints` · até 23/11 (seg) · Pronto quando: há um print de cada tela (login, cadastro, lista de pets, formulário de pet, detalhe com as abas, formulário de cuidado).
- [ ] **README final** · Emerson · `emerson/readme-final` · até 24/11 (ter) · Pronto quando: o README descreve as telas novas e mostra os prints.
- [ ] **Revisar o código para a apresentação** · Marcos e Emerson · sem branch · 24/11 (ter) · Pronto quando: os dois conseguem explicar cada tela, o Fragment e o fluxo Activity → Repository → DAO.
