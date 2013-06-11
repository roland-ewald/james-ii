/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

import org.jamesii.core.util.misc.IDirectory;

/**
 * Converter for Boolean string values.
 * 
 * @author Stefan Rybacki
 */
public class DirectoryConverter implements IStringConverter<IDirectory> {

  @Override
  public IDirectory convert(String encodedObject) {
    try {
      return new Directory(encodedObject);
    } catch (Exception e) {
      return null;
    }

  }

}
