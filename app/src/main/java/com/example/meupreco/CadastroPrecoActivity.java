package com.example.meupreco;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meupreco.modelo.Mercado;
import com.example.meupreco.modelo.Produto;
import com.example.meupreco.persistencia.MeuPrecoDatabase;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Meu Preço - Entrega Final.
 *
 * Activity de manipulação da entidade Produto (inserção e alteração). O
 * formulário grava direto no banco de dados (Room).
 *
 * - O Spinner de mercados é preenchido com a tabela mercados: o mercado
 *   escolhido vira a chave estrangeira mercadoId do Produto.
 * - A data da compra (LocalDate) é escolhida em um DatePicker.
 * - Se receber EXTRA_ID, abre em modo edição com os dados do banco.
 */
public class CadastroPrecoActivity extends AppCompatActivity {

    // Posição do item "Selecione uma categoria" dentro do Spinner.
    private static final int POSICAO_CATEGORIA_VAZIA = 0;

    /** Id do produto a editar (ausente = novo cadastro). */
    public static final String EXTRA_ID = "extra_id";
    /** Nome do produto salvo, devolvido para a lista mostrar o Toast. */
    public static final String EXTRA_NOME = "extra_nome";

    private Toolbar toolbar;
    private ScrollView scrollFormulario;

    private EditText editProduto;
    private EditText editMarca;
    private EditText editPreco;
    private EditText editQuantidade;
    private EditText editObservacoes;

    private Spinner spinnerMercado;
    private Spinner spinnerCategoria;
    private DatePicker datePickerCompra;

    private RadioGroup grupoUnidade;
    private RadioGroup grupoPagamento;

    private CheckBox checkPromocao;
    private CheckBox checkFavorito;
    private CheckBox checkLembrete;

    private MeuPrecoDatabase banco;
    private Preferencias prefs;

    /** Mercados mostrados no Spinner, na mesma ordem dos itens. */
    private List<Mercado> mercados;

