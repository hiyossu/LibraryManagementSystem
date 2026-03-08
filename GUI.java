package Library;
import DB.db;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class GUI extends JFrame {

    private Color bgColor = new Color(13, 10, 10);
    private Color sidebarColor = new Color(18, 5, 5);
    private Color panelColor = new Color(30, 8, 8);
    private Color cardColor = new Color(34, 12, 12);
    private Color gold = new Color(212, 160, 23);
    private Color darkGold = new Color(154, 114, 10);
    private Color red = new Color(190, 16, 16);
    private Color textColor = new Color(245, 236, 220);
    private Color grayText = new Color(136, 120, 104);
    private Color divColor = new Color(58, 20, 20);
    private Color successColor = new Color(107, 181, 114);
    private Color warnColor = new Color(232, 168, 64);
    private Color errColor = new Color(192, 80, 80);
    private Color fieldBg = new Color(16, 4, 4);
    private Color fieldBorder = new Color(74, 24, 24);
    private Color tableBg = new Color(22, 7, 7);
    private Color tableAlt = new Color(28, 9, 9);
    private Color tableHeader = new Color(50, 15, 15);

    private JTextField txtTitle, txtGenre, txtDewey;
    private JTextField txtCheckoutBookId, txtCheckoutBorrowerId, txtCheckoutCondition;
    private JTextField txtReturnLoanId;
    private JTextField txtSearch;
    private JLabel  lblStatus;
    private JPanel  mainContent;
    private CardLayout cardLayout;
    private JPanel activeNavBtn = null;

    private DefaultTableModel boardGameModel, dvdModel, magazineModel;
    private ImageIcon iconEmblem, iconText;

    private String currentRole = null;
    private int currentStudentId = -1;
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try { new GUI().setVisible(true); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
    }

    public GUI() {
        loadLogos();
        setTitle("Mapua Cardinal Library - Makati");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ── BIGGER default window ──
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 660));
        setLocationRelativeTo(null);
        setBackground(bgColor);
        if (iconEmblem != null) setIconImage(iconEmblem.getImage());
        showLoginScreen();
    }

    // ─────────────────────────────────────────────────────────────
    //  LOGIN SCREEN  (fully centered layout)
    // ─────────────────────────────────────────────────────────────
    private void showLoginScreen() {
        JPanel loginRoot = new JPanel(new GridBagLayout());
        loginRoot.setBackground(bgColor);
        loginRoot.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Outer card — fixed width, everything stacks vertically
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(divColor, 1),
            new EmptyBorder(40, 56, 44, 56)));
        card.setMaximumSize(new Dimension(520, 9999));
        card.setPreferredSize(new Dimension(520, 570));

        // ── Gold/red accent strip (full width) ──
        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setMaximumSize(new Dimension(9999, 4));
        strip.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel s1 = new JPanel(); s1.setBackground(gold);
        JPanel s2 = new JPanel(); s2.setBackground(red);
        strip.add(s1); strip.add(s2);
        card.add(strip);
        card.add(Box.createVerticalStrut(30));

        // ── Logo centered ──
        if (iconEmblem != null) {
            ImageIcon smallLogo = scaleIcon(toBufferedImage(iconEmblem), 90, 70);
            JLabel logo = new JLabel(smallLogo);
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(logo);
            card.add(Box.createVerticalStrut(14));
        }

        // ── Title centered ──
        JLabel title = new JLabel("Cardinal Library");
        title.setFont(new Font("Georgia", Font.BOLD, 30));
        title.setForeground(textColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Mapua University · Makati");
        sub.setFont(new Font("Dialog", Font.PLAIN, 14));
        sub.setForeground(grayText);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(30));

        JSeparator sep = new JSeparator();
        sep.setForeground(divColor);
        sep.setMaximumSize(new Dimension(9999, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(26));

        // ── Form fields — all use CENTER_ALIGNMENT and fill full card width ──
        JLabel roleLabel = new JLabel("SELECT YOUR ROLE");
        roleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        roleLabel.setForeground(grayText);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(roleLabel);
        card.add(Box.createVerticalStrut(8));

        String[] roles = {"Student", "Guest", "Admin"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        styleComboBox(cmbRole);
        cmbRole.setMaximumSize(new Dimension(9999, 42));
        cmbRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(cmbRole);
        card.add(Box.createVerticalStrut(18));

        // Student ID
        JLabel idLabel = new JLabel("STUDENT ID");
        idLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        idLabel.setForeground(grayText);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtLoginId = makeField("e.g. 2024101001");
        txtLoginId.setMaximumSize(new Dimension(9999, 42));
        txtLoginId.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Admin password
        JLabel passLabel = new JLabel("ADMIN PASSWORD");
        passLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        passLabel.setForeground(grayText);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField txtPass = new JPasswordField();
        stylePasswordField(txtPass);
        txtPass.setMaximumSize(new Dimension(9999, 42));
        txtPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPass.setVisible(false);
        passLabel.setVisible(false);

        card.add(idLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(txtLoginId);
        card.add(Box.createVerticalStrut(7));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(18));

        // Error label
        final JLabel errLabel = new JLabel(" ");
        errLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        errLabel.setForeground(errColor);
        errLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errLabel);
        card.add(Box.createVerticalStrut(10));

        // Enter button — full width, tall
        JButton btnLogin = makePrimaryButton("Enter Library");
        btnLogin.setMaximumSize(new Dimension(9999, 48));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setFont(new Font("Dialog", Font.BOLD, 15));
        card.add(btnLogin);

        // Role switching logic
        cmbRole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String role = cmbRole.getSelectedItem().toString();
                boolean isStudent = role.equals("Student");
                boolean isAdmin   = role.equals("Admin");
                idLabel.setVisible(isStudent);
                txtLoginId.setVisible(isStudent);
                passLabel.setVisible(isAdmin);
                txtPass.setVisible(isAdmin);
                errLabel.setText(" ");
                card.revalidate(); card.repaint();
            }
        });

        ActionListener doLogin = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String role = cmbRole.getSelectedItem().toString();
                if (role.equals("Student")) {
                    String idStr = txtLoginId.getText().trim();
                    if (!idStr.matches("\\d{10}") || !idStr.startsWith("20")) {
                        errLabel.setText("Enter a valid 10-digit Mapua student ID.");
                        return;
                    }
                    int idNo = Integer.parseInt(idStr);
                    db database = new db();
                    boolean exists = database.borrowerExists(idNo);
                    database.closeConnection();
                    if (!exists) { errLabel.setText("Student ID not found. Check with the librarian."); return; }
                    currentRole = "Student";
                    currentStudentId = idNo;
                } else if (role.equals("Admin")) {
                    String pass = new String(txtPass.getPassword());
                    if (!pass.equals(ADMIN_PASSWORD)) { errLabel.setText("Incorrect password."); return; }
                    currentRole = "Admin";
                } else {
                    currentRole = "Guest";
                }
                buildMainUI();
            }
        };

        btnLogin.addActionListener(doLogin);
        txtLoginId.addActionListener(doLogin);
        txtPass.addActionListener(doLogin);

        loginRoot.add(card, gbc);
        setContentPane(loginRoot);
        revalidate(); repaint();
    }

    private void buildMainUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainPanel(), BorderLayout.CENTER);
        revalidate(); repaint();
        if (currentRole.equals("Student")) {
            showCard("MY_LOANS");
            updateTopBar("My Loans", "My Account");
        } else {
            showCard("DASHBOARD");
            updateTopBar("Dashboard", "System / Dashboard");
            refreshDashboard();
        }
    }

    private void loadLogos() {
        iconEmblem = loadIcon("logo_emblem_small.png", 100, 75);
        iconText   = loadIcon("logo_text_small.png",   150, 53);
    }

    private ImageIcon loadIcon(String filename, int maxW, int maxH) {
        java.io.File classLocation = null;
        try {
            java.net.URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
            classLocation = new java.io.File(url.toURI());
        } catch (Exception ignored) {}
        java.util.List<String> paths = new java.util.ArrayList<String>();
        if (classLocation != null) {
            java.io.File dir = classLocation;
            for (int i = 0; i < 4; i++) {
                paths.add(dir.getAbsolutePath() + "/resources/" + filename);
                dir = dir.getParentFile();
                if (dir == null) break;
            }
        }
        paths.add("resources/" + filename);
        paths.add("Library/resources/" + filename);
        paths.add(filename);
        for (String p : paths) {
            try {
                java.io.File f = new java.io.File(p);
                if (f.exists()) { BufferedImage img = ImageIO.read(f); if (img != null) return scaleIcon(img, maxW, maxH); }
            } catch (Exception ignored) {}
        }
        try {
            InputStream is = getClass().getResourceAsStream("/" + filename);
            if (is != null) { BufferedImage img = ImageIO.read(is); is.close(); return scaleIcon(img, maxW, maxH); }
        } catch (Exception ignored) {}
        return null;
    }

    private ImageIcon scaleIcon(BufferedImage img, int maxW, int maxH) {
        if (img == null) return null;
        double scale = Math.min((double) maxW / img.getWidth(), (double) maxH / img.getHeight());
        return new ImageIcon(img.getScaledInstance((int)(img.getWidth()*scale), (int)(img.getHeight()*scale), Image.SCALE_SMOOTH));
    }

    private BufferedImage toBufferedImage(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics(); icon.paintIcon(null, g, 0, 0); g.dispose(); return bi;
    }

    // ─────────────────────────────────────────────────────────────
    //  SIDEBAR  (slightly wider)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(255, 0));   // wider sidebar
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, divColor));

        sidebar.add(buildLogoArea());

        boolean isAdmin   = "Admin".equals(currentRole);
        boolean isStudent = "Student".equals(currentRole);

        sidebar.add(makeDividerLabel("CATALOG"));
        if (isAdmin) sidebar.add(makeNavBtn("  + Add New Book", "add"));
        sidebar.add(makeNavBtn("  Search Books", "search"));

        if (isAdmin || isStudent) {
            sidebar.add(makeDividerLabel("BORROWING"));
            sidebar.add(makeNavBtn("  Checkout Book", "checkout"));
            sidebar.add(makeNavBtn("  Return Book",   "returnbook"));
        }

        sidebar.add(makeDividerLabel("MEDIA"));
        if (isAdmin || isStudent) sidebar.add(makeNavBtn("  Checkout Media", "media_checkout"));
        sidebar.add(makeNavBtn("  Board Game", "add_boardgame"));
        sidebar.add(makeNavBtn("  DVD",        "add_dvd"));
        sidebar.add(makeNavBtn("  Magazine",   "add_magazine"));

        sidebar.add(makeDividerLabel("REFERENCE"));
        sidebar.add(makeNavBtn("  Reference Book", "add_reference"));

        if (isAdmin) {
            sidebar.add(makeDividerLabel("PEOPLE"));
            sidebar.add(makeNavBtn("  Borrowers", "borrowers"));
        }

        if (isStudent) {
            sidebar.add(makeDividerLabel("MY ACCOUNT"));
            sidebar.add(makeNavBtn("  My Loans", "my_loans"));
        }

        if (isAdmin) {
            sidebar.add(makeDividerLabel("SYSTEM"));
            sidebar.add(makeNavBtn("  Dashboard",     "dashboard"));
            sidebar.add(makeNavBtn("  Notifications", "notifications"));
        }

        sidebar.add(makeNavBtn("  About", "about"));
        sidebar.add(Box.createVerticalGlue());

        JPanel logoutBtn = makeNavBtn("  Logout", "logout");
        JLabel logoutLbl = (JLabel) logoutBtn.getComponent(0);
        logoutLbl.setForeground(errColor);
        sidebar.add(logoutBtn);
        sidebar.add(buildSidebarFooter());
        return sidebar;
    }

    private JPanel buildLogoArea() {
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBackground(sidebarColor);
        logoArea.setMaximumSize(new Dimension(255, 230));
        logoArea.setBorder(new EmptyBorder(22, 0, 14, 0));
        logoArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (iconEmblem != null) {
            JLabel lbl = new JLabel(iconEmblem);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
            logoArea.add(lbl);
        } else {
            JLabel fb = new JLabel("🏛");
            fb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
            fb.setForeground(gold); fb.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(fb); logoArea.add(Box.createVerticalStrut(6));
        }
        if (iconText != null) {
            JLabel lbl = new JLabel(iconText);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(0, 0, 3, 0));
            logoArea.add(lbl);
        } else {
            JLabel lbl = new JLabel("Mapua University");
            lbl.setFont(new Font("Georgia", Font.BOLD, 14));
            lbl.setForeground(red); lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(lbl);
        }
        JLabel campus = new JLabel("Cardinal Library · Makati");
        campus.setFont(new Font("Georgia", Font.ITALIC, 11));
        campus.setForeground(grayText); campus.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoArea.add(campus); logoArea.add(Box.createVerticalStrut(12));

        JPanel stripe = new JPanel(new GridLayout(1, 2));
        stripe.setMaximumSize(new Dimension(255, 3)); stripe.setPreferredSize(new Dimension(255, 3));
        JPanel g1 = new JPanel(); g1.setBackground(gold);
        JPanel g2 = new JPanel(); g2.setBackground(red);
        stripe.add(g1); stripe.add(g2);
        logoArea.add(stripe);
        return logoArea;
    }

    private JPanel buildSidebarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(sidebarColor);
        footer.setBorder(new EmptyBorder(12, 22, 18, 22));
        footer.setMaximumSize(new Dimension(255, 62));
        JLabel a = new JLabel("ADMIN SESSION");
        a.setFont(new Font("Monospaced", Font.BOLD, 10)); a.setForeground(gold);
        JLabel v = new JLabel("LMS v2.0  2025");
        v.setFont(new Font("Dialog", Font.PLAIN, 10)); v.setForeground(grayText);
        footer.add(a, BorderLayout.NORTH); footer.add(v, BorderLayout.SOUTH);
        return footer;
    }

    private JPanel makeDividerLabel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(sidebarColor); p.setMaximumSize(new Dimension(255, 32));
        p.setBorder(new EmptyBorder(10, 22, 2, 22));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Dialog", Font.BOLD, 9)); lbl.setForeground(grayText);
        p.add(lbl, BorderLayout.CENTER);
        JSeparator sep = new JSeparator(); sep.setForeground(divColor);
        p.add(sep, BorderLayout.SOUTH);
        return p;
    }

    // ── Nav button is taller for easier clicking ──
    private JPanel makeNavBtn(final String text, final String action) {
        final JPanel btn = new JPanel(new BorderLayout());
        btn.setBackground(sidebarColor);
        btn.setMaximumSize(new Dimension(255, 46));
        btn.setBorder(new EmptyBorder(12, 22, 12, 18));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 13)); lbl.setForeground(textColor);
        btn.add(lbl, BorderLayout.CENTER);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn != activeNavBtn) btn.setBackground(panelColor); }
            public void mouseExited (MouseEvent e) { if (btn != activeNavBtn) btn.setBackground(sidebarColor); }
            public void mouseClicked(MouseEvent e) { navigateTo(action, btn); }
        });
        if (action.equals("add")) setActiveNav(btn, lbl);
        return btn;
    }

    private void setActiveNav(JPanel btn, JLabel lbl) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(sidebarColor);
            activeNavBtn.setBorder(new EmptyBorder(12, 22, 12, 18));
            Component[] comps = activeNavBtn.getComponents();
            for (Component c : comps) if (c instanceof JLabel) ((JLabel)c).setForeground(textColor);
        }
        activeNavBtn = btn;
        btn.setBackground(cardColor);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, gold),
            new EmptyBorder(12, 19, 12, 18)));
        lbl.setForeground(gold);
    }

    private void navigateTo(String action, JPanel btn) {
        JLabel lbl = (JLabel) btn.getComponent(0);
        setActiveNav(btn, lbl);
        if (action.equals("add")) {
            showCard("ADD");
            updateTopBar("Add New Book", "Catalog / Add Book");
        } else if (action.equals("search")) {
            showCard("SEARCH");
            updateTopBar("Search Books", "Catalog / Search");
            refreshSearchTable();
        } else if (action.equals("checkout")) {
            showCard("CHECKOUT");
            updateTopBar("Checkout Book", "Borrowing / Checkout");
        } else if (action.equals("returnbook")) {
            showCard("RETURN");
            updateTopBar("Return Book", "Borrowing / Return");
        } else if (action.equals("add_boardgame")) {
            showCard("ADD_BOARDGAME");
            updateTopBar("Board Game", "Media / Board Game");
            refreshMediaTable(boardGameModel, "BoardGame");
        } else if (action.equals("add_dvd")) {
            showCard("ADD_DVD");
            updateTopBar("DVD", "Media / DVD");
            refreshMediaTable(dvdModel, "DVD");
        } else if (action.equals("add_magazine")) {
            showCard("ADD_MAGAZINE");
            updateTopBar("Magazine", "Media / Magazine");
            refreshMediaTable(magazineModel, "Magazine");
        } else if (action.equals("media_checkout")) {
            showCard("MEDIA_CHECKOUT");
            updateTopBar("Checkout Media", "Media / Checkout");
            refreshMediaCheckout();
        } else if (action.equals("add_reference")) {
            showCard("ADD_REFERENCE");
            updateTopBar("Reference Book", "Reference / Reference Book");
        } else if (action.equals("borrowers")) {
            showCard("BORROWERS");
            updateTopBar("Manage Borrowers", "People / Borrowers");
            refreshBorrowersTable();
        } else if (action.equals("dashboard")) {
            showCard("DASHBOARD");
            updateTopBar("Dashboard", "System / Dashboard");
            refreshDashboard();
        } else if (action.equals("notifications")) {
            showCard("NOTIFICATIONS");
            updateTopBar("Notifications", "System / Notifications");
            refreshNotifications();
        } else if (action.equals("about")) {
            showAbout();
        } else if (action.equals("logout")) {
            currentRole = null;
            currentStudentId = -1;
            showLoginScreen();
        } else if (action.equals("my_loans")) {
            showCard("MY_LOANS");
            updateTopBar("My Loans", "My Account / Loans");
            refreshMyLoans();
        }
    }

    private JLabel lblPageTitle, lblCrumb;

    // ─────────────────────────────────────────────────────────────
    //  MAIN PANEL  (taller top bar)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bgColor);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(panelColor);
        topBar.setPreferredSize(new Dimension(0, 62));   // taller top bar
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, red),
            new EmptyBorder(0, 28, 0, 28)));

        lblPageTitle = new JLabel("Add New Book Record");
        lblPageTitle.setFont(new Font("Georgia", Font.BOLD, 20));   // bigger
        lblPageTitle.setForeground(textColor);

        lblCrumb = new JLabel("Library Catalog / Add Record");
        lblCrumb.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblCrumb.setForeground(grayText);

        topBar.add(lblPageTitle, BorderLayout.CENTER);
        topBar.add(lblCrumb,    BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        cardLayout  = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(bgColor);
        mainContent.add(buildAddBookPanel(),                          "ADD");
        mainContent.add(buildAddTypePanel("BoardGame", "Board Game"), "ADD_BOARDGAME");
        mainContent.add(buildAddTypePanel("DVD",       "DVD"),        "ADD_DVD");
        mainContent.add(buildAddTypePanel("Magazine",  "Magazine"),   "ADD_MAGAZINE");
        mainContent.add(buildMediaCheckoutPanel(),                    "MEDIA_CHECKOUT");
        mainContent.add(buildAddRefPanel(),                           "ADD_REFERENCE");
        mainContent.add(buildSearchPanel(),                           "SEARCH");
        mainContent.add(buildCheckoutPanel(),                         "CHECKOUT");
        mainContent.add(buildReturnPanel(),                           "RETURN");
        mainContent.add(buildBorrowersPanel(),                        "BORROWERS");
        mainContent.add(buildDashboardPanel(),                        "DASHBOARD");
        mainContent.add(buildNotificationsPanel(),                    "NOTIFICATIONS");
        mainContent.add(buildMyLoansPanel(),                          "MY_LOANS");
        main.add(mainContent, BorderLayout.CENTER);

        // ── Status bar ──
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(sidebarColor);
        statusBar.setPreferredSize(new Dimension(0, 34));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, divColor),
            new EmptyBorder(0, 22, 0, 22)));
        lblStatus = new JLabel("System ready");
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblStatus.setForeground(grayText);
        JLabel brand = new JLabel("Mapua Cardinal Library LMS");
        brand.setFont(new Font("Dialog", Font.PLAIN, 11));
        brand.setForeground(grayText);
        statusBar.add(lblStatus, BorderLayout.CENTER);
        statusBar.add(brand,     BorderLayout.EAST);
        main.add(statusBar, BorderLayout.SOUTH);
        return main;
    }

    private void showCard(String name)                    { cardLayout.show(mainContent, name); }
    private void updateTopBar(String title, String crumb) { lblPageTitle.setText(title); lblCrumb.setText(crumb); }

    // ─────────────────────────────────────────────────────────────
    //  ADD BOOK PANEL
    // ─────────────────────────────────────────────────────────────
    private JPanel buildAddBookPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(32, 40, 32, 40));

        JPanel card = buildAddCard("Add New Book", "Register a new book into the library catalog.");
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(24, 36, 28, 36));

        body.add(makeFormLabel("Title"));
        body.add(Box.createVerticalStrut(8));
        txtTitle = makeField("e.g., The Great Gatsby");
        txtTitle.setMaximumSize(new Dimension(9999, 42));
        txtTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(txtTitle);
        body.add(Box.createVerticalStrut(20));

        JPanel twoCol = new JPanel(new GridLayout(1, 2, 18, 0));
        twoCol.setBackground(cardColor);
        twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);
        twoCol.setMaximumSize(new Dimension(9999, 88));

        JPanel colGenre = new JPanel(); colGenre.setLayout(new BoxLayout(colGenre, BoxLayout.Y_AXIS)); colGenre.setBackground(cardColor);
        colGenre.add(makeFormLabel("Genre")); colGenre.add(Box.createVerticalStrut(8));
        txtGenre = makeField("e.g., Fiction, Science...");
        txtGenre.setMaximumSize(new Dimension(9999, 42)); txtGenre.setAlignmentX(Component.LEFT_ALIGNMENT);
        colGenre.add(txtGenre);

        JPanel colDewey = new JPanel(); colDewey.setLayout(new BoxLayout(colDewey, BoxLayout.Y_AXIS)); colDewey.setBackground(cardColor);
        colDewey.add(makeFormLabel("Dewey Decimal")); colDewey.add(Box.createVerticalStrut(8));
        txtDewey = makeField("e.g., 813.54");
        txtDewey.setMaximumSize(new Dimension(9999, 42)); txtDewey.setAlignmentX(Component.LEFT_ALIGNMENT);
        colDewey.add(txtDewey);

        twoCol.add(colGenre); twoCol.add(colDewey);
        body.add(twoCol);
        body.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("Books require a valid Dewey Decimal number (e.g., 813.54).");
        hint.setFont(new Font("Dialog", Font.ITALIC, 12)); hint.setForeground(grayText);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint);
        body.add(Box.createVerticalStrut(26));

        body.add(makeSeparatorLine());
        body.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(cardColor); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(9999, 54));
        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear Fields");
        btnAdd.setPreferredSize(new Dimension(170, 44));
        btnClear.setPreferredSize(new Dimension(140, 44));
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addRecordsTyped("Book", txtGenre.getText().trim(), txtDewey.getText().trim()); }
        });
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { txtTitle.setText(""); txtGenre.setText(""); txtDewey.setText(""); setStatus("Fields cleared.", grayText); }
        });
        btnRow.add(btnAdd); btnRow.add(btnClear);
        body.add(btnRow);

        card.add(body, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    private JPanel buildAddTypePanel(final String mediaType, final String pageTitle) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setBackground(bgColor);
        topBar.setBorder(new EmptyBorder(0, 0, 16, 0));

        final JTextField fSearch = makeField("Search by title or genre...");
        JButton btnSearch = makePrimaryButton("Search");
        JButton btnAll    = makeOutlineButton("Show All");
        btnSearch.setPreferredSize(new Dimension(110, 38));
        btnAll.setPreferredSize(new Dimension(100, 38));
        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBtns.setBackground(bgColor);
        searchBtns.add(btnSearch); searchBtns.add(btnAll);
        topBar.add(fSearch, BorderLayout.CENTER);
        topBar.add(searchBtns, BorderLayout.EAST);
        outer.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Genre", "Available"};
        final DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (mediaType.equals("BoardGame")) boardGameModel = model;
        else if (mediaType.equals("DVD"))  dvdModel       = model;
        else                               magazineModel  = model;

        final JTable table = makeStyledTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(380);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        outer.add(makeScrollPane(table), BorderLayout.CENTER);

        final Runnable loadAll = new Runnable() {
            public void run() {
                model.setRowCount(0);
                try {
                    db database = new db();
                    java.util.List<String> rows = database.getAllBooks();
                    database.closeConnection();
                    for (String row : rows) {
                        String[] parts = parseBookRow(row);
                        if (parts != null && parts[2].equals(mediaType)) {
                            model.addRow(new Object[]{
                                parts[0], parts[1], parts[3],
                                parts[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"
                            });
                        }
                    }
                } catch (Exception ex) { setStatus("Error loading " + pageTitle + ": " + ex.getMessage(), errColor); }
            }
        };

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String kw = fSearch.getText().trim().toLowerCase();
                if (kw.isEmpty()) { loadAll.run(); return; }
                model.setRowCount(0);
                try {
                    db database = new db();
                    java.util.List<String> rows = database.getAllBooks();
                    database.closeConnection();
                    for (String row : rows) {
                        String[] parts = parseBookRow(row);
                        if (parts != null && parts[2].equals(mediaType) &&
                            (parts[1].toLowerCase().contains(kw) || parts[3].toLowerCase().contains(kw))) {
                            model.addRow(new Object[]{
                                parts[0], parts[1], parts[3],
                                parts[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"
                            });
                        }
                    }
                } catch (Exception ex) { setStatus("Error searching: " + ex.getMessage(), errColor); }
            }
        });
        btnAll.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { fSearch.setText(""); loadAll.run(); } });
        fSearch.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { btnSearch.doClick(); } });

        return outer;
    }

    private void refreshMediaTable(DefaultTableModel model, String mediaType) {
        if (model == null) return;
        model.setRowCount(0);
        try {
            db database = new db();
            java.util.List<String> rows = database.getAllBooks();
            database.closeConnection();
            for (String row : rows) {
                String[] parts = parseBookRow(row);
                if (parts != null && parts[2].equals(mediaType)) {
                    model.addRow(new Object[]{
                        parts[0], parts[1], parts[3],
                        parts[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"
                    });
                }
            }
        } catch (Exception ex) { setStatus("Error loading list: " + ex.getMessage(), errColor); }
    }

    private String[] parseBookRow(String row) {
        try {
            String id        = row.split("\\| Title: ")[0].replace("ID: ", "").trim();
            String title     = row.split("\\| Title: ")[1].split("\\| Type: ")[0].trim();
            String type      = row.split("\\| Type: ")[1].split("\\| Genre: ")[0].trim();
            String genre     = row.split("\\| Genre: ")[1].split("\\| DDC: ")[0].trim();
            String ddc       = row.split("\\| DDC: ")[1].split("\\| Can Borrow: ")[0].trim();
            String canBorrow = row.split("\\| Can Borrow: ")[1].trim();
            return new String[]{id, title, type, genre, ddc, canBorrow};
        } catch (Exception e) { return null; }
    }

    private DefaultTableModel mediaCheckoutModel;
    private JTextField txtMediaBorrowerId, txtMediaCondition;

    private JPanel buildMediaCheckoutPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 18, 32));

        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setBackground(bgColor);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        final String[] filterTypes  = {"All", "BoardGame", "DVD", "Magazine"};
        final String[] filterLabels = {"All", "Board Game", "DVD", "Magazine"};
        final JPanel[] pills        = new JPanel[filterTypes.length];
        final JLabel[] pillLbls     = new JLabel[filterTypes.length];
        final String[] activeFilter = {"All"};

        JPanel pillRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pillRow.setBackground(bgColor);
        for (int i = 0; i < filterTypes.length; i++) {
            JPanel pill = new JPanel(new BorderLayout());
            pill.setCursor(new Cursor(Cursor.HAND_CURSOR));
            JLabel lbl = new JLabel(filterLabels[i]);
            lbl.setFont(new Font("Dialog", Font.PLAIN, 13));
            lbl.setBorder(new EmptyBorder(6, 16, 6, 16));
            pill.add(lbl);
            pills[i] = pill; pillLbls[i] = lbl;
            pillRow.add(pill);
        }

        final JTextField fSearch = makeField("Search by title or genre...");
        JButton btnSearch = makePrimaryButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 36));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(bgColor);
        right.add(fSearch); right.add(btnSearch);

        topBar.add(pillRow, BorderLayout.WEST);
        topBar.add(right,   BorderLayout.EAST);
        outer.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Type", "Genre", "Available"};
        mediaCheckoutModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        final JTable table = makeStyledTable(mediaCheckoutModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(310);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outer.add(makeScrollPane(table), BorderLayout.CENTER);

        // Bottom form
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(panelColor);
        bottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, divColor),
            new EmptyBorder(14, 18, 14, 18)));

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        formRow.setBackground(panelColor);

        JLabel lblSel = new JLabel("Selected ID:");
        lblSel.setFont(new Font("Dialog", Font.BOLD, 12)); lblSel.setForeground(grayText);
        final JTextField fSelId = makeField("—");
        fSelId.setPreferredSize(new Dimension(70, 36)); fSelId.setEditable(false);
        fSelId.setBackground(new Color(10, 3, 3));

        JLabel lblBorr = new JLabel("Borrower ID:");
        lblBorr.setFont(new Font("Dialog", Font.BOLD, 12)); lblBorr.setForeground(grayText);
        txtMediaBorrowerId = makeField("e.g., 10001");
        txtMediaBorrowerId.setPreferredSize(new Dimension(130, 36));

        JLabel lblCond = new JLabel("Condition:");
        lblCond.setFont(new Font("Dialog", Font.BOLD, 12)); lblCond.setForeground(grayText);
        txtMediaCondition = makeField("Good");
        txtMediaCondition.setPreferredSize(new Dimension(110, 36));

        JButton btnCheckout = makePrimaryButton("Checkout");
        JButton btnClear    = makeOutlineButton("Clear");
        btnCheckout.setPreferredSize(new Dimension(120, 36));
        btnClear.setPreferredSize(new Dimension(90, 36));

        formRow.add(lblSel); formRow.add(fSelId);
        formRow.add(lblBorr); formRow.add(txtMediaBorrowerId);
        formRow.add(lblCond); formRow.add(txtMediaCondition);
        formRow.add(btnCheckout); formRow.add(btnClear);
        bottom.add(formRow, BorderLayout.CENTER);
        outer.add(bottom, BorderLayout.SOUTH);

        final Runnable refreshPills = new Runnable() {
            public void run() {
                for (int i = 0; i < filterTypes.length; i++) {
                    boolean active = filterTypes[i].equals(activeFilter[0]);
                    pills[i].setBackground(active ? gold : fieldBg);
                    pills[i].setBorder(active
                        ? BorderFactory.createLineBorder(gold, 1)
                        : BorderFactory.createLineBorder(fieldBorder, 1));
                    pillLbls[i].setForeground(active ? new Color(21, 7, 7) : grayText);
                }
            }
        };
        refreshPills.run();

        final Runnable loadFiltered = new Runnable() {
            public void run() {
                mediaCheckoutModel.setRowCount(0);
                String kw = fSearch.getText().trim().toLowerCase();
                try {
                    db database = new db();
                    java.util.List<String> rows = database.getAllBooks();
                    database.closeConnection();
                    for (String row : rows) {
                        String[] p = parseBookRow(row);
                        if (p == null) continue;
                        String type = p[2];
                        if (!type.equals("BoardGame") && !type.equals("DVD") && !type.equals("Magazine")) continue;
                        if (!activeFilter[0].equals("All") && !type.equals(activeFilter[0])) continue;
                        if (!kw.isEmpty() && !p[1].toLowerCase().contains(kw) && !p[3].toLowerCase().contains(kw)) continue;
                        String label = type.equals("BoardGame") ? "Board Game" : type;
                        mediaCheckoutModel.addRow(new Object[]{
                            p[0], p[1], label, p[3],
                            p[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"
                        });
                    }
                } catch (Exception ex) { setStatus("Error loading media: " + ex.getMessage(), errColor); }
            }
        };

        for (int i = 0; i < filterTypes.length; i++) {
            final int idx = i;
            pills[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { activeFilter[0] = filterTypes[idx]; refreshPills.run(); loadFiltered.run(); }
                public void mouseEntered(MouseEvent e) { if (!filterTypes[idx].equals(activeFilter[0])) pills[idx].setBackground(panelColor); }
                public void mouseExited (MouseEvent e) { if (!filterTypes[idx].equals(activeFilter[0])) pills[idx].setBackground(fieldBg); }
            });
        }

        table.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) fSelId.setText(mediaCheckoutModel.getValueAt(row, 0).toString());
            }
        });

        btnSearch.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { loadFiltered.run(); } });
        fSearch.addActionListener(new ActionListener()   { public void actionPerformed(ActionEvent e) { loadFiltered.run(); } });

        btnCheckout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selId  = fSelId.getText().trim();
                String borrId = txtMediaBorrowerId.getText().trim();
                String cond   = txtMediaCondition.getText().trim();
                if (selId.isEmpty() || selId.equals("—")) { setStatus("Please select an item from the list.", warnColor); return; }
                if (borrId.isEmpty()) { setStatus("Please enter a Borrower ID.", warnColor); return; }
                if (cond.isEmpty()) cond = "Good";
                try {
                    int bookId     = Integer.parseInt(selId);
                    int borrowerId = Integer.parseInt(borrId);
                    db database = new db();
                    if (!database.borrowerExists(borrowerId)) { setStatus("Borrower ID " + borrowerId + " not found.", errColor); database.closeConnection(); return; }
                    boolean ok = database.checkoutBook(bookId, borrowerId, cond);
                    database.closeConnection();
                    if (ok) {
                        setStatus("Item ID " + bookId + " checked out to Borrower " + borrowerId + ". Due in 14 days.", successColor);
                        fSelId.setText("—"); txtMediaBorrowerId.setText(""); txtMediaCondition.setText("Good");
                        loadFiltered.run();
                    } else {
                        setStatus("Checkout failed — item unavailable or borrower reached limit.", errColor);
                    }
                } catch (NumberFormatException ex) { setStatus("IDs must be numbers.", warnColor); }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fSelId.setText("—"); txtMediaBorrowerId.setText(""); txtMediaCondition.setText("Good");
                table.clearSelection();
            }
        });

        return outer;
    }

    private void refreshMediaCheckout() {
        if (mediaCheckoutModel == null) return;
        mediaCheckoutModel.setRowCount(0);
        try {
            db database = new db();
            java.util.List<String> rows = database.getAllBooks();
            database.closeConnection();
            for (String row : rows) {
                String[] p = parseBookRow(row);
                if (p == null) continue;
                String type = p[2];
                if (!type.equals("BoardGame") && !type.equals("DVD") && !type.equals("Magazine")) continue;
                String label = type.equals("BoardGame") ? "Board Game" : type;
                mediaCheckoutModel.addRow(new Object[]{
                    p[0], p[1], label, p[3],
                    p[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"
                });
            }
        } catch (Exception ex) { setStatus("Error loading media: " + ex.getMessage(), errColor); }
    }

    private JPanel buildAddRefPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(32, 40, 32, 40));

        JPanel card = buildAddCard("Add Reference Book", "Reference books are for in-library use only and cannot be borrowed.");
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(24, 36, 28, 36));

        body.add(makeFormLabel("Title"));
        body.add(Box.createVerticalStrut(8));
        final JTextField fTitle = makeField("e.g., Black's Law Dictionary");
        fTitle.setMaximumSize(new Dimension(9999, 42)); fTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(fTitle);
        body.add(Box.createVerticalStrut(20));

        JPanel twoCol = new JPanel(new GridLayout(1, 2, 18, 0));
        twoCol.setBackground(cardColor); twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);
        twoCol.setMaximumSize(new Dimension(9999, 88));

        JPanel colGenre = new JPanel(); colGenre.setLayout(new BoxLayout(colGenre, BoxLayout.Y_AXIS)); colGenre.setBackground(cardColor);
        colGenre.add(makeFormLabel("Genre")); colGenre.add(Box.createVerticalStrut(8));
        final JTextField fGenre = makeField("e.g., Law, Medicine, Science...");
        fGenre.setMaximumSize(new Dimension(9999, 42)); fGenre.setAlignmentX(Component.LEFT_ALIGNMENT);
        colGenre.add(fGenre);

        JPanel colDewey = new JPanel(); colDewey.setLayout(new BoxLayout(colDewey, BoxLayout.Y_AXIS)); colDewey.setBackground(cardColor);
        colDewey.add(makeFormLabel("Dewey Decimal")); colDewey.add(Box.createVerticalStrut(8));
        final JTextField fDewey = makeField("e.g., 340.1");
        fDewey.setMaximumSize(new Dimension(9999, 42)); fDewey.setAlignmentX(Component.LEFT_ALIGNMENT);
        colDewey.add(fDewey);

        twoCol.add(colGenre); twoCol.add(colDewey);
        body.add(twoCol);
        body.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("⚠  Reference books are for in-library use only and cannot be borrowed.");
        hint.setFont(new Font("Dialog", Font.ITALIC, 12)); hint.setForeground(warnColor);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint);
        body.add(Box.createVerticalStrut(26));

        body.add(makeSeparatorLine());
        body.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(cardColor); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(9999, 54));
        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear Fields");
        btnAdd.setPreferredSize(new Dimension(170, 44));
        btnClear.setPreferredSize(new Dimension(140, 44));
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtTitle = fTitle;
                addRecordsTyped("ReferenceBook", fGenre.getText().trim(), fDewey.getText().trim());
            }
        });
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { fTitle.setText(""); fGenre.setText(""); fDewey.setText(""); setStatus("Fields cleared.", grayText); }
        });
        btnRow.add(btnAdd); btnRow.add(btnClear);
        body.add(btnRow);

        card.add(body, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    private JPanel buildAddCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(760, 420));   // wider card

        JPanel accent = new JPanel(new GridLayout(1, 2));
        accent.setPreferredSize(new Dimension(0, 5));    // slightly thicker accent
        JPanel a1 = new JPanel(); a1.setBackground(gold);
        JPanel a2 = new JPanel(); a2.setBackground(red);
        accent.add(a1); accent.add(a2);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cardColor);
        header.setBorder(new EmptyBorder(22, 36, 16, 36));
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Georgia", Font.BOLD, 24)); lblT.setForeground(textColor);
        JLabel lblS = new JLabel(subtitle);
        lblS.setFont(new Font("Dialog", Font.PLAIN, 13)); lblS.setForeground(grayText);
        header.add(lblT, BorderLayout.NORTH); header.add(lblS, BorderLayout.SOUTH);

        JSeparator sep = new JSeparator(); sep.setForeground(divColor);

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(cardColor);
        north.add(accent,  BorderLayout.NORTH);
        north.add(header,  BorderLayout.CENTER);
        north.add(sep,     BorderLayout.SOUTH);
        card.add(north, BorderLayout.NORTH);
        return card;
    }

    private JSeparator makeSeparatorLine() {
        JSeparator s = new JSeparator(); s.setForeground(divColor);
        s.setMaximumSize(new Dimension(9999, 1));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        return s;
    }

    private JLabel makeFormLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(grayText);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleComboBox(JComboBox<String> cmb) {
        cmb.setBackground(fieldBg); cmb.setForeground(textColor);
        cmb.setFont(new Font("Dialog", Font.PLAIN, 14));
        cmb.setBorder(BorderFactory.createLineBorder(fieldBorder, 1));
        cmb.setPreferredSize(new Dimension(0, 42));
        cmb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? cardColor : fieldBg); setForeground(sel ? gold : textColor);
                setBorder(new EmptyBorder(5, 12, 5, 12));
                return this;
            }
        });
    }

    private void stylePasswordField(JPasswordField pf) {
        pf.setBackground(fieldBg);
        pf.setForeground(textColor);
        pf.setCaretColor(textColor);
        pf.setFont(new Font("Dialog", Font.PLAIN, 14));
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldBorder, 1),
            new EmptyBorder(5, 12, 5, 12)));
    }

    private DefaultTableModel myLoansModel, myHistoryModel;

    private JPanel buildMyLoansPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel heading = new JLabel("My Library Account");
        heading.setFont(new Font("Georgia", Font.BOLD, 20));
        heading.setForeground(textColor);
        heading.setBorder(new EmptyBorder(0, 0, 18, 0));
        outer.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 18));
        content.setBackground(bgColor);

        JPanel activeCard = new JPanel(new BorderLayout());
        activeCard.setBackground(cardColor);
        activeCard.setBorder(BorderFactory.createLineBorder(divColor, 1));
        JLabel activeTitle = new JLabel("  Active Loans");
        activeTitle.setFont(new Font("Dialog", Font.BOLD, 13));
        activeTitle.setForeground(gold);
        activeTitle.setBorder(new EmptyBorder(12, 14, 12, 14));
        String[] activeCols = {"Loan ID", "Title", "Type", "Date Borrowed", "Due Date"};
        myLoansModel = new DefaultTableModel(activeCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable activeTable = makeStyledTable(myLoansModel);
        activeCard.add(activeTitle, BorderLayout.NORTH);
        activeCard.add(makeScrollPane(activeTable), BorderLayout.CENTER);

        JPanel historyCard = new JPanel(new BorderLayout());
        historyCard.setBackground(cardColor);
        historyCard.setBorder(BorderFactory.createLineBorder(divColor, 1));
        JLabel historyTitle = new JLabel("  Borrow History");
        historyTitle.setFont(new Font("Dialog", Font.BOLD, 13));
        historyTitle.setForeground(grayText);
        historyTitle.setBorder(new EmptyBorder(12, 14, 12, 14));
        String[] historyCols = {"Loan ID", "Title", "Type", "Date Borrowed", "Date Returned", "Fine"};
        myHistoryModel = new DefaultTableModel(historyCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = makeStyledTable(myHistoryModel);
        historyCard.add(historyTitle, BorderLayout.NORTH);
        historyCard.add(makeScrollPane(historyTable), BorderLayout.CENTER);

        content.add(activeCard);
        content.add(historyCard);
        outer.add(content, BorderLayout.CENTER);
        return outer;
    }

    private void refreshMyLoans() {
        if (myLoansModel == null || myHistoryModel == null) return;
        myLoansModel.setRowCount(0);
        myHistoryModel.setRowCount(0);
        if (currentStudentId < 0) return;
        try {
            db database = new db();
            List<String> loans = database.getActiveLoansByBorrower(currentStudentId);
            for (String l : loans) {
                String[] p = l.split(" \\| ");
                String loanId = "", bookId = "", borrowed = "", due = "";
                for (String s : p) {
                    if (s.startsWith("LoanID: "))     loanId  = s.replace("LoanID: ", "").trim();
                    else if (s.startsWith("BookID: ")) bookId  = s.replace("BookID: ", "").trim();
                    else if (s.startsWith("Borrowed: ")) borrowed = s.replace("Borrowed: ", "").trim();
                    else if (s.startsWith("Due: "))    due     = s.replace("Due: ", "").trim();
                }
                String title = "", type = "";
                if (!bookId.isEmpty()) {
                    try {
                        String status = database.getBookStatus(Integer.parseInt(bookId));
                        String[] sp = status.split(" — ");
                        if (sp.length > 0) title = sp[0];
                    } catch (Exception ignored) {}
                }
                myLoansModel.addRow(new Object[]{loanId, title, type, borrowed, due});
            }
            database.closeConnection();
        } catch (Exception ex) {
            setStatus("Error loading loans: " + ex.getMessage(), errColor);
        }
    }

    private JTable searchTable;
    private DefaultTableModel searchModel;

    private JPanel buildSearchPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(bgColor);
        bar.setBorder(new EmptyBorder(0, 0, 18, 0));
        txtSearch = makeField("Search by title, genre, or type...");
        JButton btnSearch = makePrimaryButton("Search");
        JButton btnAll    = makeOutlineButton("Show All");
        btnSearch.setPreferredSize(new Dimension(120, 38));
        btnAll.setPreferredSize(new Dimension(110, 38));
        btnSearch.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doSearch(); } });
        btnAll.addActionListener(new ActionListener()    { public void actionPerformed(ActionEvent e) { refreshSearchTable(); } });
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setBackground(bgColor);
        btnBar.add(btnSearch); btnBar.add(btnAll);
        bar.add(txtSearch, BorderLayout.CENTER);
        bar.add(btnBar, BorderLayout.EAST);
        outer.add(bar, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Type", "Genre", "Dewey Decimal", "Available"};
        searchModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        searchTable = makeStyledTable(searchModel);
        searchTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        searchTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        searchTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        outer.add(makeScrollPane(searchTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bot.setBackground(bgColor);
        bot.setBorder(new EmptyBorder(14, 0, 0, 0));
        JButton btnDel = makeOutlineButton("Delete Selected");
        btnDel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteSelectedBook(); } });
        bot.add(btnDel);
        outer.add(bot, BorderLayout.SOUTH);
        return outer;
    }

    private void refreshSearchTable() {
        searchModel.setRowCount(0);
        db database = new db();
        List<String> books = database.getAllBooks();
        database.closeConnection();
        for (String entry : books) {
            String[] parts = entry.split(" \\| ");
            String id = "", title = "", type = "", genre = "", ddc = "", avail = "";
            for (String p : parts) {
                if (p.startsWith("ID: "))              id    = p.replace("ID: ", "").trim();
                else if (p.startsWith("Title: "))      title = p.replace("Title: ", "").trim();
                else if (p.startsWith("Type: "))       type  = p.replace("Type: ", "").trim();
                else if (p.startsWith("Genre: "))      genre = p.replace("Genre: ", "").trim();
                else if (p.startsWith("DDC: "))        ddc   = p.replace("DDC: ", "").trim();
                else if (p.startsWith("Can Borrow: ")) avail = p.replace("Can Borrow: ", "").trim().equals("true") ? "Yes" : "No";
            }
            searchModel.addRow(new Object[]{id, title, type, genre, ddc, avail});
        }
        setStatus("Loaded " + searchModel.getRowCount() + " books.", successColor);
    }

    private void doSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { refreshSearchTable(); return; }
        searchModel.setRowCount(0);
        db database = new db();
        List<String> results = database.searchBook(kw);
        database.closeConnection();
        for (String entry : results) {
            String[] parts = entry.split(" \\| ");
            String id = "", title = "", type = "", genre = "", ddc = "";
            for (String p : parts) {
                if (p.startsWith("ID: "))          id    = p.replace("ID: ", "").trim();
                else if (p.startsWith("Title: "))  title = p.replace("Title: ", "").trim();
                else if (p.startsWith("Type: "))   type  = p.replace("Type: ", "").trim();
                else if (p.startsWith("Genre: "))  genre = p.replace("Genre: ", "").trim();
                else if (p.startsWith("DDC: "))    ddc   = p.replace("DDC: ", "").trim();
            }
            searchModel.addRow(new Object[]{id, title, type, genre, ddc, "—"});
        }
        setStatus("Found " + searchModel.getRowCount() + " result(s) for: " + kw, gold);
    }

    private void deleteSelectedBook() {
        int row = searchTable.getSelectedRow();
        if (row < 0) { setStatus("Select a book to delete.", warnColor); return; }
        String idStr = searchModel.getValueAt(row, 0).toString();
        String title = searchModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + title + "\"? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        db database = new db();
        boolean ok = database.deleteBook(Integer.parseInt(idStr));
        database.closeConnection();
        if (ok) { setStatus("Deleted: \"" + title + "\".", successColor); refreshSearchTable(); }
        else      setStatus("Delete failed. Check console.", errColor);
    }

    // ─────────────────────────────────────────────────────────────
    //  CHECKOUT PANEL  (wider card)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildCheckoutPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(bgColor);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(520, 340));   // wider

        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 4));
        JPanel s1 = new JPanel(); s1.setBackground(gold);
        JPanel s2 = new JPanel(); s2.setBackground(red);
        strip.add(s1); strip.add(s2);
        card.add(strip, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(24, 32, 28, 32));

        JLabel title = new JLabel("Checkout Book");
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(textColor);
        JLabel sub = new JLabel("Loan a book to a registered borrower. Due date is set to 14 days.");
        sub.setFont(new Font("Dialog", Font.PLAIN, 12));
        sub.setForeground(grayText);

        body.add(title);
        body.add(Box.createVerticalStrut(5));
        body.add(sub);
        body.add(Box.createVerticalStrut(18));
        body.add(new JSeparator() {{ setForeground(divColor); setMaximumSize(new Dimension(9999, 1)); }});
        body.add(Box.createVerticalStrut(18));

        txtCheckoutBookId     = makeField("Enter Book ID");
        txtCheckoutBorrowerId = makeField("Enter Borrower ID No.");
        txtCheckoutCondition  = makeField("e.g., Good, Fair, Poor");

        body.add(makeInlineRow("Book ID",        txtCheckoutBookId));     body.add(Box.createVerticalStrut(12));
        body.add(makeInlineRow("Borrower ID",    txtCheckoutBorrowerId)); body.add(Box.createVerticalStrut(12));
        body.add(makeInlineRow("Book Condition", txtCheckoutCondition));  body.add(Box.createVerticalStrut(24));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setBackground(cardColor);
        btns.setMaximumSize(new Dimension(9999, 48));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnCheckout = makePrimaryButton("Checkout");
        JButton btnClear    = makeOutlineButton("Clear");
        btnCheckout.setPreferredSize(new Dimension(140, 44));
        btnClear.setPreferredSize(new Dimension(110, 44));
        btnCheckout.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doCheckout(); } });
        btnClear.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
            txtCheckoutBookId.setText(""); txtCheckoutBorrowerId.setText(""); txtCheckoutCondition.setText("");
        }});
        btns.add(btnCheckout); btns.add(btnClear);
        body.add(btns);

        card.add(body, BorderLayout.CENTER);
        wrap.add(card);
        return wrap;
    }

    private void doCheckout() {
        String bookIdStr     = txtCheckoutBookId.getText().trim();
        String borrowerIdStr = txtCheckoutBorrowerId.getText().trim();
        String condition     = txtCheckoutCondition.getText().trim();
        if (bookIdStr.isEmpty() || borrowerIdStr.isEmpty()) { setStatus("Please fill in Book ID and Borrower ID.", warnColor); return; }
        if (condition.isEmpty()) condition = "Good";
        try {
            int bookId     = Integer.parseInt(bookIdStr);
            int borrowerId = Integer.parseInt(borrowerIdStr);
            db database = new db();
            if (!database.bookExists(bookId))        { setStatus("Book ID " + bookId + " not found.",        errColor); database.closeConnection(); return; }
            if (!database.borrowerExists(borrowerId)) { setStatus("Borrower ID " + borrowerId + " not found.", errColor); database.closeConnection(); return; }
            boolean ok = database.checkoutBook(bookId, borrowerId, condition);
            database.closeConnection();
            if (ok) {
                setStatus("Book ID " + bookId + " checked out to Borrower " + borrowerId + ". Due in 14 days.", successColor);
                txtCheckoutBookId.setText(""); txtCheckoutBorrowerId.setText(""); txtCheckoutCondition.setText("");
            } else {
                setStatus("Checkout failed — book may be unavailable or borrower reached their limit.", errColor);
            }
        } catch (NumberFormatException ex) {
            setStatus("Book ID and Borrower ID must be numbers.", warnColor);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  RETURN PANEL  (wider card)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildReturnPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(bgColor);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(520, 340));   // wider

        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 4));
        JPanel s1 = new JPanel(); s1.setBackground(gold);
        JPanel s2 = new JPanel(); s2.setBackground(red);
        strip.add(s1); strip.add(s2);
        card.add(strip, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(24, 32, 28, 32));

        JLabel title = new JLabel("Return Book");
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(textColor);
        JLabel sub = new JLabel("Process a book return and calculate any overdue fines.");
        sub.setFont(new Font("Dialog", Font.PLAIN, 12));
        sub.setForeground(grayText);
        body.add(title); body.add(Box.createVerticalStrut(5)); body.add(sub);
        body.add(Box.createVerticalStrut(18));
        body.add(new JSeparator() {{ setForeground(divColor); setMaximumSize(new Dimension(9999, 1)); }});
        body.add(Box.createVerticalStrut(18));

        txtReturnLoanId = makeField("Enter Loan ID");
        body.add(makeInlineRow("Loan ID", txtReturnLoanId));
        body.add(Box.createVerticalStrut(12));

        final JLabel lblFine = new JLabel(" ");
        lblFine.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblFine.setForeground(warnColor);
        lblFine.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblFine);
        body.add(Box.createVerticalStrut(12));

        JButton btnCheck = makeOutlineButton("Check Fine");
        btnCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCheck.setMaximumSize(new Dimension(9999, 40));
        btnCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idStr = txtReturnLoanId.getText().trim();
                if (idStr.isEmpty()) { lblFine.setText("Enter a Loan ID first."); return; }
                try {
                    db database = new db();
                    double fine = database.calculateOverdueFine(Integer.parseInt(idStr));
                    String borrowed = database.getDateBorrowed(Integer.parseInt(idStr));
                    database.closeConnection();
                    if (fine > 0) lblFine.setText("Fine: PHP " + String.format("%.2f", fine) + "  |  Borrowed: " + borrowed);
                    else          lblFine.setText("No fine. Borrowed: " + borrowed);
                } catch (NumberFormatException ex) { lblFine.setText("Loan ID must be a number."); }
            }
        });
        body.add(btnCheck);
        body.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setBackground(cardColor);
        btns.setMaximumSize(new Dimension(9999, 48));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnReturn = makePrimaryButton("Process Return");
        JButton btnClear  = makeOutlineButton("Clear");
        btnReturn.setPreferredSize(new Dimension(160, 44));
        btnClear.setPreferredSize(new Dimension(110, 44));
        btnReturn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doReturn(lblFine); } });
        btnClear.addActionListener(new ActionListener()  { public void actionPerformed(ActionEvent e) { txtReturnLoanId.setText(""); lblFine.setText(" "); } });
        btns.add(btnReturn); btns.add(btnClear);
        body.add(btns);

        card.add(body, BorderLayout.CENTER);
        wrap.add(card);
        return wrap;
    }

    private void doReturn(JLabel lblFine) {
        String idStr = txtReturnLoanId.getText().trim();
        if (idStr.isEmpty()) { setStatus("Enter a Loan ID.", warnColor); return; }
        try {
            int loanId = Integer.parseInt(idStr);
            db database = new db();
            double fine = database.calculateOverdueFine(loanId);
            boolean ok  = database.returnBook(loanId);
            database.closeConnection();
            if (ok) {
                String msg = fine > 0 ? "Book returned. Fine: PHP " + String.format("%.2f", fine) : "Book returned. No fine.";
                setStatus(msg, successColor);
                lblFine.setText(fine > 0 ? "Fine collected: PHP " + String.format("%.2f", fine) : "No fine.");
                txtReturnLoanId.setText("");
            } else setStatus("Return failed. Check Loan ID.", errColor);
        } catch (NumberFormatException ex) { setStatus("Loan ID must be a number.", warnColor); }
    }

    private JTable borrowersTable;
    private DefaultTableModel borrowersModel;
    private JTextField txtBorrName, txtBorrId, txtBorrSchool;
    private JComboBox<String> cmbBorrType;

    private JPanel buildBorrowersPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        addBar.setBackground(bgColor);
        addBar.setBorder(new EmptyBorder(0, 0, 16, 0));

        txtBorrName = makeField("Full Name");
        txtBorrName.setPreferredSize(new Dimension(200, 38));

        cmbBorrType = new JComboBox<>(new String[]{"Student", "Guest"});
        styleComboBox(cmbBorrType);
        cmbBorrType.setPreferredSize(new Dimension(110, 38));

        txtBorrId = makeField("ID No.");
        txtBorrId.setPreferredSize(new Dimension(140, 38));

        txtBorrSchool = makeField("School / Institution");
        txtBorrSchool.setPreferredSize(new Dimension(200, 38));
        txtBorrSchool.setVisible(false);

        JButton btnAdd = makePrimaryButton("+ Add Borrower");
        btnAdd.setPreferredSize(new Dimension(150, 38));

        cmbBorrType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isGuest = cmbBorrType.getSelectedItem().equals("Guest");
                txtBorrId.setEnabled(!isGuest);
                txtBorrId.setVisible(!isGuest);
                txtBorrSchool.setVisible(isGuest);
                if (isGuest) {
                    txtBorrId.setText("");
                    db database = new db();
                    int nextId = database.getNextGuestId();
                    database.closeConnection();
                    txtBorrId.setText(String.valueOf(nextId));
                }
                addBar.revalidate();
                addBar.repaint();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doAddBorrower(); }
        });

        addBar.add(makeLabel("Name:")); addBar.add(txtBorrName);
        addBar.add(makeLabel("Type:")); addBar.add(cmbBorrType);
        addBar.add(makeLabel("ID No.:")); addBar.add(txtBorrId);
        addBar.add(txtBorrSchool);
        addBar.add(btnAdd);
        outer.add(addBar, BorderLayout.NORTH);

        String[] cols = {"ID No.", "Name", "Type", "School", "Active Loans", "Can Borrow"};
        borrowersModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        borrowersTable = makeStyledTable(borrowersModel);
        borrowersTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        borrowersTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        borrowersTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        borrowersTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        borrowersTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        borrowersTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        outer.add(makeScrollPane(borrowersTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bot.setBackground(bgColor);
        bot.setBorder(new EmptyBorder(14, 0, 0, 0));
        JButton btnLoans = makeOutlineButton("View Active Loans");
        btnLoans.setPreferredSize(new Dimension(170, 36));
        btnLoans.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { viewActiveLoans(); }
        });
        bot.add(btnLoans);
        outer.add(bot, BorderLayout.SOUTH);
        return outer;
    }

    private void refreshBorrowersTable() {
        borrowersModel.setRowCount(0);
        db database = new db();
        List<String> list = database.getAllBorrowers();
        for (String entry : list) {
            String[] parts = entry.split(" \\| ");
            String idNo = "", name = "", type = "", school = "";
            for (String p : parts) {
                if (p.startsWith("ID: "))         idNo   = p.replace("ID: ", "").trim();
                else if (p.startsWith("Name: "))   name   = p.replace("Name: ", "").trim();
                else if (p.startsWith("Type: "))   type   = p.replace("Type: ", "").trim();
                else if (p.startsWith("School: ")) school = p.replace("School: ", "").trim();
            }
            int idNoInt = -1;
            try { idNoInt = Integer.parseInt(idNo); } catch (Exception ignored) {}
            int activeLoans = 0;
            boolean canBorrow = false;
            if (idNoInt > 0) {
                List<String> loans = database.getActiveLoansByBorrower(idNoInt);
                activeLoans = loans.size();
                canBorrow = database.canBorrow(idNoInt);
            }
            borrowersModel.addRow(new Object[]{idNo, name, type, school, activeLoans, canBorrow ? "Yes" : "No"});
        }
        database.closeConnection();
        setStatus("Loaded " + borrowersModel.getRowCount() + " borrowers.", successColor);
    }

    private void doAddBorrower() {
        String name   = txtBorrName.getText().trim();
        String idStr  = txtBorrId.getText().trim();
        String type   = cmbBorrType.getSelectedItem().toString();
        String school = txtBorrSchool.getText().trim();

        if (name.isEmpty()) { setStatus("Enter a name.", warnColor); return; }
        if (idStr.isEmpty()) { setStatus("Enter an ID No.", warnColor); return; }

        if (type.equals("Student")) {
            if (!idStr.matches("\\d{10}")) { setStatus("Student ID must be exactly 10 digits.", warnColor); return; }
            if (!idStr.startsWith("20"))   { setStatus("Student ID must start with '20'.", warnColor); return; }
        }
        if (type.equals("Guest") && school.isEmpty()) { setStatus("Please enter the guest's school or institution.", warnColor); return; }

        try {
            int idNo = Integer.parseInt(idStr);
            db database = new db();
            boolean ok = database.addBorrower(name, idNo, type, type.equals("Guest") ? school : null);
            database.closeConnection();
            if (ok) {
                setStatus(type + " \"" + name + "\" added successfully.", successColor);
                txtBorrName.setText(""); txtBorrSchool.setText("");
                cmbBorrType.setSelectedIndex(0);
                txtBorrId.setText(""); txtBorrId.setEnabled(true);
                txtBorrSchool.setVisible(false);
                refreshBorrowersTable();
            } else {
                setStatus("Failed to add borrower. ID may already exist.", errColor);
            }
        } catch (NumberFormatException ex) {
            setStatus("ID No. must be a number.", warnColor);
        }
    }

    private void viewActiveLoans() {
        int row = borrowersTable.getSelectedRow();
        if (row < 0) { setStatus("Select a borrower first.", warnColor); return; }
        String idStr = borrowersModel.getValueAt(row, 0).toString();
        String name  = borrowersModel.getValueAt(row, 1).toString();
        try {
            int idNo = Integer.parseInt(idStr);
            db database = new db();
            List<String> loans = database.getActiveLoansByBorrower(idNo);
            database.closeConnection();
            if (loans.isEmpty()) { JOptionPane.showMessageDialog(this, name + " has no active loans.", "Active Loans", JOptionPane.INFORMATION_MESSAGE); return; }
            StringBuilder sb = new StringBuilder("Active loans for " + name + ":\n\n");
            for (String l : loans) sb.append(l).append("\n");
            JOptionPane.showMessageDialog(this, sb.toString(), "Active Loans", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) { setStatus("Invalid ID.", errColor); }
    }

    private JLabel lblTotalBooks, lblAvailBooks, lblTotalBorrowers, lblActiveLoans, lblOverdue;

    // ─────────────────────────────────────────────────────────────
    //  DASHBOARD  (larger stat numbers)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildDashboardPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(32, 32, 32, 32));
        JLabel heading = new JLabel("Library Overview");
        heading.setFont(new Font("Georgia", Font.BOLD, 22));
        heading.setForeground(textColor);
        heading.setBorder(new EmptyBorder(0, 0, 22, 0));
        outer.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 18));
        grid.setBackground(bgColor);
        lblTotalBooks     = new JLabel("—"); lblAvailBooks    = new JLabel("—");
        lblTotalBorrowers = new JLabel("—"); lblActiveLoans   = new JLabel("—");
        lblOverdue        = new JLabel("—");
        grid.add(makeStatCard("Total Books",     lblTotalBooks,     gold));
        grid.add(makeStatCard("Available Now",   lblAvailBooks,     successColor));
        grid.add(makeStatCard("Total Borrowers", lblTotalBorrowers, textColor));
        grid.add(makeStatCard("Active Loans",    lblActiveLoans,    warnColor));
        grid.add(makeStatCard("Overdue Books",   lblOverdue,        errColor));
        grid.add(makeRefreshCard());
        outer.add(grid, BorderLayout.CENTER);
        return outer;
    }

    private JPanel makeStatCard(String title, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(divColor),
            new EmptyBorder(24, 28, 24, 28)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Dialog", Font.PLAIN, 12));
        t.setForeground(grayText);
        valueLabel.setFont(new Font("Georgia", Font.BOLD, 42));  // bigger stat number
        valueLabel.setForeground(valueColor);
        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeRefreshCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(divColor),
            new EmptyBorder(24, 28, 24, 28)));
        JButton btn = makePrimaryButton("Refresh Stats");
        btn.setPreferredSize(new Dimension(150, 46));
        btn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { refreshDashboard(); } });
        card.add(btn);
        return card;
    }

    private void refreshDashboard() {
        db database = new db();
        List<String> allBooks = database.getAllBooks();
        int total = allBooks.size();
        int avail = 0;
        for (String b : allBooks) if (b.contains("Can Borrow: true")) avail++;
        List<String> allBorrowers = database.getAllBorrowers();
        int totalBorrowers = allBorrowers.size();
        int activeLoans = 0;
        for (String b : allBorrowers) {
            String[] parts = b.split(" \\| ");
            for (String p : parts) {
                if (p.startsWith("ID: ")) {
                    try {
                        int idNo = Integer.parseInt(p.replace("ID: ", "").trim());
                        List<String> loans = database.getActiveLoansByBorrower(idNo);
                        activeLoans += loans.size();
                    } catch (Exception ignored) {}
                }
            }
        }
        int checkedOut = total - avail;
        database.closeConnection();
        lblTotalBooks.setText(String.valueOf(total));
        lblAvailBooks.setText(String.valueOf(avail));
        lblTotalBorrowers.setText(String.valueOf(totalBorrowers));
        lblActiveLoans.setText(String.valueOf(activeLoans));
        lblOverdue.setText(String.valueOf(checkedOut > activeLoans ? checkedOut - activeLoans : 0));
        setStatus("Dashboard refreshed.", successColor);
    }

    private JTextArea notifArea;

    private JPanel buildNotificationsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBackground(bgColor);
        top.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel heading = new JLabel("System Notifications & Overdue Alerts");
        heading.setFont(new Font("Georgia", Font.BOLD, 17));
        heading.setForeground(textColor);
        JPanel topBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topBtns.setBackground(bgColor);
        JButton btnScan    = makePrimaryButton("Scan Overdue");
        JButton btnRefresh = makeOutlineButton("Refresh");
        btnScan.setPreferredSize(new Dimension(140, 36));
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnScan.addActionListener(new ActionListener()    { public void actionPerformed(ActionEvent e) { scanOverdue(); } });
        btnRefresh.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { refreshNotifications(); } });
        topBtns.add(btnScan); topBtns.add(btnRefresh);
        top.add(heading, BorderLayout.CENTER);
        top.add(topBtns, BorderLayout.EAST);
        outer.add(top, BorderLayout.NORTH);

        notifArea = new JTextArea();
        notifArea.setEditable(false);
        notifArea.setBackground(tableBg);
        notifArea.setForeground(textColor);
        notifArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        notifArea.setBorder(new EmptyBorder(14, 16, 14, 16));
        JScrollPane scroll = new JScrollPane(notifArea);
        scroll.setBorder(BorderFactory.createLineBorder(divColor));
        scroll.getViewport().setBackground(tableBg);
        scroll.getVerticalScrollBar().setBackground(sidebarColor);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    private void refreshNotifications() {
        db database = new db();
        List<String> allBorrowers = database.getAllBorrowers();
        StringBuilder sb = new StringBuilder();
        for (String b : allBorrowers) {
            String[] parts = b.split(" \\| ");
            String idStr = "", name = "";
            for (String p : parts) {
                if (p.startsWith("ID: "))        idStr = p.replace("ID: ", "").trim();
                else if (p.startsWith("Name: ")) name  = p.replace("Name: ", "").trim();
            }
            try {
                int idNo = Integer.parseInt(idStr);
                List<String> notes = database.getNotificationsForBorrower(idNo);
                if (!notes.isEmpty()) {
                    sb.append("── ").append(name).append(" (ID: ").append(idNo).append(") ──\n");
                    for (String n : notes) sb.append("  ").append(n).append("\n");
                    sb.append("\n");
                }
            } catch (Exception ignored) {}
        }
        database.closeConnection();
        notifArea.setText(sb.length() == 0 ? "No notifications found." : sb.toString());
        setStatus("Notifications loaded.", successColor);
    }

    private void scanOverdue() {
        db database = new db();
        database.notifyOverdueBorrowers();
        database.closeConnection();
        setStatus("Overdue scan complete. Notifications sent.", warnColor);
        refreshNotifications();
    }

    public void addRecordsTyped(String mediaType, String genre, String dewey) {
        String title = txtTitle.getText().trim();
        if (title.isEmpty())  { setStatus("Title cannot be empty.", warnColor); return; }
        if (genre.isEmpty())  { setStatus("Genre cannot be empty.", warnColor); return; }
        if (mediaType.equals("Book") || mediaType.equals("ReferenceBook")) {
            if (dewey.isEmpty()) { setStatus("Dewey Decimal cannot be empty.", warnColor); return; }
            if (!dewey.matches("\\d{3}(\\.\\d+)?")) { setStatus("Dewey Decimal must be a valid number (e.g., 813.54).", warnColor); return; }
        }
        db database = new db();
        boolean ok = database.addBookTyped(title, mediaType, genre, dewey);
        database.closeConnection();
        if (ok) setStatus("Added " + mediaType + ": \"" + title + "\" successfully.", successColor);
        else     setStatus("Database error. Check console.", errColor);
    }

    public void addRecords() {
        String genre = txtGenre != null ? txtGenre.getText().trim() : "";
        String dewey = txtDewey != null ? txtDewey.getText().trim() : "";
        addRecordsTyped("Book", genre, dewey);
    }

    public void clearAddFields() {
        if (txtTitle != null) txtTitle.setText("");
        if (txtGenre != null) txtGenre.setText("");
        if (txtDewey != null) txtDewey.setText("");
        setStatus("Fields cleared.", grayText);
    }

    public void showAbout() {
        JDialog dlg = new JDialog(this, "About", true);
        dlg.setSize(420, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor);
        panel.setBorder(new EmptyBorder(28, 34, 28, 34));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        logoRow.setBackground(cardColor);
        if (iconEmblem != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconEmblem), 70, 52)));
        if (iconText   != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconText),  120, 42)));
        panel.add(logoRow, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(cardColor);
        info.setBorder(new EmptyBorder(20, 0, 14, 0));
        String[] lines = {"Mapua Cardinal Library — Makati Campus", "Library Management System v2.0", "", "Mapua University  |  Est. 1925"};
        for (String line : lines) {
            JLabel l = new JLabel(line.isEmpty() ? " " : line);
            l.setFont(line.contains("v2.0") || line.isEmpty()
                ? new Font("Dialog", Font.PLAIN, 13)
                : new Font("Georgia", Font.BOLD, 14));
            l.setForeground(line.contains("Est.") ? gold : textColor);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.add(l);
        }
        panel.add(info, BorderLayout.CENTER);

        JButton ok = makePrimaryButton("Close");
        ok.setPreferredSize(new Dimension(100, 38));
        ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { dlg.dispose(); } });
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(cardColor);
        btnRow.add(ok);
        panel.add(btnRow, BorderLayout.SOUTH);
        dlg.setContentPane(panel);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    //  SHARED HELPERS
    // ─────────────────────────────────────────────────────────────
    private JPanel makeInlineRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setBackground(cardColor);
        row.setMaximumSize(new Dimension(9999, 42));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Dialog", Font.BOLD, 12));
        lbl.setForeground(grayText);
        lbl.setPreferredSize(new Dimension(126, 42));
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private GridBagConstraints makeFC() {
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.anchor = GridBagConstraints.WEST;
        return fc;
    }

    private void addRow(JPanel p, GridBagConstraints fc, String labelText, JTextField field, int row) {
        fc.gridy = row; fc.gridx = 0; fc.weightx = 0; fc.gridwidth = 1; fc.insets = new Insets(8, 0, 8, 10);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Dialog", Font.BOLD, 12));
        lbl.setForeground(grayText);
        lbl.setPreferredSize(new Dimension(120, 22));
        p.add(lbl, fc);
        fc.gridx = 1; fc.weightx = 1; fc.gridwidth = 3; fc.insets = new Insets(8, 0, 8, 0);
        p.add(field, fc);
    }

    private JTextField makeField(final String placeholder) {
        JTextField tf = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(grayText);
                    g2.setFont(new Font("Dialog", Font.ITALIC, 13));
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int y = ins.top + (getHeight()-ins.top-ins.bottom-fm.getHeight())/2 + fm.getAscent();
                    g2.drawString(placeholder, ins.left + 4, y);
                    g2.dispose();
                }
            }
        };
        tf.setFont(new Font("Dialog", Font.PLAIN, 14));
        tf.setForeground(textColor);
        tf.setBackground(fieldBg);
        tf.setCaretColor(gold);
        tf.setPreferredSize(new Dimension(0, 42));   // taller fields
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldBorder, 1),
            new EmptyBorder(5, 12, 5, 12)));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(gold, 1),
                    new EmptyBorder(5, 12, 5, 12)));
                tf.repaint();
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fieldBorder, 1),
                    new EmptyBorder(5, 12, 5, 12)));
                tf.repaint();
            }
        });
        return tf;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 12));
        l.setForeground(grayText);
        return l;
    }

    private JTable makeStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(tableBg); table.setForeground(textColor);
        table.setGridColor(divColor);
        table.setRowHeight(34);   // taller rows
        table.setFont(new Font("Dialog", Font.PLAIN, 13));
        table.setSelectionBackground(cardColor); table.setSelectionForeground(gold);
        table.setShowHorizontalLines(true); table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(tableHeader);
        table.getTableHeader().setForeground(grayText);
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, divColor));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(sel ? cardColor : (r % 2 == 0 ? tableBg : tableAlt));
                setForeground(sel ? gold : textColor);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
        return table;
    }

    private JScrollPane makeScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(divColor));
        scroll.getViewport().setBackground(tableBg);
        scroll.getVerticalScrollBar().setBackground(sidebarColor);
        return scroll;
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? darkGold
                          : getModel().isRollover() ? new Color(220, 172, 32)
                          : gold);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setForeground(new Color(21, 7, 7));
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));
        return btn;
    }

    private JButton makeOutlineButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? divColor
                          : getModel().isRollover() ? panelColor
                          : cardColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.setColor(fieldBorder);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.PLAIN, 13));
        btn.setForeground(textColor);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(125, 40));
        return btn;
    }

    private JPanel makeCard(String title, String subtitle, int w, int h) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(w, h));
        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 4));
        JPanel g1 = new JPanel(); g1.setBackground(gold);
        JPanel g2 = new JPanel(); g2.setBackground(red);
        strip.add(g1); strip.add(g2);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cardColor);
        header.setBorder(new EmptyBorder(18, 30, 14, 30));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Georgia", Font.BOLD, 20)); t.setForeground(textColor);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Dialog", Font.PLAIN, 12)); s.setForeground(grayText);
        header.add(t, BorderLayout.NORTH); header.add(s, BorderLayout.SOUTH);
        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(cardColor);
        north.add(strip, BorderLayout.NORTH);
        north.add(header, BorderLayout.CENTER);
        north.add(new JSeparator() {{ setForeground(divColor); }}, BorderLayout.SOUTH);
        card.add(north, BorderLayout.NORTH);
        return card;
    }

    private void addFormToCard(JPanel card, JPanel form) { card.add(form, BorderLayout.SOUTH); }

    private void setStatus(String msg, Color color) { lblStatus.setText(msg); lblStatus.setForeground(color); }
}