package com.example.meupreco.modelo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/**
 * Meu Preço - Entrega Final.
 *
 * Entidade (tabela "produtos") do tema "Registro pessoal de preços de produtos
 * em mercados". Cada objeto é um preço anotado de um produto em um mercado.
 *
 * - Chave estrangeira: mercadoId aponta para mercados.id. Com RESTRICT, o banco
 *   não deixa excluir um mercado que ainda tenha preços registrados.
 * - dataCompra é um LocalDate, gravado no banco pelo ConversorData.
 * - categoria, unidade e pagamento são guardados como números (códigos), e não
 *   como texto, para que o registro continue correto se o idioma do aparelho
 *   mudar. O texto exibido vem dos recursos (strings/arrays) de cada idioma.
 */
@Entity(tableName = "produtos",
        foreignKeys = @ForeignKey(entity = Mercado.class,
                parentColumns = "id",
                childColumns = "mercadoId",
                onDelete = ForeignKey.RESTRICT),
        indices = @Index("mercadoId"))
public class Produto {

    // Códigos da unidade de medida (RadioGroup de unidade).
    public static final int UNIDADE_UNIDADE = 0;
    public static final int UNIDADE_QUILO = 1;
    public static final int UNIDADE_LITRO = 2;

    // Códigos da forma de pagamento (RadioGroup de pagamento).
    public static final int PAGAMENTO_DINHEIRO = 0;
    public static final int PAGAMENTO_CARTAO = 1;
    public static final int PAGAMENTO_PIX = 2;

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String nome = "";

    @NonNull
    private String marca = "";

    /** Chave estrangeira para o Mercado onde o preço foi anotado. */
    private long mercadoId;

    /** Posição da categoria no array R.array.categorias (1 em diante). */
    private int categoria;

    /** Um dos códigos UNIDADE_*. */
    private int unidade;

    private double preco;

    private double quantidade;

    /** Um dos códigos PAGAMENTO_*. */
    private int pagamento;

    private boolean promocao;

    private boolean favorito;

    private boolean lembrete;

    private String observacoes;

    /** Data da compra, escolhida no DatePicker. */
    @NonNull
    private LocalDate dataCompra = LocalDate.now();

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

    @NonNull
    public String getMarca() {
        return marca;
    }

    public void setMarca(@NonNull String marca) {
        this.marca = marca;
    }

    public long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getUnidade() {
        return unidade;
    }

    public void setUnidade(int unidade) {
        this.unidade = unidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public int getPagamento() {
        return pagamento;
    }

    public void setPagamento(int pagamento) {
        this.pagamento = pagamento;
    }

    public boolean isPromocao() {
        return promocao;
    }

    public void setPromocao(boolean promocao) {
        this.promocao = promocao;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public boolean isLembrete() {
        return lembrete;
    }

    public void setLembrete(boolean lembrete) {
        this.lembrete = lembrete;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @NonNull
    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(@NonNull LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }
}
