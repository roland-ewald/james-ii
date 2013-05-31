/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import org.jamesii.gui.utils.parameters.factories.PluginTypeParameters;

/**
 * @author Stefan Rybacki
 */
public class PluginTypeParameterPropertyProvider extends
    AbstractParameterPropertyProvider {

  @Override
  public boolean supportsClass(Class<?> parentClass) {
    return (PluginTypeParameters.class.equals(parentClass));
  }

}
