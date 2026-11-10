package br.com.pdv.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Map;

public final class Tema {

    private Tema() {
        // classe utilitária: não deve ser instanciada
    }

    // ===== Paleta de cores =====
    public static final Color FUNDO              = new Color(0xF8FAFC); // cinza quase branco
    public static final Color SUPERFICIE         = Color.WHITE;         // cartões
    public static final Color BORDA              = new Color(0xE2E8F0); // divisórias suaves
    public static final Color TEXTO              = new Color(0x0F172A); // quase preto
    public static final Color TEXTO_SECUNDARIO   = new Color(0x64748B); // rótulos
    public static final Color DESTAQUE           = new Color(0x4F46E5); // índigo: ação principal
    public static final Color SUCESSO            = new Color(0x059669); // verde: troco, venda paga
    public static final Color ERRO               = new Color(0xDC2626); // vermelho: erros
    public static final Color PAINEL_TOTAL       = new Color(0x0F172A); // cartão escuro do total
    public static final Color TEXTO_SOBRE_ESCURO = new Color(0xF8FAFC);

    // ===== Medidas =====
    public static final int RAIO = 12;        // cantos arredondados
    public static final int ESPACO = 16;      // espaçamento padrão entre blocos

    // ===== Tipografia =====
    public static Font fonte(int estilo, float tamanho) {
        return UIManager.getFont("defaultFont").deriveFont(estilo, tamanho);
    }

    // ===== Aplica o tema em todo o Swing =====
    public static void aplicar() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#4F46E5"));
        FlatLightLaf.setup();

        UIManager.put("Button.arc", RAIO);
        UIManager.put("Component.arc", RAIO);
        UIManager.put("TextComponent.arc", RAIO);
        UIManager.put("Component.focusWidth", 2);

        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 10);

        UIManager.put("Table.rowHeight", 36);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("TableHeader.height", 36);

        UIManager.put("Panel.background", FUNDO);
    }
}