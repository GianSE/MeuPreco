package com.example.meupreco;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Meu Preço - Entrega 5.
 *
 * Encapsula o acesso ao SharedPreferences do aplicativo, guardando as
 * configurações escolhidas pelo usuário (idioma, ordenação da lista, sugestão
 * de preenchimento e o último mercado usado).
 */
public class Preferencias {

    private static final String ARQUIVO = "meupreco_prefs";

    public static final String KEY_IDIOMA = "idioma";              // "", "en" ou "pt-BR"
    public static final String KEY_ORDENACAO = "ordenacao";        // "nome" ou "preco"
    public static final String KEY_SUGERIR = "sugerir_mercado";    // boolean
    public static final String KEY_ULTIMO_MERCADO = "ultimo_mercado";

    public static final String IDIOMA_SISTEMA = "";
    public static final String IDIOMA_INGLES = "en";
    public static final String IDIOMA_PORTUGUES = "pt-BR";

    public static final String ORDENACAO_NOME = "nome";
    public static final String ORDENACAO_PRECO = "preco";

    private final SharedPreferences sp;

    public Preferencias(Context contexto) {
        sp = contexto.getApplicationContext()
                .getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    // --- Idioma ---
    public String getIdioma() {
        return sp.getString(KEY_IDIOMA, IDIOMA_SISTEMA);
    }

    public void setIdioma(String idioma) {
        sp.edit().putString(KEY_IDIOMA, idioma).apply();
    }

    // --- Ordenação da lista ---
    public String getOrdenacao() {
        return sp.getString(KEY_ORDENACAO, ORDENACAO_NOME);
    }

    public void setOrdenacao(String ordenacao) {
        sp.edit().putString(KEY_ORDENACAO, ordenacao).apply();
    }

    // --- Sugerir último mercado ---
    public boolean isSugerirMercado() {
        return sp.getBoolean(KEY_SUGERIR, false);
    }

    public void setSugerirMercado(boolean sugerir) {
        sp.edit().putBoolean(KEY_SUGERIR, sugerir).apply();
    }

    // --- Último mercado digitado (usado pela sugestão) ---
    public String getUltimoMercado() {
        return sp.getString(KEY_ULTIMO_MERCADO, "");
    }

    public void setUltimoMercado(String mercado) {
        sp.edit().putString(KEY_ULTIMO_MERCADO, mercado).apply();
    }

    /**
     * Restaura todas as configurações para os valores padrão.
     */
    public void restaurarPadroes() {
        sp.edit().clear().apply();
    }
}
