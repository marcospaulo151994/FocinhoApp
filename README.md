# 🐾 Focinho

Aplicativo Android para tutores manterem a saúde dos pets em dia: cadastro dos animais, carteira de vacinação, controle de vermífugos e histórico de consultas ao veterinário.

Projeto Final da disciplina **Desenvolvimento Mobile II** — Universidade de Passo Fundo (UPF).

## Integrantes

- Emerson Rosado Scalcon
- Marcos Paulo de Medeiros

## Descrição da proposta

Muitos tutores perdem a carteirinha de papel ou esquecem a data do próximo reforço de vacina ou vermífugo. O Focinho reúne tudo isso no celular:

- **Conta do tutor:** cadastro e login (local, sem servidor). Cada usuário vê só os próprios pets.
- **Pets:** cadastro com nome, espécie, raça, sexo, data de nascimento, peso e observações. A idade é calculada automaticamente.
- **Cuidados de saúde:** vacinas, vermífugos e consultas, com data da próxima dose/retorno e indicação de itens atrasados ou próximos do vencimento.

## Tecnologias utilizadas

| Item | Uso no projeto |
|---|---|
| Kotlin | Linguagem do app |
| Android Studio / Gradle (AGP 9) | IDE e build |
| Activities e Fragments | Telas e seções reutilizáveis |
| Layouts XML + Material Components 3 | Interface |
| ViewBinding | Acesso tipado às views dos layouts |
| Room (SQLite) + KSP | Persistência local de usuários, pets e cuidados |
| Kotlin Coroutines + Flow | Acesso ao banco fora da thread principal e listas que se atualizam sozinhas |
| SharedPreferences | Sessão do usuário logado |
| JUnit 4 | Testes unitários das regras de negócio |
| Git + GitHub | Versionamento e trabalho em dupla (branches) |

## Arquitetura

```
br.upf.focinhoapp
├── FocinhoApplication.kt      # cria o banco e os repositórios (uma vez só)
├── data
│   ├── model/                 # objetos de domínio / entidades do Room (Usuario, Pet, Cuidado, enums)
│   ├── dao/                   # consultas SQL (UsuarioDao, PetDao, CuidadoDao)
│   ├── local/                 # AppDatabase e conversores de tipo
│   └── repository/            # regras de negócio entre as telas e o banco
├── ui
│   ├── base/                  # BaseActivity (ciclo de vida, toolbar) e extensões de formulário
│   ├── login/                 # LoginActivity, CadastroUsuarioActivity
│   ├── home/                  # MainActivity (lista de pets)
│   ├── pets/                  # PetDetalheActivity (abas), PetFormActivity (CRUD) e PetAdapter
│   └── cuidados/              # CuidadoListFragment (uma aba do detalhe), CuidadoFormActivity (CRUD) e CuidadoAdapter
└── util/                      # DateUtils, Validacoes, HashUtils, SessionManager
```

Fluxo de dados: **Activity → Repository → DAO → Room (SQLite)**. As listas usam `Flow`, então qualquer inserção, edição ou exclusão aparece na tela sem recarregar.

### Telas (Activities)

| Activity | Função |
|---|---|
| `LoginActivity` | Login (tela inicial). Pula direto para a lista se já houver sessão |
| `CadastroUsuarioActivity` | Criação de conta com validações |
| `MainActivity` | Lista de pets do usuário, estado vazio e logout |
| `PetDetalheActivity` | Detalhe do pet com abas de vacinas, vermífugos e consultas. Cada aba é um `CuidadoListFragment` |
| `PetFormActivity` | Cadastro, edição e exclusão de pet (CRUD) |
| `CuidadoFormActivity` | Cadastro, edição e exclusão de vacina, vermífugo ou consulta (CRUD) |

### Regras de negócio implementadas

- E-mail em formato válido e único; senha com no mínimo 6 caracteres, salva como hash SHA-256.
- Nome do pet com no mínimo 2 letras; espécie e sexo obrigatórios.
- Raça escolhida numa lista conforme a espécie (cão ou gato); trocar a espécie limpa a raça.
- Data de nascimento não pode ser no futuro; peso entre 0 e 150 kg (aceita vírgula ou ponto).
- Excluir um pet apaga os registros de saúde dele (chave estrangeira com `CASCADE`), com confirmação antes.
- Cuidado com título e data obrigatórios. Vacina e vermífugo não aceitam data no futuro; consulta aceita, para registrar uma consulta agendada. A próxima data precisa ser depois da data do cuidado.
- Cuidado com próxima data: **atrasado** (já passou), **próximo** (até 7 dias) ou **em dia**.

### Ciclo de vida

Todas as telas herdam de `BaseActivity`, que registra no Logcat cada chamada do ciclo de vida. No Android Studio, abra o **Logcat** e filtre por `CicloVida` para acompanhar `onCreate → onStart → onResume → onPause → onStop → onDestroy` ao navegar, minimizar ou girar o aparelho. O formulário de pet também mostra o uso de `onSaveInstanceState` para não perder a data escolhida na rotação.

## Instruções de execução

1. Clone o repositório:
   ```bash
   git clone https://github.com/marcospaulo151994/FocinhoApp.git
   ```
2. Abra a pasta no **Android Studio** (versão compatível com AGP 9.5) e aguarde o *Gradle Sync*.
3. Selecione um emulador ou aparelho com Android 7.0 (API 24) ou superior.
4. Clique em **Run ▶**.
5. Na primeira execução, toque em **"Cadastre-se"** para criar uma conta (os dados ficam salvos no aparelho).

Testes unitários: botão direito em `app/src/test` → **Run Tests**.

## Prints das telas

As capturas ficam em [`docs/prints`](docs/prints) _(serão adicionadas ao final do desenvolvimento)_.

## Organização do Git

- `main`: versão estável, usada na entrega.
- `nome/funcionalidade`: uma branch por tarefa, com o nome de quem faz (ex.: `marcos/detalhe-pet`, `marcos/form-cuidado`).
- Cada branch entra na `main` por Pull Request.
- As primeiras funcionalidades usaram `feature/<nome>` (ex.: `feature/login`, `feature/crud-pets`), também mescladas na `main` por Pull Request.
