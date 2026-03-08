package Library.UI;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UIFactory {

    private final Color gold;
    private final Color darkGold;
    private final Color red;
    private final Color textColor;
    private final Color grayText;
    private final Color divColor;
    private final Color panelColor;
    private final Color cardColor;
    private final Color fieldBg;
    private final Color fieldBorder;

    public UIFactory(Color gold, Color darkGold, Color red, Color textColor,
                     Color grayText, Color divColor, Color panelColor,
                     Color cardColor, Color fieldBg, Color fieldBorder) {
        this.gold        = gold;
        this.darkGold    = darkGold;
        this.red         = red;
        this.textColor   = textColor;
        this.grayText    = grayText;
        this.divColor    = divColor;
        this.panelColor  = panelColor;
        this.cardColor   = cardColor;
        this.fieldBg     = fieldBg;
        this.fieldBorder = fieldBorder;
    }

    public JTextField makeField(String placeholder) {
        JTextField tf = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(grayText);
                    g2.setFont(new Font("Dialog", Font.ITALIC, 12));
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int y = ins.top + (getHeight() - ins.top - ins.bottom - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, ins.left + 4, y);
                    g2.dispose();
                }
            }
        };
        tf.setFont(new Font("Dialog", Font.PLAIN, 13));
        tf.setForeground(textColor);
        tf.setBackground(fieldBg);
        tf.setCaretColor(gold);
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldBorder, 1),
            new EmptyBorder(4, 10, 4, 10)));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(gold, 1), new EmptyBorder(4, 10, 4, 10)));
                tf.repaint();
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fieldBorder, 1), new EmptyBorder(4, 10, 4, 10)));
                tf.repaint();
            }
        });
        return tf;
    }

    public void addRow(JPanel p, GridBagConstraints fc,
                       String labelText, JTextField field, int row) {
        fc.gridy = row; fc.gridx = 0; fc.weightx = 0; fc.gridwidth = 1;
        fc.insets = new Insets(8, 0, 8, 10);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Dialog", Font.BOLD, 11));
        lbl.setForeground(grayText);
        lbl.setPreferredSize(new Dimension(95, 20));
        p.add(lbl, fc);
        fc.gridx = 1; fc.weightx = 1; fc.gridwidth = 3;
        fc.insets = new Insets(8, 0, 8, 0);
        p.add(field, fc);
    }

    public JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if      (getModel().isPressed())  g2.setColor(darkGold);
                else if (getModel().isRollover()) g2.setColor(new Color(220, 172, 32));
                else                              g2.setColor(gold);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setForeground(new Color(21, 7, 7));
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(155, 38));
        return btn;
    }

    public JButton makeOutlineButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if      (getModel().isPressed())  g2.setColor(divColor);
                else if (getModel().isRollover()) g2.setColor(panelColor);
                else                              g2.setColor(cardColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(fieldBorder);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.PLAIN, 13));
        btn.setForeground(textColor);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
        return btn;
    }

    public JPanel makeButtonRow(Runnable onAdd, Runnable onClear, Runnable onDash) {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setBackground(cardColor);
        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear");
        JButton btnDash  = makeOutlineButton("Dashboard");
        btnAdd  .addActionListener(e -> onAdd  .run());
        btnClear.addActionListener(e -> onClear.run());
        btnDash .addActionListener(e -> onDash .run());
        buttons.add(btnAdd);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(btnClear);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(btnDash);
        return buttons;
    }
}