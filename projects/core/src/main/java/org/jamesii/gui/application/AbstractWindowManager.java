/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jamesii.SimSystem;
import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.action.ActionManager;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Abstract class implementing main functionality of the {@link IWindowManager}
 * interface. It delegates events to the actual implementing class. Use this as
 * starting point for own window managers.
 * 
 * @author Stefan Rybacki
 */

public abstract class AbstractWindowManager extends ComponentAdapter implements
    IWindowManager, PropertyChangeListener {
  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class ChangeLAFRunnable implements Runnable {

    private final String lafclassname;

    /**
     * @param lafclassname
     */
    private ChangeLAFRunnable(String lafclassname) {
      this.lafclassname = lafclassname;
    }

    @Override
    public void run() {
      try {
        if (lafclassname != null
            && !lafclassname.equals(UIManager.getLookAndFeel().getClass()
                .getName())) {
          UIManager.setLookAndFeel(lafclassname);
          BasicUtilities.updateTreeUI();
        }
      } catch (ClassNotFoundException | InstantiationException
          | IllegalAccessException | UnsupportedLookAndFeelException e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * list of {@link IWindowListener}s
   */
  private final ListenerSupport<IWindowListener> windowListeners =
      new ListenerSupport<>();

  /**
   * list of registered {@link IWindow}s
   */
  private final List<IWindow> windows = new ArrayList<>();

  /**
   * main frame of {@link IApplication} the manager belongs to
   */
  private JFrame mainFrame = null;

  /**
   * currently active {@link IWindow} null if none
   */
  private IWindow activeWindow = null;

  /**
   * stores the main window size before it was changed to current size
   */
  private Dimension size;

  /**
   * stores the main window position
   */
  private Point position;

  /**
   * Creates an instance of {@link AbstractWindowManager}.
   * 
   * @param windowSize
   *          the starting window size of the application's main window
   * @param windowPosition
   *          the window position
   */
  public AbstractWindowManager(Dimension windowSize, Point windowPosition) {
    // register global focus listener to track focus changes to change
    // activeWindow accordingly
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addPropertyChangeListener(this);
    size = windowSize;
    position = windowPosition;
  }

  @Override
  public void activateWindow(IWindow window) {
    // if already activated to nothing
    if (window == activeWindow) { // NOSONAR: intentional object equality check
      return;
    }

    // deactivate currently active window
    deactivateWindow(activeWindow);

    activeWindow = window;
    if (activeWindow != null) {
      activeWindow.windowActivated();
    }
    windowActivated(window);

    fireWindowActivated(window);
  }

  /**
   * Delegate method that is called after {@link #activateWindow(IWindow)} is
   * called on that window manager. Implement this method in subclassing window
   * managers instead of the actual {@link #activeWindow} method.
   * 
   * @param window
   *          the window that was activated
   */
  protected abstract void windowActivated(IWindow window);

  @Override
  public void addWindow(IWindow window) {
    if (!windows.contains(window) && window != null) {
      windows.add(window);
      window.installWindowManager(this);
      fireWindowOpened(window);
      windowAdded(window);
    } else if (window != null) {
      activateWindow(window);
    }
  }

  /**
   * Delegate method that is called after {@link #addWindow(IWindow)} is called
   * on that window manager. Implement this method in subclassing window
   * managers instead of the actual {@link #addWindow(IWindow)} method.
   * 
   * @param window
   *          the window that was added
   */
  protected abstract void windowAdded(IWindow window);

  @Override
  public synchronized void addWindowListener(IWindowListener listener) {
    windowListeners.addListener(listener);
  }

  /**
   * Delegate method that is called after {@link #closeWindow(IWindow)} is
   * called on that window manager. Implement this method in subclassing window
   * managers instead of the actual {@link #closeWindow(IWindow)} method.
   * 
   * @param window
   *          the window that was closed
   */
  protected abstract void windowClosed(IWindow window);

  @Override
  public IWindow getActiveWindow() {
    return activeWindow;
  }

  @Override
  public IWindow getWindow(int index) {
    return windows.get(index);
  }

  @Override
  public int getWindowCount() {
    return windows.size();
  }

  @Override
  public List<IWindow> getWindows() {
    return new ArrayList<>(windows);
  }

  @Override
  public boolean isActive(IWindow window) {
    return getActiveWindow() == window;
  }

  @Override
  public boolean isVisible(IWindow window) {
    return windows.contains(window);
  }

  @Override
  public boolean closeWindow(IWindow window) {
    if (window == null) {
      return false;
    }
    if (!window.canClose()) {
      return false;
    }

    if (windows.remove(window)) {
      // remove actions from action manager related to this window
      ActionManager.removeRelative(window.getActions(),
          getRelativeURLFor(window));
      ActionManager.removeBranch(getRelativeURLFor(window));
      if (getActiveWindow() == window) {
        activateWindow(null);
      }
      windowClosed(window);
      window.windowClosed();
      removeRelativeURLFor(window);
      fireWindowClosed(window);
      return true;
    }
    return false;
  }

  /**
   * This method is called after a window was closed and is meant to be for
   * cleaning up relative URL for window.
   * 
   * @param window
   */
  protected abstract void removeRelativeURLFor(IWindow window);

  @Override
  public synchronized void removeWindowListener(IWindowListener listener) {
    windowListeners.removeListener(listener);
  }

  @Override
  public void setMainFrame(JFrame main) {
    mainFrame = main;
    main.addComponentListener(this);
  }

  @Override
  public void setLookAndFeel(final String lafclassname) {
    try {
      BasicUtilities.invokeAndWaitOnEDT(new ChangeLAFRunnable(lafclassname));
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    }

  }

  @Override
  public void deactivateWindow(IWindow window) {
    if (window != null) {
      window.windowDeactivated();
      fireWindowDeactivated(window);
    }
    if (activeWindow == window) {// NOSONAR: intentional object reference
                                 // equality check
      activeWindow = null;
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // if focus owner changed react and change active window
    // accordingly
    if (("focusOwner".equals(evt.getPropertyName()))) {
      // check wether focused element is within the window
      Component focusOwner =
          KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

      // get the IWindow the focus owner is in if any
      IWindow aWindow = null;
      if (focusOwner != null) {
        for (IWindow w : windows) {
          if (SwingUtilities.isDescendingFrom(focusOwner,
              getContainerOfWindow(w))) {
            aWindow = w;
            break;
          }
        }
      }

      // check for active Window change
      if (aWindow != activeWindow && aWindow != null) { // NOSONAR: intentional
                                                        // object equality check
        activateWindow(aWindow);
      }
    }
  }

  /**
   * Notifies the listeners that the given window was activated.
   * 
   * @param window
   *          window that was activated
   */
  protected final synchronized void fireWindowActivated(IWindow window) {
    for (IWindowListener l : windowListeners.getListeners()) {
      if (l != null) {
        l.windowActivated(window);
      }
    }
  }

  /**
   * Notifies the listeners that the given window was deactivated
   * 
   * @param window
   *          window that was deactivated
   */
  protected final synchronized void fireWindowDeactivated(IWindow window) {
    for (IWindowListener l : windowListeners.getListeners()) {
      if (l != null) {
        l.windowDeactivated(window);
      }
    }
  }

  /**
   * Notifies the listeners that the given window was closed.
   * 
   * @param window
   *          window that was closed
   */
  protected final synchronized void fireWindowClosed(IWindow window) {
    for (IWindowListener l : windowListeners.getListeners()) {
      if (l != null) {
        l.windowClosed(window);
      }
    }
  }

  /**
   * Notifies the listeners that the given window was opened.
   * 
   * @param window
   *          window that was opened
   */
  protected final synchronized void fireWindowOpened(IWindow window) {
    for (IWindowListener l : windowListeners.getListeners()) {
      if (l != null) {
        l.windowOpened(window);
      }
    }
  }

  /**
   * Delegate method that is used for focus traversal to find the
   * {@link IWindow} the current focus lies in. To achieve that implement this
   * method and return the enclosing Swing container that actually holds the
   * specified {@link IWindow}.
   * 
   * @param window
   *          the window the enclosing container is needed
   * @return the container that holds the actual content of the given
   *         {@link IWindow}
   */
  protected abstract Component getContainerOfWindow(IWindow window);

  @Override
  public JFrame getMainWindow() {
    return mainFrame;
  }

  @Override
  public void componentResized(ComponentEvent e) {
    if (e.getComponent() instanceof JFrame) {
      JFrame w = (JFrame) e.getComponent();
      if (w.getExtendedState() == Frame.NORMAL) {
        size = w.getSize();
      }
    }
  }

  @Override
  public void componentMoved(ComponentEvent e) {
    if (e.getComponent() instanceof JFrame) {
      JFrame w = (JFrame) e.getComponent();
      if (w.getExtendedState() == Frame.NORMAL) {
        position = w.getLocation();
      }
    }
  }

  @Override
  public void exitingApplication() {
    // close all windows no matter of canClose return true or false so
    // omit it
    for (IWindow window : windows) {
      if (window == null) {
        continue;
      }

      windowClosed(window);
      fireWindowClosed(window);
    }
  }

  @Override
  public Window getWindowFor(IWindow window) {
    Component c = getContainerOfWindow(window);
    if (c == null) {
      return null;
    }
    c = SwingUtilities.getRoot(c);
    if (c instanceof Window) {
      return (Window) c;
    }
    return null;
  }

  /**
   * Helper function that returns the main window size before the last size
   * change. This is particularly helpful after the state of the main window
   * changed to minimized or maximized because in that case the original window
   * size before state change is lost.
   * 
   * @return the old dimensions
   */
  public final Dimension getSize() {
    return size;
  }

  /**
   * Gets the window position.
   * 
   * @return the window position
   */
  public Point getPosition() {
    return position;
  }

}
