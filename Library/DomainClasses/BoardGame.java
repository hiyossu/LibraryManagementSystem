package Library.DomainClasses;

public abstract class BoardGame extends LibraryItem implements isBorrowable {

    protected boolean canBorrow;

    public BoardGame(String title, String type, boolean canBorrow) {
        super(title, type);
        this.canBorrow = canBorrow;
    }

    @Override public boolean canBorrow()        { return canBorrow;  }
    @Override public double  calculateLateFee() { return 3.0 * 5.0; }

    public void setCanBorrow(boolean canBorrow) { this.canBorrow = canBorrow; }

    @Override
    public String toString() {
        return "BoardGame{title='" + getTitle() + "', type='" + getType()
             + "', canBorrow=" + canBorrow + '}';
    }
}