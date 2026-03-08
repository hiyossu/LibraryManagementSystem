package Library.DomainClasses;
import java.time.LocalDateTime;

public class Book extends LibraryItem implements isBorrowable {

    private final String    genre;
    private final String    deweyDecimal;
    private final String    isbn;
    private final String    author;
    private final int       pages;
    private final LocalDateTime yearPublished;
    protected boolean       canBorrow;

    public Book(String title, String type, String genre, String deweyDecimal,
                String isbn, String author, int pages, LocalDateTime yearPublished) {
        super(title, type);
        this.genre         = genre;
        this.deweyDecimal  = deweyDecimal;
        this.isbn          = isbn;
        this.author        = author;
        this.pages         = pages;
        this.yearPublished = yearPublished;
        this.canBorrow     = true;
    }

    public Book(String title, String type, String genre, String deweyDecimal) {
        this(title, type, genre, deweyDecimal, "", "", 0, LocalDateTime.now());
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String    getGenre()        { return genre;         }
    public String    getDeweyDecimal() { return deweyDecimal;  }
    public String    getIsbn()         { return isbn;          }
    public String    getAuthor()       { return author;        }
    public int       getPages()        { return pages;         }
    public LocalDateTime getYearPublished(){ return yearPublished; }

    // ── isBorrowable ──────────────────────────────────────────────────────
    @Override
    public boolean canBorrow() { return canBorrow; }

    // ── LibraryItem ───────────────────────────────────────────────────────
    @Override
    public double calculateLateFee() { return 10.0; }

    @Override
    public String toString() {
        return "book{title='" + getTitle() + "', author='" + author
             + "', dewey='" + deweyDecimal + "'}";
    }
}

