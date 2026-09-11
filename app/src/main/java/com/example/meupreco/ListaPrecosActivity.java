package com.example.meupreco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Meu Preço - Entrega 3.
 *
 * Activity principal (Launcher). Exibe, em uma ListView, os produtos que o
 * usuário cadastra na tela de Cadastro. A lista começa vazia e vai sendo
 * preenchida a cada cadastro devolvido com RESULT_OK.
 *
 * - Botão "Adicionar": abre a tela de Cadastro esperando um resultado
 *   (startActivityForResult).
 * - Botão "Sobre": abre a tela de Autoria do app (startActivity).
 */
public class ListaPrecosActivity extends AppCompatActivity {

    // Código usado para identificar o retorno da tela de Cadastro.
    private static final int REQUISICAO_CADASTRO = 1;

    private final ArrayList<Produto> produtos = new ArrayList<>();
    private ProdutoAdapter adapter;

    private Toolbar toolbar;
    private ListView listaProdutos;
    private Button botaoAdicionar;
    private Button botaoSobre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_precos);

        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        toolbar = findViewById(R.id.toolbar);
        listaProdutos = findViewById(R.id.listaProdutos);
        botaoAdicionar = findViewById(R.id.botaoAdicionar);
        botaoSobre = findViewById(R.id.botaoSobre);

        // Exibe a Barra do Aplicativo com título e subtítulo.
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_lista);
            getSupportActionBar().setSubtitle(R.string.subtitulo_lista);
        }

        // A ListView usa o Adapter customizado ligado ao ArrayList.
        adapter = new ProdutoAdapter(this, produtos);
        listaProdutos.setAdapter(adapter);
        listaProdutos.setEmptyView(findViewById(R.id.textVazio));

        listaProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produto produto = produtos.get(position);
                String mensagem = getString(
                        R.string.item_clicado,
                        produto.getNome(),
                        produto.getMarca(),
                        String.format(Locale.forLanguageTag("pt-BR"), "%.2f", produto.getPreco()));
                Toast.makeText(ListaPrecosActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });

        // Botão "Adicionar": abre o Cadastro esperando um resultado.
        botaoAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaPrecosActivity.this, CadastroPrecoActivity.class);
                startActivityForResult(intent, REQUISICAO_CADASTRO);
            }
        });

        // Botão "Sobre": abre a tela de Autoria.
        botaoSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListaPrecosActivity.this, AutoriaActivity.class));
            }
        });

        tratarBarrasDoSistema();
    }

    /**
     * Recebe o resultado da tela de Cadastro. Quando volta com RESULT_OK, monta
     * um objeto Produto com os valores retornados, adiciona ao ArrayList e chama
     * notifyDataSetChanged() para o Adapter redesenhar a ListView.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUISICAO_CADASTRO && resultCode == RESULT_OK && data != null) {
            Produto produto = new Produto(
                    data.getStringExtra(CadastroPrecoActivity.EXTRA_NOME),
                    data.getStringExtra(CadastroPrecoActivity.EXTRA_MARCA),
                    data.getStringExtra(CadastroPrecoActivity.EXTRA_MERCADO),
                    data.getStringExtra(CadastroPrecoActivity.EXTRA_CATEGORIA),
                    data.getStringExtra(CadastroPrecoActivity.EXTRA_UNIDADE),
                    data.getDoubleExtra(CadastroPrecoActivity.EXTRA_PRECO, 0));

            produtos.add(produto);
            adapter.notifyDataSetChanged();

            Toast.makeText(this,
                    getString(R.string.produto_adicionado, produto.getNome()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Posiciona corretamente a Barra do Aplicativo a partir do API 35
     * (edge-to-edge): o recuo do topo vai para a Toolbar e o recuo de baixo
     * para a ListView, para o último item não ficar atrás da barra de navegação.
     */
    private void tratarBarrasDoSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.raiz),
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                        Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.displayCutout());
                        toolbar.setPadding(barras.left, barras.top, barras.right, 0);
                        listaProdutos.setPadding(barras.left, listaProdutos.getPaddingTop(),
                                barras.right, barras.bottom);
                        return insets;
                    }
                });
    }
}
