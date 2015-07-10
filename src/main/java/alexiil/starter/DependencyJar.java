package alexiil.starter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
    public String getClasspath() {
        String f = System.getProperty("file.separator");
        return System.getProperty("user.home") + f + ".java-starter" + f + (isApp ? "apps" : "libs") + f + file;
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

    @Override
    public List<IDependency> getDependencies() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + (isApp ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DependencyJar other = (DependencyJar) obj;
        if (file == null) {
            if (other.file != null)
                return false;
        }
        else if (!file.equals(other.file))
            return false;
        if (isApp != other.isApp)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        return true;
    }
}
