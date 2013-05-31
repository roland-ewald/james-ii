/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

import java.io.File;

import org.jamesii.core.util.misc.IDirectory;

/**
 * @author Stefan Rybacki
 * 
 */
public class Directory implements IDirectory {
  private File internal;

  public Directory(String dir) {
    setDir(dir);
  }

  public Directory(File dir) {
    setDir(dir);
  }

  @Override
  public File asFile() {
    return internal;
  }

  @Override
  public final void setDir(File dir) {
    internal = dir;
  }

  public final void setDir(String dir) {
    internal = new File(dir);
  }

  @Override
  public String toString() {
    return internal.getPath();
  }
}
