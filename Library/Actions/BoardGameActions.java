package Library.Actions;

import DB.db;
import Library.DomainClasses.BoardGame;
import Library.FormContext;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BoardGameActions {

    private final FormContext ctx;
    private final JFrame      owner;

    public BoardGameActions(FormContext ctx, JFrame owner) {
        this.ctx = ctx; this.owner = owner;
    }

    public void addRecord() {
        String title = ctx.txtTitle.getText().trim();
        String type  = ctx.txtType .getText().trim();

        if (title.isEmpty() || type.isEmpty()) {
            ctx.lblStatus.setForeground(ctx.warnColor);
            ctx.lblStatus.setText("Please fill in all fields.");
            JOptionPane.showMessageDialog(owner, "Please complete all fields.",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        db database = new db();
        BoardGame newGame = new BoardGame(title, type, true);
        boolean success = database.addBoardGame(newGame);

        if (success) {
            ctx.lblStatus.setForeground(ctx.successColor);
            ctx.lblStatus.setText("Added Board Game: \"" + title + "\" successfully.");
            ctx.clearFields();
        } else {
            ctx.lblStatus.setForeground(ctx.errColor);
            ctx.lblStatus.setText("Database error. Check console.");
        }
        database.closeConnection();
    }
}