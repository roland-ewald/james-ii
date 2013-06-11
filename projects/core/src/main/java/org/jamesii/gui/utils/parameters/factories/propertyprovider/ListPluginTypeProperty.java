/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.factories.ListParameters;
import org.jamesii.gui.utils.parameters.factories.PluginTypeParameters;

/**
 * @author Stefan Rybacki
 * 
 */
public class ListPluginTypeProperty extends AbstractPluginTypeProperty {

  public ListPluginTypeProperty(String name, Class<?> plugintype) {
    super(name, plugintype, ListParameters.class);
  }

  @Override
  protected PluginTypeParameters getPluginTypeParameters(ParameterBlock block) {
    return new ListParameters(getBaseFactory(), block);
  }

  @Override
  public Object getValue(Object parent) {
    return super.getValue(parent);
  }

  @Override
  public void setValue(Object parent, Object value) {
    super.setValue(parent, value);
  }

}
