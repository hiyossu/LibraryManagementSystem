package Library;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
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

        // Absolute Layout per instructions for WindowBuilder
        contentPane.setLayout(null); 

        //Admin Indicator
        JLabel lblAdmin = new JLabel("ADMIN ACCESS");
        lblAdmin.setBounds(425, 11, 120, 14);
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

        JButton btnCalendar = new JButton("Calendar");
        btnCalendar.setBounds(370, 50, 120, 30);
        btnCalendar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayCalendar(); 
            }
        });
        
        contentPane.add(btnAdd);
        contentPane.add(btnDashboard);
        contentPane.add(btnCalendar);

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
        lblStatus.setBounds(30, 300, 400, 14);
        contentPane.add(lblStatus);
    }


    // UML Methods
    public void addRecords() {
        System.out.println("Connecting to dbsample..."); 
        String title = txtTitle.getText();
        String dewey = txtDewey.getText();

        if(!title.isEmpty() && !dewey.isEmpty()) {
            lblStatus.setText("Status: Attempting to add " + title);
            // Insert Database Logic here as seen in Practice Exercise #2
        } else {
            lblStatus.setText("Status: Error - Title and Dewey are required");
        }
    }
    

    public void displayDashboard() {
        System.out.println("Displaying Dashboard..."); 
    }

    public void displayCalendar() {
        System.out.println("Displaying Calendar..."); 
    }

}

