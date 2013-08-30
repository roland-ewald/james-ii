/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.core.plugins.PluginLoadException;
import org.xml.sax.InputSource;

/**
 * The disc plug-in finder is the most simple, but a brute force, way to enable
 * support for plug'n simulate. It allows to automatically detect new extension
 * points and extensions in a list of directories.
 * 
 * Therefore it searches in predefined and additional paths, collects all
 * available plug-in type and plug-in classes and registers them.
 * 
 * @author Jan Himmelspach
 * 
 */
public class DiscPlugInFinder implements IPlugInFinder {

  private static final String JARFILE_ENDING = ".jar";

  /** The found plug-ins. */
  private List<IPluginData> foundPlugins = new ArrayList<>();

  /** The found plug-in types. */
  private List<IPluginTypeData> foundPluginTypes = new ArrayList<>();

  /** The paths the plug-ins have been found in. */
  private Map<IPluginData, String> paths = new HashMap<>();

  /** The list of jars found on the search paths. */
  private List<URL> jars = new ArrayList<>();

  /**
   * The reader used to parse the plug-in type and plug-in files.
   */
  XMLReader reader;

  /**
   * Instantiates a new disc plug in finder.
   */
  public DiscPlugInFinder() {
    super();

    reader = new XMLReader();

  }

  /**
   * Search in the given directory (and all its sub directories) for XML files.
   * 
   * @param directory
   */
  public void parseDirectory(String directory) {
    parseDirectory(directory, true);
  }

