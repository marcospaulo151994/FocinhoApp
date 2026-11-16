package br.upf.focinhoapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.upf.focinhoapp.data.model.Cuidado
import br.upf.focinhoapp.data.model.TipoCuidado
import kotlinx.coroutines.flow.Flow

/** CRUD dos registros de saúde (vacinas, vermífugos e consultas). */
@Dao
interface CuidadoDao {

    @Insert
    suspend fun inserir(cuidado: Cuidado): Long

    /** Registros de um tipo para um pet, do mais recente para o mais antigo. */
    @Query("SELECT * FROM cuidados WHERE petId = :petId AND tipo = :tipo ORDER BY data DESC")
    fun listarPorPetETipo(petId: Long, tipo: TipoCuidado): Flow<List<Cuidado>>

    @Query("SELECT * FROM cuidados WHERE id = :id")
    suspend fun buscarPorId(id: Long): Cuidado?

    @Update
    suspend fun atualizar(cuidado: Cuidado)

    @Delete
    suspend fun excluir(cuidado: Cuidado)
}
