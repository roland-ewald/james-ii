/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.factories.PluginCreationException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.logging.ILogListener;
import org.jamesii.gui.application.AbstractApplicationInformation;
import org.jamesii.gui.application.ApplicationManager;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IApplication;
import org.jamesii.gui.application.IApplicationInformation;
import org.jamesii.gui.application.IApplicationManager;
import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowListener;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.MemoryMonitor;
import org.jamesii.gui.application.QuitAction;
import org.jamesii.gui.application.action.ActionIAction;
import org.jamesii.gui.application.action.ActionManager;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.autotask.AutoTaskManager;
import org.jamesii.gui.application.autotask.IAutoTask;
import org.jamesii.gui.application.james.dnd.DropableGlassPane;
import org.jamesii.gui.application.james.dnd.WindowDropRegion;
import org.jamesii.gui.application.preferences.Preferences;
import org.jamesii.gui.application.resource.ApplicationResourceManager;
import org.jamesii.gui.application.resource.BasicResources;
import org.jamesii.gui.application.resource.JamesResourceProviderFactory;
import org.jamesii.gui.application.resource.iconset.IIconSet;
import org.jamesii.gui.application.resource.iconset.IconSetManager;
import org.jamesii.gui.application.resource.iconset.plugintype.IconSetFactory;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.TaskManager;
import org.jamesii.gui.application.task.TaskMonitor;
import org.jamesii.gui.perspective.BackgroundTaskView;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.PerspectivesManager;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Main class for the James GUI. Implements {@link IApplication} for managing
 * the applications life cycle.
 * 
 * @author Stefan Rybacki
 */
