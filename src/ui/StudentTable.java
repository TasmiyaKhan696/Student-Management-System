package ui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class StudentTable extends JPanel {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG         = new Color(241, 245, 249);
    private static final Color SURFACE    = Color.WHITE;
    private static final Color BORDER     = new Color(226, 232, 240);
    private static final Color ROW_ALT    = new Color(248, 250, 252);
    private static final Color ROW_SEL    = new Color(239, 246, 255);
    private static final Color MUTED      = new Color(100, 116, 139);
    private static final Color TEXT       = new Color(15, 23, 42);
    private static final Color BLUE       = new Color(37, 99, 235);
    private static final Color TEAL       = new Color(13, 148, 136);

    // Badge colours
    private static final Color RED_BG    = new Color(254, 242, 242);
    private static final Color RED_FG    = new Color(185, 28,  28);
    private static final Color AMBER_BG  = new Color(255, 251, 235);
    private static final Color AMBER_FG  = new Color(146, 64,  14);
    private static final Color VIOLET_BG = new Color(245, 243, 255);
    private static final Color VIOLET_FG = new Color(109, 40, 217);
    private static final Color GRAY_BG   = new Color(241, 245, 249);

    private static final String[] COLS = {
        "#", "Student ID", "Name", "Department",
        "Library", "Fees", "Sem 1", "Sem 2",
        "Email", "Phone", "Year"
    };

    private DefaultTableModel model;
    private JTable            table;
    private JTextField        searchField;
    private JLabel            countLabel;
    private List<Student>     data = new ArrayList<>();
    private MainFrame         mainFrame;
    private StudentForm       formRef;

    public StudentTable()                   { buildUI(); }
    public void setFormRef(StudentForm f)   { this.formRef = f; }
    public void setMainFrame(MainFrame mf)  { this.mainFrame = mf; }

    // ── Build UI ─────────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildPageHeader(), BorderLayout.NORTH);
        add(buildTableCard(),  BorderLayout.CENTER);
        add(buildFooter(),     BorderLayout.SOUTH);

        load();
    }

    private JPanel buildPageHeader() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setBackground(BG);

        JLabel title = new JLabel("Students");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        outer.add(title,         BorderLayout.NORTH);
        outer.add(buildToolbar(), BorderLayout.CENTER);
        return outer;
    }

    // ── Toolbar ──────────────────────────────────────────────────────────────

    private JPanel buildToolbar() {
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 14));
                g2.fillRoundRect(2, 4, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 12, 12);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 12, 12);
                g2.dispose();
            }
        };
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        searchField = StudentForm.styledField("Search by name, ID, email…");
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.addActionListener(e -> performSearch());

        // Primary action
        PillButton searchBtn  = new PillButton("Search",    BLUE,       Color.WHITE,  false);
        // Neutral
        PillButton clearBtn   = new PillButton("Clear",     GRAY_BG,    MUTED,        true);
        PillButton refreshBtn = new PillButton("Refresh",   GRAY_BG,    MUTED,        true);
        // Semantic
        PillButton editBtn    = new PillButton("Edit",      AMBER_BG,   AMBER_FG,     true);
        PillButton deleteBtn  = new PillButton("Delete",    RED_BG,     RED_FG,       true);
        PillButton marksBtn        = new PillButton("Add Marks",    VIOLET_BG,  VIOLET_FG, true);
        PillButton updateMarksBtn  = new PillButton("Update Marks", new Color(240, 253, 244), new Color(22, 101, 52), true);

        searchBtn.addActionListener(e       -> performSearch());
        clearBtn.addActionListener(e        -> { searchField.setText(""); load(); });
        refreshBtn.addActionListener(e      -> load());
        editBtn.addActionListener(e         -> editSelected());
        deleteBtn.addActionListener(e       -> deleteSelected());
        marksBtn.addActionListener(e        -> openMarksDialog(false));
        updateMarksBtn.addActionListener(e  -> openMarksDialog(true));

        bar.add(searchField);
        bar.add(searchBtn);
        bar.add(vSep());
        bar.add(editBtn);
        bar.add(deleteBtn);
        bar.add(vSep());
        bar.add(clearBtn);
        bar.add(refreshBtn);
        bar.add(vSep());
        bar.add(marksBtn);
        bar.add(updateMarksBtn);

        return bar;
    }

    private JSeparator vSep() {
        JSeparator s = new JSeparator(JSeparator.VERTICAL);
        s.setPreferredSize(new Dimension(1, 22));
        s.setForeground(BORDER);
        return s;
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private JScrollPane buildTableCard() {
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ROW_SEL);
                    c.setForeground(TEXT);
                } else {
                    c.setBackground(row % 2 == 0 ? SURFACE : ROW_ALT);
                    c.setForeground(col == 0 || col == 9 || col == 10 ? MUTED : TEXT);
                }
                return c;
            }
        };

        styleTable();

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getViewport().setBackground(SURFACE);
        return sp;
    }

    private void styleTable() {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(TEXT);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(MUTED);
        header.setPreferredSize(new Dimension(0, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        int[] widths = {36, 90, 150, 150, 95, 80, 55, 55, 180, 110, 55};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col : new int[]{0, 6, 7, 10})
            table.getColumnModel().getColumn(col).setCellRenderer(center);

        table.getColumnModel().getColumn(5).setCellRenderer(
            new BadgeRenderer("Paid", new Color(209, 250, 229), new Color(6, 95, 70),
                              "Pending", AMBER_BG, AMBER_FG));
        table.getColumnModel().getColumn(4).setCellRenderer(
            new BadgeRenderer("Borrowed", AMBER_BG, AMBER_FG,
                              "Returned", new Color(209, 250, 229), new Color(6, 95, 70)));
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(BG);
        countLabel = new JLabel("Loading…");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(MUTED);
        p.add(countLabel);
        return p;
    }

    // ── Data ─────────────────────────────────────────────────────────────────

    public void load() {
        data = new StudentDAO().getAllStudents();
        populate(data);
    }

    private void performSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { load(); return; }
        data = new StudentDAO().search(kw);
        populate(data);
    }

    private void populate(List<Student> list) {
        model.setRowCount(0);
        int i = 1;
        for (Student s : list) {
            model.addRow(new Object[]{
                i++,
                s.getStudentId(),
                s.getFirstName() + " " + s.getLastName(),
                s.getDepartment(),
                s.getLibraryStatus(),
                s.isFeesPaid() ? "Paid" : "Pending",
                s.getSem1Marks(),
                s.getSem2Marks(),
                s.getEmail(),
                s.getPhone() != null ? s.getPhone() : "—",
                s.getEnrollDate() != null ? s.getEnrollDate().getYear() : "—"
            });
        }
        countLabel.setText(list.size() + " student" + (list.size() == 1 ? "" : "s"));
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Select a student to edit."); return; }
        Student s = data.get(table.convertRowIndexToModel(row));
        if (mainFrame != null) mainFrame.showFormWithStudent(s);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Select a student to delete."); return; }
        Student s = data.get(table.convertRowIndexToModel(row));
        int opt = JOptionPane.showConfirmDialog(this,
            "<html><b>Delete " + s.getFirstName() + " " + s.getLastName() + "?</b><br>This cannot be undone.</html>",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            boolean ok = new StudentDAO().deleteStudent(s.getId());
            if (ok) load();
            else toast("Delete failed — check the console for details.");
        }
    }

    // updateMode=false → Add Marks (INSERT), updateMode=true → Update Marks (UPDATE)
    private void openMarksDialog(boolean updateMode) {
        int row = table.getSelectedRow();
        if (row < 0) { toast("Select a student first."); return; }
        Student s = data.get(table.convertRowIndexToModel(row));

        StudentDAO dao = new StudentDAO();

        JComboBox<Integer> semBox   = new JComboBox<>(new Integer[]{1, 2});
        JTextField marksField       = StudentForm.styledField("0 – 100");

        // Pre-fill current marks when updating
        if (updateMode) {
            semBox.addActionListener(e -> {
                int sem = (Integer) semBox.getSelectedItem();
                int cur = sem == 1 ? s.getSem1Marks() : s.getSem2Marks();
                marksField.setText(cur > 0 ? String.valueOf(cur) : "");
            });
            // Trigger once for initial value
            int cur = s.getSem1Marks();
            marksField.setText(cur > 0 ? String.valueOf(cur) : "");
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        panel.add(new JLabel("Semester:")); panel.add(semBox);
        panel.add(new JLabel("Marks:"));   panel.add(marksField);

        String title = (updateMode ? "Update Marks" : "Add Marks")
                       + " — " + s.getFirstName() + " " + s.getLastName();

        int opt = JOptionPane.showConfirmDialog(this, panel,
            title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            try {
                int sem   = (Integer) semBox.getSelectedItem();
                int marks = Integer.parseInt(marksField.getText().trim());
                if (marks < 0 || marks > 100) throw new NumberFormatException();

                boolean ok;
                if (updateMode) {
                    if (!dao.marksExist(s.getStudentId(), sem)) {
                        toast("No existing marks for Semester " + sem + ". Use 'Add Marks' instead.");
                        return;
                    }
                    ok = dao.updateMarks(s.getStudentId(), sem, marks);
                    if (ok) toast("Marks updated for " + s.getFirstName() + ".");
                    else    toast("Update failed — record may not exist.");
                } else {
                    if (dao.marksExist(s.getStudentId(), sem)) {
                        toast("Marks already exist for Semester " + sem + ". Use 'Update Marks' instead.");
                        return;
                    }
                    ok = dao.addMarks(s.getStudentId(), sem, marks);
                    if (ok) toast("Marks saved for " + s.getFirstName() + ".");
                    else    toast("Failed to save marks.");
                }

                if (ok) load();

            } catch (NumberFormatException ex) {
                toast("Enter a valid mark between 0 and 100.");
            }
        }
    }

    private void toast(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── PillButton — rounded, lightly 3D, works on all LAFs ─────────────────

    private static class PillButton extends JButton {
        private final Color bg, fg;
        private final boolean bordered;
        private boolean hov = false, press = false;

        PillButton(String text, Color bg, Color fg, boolean bordered) {
            super(text);
            this.bg = bg; this.fg = fg; this.bordered = bordered;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(fg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hov = true;   repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hov = false;  repaint(); }
                @Override public void mousePressed(MouseEvent e) { press = true;  repaint(); }
                @Override public void mouseReleased(MouseEvent e){ press = false; repaint(); }
            });
        }

        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width + 4, 32);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = 20;

            // Shadow for non-bordered (primary) buttons
            if (!bordered && !press) {
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(1, 3, w - 2, h - 1, arc, arc);
            }

            // Fill
            Color fill = press ? bg.darker() : (hov ? bg.brighter() : bg);
            g2.setColor(fill);
            g2.fillRoundRect(0, press ? 1 : 0, w - 1, h - 2, arc, arc);

            // Shine
            if (!bordered && !press) {
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(2, 1, w - 5, h / 2 - 1, arc - 2, arc - 2);
            }

            // Border outline
            if (bordered) {
                g2.setColor(hov ? fg : BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 2, h - 2, arc, arc);
            }

            g2.dispose();
            super.paintComponent(g);
        }

        @Override public boolean isOpaque() { return false; }
    }

    // ── Badge renderer ───────────────────────────────────────────────────────

    private static class BadgeRenderer extends DefaultTableCellRenderer {
        private final String v1, v2;
        private final Color b1, f1, b2, f2;

        BadgeRenderer(String v1, Color b1, Color f1, String v2, Color b2, Color f2) {
            this.v1=v1; this.b1=b1; this.f1=f1;
            this.v2=v2; this.b2=b2; this.f2=f2;
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override public Component getTableCellRendererComponent(
                JTable t, Object value, boolean sel, boolean foc, int row, int col) {

            String v = value != null ? value.toString() : "";
            Color bg = v.equals(v1) ? b1 : v.equals(v2) ? b2 : GRAY_BG;
            Color fg = v.equals(v1) ? f1 : v.equals(v2) ? f2 : MUTED;

            JLabel lbl = new JLabel(v) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(fg);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(false);
            lbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(sel ? ROW_SEL : row % 2 == 0 ? SURFACE : ROW_ALT);
            wrap.add(lbl);
            return wrap;
        }
    }
}
