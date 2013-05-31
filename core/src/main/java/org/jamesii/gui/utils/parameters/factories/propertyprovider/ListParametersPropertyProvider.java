/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.objecteditor.property.IProperty;
import org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider;
import org.jamesii.gui.utils.parameters.factories.ListParameters;
import org.jamesii.gui.utils.parameters.list.Entry;

/**
 * @author Stefan Rybacki
 * 
 */
public class ListParametersPropertyProvider implements IPropertyProvider {

  @Override
  public List<IProperty> getPropertiesFor(Class<?> parentClass, Object parent) {
    List<IProperty> result = new ArrayList<>();

    try {
      if (ListParameters.class.isAssignableFrom(parentClass) && parent != null
          && parent instanceof ListParameters) {
        ListParameters parameters = (ListParameters) parent;
        List<Entry> list = parameters.getList();

        for (int i = 0; i < list.size(); i++) {
          // Entry f = list.get(i);
          result.add(new ListParametersItemProperty(String.valueOf(i), i,
              SimSystem.getRegistry().getClassLoader()
                  .loadClass(parameters.getBaseFactory())));
        }
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }

    return result;
  }

  @Override
  public boolean supportsClass(Class<?> parentClass) {
    return ListParameters.class.equals(parentClass);
  }

}
