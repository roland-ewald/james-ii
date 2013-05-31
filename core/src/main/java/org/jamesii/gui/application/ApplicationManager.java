/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Main application class that simply sets up a JFrame with Menubar, Statusbar
 * and Toolbar using the given class that implements {@link IApplication}.
 * Provides also a window, task and resource manager.
 * 
 * @author Stefan Rybacki
 */

public final class ApplicationManager implements WindowListener,
    IApplicationManager {

  private static final int TIMERPERIOD = 1000;

  private static final int TIMERDELAY = 1000;

  private static final int STANDARDWINDOWHEIGHT = 600;

  private static final int STANDARDWINDOWWIDTH = 800;

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private static final class DeadLockDetectionTimerTask extends TimerTask {
    @Override
    public void run() {
      ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
      long[] ids = tmx.findDeadlockedThreads();
      if (ids != null) {
        ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
        StringBuilder text = new StringBuilder();
        text.append("The following threads are deadlocked:\n");
        for (ThreadInfo ti : infos) {
          text.append(ti);
          text.append("\n");
        }
        SimSystem.report(Level.SEVERE, text.toString());
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
  private final class ExitApplicationTask extends AbstractTask {

    private final boolean emergency;

    /**
     * @param taskName
     * @param emergency
     */
    private ExitApplicationTask(String taskName, boolean emergency) {
      super(taskName);
      this.emergency = emergency;
    }

    @Override
    protected void task() {
      try {
        application.exitingApplication(this, emergency);
        getWindowManager().exitingApplication();
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }

    @Override
    public void cancelTask() {
      // nothing to do
    }

    @Override
    public boolean isBlocking() {
      return true;
    }
  }

  /**
   * application's main window
   */
  private JFrame mainFrame;

  /**
   * splash screen
   */
  private SplashScreen splashScreen;

  /**
   * application instance
   */
  private IApplication application = null;

  /**
   * Hidden constructor used by static method {@link #create(IApplication)}
   * 
   * @param app
   *          the application instance to use
   * @param showTitle
   *          flag indicating whether to show the application title in splash
   *          screen or not
   * @throws InterruptedException
   * @throws InvocationTargetException
   */
  private ApplicationManager(IApplication app, final boolean showTitle)
      throws InterruptedException, InvocationTargetException {
    super();

    // start deadlock detector timer
    Timer deadLockTimer = new Timer(true);
    deadLockTimer.schedule(new DeadLockDetectionTimerTask(), TIMERDELAY,
        TIMERPERIOD);

    if (app == null) {
      throw new IllegalArgumentException("Application parameter can't be null!");
    }
    application = app;
    BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        // create splash screen
        Image image = null;
        try {
          image = application.getSplashImage();
        } catch (Exception e) {
          SimSystem.report(e);
        }

        splashScreen =
            new SplashScreen(image, application.getApplicationInformation()
                .getTitle(), application.getApplicationInformation()
                .getVersion(), application.getApplicationInformation()
                .getVendor(), showTitle);
      }
    });
  }

  /**
   * starts the application by showing a splashscreen and initializing the
   * application and eventually by displaying the generated main window.
   */
  @Override
  public void start() {
    if (application.showSplashScreen()) {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          splashScreen.setVisible(true);
          splashScreen.pack();
        }
      });
    }

    splashScreen.taskInfo(this, "Initializing Application...");

    boolean init = false;
    try {
      init = application.initialize(splashScreen);
    } catch (Exception e) {
      SimSystem.report(e);
    }

    if (!init) {
      close(true);
    } else {
      // create main form
      try {
        BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
          @Override
          public void run() {
            initAndShowMainWindow();
          }
        });
      } catch (InterruptedException | InvocationTargetException e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Helper method to put UI initialization in EDT
   */
  private void initAndShowMainWindow() {
    splashScreen.taskInfo(this, "Creating Main Window...");

    mainFrame = new JFrame(application.getApplicationInformation().getTitle());
    mainFrame.getContentPane().setLayout(new BorderLayout());

    getWindowManager().setMainFrame(mainFrame);

    JComponent content = null;
    try {
      content = application.createContent();
    } catch (Exception e) {
      SimSystem.report(e);
    }
    if (content != null) {
      mainFrame.getContentPane().add(content, BorderLayout.CENTER);
    }

    splashScreen.taskInfo(this, "Creating Menus...");
    JMenuBar mb = null;
    try {
      mb = application.createMenuBar();
    } catch (Exception e) {
      SimSystem.report(e);
    }
    if (mb != null) {
      mainFrame.setJMenuBar(mb);
    }

    splashScreen.taskInfo(this, "Creating Toolbar...");
    JToolBar tb = null;
    try {
      tb = application.createToolBar();
    } catch (Exception e) {
      SimSystem.report(e);
    }
    if (tb != null) {
      mainFrame.getContentPane().add(tb, BorderLayout.PAGE_START);
    }

    Point mainWindowPosition = application.getMainWindowPosition();
    // the screen the window resides on
    GraphicsDevice screen =
        GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();

    if (mainWindowPosition != null) {
      // check whether window position is on a screen and not outside
      // of the screen space
      boolean onScreen = false;
      for (GraphicsDevice d : GraphicsEnvironment.getLocalGraphicsEnvironment()
          .getScreenDevices()) {
        Rectangle bounds = d.getDefaultConfiguration().getBounds();
        Insets insets =
            Toolkit.getDefaultToolkit().getScreenInsets(
                d.getDefaultConfiguration());

        if (mainWindowPosition.x >= bounds.x + insets.left
            && mainWindowPosition.y >= bounds.y + insets.top
            && mainWindowPosition.x < bounds.x + bounds.width - insets.right
            && mainWindowPosition.y < bounds.y + bounds.height - insets.bottom) {
          screen = d;
          onScreen = true;
          break;
        }
      }

      if (!onScreen) {
        mainWindowPosition = null;
      }
    }

    Dimension mainWindowSize = application.getMainWindowSize();
    Rectangle bounds = screen.getDefaultConfiguration().getBounds();
    Insets insets =
        Toolkit.getDefaultToolkit().getScreenInsets(
            screen.getDefaultConfiguration());

    if (mainWindowSize != null) {
      // check whether the window size is smaller or equal to the
      // residing screen size
      mainWindowSize.width =
          Math.min(bounds.width - insets.left - insets.right,
              mainWindowSize.width);
      mainWindowSize.height =
          Math.min(bounds.height - insets.bottom - insets.top,
              mainWindowSize.height);
    } else {
      mainWindowSize =
          new Dimension(Math.min(STANDARDWINDOWWIDTH, bounds.width
              - insets.left - insets.right), Math.min(STANDARDWINDOWHEIGHT,
              bounds.height - insets.bottom - insets.top));
    }

    mainFrame.setSize(mainWindowSize);

    if (mainWindowPosition == null) {
      mainFrame.setLocationRelativeTo(null);
    } else {
      mainFrame.setLocation(mainWindowPosition);
    }

    mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    mainFrame.setExtendedState(application.getMainWindowState());

    mainFrame.addWindowListener(this);

    // also add a status bar showing the current available and used
    // memory
    Box statusBar = Box.createHorizontalBox();
    try {
      JComponent status = application.getStatusBar();
      if (status != null) {
        statusBar.add(status);
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }

    mainFrame.add(statusBar, BorderLayout.SOUTH);

    mainFrame.setVisible(true);
  }

  /**
   * Tries to close the application. This can be denied by implementing
   * {@link IApplication#canClose()}
   */
  @Override
  public void close(final boolean emergency) {
    if (emergency || application.canClose()) {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          if (mainFrame != null) {
            mainFrame.setVisible(false);
            mainFrame.dispose();
          }
        }
      });

      final ITask task =
          new ExitApplicationTask("Exiting Application", emergency);

      new Thread(new Runnable() {
        @Override
        public void run() {
          ProgressDialog.runTaskAndWait(task);
          SimSystem.shutDown(0);
        }
      }).start();
    }
  }

  @Override
  public void windowActivated(WindowEvent e) {
    // nothing to do
  }

  @Override
  public void windowClosed(WindowEvent e) {
    // nothing to do
  }

  @Override
  public void windowClosing(WindowEvent e) {
    close(false);
  }

  @Override
  public void windowDeactivated(WindowEvent e) {
    // nothing to do
  }

  @Override
  public void windowDeiconified(WindowEvent e) {
    application.deIconified();
  }

  @Override
  public void windowIconified(WindowEvent e) {
    application.iconified();
  }

  @Override
  public void windowOpened(WindowEvent e) {
    // close splash screen one second after the main window is shown
    if (splashScreen.isVisible() && application.showSplashScreen()) {
      Timer timer = new Timer(true);

      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          cancel();
          BasicUtilities.invokeLaterOnEDT(new Runnable() {
            @Override
            public void run() {
              splashScreen.setVisible(false);
              splashScreen.dispose();
            }
          });
        }
      }, 500, 1000);
    }

    application.started();
  }

  /**
   * Main method for this class that creates an {@link IApplicationManager}
   * instance for the given {@link IApplication} object.
   * 
   * @param app
   *          the application instance to use
   * @return an {@link IApplicationManager} instance to call
   *         {@link IApplicationManager#start()} and
   *         {@link IApplicationManager#close(boolean)} on
   */
  public static IApplicationManager create(IApplication app) {
    IApplicationManager m = null;
    try {
      m = new ApplicationManager(app, true);
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    }
    ApplicationManagerManager.setApplicationManager(m);
    return m;
  }

  @Override
  public IApplication getApplication() {
    return application;
  }

  @Override
  public IWindowManager getWindowManager() {
    return application.getWindowManager();
  }

}