public final class JamesGUI extends AbstractApplicationInformation implements
    IApplication, IWindowListener {

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class StatusBarMouseAdapter extends MouseAdapter {
    /**
     * 
     */
    private final TaskMonitor tm;

    /**
     * @param tm
     */
    private StatusBarMouseAdapter(TaskMonitor tm) {
      this.tm = tm;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      if (tm.isHide()) {
        tm.setCursor(Cursor.getDefaultCursor());
      } else {
        tm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (!tm.isHide()) {
        getWindowManager().addWindow(BackgroundTaskView.getInstance());
      }
    }
  }

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private static final class RestoreLookAndFeel implements Runnable {
    @Override
    public void run() {
      try {
        String lf = Preferences.get("LookAndFeel");
        if (lf != null) {
          UIManager.setLookAndFeel(lf);
        } else {
          // start with system look and feel
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e1) {
          SimSystem.report(e1);
        }
      } catch (ClassNotFoundException | InstantiationException
          | IllegalAccessException | UnsupportedLookAndFeelException e) {
        SimSystem.report(e);
      } finally {
        BasicUtilities.updateTreeUI();
      }
    }
  }

  private final class RunningAutoTasks extends AbstractTask {
    /**
     * @param taskName
     */
    private RunningAutoTasks(String taskName) {
      super(taskName);
    }

    @Override
    protected void task() {
      List<IAutoTask> autoTasks = AutoTaskManager.getAutoTasks();
      for (int i = 0; i < autoTasks.size(); i++) {
        try {
          IAutoTask task = autoTasks.get(i);
          setProgress((float) i / autoTasks.size());
          task.applicationStarted(getWindowManager());
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }
    }

    @Override
    protected void cancelTask() {
    }
  }

  /**
   * Message text for errors during starting up
   */
  private static final String COULDNT_START_EXITING =
      "Couldn't start Application. Exiting...";

  /**
   * The Constant CONFIRM_EXIT. Identifier in {@link Preferences} which
   * indicates whether the confirm dialog should be visible when closing
   * org.jamesii or not.
   */
  public static final String CONFIRM_EXIT = "org.jamesii.confirm.exit";

  /** Application manager. */
  private static IApplicationManager manager;

  /** Window manager. */
  private IWindowManager windowManager;

  /**
   * the window creator used to manage windows for the gui system delivered with
   * James II
   */
  private JamesMainComponent content;

  /** Configuration file name. */
  private static String configFile = "mainconfig.xml";

  /** Full path to configuration file. */
  private static String cFile = null;

  /**
   * security manager at application startup
   */
  private static SecurityManager originalSecurityManager = null;

  @Override
  public JComponent createContent() {
    windowManager.addWindowListener(this);

    return content;
  }

  @Override
  public JMenuBar createMenuBar() {
    JMenuBar menuBar = null;
    try {
      menuBar = ActionManager.createJMenuBarFor("org.jamesii.menu.main");
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      SimSystem.report(e);
    }

    return menuBar;
  }

  @Override
  public JToolBar createToolBar() {
    JToolBar toolBar;
    try {
      toolBar = ActionManager.createJToolBarFor("org.jamesii.toolbar.main");
      if (toolBar != null) {
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        toolBar.revalidate();
      }
      return toolBar;
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      SimSystem.report(e);
    }
    return null;
  }

  /**
   * @param args
   * @throws UnsupportedLookAndFeelException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   */
  public static void main(String[] args) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException,
      UnsupportedLookAndFeelException {

    String vers =
        System.getProperty("os.name").toLowerCase(Locale.getDefault());

    // only execute if we are actually on a mac system
    if (vers.contains("mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("com.apple.macos.smallTabs", "true");
      System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
      System.setProperty("com.apple.mrj.application.apple.menu.about.name",
          SimSystem.SIMSYSTEM + " " + SimSystem.VERSION);
    }

    if (!org.jamesii.core.util.BasicUtilities.checkJavaVersion("1.6")) {
      String msg =
          String.format("You are not running " + SimSystem.SIMSYSTEM
              + " from a sufficient JRE!\n" + SimSystem.SIMSYSTEM
              + " requires at least Java %s whereas you are running %s.",
              "1.6", System.getProperty("java.specification.version"));
      SimSystem.report(Level.SEVERE, msg);
      JOptionPane.showMessageDialog(null, msg, "Version conflict",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      Locale.setDefault(Locale.ENGLISH);
    } catch (SecurityException e) {
      SimSystem
          .report(Level.SEVERE, "Setting the locale to english failed.", e);
    }

    handleCmdLineArguments(args);

    originalSecurityManager = System.getSecurityManager();
    SecurityManager securityManager = new SecurityManager() {

      @Override
      public void checkPermission(Permission permission) {
        // This Prevents the shutting down of JVM.(in case of
        // System.exit())
        if (permission.getName().contains("exitVM")) {
          throw new SecurityException("System.exit attempted and blocked.");
          // TODO sr137: in case somebody (plugin) tries to exit org.jamesii
          // issue a
          // notification to org.jamesii main window and tell it to show
          // confirmation
          // dialog
        }
      }
    };
    System.setSecurityManager(securityManager);

    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

    // set global exception handler
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

      @Override
      public void uncaughtException(Thread t, Throwable e) {
        SimSystem.report(e);
      }

    });

    cFile = SimSystem.getConfigDirectory() + File.separator + configFile;

    // splash screen always with system look and feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | IllegalAccessException
        | InstantiationException | UnsupportedLookAndFeelException e) {
    }

    JamesGUI gui = new JamesGUI();
    manager = ApplicationManager.create(gui);

    if (manager == null) {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          JOptionPane.showMessageDialog(null, COULDNT_START_EXITING);
        }
      });
    } else {
      try {
        manager.start();
      } catch (Throwable e1) { // NOSONAR: if any exception during start reaches
                               // here we are trying to exit
        SimSystem.report(e1);
        BasicUtilities.invokeLaterOnEDT(new Runnable() {
          @Override
          public void run() {
            JOptionPane.showMessageDialog(null, COULDNT_START_EXITING);
            manager.close(true);
          }
        });
      }
    }
  }

  /**
   * Handles command-line arguments.
   * 
   * @param args
   *          the command line arguments
   */
  private static void handleCmdLineArguments(String[] args) {
    // TODO :generalise see:org.jamesii.core.cmdparameters
    if (args.length > 0) {
      Registry.setCustomPluginDirectory(args[0]);
    }
  }

  @Override
  public boolean canClose() {
    Boolean b = Preferences.get(CONFIRM_EXIT);
    if (b == null) {
      b = Boolean.TRUE;
    }

    if (!b.booleanValue()) {
      return true;
    }

    String options[] = { "Yes", "No", "Yes, don't ask again" };

    int value =
        JOptionPane.showOptionDialog(
            manager.getWindowManager().getMainWindow(),
            "Do you really want to quit?", "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // Use
            // default
            // icon
            // for
            // message
            // type
            options, options[1]);

    if (value == 2) {
      // means yes and don't ask again save this to preferences
      Preferences.put(CONFIRM_EXIT, Boolean.FALSE);
    }

    return value == 0 || value == 2;
  }

  /**
   * {@inheritDoc} If emergency is {@code true} no config file will be stored.
   */
  @Override
  public void exitingApplication(IProgressListener l, boolean emergency) {
    // try to install original security manager
    System.setSecurityManager(originalSecurityManager);

    if (!emergency) {
      // save look & feel selection
      try {
        if (l != null) {
          l.taskInfo(this, "Storing open perspectives...");
        }
        // store open perspectives
        List<IPerspective> availablePerspectives =
            PerspectivesManager.getAvailablePerspectives();
        for (IPerspective p : availablePerspectives) {
          Preferences.put(String.format("org.jamesii.perspectives.open.%s", p
              .getClass().getName()), Boolean.valueOf(PerspectivesManager
              .isOpen(p)));
        }

        if (l != null) {
          l.taskInfo(this, "Shutting down perspectives...");
        }
        // close all open perspectives
        PerspectivesManager.closeOpenPerspectives();

        if (l != null) {
          l.taskInfo(this, "Storing current Look&Feel...");
        }
        Preferences.put("LookAndFeel", UIManager.getLookAndFeel().getClass()
            .getName());

        if (l != null) {
          l.taskInfo(this, "Storing window state...");
        }
        Preferences.put("mainWindow.state",
            Integer.valueOf(windowManager.getMainWindow().getExtendedState()));

        if (l != null) {
          l.taskInfo(this, "Storing window size...");
        }
        windowManager.getMainWindow().setExtendedState(Frame.NORMAL);

        Preferences.put("mainWindow.size",
            ((WindowManager) windowManager).getSize());
        Preferences.put("mainWindow.position",
            ((WindowManager) windowManager).getPosition());

        Preferences.saveTo(cFile);
      } catch (Exception e) { // NOSONAR
        SimSystem.report(e);
      }
    }
  }

  @Override
  public Dimension getMainWindowSize() {
    return Preferences.get("mainWindow.size");
  }

  @Override
  public Point getMainWindowPosition() {
    return Preferences.get("mainWindow.position");
  }

  @Override
  public void started() {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        DropableGlassPane glassPane = new DropableGlassPane();

        glassPane.addDropRegion(new WindowDropRegion(null,
            Contribution.LEFT_VIEW, content) {

          @Override
          public Rectangle getBounds() {
            Rectangle bounds =
                getWindowManager().getMainWindow().getContentPane().getBounds();

            bounds.height = (bounds.height - bounds.y) * 2 / 3;
            bounds.width = bounds.width / 3;

            return bounds;
          }
        });

        glassPane.addDropRegion(new WindowDropRegion(null,
            Contribution.RIGHT_VIEW, content) {

          @Override
          public Rectangle getBounds() {
            Rectangle bounds =
                getWindowManager().getMainWindow().getContentPane().getBounds();

            bounds.height = (bounds.height - bounds.y) * 2 / 3;
            bounds.width = bounds.width / 3;
            bounds.x = bounds.x + bounds.width * 2;

            return bounds;
          }
        });

        glassPane.addDropRegion(new WindowDropRegion(null,
            Contribution.BOTTOM_VIEW, content) {

          @Override
          public Rectangle getBounds() {
            Rectangle bounds =
                getWindowManager().getMainWindow().getContentPane().getBounds();

            bounds.height = (bounds.height - bounds.y) / 3;
            bounds.y = bounds.y + bounds.height * 2;

            return bounds;
          }
        });

        glassPane
.addDropRegion(new WindowDropRegion(null, Contribution.EDITOR,
            content) {

              @Override
              public Rectangle getBounds() {
                Rectangle bounds =
                    getWindowManager().getMainWindow().getContentPane()
                        .getBounds();

                bounds.height = (bounds.height - bounds.y) * 2 / 3;

                return bounds;
              }
            });

        getWindowManager().getMainWindow().setGlassPane(glassPane);
      }

    });

    // now get all the autotasks and execute their applicationStarted
    // methods
    ITask autoTaskTask = new RunningAutoTasks("Running Autotasks");

    TaskManager.addTask(autoTaskTask);
  }

  @Override
  public boolean initialize(final IProgressListener l) {
    int taskCount = 4;
    int task = 0;

    l.taskInfo(this, "Loading PlugIns...");
    ILogListener logListener;
    ApplicationLogger.addLogListener(logListener = new ILogListener() {

      @Override
      public void publish(LogRecord record) {
        if (record.getLevel().intValue() <= Level.INFO.intValue()) {
          l.taskInfo(this, record.getMessage());
        }
      }

      @Override
      public void flush() {
      }

    });
    try {
      SimSystem.getRegistry();
    } catch (Throwable e) { // NOSONAR
      SimSystem.report(e);
      // if we reach here we better close the GUI
      JOptionPane.showMessageDialog(null,
          "Error while initializing JAMES II registry.\nExiting application.",
          "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    ApplicationLogger.removeLogListener(logListener);

    task++;

    // setting previous look & feel
    try {
      Preferences.loadFrom(cFile);
    } catch (Exception e) {
    }

    SwingUtilities.invokeLater(new RestoreLookAndFeel());

    l.taskInfo(this, "Creating Window Manager...");
    try {
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          content = new JamesMainComponent();
          if (content != null) {
            windowManager =
                new WindowManager(content, JamesGUI.this.getMainWindowSize(),
                    JamesGUI.this.getMainWindowPosition());
          }
        }
      });
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
      return false;
    }

    if (content == null || windowManager == null) {
      return false;
    }

    // setting resource provider factory to replace the basic factory
    // (note: if
    // some thing
    // already uses a resource provider only registered thru org.jamesii but
    // not
    // available thru
    // the basic resource factory the resources can't be loaded prior
    // this
    // point)
    l.taskInfo(this, "Setting up Resource Manager...");
    ApplicationResourceManager
        .setDefaultResourceProviderFactory(JamesResourceProviderFactory
            .getInstance());

    task++;

    l.taskInfo(this, "Setting up Default Icon Set...");
    initIconSet();

    task++;

    l.taskInfo(this, "Creating Perspectives...");
    ActionManager.registerAction(new ActionSet("org.jamesii.file", "File",
        "org.jamesii.menu.main?first", null));

    ActionManager.registerAction(new ActionIAction(new QuitAction(manager),
        "quit", new String[] { "org.jamesii.menu.main/org.jamesii.file?last" },
        null));

    initPerspectives(l, 1f / (taskCount) * task, 1f / (taskCount));

    task++;

    l.progress(this, 1.0f);

    return true;
  }

  /**
   * Helper method that initializes the icon set to use
   */
  private void initIconSet() {
    Registry registry = SimSystem.getRegistry();
    if (registry != null) {
      List<IconSetFactory> iconSetFactories =
          SimSystem.getRegistry().getFactories(IconSetFactory.class);

      if (iconSetFactories == null) {
        iconSetFactories = new ArrayList<>();
      }

      ParameterBlock params = new ParameterBlock();

      String iconSetClassName = Preferences.get("org.jamesii.iconset");

      for (IconSetFactory factory : iconSetFactories) {
        try {
          IIconSet iconSet = factory.create(params, SimSystem.getRegistry().createContext());
          // register icon set as available in the icon set manager
          IconSetManager.registerIconSet(iconSet);

          if (iconSetClassName == null) {
            IconSetManager.setDefaultIconSet(iconSet);
          }

          if (iconSet.getClass().getName().equals(iconSetClassName)) {
            IconSetManager.setDefaultIconSet(iconSet);
          }
        } catch (PluginCreationException e) {
          SimSystem.report(Level.WARNING, null,
              "Couldn't create icon set %s (%s)",
              new Object[] { factory.getName(), e.getMessage() });
        }

      }

      // TODO sr137: put into iconset manager
      Preferences.put("org.jamesii.iconset", IconSetManager.getIconSet()
          .getClass().getName());
    }
  }

  /**
   * Helper method that initializes the perspectives
   * 
   * @param l
   *          a progress listener
   * @param startProgress
   *          value from where (between 0 and 1) the progress is defined
   * @param progressScale
   *          value to where (between 0 and 1) the progress is defined
   */
  private void initPerspectives(IProgressListener l, float startProgress,
      float progressScale) {

    l.taskInfo(this, "Getting available perspectives...");
    List<IPerspective> availablePerspectives =
        PerspectivesManager.getAvailablePerspectives();

    int pCount = availablePerspectives.size();
    int r = -1;

    for (IPerspective perspective : availablePerspectives) {
      r++;
      l.progress(this, startProgress + ((float) r / pCount * progressScale));

      // if perspective is null or should not be displayed because the
      // user
      // doesn't want it to skip that perspective
      if (perspective != null && displayPerspective(perspective)) {
        l.taskInfo(this,
            String.format("Creating Perspective: %s", perspective.getName()));

        PerspectivesManager.openPerspective(perspective);
      }
    }
  }

  /**
   * Helper method that returns true if the specified perspective is to be
   * displayed or not. This can be the case if the perspective is mandatory or
   * was selected by the user.
   * 
   * @param perspective
   *          the perspective in question
   * @return true if perspective should be activated
   */
  private boolean displayPerspective(IPerspective perspective) {
    if (perspective == null) {
      return false;
    }

    Boolean open =
        Preferences.get(String.format("org.jamesii.perspectives.open.%s",
            perspective.getClass().getName()));
    return (perspective.isMandatory() || (open != null && open.booleanValue()));
  }

  @Override
  public Image getSplashImage() {
    return Toolkit.getDefaultToolkit().createImage(
        getClass().getResource(BasicResources.IMAGE_COSA_LOGO_PATH));
  }

  @Override
  public boolean showSplashScreen() {
    return true;
  }

  @Override
  public void deIconified() {
    // nothing to do here
  }

  @Override
  public void iconified() {
    // nothing to do here
  }

  @Override
  public int getMainWindowState() {
    Integer state = Preferences.get("mainWindow.state");
    if (state != null) {
      return ((Integer) Preferences.get("mainWindow.state")).intValue();
    }

    return Frame.NORMAL;
  }

  @Override
  public IApplicationInformation getApplicationInformation() {
    return this;
  }

  @Override
  public String getTitle() {
    return SimSystem.SIMSYSTEM + " " + SimSystem.VERSION;
  }

  @Override
  public String getVendor() {
    return "University of Rostock";
  }

  @Override
  public String getVersion() {
    return "GUI Version: " + super.getVersion();
  }

  @Override
  public int getVersionBuild() {
    return 5;
  }

  @Override
  public int getVersionMajor() {
    return 0;
  }

  @Override
  public int getVersionMinor() {
    return 1;
  }

  @Override
  public int getVersionPatchLevel() {
    return 0;
  }

  @Override
  public IWindowManager getWindowManager() {
    return windowManager;
  }

  @Override
  public void windowActivated(IWindow window) {
    // nothing to do here

  }

  @Override
  public void windowClosed(IWindow window) {
    // nothing to do here

  }

  @Override
  public void windowDeactivated(IWindow window) {
    // nothing to do here

  }

  @Override
  public void windowOpened(IWindow window) {
    // nothing to do here
  }

  @Override
  public JComponent getStatusBar() {
    Box hBox = Box.createHorizontalBox();
    final TaskMonitor tm = new TaskMonitor();
    MouseAdapter mA = new StatusBarMouseAdapter(tm);
    tm.addMouseMotionListener(mA);
    tm.addMouseListener(mA);

    Dimension prefSize = tm.getPreferredSize();
    tm.setPreferredSize(new Dimension(450, prefSize.height));
    tm.setMaximumSize(tm.getPreferredSize());
    hBox.add(Box.createHorizontalGlue());
    hBox.add(tm);
    hBox.add(Box.createHorizontalStrut(5));
    hBox.add(new MemoryMonitor());

    return hBox;
  }

}
