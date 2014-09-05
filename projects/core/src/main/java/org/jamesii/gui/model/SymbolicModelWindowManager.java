/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.data.model.write.plugintype.AbstractModelWriterFactory;
import org.jamesii.core.data.model.write.plugintype.ModelWriterFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;
import org.jamesii.gui.utils.factories.IFactoryDescriptionRenderer;
import org.jamesii.gui.utils.history.History;

/**
 * Manages the model windows in the GUI.
 * 
 * @author Roland Ewald
 */
public class SymbolicModelWindowManager implements ISymbolicModelWindowManager {

  /** Map to the model writer factories that shall be used. */
  private Map<ISymbolicModel<?>, ModelWriterFactory> modelWFactories =
      new HashMap<>();

  /** Map to the model reader factories that shall be used. */
  private Map<ISymbolicModel<?>, ModelReaderFactory> modelRFactories =
      new HashMap<>();

  /** Parameters to read/write the models. */
  private Map<ISymbolicModel<?>, ParameterBlock> modelRWParams =
      new HashMap<>();

  /** Custom parameter to read/write models, dependent on factory. */
  private Map<ISymbolicModel<?>, ParameterBlock> customModelRWParams =
      new HashMap<>();

  /** Storage for model <-> model window dependencies. */
  private Map<ISymbolicModel<?>, List<ModelWindow<? extends ISymbolicModel<?>>>> modelWindows =
      new HashMap<>();

  /**
   * Holds the save-status for each model. True, if the model has just been
   * saved, otherwise false.
   */
  private Map<ISymbolicModel<?>, Boolean> saveStatus = new HashMap<>();

  /** the window manager to use. */
  private IWindowManager winManager;

  /**
   * Default constructor.
   * 
   * @param windowManager
   *          the window manager to use TODO add usage purpose
   */
  public SymbolicModelWindowManager(IWindowManager windowManager) {
    winManager = windowManager;
  }

  /**
   * Get all managed models.
   * 
   * @return list of managed models
   */
  public List<ISymbolicModel<?>> getOpenModels() {
    return new ArrayList<>(modelWindows.keySet());
  }

  @Override
  public void addModel(ISymbolicModel<?> model) {
    addModelWindow(model, null, null, null, null);
  }

  @Override
  public void addModel(ModelReaderFactory factoryR, ParameterBlock parameter,
      ParameterBlock cutomReaderParameters) {

    ISymbolicModel<?> model = null;

    for (Entry<ISymbolicModel<?>, ParameterBlock> modelEntry : modelRWParams
        .entrySet()) {
      if (parameter.equals(modelEntry.getValue())) {
        model = modelEntry.getKey();
        break;
      }
    }

    // if the model does not already exist, we create it.
    if (model == null) {
      try {
        model =
            factoryR.create(cutomReaderParameters, SimSystem.getRegistry().createContext()).read(
                (URI) parameter.getSubBlockValue("URI"));
      } catch (Throwable t) {
        SimSystem.report(Level.SEVERE, "Reading the given model failed.", t);
      }
    }

    if (model == null) {
      SimSystem.report(Level.WARNING,
          "The reader didn't create a model instance!");
      return;
    }

    // store opened model URI in History
    History.putValueIntoHistory(ModelPerspective.RECENTLY_USED_ID, parameter
        .getSubBlockValue("URI").toString());

    addModelWindow(model, factoryR, null, parameter, cutomReaderParameters);
  }

  /**
   * Adds model window and default editor.
   * 
   * @param model
   *          model for which a window shall be added
   * @param parameter
   *          parameters for the model reader/writer factory
   * @param customParameters
   *          parameter block with custom parameters
   * @param factoryR
   *          the reader factory for the model
   * @param factoryW
   *          the writer factory for the model
   */
  protected void addModelWindow(ISymbolicModel<?> model,
      ModelReaderFactory factoryR, ModelWriterFactory factoryW,
      ParameterBlock parameter, ParameterBlock customParameters) {

    ParameterBlock amwfp =
        new ParameterBlock(model, AbstractModelWindowFactory.MODEL);
    List<ModelWindowFactory> modelWindowFactories = null;

    modelWindowFactories =
        SimSystem.getRegistry().getFactoryOrEmptyList(
            AbstractModelWindowFactory.class, amwfp);

    if (modelWindowFactories.size() == 0) {
      ApplicationLogger
          .log(
              Level.SEVERE,
              "Error while creating new model: There is no editor available for models of type ' "
                  + model.getClass().getName() + "'.");
      return;
    }

    if (modelWindowFactories.size() > 1) {
      FactorySelectionDialog<ModelWindowFactory> fsDialog =
          new FactorySelectionDialog<>(null, modelWindowFactories, new JLabel(
              "Model: '" + model.getName() + "'"),
              "Choose available model windows.", false);
      fsDialog.setVisible(true);
      modelWindowFactories = fsDialog.getSelectedFactories();
    }

    modelRFactories.put(model, factoryR);
    modelWFactories.put(model, factoryW);
    if (parameter != null) {
      parameter.setValue(model);
    }
    modelRWParams.put(model, parameter);
    customModelRWParams.put(model, customParameters);

    for (ModelWindowFactory chosenFactory : modelWindowFactories) {
      addModelWindow(
          model,
          chosenFactory.create(amwfp,
              ModelPerspective.getSymbolicModelWindowManager()));
    }
  }

