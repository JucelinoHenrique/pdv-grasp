package br.com.pdv.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public class Produto {

    private final String codigo;
    private final String descricao;
    private final BigDecimal preco;

    public Produto(String codigo, String descricao, BigDecimal preco) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código do produto é obrigatório.");
        }
        if (preco == null || preco.signum() < 0) {
            throw new IllegalArgumentException("Preço não pode ser nulo ou negativo.");
        }
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        return codigo.equals(((Produto) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao + " (R$ " + preco + ")";
    }
}