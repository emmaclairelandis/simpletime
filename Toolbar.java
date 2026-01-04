// https://www.youtube.com/watch?v=_rztg4x_fq8

import javax.swing.*;
import java.awt.BorderLayout;

public class Toolbar {
    JFrame frame = new JFrame();
    JTextField textField = new JTextField(15);
    JButton btnAdd = new JButton("Add");
    JButton btnDelete = new JButton("Delete");
    JToolBar toolBar = new JToolBar();

    public Toolbar() {
        toolBar.add(textField);
        toolBar.add(btnAdd);
        toolBar.add(btnDelete);
        frame.add(toolBar, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
