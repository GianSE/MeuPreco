package com.example.meupreco;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

/**
 * Meu Preço - Entrega 5.
 *
 * Classe Application que, ao iniciar o app, aplica o idioma que o usuário
 * escolheu e que está persistido no SharedPreferences. Assim a preferência de
 * idioma sobrevive ao fechamento do aplicativo.
 */
public class MeuPrecoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        aplicarIdioma(new Preferencias(this).getIdioma());
    }

    /**
     * Aplica o idioma por app (per-app language). Código vazio = segue o idioma
     * do sistema; caso contrário usa a tag informada (ex.: "en" ou "pt-BR").
     */
    public static void aplicarIdioma(String codigo) {
        LocaleListCompat locales = (codigo == null || codigo.isEmpty())
                ? LocaleListCompat.getEmptyLocaleList()
                : LocaleListCompat.forLanguageTags(codigo);
        AppCompatDelegate.setApplicationLocales(locales);
    }
}
