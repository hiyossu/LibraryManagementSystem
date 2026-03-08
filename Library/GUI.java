package Library;

import Library.actions.BoardGameActions;
import Library.actions.BookActions;
import Library.actions.DvdActions;
import Library.forms.BoardGameFormCard;
import Library.forms.BookFormCard;
import Library.forms.DvdFormCard;
import Library.ui.UIFactory;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class GUI extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────
    private final Color bgColor      = new Color(13, 10, 10);
    private final Color sidebarColor = new Color(18, 5, 5);
    private final Color panelColor   = new Color(30, 8, 8);
    private final Color cardColor    = new Color(34, 12, 12);
    private final Color gold         = new Color(212, 160, 23);
    private final Color darkGold     = new Color(154, 114, 10);
    private final Color red          = new Color(190, 16, 16);
    private final Color textColor    = new Color(245, 236, 220);
    private final Color grayText     = new Color(136, 120, 104);
    private final Color divColor     = new Color(58, 20, 20);
    private final Color successColor = new Color(107, 181, 114);
    private final Color warnColor    = new Color(232, 168, 64);
    private final Color errColor     = new Color(192, 80, 80);
    private final Color fieldBg      = new Color(16, 4, 4);
    private final Color fieldBorder  = new Color(74, 24, 24);

    // ── Shared infrastructure ─────────────────────────────────────────────
    private FormContext ctx;
    private UIFactory   ui;

    // ── Swappable layout references ───────────────────────────────────────
    private JPanel cardContainer;
    private JLabel pageTitle;
    private JLabel crumb;

    // ── Sidebar nav buttons (kept so we can toggle active state) ─────────
    private JPanel navBtnBook;
    private JPanel navBtnDvd;
    private JPanel navBtnBoardGame;

    // ── Logos ─────────────────────────────────────────────────────────────
    private ImageIcon iconEmblem;
    private ImageIcon iconText;

    // ─────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        EventQueue.invokeLater(() -> new GUI().setVisible(true));
    }

    public GUI() {
        loadLogos();

        // Build shared infrastructure first
        ui  = new UIFactory(gold, darkGold, red, textColor, grayText,
                            divColor, panelColor, cardColor, fieldBg, fieldBorder);
        ctx = new FormContext(null,   // lblStatus wired in buildMainPanel()
                              successColor, warnColor, errColor, grayText);

        setTitle("Mapua Cardinal Library - Makati");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setMinimumSize(new Dimension(820, 540));
        setLocationRelativeTo(null);
        setBackground(bgColor);
        if (iconEmblem != null) setIconImage(iconEmblem.getImage());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgColor);
        setContentPane(root);
        root.add(buildSidebar(),    BorderLayout.WEST);
        root.add(buildMainPanel(), BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  LOGOS
    // ─────────────────────────────────────────────────────────────────────
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

        java.util.List<String> searchPaths = new java.util.ArrayList<>();
        if (classLocation != null) {
            java.io.File dir = classLocation;
            for (int i = 0; i < 4; i++) {
                searchPaths.add(dir.getAbsolutePath() + "/resources/" + filename);
                dir = dir.getParentFile();
                if (dir == null) break;
            }
        }
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
        int w = (int)(img.getWidth()  * scale);
        int h = (int)(img.getHeight() * scale);
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    // ─────────────────────────────────────────────────────────────────────
    //  SIDEBAR
    // ─────────────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, divColor));

        sidebar.add(buildLogoArea());
        sidebar.add(makeDividerLabel("NAVIGATION"));

        navBtnBook      = makeNavBtn("+ Add New Book",       "add",         true);
        navBtnDvd       = makeNavBtn("+ Add New DVD",        "addDvd",      false);
        navBtnBoardGame = makeNavBtn("+ Add New Board Game", "addBoardGame", false);

        sidebar.add(navBtnBook);
        sidebar.add(navBtnDvd);
        sidebar.add(navBtnBoardGame);
        sidebar.add(makeNavBtn("  Dashboard",    "dashboard", false));
        sidebar.add(makeNavBtn("  Clear Fields", "clear",     false));
        sidebar.add(makeDividerLabel("SYSTEM"));
        sidebar.add(makeNavBtn("  About",        "about",     false));
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
            JLabel fb = new JLabel("🏛");
            fb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            fb.setForeground(gold);
            fb.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(fb);
            logoArea.add(Box.createVerticalStrut(6));
        }

        if (iconText != null) {
            JLabel lbl = new JLabel(iconText);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(0, 0, 2, 0));
            logoArea.add(lbl);
        } else {
            JLabel lbl = new JLabel("Mapua University");
            lbl.setFont(new Font("Georgia", Font.BOLD, 13));
            lbl.setForeground(red);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoArea.add(lbl);
        }

        JLabel campus = new JLabel("Cardinal Library · Makati");
        campus.setFont(new Font("Georgia", Font.ITALIC, 10));
        campus.setForeground(grayText);
        campus.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoArea.add(campus);
        logoArea.add(Box.createVerticalStrut(10));

        JPanel stripe = new JPanel(new GridLayout(1, 2));
        stripe.setMaximumSize(new Dimension(235, 3));
        stripe.setPreferredSize(new Dimension(235, 3));
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
        JLabel admin = new JLabel("ADMIN SESSION");
        admin.setFont(new Font("Monospaced", Font.BOLD, 10));
        admin.setForeground(gold);
        footer.add(admin, BorderLayout.NORTH);
        JLabel ver = new JLabel("LMS v2.0  2025");
        ver.setFont(new Font("Dialog", Font.PLAIN, 10));
        ver.setForeground(grayText);
        footer.add(ver, BorderLayout.SOUTH);
        return footer;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  NAV BUTTON
    // ─────────────────────────────────────────────────────────────────────
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
            public void mouseEntered(MouseEvent e) {
                if (!isActiveBtn(btn)) btn.setBackground(panelColor);
            }
            public void mouseExited(MouseEvent e) {
                if (!isActiveBtn(btn)) btn.setBackground(sidebarColor);
            }
            public void mouseClicked(MouseEvent e) {
                switch (action) {
                    case "add":          switchToBookMode();      break;
                    case "addDvd":       switchToDvdMode();       break;
                    case "addBoardGame": switchToBoardGameMode(); break;
                    case "dashboard":    displayDashboard();      break;
                    case "clear":        ctx.clearFields();       break;
                    case "about":        showAbout();             break;
                }
            }
        });
        return btn;
    }

    private boolean isActiveBtn(JPanel btn) {
        return btn.getBackground().equals(cardColor)
            && btn.getBorder() instanceof CompoundBorder;
    }

    private void setNavActive(JPanel btn, boolean active) {
        JLabel lbl = (JLabel) btn.getComponent(0);
        if (active) {
            btn.setBackground(cardColor);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, gold),
                new EmptyBorder(10, 17, 10, 16)));
            lbl.setForeground(gold);
        } else {
            btn.setBackground(sidebarColor);
            btn.setBorder(new EmptyBorder(10, 20, 10, 16));
            lbl.setForeground(textColor);
        }
        btn.repaint();
    }

    // ─────────────────────────────────────────────────────────────────────
    //  MODE SWITCHING
    // ─────────────────────────────────────────────────────────────────────
    private void switchToBookMode() {
        setNavActive(navBtnBook, true);
        setNavActive(navBtnDvd, false);
        setNavActive(navBtnBoardGame, false);
        pageTitle.setText("Add New Book Record");
        crumb.setText("Library Catalog / Add Book");
        BookActions actions = new BookActions(ctx, this);
        swapFormCard(new BookFormCard(ctx, ui, gold, red, cardColor,
            divColor, grayText, textColor)
            .build(actions::addRecord, ctx::clearFields, this::displayDashboard));
    }

    private void switchToDvdMode() {
        setNavActive(navBtnBook, false);
        setNavActive(navBtnDvd, true);
        setNavActive(navBtnBoardGame, false);
        pageTitle.setText("Add New DVD Record");
        crumb.setText("Library Catalog / Add DVD");
        DvdActions actions = new DvdActions(ctx, this);
        swapFormCard(new DvdFormCard(ctx, ui, gold, red, cardColor,
            divColor, grayText, textColor)
            .build(actions::addRecord, ctx::clearFields, this::displayDashboard));
    }

    private void switchToBoardGameMode() {
        setNavActive(navBtnBook, false);
        setNavActive(navBtnDvd, false);
        setNavActive(navBtnBoardGame, true);
        pageTitle.setText("Add New Board Game Record");
        crumb.setText("Library Catalog / Add Board Game");
        BoardGameActions actions = new BoardGameActions(ctx, this);
        swapFormCard(new BoardGameFormCard(ctx, ui, gold, cardColor,
            divColor, grayText, textColor)
            .build(actions::addRecord, ctx::clearFields, this::displayDashboard));
    }

    private void swapFormCard(JPanel newCard) {
        cardContainer.removeAll();
        cardContainer.add(newCard);
        cardContainer.revalidate();
        cardContainer.repaint();
    }

    // ─────────────────────────────────────────────────────────────────────
    //  MAIN PANEL
    // ─────────────────────────────────────────────────────────────────────
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
        pageTitle = new JLabel("Add New Book Record");
        pageTitle.setFont(new Font("Georgia", Font.BOLD, 17));
        pageTitle.setForeground(textColor);
        topBar.add(pageTitle, BorderLayout.CENTER);
        crumb = new JLabel("Library Catalog / Add Book");
        crumb.setFont(new Font("Dialog", Font.PLAIN, 11));
        crumb.setForeground(grayText);
        topBar.add(crumb, BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        // Card container (swappable center)
        cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setBackground(bgColor);

        // Wire the initial Book form
        BookActions bookActions = new BookActions(ctx, this);
        cardContainer.add(new BookFormCard(ctx, ui, gold, red, cardColor,
            divColor, grayText, textColor)
            .build(bookActions::addRecord, ctx::clearFields, this::displayDashboard));
        main.add(cardContainer, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(sidebarColor);
        statusBar.setPreferredSize(new Dimension(0, 32));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, divColor),
            new EmptyBorder(0, 20, 0, 20)));
        JLabel lblStatus = new JLabel("System ready");
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblStatus.setForeground(grayText);
        statusBar.add(lblStatus, BorderLayout.CENTER);
        ctx.lblStatus = lblStatus;   // wire into shared context NOW that it exists

        JLabel brand = new JLabel("Mapua Cardinal Library LMS");
        brand.setFont(new Font("Dialog", Font.PLAIN, 10));
        brand.setForeground(grayText);
        statusBar.add(brand, BorderLayout.EAST);
        main.add(statusBar, BorderLayout.SOUTH);

        return main;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  MISC ACTIONS  (stay in GUI since they touch the frame directly)
    // ─────────────────────────────────────────────────────────────────────
    private void displayDashboard() {
        ctx.lblStatus.setForeground(gold);
        ctx.lblStatus.setText("Dashboard coming soon.");
        JOptionPane.showMessageDialog(this, "Dashboard is under construction.",
            "Dashboard", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JDialog dlg = new JDialog(this, "About", true);
        dlg.setSize(380, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor);
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        logoRow.setBackground(cardColor);
        if (iconEmblem != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconEmblem), 70, 52)));
        if (iconText   != null) logoRow.add(new JLabel(scaleIcon(toBufferedImage(iconText),  120, 42)));
        panel.add(logoRow, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(cardColor);
        info.setBorder(new EmptyBorder(18, 0, 12, 0));
        for (String line : new String[]{
                "Mapua Cardinal Library — Makati Campus",
                "Library Management System v2.0", "",
                "Mapua University  |  Est. 1925"}) {
            JLabel l = new JLabel(line.isEmpty() ? " " : line);
            l.setFont(line.contains("v2.0") || line.isEmpty()
                ? new Font("Dialog", Font.PLAIN, 12)
                : new Font("Georgia", Font.BOLD, 13));
            l.setForeground(line.contains("Est.") ? gold : textColor);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.add(l);
        }
        panel.add(info, BorderLayout.CENTER);

        JButton ok = ui.makePrimaryButton("   Close   ");
        ok.setPreferredSize(new Dimension(90, 34));
        ok.addActionListener(e -> dlg.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(cardColor);
        btnRow.add(ok);
        panel.add(btnRow, BorderLayout.SOUTH);

        dlg.setContentPane(panel);
        dlg.setVisible(true);
    }

    private BufferedImage toBufferedImage(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(
            icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }
}