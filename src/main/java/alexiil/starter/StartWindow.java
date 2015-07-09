package alexiil.starter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class StartWindow {
    private JFrame frame;
    private static StartWindow window;
    private static final List<App> apps = new ArrayList<App>();
    private static final App thisApp = ApplicationResolver.resolveThis();

    private JPanel pnlThisApp, pnlAppList;
    private JScrollPane spAppList;

    static {
        try {
            /*
             * Set the current look and feel of the application to the current system's look (so, if this is on windows,
             * it will look like other windows applications, and if this is on MAC OSX then it will look like MAC
             * applications, etc). Most of this is just debug code (to support changing the L&F later, to see what L&F's
             * exist.
             */
            System.out.println("All look and feels currently installed:");
            LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
            for (LookAndFeelInfo lf : looks)
                System.out.println("  -" + lf.getName() + "=" + lf.getClassName());
            String clsName = UIManager.getSystemLookAndFeelClassName();
            System.out.println("System look and feel:");
            System.out.println("  -" + UIManager.getSystemLookAndFeelClassName());
            System.out.println("Using look and feel:");
            System.out.println("  -" + clsName);
            UIManager.setLookAndFeel(clsName);
            LookAndFeel laf = UIManager.getLookAndFeel();
            System.out.println("  -" + laf.toString());
        }
        catch (Exception e) {
            /*
             * This SHOULDN'T ever throw (as we are passing a known argument back to the UIManager.setLookAndFeel). If
             * it does, its not too big of a problem, as the default look and feel will be used (which looks slightly
             * different, but shouldn't stop the user from being able to use it.
             */
            e.printStackTrace();
        }
    }

    /** Launch the application. */
    public static void main(String[] args) {
        try {
            EventQueue.invokeAndWait(() -> {
                window = new StartWindow();
                window.frame.setVisible(true);
                if (thisApp != null) {
                    window.addApp(thisApp, window.pnlThisApp);
                }
            });
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        ApplicationResolver.resolve(apps, () -> {
            window.addApp(apps.get(apps.size() - 1), window.addPanelToList());
        });
    }

    /** @wbp.parser.entryPoint */
    public StartWindow() {
        initialize();
    }

    /** Initialize the contents of the frame. */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pnlThisApp = new JPanel();
        pnlThisApp.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Install this:"));
        frame.add(pnlThisApp, BorderLayout.SOUTH);

        spAppList = new JScrollPane();
        spAppList.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Installed apps:"));
        frame.add(spAppList, BorderLayout.NORTH);

        pnlAppList = new JPanel();
        pnlAppList.setBackground(Color.WHITE);
        pnlAppList.setLayout(new BoxLayout(pnlAppList, BoxLayout.Y_AXIS));
        spAppList.setViewportView(pnlAppList);

    }

    private JPanel addPanelToList() {
        JPanel pnl = new JPanel();
        pnlAppList.add(pnl);
        pnl.setBorder(new LineBorder(Color.GRAY, 1));
        return pnl;
    }

    private void addApp(App app, JPanel panel) {
        panel.removeAll();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel(app.name));

        JButton install = new JButton();
        install.setText("Install");
        install.setVisible(panel == pnlThisApp);
        panel.add(install);

        JButton deps = new JButton();
        deps.setText("Download Dependencies");
        panel.add(deps);

        JButton launch = new JButton();
        launch.setText("Launch!");
        launch.setEnabled(app.areDependenciesSatisfied());
        panel.add(launch);

        install.addActionListener((event) -> {
            File location = new File(System.getProperty("user.home"), ".java-starter");
            File folder = new File(location, app.startLocation);
            if (app.writeInfo(new File(folder, "app-info"))) {
                File appList = new File(location, "app-list");
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(appList));
                    File tempFile = new File(appList, "temp");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                    writer.write(app.startLocation);
                    reader.close();
                    writer.close();

                    Files.move(appList.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    install.setVisible(false);
                    frame.remove(pnlThisApp);
                    pnlAppList.add(pnlThisApp);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        deps.addActionListener((event) -> {
            Runnable run = DownloadProgress.open(app);
            app.installDependencies(() -> {
                launch.setEnabled(app.areDependenciesSatisfied());
                run.run();
            });
        });

        launch.addActionListener((event) -> {
            try {
                app.start();
            }
            catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, splitUp(e.getMessage()), "Error while opening the app!", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String splitUp(String message) {
        String holder = "";
        int index = 0;
        while (index < message.length()) {
            int oldIndex = index;
            index += Math.min(message.length() - index, 40);
            holder += message.substring(oldIndex, index) + "\n";
        }
        return holder;
    }
}
