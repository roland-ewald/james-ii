/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.execution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.observe.INotifyingObserver;
import org.jamesii.core.observe.listener.IObserverListener;
import org.jamesii.core.observe.listener.plugintype.AbstractObserverListenerFactory;
import org.jamesii.core.observe.listener.plugintype.ObserverListenerFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;

/**
 * This class implements the management of various observers for different
 * (possibly simultaneous) simulation runs that are executed for one experiment.
 * 
 * @author Roland Ewald
 */
public class ExperimentObservationManager {

  /**
   * Flag that determines whether the manager should choose a new listener for
   * each newly registered observer automatically.
   */
  private boolean chooseAutomaticallyForExperiment = true;

  /**
   * Check box to let the user decide whether he wants to choose factories
   * manually or not.
   */
  private final JCheckBox chooseAutomaticallyCheckbox = new JCheckBox(
      "Choose automatically for this experiment");

  /**
   * Flag that determines whether the manager should attempt to close the
   * listeners (in case they are instances of {@link IWindow}) when the
   * observation of such a run is finished.
   */
  private boolean closeOnFinish = false;

  /**
   * Check box to let the user decide whether the windows shall be closed after
   * finish.
   */
  private final JCheckBox closeOnFinishCheckbox = new JCheckBox("Close on finish");

  /**
   * Map that stores which factories and observer listeners to use for observer
   * listener creation or re-use.
   */
  private final Map<String, List<ObserverListenerEntry>> currentObserverListenerEntries =
      new HashMap<>();

  /**
   * Map from the running simulation (represented by the SRTI object) to the map
   * of notifying observer class => list of all observer listeners that are fed
   * by the notifying observer.
   */
  private final Map<ComputationTaskRuntimeInformation, Map<INotifyingObserver<?>, List<IObserverListener>>> currentObserverListeners =
      new HashMap<>();

  /** Flag to switch re-use of observer listener windows on/off. */
  private boolean reuseWindows = true;

  /**
   * Check box to let the user decide whether the last windows shall be re-used
   * or not.
   */
  private final JCheckBox reuseWindowsCheckbox = new JCheckBox(
      "Re-use existing windows");

