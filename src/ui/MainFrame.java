package ui;

import dao.StudentDAO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color DARK_HEADER = new Color(18, 24, 40);
    private static final Color ACCENT      = new Color(59, 130, 246);
    private static final Color ACCENT_DARK = new Color(29, 78, 216);
    private static final Color TEAL        = new Color(20, 184, 166);
    private static final Color GREEN       = new Color(34, 197, 94);
    private static final Color BG          = new Color(15, 20, 40);
    private static final Color CARD_BG     = new Color(22, 30, 52);
    private static final Color CARD_BORDER = new Color(40, 52, 80);
    private static final Color TEXT_BRIGHT = new Color(241, 245, 249);
    private static final Color TEXT_DIM    = new Color(148, 163, 184);

    private StudentTable tablePanel;
    private StudentForm  formPanel;
    private JTabbedPane  tabs;

    public MainFrame() {
        setTitle("Student Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1160, 760);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(),    BorderLayout.NORTH);
        tabs = buildTabbedPane();
        add(tabs,             BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Tabbed pane ───────────────────────────────────────────────────────────

    private JTabbedPane buildTabbedPane() {
        JTabbedPane tp = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tp.setOpaque(true);
        tp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tp.setBackground(BG);
        tp.setForeground(TEXT_DIM);
        tp.setBorder(BorderFactory.createEmptyBorder());

        tp.addTab("  Dashboard  ", buildDashboard());

        tablePanel = new StudentTable();
        tp.addTab("  Students  ", tablePanel);

        formPanel = new StudentForm(() -> {
            tablePanel.load();
            tp.setSelectedIndex(1);
        });
        tp.addTab("  Add Student  ", formPanel);

        tablePanel.setFormRef(formPanel);
        tablePanel.setMainFrame(this);
        return tp;
    }

    public void showFormWithStudent(model.Student s) {
        tabs.setSelectedIndex(2);
        formPanel.loadForEdit(s);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(DARK_HEADER);
        h.setPreferredSize(new Dimension(0, 56));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 52, 80)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255, 255, 255, 200));
                int s = 3, gap = 3, cx = getWidth() / 2, cy = getHeight() / 2;
                g2.fillOval(cx - s - gap, cy - s - gap, s, s);
                g2.fillOval(cx + gap,     cy - s - gap, s, s);
                g2.fillOval(cx - s - gap, cy + gap,     s, s);
                g2.fillOval(cx + gap,     cy + gap,     s, s);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(28, 28));
        logo.setOpaque(false);

        JLabel title = new JLabel("Student Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_BRIGHT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        left.add(logo);
        left.add(title);

        JLabel stack = new JLabel("Java  ·  Swing  ·  JDBC  ·  MySQL");
        stack.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stack.setForeground(new Color(71, 85, 105));
        stack.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));

        h.add(left,  BorderLayout.WEST);
        h.add(stack, BorderLayout.EAST);
        return h;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    private JPanel buildDashboard() {
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // ── Top: heading block LEFT + stat cards RIGHT ────────────────────
        JPanel topSection = new JPanel(new BorderLayout(32, 0));
        topSection.setOpaque(false);
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        // Heading block
        JPanel headingBlock = new JPanel();
        headingBlock.setLayout(new BoxLayout(headingBlock, BoxLayout.Y_AXIS));
        headingBlock.setOpaque(false);
        headingBlock.setPreferredSize(new Dimension(260, 130));

        JLabel overline = new JLabel("OVERVIEW");
        overline.setFont(new Font("Segoe UI", Font.BOLD, 10));
        overline.setForeground(ACCENT);
        overline.setAlignmentX(LEFT_ALIGNMENT);
        overline.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        pageTitle.setForeground(TEXT_BRIGHT);
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel pageSub = new JLabel(
            "<html><body style='color:#64748b;width:210px'>"
            + "Manage students, fees, library status and academic records."
            + "</body></html>");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageSub.setAlignmentX(LEFT_ALIGNMENT);
        pageSub.setBorder(BorderFactory.createEmptyBorder(8, 0, 20, 0));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        RoundButton addBtn  = new RoundButton("+ Add Student",  ACCENT,           ACCENT_DARK);
        RoundButton viewBtn = new RoundButton("View Students",  new Color(30, 42, 70), new Color(22, 32, 58));
        viewBtn.setForeground(TEXT_DIM);

        addBtn.addActionListener(e  -> tabs.setSelectedIndex(2));
        viewBtn.addActionListener(e -> { tablePanel.load(); tabs.setSelectedIndex(1); });

        btnRow.add(addBtn);
        btnRow.add(viewBtn);

        headingBlock.add(overline);
        headingBlock.add(pageTitle);
        headingBlock.add(pageSub);
        headingBlock.add(btnRow);

        // Stat cards
        int total = 0;
        try { total = new StudentDAO().count(); } catch (Exception ignored) {}

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.add(statCard("Total Students", String.valueOf(total), ACCENT,
            new Color(59, 130, 246, 30), new Color(59, 130, 246, 80)));
        cardsPanel.add(statCard("Departments",    "8",                   TEAL,
            new Color(20, 184, 166, 30), new Color(20, 184, 166, 80)));
        cardsPanel.add(statCard("System Status",  "Online",              GREEN,
            new Color(34, 197, 94,  30), new Color(34, 197, 94,  80)));

        topSection.add(headingBlock, BorderLayout.WEST);
        topSection.add(cardsPanel,   BorderLayout.CENTER);

        // ── Divider ───────────────────────────────────────────────────────
        JSeparator divider = new JSeparator();
        divider.setForeground(CARD_BORDER);
        divider.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // ── OOP card ──────────────────────────────────────────────────────
        JPanel oopCard = buildOopCard();

        JPanel bottomSection = new JPanel(new BorderLayout(0, 14));
        bottomSection.setOpaque(false);
        bottomSection.add(divider, BorderLayout.NORTH);
        bottomSection.add(oopCard, BorderLayout.CENTER);

        root.add(topSection,    BorderLayout.NORTH);
        root.add(bottomSection, BorderLayout.CENTER);
        return root;
    }

    // ── Stat card ─────────────────────────────────────────────────────────────

    private JPanel statCard(String label, String value, Color accent, Color bgTint, Color borderTint) {
        JPanel c = new JPanel(new BorderLayout(0, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(bgTint);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(borderTint);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
                // Left accent stripe
                g2.setColor(accent);
                g2.fillRoundRect(0, 16, 3, getHeight() - 32, 2, 2);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(
            accent.getRed(), accent.getGreen(), accent.getBlue(), 180));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 36));
        val.setForeground(TEXT_BRIGHT);

        // Status dot + "Active" text
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillOval(0, 6, 6, 6);
                g2.dispose();
            }
        };
        bottom.setOpaque(false);

        JLabel trend = new JLabel("     Active");   // spaces make room for painted dot
        trend.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        trend.setForeground(new Color(
            accent.getRed(), accent.getGreen(), accent.getBlue(), 160));
        bottom.add(trend);

        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.setOpaque(false);
        text.add(lbl,    BorderLayout.NORTH);
        text.add(val,    BorderLayout.CENTER);
        text.add(bottom, BorderLayout.SOUTH);

        c.add(text, BorderLayout.CENTER);
        return c;
    }

    // ── OOP card ─────────────────────────────────────────────────────────────

    private JPanel buildOopCard() {
        JPanel card = new JPanel(new BorderLayout(0, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JLabel heading = new JLabel("OOP Architecture");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_BRIGHT);

        JLabel sub = new JLabel("Design patterns applied in this project");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXT_DIM);

        JPanel headText = new JPanel();
        headText.setLayout(new BoxLayout(headText, BoxLayout.Y_AXIS));
        headText.setOpaque(false);
        headText.add(heading);
        headText.add(Box.createVerticalStrut(2));
        headText.add(sub);
        headerRow.add(headText, BorderLayout.WEST);

        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        sep.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        String[][] concepts = {
            {"Abstract class",     "Person — base class for all people"},
            {"Interface",          "Manageable — validate / summary / ID"},
            {"Inheritance",        "Student extends Person"},
            {"Polymorphism",       "getRole() overridden per subclass"},
            {"Encapsulation",      "Private fields + getters / setters"},
            {"Method overloading", "3 Student constructors"},
        };

        // Pre-compute lightened accent colours (no runtime Math.min needed)
        Color[] chipAccents = {
            new Color(147, 197, 253),   // blue-300
            new Color(94,  234, 212),   // teal-300
            new Color(134, 239, 172),   // green-300
            new Color(216, 180, 254),   // purple-300
            new Color(253, 186, 116),   // orange-300
            new Color(253, 224, 71),    // yellow-300
        };
        Color[] chipBase = {
            new Color(59,  130, 246),
            new Color(20,  184, 166),
            new Color(34,  197, 94),
            new Color(168, 85,  247),
            new Color(249, 115, 22),
            new Color(234, 179, 8),
        };

        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 10));
        grid.setOpaque(false);
        for (int i = 0; i < concepts.length; i++) {
            grid.add(conceptChip(concepts[i][0], concepts[i][1], chipBase[i], chipAccents[i]));
        }

        card.add(headerRow, BorderLayout.NORTH);
        card.add(sep,       BorderLayout.CENTER);
        card.add(grid,      BorderLayout.SOUTH);
        return card;
    }

    private JPanel conceptChip(String concept, String desc,
                               Color base, Color light) {
        JPanel chip = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 20));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.setColor(base);
                g2.fillRoundRect(0, 8, 3, getHeight() - 16, 2, 2);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel name = new JLabel(concept);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        name.setForeground(light);

        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        d.setForeground(TEXT_DIM);

        chip.add(name, BorderLayout.NORTH);
        chip.add(d,    BorderLayout.CENTER);
        return chip;
    }

    // ── Status bar ────────────────────────────────────────────────────────────

    private JLabel buildStatusBar() {
        JLabel bar = new JLabel("  \u25cf  Ready  \u00b7  Java Swing + JDBC  \u00b7  MySQL Database");
        bar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bar.setForeground(new Color(34, 197, 94));
        bar.setBackground(new Color(12, 17, 35));
        bar.setOpaque(true);
        bar.setPreferredSize(new Dimension(0, 26));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 52, 80)),
            BorderFactory.createEmptyBorder(0, 12, 0, 0)
        ));
        return bar;
    }

    // ── RoundButton — custom painted, works on all Look & Feels ──────────────

    public static class RoundButton extends JButton {
        private final Color bgTop, bgBot;
        private boolean hov = false, press = false;

        public RoundButton(String text, Color bgTop, Color bgBot) {
            super(text);
            this.bgTop = bgTop;
            this.bgBot = bgBot;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(148, 38));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { hov = true;   repaint(); }
                @Override public void mouseExited(MouseEvent e)   { hov = false;  repaint(); }
                @Override public void mousePressed(MouseEvent e)  { press = true;  repaint(); }
                @Override public void mouseReleased(MouseEvent e) { press = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = 10;
            int y = press ? 1 : 0;

            if (!press) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(1, 3, w - 2, h - 2, arc, arc);
            }

            Color top = hov ? bgTop.brighter() : bgTop;
            GradientPaint gp = new GradientPaint(0, y, top, 0, h - 2 + y, bgBot);
            g2.setPaint(gp);
            g2.fillRoundRect(0, y, w - 1, h - 3, arc, arc);

            if (!press) {
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(2, y + 1, w - 5, h / 2 - 2, arc - 2, arc - 2);
            }

            g2.dispose();
            super.paintComponent(g);
        }

        @Override public boolean isOpaque() { return false; }
    }
}