package com.example.meupreco;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Meu Preço - Entrega 2.
 *
 * Activity principal (Launcher) que exibe, em uma ListView que ocupa toda a
 * tela, uma lista de produtos com seus preços registrados em mercados.
 *
 * Os dados são carregados de arrays do resource (arrays.xml), transformados em
 * objetos {@link Produto}, guardados em um ArrayList e exibidos por um
 * {@link ProdutoAdapter} customizado. Ao tocar em um item, um Toast identifica
 * o produto selecionado.
 */
public class ListaPrecosActivity extends AppCompatActivity {

    private final ArrayList<Produto> produtos = new ArrayList<>();

    private View raiz;
    private View barraTopo;
    private ListView listaProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_precos);

        // Ícones da barra de status em branco, para contrastar com a faixa verde.
        androidx.core.view.WindowInsetsControllerCompat controlador =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controlador.setAppearanceLightStatusBars(false);

        raiz = findViewById(R.id.raiz);
        barraTopo = findViewById(R.id.barraTopo);
        listaProdutos = findViewById(R.id.listaProdutos);

        carregarProdutos();
        tratarBarrasDoSistema();

        ProdutoAdapter adapter = new ProdutoAdapter(this, produtos);
        listaProdutos.setAdapter(adapter);

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
    }

    /**
     * Lê os arrays paralelos do resource e instancia os objetos Produto,
     * guardando-os no ArrayList. Cada índice combina um dado de cada array.
     */
    private void carregarProdutos() {
        Resources res = getResources();
        String[] nomes = res.getStringArray(R.array.dados_nomes);
        String[] marcas = res.getStringArray(R.array.dados_marcas);
        String[] mercados = res.getStringArray(R.array.dados_mercados);
        String[] categorias = res.getStringArray(R.array.dados_categorias);
        String[] unidades = res.getStringArray(R.array.dados_unidades);
        String[] precos = res.getStringArray(R.array.dados_precos);

        for (int i = 0; i < nomes.length; i++) {
            double preco = Double.parseDouble(precos[i].replace(",", "."));
            produtos.add(new Produto(
                    nomes[i], marcas[i], mercados[i], categorias[i], unidades[i], preco));
        }
    }

    /**
     * Distribui os espaçamentos das barras do sistema (edge-to-edge do
     * Android 15+): o recuo do topo vai para a faixa verde e o recuo de baixo
     * para a ListView, de modo que o último item não fique atrás da barra de
     * navegação.
     */
    private void tratarBarrasDoSistema() {
        final int paddingBase = Math.round(16 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(raiz, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                        | WindowInsetsCompat.Type.displayCutout());

                barraTopo.setPadding(paddingBase + barras.left, paddingBase + barras.top,
                        paddingBase + barras.right, paddingBase);
                listaProdutos.setPadding(barras.left, listaProdutos.getPaddingTop(),
                        barras.right, barras.bottom);
                return insets;
            }
        });
    }
}
