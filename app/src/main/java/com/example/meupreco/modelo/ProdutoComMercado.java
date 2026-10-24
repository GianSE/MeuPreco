package com.example.meupreco.modelo;

import androidx.room.Embedded;

/**
 * Meu Preço - Entrega Final.
 *
 * Resultado da consulta que junta (JOIN) a tabela produtos com a tabela
 * mercados pela chave estrangeira. Não é uma tabela: só carrega o Produto
 * completo mais o nome do mercado, para a lista exibir os dois juntos.
 */
public class ProdutoComMercado {

    /** Todas as colunas da tabela produtos. */
    @Embedded
    public Produto produto;

    /** Coluna "nomeMercado" da consulta (mercados.nome AS nomeMercado). */
    public String nomeMercado;
}
