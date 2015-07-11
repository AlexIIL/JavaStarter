package alexiil.starter;

import java.io.File;
import java.util.List;

public interface IDependency {
    /** @return True if all the parts of this dependency are satisfied completely */
    public boolean exists();

    /**
     * This is given the single file that has been downloaded, and should extract it to the proper place (Somewhere
     * inside of the librarayBase folder)
     */
    public void putInPlace(File currentLocation);

    /** This should get the properties required for launching with the dependencies */
    public String getClasspath();

    public String getURL();

    /** This should return a list of dependencies that this dependency requires. It is safe to return null. */
    public List<IDependency> getDependencies();

    public String getFileName();

    public String getName();

    public boolean isApp();

    public String getType();

    public default String getArguments() {
        return "";
    }
}
