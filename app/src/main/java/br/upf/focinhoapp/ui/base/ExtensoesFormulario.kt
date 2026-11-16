package br.upf.focinhoapp.ui.base

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import br.upf.focinhoapp.R
import br.upf.focinhoapp.util.DateUtils
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

/*
 * Funções de extensão: "adicionam" métodos a classes que já existem
 * (TextInputLayout, EditText), deixando o código das telas mais curto.
 */

/** Some com a mensagem de erro assim que o usuário volta a digitar no campo. */
fun TextInputLayout.limparErroAoDigitar() {
    editText?.doAfterTextChanged { error = null }
}

/** Texto do campo sem espaços nas pontas (nunca nulo). */
fun EditText.textoLimpo(): String = text?.toString()?.trim().orEmpty()

/**
 * Transforma um EditText num campo de data: ao tocar, abre um calendário
 * (DatePickerDialog) e escreve a data escolhida no formato dd/MM/yyyy.
 *
 * O campo deve estar com focusable="false" no XML, para o teclado não abrir.
 *
 * @param dataAtual função que informa a data já selecionada (o calendário abre nela).
 * @param permitirFuturo se false, não deixa escolher datas depois de hoje.
 * @param permitirLimpar mostra o botão "Limpar" (para campos opcionais).
 * @param aoAlterar chamado com a nova data, ou null se o usuário limpou.
 */
fun EditText.configurarSeletorData(
    dataAtual: () -> Long?,
    permitirFuturo: Boolean,
    permitirLimpar: Boolean,
    aoAlterar: (Long?) -> Unit
) {
    setOnClickListener {
        val calendario = Calendar.getInstance()
        dataAtual()?.let { calendario.timeInMillis = it }

        val dialogo = DatePickerDialog(
            context,
            { _, ano, mes, dia ->
                val millis = DateUtils.criarData(ano, mes, dia)
                setText(DateUtils.formatar(millis))
                aoAlterar(millis)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        if (!permitirFuturo) {
            dialogo.datePicker.maxDate = System.currentTimeMillis()
        }
        if (permitirLimpar) {
            dialogo.setButton(DialogInterface.BUTTON_NEUTRAL, context.getString(R.string.limpar)) { _, _ ->
                setText("")
                aoAlterar(null)
            }
        }
        dialogo.show()
    }
}
