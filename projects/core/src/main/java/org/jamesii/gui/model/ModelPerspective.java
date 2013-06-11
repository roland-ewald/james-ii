/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model;

import java.awt.Window;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.plugintype.AbstractModelFactory;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Comparators;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.SeparatorAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.model.actions.NewModelAction;
import org.jamesii.gui.model.actions.OpenModelAction;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.gui.perspective.AbstractPerspective;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogParameter;
import org.jamesii.gui.utils.history.History;
import org.jamesii.gui.utils.history.HistoryItemEvent;
import org.jamesii.gui.utils.history.IHistoryItemListener;

/**
 * Perspective that handles the UI elements required for modeling.
 * 
 * @author Stefan Rybacki
 */
public class ModelPerspective extends AbstractPerspective {

  /**
   * Use this constant to access the {@link History} for opened models. Stored
   * values have to be in {@link URI} valid format.
   */
  public static final String RECENTLY_USED_ID = "org.jamesii.used.model";

  /**
   * Reference to manager for model listener.
   */
  private static SymbolicModelWindowManager modelWindowManager = null;

  /**
   * The base actions that do not change over time
   */
  private List<IAction> baseActions = new ArrayList<>();

  /**
   * The recently opened actions which represent a dynamic list of actions that
   * might change over time
   */
  private List<IAction> recentlyOpenedActions = new ArrayList<>();

  /**
   * @return the symbolic model window manager to use
   */
  public static ISymbolicModelWindowManager getSymbolicModelWindowManager() {
    return modelWindowManager;
  }

  @Override
  public String getDescription() {
    return "Allows to edit and create models.";
  }

  @Override
  public String getName() {
    return "Modeling Perspective";
  }

  @SuppressWarnings("unchecked")
  @Override
  protected synchronized List<IAction> generateActions() {

    if (modelWindowManager == null) {
      synchronized (this) {
        modelWindowManager = new SymbolicModelWindowManager(getWindowManager());
      }
    }

    baseActions.add(new ActionSet("model.new", "Model", new String[] {
        "org.jamesii.menu.main/org.jamesii.file/org.jamesii.new?first",
        "org.jamesii.toolbar.main/org.jamesii.new?first" }, null));

    baseActions.add(new ActionSet("model.open", "Model", new String[] {
        "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open?first",
        "org.jamesii.toolbar.main/org.jamesii.open?first" }, null));

    History.addListener(new IHistoryItemListener() {

      @Override
      public void cleaned(HistoryItemEvent event) {
        generateRecentlyOpenedActions();
        fireActionsChanged(getMergedActions());
      }

      @Override
      public void idRemoved(HistoryItemEvent event) {
        if (event.getId().equals(RECENTLY_USED_ID)
            || History.isSubkey(event.getId(), RECENTLY_USED_ID)) {
          generateRecentlyOpenedActions();
          fireActionsChanged(getMergedActions());
        }
      }

      @Override
      public void valueAdded(HistoryItemEvent event) {
        if (event.getId().equals(RECENTLY_USED_ID)
            || History.isSubkey(event.getId(), RECENTLY_USED_ID)) {
          generateRecentlyOpenedActions();
          fireActionsChanged(getMergedActions());
        }
      }

      @Override
      public void valueChanged(HistoryItemEvent event) {
        if (event.getId().equals(RECENTLY_USED_ID)
            || History.isSubkey(event.getId(), RECENTLY_USED_ID)) {
          generateRecentlyOpenedActions();
          fireActionsChanged(getMergedActions());
        }
      }

      @Override
      public void valueRemoved(HistoryItemEvent event) {
        if (event.getId().equals(RECENTLY_USED_ID)
            || History.isSubkey(event.getId(), RECENTLY_USED_ID)) {
          generateRecentlyOpenedActions();
          fireActionsChanged(getMergedActions());
        }
      }

    });

    ParameterBlock params =
        FactoryParameterDialogParameter.getParameterBlock(
            ModelReaderFactory.class, null);

    try {
      List<FactoryParameterDialogFactory<?, ?, ?>> factoryList =
          SimSystem.getRegistry().getFactoryOrEmptyList(
              AbstractFactoryParameterDialogFactory.class, params);

      for (FactoryParameterDialogFactory<?, ?, ?> dialogFactory : factoryList) {
        try {
          final IFactoryParameterDialog<ModelReaderFactory> dialog =
              (IFactoryParameterDialog<ModelReaderFactory>) dialogFactory
                  .create(params);
 

         baseActions
              .add(new AbstractAction(
                  "model.open." + dialog.hashCode(),
                  dialog.getMenuDescription(),
                  new String[] {
                      "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/model.open",
                      "org.jamesii.toolbar.main/org.jamesii.open/model.open" },
                  null) {
                private final ActionListener action = new OpenModelAction(
                    dialog);

                @Override
                public void execute() {
                  action.actionPerformed(null);
                }
              });
        } catch (Throwable e) {
          SimSystem.report(e);
        }
      }
      
    } catch (Exception e) {
      SimSystem.report(e);
    }

    // init new model list
    List<ModelFactory> factories = null;

    factories =
        SimSystem.getRegistry().getFactoryOrEmptyList(
            AbstractModelFactory.class, null);

    final Map<Class<? extends Formalism>, ModelFactory> modelFactories =
        new HashMap<>();

    for (ModelFactory factory : factories) {
      Class<? extends Formalism> fClass = factory.getFormalism().getClass();
      if (modelFactories.containsKey(fClass)) {
        continue;
      }
      modelFactories.put(fClass, factory);
    }

    ArrayList<Class<? extends Formalism>> formalisms =
        new ArrayList<>(modelFactories.keySet());
    Collections.sort(formalisms, Comparators.getClassNameComparator());

    for (final Class<? extends Formalism> formalism : formalisms) {
      // check whether an editor for the given formalism exists before adding it
      // to the actions list
      try {
        ParameterBlock amwfp =
            new ParameterBlock(modelFactories.get(formalism).create(),
                AbstractModelWindowFactory.MODEL);
        List<ModelWindowFactory> modelWindowFactories = null;

        modelWindowFactories =
            SimSystem.getRegistry().getFactoryOrEmptyList(
                AbstractModelWindowFactory.class, amwfp);

        if (modelWindowFactories.size() > 0) {
          baseActions.add(new NewModelAction(modelFactories.get(formalism)
              .getFormalism(), modelFactories.get(formalism), null));
        }
      } finally {

      }

    }

    // TODO sr137: reactivate generateRecentlyOpenedActions();

    return getMergedActions();
  }

