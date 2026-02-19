package Library;

public class Book extends LibraryItem implements isBorrowable{
    public String genre; 
    protected boolean canBorrow; 

    public Book(String title, String type, String genre, boolean canBorrow){
        super(title, type);
        this.genre = genre; 
        this.canBorrow = canBorrow; 

    }

    @Override 
    public double calculateLateFee(){
        return 0.5 * 30; 
    }

    @Override 
    public boolean canBorrow() {
        return true; 
    }
}
