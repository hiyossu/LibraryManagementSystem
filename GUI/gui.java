package GUI;

import DB;
import Library;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame {

    private String windowTitle = "Library Management System";
    private int width = 550;
    private int height = 375;
    private JPanel contentPane;
    
    private JTextField txtTitle;
    private JTextField txtType;
    private JTextField txtGenre;
    private JTextField txtDewey;
    private JLabel lblStatus;

    private Color colorCreme = new Color(255, 253, 208); 
    private Color colorMaroon = new Color(128, 0, 0);  

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                GUI frame = new GUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public GUI() {
        setTitle(windowTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, width, height);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(colorCreme);

        JLabel lblAdmin = new JLabel("ADMIN ACCESS");
        lblAdmin.setBounds(430, 11, 120, 14);
        lblAdmin.setForeground(colorMaroon);
        contentPane.add(lblAdmin);

        JButton btnAdd = new JButton("Add Record");
        btnAdd.setBounds(30, 50, 120, 30);
        btnAdd.addActionListener(e -> addRecords());

        JButton btnDashboard = new JButton("Dashboard");
        btnDashboard.setBounds(200, 50, 120, 30);
        btnDashboard.addActionListener(e -> displayDashboard());

        JButton btnClear = new JButton("Clear Fields");
        btnClear.setBounds(370, 50, 120, 30);
        btnClear.addActionListener(e -> clearFields());

        styleButton(btnAdd);
        styleButton(btnDashboard);
        styleButton(btnClear);
        
        contentPane.add(btnAdd);
        contentPane.add(btnDashboard);
        contentPane.add(btnClear);

        // Inputs
        createField("Title:", 100, txtTitle = new JTextField());
        createField("Type:", 130, txtType = new JTextField());
        createField("Genre:", 160, txtGenre = new JTextField());
        createField("Dewey:", 190, txtDewey = new JTextField());

        lblStatus = new JLabel("Status: Ready...");
        lblStatus.setBounds(30, 300, 400, 25);
        contentPane.add(lblStatus);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(colorMaroon);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    private void createField(String labelText, int y, JTextField field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setBounds(30, y, 100, 25);
        contentPane.add(lbl);
        field.setBounds(150, y, 200, 25);
        contentPane.add(field);
    }

    public void addRecords() {
        String title = txtTitle.getText().trim();
        String type = txtType.getText().trim();
        String genre = txtGenre.getText().trim();
        String dewey = txtDewey.getText().trim();

        if (title.isEmpty() || type.isEmpty() || genre.isEmpty() || dewey.isEmpty()) {
            lblStatus.setForeground(Color.ORANGE); 
            lblStatus.setText("Status: Please complete the data inputs.");
            JOptionPane.showMessageDialog(this, "Please complete all fields.", "Empty Data", JOptionPane.WARNING_MESSAGE);
        } else {
            db database = new db();       
            book newBook = new book(title, type, genre, dewey);
            
            boolean success = database.addBook(newBook);
            
            if (success) {
                lblStatus.setForeground(new Color(34, 139, 34));
                lblStatus.setText("Status: Successfully added " + title);
                clearFields();
            } else {
                lblStatus.setForeground(colorMaroon); 
                lblStatus.setText("Status: Database Error! Check console.");
            }
            database.closeConnection();
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