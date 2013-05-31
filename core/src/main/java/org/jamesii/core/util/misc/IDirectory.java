/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.File;

/**
 * @author Stefan Rybacki
 * 
 */
public interface IDirectory {

  /**
   * Returns the directory as {@link File} object.
   * 
   * @return the file object
   */
  File asFile();

  /**
   * Sets the dir.
   * 
   * @param dir
   *          the new dir
   */
  void setDir(File dir);

}
