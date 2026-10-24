package com.example.meupreco;

import android.content.Context;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Meu Preço - Entrega Final.
 *
 * Conversões de valores para texto, sempre no idioma configurado no aparelho.
 * Nada aqui fixa um idioma: o Locale vem do sistema (Locale.getDefault()) e os
 * símbolos/textos vêm dos recursos de cada idioma (values / values-pt-rBR).
 */
public final class Formatacao {

    private Formatacao() {
    }

    /**
     * Converte LocalDate para String no formato do idioma do aparelho, por
     * exemplo "Oct 24, 2026" (inglês) ou "24 de out. de 2026" (português).
     * O ofLocalizedDate escolhe a ordem de dia/mês/ano e o nome do mês de
     * acordo com o Locale, em vez de usar um padrão fixo como "dd/MM/yyyy".
     */
    public static String data(LocalDate data) {
        DateTimeFormatter formato = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault());
        return data.format(formato);
    }

    /**
     * Preço com o símbolo da moeda vindo de R.string.formato_preco, ex.:
     * "R$ 27,90" (português) ou "$ 27.90" (inglês).
     */
    public static String preco(Context contexto, double preco) {
        String valor = String.format(Locale.getDefault(), "%.2f", preco);
        return contexto.getString(R.string.formato_preco, valor);
    }

    /**
     * Número sem casas decimais desnecessárias, ex.: "2" ou "1,5". Sem
     * separador de milhar, para o texto poder ser lido de volta no formulário.
     */
    public static String numero(double valor) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.getDefault());
        formato.setMaximumFractionDigits(3);
        formato.setGroupingUsed(false);
        return formato.format(valor);
    }

    /**
     * Converte o texto digitado em número, aceitando vírgula ou ponto como
     * separador decimal. Retorna -1 quando o texto não é um número válido.
     */
    public static double lerNumero(String texto) {
        try {
            return Double.parseDouble(texto.replace(",", "."));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Quantidade de preços com plural traduzido: "1 preço", "3 preços" (ou
     * "1 price", "3 prices"). O zero tem texto próprio ("Nenhum preço"),
     * porque nas regras de plural do português o 0 usaria a forma singular.
     */
    public static String totalPrecos(Context contexto, int total) {
        if (total == 0) {
            return contexto.getString(R.string.nenhum_preco);
        }
        return contexto.getResources().getQuantityString(R.plurals.total_precos, total, total);
    }

    /** Item de um array de recursos, com proteção contra posição inválida. */
    public static String itemDoArray(String[] array, int posicao) {
        return (posicao >= 0 && posicao < array.length) ? array[posicao] : "";
    }
}
