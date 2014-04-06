package nodemanager.node;

/**
 * NodeManager
 *
 * @link https://github.com/brian978/NodeManager
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class NodeProperties
{
    private String name = "";
    private String path = "";
    private boolean logging = false;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isLogging()
    {
        return logging;
    }

    public void setLogging(boolean logging)
    {
        this.logging = logging;
    }
}
