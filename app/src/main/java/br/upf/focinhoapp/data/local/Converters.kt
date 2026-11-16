package br.upf.focinhoapp.data.local

import androidx.room.TypeConverter
import br.upf.focinhoapp.data.model.Especie
import br.upf.focinhoapp.data.model.Sexo
import br.upf.focinhoapp.data.model.TipoCuidado

/**
 * O SQLite só entende tipos simples (texto, número...). Os conversores ensinam
 * o Room a transformar nossos enums em texto ao salvar, e de volta ao ler.
 *
 * Salvamos o `name` do enum (ex.: "CAO") em vez da posição (ordinal) porque,
 * se alguém reordenar o enum no futuro, os dados antigos continuam corretos.
 */
class Converters {

    @TypeConverter
    fun especieParaTexto(especie: Especie): String = especie.name

    @TypeConverter
    fun textoParaEspecie(texto: String): Especie = Especie.valueOf(texto)

    @TypeConverter
    fun sexoParaTexto(sexo: Sexo): String = sexo.name

    @TypeConverter
    fun textoParaSexo(texto: String): Sexo = Sexo.valueOf(texto)

    @TypeConverter
    fun tipoParaTexto(tipo: TipoCuidado): String = tipo.name

    @TypeConverter
    fun textoParaTipo(texto: String): TipoCuidado = TipoCuidado.valueOf(texto)
}
