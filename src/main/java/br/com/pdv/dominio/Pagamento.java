package br.com.pdv.dominio;

import java.math.BigDecimal;

public class Pagamento {

    private final BigDecimal valorEntregue;

    Pagamento(BigDecimal valorEntregue) {
        if (valorEntregue == null || valorEntregue.signum() <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo.");
        }
        this.valorEntregue = valorEntregue;
    }

    public BigDecimal getValorEntregue() {
        return valorEntregue;
    }
}