  /**
   * Search in the given directory (and if recursive is true in all its sub
   * directories) for XML files.
   * 
   * @param directory
   *          The directory to start the search with
   * @param recursive
   *          Set to true if the subdirs shall be included in the search
   */
  public void parseDirectory(String directory, boolean recursive) {
    // fetch all entries of the directory

    if (directory == null || !(new File(directory)).exists()) {
      throw new PluginLoadException("Plug-in dir " + directory + " not found!");
    }

    File f = new File(directory);

    // Support for JAR files that have been added to the class path directly
    if (!f.isDirectory() && directory.endsWith(JARFILE_ENDING)) {
      processJARFile(f);
      return; // we don't need to recurse from here
    }

    File[] files = f.listFiles();

    if (files == null) {
      throw new RuntimeException("Plug-in dir " + directory + " empty!");
    }

    for (int i = 0; i < files.length; i++) {
      if (files[i].isFile()) {
        String s = files[i].getPath(); // using getPath instead of
        // getName to be
        // able to read jars in other
        // dirs than
        // the current working directory
        // if we have found a jar archive file we'll get in and parse it
        if ((s.endsWith(JARFILE_ENDING))) {
          processJARFile(new File(s));
        }

        // if we have found a plug-in type description we have to read
        if ((s.endsWith("plugintype.xml"))
            || (s.endsWith("plugintype.XML") || (s.endsWith(".plugintype")))) {

          // if (Files.readASCIIFile(files[i]).contains(
          // "http://www.jamesii.org/plugintype"))
          {
            InputSource source = null;
            try {
              source = new InputSource(new FileInputStream(files[i]));
            } catch (FileNotFoundException e) {
              SimSystem.report(e);
            }

            // read the XML type file and add a found plug-in to the
            // internal list
            // of plug-in types found
            IPluginTypeData data =
                reader.readPluginTypeXMLFile(source, directory + " --- " + s);
            if (data != null) {
              foundPluginTypes.add(data);
            }

          }

        }

        // if xml file found, open, read, add, get out
        if ((s.endsWith("plugin.xml"))
            || (s.endsWith("plugin.XML") || (s.endsWith(".plugin")))) {
          // if (Files.readASCIIFile(files[i]).contains(
          // "http://www.jamesii.org/plugin"))
          {
            InputSource source = null;
            try {
              source = new InputSource(new FileInputStream(files[i]));
            } catch (FileNotFoundException e) {
              SimSystem.report(e);
            }

            // read the XML file and add a found plugin to the internal
            // list of
            // plug-ins found

            try {
              IPluginData data =
                  reader.readPluginXMLFile(source, s,
                      "Path:" + files[i].getAbsolutePath());
              if (data != null) {
                foundPlugins.add(data);
              }
              // readXMLFile has added the plug-in to foundPlugins
              paths.put(foundPlugins.get(foundPlugins.size() - 1),
                  files[i].getAbsolutePath());

              SimSystem.report(Level.INFO,
                  "Found a plug-in " + files[i].getAbsolutePath());

            } catch (RuntimeException e) {
              SimSystem.report(Level.SEVERE, "Was not able to read file "
              + files[i] + " \nOriginal exception was: " + e.toString()
              + " --- \n");
              SimSystem.report(e);
            }

            // recursive = false; we can currently have even more
            // plugins in sub
            // dirs!!!!!
            // break; remove by JH -> there can be more than one xml
            // file per
            // directory!
          }

        }

      }
    }

    if (recursive) {
      // iterate over all items of the file list
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          // if entry is a directory get in (recursively)
          parseDirectory(files[i].getAbsolutePath(), recursive);
        }
      }
    }

  }

  private void processJARFile(File jarFile) {
    try {
      // remember the jar file in the list of jars to be added to the
      // class path
      URL url = jarFile.toURI().toURL();
      if (jars.contains(url)) {
        // skip this jas as it has already been scanned (is added to the
        // classpath and contained in a directory scanned, e.g.)
        return;
      }
      jars.add(url);
    } catch (MalformedURLException e) {
      SimSystem.report(e);
    }

    parseJar(jarFile);
  }

  /**
   * Parses the passed jar file. Thus the plug-in management system can find and
   * use plug-ins in jar archives.
   * 
   * @param jarFile
   *          the jar file
   */
  public void parseJar(File jarFile) {
    JarFile archive = null;
    try {
      archive = new JarFile(jarFile);
    } catch (IOException e) {
      SimSystem.report(Level.WARNING, "Was not able to read jar file in path "
          + jarFile.getPath() + " with name " + jarFile.getName(), e);
      return;
    }

    List<String> pluginNames = new ArrayList<>();

    Enumeration<JarEntry> entries = archive.entries();
    while (entries.hasMoreElements()) {
      JarEntry curEntry = entries.nextElement();
      String fileName = curEntry.getName();
      if (fileName.endsWith("plugin.xml") || fileName.endsWith(".plugin")) {

        try {
          InputStream xmlInput = archive.getInputStream(curEntry);
          InputSource source = new InputSource(xmlInput);

          try {
            IPluginData data =
                reader.readPluginXMLFile(source, fileName, "JAR file:"
                    + jarFile.getAbsolutePath());
            foundPlugins.add(data);
            paths.put(foundPlugins.get(foundPlugins.size() - 1),
                jarFile.getName() + ":" + fileName);
            pluginNames.add(fileName);
          } catch (RuntimeException e) {
            SimSystem.report(Level.SEVERE, "Was not able to read file "
                + fileName + "\nOriginal exception was: " + e.toString());
          }

        } catch (IOException e) {
          SimSystem.report(e);
        }
      }
      if (fileName.endsWith("plugintype.xml")
          || fileName.endsWith(".plugintype")) {

        try {
          InputStream xmlInput = archive.getInputStream(curEntry);
          InputSource source = new InputSource(xmlInput);

          IPluginTypeData data = reader.readPluginTypeXMLFile(source, fileName);
          if (data != null) {
            foundPluginTypes.add(data);
          }

          // if (readXMLTypeFile(source)) {
          // pluginNames.add(fileName);
          // }

        } catch (IOException e) {
          SimSystem.report(e);
        }
      }
    }

    if (pluginNames.size() > 0) {

      StringBuffer s =
          new StringBuffer("Found plug-in(s) in " + jarFile.getName() + ":");
      for (String curName : pluginNames) {
        s.append("    " + curName);
      }
      SimSystem.report(Level.INFO, s.toString());
    }

  }

  private void addToPluginTypeList(IPluginTypeData pluginTypeData) {
    for (IPluginTypeData d : foundPluginTypes) {
      if (d.getId().compareTo(pluginTypeData.getId()) == 0) {
        SimSystem.report(Level.INFO, "Plug-in type found twice ("
            + pluginTypeData.getId().getName() + "- "
            + pluginTypeData.getId().getVersion()
            + ". First plug-in type found will be used.");
        return;
      }
    }
    foundPluginTypes.add(pluginTypeData);
  }

  @Override
  public List<IPluginTypeData> getFoundPluginTypes() {
    return foundPluginTypes;
  }

  @Override
  public List<IPluginData> getFoundPlugins() {
    return foundPlugins;
  }

  @Override
  public Map<IPluginData, String> getPaths() {
    return paths;
  }

  @Override
  public List<URL> getJARLocations() {
    return jars;
  }

}
