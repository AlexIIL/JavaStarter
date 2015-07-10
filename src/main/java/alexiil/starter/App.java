package alexiil.starter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class App {
    public static final String NAME = "name";
    public static final String DEPENDENCY_NAME = "dependency.name.";
    // The URL to download the file from
    public static final String DEPENDENCY_LOCATION = "dependency.location.";
    // The file name to use for the dependency
    public static final String DEPENDENCY_FILE = "dependency.file.";
    // The type of the dependency. Can be JAR or NATIVE
    public static final String DEPENDENCY_TYPE = "dependency.type.";
    public static final String DEPENDENCY_TYPE_JAR = "jar";
    // Special dependency type for LWJGL. (It needs specialist treatment to work properly)
    public static final String DEPENDENCY_TYPE_LWJGL = "lwjgl";
    public static final String START_LOCATION = "folder.start";
    // The location (URL) of the actual app we are trying to run
    public static final String APP_LOCATION = "location";
    // The class with a main method to start
    public static final String MAIN_CLASS = "mainclass";

    public final List<IDependency> dependencies;
    private final MutableDouble installProgress = new MutableDouble();
    public String startLocation;
    public final String name, mainClass;
    private volatile int downloads = 0;

    public App(Properties props) {
        List<IDependency> depends = new ArrayList<IDependency>();
        String tempName = "";
        String locationURL = "";
        String tempMain = "";
        for (Entry<Object, Object> obj : props.entrySet()) {
            String key = (String) obj.getKey();
            String value = props.getProperty(key);

            if (key.startsWith(DEPENDENCY_NAME)) {
                String dependency = key.substring(DEPENDENCY_NAME.length());
                String location = props.getProperty(DEPENDENCY_LOCATION + dependency);
                String type = props.getProperty(DEPENDENCY_TYPE + dependency);
                String file = props.getProperty(DEPENDENCY_FILE + dependency);

                IDependency dep = null;

                if (type.equalsIgnoreCase(DEPENDENCY_TYPE_JAR)) {
                    dep = new DependencyJar(dependency, file, location, false);
                    depends.add(dep);
                }
            }
            else if (key.equalsIgnoreCase(START_LOCATION)) {
                startLocation = value;
            }
            else if (key.equalsIgnoreCase(NAME)) {
                tempName = value;
            }
            else if (key.equalsIgnoreCase(APP_LOCATION)) {
                locationURL = value;
            }
            else if (key.equalsIgnoreCase(MAIN_CLASS)) {
                tempMain = value;
            }
        }
        name = tempName;
        mainClass = tempMain;
        String file = name + ".jar";
        depends.add(new DependencyJar(name, file, locationURL, true));

        dependencies = Collections.unmodifiableList(depends);

        System.out.println("Found the app " + name);
        System.out.println("Dependencies:");
        for (IDependency dep : dependencies) {
            System.out.println("  -" + dep.getFileName() + " from " + dep.getURL());
        }

        installProgress.value = -1;
    }

    /**
     * Open the application, downloading all dependencies first (If required). Generally, this won't download the
     * dependencies, unless they were deleted for some reason between opening the launcher and clicking launch.
     */
    public void start() throws IOException {
        if (!areDependenciesSatisfied()) {
            installDependencies(false);
            if (!areDependenciesSatisfied())
                throw new IOException("Dependencies were not satisfied\nBut an exception hasn't happened!\nWhat?");
        }

        String f = System.getProperty("file.separator");

        String args = "-cp \".";

        for (IDependency dep : dependencies) {
            args += System.getProperty("path.separator") + dep.getClasspath();
        }

        args += "\" " + mainClass;

        String javaDir = System.getProperty("java.home") + f + "bin" + f + "java";

        String command = javaDir + " " + args;
        File launchDir = new File(System.getProperty("user.home"), ".java-starter/" + startLocation);

        System.out.println("Launching " + name + " as " + command);

        if (!launchDir.exists()) {
            launchDir.mkdirs();
            System.out.println("Created " + launchDir);
        }

        Runtime.getRuntime().exec(command, null, launchDir);
    }

    public boolean isInstalling() {
        return installProgress.value != -1;
    }

    public double getInstallProgress() {
        return installProgress.value;
    }

    public boolean areDependenciesSatisfied() {
        boolean satisfied = true;
        for (IDependency dep : dependencies) {
            if (!dep.exists()) {
                satisfied = false;
                break;
            }
        }
        return satisfied;
    }

    private void installDependenciesActual() throws IOException {
        int progress = 0;
        int needed = dependencies.size();
        installProgress.value = 0;
        for (IDependency dep : dependencies) {
            if (!dep.exists()) {
                String url = dep.getURL();
                File downloaded = DependencyDownloader.tryThrice(url, installProgress, progress / (double) needed, (progress + 1) / (double) needed);
                dep.putInPlace(downloaded);
                progress++;
                installProgress.value = (double) progress / (double) needed;
            }
            downloads++;
        }
        installProgress.value = -1;
    }

    public void installDependencies(boolean runInOtherThread) throws IOException {
        installDependencies(false, null);
    }

    public void installDependencies(Runnable onCompletion) {
        try {
            installDependencies(true, onCompletion);
        }
        catch (IOException ignored) {
            // It will never throw an IOException.
        }
    }

    public void installDependencies(boolean runInOtherThread, Runnable onCompletion) throws IOException {
        if (!isInstalling()) {
            if (runInOtherThread)
                new Thread(() -> {
                    try {
                        installDependenciesActual();
                        if (onCompletion != null)
                            onCompletion.run();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }, "Dependency-Installer-" + name).start();
            else
                installDependenciesActual();
        }
    }

    public boolean writeInfo(File file) {
        if (file.exists())
            return false;
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(NAME + "=" + name + "\n");
            writer.write(START_LOCATION + "=" + startLocation + "\n");

            for (IDependency dep : dependencies) {
                writer.write("\n");
                writer.write(DEPENDENCY_NAME + dep.getName() + "=true\n");
                writer.write(DEPENDENCY_FILE + dep.getName() + "=" + dep.getFileName() + "\n");
                writer.write(DEPENDENCY_LOCATION + dep.getName() + "=" + dep.getURL() + "\n");
                writer.write(DEPENDENCY_TYPE + dep.getName() + "=" + dep.getType() + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            DependencyDownloader.closeQuietly(writer);
        }
        return true;
    }

    public boolean consumeFinishedDepDownload() {
        if (downloads <= 0)
            return false;
        downloads--;
        return true;
    }
}