  {
    reuseWindowsCheckbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (reuseWindowsCheckbox.isSelected()) {
          closeOnFinishCheckbox.setSelected(false);
          closeOnFinishCheckbox.setEnabled(false);
        } else {
          closeOnFinishCheckbox.setEnabled(true);
        }
      }
    });
  }
  {
    closeOnFinishCheckbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (closeOnFinishCheckbox.isSelected()) {
          reuseWindowsCheckbox.setSelected(false);
          reuseWindowsCheckbox.setEnabled(false);
        } else {
          reuseWindowsCheckbox.setEnabled(true);
        }
      }
    });
  }

  /**
   * Flag to disable user interaction and therefore omit any visualization
   * (useful for batch operations).
   */
  private boolean disabled = false;

  /**
   * Chooses observers automatically.
   * 
   * @param crti
   *          the crti
   * @param observer
   *          the observer
   * @param listenerLookupKey
   *          the lookup used for the internal buffer of listener observers
   */
  void chooseListenersAutomatically(ComputationTaskRuntimeInformation crti,
      INotifyingObserver<?> observer, String listenerLookupKey) {

    List<ObserverListenerEntry> olEntries =
        currentObserverListenerEntries.get(listenerLookupKey);
    List<IObserverListener> createdListeners = new ArrayList<>();

    for (ObserverListenerEntry olEntry : olEntries) {

      if (!reuseWindows) {
        olEntry.setListener(olEntry.getListenerFactory().create(
            olEntry.getParameters(), SimSystem.getRegistry().createContext()));
      }

      registerListener(observer, olEntry.getListener(), !reuseWindows);
      createdListeners.add(olEntry.getListener());
    }

    updateCurrentObserverListeners(crti, observer, createdListeners);
  }

  /**
   * Checks if is close on finish.
   * 
   * @return true, if is close on finish
   */
  public boolean isCloseOnFinish() {
    return closeOnFinish;
  }

  /**
   * Registers an observer for the experiment.
   * 
   * @param crti
   *          simulation runtime info
   * @param observer
   *          the notifying observer
   */
  public void registerExperimentObserver(
      ComputationTaskRuntimeInformation crti, INotifyingObserver<?> observer) {
    throw new OperationNotSupportedException(
        "Not yet implemented (experiment observation manager)! " + crti
            + " -- " + observer);
  }

  /**
   * Registers listener at observer, adds listener to main window if necessary.
   * 
   * @param observer
   *          the observer
   * @param observerListener
   *          the observer listener
   * @param registerAsWindow
   *          the register as window
   */
  void registerListener(INotifyingObserver<?> observer,
      IObserverListener observerListener, boolean registerAsWindow) {
    observer.addListener(observerListener);
    if (!registerAsWindow || !(observerListener instanceof IWindow)) {
      return;
    }
    addWindow((IWindow) observerListener);
  }

  /**
   * Registers a model observer.
   * 
   * @param srti
   *          simulation runtime info
   * @param observer
   *          the notifying observer
   */
  public void registerModelObserver(ComputationTaskRuntimeInformation srti,
      INotifyingObserver<?> observer) {
    registerObserver(srti, observer);
  }

  /**
   * Registers observer listeners for the given runtime information and the
   * notifying observer.
   * 
   * @param crti
   *          computation task runtime information
   * @param observer
   *          notifying model observer that can be listened to
   */
  public synchronized void registerObserver(
      ComputationTaskRuntimeInformation crti, INotifyingObserver<?> observer) {

    if (isDisabled()) {
      return;
    }

    String listenerLookupKey = generateListenerEntryLookupKey(crti, observer);

    if (chooseAutomaticallyForExperiment
        && currentObserverListenerEntries.containsKey(listenerLookupKey)) {
      chooseListenersAutomatically(crti, observer, listenerLookupKey);
      return;
    }

    ParameterBlock aolfp =
        new ParameterBlock(observer.getClass(),
            AbstractObserverListenerFactory.OBSERVER_CLASS);

    List<ObserverListenerFactory> factories =
        SimSystem.getRegistry().getFactoryOrEmptyList(
            AbstractObserverListenerFactory.class, aolfp);

    FactorySelectionDialog<ObserverListenerFactory> dialog =
        new FactorySelectionDialog<>(null, factories, new JLabel("Observer:"
            + observer.getClass().getName()), "Choose observer output handler",
            false);

    dialog.getAddContentPanel().add(chooseAutomaticallyCheckbox);
    dialog.getAddContentPanel().add(reuseWindowsCheckbox);
    dialog.getAddContentPanel().add(closeOnFinishCheckbox);

    SwingUtilities.updateComponentTreeUI(dialog);
    dialog.setVisible(true);

    if (dialog.isOkButtonPressed()) {
      List<ParameterizedFactory<ObserverListenerFactory>> selectedFactories =
          dialog.getSelectedFactoriesAndParameters();
      List<IObserverListener> createdListeners = new ArrayList<>();
      List<ObserverListenerEntry> createdListenerEntries = new ArrayList<>();

      updateConfigFlags();

      for (ParameterizedFactory<ObserverListenerFactory> factory : selectedFactories) {
        IObserverListener observerListener =
            factory.getFactory().create(factory.getParameters(), SimSystem.getRegistry().createContext());

        if (observerListener == null) {
          continue;
        }

        // Register newly created listener at observer
        createdListeners.add(observerListener);
        createdListenerEntries.add(new ObserverListenerEntry(observerListener,
            factory.getFactory(), factory.getParameters()));

        registerListener(observer, observerListener, true);
      }

      // Save creation information
      currentObserverListenerEntries.put(listenerLookupKey,
          createdListenerEntries);

      updateCurrentObserverListeners(crti, observer, createdListeners);
    }
  }

  /**
   * Generate listener entry lookup key.
   * 
   * @param crti
   *          the computation task runtime information
   * @param observer
   *          the observer
   * @return the string
   */
  protected String generateListenerEntryLookupKey(
      ComputationTaskRuntimeInformation crti, INotifyingObserver<?> observer) {
    String listenerLookupKey =
        crti.getComputationTaskConfiguration().getExperimentID().asString()
            + observer.getClass();
    return listenerLookupKey;
  }

  /**
   * Registers a simulation observer.
   * 
   * @param srti
   *          simulation runtime info
   * @param observer
   *          the notifying observer
   */
  public void registerSimulationObserver(
      ComputationTaskRuntimeInformation srti, INotifyingObserver<?> observer) {
    registerObserver(srti, observer);
  }

  /**
   * Reset.
   */
  public void reset() {
    currentObserverListeners.clear();
    currentObserverListenerEntries.clear();
  }

  /**
   * When a run is finished the observation manager is configured to close the
   * windows of the observer listeners, this is done here. All observer
   * listeners will be notified that this run is finished.
   * 
   * @param srti
   *          simulation runtime info
   */
  public void runFinished(ComputationTaskRuntimeInformation srti) {

    if (!currentObserverListeners.containsKey(srti)) {
      return;
    }

    Collection<List<IObserverListener>> observerListeners =
        currentObserverListeners.get(srti).values();
    ArrayList<IObserverListener> allObsListeners = new ArrayList<>();
    for (List<IObserverListener> obsListenerList : observerListeners) {
      allObsListeners.addAll(obsListenerList);
    }

    if (closeOnFinish) {
      for (IObserverListener observerListener : allObsListeners) {
        if (observerListener instanceof IWindow) {
          removeWindow((IWindow) observerListener);
        }
      }
      currentObserverListeners.get(srti).clear();
    }
  }

  /**
   * Sets the close on finish.
   * 
   * @param closeOnFinish
   *          the new close on finish
   */
  public void setCloseOnFinish(boolean closeOnFinish) {
    this.closeOnFinish = closeOnFinish;
  }

  /**
   * Updates behaviour flags.
   */
  void updateConfigFlags() {
    chooseAutomaticallyForExperiment = chooseAutomaticallyCheckbox.isSelected();
    reuseWindows = reuseWindowsCheckbox.isSelected();
    closeOnFinish = closeOnFinishCheckbox.isSelected();
  }

  /**
   * Updates current observer listeners data structure.
   * 
   * @param srti
   *          the srti
   * @param observer
   *          the observer
   * @param createdListeners
   *          the created listeners
   */
  void updateCurrentObserverListeners(ComputationTaskRuntimeInformation srti,
      INotifyingObserver<?> observer, List<IObserverListener> createdListeners) {

    // Update listener map
    if (!currentObserverListeners.containsKey(srti)) {
      currentObserverListeners.put(srti,
          new HashMap<INotifyingObserver<?>, List<IObserverListener>>());
    }
    currentObserverListeners.get(srti).put(observer, createdListeners);

    // Initialise all listeners
    for (IObserverListener ol : createdListeners) {
      ol.init(srti);
    }
  }

  /**
   * Checks if is disabled.
   * 
   * @return true, if is disabled
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Sets the disabled.
   * 
   * @param disabled
   *          the new disabled
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  /**
   * Adds the window.
   * 
   * @param observerWindow
   *          the observer window
   */
  protected void addWindow(IWindow observerWindow) {
    WindowManagerManager.getWindowManager().addWindow(observerWindow);
  }

  /**
   * Removes the window.
   * 
   * @param observerWindow
   *          the observer window
   */
  protected void removeWindow(IWindow observerWindow) {
    WindowManagerManager.getWindowManager().closeWindow(observerWindow);
  }

}

