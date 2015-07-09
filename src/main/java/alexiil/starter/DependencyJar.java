package alexiil.starter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DependencyJar implements IDependency {
    public final String name, file, url;
    public final boolean isApp;

    public DependencyJar(String name, String fileName, String url, boolean isApp) {
        this.name = name;
        this.file = fileName;
        this.url = url;
        this.isApp = isApp;
    }

    @Override
    public boolean exists() {
        File folder = makeFolders();
        File jar = new File(folder, file);
        boolean exists = jar.exists();
        System.out.println(jar + " does" + (exists ? " " : "n't ") + "exist");
        return jar.exists();
    }

    @Override
    public void putInPlace(File currentLocation) {
        File libFolder = makeFolders();
        File neededFile = new File(libFolder, file);
        try {
            Files.copy(currentLocation.toPath(), neededFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLaunchProperties() {
        return "";
    }

    @Override
    public String getURL() {
        return url;
    }

    private File makeFolders() {
        File folderBase = new File(System.getProperty("user.home"), ".java-starter");
        if (!folderBase.isDirectory()) {
            folderBase.mkdir();
            try {
                Files.setAttribute(folderBase.toPath(), "dos:hidden", true);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        folderBase = new File(folderBase, isApp ? "apps" : "libs");
        if (!folderBase.isDirectory()) {
            folderBase.mkdir();
            try {
                Files.setAttribute(folderBase.toPath(), "dos:hidden", true);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return folderBase;
    }

    @Override
    public String getFileName() {
        return file;
    }

    @Override
    public String toString() {
        return "DependencyJar [name=" + name + ", file=" + file + ", url=" + url + ", isApp=" + isApp + "]";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isApp() {
        return isApp;
    }

    @Override
    public String getType() {
        return "jar";
    }
}
