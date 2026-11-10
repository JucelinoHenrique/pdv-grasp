package br.com.pdv.dominio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CatalogoProdutos {

    private final Map<String, Produto> produtos = new HashMap<>();

    public Produto cadastrarProduto(String codigo, String descricao, BigDecimal preco) {
        if (produtos.containsKey(codigo)) {
            throw new IllegalArgumentException("Já existe produto com o código " + codigo);
        }
        Produto produto = new Produto(codigo, descricao, preco);
        produtos.put(codigo, produto);
        return produto;
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(produtos.get(codigo));
    }
}