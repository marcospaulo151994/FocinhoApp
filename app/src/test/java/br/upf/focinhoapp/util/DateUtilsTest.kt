package br.upf.focinhoapp.util

import br.upf.focinhoapp.data.model.Cuidado
import br.upf.focinhoapp.data.model.SituacaoCuidado
import br.upf.focinhoapp.data.model.TipoCuidado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Testes das funções de data e da regra de situação dos cuidados. */
class DateUtilsTest {

    // Lembrete: no Calendar o mês começa em 0 (0 = janeiro, 10 = novembro).
    private val hoje = DateUtils.criarData(2026, 10, 15)

    @Test
    fun `formata e converte datas no padrao brasileiro`() {
        assertEquals("15/11/2026", DateUtils.formatar(hoje))
        assertEquals(hoje, DateUtils.converter("15/11/2026"))
        assertNull(DateUtils.converter("31/02/2026")) // data inexistente
        assertNull(DateUtils.converter("texto"))
    }

    @Test
    fun `descreve a idade por extenso`() {
        assertEquals("Menos de 1 mês", DateUtils.descreverIdade(DateUtils.criarData(2026, 10, 1), hoje))
        assertEquals("1 mês", DateUtils.descreverIdade(DateUtils.criarData(2026, 9, 15), hoje))
        assertEquals("5 meses", DateUtils.descreverIdade(DateUtils.criarData(2026, 5, 10), hoje))
        assertEquals("1 ano", DateUtils.descreverIdade(DateUtils.criarData(2025, 10, 15), hoje))
        assertEquals("2 anos e 3 meses", DateUtils.descreverIdade(DateUtils.criarData(2024, 7, 1), hoje))
    }

    @Test
    fun `conta dias entre datas`() {
        assertEquals(10L, DateUtils.diasEntre(hoje, DateUtils.criarData(2026, 10, 25)))
        assertEquals(-5L, DateUtils.diasEntre(hoje, DateUtils.criarData(2026, 10, 10)))
    }

    @Test
    fun `situacao do cuidado conforme a proxima data`() {
        fun cuidado(proxima: Long?) = Cuidado(
            petId = 1, tipo = TipoCuidado.VACINA, titulo = "V10",
            data = DateUtils.criarData(2025, 10, 15), proximaData = proxima
        )
        assertEquals(SituacaoCuidado.SEM_PREVISAO, cuidado(null).situacao(hoje))
        assertEquals(SituacaoCuidado.ATRASADO, cuidado(DateUtils.criarData(2026, 10, 14)).situacao(hoje))
        assertEquals(SituacaoCuidado.PROXIMO, cuidado(DateUtils.criarData(2026, 10, 20)).situacao(hoje))
        assertEquals(SituacaoCuidado.EM_DIA, cuidado(DateUtils.criarData(2027, 0, 1)).situacao(hoje))
    }
}
