/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.parameter.read.plugintype;

import java.net.URI;

import org.jamesii.core.data.IFileHandling;

/**
 * Super class of all model parameter reader factories that create file readers.
 * 
 * @author Stefan Rybacki
 */
public abstract class ModelParameterFileReaderFactory extends
    ModelParameterReaderFactory implements IFileHandling {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -9070589108539376252L;

  @Override
  public boolean supportsURI(URI uri) {
    if (uri.getScheme().equals("file-" + getFileEnding())) {
      return true;
    }
    return false;
  }

}
