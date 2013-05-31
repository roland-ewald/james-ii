/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.implementationprovider;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A default implementation of {@link IImplementationProvider} which can be used
 * to simply provide a list of implementations for a specific property
 * identified by its name.
 * 
 * @author Stefan Rybacki
 */
public class DefaultImplementationProvider implements
    IImplementationProvider<Object> {

  /**
   * The implementations.
   */
  private Map<String, ?> implementations;

  /**
   * The property name.
   */
  private String propertyName;

  /**
   * Instantiates a new default implementation provider.
   * 
   * @param implementations
   *          the implementations that should be provided for the specified
   *          property
   * @param propertyName
   *          the property name the implementations are provided for if they are
   *          assignable
   */
  public DefaultImplementationProvider(Map<String, ?> implementations,
      String propertyName) {
    this.implementations = implementations;
    this.propertyName = propertyName;
  }

  @Override
  public Map<String, Object> getImplementations(Object parentProperty,
      String pN, Class<?> propertyType) {
    Map<String, Object> o = new HashMap<>();

    // get all object from provides list that are assignable to
    // property
    for (Entry<String, ?> i : implementations.entrySet()) {
      if (propertyType.isAssignableFrom(i.getValue().getClass())
          && (this.propertyName == null || this.propertyName.equals(pN))) {
        o.put(i.getKey(), i.getValue());
      }
    }

    return o;
  }

}
