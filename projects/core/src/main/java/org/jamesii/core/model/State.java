/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

/**
 * The Class State. Many models have a state on which functions of a model
 * compute something. This class extends the {@link AbstractState} class by
 * implementing all abstract methods defined in there.
 * 
 * Class from which all model states must inherit from
 * 
 * @author Jan Himmelspach
 */
public class State extends AbstractState {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 1173907981937875619L;

  /** Internal flag which determines whether the state has been changed or not. */
  private boolean changed = false;

  @Override
  public void changed() {
    changed = true;
  }

  @Override
  public boolean hasChanged() {
    return changed;
  }

  @Override
  public boolean isChangedRR() {
    boolean chg = changed;
    if (chg) {
      super.changed();
      clearChanged();
    }
    return chg;
  }

  @Override
  public void clearChanged() {
    changed = false;
  }

}
