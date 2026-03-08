package Library.Forms;

import Library.FormContext;
import Library.UI.UIFactory;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Builds and returns the DVD Registration form card.
 * No Dewey Decimal field — ctx.txtDewey is set to null.
 */
public class DvdFormCard {

    private final FormContext ctx;
    private final UIFactory   ui;
    private final Color       gold;
    private final Color       red;
    private final Color       cardColor;
    private final Color       divColor;
    private final Color       grayText;
    private final Color       textColor;

    public DvdFormCard(FormContext ctx, UIFactory ui,
                       Color gold, Color red, Color cardColor,
                       Color divColor, Color grayText, Color textColor) {
        this.ctx       = ctx;
        this.ui        = ui;
        this.gold      = gold;
        this.red       = red;
        this.cardColor = cardColor;
        this.divColor  = divColor;
        this.grayText  = grayText;
        this.textColor = textColor;
    }

    public JPanel build(Runnable onAdd, Runnable onClear, Runnable onDash) {
        JPanel card = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(red);          // Red stripe distinguishes DVD from Book
                g.fillRect(0, 0, getWidth(), 3);
            }
        };
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(560, 410));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;

        // ── Header ────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cardColor);
        header.setBorder(new EmptyBorder(20, 28, 12, 28));
        JLabel title = new JLabel("DVD Registration");
        title.setFont(new Font("Georgia", Font.BOLD, 21));
        title.setForeground(textColor);
        JLabel subtitle = new JLabel("Enter details for a new DVD catalog entry.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(grayText);
        header.add(title,    BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        c.gridy = 0; c.weighty = 0;
        card.add(header, c);

        // ── Red / Gold divider strip (reversed to signal DVD) ─────────────
        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 3));
        JPanel s1 = new JPanel(); s1.setBackground(red);
        JPanel s2 = new JPanel(); s2.setBackground(gold);
        strip.add(s1); strip.add(s2);
        c.gridy = 1;
        card.add(strip, c);

        // ── Form fields ───────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(cardColor);
        form.setBorder(new EmptyBorder(20, 28, 20, 28));
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill   = GridBagConstraints.HORIZONTAL;
        fc.anchor = GridBagConstraints.WEST;

        ctx.txtTitle = ui.makeField("e.g., Inception");
        ctx.txtType  = ui.makeField("e.g., Blu-ray, DVD...");
        ctx.txtGenre = ui.makeField("e.g., Action, Documentary...");
        ctx.txtDewey = null;    // DVDs don't use Dewey Decimal

        ui.addRow(form, fc, "DVD Title",  ctx.txtTitle, 0);
        ui.addRow(form, fc, "Media Type", ctx.txtType,  1);
        ui.addRow(form, fc, "Genre",      ctx.txtGenre, 2);

        fc.gridy = 3; fc.gridx = 0; fc.gridwidth = 4;
        fc.insets = new Insets(22, 0, 0, 0);
        form.add(ui.makeButtonRow(onAdd, onClear, onDash), fc);

        c.gridy = 2; c.weighty = 1;
        card.add(form, c);
        return card;
    }
}
