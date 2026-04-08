package ui;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("Student Management System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Add Student", new StudentForm());
        tabs.add("View Students", new StudentTable());

        add(tabs);

        setVisible(true);
    }
}