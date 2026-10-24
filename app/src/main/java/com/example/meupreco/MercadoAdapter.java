package com.example.meupreco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.meupreco.modelo.Mercado;
import com.example.meupreco.modelo.MercadoComTotal;

import java.util.List;

/**
 * Meu Preço - Entrega Final.
 *
 * Adapter customizado da lista de mercados. Cada linha (item_mercado.xml)
 * mostra o nome, a cidade e quantos preços foram registrados no mercado.
 * Também usa o padrão ViewHolder.
 */
public class MercadoAdapter extends ArrayAdapter<MercadoComTotal> {

    private final LayoutInflater inflater;

    public MercadoAdapter(Context context, List<MercadoComTotal> itens) {
        super(context, 0, itens);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mercado, parent, false);
            holder = new ViewHolder();
            holder.textNome = convertView.findViewById(R.id.textNomeMercado);
            holder.textCidade = convertView.findViewById(R.id.textCidade);
            holder.textTotal = convertView.findViewById(R.id.textTotal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MercadoComTotal item = getItem(position);
        if (item != null) {
            Mercado mercado = item.mercado;
            holder.textNome.setText(mercado.getNome());

            String cidade = mercado.getCidade();
            holder.textCidade.setText(cidade == null || cidade.isEmpty()
                    ? getContext().getString(R.string.cidade_nao_informada)
                    : cidade);

            // Plural traduzido: "1 preço" / "3 preços" ou "1 price" / "3 prices".
            holder.textTotal.setText(Formatacao.totalPrecos(getContext(), item.totalProdutos));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textNome;
        TextView textCidade;
        TextView textTotal;
    }
}
