# Requisitos do professor

Atualizado em 18/11/2026. Resumo: 14 feitos, nenhum falta.

Caminhos que começam com `ui/`, `data/` ou `util/` são relativos a
`app/src/main/java/br/upf/focinhoapp/`.

| Requisito | Status | Onde está no código |
|---|---|---|
| Kotlin | ✅ feito | Todo o código do app (`app/src/main/java/`) |
| Layouts XML | ✅ feito | `app/src/main/res/layout/` (5 layouts, sem Compose) |
| Activities e Fragments | ✅ feito | 6 Activities em `ui/`. Fragment: `ui/cuidados/CuidadoListFragment.kt`, usado nas 3 abas de `ui/pets/PetDetalheActivity.kt` |
| Ciclo de vida | ✅ feito | `ui/base/BaseActivity.kt` (log `CicloVida`), `onSaveInstanceState` em `ui/pets/PetFormActivity.kt`, `repeatOnLifecycle` em `ui/home/MainActivity.kt` |
| Login funcional | ✅ feito | `ui/login/LoginActivity.kt`, `data/repository/UsuarioRepository.kt`, `util/SessionManager.kt` |
| Mínimo 4 Activities | ✅ feito | `LoginActivity`, `CadastroUsuarioActivity`, `MainActivity`, `PetDetalheActivity`, `PetFormActivity`, `CuidadoFormActivity` (declaradas em `app/src/main/AndroidManifest.xml`) |
| Navegação e layout consistentes | ✅ feito | `configurarToolbar` e `navegarPara` em `ui/base/BaseActivity.kt`; `themes.xml` e `dimens.xml` em `app/src/main/res/values/` |
| Formulários, validações e regras de negócio | ✅ feito | `util/Validacoes.kt`, `ui/login/CadastroUsuarioActivity.kt`, `ui/pets/PetFormActivity.kt`, `ui/cuidados/CuidadoFormActivity.kt`, `situacao()` em `data/model/Cuidado.kt`, `permiteDataFutura` em `data/model/TipoCuidado.kt` |
| Objetos de domínio | ✅ feito | `data/model/` (`Usuario`, `Pet`, `Cuidado`, `Especie`, `Sexo`, `TipoCuidado`) |
| Persistência com Room | ✅ feito | `data/local/AppDatabase.kt`, `data/local/Converters.kt`, `data/dao/` |
| CRUD | ✅ feito | Pets: `data/dao/PetDao.kt`, `ui/home/MainActivity.kt` (listar), `ui/pets/PetFormActivity.kt` (criar, editar, excluir). Cuidados: `data/dao/CuidadoDao.kt`, `ui/cuidados/CuidadoListFragment.kt` (listar), `ui/cuidados/CuidadoFormActivity.kt` (criar, editar, excluir) |
| GitHub com histórico coerente e branches | ✅ feito | PRs #1 a #3 na `main` (`feature/estrutura-base`, `feature/login`, `feature/crud-pets`). Manter o fluxo nas próximas branches |
| README completo | ✅ feito | `README.md`: proposta, tecnologias, arquitetura, telas, regras de negócio, instruções de execução e prints |
| Arquivo com prints das telas | ✅ feito | `docs/prints/` (6 prints: login, cadastro, lista de pets, formulário de pet, detalhe com as abas, formulário de cuidado) |
