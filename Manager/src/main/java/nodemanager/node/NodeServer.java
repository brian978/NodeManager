package nodemanager.node;

import java.io.File;

/**
 * NodeManager
 *
 * @link https://github.com/brian978/NodeManager
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class NodeServer
{
    private String name = "";
    private File file = null;
    private boolean logging = false;

    public String getName()
    {
        return name;
    }

    /**
     * Called using Reflection
     *
     * @param name NodeJS instance name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public File getFile()
    {
        return file;
    }

    /**
     * Called using Reflection
     *
     * @param path NodeJS application path
     */
    public void setPath(String path)
    {
        file = new File(path);
    }

    public boolean isLogging()
    {
        return logging;
    }

    /**
     * Called using Reflection
     *
     * @param logging Used to enable or disable logging
     */
    public void setLogging(String logging)
    {
        this.logging = Boolean.parseBoolean(logging);
    }
}
