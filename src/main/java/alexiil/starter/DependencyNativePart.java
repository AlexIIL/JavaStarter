package alexiil.starter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class DependencyNativePart implements IDependency {
    public final String name, file, url;

    public DependencyNativePart(String name, String fileName, String url) {
        this.name = name;
        this.file = fileName;
        this.url = url;
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
        return null;
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

        folderBase = new File(folderBase, "libs");
        if (!folderBase.isDirectory()) {
            folderBase.mkdir();
        }

        folderBase = new File(folderBase, "natives");
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
    public List<IDependency> getDependencies() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isApp() {
        return false;
    }

    @Override
    public String getType() {
        return "native-part";
    }
}
