package br.upf.focinhoapp.ui.login

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import br.upf.focinhoapp.R
import br.upf.focinhoapp.data.repository.ResultadoAutenticacao
import br.upf.focinhoapp.databinding.ActivityLoginBinding
import br.upf.focinhoapp.ui.base.BaseActivity
import br.upf.focinhoapp.ui.base.limparErroAoDigitar
import br.upf.focinhoapp.ui.base.textoLimpo
import br.upf.focinhoapp.ui.home.MainActivity
import br.upf.focinhoapp.util.SessionManager
import br.upf.focinhoapp.util.Validacoes
import kotlinx.coroutines.launch

/**
 * Tela de login (tela inicial do app, definida como LAUNCHER no Manifest).
 *
 * Fluxo:
 * 1. Se já existe sessão salva, pula direto para a lista de pets.
 * 2. Valida os campos (formato do e-mail, senha preenchida).
 * 3. Confere as credenciais no banco local via [UsuarioRepository].
 * 4. Em caso de sucesso, salva a sessão e abre a [MainActivity].
 */
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val sessao by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usuário já logado: não faz sentido mostrar o login de novo.
        if (sessao.estaLogado()) {
            navegarPara(MainActivity::class.java, limparPilha = true)
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)

        configurarEventos()
    }

    private fun configurarEventos() {
        binding.layoutEmail.limparErroAoDigitar()
        binding.layoutSenha.limparErroAoDigitar()

        binding.botaoEntrar.setOnClickListener { entrar() }

        // Tecla "concluído" do teclado no campo de senha também faz login.
        binding.campoSenha.setOnEditorActionListener { _, acao, _ ->
            if (acao == EditorInfo.IME_ACTION_DONE) {
                entrar()
                true
            } else {
                false
            }
        }

        binding.botaoCriarConta.setOnClickListener {
            navegarPara(CadastroUsuarioActivity::class.java)
        }
    }

    /** Valida os campos e, se estiver tudo certo, tenta autenticar. */
    private fun entrar() {
        val email = binding.campoEmail.textoLimpo()
        val senha = binding.campoSenha.text?.toString().orEmpty()

        var valido = true
        if (!Validacoes.emailValido(email)) {
            binding.layoutEmail.error = getString(R.string.erro_email_invalido)
            valido = false
        }
        if (senha.isEmpty()) {
            binding.layoutSenha.error = getString(R.string.erro_senha_vazia)
            valido = false
        }
        if (!valido) return

        definirCarregando(true)

        // lifecycleScope: a corrotina é cancelada sozinha se a tela for destruída.
        lifecycleScope.launch {
            when (val resultado = app.usuarioRepository.autenticar(email, senha)) {
                is ResultadoAutenticacao.Sucesso -> {
                    sessao.iniciarSessao(resultado.usuario)
                    navegarPara(MainActivity::class.java, limparPilha = true)
                }
                ResultadoAutenticacao.EmailNaoEncontrado -> {
                    binding.layoutEmail.error = getString(R.string.erro_email_nao_encontrado)
                }
                ResultadoAutenticacao.SenhaIncorreta -> {
                    binding.layoutSenha.error = getString(R.string.erro_senha_incorreta)
                }
            }
            definirCarregando(false)
        }
    }

    /** Desabilita o botão enquanto consulta o banco, evitando cliques duplos. */
    private fun definirCarregando(carregando: Boolean) {
        binding.botaoEntrar.isEnabled = !carregando
    }
}
