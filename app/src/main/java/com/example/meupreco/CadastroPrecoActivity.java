package com.example.meupreco;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

/**
 * Meu Preço - Entrega 1 (atualizada na Entrega 3).
 *
 * Activity com o formulário de cadastro de um registro pessoal de preço
 * de um produto comprado em um mercado.
 *
 * O layout usa ScrollView para que o formulário possa ser rolado em
 * aparelhos com tela pequena (a partir de 4.7", como o perfil Nexus 4).
 */
public class CadastroPrecoActivity extends AppCompatActivity {

    // Posição do item "Selecione uma categoria" dentro do Spinner.
    private static final int POSICAO_CATEGORIA_VAZIA = 0;

    // Chaves usadas para devolver os dados do produto à tela de Listagem.
    public static final String EXTRA_NOME = "extra_nome";
    public static final String EXTRA_MARCA = "extra_marca";
    public static final String EXTRA_MERCADO = "extra_mercado";
    public static final String EXTRA_CATEGORIA = "extra_categoria";
    public static final String EXTRA_UNIDADE = "extra_unidade";
    public static final String EXTRA_PRECO = "extra_preco";

    private View raiz;
    private Toolbar toolbar;
    private ScrollView scrollFormulario;

    private EditText editProduto;
    private EditText editMarca;
    private EditText editMercado;
    private EditText editPreco;
    private EditText editQuantidade;
    private EditText editObservacoes;

    private Spinner spinnerCategoria;

    private RadioGroup grupoUnidade;
    private RadioGroup grupoPagamento;

    private CheckBox checkPromocao;
    private CheckBox checkFavorito;
    private CheckBox checkLembrete;

