import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Stopwatch implements ActionListener {

    JFrame frame = new JFrame();
    JButton startButton = new JButton("START");
    JButton resetButton = new JButton("RESET");
    JLabel timeLabel = new JLabel();
    int elapsedTime = 0;
    int seconds = 0;
    int minutes = 0;
    int hours = 0;
    boolean started = false;
    String secondString = String.format("%02d", seconds);
    String minuteString = String.format("%02d", minutes);
    String hourString = String.format("%02d", hours);

    Stopwatch(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

    }

    void start() {

    }

    void stop() {

    }

    void reset() {

    }
}
