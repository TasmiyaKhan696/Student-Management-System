package ui;

import javax.swing.*;
import java.awt.*;

public class StudentForm extends JPanel {

    JTextField nameField = new JTextField(15);
    JTextField deptField = new JTextField(15);

    public StudentForm() {

        setLayout(new GridLayout(3,2));

        add(new JLabel("Name:"));
        add(nameField);

        add(new JLabel("Department:"));
        add(deptField);

        JButton btn = new JButton("Add Student");
        add(btn);

        btn.addActionListener(e -> {
            String name = nameField.getText();
            String dept = deptField.getText();

            JOptionPane.showMessageDialog(this,
                    "Student Added:\n" + name + " - " + dept);
        });
    }
}