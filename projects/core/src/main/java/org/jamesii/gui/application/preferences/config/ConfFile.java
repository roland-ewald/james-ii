/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences.config;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Files;

/**
 * 
 * Encapsulates a Hashmap that can be written as an XML-file to the disk. The
 * Hashtable contains properties of different data types, a key should be a
 * String that identifies a property.
 * 
 * Created: 13.04.2004
 * 
 * @author Roland Ewald
 */
public abstract class ConfFile {

  /**
   * Serialisation ID
   */
  static final long serialVersionUID = -7411525476011960056L;

  /**
   * Filename
   */
  private transient String fileName = null;

  /**
   * Map with all values
   */
  private Map<String, Serializable> values = new HashMap<>();

  /**
   * Default constructor
   */
  public ConfFile() {
    super();
    setDefaults();
  }

  /**
   * Adds all values from the given conf file to this conf file
   * 
   * @param file
   */
  public void add(ConfFile file) {
    values.putAll(file.getValues());
  }

  /**
   * Deletes the (formerly loaded) file
   * 
   * @return true, if operation was successful
   * @throws Exception
   */
  public boolean deleteFile() {

    if (this.fileName == null) {
      return false;
    }

    File f = new File(fileName);
    return f.delete();
  }

  /**
   * Deletes the (formerly loaded) file.
   * 
   * @param component
   *          the component
   * 
   * @return true, if operation was successful
   */
  public boolean deleteFile(Component component) {
    boolean success;
    try {
      success = deleteFile();
    } catch (Exception e) {
      SimSystem.report(Level.SEVERE, null,
          "Error while attempting to delete profile file '" + getFileName()
              + "'", null, e);
      return false;
    }
    return success;
  }

  /**
   * Gets the object stored at key, key.
   * 
   * @param key
   *          the key
   * 
   * @return the serializable object stored with the key passed as parameter
   */
  public Serializable get(String key) {
    return values.get(key);
  }

  /**
   * @return file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the property.
   * 
   * @param <V>
   * 
   * @param key
   *          the key
   * @param defaultValue
   *          the default value
   * 
   * @return the property
   */
  @SuppressWarnings("unchecked")
  public <V extends Serializable> V getProperty(String key, V defaultValue) {
    V retVal = (V) get(key);
    if (retVal == null) {
      return defaultValue;
    }
    return retVal;
  }

  /**
   * Gets the values.
   * 
   * @return the values
   */
  public Map<String, Serializable> getValues() {
    return values;
  }

  /**
   * Put.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public void put(String key, Serializable value) {
    values.put(key, value);
  }

  /**
   * Reads a xml-file containing configuration values. If the file is not found,
   * a new file with default values is created.
   * 
   * @param fName
   *          Path and Filename of the File to be read
   * @throws FileNotFoundException
   * 
   * @throws Exception
   *           the exception
   * 
   * @see ConfFile#setDefaults
   */
  public void readFile(String fName) throws FileNotFoundException {
    add((ConfFile) Files.load(fName));
    setFileName(fName);
  }

  /**
   * Read file.
   * 
   * @param fName
   *          the f name
   * @param component
   *          the component
   * 
   * @return true, if read file
   */
  public boolean readFile(String fName, Component component) {
    try {
      readFile(fName);
    } catch (Exception e) {
      SimSystem.report(Level.SEVERE, null, "Error while reading profile file '"
          + getFileName() + "'", null, e);
      return false;
    }
    return true;
  }

  /**
   * Function for ensuring the availability of certain default values.
   */
  public abstract void setDefaults();

  /**
   * @param fileName
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @param values
   */
  public void setValues(Map<String, Serializable> values) {
    this.values = values;
  }

  /**
   * Writes a XML-file containing the configuration that already existed to the
   * disk Uses the former filename .
   * 
   * @return boolean success - true, if operation was successfull
   * @throws java.io.IOException
   * @throws FileNotFoundException
   * 
   * @throws Exception
   *           the exception
   */
  public boolean writeFile() throws FileNotFoundException {
    if (fileName != null) {
      writeFile(fileName);
      return true;
    }
    return false;
  }

  /**
   * Write file.
   * 
   * @param component
   *          the component
   * 
   * @return true, if write file
   */
  public boolean writeFile(Component component) {
    return writeFile(getFileName(), component);
  }

  /**
   * 
   * @param fName
   * @throws java.io.IOException
   * @throws FileNotFoundException
   * @throws Exception
   */
  public void writeFile(String fName) throws FileNotFoundException {
    fileName = fName;
    Files.save(this, fName, true);
  }

  /**
   * Write file.
   * 
   * @param fName
   *          the f name
   * @param component
   *          the component
   * 
   * @return true, if write file
   */
  public boolean writeFile(String fName, Component component) {
    try {
      writeFile(fName);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, null, "Error: Could not save to '"
          + getFileName() + "'", null, ex);
      return false;
    }
    return true;
  }

}
