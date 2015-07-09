package alexiil.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ApplicationResolver {
    /** Returns null if this is not an installer */
    public static App resolveThis() {
        Properties thisProp = new Properties();
        InputStream stream = ApplicationResolver.class.getResourceAsStream("/app-info");
        if (stream == null) {
            System.out.println("This does not have an app file, assuming this is not an app");
            return null;
        }

        try {
            thisProp.load(stream);
        }
        catch (IOException e) {
            System.out.println("This had an app file, but failed to read for some reason!");
            e.printStackTrace();
            return null;
        }

        System.out.println("Successfully found this app!");

        return new App(thisProp);
    }

    /** This resolves the apps currently listed in the directory specified in the "user:home/.java-starter" folder*/
    public static void resolve(List<App> addToThis, Runnable onAdd) {
        File appBase = new File(System.getProperty("user.home"), ".java-starter");
        if (!appBase.isDirectory()) {
            appBase.mkdir();
            try {
                Files.setAttribute(appBase.toPath(), "dos:hidden", true);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        File appList = new File(appBase, "app-list");
        if (!appList.exists()) {
            return;
        }
        List<String> apps = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(appList));
            String line = null;
            while ((line = reader.readLine()) != null) {
                apps.add(line);
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                DependencyDownloader.closeQuietly(reader);
            }
        }

        for (String app : apps) {
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(new File(new File(appBase, app), "app-info")));
                addToThis.add(new App(prop));
                onAdd.run();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
