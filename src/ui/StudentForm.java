package ui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class StudentForm extends JPanel {

    // ── Palette ──────────────────────────────────────────────────────────────
    static final Color BLUE       = new Color(37, 99, 235);
    static final Color BLUE_LIGHT = new Color(219, 234, 254);
    static final Color GREEN      = new Color(22, 163, 74);
    static final Color RED        = new Color(220, 38, 38);
    static final Color BG         = new Color(248, 250, 252);
    static final Color SURFACE    = Color.WHITE;
    static final Color BORDER     = new Color(226, 232, 240);
    static final Color LABEL_FG   = new Color(71, 85, 105);
    static final Color TEXT       = new Color(15, 23, 42);
    static final Color MUTED      = new Color(100, 116, 139);

    // ── Fields ───────────────────────────────────────────────────────────────
    private JTextField sidField   = styledField("e.g. STU-0042");
    private JTextField fnField    = styledField("First name");
    private JTextField lnField    = styledField("Last name");
    private JTextField emailField = styledField("student@university.edu");
    private JTextField phoneField = styledField("+91 …");
    private JTextField addrField  = styledField("Street, City");

    private JComboBox<String> deptBox = new JComboBox<>(new String[]{
        "Computer Science", "Mathematics", "Physics", "Chemistry",
        "Engineering", "Business", "Biology", "English Literature"
    });

    private JComboBox<String> libraryBox = new JComboBox<>(new String[]{
        "Not Borrowed", "Borrowed", "Returned"
    });

    private JCheckBox feesBox = new JCheckBox("Mark as paid");

    private JComboBox<Integer> yearBox;

    private JLabel feedback = new JLabel(" ");

    private Student  editTarget = null;
    private Runnable onSave;

    // ── Constructor ──────────────────────────────────────────────────────────

    public StudentForm(Runnable onSave) {
        this.onSave = onSave;
        buildUI();
    }

    // ── Public API ───────────────────────────────────────────────────────────

    public void loadForEdit(Student s) {
        editTarget = s;
        sidField.setText(s.getStudentId());
        sidField.setEditable(false);
        sidField.setForeground(MUTED);
        fnField.setText(s.getFirstName());
        lnField.setText(s.getLastName());
        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhone() != null ? s.getPhone() : "");
        addrField.setText(s.getAddress() != null ? s.getAddress() : "");
        deptBox.setSelectedItem(s.getDepartment());
        if (s.getEnrollDate() != null) yearBox.setSelectedItem(s.getEnrollDate().getYear());
        libraryBox.setSelectedItem(s.getLibraryStatus());
        feesBox.setSelected(s.isFeesPaid());
        feedback.setText(" ");
    }

    public void resetToAdd() {
        editTarget = null;
        sidField.setEditable(true);
        sidField.setForeground(TEXT);
        sidField.setText("");
        fnField.setText(""); lnField.setText("");
        emailField.setText(""); phoneField.setText(""); addrField.setText("");
        deptBox.setSelectedIndex(0);
        yearBox.setSelectedItem(LocalDate.now().getYear());
        libraryBox.setSelectedItem("Not Borrowed");
        feesBox.setSelected(false);
        feedback.setText(" ");
    }

    // ── Build UI ─────────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        // Outer scroll pane so nothing gets clipped on small windows
        JPanel inner = new JPanel(new BorderLayout(0, 20));
        inner.setBackground(BG);
        inner.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        inner.add(buildPageHeader(), BorderLayout.NORTH);
        inner.add(buildCard(),       BorderLayout.CENTER);
        inner.add(buildActionBar(),  BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);

        // Build yearBox before layout
        Integer[] years = new Integer[15];
        int yr = LocalDate.now().getYear();
        for (int i = 0; i < 15; i++) years[i] = yr - i;
        yearBox = new JComboBox<>(years);
        styleCombo(deptBox); styleCombo(yearBox); styleCombo(libraryBox);

        feesBox.setBackground(SURFACE);
        feesBox.setForeground(TEXT);
        feesBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feesBox.setFocusPainted(false);
    }

    private JPanel buildPageHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);

        JLabel title = new JLabel(editTarget == null ? "Add New Student" : "Edit Student");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Fill in the details below and click Save.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(MUTED);
        sub.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.add(title); text.add(sub);

        p.add(text, BorderLayout.WEST);
        return p;
    }

    private JPanel buildCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));

        Integer[] years = new Integer[15];
        int yr = LocalDate.now().getYear();
        for (int i = 0; i < 15; i++) years[i] = yr - i;
        yearBox = new JComboBox<>(years);

        styleCombo(deptBox); styleCombo(yearBox); styleCombo(libraryBox);
        feesBox.setBackground(SURFACE);
        feesBox.setForeground(TEXT);
        feesBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feesBox.setFocusPainted(false);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Section: Identity
        addSection(card, g, 0, "Student Identity");
        addRow(card, g, 1, 0, "Student ID *",  sidField);
        addRow(card, g, 1, 2, "First Name *",  fnField);
        addRow(card, g, 2, 0, "Last Name *",   lnField);
        addRow(card, g, 2, 2, "Email *",        emailField);

        // Divider
        addDivider(card, g, 3);

        // Section: Contact
        addSection(card, g, 4, "Contact & Location");
        addRow(card, g, 5, 0, "Phone",   phoneField);
        addRow(card, g, 5, 2, "Address", addrField);

        // Divider
        addDivider(card, g, 6);

        // Section: Academic
        addSection(card, g, 7, "Academic Details");
        addRow(card, g, 8, 0, "Department *",  deptBox);
        addRow(card, g, 8, 2, "Enrol Year",    yearBox);
        addRow(card, g, 9, 0, "Library Status", libraryBox);
        addRow(card, g, 9, 2, "Fees Paid",      feesBox);

        return card;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);

        feedback.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        FormButton clearBtn = new FormButton("Discard",
            new Color(241, 245, 249), new Color(220, 228, 240),
            new Color(71, 85, 105), true);

        FormButton saveBtn = new FormButton(
            editTarget == null ? "Save Student" : "Update Student",
            BLUE, new Color(29, 78, 216), Color.WHITE, false);

        clearBtn.addActionListener(e -> resetToAdd());
        saveBtn.addActionListener(e  -> save());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(BG);
        btns.add(feedback);
        btns.add(clearBtn);
        btns.add(saveBtn);

        bar.add(btns, BorderLayout.EAST);
        return bar;
    }

    // ── Save logic ───────────────────────────────────────────────────────────

    private void save() {
        String sid   = sidField.getText().trim();
        String fn    = fnField.getText().trim();
        String ln    = lnField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String addr  = addrField.getText().trim();
        String dept  = (String)  deptBox.getSelectedItem();
        int    year  = (Integer) yearBox.getSelectedItem();

        if (sid.isEmpty() || fn.isEmpty() || ln.isEmpty() || email.isEmpty()) {
            setFeedback("Fields marked * are required.", RED); return;
        }
        if (!email.contains("@")) {
            setFeedback("Please enter a valid email address.", RED); return;
        }

        Student s = editTarget != null ? editTarget : new Student();
        s.setStudentId(sid);
        s.setFirstName(fn);      s.setLastName(ln);
        s.setEmail(email);       s.setPhone(phone);
        s.setAddress(addr);      s.setDepartment(dept);
        s.setEnrollDate(LocalDate.of(year, 1, 1));
        s.setLibraryStatus((String)  libraryBox.getSelectedItem());
        s.setFeesPaid(feesBox.isSelected());

        boolean ok = (editTarget == null)
            ? new StudentDAO().addStudent(s)
            : new StudentDAO().updateStudent(s);

        if (ok) {
            setFeedback("Saved successfully!", GREEN);
            resetToAdd();
            if (onSave != null) onSave.run();
        } else {
            setFeedback("Database error — please try again.", RED);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void setFeedback(String msg, Color c) {
        feedback.setText(msg);
        feedback.setForeground(c);
    }

    private void addSection(JPanel p, GridBagConstraints g, int row, String title) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 4; g.weightx = 1;
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        p.add(lbl, g);
        g.gridwidth = 1;
    }

    private void addDivider(JPanel p, GridBagConstraints g, int row) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 4; g.weightx = 1;
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        p.add(sep, g);
        g.gridwidth = 1;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, int col, String label, JComponent field) {
        g.gridx = col;     g.gridy = row; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(LABEL_FG);
        lbl.setPreferredSize(new Dimension(120, 28));
        p.add(lbl, g);

        g.gridx = col + 1; g.weightx = 1;
        p.add(field, g);
    }

    // ── Static factory methods (used by StudentTable too) ────────────────────

    static JTextField styledField() {
        return styledField("");
    }

    static JTextField styledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !placeholder.isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(148, 163, 184));
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT);
        f.setPreferredSize(new Dimension(200, 34));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        f.setBackground(SURFACE);
        // Subtle focus border
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BLUE, 1, true),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(203, 213, 225), 1, true),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        return f;
    }

    static void styleCombo(JComboBox<?> b) {
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setPreferredSize(new Dimension(200, 34));
        b.setBackground(SURFACE);
    }

    static void styleBtn(JButton b, Color bg, Color fg) {
        // kept for compatibility — use FormButton for new buttons
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true); b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
    }

    // ── FormButton — fully custom-painted, works on all Look & Feels ─────────

    private static class FormButton extends JButton {
        private final Color bgNorm, bgPress, fg;
        private final boolean ghost;
        private boolean hov = false, press = false;

        FormButton(String text, Color bgNorm, Color bgPress, Color fg, boolean ghost) {
            super(text);
            this.bgNorm = bgNorm; this.bgPress = bgPress;
            this.fg = fg; this.ghost = ghost;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(fg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(ghost ? 110 : 148, 38));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e)  { hov = true;   repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e)   { hov = false;  repaint(); }
                @Override public void mousePressed(java.awt.event.MouseEvent e)  { press = true;  repaint(); }
                @Override public void mouseReleased(java.awt.event.MouseEvent e) { press = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = 10;
            int yOff = press ? 1 : 0;

            if (ghost) {
                Color fill = press ? new Color(215, 225, 238)
                           : hov   ? new Color(230, 236, 245)
                           : bgNorm;
                g2.setColor(fill);
                g2.fillRoundRect(0, yOff, w - 1, h - 3, arc, arc);
                g2.setColor(new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, yOff, w - 2, h - 4, arc, arc);
            } else {
                if (!press) {
                    g2.setColor(new Color(0, 0, 0, 45));
                    g2.fillRoundRect(1, 3, w - 2, h - 2, arc, arc);
                }
                Color top = hov ? bgNorm.brighter() : bgNorm;
                GradientPaint gp = new GradientPaint(0, yOff, top, 0, h - 2 + yOff, bgPress);
                g2.setPaint(gp);
                g2.fillRoundRect(0, yOff, w - 1, h - 3, arc, arc);
                if (!press) {
                    g2.setColor(new Color(255, 255, 255, 35));
                    g2.fillRoundRect(2, yOff + 1, w - 5, h / 2 - 2, arc - 2, arc - 2);
                }
            }
            g2.dispose();
            super.paintComponent(g);
        }

        @Override public boolean isOpaque() { return false; }
    }
}
