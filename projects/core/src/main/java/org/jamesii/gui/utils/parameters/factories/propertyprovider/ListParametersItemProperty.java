/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import org.jamesii.gui.utils.objecteditor.property.AbstractProperty;
import org.jamesii.gui.utils.parameters.factories.ListParameters;
import org.jamesii.gui.utils.parameters.factories.PluginTypeParameters;
import org.jamesii.gui.utils.parameters.list.Entry;

/**
 * Example implementation of a property that represents a list item of
 * {@link ListParameters} rather than an actual property.
 * 
 * @author Stefan Rybacki
 * @see ListParametersPropertyProvider
 */
public class ListParametersItemProperty extends AbstractProperty {

  /**
   * The index of the item in the list.
   */
  private int index;

  /**
   * Instantiates a new copy
   * 
   * @param name
   *          the name
   * @param index
   *          the item index
   * @param type
   *          the type
   */
  public ListParametersItemProperty(String name, int index, Class<?> type) {
    super(name, type);
    this.index = index;
  }

  @Override
  public Object getValue(Object parent) {
    if (parent instanceof ListParameters) {
      ListParameters params = (ListParameters) parent;
      Entry entry = params.getList().get(index);
      PluginTypeParameters ptp =
          new PluginTypeParameters(params.getBaseFactory(),
              entry.getFactoryName(), entry.getParameters());
      // set instance of ptp as entry's parameterblock since the incoming
      // parameterblock is copied inside
      entry.setParameters(ptp.getAsParameterBlock());
      return ptp;
    }

    return null;
  }

  @Override
  public void setValue(Object parent, Object value) {
  }

  @Override
  public boolean isWritable() {
    return false;
  }

}
