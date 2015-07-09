package alexiil.starter;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class DownloadProgress extends JFrame {
    private static int currentIndex = 0;
    private static Map<Integer, DownloadProgress> map = new HashMap<Integer, DownloadProgress>();

    private JPanel contentPane;
    private JProgressBar progressBar;
    private JList<String> list;
    private final App app;
    private boolean[] downs;

    /**
     * Launch the application.
     */
    public static Runnable open(App app) {
        final int index = currentIndex++;
        Runnable run =
            () -> {
                DownloadProgress dp = map.remove(index);
                if (dp != null) {
                    dp.dispose();
                }
                JOptionPane.showMessageDialog(null, "Successfully downloaded all the required libraries!", "Download Status",
                    JOptionPane.INFORMATION_MESSAGE);
            };
        DownloadProgress frame = new DownloadProgress(app);
        map.put(index, frame);
        EventQueue.invokeLater(() -> {
            try {
                frame.init();
                frame.setVisible(true);
                frame.update();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        return run;
    }

    /**
     * Create the frame.
     */
    public DownloadProgress(App app) {
        this.app = app;
    }

    public void init() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        progressBar = new JProgressBar();
        contentPane.add(progressBar, BorderLayout.SOUTH);

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        list = new JList<String>();
        String[] deps = new String[app.dependencies.size()];
        downs = new boolean[deps.length];
        for (int i = 0; i < app.dependencies.size(); i++) {
            IDependency dep = app.dependencies.get(i);
            deps[i] = dep.getName();
            downs[i] = dep.exists();
        }
        list.setModel(new AbstractListModel<String>() {
            String[] values = deps;
            boolean[] downloaded = downs;

            public int getSize() {
                return values.length;
            }

            public String getElementAt(int index) {
                return (downloaded[index] ? "\u2611  " : "\u2610  ") + values[index];
            }
        });
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentPane.add(list, BorderLayout.CENTER);

        contentPane.invalidate();
        contentPane.repaint();
    }

    private void updateDownloads() {
        for (int i = 0; i < downs.length; i++) {
            downs[i] = app.dependencies.get(i).exists();
        }
    }

    public void update() {
        final Timer timer = new Timer(17, null);
        ActionListener list = (event) -> {
            if (!app.isInstalling()) {
                timer.stop();
            }

            if (app.consumeFinishedDepDownload()) {
                updateDownloads();
            }

            progressBar.setValue((int) (app.getInstallProgress() * 100));
            contentPane.invalidate();
            contentPane.repaint();
        };
        timer.addActionListener(list);
        timer.start();
    }
}
