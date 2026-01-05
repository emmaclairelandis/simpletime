// https://www.youtube.com/watch?v=0cATENiMsBE

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Stopwatch extends JPanel implements ActionListener {

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

    Timer timer = new Timer(1000, new ActionListener() {

        public void actionPerformed(ActionEvent e) {

            elapsedTime=elapsedTime+1000;
            hours = (elapsedTime/3600000);
            minutes = (elapsedTime/60000) % 60;
            seconds = (elapsedTime/1000) % 60;
            secondString = String.format("%02d", seconds);
            minuteString = String.format("%02d", minutes);
            hourString = String.format("%02d", hours);
            timeLabel.setText(hourString+":"+minuteString+":"+secondString);

        }

    });

    Stopwatch(){

        Dimension size = this.getSize();

        timeLabel.setText(hourString+":"+minuteString+":"+secondString);
        timeLabel.setBounds(
            ((size.width)/2),
            ((size.height)/2),
            200,
            100
        );
        timeLabel.setFont(new Font("Verdana",Font.PLAIN,35));
        timeLabel.setBorder(BorderFactory.createBevelBorder(1));
        timeLabel.setOpaque(true);
        timeLabel.setHorizontalAlignment(JTextField.CENTER);

        startButton.setBounds(100,200,100,50);
        startButton.setFont(new Font("Verdana",Font.PLAIN,20));
        startButton.setFocusable(false);
        startButton.addActionListener(this);

        resetButton.setBounds(200,200,100,50);
        resetButton.setFont(new Font("Verdana",Font.PLAIN,20));
        resetButton.setFocusable(false);
        resetButton.addActionListener(this);

        this.add(startButton);
        this.add(resetButton);
        this.add(timeLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==startButton) {
            start();
            if(started==false) {
                started=true;
                startButton.setText("STOP");
                start();
            }
            else {
                started=false;
                startButton.setText("START");
                stop();
            }
        }
        if(e.getSource()==resetButton) {
            started=false;
            startButton.setText("START");
            reset();
        }
    }

    void start() {
        timer.start();
    }

    void stop() {
        timer.stop();
    }

    void reset() {
        timer.stop();
        elapsedTime=0;
        seconds=0;
        minutes=0;
        hours=0;
        secondString = String.format("%02d", seconds);
        minuteString = String.format("%02d", minutes);
        hourString = String.format("%02d", hours);
        timeLabel.setText(hourString+":"+minuteString+":"+secondString);
    }
}
