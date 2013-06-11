/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.implementationprovider;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.gui.utils.objecteditor.implementationprovider.IImplementationProvider;

/**
 * An {@link IImplementationProvider} implementation providing registered
 * plugins for a given plugin type. This provider provides implementations for a
 * specific base factory.
 * 
 * @author Stefan Rybacki
 */
public class PluginTypeImplementationProvider implements
    IImplementationProvider<Factory> {

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Factory> getImplementations(Object parentProperty,
      String propertyName, Class<?> propertyType) {

    // find all possible factories that are assignable to propertyType
    if (!Factory.class.isAssignableFrom(propertyType)) {
      return null;
    }

    List<Factory<?>> factories =
        SimSystem.getRegistry().getFactories(
            (Class<? extends Factory<?>>) propertyType);

    if (factories == null) {
      return null;
    }

    // use sorted map
    Map<String, Factory> result = new TreeMap<>();

    for (Factory f : factories) {
      // TODO sr137: use description as tooltip
      result.put(f.getReadableName(), f);
    }

    return result;
  }

}
