package br.upf.focinhoapp.ui.pets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import br.upf.focinhoapp.R
import br.upf.focinhoapp.data.model.Especie
import br.upf.focinhoapp.data.model.Pet
import br.upf.focinhoapp.data.model.Sexo
import br.upf.focinhoapp.databinding.ActivityPetFormBinding
import br.upf.focinhoapp.ui.base.BaseActivity
import br.upf.focinhoapp.ui.base.configurarSeletorData
import br.upf.focinhoapp.ui.base.limparErroAoDigitar
import br.upf.focinhoapp.ui.base.textoLimpo
import br.upf.focinhoapp.util.DateUtils
import br.upf.focinhoapp.util.SessionManager
import br.upf.focinhoapp.util.Validacoes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Formulário de pet. A mesma tela serve para:
 * - **Create:** aberta sem id -> cadastra um pet novo;
 * - **Update:** aberta com id -> carrega o pet e salva as alterações;
 * - **Delete:** no modo edição, o ícone de lixeira exclui o pet.
 *
 * Use [criarIntent] para abrir esta tela.
 */
class PetFormActivity : BaseActivity() {

    private lateinit var binding: ActivityPetFormBinding
    private val sessao by lazy { SessionManager(this) }

    /** Id recebido pela Intent; null = cadastro novo. */
    private var petId: Long? = null

    /** Pet como está no banco (só no modo edição). */
    private var petOriginal: Pet? = null

    /** Data de nascimento escolhida no calendário (millis) ou null. */
    private var dataNascimento: Long? = null

