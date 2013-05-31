/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jamesii.gui.application.resource.iconset.IIconSet;
import org.jamesii.gui.base.AbstractPropertyChangeSupport;

/**
 * Class that models the default preferences for the James GUI and encapsulates
 * it in a model and provides the option as sub models.
 * 
 * @see #getIconSets()
 * @see #getPerspectives()
 * 
 * @author Stefan Rybacki
 */
final class DefaultGUIPreferencesModel extends AbstractPropertyChangeSupport
    implements ListDataListener {
  /**
   * ComboBoxModel that provides available {@link IIconSet}s
   */
  private final IconSetComboBoxModel iconSetModel = new IconSetComboBoxModel();

  /**
   * CheckBoxGroupModel to provide available perspectives as list
   */
  private final PerspectivesCheckBoxGroupModel perspectivesModel =
      new PerspectivesCheckBoxGroupModel();

  /**
   * Creates an instance of {@link DefaultGUIPreferencesModel}
   */
  public DefaultGUIPreferencesModel() {
    super();
    iconSetModel.addListDataListener(this);
  }

  /**
   * @return available {@link IIconSet}s wrapped in a {@link ComboBoxModel}
   */
  public IconSetComboBoxModel getIconSets() {
    return iconSetModel;
  }

  /**
   * Sets the selected perspective according to the given preset.
   * 
   * @param p
   *          the preset
   */
  public void setPreset(PerspectivePreset p) {
    firePropertyChange("preset", null, p);

    // change the selected perspectives list according to preset
    if (p == null) {
      perspectivesModel.setSelectedPerspectives(new ArrayList<IPerspective>());
    } else {
      List<IPerspective> list = new ArrayList<>();
      for (int i = 0; i < perspectivesModel.getSize(); i++) {
        IPerspective per = perspectivesModel.getItemAt(i);
        if (p.isInPreset(per) || per.isMandatory()) {
          list.add(per);
        }
      }
      perspectivesModel.setSelectedPerspectives(list);
    }
  }

  /**
   * Sets the icon set
   * 
   * @param set
   *          the icon set to set
   */
  public void setIconSet(IIconSet set) {
    if (set == null) {
      return;
    }
    IIconSet old = iconSetModel.getSelectedIconSet();
    if (!set.equals(old)) {
      iconSetModel.setSelectedIconSet(set);
      firePropertyChange("iconSet", old, set);
    }
  }

  /**
   * @return the selected icon set
   */
  public IIconSet getIconSet() {
    return iconSetModel.getSelectedIconSet();
  }

  /**
   * @return selected perspectives
   */
  public List<IPerspective> getSelectedPerspectives() {
    return perspectivesModel.getSelectedPerspectives();
  }

  @Override
  public void contentsChanged(ListDataEvent e) {
    if (e.getSource() == iconSetModel) {
      setIconSet(iconSetModel.getSelectedIconSet());
    }
  }

  @Override
  public void intervalAdded(ListDataEvent e) {
    // nothing to do
  }

  @Override
  public void intervalRemoved(ListDataEvent e) {
    // nothing to do
  }

  /**
   * @return available {@link IPerspective}s wrapped in a {@link ComboBoxModel}
   */
  public PerspectivesCheckBoxGroupModel getPerspectives() {
    return perspectivesModel;
  }
}
