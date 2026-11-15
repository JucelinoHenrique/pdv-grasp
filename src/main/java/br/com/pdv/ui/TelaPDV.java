package br.com.pdv.ui;

import br.com.pdv.controlador.ControladorPDV;
import br.com.pdv.dominio.ItemVenda;
import br.com.pdv.dominio.Venda;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class TelaPDV extends JFrame {

    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    // A única ligação da tela com o sistema
    private final ControladorPDV controlador;

    // ===== Entrada de itens =====
    private final JTextField txtCodigo = new JTextField(12);
    private final JSpinner spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JButton btnAdicionar = new JButton("Adicionar   Enter");

    // ===== Cupom (tabela de itens) =====
    private final DefaultTableModel modeloItens = new DefaultTableModel(
            new Object[]{"Produto", "Qtd", "Preço unit.", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false; // o cupom é só para leitura
        }
    };
    private final JTable tabelaItens = new JTable(modeloItens);

    // ===== Painel lateral =====
    private final JLabel lblTotal = new JLabel(MOEDA.format(0));
    private final JButton btnFinalizar = new JButton("Finalizar venda   F2");
    private final JTextField txtValorRecebido = new JTextField();
    private final JButton btnPagar = new JButton("Receber pagamento   F4");
    private final JLabel lblTroco = new JLabel("Troco: " + MOEDA.format(0));
    private final JButton btnNovaVenda = new JButton("Nova venda   F1");

    // ===== Indicadores =====
    private final JLabel lblStatusVenda = new JLabel();
    private final JLabel lblMensagem = new JLabel(" ");

    public TelaPDV(ControladorPDV controlador) {
        super("PDV Comercial");
        this.controlador = controlador;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 620));

        JPanel raiz = new JPanel(new BorderLayout(0, Tema.ESPACO));
        raiz.setBorder(new EmptyBorder(Tema.ESPACO, Tema.ESPACO, Tema.ESPACO, Tema.ESPACO));
        raiz.add(criarCabecalho(), BorderLayout.NORTH);
        raiz.add(criarCentro(), BorderLayout.CENTER);
        raiz.add(criarBarraStatus(), BorderLayout.SOUTH);
        setContentPane(raiz);

        setLocationRelativeTo(null); // abre no centro da tela

        configurarAcoes();
        novaVenda(); // a tela já abre com uma venda pronta para receber itens
    }

    // ---------- Cabeçalho: título + status da venda ----------
    private JComponent criarCabecalho() {
        JLabel titulo = new JLabel("PDV Comercial");
        titulo.setFont(Tema.fonte(Font.BOLD, 22f));
        titulo.setForeground(Tema.TEXTO);

        lblStatusVenda.setFont(Tema.fonte(Font.BOLD, 13f));
        mostrarStatus("● Nenhuma venda", Tema.TEXTO_SECUNDARIO);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(lblStatusVenda, BorderLayout.EAST);
        return cabecalho;
    }

    // ---------- Centro: itens à esquerda, lateral à direita ----------
    private JComponent criarCentro() {
        JPanel centro = new JPanel(new BorderLayout(Tema.ESPACO, 0));
        centro.setOpaque(false);
        centro.add(criarAreaItens(), BorderLayout.CENTER);
        centro.add(criarLateral(), BorderLayout.EAST);
        return centro;
    }

    private JComponent criarAreaItens() {
        txtCodigo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Código do produto");
        txtCodigo.setFont(Tema.fonte(Font.PLAIN, 16f));
        spnQuantidade.setFont(Tema.fonte(Font.PLAIN, 16f));
        estilizarBotaoPrimario(btnAdicionar);

        // Linha de entrada: [código (estica)] [qtd] [botão]
        JPanel entrada = new JPanel(new GridBagLayout());
        entrada.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(0, 0, 0, 8);
        g.weightx = 1;
        entrada.add(txtCodigo, g);
        g.weightx = 0;
        entrada.add(spnQuantidade, g);
        g.insets = new Insets(0, 0, 0, 0);
        entrada.add(btnAdicionar, g);

        configurarTabela();
        JScrollPane rolagem = new JScrollPane(tabelaItens);
        rolagem.setBorder(new EmptyBorder(0, 0, 0, 0));
        rolagem.getViewport().setBackground(Tema.SUPERFICIE);

        JLabel tituloCupom = new JLabel("Itens da venda");
        tituloCupom.setFont(Tema.fonte(Font.BOLD, 15f));
        tituloCupom.setForeground(Tema.TEXTO_SECUNDARIO);

        JPanel lista = new JPanel(new BorderLayout(0, 8));
        lista.setOpaque(false);
        lista.add(tituloCupom, BorderLayout.NORTH);
        lista.add(rolagem, BorderLayout.CENTER);

        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, Tema.ESPACO));
        cartao.add(entrada, BorderLayout.NORTH);
        cartao.add(lista, BorderLayout.CENTER);
        return cartao;
    }

    private void configurarTabela() {
        tabelaItens.setFont(Tema.fonte(Font.PLAIN, 14f));
        tabelaItens.getTableHeader().setFont(Tema.fonte(Font.BOLD, 13f));
        tabelaItens.getTableHeader().setReorderingAllowed(false);
        tabelaItens.setFillsViewportHeight(true);
        tabelaItens.setFocusable(false);

        // Números alinhados à direita, como num cupom fiscal
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int coluna = 1; coluna <= 3; coluna++) {
            tabelaItens.getColumnModel().getColumn(coluna).setCellRenderer(direita);
        }
        tabelaItens.getColumnModel().getColumn(0).setPreferredWidth(320);
        tabelaItens.getColumnModel().getColumn(1).setPreferredWidth(60);
    }

    // ---------- Lateral: total, pagamento e troco ----------
    private JComponent criarLateral() {
        JLabel rotuloTotal = new JLabel("TOTAL");
        rotuloTotal.setFont(Tema.fonte(Font.BOLD, 13f));
        rotuloTotal.setForeground(Tema.TEXTO_SOBRE_ESCURO);

        lblTotal.setFont(Tema.fonte(Font.BOLD, 40f));
        lblTotal.setForeground(Tema.TEXTO_SOBRE_ESCURO);

        JPanel cartaoTotal = new JPanel(new BorderLayout(0, 4));
        cartaoTotal.setBackground(Tema.PAINEL_TOTAL);
        cartaoTotal.putClientProperty(FlatClientProperties.STYLE, "arc: " + Tema.RAIO);
        cartaoTotal.setBorder(new EmptyBorder(20, 20, 20, 20));
        cartaoTotal.add(rotuloTotal, BorderLayout.NORTH);
        cartaoTotal.add(lblTotal, BorderLayout.CENTER);

        JLabel rotuloPagamento = new JLabel("Pagamento em dinheiro");
        rotuloPagamento.setFont(Tema.fonte(Font.BOLD, 15f));
        rotuloPagamento.setForeground(Tema.TEXTO_SECUNDARIO);

        txtValorRecebido.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Valor recebido (ex.: 100,00)");
        txtValorRecebido.setFont(Tema.fonte(Font.PLAIN, 16f));

        lblTroco.setFont(Tema.fonte(Font.BOLD, 20f));
        lblTroco.setForeground(Tema.SUCESSO);

        estilizarBotaoPrimario(btnFinalizar);
        estilizarBotaoPrimario(btnPagar);
        btnNovaVenda.setMargin(new Insets(10, 18, 10, 18));

        JPanel lateral = criarCartao();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setPreferredSize(new Dimension(320, 0));

        adicionarNaLateral(lateral, cartaoTotal, 0);
        adicionarNaLateral(lateral, btnFinalizar, 12);
        adicionarNaLateral(lateral, rotuloPagamento, 28);
        adicionarNaLateral(lateral, txtValorRecebido, 8);
        adicionarNaLateral(lateral, btnPagar, 8);
        adicionarNaLateral(lateral, lblTroco, 16);
        lateral.add(Box.createVerticalGlue()); // empurra o próximo para o fim
        adicionarNaLateral(lateral, btnNovaVenda, 0);
        return lateral;
    }

    private void adicionarNaLateral(JPanel lateral, JComponent componente, int espacoAntes) {
        if (espacoAntes > 0) {
            lateral.add(Box.createVerticalStrut(espacoAntes));
        }
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, componente.getPreferredSize().height));
        lateral.add(componente);
    }

    // ---------- Rodapé: mensagens para o operador ----------
    private JComponent criarBarraStatus() {
        lblMensagem.setFont(Tema.fonte(Font.PLAIN, 14f));
        lblMensagem.setForeground(Tema.TEXTO_SECUNDARIO);

        JPanel barra = criarCartao();
        barra.setLayout(new BorderLayout());
        barra.setBorder(new EmptyBorder(10, Tema.ESPACO, 10, Tema.ESPACO));
        barra.add(lblMensagem, BorderLayout.CENTER);
        return barra;
    }

    // ---------- Utilitários visuais ----------
    private JPanel criarCartao() {
        JPanel cartao = new JPanel();
        cartao.setBackground(Tema.SUPERFICIE);
        cartao.putClientProperty(FlatClientProperties.STYLE, "arc: " + Tema.RAIO);
        cartao.setBorder(new EmptyBorder(Tema.ESPACO, Tema.ESPACO, Tema.ESPACO, Tema.ESPACO));
        return cartao;
    }

    private void estilizarBotaoPrimario(JButton botao) {
        botao.putClientProperty(FlatClientProperties.STYLE,
                "background: " + hex(Tema.DESTAQUE) + ";"
                + "foreground: #FFFFFF;"
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "font: bold;"
                + "disabledBackground: #E2E8F0;"
                + "disabledText: #94A3B8");
        botao.setMargin(new Insets(10, 18, 10, 18));
    }

    private static String hex(Color cor) {
        return String.format("#%06X", cor.getRGB() & 0xFFFFFF);
    }

    private void mostrarStatus(String texto, Color cor) {
        lblStatusVenda.setText(texto);
        lblStatusVenda.setForeground(cor);
    }

    // =====================================================================
    // PASSO 10: comportamento. A tela só conversa com o controlador.
    // =====================================================================

    private void configurarAcoes() {
        btnAdicionar.addActionListener(e -> adicionarItem());
        txtCodigo.addActionListener(e -> adicionarItem());            // Enter no código
        btnFinalizar.addActionListener(e -> finalizarVenda());
        btnPagar.addActionListener(e -> receberPagamento());
        txtValorRecebido.addActionListener(e -> receberPagamento());  // Enter no valor
        btnNovaVenda.addActionListener(e -> novaVenda());

        registrarAtalho("F1", btnNovaVenda);
        registrarAtalho("F2", btnFinalizar);
        registrarAtalho("F4", btnPagar);
    }

    private void registrarAtalho(String tecla, JButton botao) {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(tecla), tecla);
        getRootPane().getActionMap().put(tecla, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botao.doClick(); // se o botão estiver desabilitado, nada acontece
            }
        });
    }

    // ---------- Eventos do sistema: cada um chama UM método do controlador ----------

    private void novaVenda() {
        executar(() -> {
            controlador.iniciarNovaVenda();
            txtValorRecebido.setText("");
            mostrarSucesso("Nova venda iniciada. Digite o código do produto e tecle Enter.");
        });
    }

    private void adicionarItem() {
        executar(() -> {
            String codigo = txtCodigo.getText().trim();
            int quantidade = (Integer) spnQuantidade.getValue();
            ItemVenda item = controlador.adicionarItem(codigo, quantidade);
            txtCodigo.setText("");
            spnQuantidade.setValue(1);
            mostrarSucesso("Adicionado: " + quantidade + "x " + item.getProduto().getDescricao());
        });
    }

    private void finalizarVenda() {
        executar(() -> {
            BigDecimal total = controlador.finalizarVenda();
            mostrarSucesso("Venda finalizada. Total a receber: " + MOEDA.format(total));
        });
    }

    private void receberPagamento() {
        executar(() -> {
            BigDecimal valor = lerValor(txtValorRecebido.getText());
            BigDecimal troco = controlador.realizarPagamento(valor);
            lblTroco.setText("Troco: " + MOEDA.format(troco));
            mostrarSucesso("Pagamento recebido. Troco de " + MOEDA.format(troco) + ". F1 inicia nova venda.");
        });
    }

    // ---------- Infraestrutura da tela ----------

    /** Executa uma ação, mostra o erro (se houver) e sempre atualiza a tela. */
    private void executar(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarErro(ex.getMessage());
            Toolkit.getDefaultToolkit().beep();
        }
        atualizarTela();
    }

    /** Converte o texto digitado ("100,50") em BigDecimal. Isso é tarefa da tela, não do domínio. */
    private BigDecimal lerValor(String texto) {
        String normalizado = texto.trim().replace(",", ".");
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("Informe o valor recebido.");
        }
        try {
            return new BigDecimal(normalizado);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido. Use o formato 100,00");
        }
    }

    /** Redesenha a tela a partir do estado atual da venda. */
    private void atualizarTela() {
        Venda venda = controlador.getVendaAtual();
        boolean semVenda   = venda == null;
        boolean aberta     = !semVenda && !venda.isFinalizada();
        boolean aguardando = !semVenda && venda.isFinalizada() && !venda.isPaga();
        boolean paga       = !semVenda && venda.isPaga();

        // Cupom
        modeloItens.setRowCount(0);
        if (!semVenda) {
            for (ItemVenda item : venda.getItens()) {
                modeloItens.addRow(new Object[]{
                    item.getProduto().getDescricao(),
                    item.getQuantidade(),
                    MOEDA.format(item.getProduto().getPreco()),
                    MOEDA.format(item.calcularSubtotal())
                });
            }
        }

        // Total e troco
        lblTotal.setText(MOEDA.format(semVenda ? BigDecimal.ZERO : controlador.getTotalAtual()));
        if (!paga) {
            lblTroco.setText("Troco: " + MOEDA.format(0));
        }

        // Selo de status
        if (semVenda) {
            mostrarStatus("● Nenhuma venda", Tema.TEXTO_SECUNDARIO);
        } else if (aberta) {
            mostrarStatus("● Venda aberta", Tema.DESTAQUE);
        } else if (aguardando) {
            mostrarStatus("● Aguardando pagamento", Tema.AVISO);
        } else {
            mostrarStatus("● Venda paga", Tema.SUCESSO);
        }

        // Só o que faz sentido no estado atual fica habilitado
        txtCodigo.setEnabled(aberta);
        spnQuantidade.setEnabled(aberta);
        btnAdicionar.setEnabled(aberta);
        btnFinalizar.setEnabled(aberta);
        txtValorRecebido.setEnabled(aguardando);
        btnPagar.setEnabled(aguardando);
        btnNovaVenda.setEnabled(semVenda || paga);

        // Foco no próximo passo natural do operador
        if (aberta) {
            txtCodigo.requestFocusInWindow();
        } else if (aguardando) {
            txtValorRecebido.requestFocusInWindow();
        } else {
            btnNovaVenda.requestFocusInWindow();
        }
    }

    private void mostrarSucesso(String texto) {
        lblMensagem.setText(texto);
        lblMensagem.setForeground(Tema.SUCESSO);
    }

    private void mostrarErro(String texto) {
        lblMensagem.setText(texto);
        lblMensagem.setForeground(Tema.ERRO);
    }
}