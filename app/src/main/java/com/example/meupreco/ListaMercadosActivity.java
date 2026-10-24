package com.example.meupreco;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
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

import com.example.meupreco.modelo.Mercado;
import com.example.meupreco.modelo.MercadoComTotal;
import com.example.meupreco.persistencia.MeuPrecoDatabase;

import java.util.ArrayList;

/**
 * Meu Preço - Entrega Final.
 *
 * Lista os mercados cadastrados no banco (segunda entidade), com quantos
 * preços cada um possui.
 *
 * - Menu de opções: Adicionar (ícone na barra).
 * - Menu de Ação Contextual (toque longo): Editar e Excluir.
 * - Excluir pede confirmação em um AlertDialog. Se o mercado tiver preços
 *   registrados, a chave estrangeira não permite a exclusão, e um AlertDialog
 *   de aviso explica o motivo.
 * - Botão Up volta para a lista de preços (Activity pai).
 */
public class ListaMercadosActivity extends AppCompatActivity {

    private static final int REQUISICAO_ADICIONAR = 1;
    private static final int REQUISICAO_EDITAR = 2;

    private final ArrayList<MercadoComTotal> mercados = new ArrayList<>();
    private MercadoAdapter adapter;
    private MeuPrecoDatabase banco;

    private Toolbar toolbar;
    private ListView listaMercados;

    private int posicaoSelecionada = -1;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mercados);

        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        banco = MeuPrecoDatabase.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        listaMercados = findViewById(R.id.listaMercados);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_mercados);
            getSupportActionBar().setSubtitle(R.string.subtitulo_mercados);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new MercadoAdapter(this, mercados);
        listaMercados.setAdapter(adapter);
        listaMercados.setEmptyView(findViewById(R.id.textVazio));
        listaMercados.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Toque simples: Toast com o nome e o total de preços do mercado.
        listaMercados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null) {
                    selecionarItem(position);
                    return;
                }
                listaMercados.setItemChecked(position, false);

                MercadoComTotal item = mercados.get(position);
                String mensagem = getString(R.string.item_mercado_clicado,
                        item.mercado.getNome(),
                        Formatacao.totalPrecos(ListaMercadosActivity.this, item.totalProdutos));
                Toast.makeText(ListaMercadosActivity.this, mensagem, Toast.LENGTH_SHORT).show();
            }
        });

        // Toque longo: abre o CAB com o item destacado.
        listaMercados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

    @Override
    protected void onResume() {
        super.onResume();
        carregarMercados();
    }

    /** Lê os mercados (com o total de preços) do banco e atualiza a lista. */
    private void carregarMercados() {
        mercados.clear();
        mercados.addAll(banco.mercadoDao().listarComTotal());
        adapter.notifyDataSetChanged();
    }

    /** Destaca a linha e mostra o nome do mercado no título do CAB. */
    private void selecionarItem(int position) {
        posicaoSelecionada = position;
        listaMercados.setItemChecked(position, true);
        if (actionMode != null) {
            actionMode.setTitle(mercados.get(position).mercado.getNome());
        }
    }

    // ------------------------------------------------------------------
    // Menu de opções e botão Up
    // ------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_mercados, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.acao_adicionar) {
            startActivityForResult(new Intent(this, CadastroMercadoActivity.class),
                    REQUISICAO_ADICIONAR);
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
                editarMercado(posicaoSelecionada);
                mode.finish();
                return true;
            } else if (id == R.id.acao_excluir) {
                confirmarExclusao(posicaoSelecionada);
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (posicaoSelecionada >= 0) {
                listaMercados.setItemChecked(posicaoSelecionada, false);
            }
            posicaoSelecionada = -1;
            actionMode = null;
        }
    };

    private void editarMercado(int posicao) {
        Intent intent = new Intent(this, CadastroMercadoActivity.class);
        intent.putExtra(CadastroMercadoActivity.EXTRA_ID, mercados.get(posicao).mercado.getId());
        startActivityForResult(intent, REQUISICAO_EDITAR);
    }

    /**
     * Antes de excluir, verifica se o mercado é usado por algum preço. Se for,
     * mostra um AlertDialog de aviso (a chave estrangeira impede a exclusão);
     * senão, um AlertDialog pede a confirmação do usuário.
     */
    private void confirmarExclusao(int posicao) {
        MercadoComTotal item = mercados.get(posicao);
        final Mercado mercado = item.mercado;

        if (item.totalProdutos > 0) {
            avisarMercadoEmUso(mercado, item.totalProdutos);
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(getString(R.string.dialogo_excluir_mercado, mercado.getNome()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.bt_excluir, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        excluirMercado(mercado);
                    }
                })
                .setNegativeButton(R.string.bt_cancelar, null)
                .show();
    }

    private void excluirMercado(Mercado mercado) {
        try {
            banco.mercadoDao().excluir(mercado);
        } catch (SQLiteConstraintException e) {
            // Segurança extra: se algum preço apontar para o mercado, o banco
            // recusa a exclusão (ForeignKey.RESTRICT).
            avisarMercadoEmUso(mercado, banco.produtoDao().contarPorMercado(mercado.getId()));
            return;
        }
        if (actionMode != null) {
            actionMode.finish();
        }
        carregarMercados();
        Toast.makeText(this, getString(R.string.mercado_excluido, mercado.getNome()),
                Toast.LENGTH_SHORT).show();
    }

    /** AlertDialog de aviso: o mercado não pode ser excluído. */
    private void avisarMercadoEmUso(Mercado mercado, int total) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialogo_mercado_em_uso_titulo)
                .setMessage(getString(R.string.dialogo_mercado_em_uso, mercado.getNome(),
                        Formatacao.totalPrecos(this, total)))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.bt_ok, null)
                .show();
    }

    // ------------------------------------------------------------------
    // Retorno do cadastro de mercado
    // ------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        String nome = data.getStringExtra(CadastroMercadoActivity.EXTRA_NOME);
        int mensagem = (requestCode == REQUISICAO_EDITAR)
                ? R.string.mercado_editado : R.string.mercado_adicionado;
        Toast.makeText(this, getString(mensagem, nome), Toast.LENGTH_SHORT).show();
        // A lista é recarregada no onResume.
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
                        listaMercados.setPadding(barras.left, listaMercados.getPaddingTop(),
                                barras.right, barras.bottom);
                        return insets;
                    }
                });
    }
}
