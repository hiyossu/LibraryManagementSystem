package Library.DomainClasses;
 
public interface isBorrowable {
    boolean canBorrow();
    double calculateLateFee(long daysOverdue);
 
}
 