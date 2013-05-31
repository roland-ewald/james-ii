/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view.item;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.core.util.misc.Strings;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class PluginTypeItem extends BasicItem {

  /**
   * The list of properties for this class.
   */
  private static Properties properties = new Properties();

  static {
    properties.registerProperty(new IProperty<PluginTypeItem>() {

      @Override
      public String getName() {
        return "Name";
      }

      @Override
      public Object getValue(PluginTypeItem item) {
        return item.data.getId().getName();
      }
    });

    properties.registerProperty(new IProperty<PluginTypeItem>() {

      @Override
      public String getName() {
        return "Version";
      }

      @Override
      public Object getValue(PluginTypeItem item) {
        return item.data.getId().getVersion();
      }
    });

    properties.registerProperty(new IProperty<PluginTypeItem>() {

      @Override
      public String getName() {
        return "Type";
      }

      @Override
      public Object getValue(PluginTypeItem item) {
        return item.getType();
      }
    });

    properties.registerProperty(new IProperty<PluginTypeItem>() {

      @Override
      public String getName() {
        return "Description";
      }

      @Override
      public Object getValue(PluginTypeItem item) {
        return item.data.getDescription();
      }
    });

    properties.registerProperty(new IProperty<PluginTypeItem>() {

      @Override
      public String getName() {
        return "Abstract Factory";
      }

      @Override
      public Object getValue(PluginTypeItem item) {
        return item.data.getAbstractFactory();
      }
    });

    properties.registerProperty(new IProperty<PluginTypeItem>() {

      @Override
      public String getName() {
        return "Base Factory";
      }

      @Override
      public Object getValue(PluginTypeItem item) {
        return item.data.getBaseFactory();
      }
    });

    properties.registerProperty(new IProperty<PluginTypeItem>() {

      @Override
      public String getName() {
        return "Factories";
      }

      @Override
      public Object getValue(PluginTypeItem item) {
        List<String> help = new ArrayList<>();

        try {
          @SuppressWarnings("unchecked")
          List<Factory<?>> factories =
              SimSystem.getRegistry()
                  .getFactories(
                      (Class<AbstractFactory<Factory<?>>>) SimSystem
                          .getRegistry().getAbstractFactoryByName(
                              item.data.getAbstractFactory()),
                      new ParameterBlock());
          for (Factory<?> inf : factories) {
            help.add(inf.getClass().getName());
          }
          return Strings.dispIterable(help);
        } catch (Exception e) {
          return "";
        }
      }
    });
  }

  /**
   * The plug-in data represented by this viewable item.
   */
  private IPluginTypeData data;

  public PluginTypeItem(IPluginTypeData d) {
    data = d;
  }

  @Override
  public Object getObject() {
    return data;
  }

  @Override
  public String getLabel() {
    return data.getId().getName();
  }

  @Override
  public Icon getIcon(final Dimension dim) {
    return getIcon(dim, data.getId().getIconURI());
  }

  @Override
  public String getType() {
    return "plug-in definition";
  }

  @Override
  public String getSize() {
    try {
      @SuppressWarnings("unchecked")
      List<Factory<?>> factories =
          SimSystem.getRegistry().getFactories(
              (Class<AbstractFactory<Factory<?>>>) SimSystem.getRegistry()
                  .getAbstractFactoryByName(data.getAbstractFactory()), null);
      return factories.size() + " factories installed";
    } catch (Exception e) {

    }
    return "no factories";

  }

  @Override
  protected Properties getInternalProperties() {
    return properties;
  }

}
