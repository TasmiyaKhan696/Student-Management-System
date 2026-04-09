package ui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;

public class StudentForm extends JPanel {

    // Colours
    static final Color BLUE       = new Color(37, 99, 235);
    static final Color BLUE_LIGHT = new Color(219, 234, 254);
    static final Color GREEN      = new Color(22, 163, 74);
    static final Color RED        = new Color(220, 38, 38);
    static final Color BG         = new Color(248, 250, 252);
    static final Color LABEL_FG   = new Color(71, 85, 105);

    private JTextField sidField   = styledField();
    private JTextField fnField    = styledField();
    private JTextField lnField    = styledField();
    private JTextField emailField = styledField();
    private JTextField phoneField = styledField();
    private JTextField addrField  = styledField();

    private JComboBox<String> deptBox  = new JComboBox<>(new String[]{
        "Computer Science","Mathematics","Physics","Chemistry",
        "Engineering","Business","Biology","English Literature"
    });
    private JComboBox<String> gradeBox = new JComboBox<>(new String[]{
        "A+","A","A-","B+","B","B-","C+","C","C-","D","F","Not Assigned"
    });
    private JComboBox<Integer> yearBox;

    private JLabel feedback = new JLabel(" ");

    private Student editTarget = null;   // null = add mode
    private Runnable onSave;

    public StudentForm(Runnable onSave) {
        this.onSave = onSave;
        buildUI("Add New Student");
    }

    public void loadForEdit(Student s) {
        this.editTarget = s;
        sidField.setText(s.getStudentId());
        sidField.setEditable(false);
        fnField.setText(s.getFirstName());
        lnField.setText(s.getLastName());
        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhone() != null ? s.getPhone() : "");
        addrField.setText(s.getAddress() != null ? s.getAddress() : "");
        deptBox.setSelectedItem(s.getDepartment());
        gradeBox.setSelectedItem(s.getGrade() != null ? s.getGrade() : "Not Assigned");
        if (s.getEnrollDate() != null) yearBox.setSelectedItem(s.getEnrollDate().getYear());
        feedback.setText(" ");

        // Swap heading label
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel && ((JLabel)c).getText().contains("Student")) {
                ((JLabel)c).setText("Edit Student");
            }
        }
    }

    public void resetToAdd() {
        editTarget = null;
        sidField.setEditable(true);
        sidField.setText("");
        fnField.setText(""); lnField.setText("");
        emailField.setText(""); phoneField.setText(""); addrField.setText("");
        deptBox.setSelectedIndex(0);
        gradeBox.setSelectedIndex(11);
        yearBox.setSelectedItem(LocalDate.now().getYear());
        feedback.setText(" ");
    }

    private void buildUI(String heading) {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // ── Heading ───────────────────────────────────────────────────────────
        JLabel title = new JLabel(heading);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BLUE);
        title.setBorder(BorderFactory.createEmptyBorder(0,0,16,0));
        add(title, BorderLayout.NORTH);

        // ── Form grid ─────────────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 6, 7, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        Integer[] years = new Integer[15];
        int yr = LocalDate.now().getYear();
        for (int i = 0; i < 15; i++) years[i] = yr - i;
        yearBox = new JComboBox<>(years);
        styleCombo(deptBox); styleCombo(gradeBox); styleCombo(yearBox);

        // Row 0: Student ID | First Name
        addRow(grid, g, 0, 0, "Student ID *", sidField);
        addRow(grid, g, 0, 2, "First Name *", fnField);

        // Row 1: Last Name | Email
        addRow(grid, g, 1, 0, "Last Name *",  lnField);
        addRow(grid, g, 1, 2, "Email *",       emailField);

        // Row 2: Phone | Address
        addRow(grid, g, 2, 0, "Phone",         phoneField);
        addRow(grid, g, 2, 2, "Address",        addrField);

        // Row 3: Department | Grade
        addRow(grid, g, 3, 0, "Department *",  deptBox);
        addRow(grid, g, 3, 2, "Grade",          gradeBox);

        // Row 4: Year
        addRow(grid, g, 4, 0, "Enrol Year",    yearBox);

        add(grid, BorderLayout.CENTER);

        // ── Button bar ────────────────────────────────────────────────────────
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bar.setBackground(BG);

        feedback.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton clearBtn = new JButton("Clear");
        JButton saveBtn  = new JButton("Save Student");

        styleBtn(clearBtn, new Color(100, 116, 139), Color.WHITE);
        styleBtn(saveBtn,  BLUE, Color.WHITE);

        clearBtn.addActionListener(e -> resetToAdd());
        saveBtn.addActionListener(e  -> save());

        bar.add(feedback);
        bar.add(clearBtn);
        bar.add(saveBtn);
        add(bar, BorderLayout.SOUTH);
    }

    private void save() {
        String sid   = sidField.getText().trim();
        String fn    = fnField.getText().trim();
        String ln    = lnField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String addr  = addrField.getText().trim();
        String dept  = (String)  deptBox.getSelectedItem();
        String grade = (String)  gradeBox.getSelectedItem();
        int    year  = (Integer) yearBox.getSelectedItem();

        if (sid.isEmpty() || fn.isEmpty() || ln.isEmpty() || email.isEmpty()) {
            setFeedback("Fields marked * are required.", RED); return;
        }
        if (!email.contains("@")) {
            setFeedback("Please enter a valid email address.", RED); return;
        }

        Student s = editTarget != null ? editTarget : new Student();
        s.setStudentId(sid);
        s.setFirstName(fn); s.setLastName(ln);
        s.setEmail(email);  s.setPhone(phone);
        s.setAddress(addr); s.setDepartment(dept);
        s.setGrade("Not Assigned".equals(grade) ? null : grade);
        s.setEnrollDate(LocalDate.of(year, 1, 1));

        StudentDAO dao = new StudentDAO();
        boolean ok;
        if (editTarget == null) {
            ok = dao.addStudent(s);
        } else {
            ok = dao.updateStudent(s);
        }

        if (ok) {
            setFeedback(editTarget == null ? "Student saved!" : "Student updated!", GREEN);
            resetToAdd();
            if (onSave != null) onSave.run();
        } else {
            setFeedback("Save failed. Check DB connection.", RED);
        }
    }

    private void setFeedback(String msg, Color c) {
        feedback.setText(msg);
        feedback.setForeground(c);
    }

    // ── Layout helpers ────────────────────────────────────────────────────────

    private void addRow(JPanel p, GridBagConstraints g, int row, int col, String label, JComponent field) {
        g.gridx = col;     g.gridy = row; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(LABEL_FG);
        lbl.setPreferredSize(new Dimension(110, 26));
        p.add(lbl, g);

        g.gridx = col + 1; g.weightx = 1;
        p.add(field, g);
    }

    static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(180, 32));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    static void styleCombo(JComboBox<?> b) {
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setPreferredSize(new Dimension(180, 32));
        b.setBackground(Color.WHITE);
    }

    static void styleBtn(JButton b, Color bg, Color fg) {
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(140, 36));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
    }
}