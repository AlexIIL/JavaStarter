package alexiil.starter;

import java.util.ArrayList;
import java.util.List;

public class DependencyLwjgl extends DependencyJar {
    public DependencyLwjgl(String name, String file, String url) {
        super(name, file, url, false);
    }

    @Override
    public String getType() {
        return "lwjgl";
    }

    @Override
    public List<IDependency> getDependencies() {
        List<IDependency> deps = new ArrayList<IDependency>();

        return deps;
    }
}
