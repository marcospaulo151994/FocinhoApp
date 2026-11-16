package br.upf.focinhoapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.upf.focinhoapp.data.model.Pet
import kotlinx.coroutines.flow.Flow

/**
 * Operações de CRUD dos pets.
 *
 * As listagens devolvem [Flow]: um "fluxo" que emite uma lista nova sempre que
 * a tabela muda. Assim a tela se atualiza sozinha depois de inserir, editar
 * ou excluir, sem precisar recarregar manualmente.
 */
@Dao
interface PetDao {

    // ---------- Create ----------
    @Insert
    suspend fun inserir(pet: Pet): Long

    // ---------- Read ----------
    /** Pets de um usuário, em ordem alfabética (ignorando maiúsculas/minúsculas). */
    @Query("SELECT * FROM pets WHERE usuarioId = :usuarioId ORDER BY nome COLLATE NOCASE")
    fun listarPorUsuario(usuarioId: Long): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun buscarPorId(id: Long): Pet?

    /** Versão "observável" de [buscarPorId], útil para telas de detalhe. */
    @Query("SELECT * FROM pets WHERE id = :id")
    fun observarPorId(id: Long): Flow<Pet?>

    // ---------- Update ----------
    @Update
    suspend fun atualizar(pet: Pet)

    // ---------- Delete ----------
    @Delete
    suspend fun excluir(pet: Pet)
}
