package com.example.meupreco;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Meu Preço - Entrega 3.
 *
 * Activity que exibe os dados de autoria do aplicativo: nome do aluno, curso,
 * e-mail, uma breve descrição do que o app faz, além do logo e do nome da UTFPR.
 *
 * É aberta pela tela de listagem através do botão "Sobre" (startActivity).
 */
public class AutoriaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private View conteudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoria);

        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        toolbar = findViewById(R.id.toolbar);
        conteudo = findViewById(R.id.conteudoAutoria);

        // Exibe a Barra do Aplicativo (Toolbar como ActionBar) com botão de voltar.
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_autoria);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tratarBarrasDoSistema();
    }

    /**
     * Posiciona corretamente a Barra do Aplicativo a partir do API 35 (edge-to-edge):
     * o recuo do topo vai para a Toolbar e os demais recuos para o conteúdo.
     */
    private void tratarBarrasDoSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.raiz),
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                        Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.displayCutout());
                        toolbar.setPadding(barras.left, barras.top, barras.right, 0);
                        conteudo.setPadding(barras.left, conteudo.getPaddingTop(),
                                barras.right, barras.bottom);
                        return insets;
                    }
                });
    }

    /**
     * Trata o clique na seta "voltar" da Barra do Aplicativo.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
