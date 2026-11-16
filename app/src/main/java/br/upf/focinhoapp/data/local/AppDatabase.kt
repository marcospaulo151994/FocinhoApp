package br.upf.focinhoapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.upf.focinhoapp.data.dao.CuidadoDao
import br.upf.focinhoapp.data.dao.PetDao
import br.upf.focinhoapp.data.dao.UsuarioDao
import br.upf.focinhoapp.data.model.Cuidado
import br.upf.focinhoapp.data.model.Pet
import br.upf.focinhoapp.data.model.Usuario

/**
 * Banco de dados local do app (SQLite gerenciado pelo Room).
 *
 * - `entities`: as tabelas do banco.
 * - `version`: deve ser incrementada sempre que a estrutura das tabelas mudar.
 * - `exportSchema = false`: não gera arquivos JSON de schema (desnecessário aqui).
 */
@Database(
    entities = [Usuario::class, Pet::class, Cuidado::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun petDao(): PetDao
    abstract fun cuidadoDao(): CuidadoDao

    companion object {
        private const val NOME_ARQUIVO = "focinho.db"

        // @Volatile garante que todas as threads vejam o valor mais atual.
        @Volatile
        private var instancia: AppDatabase? = null

        /**
         * Devolve a instância única (Singleton) do banco. Abrir o banco é caro,
         * então criamos uma vez só e reaproveitamos no app inteiro.
         */
        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOME_ARQUIVO
                )
                    // Durante o desenvolvimento: se a versão mudar e não houver
                    // migração, o banco é recriado do zero (os dados de teste se perdem).
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instancia = it }
            }
    }
}
