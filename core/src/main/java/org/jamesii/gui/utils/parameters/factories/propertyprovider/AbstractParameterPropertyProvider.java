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
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.gui.utils.objecteditor.property.IProperty;
import org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider;
import org.jamesii.gui.utils.parameters.factories.FactoryParameters;

/**
 * @author Stefan Rybacki
 */
public abstract class AbstractParameterPropertyProvider implements
    IPropertyProvider {

  @Override
  public List<IProperty> getPropertiesFor(Class<?> parentClass, Object parent) {
    if (!supportsClass(parentClass)) {
      return null;
    }

    FactoryParameters parameters = (FactoryParameters) parent;
    String f = parameters.getFactory();
    if (f == null) {
      return null;
    }

    // load factory parameters
    IFactoryInfo factoryInfo = SimSystem.getRegistry().getFactoryInfo(f);
    List<IParameter> fParams = new ArrayList<>(factoryInfo.getParameters());

    IPluginTypeData pluginType =
        SimSystem.getRegistry().getPluginType(
            SimSystem.getRegistry().getPlugin(factoryInfo));

    fParams.addAll(pluginType.getParameters());

    if (fParams == null || fParams.size() == 0) {
      return null;
    }

    // for each factory parameter create a property
    List<IProperty> properties = new ArrayList<>(fParams.size());
    for (IParameter p : fParams) {
      try {
        // check whether parameter is a plugin type
        if (p.getPluginType() != null && p.getPluginType().length() > 0) {
          // check what type of plugintype (string, list, map, etc.) is
          // requested
          IProperty property = null;
          if (List.class.getName().equals(p.getType())) {
            property =
                new ListPluginTypeProperty(p.getName(), SimSystem.getRegistry()
                    .getClassLoader().loadClass(p.getPluginType()));
          } else {
            property =
                new PluginTypeProperty(p.getName(), SimSystem.getRegistry()
                    .getClassLoader().loadClass(p.getPluginType()));
          }

          if (property != null) {
            properties.add(property);
          }
        } else {
          IProperty property =
              new FactoryParameterProperty(p.getName(), SimSystem.getRegistry()
                  .getClassLoader().loadClass(p.getType()));

          properties.add(property);
        }
      } catch (ClassNotFoundException e) {
        SimSystem.report(e);
      }
    }

    return properties;
  }

}
