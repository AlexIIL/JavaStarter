package alexiil.starter;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JFrame;

public class StartWindow {
    private JFrame frame;

    /** Launch the application. */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    StartWindow window = new StartWindow();
                    window.frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        List<App> apps = ApplicationResolver.resolve();
    }

    /** Create the application. */
    public StartWindow() {
        initialize();
    }

    /** Initialize the contents of the frame. */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}