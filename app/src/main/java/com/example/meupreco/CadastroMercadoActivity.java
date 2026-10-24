package com.example.meupreco;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meupreco.modelo.Mercado;
import com.example.meupreco.persistencia.MeuPrecoDatabase;

/**
 * Meu Preço - Entrega Final.
 *
 * Activity de manipulação da entidade Mercado (inserção e alteração). Se
 * receber EXTRA_ID, abre em modo edição com os dados lidos do banco.
 */
public class CadastroMercadoActivity extends AppCompatActivity {

    /** Id do mercado a editar (ausente = novo cadastro). */
    public static final String EXTRA_ID = "extra_id";
    /** Nome do mercado salvo, devolvido para a tela anterior mostrar o Toast. */
    public static final String EXTRA_NOME = "extra_nome";

    private Toolbar toolbar;
    private ScrollView scrollFormulario;
    private EditText editNome;
    private EditText editCidade;

    private MeuPrecoDatabase banco;

    /** Mercado em edição (null quando é um novo cadastro). */
    private Mercado mercadoEmEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_mercado);

        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        banco = MeuPrecoDatabase.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        scrollFormulario = findViewById(R.id.scrollFormulario);
        editNome = findViewById(R.id.editNomeMercado);
        editCidade = findViewById(R.id.editCidade);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id != -1) {
            mercadoEmEdicao = banco.mercadoDao().buscarPorId(id);
        }
        if (mercadoEmEdicao != null) {
            editNome.setText(mercadoEmEdicao.getNome());
            editCidade.setText(mercadoEmEdicao.getCidade());
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mercadoEmEdicao != null
                    ? R.string.titulo_editar_mercado : R.string.titulo_cadastro_mercado);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tratarBarrasDoSistema();
    }

    // ------------------------------------------------------------------
    // Menu de opções (Salvar / Limpar) e botão Up
    // ------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acao_salvar) {
            salvar();
            return true;
        } else if (id == R.id.acao_limpar) {
            limpar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ------------------------------------------------------------------
    // Salvar e limpar
    // ------------------------------------------------------------------

    /**
     * Valida o nome (obrigatório e sem repetir outro mercado) e grava no banco:
     * alteração quando está editando, inserção quando é novo.
     */
    private void salvar() {
        String nome = editNome.getText().toString().trim();
        String cidade = editCidade.getText().toString().trim();

        if (nome.isEmpty()) {
            avisarErro(getString(R.string.erro_nome_mercado));
            return;
        }

        Mercado existente = banco.mercadoDao().buscarPorNome(nome);
        boolean outroComMesmoNome = existente != null
                && (mercadoEmEdicao == null || existente.getId() != mercadoEmEdicao.getId());
        if (outroComMesmoNome) {
            avisarErro(getString(R.string.erro_mercado_duplicado, nome));
            return;
        }

        if (mercadoEmEdicao != null) {
            mercadoEmEdicao.setNome(nome);
            mercadoEmEdicao.setCidade(cidade);
            banco.mercadoDao().alterar(mercadoEmEdicao);
        } else {
            banco.mercadoDao().inserir(new Mercado(nome, cidade));
        }

        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_NOME, nome);
        setResult(RESULT_OK, resultado);
        finish();
    }

    private void avisarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
        editNome.setError(mensagem);
        editNome.requestFocus();
    }

    private void limpar() {
        editNome.setText("");
        editCidade.setText("");
        editNome.setError(null);
        editNome.requestFocus();
        Toast.makeText(this, R.string.msg_limpo, Toast.LENGTH_SHORT).show();
    }

    // ------------------------------------------------------------------
    // Barra do sistema (edge-to-edge)
    // ------------------------------------------------------------------

    private void tratarBarrasDoSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.raiz),
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                        Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.displayCutout());
                        Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
                        toolbar.setPadding(barras.left, barras.top, barras.right, 0);
                        scrollFormulario.setPadding(barras.left, 0, barras.right,
                                Math.max(barras.bottom, teclado.bottom));
                        return insets;
                    }
                });
    }
}
