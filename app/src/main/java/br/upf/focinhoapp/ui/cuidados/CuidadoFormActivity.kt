package br.upf.focinhoapp.ui.cuidados

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import br.upf.focinhoapp.R
import br.upf.focinhoapp.data.model.Cuidado
import br.upf.focinhoapp.data.model.TipoCuidado
import br.upf.focinhoapp.databinding.ActivityCuidadoFormBinding
import br.upf.focinhoapp.ui.base.BaseActivity
import br.upf.focinhoapp.ui.base.configurarSeletorData
import br.upf.focinhoapp.ui.base.limparErroAoDigitar
import br.upf.focinhoapp.ui.base.textoLimpo
import br.upf.focinhoapp.util.DateUtils
import br.upf.focinhoapp.util.Validacoes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Formulário de cuidado (vacina, vermífugo ou consulta). Segue a mesma ideia
 * do formulário de pet; a mesma tela serve para:
 * - **Create:** aberta sem id -> cadastra um registro novo;
 * - **Update:** aberta com id -> carrega o registro e salva as alterações;
 * - **Delete:** no modo edição, o ícone de lixeira exclui o registro.
 *
 * A tela é uma só para os três tipos: o [TipoCuidado] recebido pela Intent
 * define os rótulos dos campos (ex.: "Nome da vacina" ou "Motivo da consulta").
 *
 * Use [criarIntent] para abrir esta tela.
 */
class CuidadoFormActivity : BaseActivity() {

    private lateinit var binding: ActivityCuidadoFormBinding

    /** Pet dono do registro, recebido pela Intent. */
    private val petId: Long by lazy { intent.getLongExtra(EXTRA_PET_ID, SEM_ID) }

    /** Tipo do registro, recebido pela Intent (o enum viaja como texto). */
    private val tipo: TipoCuidado by lazy {
        TipoCuidado.valueOf(intent.getStringExtra(EXTRA_TIPO).orEmpty())
    }

    /** Id recebido pela Intent; null = registro novo. */
    private var cuidadoId: Long? = null

    /** Cuidado como está no banco (só no modo edição). */
    private var cuidadoOriginal: Cuidado? = null

    /** Datas escolhidas nos calendários (millis) ou null. */
    private var dataSelecionada: Long? = null
    private var proximaDataSelecionada: Long? = null

