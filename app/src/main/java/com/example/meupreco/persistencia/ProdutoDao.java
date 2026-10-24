package com.example.meupreco.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.meupreco.modelo.Produto;
import com.example.meupreco.modelo.ProdutoComMercado;

import java.util.List;

/**
 * Meu Preço - Entrega Final.
 *
 * DAO da tabela produtos. As três consultas de listagem fazem um JOIN com a
 * tabela mercados (pela chave estrangeira mercadoId) para trazer também o nome
 * do mercado, e diferem só na ordenação escolhida nas Configurações.
 */
@Dao
public interface ProdutoDao {

    /** Insere e devolve o id gerado. */
    @Insert
    long inserir(Produto produto);

    @Update
    int alterar(Produto produto);

    @Delete
    int excluir(Produto produto);

    @Query("SELECT * FROM produtos WHERE id = :id")
    Produto buscarPorId(long id);

    /** Quantos preços usam o mercado (chave estrangeira). */
    @Query("SELECT COUNT(*) FROM produtos WHERE mercadoId = :mercadoId")
    int contarPorMercado(long mercadoId);

    String CONSULTA_COM_MERCADO = "SELECT produtos.*, mercados.nome AS nomeMercado "
            + "FROM produtos INNER JOIN mercados ON produtos.mercadoId = mercados.id ";

    @Query(CONSULTA_COM_MERCADO + "ORDER BY produtos.nome COLLATE NOCASE")
    List<ProdutoComMercado> listarPorNome();

    @Query(CONSULTA_COM_MERCADO + "ORDER BY produtos.preco")
    List<ProdutoComMercado> listarPorPreco();

    /** Mais recentes primeiro. */
    @Query(CONSULTA_COM_MERCADO + "ORDER BY produtos.dataCompra DESC, produtos.nome COLLATE NOCASE")
    List<ProdutoComMercado> listarPorData();
}
