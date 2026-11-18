package br.upf.focinhoapp.ui.cuidados

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.upf.focinhoapp.FocinhoApplication
import br.upf.focinhoapp.data.model.TipoCuidado
import br.upf.focinhoapp.databinding.FragmentCuidadoListBinding
import kotlinx.coroutines.launch

/**
 * Lista os cuidados de um pet para um tipo: vacinas, vermífugos ou consultas.
 *
 * Um Fragment é um "pedaço de tela" reutilizável que vive dentro de uma
 * Activity. Este mesmo Fragment é usado nas três abas do detalhe do pet; o que
 * muda de uma aba para outra são os argumentos (o pet e o tipo).
 *
 * Use [novaInstancia] para criar, nunca o construtor com parâmetros: o Android
 * recria o Fragment sozinho (ex.: ao girar a tela) e só os argumentos são
 * preservados.
 */
class CuidadoListFragment : Fragment() {

    // No Fragment a view pode ser destruída enquanto o Fragment continua vivo
    // (ex.: ao trocar de aba). Por isso o binding só vale entre onCreateView e
    // onDestroyView, e é zerado no final para não segurar views antigas.
    private var _binding: FragmentCuidadoListBinding? = null
    private val binding get() = _binding!!

    private val petId: Long by lazy { requireArguments().getLong(ARG_PET_ID) }

    private val tipo: TipoCuidado by lazy {
        TipoCuidado.valueOf(requireArguments().getString(ARG_TIPO).orEmpty())
    }

    /** O Fragment infla o próprio layout aqui (na Activity isso é feito no onCreate). */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuidadoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Com a view pronta, configuramos a lista e começamos a observar o banco. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toque num registro: abre o formulário em modo edição.
        val adapter = CuidadoAdapter { cuidado -> abrirFormulario(cuidado.id) }
        binding.listaCuidados.adapter = adapter

        // Botão "Novo registro": abre o formulário em modo cadastro.
        binding.botaoNovo.setOnClickListener { abrirFormulario(cuidadoId = null) }

        observarCuidados(adapter)
    }

    /**
     * Abre o [CuidadoFormActivity] já com o pet e o tipo desta aba.
     * @param cuidadoId registro a editar, ou null para cadastrar um novo.
     */
    private fun abrirFormulario(cuidadoId: Long?) {
        startActivity(CuidadoFormActivity.criarIntent(requireContext(), petId, tipo, cuidadoId))
    }

    /**
     * Fica "escutando" os cuidados deste pet e tipo no banco. Usamos o ciclo de
     * vida da view (viewLifecycleOwner), e não o do Fragment, para a coleta
     * parar junto com a view. Como o DAO devolve um Flow, toda inserção, edição
     * ou exclusão atualiza a lista automaticamente.
     */
    private fun observarCuidados(adapter: CuidadoAdapter) {
        val repositorio = (requireActivity().application as FocinhoApplication).cuidadoRepository

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repositorio.listar(petId, tipo).collect { cuidados ->
                    adapter.submitList(cuidados)
                    binding.grupoVazio.isVisible = cuidados.isEmpty()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PET_ID = "arg_pet_id"
        private const val ARG_TIPO = "arg_tipo"

        /**
         * Cria o Fragment já com os argumentos.
         * @param petId pet dono dos registros.
         * @param tipo qual lista mostrar (vacinas, vermífugos ou consultas).
         */
        fun novaInstancia(petId: Long, tipo: TipoCuidado): CuidadoListFragment =
            CuidadoListFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PET_ID, petId)
                    // O enum vai como texto (name), que é um tipo simples aceito pelo Bundle.
                    putString(ARG_TIPO, tipo.name)
                }
            }
    }
}
