/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.provider;

import java.util.Collections;
import java.util.List;

import org.jamesii.gui.utils.objecteditor.property.IProperty;

/**
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Stefan Rybacki
 * 
 */
public class NoPropertyProvider implements IPropertyProvider {

  @Override
  public List<IProperty> getPropertiesFor(Class<?> parentClass, Object parent) {
    return Collections.emptyList();
  }

  @Override
  public boolean supportsClass(Class<?> parentClass) {
    return true;
  }

}
