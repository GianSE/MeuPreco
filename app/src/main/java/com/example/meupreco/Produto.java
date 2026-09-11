package com.example.meupreco;

/**
 * Entidade do tema "Registro pessoal de preços de produtos em mercados".
 *
 * Cada objeto representa um produto cujo preço foi anotado em um mercado.
 * Possui seis atributos (nome, marca, mercado, categoria, unidade e preço).
 */
public class Produto {

    private final String nome;
    private final String marca;
    private final String mercado;
    private final String categoria;
    private final String unidade;
    private final double preco;

    public Produto(String nome, String marca, String mercado,
                   String categoria, String unidade, double preco) {
        this.nome = nome;
        this.marca = marca;
        this.mercado = mercado;
        this.categoria = categoria;
        this.unidade = unidade;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public String getMarca() {
        return marca;
    }

    public String getMercado() {
        return mercado;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getPreco() {
        return preco;
    }
}
