/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.plugintype;

import java.util.Map;

import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;

/**
 * The Class ModelParameterWindow. All windows that can be used to parameterize
 * a model (or a part thereof) must be descendants of this class.
 * 
 * @param <M>
 *          type of the symbolic model
 * @author Stefan Rybacki
 */
public abstract class ModelParameterWindow<M extends ISymbolicModel<?>> extends
    AbstractWindow {

  /** Reference to the model to be parameterized. */
  private final M model;

  /**
   * Creates a new instance of {@code ModelParameterWindow}.
   * 
   * @param title
   *          title of the window
   * @param mod
   *          the model to be parameterized
   */
  public ModelParameterWindow(String title, M mod) {
    super(title, null, Contribution.EDITOR);
    model = mod;
  }

  /**
   * Gets the model.
   * 
   * @return the model to be parameterized
   */
  public M getModel() {
    return model;
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.model.parameter.editor";
  }

  /**
   * Gets the parameters set and created by this editor. Parameters can be of
   * any type but have to be put into a map so they can be merged with
   * additional model parameters.
   * 
   * @return the parameters created and set by this editor
   */
  public abstract Map<String, ?> getParameters();

  /**
   * Sets the parameters from the given object.
   * 
   * @param parameters
   *          the parameters to set, usually the object is the same kind as
   *          returned by {@link #getParameters()}
   */
  public abstract void setParameters(Map<String, ?> parameters);

  /**
   * Gets additional parameters also set by the editor that can for instance be
   * used for visualization purposes but don't be real experiment parameters
   * that are returned by {@link #getParameters()}. Override this method to
   * provide custom parameters.
   * 
   * @return the additional user parameters created or set by this editor
   */
  public Object getUserParameters() {
    return null;
  }
}
