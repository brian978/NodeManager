package com.acamar.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 * JustChat
 *
 * @link https://github.com/brian978/JustChat
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class Properties extends java.util.Properties
{
    protected File file;

    public Properties(String filename)
    {
        this.file = new File(filename);
    }

    public Properties(File file)
    {
        this.file = file;
    }

    public void load() throws IOException
    {
        load(new FileInputStream(this.file));
    }

    public boolean checkAndLoad()
    {
        boolean fileLoaded = false;

        if (!file.exists()) {
            try {
                if(file.createNewFile()) {
                    store();
                    fileLoaded = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                load();
                fileLoaded = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileLoaded;
    }

    public void store() throws IOException
    {
        FileOutputStream stream = new FileOutputStream(this.file);
        OutputStreamWriter writer = new OutputStreamWriter(stream, Charset.forName("UTF-8"));
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        store(bufferedWriter, "Generated by Acamar package");
    }
}
