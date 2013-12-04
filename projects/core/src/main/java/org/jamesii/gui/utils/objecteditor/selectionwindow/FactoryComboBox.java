/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.selectionwindow;

import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.gui.utils.AbstractComboBoxModel;
import org.jamesii.gui.utils.FactoryListCellRenderer;

/**
 * 
 * 
 * @author Johannes Rössel
 * 
 * @param <T>
 */
public class FactoryComboBox<T extends Factory<?>> extends JComboBox {

  /**
   * A model for the {@link FactoryComboBox} that saves pairs of {@link Factory}
   * instances and associated {@link ParameterBlock}s. The model is initialised
   * with a list of factories and generally just stores the
   * {@link ParameterBlock}s that get associated with each factory.
   * 
   * @author Johannes Rössel
   * 
   * @param <F>
   *          The subclass of {@link Factory} to store in this model.
   */
  private class FactoryComboBoxModel<F extends Factory<?>> extends
      AbstractComboBoxModel<ParameterizedFactory<F>> {
    /** Serialisation ID. */
    private static final long serialVersionUID = -6697817703706197117L;

    /**
     * Initialises a new instance of the {@link FactoryComboBoxModel} class
     * using the given list of factories.
     * 
     * @param factories
     *          A list of factories to store in this model.
     */
    public FactoryComboBoxModel(List<F> factories) {
      for (F item : factories) {
        this.addElement(new ParameterizedFactory<>(item, new ParameterBlock()));
      }
    }

    /**
     * Retrieves the index of a specific factory within the model, using the
     * given factory class name to look it up.
     * 
     * @param factoryName
     *          The name of the factory class to find.
     * @return The index of the found factory if it exists within this model or
     *         {@code -1} if it can't be found.
     */
    @SuppressWarnings("unchecked")
    public int findFactory(String factoryName) {
      for (int i = 0; i < getSize(); i++) {
        Object item = getElementAt(i);
        ParameterizedFactory<F> pair = (ParameterizedFactory<F>) item;
        if (factoryName.equals(pair.getFactory().getClass().getName())) {
          return i;
        }
      }

      return -1;
    }
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 8616918570154119142L;

  /**
   * Initialises a new instance of the {@link FactoryComboBox} class using the
   * given list of factories.
   * 
   * @param factories
   *          A list of factories to populate the ComboBox.
   */
  public FactoryComboBox(List<T> factories) {
    this.setModel(new FactoryComboBoxModel<>(factories));

    // set a renderer that displays just the factory name. Storage of the
    // parameters should be transparent after all.
    this.setRenderer(new FactoryListCellRenderer() {
      /** Serialisation ID. */
      private static final long serialVersionUID = -2268078941920094150L;

      @Override
      public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list,
            value != null ? ((ParameterizedFactory<?>) value).getFactory()
                : null, index, isSelected, cellHasFocus);
      }
    });
  }

  /**
   * Retrieves the factory name at the specified index.
   * 
   * @param index
   *          The index in the model.
   * @return The name of the factory at the specified index or {@code null} if
   *         the index was out of range.
   */
  @SuppressWarnings("unchecked")
  public String getFactory(int index) {
    Object item = getItemAt(index);

    if (item == null) {
      return null;
    }

    ParameterizedFactory<T> pair = (ParameterizedFactory<T>) item;
    Factory<?> factory = pair.getFactory();

    return factory.getClass().getName();
  }

  /**
   * Retrieves the {@link ParameterBlock} associated with a factory at a
   * specified index.
   * 
   * @param index
   *          The index in the model.
   * @return The {@link ParameterBlock} at the specified index or {@code null}
   *         if the index was out of range.
   */
  @SuppressWarnings("unchecked")
  public ParameterBlock getParameters(int index) {
    Object item = getItemAt(index);

    if (item == null) {
      return null;
    }

    ParameterizedFactory<T> pair = (ParameterizedFactory<T>) item;

    return pair.getParameters();
  }

  /**
   * Retrieves the currently selected factory name.
   * 
   * @return The class name of the currently selected factory or {@code null} if
   *         there is no selection.
   */
  public String getSelectedFactory() {
    return getFactory(getSelectedIndex());
  }

  /**
   * Sets the currently selected factory. If the factory can't be found the
   * selection doesn't change.
   * 
   * @param factoryName
   *          The class name of the factory to select.
   */
  public void setSelectedFactory(String factoryName) {
    int index = ((FactoryComboBoxModel<T>) getModel()).findFactory(factoryName);

    if (index == -1) {
      return;
    }

    setSelectedIndex(index);
  }

  /**
   * Retrieves the {@link ParameterBlock} for the currently selected factory.
   * 
   * @return The {@link ParameterBlock} of the currently selected factory or
   *         {@code null} if there is no selection.
   */
  public ParameterBlock getSelectedParameters() {
    return getParameters(getSelectedIndex());
  }

  /**
   * Sets the {@link ParameterBlock} for a given item. The item in this case is
   * the {@link ParameterizedFactory} that is stored in the model.
   * 
   * @param item
   *          The list item to set parameters for.
   * @param parameters
   *          The parameters to set.
   */
  @SuppressWarnings("unchecked")
  public void setParameters(Object item, ParameterBlock parameters) {
    if (item == null) {
      return;
    }

    ParameterizedFactory<T> pair = (ParameterizedFactory<T>) item;

    pair.setParameter(parameters);
  }

  /**
   * Sets the {@link ParameterBlock} for a given factory. If the factory name
   * can't be found in the model no changes are made.
   * 
   * @param factoryName
   *          The class name of the factory.
   * @param parameters
   *          The parameters to set.
   */
  @SuppressWarnings("unchecked")
  public void setParameters(String factoryName, ParameterBlock parameters) {
    FactoryComboBoxModel<T> model = (FactoryComboBoxModel<T>) getModel();

    int index = model.findFactory(factoryName);

    if (index == -1) {
      return;
    }

    Object item = model.getElementAt(index);
    ParameterizedFactory<T> pair = (ParameterizedFactory<T>) item;
    pair.setParameter(parameters);
  }
}
