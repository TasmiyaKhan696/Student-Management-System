package ui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class StudentTable extends JPanel {

    private static final String[] COLS = {
        "#", "Student ID", "Name", "Department", "Grade", "Email", "Phone", "Enrolled"
    };

    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;
    private JLabel countLabel;
    private List<Student> data = new ArrayList<>();

    private StudentForm formRef;   // injected so we can open edit

    public StudentTable() {
        buildUI();
    }

    public void setFormRef(StudentForm f) { this.formRef = f; }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        load();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchIcon.setForeground(new Color(100, 116, 139));

        searchField = StudentForm.styledField();
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setToolTipText("Search by name, ID, dept, email…");

        JButton searchBtn  = makeBtn("Search",      new Color(37,  99, 235));
        JButton clearBtn   = makeBtn("Clear",       new Color(100,116, 139));
        JButton refreshBtn = makeBtn("Refresh",     new Color(15, 118, 110));
        JButton deleteBtn  = makeBtn("Delete",      new Color(220, 38,  38));
        JButton editBtn    = makeBtn("Edit",        new Color(217,119,  6));

        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performSearch();
            }
        });
        searchBtn.addActionListener(e  -> performSearch());
        clearBtn.addActionListener(e   -> { searchField.setText(""); load(); });
        refreshBtn.addActionListener(e -> load());
        editBtn.addActionListener(e    -> editSelected());
        deleteBtn.addActionListener(e  -> deleteSelected());

        bar.add(searchIcon);
        bar.add(searchField);
        bar.add(searchBtn);
        bar.add(clearBtn);
        bar.add(Box.createHorizontalStrut(16));
        bar.add(editBtn);
        bar.add(deleteBtn);
        bar.add(refreshBtn);
        return bar;
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(30, 64, 175));
        table.setAutoCreateRowSorter(true);

        // Header style
        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hdr.setBackground(new Color(241, 245, 249));
        hdr.setForeground(new Color(51, 65, 85));
        hdr.setPreferredSize(new Dimension(0, 36));

        // Column widths
        int[] w = {40, 110, 150, 150, 70, 190, 110, 100};
        for (int i = 0; i < w.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Alternating row renderer with grade colour badge
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    setForeground(new Color(30, 41, 59));
                }
                // Colour-code grade column (index 4)
                if (col == 4 && val != null && !sel) {
                    String g = val.toString();
                    if (g.startsWith("A"))      setForeground(new Color(22, 163, 74));
                    else if (g.startsWith("B")) setForeground(new Color(37,  99, 235));
                    else if (g.startsWith("C")) setForeground(new Color(217,119,  6));
                    else if (g.startsWith("D") || g.startsWith("F"))
                                                setForeground(new Color(220, 38, 38));
                }
                return this;
            }
        });

        // Double-click to edit
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        p.setBackground(new Color(248, 250, 252));
        countLabel = new JLabel("Loading…");
        countLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        countLabel.setForeground(new Color(100, 116, 139));
        p.add(countLabel);
        return p;
    }

    // ── Data operations ───────────────────────────────────────────────────────

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
                s.getGradeLabel(),
                s.getEmail(),
                s.getPhone() != null ? s.getPhone() : "",
                s.getEnrollDate() != null ? s.getEnrollDate().getYear() : ""
            });
        }
        countLabel.setText("Showing " + list.size() + " student" + (list.size() == 1 ? "" : "s"));
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a student to edit."); return; }
        Student s = data.get(table.convertRowIndexToModel(row));
        if (formRef != null) {
            formRef.loadForEdit(s);
            // Switch to form tab — walk up to MainFrame's tabbed pane
            Container parent = getParent();
            while (parent != null && !(parent instanceof JTabbedPane)) parent = parent.getParent();
            if (parent instanceof JTabbedPane) ((JTabbedPane)parent).setSelectedIndex(1);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a student to delete."); return; }
        Student s = data.get(table.convertRowIndexToModel(row));
        int opt   = JOptionPane.showConfirmDialog(this,
            "Delete " + s.getFirstName() + " " + s.getLastName() + "?\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            boolean ok = new StudentDAO().deleteStudent(s.getId());
            if (ok) { JOptionPane.showMessageDialog(this, "Deleted successfully."); load(); }
            else    { JOptionPane.showMessageDialog(this, "Delete failed — check DB."); }
        }
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(82, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}