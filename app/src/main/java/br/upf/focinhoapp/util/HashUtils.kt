package br.upf.focinhoapp.util

import java.security.MessageDigest

/** Funções de hash. Usadas para não salvar senhas em texto puro no banco. */
object HashUtils {

    /** Devolve o SHA-256 do texto em hexadecimal (64 caracteres). */
    fun sha256(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray(Charsets.UTF_8))
            .joinToString(separator = "") { byte -> "%02x".format(byte) }
}
