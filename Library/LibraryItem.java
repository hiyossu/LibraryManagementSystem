package Library; 

abstract class LibraryItem {
    protected String title;
    protected String type;

    public LibraryItem(String title, String type){
        this.title = title;
        this.type = type;

    }

    abstract double calculateLateFee();
}