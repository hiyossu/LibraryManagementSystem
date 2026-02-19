package Library;

public class ReferenceBook extends LibraryItem implements isBorrowable{
    public String genre; 
    public String deweyDecimal;
    protected boolean canBorrow; 

    public ReferenceBook(String title, String type, String genre, String deweyDecimal, boolean canBorrow){
        super(title, type);
        this.genre = genre; 
        this.deweyDecimal = deweyDecimal;
        this.canBorrow = canBorrow; 

    }

    @Override 
    public double calculateLateFee(){
        return 0.5 * 30; // change this formula
    }

    @Override 
    public boolean canBorrow() {
        return false; 
    }

    public String getTitle(){
        return title;
    }
    public String getGenre(){
        return genre;
    }

    public String  getDeweyDecimal() {
        return deweyDecimal;
    }

    public boolean getBorrowable(){
        return canBorrow; 
    }
    
}

