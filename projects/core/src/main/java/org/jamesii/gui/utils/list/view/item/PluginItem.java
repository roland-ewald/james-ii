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
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.core.util.misc.Strings;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class PluginItem extends BasicItem {

  /**
   * The list of properties for this class.
   */
  private static Properties properties = new Properties();

  static {
    properties.registerProperty(new IProperty<PluginItem>() {

      @Override
      public String getName() {
        return "Name";
      }

      @Override
      public Object getValue(PluginItem item) {
        return item.data.getId().getName();
      }
    });

    properties.registerProperty(new IProperty<PluginItem>() {

      @Override
      public String getName() {
        return "Version";
      }

      @Override
      public Object getValue(PluginItem item) {
        return item.data.getId().getVersion();
      }
    });

    properties.registerProperty(new IProperty<PluginItem>() {

      @Override
      public String getName() {
        return "Type";
      }

      @Override
      public Object getValue(PluginItem item) {
        return item.getType();
      }
    });

    properties.registerProperty(new IProperty<PluginItem>() {

      @Override
      public String getName() {
        return "License";
      }

      @Override
      public Object getValue(PluginItem item) {
        return item.data.getLicenseURI();
      }
    });

    properties.registerProperty(new IProperty<PluginItem>() {

      @Override
      public String getName() {
        return "Location";
      }

      @Override
      public Object getValue(PluginItem item) {
        return item.data.getPluginLocation();
      }
    });

    properties.registerProperty(new IProperty<PluginItem>() {

      @Override
      public String getName() {
        return "Factories";
      }

      @Override
      public Object getValue(PluginItem item) {
        List<String> help = new ArrayList<>();
        for (IFactoryInfo inf : item.data.getFactories()) {
          help.add(inf.getClassname());
        }
        return Strings.dispIterable(help);
      }
    });

    properties.registerProperty(new IProperty<PluginItem>() {

      @Override
      public String getName() {
        return "Size";
      }

      @Override
      public Object getValue(PluginItem item) {
        return item.data.getFactories().size();
      }
    });
  }

  /**
   * The plug-in data represented by this viewable item.
   */
  private IPluginData data;

  public PluginItem(IPluginData d) {
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

    // backup: if no icon is given try to use the icon of the plug-in type
    if (data.getId().getIconURI() == null) {
      IPluginTypeData ptdata = SimSystem.getRegistry().getPluginType(data);
      if (ptdata != null) {
        return getIcon(dim, ptdata.getId().getIconURI());
      }

    }

    return getIcon(dim, data.getId().getIconURI());
  }

  @Override
  public String getType() {
    return "plug-in definition";
  }

  @Override
  public String getSize() {
    if (data.getFactories().size() == 1) {
      return "1 factory";
    }
    return data.getFactories().size() + " factories";
  }

  @Override
  protected Properties getInternalProperties() {
    return properties;
  }

}
