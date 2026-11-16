package br.upf.focinhoapp.ui.login

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import br.upf.focinhoapp.R
import br.upf.focinhoapp.data.repository.ResultadoCadastro
import br.upf.focinhoapp.databinding.ActivityCadastroUsuarioBinding
import br.upf.focinhoapp.ui.base.BaseActivity
import br.upf.focinhoapp.ui.base.limparErroAoDigitar
import br.upf.focinhoapp.ui.base.textoLimpo
import br.upf.focinhoapp.ui.home.MainActivity
import br.upf.focinhoapp.util.SessionManager
import br.upf.focinhoapp.util.Validacoes
import kotlinx.coroutines.launch

/**
 * Cadastro de uma nova conta de tutor.
 *
 * Regras de negócio validadas:
 * - nome com pelo menos 2 caracteres;
 * - e-mail em formato válido e ainda não cadastrado;
 * - senha com pelo menos [Validacoes.SENHA_TAMANHO_MINIMO] caracteres;
 * - confirmação igual à senha.
 *
 * Depois de criar a conta, o usuário já entra logado.
 */
class CadastroUsuarioActivity : BaseActivity() {

    private lateinit var binding: ActivityCadastroUsuarioBinding
    private val sessao by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)
        configurarToolbar(binding.toolbar, getString(R.string.titulo_cadastro))

        listOf(
            binding.layoutNome,
            binding.layoutEmail,
            binding.layoutSenha,
            binding.layoutConfirmarSenha
        ).forEach { it.limparErroAoDigitar() }

        binding.layoutSenha.helperText =
            getString(R.string.helper_senha, Validacoes.SENHA_TAMANHO_MINIMO)

        binding.botaoCadastrar.setOnClickListener { cadastrar() }
    }

    private fun cadastrar() {
        val nome = binding.campoNome.textoLimpo()
        val email = binding.campoEmail.textoLimpo()
        val senha = binding.campoSenha.text?.toString().orEmpty()
        val confirmacao = binding.campoConfirmarSenha.text?.toString().orEmpty()

        if (!formularioValido(nome, email, senha, confirmacao)) return

        binding.botaoCadastrar.isEnabled = false
        lifecycleScope.launch {
            when (val resultado = app.usuarioRepository.cadastrar(nome, email, senha)) {
                is ResultadoCadastro.Sucesso -> {
                    sessao.iniciarSessao(resultado.usuario)
                    mostrarMensagem(getString(R.string.msg_conta_criada, resultado.usuario.nome))
                    navegarPara(MainActivity::class.java, limparPilha = true)
                }
                ResultadoCadastro.EmailJaCadastrado -> {
                    binding.layoutEmail.error = getString(R.string.erro_email_ja_cadastrado)
                    binding.botaoCadastrar.isEnabled = true
                }
            }
        }
    }

    /** Marca os campos com erro e devolve true se tudo estiver válido. */
    private fun formularioValido(
        nome: String,
        email: String,
        senha: String,
        confirmacao: String
    ): Boolean {
        var valido = true

        if (!Validacoes.nomeValido(nome)) {
            binding.layoutNome.error = getString(R.string.erro_nome_usuario)
            valido = false
        }
        if (!Validacoes.emailValido(email)) {
            binding.layoutEmail.error = getString(R.string.erro_email_invalido)
            valido = false
        }
        if (!Validacoes.senhaValida(senha)) {
            binding.layoutSenha.error =
                getString(R.string.erro_senha_curta, Validacoes.SENHA_TAMANHO_MINIMO)
            valido = false
        }
        if (confirmacao != senha) {
            binding.layoutConfirmarSenha.error = getString(R.string.erro_senhas_diferentes)
            valido = false
        }
        return valido
    }
}
