package com.example.meupreco.modelo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Meu Preço - Entrega Final.
 *
 * Entidade (tabela "mercados") que representa um mercado onde os preços são
 * anotados. É a tabela "pai" da chave estrangeira: cada Produto aponta para um
 * Mercado pelo campo mercadoId.
 *
 * O nome é único (índice unique) para não existirem dois mercados iguais.
 */
@Entity(tableName = "mercados",
        indices = @Index(value = "nome", unique = true))
public class Mercado {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String nome = "";

    private String cidade;

    /** Construtor vazio usado pelo Room ao ler os dados do banco. */
    public Mercado() {
    }

    /** Construtor de conveniência (ignorado pelo Room). */
    @Ignore
    public Mercado(@NonNull String nome, String cidade) {
        this.nome = nome;
        this.cidade = cidade;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    /**
     * O ArrayAdapter do Spinner de mercados usa o toString() para exibir cada
     * item, por isso ele devolve o nome do mercado.
     */
    @NonNull
    @Override
    public String toString() {
        return nome;
    }
}
