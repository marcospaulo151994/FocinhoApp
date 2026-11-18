package br.upf.focinhoapp.ui.pets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.upf.focinhoapp.R
import br.upf.focinhoapp.data.model.Pet
import br.upf.focinhoapp.data.model.TipoCuidado
import br.upf.focinhoapp.databinding.ActivityPetDetalheBinding
import br.upf.focinhoapp.ui.base.BaseActivity
import br.upf.focinhoapp.ui.cuidados.CuidadoListFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

/**
 * Detalhe de um pet: mostra os dados dele na toolbar e, em três abas, as
 * vacinas, os vermífugos e as consultas. Cada aba é um [CuidadoListFragment].
 *
 * O lápis da toolbar abre o [PetFormActivity], onde o pet pode ser editado ou
 * excluído.
 *
 * Use [criarIntent] para abrir esta tela.
 */
class PetDetalheActivity : BaseActivity() {

    private lateinit var binding: ActivityPetDetalheBinding

    /** Id do pet recebido pela Intent. */
    private val petId: Long by lazy { intent.getLongExtra(EXTRA_PET_ID, SEM_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)

        // O título começa vazio e é preenchido quando o pet chega do banco.
        configurarToolbar(binding.toolbar, titulo = "")

        configurarAbas()
        observarPet()
    }

    /**
     * Liga as abas (TabLayout) às páginas (ViewPager2). Há uma aba para cada
     * [TipoCuidado], na ordem em que aparecem no enum.
     */
    private fun configurarAbas() {
        val tipos = TipoCuidado.entries

        binding.paginas.adapter = CuidadosPagerAdapter(this, petId, tipos)

        // O TabLayoutMediator mantém os dois sincronizados: tocar numa aba troca
        // a página, e arrastar a página para o lado troca a aba selecionada.
        TabLayoutMediator(binding.abas, binding.paginas) { aba, posicao ->
            aba.setText(tipos[posicao].titulo)
        }.attach()
    }

    /**
     * Observa o pet no banco. Como é um Flow, a toolbar se atualiza sozinha
     * depois de uma edição. Se o pet deixar de existir (foi excluído pelo
     * formulário), não há o que mostrar, então a tela fecha.
     */
    private fun observarPet() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.petRepository.observarPet(petId).collect { pet ->
                    if (pet == null) {
                        finish()
                    } else {
                        exibirPet(pet)
                    }
                }
            }
        }
    }

    private fun exibirPet(pet: Pet) {
        supportActionBar?.title = pet.nome
        // Ex.: "Cão · Labrador · 2 anos e 3 meses" (partes vazias são ignoradas)
        supportActionBar?.subtitle = listOfNotNull(
            getString(pet.especie.rotulo),
            pet.raca,
            pet.descricaoIdade()
        ).joinToString(" · ")
    }

    // ------------------------------------------------------------------
    // Menu da toolbar
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pet_detalhe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.acao_editar -> {
            startActivity(PetFormActivity.criarIntent(this, petId))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        private const val EXTRA_PET_ID = "extra_pet_id"
        private const val SEM_ID = -1L

        /** Cria a Intent para abrir o detalhe do pet com o [petId] informado. */
        fun criarIntent(context: Context, petId: Long): Intent =
            Intent(context, PetDetalheActivity::class.java).apply {
                putExtra(EXTRA_PET_ID, petId)
            }
    }
}

/**
 * Adapter das páginas do ViewPager2: cria um [CuidadoListFragment] para cada
 * tipo de cuidado. É o mesmo Fragment nas três abas, mudando só o tipo.
 */
private class CuidadosPagerAdapter(
    activity: FragmentActivity,
    private val petId: Long,
    private val tipos: List<TipoCuidado>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tipos.size

    override fun createFragment(position: Int): Fragment =
        CuidadoListFragment.novaInstancia(petId, tipos[position])
}