  /**
   * Registers a listener for a given model.
   * 
   * @param model
   *          model for which a window shall be added
   * @param window
   *          model window that shall be added
   */
  protected void addModelWindow(ISymbolicModel<?> model,
      ModelWindow<? extends ISymbolicModel<?>> window) {

    boolean newModel = false;

    if (modelWindows.get(model) == null) {
      newModel = true;
      modelWindows.put(model,
          new ArrayList<ModelWindow<? extends ISymbolicModel<?>>>());
    }

    if (!modelWindows.get(model).contains(window)) {
      modelWindows.get(model).add(window);
    }

    // Initialises window
    window.initWindow();

    winManager.addWindow(window);

    // Save status should be true if model was opened (ie., there
    // already are
    // model reader/writer params), and otherwise false
    if (newModel) {
      saveStatus.put(model, modelRWParams.get(model) != null);
    }
  }

  @Override
  public void addWindowForModel(ISymbolicModel<?> model) {
    addModelWindow(model, modelRFactories.get(model),
        modelWFactories.get(model), modelRWParams.get(model),
        customModelRWParams.get(model));
  }

  /**
   * Displays status in status bar.
   * 
   * @param model
   *          model that was written
   * @param uri
   *          location where the model was written to
   */
  void displayStatus(ISymbolicModel<?> model, URI uri) {
    SimSystem.report(Level.INFO, "Model '" + model.getName() + "' saved to: "
        + BasicUtilities.displayURI(uri));
  }

  @Override
  public synchronized void modelUpdated(ISymbolicModel<?> model,
      ModelWindow<? extends ISymbolicModel<?>> updatingWindow) {
    if (modelWindows.get(model) == null) {
      return;
    }
    if (!modelWindows.get(model).contains(updatingWindow)) {
      throw new RuntimeException(
          "Cannot process update on model that is not managed yet!");
    }
    for (ModelWindow<? extends ISymbolicModel<?>> window : modelWindows
        .get(model)) {
      if (!window.equals(updatingWindow)) {
        window.modelChanged();
      }
    }
    saveStatus.put(model, false);
  }

  /**
   * Removes a model window.
   * 
   * @param model
   *          model that is attached to the window that is being closed
   * @param window
   *          window to be removed
   * @return true, if the window needs indeed to be removed, otherwise false
   */
  @Override
  public boolean removeModelWindow(ISymbolicModel<?> model,
      ModelWindow<? extends ISymbolicModel<?>> window) {

    if (modelWindows.get(model) == null || model == null
        || modelWindows.get(model).size() <= 0) {
      return true;
    }

    // If this is the last window attached to the model and it is not
    // saved -
    // ask user for saving instructions
    if (modelWindows.get(model).size() == 1
        && modelWindows.get(model).get(0).equals(window)
        && !saveStatus.get(model)) {

      // winManager.closeWindow(window);

      int saveModelDecision =
          BasicUtilities.printQuestion(WindowManagerManager.getWindowManager()
              .getMainWindow(), "Save unsaved model?", String.format(
              "You are closing the last view on the model '%s'.\n "
                  + "If you do not save the model, all changes since "
                  + "last save will be lost.\n "
                  + "Do you want to save the model now?", model.getName()));

      switch (saveModelDecision) {
      case JOptionPane.CANCEL_OPTION:
        return false;
      case JOptionPane.YES_OPTION:
        saveModel(window);
        // If user aborted saving, don't close the window
        if (!saveStatus.get(model)) {
          return false;
        }

        break;
      }

      modelWindows.remove(model);
      customModelRWParams.remove(model);
      modelRFactories.remove(model);
      modelWFactories.remove(model);
      modelRWParams.remove(model);
      saveStatus.remove(model);
    } else {
      modelWindows.get(model).remove(window);
    }

    return true;
  }

  @Override
  public void saveModel(ISymbolicModel<?> model) {
    List<ModelWindow<? extends ISymbolicModel<?>>> modWindows =
        this.modelWindows.get(model);
    if (modWindows == null || modWindows.size() == 0) {
      throw new RuntimeException("No model window for model '"
          + model.getName() + "' available.");
    }
    saveModel(modWindows.get(modWindows.size() - 1));
  }

  @Override
  public void saveModel(ModelWindow<? extends ISymbolicModel<?>> modelWindow) {

    ISymbolicModel<?> model = modelWindow.getModel();
    ModelWriterFactory mrwFactory = modelWFactories.get(model);
    ParameterBlock mrwParameter = modelRWParams.get(model);

    if (mrwFactory == null || mrwParameter == null) {
      saveModelAs(modelWindow);
      return;
    }

    saveModel(modelWindow, mrwFactory, mrwParameter,
        customModelRWParams.get(model));
  }

