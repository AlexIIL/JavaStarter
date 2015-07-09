package alexiil.starter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ApplicationResolver {
    /** This resolves the apps currently listed inside this jar, and all in the directory specified by the file in this jar */
    public static List<App> resolve() {
        List<App> apps = new ArrayList<App>();
        Properties thisProp = new Properties();
        try {
            thisProp.load(ApplicationResolver.class.getResourceAsStream("app-info"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        apps.add(new App(thisProp));

        String base = thisProp.getProperty("folder.base");
        File appBase = null;
        if (base.startsWith("%USER-HOME%")) {
            appBase = new File(System.getProperty("user.home"), base.substring(11));
        }
        else {
            appBase = new File(base);
        }
        
        

        return apps;
    }
}
