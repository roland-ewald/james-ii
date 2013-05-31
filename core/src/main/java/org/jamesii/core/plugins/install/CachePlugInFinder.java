/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.install;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.core.plugins.PlugInInfo;
import org.jamesii.core.util.misc.Files;

/**
 * Read the list of plug-in-types and plug-ins from a cache. This implies that
 * new plug-ins (and types) will not automatically be available but have to be
 * added "manually" to the lists.
 * 
 * @author Jan Himmelspach
 */
public class CachePlugInFinder implements IPlugInFinder {

  /** The info. */
  private PlugInInfo info;

  /** The cache name. */
  private static String cacheName = "plugincache.info";

  /**
   * Instantiates a new cache plug in finder.
   */
  public CachePlugInFinder() {
    super();
    info = new PlugInInfo();
    read();
  }

  /**
   * Read the information from the cache file.
   */
  public void read() {
    try {
      // new JavaInfo().getUserWorkingDir()

      info =
          (PlugInInfo) Files.load(java.net.URLDecoder.decode(
              SimSystem.getConfigDirectory() + File.separator + cacheName,
              SimSystem.getEncoding()));
    } catch (Exception e) {
      throw new RuntimeException("Reading the cache failed", e);
    }
  }

  /**
   * Write the cache.
   */
  public void write() {
    try {
      Files.save(info, SimSystem.getConfigDirectory() + File.separator
          + cacheName);// new JavaInfo().getUserWorkingDir()+cacheName);
    } catch (FileNotFoundException e) {
      SimSystem.report(e);
    } catch (IOException e) {
      SimSystem.report(e);
    }
  }

  /**
   * Copy information from a different plug-in finder into the cache plug-in
   * finder (e.g., for saving)
   * 
   * @param finder
   *          the finder
   */
  public void copyFrom(IPlugInFinder finder) {
    info = new PlugInInfo();
    info.setFoundPlugins(finder.getFoundPlugins());
    info.setFoundPluginTypes(finder.getFoundPluginTypes());
    info.setPaths(finder.getPaths());
  }

  @Override
  public List<IPluginTypeData> getFoundPluginTypes() {
    return info.getFoundPluginTypes();
  }

  @Override
  public List<IPluginData> getFoundPlugins() {
    return info.getFoundPlugins();
  }

  @Override
  public Map<IPluginData, String> getPaths() {
    return info.getPaths();
  }

  @Override
  public List<URL> getJARLocations() {
    return new ArrayList<>();
  }

}
