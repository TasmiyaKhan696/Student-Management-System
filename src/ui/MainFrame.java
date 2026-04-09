package ui;

import dao.StudentDAO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final Color BLUE    = new Color(37, 99, 235);
    private static final Color SIDEBAR = new Color(30, 41, 59);
    private static final Color BG      = new Color(248, 250, 252);

    private StudentTable tablePanel;
    private StudentForm  formPanel;
    private JTabbedPane  tabs;

    public MainFrame() {
        setTitle("Student Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(BG);

        // Tab 0: Dashboard
        tabs.addTab("  Dashboard  ", buildDashboard());

        // Tab 1: View Students
        tablePanel = new StudentTable();
        tabs.addTab("  View Students  ", tablePanel);

        // Tab 2: Add Student
        formPanel = new StudentForm(() -> {
            tablePanel.load();
            tabs.setSelectedIndex(1);   // jump to table after save
        });
        tabs.addTab("  Add Student  ", formPanel);

        // Wire form ↔ table for edit
        tablePanel.setFormRef(formPanel);

        add(tabs, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BLUE);
        h.setPreferredSize(new Dimension(0, 60));
        h.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        // Left: icon + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("■");   // simple square icon
        icon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        icon.setForeground(new Color(147, 197, 253));

        JLabel title = new JLabel("Student Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        left.add(icon); left.add(title);

        // Right: subtitle
        JLabel sub = new JLabel("Java + Swing + JDBC");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(147, 197, 253));

        h.add(left,  BorderLayout.WEST);
        h.add(sub,   BorderLayout.EAST);
        return h;
    }

    // ── Dashboard tab ─────────────────────────────────────────────────────────

    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(30, 41, 59));
        heading.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        p.add(heading, BorderLayout.NORTH);

        // ── Stat cards row ─────────────────────────────────────────────────
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBackground(BG);

        int total = 0;
        try { total = new StudentDAO().count(); } catch (Exception ignored) {}

        cards.add(statCard("Total Students", String.valueOf(total), new Color(37, 99, 235), new Color(219, 234, 254)));
        cards.add(statCard("Departments",    "8",                   new Color(15, 118, 110),new Color(204, 251, 241)));
        cards.add(statCard("System Status",  "Online",              new Color(22, 163,  74),new Color(220, 252, 231)));

        JPanel cardsWrap = new JPanel(new BorderLayout());
        cardsWrap.setBackground(BG);
        cardsWrap.setBorder(BorderFactory.createEmptyBorder(0,0,24,0));
        cardsWrap.add(cards);

        // ── Quick-action buttons ───────────────────────────────────────────
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setBackground(BG);

        JButton goAdd    = bigBtn("+ Add Student",   BLUE);
        JButton goView   = bigBtn("View All Students", new Color(15,118,110));

        goAdd.addActionListener(e  -> tabs.setSelectedIndex(2));
        goView.addActionListener(e -> { tablePanel.load(); tabs.setSelectedIndex(1); });

        actions.add(goAdd); actions.add(goView);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(BG);
        center.add(cardsWrap, BorderLayout.NORTH);
        center.add(actions,   BorderLayout.CENTER);

        // ── Info card ──────────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226,232,240), 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        String[][] rows = {
            {"Abstract class",    "Person — base for all people"},
            {"Interface",         "Manageable — validate / summary / ID"},
            {"Inheritance",       "Student extends Person"},
            {"Polymorphism",      "getRole() overridden per subclass"},
            {"Encapsulation",     "Private fields + getters/setters"},
            {"Method overloading","3 Student constructors"},
        };

        JLabel infoTitle = new JLabel("OOP Concepts in this project");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTitle.setForeground(new Color(30, 41, 59));
        infoTitle.setAlignmentX(LEFT_ALIGNMENT);
        infoTitle.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        info.add(infoTitle);

        for (String[] row : rows) {
            JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            line.setBackground(Color.WHITE);
            line.setAlignmentX(LEFT_ALIGNMENT);
            JLabel concept = new JLabel(row[0]);
            concept.setFont(new Font("Segoe UI", Font.BOLD, 12));
            concept.setForeground(BLUE);
            concept.setPreferredSize(new Dimension(160, 20));
            JLabel desc = new JLabel(row[1]);
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            desc.setForeground(new Color(71, 85, 105));
            line.add(concept); line.add(desc);
            info.add(line);
        }

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(BG);
        south.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        south.add(info, BorderLayout.NORTH);

        p.add(center, BorderLayout.CENTER);
        p.add(south,  BorderLayout.SOUTH);
        return p;
    }

    private JPanel statCard(String label, String value, Color fg, Color bg) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(bg);
        c.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bg.darker(), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 36));
        val.setForeground(fg);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(fg.darker());
        c.add(val, BorderLayout.CENTER);
        c.add(lbl, BorderLayout.SOUTH);
        return c;
    }

    private JButton bigBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(180, 42));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Status bar ────────────────────────────────────────────────────────────

    private JLabel buildStatusBar() {
        JLabel bar = new JLabel("  Ready  |  Java Swing + JDBC  |  MySQL Database");
        bar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bar.setForeground(new Color(100, 116, 139));
        bar.setBackground(new Color(241, 245, 249));
        bar.setOpaque(true);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        return bar;
    }
}