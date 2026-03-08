package Library;


public class BoardGame extends LibraryItem implements isBorrowable {

    protected boolean canBorrow;

    public BoardGame(String title, String type, boolean canBorrow) {
        super(title, type);
        this.canBorrow = canBorrow;
    }

    // ── isBorrowable ──────────────────────────────────────────────────────
    @Override
    public boolean canBorrow() { return canBorrow; }

    // ── LibraryItem ───────────────────────────────────────────────────────
    @Override
    public double calculateLateFee() { return 3.0 * 5.0; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setCanBorrow(boolean canBorrow) { this.canBorrow = canBorrow; }

    @Override
    public String toString() {
        return "BoardGame{title='" + getTitle() + "', type='" + getType()
             + "', canBorrow=" + canBorrow + '}';
    }
}