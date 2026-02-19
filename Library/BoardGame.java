package Library; 

public class BoardGame extends LibraryItem implements isBorrowable{
    String genre;
    protected boolean canBorrow; 

    public BoardGame(String title, String type, boolean canBorrow){
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

    public String getGenre(){
        return genre;
    }
}