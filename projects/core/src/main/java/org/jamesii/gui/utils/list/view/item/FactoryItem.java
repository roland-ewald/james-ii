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
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.core.util.misc.Strings;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class FactoryItem extends BasicItem {

  /**
   * The list of properties for this class.
   */
  private static Properties properties = new Properties();

  static {
    properties.registerProperty(new IProperty<FactoryItem>() {

      @Override
      public String getName() {
        return "Name";
      }

      @Override
      public Object getValue(FactoryItem item) {
        return item.data.getName();
      }
    });

    properties.registerProperty(new IProperty<FactoryItem>() {

      @Override
      public String getName() {
        return "Classname";
      }

      @Override
      public Object getValue(FactoryItem item) {
        return item.data.getClassname();
      }
    });

    properties.registerProperty(new IProperty<FactoryItem>() {

      @Override
      public String getName() {
        return "Type";
      }

      @Override
      public Object getValue(FactoryItem item) {
        return item.getType();
      }
    });

    properties.registerProperty(new IProperty<FactoryItem>() {

      @Override
      public String getName() {
        return "Description";
      }

      @Override
      public Object getValue(FactoryItem item) {
        return item.data.getDescription();
      }
    });

    properties.registerProperty(new IProperty<FactoryItem>() {

      @Override
      public String getName() {
        return "Location";
      }

      @Override
      public Object getValue(FactoryItem item) {
        return item.data.getPluginDefLocation();
      }
    });

    properties.registerProperty(new IProperty<FactoryItem>() {

      @Override
      public String getName() {
        return "Parameters";
      }

      @Override
      public Object getValue(FactoryItem item) {
        List<String> help = new ArrayList<>();
        for (IParameter inf : item.data.getParameters()) {
          help.add(inf.getName());
        }
        return Strings.dispIterable(help);
      }
    });

  }

  /**
   * The plug-in data represented by this viewable item.
   */
  private IFactoryInfo data;

  public FactoryItem(IFactoryInfo d) {
    data = d;
  }

  @Override
  public Object getObject() {
    return data;
  }

  @Override
  public String getLabel() {
    if ((data.getName() != null) && (!data.getName().isEmpty())) {
      return data.getName();
    }
    // fallback: return the classname
    return data.getClassname();
  }

  @Override
  public Icon getIcon(final Dimension dim) {

    // backup: if no icon is given try to use the icon of the plug-in
    if (data.getIconURI() == null) {

      IPluginData pdata = SimSystem.getRegistry().getPlugin(data);

      if (pdata != null) {

        if (pdata.getId().getIconURI() != null) {
          return getIcon(dim, pdata.getId().getIconURI());
        }

        // backup: if no icon is given try to use the icon of the plug-in type
        IPluginTypeData ptdata = SimSystem.getRegistry().getPluginType(pdata);
        if (ptdata != null) {
          return getIcon(dim, ptdata.getId().getIconURI());
        }

      }

    }

    return getIcon(dim, data.getIconURI());
  }

  @Override
  public String getType() {
    return "factory";
  }

  @Override
  public String getSize() {
    return "-";
  }

  @Override
  protected Properties getInternalProperties() {
    return properties;
  }

}
