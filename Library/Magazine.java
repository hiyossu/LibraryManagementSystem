package Library;

public class Magazine extends LibraryItem implements isBorrowable{
    String genre;
    protected boolean canBorrow; 

    public Magazine (String title, String type, boolean canBorrow){
        super(title, type);
        this.genre = genre;
        this.canBorrow = canBorrow;
    }

    @Override
    public double calculateLateFee(){
        return 3.0 * 5.0;  // change this formula
    }

    @Override
    public boolean canBorrow(){
        return true; 
    }
}