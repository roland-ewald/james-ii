/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.windows.plugintype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.model.ISymbolicModelWindowManager;

/**
 * All windows displaying a model (or a part thereof) must be descendants of
 * this class. Model windows must always be automatically updateable via
 * modelChanged() (observer pattern), because there may be more than one model
 * window viewing the same part of a model. Model - View - Controller paradigm!!
 * 
 * Created on 31. March 2004, 14:13
 * 
 * @param <M>
 *          the type of the model to be shown
 * 
 * @author Jan Himmelspach (stub), Roland Ewald
 */
public abstract class ModelWindow<M extends ISymbolicModel<?>> extends
    AbstractWindow {

  /** Reference to the model to be displayed. */
  private M model;

  /** Reference to model window manager (to propagate changes). */
  private final ISymbolicModelWindowManager modelManager;

  /**
   * Creates a new instance of ModelWindow.
   * 
   * @param title
   *          title of the window
   * @param mod
   *          the model to be viewed/edited
   * @param mwManager
   *          the model window manager for this model
   */
  public ModelWindow(String title, M mod, ISymbolicModelWindowManager mwManager) {
    super(title, null, Contribution.EDITOR);
    model = mod;
    modelManager = mwManager;
  }

  /**
   * Gets the model.
   * 
   * @return the model
   */
  public M getModel() {
    return model;
  }

  /**
   * Will be called by {@link org.jamesii.gui.model.SymbolicModelWindowManager}
   * to let the model initialize itself. This is important for all actions that
   * require that the model is already properly registered at the
   * {@link org.jamesii.gui.model.SymbolicModelWindowManager}.
   */
  public void initWindow() {
  }

  /**
   * Called when the model of this window is changed.
   */
  public abstract void modelChanged();

  /**
   * Called when the model of this window should be prepared for saving (after
   * each model window has executed this method, modelChanged() will be called
   * subsequently).
   */
  public abstract void prepareModelSaving();

  @Override
  protected IAction[] generateActions() {
    List<IAction> actions = new ArrayList<>();
    IAction[] generatedActions = generateAdditionalActions();
    if (generatedActions != null) {
      actions.addAll(Arrays.asList(generatedActions));
    }
    return actions.toArray(new IAction[actions.size()]);
  }

  /*
   * // supposed to be abstract but due to compatibility issues it is not yet //
   * abstract
   * 
   * @Override protected final IAction[] generateActions() {
   * 
   * IAction[] actionsMenuBar = ActionManager
   * .getActionsFromMenuBar(getJMenuBar()); List<IAction> resultingActions = new
   * ArrayList<IAction>(); if (actionsMenuBar != null) for (IAction action :
   * actionsMenuBar) resultingActions.add(action);
   * 
   * IAction[] addActions = generateAdditionalActions(); if (addActions != null)
   * for (IAction action : addActions) resultingActions.add(action);
   * 
   * resultingActions.add(new NewModelWindowAction(getClass().getName() +
   * model.hashCode(), this)); return resultingActions.toArray(new
   * IAction[resultingActions.size()]); }
   */

  /**
   * Override this method to add window/model-specific actions.
   * 
   * @return the i action[]
   */
  protected IAction[] generateAdditionalActions() {
    return null;
  }

  @Override
  public boolean canClose() {
    return modelManager.removeModelWindow(model, this);
  }

  @Override
  public void windowClosed() {
    System.err.println("CLOSED!");
    // modelManager.removeModelWindow(model, this);
  }

  @Override
  public void save() {
    try {
      modelManager.saveModel(this);
      // ModelPerspective.getSymbolicModelWindowManager().saveModel(this);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Error while saving model '" + model.getName() + "'", ex);
    }
  }

  @Override
  public final boolean isSaveable() {
    return true;
  }

  @Override
  public void saveAs() {
    try {
      modelManager.saveModelAs(this);
      // ModelPerspective.getSymbolicModelWindowManager().saveModelAs(this);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Error while saving model '" + model.getName() + "'", ex);
    }
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.model.editor";
  }

  /**
   * @return the modelManager
   */
  protected ISymbolicModelWindowManager getModelManager() {
    return modelManager;
  }

}
