package Library;

public abstract class LibraryItem {

    protected String title;
    protected String type;

    public LibraryItem(String title, String type) {
        this.title = title;
        this.type  = type;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String getTitle() { 
        return title;
     }
    public String getType()  { 
        return type;  
    }

    // ── Abstract ──────────────────────────────────────────────────────────
    public abstract double calculateLateFee();
}