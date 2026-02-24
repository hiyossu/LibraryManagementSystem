package DB;

import Library.book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class db {

    private static final String URL = "jdbc:mysql://localhost:3306/LibraryManagementSystem?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "Fons113122";

    private Connection connection;

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

    public List<String> getAllBooks() {
        List<String> books = new ArrayList<>();
        String sql = "SELECT * FROM Book";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String entry = "ID: " + rs.getInt("id")
                        + " | Title: " + rs.getString("title")
                        + " | Type: " + rs.getString("type")
                        + " | Genre: " + rs.getString("genre")
                        + " | DDC: " + rs.getString("deweyDecimal")
                        + " | Can Borrow: " + rs.getBoolean("canBorrow");
                books.add(entry);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
        return books;
    }
    public List<String> searchBook(String keyword) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT * FROM Book WHERE title LIKE ? OR genre LIKE ? OR type LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);
            stmt.setString(3, kw);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String entry = "ID: " + rs.getInt("id")
                        + " | Title: " + rs.getString("title")
                        + " | Type: " + rs.getString("type")
                        + " | Genre: " + rs.getString("genre")
                        + " | DDC: " + rs.getString("deweyDecimal");
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
}