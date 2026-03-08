package Library.Actions;

import DB.db;
import Library.DomainClasses.Book;
import Library.FormContext;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BookActions {

    private final FormContext ctx;
    private final JFrame      owner;

    public BookActions(FormContext ctx, JFrame owner) {
        this.ctx = ctx; this.owner = owner;
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
        Book newBook = new Book(title, type, genre, dewey);
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