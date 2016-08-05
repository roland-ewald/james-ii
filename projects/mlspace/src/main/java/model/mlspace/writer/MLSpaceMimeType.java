/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import org.jamesii.core.data.model.read.plugintype.IMIMEType;

public final class MLSpaceMimeType implements IMIMEType {

  private static final MLSpaceMimeType INSTANCE = new MLSpaceMimeType();

  private MLSpaceMimeType() {
  }

  public static MLSpaceMimeType getInstance() {
    return INSTANCE;
  }

}
