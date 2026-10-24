package com.example.meupreco.modelo;

import androidx.room.Embedded;

/**
 * Meu Preço - Entrega Final.
 *
 * Resultado da consulta que lista os mercados junto com a quantidade de preços
 * registrados em cada um (LEFT JOIN + COUNT). A lista de mercados usa o total
 * para mostrar a informação e para impedir a exclusão de um mercado em uso.
 */
public class MercadoComTotal {

    @Embedded
    public Mercado mercado;

    /** Coluna "totalProdutos" da consulta (COUNT(produtos.id)). */
    public int totalProdutos;
}
