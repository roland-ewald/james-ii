/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import org.jamesii.gui.utils.list.IViewableItem;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class ListView extends SmallIconView {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = -8013126307401784517L;

  public ListView(IViewableItem[] data) {
    super(data);
    setLayoutOrientation(javax.swing.JList.VERTICAL);
  }

}
