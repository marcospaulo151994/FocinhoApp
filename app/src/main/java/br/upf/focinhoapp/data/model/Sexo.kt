package br.upf.focinhoapp.data.model

import androidx.annotation.StringRes
import br.upf.focinhoapp.R

/** Sexo do pet. */
enum class Sexo(@StringRes val rotulo: Int) {
    MACHO(R.string.sexo_macho),
    FEMEA(R.string.sexo_femea)
}
