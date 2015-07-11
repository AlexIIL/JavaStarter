package alexiil.starter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DependencyLwjgl extends DependencyJar {
    public DependencyLwjgl(String file) {
        super("lwjgl", file, "http://build.lwjgl.org/release/latest/lwjgl.zip", false);
    }

    @Override
    public String getType() {
        return "lwjgl";
    }

    @Override
    public void putInPlace(File currentLocation) {
        String[] files =
            new String[] { "liblwjgl.dylib", "liblwjgl.so", "liblwjgl32.so", "libopenal.dylib", "libopenal.so", "libopenal32.so", "lwjgl.dll",
                "lwjgl32.dll", "OpenAL.dll", "OpenAL32.dll" };

        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(currentLocation));

            File base = makeFolders();
            File natives = new File(base, "lwjgl-natives");
            natives.mkdir();

            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                if (name.endsWith("lwjgl.jar")) {
                    File loc = new File(base, "lwjgl.jar");
                    Files.copy(zis, loc.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    continue;
                }

                for (String file : files) {
                    if (name.endsWith(file)) {
                        File loc = new File(natives, file);
                        Files.copy(zis, loc.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        break;
                    }
                }
                zis.closeEntry();
            }
            zis.close();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
    }

    @Override
    public boolean exists() {
        File folder = makeFolders();
        File jar = new File(folder, file);
        boolean exists = jar.exists();
        System.out.println(jar + " does" + (exists ? " " : "n't ") + "exist, assuming the natives also do" + (exists ? "" : "n't") + " exist");
        return jar.exists();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj.getClass() == getClass();
    }

    @Override
    public String getArguments() {
        String f = System.getProperty("file.seperator");
        return "-Dorg.lwjgl.librarypath=\"" + System.getProperty("user.dir") + f + ".java-starter" + f + "libs" + f + "lwjgl-natives\"";
    }
}
