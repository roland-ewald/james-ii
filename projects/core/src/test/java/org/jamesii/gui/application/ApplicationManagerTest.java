/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 * 
 */
public class ApplicationManagerTest extends TestCase {

  /**
   * A {@link IApplication} implementation for testing purposes basically
   * providing information of called methods.
   */
  static class TestApp extends AbstractApplicationInformation implements
      IApplication {

    /**
     * The create content.
     */
    private boolean createContent = false;

    /**
     * The create menu bar.
     */
    private boolean createMenuBar = false;

    /**
     * The create tool bar.
     */
    private boolean createToolBar = false;

    /**
     * The exiting application.
     */
    private boolean exitingApplication = false;

    /**
     * The can close.
     */
    private boolean canClose = false;

    /**
     * The get main window size.
     */
    private boolean getMainWindowSize = false;

    /**
     * The get main window state.
     */
    private boolean getMainWindowState = false;

    /**
     * The started.
     */
    private boolean started = false;

    /**
     * The get splash image.
     */
    private boolean getSplashImage = false;

    /**
     * The initialize.
     */
    private boolean initialize = false;

    /**
     * The show splash screen.
     */
    private boolean showSplashScreen = false;

    @Override
    public boolean canClose() {
      canClose = true;
      return true;
    }

    @Override
    public JComponent createContent() {
      // initialize must be called first
      assertTrue(initialize);
      createContent = true;
      return null;
    }

    @Override
    public JMenuBar createMenuBar() {
      // initialize must be called first
      assertTrue(initialize);
      createMenuBar = true;
      return null;
    }

    @Override
    public JToolBar createToolBar() {
      // initialize must be called first
      assertTrue(initialize);
      createToolBar = true;
      return null;
    }

    @Override
    public void deIconified() {
    }

    @Override
    public void exitingApplication(IProgressListener l, boolean a) {
      // canClose must be called first
      assertTrue(canClose);
      exitingApplication = true;
    }

    @Override
    public Dimension getMainWindowSize() {
      // initialize must be called first
      assertTrue(initialize);
      getMainWindowSize = true;
      return null;
    }

    @Override
    public int getMainWindowState() {
      // initialize must be called first
      assertTrue(initialize);
      getMainWindowState = true;
      return 0;
    }

    @Override
    public Image getSplashImage() {
      getSplashImage = true;
      return null;
    }

    @Override
    public void iconified() {
    }

    @Override
    public boolean initialize(IProgressListener progress) {
      // showSplashScreen must be called first
      assertTrue(showSplashScreen);
      initialize = true;
      try {
        Thread.sleep(4000);
      } catch (InterruptedException e) {
        fail(e.getMessage());
      }
      return true;
    }

    @Override
    public boolean showSplashScreen() {
      showSplashScreen = true;
      return true;
    }

    @Override
    public synchronized void started() {
      assertTrue(initialize);
      assertTrue(showSplashScreen);
      assertTrue(createMenuBar);
      assertTrue(createToolBar);
      assertTrue(createContent);
      started = true;
    }

    @Override
    public IApplicationInformation getApplicationInformation() {
      return this;
    }

    @Override
    public String getTitle() {
      return "JUnit TestCase";
    }

    @Override
    public String getVendor() {
      return "University of Rostock";
    }

    @Override
    public int getVersionBuild() {
      return 10;
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
      return new AbstractWindowManager(new Dimension(100, 100), new Point(0, 0)) {

        @Override
        protected Component getContainerOfWindow(IWindow window) {
          return null;
        }

        @Override
        protected void windowActivated(IWindow window) {
        }

        @Override
        protected void windowAdded(IWindow window) {
        }

        @Override
        protected void windowClosed(IWindow window) {
        }

        @Override
        public String getRelativeURLFor(IWindow window) {
          return null;
        }

        @Override
        public JComponent createContainer(IWindow window) {
          return null;
        }

        @Override
        protected void removeRelativeURLFor(IWindow window) {
        }

      };
    }

    @Override
    public JComponent getStatusBar() {
      return null;
    }

    @Override
    public Point getMainWindowPosition() {
      // TODO Auto-generated method stub
      return null;
    }

  }

  /**
   * The semaphore.
   */
  private java.util.concurrent.Semaphore semaphore;

  /**
   * The security exception.
   */
  private SecurityException securityException;

  private boolean headless = false;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    headless = GraphicsEnvironment.isHeadless();
  }

  /**
   * Test uses all interface methods. Testing the order or at least the amount
   * of called methods of {@link IApplication}.
   */
  public final void testUsesAllInterfaceMethods() {
    if (headless) {
      return;
    }

    final TestApp app = new TestApp();
    IApplicationManager manager = ApplicationManager.create(app);
    try {
      manager.start();
    } catch (Throwable e2) {
      e2.printStackTrace();
      fail(e2.getMessage());
    }
    long start = System.currentTimeMillis();
    // wait until start or 60 seconds max
    while (!app.started && System.currentTimeMillis() - start < 60000) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        fail(e.getMessage());
      }
    }
    // check called methods
    assertTrue(app.started);
    assertTrue(app.createContent);
    assertTrue(app.createMenuBar);
    assertTrue(app.createToolBar);
    assertTrue(app.getMainWindowSize);
    assertTrue(app.getMainWindowState);
    assertTrue(app.getSplashImage);
    assertTrue(app.initialize);
    assertTrue(app.showSplashScreen);

    SecurityManager sm = System.getSecurityManager();
    SecurityManager securityManager = new SecurityManager() {

      @Override
      public void checkPermission(Permission permission) {
        // This Prevents the shutting down of JVM.(in case of System.exit())
        if (permission.getName().contains("exitVM")) {
          throw securityException =
              new SecurityException("System.exit attempted and blocked.");
        }
      }
    };
    System.setSecurityManager(securityManager);

    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

      @Override
      public void uncaughtException(Thread t, Throwable e) {
        // check whether it was the exception caused by a System.exit
        if (e == securityException) {
          semaphore.release();
        } else {
          throw new RuntimeException(e);
        }
      }

    });

    // wait for a second before trying to close the main window
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e1) {
      fail(e1.getMessage());
    }

    semaphore = new java.util.concurrent.Semaphore(0);

    try {
      manager.close(false);
      // wait for system.exit() only for 60 seconds then abort
      assertTrue(semaphore.tryAcquire(60, TimeUnit.SECONDS));
      assertTrue(app.canClose);
      assertTrue(app.exitingApplication);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    System.setSecurityManager(sm);

  }

}
