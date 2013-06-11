/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.factories.PluginCreationException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.IPerspectiveChangeListener;
import org.jamesii.gui.application.action.ActionManager;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Class to manage through James plugin system available perspectives. Use this
 * class to open and close perspectives as well as retrieve available
 * perspectives or to check whether a perspective is open.
 * 
 * @author Stefan Rybacki
 */
public final class PerspectivesManager implements IPerspectiveChangeListener {
  /**
   * list of currently open perspectives
   */
  private final List<IPerspective> openPerspectives = new ArrayList<>();

  /**
   * list of all through the plug-in system available perspectives
   */
  private final List<IPerspective> availablePerspectives = new ArrayList<>();

  /**
   * stores with perspective associated actions for later removal on actions
   * change (used so we don't rely on properly provided old actions when
   * {@link #perspectiveActionsChanged(IPerspective, IAction[]) is called)}
   */
  private final Map<IPerspective, IAction[]> actionsForPerspective =
      new HashMap<>();

  /**
   * singleton instance of the manager
   */
  private static final PerspectivesManager INSTANCE = new PerspectivesManager();

  /**
   * Constructor omitted
   */
  private PerspectivesManager() {
    initPerspectives();
  }

  @Override
  public void perspectiveActionsChanged(IPerspective perspective,
      IAction[] oldActions) {
    // remove old actions (don't rely on provided old actions)

    IAction[] oActions = actionsForPerspective.get(perspective);
    if (oActions != null) {
      ActionManager.remove(oActions);
    }

    // add new actions
    List<IAction> actions = perspective.getActions();
    if (actions != null) {
      actionsForPerspective.put(perspective,
          actions.toArray(new IAction[actions.size()]));
      for (IAction a : actions) {
        if (a != null) {
          ActionManager.registerAction(a);
        }
      }
    } else {
      actionsForPerspective.put(perspective, null);
    }
  }

  /**
   * helper function that initializes the list of available perspectives
   */
  private synchronized void initPerspectives() {
    // search for plugins implementing PerspectiveFactory
    Registry registry = SimSystem.getRegistry();

    if (registry != null) {
      List<PerspectiveFactory> perspFactories = null;
      perspFactories =
          SimSystem.getRegistry().getFactories(PerspectiveFactory.class);
      if (perspFactories == null) {
        perspFactories = new ArrayList<>();
      }

      ParameterBlock perspParams = new ParameterBlock();

      for (PerspectiveFactory factory : perspFactories) {
        IPerspective perspective = null;
        try {
          perspective = factory.create(perspParams);
          availablePerspectives.add(perspective);
        } catch (PluginCreationException e) {
          SimSystem.report(Level.WARNING, null,
              "Was not able to create Perspective: %s (%s)", new Object[] {
                  perspParams, e.getMessage() });
        }
      }
    }
  }

  /**
   * Closes the specified perspective it is open and in the list of available
   * perspectives. After closing the perspective
   * {@link IPerspective#perspectiveClosed()} is issued as well as the
   * registered actions are removed.
   * 
   * @see #openPerspective(IPerspective)
   * @param p
   *          the perspective to close
   */
  public static synchronized void closePerspective(final IPerspective p) {
    if (!INSTANCE.openPerspectives.contains(p) || p == null) {
      return;
    }

    try {
      INSTANCE.openPerspectives.remove(p);
      p.perspectiveClosed();
      p.removePerspectiveChangeListener(INSTANCE);

      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          // remove actions from action manager
          IAction[] actions = INSTANCE.actionsForPerspective.get(p);
          if (actions != null) {
            ActionManager.remove(actions);
          }
        }
      });
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    } catch (Exception e) {
      SimSystem.report(Level.WARNING, null,
          "Exception while closing perspective %s (%s)",
          new Object[] { p.getName(), e.getMessage() });
    }

  }

  /**
   * Opens the specified perspective if it is in the list of available
   * perspectives. After opening {@link IPerspective#openPerspective()} is
   * called and the actions are registered in the {@link ActionManager}.
   * 
   * @see #closePerspective(IPerspective)
   * @see #closeOpenPerspectives()
   * @param p
   *          the perspective to open
   */
  public static synchronized void openPerspective(final IPerspective p) {
    if (INSTANCE.openPerspectives.contains(p) || p == null) {
      return;
    }

    p.addPerspectiveChangeListener(INSTANCE);
    try {
      p.openPerspective();
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          // add actions to action manager
          List<IAction> actions = p.getActions();
          if (actions != null) {
            INSTANCE.actionsForPerspective.put(p,
                actions.toArray(new IAction[actions.size()]));
            for (IAction a : actions) {
              if (a != null) {
                ActionManager.registerAction(a);
              }
            }
          } else {
            INSTANCE.actionsForPerspective.put(p, null);
          }
        }
      });
      INSTANCE.openPerspectives.add(p);
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    } catch (Exception e) {
      SimSystem.report(Level.WARNING, null,
          "Couldn't open perspective %s (%s)",
          new Object[] { p.getName(), e.getMessage() });
    }

  }

  /**
   * @return a list of all currently open perspectives
   */
  public static synchronized List<IPerspective> getOpenPerspectives() {
    // return defensive copy
    return new ArrayList<>(INSTANCE.openPerspectives);
  }

  /**
   * @return a list of all currently available perspectives
   */
  public static synchronized List<IPerspective> getAvailablePerspectives() {
    // return defensive copy
    return new ArrayList<>(INSTANCE.availablePerspectives);
  }

  /**
   * @param perspective
   *          the perspective to check for open
   * @return true if the specified perspective is open
   */
  public static synchronized boolean isOpen(IPerspective perspective) {
    return INSTANCE.openPerspectives.contains(perspective);
  }

  /**
   * Closes all currently open perspectives. Attention: this also closes
   * mandatory open perspectives and is only thought to be called when exiting
   * the application so that all perspectives can clean up after themselfs.
   */
  public static synchronized void closeOpenPerspectives() {
    for (int i = INSTANCE.openPerspectives.size() - 1; i >= 0; i--) {
      try {
        closePerspective(INSTANCE.openPerspectives.get(i));
      } catch (Exception e) {
        SimSystem.report(Level.WARNING, null,
            "Couldn't close perspective %s (%s)", new Object[] {
                INSTANCE.openPerspectives.get(i).getName(), e.getMessage() });
      }
    }
  }

  /**
   * Helper function that returns an {@link IPerspective} instance for a given
   * class name this way it is not needed to create multiple instances of a
   * single perspective.
   * 
   * @param name
   *          the class name of the desired perspective
   * @return {@code null} if no perspective with the given class name can be
   *         found or the perspective if found
   */
  public static synchronized IPerspective getPerspectiveForClassName(String name) {
    if (name == null) {
      return null;
    }

    for (IPerspective p : INSTANCE.availablePerspectives) {
      if (name.equals(p.getClass().getName())) {
        return p;
      }
    }
    return null;
  }

}
