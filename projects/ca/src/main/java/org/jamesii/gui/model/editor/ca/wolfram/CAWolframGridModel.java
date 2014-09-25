/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.wolfram;

import java.awt.Rectangle;

import org.jamesii.gui.visualization.grid.AbstractGrid2DModel;
import org.jamesii.gui.visualization.grid.Grid2D;
import org.jamesii.gui.visualization.grid.IEditableGrid2DModel;
import org.jamesii.model.carules.symbolic.DefaultSymbolicCAModelInformation;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;
import org.jamesii.model.carules.symbolic.ISymbolicCAModelInformation;

/**
 * A grid model for editing Wolfram rules in a {@link Grid2D}.
 * 
 * @author Stefan Rybacki
 */
public class CAWolframGridModel extends AbstractGrid2DModel implements
    IEditableGrid2DModel {
  /** The CA model underlying the grid model. */
  private ISymbolicCAModel caModel;

  /** The Wolfram CA Rule. */
  private int iWolframRule;

  private ISymbolicCAModelInformation modelInfo;

  /**
   * Creates a grid model for a Wolfram CA model that can be used in a
   * {@link Grid2D}
   * 
   * @param model
   *          The symbolic CA model.
   */
  public CAWolframGridModel(ISymbolicCAModel model) {
    super();
    caModel = model;
    modelInfo = model.getAsDataStructure();
    if (modelInfo.isWolfram()) {
      iWolframRule = modelInfo.getWolframRule();
    } else {
      iWolframRule = 0;
    }
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(0, 0, 8, 1);
  }

  @Override
  public Object getValueAt(int x, int y) {
    if (x < 0 || x > 7 || y != 0) {
      throw new IndexOutOfBoundsException("x, y not within bounds!");
    }

    int res = (iWolframRule >> (7 - x)) & 1;

    return Integer.valueOf(res);
  }

  /**
   * Gets the Wolfram rule.
   * 
   * @return The current rule in this model.
   */
  public int getIWolframRule() {
    return iWolframRule;
  }

  /**
   * Sets the Wolfram rule.
   * 
   * @param wolframRule
   *          The rule to set.
   */
  public void setIWolframRule(int wolframRule) {
    iWolframRule = wolframRule;

    ISymbolicCAModelInformation ds = caModel.getAsDataStructure();

    DefaultSymbolicCAModelInformation mi =
        new DefaultSymbolicCAModelInformation(ds.getDimensions(),
            ds.getNeighborhood(), ds.getRules(), ds.getStates(),
            ds.isWolfram(), wolframRule, ds.getModelComment(), null);

    caModel.setFromDataStructure(mi);
    for (int i = 0; i < 8; i++) {
      fireCellChanged(i, 0);
    }
  }

  @Override
  public void setBounds(Rectangle newBounds) {
  }

  @Override
  public void setValueAt(int x, int y, Object newValue) {
    if (x < 0 || x > 7 || y != 0) {
      throw new IndexOutOfBoundsException("x, y not within bounds!");
    }

    int nV = (Integer) newValue > 0 ? 1 : 0;
    if (nV == 0) {
      iWolframRule &= ~(1 << (7 - x));
    } else {
      iWolframRule |= 1 << (7 - x);
    }
    fireCellChanged(x, y);
  }

  @Override
  public void clear() {
  }
}
