package br.upf.focinhoapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes das regras de validação. Rodam na JVM do computador, sem emulador:
 * clique com o botão direito na pasta "test" > Run Tests.
 */
class ValidacoesTest {

    @Test
    fun `email valido e invalido`() {
        assertTrue(Validacoes.emailValido("tutor@upf.br"))
        assertTrue(Validacoes.emailValido("  nome.sobrenome@gmail.com "))
        assertFalse(Validacoes.emailValido("sem-arroba.com"))
        assertFalse(Validacoes.emailValido("faltou@dominio"))
        assertFalse(Validacoes.emailValido(""))
    }

    @Test
    fun `senha precisa ter tamanho minimo`() {
        assertFalse(Validacoes.senhaValida("12345"))
        assertTrue(Validacoes.senhaValida("123456"))
    }

    @Test
    fun `nome precisa de pelo menos duas letras`() {
        assertFalse(Validacoes.nomeValido(" A "))
        assertTrue(Validacoes.nomeValido("Bo"))
    }

    @Test
    fun `peso aceita virgula ou ponto e respeita limites`() {
        assertEquals(4.5, Validacoes.converterPeso("4,5")!!, 0.0001)
        assertEquals(4.5, Validacoes.converterPeso("4.5")!!, 0.0001)
        assertNull(Validacoes.converterPeso("abc"))
        assertFalse(Validacoes.pesoValido(0.0))
        assertFalse(Validacoes.pesoValido(200.0))
        assertTrue(Validacoes.pesoValido(12.3))
    }

    @Test
    fun `data no futuro nao e aceita`() {
        val hoje = DateUtils.criarData(2026, 10, 15)
        assertTrue(Validacoes.dataNaoFutura(DateUtils.criarData(2026, 10, 15), hoje))
        assertTrue(Validacoes.dataNaoFutura(DateUtils.criarData(2025, 0, 1), hoje))
        assertFalse(Validacoes.dataNaoFutura(DateUtils.criarData(2026, 10, 16), hoje))
    }

    @Test
    fun `proxima data precisa ser depois da data do cuidado`() {
        val aplicacao = DateUtils.criarData(2026, 0, 10)
        assertTrue(Validacoes.proximaDataValida(aplicacao, DateUtils.criarData(2027, 0, 10)))
        assertFalse(Validacoes.proximaDataValida(aplicacao, aplicacao))
    }
}
