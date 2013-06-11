/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.provider;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.objecteditor.ObjectEditorComponent;
import org.jamesii.gui.utils.objecteditor.property.IProperty;

/**
 * The default implementation of a property provider, providing read and
 * writable bean properties of the specified object. This class is automatically
 * utilized by {@link ObjectEditorComponent} when there is no custom
 * {@link IPropertyProvider} for a class.
 * 
 * @author Stefan Rybacki
 */
public class DefaultPropertyProvider implements IPropertyProvider {

  @Override
  public List<IProperty> getPropertiesFor(Class<?> parentClass, Object parent) {
    List<IProperty> result = new ArrayList<>();

    try {
      BeanInfo info = Introspector.getBeanInfo(parentClass);

      // filter properties that are read or write only
      PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
      for (PropertyDescriptor pd : propertyDescriptors) {
        if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
          // check property filters also
          result.add(new DefaultProperty(pd.getName(), pd.getPropertyType(), pd
              .getReadMethod(), pd.getWriteMethod(), true));
        }
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }

    return result;
  }

  @Override
  public boolean supportsClass(Class<?> parentClass) {
    return true;
  }

}
