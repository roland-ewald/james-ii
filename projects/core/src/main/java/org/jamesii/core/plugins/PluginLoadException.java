/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

/**
 * The Class PluginLoadException.
 */
public class PluginLoadException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 295254424728420515L;

  /**
   * Instantiates a new plugin load exception.
   * 
   * @param msg
   *          the msg
   */
  public PluginLoadException(String msg) {
    super(msg);
  }

  public PluginLoadException(Throwable cause) {
    super(cause);
  }

  public PluginLoadException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
