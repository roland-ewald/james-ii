/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.provider;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.utils.objecteditor.property.IProperty;

/**
 * Property provider for {@link List} properties. It provides a property for
 * each item in the list and therefore makes it editable.
 * 
 * @author Stefan Rybacki
 */
public class ListPropertyProvider implements IPropertyProvider {

  @Override
  public List<IProperty> getPropertiesFor(Class<?> parentClass, Object parent) {
    List<IProperty> result = new ArrayList<>();

    if (List.class.isAssignableFrom(parentClass) && parent != null
        && (parent instanceof List<?>)) {
      List<?> list = (List<?>) parent;
      for (int i = 0; i < list.size(); i++) {
        // Unfortunately the generic type of List is not accessible
        // here so there is
        // no way to know which items could be added (we could
        // introduce an annotation that holds that type)

        result.add(new ListItemProperty(String.valueOf(i), i, Object.class));
      }
    }

    return result;
  }

  @Override
  public boolean supportsClass(Class<?> parentClass) {
    return List.class.isAssignableFrom(parentClass);
  }

}
