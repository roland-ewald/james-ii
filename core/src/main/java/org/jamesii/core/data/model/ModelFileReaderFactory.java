/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model;

import java.net.URI;

import org.jamesii.core.data.IFileHandling;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;

/**
 * Base factory for any {@link ModelReaderFactory} that works on files. Provides
 * several convenience methods to extract a file path from an {@link URI} etc.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class ModelFileReaderFactory extends ModelReaderFactory
    implements IFileHandling {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3751773646401707234L;

  @Override
  public boolean supportsURI(URI uri) {
    if (uri.getScheme() == null) {
      return false;
    }
    if (uri.getScheme().equals("file-" + getFileEnding())) {
      return true;
    }
    return false;
  }

}
