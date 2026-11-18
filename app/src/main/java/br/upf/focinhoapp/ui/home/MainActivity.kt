package br.upf.focinhoapp.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.upf.focinhoapp.R
import br.upf.focinhoapp.databinding.ActivityMainBinding
import br.upf.focinhoapp.ui.base.BaseActivity
import br.upf.focinhoapp.ui.login.LoginActivity
import br.upf.focinhoapp.ui.pets.PetAdapter
import br.upf.focinhoapp.ui.pets.PetDetalheActivity
import br.upf.focinhoapp.ui.pets.PetFormActivity
import br.upf.focinhoapp.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Tela principal: lista os pets do usuário logado (o "R" do CRUD).
 *
 * - Toque num pet: abre o detalhe do pet (de lá é possível editar ou excluir).
 * - Botão "Novo pet": abre o formulário em modo cadastro.
 * - Menu "Sair": encerra a sessão e volta para o login.
 */
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sessao by lazy { SessionManager(this) }

    /** Adapter da RecyclerView; recebe o que fazer quando um pet é tocado. */
    private val adapter = PetAdapter { pet ->
        startActivity(PetDetalheActivity.criarIntent(this, pet.id))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Proteção: sem sessão, volta para o login.
        if (!sessao.estaLogado()) {
            navegarPara(LoginActivity::class.java, limparPilha = true)
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)
        configurarToolbar(binding.toolbar, getString(R.string.titulo_meus_pets), mostrarVoltar = false)
        supportActionBar?.subtitle = getString(R.string.ola_usuario, sessao.usuarioNome)

        binding.listaPets.adapter = adapter
        binding.botaoNovoPet.setOnClickListener {
            startActivity(PetFormActivity.criarIntent(this, petId = null))
        }

        observarPets()
    }

    /**
     * Fica "escutando" a lista de pets no banco. repeatOnLifecycle(STARTED)
     * começa a coletar quando a tela fica visível (onStart) e para quando ela
     * some (onStop), economizando recursos. Como o DAO devolve um Flow, toda
     * inserção/edição/exclusão atualiza a lista automaticamente.
     */
    private fun observarPets() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.petRepository.listarPets(sessao.usuarioId).collect { pets ->
                    adapter.submitList(pets)
                    binding.grupoVazio.isVisible = pets.isEmpty()
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Menu da toolbar
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.acao_sair -> {
            confirmarSaida()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun confirmarSaida() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.titulo_sair)
            .setMessage(R.string.msg_sair)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.sair) { _, _ ->
                sessao.encerrarSessao()
                navegarPara(LoginActivity::class.java, limparPilha = true)
            }
            .show()
    }
}