    // Indica se a tela foi aberta em modo de edição de um item já existente.
    private boolean modoEdicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_preco);

        // Ícones da barra de status em branco, para contrastar com a faixa verde.
        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        buscarComponentes();
        configurarSpinner();
        tratarBarrasDoSistema();

        // Se veio com dados de um item, abre em modo de edição e preenche o form.
        modoEdicao = getIntent().hasExtra(EXTRA_NOME);
        if (modoEdicao) {
            preencherCampos(getIntent());
        }

        // Exibe a Barra do Aplicativo com título e botão de voltar (cancelar).
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    modoEdicao ? R.string.titulo_cadastro_editar : R.string.titulo_cadastro_bar);
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

    /**
     * A seta "voltar" da Barra do Aplicativo cancela o cadastro (volta sem
     * adicionar nada à lista).
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Liga os atributos da classe aos componentes declarados no arquivo de layout.
     */
    private void buscarComponentes() {
        raiz = findViewById(R.id.raiz);
        toolbar = findViewById(R.id.toolbar);
        scrollFormulario = findViewById(R.id.scrollFormulario);

        editProduto = findViewById(R.id.editProduto);
        editMarca = findViewById(R.id.editMarca);
        editMercado = findViewById(R.id.editMercado);
        editPreco = findViewById(R.id.editPreco);
        editQuantidade = findViewById(R.id.editQuantidade);
        editObservacoes = findViewById(R.id.editObservacoes);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        grupoUnidade = findViewById(R.id.grupoUnidade);
        grupoPagamento = findViewById(R.id.grupoPagamento);

        checkPromocao = findViewById(R.id.checkPromocao);
        checkFavorito = findViewById(R.id.checkFavorito);
        checkLembrete = findViewById(R.id.checkLembrete);
    }

    /**
     * Preenche o formulário com os dados de um produto recebido por Intent
     * (modo edição), para que o usuário possa alterá-los.
     */
    private void preencherCampos(Intent intent) {
        editProduto.setText(intent.getStringExtra(EXTRA_NOME));
        editMarca.setText(intent.getStringExtra(EXTRA_MARCA));
        editMercado.setText(intent.getStringExtra(EXTRA_MERCADO));
        editPreco.setText(String.format(Locale.forLanguageTag("pt-BR"),
                "%.2f", intent.getDoubleExtra(EXTRA_PRECO, 0)));

        selecionarCategoria(intent.getStringExtra(EXTRA_CATEGORIA));
        selecionarUnidade(intent.getStringExtra(EXTRA_UNIDADE));
    }

    /**
     * Seleciona no Spinner a categoria informada (procura pelo texto no array).
     */
    private void selecionarCategoria(String categoria) {
        if (categoria == null) {
            return;
        }
        String[] categorias = getResources().getStringArray(R.array.categorias);
        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(categoria)) {
                spinnerCategoria.setSelection(i);
                return;
            }
        }
    }

    /**
     * Marca no RadioGroup de unidade o RadioButton cujo texto corresponde.
     */
    private void selecionarUnidade(String unidade) {
        if (unidade == null) {
            return;
        }
        for (int i = 0; i < grupoUnidade.getChildCount(); i++) {
            View filho = grupoUnidade.getChildAt(i);
            if (filho instanceof RadioButton
                    && ((RadioButton) filho).getText().toString().equals(unidade)) {
                ((RadioButton) filho).setChecked(true);
                return;
            }
        }
    }

    /**
     * Monta o adaptador do Spinner a partir do array de categorias (arrays.xml).
     */
    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(
                this,
                R.array.categorias,
                android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adaptador);
        spinnerCategoria.setSelection(POSICAO_CATEGORIA_VAZIA);
    }

    /**
     * A partir do Android 15 as telas são desenhadas de borda a borda. Aqui os
     * espaçamentos das barras do sistema são distribuídos: o recuo do topo
     * (barra de status / recorte da câmera) vai para a faixa verde, e os recuos
     * das laterais e da parte de baixo (barra de navegação / teclado) vão para o
     * formulário, para que o título não fique escondido e o último botão não
     * fique atrás da barra de navegação.
     */
    private void tratarBarrasDoSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                        | WindowInsetsCompat.Type.displayCutout());
                Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());

                // A Barra do Aplicativo (Toolbar) recebe o recuo do topo, e o
                // fundo verde preenche atrás da barra de status.
                toolbar.setPadding(barras.left, barras.top, barras.right, 0);

                // O formulário recebe os recuos das laterais e da parte de baixo.
                scrollFormulario.setPadding(barras.left, 0, barras.right,
                        Math.max(barras.bottom, teclado.bottom));
                return insets;
            }
        });
    }

    // ------------------------------------------------------------------
    // Ação "Salvar"
    // ------------------------------------------------------------------

    /**
     * Lê os valores digitados/selecionados, valida cada um deles e, quando o
     * formulário está correto, devolve os dados à tela de Listagem através de
     * setResult(RESULT_OK) e encerra esta Activity.
     */
    private void salvar() {
        String produto = editProduto.getText().toString().trim();
        String marca = editMarca.getText().toString().trim();
        String mercado = editMercado.getText().toString().trim();
        String precoDigitado = editPreco.getText().toString().trim();

        // --- Validação dos campos que compõem a entidade Produto ---
        if (produto.isEmpty()) {
            avisarErro(R.string.erro_produto, editProduto);
            return;
        }
        if (marca.isEmpty()) {
            avisarErro(R.string.erro_marca, editMarca);
            return;
        }
        if (mercado.isEmpty()) {
            avisarErro(R.string.erro_mercado, editMercado);
            return;
        }
        if (precoDigitado.isEmpty()) {
            avisarErro(R.string.erro_preco, editPreco);
            return;
        }
        double preco = converterParaNumero(precoDigitado);
        if (preco <= 0) {
            avisarErro(R.string.erro_preco_invalido, editPreco);
            return;
        }

        // --- Validação do Spinner ---
        if (spinnerCategoria.getSelectedItemPosition() == POSICAO_CATEGORIA_VAZIA) {
            avisarErro(R.string.erro_categoria, spinnerCategoria);
            return;
        }
        String categoria = spinnerCategoria.getSelectedItem().toString();

        // --- Validação do RadioGroup de unidade ---
        if (grupoUnidade.getCheckedRadioButtonId() == -1) {
            avisarErro(R.string.erro_unidade, grupoUnidade);
            return;
        }
        String unidade = lerRadioSelecionado(grupoUnidade);
        // Os campos quantidade, forma de pagamento, opções e observações
        // continuam no formulário, mas são opcionais: não fazem parte da
        // entidade Produto exibida (e persistida) na lista.

        // --- Formulário válido: devolve os dados à tela de Listagem ---
        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_NOME, produto);
        resultado.putExtra(EXTRA_MARCA, marca);
        resultado.putExtra(EXTRA_MERCADO, mercado);
        resultado.putExtra(EXTRA_CATEGORIA, categoria);
        resultado.putExtra(EXTRA_UNIDADE, unidade);
        resultado.putExtra(EXTRA_PRECO, preco);

        setResult(RESULT_OK, resultado);
        finish();
    }

    /**
     * Converte o texto digitado em número, aceitando vírgula ou ponto como
     * separador decimal. Retorna -1 quando o texto não é um número válido.
     */
    private double converterParaNumero(String texto) {
        try {
            return Double.parseDouble(texto.replace(",", "."));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Devolve o texto do RadioButton marcado dentro do RadioGroup informado.
     */
    private String lerRadioSelecionado(RadioGroup grupo) {
        RadioButton selecionado = findViewById(grupo.getCheckedRadioButtonId());
        return selecionado.getText().toString();
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
    // Botão "Limpar"
    // ------------------------------------------------------------------

    /**
     * Apaga o conteúdo dos EditText, desmarca os RadioButton e os CheckBox,
     * volta o Spinner para o item inicial e avisa o usuário com um Toast.
     */
    private void limpar() {
        editProduto.setText("");
        editMarca.setText("");
        editMercado.setText("");
        editPreco.setText("");
        editQuantidade.setText("");
        editObservacoes.setText("");

        editProduto.setError(null);
        editMarca.setError(null);
        editMercado.setError(null);
        editPreco.setError(null);
        editQuantidade.setError(null);
        editObservacoes.setError(null);

        grupoUnidade.clearCheck();
        grupoPagamento.clearCheck();

        checkPromocao.setChecked(false);
        checkFavorito.setChecked(false);
        checkLembrete.setChecked(false);

        spinnerCategoria.setSelection(POSICAO_CATEGORIA_VAZIA);

        editProduto.requestFocus();
        scrollFormulario.smoothScrollTo(0, 0);

        Toast.makeText(this, R.string.msg_limpo, Toast.LENGTH_SHORT).show();
    }
}
