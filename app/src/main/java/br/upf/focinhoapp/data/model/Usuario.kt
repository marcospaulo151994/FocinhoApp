package br.upf.focinhoapp.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tutor (dono) dos pets. Cada usuário enxerga apenas os próprios animais.
 *
 * O login é "simulado" no sentido de que não há servidor: as contas ficam
 * salvas localmente no banco Room do aparelho.
 *
 * @property id identificador gerado automaticamente pelo banco.
 * @property nome nome de exibição do tutor.
 * @property email e-mail usado no login (único; salvo sempre em minúsculas).
 * @property senhaHash hash SHA-256 da senha. Nunca guardamos a senha em texto puro.
 */
@Entity(
    tableName = "usuarios",
    // Índice único: o banco recusa dois usuários com o mesmo e-mail.
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val email: String,
    val senhaHash: String
)
