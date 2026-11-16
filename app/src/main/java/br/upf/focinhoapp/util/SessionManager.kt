package br.upf.focinhoapp.util

import android.content.Context
import androidx.core.content.edit
import br.upf.focinhoapp.data.model.Usuario

/**
 * Guarda quem está logado usando SharedPreferences (um pequeno arquivo
 * chave-valor do app). Assim o usuário continua logado mesmo depois de
 * fechar e reabrir o aplicativo, até tocar em "Sair".
 */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)

    /** Id do usuário logado, ou [SEM_USUARIO] se ninguém estiver logado. */
    val usuarioId: Long
        get() = prefs.getLong(CHAVE_ID, SEM_USUARIO)

    val usuarioNome: String
        get() = prefs.getString(CHAVE_NOME, null).orEmpty()

    fun estaLogado(): Boolean = usuarioId != SEM_USUARIO

    fun iniciarSessao(usuario: Usuario) {
        // edit { } (core-ktx) abre a edição e aplica (apply) ao final.
        prefs.edit {
            putLong(CHAVE_ID, usuario.id)
            putString(CHAVE_NOME, usuario.nome)
        }
    }

    fun encerrarSessao() {
        prefs.edit { clear() }
    }

    companion object {
        private const val ARQUIVO = "sessao_focinho"
        private const val CHAVE_ID = "usuario_id"
        private const val CHAVE_NOME = "usuario_nome"
        const val SEM_USUARIO = -1L
    }
}
