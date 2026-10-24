package com.example.meupreco.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.meupreco.modelo.Mercado;
import com.example.meupreco.modelo.MercadoComTotal;

import java.util.List;

/**
 * Meu Preço - Entrega Final.
 *
 * DAO (Data Access Object) da tabela mercados: o Room gera a implementação de
 * cada método a partir das anotações (@Insert, @Update, @Delete, @Query).
 */
@Dao
public interface MercadoDao {

    /** Insere e devolve o id gerado. */
    @Insert
    long inserir(Mercado mercado);

    @Update
    int alterar(Mercado mercado);

    @Delete
    int excluir(Mercado mercado);

    @Query("SELECT * FROM mercados WHERE id = :id")
    Mercado buscarPorId(long id);

    /** Usado para não cadastrar dois mercados com o mesmo nome. */
    @Query("SELECT * FROM mercados WHERE nome = :nome COLLATE NOCASE LIMIT 1")
    Mercado buscarPorNome(String nome);

    /** Usado no Spinner de mercados do cadastro de preço. */
    @Query("SELECT * FROM mercados ORDER BY nome COLLATE NOCASE")
    List<Mercado> listarTodos();

    @Query("SELECT COUNT(*) FROM mercados")
    int contar();

    /**
     * Lista os mercados com a quantidade de preços registrados em cada um.
     * O LEFT JOIN mantém na lista os mercados que ainda não têm preços.
     */
    @Query("SELECT mercados.*, COUNT(produtos.id) AS totalProdutos "
            + "FROM mercados LEFT JOIN produtos ON produtos.mercadoId = mercados.id "
            + "GROUP BY mercados.id "
            + "ORDER BY mercados.nome COLLATE NOCASE")
    List<MercadoComTotal> listarComTotal();
}