/**
 * Auxiliary class to keep track of {@link IObserverListener} entities.
 * 
 * @author Roland Ewald
 * 
 */
class ObserverListenerEntry {

  /** The listener. */
  private IObserverListener listener;

  /** The factory for the listener. */
  private ObserverListenerFactory listenerFactory;

  /** The factory parameters. */
  private ParameterBlock parameters;

  /**
   * Default constructor.
   * 
   * @param ol
   *          the observer listener
   * @param olf
   *          the listener's factory
   * @param params
   *          the factory paramters
   */
  ObserverListenerEntry(IObserverListener ol, ObserverListenerFactory olf,
      ParameterBlock params) {
    setListener(ol);
    setListenerFactory(olf);
    setParameters(params);
  }

  /**
   * @return the listenerFactory
   */
  public ObserverListenerFactory getListenerFactory() {
    return listenerFactory;
  }

  /**
   * @param listenerFactory
   *          the listenerFactory to set
   */
  public final void setListenerFactory(ObserverListenerFactory listenerFactory) {
    this.listenerFactory = listenerFactory;
  }

  /**
   * @return the listener
   */
  public IObserverListener getListener() {
    return listener;
  }

  /**
   * @param listener
   *          the listener to set
   */
  public final void setListener(IObserverListener listener) {
    this.listener = listener;
  }

  /**
   * @return the parameters
   */
  public ParameterBlock getParameters() {
    return parameters;
  }

  /**
   * @param parameters
   *          the parameters to set
   */
  public final void setParameters(ParameterBlock parameters) {
    this.parameters = parameters;
  }

}