package br.upf.focinhoapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.upf.focinhoapp.util.DateUtils

/**
 * Registro de saúde de um pet: vacina, vermífugo ou consulta (ver [TipoCuidado]).
 *
 * @property petId pet ao qual o registro pertence.
 * @property titulo nome da vacina, produto do vermífugo ou motivo da consulta.
 * @property data quando o cuidado aconteceu (ou vai acontecer, no caso de consulta agendada).
 * @property proximaData próxima dose / aplicação / retorno; opcional.
 * @property veterinario nome do veterinário ou clínica; opcional.
 * @property detalhe lote da vacina, dose do vermífugo ou diagnóstico da consulta; opcional.
 */
@Entity(
    tableName = "cuidados",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            // Apagar o pet apaga todo o histórico dele.
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["petId"])]
)
data class Cuidado(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val tipo: TipoCuidado,
    val titulo: String,
    val data: Long,
    val proximaData: Long? = null,
    val veterinario: String? = null,
    val detalhe: String? = null,
    val observacoes: String? = null
) {
    /**
     * Regra de negócio: em que situação está a próxima data deste cuidado.
     * - SEM_PREVISAO: não há próxima data cadastrada;
     * - ATRASADO: a próxima data já passou;
     * - PROXIMO: vence em até [DIAS_AVISO] dias;
     * - EM_DIA: vence mais adiante.
     */
    fun situacao(hoje: Long = System.currentTimeMillis()): SituacaoCuidado {
        val proxima = proximaData ?: return SituacaoCuidado.SEM_PREVISAO
        val dias = DateUtils.diasEntre(hoje, proxima)
        return when {
            dias < 0 -> SituacaoCuidado.ATRASADO
            dias <= DIAS_AVISO -> SituacaoCuidado.PROXIMO
            else -> SituacaoCuidado.EM_DIA
        }
    }

    companion object {
        /** Quantos dias antes do vencimento o cuidado passa a ser "próximo". */
        const val DIAS_AVISO = 7
    }
}

/** Situação da próxima data de um [Cuidado]. */
enum class SituacaoCuidado { SEM_PREVISAO, EM_DIA, PROXIMO, ATRASADO }
