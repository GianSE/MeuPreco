package com.example.meupreco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.meupreco.modelo.Produto;
import com.example.meupreco.modelo.ProdutoComMercado;

import java.util.List;

/**
 * Adapter customizado para exibir cada preço registrado (Produto + nome do
 * Mercado) em uma linha da ListView.
 *
 * Usa o layout de item "item_produto.xml" e o padrão ViewHolder para reaproveitar
 * as views recicladas pela ListView, evitando findViewById a cada rolagem.
 */
public class ProdutoAdapter extends ArrayAdapter<ProdutoComMercado> {

    private final LayoutInflater inflater;

    // Textos traduzidos da categoria e da unidade (o banco guarda só o código).
    private final String[] categorias;
    private final String[] unidades;

    public ProdutoAdapter(Context context, List<ProdutoComMercado> itens) {
        super(context, 0, itens);
        inflater = LayoutInflater.from(context);
        categorias = context.getResources().getStringArray(R.array.categorias);
        unidades = context.getResources().getStringArray(R.array.unidades);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_produto, parent, false);
            holder = new ViewHolder();
            holder.textNome = convertView.findViewById(R.id.textNome);
            holder.textDetalhe = convertView.findViewById(R.id.textDetalhe);
            holder.textMercado = convertView.findViewById(R.id.textMercado);
            holder.textPreco = convertView.findViewById(R.id.textPreco);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProdutoComMercado item = getItem(position);
        if (item != null) {
            Produto produto = item.produto;
            Context contexto = getContext();
            holder.textNome.setText(produto.getNome());
            holder.textDetalhe.setText(contexto.getString(R.string.formato_item_linha,
                    produto.getMarca(),
                    Formatacao.itemDoArray(categorias, produto.getCategoria())));
            // Mercado (vindo da chave estrangeira) e a data no formato do idioma.
            holder.textMercado.setText(contexto.getString(R.string.formato_item_linha,
                    item.nomeMercado,
                    Formatacao.data(produto.getDataCompra())));
            holder.textPreco.setText(contexto.getString(R.string.formato_item_preco,
                    Formatacao.preco(contexto, produto.getPreco()),
                    Formatacao.itemDoArray(unidades, produto.getUnidade())));
        }

        return convertView;
    }

    /**
     * Guarda as referências das views de uma linha (padrão ViewHolder).
     */
    private static class ViewHolder {
        TextView textNome;
        TextView textDetalhe;
        TextView textMercado;
        TextView textPreco;
    }
}
