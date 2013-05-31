/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.factories;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * @author Stefan Rybacki
 * 
 */
class CheckListItem<F extends Factory<?>> implements IFactorySelector<F> {
  private boolean selected;

  private ParameterBlock block;

  private F factory;

  public CheckListItem(F fac, ParameterBlock block, boolean selected) {
    this.factory = fac;
    this.block = block;
    this.selected = selected;
  }

  @Override
  public F getFactory() {
    return factory;
  }

  @Override
  public ParameterBlock getParameters() {
    return block;
  }

  @Override
  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean s) {
    selected = s;
  }

  @Override
  public void setParameters(ParameterBlock parameterBlock) {
    block = parameterBlock;
  }

}
