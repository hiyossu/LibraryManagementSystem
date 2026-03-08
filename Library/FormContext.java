package Library;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;

/**
 * Shared state holder passed to every form card and action handler.
 * Keeps a single source of truth for the active form's fields and status bar,
 * so GUI.java, form cards, and action classes never need to reach into each other.
 */
public class FormContext {

    // ── Active form fields (null when not present on the current form) ──
    public JTextField txtTitle;
    public JTextField txtType;
    public JTextField txtGenre;
    public JTextField txtDewey;

    // ── Status bar label (lives in the main panel, always present) ──────
    public JLabel lblStatus;

    // ── Status colours (set once by GUI, read by action handlers) ────────
    public Color successColor;
    public Color warnColor;
    public Color errColor;
    public Color grayText;

    public FormContext(JLabel lblStatus,
                       Color successColor, Color warnColor,
                       Color errColor,     Color grayText) {
        this.lblStatus    = lblStatus;
        this.successColor = successColor;
        this.warnColor    = warnColor;
        this.errColor     = errColor;
        this.grayText     = grayText;
    }

    /** Clears every field that is currently present on the active form. */
    public void clearFields() {
        if (txtTitle != null) txtTitle.setText("");
        if (txtType  != null) txtType .setText("");
        if (txtGenre != null) txtGenre.setText("");
        if (txtDewey != null) txtDewey.setText("");
        if (lblStatus != null) {
            lblStatus.setForeground(grayText);
            lblStatus.setText("Fields cleared.");
        }
    }
}