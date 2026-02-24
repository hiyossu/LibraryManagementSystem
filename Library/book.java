package Library;

public class book extends LibraryItem implements isBorrowable{
    public String genre; 
    public String deweyDecimal;
    protected boolean canBorrow; 

    public book(String title, String type, String genre, String deweyDecimal){
        super(title, type);
        this.genre = genre; 
        this.deweyDecimal = deweyDecimal;

    }

    @Override 
    public double calculateLateFee(){
        return 0.5 * 30; // change this formula
    }

    @Override 
    public boolean canBorrow() {
        return true; 
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
        return this.canBorrow; 
    }

    public String getType() {
        return this.type; 
}
    
}

