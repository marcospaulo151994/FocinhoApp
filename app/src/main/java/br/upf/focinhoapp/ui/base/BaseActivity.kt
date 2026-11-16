package br.upf.focinhoapp.ui.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.upf.focinhoapp.FocinhoApplication
import com.google.android.material.appbar.MaterialToolbar

/**
 * Activity base de todas as telas do app. Centraliza o que é comum:
 *
 * 1. **Ciclo de vida:** cada método (onCreate, onStart, onResume, onPause,
 *    onStop, onRestart, onDestroy) escreve no Logcat. Filtre por "CicloVida"
 *    no Android Studio para ver a ordem em que o Android chama cada um ao
 *    abrir telas, girar o celular, voltar, minimizar o app etc.
 * 2. **Toolbar** padronizada (título e botão de voltar), para manter a
 *    navegação consistente entre as telas.
 * 3. **Edge-to-edge:** a partir do Android 15 o app desenha atrás das barras
 *    do sistema; [aplicarInsets] adiciona o espaçamento certo.
 * 4. Atalhos para o [FocinhoApplication] (repositórios) e mensagens.
 */
abstract class BaseActivity : AppCompatActivity() {

    /** Tag do Logcat, ex.: "CicloVida/LoginActivity". */
    private val tagLog: String
        get() = "CicloVida/${javaClass.simpleName}"

    /** Acesso aos repositórios compartilhados. */
    protected val app: FocinhoApplication
        get() = application as FocinhoApplication

    // ------------------------------------------------------------------
    // Ciclo de vida
    // ------------------------------------------------------------------

    /** Tela sendo criada: aqui inflamos o layout e configuramos as views. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d(tagLog, "onCreate (estado salvo? ${savedInstanceState != null})")
    }

    /** Tela ficando visível. */
    override fun onStart() {
        super.onStart()
        Log.d(tagLog, "onStart")
    }

    /** Tela em primeiro plano, recebendo toques. */
    override fun onResume() {
        super.onResume()
        Log.d(tagLog, "onResume")
    }

    /** Outra tela/diálogo vai para a frente; a tela perde o foco. */
    override fun onPause() {
        super.onPause()
        Log.d(tagLog, "onPause")
    }

    /** Tela não está mais visível (foi para trás ou o app foi minimizado). */
    override fun onStop() {
        super.onStop()
        Log.d(tagLog, "onStop")
    }

    /** Tela parada voltando a ser exibida (antes de onStart). */
    override fun onRestart() {
        super.onRestart()
        Log.d(tagLog, "onRestart")
    }

    /** Tela sendo destruída (finish(), botão voltar ou rotação). */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(tagLog, "onDestroy (finalizando? $isFinishing)")
    }

    // ------------------------------------------------------------------
    // Utilitários para as telas
    // ------------------------------------------------------------------

    /**
     * Usa a [toolbar] do layout como ActionBar da tela.
     * @param mostrarVoltar exibe a seta de voltar (false na tela inicial).
     */
    protected fun configurarToolbar(
        toolbar: MaterialToolbar,
        titulo: String? = null,
        mostrarVoltar: Boolean = true
    ) {
        setSupportActionBar(toolbar)
        titulo?.let { supportActionBar?.title = it }
        supportActionBar?.setDisplayHomeAsUpEnabled(mostrarVoltar)
    }

    /** Seta de voltar da toolbar: faz o mesmo que o botão voltar do sistema. */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * Adiciona à [raiz] um espaçamento igual ao tamanho das barras do sistema
     * (status bar, barra de navegação) e do teclado, para nada ficar escondido.
     */
    protected fun aplicarInsets(raiz: View) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz) { view, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
    }

    protected fun mostrarMensagem(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
    }

    /**
     * Abre outra Activity. Com [limparPilha] = true, as telas anteriores são
     * removidas (usado após login/logout, para o "voltar" não retornar a elas).
     */
    protected fun navegarPara(destino: Class<*>, limparPilha: Boolean = false) {
        val intent = Intent(this, destino)
        if (limparPilha) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        if (limparPilha) finish()
    }
}
