package br.com.pdv.app;

import br.com.pdv.controlador.ControladorPDV;
import br.com.pdv.dominio.CatalogoProdutos;
import br.com.pdv.ui.Tema;
import br.com.pdv.ui.TelaPDV;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;

public class AppSwing {

    public static void main(String[] args) {
        Tema.aplicar(); // sempre antes de criar qualquer componente

        CatalogoProdutos catalogo = new CatalogoProdutos();
        catalogo.cadastrarProduto("001", "Arroz 5kg", new BigDecimal("27.90"));
        catalogo.cadastrarProduto("002", "Feijão 1kg", new BigDecimal("8.49"));
        catalogo.cadastrarProduto("003", "Café 500g", new BigDecimal("18.75"));
        catalogo.cadastrarProduto("004", "Açúcar 1kg", new BigDecimal("4.99"));
        catalogo.cadastrarProduto("005", "Leite 1L", new BigDecimal("5.29"));

        ControladorPDV controlador = new ControladorPDV(catalogo);

        SwingUtilities.invokeLater(() -> new TelaPDV(controlador).setVisible(true));
    }
}