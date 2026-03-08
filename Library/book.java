package Library;
import java.time.LocalDate;

public class Book extends LibraryItem implements isBorrowable{
    private final String isbn;
    private final String author;
    private final int pages;
    private final LocalDate yearPublished;
    private final String genre; 
    private final String deweyDecimal;

    protected boolean canBorrow; 

    public Book(String title, String type, String genre, String deweyDecimal, String isbn, String author, int pages, LocalDate yearPublished){
        super(title, type);
        this.genre = genre; 
        this.deweyDecimal = deweyDecimal;
        this.isbn = isbn;
        this.author = author;
        this.pages = pages;
        this.yearPublished = yearPublished;
        this.canBorrow = true;
    }

public String getGenre() { return genre; }
    public String getDeweyDecimal() { return deweyDecimal; }
    public String getIsbn() { return isbn; }
    public String getAuthor() { return author; }
    public int getPages() { return pages; }
    public LocalDate getYearPublished() { return yearPublished; }

    @Override 
    public boolean canBorrow() { return this.canBorrow; }

    @Override 
    public double calculateLateFee() {
        return 10.0; 
    }
    

}

