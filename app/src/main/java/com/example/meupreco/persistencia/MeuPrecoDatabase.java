package com.example.meupreco.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.meupreco.modelo.Mercado;
import com.example.meupreco.modelo.Produto;

/**
 * Meu Preço - Entrega Final.
 *
 * Banco de dados Room do aplicativo, com as tabelas mercados e produtos.
 * Segue o padrão Singleton: existe uma única instância do banco para o app
 * inteiro, obtida com MeuPrecoDatabase.getInstance(context).
 */
@Database(entities = {Mercado.class, Produto.class}, version = 1, exportSchema = false)
@TypeConverters(ConversorData.class)
public abstract class MeuPrecoDatabase extends RoomDatabase {

    private static final String NOME_ARQUIVO = "meupreco.db";

    private static volatile MeuPrecoDatabase instancia;

    public abstract MercadoDao mercadoDao();

    public abstract ProdutoDao produtoDao();

    public static MeuPrecoDatabase getInstance(Context contexto) {
        if (instancia == null) {
            synchronized (MeuPrecoDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(contexto.getApplicationContext(),
                                    MeuPrecoDatabase.class, NOME_ARQUIVO)
                            // As consultas são pequenas (dados de um único
                            // usuário), por isso podem rodar na thread principal.
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instancia;
    }
}