    /** Produto em edição (null quando é um novo cadastro). */
    private Produto produtoEmEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_preco);

        // Ícones da barra de status em branco, para contrastar com a faixa verde.
        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        banco = MeuPrecoDatabase.getInstance(this);
        prefs = new Preferencias(this);

        buscarComponentes();
        configurarSpinnerCategoria();
        configurarDatePicker();
        tratarBarrasDoSistema();

        // Exibe a Barra do Aplicativo com título e botão Up (cancelar).
        setSupportActionBar(toolbar);

        if (!configurarSpinnerMercado()) {
            return;
        }

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id != -1) {
            produtoEmEdicao = banco.produtoDao().buscarPorId(id);
        }

        if (produtoEmEdicao != null) {
            preencherCampos(produtoEmEdicao);
        } else if (prefs.isSugerirMercado()) {
            // Sugestão (SharedPreferences): já seleciona o último mercado usado.
            selecionarMercado(prefs.getUltimoMercadoId());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(produtoEmEdicao != null
                    ? R.string.titulo_cadastro_editar : R.string.titulo_cadastro_bar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        } else if (id == android.R.id.home) {
            // Botão Up: cancela a inclusão/edição e volta para a Listagem.
            finish();
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
    // Preparação dos componentes
    // ------------------------------------------------------------------

    /**
     * Liga os atributos da classe aos componentes declarados no arquivo de layout.
     */
    private void buscarComponentes() {
        toolbar = findViewById(R.id.toolbar);
        scrollFormulario = findViewById(R.id.scrollFormulario);

        editProduto = findViewById(R.id.editProduto);
        editMarca = findViewById(R.id.editMarca);
        editPreco = findViewById(R.id.editPreco);
        editQuantidade = findViewById(R.id.editQuantidade);
        editObservacoes = findViewById(R.id.editObservacoes);

        spinnerMercado = findViewById(R.id.spinnerMercado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        datePickerCompra = findViewById(R.id.datePickerCompra);

        grupoUnidade = findViewById(R.id.grupoUnidade);
        grupoPagamento = findViewById(R.id.grupoPagamento);

        checkPromocao = findViewById(R.id.checkPromocao);
        checkFavorito = findViewById(R.id.checkFavorito);
        checkLembrete = findViewById(R.id.checkLembrete);
    }

    /**
     * Monta o adaptador do Spinner de categorias a partir do arrays.xml.
     */
    private void configurarSpinnerCategoria() {
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(
                this,
                R.array.categorias,
                android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adaptador);
        spinnerCategoria.setSelection(POSICAO_CATEGORIA_VAZIA);
    }

    /**
     * Preenche o Spinner com os mercados do banco (segunda entidade). O
     * ArrayAdapter exibe o toString() de cada Mercado, que é o nome.
     *
     * Se não houver mercados, um AlertDialog avisa o usuário e a tela fecha,
     * pois todo preço precisa de um mercado (chave estrangeira).
     *
     * @return false quando não há mercados cadastrados.
     */
    private boolean configurarSpinnerMercado() {
        mercados = banco.mercadoDao().listarTodos();

        if (mercados.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialogo_sem_mercado_titulo)
                    .setMessage(R.string.dialogo_sem_mercado)
                    .setCancelable(false)
                    .setPositiveButton(R.string.bt_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
            return false;
        }

        ArrayAdapter<Mercado> adaptador = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mercados);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMercado.setAdapter(adaptador);
        return true;
    }

    /**
     * O DatePicker começa na data de hoje e não aceita datas futuras (a
     * compra já aconteceu).
     */
    private void configurarDatePicker() {
        datePickerCompra.setMaxDate(System.currentTimeMillis());
        definirData(LocalDate.now());
    }

    // ------------------------------------------------------------------
    // Conversões entre o formulário e o Produto
    // ------------------------------------------------------------------

    /**
     * Mostra um LocalDate no DatePicker. Atenção: no DatePicker os meses vão
     * de 0 a 11, enquanto no LocalDate vão de 1 a 12.
     */
    private void definirData(LocalDate data) {
        datePickerCompra.updateDate(data.getYear(), data.getMonthValue() - 1, data.getDayOfMonth());
    }

    /** Lê a data escolhida no DatePicker como LocalDate. */
    private LocalDate lerData() {
        return LocalDate.of(datePickerCompra.getYear(),
                datePickerCompra.getMonth() + 1,
                datePickerCompra.getDayOfMonth());
    }

    /** Seleciona no Spinner o mercado com o id informado (se existir). */
    private void selecionarMercado(long mercadoId) {
        for (int i = 0; i < mercados.size(); i++) {
            if (mercados.get(i).getId() == mercadoId) {
                spinnerMercado.setSelection(i);
                return;
            }
        }
    }

    /** Converte o RadioButton marcado no código Produto.UNIDADE_*. */
    private int codigoUnidade(int idRadio) {
        if (idRadio == R.id.radioQuilo) {
            return Produto.UNIDADE_QUILO;
        } else if (idRadio == R.id.radioLitro) {
            return Produto.UNIDADE_LITRO;
        }
        return Produto.UNIDADE_UNIDADE;
    }

    /** Converte o código Produto.UNIDADE_* no RadioButton correspondente. */
    private int radioDaUnidade(int codigo) {
        if (codigo == Produto.UNIDADE_QUILO) {
            return R.id.radioQuilo;
        } else if (codigo == Produto.UNIDADE_LITRO) {
            return R.id.radioLitro;
        }
        return R.id.radioUnidade;
    }

    /** Converte o RadioButton marcado no código Produto.PAGAMENTO_*. */
    private int codigoPagamento(int idRadio) {
        if (idRadio == R.id.radioCartao) {
            return Produto.PAGAMENTO_CARTAO;
        } else if (idRadio == R.id.radioPix) {
            return Produto.PAGAMENTO_PIX;
        }
        return Produto.PAGAMENTO_DINHEIRO;
    }

    /** Converte o código Produto.PAGAMENTO_* no RadioButton correspondente. */
    private int radioDoPagamento(int codigo) {
        if (codigo == Produto.PAGAMENTO_CARTAO) {
            return R.id.radioCartao;
        } else if (codigo == Produto.PAGAMENTO_PIX) {
            return R.id.radioPix;
        }
        return R.id.radioDinheiro;
    }

    /**
     * Preenche o formulário com um produto lido do banco (modo edição).
     */
    private void preencherCampos(Produto produto) {
        editProduto.setText(produto.getNome());
        editMarca.setText(produto.getMarca());
        editPreco.setText(String.format(Locale.getDefault(), "%.2f", produto.getPreco()));
        editQuantidade.setText(Formatacao.numero(produto.getQuantidade()));
        editObservacoes.setText(produto.getObservacoes());

        selecionarMercado(produto.getMercadoId());
        spinnerCategoria.setSelection(produto.getCategoria());
        definirData(produto.getDataCompra());

        grupoUnidade.check(radioDaUnidade(produto.getUnidade()));
        grupoPagamento.check(radioDoPagamento(produto.getPagamento()));

        checkPromocao.setChecked(produto.isPromocao());
        checkFavorito.setChecked(produto.isFavorito());
        checkLembrete.setChecked(produto.isLembrete());
    }

    // ------------------------------------------------------------------
    // Ação "Salvar"
    // ------------------------------------------------------------------

    /**
     * Valida o formulário e, se estiver correto, insere ou altera o produto
     * no banco. Depois devolve RESULT_OK para a lista e encerra a tela.
     */
    private void salvar() {
        String nome = editProduto.getText().toString().trim();
        String marca = editMarca.getText().toString().trim();
        String precoDigitado = editPreco.getText().toString().trim();
        String quantidadeDigitada = editQuantidade.getText().toString().trim();

        // --- Validação dos campos de texto ---
        if (nome.isEmpty()) {
            avisarErro(R.string.erro_produto, editProduto);
            return;
        }
        if (marca.isEmpty()) {
            avisarErro(R.string.erro_marca, editMarca);
            return;
        }
        if (precoDigitado.isEmpty()) {
            avisarErro(R.string.erro_preco, editPreco);
            return;
        }
        double preco = Formatacao.lerNumero(precoDigitado);
        if (preco <= 0) {
            avisarErro(R.string.erro_preco_invalido, editPreco);
            return;
        }
        if (quantidadeDigitada.isEmpty()) {
            avisarErro(R.string.erro_quantidade, editQuantidade);
            return;
        }
        double quantidade = Formatacao.lerNumero(quantidadeDigitada);
        if (quantidade <= 0) {
            avisarErro(R.string.erro_quantidade_invalida, editQuantidade);
            return;
        }

        // --- Validação do Spinner e dos RadioGroup ---
        if (spinnerCategoria.getSelectedItemPosition() == POSICAO_CATEGORIA_VAZIA) {
            avisarErro(R.string.erro_categoria, spinnerCategoria);
            return;
        }
        if (grupoUnidade.getCheckedRadioButtonId() == -1) {
            avisarErro(R.string.erro_unidade, grupoUnidade);
            return;
        }
        if (grupoPagamento.getCheckedRadioButtonId() == -1) {
            avisarErro(R.string.erro_pagamento, grupoPagamento);
            return;
        }

        // Mercado escolhido no Spinner: o id dele é a chave estrangeira.
        Mercado mercado = (Mercado) spinnerMercado.getSelectedItem();

        // --- Formulário válido: monta o Produto ---
        Produto produto = (produtoEmEdicao != null) ? produtoEmEdicao : new Produto();
        produto.setNome(nome);
        produto.setMarca(marca);
        produto.setMercadoId(mercado.getId());
        produto.setCategoria(spinnerCategoria.getSelectedItemPosition());
        produto.setUnidade(codigoUnidade(grupoUnidade.getCheckedRadioButtonId()));
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);
        produto.setPagamento(codigoPagamento(grupoPagamento.getCheckedRadioButtonId()));
        produto.setPromocao(checkPromocao.isChecked());
        produto.setFavorito(checkFavorito.isChecked());
        produto.setLembrete(checkLembrete.isChecked());
        produto.setObservacoes(editObservacoes.getText().toString().trim());
        produto.setDataCompra(lerData());

        // --- Grava no banco: alteração (já tem id) ou inserção (novo) ---
        if (produtoEmEdicao != null) {
            banco.produtoDao().alterar(produto);
        } else {
            banco.produtoDao().inserir(produto);
        }

        // Guarda o mercado usado (usado pela sugestão nas Configurações).
        prefs.setUltimoMercadoId(mercado.getId());

        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_NOME, nome);
        setResult(RESULT_OK, resultado);
        finish();
    }

    /**
     * Mostra a mensagem de erro em um Toast, rola a tela até o componente com
     * problema e devolve o foco de edição para ele.
     */
    private void avisarErro(int idMensagem, final View componente) {
        Toast.makeText(this, idMensagem, Toast.LENGTH_SHORT).show();

        if (componente instanceof EditText) {
            ((EditText) componente).setError(getString(R.string.campo_obrigatorio));
        }

        componente.requestFocus();
        scrollFormulario.post(new Runnable() {
            @Override
            public void run() {
                scrollFormulario.smoothScrollTo(0, calcularPosicaoNaRolagem(componente));
            }
        });
    }

    /**
     * Soma as posições do componente e de seus containers até chegar ao
     * ScrollView, obtendo a coordenada vertical usada para rolar a tela.
     */
    private int calcularPosicaoNaRolagem(View componente) {
        int posicao = 0;
        View atual = componente;
        while (atual != null && atual != scrollFormulario) {
            posicao += atual.getTop();
            if (atual.getParent() instanceof View) {
                atual = (View) atual.getParent();
            } else {
                atual = null;
            }
        }
        return posicao;
    }

    // ------------------------------------------------------------------
    // Ação "Limpar"
    // ------------------------------------------------------------------

    /**
     * Apaga o conteúdo dos campos, desmarca os RadioButton e os CheckBox,
     * volta os Spinners e a data para o início e avisa com um Toast.
     */
    private void limpar() {
        editProduto.setText("");
        editMarca.setText("");
        editPreco.setText("");
        editQuantidade.setText("");
        editObservacoes.setText("");

        editProduto.setError(null);
        editMarca.setError(null);
        editPreco.setError(null);
        editQuantidade.setError(null);

        grupoUnidade.clearCheck();
        grupoPagamento.clearCheck();

        checkPromocao.setChecked(false);
        checkFavorito.setChecked(false);
        checkLembrete.setChecked(false);

        spinnerMercado.setSelection(0);
        spinnerCategoria.setSelection(POSICAO_CATEGORIA_VAZIA);
        definirData(LocalDate.now());

        editProduto.requestFocus();
        scrollFormulario.smoothScrollTo(0, 0);

        Toast.makeText(this, R.string.msg_limpo, Toast.LENGTH_SHORT).show();
    }

    // ------------------------------------------------------------------
    // Barra do sistema (edge-to-edge)
    // ------------------------------------------------------------------

    /**
     * A partir do Android 15 as telas são desenhadas de borda a borda. O recuo
     * do topo vai para a Toolbar, e os das laterais e de baixo (barra de
     * navegação / teclado) vão para o formulário.
     */
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
