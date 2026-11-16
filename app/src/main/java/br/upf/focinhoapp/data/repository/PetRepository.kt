package br.upf.focinhoapp.data.repository

import br.upf.focinhoapp.data.dao.PetDao
import br.upf.focinhoapp.data.model.Pet
import kotlinx.coroutines.flow.Flow

/** Acesso aos dados dos pets (CRUD). */
class PetRepository(private val dao: PetDao) {

    fun listarPets(usuarioId: Long): Flow<List<Pet>> = dao.listarPorUsuario(usuarioId)

    fun observarPet(id: Long): Flow<Pet?> = dao.observarPorId(id)

    suspend fun buscarPorId(id: Long): Pet? = dao.buscarPorId(id)

    suspend fun inserir(pet: Pet): Long = dao.inserir(pet)

    suspend fun atualizar(pet: Pet) = dao.atualizar(pet)

    /** Exclui o pet. Pelo CASCADE do banco, os cuidados dele também são apagados. */
    suspend fun excluir(pet: Pet) = dao.excluir(pet)
}
