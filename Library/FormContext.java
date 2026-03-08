package Library;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Shared state holder passed to every form card and action handler.
 * Single source of truth for the active form's fields and the status bar.
 */
public class FormContext {

    public JTextField txtTitle;
    public JTextField txtType;
    public JTextField txtGenre;
    public JTextField txtDewey;

    public JLabel lblStatus;

    public final Color successColor;
    public final Color warnColor;
    public final Color errColor;
    public final Color grayText;

    public FormContext(JLabel lblStatus,
                       Color successColor, Color warnColor,
                       Color errColor,     Color grayText) {
        this.lblStatus    = lblStatus;
        this.successColor = successColor;
        this.warnColor    = warnColor;
        this.errColor     = errColor;
        this.grayText     = grayText;
    }

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