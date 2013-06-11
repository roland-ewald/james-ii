/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences;

import java.io.FileNotFoundException;
import java.io.Serializable;

import org.jamesii.gui.application.preferences.config.ConfFile;
import org.jamesii.gui.application.preferences.config.MainConfFile;

/**
 * Singleton preferences class to be used for storing all kinds of preferences.
 * So this class is intended to be used by any other class that needs to persist
 * preferences over JAMES II sessions boundaries.
 * <p>
 * The main methods to use are {@link Preferences#put(String, Serializable)} and
 * {@link Preferences#get(String)} where the first one is to persist a
 * serializable value with an id and the latter to restore it.
 * 
 * @author Stefan Rybacki
 */
public final class Preferences {

  /**
   * The Constant instance.
   */
  private static final Preferences INSTANCE = new Preferences();

  /**
   * The config file the preferences are persisted in.
   */
  private final ConfFile config = new MainConfFile();

  /**
   * Omitted constructor
   */
  private Preferences() {
    // nothing to do here
  }

  /**
   * Loads the preferences from the specified file.
   * 
   * @param file
   *          the file to load from
   * @throws FileNotFoundException
   * @throws Exception
   */
  public static final void loadFrom(String file) throws FileNotFoundException {
    INSTANCE.config.readFile(file);
  }

  /**
   * Saves the preferences to the specified file.
   * 
   * @param file
   *          the file to save to
   * @throws FileNotFoundException
   * @throws Exception
   */
  public static final void saveTo(String file) throws FileNotFoundException {
    INSTANCE.config.writeFile(file);
  }

  /**
   * Loads additional preferences from the specified filse. In contrast to
   * {@link #loadFrom(String)} does this method not clear the existing
   * preferences before loading.
   * 
   * @param file
   *          the file to load from
   * @throws FileNotFoundException
   * @throws Exception
   */
  public static final void addFromFile(String file)
      throws FileNotFoundException {
    ConfFile m = new MainConfFile();
    m.readFile(file);
    INSTANCE.config.add(m);
  }

  /**
   * Use this method to persist a serializable value identified by a unique id.
   * 
   * @param key
   *          the unique id
   * @param value
   *          the value to store
   */
  public static final void put(String key, Serializable value) {
    INSTANCE.config.put(key, value);
  }

  /**
   * Returns a previously stored value identified by the given id.
   * 
   * @param <E>
   *          a auto cast generic parameter
   * @param key
   *          the id to identify the value
   * @return a previously stored value identified by given id or {@code null} if
   *         not existing
   */
  @SuppressWarnings("unchecked")
  public static final <E> E get(String key) {
    return (E) INSTANCE.config.get(key);
  }
}
