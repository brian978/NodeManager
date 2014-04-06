package nodemanager;

import com.acamar.util.os.OperatingSystem;
import com.acamar.util.os.OperatingSystemFamily;

import java.io.File;
import java.util.Vector;

/**
 * NodeManager
 *
 * @link https://github.com/brian978/NodeManager
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class ExecutableResolver
{
    private String osName = System.getProperty("os.name");
    File executable = null;

    public Vector<String> getPaths()
    {
        Vector<String> paths = new Vector<>();
        OperatingSystem os = OperatingSystem.resolve();

        if (os.getFamily() == OperatingSystemFamily.WINDOWS) {
            paths.add("C:\\Program Files\\nodejs\\node.exe");
            paths.add("C:\\Program Files (x86)\\nodejs\\node.exe");
        } else if (os.getFamily() == OperatingSystemFamily.LINUX || os.getFamily() == OperatingSystemFamily.UNIX) {
            paths.add("/usr/bin/nodejs");
        }

        return paths;
    }

    public File getFile()
    {
        // Checking the available paths
        if (executable == null) {
            for (String path : getPaths()) {
                executable = new File(path);
                if (executable.exists()) {
                    break;
                }
            }
        }

        return executable;
    }
}
