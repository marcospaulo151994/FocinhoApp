package br.upf.focinhoapp

import android.app.Application
import br.upf.focinhoapp.data.local.AppDatabase
import br.upf.focinhoapp.data.repository.CuidadoRepository
import br.upf.focinhoapp.data.repository.PetRepository
import br.upf.focinhoapp.data.repository.UsuarioRepository

/**
 * Classe Application: é criada uma única vez, antes de qualquer Activity,
 * e vive enquanto o app estiver aberto. Usamos ela para guardar objetos
 * compartilhados (banco e repositórios), evitando recriá-los em cada tela.
 *
 * `by lazy` faz o objeto ser criado só na primeira vez que for usado.
 *
 * Registrada no AndroidManifest.xml pelo atributo android:name.
 */
class FocinhoApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val usuarioRepository: UsuarioRepository by lazy { UsuarioRepository(database.usuarioDao()) }
    val petRepository: PetRepository by lazy { PetRepository(database.petDao()) }
    val cuidadoRepository: CuidadoRepository by lazy { CuidadoRepository(database.cuidadoDao()) }
}
