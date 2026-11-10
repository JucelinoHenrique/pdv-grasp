package br.com.pdv.controlador;

import br.com.pdv.dominio.CatalogoProdutos;
import br.com.pdv.dominio.ItemVenda;
import br.com.pdv.dominio.Produto;
import br.com.pdv.dominio.Venda;

import java.math.BigDecimal;

public class ControladorPDV {

    private final CatalogoProdutos catalogo;
    private Venda vendaAtual;

    public ControladorPDV(CatalogoProdutos catalogo) {
        this.catalogo = catalogo;
    }

    // Evento do sistema 1
    public void iniciarNovaVenda() {
        if (vendaAtual != null && !vendaAtual.isPaga()) {
            throw new IllegalStateException("Existe uma venda em andamento.");
        }
        vendaAtual = new Venda();
    }

    // Evento do sistema 2
    public ItemVenda adicionarItem(String codigoProduto, int quantidade) {
        verificarVendaIniciada();
        Produto produto = catalogo.buscarPorCodigo(codigoProduto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produto não encontrado: " + codigoProduto));
        return vendaAtual.adicionarItem(produto, quantidade);
    }

    // Evento do sistema 3
    public BigDecimal finalizarVenda() {
        verificarVendaIniciada();
        vendaAtual.finalizar();
        return vendaAtual.calcularTotal();
    }

    // Evento do sistema 4
    public BigDecimal realizarPagamento(BigDecimal valorEntregue) {
        verificarVendaIniciada();
        vendaAtual.registrarPagamento(valorEntregue);
        return vendaAtual.calcularTroco();
    }

    public BigDecimal getTotalAtual() {
        verificarVendaIniciada();
        return vendaAtual.calcularTotal();
    }

    public Venda getVendaAtual() {
        return vendaAtual;
    }

    private void verificarVendaIniciada() {
        if (vendaAtual == null) {
            throw new IllegalStateException("Nenhuma venda iniciada.");
        }
    }
}