package br.upf.focinhoapp.data.repository

import br.upf.focinhoapp.data.dao.UsuarioDao
import br.upf.focinhoapp.data.model.Usuario
import br.upf.focinhoapp.util.HashUtils

/**
 * Regras de negócio de contas de usuário (cadastro e login).
 *
 * O repositório fica entre as telas e o banco: as Activities não falam com o
 * DAO diretamente. Se um dia o login passar a usar uma API, só esta classe muda.
 */
class UsuarioRepository(private val dao: UsuarioDao) {

    /**
     * Cria uma nova conta. O e-mail é normalizado (sem espaços, minúsculo)
     * e a senha é salva como hash.
     */
    suspend fun cadastrar(nome: String, email: String, senha: String): ResultadoCadastro {
        val emailNormalizado = normalizarEmail(email)

        if (dao.buscarPorEmail(emailNormalizado) != null) {
            return ResultadoCadastro.EmailJaCadastrado
        }

        val usuario = Usuario(
            nome = nome.trim(),
            email = emailNormalizado,
            senhaHash = HashUtils.sha256(senha)
        )
        val idGerado = dao.inserir(usuario)
        return ResultadoCadastro.Sucesso(usuario.copy(id = idGerado))
    }

    /** Confere e-mail e senha. Compara o hash da senha digitada com o salvo. */
    suspend fun autenticar(email: String, senha: String): ResultadoAutenticacao {
        val usuario = dao.buscarPorEmail(normalizarEmail(email))
            ?: return ResultadoAutenticacao.EmailNaoEncontrado

        return if (usuario.senhaHash == HashUtils.sha256(senha)) {
            ResultadoAutenticacao.Sucesso(usuario)
        } else {
            ResultadoAutenticacao.SenhaIncorreta
        }
    }

    private fun normalizarEmail(email: String) = email.trim().lowercase()
}

/**
 * Resultados possíveis do cadastro. Uma `sealed class` obriga a tela a tratar
 * todos os casos no `when` (o compilador avisa se faltar algum).
 */
sealed class ResultadoCadastro {
    data class Sucesso(val usuario: Usuario) : ResultadoCadastro()
    data object EmailJaCadastrado : ResultadoCadastro()
}

/** Resultados possíveis do login. */
sealed class ResultadoAutenticacao {
    data class Sucesso(val usuario: Usuario) : ResultadoAutenticacao()
    data object EmailNaoEncontrado : ResultadoAutenticacao()
    data object SenhaIncorreta : ResultadoAutenticacao()
}
