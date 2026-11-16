package br.upf.focinhoapp.ui.pets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.upf.focinhoapp.data.model.Pet
import br.upf.focinhoapp.databinding.ItemPetBinding

/**
 * Adapter da lista de pets (RecyclerView).
 *
 * ListAdapter + DiffUtil comparam a lista antiga com a nova e só redesenham
 * os itens que mudaram, com animação, em vez de recarregar tudo.
 *
 * @param aoClicar ação executada quando o usuário toca num pet.
 */
class PetAdapter(
    private val aoClicar: (Pet) -> Unit
) : ListAdapter<Pet, PetAdapter.PetViewHolder>(DIFF) {

    /** Guarda as views de um item da lista (layout item_pet.xml). */
    class PetViewHolder(
        private val binding: ItemPetBinding,
        private val aoClicar: (Pet) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /** Preenche o item com os dados do [pet]. */
        fun vincular(pet: Pet) {
            val contexto = binding.root.context

            binding.textoEmoji.text = pet.especie.emoji
            binding.textoNome.text = pet.nome

            // Ex.: "Cão · Labrador · 2 anos e 3 meses" (partes vazias são ignoradas)
            binding.textoDetalhes.text = listOfNotNull(
                contexto.getString(pet.especie.rotulo),
                pet.raca,
                pet.descricaoIdade()
            ).joinToString(" · ")

            binding.root.setOnClickListener { aoClicar(pet) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ItemPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetViewHolder(binding, aoClicar)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.vincular(getItem(position))
    }

    companion object {
        /** Regras para o DiffUtil saber se dois itens são o mesmo pet e se mudaram. */
        private val DIFF = object : DiffUtil.ItemCallback<Pet>() {
            override fun areItemsTheSame(antigo: Pet, novo: Pet) = antigo.id == novo.id
            override fun areContentsTheSame(antigo: Pet, novo: Pet) = antigo == novo
        }
    }
}
