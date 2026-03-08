package Library.Logic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowingSystem {

    private final LocalDate dateBorrowed;
    private final LocalDate dateReturned;
    private final LocalDate dueDate;
    private final String    borrowedCondition;
    private final double    fineAmount;

    private final DataValidation         validator;
    private final List<ExceptionHandler> exceptionHandlers = new ArrayList<>();

    public BorrowingSystem(LocalDate dateBorrowed, LocalDate dateReturned,
                           LocalDate dueDate,       String borrowedCondition) {
        this.dateBorrowed      = dateBorrowed;
        this.dateReturned      = dateReturned;
        this.dueDate           = dateBorrowed.plusWeeks(1);
        this.borrowedCondition = borrowedCondition;
        this.fineAmount        = calculateOverdueFine();
        this.validator         = new DataValidation();
    }

    public void checkout(String isbn, String title, String author, String ddsCode,
                         int pages, LocalDate yearPublished, String type, String genre) {
        exceptionHandlers.clear();
        boolean isValid = validator.checkConstraints(isbn, title, author, ddsCode,
                                                     pages, yearPublished, type, genre);
        if (!isValid) {
            for (String msg : validator.getErrors())
                exceptionHandlers.add(new ExceptionHandler(1000, msg));
            showErrors();
        } else {
            System.out.println("Checkout successful!");
            System.out.println("Checkout date : " + dateBorrowed);
            System.out.println("Due date      : " + dueDate);
        }
    }

    public void returnItem() {
        System.out.println("Item returned on   : " + dateReturned);
        System.out.println("Condition on return: " + borrowedCondition);
        System.out.println("Fine amount        : Php " + fineAmount);
    }

    public double calculateOverdueFine() {
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, dateReturned);
        return (daysOverdue > 0) ? daysOverdue * 10.0 : 0.0;
    }

    private void showErrors() {
        for (ExceptionHandler eh : exceptionHandlers) eh.logError();
    }

    public LocalDate getDateBorrowed()      { return dateBorrowed;      }
    public LocalDate getDateReturned()      { return dateReturned;      }
    public LocalDate getDueDate()           { return dueDate;           }
    public String    getBorrowedCondition() { return borrowedCondition; }
    public double    getFineAmount()        { return fineAmount;        }

    public static void main(String[] args) {
        LocalDate today    = LocalDate.now();
        LocalDate lastWeek = today.minusWeeks(1);

        BorrowingSystem session = new BorrowingSystem(lastWeek, today, null, "Good");
        session.checkout("1234567890", "Java Logic", "Author Name",
                         "005.1", 250, LocalDate.of(2025, 1, 1), "Book", "Programming");
        System.out.println("---");
        session.returnItem();
    }
}