package Library.DomainClasses;

public abstract class DVD extends LibraryItem implements isBorrowable {

    private String    genre;
    protected boolean canBorrow;

    public DVD(String title, String type, String genre, boolean canBorrow) {
        super(title, type);
        this.genre     = genre;
        this.canBorrow = canBorrow;
    }

    @Override public boolean canBorrow()        { return canBorrow;  }
    @Override public double  calculateLateFee() { return 3.0 * 2.0; }

    public String getGenre()             { return genre; }
    public void   setGenre(String genre) { this.genre = genre; }

    @Override
    public String toString() {
        return "DVD{title='" + getTitle() + "', genre='" + genre
             + "', canBorrow=" + canBorrow + '}';
    }
}