    private val modoEdicao: Boolean
        get() = cuidadoId != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuidadoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)

        cuidadoId = intent.getLongExtra(EXTRA_CUIDADO_ID, SEM_ID).takeIf { it != SEM_ID }

        val titulo = if (modoEdicao) R.string.titulo_editar_registro else R.string.titulo_novo_registro
        configurarToolbar(binding.toolbar, getString(titulo))
        // O subtítulo mostra em qual lista o registro entra (ex.: "Vacinas").
        supportActionBar?.subtitle = getString(tipo.titulo)

        // Ciclo de vida: ao girar a tela a Activity é recriada. Os textos dos
        // campos o Android restaura sozinho, mas as variáveis das datas não;
        // por isso salvamos em onSaveInstanceState e recuperamos aqui.
        if (savedInstanceState?.containsKey(ESTADO_DATA) == true) {
            dataSelecionada = savedInstanceState.getLong(ESTADO_DATA)
        }
        if (savedInstanceState?.containsKey(ESTADO_PROXIMA_DATA) == true) {
            proximaDataSelecionada = savedInstanceState.getLong(ESTADO_PROXIMA_DATA)
        }

        configurarCampos()
        binding.botaoSalvar.setOnClickListener { salvar() }

        // Só preenche os campos na primeira abertura; após rotação eles já
        // foram restaurados e sobrescrever apagaria o que o usuário digitou.
        cuidadoId?.let { carregarCuidado(it, preencherCampos = savedInstanceState == null) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dataSelecionada?.let { outState.putLong(ESTADO_DATA, it) }
        proximaDataSelecionada?.let { outState.putLong(ESTADO_PROXIMA_DATA, it) }
    }

    // ------------------------------------------------------------------
    // Configuração dos campos
    // ------------------------------------------------------------------

    private fun configurarCampos() = with(binding) {
        // Rótulos que mudam conforme o tipo (vêm do enum TipoCuidado).
        layoutTitulo.hint = getString(tipo.rotuloTitulo)
        layoutData.hint = getString(tipo.rotuloData)
        layoutProximaData.hint =
            getString(R.string.rotulo_opcional, getString(tipo.rotuloProximaData))
        layoutDetalhe.hint = getString(R.string.rotulo_opcional, getString(tipo.rotuloDetalhe))

        listOf(layoutTitulo, layoutData, layoutProximaData).forEach { it.limparErroAoDigitar() }

        // Data do cuidado: obrigatória, por isso não tem o botão "Limpar".
        campoData.configurarSeletorData(
            dataAtual = { dataSelecionada },
            permitirFuturo = tipo.permiteDataFutura,
            permitirLimpar = false
        ) { novaData ->
            dataSelecionada = novaData
            layoutData.error = null
        }

        // Próxima data: opcional e normalmente no futuro.
        campoProximaData.configurarSeletorData(
            dataAtual = { proximaDataSelecionada },
            permitirFuturo = true,
            permitirLimpar = true
        ) { novaData ->
            proximaDataSelecionada = novaData
            layoutProximaData.error = null
        }
    }

    // ------------------------------------------------------------------
    // Carregar (modo edição)
    // ------------------------------------------------------------------

    private fun carregarCuidado(id: Long, preencherCampos: Boolean) {
        lifecycleScope.launch {
            val cuidado = app.cuidadoRepository.buscarPorId(id)
            if (cuidado == null) {
                mostrarMensagem(getString(R.string.erro_registro_nao_encontrado))
                finish()
                return@launch
            }
            cuidadoOriginal = cuidado
            if (preencherCampos) exibirCuidado(cuidado)
        }
    }

    private fun exibirCuidado(cuidado: Cuidado) = with(binding) {
        campoTitulo.setText(cuidado.titulo)
        dataSelecionada = cuidado.data
        campoData.setText(DateUtils.formatar(cuidado.data))
        proximaDataSelecionada = cuidado.proximaData
        campoProximaData.setText(cuidado.proximaData?.let { DateUtils.formatar(it) }.orEmpty())
        campoVeterinario.setText(cuidado.veterinario.orEmpty())
        campoDetalhe.setText(cuidado.detalhe.orEmpty())
        campoObservacoes.setText(cuidado.observacoes.orEmpty())
    }

    // ------------------------------------------------------------------
    // Salvar (Create / Update)
    // ------------------------------------------------------------------

    private fun salvar() {
        // No modo edição, espera o registro terminar de carregar antes de salvar.
        if (modoEdicao && cuidadoOriginal == null) return

        val cuidado = montarCuidadoValidado() ?: return

        binding.botaoSalvar.isEnabled = false
        lifecycleScope.launch {
            if (modoEdicao) {
                app.cuidadoRepository.atualizar(cuidado)
            } else {
                app.cuidadoRepository.inserir(cuidado)
            }
            mostrarMensagem(getString(R.string.msg_registro_salvo))
            finish() // volta para a lista, que se atualiza sozinha (Flow)
        }
    }

    /**
     * Lê e valida o formulário. Marca os erros nos campos e devolve null se
     * algo estiver inválido; caso contrário devolve o [Cuidado] pronto para salvar.
     */
    private fun montarCuidadoValidado(): Cuidado? = with(binding) {
        var valido = true

        // Título (obrigatório)
        val titulo = campoTitulo.textoLimpo()
        if (!Validacoes.nomeValido(titulo)) {
            layoutTitulo.error = getString(R.string.erro_titulo_cuidado)
            valido = false
        }

        // Data (obrigatória). Vacina e vermífugo não aceitam data no futuro.
        val data = dataSelecionada
        if (data == null) {
            layoutData.error = getString(R.string.erro_data_obrigatoria)
            valido = false
        } else if (!tipo.permiteDataFutura && !Validacoes.dataNaoFutura(data)) {
            layoutData.error = getString(R.string.erro_data_futura)
            valido = false
        }

        // Próxima data (opcional, mas precisa ser depois da data do cuidado)
        val proximaData = proximaDataSelecionada
        if (data != null && proximaData != null && !Validacoes.proximaDataValida(data, proximaData)) {
            layoutProximaData.error = getString(R.string.erro_proxima_data)
            valido = false
        }

        // A checagem de null abaixo também permite ao Kotlin tratar a data
        // como não nula daqui para frente (smart cast).
        if (!valido || data == null) return null

        val veterinario = campoVeterinario.textoLimpo().ifBlank { null }
        val detalhe = campoDetalhe.textoLimpo().ifBlank { null }
        val observacoes = campoObservacoes.textoLimpo().ifBlank { null }

        // Edição: copia o original (mantém id, pet e tipo) trocando os campos.
        // Cadastro: cria um Cuidado novo para o pet e o tipo recebidos.
        cuidadoOriginal?.copy(
            titulo = titulo,
            data = data,
            proximaData = proximaData,
            veterinario = veterinario,
            detalhe = detalhe,
            observacoes = observacoes
        ) ?: Cuidado(
            petId = petId,
            tipo = tipo,
            titulo = titulo,
            data = data,
            proximaData = proximaData,
            veterinario = veterinario,
            detalhe = detalhe,
            observacoes = observacoes
        )
    }

    // ------------------------------------------------------------------
    // Excluir (Delete)
    // ------------------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // A lixeira só aparece quando estamos editando um registro existente.
        // O menu é o mesmo do formulário de pet (só tem a ação de excluir).
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
        val cuidado = cuidadoOriginal ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.titulo_excluir_registro)
            .setMessage(getString(R.string.msg_excluir_registro, cuidado.titulo))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.excluir) { _, _ ->
                lifecycleScope.launch {
                    app.cuidadoRepository.excluir(cuidado)
                    mostrarMensagem(getString(R.string.msg_registro_excluido))
                    finish()
                }
            }
            .show()
    }

    companion object {
        private const val EXTRA_PET_ID = "extra_pet_id"
        private const val EXTRA_TIPO = "extra_tipo"
        private const val EXTRA_CUIDADO_ID = "extra_cuidado_id"
        private const val ESTADO_DATA = "estado_data"
        private const val ESTADO_PROXIMA_DATA = "estado_proxima_data"
        private const val SEM_ID = -1L

        /**
         * Cria a Intent para abrir o formulário.
         * @param petId pet dono do registro.
         * @param tipo vacina, vermífugo ou consulta.
         * @param cuidadoId id do registro a editar, ou null para cadastrar um novo.
         */
        fun criarIntent(
            context: Context,
            petId: Long,
            tipo: TipoCuidado,
            cuidadoId: Long?
        ): Intent =
            Intent(context, CuidadoFormActivity::class.java).apply {
                putExtra(EXTRA_PET_ID, petId)
                putExtra(EXTRA_TIPO, tipo.name)
                cuidadoId?.let { putExtra(EXTRA_CUIDADO_ID, it) }
            }
    }
}
