package DB;

import Library.DomainClasses.BoardGame;
import Library.DomainClasses.Book;
import Library.DomainClasses.DVD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class db {

    private static final String URL      = "jdbc:mysql://localhost:3306/LibraryManagementSystem?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER     = "root";
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

    // ─────────────────────────────────────────────────────────────────────
    //  BOOK
    // ─────────────────────────────────────────────────────────────────────
    public boolean addBook(Book b) {
        String sql = "INSERT INTO Book (title, type, genre, deweyDecimal, canBorrow) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString (1, b.getTitle());
            stmt.setString (2, b.getType());
            stmt.setString (3, b.getGenre());
            stmt.setString (4, b.getDeweyDecimal());
            stmt.setBoolean(5, b.canBorrow());      // FIX: was getBorrowable(), method is canBorrow()
            stmt.executeUpdate();
            System.out.println("Book saved: " + b.getTitle());
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
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add("ID: "          + rs.getInt    ("id")
                        + " | Title: "    + rs.getString ("title")
                        + " | Type: "     + rs.getString ("type")
                        + " | Genre: "    + rs.getString ("genre")
                        + " | DDC: "      + rs.getString ("deweyDecimal")
                        + " | Borrow: "   + rs.getBoolean("canBorrow"));
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
                results.add("ID: "       + rs.getInt   ("id")
                        + " | Title: "   + rs.getString("title")
                        + " | Type: "    + rs.getString("type")
                        + " | Genre: "   + rs.getString("genre")
                        + " | DDC: "     + rs.getString("deweyDecimal"));
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
            stmt.setInt   (5, id);
            stmt.executeUpdate();
            System.out.println("Book updated: id=" + id);
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
            System.out.println("Book deleted: id=" + id);
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  DVD
    // ─────────────────────────────────────────────────────────────────────
    public boolean addDvd(DVD d) {
        String sql = "INSERT INTO DVD (title, type, genre, canBorrow) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString (1, d.getTitle());
            stmt.setString (2, d.getType());
            stmt.setString (3, d.getGenre());
            stmt.setBoolean(4, d.canBorrow());
            stmt.executeUpdate();
            System.out.println("DVD saved: " + d.getTitle());
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding DVD: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllDvds() {
        List<String> dvds = new ArrayList<>();
        String sql = "SELECT * FROM DVD";
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dvds.add("ID: "         + rs.getInt    ("id")
                       + " | Title: "   + rs.getString ("title")
                       + " | Type: "    + rs.getString ("type")
                       + " | Genre: "   + rs.getString ("genre")
                       + " | Borrow: "  + rs.getBoolean("canBorrow"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching DVDs: " + e.getMessage());
        }
        return dvds;
    }

    public boolean deleteDvd(int id) {
        String sql = "DELETE FROM DVD WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("DVD deleted: id=" + id);
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting DVD: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  BOARD GAME
    // ─────────────────────────────────────────────────────────────────────
    public boolean addBoardGame(BoardGame g) {
        String sql = "INSERT INTO BoardGame (title, type, canBorrow) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString (1, g.getTitle());
            stmt.setString (2, g.getType());
            stmt.setBoolean(3, g.canBorrow());
            stmt.executeUpdate();
            System.out.println("Board game saved: " + g.getTitle());
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding board game: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllBoardGames() {
        List<String> games = new ArrayList<>();
        String sql = "SELECT * FROM BoardGame";
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                games.add("ID: "        + rs.getInt    ("id")
                        + " | Title: "  + rs.getString ("title")
                        + " | Type: "   + rs.getString ("type")
                        + " | Borrow: " + rs.getBoolean("canBorrow"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching board games: " + e.getMessage());
        }
        return games;
    }

    public boolean deleteBoardGame(int id) {
        String sql = "DELETE FROM BoardGame WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Board game deleted: id=" + id);
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting board game: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  CONNECTION
    // ─────────────────────────────────────────────────────────────────────
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