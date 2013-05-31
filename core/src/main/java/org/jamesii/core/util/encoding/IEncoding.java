/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.encoding;

import java.io.File;

/**
 * Simple interface to be implemented by plug-ins providing encoding detection
 * capabilities.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IEncoding {

  /**
   * Retrieve the encoding of the file passed. If the encoding cannot be
   * determined an empty string is returned. The encoding might just have been
   * guessed - depending on the plug-in used.
   * 
   * @param file
   *          to be checked
   * @return a string containing the identifier of the encoding
   */
  String getEncoding(File file);

}
