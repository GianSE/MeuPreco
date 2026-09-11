package com.example.meupreco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

/**
 * Adapter customizado para exibir cada {@link Produto} em uma linha da ListView.
 *
 * Usa o layout de item "item_produto.xml" e o padrão ViewHolder para reaproveitar
 * as views recicladas pela ListView, evitando findViewById a cada rolagem.
 */
public class ProdutoAdapter extends ArrayAdapter<Produto> {

    private final LayoutInflater inflater;

    public ProdutoAdapter(Context context, List<Produto> produtos) {
        super(context, 0, produtos);
        inflater = LayoutInflater.from(context);
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

        Produto produto = getItem(position);
        if (produto != null) {
            holder.textNome.setText(produto.getNome());
            holder.textDetalhe.setText(produto.getMarca() + " • " + produto.getCategoria());
            holder.textMercado.setText(produto.getMercado());
            holder.textPreco.setText(formatarPreco(produto.getPreco(), produto.getUnidade()));
        }

        return convertView;
    }

    /**
     * Formata o preço no padrão brasileiro, ex.: "R$ 27,90 / Pacote".
     */
    private String formatarPreco(double preco, String unidade) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", preco)
                + "\n" + unidade;
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
