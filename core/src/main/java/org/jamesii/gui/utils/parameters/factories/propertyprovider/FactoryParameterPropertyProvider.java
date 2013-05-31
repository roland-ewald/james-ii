/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider;
import org.jamesii.gui.utils.parameters.factories.FactoryParameters;

/**
 * @author Stefan Rybacki
 */
public class FactoryParameterPropertyProvider extends
    AbstractParameterPropertyProvider implements IPropertyProvider {

  @Override
  public boolean supportsClass(Class<?> parentClass) {
    return (FactoryParameters.class.equals(parentClass));
  }

}
