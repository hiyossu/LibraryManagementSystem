package Library.DomainClasses;

public abstract class ReferenceBook extends LibraryItem implements isBorrowable {

    private String    genre;
    private String    deweyDecimal;
    protected boolean canBorrow;

    public ReferenceBook(String title, String type, String genre,
                         String deweyDecimal, boolean canBorrow) {
        super(title, type);
        this.genre        = genre;
        this.deweyDecimal = deweyDecimal;
        this.canBorrow    = canBorrow;
    }

    @Override public boolean canBorrow()        { return canBorrow;   }
    @Override public double  calculateLateFee() { return 0.5 * 30;   }

    public String  getGenre()        { return genre;        }
    public String  getDeweyDecimal() { return deweyDecimal; }
    public boolean getBorrowable()   { return canBorrow;    }

    @Override
    public String toString() {
        return "ReferenceBook{title='" + getTitle() + "', dewey='" + deweyDecimal
             + "', canBorrow=" + canBorrow + '}';
    }
}