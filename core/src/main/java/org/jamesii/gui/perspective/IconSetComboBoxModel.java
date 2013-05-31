/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.util.List;

import org.jamesii.gui.application.resource.iconset.IIconSet;
import org.jamesii.gui.application.resource.iconset.IconSetManager;
import org.jamesii.gui.utils.AbstractComboBoxModel;

/**
 * Simple list model providing access to all registered {@link IIconSet}s.
 * 
 * @author Stefan Rybacki
 */
class IconSetComboBoxModel extends AbstractComboBoxModel<IIconSet> {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 3259553986892079233L;

  /**
   * Creates a new icon set combo box model with prepopulated icon sets
   */
  public IconSetComboBoxModel() {
    super();

    List<IIconSet> sets = IconSetManager.getAvailableIconSets();

    for (IIconSet set : sets) {
      addElement(set);
    }
  }

  /**
   * Convenience method to select an icon set
   * 
   * @param set
   *          the icon set to select
   */
  public void setSelectedIconSet(IIconSet set) {
    if (set == null) {
      return;
    }

    for (int i = 0; i < getSize(); i++) {
      if (getElementAt(i).getClass().equals(set.getClass())) {
        setSelectedItem(getElementAt(i));
        return;
      }
    }
  }

  /**
   * @return the currently selected icon set
   */
  public IIconSet getSelectedIconSet() {
    return (IIconSet) getSelectedItem();
  }
}
