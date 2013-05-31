/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jamesii.gui.utils.list.IViewableItem;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class SmallIconView extends JList implements IItemView {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = -7720927753191876061L;

  public SmallIconView(IViewableItem[] data) {
    super(data);
    // list.setPreferredSize(new Dimension(675,100));
    setCellRenderer(new SmallIconCellRenderer());
    setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
    setVisibleRowCount(-1);
  }

  // /**
  // *
  // * @author Jan Himmelspach
  // *
  // */
  // public class SmallIconCellRenderer implements ListCellRenderer {
  //
  // @Override
  // public Component getListCellRendererComponent(JList list, Object value,
  // int index, boolean isSelected, boolean cellHasFocus) {
  //
  // IViewableItem data = (IViewableItem) value;
  //
  // JLabel result = new JLabel(data.getLabel());
  // result.setIconTextGap(5);
  // result.setIcon(data.getIcon(IViewableItem.SMALL));
  //
  // if (isSelected) {
  // result.setOpaque(true);
  // // result.setForeground(Color.WHITE);
  // result.setBackground(new Color(206, 226, 252));
  // }
  //
  // return result;
  // }
  //
  // }

  /**
   * 
   * @author Jan Himmelspach
   * 
   */
  public class SmallIconCellRenderer extends DefaultListCellRenderer {

    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = -1350838607238549668L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {

      IViewableItem data = (IViewableItem) value;

      Component sup =
          super.getListCellRendererComponent(list, data.getLabel(), index,
              isSelected, cellHasFocus);

      JLabel result = (JLabel) sup;
      result.setIconTextGap(5);
      result.setIcon(data.getIcon(IViewableItem.SMALL));

      return result;
    }

  }

}
