package br.upf.focinhoapp.data.repository

import br.upf.focinhoapp.data.dao.CuidadoDao
import br.upf.focinhoapp.data.model.Cuidado
import br.upf.focinhoapp.data.model.TipoCuidado
import kotlinx.coroutines.flow.Flow

/** Acesso aos registros de saúde (vacinas, vermífugos e consultas). */
class CuidadoRepository(private val dao: CuidadoDao) {

    fun listar(petId: Long, tipo: TipoCuidado): Flow<List<Cuidado>> =
        dao.listarPorPetETipo(petId, tipo)

    suspend fun buscarPorId(id: Long): Cuidado? = dao.buscarPorId(id)

    suspend fun inserir(cuidado: Cuidado): Long = dao.inserir(cuidado)

    suspend fun atualizar(cuidado: Cuidado) = dao.atualizar(cuidado)

    suspend fun excluir(cuidado: Cuidado) = dao.excluir(cuidado)
}
