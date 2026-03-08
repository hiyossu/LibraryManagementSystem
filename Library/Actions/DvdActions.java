package Library.Actions;

import DB.db;
import Library.DomainClasses.DVD;
import Library.FormContext;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Handles the "Add to Catalog" action for DVDs.
 */
public class DvdActions {

    private final FormContext ctx;
    private final JFrame      owner;

    public DvdActions(FormContext ctx, JFrame owner) {
        this.ctx   = ctx;
        this.owner = owner;
    }

    public void addRecord() {
        String title = ctx.txtTitle.getText().trim();
        String type  = ctx.txtType .getText().trim();
        String genre = ctx.txtGenre.getText().trim();

        if (title.isEmpty() || type.isEmpty() || genre.isEmpty()) {
            ctx.lblStatus.setForeground(ctx.warnColor);
            ctx.lblStatus.setText("Please fill in all fields.");
            JOptionPane.showMessageDialog(owner, "Please complete all fields.",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        db database = new db();
        DVD newDvd = new DVD(title, type, genre, true);
        boolean success = database.addDvd(newDvd);

        if (success) {
            ctx.lblStatus.setForeground(ctx.successColor);
            ctx.lblStatus.setText("Added DVD: \"" + title + "\" successfully.");
            ctx.clearFields();
        } else {
            ctx.lblStatus.setForeground(ctx.errColor);
            ctx.lblStatus.setText("Database error. Check console.");
        }
        database.closeConnection();
    }
}
