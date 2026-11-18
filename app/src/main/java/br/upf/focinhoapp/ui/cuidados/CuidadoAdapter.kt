package br.upf.focinhoapp.ui.cuidados

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.upf.focinhoapp.R
import br.upf.focinhoapp.data.model.Cuidado
import br.upf.focinhoapp.data.model.SituacaoCuidado
import br.upf.focinhoapp.databinding.ItemCuidadoBinding
import br.upf.focinhoapp.util.DateUtils
import com.google.android.material.color.MaterialColors

/**
 * Adapter da lista de cuidados (RecyclerView), no mesmo padrão do PetAdapter:
 * ListAdapter + DiffUtil só redesenham os itens que mudaram.
 */
class CuidadoAdapter : ListAdapter<Cuidado, CuidadoAdapter.CuidadoViewHolder>(DIFF) {

    /** Guarda as views de um item da lista (layout item_cuidado.xml). */
    class CuidadoViewHolder(
        private val binding: ItemCuidadoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /** Preenche o item com os dados do [cuidado]. */
        fun vincular(cuidado: Cuidado) {
            val contexto = binding.root.context

            binding.textoTitulo.text = cuidado.titulo
            binding.textoData.text = DateUtils.formatar(cuidado.data)

            // A segunda linha só aparece quando há próxima data cadastrada.
            val proxima = cuidado.proximaData
            binding.grupoProxima.isVisible = proxima != null
            if (proxima != null) {
                // Ex.: "Próxima dose: 12/05/2027" (o rótulo muda conforme o tipo)
                binding.textoProxima.text = contexto.getString(
                    R.string.cuidado_proxima_data,
                    contexto.getString(cuidado.tipo.rotuloProximaData),
                    DateUtils.formatar(proxima)
                )
                exibirSituacao(cuidado.situacao())
            }
        }

        /**
         * Mostra a situação da próxima data com texto e cor. As cores vêm do
         * tema (erro, primária, secundária), então funcionam no claro e no escuro.
         */
        private fun exibirSituacao(situacao: SituacaoCuidado) {
            val (texto, atributoCor) = when (situacao) {
                SituacaoCuidado.ATRASADO ->
                    R.string.situacao_atrasado to androidx.appcompat.R.attr.colorError
                SituacaoCuidado.PROXIMO ->
                    R.string.situacao_proximo to androidx.appcompat.R.attr.colorPrimary
                SituacaoCuidado.EM_DIA, SituacaoCuidado.SEM_PREVISAO ->
                    R.string.situacao_em_dia to com.google.android.material.R.attr.colorSecondary
            }
            binding.textoSituacao.setText(texto)
            binding.textoSituacao.setTextColor(
                MaterialColors.getColor(binding.textoSituacao, atributoCor)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuidadoViewHolder {
        val binding = ItemCuidadoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuidadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuidadoViewHolder, position: Int) {
        holder.vincular(getItem(position))
    }

    companion object {
        /** Regras para o DiffUtil saber se dois itens são o mesmo cuidado e se mudaram. */
        private val DIFF = object : DiffUtil.ItemCallback<Cuidado>() {
            override fun areItemsTheSame(antigo: Cuidado, novo: Cuidado) = antigo.id == novo.id
            override fun areContentsTheSame(antigo: Cuidado, novo: Cuidado) = antigo == novo
        }
    }
}
