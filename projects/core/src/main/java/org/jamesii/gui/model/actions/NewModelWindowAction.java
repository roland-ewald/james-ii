/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.actions;

import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.model.ModelPerspective;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;

/**
 * Implements the addition of a new {@link ModelWindow} for the current model.
 * 
 * @author Roland Ewald
 */
public class NewModelWindowAction extends AbstractAction {

  /**
   * The model window this action is assigned to.
   */
  private final ModelWindow<?> thisWin;

  /**
   * Instantiates a new new model window action.
   * 
   * @param id
   *          the id to use as id in
   *          {@link org.jamesii.gui.application.action.IAction}
   * @param thisWindow
   *          the model window this action is assigned to
   */
  public NewModelWindowAction(String id, ModelWindow<?> thisWindow) {
    super("org.jamesii.model.newmodelwindow." + id, "New Window",
        new String[] { "" }, null, null, thisWindow);
    thisWin = thisWindow;
  }

  @Override
  public void execute() {
    ModelPerspective.getSymbolicModelWindowManager().addWindowForModel(
        thisWin.getModel());
  }

}
