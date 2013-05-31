/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionIAction;
import org.jamesii.gui.application.action.ActionManager;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.SeparatorAction;
import org.jamesii.gui.application.james.AboutDialog;
import org.jamesii.gui.application.james.PlugInView;
import org.jamesii.gui.application.logging.LogView;
import org.jamesii.gui.application.preferences.IPreferencesPage;
import org.jamesii.gui.application.preferences.PreferencesDialog;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.application.resource.iconset.IconSetManager;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Default mandatory perspective providing basic functionality in JAMES II GUI
 * like preferences, help, quit ...
 * 
 * @author Stefan Rybacki
 */
final class DefaultPerspective extends AbstractPerspective {

  /** The Constant instance. */
  private static final DefaultPerspective instance = new DefaultPerspective();

  /**
   * Instantiates a new default perspective.
   */
  private DefaultPerspective() {
    IconSetManager.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        BasicUtilities.invokeLaterOnEDT(new Runnable() {

          @Override
          public void run() {
            // update all windows and components
            ActionManager.updateUI();
            for (Frame f : Frame.getFrames()) {
              SwingUtilities.updateComponentTreeUI(f);
            }
            for (Window w : Window.getWindows()) {
              SwingUtilities.updateComponentTreeUI(w);
            }
          }

        });
      }
    });
  }

  @Override
  public String getDescription() {
    return "Standard Mandatory Default Perspective";
  }

  @Override
  public String getName() {
    return "Default Perspective";
  }

  @Override
  protected List<IAction> generateActions() {
    final IWindowManager windowManager = getWindowManager();

    final List<IAction> actions = new ArrayList<>();
    actions.add(new ActionSet("org.jamesii.edit", "Edit",
        "org.jamesii.menu.main?after=org.jamesii.file", null));
    actions
        .add(new AbstractAction(
            "org.jamesii.preferences",
            "Preferences...",
            new String[] { "org.jamesii.menu.main/org.jamesii.edit?after=org.jamesii.edit.additionals" },
            null) {
          @Override
          public void execute() {
            PreferencesDialog.showPreferencesDialog(windowManager);
          }
        });

    Icon newIcon = null;
    newIcon = IconManager.getIcon(IconIdentifier.NEW_SMALL, "New");

    actions.add(new ActionSet("org.jamesii.new", "New", new String[] {
        "org.jamesii.menu.main/org.jamesii.file?first",
        "org.jamesii.toolbar.main?first" }, null, null, newIcon, null));

    Icon openIcon = null;
    openIcon = IconManager.getIcon(IconIdentifier.OPEN_SMALL, "Open");

    actions.add(new ActionSet("org.jamesii.open", "Open", new String[] {
        "org.jamesii.menu.main/org.jamesii.file?after=org.jamesii.new",
        "org.jamesii.toolbar.main?after=org.jamesii.new" }, null, null,
        openIcon, null));

    Icon saveIcon = null;
    saveIcon = IconManager.getIcon(IconIdentifier.SAVE_SMALL, "Save");

    actions.add(new SaveAction("org.jamesii.save", "Save", windowManager,
        saveIcon, new String[] {
            "org.jamesii.menu.main/org.jamesii.file?after=org.jamesii.open",
            "org.jamesii.toolbar.main?after=org.jamesii.open" }, KeyStroke
            .getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
            .toString(), Integer.valueOf(KeyEvent.VK_S), null));

    Icon saveAsIcon = null;
    saveAsIcon = IconManager.getIcon(IconIdentifier.SAVE_SMALL, "Save As...");
    actions
        .add(new SaveAsAction(
            "org.jamesii.saveas",
            "Save As...",
            windowManager,
            saveAsIcon,
            new String[] { "org.jamesii.menu.main/org.jamesii.file?after=org.jamesii.save" },
            null, null, null));

    actions
        .add(new SeparatorAction(
            "org.jamesii.file.additionals1",
            new String[] { "org.jamesii.menu.main/org.jamesii.file?after=org.jamesii.saveas" },
            null));

    actions.add(new SeparatorAction("org.jamesii.toolbar.edit",
        new String[] { "org.jamesii.toolbar.main?after=org.jamesii.save" },
        null));

    actions.add(new ActionIAction(new UndoAction(windowManager),
        "org.jamesii.undo", new String[] {
            "org.jamesii.toolbar.main?after=org.jamesii.toolbar.edit",
            "org.jamesii.menu.main/org.jamesii.edit?first" }, null));

    actions.add(new ActionIAction(new RedoAction(windowManager),
        "org.jamesii.redo", new String[] {
            "org.jamesii.toolbar.main?after=org.jamesii.undo",
            "org.jamesii.menu.main/org.jamesii.edit?after=org.jamesii.undo" },
        null));

    actions.add(new SeparatorAction("org.jamesii.toolbar.additionals",
        new String[] { "org.jamesii.toolbar.main?after=org.jamesii.redo" },
        null));
    actions
        .add(new SeparatorAction(
            "org.jamesii.edit.additionals",
            new String[] { "org.jamesii.menu.main/org.jamesii.edit?after=org.jamesii.redo" },
            null));

    actions.add(new ActionSet("org.jamesii.help", "Help",
        "org.jamesii.menu.main?last", null));

    actions.add(new AbstractAction("org.jamesii.showSystemInfo",
        "Show System Information",
        new String[] { "org.jamesii.menu.main/org.jamesii.help?first" }, null) {

      @Override
      public void execute() {
        BasicUtilities.invokeLaterOnEDT(new Runnable() {
          @Override
          public void run() {
            JavaInfo info = new JavaInfo();
            windowManager.addWindow(new SystemInfoView(info, "this system",
                Contribution.DIALOG));
          }
        });
      }

    });

    actions
        .add(new AbstractAction(
            "org.jamesii.showPlugInInfo",
            "Inspect PlugIns",
            new String[] { "org.jamesii.menu.main/org.jamesii.help?after=org.jamesii.showSystemInfo" },
            null) {

          @Override
          public void execute() {
            BasicUtilities.invokeLaterOnEDT(new Runnable() {
              @Override
              public void run() {
                windowManager.addWindow(new PlugInView(Contribution.DIALOG));
              }
            });
          }

        });

    actions
        .add(new AbstractAction(
            "org.jamesii.showLogView",
            "Show Log View",
            new String[] { "org.jamesii.menu.main/org.jamesii.help?after=org.jamesii.showPlugInInfo" },
            null) {

          @Override
          public void execute() {
            BasicUtilities.invokeLaterOnEDT(new Runnable() {
              @Override
              public void run() {
                windowManager.addWindow(LogView.getInstance());
              }
            });
          }

        });

    actions.add(new ActionIAction(new HelpAction(), "help",
        new String[] { "org.jamesii.menu.main/org.jamesii.help" }, null));

    actions
        .add(new AbstractAction(
            "org.jamesii.showBackgroundTaskManager",
            "Show Background Tasks",
            new String[] { "org.jamesii.menu.main/org.jamesii.help?after=org.jamesii.showLogView" },
            null) {

          @Override
          public void execute() {
            BasicUtilities.invokeLaterOnEDT(new Runnable() {
              @Override
              public void run() {
                windowManager.addWindow(BackgroundTaskView.getInstance());
              }
            });
          }

        });

    actions.add(new AbstractAction("org.jamesii.help.about", "About...",
        new String[] { "org.jamesii.menu.main/org.jamesii.help?last" }, null) {

      @Override
      public void execute() {
        BasicUtilities.invokeLaterOnEDT(new Runnable() {
          @Override
          public void run() {
            AboutDialog aboutDialog =
                new AboutDialog(getWindowManager().getMainWindow());
            aboutDialog.setLocationRelativeTo(getWindowManager()
                .getMainWindow());
            aboutDialog.setVisible(true);
          }
        });
      }

    });

    return actions;
  }

  @Override
  public List<IPreferencesPage> getPreferencesPages() {
    List<IPreferencesPage> pages = new ArrayList<>();
    pages.add(new DefaultPreferences());

    return pages;
  }

  @Override
  public boolean isMandatory() {
    return true;
  }

  /**
   * Gets the singleton instance of {@link DefaultPerspective}.
   * 
   * @return singleton instance of {@link DefaultPerspective}
   */
  public static IPerspective getInstance() {
    return instance;
  }

}
