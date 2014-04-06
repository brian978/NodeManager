package nodemanager.node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * NodeManager
 *
 * @link https://github.com/brian978/NodeManager
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class Logger
{
    protected BufferedWriter writer = null;

    public Logger()
    {
    }

    public Logger(File file)
    {
        createWriter(file);
    }

    public Logger(String fileName)
    {
        createWriter(new File(fileName));
    }

    private void createWriter(File file)
    {
        try {
            System.out.println("Logging to " + file.getAbsolutePath());
            writer = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFile(File file)
    {
        if(writer == null) {
            createWriter(file);
        }
    }

    public Logger log(String line)
    {
        if(writer != null) {
            try {
                writer.write(line);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public void flush()
    {
        if(writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close()
    {
        if(writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