    private val modoEdicao: Boolean
        get() = petId != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)

        petId = intent.getLongExtra(EXTRA_PET_ID, SEM_ID).takeIf { it != SEM_ID }

        val titulo = if (modoEdicao) R.string.titulo_editar_pet else R.string.titulo_novo_pet
        configurarToolbar(binding.toolbar, getString(titulo))

        // Ciclo de vida: ao girar a tela a Activity é recriada. Os textos dos
        // campos o Android restaura sozinho, mas variáveis como a data não;
        // por isso salvamos em onSaveInstanceState e recuperamos aqui.
        if (savedInstanceState?.containsKey(ESTADO_DATA_NASCIMENTO) == true) {
            dataNascimento = savedInstanceState.getLong(ESTADO_DATA_NASCIMENTO)
        }

        configurarCampos()
        binding.botaoSalvar.setOnClickListener { salvar() }

        // Só preenche os campos na primeira abertura; após rotação eles já
        // foram restaurados e sobrescrever apagaria o que o usuário digitou.
        petId?.let { carregarPet(it, preencherCampos = savedInstanceState == null) }
    }

    override fun onResume() {
        super.onResume()
        // O adapter do dropdown é (re)definido no onResume porque, depois de uma
        // rotação, o AutoCompleteTextView restaura o texto e filtra a lista,
        // mostrando só a opção já escolhida. Recriar o adapter aqui evita isso.
        configurarDropdownEspecie()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dataNascimento?.let { outState.putLong(ESTADO_DATA_NASCIMENTO, it) }
    }

    // ------------------------------------------------------------------
    // Configuração dos campos
    // ------------------------------------------------------------------

    private fun configurarCampos() = with(binding) {
        listOf(layoutNome, layoutEspecie, layoutNascimento, layoutPeso)
            .forEach { it.limparErroAoDigitar() }

        grupoSexo.setOnCheckedChangeListener { _, _ -> textoErroSexo.isVisible = false }

        campoNascimento.configurarSeletorData(
            dataAtual = { dataNascimento },
            permitirFuturo = false,
            permitirLimpar = true
        ) { novaData ->
            dataNascimento = novaData
            layoutNascimento.error = null
        }
    }

    private fun configurarDropdownEspecie() {
        val opcoes = Especie.entries.map { getString(it.rotulo) }
        binding.campoEspecie.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, opcoes)
        )
    }

    // ------------------------------------------------------------------
    // Carregar (modo edição)
    // ------------------------------------------------------------------

    private fun carregarPet(id: Long, preencherCampos: Boolean) {
        lifecycleScope.launch {
            val pet = app.petRepository.buscarPorId(id)
            if (pet == null) {
                mostrarMensagem(getString(R.string.erro_pet_nao_encontrado))
                finish()
                return@launch
            }
            petOriginal = pet
            if (preencherCampos) exibirPet(pet)
        }
    }

    private fun exibirPet(pet: Pet) = with(binding) {
        campoNome.setText(pet.nome)
        // O "false" evita que o dropdown abra/filtre ao definir o texto.
        campoEspecie.setText(getString(pet.especie.rotulo), false)
        campoRaca.setText(pet.raca.orEmpty())
        grupoSexo.check(
            when (pet.sexo) {
                Sexo.MACHO -> R.id.radio_macho
                Sexo.FEMEA -> R.id.radio_femea
            }
        )
        dataNascimento = pet.dataNascimento
        campoNascimento.setText(pet.dataNascimento?.let { DateUtils.formatar(it) }.orEmpty())
        campoPeso.setText(pet.pesoKg?.toString().orEmpty())
        campoObservacoes.setText(pet.observacoes.orEmpty())
    }

    // ------------------------------------------------------------------
    // Salvar (Create / Update)
    // ------------------------------------------------------------------

    private fun salvar() {
        // No modo edição, espera o pet terminar de carregar antes de salvar.
        if (modoEdicao && petOriginal == null) return

        val pet = montarPetValidado() ?: return

        binding.botaoSalvar.isEnabled = false
        lifecycleScope.launch {
            if (modoEdicao) {
                app.petRepository.atualizar(pet)
            } else {
                app.petRepository.inserir(pet)
            }
            mostrarMensagem(getString(R.string.msg_pet_salvo, pet.nome))
            finish() // volta para a lista, que se atualiza sozinha (Flow)
        }
    }

    /**
     * Lê e valida o formulário. Marca os erros nos campos e devolve null se
     * algo estiver inválido; caso contrário devolve o [Pet] pronto para salvar.
     */
    private fun montarPetValidado(): Pet? = with(binding) {
        var valido = true

        // Nome (obrigatório)
        val nome = campoNome.textoLimpo()
        if (!Validacoes.nomeValido(nome)) {
            layoutNome.error = getString(R.string.erro_nome_pet)
            valido = false
        }

        // Espécie (obrigatória): converte o texto do dropdown de volta no enum
        val textoEspecie = campoEspecie.text.toString()
        val especie = Especie.entries.firstOrNull { getString(it.rotulo) == textoEspecie }
        if (especie == null) {
            layoutEspecie.error = getString(R.string.erro_especie)
            valido = false
        }

        // Sexo (obrigatório)
        val sexo = when (grupoSexo.checkedRadioButtonId) {
            R.id.radio_macho -> Sexo.MACHO
            R.id.radio_femea -> Sexo.FEMEA
            else -> null
        }
        if (sexo == null) {
            textoErroSexo.isVisible = true
            valido = false
        }

        // Nascimento (opcional, mas não pode ser no futuro)
        val nascimento = dataNascimento
        if (nascimento != null && !Validacoes.dataNaoFutura(nascimento)) {
            layoutNascimento.error = getString(R.string.erro_data_futura)
            valido = false
        }

        // Peso (opcional, mas se preenchido precisa ser um número realista)
        val textoPeso = campoPeso.textoLimpo()
        val peso = Validacoes.converterPeso(textoPeso)
        if (textoPeso.isNotEmpty() && (peso == null || !Validacoes.pesoValido(peso))) {
            layoutPeso.error = getString(R.string.erro_peso, Validacoes.PESO_MAXIMO_KG.toInt())
            valido = false
        }

        // As checagens de null abaixo também permitem ao Kotlin tratar
        // especie e sexo como não nulos daqui para frente (smart cast).
        if (!valido || especie == null || sexo == null) return null

        val raca = campoRaca.textoLimpo().ifBlank { null }
        val observacoes = campoObservacoes.textoLimpo().ifBlank { null }

        // Edição: copia o original (mantém id e dono) trocando os campos.
        // Cadastro: cria um Pet novo para o usuário logado.
        petOriginal?.copy(
            nome = nome,
            especie = especie,
            raca = raca,
            sexo = sexo,
            dataNascimento = nascimento,
            pesoKg = peso,
            observacoes = observacoes
        ) ?: Pet(
            usuarioId = sessao.usuarioId,
            nome = nome,
            especie = especie,
            raca = raca,
            sexo = sexo,
            dataNascimento = nascimento,
            pesoKg = peso,
            observacoes = observacoes
        )
    }

    // ------------------------------------------------------------------
    // Excluir (Delete)
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // A lixeira só aparece quando estamos editando um pet existente.
        if (modoEdicao) menuInflater.inflate(R.menu.menu_pet_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.acao_excluir -> {
            confirmarExclusao()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /** Pede confirmação antes de excluir, pois a ação não pode ser desfeita. */
    private fun confirmarExclusao() {
        val pet = petOriginal ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.titulo_excluir_pet)
            .setMessage(getString(R.string.msg_excluir_pet, pet.nome))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.excluir) { _, _ ->
                lifecycleScope.launch {
                    app.petRepository.excluir(pet)
                    mostrarMensagem(getString(R.string.msg_pet_excluido, pet.nome))
                    finish()
                }
            }
            .show()
    }

    companion object {
        private const val EXTRA_PET_ID = "extra_pet_id"
        private const val ESTADO_DATA_NASCIMENTO = "estado_data_nascimento"
        private const val SEM_ID = -1L

        /**
         * Cria a Intent para abrir o formulário.
         * @param petId id do pet a editar, ou null para cadastrar um novo.
         */
        fun criarIntent(context: Context, petId: Long?): Intent =
            Intent(context, PetFormActivity::class.java).apply {
                petId?.let { putExtra(EXTRA_PET_ID, it) }
            }
    }
}
