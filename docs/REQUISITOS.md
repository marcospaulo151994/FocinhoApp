# Requisitos do professor

Atualizado em 18/11/2026. Resumo: 11 feitos, 3 faltam.

Caminhos que começam com `ui/`, `data/` ou `util/` são relativos a
`app/src/main/java/br/upf/focinhoapp/`.

| Requisito | Status | Onde está no código |
|---|---|---|
| Kotlin | ✅ feito | Todo o código do app (`app/src/main/java/`) |
| Layouts XML | ✅ feito | `app/src/main/res/layout/` (5 layouts, sem Compose) |
| Activities e Fragments | ⬜ falta | 4 Activities em `ui/`. Falta o Fragment: `CuidadoListFragment` (Sprint 1) |
| Ciclo de vida | ✅ feito | `ui/base/BaseActivity.kt` (log `CicloVida`), `onSaveInstanceState` em `ui/pets/PetFormActivity.kt`, `repeatOnLifecycle` em `ui/home/MainActivity.kt` |
| Login funcional | ✅ feito | `ui/login/LoginActivity.kt`, `data/repository/UsuarioRepository.kt`, `util/SessionManager.kt` |
| Mínimo 4 Activities | ✅ feito | `LoginActivity`, `CadastroUsuarioActivity`, `MainActivity`, `PetFormActivity` (declaradas em `app/src/main/AndroidManifest.xml`) |
| Navegação e layout consistentes | ✅ feito | `configurarToolbar` e `navegarPara` em `ui/base/BaseActivity.kt`; `themes.xml` e `dimens.xml` em `app/src/main/res/values/` |
| Formulários, validações e regras de negócio | ✅ feito | `util/Validacoes.kt`, `ui/login/CadastroUsuarioActivity.kt`, `ui/pets/PetFormActivity.kt`, `situacao()` em `data/model/Cuidado.kt` |
| Objetos de domínio | ✅ feito | `data/model/` (`Usuario`, `Pet`, `Cuidado`, `Especie`, `Sexo`, `TipoCuidado`) |
| Persistência com Room | ✅ feito | `data/local/AppDatabase.kt`, `data/local/Converters.kt`, `data/dao/` |
| CRUD | ✅ feito | Pets: `data/dao/PetDao.kt`, `ui/home/MainActivity.kt` (listar), `ui/pets/PetFormActivity.kt` (criar, editar, excluir). O CRUD de cuidados entra na Sprint 1 |
| GitHub com histórico coerente e branches | ✅ feito | PRs #1 a #3 na `main` (`feature/estrutura-base`, `feature/login`, `feature/crud-pets`). Manter o fluxo nas próximas branches |
| README completo | ⬜ falta | `README.md` existe; falta a versão final com os prints (Sprint 2) |
| Arquivo com prints das telas | ⬜ falta | `docs/prints/` está vazio (Sprint 2) |
