package alexiil.starter;

import java.io.File;

public class DependencyJar implements IDependency {
    public final String url;

    public DependencyJar(String url) {
        this.url = url;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public void putInPlace(File currentLocation, File libraryBase) {

    }

    @Override
    public String getLaunchProperties(File libraryBase) {
        return "";
    }

    @Override
    public String getURL() {
        return url;
    }
}
