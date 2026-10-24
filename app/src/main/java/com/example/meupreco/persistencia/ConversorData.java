package com.example.meupreco.persistencia;

import androidx.room.TypeConverter;

import java.time.LocalDate;

/**
 * Meu Preço - Entrega Final.
 *
 * O SQLite não tem um tipo "data". Este TypeConverter ensina o Room a gravar o
 * LocalDate como um número (quantidade de dias desde 01/01/1970, o "epoch day")
 * e a transformá-lo de volta em LocalDate ao ler. Guardar como número também
 * permite ordenar por data direto na consulta (ORDER BY dataCompra).
 */
public class ConversorData {

    @TypeConverter
    public static Long deLocalDate(LocalDate data) {
        return data == null ? null : data.toEpochDay();
    }

    @TypeConverter
    public static LocalDate paraLocalDate(Long dias) {
        return dias == null ? null : LocalDate.ofEpochDay(dias);
    }
}
