package br.upf.focinhoapp.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Funções de data. As datas do app são Long (milissegundos), e aqui
 * convertemos para texto "dd/MM/yyyy" e fazemos cálculos (idade, dias).
 *
 * Usamos Calendar/SimpleDateFormat em vez de java.time porque o minSdk é 24
 * e java.time só existe a partir do Android 8 (API 26).
 *
 * Este objeto não depende de nada do Android, então pode ser testado
 * com testes unitários comuns (ver DateUtilsTest).
 */
object DateUtils {

    private val LOCALE_BR: Locale = Locale.forLanguageTag("pt-BR")
    private const val PADRAO_DATA = "dd/MM/yyyy"

    /** Millis -> "15/11/2026". */
    fun formatar(millis: Long): String =
        SimpleDateFormat(PADRAO_DATA, LOCALE_BR).format(Date(millis))

    /** "15/11/2026" -> millis. Devolve null se o texto não for uma data válida. */
    fun converter(texto: String): Long? = try {
        SimpleDateFormat(PADRAO_DATA, LOCALE_BR)
            .apply { isLenient = false } // recusa datas como 31/02
            .parse(texto.trim())
            ?.time
    } catch (e: ParseException) {
        null
    }

    /** Cria uma data a partir de ano, mês (0 = janeiro, como no Calendar) e dia. */
    fun criarData(ano: Int, mes: Int, dia: Int): Long =
        Calendar.getInstance().apply {
            clear()
            set(ano, mes, dia)
        }.timeInMillis

    /** Zera hora, minuto, segundo e milissegundo, ficando só com o dia. */
    fun inicioDoDia(millis: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    /** Quantos dias de [inicio] até [fim] (negativo se [fim] for antes). */
    fun diasEntre(inicio: Long, fim: Long): Long {
        val diferenca = inicioDoDia(fim) - inicioDoDia(inicio)
        // Math.round compensa a hora a mais/a menos do horário de verão.
        return Math.round(diferenca.toDouble() / TimeUnit.DAYS.toMillis(1))
    }

    /**
     * Idade por extenso entre [nascimento] e [referencia].
     * Ex.: "Menos de 1 mês", "5 meses", "1 ano", "2 anos e 3 meses".
     */
    fun descreverIdade(nascimento: Long, referencia: Long): String {
        val inicio = Calendar.getInstance().apply { timeInMillis = nascimento }
        val fim = Calendar.getInstance().apply { timeInMillis = referencia }

        var meses = (fim.get(Calendar.YEAR) - inicio.get(Calendar.YEAR)) * 12 +
                (fim.get(Calendar.MONTH) - inicio.get(Calendar.MONTH))
        // Se ainda não chegou no "dia do aniversário" do mês, não completou o mês.
        if (fim.get(Calendar.DAY_OF_MONTH) < inicio.get(Calendar.DAY_OF_MONTH)) meses--
        if (meses < 0) meses = 0

        val anos = meses / 12
        val mesesRestantes = meses % 12

        return when {
            anos == 0 && mesesRestantes == 0 -> "Menos de 1 mês"
            anos == 0 -> plural(mesesRestantes, "mês", "meses")
            mesesRestantes == 0 -> plural(anos, "ano", "anos")
            else -> "${plural(anos, "ano", "anos")} e ${plural(mesesRestantes, "mês", "meses")}"
        }
    }

    private fun plural(quantidade: Int, singular: String, plural: String) =
        "$quantidade ${if (quantidade == 1) singular else plural}"
}
