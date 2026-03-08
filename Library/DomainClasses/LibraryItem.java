package Library.DomainClasses;

public abstract class LibraryItem {

    protected String title;
    protected String type;

    public LibraryItem(String title, String type) {
        this.title = title;
        this.type  = type;
    }

    public String getTitle() { return title; }
    public String getType()  { return type;  }

    public abstract double calculateLateFee();
}