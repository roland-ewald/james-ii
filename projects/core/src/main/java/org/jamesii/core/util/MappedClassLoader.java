/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class loader can be used to create custom mappings of compiled class
 * files to classnames. If loading a mapped class using this class loader the
 * mapped class content is prioritized rather than any other class
 * implementation within the class path.
 * 
 * @author Stefan Rybacki
 */
public class MappedClassLoader extends ClassLoader {

  /**
   * The className classContent mapping.
   */
  private final Map<String, byte[]> mapping = new HashMap<>();

  /**
   * Instantiates a new mapped class loader.
   */
  public MappedClassLoader() {
    super();
  }

  /**
   * Instantiates a new mapped class loader.
   * 
   * @param parent
   *          the parent classloader
   */
  public MappedClassLoader(ClassLoader parent) {
    super(parent);
  }

  /**
   * Maps a specific class file to a specific class name. The mapping is
   * prioritized if a mapped class is acquired using {@link #loadClass(String)}.
   * This can be used to control which class is actually loaded for the calling
   * class in case there are multiple versions of a class. Classes are preLoaded
   * that means when calling {@link #map(File, String)} the class file is loaded
   * and cached.
   * <p/>
   * If a compiled class file changed remap it and it will be reloaded.
   * 
   * @param classFile
   *          the compiled class file to load
   * @param className
   *          the class name the class file is loaded for
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void map(File classFile, String className) throws IOException {
    InputStream input = new BufferedInputStream(new FileInputStream(classFile));

    map(input, className);
  }

  /**
   * Maps a specific class file represented by a {@link InputStream} to a
   * specific class name. The mapping is prioritized if a mapped class is
   * acquired using {@link #loadClass(String)}. This can be used to control
   * which class is actually loaded for the calling class in case there are
   * multiple versions of a class. Classes are preLoaded that means when calling
   * {@link #map(InputStream, String)} the class file is loaded and cached.
   * <p/>
   * If a compiled class stream changed remap it and it will be reloaded.
   * 
   * @param input
   *          the compiled class file provided as input stream this way it can
   *          come from everywhere
   * @param className
   *          the class name the class file is loaded for
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void map(InputStream input, String className) throws IOException {
    // read the file
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int data = input.read();

    while (data != -1) {
      buffer.write(data);
      data = input.read();
    }
    input.close();
    byte[] content = buffer.toByteArray();

    mapping.put(className, content);
  }

  @Override
  public java.lang.Class<?> loadClass(String name)
      throws ClassNotFoundException {
    if (!mapping.containsKey(name)) {
      return super.loadClass(name);
    }

    byte[] classData = mapping.get(name);
    try {
      return defineClass(name, classData, 0, classData.length);
    } catch (Exception e) {
      return super.loadClass(name);
    }
  }
}
