/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class QualitativeBaseVariable.
 * 
 * @param <T>
 */
public class QualitativeBaseVariable<T> extends BaseVariable<T> implements
    IQualitativeVariable<T> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -122221892365346952L;

  /** Internal list of categories for this variable. */
  private List<T> categories;

  /** Internal flag. If true the categories are ordered, otherwise not */
  private boolean ordinal = false;

  /**
   * Instantiates a new qualitative variable.
   */
  public QualitativeBaseVariable() {
    super();
  }

  /**
   * Create a new ordinal variable using the given categories.
   * 
   * @param categories
   *          Categories of the variable
   * @param ordinal
   *          Are the categories ordinal or nominal
   */
  public QualitativeBaseVariable(List<T> categories, boolean ordinal) {
    super();
    this.categories = categories;
    this.ordinal = ordinal;
  }

  @Override
  @SuppressWarnings("unchecked")
  public BaseVariable<T> copyVariable() {
    QualitativeBaseVariable<T> newVar = null;
    try {
      newVar = this.getClass().newInstance();
      newVar.setName(this.getName());
      newVar.setValue(this.getValue());
      newVar.setOrdinal(this.isOrdinal());

      // Copy categories array list
      List<T> newCategories = new ArrayList<>();
      List<T> originalCategories = this.getCategories();
      for (int i = 0; i < originalCategories.size(); i++) {
        newCategories.add(originalCategories.get(i));
      }
      newVar.setCategories(newCategories);

    } catch (Exception ex) {
      SimSystem.report(ex);
    }
    return newVar;
  }

  @Override
  public List<T> getCategories() {
    return categories;
  }

  @Override
  public T getCategory(int num) {
    return categories.get(num);
  }

  @Override
  public int getIndexOf(T cat) {
    return categories.indexOf(cat);
  }

  @Override
  public boolean isOrdinal() {
    return ordinal;
  }

  /**
   * Sets the categories.
   * 
   * @param categories
   *          the categories
   * 
   * @see org.jamesii.core.model.variables.IQualitativeVariable#setCategories(List)
   */
  @Override
  public void setCategories(List<T> categories) {
    this.categories = categories;
  }

  /**
   * Sets the ordinal.
   * 
   * @param ordinal
   *          the ordinal
   * 
   * @see org.jamesii.core.model.variables.IQualitativeVariable#setOrdinal(boolean)
   */
  @Override
  public void setOrdinal(boolean ordinal) {
    this.ordinal = ordinal;
  }

  /**
   * Sets the random value.
   * 
   * @see org.jamesii.core.model.variables.IQualitativeVariable#setRandomValue()
   */
  @Override
  public void setRandomValue() {

    IRandom r1 = SimSystem.getRNGGenerator().getNextRNG();
    int rand1 = r1.nextInt(getCategories().size());

    setValue(getCategory(rand1));
  }
}
