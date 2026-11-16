package br.upf.focinhoapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.upf.focinhoapp.util.DateUtils

/**
 * Animal de estimação cadastrado por um [Usuario].
 *
 * Datas são guardadas como Long (milissegundos desde 1970, o "epoch"), que é o
 * formato mais simples de salvar no SQLite e funciona em qualquer versão do Android.
 *
 * @property usuarioId dono do pet (chave estrangeira para [Usuario]).
 * @property dataNascimento data de nascimento em milissegundos; opcional.
 * @property pesoKg peso em quilos; opcional.
 */
@Entity(
    tableName = "pets",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            // Se o usuário for apagado, os pets dele somem junto.
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Índice na chave estrangeira deixa as consultas "pets do usuário X" mais rápidas.
    indices = [Index(value = ["usuarioId"])]
)
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val nome: String,
    val especie: Especie,
    val raca: String? = null,
    val sexo: Sexo,
    val dataNascimento: Long? = null,
    val pesoKg: Double? = null,
    val observacoes: String? = null
) {
    /**
     * Idade por extenso (ex.: "2 anos e 3 meses"), ou null se a data de
     * nascimento não foi informada. Regra de negócio que pertence ao domínio,
     * por isso fica aqui e não na tela.
     */
    fun descricaoIdade(referencia: Long = System.currentTimeMillis()): String? =
        dataNascimento?.let { DateUtils.descreverIdade(it, referencia) }
}
