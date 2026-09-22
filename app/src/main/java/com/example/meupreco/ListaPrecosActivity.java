package com.example.meupreco;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Meu Preço - Entrega 4.
 *
 * Activity principal (Launcher). Exibe em uma ListView os produtos cadastrados
 * pelo usuário. As ações ficam em menus:
 *
 * - Menu de opções (barra do app): "Adicionar" (abre o Cadastro esperando
 *   resultado) e "Sobre" (abre a Autoria).
 * - Menu de Ação Contextual (CAB), aberto ao manter um item pressionado:
 *   "Editar" (abre o Cadastro em modo edição com os dados do item) e "Excluir"
 *   (remove o item do ArrayList).
 */
public class ListaPrecosActivity extends AppCompatActivity {

    // Códigos para diferenciar o retorno do Cadastro (novo x edição).
    private static final int REQUISICAO_ADICIONAR = 1;
    private static final int REQUISICAO_EDITAR = 2;

    private final ArrayList<Produto> produtos = new ArrayList<>();
    private ProdutoAdapter adapter;

    private Toolbar toolbar;
    private ListView listaProdutos;

    // Posição do item alvo do CAB e do item em edição.
    private int posicaoSelecionada = -1;
    private int posicaoEditando = -1;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_precos);

        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        toolbar = findViewById(R.id.toolbar);
        listaProdutos = findViewById(R.id.listaProdutos);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_lista);
            getSupportActionBar().setSubtitle(R.string.subtitulo_lista);
        }

        adapter = new ProdutoAdapter(this, produtos);
        listaProdutos.setAdapter(adapter);
        listaProdutos.setEmptyView(findViewById(R.id.textVazio));
        listaProdutos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Toque simples: mostra um Toast identificando o item.
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

        // Toque longo: abre o Menu de Ação Contextual (CAB) para o item.
        listaProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null) {
                    return false;
                }
                posicaoSelecionada = position;
                listaProdutos.setItemChecked(position, true);
                actionMode = startSupportActionMode(callbackContextual);
                return true;
            }
        });

        tratarBarrasDoSistema();
    }

    // ------------------------------------------------------------------
    // Menu de opções (barra do app)
    // ------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acao_adicionar) {
            Intent intent = new Intent(this, CadastroPrecoActivity.class);
            startActivityForResult(intent, REQUISICAO_ADICIONAR);
            return true;
        } else if (id == R.id.acao_sobre) {
            startActivity(new Intent(this, AutoriaActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------------------------------------
    // Menu de Ação Contextual (CAB)
    // ------------------------------------------------------------------

    private final ActionMode.Callback callbackContextual = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_contextual, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.acao_editar) {
                editarProduto(posicaoSelecionada);
                mode.finish();
                return true;
            } else if (id == R.id.acao_excluir) {
                excluirProduto(posicaoSelecionada);
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (posicaoSelecionada >= 0) {
                listaProdutos.setItemChecked(posicaoSelecionada, false);
            }
            posicaoSelecionada = -1;
            actionMode = null;
        }
    };

    /**
     * Abre o Cadastro em modo edição, enviando os dados do produto selecionado
     * e esperando o resultado (com os valores alterados) de volta.
     */
    private void editarProduto(int posicao) {
        Produto p = produtos.get(posicao);
        posicaoEditando = posicao;

        Intent intent = new Intent(this, CadastroPrecoActivity.class);
        intent.putExtra(CadastroPrecoActivity.EXTRA_NOME, p.getNome());
        intent.putExtra(CadastroPrecoActivity.EXTRA_MARCA, p.getMarca());
        intent.putExtra(CadastroPrecoActivity.EXTRA_MERCADO, p.getMercado());
        intent.putExtra(CadastroPrecoActivity.EXTRA_CATEGORIA, p.getCategoria());
        intent.putExtra(CadastroPrecoActivity.EXTRA_UNIDADE, p.getUnidade());
        intent.putExtra(CadastroPrecoActivity.EXTRA_PRECO, p.getPreco());
        startActivityForResult(intent, REQUISICAO_EDITAR);
    }

    /**
     * Remove o produto do ArrayList e redesenha a lista.
     */
    private void excluirProduto(int posicao) {
        Produto removido = produtos.remove(posicao);
        adapter.notifyDataSetChanged();
        Toast.makeText(this,
                getString(R.string.produto_excluido, removido.getNome()),
                Toast.LENGTH_SHORT).show();
    }

    // ------------------------------------------------------------------
    // Retorno do Cadastro (novo item ou edição)
    // ------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            posicaoEditando = -1;
            return;
        }

        Produto produto = new Produto(
                data.getStringExtra(CadastroPrecoActivity.EXTRA_NOME),
                data.getStringExtra(CadastroPrecoActivity.EXTRA_MARCA),
                data.getStringExtra(CadastroPrecoActivity.EXTRA_MERCADO),
                data.getStringExtra(CadastroPrecoActivity.EXTRA_CATEGORIA),
                data.getStringExtra(CadastroPrecoActivity.EXTRA_UNIDADE),
                data.getDoubleExtra(CadastroPrecoActivity.EXTRA_PRECO, 0));

        if (requestCode == REQUISICAO_ADICIONAR) {
            produtos.add(produto);
            adapter.notifyDataSetChanged();
            Toast.makeText(this,
                    getString(R.string.produto_adicionado, produto.getNome()),
                    Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUISICAO_EDITAR && posicaoEditando >= 0) {
            produtos.set(posicaoEditando, produto);
            adapter.notifyDataSetChanged();
            Toast.makeText(this,
                    getString(R.string.produto_editado, produto.getNome()),
                    Toast.LENGTH_SHORT).show();
            posicaoEditando = -1;
        }
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
                        toolbar.setPadding(barras.left, barras.top, barras.right, 0);
                        listaProdutos.setPadding(barras.left, listaProdutos.getPaddingTop(),
                                barras.right, barras.bottom);
                        return insets;
                    }
                });
    }
}
