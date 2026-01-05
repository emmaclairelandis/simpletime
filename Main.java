import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //If you get an error saying you need X11 support or something, and you're on WSL, just boot through Powershell
        //Stopwatch stopwatch = new Stopwatch();
        Data.dataCollection();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SimpleTime");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280,720);

            frame.setJMenuBar(Menubar.createMenuBar());

            frame.add(new Stopwatch());

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        
        });
        }
    }

