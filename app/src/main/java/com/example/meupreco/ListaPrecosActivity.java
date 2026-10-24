package com.example.meupreco;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meupreco.modelo.Produto;
import com.example.meupreco.modelo.ProdutoComMercado;
import com.example.meupreco.persistencia.MeuPrecoDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Meu Preço - Entrega Final.
 *
 * Activity principal (Launcher). Exibe em uma ListView os preços gravados no
 * banco de dados (Room), junto com o nome do mercado (chave estrangeira).
 *
 * - Menu de opções (ícones na barra): Adicionar, Mercados, Configurações e Sobre.
 * - Menu de Ação Contextual (CAB), aberto ao manter um item pressionado:
 *   Editar (abre o Cadastro em modo edição) e Excluir (pede confirmação em um
 *   AlertDialog antes de apagar do banco).
 */
public class ListaPrecosActivity extends AppCompatActivity {

    // Códigos para diferenciar o retorno das telas abertas com resultado.
    private static final int REQUISICAO_ADICIONAR = 1;
    private static final int REQUISICAO_EDITAR = 2;
    private static final int REQUISICAO_PRIMEIRO_MERCADO = 3;

    private final ArrayList<ProdutoComMercado> produtos = new ArrayList<>();
    private ProdutoAdapter adapter;
    private Preferencias prefs;
    private MeuPrecoDatabase banco;

    private Toolbar toolbar;
    private ListView listaProdutos;

    // Posição do item alvo do CAB.
    private int posicaoSelecionada = -1;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_precos);

        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        prefs = new Preferencias(this);
        banco = MeuPrecoDatabase.getInstance(this);

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
                if (actionMode != null) {
                    // Com o CAB aberto, o toque troca o item selecionado (destacado).
                    selecionarItem(position);
                    return;
                }
                // Fora do CAB, o toque simples não deixa a linha destacada.
                listaProdutos.setItemChecked(position, false);

                Produto produto = produtos.get(position).produto;
                String mensagem = getString(
                        R.string.item_clicado,
                        produto.getNome(),
                        produto.getMarca(),
                        Formatacao.preco(ListaPrecosActivity.this, produto.getPreco()));
                Toast.makeText(ListaPrecosActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });

        // Toque longo: abre o Menu de Ação Contextual (CAB) para o item. O
        // setItemChecked ativa a linha, que fica destacada pelo fundo
        // drawable/fundo_item_lista.xml (state_activated).
        listaProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null) {
                    return false;
                }
                actionMode = startSupportActionMode(callbackContextual);
                selecionarItem(position);
                return true;
            }
        });

        tratarBarrasDoSistema();
    }

    /**
     * Toda vez que a lista volta à tela (depois de cadastrar, editar, mudar as
     * configurações...), os dados são lidos de novo do banco.
     */
    @Override
    protected void onResume() {
        super.onResume();
        carregarProdutos();
    }

    /**
     * Consulta o banco com a ordenação escolhida nas Configurações
     * (SharedPreferences) e atualiza a ListView.
     */
    private void carregarProdutos() {
        List<ProdutoComMercado> resultado;
        String ordenacao = prefs.getOrdenacao();
        if (Preferencias.ORDENACAO_PRECO.equals(ordenacao)) {
            resultado = banco.produtoDao().listarPorPreco();
        } else if (Preferencias.ORDENACAO_DATA.equals(ordenacao)) {
            resultado = banco.produtoDao().listarPorData();
        } else {
            resultado = banco.produtoDao().listarPorNome();
        }

        produtos.clear();
        produtos.addAll(resultado);
        adapter.notifyDataSetChanged();
    }

    /**
     * Marca o item como selecionado no CAB: a linha fica destacada
     * (setItemChecked + state_activated) e o nome do produto aparece como
     * título da barra contextual.
     */
    private void selecionarItem(int position) {
        posicaoSelecionada = position;
        listaProdutos.setItemChecked(position, true);
        if (actionMode != null) {
            actionMode.setTitle(produtos.get(position).produto.getNome());
        }
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
            adicionarProduto();
            return true;
        } else if (id == R.id.acao_mercados) {
            startActivity(new Intent(this, ListaMercadosActivity.class));
            return true;
        } else if (id == R.id.acao_configuracoes) {
            startActivity(new Intent(this, ConfiguracoesActivity.class));
            return true;
        } else if (id == R.id.acao_sobre) {
            startActivity(new Intent(this, AutoriaActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Abre o Cadastro de preço. Como todo preço precisa de um mercado (chave
     * estrangeira), se ainda não houver mercados um AlertDialog avisa o usuário
     * e oferece abrir o cadastro de mercado.
     */
    private void adicionarProduto() {
        if (banco.mercadoDao().contar() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialogo_sem_mercado_titulo)
                    .setMessage(R.string.dialogo_sem_mercado)
                    .setPositiveButton(R.string.bt_cadastrar_mercado, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ListaPrecosActivity.this,
                                    CadastroMercadoActivity.class);
                            startActivityForResult(intent, REQUISICAO_PRIMEIRO_MERCADO);
                        }
                    })
                    .setNegativeButton(R.string.bt_cancelar, null)
                    .show();
            return;
        }
        startActivityForResult(new Intent(this, CadastroPrecoActivity.class), REQUISICAO_ADICIONAR);
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
                // O CAB continua aberto (item destacado) enquanto o usuário
                // decide no AlertDialog.
                confirmarExclusao(posicaoSelecionada);
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
     * Abre o Cadastro em modo edição. Basta enviar o id: o Cadastro busca o
     * registro completo no banco.
     */
    private void editarProduto(int posicao) {
        Produto produto = produtos.get(posicao).produto;
        Intent intent = new Intent(this, CadastroPrecoActivity.class);
        intent.putExtra(CadastroPrecoActivity.EXTRA_ID, produto.getId());
        startActivityForResult(intent, REQUISICAO_EDITAR);
    }

    /**
     * AlertDialog de confirmação: só exclui do banco se o usuário tocar em
     * "Excluir". Em "Cancelar" nada acontece e o item continua selecionado.
     */
    private void confirmarExclusao(int posicao) {
        final Produto produto = produtos.get(posicao).produto;

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(getString(R.string.dialogo_excluir_produto, produto.getNome()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.bt_excluir, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        banco.produtoDao().excluir(produto);
                        if (actionMode != null) {
                            actionMode.finish();
                        }
                        carregarProdutos();
                        Toast.makeText(ListaPrecosActivity.this,
                                getString(R.string.produto_excluido, produto.getNome()),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.bt_cancelar, null)
                .show();
    }

    // ------------------------------------------------------------------
    // Retorno das telas abertas com startActivityForResult
    // ------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        String nome = data.getStringExtra(CadastroPrecoActivity.EXTRA_NOME);

        if (requestCode == REQUISICAO_ADICIONAR) {
            Toast.makeText(this, getString(R.string.produto_adicionado, nome),
                    Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUISICAO_EDITAR) {
            Toast.makeText(this, getString(R.string.produto_editado, nome),
                    Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUISICAO_PRIMEIRO_MERCADO) {
            // O primeiro mercado acabou de ser criado: segue para o cadastro do preço.
            Toast.makeText(this, getString(R.string.mercado_adicionado, nome),
                    Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(this, CadastroPrecoActivity.class),
                    REQUISICAO_ADICIONAR);
        }
        // A lista é recarregada do banco no onResume, que vem logo em seguida.
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
