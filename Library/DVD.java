package Library;

public class DVD extends LibraryItem implements isBorrowable{
    public String genre; 
    protected boolean canBorrow;

    public DVD(String title, String type, boolean canBorrow){
        super(title, type);
        this.genre = genre;
        this.canBorrow = canBorrow;

    }
        
    @Override
    public double calculateLateFee(){
        return 3.0 * 2.0; // change this formula
    }

    @Override
    public boolean canBorrow(){
        return true; 
    }

    public String getGenre(){
        return genre;
    }
}

