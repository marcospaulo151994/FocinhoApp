package br.upf.focinhoapp.data.model

import androidx.annotation.StringRes
import br.upf.focinhoapp.R

/**
 * Espécies de pet suportadas pelo app.
 *
 * @property rotulo texto exibido na interface (vem do strings.xml).
 * @property emoji ícone usado como "avatar" do pet nas listas.
 */
enum class Especie(@StringRes val rotulo: Int, val emoji: String) {
    CAO(R.string.especie_cao, "🐶"),
    GATO(R.string.especie_gato, "🐱"),
    OUTRO(R.string.especie_outro, "🐾")
}
