package Library;
import DB.db;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class GUI extends JFrame {
    // colors
    private Color bgColor      = new Color(13, 10, 10);
    private Color sidebarColor = new Color(18, 5, 5);
    private Color panelColor   = new Color(30, 8, 8);
    private Color cardColor    = new Color(34, 12, 12);
    private Color gold         = new Color(212, 160, 23);
    private Color darkGold     = new Color(154, 114, 10);
    private Color red          = new Color(190, 16, 16);
    private Color textColor    = new Color(245, 236, 220);
    private Color grayText     = new Color(136, 120, 104);
    private Color divColor     = new Color(58, 20, 20);
    private Color successColor = new Color(107, 181, 114);
    private Color warnColor    = new Color(232, 168, 64);
    private Color errColor     = new Color(192, 80, 80);
    private Color fieldBg      = new Color(16, 4, 4);
    private Color fieldBorder  = new Color(74, 24, 24);

    private JTextField txtTitle;
    private JTextField txtType;
    private JTextField txtGenre;
    private JTextField txtDewey;
    private JLabel     lblStatus;

    // Logo images loaded from resources
    private ImageIcon iconEmblem;
    private ImageIcon iconText;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public GUI() {
        loadLogos();
        setTitle("Mapua Cardinal Library - Makati");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setMinimumSize(new Dimension(820, 540));
        setLocationRelativeTo(null);
        setBackground(bgColor);

        // Set application icon to the emblem if loaded
        if (iconEmblem != null) {
            setIconImage(iconEmblem.getImage());
        }

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        setContentPane(root);
        root.add(buildSidebar(),    BorderLayout.WEST);
        root.add(buildMainPanel(), BorderLayout.CENTER);
    }

    /**
     * Loads both logo images from the resources folder (on the classpath).
     * Falls back gracefully if the files are missing.
     */
    private void loadLogos() {
        iconEmblem = loadIcon("logo_emblem_small.png", 100, 75);
        iconText   = loadIcon("logo_text_small.png",   150, 53);
    }

    private ImageIcon loadIcon(String filename, int maxW, int maxH) {
        // Find the project root by walking up from the class file location
        java.io.File classLocation = null;
        try {
            java.net.URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
            classLocation = new java.io.File(url.toURI());
        } catch (Exception ignored) {}

        java.util.List<String> searchPaths = new java.util.ArrayList<>();
        // Add paths relative to class/bin location (walk up to find project root)
        if (classLocation != null) {
            java.io.File dir = classLocation;
            for (int i = 0; i < 4; i++) {
                searchPaths.add(dir.getAbsolutePath() + "/resources/" + filename);
                dir = dir.getParentFile();
                if (dir == null) break;
            }
        }
        // Add working-directory-relative paths
        searchPaths.add("resources/" + filename);
        searchPaths.add("Library/resources/" + filename);
        searchPaths.add(filename);

        for (String p : searchPaths) {
            try {
                java.io.File f = new java.io.File(p);
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) return scaleIcon(img, maxW, maxH);
                }
            } catch (Exception ignored) {}
        }
        // Classpath fallback
        try {
            InputStream is = getClass().getResourceAsStream("/" + filename);
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                is.close();
                return scaleIcon(img, maxW, maxH);
            }
        } catch (Exception ignored) {}
        System.err.println("Logo not found: " + filename);
        return null;
    }

    private ImageIcon scaleIcon(BufferedImage img, int maxW, int maxH) {
        if (img == null) return null;
        double scale = Math.min((double) maxW / img.getWidth(), (double) maxH / img.getHeight());
        int w = (int) (img.getWidth()  * scale);
        int h = (int) (img.getHeight() * scale);
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ─────────────────────────────────────────────────────────────
    //  SIDEBAR
    // ─────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, divColor));

        sidebar.add(buildLogoArea());
        sidebar.add(makeDividerLabel("NAVIGATION"));
        sidebar.add(makeNavBtn("+ Add New Book", "add",       true));
        sidebar.add(makeNavBtn("  Dashboard",    "dashboard", false));
        sidebar.add(makeNavBtn("  Clear Fields", "clear",     false));
        sidebar.add(makeDividerLabel("SYSTEM"));
        sidebar.add(makeNavBtn("  About",        "about",     false));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildSidebarFooter());
        return sidebar;
    }

    /**
     * Logo area: emblem image on top, "MAPÚA UNIVERSITY" text logo below,
     * campus label, then the red accent stripe.
     */
    private JPanel buildLogoArea() {
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBackground(sidebarColor);
        logoArea.setMaximumSize(new Dimension(235, 220));
        logoArea.setBorder(new EmptyBorder(18, 0, 12, 0));
        logoArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Emblem image (or fallback emoji) ──────────────────────
        if (iconEmblem != null) {
            JLabel emblemLbl = new JLabel(iconEmblem);
            emblemLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Tint effect: wrap in a panel with dark bg so the image blends in
            emblemLbl.setBorder(new EmptyBorder(0, 0, 6, 0));
            logoArea.add(emblemLbl);
        } else {
            JLabel fallback = new JLabel("🏛");
            fallback.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            fallback.setForeground(gold);
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(fallback);
            logoArea.add(Box.createVerticalStrut(6));
        }

        // ── MAPÚA UNIVERSITY text logo (or plain text fallback) ───
        if (iconText != null) {
            JLabel textLogoLbl = new JLabel(iconText);
            textLogoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            textLogoLbl.setBorder(new EmptyBorder(0, 0, 2, 0));
            logoArea.add(textLogoLbl);
        } else {
            JLabel nameLabel = new JLabel("Mapua University");
            nameLabel.setFont(new Font("Georgia", Font.BOLD, 13));
            nameLabel.setForeground(red);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(nameLabel);
        }

        // ── Campus sub-label ──────────────────────────────────────
        JLabel campusLabel = new JLabel("Cardinal Library · Makati");
        campusLabel.setFont(new Font("Georgia", Font.ITALIC, 10));
        campusLabel.setForeground(grayText);
        campusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoArea.add(campusLabel);
        logoArea.add(Box.createVerticalStrut(10));

        // ── Gold / Red accent stripe ──────────────────────────────
        JPanel stripe = new JPanel(new GridLayout(1, 2));
        stripe.setMaximumSize(new Dimension(235, 3));
        stripe.setPreferredSize(new Dimension(235, 3));
        JPanel g1 = new JPanel(); g1.setBackground(gold);
        JPanel g2 = new JPanel(); g2.setBackground(red);
        stripe.add(g1);
        stripe.add(g2);
        logoArea.add(stripe);

        return logoArea;
    }

    private JPanel buildSidebarFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(sidebarColor);
        footer.setBorder(new EmptyBorder(12, 20, 16, 20));
        footer.setMaximumSize(new Dimension(235, 60));

        JLabel adminLbl = new JLabel("ADMIN SESSION");
        adminLbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        adminLbl.setForeground(gold);
        footer.add(adminLbl, BorderLayout.NORTH);

        JLabel verLbl = new JLabel("LMS v2.0  2025");
        verLbl.setFont(new Font("Dialog", Font.PLAIN, 10));
        verLbl.setForeground(grayText);
        footer.add(verLbl, BorderLayout.SOUTH);
        return footer;
    }

    // ─────────────────────────────────────────────────────────────
    //  NAV HELPERS
    // ─────────────────────────────────────────────────────────────
    private JPanel makeDividerLabel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(sidebarColor);
        p.setMaximumSize(new Dimension(235, 30));
        p.setBorder(new EmptyBorder(8, 20, 2, 20));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Dialog", Font.BOLD, 9));
        lbl.setForeground(grayText);
        p.add(lbl, BorderLayout.CENTER);

        JSeparator sep = new JSeparator();
        sep.setForeground(divColor);
        p.add(sep, BorderLayout.SOUTH);
        return p;
    }

    private JPanel makeNavBtn(String text, String action, boolean active) {
        JPanel btn = new JPanel(new BorderLayout());
        btn.setBackground(active ? cardColor : sidebarColor);
        btn.setMaximumSize(new Dimension(235, 42));

        if (active) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, gold),
                new EmptyBorder(10, 17, 10, 16)));
        } else {
            btn.setBorder(new EmptyBorder(10, 20, 10, 16));
        }

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 13));
        lbl.setForeground(active ? gold : textColor);
        btn.add(lbl, BorderLayout.CENTER);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (!active) btn.setBackground(panelColor); }
            public void mouseExited (MouseEvent e) { if (!active) btn.setBackground(sidebarColor); }
            public void mouseClicked(MouseEvent e) {
                if      (action.equals("add"))       addRecords();
                else if (action.equals("dashboard")) displayDashboard();
                else if (action.equals("clear"))     clearFields();
                else if (action.equals("about"))     showAbout();
            }
        });
        return btn;
    }

    // ─────────────────────────────────────────────────────────────
    //  MAIN PANEL
    // ─────────────────────────────────────────────────────────────
    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bgColor);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(panelColor);
        topBar.setPreferredSize(new Dimension(0, 54));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, red),
            new EmptyBorder(0, 24, 0, 24)));

        JLabel pageTitle = new JLabel("Add New Book Record");
        pageTitle.setFont(new Font("Georgia", Font.BOLD, 17));
        pageTitle.setForeground(textColor);
        topBar.add(pageTitle, BorderLayout.CENTER);

        JLabel crumb = new JLabel("Library Catalog / Add Record");
        crumb.setFont(new Font("Dialog", Font.PLAIN, 11));
        crumb.setForeground(grayText);
        topBar.add(crumb, BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        // Center form
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(bgColor);
        center.add(buildFormCard());
        main.add(center, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(sidebarColor);
        statusBar.setPreferredSize(new Dimension(0, 32));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, divColor),
            new EmptyBorder(0, 20, 0, 20)));

        lblStatus = new JLabel("System ready");
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblStatus.setForeground(grayText);
        statusBar.add(lblStatus, BorderLayout.CENTER);

        JLabel brandLbl = new JLabel("Mapua Cardinal Library LMS");
        brandLbl.setFont(new Font("Dialog", Font.PLAIN, 10));
        brandLbl.setForeground(grayText);
        statusBar.add(brandLbl, BorderLayout.EAST);
        main.add(statusBar, BorderLayout.SOUTH);

        return main;
    }

    // ─────────────────────────────────────────────────────────────
    //  FORM CARD
    // ─────────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(gold);
                g.fillRect(0, 0, getWidth(), 3);
            }
        };
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(divColor, 1));
        card.setPreferredSize(new Dimension(560, 450));

        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx   = 0;

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cardColor);
        header.setBorder(new EmptyBorder(20, 28, 12, 28));

        JLabel title = new JLabel("Book Registration");
        title.setFont(new Font("Georgia", Font.BOLD, 21));
        title.setForeground(textColor);

        JLabel subtitle = new JLabel("Enter bibliographic details for a new catalog entry.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(grayText);

        header.add(title,    BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        c.gridy  = 0; c.weighty = 0;
        card.add(header, c);

        // Gold/Red strip
        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setPreferredSize(new Dimension(0, 3));
        JPanel gs1 = new JPanel(); gs1.setBackground(gold);
        JPanel gs2 = new JPanel(); gs2.setBackground(red);
        strip.add(gs1); strip.add(gs2);
        c.gridy = 1;
        card.add(strip, c);

        // Form fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(cardColor);
        form.setBorder(new EmptyBorder(20, 28, 20, 28));

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill   = GridBagConstraints.HORIZONTAL;
        fc.anchor = GridBagConstraints.WEST;

        txtTitle = makeField("e.g., The Great Gatsby");
        addRow(form, fc, "Book Title",    txtTitle, 0);
        txtType = makeField("e.g., Novel, Reference...");
        addRow(form, fc, "Media Type",    txtType,  1);
        txtGenre = makeField("e.g., Fiction, Science...");
        addRow(form, fc, "Genre",         txtGenre, 2);
        txtDewey = makeField("e.g., 813.54");
        addRow(form, fc, "Dewey Decimal", txtDewey, 3);

        fc.gridy = 4; fc.gridx = 0; fc.gridwidth = 4;
        fc.insets = new Insets(22, 0, 0, 0);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setBackground(cardColor);

        JButton btnAdd   = makePrimaryButton("+ Add to Catalog");
        JButton btnClear = makeOutlineButton("Clear");
        JButton btnDash  = makeOutlineButton("Dashboard");

        btnAdd.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { addRecords(); } });
        btnClear.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { clearFields(); } });
        btnDash.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { displayDashboard(); } });

        buttons.add(btnAdd);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(btnClear);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(btnDash);

        form.add(buttons, fc);
        c.gridy = 2; c.weighty = 1;
        card.add(form, c);
        return card;
    }

    private void addRow(JPanel p, GridBagConstraints fc, String labelText, JTextField field, int row) {
        fc.gridy = row; fc.gridx = 0; fc.weightx = 0; fc.gridwidth = 1;
        fc.insets = new Insets(8, 0, 8, 10);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Dialog", Font.BOLD, 11));
        lbl.setForeground(grayText);
        lbl.setPreferredSize(new Dimension(95, 20));
        p.add(lbl, fc);

        fc.gridx = 1; fc.weightx = 1; fc.gridwidth = 3;
        fc.insets = new Insets(8, 0, 8, 0);
        p.add(field, fc);
    }

    private JTextField makeField(String placeholder) {
        JTextField tf = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(grayText);
                    g2.setFont(new Font("Dialog", Font.ITALIC, 12));
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int y = ins.top + (getHeight() - ins.top - ins.bottom - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, ins.left + 4, y);
                    g2.dispose();
                }
            }
        };
        tf.setFont(new Font("Dialog", Font.PLAIN, 13));
        tf.setForeground(textColor);
        tf.setBackground(fieldBg);
        tf.setCaretColor(gold);
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldBorder, 1),
            new EmptyBorder(4, 10, 4, 10)));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(gold, 1), new EmptyBorder(4, 10, 4, 10)));
                tf.repaint();
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(fieldBorder, 1), new EmptyBorder(4, 10, 4, 10)));
                tf.repaint();
            }
        });
        return tf;
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if      (getModel().isPressed())  g2.setColor(darkGold);
                else if (getModel().isRollover()) g2.setColor(new Color(220, 172, 32));
                else                              g2.setColor(gold);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setForeground(new Color(21, 7, 7));
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(155, 38));
        return btn;
    }

    private JButton makeOutlineButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if      (getModel().isPressed())  g2.setColor(divColor);
                else if (getModel().isRollover()) g2.setColor(panelColor);
                else                              g2.setColor(cardColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(fieldBorder);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.PLAIN, 13));
        btn.setForeground(textColor);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────
    //  ACTIONS
    // ─────────────────────────────────────────────────────────────
    public void addRecords() {
        String title = txtTitle.getText().trim();
        String type  = txtType.getText().trim();
        String genre = txtGenre.getText().trim();
        String dewey = txtDewey.getText().trim();

        if (title.isEmpty() || type.isEmpty() || genre.isEmpty() || dewey.isEmpty()) {
            lblStatus.setForeground(warnColor);
            lblStatus.setText("Please fill in all fields.");
            JOptionPane.showMessageDialog(this, "Please complete all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        db database = new db();
        book newBook = new book(title, type, genre, dewey);
        boolean success = database.addBook(newBook);
        if (success) {
            lblStatus.setForeground(successColor);
            lblStatus.setText("Added: \"" + title + "\" successfully.");
            clearFields();
        } else {
            lblStatus.setForeground(errColor);
            lblStatus.setText("Database error. Check console.");
        }
        database.closeConnection();
    }

    public void displayDashboard() {
        lblStatus.setForeground(gold);
        lblStatus.setText("Dashboard coming soon.");
        JOptionPane.showMessageDialog(this, "Dashboard is under construction.", "Dashboard", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtTitle.setText(""); txtType.setText("");
        txtGenre.setText(""); txtDewey.setText("");
        lblStatus.setForeground(grayText);
        lblStatus.setText("Fields cleared.");
    }

    public void showAbout() {
        // Build a custom dialog with both logos
        JDialog dlg = new JDialog(this, "About", true);
        dlg.setSize(380, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor);
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        logoRow.setBackground(cardColor);

        if (iconEmblem != null) {
            // Scale down further for the dialog
            ImageIcon smallEmblem = scaleIcon(toBufferedImage(iconEmblem), 70, 52);
            logoRow.add(new JLabel(smallEmblem));
        }
        if (iconText != null) {
            ImageIcon smallText = scaleIcon(toBufferedImage(iconText), 120, 42);
            logoRow.add(new JLabel(smallText));
        }
        panel.add(logoRow, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(cardColor);
        info.setBorder(new EmptyBorder(18, 0, 12, 0));

        String[] lines = {
            "Mapua Cardinal Library — Makati Campus",
            "Library Management System v2.0",
            "",
            "Mapua University  |  Est. 1925"
        };
        for (String line : lines) {
            JLabel l = new JLabel(line.isEmpty() ? " " : line);
            l.setFont(line.contains("v2.0") || line.isEmpty()
                ? new Font("Dialog", Font.PLAIN, 12)
                : new Font("Georgia", Font.BOLD, 13));
            l.setForeground(line.contains("Est.") ? gold : textColor);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.add(l);
        }
        panel.add(info, BorderLayout.CENTER);

        JButton ok = makePrimaryButton("   Close   ");
        ok.setPreferredSize(new Dimension(90, 34));
        ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { dlg.dispose(); } });
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(cardColor);
        btnRow.add(ok);
        panel.add(btnRow, BorderLayout.SOUTH);

        dlg.setContentPane(panel);
        dlg.getContentPane().setBackground(cardColor);
        dlg.setVisible(true);
    }

    /** Helper: convert ImageIcon back to BufferedImage for re-scaling. */
    private BufferedImage toBufferedImage(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(
            icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }
}