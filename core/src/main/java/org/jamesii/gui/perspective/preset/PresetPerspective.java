/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.preset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jamesii.gui.application.ProgressDialog;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.SeparatorAction;
import org.jamesii.gui.application.preferences.IPreferencesPage;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.perspective.AbstractPerspective;
import org.jamesii.gui.perspective.IPerspectivePresetChangeListener;
import org.jamesii.gui.perspective.PerspectivePreset;
import org.jamesii.gui.perspective.PerspectivePresetManager;

/**
 * Perspective providing direct access to {@link PerspectivePreset}.
 * 
 * @author Stefan Rybacki
 */
class PresetPerspective extends AbstractPerspective implements
    IPerspectivePresetChangeListener {
  /**
   * an atomic counter used to generate unique ids for preset actions in
   * {@link #generateActions()}
   */
  private final AtomicInteger counter = new AtomicInteger();

  /**
   * maps actions to presets
   */
  private final Map<PerspectivePreset, IAction> presetActions = new HashMap<>();

  /**
   * stores generated actions
   */
  private final List<IAction> actions = new ArrayList<>();

  /**
   * Instantiates a new preset perspective.
   */
  public PresetPerspective() {
    super();
    PerspectivePresetManager.addPresetChangeListener(this);
  }

  @Override
  public String getDescription() {
    return getName();
  }

  @Override
  public String getName() {
    return "Perspective Preset Perspective";
  }

  @Override
  protected List<IAction> generateActions() {
    List<PerspectivePreset> presets =
        PerspectivePresetManager.getAvailablePresets();

    if (presets != null) {
      actions.add(new ActionSet("org.jamesii.presets", "Perspective Presets",
          "org.jamesii.menu.main/org.jamesii.edit?last", null));
      actions.add(SeparatorAction.getSeparatorFor(
          "org.jamesii.menu.main/org.jamesii.edit?before=org.jamesii.presets",
          null));

      Collections.sort(presets, new PerspectivePresetComparator());
      for (final PerspectivePreset p : presets) {
        addPreset(p);
      }
    }

    return actions;
  }

  /**
   * Helper method that generates an {@link IAction} for the given preset.
   * 
   * @param p
   *          the preset an {@link IAction} should be created for
   * @return the generated {@link IAction}
   */
  private IAction createActionForPreset(final PerspectivePreset p) {
    return new AbstractAction(
        "org.jamesii.perspectives.presets." + counter.incrementAndGet(),
        p.getName(),
        new String[] { "org.jamesii.menu.main/org.jamesii.edit/org.jamesii.presets" },
        null) {

      @Override
      public void execute() {
        // simply activate and deactive perspectives according to the preset
        // show progress window
        // put this into ITask and let the TaskManager take care of execution
        ITask task = new AbstractTask("Applying perspective changes") {

          @Override
          protected void task() {
            setTaskInfo(getName() + "...");
            PerspectivePresetManager.setPreset(p);
          }

          @Override
          public boolean isBlocking() {
            return true;
          }

          @Override
          protected void cancelTask() {
          }
        };

        ProgressDialog.runTask(task);
      }
    };
  }

  /**
   * Adds a preset.
   * 
   * @param p
   *          the preset to add
   */
  private void addPreset(PerspectivePreset p) {
    IAction a = createActionForPreset(p);
    actions.add(a);
    presetActions.put(p, a);
  }

  @Override
  public List<IPreferencesPage> getPreferencesPages() {
    List<IPreferencesPage> pages = new ArrayList<>();
    return pages;
  }

  @Override
  public boolean isMandatory() {
    return true;
  }

  @Override
  public void presetAdded(PerspectivePreset p) {
    // find action for preset and add action to actions list and notify
    // listeners
    addPreset(p);
    fireActionsChanged(actions);
  }

  @Override
  public void presetDeleted(PerspectivePreset p) {
    // find action for preset and remove action from actions list and notify
    // listeners
    actions.remove(presetActions.get(p));
    fireActionsChanged(actions);
  }
}
