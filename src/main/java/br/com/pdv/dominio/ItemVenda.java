package br.com.pdv.dominio;

import java.math.BigDecimal;

public class ItemVenda {

    private final Produto produto;
    private int quantidade;

    ItemVenda(Produto produto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public BigDecimal calcularSubtotal() {
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

    void adicionarQuantidade(int quantidadeAdicional) {
        if (quantidadeAdicional <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        this.quantidade += quantidadeAdicional;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    @Override
    public String toString() {
        return quantidade + "x " + produto.getDescricao() + " = R$ " + calcularSubtotal();
    }
}