/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.list;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * A {@link ListModel} that holds pairs of factory names and parameter blocks.
 * 
 * 
 * @author Johannes Rössel
 */
public class ConfigurationListModel extends AbstractListModel {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2433865072707477031L;

  /** The list of entries. */
  private List<Entry> list;

  /**
   * Initialises a new instance of the {@link ConfigurationListModel} class with
   * the specified list of entries.
   * 
   * @param list
   *          The list of entries to use.
   */
  public ConfigurationListModel(List<Entry> list) {
    this.list = list;
  }

  @Override
  public Object getElementAt(int index) {
    return list.get(index);
  }

  @Override
  public int getSize() {
    return list.size();
  }

  /**
   * Adds an element to the list.
   * 
   * @param factoryName
   *          The factory class name.
   * @param parameters
   *          The ParameterBlock.
   */
  public void addElement(String factoryName, ParameterBlock parameters) {
    Entry entry = new Entry();
    entry.setFactoryName(factoryName);
    entry.setParameters(parameters);
    list.add(entry);

    int index = list.size() - 1;
    fireIntervalAdded(this, index, index);
  }

  /**
   * Removes an element from the list.
   * 
   * @param index
   *          The index of the element to remove.
   */
  public void removeElement(int index) {
    list.remove(index);
    fireIntervalRemoved(this, index, index);
  }

  /**
   * Edits a specific index, that is, it sets a new factory name and parameter
   * block.
   * 
   * @param index
   *          The element index.
   * @param newFactoryName
   *          The new factory name.
   * @param newParameters
   *          The new parameter block.
   */
  public void editElement(int index, String newFactoryName,
      ParameterBlock newParameters) {
    list.get(index).setFactoryName(newFactoryName);
    list.get(index).setParameters(newParameters);
    fireContentsChanged(this, index, index);
  }

  /**
   * Swap the two list elements given
   * 
   * @param elementOne
   * @param elementTwo
   */
  public void swap(int elementOne, int elementTwo) {
    Entry temp = list.get(elementOne);
    list.set(elementOne, list.get(elementTwo));
    list.set(elementTwo, temp);
  }

}