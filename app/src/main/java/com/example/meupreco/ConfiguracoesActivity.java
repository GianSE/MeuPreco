package com.example.meupreco;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Meu Preço - Entrega 5.
 *
 * Tela de Configurações. As escolhas do usuário (idioma, ordenação da lista e
 * sugestão de preenchimento) são gravadas no SharedPreferences (Preferencias).
 * A troca de idioma é aplicada na hora com AppCompatDelegate (per-app language).
 */
public class ConfiguracoesActivity extends AppCompatActivity {

    private Preferencias prefs;

    private Toolbar toolbar;
    private View conteudo;
    private RadioGroup grupoIdioma;
    private RadioGroup grupoOrdenacao;
    private SwitchMaterial switchSugerir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        prefs = new Preferencias(this);

        toolbar = findViewById(R.id.toolbar);
        conteudo = findViewById(R.id.conteudo);
        grupoIdioma = findViewById(R.id.grupoIdioma);
        grupoOrdenacao = findViewById(R.id.grupoOrdenacao);
        switchSugerir = findViewById(R.id.switchSugerir);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_configuracoes);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        carregarEstadoAtual();
        configurarListeners();
        tratarBarrasDoSistema();
    }

    /**
     * Marca os controles de acordo com as preferências já salvas.
     * (Feito ANTES de registrar os listeners para não disparar gravações.)
     */
    private void carregarEstadoAtual() {
        switch (prefs.getIdioma()) {
            case Preferencias.IDIOMA_INGLES:
                grupoIdioma.check(R.id.radioIdiomaIngles);
                break;
            case Preferencias.IDIOMA_PORTUGUES:
                grupoIdioma.check(R.id.radioIdiomaPortugues);
                break;
            default:
                grupoIdioma.check(R.id.radioIdiomaSistema);
                break;
        }

        if (Preferencias.ORDENACAO_PRECO.equals(prefs.getOrdenacao())) {
            grupoOrdenacao.check(R.id.radioOrdenacaoPreco);
        } else {
            grupoOrdenacao.check(R.id.radioOrdenacaoNome);
        }

        switchSugerir.setChecked(prefs.isSugerirMercado());
    }

    private void configurarListeners() {
        grupoIdioma.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String idioma;
                if (checkedId == R.id.radioIdiomaIngles) {
                    idioma = Preferencias.IDIOMA_INGLES;
                } else if (checkedId == R.id.radioIdiomaPortugues) {
                    idioma = Preferencias.IDIOMA_PORTUGUES;
                } else {
                    idioma = Preferencias.IDIOMA_SISTEMA;
                }
                prefs.setIdioma(idioma);
                // Aplica na hora; o sistema recria as Activities no novo idioma.
                MeuPrecoApp.aplicarIdioma(idioma);
            }
        });

        grupoOrdenacao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                prefs.setOrdenacao(checkedId == R.id.radioOrdenacaoPreco
                        ? Preferencias.ORDENACAO_PRECO
                        : Preferencias.ORDENACAO_NOME);
            }
        });

        switchSugerir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setSugerirMercado(isChecked);
            }
        });

        findViewById(R.id.botaoRestaurar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurarPadroes();
            }
        });
    }

    /**
     * Restaura as configurações padrão, atualiza os controles e volta o idioma
     * para o padrão do sistema.
     */
    private void restaurarPadroes() {
        prefs.restaurarPadroes();
        carregarEstadoAtual();
        MeuPrecoApp.aplicarIdioma(Preferencias.IDIOMA_SISTEMA);
        Toast.makeText(this, R.string.msg_padroes_restaurados, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

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
}
