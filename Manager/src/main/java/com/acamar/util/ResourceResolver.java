package com.acamar.util;

import java.net.URL;

/**
 * JustChat
 *
 * @link https://github.com/brian978/JustChat
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class ResourceResolver
{
    public static URL getResource(Class object, String relativePath)
    {
        return object.getClassLoader().getResource(object.getPackage().getName().replace('.', '/') + relativePath);
    }
}
