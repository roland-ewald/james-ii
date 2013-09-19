/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jamesii.gui.perspective.laf.LafManager;
import org.jamesii.gui.utils.AbstractComboBoxModel;

/**
 * ComboBox Model that provides all available look and feels as list/combo box
 * model.
 * 
 * @see LookAndFeelListCellRenderer
 * @author Stefan Rybacki
 */
public final class LookAndFeelComboBoxModel extends
    AbstractComboBoxModel<LookAndFeelInfo> {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 334956109336892289L;

  /**
   * Creates a new LookAndFeelComobBoxModel containing all through the
   * {@link javax.swing.UIManager} available look and feels.
   */
  public LookAndFeelComboBoxModel() {
    super();

    // get all look and feels and propagate them through items
    List<LookAndFeelInfo> lookAndFeels = LafManager.getLookAndFeels();
    for (LookAndFeelInfo i : lookAndFeels) {
      addElement(i);
    }
  }

  /**
   * Convenience method to retrieve the selected look and feel info
   * 
   * @return the selected look and feel info
   */
  public LookAndFeelInfo getSelectedLookAndFeelInfo() {
    return (LookAndFeelInfo) getSelectedItem();
  }

  /**
   * Convenience method to select an item based on the given look and feel
   * 
   * @param l
   *          the look and feel to select
   */
  public void setSelectedLookAndFeel(LookAndFeel l) {
    setSelectedLookAndFeelClass(l.getClass().getName());
  }

  /**
   * Convenience method to select an item based on the given look and feel info
   * 
   * @param l
   *          the look and feel info to select
   */
  public void setSelectedLookAndFeelInfo(LookAndFeelInfo l) {
    setSelectedItem(l);
  }

  /**
   * Convenience method to select an item based on its class name
   * 
   * @param className
   *          the look and feel class name to select
   */
  public void setSelectedLookAndFeelClass(String className) {
    for (int i = 0; i < getSize(); i++) {
      LookAndFeelInfo info = (LookAndFeelInfo) getElementAt(i);
      if (info.getClassName().equals(className)) {
        setSelectedItem(info);
        return;
      }
    }
  }
}
