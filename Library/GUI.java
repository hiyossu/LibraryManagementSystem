package Library;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.awt.event.ActionEvent;
import java.awt.EventQueue;

public class GUI extends JFrame {

    // Variables from UML Class Diagram
    private String windowTitle = "Library Management System";
    private int width = 550;
    private int height = 375;
    private String themeColor = "Creme";
    private String statusMessage = "Ready...";
    private JPanel contentPane;
    
    private JTextField txtTitle;
    private JTextField txtType;
    private JTextField txtGenre;
    private JTextField txtDewey;
    private JLabel lblStatus;

    private Color colorCreme = new Color(255, 253, 208); // Hex: #FFFDD0
    private Color colorMaroon = new Color(128, 0, 0);    // Hex: #800000

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI frame = new GUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public GUI() {
        // Initialization based on variables
        setTitle(windowTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, width, height);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(colorCreme); // Creme color

        //Admin Indicator
        JLabel lblAdmin = new JLabel("ADMIN ACCESS");
        lblAdmin.setBounds(430, 11, 120, 14);
        lblAdmin.setForeground(Color.RED);
        contentPane.add(lblAdmin);

        // Display Button implementation
        JButton btnAdd = new JButton("Add Record");
        btnAdd.setBounds(30, 50, 120, 30);
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               addRecords(); 
            }
        });

        JButton btnDashboard = new JButton("Dashboard");
        btnDashboard.setBounds(200, 50, 120, 30);
        btnDashboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayDashboard(); 
            }
        });

        JButton btnClear = new JButton("Clear Fields");
        btnClear.setBounds(370, 50, 120, 30);
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields(); 
            }
        });

        btnAdd.setBackground(colorMaroon); 
        btnAdd.setForeground(Color.WHITE);
        btnDashboard.setBackground(colorMaroon);
        btnDashboard.setForeground(Color.WHITE);
        btnClear.setBackground(colorMaroon);    
        btnClear.setForeground(Color.WHITE);
        
        contentPane.add(btnAdd);
        contentPane.add(btnDashboard);
        contentPane.add(btnClear);


        // Status Label
       // Title
        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setBounds(30, 100, 100, 25);
        contentPane.add(lblTitle);

        txtTitle = new JTextField();
        txtTitle.setBounds(150, 100, 200, 25);
        contentPane.add(txtTitle);

        // Type
        JLabel lblType = new JLabel("Type:");
        lblType.setBounds(30, 130, 100, 25);
        contentPane.add(lblType);

        txtType = new JTextField();
        txtType.setBounds(150, 130, 200, 25);
        contentPane.add(txtType);

        // Genre
        JLabel lblGenre = new JLabel("Genre:");
        lblGenre.setBounds(30, 160, 100, 25);
        contentPane.add(lblGenre);

        txtGenre = new JTextField();
        txtGenre.setBounds(150, 160, 200, 25);
        contentPane.add(txtGenre);

        // Dewey Decimal
        JLabel lblDewey = new JLabel("Dewey Decimal:");
        lblDewey.setBounds(30, 190, 100, 25);
        contentPane.add(lblDewey);

        txtDewey = new JTextField();
        txtDewey.setBounds(150, 190, 200, 25);
        contentPane.add(txtDewey);

        // Status Label
        lblStatus = new JLabel("Status: " + statusMessage);
        lblStatus.setBounds(30, 300, 400, 25);
        contentPane.add(lblStatus);
    }


    // UML Methods
public void addRecords() {
    String title = txtTitle.getText().trim();
    String type = txtType.getText().trim();
    String genre = txtGenre.getText().trim();
    String dewey = txtDewey.getText().trim();

    if (title.isEmpty() || type.isEmpty() || genre.isEmpty() || dewey.isEmpty()) {
        // Validation Failed: Set to Orange (or Red)
        lblStatus.setForeground(Color.ORANGE); 
        lblStatus.setText("Status: Please complete the data inputs.");
        
        javax.swing.JOptionPane.showMessageDialog(this, "Please complete the data inputs.", "Empty Data", javax.swing.JOptionPane.WARNING_MESSAGE);
    } else {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsample", "root", "");
            String query = "INSERT INTO tblbooks (title, type, genre, dewey) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            pstmt.setString(2, type);
            pstmt.setString(3, genre);
            pstmt.setString(4, dewey);

            pstmt.executeUpdate();
            
            // Success: Set to Green
            lblStatus.setForeground(new Color(0, 128, 0)); // Dark Green for better visibility
            lblStatus.setText("Status: Successfully added " + title);
            
            clearFields();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            // Database Error: Set to Orange/Red
            lblStatus.setForeground(Color.RED); 
            lblStatus.setText("Status: Database Error! " + ex.getMessage());
        }
    }
}
    
    public void displayDashboard() {
        System.out.println("Displaying Dashboard..."); 
    }

    public void clearFields() {
        txtTitle.setText("");
        txtType.setText("");
        txtGenre.setText("");
        txtDewey.setText("");
    }
    }



