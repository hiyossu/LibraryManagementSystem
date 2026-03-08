package Library.actions;

import DB.db;
import Library.FormContext;
import Library.book;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Handles the "Add to Catalog" action for books.
 * Reads from FormContext, validates, calls db, updates the status label.
 */
public class BookActions {

    private final FormContext ctx;
    private final JFrame      owner;

    public BookActions(FormContext ctx, JFrame owner) {
        this.ctx   = ctx;
        this.owner = owner;
    }

    public void addRecord() {
        String title = ctx.txtTitle.getText().trim();
        String type  = ctx.txtType .getText().trim();
        String genre = ctx.txtGenre.getText().trim();
        String dewey = (ctx.txtDewey != null) ? ctx.txtDewey.getText().trim() : "";

        if (title.isEmpty() || type.isEmpty() || genre.isEmpty() || dewey.isEmpty()) {
            ctx.lblStatus.setForeground(ctx.warnColor);
            ctx.lblStatus.setText("Please fill in all fields.");
            JOptionPane.showMessageDialog(owner, "Please complete all fields.",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        db database = new db();
        book newBook = new book(title, type, genre, dewey);
        boolean success = database.addBook(newBook);

        if (success) {
            ctx.lblStatus.setForeground(ctx.successColor);
            ctx.lblStatus.setText("Added: \"" + title + "\" successfully.");
            ctx.clearFields();
        } else {
            ctx.lblStatus.setForeground(ctx.errColor);
            ctx.lblStatus.setText("Database error. Check console.");
        }
        database.closeConnection();
    }
}
