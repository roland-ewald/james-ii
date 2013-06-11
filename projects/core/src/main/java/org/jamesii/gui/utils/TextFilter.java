/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

/**
 * A default filter that implements {@link IFilter} using text as filter value.
 * Values are filtered if the specified filter value is not part of the tested
 * value.
 * 
 * @author Stefan Rybacki
 * 
 */
public class TextFilter extends AbstractFilter<String> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7028991264510823005L;

  /**
   * Creates a new default text filter using text for filtering
   */
  public TextFilter() {
    this(null);
  }

  /**
   * Creates a new text filter using text for filtering and setting the filter
   * value to the specified text.
   * 
   * @param filterText
   *          the filter text to set
   */
  public TextFilter(String filterText) {
    super(filterText);
  }

  @Override
  public boolean filteredWithValue(String value, Object object) {
    if (value == null || value.length() == 0) {
      return false;
    }

    return !object.toString().toLowerCase().contains(value.toLowerCase());
  }

}
