package Library;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowingSystem {
    private LocalDate dateBorrowed;
    private LocalDate dateReturned;
    private LocalDate dueDate;
    private String borrowedCondition;
    private double fineAmount = 0.0;

    public BorrowingSystem(LocalDate dateBorrowed, LocalDate dateReturned, LocalDate dueDate, String borrowedCondition){
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
        this.dueDate = dateBorrowed.plusWeeks(1);
        this.borrowedCondition = borrowedCondition;
        this.fineAmount = calculateOverdueFine();
    }

    public void checkout(){
        System.out.println("Item checked out on: " + dateBorrowed);
        System.out.println("Due date: " + dueDate);
    }

    public void returnItem(){
        System.out.println("Item returned on: " + dateReturned);
        System.out.println("Condition upon return: " + borrowedCondition);
        System.out.println("Fine amount: Php" + fineAmount);
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
        
        session.checkout();
        System.out.println("---");
        session.returnItem();
    }
}