package br.upf.focinhoapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.upf.focinhoapp.data.model.Usuario

/**
 * DAO (Data Access Object) de usuários: aqui ficam as operações de banco.
 * O Room gera a implementação desta interface automaticamente (via KSP).
 *
 * Funções `suspend` rodam em segundo plano: o Room não deixa acessar o
 * banco na thread principal, para não travar a interface.
 */
@Dao
interface UsuarioDao {

    /** Insere um usuário e devolve o id gerado. Falha se o e-mail já existir. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: Long): Usuario?
}
