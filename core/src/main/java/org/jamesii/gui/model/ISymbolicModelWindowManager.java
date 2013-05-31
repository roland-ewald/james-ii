/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model;

import java.net.URI;

import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;

/**
 * Manager interface that is used to provide management mechanisms for open
 * {@link ModelWindow}s.
 * 
 * @author Stefan Rybacki
 */
public interface ISymbolicModelWindowManager {

  /**
   * Updates all other model windows registered for the given model (*except*
   * for the updating window).
   * 
   * @param model
   *          symbolic model that was updated
   * @param updatingWindow
   *          window that issued the update
   */
  void modelUpdated(ISymbolicModel<?> model,
      ModelWindow<? extends ISymbolicModel<?>> updatingWindow);

  /**
   * Removes a model window.
   * 
   * @param model
   *          symbolic model that is attached to the window that is being closed
   * @param window
   *          window to be removed
   * @return true, if the window needs indeed to be removed, otherwise false
   */
  boolean removeModelWindow(ISymbolicModel<?> model,
      ModelWindow<? extends ISymbolicModel<?>> window);

  /**
   * Reads model and adds it to model window manager.
   * 
   * @param factory
   *          factory to read/write the model
   * @param parameter
   *          parameter to read/write the model
   * @param cutomReaderParameters
   *          custom model reader parameters
   */
  void addModel(ModelReaderFactory factory, ParameterBlock parameter,
      ParameterBlock cutomReaderParameters);

  /**
   * Adds a window for the given model
   * 
   * @param model
   *          the model a window should be created for
   */
  void addWindowForModel(ISymbolicModel<?> model);

  /**
   * Adds model to model window manager.
   * 
   * @param model
   *          model to be added
   */
  void addModel(ISymbolicModel<?> model);

  /**
   * Saves a model, will be the same as if the user saved the model from the
   * model window that was opened last.
   * 
   * @param model
   *          the model to be saved
   */
  void saveModel(ISymbolicModel<?> model);

  /**
   * Save model if model reader writer factory and reader writer parameters are
   * known, otherwise
   * {@link SymbolicModelWindowManager#saveModelAs(ModelWindow)} is called.
   * 
   * @param modelWindow
   *          model window which is attached to the model
   */
  void saveModel(ModelWindow<? extends ISymbolicModel<?>> modelWindow);

  /**
   * Save model with a model reader writer to be selected.
   * 
   * @param modelWindow
   *          model window that is active
   */
  void saveModelAs(ModelWindow<? extends ISymbolicModel<?>> modelWindow);

  /**
   * Gets the model URI.
   * 
   * If the model has been loaded from or already been written to a file this
   * method will retrieve the URI. If not the URI might be null.
   * 
   * @param modelWindow
   *          the model window
   * 
   * @return the model uri
   */
  URI getModelURI(ModelWindow<? extends ISymbolicModel<?>> modelWindow);

  /**
   * Gets the model uri.
   * 
   * If the model has been loaded from or already been written to a file this
   * method will retrieve the URI. If not the URI might be null.
   * 
   * @param model
   *          the model
   * 
   * @return the model uri
   */
  URI getModelURI(ISymbolicModel<?> model);
}