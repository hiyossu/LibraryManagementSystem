package Library.DomainClasses;
<<<<<<< HEAD
import java.time.LocalDateTime;
=======

import java.time.LocalDate;
>>>>>>> f264304e8dc198434381c9ba3b00fb3ebce89920

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

    /** Convenience constructor used by the GUI form (no ISBN/author/pages/date). */
    public Book(String title, String type, String genre, String deweyDecimal) {
        this(title, type, genre, deweyDecimal, "", "", 0, LocalDateTime.now());
    }

<<<<<<< HEAD
    // ── Getters ───────────────────────────────────────────────────────────
    public String    getGenre()        { return genre;         }
    public String    getDeweyDecimal() { return deweyDecimal;  }
    public String    getIsbn()         { return isbn;          }
    public String    getAuthor()       { return author;        }
    public int       getPages()        { return pages;         }
    public LocalDateTime getYearPublished(){ return yearPublished; }
=======
    @Override public boolean canBorrow()         { return canBorrow;     }
    @Override public double  calculateLateFee()  { return 10.0;          }
>>>>>>> f264304e8dc198434381c9ba3b00fb3ebce89920

    public String    getGenre()         { return genre;         }
    public String    getDeweyDecimal()  { return deweyDecimal;  }
    public String    getIsbn()          { return isbn;          }
    public String    getAuthor()        { return author;        }
    public int       getPages()         { return pages;         }
    public LocalDate getYearPublished() { return yearPublished; }

    @Override
    public String toString() {
        return "Book{title='" + getTitle() + "', author='" + author
             + "', dewey='" + deweyDecimal + "'}";
    }
}