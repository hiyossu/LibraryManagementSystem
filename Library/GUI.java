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

    private Color bgColor = new Color(255, 255, 255);
    private Color sidebarColor = new Color(255, 255, 255);
    private Color panelColor = new Color(255, 255, 255);
    private Color cardColor = new Color(255, 255, 255);
    private Color gold = new Color(212, 160, 23);
    private Color darkGold = new Color(154, 114, 10);
    private Color red = new Color(190, 16, 16);
    private Color textColor = new Color(0, 0, 0);
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
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setBackground(bgColor);
        if (iconEmblem != null) setIconImage(iconEmblem.getImage());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        setContentPane(root);
        root.add(buildSidebar(),    BorderLayout.WEST);
        root.add(buildMainPanel(), BorderLayout.CENTER);
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

    // ── SIDEBAR ───────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, divColor));

        sidebar.add(buildLogoArea());
        sidebar.add(makeDividerLabel("PEOPLE"));
        sidebar.add(makeNavBtn("  Borrowers",      "borrowers"));
        sidebar.add(makeDividerLabel("CATALOG"));
        sidebar.add(makeNavBtn("+ Add New Book",   "add"));
        sidebar.add(makeNavBtn("  Search Books",   "search"));
        sidebar.add(makeDividerLabel("BORROWING"));
        sidebar.add(makeNavBtn("  Checkout Book",  "checkout"));
        sidebar.add(makeNavBtn("  Return Book",    "returnbook"));
        sidebar.add(makeDividerLabel("MEDIA"));
        sidebar.add(makeNavBtn("  Checkout Media",  "media_checkout"));
        sidebar.add(makeNavBtn("  Board Game",      "add_boardgame"));
        sidebar.add(makeNavBtn("  DVD",             "add_dvd"));
        sidebar.add(makeNavBtn("  Magazine",        "add_magazine"));
        sidebar.add(makeDividerLabel("REFERENCE"));
        sidebar.add(makeNavBtn("  Reference Book", "add_reference"));
        sidebar.add(makeDividerLabel("SYSTEM"));
        sidebar.add(makeNavBtn("  Dashboard",      "dashboard"));
        sidebar.add(makeNavBtn("  Notifications",  "notifications"));
        sidebar.add(makeNavBtn("  About",          "about"));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildSidebarFooter());
        return sidebar;
    }

    private JPanel buildLogoArea() {
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBackground(sidebarColor);
        logoArea.setMaximumSize(new Dimension(235, 220));
        logoArea.setBorder(new EmptyBorder(18, 0, 12, 0));
        logoArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (iconEmblem != null) {
            JLabel lbl = new JLabel(iconEmblem);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
            logoArea.add(lbl);
        } else {
            JLabel fb = new JLabel("\uD83C\uDFDB");
            fb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            fb.setForeground(gold); fb.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(fb); logoArea.add(Box.createVerticalStrut(6));
        }
        if (iconText != null) {
            JLabel lbl = new JLabel(iconText);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(0, 0, 2, 0));
            logoArea.add(lbl);
        } else {
            JLabel lbl = new JLabel("Mapua University");
            lbl.setFont(new Font("Georgia", Font.BOLD, 13));
            lbl.setForeground(red); lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(lbl);
        }
        JLabel campus = new JLabel("Cardinal Library \u00B7 Makati");
        campus.setFont(new Font("Georgia", Font.ITALIC, 10));
        campus.setForeground(grayText); campus.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoArea.add(campus); logoArea.add(Box.createVerticalStrut(10));

        JPanel stripe = new JPanel(new GridLayout(1, 2));
        stripe.setMaximumSize(new Dimension(235, 3)); stripe.setPreferredSize(new Dimension(235, 3));
        JPanel g1 = new JPanel(); g1.setBackground(gold);
        JPanel g2 = new JPanel(); g2.setBackground(red);
        stripe.add(g1); stripe.add(g2);
        logoArea.add(stripe);
        return logoArea;
    }

    private JPanel buildSidebarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(sidebarColor);
        footer.setBorder(new EmptyBorder(12, 20, 16, 20));
        footer.setMaximumSize(new Dimension(235, 60));
        JLabel a = new JLabel("ADMIN SESSION");
        a.setFont(new Font("Monospaced", Font.BOLD, 10)); a.setForeground(gold);
        JLabel v = new JLabel("LMS v2.0  2025");
        v.setFont(new Font("Dialog", Font.PLAIN, 10)); v.setForeground(grayText);
        footer.add(a, BorderLayout.NORTH); footer.add(v, BorderLayout.SOUTH);
        return footer;
    }

    private JPanel makeDividerLabel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(sidebarColor); p.setMaximumSize(new Dimension(235, 30));
        p.setBorder(new EmptyBorder(8, 20, 2, 20));
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
        btn.setMaximumSize(new Dimension(235, 42));
        btn.setBorder(new EmptyBorder(10, 20, 10, 16));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 13)); lbl.setForeground(textColor);
        btn.add(lbl, BorderLayout.CENTER);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn != activeNavBtn) btn.setBackground(panelColor); }
            public void mouseExited (MouseEvent e) { if (btn != activeNavBtn) btn.setBackground(sidebarColor); }
            public void mouseClicked(MouseEvent e) { navigateTo(action, btn); }
        });
        if (action.equals("borrowers")) setActiveNav(btn, lbl);
        return btn;
    }

    private void setActiveNav(JPanel btn, JLabel lbl) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(sidebarColor);
            activeNavBtn.setBorder(new EmptyBorder(10, 20, 10, 16));
            Component[] comps = activeNavBtn.getComponents();
            for (Component c : comps) if (c instanceof JLabel) ((JLabel)c).setForeground(textColor);
        }
        activeNavBtn = btn;
        btn.setBackground(cardColor);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, gold),
            new EmptyBorder(10, 17, 10, 16)));
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
        }
    }

    // ── MAIN PANEL ────────────────────────────────────────────────────────

    private JLabel lblPageTitle, lblCrumb;

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bgColor);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(panelColor);
        topBar.setPreferredSize(new Dimension(0, 54));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, red), new EmptyBorder(0, 24, 0, 24)));
        lblPageTitle = new JLabel("Add New Book Record");
        lblPageTitle.setFont(new Font("Georgia", Font.BOLD, 17)); lblPageTitle.setForeground(textColor);
        lblCrumb = new JLabel("Library Catalog / Add Record");
        lblCrumb.setFont(new Font("Dialog", Font.PLAIN, 11)); lblCrumb.setForeground(grayText);
        topBar.add(lblPageTitle, BorderLayout.CENTER);
        topBar.add(lblCrumb,    BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        cardLayout  = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(bgColor);
        mainContent.add(buildAddBookPanel(),                           "ADD");
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
        main.add(mainContent, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(sidebarColor);
        statusBar.setPreferredSize(new Dimension(0, 32));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, divColor), new EmptyBorder(0, 20, 0, 20)));
        lblStatus = new JLabel("System ready");
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 11)); lblStatus.setForeground(grayText);
        JLabel brand = new JLabel("Mapua Cardinal Library LMS");
        brand.setFont(new Font("Dialog", Font.PLAIN, 10)); brand.setForeground(grayText);
        statusBar.add(lblStatus, BorderLayout.CENTER);
        statusBar.add(brand,     BorderLayout.EAST);
        main.add(statusBar, BorderLayout.SOUTH);

        cardLayout.show(mainContent, "BORROWERS");
        updateTopBar("Manage Borrowers", "People / Borrowers");
        return main;
    }

    private void showCard(String name)                     { cardLayout.show(mainContent, name); }
    private void updateTopBar(String title, String crumb)  { lblPageTitle.setText(title); lblCrumb.setText(crumb); }
    private void setStatus(String msg, Color color)        { lblStatus.setText(msg); lblStatus.setForeground(color); }

    // ── ADD BOOK ──────────────────────────────────────────────────────────

    private JPanel buildAddBookPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel card = buildAddCard("Add New Book", "Register a new book into the library catalog.");
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(20, 32, 24, 32));

        body.add(makeFormLabel("Title"));
        body.add(Box.createVerticalStrut(6));
        txtTitle = makeField("e.g., The Great Gatsby");
        txtTitle.setMaximumSize(new Dimension(9999, 38));
        txtTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(txtTitle);
        body.add(Box.createVerticalStrut(16));

        JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
        twoCol.setBackground(cardColor);
        twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);
        twoCol.setMaximumSize(new Dimension(9999, 80));

        JPanel colGenre = new JPanel(); colGenre.setLayout(new BoxLayout(colGenre, BoxLayout.Y_AXIS)); colGenre.setBackground(cardColor);
        colGenre.add(makeFormLabel("Genre")); colGenre.add(Box.createVerticalStrut(6));
        txtGenre = makeField("e.g., Fiction, Science...");
        txtGenre.setMaximumSize(new Dimension(9999, 38)); txtGenre.setAlignmentX(Component.LEFT_ALIGNMENT);
        colGenre.add(txtGenre);

        JPanel colDewey = new JPanel(); colDewey.setLayout(new BoxLayout(colDewey, BoxLayout.Y_AXIS)); colDewey.setBackground(cardColor);
        colDewey.add(makeFormLabel("Dewey Decimal")); colDewey.add(Box.createVerticalStrut(6));
        txtDewey = makeField("e.g., 813.54");
        txtDewey.setMaximumSize(new Dimension(9999, 38)); txtDewey.setAlignmentX(Component.LEFT_ALIGNMENT);
        colDewey.add(txtDewey);

        twoCol.add(colGenre); twoCol.add(colDewey);
        body.add(twoCol);
        body.add(Box.createVerticalStrut(8));

        JLabel hint = new JLabel("Books require a valid Dewey Decimal number (e.g., 813.54).");
        hint.setFont(new Font("Dialog", Font.ITALIC, 11)); hint.setForeground(grayText);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint);
        body.add(Box.createVerticalStrut(22));
        body.add(makeSeparatorLine());
        body.add(Box.createVerticalStrut(18));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(cardColor); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(9999, 50));
        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear Fields");
        btnAdd.setPreferredSize(new Dimension(160, 40)); btnClear.setPreferredSize(new Dimension(130, 40));
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

    // ── ADD MEDIA TYPE (BoardGame / DVD / Magazine) ───────────────────────

    private JPanel buildAddTypePanel(final String mediaType, final String displayName) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(16, 28, 16, 28));

        // ── Add form at top ───────────────────────────────────────────────
        JPanel addForm = new JPanel(new BorderLayout());
        addForm.setBackground(cardColor);
        addForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(divColor, 1), new EmptyBorder(14, 20, 14, 20)));

        // Accent stripe
        JPanel accent = new JPanel(new GridLayout(1, 2));
        accent.setPreferredSize(new Dimension(0, 3));
        JPanel a1 = new JPanel(); a1.setBackground(gold);
        JPanel a2 = new JPanel(); a2.setBackground(red);
        accent.add(a1); accent.add(a2);

        JPanel formBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        formBody.setBackground(cardColor);

        JLabel lblForm = new JLabel("Add " + displayName);
        lblForm.setFont(new Font("Georgia", Font.BOLD, 14)); lblForm.setForeground(textColor);

        final JTextField fTitle = makeField("Title");
        fTitle.setPreferredSize(new Dimension(260, 34));

        final JTextField fGenre = makeField("Genre");
        fGenre.setPreferredSize(new Dimension(160, 34));

        // DVD and Magazine get canBorrow toggle; BoardGame gets it too
        final JComboBox<String> cmbBorrow = new JComboBox<>(new String[]{"Can Borrow: Yes", "Can Borrow: No"});
        styleComboBox(cmbBorrow);
        cmbBorrow.setPreferredSize(new Dimension(150, 34));

        JButton btnAdd   = makePrimaryButton("+ Add");
        JButton btnClear = makeOutlineButton("Clear");
        btnAdd.setPreferredSize(new Dimension(100, 34));
        btnClear.setPreferredSize(new Dimension(80, 34));

        formBody.add(makeLabel("Title:")); formBody.add(fTitle);
        formBody.add(makeLabel("Genre:")); formBody.add(fGenre);
        formBody.add(makeLabel("Borrow:")); formBody.add(cmbBorrow);
        formBody.add(btnAdd); formBody.add(btnClear);

        addForm.add(accent, BorderLayout.NORTH);
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(cardColor);
        headerRow.setBorder(new EmptyBorder(0, 0, 8, 0));
        headerRow.add(lblForm, BorderLayout.WEST);
        addForm.add(headerRow, BorderLayout.CENTER);
        addForm.add(formBody, BorderLayout.SOUTH);
        outer.add(addForm, BorderLayout.NORTH);

        // ── Search bar ────────────────────────────────────────────────────
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setBackground(bgColor);
        searchBar.setBorder(new EmptyBorder(12, 0, 10, 0));

        final JTextField fSearch = makeField("Search by title or genre...");
        JButton btnSearch = makePrimaryButton("Search");
        JButton btnAll    = makeOutlineButton("Show All");
        btnSearch.setPreferredSize(new Dimension(100, 34));
        btnAll.setPreferredSize(new Dimension(90, 34));
        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBtns.setBackground(bgColor);
        searchBtns.add(btnSearch); searchBtns.add(btnAll);
        searchBar.add(fSearch, BorderLayout.CENTER);
        searchBar.add(searchBtns, BorderLayout.EAST);

        // ── Table ─────────────────────────────────────────────────────────
        String[] cols = {"ID", "Title", "Genre", "Can Borrow"};
        final DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (mediaType.equals("BoardGame")) boardGameModel = model;
        else if (mediaType.equals("DVD"))  dvdModel       = model;
        else                               magazineModel  = model;

        final JTable table = makeStyledTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(320);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);

        // Delete button row
        JPanel botRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botRow.setBackground(bgColor); botRow.setBorder(new EmptyBorder(8, 0, 0, 0));
        JButton btnDel = makeOutlineButton("Delete Selected");
        botRow.add(btnDel);

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(bgColor);
        tableArea.add(searchBar,           BorderLayout.NORTH);
        tableArea.add(makeScrollPane(table), BorderLayout.CENTER);
        tableArea.add(botRow,              BorderLayout.SOUTH);
        outer.add(tableArea, BorderLayout.CENTER);

        // ── Load helpers ──────────────────────────────────────────────────
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
                                parts[5].equalsIgnoreCase("true") ? "\u2714  Yes" : "\u2718  No"
                            });
                        }
                    }
                } catch (Exception ex) { setStatus("Error loading " + displayName + ": " + ex.getMessage(), errColor); }
            }
        };

        // ── Button actions ────────────────────────────────────────────────
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = fTitle.getText().trim();
                String genre = fGenre.getText().trim();
                boolean canBorrow = cmbBorrow.getSelectedIndex() == 0;
                if (title.isEmpty()) { setStatus("Title cannot be empty.", warnColor); return; }
                if (genre.isEmpty()) { setStatus("Genre cannot be empty.", warnColor); return; }
                db database = new db();
                boolean ok = database.addBookTyped(title, mediaType, genre, "");
                database.closeConnection();
                if (ok) {
                    setStatus("Added " + displayName + ": \"" + title + "\" successfully.", successColor);
                    fTitle.setText(""); fGenre.setText(""); cmbBorrow.setSelectedIndex(0);
                    loadAll.run();
                } else {
                    setStatus("Database error. Check console.", errColor);
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fTitle.setText(""); fGenre.setText(""); cmbBorrow.setSelectedIndex(0);
                setStatus("Fields cleared.", grayText);
            }
        });

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
                                parts[5].equalsIgnoreCase("true") ? "\u2714  Yes" : "\u2718  No"
                            });
                        }
                    }
                } catch (Exception ex) { setStatus("Error searching: " + ex.getMessage(), errColor); }
            }
        });

        btnAll.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { fSearch.setText(""); loadAll.run(); } });
        fSearch.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { btnSearch.doClick(); } });

        btnDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) { setStatus("Select an item to delete.", warnColor); return; }
                String idStr  = model.getValueAt(row, 0).toString();
                String title  = model.getValueAt(row, 1).toString();
                int confirm = JOptionPane.showConfirmDialog(null,
                    "Delete \"" + title + "\"? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                db database = new db();
                boolean ok = database.deleteBook(Integer.parseInt(idStr));
                database.closeConnection();
                if (ok) { setStatus("Deleted: \"" + title + "\".", successColor); loadAll.run(); }
                else      setStatus("Delete failed. Check console.", errColor);
            }
        });

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
                        parts[5].equalsIgnoreCase("true") ? "\u2714  Yes" : "\u2718  No"
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

    // ── MEDIA CHECKOUT ────────────────────────────────────────────────────

    private DefaultTableModel mediaCheckoutModel;
    private JTextField txtMediaBorrowerId, txtMediaCondition;

    private JPanel buildMediaCheckoutPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(24, 28, 16, 28));

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(bgColor);
        topBar.setBorder(new EmptyBorder(0, 0, 12, 0));

        final String[] filterTypes  = {"All", "BoardGame", "DVD", "Magazine"};
        final String[] filterLabels = {"All", "Board Game", "DVD", "Magazine"};
        final JPanel[] pills        = new JPanel[filterTypes.length];
        final JLabel[] pillLbls     = new JLabel[filterTypes.length];
        final String[] activeFilter = {"All"};

        JPanel pillRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pillRow.setBackground(bgColor);
        for (int i = 0; i < filterTypes.length; i++) {
            JPanel pill = new JPanel(new BorderLayout());
            pill.setCursor(new Cursor(Cursor.HAND_CURSOR));
            JLabel lbl = new JLabel(filterLabels[i]);
            lbl.setFont(new Font("Dialog", Font.PLAIN, 12));
            lbl.setBorder(new EmptyBorder(5, 14, 5, 14));
            pill.add(lbl);
            pills[i] = pill; pillLbls[i] = lbl;
            pillRow.add(pill);
        }

        final JTextField fSearch = makeField("Search by title or genre...");
        JButton btnSearch = makePrimaryButton("Search");
        btnSearch.setPreferredSize(new Dimension(90, 34));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
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
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outer.add(makeScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(panelColor);
        bottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, divColor),
            new EmptyBorder(12, 16, 12, 16)));

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        formRow.setBackground(panelColor);

        JLabel lblSel = new JLabel("Selected ID:");
        lblSel.setFont(new Font("Dialog", Font.BOLD, 11)); lblSel.setForeground(grayText);
        final JTextField fSelId = makeField("\u2014");
        fSelId.setPreferredSize(new Dimension(60, 34)); fSelId.setEditable(false);
        fSelId.setBackground(new Color(10, 3, 3));

        JLabel lblBorr = new JLabel("Borrower ID:");
        lblBorr.setFont(new Font("Dialog", Font.BOLD, 11)); lblBorr.setForeground(grayText);
        txtMediaBorrowerId = makeField("e.g., 10001");
        txtMediaBorrowerId.setPreferredSize(new Dimension(120, 34));

        JLabel lblCond = new JLabel("Condition:");
        lblCond.setFont(new Font("Dialog", Font.BOLD, 11)); lblCond.setForeground(grayText);
        txtMediaCondition = makeField("Good");
        txtMediaCondition.setPreferredSize(new Dimension(100, 34));

        JButton btnCheckout = makePrimaryButton("Checkout");
        JButton btnClear    = makeOutlineButton("Clear");
        btnCheckout.setPreferredSize(new Dimension(110, 34));
        btnClear.setPreferredSize(new Dimension(80, 34));

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
                            p[5].equalsIgnoreCase("true") ? "\u2714  Yes" : "\u2718  No"
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
                if (selId.isEmpty() || selId.equals("\u2014")) { setStatus("Please select an item from the list.", warnColor); return; }
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
                        fSelId.setText("\u2014"); txtMediaBorrowerId.setText(""); txtMediaCondition.setText("Good");
                        loadFiltered.run();
                    } else {
                        setStatus("Checkout failed \u2014 item unavailable or borrower reached limit.", errColor);
                    }
                } catch (NumberFormatException ex) { setStatus("IDs must be numbers.", warnColor); }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fSelId.setText("\u2014"); txtMediaBorrowerId.setText(""); txtMediaCondition.setText("Good");
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
                    p[5].equalsIgnoreCase("true") ? "\u2714  Yes" : "\u2718  No"
                });
            }
        } catch (Exception ex) { setStatus("Error loading media: " + ex.getMessage(), errColor); }
    }

    // ── ADD REFERENCE BOOK ────────────────────────────────────────────────

    private JPanel buildAddRefPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel card = buildAddCard("Add Reference Book", "Reference books are in-library use only and cannot be borrowed.");
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(20, 32, 24, 32));

        body.add(makeFormLabel("Title"));
        body.add(Box.createVerticalStrut(6));
        final JTextField fTitle = makeField("e.g., Black's Law Dictionary");
        fTitle.setMaximumSize(new Dimension(9999, 38)); fTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(fTitle);
        body.add(Box.createVerticalStrut(16));

        JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
        twoCol.setBackground(cardColor); twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);
        twoCol.setMaximumSize(new Dimension(9999, 80));

        JPanel colGenre = new JPanel(); colGenre.setLayout(new BoxLayout(colGenre, BoxLayout.Y_AXIS)); colGenre.setBackground(cardColor);
        colGenre.add(makeFormLabel("Genre")); colGenre.add(Box.createVerticalStrut(6));
        final JTextField fGenre = makeField("e.g., Law, Medicine, Science...");
        fGenre.setMaximumSize(new Dimension(9999, 38)); fGenre.setAlignmentX(Component.LEFT_ALIGNMENT);
        colGenre.add(fGenre);

        JPanel colDewey = new JPanel(); colDewey.setLayout(new BoxLayout(colDewey, BoxLayout.Y_AXIS)); colDewey.setBackground(cardColor);
        colDewey.add(makeFormLabel("Dewey Decimal")); colDewey.add(Box.createVerticalStrut(6));
        final JTextField fDewey = makeField("e.g., 340.1");
        fDewey.setMaximumSize(new Dimension(9999, 38)); fDewey.setAlignmentX(Component.LEFT_ALIGNMENT);
        colDewey.add(fDewey);

        twoCol.add(colGenre); twoCol.add(colDewey);
        body.add(twoCol);
        body.add(Box.createVerticalStrut(8));

        JLabel hint = new JLabel("\u26A0  Reference books are for in-library use only and cannot be borrowed.");
        hint.setFont(new Font("Dialog", Font.ITALIC, 11)); hint.setForeground(warnColor);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint);
        body.add(Box.createVerticalStrut(22));
        body.add(makeSeparatorLine());
        body.add(Box.createVerticalStrut(18));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(cardColor); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(9999, 50));
        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear Fields");
        btnAdd.setPreferredSize(new Dimension(160, 40)); btnClear.setPreferredSize(new Dimension(130, 40));
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = fTitle.getText().trim();
                String genre = fGenre.getText().trim();
                String dewey = fDewey.getText().trim();
                if (title.isEmpty()) { setStatus("Title cannot be empty.", warnColor); return; }
                if (genre.isEmpty()) { setStatus("Genre cannot be empty.", warnColor); return; }
                if (dewey.isEmpty()) { setStatus("Dewey Decimal cannot be empty.", warnColor); return; }
                if (!dewey.matches("\\d{3}(\\.\\d+)?")) { setStatus("Dewey Decimal must be a valid number (e.g., 340.1).", warnColor); return; }
                db database = new db();
                boolean ok = database.addBookTyped(title, "ReferenceBook", genre, dewey);
                database.closeConnection();
                if (ok) {
                    setStatus("Added Reference Book: \"" + title + "\" successfully.", successColor);
                    fTitle.setText(""); fGenre.setText(""); fDewey.setText("");
                } else {
                    setStatus("Database error. Check console.", errColor);
                }
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

    // ── SEARCH ────────────────────────────────────────────────────────────

    private JTable searchTable;
    private DefaultTableModel searchModel;

    private JPanel buildSearchPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(bgColor); bar.setBorder(new EmptyBorder(0, 0, 16, 0));
        txtSearch = makeField("Search by title, genre, or type...");
        JButton btnSearch = makePrimaryButton("Search");
        JButton btnAll    = makeOutlineButton("Show All");
        btnSearch.setPreferredSize(new Dimension(110, 36));
        btnAll.setPreferredSize(new Dimension(100, 36));
        btnSearch.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doSearch(); } });
        btnAll.addActionListener(new ActionListener()    { public void actionPerformed(ActionEvent e) { refreshSearchTable(); } });
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); btnBar.setBackground(bgColor);
        btnBar.add(btnSearch); btnBar.add(btnAll);
        bar.add(txtSearch, BorderLayout.CENTER); bar.add(btnBar, BorderLayout.EAST);
        outer.add(bar, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Type", "Genre", "Dewey Decimal", "Available"};
        searchModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        searchTable = makeStyledTable(searchModel);
        searchTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        searchTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        searchTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        outer.add(makeScrollPane(searchTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bot.setBackground(bgColor); bot.setBorder(new EmptyBorder(12, 0, 0, 0));
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
                if      (p.startsWith("ID: "))         id    = p.replace("ID: ", "").trim();
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
                if      (p.startsWith("ID: "))    id    = p.replace("ID: ", "").trim();
                else if (p.startsWith("Title: ")) title = p.replace("Title: ", "").trim();
                else if (p.startsWith("Type: "))  type  = p.replace("Type: ", "").trim();
                else if (p.startsWith("Genre: ")) genre = p.replace("Genre: ", "").trim();
                else if (p.startsWith("DDC: "))   ddc   = p.replace("DDC: ", "").trim();
            }
            searchModel.addRow(new Object[]{id, title, type, genre, ddc, "\u2014"});
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

    // ── CHECKOUT ─────────────────────────────────────────────────────────

    private JPanel buildCheckoutPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(bgColor);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(460, 310));

        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 3));
        JPanel s1 = new JPanel(); s1.setBackground(gold);
        JPanel s2 = new JPanel(); s2.setBackground(red);
        strip.add(s1); strip.add(s2);
        card.add(strip, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(20, 28, 24, 28));

        JLabel titleLbl = new JLabel("Checkout Book");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 18)); titleLbl.setForeground(textColor);
        JLabel sub = new JLabel("Loan a book to a registered borrower. Due date is set to 14 days.");
        sub.setFont(new Font("Dialog", Font.PLAIN, 11)); sub.setForeground(grayText);

        body.add(titleLbl); body.add(Box.createVerticalStrut(4)); body.add(sub);
        body.add(Box.createVerticalStrut(16));
        JSeparator sep = new JSeparator(); sep.setForeground(divColor); sep.setMaximumSize(new Dimension(9999, 1));
        body.add(sep);
        body.add(Box.createVerticalStrut(16));

        txtCheckoutBookId     = makeField("Enter Book ID");
        txtCheckoutBorrowerId = makeField("Enter Borrower ID No.");
        txtCheckoutCondition  = makeField("e.g., Good, Fair, Poor");

        body.add(makeInlineRow("Book ID",        txtCheckoutBookId));     body.add(Box.createVerticalStrut(10));
        body.add(makeInlineRow("Borrower ID",    txtCheckoutBorrowerId)); body.add(Box.createVerticalStrut(10));
        body.add(makeInlineRow("Book Condition", txtCheckoutCondition));  body.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btns.setBackground(cardColor);
        btns.setMaximumSize(new Dimension(9999, 40));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnCheckout = makePrimaryButton("Checkout");
        JButton btnClear    = makeOutlineButton("Clear");
        btnCheckout.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doCheckout(); } });
        btnClear.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
            txtCheckoutBookId.setText(""); txtCheckoutBorrowerId.setText(""); txtCheckoutCondition.setText("");
        }});
        btns.add(btnCheckout); btns.add(Box.createHorizontalStrut(10)); btns.add(btnClear);
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
            if (!database.bookExists(bookId))         { setStatus("Book ID " + bookId + " not found.",         errColor); database.closeConnection(); return; }
            if (!database.borrowerExists(borrowerId)) { setStatus("Borrower ID " + borrowerId + " not found.", errColor); database.closeConnection(); return; }
            boolean ok = database.checkoutBook(bookId, borrowerId, condition);
            database.closeConnection();
            if (ok) {
                setStatus("Book ID " + bookId + " checked out to Borrower " + borrowerId + ". Due in 14 days.", successColor);
                txtCheckoutBookId.setText(""); txtCheckoutBorrowerId.setText(""); txtCheckoutCondition.setText("");
            } else {
                setStatus("Checkout failed \u2014 book may be unavailable or borrower reached their limit.", errColor);
            }
        } catch (NumberFormatException ex) {
            setStatus("Book ID and Borrower ID must be numbers.", warnColor);
        }
    }

    // ── RETURN ────────────────────────────────────────────────────────────

    private JPanel buildReturnPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(bgColor);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(460, 300));

        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 3));
        JPanel s1 = new JPanel(); s1.setBackground(gold);
        JPanel s2 = new JPanel(); s2.setBackground(red);
        strip.add(s1); strip.add(s2);
        card.add(strip, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(cardColor);
        body.setBorder(new EmptyBorder(20, 28, 24, 28));

        JLabel titleLbl = new JLabel("Return Book");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 18)); titleLbl.setForeground(textColor);
        JLabel sub = new JLabel("Process a book return and calculate any overdue fines.");
        sub.setFont(new Font("Dialog", Font.PLAIN, 11)); sub.setForeground(grayText);
        body.add(titleLbl); body.add(Box.createVerticalStrut(4)); body.add(sub);
        body.add(Box.createVerticalStrut(16));
        JSeparator sep = new JSeparator(); sep.setForeground(divColor); sep.setMaximumSize(new Dimension(9999, 1));
        body.add(sep);
        body.add(Box.createVerticalStrut(16));

        txtReturnLoanId = makeField("Enter Loan ID");
        body.add(makeInlineRow("Loan ID", txtReturnLoanId));
        body.add(Box.createVerticalStrut(10));

        final JLabel lblFine = new JLabel(" ");
        lblFine.setFont(new Font("Dialog", Font.PLAIN, 11)); lblFine.setForeground(warnColor);
        lblFine.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblFine);
        body.add(Box.createVerticalStrut(10));

        JButton btnCheck = makeOutlineButton("Check Fine");
        btnCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCheck.setMaximumSize(new Dimension(9999, 36));
        btnCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idStr = txtReturnLoanId.getText().trim();
                if (idStr.isEmpty()) { lblFine.setText("Enter a Loan ID first."); return; }
                try {
                    int loanId = Integer.parseInt(idStr);
                    db database = new db();
                    double fine     = database.calculateOverdueFine(loanId);
                    String borrowed = database.getDateBorrowed(loanId);
                    database.closeConnection();
                    if (fine > 0) lblFine.setText("Fine: PHP " + String.format("%.2f", fine) + "  |  Borrowed: " + borrowed);
                    else          lblFine.setText("No fine. Borrowed: " + borrowed);
                } catch (NumberFormatException ex) { lblFine.setText("Loan ID must be a number."); }
            }
        });
        body.add(btnCheck);
        body.add(Box.createVerticalStrut(16));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btns.setBackground(cardColor);
        btns.setMaximumSize(new Dimension(9999, 40));
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnReturn = makePrimaryButton("Process Return");
        JButton btnClear  = makeOutlineButton("Clear");
        btnReturn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { doReturn(lblFine); } });
        btnClear.addActionListener(new ActionListener()  { public void actionPerformed(ActionEvent e) { txtReturnLoanId.setText(""); lblFine.setText(" "); } });
        btns.add(btnReturn); btns.add(Box.createHorizontalStrut(10)); btns.add(btnClear);
        body.add(btns);

        card.add(body, BorderLayout.CENTER);
        wrap.add(card);
        return wrap;
    }

    private void doReturn(JLabel lblFine) {
        String idStr = txtReturnLoanId.getText().trim();
        if (idStr.isEmpty()) { setStatus("Enter a Loan ID.", warnColor); return; }
        try {
            int loanId  = Integer.parseInt(idStr);
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

    // ── BORROWERS ─────────────────────────────────────────────────────────

    private JTable borrowersTable;
    private DefaultTableModel borrowersModel;
    private JTextField txtBorrName, txtBorrId, txtBorrSchool;
    private JComboBox<String> cmbBorrType;

    private JPanel buildBorrowersPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addBar.setBackground(bgColor);
        addBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        txtBorrName = makeField("Full Name");
        txtBorrName.setPreferredSize(new Dimension(180, 36));

        cmbBorrType = new JComboBox<>(new String[]{"Student", "Guest"});
        styleComboBox(cmbBorrType);
        cmbBorrType.setPreferredSize(new Dimension(100, 36));

        txtBorrId = makeField("ID No.");
        txtBorrId.setPreferredSize(new Dimension(130, 36));

        txtBorrSchool = makeField("School / Institution");
        txtBorrSchool.setPreferredSize(new Dimension(180, 36));
        txtBorrSchool.setVisible(false);

        JButton btnAdd = makePrimaryButton("+ Add Borrower");
        btnAdd.setPreferredSize(new Dimension(140, 36));

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
        borrowersTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        borrowersTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        borrowersTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        borrowersTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        borrowersTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        borrowersTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        outer.add(makeScrollPane(borrowersTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bot.setBackground(bgColor);
        bot.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton btnLoans = makeOutlineButton("View Active Loans");
        btnLoans.setPreferredSize(new Dimension(160, 34));
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
                if      (p.startsWith("ID: "))     idNo   = p.replace("ID: ", "").trim();
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
                canBorrow   = database.canBorrow(idNoInt);
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

        if (name.isEmpty())  { setStatus("Enter a name.", warnColor); return; }
        if (idStr.isEmpty()) { setStatus("Enter an ID No.", warnColor); return; }

        if (type.equals("Student")) {
            if (!idStr.matches("\\d{10}"))   { setStatus("Student ID must be exactly 10 digits (e.g. 2024101001).", warnColor); return; }
            if (!idStr.startsWith("20"))     { setStatus("Student ID must start with '20' (e.g. 2024101001).", warnColor); return; }
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
            if (loans.isEmpty()) {
                JOptionPane.showMessageDialog(this, name + " has no active loans.", "Active Loans", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            StringBuilder sb = new StringBuilder("Active loans for " + name + ":\n\n");
            for (String l : loans) sb.append(l).append("\n");
            JOptionPane.showMessageDialog(this, sb.toString(), "Active Loans", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) { setStatus("Invalid ID.", errColor); }
    }

    // ── DASHBOARD ─────────────────────────────────────────────────────────

    private JLabel lblTotalBooks, lblAvailBooks, lblTotalBorrowers, lblActiveLoans, lblOverdue;

    private JPanel buildDashboardPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(28, 28, 28, 28));
        JLabel heading = new JLabel("Library Overview");
        heading.setFont(new Font("Georgia", Font.BOLD, 20)); heading.setForeground(textColor);
        heading.setBorder(new EmptyBorder(0, 0, 20, 0));
        outer.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setBackground(bgColor);
        lblTotalBooks     = new JLabel("\u2014"); lblAvailBooks    = new JLabel("\u2014");
        lblTotalBorrowers = new JLabel("\u2014"); lblActiveLoans   = new JLabel("\u2014");
        lblOverdue        = new JLabel("\u2014");
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
            BorderFactory.createLineBorder(divColor), new EmptyBorder(20, 24, 20, 24)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Dialog", Font.PLAIN, 11)); t.setForeground(grayText);
        valueLabel.setFont(new Font("Georgia", Font.BOLD, 36)); valueLabel.setForeground(valueColor);
        card.add(t, BorderLayout.NORTH); card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeRefreshCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(divColor), new EmptyBorder(20, 24, 20, 24)));
        JButton btn = makePrimaryButton("Refresh Stats");
        btn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { refreshDashboard(); } });
        card.add(btn);
        return card;
    }

    private void refreshDashboard() {
        db database = new db();
        List<String> allBooks = database.getAllBooks();
        int total = allBooks.size(), avail = 0;
        for (String b : allBooks) if (b.contains("Can Borrow: true")) avail++;
        List<String> allBorrowers = database.getAllBorrowers();
        int totalBorrowers = allBorrowers.size(), activeLoans = 0;
        for (String b : allBorrowers) {
            for (String p : b.split(" \\| ")) {
                if (p.startsWith("ID: ")) {
                    try {
                        int idNo = Integer.parseInt(p.replace("ID: ", "").trim());
                        activeLoans += database.getActiveLoansByBorrower(idNo).size();
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

    // ── NOTIFICATIONS ─────────────────────────────────────────────────────

    private JTextArea notifArea;

    private JPanel buildNotificationsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(bgColor);
        outer.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setBackground(bgColor); top.setBorder(new EmptyBorder(0, 0, 14, 0));
        JLabel heading = new JLabel("System Notifications & Overdue Alerts");
        heading.setFont(new Font("Georgia", Font.BOLD, 15)); heading.setForeground(textColor);
        JPanel topBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topBtns.setBackground(bgColor);
        JButton btnScan    = makePrimaryButton("Scan Overdue");
        JButton btnRefresh = makeOutlineButton("Refresh");
        btnScan.setPreferredSize(new Dimension(130, 34));
        btnRefresh.setPreferredSize(new Dimension(100, 34));
        btnScan.addActionListener(new ActionListener()    { public void actionPerformed(ActionEvent e) { scanOverdue(); } });
        btnRefresh.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { refreshNotifications(); } });
        topBtns.add(btnScan); topBtns.add(btnRefresh);
        top.add(heading, BorderLayout.CENTER); top.add(topBtns, BorderLayout.EAST);
        outer.add(top, BorderLayout.NORTH);

        notifArea = new JTextArea();
        notifArea.setEditable(false);
        notifArea.setBackground(tableBg); notifArea.setForeground(textColor);
        notifArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        notifArea.setBorder(new EmptyBorder(12, 14, 12, 14));
        JScrollPane scroll = new JScrollPane(notifArea);
        scroll.setBorder(BorderFactory.createLineBorder(divColor));
        scroll.getViewport().setBackground(tableBg);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    private void refreshNotifications() {
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
                    sb.append("\u2500\u2500 ").append(name).append(" (ID: ").append(idNo).append(") \u2500\u2500\n");
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

    // ── DB ACTIONS ────────────────────────────────────────────────────────

    public void addRecordsTyped(String mediaType, String genre, String dewey) {
        String title = txtTitle != null ? txtTitle.getText().trim() : "";
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
        else    setStatus("Database error. Check console.", errColor);
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

    // ── ABOUT ─────────────────────────────────────────────────────────────

    public void showAbout() {
        JDialog dlg = new JDialog(this, "About", true);
        dlg.setSize(380, 280); dlg.setLocationRelativeTo(this); dlg.setResizable(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor); panel.setBorder(new EmptyBorder(24, 30, 24, 30));
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        logoRow.setBackground(cardColor);
        if (iconEmblem != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconEmblem), 70, 52)));
        if (iconText   != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconText),  120, 42)));
        panel.add(logoRow, BorderLayout.NORTH);
        JPanel info = new JPanel(); info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(cardColor); info.setBorder(new EmptyBorder(18, 0, 12, 0));
        String[] lines = {"Mapua Cardinal Library \u2014 Makati Campus", "Library Management System v2.0", "", "Mapua University  |  Est. 1925"};
        for (String line : lines) {
            JLabel l = new JLabel(line.isEmpty() ? " " : line);
            l.setFont(line.contains("v2.0") || line.isEmpty() ? new Font("Dialog", Font.PLAIN, 12) : new Font("Georgia", Font.BOLD, 13));
            l.setForeground(line.contains("Est.") ? gold : textColor);
            l.setAlignmentX(Component.CENTER_ALIGNMENT); info.add(l);
        }
        panel.add(info, BorderLayout.CENTER);
        JButton ok = makePrimaryButton("Close"); ok.setPreferredSize(new Dimension(90, 34));
        ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { dlg.dispose(); } });
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(cardColor); btnRow.add(ok);
        panel.add(btnRow, BorderLayout.SOUTH);
        dlg.setContentPane(panel); dlg.setVisible(true);
    }

    // ── SHARED WIDGET HELPERS ─────────────────────────────────────────────

    private JPanel buildAddCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(700, 400));

        JPanel accent = new JPanel(new GridLayout(1, 2));
        accent.setPreferredSize(new Dimension(0, 4));
        JPanel a1 = new JPanel(); a1.setBackground(gold);
        JPanel a2 = new JPanel(); a2.setBackground(red);
        accent.add(a1); accent.add(a2);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cardColor);
        header.setBorder(new EmptyBorder(20, 32, 14, 32));
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Georgia", Font.BOLD, 22)); lblT.setForeground(textColor);
        JLabel lblS = new JLabel(subtitle);
        lblS.setFont(new Font("Dialog", Font.PLAIN, 12)); lblS.setForeground(grayText);
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

    private JPanel makeCard(String title, String subtitle, int w, int h) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(w, h));

        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 3));
        JPanel g1 = new JPanel(); g1.setBackground(gold);
        JPanel g2 = new JPanel(); g2.setBackground(red);
        strip.add(g1); strip.add(g2);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cardColor);
        header.setBorder(new EmptyBorder(16, 28, 12, 28));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Georgia", Font.BOLD, 18)); t.setForeground(textColor);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Dialog", Font.PLAIN, 11)); s.setForeground(grayText);
        header.add(t, BorderLayout.NORTH); header.add(s, BorderLayout.SOUTH);

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(cardColor);
        north.add(strip,  BorderLayout.NORTH);
        north.add(header, BorderLayout.CENTER);
        JSeparator sep = new JSeparator(); sep.setForeground(divColor);
        north.add(sep, BorderLayout.SOUTH);
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
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(grayText);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel makeInlineRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(cardColor);
        row.setMaximumSize(new Dimension(9999, 36));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Dialog", Font.BOLD, 11));
        lbl.setForeground(grayText);
        lbl.setPreferredSize(new Dimension(110, 36));
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 11));
        l.setForeground(grayText);
        return l;
    }

    private void styleComboBox(JComboBox<String> cmb) {
        cmb.setBackground(fieldBg); cmb.setForeground(textColor);
        cmb.setFont(new Font("Dialog", Font.PLAIN, 13));
        cmb.setBorder(BorderFactory.createLineBorder(fieldBorder, 1));
        cmb.setPreferredSize(new Dimension(0, 36));
        cmb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? cardColor : fieldBg); setForeground(sel ? gold : textColor);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
    }

    private JTextField makeField(final String placeholder) {
        JTextField tf = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(grayText);
                    g2.setFont(new Font("Dialog", Font.ITALIC, 12));
                    Insets ins = getInsets(); FontMetrics fm = g2.getFontMetrics();
                    int y = ins.top + (getHeight()-ins.top-ins.bottom-fm.getHeight())/2+fm.getAscent();
                    g2.drawString(placeholder, ins.left+4, y); g2.dispose();
                }
            }
        };
        tf.setFont(new Font("Dialog", Font.PLAIN, 13)); tf.setForeground(textColor);
        tf.setBackground(fieldBg); tf.setCaretColor(gold); tf.setPreferredSize(new Dimension(0, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldBorder, 1), new EmptyBorder(4,10,4,10)));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(gold,1),new EmptyBorder(4,10,4,10))); tf.repaint(); }
            public void focusLost (FocusEvent e) { tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fieldBorder,1),new EmptyBorder(4,10,4,10))); tf.repaint(); }
        });
        return tf;
    }

    private JTable makeStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(tableBg); table.setForeground(textColor);
        table.setGridColor(divColor); table.setRowHeight(30);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.setSelectionBackground(cardColor); table.setSelectionForeground(gold);
        table.setShowHorizontalLines(true); table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(tableHeader);
        table.getTableHeader().setForeground(grayText);
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 11));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,divColor));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(sel ? cardColor : (r % 2 == 0 ? tableBg : tableAlt));
                setForeground(sel ? gold : textColor);
                setBorder(new EmptyBorder(0, 10, 0, 10));
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
                g2.setColor(getModel().isPressed() ? darkGold : getModel().isRollover() ? new Color(220,172,32) : gold);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6); g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 13)); btn.setForeground(new Color(21,7,7));
        btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setPreferredSize(new Dimension(155, 38));
        return btn;
    }

    private JButton makeOutlineButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? divColor : getModel().isRollover() ? panelColor : cardColor);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.setColor(fieldBorder); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.PLAIN, 13)); btn.setForeground(textColor);
        btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setPreferredSize(new Dimension(120, 38));
        return btn;
    }
}
