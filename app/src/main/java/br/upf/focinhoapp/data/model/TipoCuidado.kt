package br.upf.focinhoapp.data.model

import androidx.annotation.StringRes
import br.upf.focinhoapp.R

/**
 * Tipos de registro de saúde. Vacinas, vermífugos e consultas têm a mesma
 * "forma" (algo que aconteceu numa data e pode ter uma próxima data), então
 * usamos uma única entidade [Cuidado] e diferenciamos pelo tipo.
 *
 * Cada tipo carrega os textos que o formulário deve exibir, assim a mesma tela
 * serve para os três casos, apenas trocando os rótulos.
 *
 * @property titulo nome do tipo no plural (usado nas abas).
 * @property rotuloTitulo rótulo do campo principal (nome da vacina, produto, motivo).
 * @property rotuloData rótulo da data em que o cuidado aconteceu.
 * @property rotuloProximaData rótulo da próxima data (reforço, retorno...).
 * @property rotuloDetalhe rótulo do campo complementar (lote, dose, diagnóstico).
 */
enum class TipoCuidado(
    @StringRes val titulo: Int,
    @StringRes val rotuloTitulo: Int,
    @StringRes val rotuloData: Int,
    @StringRes val rotuloProximaData: Int,
    @StringRes val rotuloDetalhe: Int
) {
    VACINA(
        R.string.tipo_vacina,
        R.string.rotulo_nome_vacina,
        R.string.rotulo_data_aplicacao,
        R.string.rotulo_proxima_dose,
        R.string.rotulo_lote
    ),
    VERMIFUGO(
        R.string.tipo_vermifugo,
        R.string.rotulo_produto,
        R.string.rotulo_data_aplicacao,
        R.string.rotulo_proxima_aplicacao,
        R.string.rotulo_dose
    ),
    CONSULTA(
        R.string.tipo_consulta,
        R.string.rotulo_motivo,
        R.string.rotulo_data_consulta,
        R.string.rotulo_retorno,
        R.string.rotulo_diagnostico
    )
}
