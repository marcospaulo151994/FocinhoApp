package br.upf.focinhoapp.util

/**
 * Regras de validação dos formulários, reunidas num lugar só.
 *
 * As funções só dizem se o valor é válido (true/false). A mensagem de erro
 * fica na tela, que busca o texto no strings.xml. Separar assim permite
 * testar as regras com testes unitários (ver ValidacoesTest).
 */
object Validacoes {

    const val SENHA_TAMANHO_MINIMO = 6
    const val NOME_TAMANHO_MINIMO = 2
    const val PESO_MAXIMO_KG = 150.0

    // Não usamos android.util.Patterns para que a regra rode também nos testes unitários.
    private val REGEX_EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun emailValido(email: String): Boolean = REGEX_EMAIL.matches(email.trim())

    fun senhaValida(senha: String): Boolean = senha.length >= SENHA_TAMANHO_MINIMO

    fun nomeValido(nome: String): Boolean = nome.trim().length >= NOME_TAMANHO_MINIMO

    /** Peso precisa ser positivo e realista para cães e gatos. */
    fun pesoValido(peso: Double): Boolean = peso > 0.0 && peso <= PESO_MAXIMO_KG

    /** Aceita "4,5" ou "4.5". Devolve null se não for número. */
    fun converterPeso(texto: String): Double? =
        texto.trim().replace(',', '.').toDoubleOrNull()

    /** A data não pode ser depois de hoje (ex.: nascimento, vacina já aplicada). */
    fun dataNaoFutura(data: Long, hoje: Long = System.currentTimeMillis()): Boolean =
        DateUtils.inicioDoDia(data) <= DateUtils.inicioDoDia(hoje)

    /** A próxima data (reforço, retorno) precisa ser depois da data do cuidado. */
    fun proximaDataValida(data: Long, proximaData: Long): Boolean =
        DateUtils.inicioDoDia(proximaData) > DateUtils.inicioDoDia(data)
}
