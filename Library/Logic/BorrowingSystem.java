package Library.Logic;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowingSystem {
    private LocalDate dateBorrowed;
    private LocalDate dateReturned;
    private LocalDate dueDate;
    private String borrowedCondition;
    private double fineAmount = 0.0;

    private DataValidation validator;
    private List<ExceptionHandler> exceptionHandlers =  new ArrayList<>();

    public BorrowingSystem(LocalDate dateBorrowed, LocalDate dateReturned, LocalDate dueDate, String borrowedCondition){
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
        this.dueDate = dateBorrowed.plusWeeks(1);
        this.borrowedCondition = borrowedCondition;
        this.fineAmount = calculateOverdueFine();

        this.validator = new DataValidation();
    }

    private void showErrors(){
        for(ExceptionHandler eh : exceptionHandlers){
            eh.logError();
        }
    }

    public void checkout(String isbn, String title, String author, String ddsCode, int pages, LocalDate yearPublished, String type, String genre){
        exceptionHandlers.clear();
        boolean isValid = validator.checkConstraints(isbn, title, author, ddsCode, pages, yearPublished, type, genre);
        if(!isValid){
            for(String errorMsg : validator.getErrors()){
                exceptionHandlers.add(new ExceptionHandler(1000, errorMsg));
            }

            showErrors();}
            else{
                System.out.println("Checkout succesful!");
                System.out.println("Checkout date: " + dateBorrowed);
                System.out.println("Due date: " + dueDate);
            }
    }

    public void returnItem(){
        System.out.println("Item returned on: " + dateReturned);
        System.out.println("Condition upon return: " + borrowedCondition);
        System.out.println("Fine amount: Php " + fineAmount);
    }

    public double calculateOverdueFine(){
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, dateReturned);
        if (daysOverdue > 0) {
            return daysOverdue * 10; 
        }
        return 0;
    }

    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDate lastWeek = today.minusWeeks(1);
        LocalDate yesterday = today.minusDays(1);

        BorrowingSystem session = new BorrowingSystem(lastWeek, today, yesterday, "Good");
        
        session.checkout("1234567890", "Java Logic", "Author Name", "005.1", 250, LocalDate.of(2025, 1, 1), "Book", "Programming");
        System.out.println("---");
        session.returnItem();
    }
}