  /**
   * Generate dynamic list of recently opened models.
   */
  private void generateRecentlyOpenedActions() {
    List<String> values =
        History.getValues(RECENTLY_USED_ID, true, History.LATEST, 10);
    recentlyOpenedActions.clear();

    if (values.size() == 0) {
      return;
    }

    recentlyOpenedActions
        .add(new SeparatorAction(
            "model.open.recent.seprator",
            new String[] {
                "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/model.open?last",
                "org.jamesii.toolbar.main/org.jamesii.open/model.open?last" },
            null));

    recentlyOpenedActions
        .add(new ActionSet(
            "model.open.recent",
            "Recently opened",
            new String[] {
                "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/model.open?after=model.open.recent.seprator",
                "org.jamesii.toolbar.main/org.jamesii.open/model.open?after=model.open.recent.seprator" },
            null));

    recentlyOpenedActions
        .add(new SeparatorAction(
            "model.open.recent.clearSeparator",
            new String[] {
                "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/model.open/model.open.recent?last",
                "org.jamesii.toolbar.main/org.jamesii.open/model.open/model.open.recent?last" },
            null));

    for (final String s : values) {
      try {
        final URI uri = new URI(s);

        // TODO sr137: find a better label than just the URI
        recentlyOpenedActions
            .add(new AbstractAction(
                "model.open.recent." + s,
                uri.getPath(),
                new String[] {
                    "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/model.open/model.open.recent",
                    "org.jamesii.toolbar.main/org.jamesii.open/model.open/model.open.recent" },
                null) {

              private ActionListener action = new OpenModelAction(
                  new IFactoryParameterDialog<ModelReaderFactory>() {

                    @Override
                    public Pair<ParameterBlock, ModelReaderFactory> getFactoryParameter(
                        Window parentWindow) {
                      return new Pair<>(new ParameterBlock(uri,
                          IURIHandling.URI),
                      // FIXME sr137: add factory here
                          null);
                    }

                    @Override
                    public String getMenuDescription() {
                      return s;
                    }

                  });

              @Override
              public void execute() {
                action.actionPerformed(null);
              }
            });
      } catch (URISyntaxException e) {
        SimSystem.report(e);
      }
    }

    recentlyOpenedActions
        .add(new AbstractAction(
            "model.open.recent.clear",
            "Clear list...",
            IconManager.getIcon(IconIdentifier.DELETE_SMALL, "Clear list..."),
            new String[] {
                "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/model.open/model.open.recent?after=model.open.recent.clearSeparator",
                "org.jamesii.toolbar.main/org.jamesii.open/model.open/model.open.recent?after=model.open.recent.clearSeparator" },
            null, null, null) {

          @Override
          public void execute() {
            if (JOptionPane
                .showConfirmDialog(getWindowManager().getMainWindow(),
                    "Do you really want to clear the list of recently opened models?") == JOptionPane.YES_OPTION) {
              History.removeIDfromHistory(RECENTLY_USED_ID);
            }
          }
        });

  }

  /**
   * Helper method that merges the basic actions list with the dynamic recently
   * opened model action list.
   * 
   * @return the merged actions
   */
  private List<IAction> getMergedActions() {
    List<IAction> actions = new ArrayList<>();
    actions.addAll(baseActions);
    actions.addAll(recentlyOpenedActions);

    return actions;
  }

}