  /**
   * Actual saving function. Model listeners are activated. Then the factory
   * writes the model. Finally, the status bar is updated.
   * 
   * @param modelWindow
   *          window that was active
   * @param mrwFactory
   *          factory to write the model
   * @param parameter
   *          parameters to write the model
   * @param customWriterParameters
   *          custom writer parameters
   */
  void saveModel(ModelWindow<? extends ISymbolicModel<?>> modelWindow,
      ModelWriterFactory mrwFactory, ParameterBlock parameter,
      ParameterBlock customWriterParameters) {
    ISymbolicModel<?> model = parameter.getValue();// getModelToWrite();
    URI uri = parameter.getSubBlockValue(IURIHandling.URI);
    setupModelSaving(modelWindow, model);
    try {
      mrwFactory.create(customWriterParameters, SimSystem.getRegistry().createContext()).write(model, uri);
    } catch (Exception ex) {
      SimSystem.report(
          Level.SEVERE,
          "Error while saving model '" + model.getName() + "' to URI '"
              + BasicUtilities.displayURI(uri) + "' with factory "
              + mrwFactory.getClass(), ex);
      return;
    }
    saveStatus.put(model, true);
    modelWFactories.put(model, mrwFactory);
    parameter.setValue(model);
    modelRWParams.put(model, parameter);
    customModelRWParams.put(model, customWriterParameters);
    displayStatus(model, uri);
  }

  @Override
  public void saveModelAs(ModelWindow<? extends ISymbolicModel<?>> modelWindow) {

    ISymbolicModel<?> model = modelWindow.getModel();

    ParameterBlock amwfp =
        new ParameterBlock(model, AbstractModelWriterFactory.MODEL);

    Map<ModelWriterFactory, IFactoryParameterDialog<?>> guiFactories =
        BasicUtilities.getGUIFactories(ModelWriterFactory.class, amwfp, null);
    ModelWriterFactory chosenFactory = null;

    if (guiFactories.size() == 0) {
      return;
    }

    // If more than one factory is available, let the user decide
    if (guiFactories.size() > 1) {

      FactorySelectionDialog<ModelWriterFactory> fsDialog =
          new FactorySelectionDialog<>(null, guiFactories.keySet(), new JLabel(
              "Model '" + model.getName() + "'"),
              "Choose method to save the model.", true,
              new IFactoryDescriptionRenderer<ModelWriterFactory>() {
                @Override
                public String getDescription(ModelWriterFactory factory) {
                  return factory.getClass().getSimpleName();
                }
              });

      fsDialog.setVisible(true);
      List<ModelWriterFactory> selFactories = fsDialog.getSelectedFactories();

      if (!fsDialog.isOkButtonPressed() || selFactories.size() == 0) {
        return;
      }

      // Take the first factory, we are in single selection mode
      chosenFactory = selFactories.get(0);
    } else {
      // Choose the only factory that is available
      Set<ModelWriterFactory> factorySet = guiFactories.keySet();
      for (ModelWriterFactory factory : factorySet) {
        chosenFactory = factory;
        break;
      }
    }

    Pair<ParameterBlock, ? extends Factory> parameter =
        guiFactories.get(chosenFactory).getFactoryParameter(null);

    // If user aborted, do nothing
    if (parameter == null) {
      return;
    }

    parameter.getFirstValue().setValue(model);
    saveModel(modelWindow, chosenFactory, parameter.getFirstValue(), null
    /*
     * FIXME: This doesn't work anymore, both custom and non-custom RW params
     * need to be passed via first parameter block
     * 
     * parameter.getSecondValue ()
     */);

  }

  /**
   * Updates listeners etc.
   * 
   * @param modelWinFocused
   *          model window that is active
   * @param model
   *          model to be updated
   */
  void setupModelSaving(
      ModelWindow<? extends ISymbolicModel<?>> modelWinFocused,
      ISymbolicModel<?> model) {

    // get out if there are no more model windows
    if (modelWindows.get(model) == null) {
      return;
    }

    // Let each model window commit its changes
    for (ModelWindow<? extends ISymbolicModel<?>> window : modelWindows
        .get(model)) {
      if (!window.equals(modelWinFocused)) {
        window.prepareModelSaving();
      }
    }

    // The active model window is the last model window to prepare the
    // model for
    // saving, as it may overwrite all earlier changes
    modelWinFocused.prepareModelSaving();

    // Refresh each model window
    for (ModelWindow<? extends ISymbolicModel<?>> window : modelWindows
        .get(model)) {
      window.modelChanged();
    }
  }

  @Override
  public URI getModelURI(ModelWindow<? extends ISymbolicModel<?>> modelWindow) {
    ISymbolicModel<?> model = modelWindow.getModel();
    return getModelURI(model);
  }

  @Override
  public URI getModelURI(ISymbolicModel<?> model) {
    if (modelRWParams.get(model) == null) {
      return null;
    }
    return modelRWParams.get(model).getSubBlockValue("URI");
  }
}
