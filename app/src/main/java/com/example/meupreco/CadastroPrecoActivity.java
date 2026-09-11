package com.example.meupreco;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

/**
 * Meu Preço - Entrega 1.
 *
 * Activity com o formulário de cadastro de um registro pessoal de preço
 * de um produto comprado em um mercado.
 *
 * O layout usa ScrollView para que o formulário possa ser rolado em
 * aparelhos com tela pequena (a partir de 4.7", como o perfil Nexus 4).
 */
public class CadastroPrecoActivity extends AppCompatActivity implements View.OnClickListener {

    // Posição do item "Selecione uma categoria" dentro do Spinner.
    private static final int POSICAO_CATEGORIA_VAZIA = 0;

    private View raiz;
    private View barraTopo;
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

    private Button botaoSalvar;
    private Button botaoLimpar;

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

        botaoSalvar.setOnClickListener(this);
        botaoLimpar.setOnClickListener(this);
    }

    /**
     * Liga os atributos da classe aos componentes declarados no arquivo de layout.
     */
    private void buscarComponentes() {
        raiz = findViewById(R.id.raiz);
        barraTopo = findViewById(R.id.barraTopo);
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

        botaoSalvar = findViewById(R.id.botaoSalvar);
        botaoLimpar = findViewById(R.id.botaoLimpar);
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
        final int paddingBase = Math.round(16 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(raiz, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                        | WindowInsetsCompat.Type.displayCutout());
                Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());

                // A faixa verde recebe o recuo do topo somado ao seu espaçamento
                // padrão, e o fundo verde preenche atrás da barra de status.
                barraTopo.setPadding(paddingBase + barras.left, paddingBase + barras.top,
                        paddingBase + barras.right, paddingBase);

                // O formulário recebe os recuos das laterais e da parte de baixo.
                scrollFormulario.setPadding(barras.left, 0, barras.right,
                        Math.max(barras.bottom, teclado.bottom));
                return insets;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.botaoSalvar) {
            salvar();
        } else if (view.getId() == R.id.botaoLimpar) {
            limpar();
        }
    }

    // ------------------------------------------------------------------
    // Botão "Salvar"
    // ------------------------------------------------------------------

    /**
     * Lê os valores digitados/selecionados, valida cada um deles e, quando o
     * formulário está correto, exibe um resumo do registro em um Toast.
     */
    private void salvar() {
        String produto = editProduto.getText().toString().trim();
        String marca = editMarca.getText().toString().trim();
        String mercado = editMercado.getText().toString().trim();
        String precoDigitado = editPreco.getText().toString().trim();
        String quantidadeDigitada = editQuantidade.getText().toString().trim();
        String observacoes = editObservacoes.getText().toString().trim();

        // --- Validação dos campos de texto ---
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
        if (quantidadeDigitada.isEmpty()) {
            avisarErro(R.string.erro_quantidade, editQuantidade);
            return;
        }
        double quantidade = converterParaNumero(quantidadeDigitada);
        if (quantidade <= 0) {
            avisarErro(R.string.erro_quantidade_invalida, editQuantidade);
            return;
        }
        if (observacoes.isEmpty()) {
            avisarErro(R.string.erro_observacoes, editObservacoes);
            return;
        }

        // --- Validação do Spinner ---
        if (spinnerCategoria.getSelectedItemPosition() == POSICAO_CATEGORIA_VAZIA) {
            avisarErro(R.string.erro_categoria, spinnerCategoria);
            return;
        }
        String categoria = spinnerCategoria.getSelectedItem().toString();

        // --- Validação dos RadioGroup ---
        if (grupoUnidade.getCheckedRadioButtonId() == -1) {
            avisarErro(R.string.erro_unidade, grupoUnidade);
            return;
        }
        if (grupoPagamento.getCheckedRadioButtonId() == -1) {
            avisarErro(R.string.erro_pagamento, grupoPagamento);
            return;
        }
        String unidade = lerRadioSelecionado(grupoUnidade);
        String pagamento = lerRadioSelecionado(grupoPagamento);

        // --- Leitura dos CheckBox ---
        String opcoes = lerOpcoesMarcadas();

        // --- Resumo mostrado no Toast (provisório, será uma listagem depois) ---
        double total = preco * quantidade;
        String resumo = produto + " (" + marca + ")"
                + "\nCategoria: " + categoria
                + "\nMercado: " + mercado
                + "\nR$ " + formatarValor(preco) + " por " + unidade
                + "\nQtde: " + formatarValor(quantidade)
                + " | Total: R$ " + formatarValor(total)
                + "\nPagamento: " + pagamento
                + "\nOpções: " + opcoes
                + "\nObs.: " + observacoes;

        Toast.makeText(this, resumo, Toast.LENGTH_LONG).show();
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
     * Formata um número com duas casas decimais e vírgula, no padrão brasileiro.
     */
    private String formatarValor(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    /**
     * Devolve o texto do RadioButton marcado dentro do RadioGroup informado.
     */
    private String lerRadioSelecionado(RadioGroup grupo) {
        RadioButton selecionado = findViewById(grupo.getCheckedRadioButtonId());
        return selecionado.getText().toString();
    }

    /**
     * Monta um texto com as opções (CheckBox) que estão marcadas.
     */
    private String lerOpcoesMarcadas() {
        StringBuilder texto = new StringBuilder();
        if (checkPromocao.isChecked()) {
            texto.append(checkPromocao.getText());
        }
        if (checkFavorito.isChecked()) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(checkFavorito.getText());
        }
        if (checkLembrete.isChecked()) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(checkLembrete.getText());
        }
        if (texto.length() == 0) {
            texto.append(getString(R.string.nenhuma_opcao));
        }
        return texto.toString();
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
