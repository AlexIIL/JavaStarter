package alexiil.starter;

import java.io.File;

public interface IDependency {
    /** @return True if all the parts of this dependency are satisfied completely */
    public boolean exists();

    /** This is given the single file that has been downloaded, and should extract it to the proper place (Somewhere
     * inside of the librarayBase folder) */
    public void putInPlace(File currentLocation, File libraryBase);

    /** This should get the properties required for launching with the dependencies */
    public String getLaunchProperties(File libraryBase);

    public String getURL();
}
