package Library;

import DB.db;
import Library.Logic.DataValidation;
import Library.Logic.ExceptionHandler;

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

    // ── Palette ────────────────────────────────────────────────────────
    private Color bgColor     = new Color(13,  10,  10);
    private Color sidebarColor= new Color(18,   5,   5);
    private Color panelColor  = new Color(30,   8,   8);
    private Color cardColor   = new Color(34,  12,  12);
    private Color gold        = new Color(212, 160,  23);
    private Color darkGold    = new Color(154, 114,  10);
    private Color red         = new Color(190,  16,  16);
    private Color textColor   = new Color(245, 236, 220);
    private Color grayText    = new Color(136, 120, 104);
    private Color divColor    = new Color( 58,  20,  20);
    private Color successColor= new Color(107, 181, 114);
    private Color warnColor   = new Color(232, 168,  64);
    private Color errColor    = new Color(192,  80,  80);
    private Color fieldBg     = new Color( 16,   4,   4);
    private Color fieldBorder = new Color( 74,  24,  24);
    private Color tableBg     = new Color( 22,   7,   7);
    private Color tableAlt    = new Color( 28,   9,   9);
    private Color tableHeader = new Color( 50,  15,  15);

    // ── Shared validation instance ─────────────────────────────────────
    private final DataValidation validator = new DataValidation();

    // ── Form fields ────────────────────────────────────────────────────
    private JTextField txtTitle, txtGenre, txtDewey;
    private JTextField txtCheckoutBookId, txtCheckoutBorrowerId, txtCheckoutCondition;
    private JTextField txtReturnLoanId;
    private JTextField txtSearch;
    private JLabel     lblStatus;
    private JPanel     mainContent;
    private CardLayout cardLayout;
    private JPanel     activeNavBtn = null;

    private DefaultTableModel boardGameModel, dvdModel, magazineModel;
    private ImageIcon iconEmblem, iconText;

    private String currentRole      = null;
    private int    currentStudentId = -1;
    private static final String ADMIN_PASSWORD = "admin123";

    // ══════════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ══════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        EventQueue.invokeLater(() -> {
            try { new GUI().setVisible(true); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public GUI() {
        loadLogos();
        setTitle("Mapua Cardinal Library - Makati");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 660));
        setLocationRelativeTo(null);
        setBackground(bgColor);
        if (iconEmblem != null) setIconImage(iconEmblem.getImage());
        showLoginScreen();
    }

    // ══════════════════════════════════════════════════════════════════
    //  LOGIN SCREEN
    // ══════════════════════════════════════════════════════════════════
    private void showLoginScreen() {
        JPanel loginRoot = new JPanel(new GridBagLayout());
        loginRoot.setBackground(bgColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor  = GridBagConstraints.CENTER;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(divColor, 1),
                new EmptyBorder(40, 56, 44, 56)));
        card.setPreferredSize(new Dimension(520, 570));

        // Colour strip
        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setMaximumSize(new Dimension(9999, 4));
        strip.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel s1 = new JPanel(); s1.setBackground(gold);
        JPanel s2 = new JPanel(); s2.setBackground(red);
        strip.add(s1); strip.add(s2);
        card.add(strip);
        card.add(Box.createVerticalStrut(30));

        if (iconEmblem != null) {
            JLabel logo = new JLabel(scaleIcon(toBufferedImage(iconEmblem), 90, 70));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(logo);
            card.add(Box.createVerticalStrut(14));
        }

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
        card.add(sep);
        card.add(Box.createVerticalStrut(26));

        JLabel roleLabel = new JLabel("SELECT YOUR ROLE");
        roleLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        roleLabel.setForeground(grayText);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(roleLabel);
        card.add(Box.createVerticalStrut(8));

        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Student", "Guest", "Admin"});
        styleComboBox(cmbRole);
        cmbRole.setMaximumSize(new Dimension(9999, 42));
        card.add(cmbRole);
        card.add(Box.createVerticalStrut(18));

        // Student ID
        JLabel idLabel = new JLabel("STUDENT ID");
        idLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        idLabel.setForeground(grayText);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField txtLoginId = makeField("e.g. 2024101001");
        txtLoginId.setMaximumSize(new Dimension(9999, 42));

        // Admin password
        JLabel passLabel = new JLabel("ADMIN PASSWORD");
        passLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        passLabel.setForeground(grayText);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField txtPass = new JPasswordField();
        stylePasswordField(txtPass);
        txtPass.setMaximumSize(new Dimension(9999, 42));
        txtPass.setVisible(false);
        passLabel.setVisible(false);

        card.add(idLabel); card.add(Box.createVerticalStrut(7));
        card.add(txtLoginId); card.add(Box.createVerticalStrut(7));
        card.add(passLabel); card.add(Box.createVerticalStrut(7));
        card.add(txtPass); card.add(Box.createVerticalStrut(18));

        final JLabel errLabel = new JLabel(" ");
        errLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        errLabel.setForeground(errColor);
        errLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errLabel);
        card.add(Box.createVerticalStrut(10));

        JButton btnLogin = makePrimaryButton("Enter Library");
        btnLogin.setMaximumSize(new Dimension(9999, 48));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setFont(new Font("Dialog", Font.BOLD, 15));
        card.add(btnLogin);

        // Role switch
        cmbRole.addActionListener(e -> {
            String role   = cmbRole.getSelectedItem().toString();
            boolean isStudent = role.equals("Student");
            boolean isAdmin   = role.equals("Admin");
            idLabel.setVisible(isStudent);
            txtLoginId.setVisible(isStudent);
            passLabel.setVisible(isAdmin);
            txtPass.setVisible(isAdmin);
            errLabel.setText(" ");
            card.revalidate(); card.repaint();
        });

        // Login action — uses DataValidation for student ID
        ActionListener doLogin = e -> {
            String role = cmbRole.getSelectedItem().toString();
            errLabel.setText(" ");

            if (role.equals("Student")) {
                String idStr = txtLoginId.getText().trim();
                // ── Validate via DataValidation ──
                if (!validator.validateStudentId(idStr)) {
                    errLabel.setText(validator.getFirstUserMessage());
                    validator.logAllErrors();
                    return;
                }
                int idNo = Integer.parseInt(idStr);
                db database = new db();
                boolean exists = database.borrowerExists(idNo);
                database.closeConnection();
                if (!exists) {
                    ExceptionHandler ex = ExceptionHandler.borrowerNotFound(idNo);
                    ex.logError();
                    errLabel.setText(ex.getUserMessage());
                    return;
                }
                currentRole      = "Student";
                currentStudentId = idNo;

            } else if (role.equals("Admin")) {
                String pass = new String(txtPass.getPassword());
                if (!pass.equals(ADMIN_PASSWORD)) {
                    ExceptionHandler ex = new ExceptionHandler(
                            ExceptionHandler.ERR_INVALID_ID, "Incorrect admin password.",
                            ExceptionHandler.Severity.WARNING);
                    ex.logError();
                    errLabel.setText(ex.getUserMessage());
                    return;
                }
                currentRole = "Admin";

            } else {
                currentRole = "Guest";
            }
            buildMainUI();
        };

        btnLogin.addActionListener(doLogin);
        txtLoginId.addActionListener(doLogin);
        txtPass.addActionListener(doLogin);

        loginRoot.add(card, gbc);
        setContentPane(loginRoot);
        revalidate(); repaint();
    }

    // ══════════════════════════════════════════════════════════════════
    //  MAIN UI SHELL
    // ══════════════════════════════════════════════════════════════════
    private void buildMainUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        setContentPane(root);
        root.add(buildSidebar(),    BorderLayout.WEST);
        root.add(buildMainPanel(),  BorderLayout.CENTER);
        revalidate(); repaint();

        if (currentRole.equals("Admin")) {
            showCard("DASHBOARD");
            updateTopBar("Dashboard", "System / Dashboard");
            refreshDashboard();
        } else {
            showCard("CHECKOUT");
            updateTopBar("Checkout Book", "Borrowing / Checkout");
        }
    }

    // ── Logo loading ───────────────────────────────────────────────────
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
        java.util.List<String> paths = new java.util.ArrayList<>();
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
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) return scaleIcon(img, maxW, maxH);
                }
            } catch (Exception ignored) {}
        }
        try {
            InputStream is = getClass().getResourceAsStream("/" + filename);
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                is.close();
                return scaleIcon(img, maxW, maxH);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private ImageIcon scaleIcon(BufferedImage img, int maxW, int maxH) {
        if (img == null) return null;
        double scale = Math.min((double) maxW / img.getWidth(), (double) maxH / img.getHeight());
        return new ImageIcon(img.getScaledInstance(
                (int)(img.getWidth()*scale), (int)(img.getHeight()*scale), Image.SCALE_SMOOTH));
    }

    private BufferedImage toBufferedImage(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    // ══════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(255, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, divColor));
        sidebar.add(buildLogoArea());

        boolean isAdmin   = "Admin".equals(currentRole);
        boolean isStudent = "Student".equals(currentRole);

        if (isStudent || "Guest".equals(currentRole)) {
            sidebar.add(makeDividerLabel("BORROWING"));
            sidebar.add(makeNavBtn("  Checkout Book", "checkout"));
            sidebar.add(makeNavBtn("  Return Book",   "returnbook"));
        }

        sidebar.add(makeDividerLabel("CATALOG"));
        if (isAdmin) sidebar.add(makeNavBtn("  + Add New Book", "add"));
        sidebar.add(makeNavBtn("  Search Books", "search"));

        if (isAdmin) {
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
        ((JLabel) logoutBtn.getComponent(0)).setForeground(errColor);
        sidebar.add(logoutBtn);
        sidebar.add(buildSidebarFooter());
        return sidebar;
    }

    private JPanel buildLogoArea() {
        JPanel area = new JPanel();
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
        area.setBackground(sidebarColor);
        area.setMaximumSize(new Dimension(255, 230));
        area.setBorder(new EmptyBorder(22, 0, 14, 0));
        area.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (iconEmblem != null) {
            JLabel lbl = new JLabel(iconEmblem);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
            area.add(lbl);
        } else {
            JLabel fb = new JLabel("🏛");
            fb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
            fb.setForeground(gold); fb.setAlignmentX(Component.CENTER_ALIGNMENT);
            area.add(fb); area.add(Box.createVerticalStrut(6));
        }
        if (iconText != null) {
            JLabel lbl = new JLabel(iconText);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(0, 0, 3, 0));
            area.add(lbl);
        } else {
            JLabel lbl = new JLabel("Mapua University");
            lbl.setFont(new Font("Georgia", Font.BOLD, 14));
            lbl.setForeground(red); lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            area.add(lbl);
        }
        JLabel campus = new JLabel("Cardinal Library · Makati");
        campus.setFont(new Font("Georgia", Font.ITALIC, 11));
        campus.setForeground(grayText); campus.setAlignmentX(Component.CENTER_ALIGNMENT);
        area.add(campus); area.add(Box.createVerticalStrut(12));

        JPanel stripe = new JPanel(new GridLayout(1, 2));
        stripe.setMaximumSize(new Dimension(255, 3));
        JPanel g1 = new JPanel(); g1.setBackground(gold);
        JPanel g2 = new JPanel(); g2.setBackground(red);
        stripe.add(g1); stripe.add(g2);
        area.add(stripe);
        return area;
    }

    private JPanel buildSidebarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(sidebarColor);
        footer.setBorder(new EmptyBorder(12, 22, 18, 22));
        footer.setMaximumSize(new Dimension(255, 62));
        String sessionLabel = "Admin".equals(currentRole)   ? "ADMIN SESSION"
                            : "Student".equals(currentRole) ? "STUDENT SESSION"
                            : "GUEST SESSION";
        JLabel a = new JLabel(sessionLabel);
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
        return btn;
    }

    private void setActiveNav(JPanel btn, JLabel lbl) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(sidebarColor);
            activeNavBtn.setBorder(new EmptyBorder(12, 22, 12, 18));
            for (Component c : activeNavBtn.getComponents())
                if (c instanceof JLabel) ((JLabel)c).setForeground(textColor);
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
        switch (action) {
            case "add":            showCard("ADD");            updateTopBar("Add New Book",     "Catalog / Add Book");          break;
            case "search":         showCard("SEARCH");         updateTopBar("Search Books",     "Catalog / Search");            refreshSearchTable(); break;
            case "checkout":       showCard("CHECKOUT");       updateTopBar("Checkout Book",    "Borrowing / Checkout");        break;
            case "returnbook":     showCard("RETURN");         updateTopBar("Return Book",      "Borrowing / Return");          refreshActiveLoansReturnTable(); break;
            case "add_boardgame":  showCard("ADD_BOARDGAME");  updateTopBar("Board Game",       "Media / Board Game");          refreshMediaTable(boardGameModel, "BoardGame"); break;
            case "add_dvd":        showCard("ADD_DVD");        updateTopBar("DVD",              "Media / DVD");                 refreshMediaTable(dvdModel, "DVD"); break;
            case "add_magazine":   showCard("ADD_MAGAZINE");   updateTopBar("Magazine",         "Media / Magazine");            refreshMediaTable(magazineModel, "Magazine"); break;
            case "media_checkout": showCard("MEDIA_CHECKOUT"); updateTopBar("Checkout Media",   "Media / Checkout");            refreshMediaCheckout(); break;
            case "add_reference":  showCard("ADD_REFERENCE");  updateTopBar("Reference Book",   "Reference / Reference Book");  break;
            case "borrowers":      showCard("BORROWERS");      updateTopBar("Manage Borrowers", "People / Borrowers");          refreshBorrowersTable(); break;
            case "dashboard":      showCard("DASHBOARD");      updateTopBar("Dashboard",        "System / Dashboard");          refreshDashboard(); break;
            case "notifications":  showCard("NOTIFICATIONS");  updateTopBar("Notifications",    "System / Notifications");      refreshNotifications(); break;
            case "my_loans":       showCard("MY_LOANS");       updateTopBar("My Loans",         "My Account / Loans");          refreshMyLoans(); break;
            case "about":          showAbout(); break;
            case "logout":         currentRole = null; currentStudentId = -1; showLoginScreen(); break;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  MAIN PANEL / CARD LAYOUT
    // ══════════════════════════════════════════════════════════════════
    private JLabel lblPageTitle, lblCrumb;

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bgColor);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(panelColor);
        topBar.setPreferredSize(new Dimension(0, 62));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, red),
                new EmptyBorder(0, 28, 0, 28)));
        lblPageTitle = new JLabel("Checkout Book");
        lblPageTitle.setFont(new Font("Georgia", Font.BOLD, 20));
        lblPageTitle.setForeground(textColor);
        lblCrumb = new JLabel("Borrowing / Checkout");
        lblCrumb.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblCrumb.setForeground(grayText);
        topBar.add(lblPageTitle, BorderLayout.CENTER);
        topBar.add(lblCrumb,     BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        // Cards
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

        // Status bar
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

    // ══════════════════════════════════════════════════════════════════
    //  ADD BOOK PANEL  — validated through DataValidation
    // ══════════════════════════════════════════════════════════════════
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

        JPanel colGenre = new JPanel();
        colGenre.setLayout(new BoxLayout(colGenre, BoxLayout.Y_AXIS));
        colGenre.setBackground(cardColor);
        colGenre.add(makeFormLabel("Genre"));
        colGenre.add(Box.createVerticalStrut(8));
        txtGenre = makeField("e.g., Fiction, Science...");
        txtGenre.setMaximumSize(new Dimension(9999, 42));
        txtGenre.setAlignmentX(Component.LEFT_ALIGNMENT);
        colGenre.add(txtGenre);

        JPanel colDewey = new JPanel();
        colDewey.setLayout(new BoxLayout(colDewey, BoxLayout.Y_AXIS));
        colDewey.setBackground(cardColor);
        colDewey.add(makeFormLabel("Dewey Decimal"));
        colDewey.add(Box.createVerticalStrut(8));
        txtDewey = makeField("e.g., 813.54");
        txtDewey.setMaximumSize(new Dimension(9999, 42));
        txtDewey.setAlignmentX(Component.LEFT_ALIGNMENT);
        colDewey.add(txtDewey);

        twoCol.add(colGenre); twoCol.add(colDewey);
        body.add(twoCol);
        body.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("Books require a valid Dewey Decimal number (e.g., 813.54).");
        hint.setFont(new Font("Dialog", Font.ITALIC, 12));
        hint.setForeground(grayText);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint);
        body.add(Box.createVerticalStrut(26));
        body.add(makeSeparatorLine());
        body.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(cardColor);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(9999, 54));
        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear Fields");
        btnAdd.setPreferredSize(new Dimension(170, 44));
        btnClear.setPreferredSize(new Dimension(140, 44));

        btnAdd.addActionListener(e -> addRecordsTyped("Book",
                txtGenre.getText().trim(), txtDewey.getText().trim()));
        btnClear.addActionListener(e -> {
            txtTitle.setText(""); txtGenre.setText(""); txtDewey.setText("");
            setStatus("Fields cleared.", grayText);
        });
        btnRow.add(btnAdd); btnRow.add(btnClear);
        body.add(btnRow);

        card.add(body, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    // ══════════════════════════════════════════════════════════════════
    //  ADD RECORD — central method with full DataValidation bridge
    // ══════════════════════════════════════════════════════════════════
    public void addRecordsTyped(String mediaType, String genre, String dewey) {
        String title = txtTitle != null ? txtTitle.getText().trim() : "";

        // ── Validate via DataValidation ──────────────────────────────
        if (!validator.checkBookFields(title, genre, dewey, mediaType)) {
            validator.logAllErrors();
            // Show the first error in the status bar
            setStatus(validator.getFirstUserMessage(), errColor);
            // Highlight all errors in a tooltip-style dialog if there are multiple
            if (validator.errorCount() > 1) showValidationErrors(validator.getExceptionHandlers());
            return;
        }

        // ── Attempt DB insert ────────────────────────────────────────
        try {
            db database = new db();
            boolean ok = database.addBookTyped(title, mediaType, genre, dewey);
            database.closeConnection();
            if (ok) {
                setStatus("Added " + mediaType + ": \"" + title + "\" successfully.", successColor);
            } else {
                ExceptionHandler ex = ExceptionHandler.dbError("addBookTyped returned false.");
                ex.logError();
                setStatus(ex.getUserMessage(), errColor);
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError();
            setStatus(handler.getUserMessage(), errColor);
        }
    }

    /**
     * Shows a compact dialog listing all validation errors when there are more than one.
     */
    private void showValidationErrors(List<ExceptionHandler> errors) {
        StringBuilder sb = new StringBuilder("<html><b>Please fix the following:</b><ul>");
        for (ExceptionHandler e : errors) {
            sb.append("<li>").append(e.getUserMessage()).append("</li>");
        }
        sb.append("</ul></html>");
        JOptionPane.showMessageDialog(this, sb.toString(),
                "Validation Errors", JOptionPane.WARNING_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════
    //  CHECKOUT — validated through DataValidation + ExceptionHandler
    // ══════════════════════════════════════════════════════════════════
    private JLabel lblCheckoutBookInfo, lblCheckoutBorrowerInfo;

    private JPanel buildCheckoutPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel heading = new JLabel("Checkout Book");
        heading.setFont(new Font("Georgia", Font.BOLD, 20));
        heading.setForeground(textColor);
        heading.setBorder(new EmptyBorder(0, 0, 18, 0));
        outer.add(heading, BorderLayout.NORTH);

        JPanel cols = new JPanel(new GridLayout(1, 2, 20, 0));
        cols.setBackground(bgColor);

        // Left — form
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(cardColor);
        formCard.setBorder(BorderFactory.createLineBorder(divColor, 1));

        JPanel formAccent = new JPanel(new GridLayout(1, 2));
        formAccent.setPreferredSize(new Dimension(0, 4));
        JPanel fa1 = new JPanel(); fa1.setBackground(gold);
        JPanel fa2 = new JPanel(); fa2.setBackground(red);
        formAccent.add(fa1); formAccent.add(fa2);
        formCard.add(formAccent, BorderLayout.NORTH);

        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setBackground(cardColor);
        formBody.setBorder(new EmptyBorder(24, 28, 28, 28));

        JLabel formTitle = new JLabel("New Loan");
        formTitle.setFont(new Font("Georgia", Font.BOLD, 18)); formTitle.setForeground(textColor);
        JLabel formSub = new JLabel("Due date will be set to 14 days from today.");
        formSub.setFont(new Font("Dialog", Font.PLAIN, 12)); formSub.setForeground(grayText);
        formBody.add(formTitle); formBody.add(Box.createVerticalStrut(4));
        formBody.add(formSub);   formBody.add(Box.createVerticalStrut(20));
        formBody.add(makeSeparatorLine()); formBody.add(Box.createVerticalStrut(20));

        txtCheckoutBookId     = makeField("Enter Book ID");
        txtCheckoutBorrowerId = makeField("Enter Borrower ID No.");
        txtCheckoutCondition  = makeField("Good");

        txtCheckoutBookId.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateCheckoutPreview(); }
        });
        txtCheckoutBorrowerId.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateCheckoutPreview(); }
        });

        formBody.add(makeFormLabel("Book ID"));      formBody.add(Box.createVerticalStrut(6));
        txtCheckoutBookId.setMaximumSize(new Dimension(9999, 42));
        txtCheckoutBookId.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBody.add(txtCheckoutBookId);             formBody.add(Box.createVerticalStrut(16));

        formBody.add(makeFormLabel("Borrower ID"));  formBody.add(Box.createVerticalStrut(6));
        txtCheckoutBorrowerId.setMaximumSize(new Dimension(9999, 42));
        txtCheckoutBorrowerId.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBody.add(txtCheckoutBorrowerId);         formBody.add(Box.createVerticalStrut(16));

        formBody.add(makeFormLabel("Book Condition")); formBody.add(Box.createVerticalStrut(6));
        txtCheckoutCondition.setMaximumSize(new Dimension(9999, 42));
        txtCheckoutCondition.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBody.add(txtCheckoutCondition);          formBody.add(Box.createVerticalStrut(28));
        formBody.add(makeSeparatorLine());           formBody.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setBackground(cardColor);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        btns.setMaximumSize(new Dimension(9999, 48));
        JButton btnCheckout = makePrimaryButton("✔  Confirm Checkout");
        JButton btnClear    = makeOutlineButton("Clear");
        btnCheckout.setPreferredSize(new Dimension(180, 44));
        btnClear.setPreferredSize(new Dimension(100, 44));
        btnCheckout.addActionListener(e -> doCheckout());
        btnClear.addActionListener(e -> {
            txtCheckoutBookId.setText(""); txtCheckoutBorrowerId.setText("");
            txtCheckoutCondition.setText("Good"); clearCheckoutPreview();
        });
        btns.add(btnCheckout); btns.add(btnClear);
        formBody.add(btns);
        formCard.add(formBody, BorderLayout.CENTER);

        // Right — preview
        JPanel previewCard = new JPanel(new BorderLayout());
        previewCard.setBackground(cardColor);
        previewCard.setBorder(BorderFactory.createLineBorder(divColor, 1));

        JPanel previewHeader = new JPanel(new BorderLayout());
        previewHeader.setBackground(new Color(40, 14, 14));
        previewHeader.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel previewTitle = new JLabel("Loan Preview");
        previewTitle.setFont(new Font("Georgia", Font.BOLD, 15)); previewTitle.setForeground(gold);
        JLabel previewHint = new JLabel("Tab out of fields to preview");
        previewHint.setFont(new Font("Dialog", Font.ITALIC, 11)); previewHint.setForeground(grayText);
        previewHeader.add(previewTitle, BorderLayout.NORTH);
        previewHeader.add(previewHint,  BorderLayout.SOUTH);
        previewCard.add(previewHeader,  BorderLayout.NORTH);

        JPanel previewBody = new JPanel();
        previewBody.setLayout(new BoxLayout(previewBody, BoxLayout.Y_AXIS));
        previewBody.setBackground(cardColor);
        previewBody.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel bookLabel = new JLabel("BOOK");
        bookLabel.setFont(new Font("Dialog", Font.BOLD, 10)); bookLabel.setForeground(grayText);
        lblCheckoutBookInfo = new JLabel("<html><i>Enter a Book ID to preview</i></html>");
        lblCheckoutBookInfo.setFont(new Font("Dialog", Font.PLAIN, 13));
        lblCheckoutBookInfo.setForeground(textColor);
        lblCheckoutBookInfo.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel borrLabel = new JLabel("BORROWER");
        borrLabel.setFont(new Font("Dialog", Font.BOLD, 10)); borrLabel.setForeground(grayText);
        borrLabel.setBorder(new EmptyBorder(18, 0, 0, 0));
        lblCheckoutBorrowerInfo = new JLabel("<html><i>Enter a Borrower ID to preview</i></html>");
        lblCheckoutBorrowerInfo.setFont(new Font("Dialog", Font.PLAIN, 13));
        lblCheckoutBorrowerInfo.setForeground(textColor);
        lblCheckoutBorrowerInfo.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel dueLabel = new JLabel("DUE DATE");
        dueLabel.setFont(new Font("Dialog", Font.BOLD, 10)); dueLabel.setForeground(grayText);
        dueLabel.setBorder(new EmptyBorder(18, 0, 0, 0));
        java.time.LocalDate dueDate = java.time.LocalDate.now().plusDays(14);
        JLabel dueDateVal = new JLabel(dueDate.toString());
        dueDateVal.setFont(new Font("Georgia", Font.BOLD, 16)); dueDateVal.setForeground(gold);
        dueDateVal.setBorder(new EmptyBorder(6, 0, 0, 0));

        for (JLabel c : new JLabel[]{bookLabel, lblCheckoutBookInfo, borrLabel,
                                      lblCheckoutBorrowerInfo, dueLabel, dueDateVal}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            previewBody.add(c);
        }
        previewCard.add(previewBody, BorderLayout.CENTER);

        cols.add(formCard); cols.add(previewCard);
        outer.add(cols, BorderLayout.CENTER);
        return outer;
    }

    private void updateCheckoutPreview() {
        String bookIdStr = txtCheckoutBookId.getText().trim();
        String borrIdStr = txtCheckoutBorrowerId.getText().trim();
        try {
            db database = new db();
            if (!bookIdStr.isEmpty()) {
                // ── Validate book ID via DataValidation ──
                DataValidation v = new DataValidation();
                try {
                    int bookId = Integer.parseInt(bookIdStr);
                    if (!v.validateID(bookId)) {
                        lblCheckoutBookInfo.setText("<html><span style='color:#c05050'>"
                                + v.getFirstUserMessage() + "</span></html>");
                    } else {
                        String status = database.getBookStatus(bookId);
                        boolean avail = status.contains("Available") && !status.contains("Not Available");
                        lblCheckoutBookInfo.setText("<html>" + status + "</html>");
                        lblCheckoutBookInfo.setForeground(avail ? successColor : errColor);
                    }
                } catch (NumberFormatException ex) {
                    ExceptionHandler handler = ExceptionHandler.invalidId();
                    handler.logError();
                    lblCheckoutBookInfo.setText("<html><span style='color:#c05050'>"
                            + handler.getUserMessage() + "</span></html>");
                }
            }
            if (!borrIdStr.isEmpty()) {
                try {
                    int borrId = Integer.parseInt(borrIdStr);
                    DataValidation v = new DataValidation();
                    if (!v.validateID(borrId)) {
                        lblCheckoutBorrowerInfo.setText("<html><span style='color:#c05050'>"
                                + v.getFirstUserMessage() + "</span></html>");
                    } else if (database.borrowerExists(borrId)) {
                        boolean canBorrow = database.canBorrow(borrId);
                        List<String> loans = database.getActiveLoansByBorrower(borrId);
                        lblCheckoutBorrowerInfo.setText("<html>ID " + borrId
                                + " — " + loans.size() + " active loan(s)<br>"
                                + (canBorrow
                                    ? "<span style='color:green'>Can borrow</span>"
                                    : "<span style='color:red'>Borrow limit reached</span>")
                                + "</html>");
                        lblCheckoutBorrowerInfo.setForeground(textColor);
                    } else {
                        ExceptionHandler handler = ExceptionHandler.borrowerNotFound(borrId);
                        handler.logError();
                        lblCheckoutBorrowerInfo.setText("<html><span style='color:#c05050'>"
                                + handler.getUserMessage() + "</span></html>");
                    }
                } catch (NumberFormatException ex) {
                    ExceptionHandler handler = ExceptionHandler.invalidId();
                    handler.logError();
                    lblCheckoutBorrowerInfo.setText("<html><span style='color:#c05050'>"
                            + handler.getUserMessage() + "</span></html>");
                }
            }
            database.closeConnection();
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError();
        }
    }

    private void clearCheckoutPreview() {
        if (lblCheckoutBookInfo    != null) lblCheckoutBookInfo.setText("<html><i>Enter a Book ID to preview</i></html>");
        if (lblCheckoutBorrowerInfo != null) lblCheckoutBorrowerInfo.setText("<html><i>Enter a Borrower ID to preview</i></html>");
    }

    private void doCheckout() {
        String bookIdStr    = txtCheckoutBookId.getText().trim();
        String borrowerIdStr = txtCheckoutBorrowerId.getText().trim();
        String condition    = txtCheckoutCondition.getText().trim();

        // ── ID validation ────────────────────────────────────────────
        int bookId, borrowerId;
        try {
            bookId     = Integer.parseInt(bookIdStr);
            borrowerId = Integer.parseInt(borrowerIdStr);
        } catch (NumberFormatException ex) {
            ExceptionHandler handler = ExceptionHandler.invalidId();
            handler.logError();
            setStatus(handler.getUserMessage(), errColor);
            return;
        }

        DataValidation v = new DataValidation();
        if (!v.validateID(bookId)) {
            v.logAllErrors(); setStatus(v.getFirstUserMessage(), errColor); return;
        }
        if (!v.validateID(borrowerId)) {
            v.logAllErrors(); setStatus(v.getFirstUserMessage(), errColor); return;
        }
        if (condition.isEmpty()) condition = "Good";

        // ── DB checks ────────────────────────────────────────────────
        try {
            db database = new db();
            if (!database.bookExists(bookId)) {
                ExceptionHandler ex = ExceptionHandler.bookNotFound(bookId);
                ex.logError(); database.closeConnection();
                setStatus(ex.getUserMessage(), errColor); return;
            }
            if (!database.borrowerExists(borrowerId)) {
                ExceptionHandler ex = ExceptionHandler.borrowerNotFound(borrowerId);
                ex.logError(); database.closeConnection();
                setStatus(ex.getUserMessage(), errColor); return;
            }
            if (!database.canBorrow(borrowerId)) {
                ExceptionHandler ex = ExceptionHandler.borrowLimit();
                ex.logError(); database.closeConnection();
                setStatus(ex.getUserMessage(), warnColor); return;
            }
            boolean ok = database.checkoutBook(bookId, borrowerId, condition);
            database.closeConnection();
            if (ok) {
                setStatus("Book ID " + bookId + " checked out to Borrower "
                        + borrowerId + ". Due in 14 days.", successColor);
                txtCheckoutBookId.setText(""); txtCheckoutBorrowerId.setText("");
                txtCheckoutCondition.setText(""); clearCheckoutPreview();
            } else {
                ExceptionHandler ex = ExceptionHandler.bookUnavailable();
                ex.logError();
                setStatus(ex.getUserMessage(), errColor);
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError();
            setStatus(handler.getUserMessage(), errColor);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  RETURN — validated through ExceptionHandler
    // ══════════════════════════════════════════════════════════════════
    private DefaultTableModel activeLoansReturnModel;

    private JPanel buildReturnPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel heading = new JLabel("Return Book");
        heading.setFont(new Font("Georgia", Font.BOLD, 20));
        heading.setForeground(textColor);
        heading.setBorder(new EmptyBorder(0, 0, 18, 0));
        outer.add(heading, BorderLayout.NORTH);

        JPanel cols = new JPanel(new GridLayout(1, 2, 20, 0));
        cols.setBackground(bgColor);

        // Left — form
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(cardColor);
        formCard.setBorder(BorderFactory.createLineBorder(divColor, 1));

        JPanel formAccent = new JPanel(new GridLayout(1, 2));
        formAccent.setPreferredSize(new Dimension(0, 4));
        JPanel fa1 = new JPanel(); fa1.setBackground(gold);
        JPanel fa2 = new JPanel(); fa2.setBackground(red);
        formAccent.add(fa1); formAccent.add(fa2);
        formCard.add(formAccent, BorderLayout.NORTH);

        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setBackground(cardColor);
        formBody.setBorder(new EmptyBorder(24, 28, 28, 28));

        JLabel formTitle = new JLabel("Process Return");
        formTitle.setFont(new Font("Georgia", Font.BOLD, 18)); formTitle.setForeground(textColor);
        JLabel formSub = new JLabel("Enter the Loan ID to calculate any fine.");
        formSub.setFont(new Font("Dialog", Font.PLAIN, 12)); formSub.setForeground(grayText);
        formBody.add(formTitle); formBody.add(Box.createVerticalStrut(4));
        formBody.add(formSub);   formBody.add(Box.createVerticalStrut(20));
        formBody.add(makeSeparatorLine()); formBody.add(Box.createVerticalStrut(20));

        formBody.add(makeFormLabel("Loan ID")); formBody.add(Box.createVerticalStrut(6));
        txtReturnLoanId = makeField("Enter Loan ID");
        txtReturnLoanId.setMaximumSize(new Dimension(9999, 42));
        txtReturnLoanId.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBody.add(txtReturnLoanId); formBody.add(Box.createVerticalStrut(16));

        // Fine preview box
        JPanel fineBox = new JPanel(new BorderLayout());
        fineBox.setBackground(new Color(40, 14, 14));
        fineBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 20, 20), 1),
                new EmptyBorder(12, 16, 12, 16)));
        fineBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        fineBox.setMaximumSize(new Dimension(9999, 80));
        JLabel fineLabelTop = new JLabel("FINE CALCULATION");
        fineLabelTop.setFont(new Font("Dialog", Font.BOLD, 10)); fineLabelTop.setForeground(grayText);
        final JLabel lblFine = new JLabel("—  Enter Loan ID and click Check Fine");
        lblFine.setFont(new Font("Dialog", Font.PLAIN, 13)); lblFine.setForeground(textColor);
        fineBox.add(fineLabelTop, BorderLayout.NORTH);
        fineBox.add(lblFine,      BorderLayout.CENTER);
        formBody.add(fineBox); formBody.add(Box.createVerticalStrut(20));

        JButton btnCheck = makeOutlineButton("Check Fine");
        btnCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCheck.setMaximumSize(new Dimension(9999, 40));
        btnCheck.addActionListener(e -> {
            String idStr = txtReturnLoanId.getText().trim();
            // ── Validate Loan ID ──
            DataValidation v = new DataValidation();
            int lid;
            try {
                lid = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                ExceptionHandler handler = ExceptionHandler.invalidId();
                handler.logError();
                lblFine.setText(handler.getUserMessage());
                lblFine.setForeground(warnColor);
                return;
            }
            if (!v.validateID(lid)) {
                v.logAllErrors();
                lblFine.setText(v.getFirstUserMessage());
                lblFine.setForeground(warnColor);
                return;
            }
            try {
                db database = new db();
                double fine     = database.calculateOverdueFine(lid);
                String borrowed = database.getDateBorrowed(lid);
                String returned = database.getDateReturned(lid);
                database.closeConnection();
                if (borrowed.equals("N/A")) {
                    ExceptionHandler ex = new ExceptionHandler(ExceptionHandler.ERR_INVALID_ID,
                            "Loan ID not found.", ExceptionHandler.Severity.ERROR);
                    ex.logError();
                    lblFine.setText(ex.getUserMessage());
                    lblFine.setForeground(errColor);
                    return;
                }
                if (!returned.equals("Not yet returned")) {
                    lblFine.setText("Already returned on " + returned + ".");
                    lblFine.setForeground(warnColor);
                    return;
                }
                if (fine > 0) {
                    lblFine.setText("PHP " + String.format("%.2f", fine) + " overdue fine  |  Borrowed: " + borrowed);
                    lblFine.setForeground(errColor);
                } else {
                    lblFine.setText("No fine  |  Borrowed: " + borrowed);
                    lblFine.setForeground(successColor);
                }
            } catch (Exception ex) {
                ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
                handler.logError();
                lblFine.setText(handler.getUserMessage());
                lblFine.setForeground(errColor);
            }
        });
        formBody.add(btnCheck); formBody.add(Box.createVerticalStrut(24));
        formBody.add(makeSeparatorLine()); formBody.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setBackground(cardColor); btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        btns.setMaximumSize(new Dimension(9999, 48));
        JButton btnReturn = makePrimaryButton("✔  Process Return");
        JButton btnClear  = makeOutlineButton("Clear");
        btnReturn.setPreferredSize(new Dimension(170, 44));
        btnClear.setPreferredSize(new Dimension(100, 44));
        btnReturn.addActionListener(e -> { doReturn(lblFine); refreshActiveLoansReturnTable(); });
        btnClear.addActionListener(e -> {
            txtReturnLoanId.setText("");
            lblFine.setText("—  Enter Loan ID and click Check Fine");
            lblFine.setForeground(textColor);
        });
        btns.add(btnReturn); btns.add(btnClear);
        formBody.add(btns);
        formCard.add(formBody, BorderLayout.CENTER);

        // Right — active loans reference table
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(cardColor);
        tableCard.setBorder(BorderFactory.createLineBorder(divColor, 1));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(new Color(40, 14, 14));
        tableHeader.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel tableTitle = new JLabel("Active Loans Reference");
        tableTitle.setFont(new Font("Georgia", Font.BOLD, 15)); tableTitle.setForeground(gold);
        JLabel tableHint = new JLabel("Click a row to auto-fill Loan ID");
        tableHint.setFont(new Font("Dialog", Font.ITALIC, 11)); tableHint.setForeground(grayText);
        tableHeader.add(tableTitle, BorderLayout.NORTH);
        tableHeader.add(tableHint,  BorderLayout.SOUTH);
        tableCard.add(tableHeader,  BorderLayout.NORTH);

        String[] rCols = {"Loan ID", "Borrower", "Book Title", "Due Date", "Fine"};
        activeLoansReturnModel = new DefaultTableModel(rCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable returnRefTable = makeStyledTable(activeLoansReturnModel);
        returnRefTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        returnRefTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        returnRefTable.getColumnModel().getColumn(2).setPreferredWidth(230);
        returnRefTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        returnRefTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        returnRefTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String fine = activeLoansReturnModel.getValueAt(r, 4).toString();
                boolean overdue = !fine.equals("—") && !fine.equals("0.00");
                if (sel)          { setBackground(cardColor); setForeground(gold); }
                else if (overdue) { setBackground(new Color(50, 10, 10)); setForeground(errColor); }
                else              { setBackground(r % 2 == 0 ? tableBg : tableAlt); setForeground(textColor); }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        returnRefTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = returnRefTable.getSelectedRow();
            if (row >= 0) {
                txtReturnLoanId.setText(activeLoansReturnModel.getValueAt(row, 0).toString());
                lblFine.setText("—  Click Check Fine to calculate");
                lblFine.setForeground(textColor);
            }
        });

        tableCard.add(makeScrollPane(returnRefTable), BorderLayout.CENTER);
        cols.add(formCard); cols.add(tableCard);
        outer.add(cols, BorderLayout.CENTER);
        return outer;
    }

    private void refreshActiveLoansReturnTable() {
        if (activeLoansReturnModel == null) return;
        activeLoansReturnModel.setRowCount(0);
        try {
            db database = new db();
            List<String> loans = database.getAllActiveLoans();
            database.closeConnection();
            java.util.Date today = new java.util.Date();
            for (String loan : loans) {
                String loanId = "", borrowerName = "", title = "", due = "";
                for (String p : loan.split(" \\| ")) {
                    if      (p.startsWith("LoanID: "))       loanId       = p.replace("LoanID: ", "").trim();
                    else if (p.startsWith("BorrowerName: ")) borrowerName = p.replace("BorrowerName: ", "").trim();
                    else if (p.startsWith("Title: "))        title        = p.replace("Title: ", "").trim();
                    else if (p.startsWith("Due: "))          due          = p.replace("Due: ", "").trim();
                }
                String fine = "—";
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    long days = (sdf.parse(sdf.format(today)).getTime() - sdf.parse(due).getTime())
                                / (1000 * 60 * 60 * 24);
                    if (days > 0) fine = String.format("%.2f", days * 5.0);
                } catch (Exception ignored) {}
                activeLoansReturnModel.addRow(new Object[]{loanId, borrowerName, title, due, fine});
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError();
            setStatus(handler.getUserMessage(), errColor);
        }
    }

    private void doReturn(JLabel lblFine) {
        String idStr = txtReturnLoanId.getText().trim();
        DataValidation v = new DataValidation();
        int loanId;
        try {
            loanId = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            ExceptionHandler handler = ExceptionHandler.invalidId();
            handler.logError();
            setStatus(handler.getUserMessage(), warnColor);
            return;
        }
        if (!v.validateID(loanId)) {
            v.logAllErrors(); setStatus(v.getFirstUserMessage(), warnColor); return;
        }
        try {
            db database = new db();
            double fine = database.calculateOverdueFine(loanId);
            boolean ok  = database.returnBook(loanId);
            database.closeConnection();
            if (ok) {
                String msg = fine > 0
                        ? "Book returned. Fine: PHP " + String.format("%.2f", fine)
                        : "Book returned. No fine.";
                setStatus(msg, successColor);
                lblFine.setText(fine > 0 ? "Fine collected: PHP " + String.format("%.2f", fine) : "No fine.");
                txtReturnLoanId.setText("");
            } else {
                ExceptionHandler ex = new ExceptionHandler(ExceptionHandler.ERR_INVALID_ID,
                        "Return failed. Verify the Loan ID.", ExceptionHandler.Severity.ERROR);
                ex.logError();
                setStatus(ex.getUserMessage(), errColor);
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError();
            setStatus(handler.getUserMessage(), errColor);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  BORROWERS PANEL  (Add Borrower validated through DataValidation)
    // ══════════════════════════════════════════════════════════════════
    private JTable borrowersTable;
    private DefaultTableModel borrowersModel, loansModel;
    private JTextField txtBorrName, txtBorrId, txtBorrSchool;
    private JComboBox<String> cmbBorrType;
    private JLabel lblLoansHeader;

    private JPanel buildBorrowersPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(20, 32, 20, 32));

        // Add-borrower bar
        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        addBar.setBackground(bgColor);
        addBar.setBorder(new EmptyBorder(0, 0, 12, 0));

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

        cmbBorrType.addActionListener(e -> {
            boolean isGuest = cmbBorrType.getSelectedItem().equals("Guest");
            txtBorrSchool.setVisible(isGuest);
            if (isGuest) {
                txtBorrId.setText(""); txtBorrId.setEnabled(false);
                try {
                    db database = new db();
                    txtBorrId.setText(String.valueOf(database.getNextGuestId()));
                    database.closeConnection();
                } catch (Exception ex) { txtBorrId.setText("10001"); }
            } else {
                txtBorrId.setText(""); txtBorrId.setEnabled(true);
            }
            addBar.revalidate(); addBar.repaint();
        });

        btnAdd.addActionListener(e -> doAddBorrower());

        addBar.add(makeLabel("Name:"));    addBar.add(txtBorrName);
        addBar.add(makeLabel("Type:"));    addBar.add(cmbBorrType);
        addBar.add(makeLabel("ID No.:")); addBar.add(txtBorrId);
        addBar.add(txtBorrSchool);
        addBar.add(btnAdd);
        outer.add(addBar, BorderLayout.NORTH);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setBackground(bgColor); split.setDividerSize(6);
        split.setResizeWeight(0.45);  split.setBorder(null);

        String[] bCols = {"ID No.", "Name", "Type", "School", "Active Loans", "Can Borrow"};
        borrowersModel = new DefaultTableModel(bCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        borrowersTable = makeStyledTable(borrowersModel);
        borrowersTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        borrowersTable.getColumnModel().getColumn(1).setPreferredWidth(190);
        borrowersTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        borrowersTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        borrowersTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        borrowersTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        borrowersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel topCard = new JPanel(new BorderLayout());
        topCard.setBackground(cardColor);
        topCard.setBorder(BorderFactory.createLineBorder(divColor, 1));
        JLabel topTitle = new JLabel("  Registered Borrowers");
        topTitle.setFont(new Font("Dialog", Font.BOLD, 12)); topTitle.setForeground(gold);
        topTitle.setBorder(new EmptyBorder(10, 12, 10, 12));
        topCard.add(topTitle, BorderLayout.NORTH);
        topCard.add(makeScrollPane(borrowersTable), BorderLayout.CENTER);
        split.setTopComponent(topCard);

        String[] lCols = {"Loan ID", "Borrower ID", "Borrower Name", "Book Title",
                          "Date Borrowed", "Due Date", "Status"};
        loansModel = new DefaultTableModel(lCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable loansTable = makeStyledTable(loansModel);
        loansTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        loansTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        loansTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        loansTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        loansTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        loansTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        loansTable.getColumnModel().getColumn(6).setPreferredWidth(90);

        loansTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String status = loansModel.getValueAt(r, 6) != null
                        ? loansModel.getValueAt(r, 6).toString() : "";
                if (sel)                   { setBackground(cardColor); setForeground(gold); }
                else if (status.equals("OVERDUE"))   { setBackground(new Color(60, 10, 10)); setForeground(errColor); }
                else if (status.equals("Due Today")) { setBackground(new Color(50, 28,  5)); setForeground(warnColor); }
                else                       { setBackground(r % 2 == 0 ? tableBg : tableAlt); setForeground(textColor); }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });

        JPanel bottomCard = new JPanel(new BorderLayout());
        bottomCard.setBackground(cardColor);
        bottomCard.setBorder(BorderFactory.createLineBorder(divColor, 1));

        JPanel loansHeader = new JPanel(new BorderLayout());
        loansHeader.setBackground(cardColor);
        loansHeader.setBorder(new EmptyBorder(10, 12, 10, 12));
        lblLoansHeader = new JLabel("  Active Loans — All Borrowers");
        lblLoansHeader.setFont(new Font("Dialog", Font.BOLD, 12)); lblLoansHeader.setForeground(grayText);

        JButton btnShowAll = makeOutlineButton("Show All");
        btnShowAll.setPreferredSize(new Dimension(100, 30));
        btnShowAll.addActionListener(e -> {
            borrowersTable.clearSelection();
            refreshAllLoansTable();
            lblLoansHeader.setText("  Active Loans — All Borrowers");
            lblLoansHeader.setForeground(grayText);
        });
        loansHeader.add(lblLoansHeader, BorderLayout.CENTER);
        loansHeader.add(btnShowAll,     BorderLayout.EAST);
        bottomCard.add(loansHeader, BorderLayout.NORTH);
        bottomCard.add(makeScrollPane(loansTable), BorderLayout.CENTER);
        split.setBottomComponent(bottomCard);

        outer.add(split, BorderLayout.CENTER);

        borrowersTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = borrowersTable.getSelectedRow();
            if (row < 0) return;
            String idStr = borrowersModel.getValueAt(row, 0).toString();
            String name  = borrowersModel.getValueAt(row, 1).toString();
            try { refreshLoansTableForBorrower(Integer.parseInt(idStr), name); }
            catch (NumberFormatException ignored) {}
        });

        return outer;
    }

    private void doAddBorrower() {
        String name   = txtBorrName.getText().trim();
        String idStr  = txtBorrId.getText().trim();
        String type   = cmbBorrType.getSelectedItem().toString();
        String school = txtBorrSchool.getText().trim();

        // ── Validate name ────────────────────────────────────────────
        if (!validator.validateInput(name)) {
            validator.logAllErrors();
            setStatus("Name: " + validator.getFirstUserMessage(), warnColor);
            return;
        }

        // ── Validate ID based on type ────────────────────────────────
        if (type.equals("Student")) {
            if (!validator.validateStudentId(idStr)) {
                validator.logAllErrors();
                setStatus(validator.getFirstUserMessage(), warnColor);
                return;
            }
        } else {
            // Guest — just needs to be a positive integer
            int guestId;
            try { guestId = Integer.parseInt(idStr); }
            catch (NumberFormatException ex) {
                ExceptionHandler handler = ExceptionHandler.invalidId();
                handler.logError(); setStatus(handler.getUserMessage(), warnColor); return;
            }
            if (!validator.validateID(guestId)) {
                validator.logAllErrors(); setStatus(validator.getFirstUserMessage(), warnColor); return;
            }
        }

        if (type.equals("Guest") && school.isEmpty()) {
            setStatus("Please enter the guest's school or institution.", warnColor); return;
        }

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
                ExceptionHandler ex = ExceptionHandler.dbError("ID may already exist.");
                ex.logError();
                setStatus(ex.getUserMessage(), errColor);
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError();
            setStatus(handler.getUserMessage(), errColor);
        }
    }

    private void refreshBorrowersTable() {
        borrowersModel.setRowCount(0);
        try {
            db database = new db();
            List<String> list = database.getAllBorrowers();
            for (String entry : list) {
                String idNo = "", name = "", type = "", school = "";
                for (String p : entry.split(" \\| ")) {
                    if      (p.startsWith("ID: "))     idNo   = p.replace("ID: ", "").trim();
                    else if (p.startsWith("Name: "))   name   = p.replace("Name: ", "").trim();
                    else if (p.startsWith("Type: "))   type   = p.replace("Type: ", "").trim();
                    else if (p.startsWith("School: ")) school = p.replace("School: ", "").trim();
                }
                int idNoInt = -1;
                try { idNoInt = Integer.parseInt(idNo); } catch (Exception ignored) {}
                int     activeLoans = 0;
                boolean canBorrow   = false;
                if (idNoInt > 0) {
                    activeLoans = database.getActiveLoansByBorrower(idNoInt).size();
                    canBorrow   = database.canBorrow(idNoInt);
                }
                borrowersModel.addRow(new Object[]{idNo, name, type, school,
                        activeLoans, canBorrow ? "Yes" : "No"});
            }
            database.closeConnection();
            refreshAllLoansTable();
            setStatus("Loaded " + borrowersModel.getRowCount() + " borrowers.", successColor);
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
    }

    private void refreshAllLoansTable() {
        if (loansModel == null) return;
        loansModel.setRowCount(0);
        try {
            db database = new db();
            List<String> allLoans = database.getAllActiveLoans();
            database.closeConnection();
            java.util.Date today = new java.util.Date();
            for (String loan : allLoans) {
                Object[] row = parseLoanRow(loan, today);
                if (row != null) loansModel.addRow(row);
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
        if (lblLoansHeader != null)
            lblLoansHeader.setText("  Active Loans — All Borrowers  (" + loansModel.getRowCount() + ")");
    }

    private void refreshLoansTableForBorrower(int borrowerId, String name) {
        if (loansModel == null) return;
        loansModel.setRowCount(0);
        try {
            db database = new db();
            List<String> loans = database.getActiveLoansByBorrower(borrowerId);
            database.closeConnection();
            java.util.Date today = new java.util.Date();
            for (String loan : loans) {
                String loanId = "", title = "", borrowed = "", due = "";
                for (String p : loan.split(" \\| ")) {
                    if      (p.startsWith("LoanID: "))   loanId   = p.replace("LoanID: ", "").trim();
                    else if (p.startsWith("Title: "))    title    = p.replace("Title: ", "").trim();
                    else if (p.startsWith("Borrowed: ")) borrowed = p.replace("Borrowed: ", "").trim();
                    else if (p.startsWith("Due: "))      due      = p.replace("Due: ", "").trim();
                }
                loansModel.addRow(new Object[]{loanId, borrowerId, name, title,
                        borrowed, due, computeStatus(due, today)});
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
        if (lblLoansHeader != null) {
            int count = loansModel.getRowCount();
            lblLoansHeader.setText("  Active Loans — " + name + "  (" + count + ")");
            lblLoansHeader.setForeground(count > 0 ? gold : grayText);
        }
    }

    private Object[] parseLoanRow(String loan, java.util.Date today) {
        try {
            String loanId = "", borrowerId = "", borrowerName = "", title = "", borrowed = "", due = "";
            for (String p : loan.split(" \\| ")) {
                if      (p.startsWith("LoanID: "))         loanId       = p.replace("LoanID: ", "").trim();
                else if (p.startsWith("BorrowerID: "))     borrowerId   = p.replace("BorrowerID: ", "").trim();
                else if (p.startsWith("BorrowerName: "))   borrowerName = p.replace("BorrowerName: ", "").trim();
                else if (p.startsWith("Title: "))          title        = p.replace("Title: ", "").trim();
                else if (p.startsWith("Borrowed: "))       borrowed     = p.replace("Borrowed: ", "").trim();
                else if (p.startsWith("Due: "))            due          = p.replace("Due: ", "").trim();
            }
            return new Object[]{loanId, borrowerId, borrowerName, title,
                    borrowed, due, computeStatus(due, today)};
        } catch (Exception e) { return null; }
    }

    private String computeStatus(String dueDateStr, java.util.Date today) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            long days = (sdf.parse(dueDateStr).getTime() - sdf.parse(sdf.format(today)).getTime())
                        / (1000 * 60 * 60 * 24);
            if (days < 0)  return "OVERDUE";
            if (days == 0) return "Due Today";
            return "Active";
        } catch (Exception e) { return "Active"; }
    }

    // ══════════════════════════════════════════════════════════════════
    //  REMAINING PANELS  (unchanged in structure; errors wired to handler)
    // ══════════════════════════════════════════════════════════════════

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

        final Runnable loadAll = () -> {
            model.setRowCount(0);
            try {
                db database = new db();
                List<String> rows = database.getAllBooks();
                database.closeConnection();
                for (String row : rows) {
                    String[] parts = parseBookRow(row);
                    if (parts != null && parts[2].equals(mediaType))
                        model.addRow(new Object[]{parts[0], parts[1], parts[3],
                                parts[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"});
                }
            } catch (Exception ex) {
                ExceptionHandler h = ExceptionHandler.dbError(ex.getMessage());
                h.logError(); setStatus(h.getUserMessage(), errColor);
            }
        };

        btnSearch.addActionListener(e -> {
            String kw = fSearch.getText().trim().toLowerCase();
            if (kw.isEmpty()) { loadAll.run(); return; }
            model.setRowCount(0);
            try {
                db database = new db();
                List<String> rows = database.getAllBooks();
                database.closeConnection();
                for (String row : rows) {
                    String[] parts = parseBookRow(row);
                    if (parts != null && parts[2].equals(mediaType)
                            && (parts[1].toLowerCase().contains(kw) || parts[3].toLowerCase().contains(kw)))
                        model.addRow(new Object[]{parts[0], parts[1], parts[3],
                                parts[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"});
                }
            } catch (Exception ex) {
                ExceptionHandler h = ExceptionHandler.dbError(ex.getMessage());
                h.logError(); setStatus(h.getUserMessage(), errColor);
            }
        });
        btnAll.addActionListener(e -> { fSearch.setText(""); loadAll.run(); });
        fSearch.addActionListener(e -> btnSearch.doClick());
        return outer;
    }

    private void refreshMediaTable(DefaultTableModel model, String mediaType) {
        if (model == null) return;
        model.setRowCount(0);
        try {
            db database = new db();
            List<String> rows = database.getAllBooks();
            database.closeConnection();
            for (String row : rows) {
                String[] parts = parseBookRow(row);
                if (parts != null && parts[2].equals(mediaType))
                    model.addRow(new Object[]{parts[0], parts[1], parts[3],
                            parts[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"});
            }
        } catch (Exception ex) {
            ExceptionHandler h = ExceptionHandler.dbError(ex.getMessage());
            h.logError(); setStatus(h.getUserMessage(), errColor);
        }
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
        outer.add(topBar,   BorderLayout.NORTH);

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

        final Runnable refreshPills = () -> {
            for (int i = 0; i < filterTypes.length; i++) {
                boolean active = filterTypes[i].equals(activeFilter[0]);
                pills[i].setBackground(active ? gold : fieldBg);
                pills[i].setBorder(active
                        ? BorderFactory.createLineBorder(gold, 1)
                        : BorderFactory.createLineBorder(fieldBorder, 1));
                pillLbls[i].setForeground(active ? new Color(21, 7, 7) : grayText);
            }
        };
        refreshPills.run();

        final Runnable loadFiltered = () -> {
            mediaCheckoutModel.setRowCount(0);
            String kw = fSearch.getText().trim().toLowerCase();
            try {
                db database = new db();
                List<String> rows = database.getAllBooks();
                database.closeConnection();
                for (String row : rows) {
                    String[] p = parseBookRow(row);
                    if (p == null) continue;
                    String type = p[2];
                    if (!type.equals("BoardGame") && !type.equals("DVD") && !type.equals("Magazine")) continue;
                    if (!activeFilter[0].equals("All") && !type.equals(activeFilter[0])) continue;
                    if (!kw.isEmpty() && !p[1].toLowerCase().contains(kw) && !p[3].toLowerCase().contains(kw)) continue;
                    String label = type.equals("BoardGame") ? "Board Game" : type;
                    mediaCheckoutModel.addRow(new Object[]{p[0], p[1], label, p[3],
                            p[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"});
                }
            } catch (Exception ex) {
                ExceptionHandler h = ExceptionHandler.dbError(ex.getMessage());
                h.logError(); setStatus(h.getUserMessage(), errColor);
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

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) fSelId.setText(mediaCheckoutModel.getValueAt(row, 0).toString());
        });

        btnSearch.addActionListener(e -> loadFiltered.run());
        fSearch.addActionListener(e -> loadFiltered.run());

        btnCheckout.addActionListener(e -> {
            String selId  = fSelId.getText().trim();
            String borrId = txtMediaBorrowerId.getText().trim();
            String cond   = txtMediaCondition.getText().trim();
            if (selId.isEmpty() || selId.equals("—")) { setStatus("Select an item from the list.", warnColor); return; }

            // ── Validate IDs ──
            int bookId, borrowerId;
            try {
                bookId     = Integer.parseInt(selId);
                borrowerId = Integer.parseInt(borrId);
            } catch (NumberFormatException ex) {
                ExceptionHandler handler = ExceptionHandler.invalidId();
                handler.logError(); setStatus(handler.getUserMessage(), warnColor); return;
            }
            DataValidation v = new DataValidation();
            if (!v.validateID(borrowerId)) { v.logAllErrors(); setStatus(v.getFirstUserMessage(), warnColor); return; }
            if (cond.isEmpty()) cond = "Good";

            try {
                db database = new db();
                if (!database.borrowerExists(borrowerId)) {
                    ExceptionHandler ex = ExceptionHandler.borrowerNotFound(borrowerId);
                    ex.logError(); database.closeConnection();
                    setStatus(ex.getUserMessage(), errColor); return;
                }
                boolean ok = database.checkoutBook(bookId, borrowerId, cond);
                database.closeConnection();
                if (ok) {
                    setStatus("Item ID " + bookId + " checked out to Borrower " + borrowerId + ". Due in 14 days.", successColor);
                    fSelId.setText("—"); txtMediaBorrowerId.setText(""); txtMediaCondition.setText("Good");
                    loadFiltered.run();
                } else {
                    ExceptionHandler ex = ExceptionHandler.bookUnavailable();
                    ex.logError(); setStatus(ex.getUserMessage(), errColor);
                }
            } catch (Exception ex) {
                ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
                handler.logError(); setStatus(handler.getUserMessage(), errColor);
            }
        });

        btnClear.addActionListener(e -> {
            fSelId.setText("—"); txtMediaBorrowerId.setText(""); txtMediaCondition.setText("Good");
            table.clearSelection();
        });
        return outer;
    }

    private void refreshMediaCheckout() {
        if (mediaCheckoutModel == null) return;
        mediaCheckoutModel.setRowCount(0);
        try {
            db database = new db();
            List<String> rows = database.getAllBooks();
            database.closeConnection();
            for (String row : rows) {
                String[] p = parseBookRow(row);
                if (p == null) continue;
                String type = p[2];
                if (!type.equals("BoardGame") && !type.equals("DVD") && !type.equals("Magazine")) continue;
                String label = type.equals("BoardGame") ? "Board Game" : type;
                mediaCheckoutModel.addRow(new Object[]{p[0], p[1], label, p[3],
                        p[5].equalsIgnoreCase("true") ? "✔  Yes" : "✘  No"});
            }
        } catch (Exception ex) {
            ExceptionHandler h = ExceptionHandler.dbError(ex.getMessage());
            h.logError(); setStatus(h.getUserMessage(), errColor);
        }
    }

    private JPanel buildAddRefPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(32, 40, 32, 40));

        JPanel card = buildAddCard("Add Reference Book",
                "Reference books are for in-library use only and cannot be borrowed.");
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(24, 36, 28, 36));

        body.add(makeFormLabel("Title")); body.add(Box.createVerticalStrut(8));
        final JTextField fTitle = makeField("e.g., Black's Law Dictionary");
        fTitle.setMaximumSize(new Dimension(9999, 42)); fTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(fTitle); body.add(Box.createVerticalStrut(20));

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
        body.add(twoCol); body.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("⚠  Reference books are for in-library use only and cannot be borrowed.");
        hint.setFont(new Font("Dialog", Font.ITALIC, 12)); hint.setForeground(warnColor);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint); body.add(Box.createVerticalStrut(26));
        body.add(makeSeparatorLine()); body.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(cardColor); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(9999, 54));
        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear Fields");
        btnAdd.setPreferredSize(new Dimension(170, 44));
        btnClear.setPreferredSize(new Dimension(140, 44));
        btnAdd.addActionListener(e -> { txtTitle = fTitle; addRecordsTyped("ReferenceBook", fGenre.getText().trim(), fDewey.getText().trim()); });
        btnClear.addActionListener(e -> { fTitle.setText(""); fGenre.setText(""); fDewey.setText(""); setStatus("Fields cleared.", grayText); });
        btnRow.add(btnAdd); btnRow.add(btnClear);
        body.add(btnRow);
        card.add(body, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    // ── Search ──────────────────────────────────────────────────────────
    private JTable searchTable;
    private DefaultTableModel searchModel;

    private JPanel buildSearchPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(bgColor); bar.setBorder(new EmptyBorder(0, 0, 18, 0));
        txtSearch = makeField("Search by title, genre, or type...");
        JButton btnSearch = makePrimaryButton("Search");
        JButton btnAll    = makeOutlineButton("Show All");
        btnSearch.setPreferredSize(new Dimension(120, 38));
        btnAll.setPreferredSize(new Dimension(110, 38));
        btnSearch.addActionListener(e -> doSearch());
        btnAll.addActionListener(e -> refreshSearchTable());
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setBackground(bgColor);
        btnBar.add(btnSearch); btnBar.add(btnAll);
        bar.add(txtSearch, BorderLayout.CENTER);
        bar.add(btnBar,    BorderLayout.EAST);
        outer.add(bar, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Type", "Genre", "Dewey Decimal", "Available"};
        searchModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        searchTable = makeStyledTable(searchModel);
        searchTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        searchTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        searchTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        outer.add(makeScrollPane(searchTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bot.setBackground(bgColor); bot.setBorder(new EmptyBorder(14, 0, 0, 0));
        JButton btnDel = makeOutlineButton("Delete Selected");
        btnDel.addActionListener(e -> deleteSelectedBook());
        bot.add(btnDel);
        outer.add(bot, BorderLayout.SOUTH);
        return outer;
    }

    private void refreshSearchTable() {
        searchModel.setRowCount(0);
        try {
            db database = new db();
            List<String> books = database.getAllBooks();
            database.closeConnection();
            for (String entry : books) {
                String id = "", title = "", type = "", genre = "", ddc = "", avail = "";
                for (String p : entry.split(" \\| ")) {
                    if      (p.startsWith("ID: "))          id    = p.replace("ID: ", "").trim();
                    else if (p.startsWith("Title: "))       title = p.replace("Title: ", "").trim();
                    else if (p.startsWith("Type: "))        type  = p.replace("Type: ", "").trim();
                    else if (p.startsWith("Genre: "))       genre = p.replace("Genre: ", "").trim();
                    else if (p.startsWith("DDC: "))         ddc   = p.replace("DDC: ", "").trim();
                    else if (p.startsWith("Can Borrow: "))  avail = p.replace("Can Borrow: ", "").trim().equals("true") ? "Yes" : "No";
                }
                searchModel.addRow(new Object[]{id, title, type, genre, ddc, avail});
            }
            setStatus("Loaded " + searchModel.getRowCount() + " books.", successColor);
        } catch (Exception ex) {
            ExceptionHandler h = ExceptionHandler.dbError(ex.getMessage());
            h.logError(); setStatus(h.getUserMessage(), errColor);
        }
    }

    private void doSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { refreshSearchTable(); return; }
        // ── Validate search term ──
        if (!validator.validateInput(kw)) {
            validator.logAllErrors(); setStatus(validator.getFirstUserMessage(), warnColor); return;
        }
        searchModel.setRowCount(0);
        try {
            db database = new db();
            List<String> results = database.searchBook(kw);
            database.closeConnection();
            for (String entry : results) {
                String id = "", title = "", type = "", genre = "", ddc = "";
                for (String p : entry.split(" \\| ")) {
                    if      (p.startsWith("ID: "))         id    = p.replace("ID: ", "").trim();
                    else if (p.startsWith("Title: "))      title = p.replace("Title: ", "").trim();
                    else if (p.startsWith("Type: "))       type  = p.replace("Type: ", "").trim();
                    else if (p.startsWith("Genre: "))      genre = p.replace("Genre: ", "").trim();
                    else if (p.startsWith("DDC: "))        ddc   = p.replace("DDC: ", "").trim();
                }
                searchModel.addRow(new Object[]{id, title, type, genre, ddc, "—"});
            }
            setStatus("Found " + searchModel.getRowCount() + " result(s) for: " + kw, gold);
        } catch (Exception ex) {
            ExceptionHandler h = ExceptionHandler.dbError(ex.getMessage());
            h.logError(); setStatus(h.getUserMessage(), errColor);
        }
    }

    private void deleteSelectedBook() {
        int row = searchTable.getSelectedRow();
        if (row < 0) { setStatus("Select a book to delete.", warnColor); return; }
        String idStr = searchModel.getValueAt(row, 0).toString();
        String title = searchModel.getValueAt(row, 1).toString();

        // ── Validate the ID before attempting delete ──
        DataValidation v = new DataValidation();
        int bookId;
        try { bookId = Integer.parseInt(idStr); }
        catch (NumberFormatException ex) {
            ExceptionHandler handler = ExceptionHandler.invalidId();
            handler.logError(); setStatus(handler.getUserMessage(), errColor); return;
        }
        if (!v.validateID(bookId)) { v.logAllErrors(); setStatus(v.getFirstUserMessage(), errColor); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + title + "\"? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            db database = new db();
            boolean ok = database.deleteBook(bookId);
            database.closeConnection();
            if (ok) { setStatus("Deleted: \"" + title + "\".", successColor); refreshSearchTable(); }
            else {
                ExceptionHandler ex = ExceptionHandler.dbError("deleteBook returned false.");
                ex.logError(); setStatus(ex.getUserMessage(), errColor);
            }
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
    }

    // ── My Loans ────────────────────────────────────────────────────────
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
        activeTitle.setFont(new Font("Dialog", Font.BOLD, 13)); activeTitle.setForeground(gold);
        activeTitle.setBorder(new EmptyBorder(12, 14, 12, 14));
        myLoansModel = new DefaultTableModel(new String[]{"Loan ID", "Title", "Date Borrowed", "Due Date", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        activeCard.add(activeTitle, BorderLayout.NORTH);
        activeCard.add(makeScrollPane(makeStyledTable(myLoansModel)), BorderLayout.CENTER);

        JPanel historyCard = new JPanel(new BorderLayout());
        historyCard.setBackground(cardColor);
        historyCard.setBorder(BorderFactory.createLineBorder(divColor, 1));
        JLabel historyTitle = new JLabel("  Borrow History");
        historyTitle.setFont(new Font("Dialog", Font.BOLD, 13)); historyTitle.setForeground(grayText);
        historyTitle.setBorder(new EmptyBorder(12, 14, 12, 14));
        myHistoryModel = new DefaultTableModel(
                new String[]{"Loan ID", "Title", "Date Borrowed", "Date Returned", "Fine"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        historyCard.add(historyTitle, BorderLayout.NORTH);
        historyCard.add(makeScrollPane(makeStyledTable(myHistoryModel)), BorderLayout.CENTER);

        content.add(activeCard); content.add(historyCard);
        outer.add(content, BorderLayout.CENTER);
        return outer;
    }

    private void refreshMyLoans() {
        if (myLoansModel == null || myHistoryModel == null) return;
        myLoansModel.setRowCount(0); myHistoryModel.setRowCount(0);
        if (currentStudentId < 0) return;
        try {
            db database = new db();
            java.util.Date today = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

            List<String> loans = database.getActiveLoansByBorrower(currentStudentId);
            for (String l : loans) {
                String loanId = "", title = "", borrowed = "", due = "";
                for (String s : l.split(" \\| ")) {
                    if      (s.startsWith("LoanID: "))   loanId   = s.replace("LoanID: ", "").trim();
                    else if (s.startsWith("Title: "))    title    = s.replace("Title: ", "").trim();
                    else if (s.startsWith("Borrowed: ")) borrowed = s.replace("Borrowed: ", "").trim();
                    else if (s.startsWith("Due: "))      due      = s.replace("Due: ", "").trim();
                }
                String status = "Active";
                try {
                    long days = (sdf.parse(sdf.format(today)).getTime() - sdf.parse(due).getTime())
                                / (1000 * 60 * 60 * 24);
                    if (days > 0)       status = "OVERDUE (" + days + "d — PHP " + (days * 5) + ")";
                    else if (days == 0) status = "Due Today";
                } catch (Exception ignored) {}
                myLoansModel.addRow(new Object[]{loanId, title, borrowed, due, status});
            }

            List<String> history = database.getBorrowHistoryForBorrower(currentStudentId);
            for (String h : history) {
                String loanId = "", title = "", borrowed = "", returned = "";
                for (String s : h.split(" \\| ")) {
                    if      (s.startsWith("LoanID: "))   loanId   = s.replace("LoanID: ", "").trim();
                    else if (s.startsWith("Title: "))    title    = s.replace("Title: ", "").trim();
                    else if (s.startsWith("Borrowed: ")) borrowed = s.replace("Borrowed: ", "").trim();
                    else if (s.startsWith("Returned: ")) returned = s.replace("Returned: ", "").trim();
                }
                double fine = 0;
                try { fine = database.calculateOverdueFine(Integer.parseInt(loanId)); } catch (Exception ignored) {}
                myHistoryModel.addRow(new Object[]{loanId, title, borrowed, returned,
                        fine > 0 ? "PHP " + String.format("%.2f", fine) : "None"});
            }
            database.closeConnection();
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
    }

    // ── Dashboard ───────────────────────────────────────────────────────
    private JLabel lblTotalBooks, lblAvailBooks, lblTotalBorrowers, lblActiveLoans, lblOverdue;
    private DefaultTableModel overdueModel;

    private JPanel buildDashboardPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(bgColor); headerRow.setBorder(new EmptyBorder(0, 0, 18, 0));
        JLabel heading = new JLabel("Library Overview");
        heading.setFont(new Font("Georgia", Font.BOLD, 22)); heading.setForeground(textColor);
        JButton btnRefresh = makePrimaryButton("↻  Refresh");
        btnRefresh.setPreferredSize(new Dimension(120, 34));
        btnRefresh.addActionListener(e -> refreshDashboard());
        headerRow.add(heading, BorderLayout.WEST); headerRow.add(btnRefresh, BorderLayout.EAST);
        outer.add(headerRow, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 5, 14, 0)) {
            public Dimension getPreferredSize() { return new Dimension(super.getPreferredSize().width, 110); }
            public Dimension getMinimumSize()   { return new Dimension(super.getMinimumSize().width,   110); }
        };
        grid.setBackground(bgColor); grid.setBorder(new EmptyBorder(0, 0, 14, 0));
        lblTotalBooks     = new JLabel("—"); lblAvailBooks    = new JLabel("—");
        lblTotalBorrowers = new JLabel("—"); lblActiveLoans   = new JLabel("—");
        lblOverdue        = new JLabel("—");
        grid.add(makeStatCard("Total Items",  lblTotalBooks,     gold));
        grid.add(makeStatCard("Available",    lblAvailBooks,     successColor));
        grid.add(makeStatCard("Borrowers",    lblTotalBorrowers, textColor));
        grid.add(makeStatCard("Active Loans", lblActiveLoans,    warnColor));
        grid.add(makeStatCard("Overdue",      lblOverdue,        errColor));

        String[] cols = {"Loan ID", "Borrower ID", "Borrower Name", "Book / Item Title",
                         "Date Borrowed", "Due Date", "Days Overdue", "Fine (PHP)"};
        overdueModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable overdueTable = makeStyledTable(overdueModel);
        overdueTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        overdueTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        overdueTable.getColumnModel().getColumn(2).setPreferredWidth(170);
        overdueTable.getColumnModel().getColumn(3).setPreferredWidth(230);
        overdueTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        overdueTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        overdueTable.getColumnModel().getColumn(6).setPreferredWidth(95);
        overdueTable.getColumnModel().getColumn(7).setPreferredWidth(90);
        overdueTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                int days = 0;
                try { days = Integer.parseInt(overdueModel.getValueAt(r, 6).toString()); } catch (Exception ignored) {}
                if (sel)          { setBackground(cardColor); setForeground(gold); }
                else if (days>=7) { setBackground(new Color(70, 8,  8)); setForeground(new Color(255,120,120)); }
                else              { setBackground(new Color(45,10,10)); setForeground(errColor); }
                setBorder(new EmptyBorder(0,12,0,12));
                return this;
            }
        });

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(cardColor);
        tableCard.setBorder(BorderFactory.createLineBorder(divColor, 1));
        JLabel tableTitle = new JLabel("  ⚠  Overdue Loans — Action Required");
        tableTitle.setFont(new Font("Dialog", Font.BOLD, 12)); tableTitle.setForeground(errColor);
        tableTitle.setBorder(new EmptyBorder(10,12,10,12));
        tableCard.add(tableTitle,                  BorderLayout.NORTH);
        tableCard.add(makeScrollPane(overdueTable), BorderLayout.CENTER);

        JPanel centre = new JPanel(new BorderLayout(0, 0));
        centre.setBackground(bgColor);
        centre.add(grid,      BorderLayout.NORTH);
        centre.add(tableCard, BorderLayout.CENTER);
        outer.add(centre, BorderLayout.CENTER);
        return outer;
    }

    private JPanel makeStatCard(String title, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(divColor),
                new EmptyBorder(14, 20, 14, 20)));
        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("Dialog", Font.PLAIN, 10)); t.setForeground(grayText);
        t.setBorder(new EmptyBorder(0, 0, 4, 0));
        valueLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        valueLabel.setForeground(valueColor);
        card.add(t,          BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void refreshDashboard() {
        try {
            db database = new db();
            int[] stats = database.getDashboardStats();
            database.closeConnection();
            lblTotalBooks.setText(String.valueOf(stats[0]));
            lblAvailBooks.setText(String.valueOf(stats[1]));
            lblTotalBorrowers.setText(String.valueOf(stats[2]));
            lblActiveLoans.setText(String.valueOf(stats[3]));
            lblOverdue.setText(String.valueOf(stats[4]));
            lblOverdue.setForeground(stats[4] > 0 ? errColor : successColor);

            if (overdueModel != null) {
                overdueModel.setRowCount(0);
                db db2 = new db();
                List<String> overdueList = db2.getOverdueLoans();
                db2.closeConnection();
                for (String loan : overdueList) {
                    String loanId="", borrowerId="", borrowerName="", title="", borrowed="", due="", daysStr="";
                    for (String p : loan.split(" \\| ")) {
                        if      (p.startsWith("LoanID: "))       loanId       = p.replace("LoanID: ", "").trim();
                        else if (p.startsWith("BorrowerID: "))   borrowerId   = p.replace("BorrowerID: ", "").trim();
                        else if (p.startsWith("BorrowerName: ")) borrowerName = p.replace("BorrowerName: ", "").trim();
                        else if (p.startsWith("Title: "))        title        = p.replace("Title: ", "").trim();
                        else if (p.startsWith("Borrowed: "))     borrowed     = p.replace("Borrowed: ", "").trim();
                        else if (p.startsWith("Due: "))          due          = p.replace("Due: ", "").trim();
                        else if (p.startsWith("DaysOverdue: "))  daysStr      = p.replace("DaysOverdue: ", "").trim();
                    }
                    int days = 0;
                    try { days = Integer.parseInt(daysStr); } catch (Exception ignored) {}
                    overdueModel.addRow(new Object[]{loanId, borrowerId, borrowerName, title,
                            borrowed, due, days, String.format("%.2f", days * 5.0)});
                }
            }
            setStatus("Dashboard refreshed.", stats[4] > 0 ? warnColor : successColor);
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
    }

    // ── Notifications ───────────────────────────────────────────────────
    private JTextArea notifArea;

    private JPanel buildNotificationsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBackground(bgColor); top.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel heading = new JLabel("System Notifications & Overdue Alerts");
        heading.setFont(new Font("Georgia", Font.BOLD, 17)); heading.setForeground(textColor);
        JPanel topBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topBtns.setBackground(bgColor);
        JButton btnScan    = makePrimaryButton("Scan Overdue");
        JButton btnRefresh = makeOutlineButton("Refresh");
        btnScan.setPreferredSize(new Dimension(140, 36));
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnScan.addActionListener(e -> scanOverdue());
        btnRefresh.addActionListener(e -> refreshNotifications());
        topBtns.add(btnScan); topBtns.add(btnRefresh);
        top.add(heading, BorderLayout.CENTER); top.add(topBtns, BorderLayout.EAST);
        outer.add(top, BorderLayout.NORTH);

        notifArea = new JTextArea();
        notifArea.setEditable(false);
        notifArea.setBackground(tableBg); notifArea.setForeground(textColor);
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
        try {
            db database = new db();
            List<String> allBorrowers = database.getAllBorrowers();
            StringBuilder sb = new StringBuilder();
            for (String b : allBorrowers) {
                String idStr = "", name = "";
                for (String p : b.split(" \\| ")) {
                    if      (p.startsWith("ID: "))   idStr = p.replace("ID: ", "").trim();
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
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
    }

    private void scanOverdue() {
        try {
            db database = new db();
            database.notifyOverdueBorrowers();
            database.closeConnection();
            setStatus("Overdue scan complete.", warnColor);
            refreshNotifications();
        } catch (Exception ex) {
            ExceptionHandler handler = ExceptionHandler.dbError(ex.getMessage());
            handler.logError(); setStatus(handler.getUserMessage(), errColor);
        }
    }

    // ── About ───────────────────────────────────────────────────────────
    public void showAbout() {
        JDialog dlg = new JDialog(this, "About", true);
        dlg.setSize(420, 300); dlg.setLocationRelativeTo(this); dlg.setResizable(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor); panel.setBorder(new EmptyBorder(28, 34, 28, 34));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        logoRow.setBackground(cardColor);
        if (iconEmblem != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconEmblem), 70, 52)));
        if (iconText   != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconText),  120, 42)));
        panel.add(logoRow, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(cardColor); info.setBorder(new EmptyBorder(20, 0, 14, 0));
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
        ok.addActionListener(e -> dlg.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(cardColor); btnRow.add(ok);
        panel.add(btnRow, BorderLayout.SOUTH);
        dlg.setContentPane(panel); dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════
    //  SHARED UI HELPERS
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildAddCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(760, 420));

        JPanel accent = new JPanel(new GridLayout(1, 2));
        accent.setPreferredSize(new Dimension(0, 5));
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
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? cardColor : fieldBg);
                setForeground(sel ? gold : textColor);
                setBorder(new EmptyBorder(5, 12, 5, 12));
                return this;
            }
        });
    }

    private void stylePasswordField(JPasswordField pf) {
        pf.setBackground(fieldBg); pf.setForeground(textColor); pf.setCaretColor(textColor);
        pf.setFont(new Font("Dialog", Font.PLAIN, 14));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fieldBorder, 1),
                new EmptyBorder(5, 12, 5, 12)));
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
        tf.setForeground(textColor); tf.setBackground(fieldBg); tf.setCaretColor(gold);
        tf.setPreferredSize(new Dimension(0, 42));
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
        l.setFont(new Font("Dialog", Font.BOLD, 12)); l.setForeground(grayText);
        return l;
    }

    private JTable makeStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(tableBg); table.setForeground(textColor);
        table.setGridColor(divColor);  table.setRowHeight(34);
        table.setFont(new Font("Dialog", Font.PLAIN, 13));
        table.setSelectionBackground(cardColor); table.setSelectionForeground(gold);
        table.setShowHorizontalLines(true); table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(tableHeader);
        table.getTableHeader().setForeground(grayText);
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, divColor));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
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
                g2.setColor(getModel().isPressed()  ? darkGold
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
                g2.setColor(getModel().isPressed()  ? divColor
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

    private void setStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
    }

    // ── Kept for backward compatibility ───────────────────────────────
    public void addRecords() { addRecordsTyped("Book",
            txtGenre != null ? txtGenre.getText().trim() : "",
            txtDewey != null ? txtDewey.getText().trim() : ""); }

    public void clearAddFields() {
        if (txtTitle != null) txtTitle.setText("");
        if (txtGenre != null) txtGenre.setText("");
        if (txtDewey != null) txtDewey.setText("");
        setStatus("Fields cleared.", grayText);
    }
}