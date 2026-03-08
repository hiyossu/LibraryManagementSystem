package Library;

public class Magazine extends LibraryItem implements isBorrowable {

    private String  genre;
    protected boolean canBorrow;

    public Magazine(String title, String type, String genre, boolean canBorrow) {
        super(title, type);
        this.genre     = genre;     
        this.canBorrow = canBorrow;
    }

    // ── isBorrowable ──────────────────────────────────────────────────────
    @Override
    public boolean canBorrow() {
         return canBorrow; 
        }  

    // ── LibraryItem ───────────────────────────────────────────────────────
    @Override
    public double calculateLateFee() { 
        return 3.0 * 5.0; 
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public String getGenre()             { return genre; }
    public void   setGenre(String genre) { this.genre = genre; }

    @Override
    public String toString() {
        return "Magazine{title='" + getTitle() + "', genre='" + genre
             + "', canBorrow=" + canBorrow + '}';
    }
}