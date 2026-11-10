package br.com.pdv.app;

import br.com.pdv.controlador.ControladorPDV;
import br.com.pdv.dominio.CatalogoProdutos;
import br.com.pdv.dominio.ItemVenda;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        // 1. Montagem do sistema
        CatalogoProdutos catalogo = new CatalogoProdutos();
        catalogo.cadastrarProduto("001", "Arroz 5kg", new BigDecimal("27.90"));
        catalogo.cadastrarProduto("002", "Feijão 1kg", new BigDecimal("8.49"));
        catalogo.cadastrarProduto("003", "Café 500g", new BigDecimal("18.75"));

        ControladorPDV pdv = new ControladorPDV(catalogo);

        // 2. Uma venda completa (caminho feliz)
        System.out.println("========== VENDA 1 ==========");
        pdv.iniciarNovaVenda();
        pdv.adicionarItem("001", 1);
        pdv.adicionarItem("002", 2);
        pdv.adicionarItem("003", 1);
        pdv.adicionarItem("002", 1); // mesmo produto: deve somar na linha do feijão

        for (ItemVenda item : pdv.getVendaAtual().getItens()) {
            System.out.println("  " + item);
        }

        BigDecimal total = pdv.finalizarVenda();
        System.out.println("TOTAL: R$ " + total);

        BigDecimal troco = pdv.realizarPagamento(new BigDecimal("100.00"));
        System.out.println("PAGO:  R$ 100.00");
        System.out.println("TROCO: R$ " + troco);

        // 3. Regras que o sistema deve recusar
        System.out.println();
        System.out.println("====== TESTES DE REGRA ======");
        pdv.iniciarNovaVenda();
        testar("Produto inexistente", () -> pdv.adicionarItem("999", 1));
        testar("Quantidade zero", () -> pdv.adicionarItem("001", 0));
        testar("Finalizar venda vazia", () -> pdv.finalizarVenda());

        pdv.adicionarItem("001", 1); // R$ 27.90
        pdv.finalizarVenda();
        testar("Adicionar item após finalizar", () -> pdv.adicionarItem("002", 1));
        testar("Pagamento insuficiente", () -> pdv.realizarPagamento(new BigDecimal("20.00")));
    }

    private static void testar(String descricao, Runnable acao) {
        try {
            acao.run();
            System.out.println("[FALHOU] " + descricao + ": deveria ter sido recusado!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[OK] " + descricao + " -> " + e.getMessage());
        }
    }
}