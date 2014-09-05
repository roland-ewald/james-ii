/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import javax.swing.JComponent;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.list.IViewableItem;
import org.jamesii.gui.utils.list.view.plugintype.ViewFactory;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class DetailedItemViewFactory extends ViewFactory {

  private static final long serialVersionUID = -1099055974662184237L;

  @Override
  public JComponent create(ParameterBlock parameter, Context context) {

    IViewableItem[] itemData = parameter.getSubBlockValue(DATA);
    String[] propertyData = parameter.getSubBlockValue(PROPERTIES);

    return new DetailsView(itemData, propertyData);
  }

}
