package Main; 
import Library.*;
import java.util.Scanner;

public class samplecode {
public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("1. Book\n2. DVD\n3. Board Game");
    System.out.print("Enter choice: ");

    int choice = scanner.nextInt();
    scanner.nextLine(); // Clear the buffer!

    switch(choice) {
        case 1 -> {
            System.out.println("--- Selected Book ---");
            System.out.print("Enter title: ");
            String title = scanner.nextLine();

            System.out.print("Enter type: ");
            String type = scanner.nextLine();

            System.out.print("Enter genre: ");
            String genre = scanner.nextLine();

            System.out.print("Dewey Decimal: ");
            String deweyDecimal = scanner.nextLine();

            book book1 = new book(title, type, genre, deweyDecimal);
            System.out.println("\nSuccessfully added: " + book1.getTitle());
        }
    }
    scanner.close(); 
    }  
} 