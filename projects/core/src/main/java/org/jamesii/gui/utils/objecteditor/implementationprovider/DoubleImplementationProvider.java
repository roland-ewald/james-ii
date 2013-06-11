/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.implementationprovider;

import java.text.DecimalFormatSymbols;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides implementations for {@link Double} properties and provides the
 * constant values {@link Double#POSITIVE_INFINITY} as well as
 * {@link Double#NEGATIVE_INFINITY}.
 * 
 * @author Stefan Rybacki
 */
public class DoubleImplementationProvider implements
    IImplementationProvider<Double> {

  @Override
  public Map<String, Double> getImplementations(Object parentProperty,
      String propertyName, Class<?> propertyType) {
    if (!Double.class.equals(propertyType)
        && !(Number.class.equals(propertyType))) {
      return null;
    }

    Map<String, Double> result = new TreeMap<>();
    result.put("-" + DecimalFormatSymbols.getInstance().getInfinity(),
        Double.NEGATIVE_INFINITY);
    result.put(DecimalFormatSymbols.getInstance().getInfinity(),
        Double.POSITIVE_INFINITY);
    return result;
  }

}
