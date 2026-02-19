package Library; 

abstract class book {Í
    protected String title;
    protected String isbn;
    protected String author;
    protected String year_published;
    protected String deweyDecimal;
    protected int pages; 

    public book(String title, String isbn, String author, String year_published, String deweyDecimal, int pages){
        this.title = title;
        this.isbn = isbn; 
        this.author = author;
        this.deweyDecimal = deweyDecimal;
        this.pages = pages;
    }

}
