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
public class TilesView extends SmallIconView {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = -7207390373114373201L;

  public TilesView(IViewableItem[] data) {
    super(data);
    setCellRenderer(new TilesCellRenderer());
  }

  /**
   * 
   * @author Jan Himmelspach
   * 
   */
  public class TilesCellRenderer extends DefaultListCellRenderer {

    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 8070793638439535490L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {

      IViewableItem data = (IViewableItem) value;

      Component sup =
          super.getListCellRendererComponent(list, "<html>" + data.getLabel()
              + "<br><font color=\"#C0C0C0\">" + data.getType() + " <br>"
              + data.getSize() + "</font></html>", index, isSelected,
              cellHasFocus);

      JLabel result = (JLabel) sup;
      result.setIconTextGap(5);
      result.setIcon(data.getIcon(IViewableItem.TILES));

      return result;
    }
  }

}
