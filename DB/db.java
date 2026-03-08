package DB;

import Library.DomainClasses.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class db {

    private static final String URL = "jdbc:mysql://localhost:3306/LibraryManagementSystem?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    // ─────────────────────────────────────────────────────────────
    //  CONNECTION
    // ─────────────────────────────────────────────────────────────
    public db() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  BOOK — add / update / delete / search / display
    //  (matches UML: Library Database methods)
    // ─────────────────────────────────────────────────────────────
    public boolean addBook(book b) {
        String sql = "INSERT INTO Book (title, type, genre, deweyDecimal, canBorrow) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, b.getTitle());
            stmt.setString(2, b.getType());
            stmt.setString(3, b.getGenre());
            stmt.setString(4, b.getDeweyDecimal());
            stmt.setBoolean(5, b.getBorrowable());
            stmt.executeUpdate();
            System.out.println("Book saved to database: " + b.getTitle());
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add any catalog item by media type.
     * canBorrow is automatically false for ReferenceBook, true for everything else.
     */
    public boolean addBookTyped(String title, String mediaType, String genre, String dewey) {
        boolean canBorrow = !mediaType.equals("ReferenceBook");
        String sql = "INSERT INTO Book (title, type, genre, deweyDecimal, canBorrow) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, mediaType);
            stmt.setString(3, genre);
            stmt.setString(4, dewey);
            stmt.setBoolean(5, canBorrow);
            stmt.executeUpdate();
            System.out.println("Item saved to database: " + title + " [" + mediaType + "]");
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding item: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllBooks() {
        List<String> books = new ArrayList<String>();
        String sql = "SELECT * FROM Book";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String entry = "ID: "         + rs.getInt("id")
                        + " | Title: "        + rs.getString("title")
                        + " | Type: "         + rs.getString("type")
                        + " | Genre: "        + rs.getString("genre")
                        + " | DDC: "          + rs.getString("deweyDecimal")
                        + " | Can Borrow: "   + rs.getBoolean("canBorrow");
                books.add(entry);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
        return books;
    }

    public List<String> searchBook(String keyword) {
        List<String> results = new ArrayList<String>();
        String sql = "SELECT * FROM Book WHERE title LIKE ? OR genre LIKE ? OR type LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            stmt.setString(1, kw); stmt.setString(2, kw); stmt.setString(3, kw);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String entry = "ID: "       + rs.getInt("id")
                        + " | Title: "      + rs.getString("title")
                        + " | Type: "       + rs.getString("type")
                        + " | Genre: "      + rs.getString("genre")
                        + " | DDC: "        + rs.getString("deweyDecimal");
                results.add(entry);
            }
        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
        return results;
    }

    public boolean updateBook(int id, String title, String type, String genre, String deweyDecimal) {
        String sql = "UPDATE Book SET title=?, type=?, genre=?, deweyDecimal=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, type);
            stmt.setString(3, genre);
            stmt.setString(4, deweyDecimal);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            System.out.println("Book updated successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int id) {
        String sql = "DELETE FROM Book WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Book deleted.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    /** UML: status() — returns the canBorrow status of a book by ID */
    public String getBookStatus(int bookId) {
        String sql = "SELECT title, canBorrow FROM Book WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("title") + " — " +
                        (rs.getBoolean("canBorrow") ? "Available" : "Not Available");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching status: " + e.getMessage());
        }
        return "Book not found.";
    }

    // ─────────────────────────────────────────────────────────────
    //  BORROWER — add / get / canBorrow / borrowCount
    //  (matches UML: Borrower — name, idNo, borrowLimit=5)
    // ─────────────────────────────────────────────────────────────
    public boolean addBorrower(String name, int idNo, String borrowerType, String school) {
        String sql = "INSERT INTO Borrower (name, idNo, borrowLimit, borrowerType, school) VALUES (?, ?, 5, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, idNo);
            stmt.setString(3, borrowerType);
            stmt.setString(4, school == null || school.isEmpty() ? null : school);
            stmt.executeUpdate();
            System.out.println("Borrower added: " + name);
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding borrower: " + e.getMessage());
            return false;
        }
    }

    public int getNextGuestId() {
        String sql = "SELECT MAX(idNo) FROM Borrower WHERE borrowerType='Guest'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next() && rs.getObject(1) != null) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            System.out.println("Error getting next guest ID: " + e.getMessage());
        }
        return 90001;
    }

    public List<String> getAllBorrowers() {
        List<String> list = new ArrayList<String>();
        String sql = "SELECT * FROM Borrower";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String school = rs.getString("school");
                list.add("ID: "           + rs.getInt("idNo")
                        + " | Name: "     + rs.getString("name")
                        + " | Limit: "    + rs.getInt("borrowLimit")
                        + " | Type: "     + rs.getString("borrowerType")
                        + " | School: "   + (school != null ? school : "Mapua University"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching borrowers: " + e.getMessage());
        }
        return list;
    }

    /** UML: canBorrow() — checks if borrower has not hit their limit */
    public boolean canBorrow(int borrowerIdNo) {
        String sql = "SELECT borrowLimit FROM Borrower WHERE idNo=?";
        String countSql = "SELECT COUNT(*) FROM BorrowingSystem WHERE borrowerIdNo=? AND dateReturned IS NULL";
        try {
            int limit = 5;
            try (PreparedStatement s = connection.prepareStatement(sql)) {
                s.setInt(1, borrowerIdNo);
                ResultSet rs = s.executeQuery();
                if (rs.next()) limit = rs.getInt("borrowLimit");
            }
            try (PreparedStatement s = connection.prepareStatement(countSql)) {
                s.setInt(1, borrowerIdNo);
                ResultSet rs = s.executeQuery();
                if (rs.next()) {
                    int activeLoans = rs.getInt(1);
                    return activeLoans < limit;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking borrow limit: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────
    //  BORROWING SYSTEM — checkout / return / fine / dates
    //  (matches UML: BorrowingSystem — checkout(), return(),
    //   calculateOverdueFine(), dateBorrowed, dateReturned, dueDate)
    // ─────────────────────────────────────────────────────────────

    /** UML: checkout() — creates a borrow record; dueDate = 14 days from today */
    public boolean checkoutBook(int bookId, int borrowerIdNo, String borrowedCondition) {
        if (!canBorrow(borrowerIdNo)) {
            System.out.println("Borrower has reached their borrow limit.");
            return false;
        }
        String sql = "INSERT INTO BorrowingSystem (bookId, borrowerIdNo, dateBorrowed, dueDate, borrowedCondition) " +
                     "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), ?)";
        String updateBook = "UPDATE Book SET canBorrow=false WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement upd  = connection.prepareStatement(updateBook)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, borrowerIdNo);
            stmt.setString(3, borrowedCondition);
            stmt.executeUpdate();
            upd.setInt(1, bookId);
            upd.executeUpdate();
            System.out.println("Book checked out. Due in 14 days.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error checking out book: " + e.getMessage());
            return false;
        }
    }

    /** UML: return() — records the return date and marks book available */
    public boolean returnBook(int borrowId) {
        String getBookId = "SELECT bookId FROM BorrowingSystem WHERE id=?";
        String sql       = "UPDATE BorrowingSystem SET dateReturned=CURDATE() WHERE id=?";
        String updateBook= "UPDATE Book SET canBorrow=true WHERE id=?";
        try {
            int bookId = -1;
            try (PreparedStatement s = connection.prepareStatement(getBookId)) {
                s.setInt(1, borrowId);
                ResultSet rs = s.executeQuery();
                if (rs.next()) bookId = rs.getInt("bookId");
            }
            try (PreparedStatement s = connection.prepareStatement(sql)) {
                s.setInt(1, borrowId);
                s.executeUpdate();
            }
            if (bookId != -1) {
                try (PreparedStatement s = connection.prepareStatement(updateBook)) {
                    s.setInt(1, bookId);
                    s.executeUpdate();
                }
            }
            System.out.println("Book returned successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    /** UML: calculateOverdueFine() — PHP 5.00 per day overdue */
    public double calculateOverdueFine(int borrowId) {
        String sql = "SELECT dueDate, dateReturned FROM BorrowingSystem WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, borrowId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                java.sql.Date dueDate      = rs.getDate("dueDate");
                java.sql.Date dateReturned = rs.getDate("dateReturned");
                java.sql.Date checkDate    = (dateReturned != null) ? dateReturned : new java.sql.Date(new Date().getTime());
                long diff = checkDate.getTime() - dueDate.getTime();
                long daysOverdue = diff / (1000 * 60 * 60 * 24);
                if (daysOverdue > 0) {
                    double fine = daysOverdue * 5.00;
                    System.out.println("Days overdue: " + daysOverdue + " | Fine: PHP " + fine);
                    return fine;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error calculating fine: " + e.getMessage());
        }
        return 0.0;
    }

    /** UML: dateBorrowed() — get the date a book was borrowed */
    public String getDateBorrowed(int borrowId) {
        String sql = "SELECT dateBorrowed FROM BorrowingSystem WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, borrowId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("dateBorrowed");
        } catch (SQLException e) {
            System.out.println("Error fetching borrow date: " + e.getMessage());
        }
        return "N/A";
    }

    /** UML: dateReturned() — get the return date of a borrow record */
    public String getDateReturned(int borrowId) {
        String sql = "SELECT dateReturned FROM BorrowingSystem WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, borrowId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String d = rs.getString("dateReturned");
                return (d != null) ? d : "Not yet returned";
            }
        } catch (SQLException e) {
            System.out.println("Error fetching return date: " + e.getMessage());
        }
        return "N/A";
    }

    /** Get all active (unreturned) loans for a borrower */
    public List<String> getActiveLoansByBorrower(int borrowerIdNo) {
        List<String> loans = new ArrayList<String>();
        String sql = "SELECT bs.id, b.title, bs.dateBorrowed, bs.dueDate " +
                     "FROM BorrowingSystem bs JOIN Book b ON bs.bookId = b.id " +
                     "WHERE bs.borrowerIdNo=? AND bs.dateReturned IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, borrowerIdNo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add("LoanID: "        + rs.getInt("id")
                        + " | Title: "      + rs.getString("title")
                        + " | Borrowed: "   + rs.getString("dateBorrowed")
                        + " | Due: "        + rs.getString("dueDate"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loans: " + e.getMessage());
        }
        return loans;
    }

    // ─────────────────────────────────────────────────────────────
    //  NOTIFICATION
    //  (matches UML: Notification — notificationID, operations())
    // ─────────────────────────────────────────────────────────────

    public boolean sendNotification(int borrowerIdNo, String message) {
        String sql = "INSERT INTO Notification (borrowerIdNo, message, sentDate) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, borrowerIdNo);
            stmt.setString(2, message);
            stmt.executeUpdate();
            System.out.println("Notification sent to borrower #" + borrowerIdNo);
            return true;
        } catch (SQLException e) {
            System.out.println("Error sending notification: " + e.getMessage());
            return false;
        }
    }

    public List<String> getNotificationsForBorrower(int borrowerIdNo) {
        List<String> notes = new ArrayList<String>();
        String sql = "SELECT id, message, sentDate FROM Notification WHERE borrowerIdNo=? ORDER BY sentDate DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, borrowerIdNo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add("[" + rs.getString("sentDate") + "] " + rs.getString("message"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
        }
        return notes;
    }

    /** Auto-notify all borrowers with overdue books (call this on app startup or daily) */
    public void notifyOverdueBorrowers() {
        String sql = "SELECT bs.id, bs.borrowerIdNo, b.title, bs.dueDate " +
                     "FROM BorrowingSystem bs JOIN Book b ON bs.bookId = b.id " +
                     "WHERE bs.dateReturned IS NULL AND bs.dueDate < CURDATE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int    borrowerIdNo = rs.getInt("borrowerIdNo");
                String title        = rs.getString("title");
                String dueDate      = rs.getString("dueDate");
                String msg = "OVERDUE: \"" + title + "\" was due on " + dueDate +
                             ". Fine: PHP " + calculateOverdueFine(rs.getInt("id"));
                sendNotification(borrowerIdNo, msg);
            }
        } catch (SQLException e) {
            System.out.println("Error scanning overdue books: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  DATA VALIDATION (UML: validateInput, checkConstraints, validateDDC)
    // ─────────────────────────────────────────────────────────────

    /** UML: validateDDC() — checks that a Dewey Decimal string is properly formatted */
    public boolean validateDDC(String ddc) {
        if (ddc == null || ddc.trim().isEmpty()) return false;
        return ddc.matches("\\d{3}(\\.\\d+)?");
    }

    /** UML: validateInput() — checks that required book fields are non-empty */
    public List<String> validateBookInput(String title, String type, String genre, String dewey) {
        List<String> errors = new ArrayList<String>();
        if (title  == null || title.trim().isEmpty())  errors.add("Title is required.");
        if (type   == null || type.trim().isEmpty())   errors.add("Media type is required.");
        if (genre  == null || genre.trim().isEmpty())  errors.add("Genre is required.");
        if (dewey  == null || dewey.trim().isEmpty())  errors.add("Dewey Decimal is required.");
        else if (!validateDDC(dewey))                  errors.add("Invalid Dewey Decimal format (e.g. 813.54).");
        return errors;
    }

    /** UML: checkConstraints() — verifies a book ID exists in the DB */
    public boolean bookExists(int bookId) {
        String sql = "SELECT id FROM Book WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking book existence: " + e.getMessage());
        }
        return false;
    }

    /** UML: checkConstraints() — verifies a borrower ID exists in the DB */
    public boolean borrowerExists(int idNo) {
        String sql = "SELECT idNo FROM Borrower WHERE idNo=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idNo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking borrower existence: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────
    //  EXCEPTION HANDLER (UML: errorCode, errorMessage, timeGenerated)
    // ─────────────────────────────────────────────────────────────

    /** Logs an application error to the ExceptionLog table */
    public void logException(int errorCode, String errorMessage) {
        String sql = "INSERT INTO ExceptionLog (errorCode, errorMessage, timeGenerated) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, errorCode);
            stmt.setString(2, errorMessage);
            stmt.executeUpdate();
            System.out.println("Exception logged [" + errorCode + "]: " + errorMessage);
        } catch (SQLException e) {
            System.out.println("Error logging exception: " + e.getMessage());
        }
    }

    public List<String> getExceptionLog() {
        List<String> log = new ArrayList<String>();
        String sql = "SELECT * FROM ExceptionLog ORDER BY timeGenerated DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                log.add("[" + rs.getString("timeGenerated") + "]"
                        + " Code: "    + rs.getInt("errorCode")
                        + " | Msg: "   + rs.getString("errorMessage"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching exception log: " + e.getMessage());
        }
        return log;
    }
}