package ui;

import javax.swing.*;
import java.awt.*;

public class StudentTable extends JPanel {

    JTextArea area = new JTextArea();

    public StudentTable() {

        setLayout(new BorderLayout());

        JButton refresh = new JButton("Refresh");

        add(refresh, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);

        refresh.addActionListener(e -> {
            area.setText("No students yet...");
        });
    }
}