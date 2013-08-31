/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.list;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * A {@link javax.swing.ListCellRenderer} that displays factories and a summary
 * of the parameters set for them in an associated
 * {@link org.jamesii.core.parameters.ParameterBlock}. This renderer assumes
 * that a {@link ConfigurationListModel} is used for the list. As a convenience
 * the {@link ParametrizedList} class can be used which uses both the correct
 * model and this renderer.
 * 
 * @author Johannes Rössel
 */
public class ParameterListCellRenderer extends DefaultListCellRenderer {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7582559251849244148L;

  /**
   * An instance of the {@link DefaultListCellRenderer} class that is used for
   * delegating the initial component creation.
   */
  private DefaultListCellRenderer defaultRendererInstance =
      new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
    // get the original component
    Component comp =
        defaultRendererInstance.getListCellRendererComponent(list, value,
            index, isSelected, cellHasFocus);

    Component result =
        CellRendererHelper.getEvaluationConfigurationEntryRendererComponent(
            comp, (org.jamesii.gui.utils.parameters.list.Entry) value);

    if (result == null) {
      // fallback to prevent infinite loops of exceptions in case that no
      // renderer component has been retrieved
      result = defaultRendererInstance;
    }
    return result;
  }

  @Override
  public void updateUI() {
    defaultRendererInstance = new DefaultListCellRenderer();
  }
}
