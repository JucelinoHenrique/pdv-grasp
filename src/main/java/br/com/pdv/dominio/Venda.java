package br.com.pdv.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venda {

    private final LocalDateTime dataHora = LocalDateTime.now();
    private final List<ItemVenda> itens = new ArrayList<>();
    private Pagamento pagamento;
    private boolean finalizada = false;

    // ===== CREATOR: a Venda cria os itens =====
    public ItemVenda adicionarItem(Produto produto, int quantidade) {
        verificarAberta();
        for (ItemVenda item : itens) {
            if (item.getProduto().equals(produto)) {
                item.adicionarQuantidade(quantidade);
                return item;
            }
        }
        ItemVenda novoItem = new ItemVenda(produto, quantidade);
        itens.add(novoItem);
        return novoItem;
    }

    // ===== INFORMATION EXPERT: a Venda calcula o total =====
    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            total = total.add(item.calcularSubtotal());
        }
        return total;
    }

    public void finalizar() {
        verificarAberta();
        if (itens.isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar uma venda sem itens.");
        }
        finalizada = true;
    }

    // ===== CREATOR: a Venda cria o pagamento =====
    public void registrarPagamento(BigDecimal valorEntregue) {
        if (!finalizada) {
            throw new IllegalStateException("Finalize a venda antes de pagar.");
        }
        if (pagamento != null) {
            throw new IllegalStateException("Esta venda já foi paga.");
        }
        if (valorEntregue.compareTo(calcularTotal()) < 0) {
            throw new IllegalArgumentException("Valor insuficiente. Total: R$ " + calcularTotal());
        }
        this.pagamento = new Pagamento(valorEntregue);
    }

    // ===== INFORMATION EXPERT: a Venda calcula o troco =====
    public BigDecimal calcularTroco() {
        if (pagamento == null) {
            throw new IllegalStateException("Venda ainda não foi paga.");
        }
        return pagamento.getValorEntregue().subtract(calcularTotal());
    }

    private void verificarAberta() {
        if (finalizada) {
            throw new IllegalStateException("A venda já foi finalizada.");
        }
    }

    public List<ItemVenda> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public boolean isPaga() {
        return pagamento != null;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}