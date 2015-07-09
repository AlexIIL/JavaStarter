package alexiil.starter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class App {
    public static final String DEPENDENCY_NAME = "dependency.name.";
    // The URL to download the file from
    public static final String DEPENDENCY_LOCATION = "dependency.location.";
    // The type of the dependency. Can be JAR or NATIVE
    public static final String DEPENDENCY_TYPE = "dependency.type.";
    public static final String DEPENDENCY_TYPE_JAR = "jar";
    public static final String DEPENDENCY_TYPE_NATIVE = "native";
    public static final String START_LOCATION = "folder.start";

    public final List<IDependency> dependencies;
    private String startLocation;

    public App(Properties props) {
        List<IDependency> depends = new ArrayList<IDependency>();
        for (Object obj : props.entrySet()) {
            String key = (String) obj;
            String value = props.getProperty(key);

            if (value.equalsIgnoreCase("true") && key.startsWith(DEPENDENCY_NAME)) {
                String dependency = key.substring(DEPENDENCY_NAME.length());
                String location = props.getProperty(DEPENDENCY_LOCATION + dependency);
                String type = props.getProperty(DEPENDENCY_TYPE + dependency);
            }
            else if (key.equalsIgnoreCase(START_LOCATION)) {
                startLocation = value;
            }

        }
        dependencies = Collections.unmodifiableList(depends);
    }

    /**
     * Open the application, downloading all dependencies first (If required)
     */
    public void start(File appBase) throws IOException {
        File libraryBase = new File(appBase, "libs");
        String args = "";
        for (IDependency depend : dependencies) {
            if (!depend.exists()) {
                File currentLocation = DependencyDownloader.tryThrice(depend.getURL());
                depend.putInPlace(currentLocation, libraryBase);
                args += " " + depend.getLaunchProperties(libraryBase);
            }
        }

        String javaDir = System.getProperty("java.home") + "/bin/java";

        String command = javaDir + "";
        File launchDir = new File(startLocation);

        Runtime.getRuntime().exec(command, null, launchDir);
    }
}
