/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jamesii.gui.utils.list.IViewableItem;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class LargeIconView extends JList implements IItemView {

  private static final long serialVersionUID = -7720927753191876061L;

  public LargeIconView(IViewableItem[] data) {
    super(data);
    setCellRenderer(new LargeIconCellRenderer());
    setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
    setVisibleRowCount(0);
  }

  /**
   * 
   * @author Jan Himmelspach
   * 
   */
  public class LargeIconCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -5278112036532848855L;

    //
    // JPanel panelResult = new JPanel();
    //
    // public LargeIconCellRenderer () {
    // super();
    // panelResult.setLayout(new BorderLayout());
    // }
    //
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      //
      // IViewableItem data = (IViewableItem) value;
      //
      // Component sup =
      //
      IViewableItem data = (IViewableItem) value;

      JPanel panelResult = new JPanel();

      panelResult.setOpaque(false);// .setBackground(Color.WHITE);

      JLabel result =
          (JLabel) super.getListCellRendererComponent(list, data.getLabel(),
              index, isSelected, cellHasFocus);
      result.setIconTextGap(5);
      result.setIcon(data.getIcon(IViewableItem.LARGE));
      result.setVerticalTextPosition(SwingConstants.BOTTOM);
      result.setHorizontalTextPosition(SwingConstants.CENTER);

      panelResult.add(result, BorderLayout.CENTER);

      return panelResult;
    }
    //
    // }

  }
}
