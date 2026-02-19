package Main; 
import Library.*;
import java.util.Scanner;

public class samplecode {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Enter your type of book to be inputted in the database: ");
        System.out.println("1. Book");
        System.out.println("2. DVD");
        System.out.println("3. Board Game");
    
        loop: while(true) {
            int choice = scanner.nextInt();
            switch(choice) {
                case 1 -> {
                    System.out.println("Selected book:");
                    System.out.println("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.println("Enter type: ");
                    String type = scanner.nextLine();
                    System.out.println("Enter genre: ");
                    String genre = scanner.nextLine();
                    System.out.println("Dewey Decimal System: ");
                    String deweyDecimal = scanner.nextLine();

                    Book book1 = new Book(title, type, genre, deweyDecimal);
                }
            }
        }
        

    }
}