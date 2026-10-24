package com.example.meupreco;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Meu Preço - Entregas 5 e Final.
 *
 * Encapsula o acesso ao SharedPreferences do aplicativo, guardando as
 * configurações escolhidas pelo usuário (ordenação da lista de preços e
 * sugestão do último mercado usado no cadastro).
 */
public class Preferencias {

    private static final String ARQUIVO = "meupreco_prefs";

    public static final String KEY_ORDENACAO = "ordenacao";            // "nome", "preco" ou "data"
    public static final String KEY_SUGERIR = "sugerir_mercado";        // boolean
    public static final String KEY_ULTIMO_MERCADO = "ultimo_mercado_id"; // long

    public static final String ORDENACAO_NOME = "nome";
    public static final String ORDENACAO_PRECO = "preco";
    public static final String ORDENACAO_DATA = "data";

    /** Valor usado quando ainda não há último mercado salvo. */
    public static final long SEM_MERCADO = -1;

    private final SharedPreferences sp;

    public Preferencias(Context contexto) {
        sp = contexto.getApplicationContext()
                .getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
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

    // --- Último mercado escolhido no cadastro (usado pela sugestão) ---
    public long getUltimoMercadoId() {
        return sp.getLong(KEY_ULTIMO_MERCADO, SEM_MERCADO);
    }

    public void setUltimoMercadoId(long mercadoId) {
        sp.edit().putLong(KEY_ULTIMO_MERCADO, mercadoId).apply();
    }

    /**
     * Restaura todas as configurações para os valores padrão.
     */
    public void restaurarPadroes() {
        sp.edit().clear().apply();
    }
}
