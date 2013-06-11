/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jamesii.gui.application.resource.iconset.IIconSet;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * List cell renderer that shows icon set names and associated icons.
 * 
 * @author Stefan Rybacki
 */
final class IconSetListCellRenderer extends DefaultListCellRenderer {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 5113394419025645490L;

  /**
   * singleton instance
   */
  private static final IconSetListCellRenderer INSTANCE =
      new IconSetListCellRenderer();

  /**
   * omitted constructor
   */
  private IconSetListCellRenderer() {
    // nothing to do here
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {

    Icon icon = null;

    if (value instanceof IIconSet) {
      icon = ((IIconSet) value).getIcon(IconIdentifier.ICONSET_ICON_SMALL);

      value = ((IIconSet) value).getName();
    }

    Component c =
        super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);

    if (c instanceof JLabel) {
      ((JLabel) c).setIcon(icon);
    }

    return c;
  }

  /**
   * @return singleton instance of this renderer
   */
  public static IconSetListCellRenderer getInstance() {
    return INSTANCE;
  }

}
