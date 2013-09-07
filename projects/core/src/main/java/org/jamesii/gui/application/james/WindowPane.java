/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowChangeListener;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.james.dnd.WindowPaneDragSupport;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.ListenerSupport;

/**
 * Custom implementation of {@link JTabbedPane} meant to serve as
 * {@link IWindow} container. It places the tabs on top of managed windows uses
 * icon and title of registered {@link IWindow}s and implements a custom UI to
 * add close button and other functionality needed as window container.
 * 
 * @author Stefan Rybacki
 */
public class WindowPane extends JTabbedPane implements MouseListener,
    ActionListener, IWindowChangeListener {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7254670217024312210L;

  /** managed windows. */
  private final List<IWindow> windows = new ArrayList<>();

  /**
   * map that connects generated containers enclosing registered {@link IWindow}
   * s.
   */
  private final Map<IWindow, JComponent> tabs = new HashMap<>();

  /** active window manager. */
  private final IWindowCreator windowCreator;

  /**
   * Creates an instance of {@link WindowPane} registering the specified window
   * manager.
   * 
   * @param wm
   *          the window manager used
   */
  public WindowPane(IWindowCreator creator) {
    super(TOP, SCROLL_TAB_LAYOUT);
    windowCreator=creator;
    setOpaque(false);
    setFocusable(false);
    setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    addMouseListener(this);

    new WindowPaneDragSupport(this);
  }

  @Override
  public void setUI(TabbedPaneUI u) {
    // make sure the custom ui is used even on look and feel change
    WindowPaneUI ui1 = new WindowPaneUI();
    ui1.addActionListener(this);
    super.setUI(ui1);
  }

  /**
   * removes the specified window if registered to this pane
   * 
   * @param window
   *          the window to remove
   */
  public void removeWindow(IWindow window) {
    int i = windows.indexOf(window);
    if (i >= 0) {
      window.removeWindowChangeListener(this);
      windows.remove(i);
      tabs.remove(window);
      removeTabAt(i);
    }
  }

  /**
   * Adds the specified window and returns the enclosing generated container.
   * 
   * @param window
   *          the window to add
   * @return the generated enclosing container for the given window
   */
  public JComponent addWindow(final IWindow window) {
    if (window == null) {
      throw new IllegalArgumentException("window can't be null!");
    }

    try {
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          addWindowEDT(window);
        }
      });
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    }

    return tabs.get(window);
  }

  /**
   * Helper method that adds an {@link IWindow} from within the Event Dispatcher
   * Thread to avoid artifacts due to the single thread nature of SWING.
   * 
   * @param window
   */
  private void addWindowEDT(final IWindow window) {
    windows.add(window);
    window.addWindowChangeListener(this);
    // add tab to box
    JComponent result = WindowManagerManager.getWindowManager().createContainer(window);
    tabs.put(window, result);
    addTab(window.getTitle(), window.getWindowIcon(), result);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    Component focusedWindow =
        KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

    Component selectedComponent =
        ((WindowPane) e.getSource()).getSelectedComponent();

    if (focusedWindow != null
        && !SwingUtilities.isDescendingFrom(focusedWindow, selectedComponent)) {
      // not sure whether this is available on all platforms
      compositeRequestFocus(selectedComponent);
    }
  }

  /**
   * Helper function that selects a component within the given component if
   * container using focus traversal policies and default components if
   * possible.
   * 
   * @param component
   *          the component to request focus in if needed in its children
   */
  public static void compositeRequestFocus(Component component) {
    if (component instanceof Container) {
      Container container = (Container) component;
      if (container.isFocusCycleRoot()) {
        FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
        Component comp = policy.getDefaultComponent(container);
        if (comp != null) {
          comp.requestFocusInWindow();
          return;
        }
      }
      Container rootAncestor = container.getFocusCycleRootAncestor();
      if (rootAncestor != null) {
        FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
        Component comp = policy.getComponentAfter(rootAncestor, container);

        if (comp != null && SwingUtilities.isDescendingFrom(comp, container)) {
          comp.requestFocusInWindow();
          return;
        }
      }
    }

    if (component != null && component.isFocusable()) {
      component.requestFocusInWindow();
      return;
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  /**
   * @return the currently displaying {@link IWindow}
   */
  public IWindow getSelectedWindow() {
    int i = getSelectedIndex();
    if (i < 0) {
      return null;
    }
    return windows.get(i);
  }

  /**
   * Helper function that selects a window identified by index
   * 
   * @param index
   *          the index of window to select
   */
  private void selectWindow(int index) {
    if (index < 0 || index >= getTabCount()) {
      return;
    }
    setSelectedIndex(index);
    revalidate();
    repaint();

    // check whether focus isn't already in componentAtIndex
    Component focusedWindow =
        KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

    if (focusedWindow != null
        && !SwingUtilities.isDescendingFrom(focusedWindow,
            getComponentAt(index))) {
      compositeRequestFocus(getComponentAt(index));
    }
  }

  /**
   * Sets the currently selected window.
   * 
   * @param window
   *          the window to select
   */
  public void setSelectedWindow(IWindow window) {
    int i = windows.indexOf(window);
    if (i >= 0) {
      selectWindow(i);
    }
  }

  /**
   * @return the number of registered windows to that container
   */
  public int getWindowCount() {
    return windows.size();
  }

  @Override
  public synchronized void actionPerformed(ActionEvent e) {
    if (WindowPaneUI.CLOSE_TAB.equals(e.getActionCommand())) {
      // the tab id is encoded into the action events id
      int tabIndex = e.getID();
      if (tabIndex >= 0 && tabIndex < windows.size()) {
        WindowManagerManager.getWindowManager().closeWindow(windows.get(tabIndex));
      }
    }

    if (WindowPaneUI.CHANGE_CONTRIBUTION.equals(e.getActionCommand())) {
      // the tab id is encoded into the action events id and the
      // contribution
      // into the source
      int tabIndex = e.getID();
      if (tabIndex >= 0 && tabIndex < windows.size()) {
        windowCreator.changeContribution(windows.get(tabIndex),
            (Contribution) e.getSource());
      }
    }
  }

  @Override
  public void windowActionsChanged(IWindow window, IAction[] oldActions) {
    // not this components responsibility
  }

  @Override
  public void windowIconChanged(IWindow window) {
    // change icon of tab
    // get tab
    int i = windows.indexOf(window);
    if (i >= 0) {
      // and change icon
      setIconAt(i, window.getWindowIcon());
    }
  }

  @Override
  public void windowTitleChanged(IWindow window) {
    // change text title of tab
    // get tab
    int i = windows.indexOf(window);
    if (i >= 0 && i < getTabCount()) {
      // and change title
      setTitleAt(i, window.getTitle());
    }
  }

  /**
   * @param window
   *          the specified window
   * @return the tab index of the specified window (-1 if not in any tab)
   */
  public int getWindowIndex(IWindow window) {
    return windows.indexOf(window);
  }

  /**
   * Returns the window that is at a given tab position.
   * 
   * @param tabIndex
   *          the tab index the window is required for
   * @return the window at given tab index
   */
  public IWindow getWindowAt(int tabIndex) {
    return windows.get(tabIndex);
  }

  /**
   * @param window
   * @param dialog
   */
  public void changeContribution(IWindow window, Contribution toContribution) {
    windowCreator.changeContribution(window, toContribution);
  }
}

/**
 * Custom UI used to render {@link WindowPane}. Adds close button and custom tab
 * design to pane.
 * 
 * @author Stefan Rybacki
 */
class WindowPaneUI extends BasicTabbedPaneUI implements MouseListener,
    MouseMotionListener {
  /**
   * close tab action
   */
  public static final String CLOSE_TAB = "CLOSE_TAB";

  /**
   * change contribution action
   */
  public static final String CHANGE_CONTRIBUTION = "CHANGE_CONTRIBUTION";

  /**
   * index for tab where a button is highlighted
   */
  private int buttonHighlightForTab = -1;

  /**
   * last highlighted tab
   */
  private int lastIndex = 0;

  /**
   * defines button sizes
   */
  private final Dimension buttonSize = new Dimension(12, 12);

  /**
   * list of registered listeners for button actions
   */
  private final ListenerSupport<ActionListener> listeners =
      new ListenerSupport<>();

  /**
   * index of tab where a popup is shown for
   */
  private int tabIndexForPopup = -1;

  /**
   * number of buttons on tab
   */
  private static final int buttons = 1;

  /**
   * index of button that is to highlight on a tab
   */
  private int buttonToHighlight;

  /**
   * tooltips for buttons
   */
  private final String[] buttonToolTip = new String[] { "Close View" };

  /**
   * index for close button
   */
  private static final int closeButtonIndex = 0;

  /**
   * Instantiates a new window pane ui.
   */
  public WindowPaneUI() {
  }

  /**
   * Adds an action listener. Actions are identified by
   * {@link ActionEvent#getActionCommand()} where it can be {@link #CLOSE_TAB}
   * or {@link #CHANGE_CONTRIBUTION}. The contribution value for
   * {@link #CHANGE_CONTRIBUTION} actions is provided via
   * {@link ActionEvent#getSource()} which means the source is the actual chosen
   * {@link Contribution} and {@link ActionEvent#getID()} provides the selected
   * tab index.
   * 
   * @param l
   *          the listener to add
   * @see #CLOSE_TAB
   * @see #CHANGE_CONTRIBUTION
   */
  public synchronized void addActionListener(ActionListener l) {
    listeners.addListener(l);
  }

  /**
   * Removes a previously registered action listener
   * 
   * @param l
   *          the listener to remove
   */
  public synchronized void removeActionListener(ActionListener l) {
    listeners.removeListener(l);
  }

  @Override
  protected void installDefaults() {
    super.installDefaults();
    tabAreaInsets.left = 2;
    selectedTabPadInsets = new Insets(3, 3, 3, 3);
    tabInsets = new Insets(2, 5, 2, 2);
    contentBorderInsets = new Insets(1, 1, 1, 1);
  }

  @Override
  protected int calculateTabHeight(int tabPlacement, int tabIndex,
      int fontHeight) {
    return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
  }

  @Override
  protected int calculateTabWidth(int tabPlacement, int tabIndex,
      FontMetrics metrics) {
    return (super.calculateTabWidth(tabPlacement, tabIndex, metrics) + (buttonSize.width * buttons));
  }

  /**
   * Helper method that calculates the rectangle of the specified button.
   * 
   * @param tabIndex
   *          the tab the button rectangle is calculated for
   * @param buttonIndex
   *          the buttons index on specified tab
   * @return the rectangle of the button
   */
  private Rectangle calculateButtonRect(int tabIndex, int buttonIndex) {
    Rectangle tabBounds = getTabBounds(tabPane, tabIndex);

    return new Rectangle(tabBounds.x + tabBounds.width - buttonSize.width
        * buttons - tabInsets.right * 2 + buttonSize.width * buttonIndex,
        tabBounds.height / 2 - buttonSize.height / 2, buttonSize.width,
        buttonSize.height);
  }

  /**
   * Draws the specified button.
   * 
   * @param g
   *          the graphics context
   * @param tabIndex
   *          the index of the tab the button is drawn for
   * @param buttonIndex
   *          the button to draw
   * @param isHover
   *          if true the button should be highlighted
   * @param bgColor
   *          background color
   * @param fgColor
   *          foreground color
   * @param x
   *          the x coordinate of the button
   * @param y
   *          the y coordinate of the button
   */
  private void drawButton(Graphics g, int tabIndex, int buttonIndex,
      boolean isHover, Color bgColor, Color fgColor, int x, int y) {
    // calculate offset due to scrolling reasons
    Rectangle r = getTabBounds(tabPane, tabIndex);
    int offsetX = x - r.x;
    int offsetY = y;

    g.setColor(bgColor);
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    Rectangle buttonRect = calculateButtonRect(tabIndex, buttonIndex);
    buttonRect.x += offsetX;
    buttonRect.y += offsetY;

    if (isHover) {
      if (buttonHighlightForTab == tabIndex) {
        g.draw3DRect(buttonRect.x, buttonRect.y, buttonRect.width,
            buttonRect.height, true);
      }
    }

    g.setColor(fgColor);

    int distance = 3;
    // draw the actual button
    if (buttonIndex == closeButtonIndex) {
      // paint the X of the close button
      g.drawLine(buttonRect.x + distance, buttonRect.y + distance, buttonRect.x
          + buttonRect.width - distance, buttonRect.y + buttonRect.height
          - distance);
      g.drawLine(buttonRect.x + buttonRect.width - distance, buttonRect.y
          + distance, buttonRect.x + distance, buttonRect.y + buttonRect.height
          - distance);
    }

  }

  @Override
  protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
      int x, int y, int w, int h, boolean isSelected) {
  }

  @Override
  protected void paintText(Graphics g, int tabPlacement, Font font,
      FontMetrics metrics, int tabIndex, String title, Rectangle textRect,
      boolean isSelected) {

    textRect.x -= (buttonSize.width * buttons) / 2;

    Color fg = getFgColor(isSelected, tabIndex);

    g.setColor(fg);
    g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
  }

  @Override
  protected void uninstallListeners() {
    super.uninstallListeners();
    tabPane.removeMouseListener(this);
    tabPane.removeMouseMotionListener(this);
    listeners.clear();
  }

  @Override
  protected void installListeners() {
    super.installListeners();
    tabPane.addMouseListener(this);
    tabPane.addMouseMotionListener(this);
  }

  /**
   * Helper method that repaints all buttons. This is useful if you want to
   * restore unhighlighted buttons for a given tab.
   * 
   * @param index
   *          the index of the tab the buttons should be repainted for
   */
  private void repaintButtons(int index) {
    if (index >= 0 && index < tabPane.getTabCount()) {
      Rectangle r = getTabBounds(tabPane, index);
      r.x = r.width + r.x - buttons * buttonSize.width - tabInsets.right * 2;
      tabPane.repaint(r);
    }
  }

  @Override
  protected void paintIcon(Graphics g, int tabPlacement, int tabIndex,
      Icon icon, Rectangle iconRect, boolean isSelected) {

    iconRect.x -= (buttonSize.width * buttons) / 2;

    super.paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // check whether the click happened on close button
    if (buttonHighlightForTab >= 0 && e.getButton() == MouseEvent.BUTTON1
        && e.getClickCount() == 1 && buttonToHighlight == closeButtonIndex) {
      notifyActionListeners(new ActionEvent(tabPane, buttonHighlightForTab,
          CLOSE_TAB));
    }

    // check whether the click happened on the drop down button
    // checkPopup(e);
  }

  // /**
  // * Helper method that checks whether a popup event occured and
  // shows
  // * in that case the {@link Contribution} popup.
  // *
  // * @param e the mouse event that might trigger a popup
  // */
  // private void checkPopup(MouseEvent e) {
  // if (buttonHighlightForTab >= 0
  // && e.getButton() == MouseEvent.BUTTON1
  // && e.getClickCount() == 1
  // && buttonToHighlight == contributionButtonIndex) {
  // tabIndexForPopup = getRolloverTab();
  // if (tabIndexForPopup >= 0) {
  // Rectangle buttonRect = calculateButtonRect(tabIndexForPopup,
  // buttonToHighlight);
  // contributionPopup.show(tabPane, buttonRect.x, buttonRect.y
  // + buttonRect.height + 2);
  // }
  // }
  // }

  /**
   * Helper method that notifies registered action listeners using the specified
   * {@link ActionEvent}.
   * 
   * @param actionEvent
   *          the action event to use in notification
   */
  private void notifyActionListeners(ActionEvent actionEvent) {
    for (ActionListener l : listeners) {
      if (l != null) {
        l.actionPerformed(actionEvent);
      }
    }
  }

  @Override
  protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
      int selectedIndex, int x, int y, int w, int h) {
    g.setColor(getBgColor(true, selectedIndex).darker().darker());
    g.fillRect(x, y, contentBorderInsets.left, h);
  }

  @Override
  protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
      int selectedIndex, int x, int y, int w, int h) {
    g.setColor(getBgColor(true, selectedIndex).darker().darker());
    g.fillRect(x, y + h - contentBorderInsets.bottom, w,
        contentBorderInsets.bottom);
  }

  @Override
  protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
      int selectedIndex, int x, int y, int w, int h) {
    g.setColor(getBgColor(true, selectedIndex).darker().darker());
    g.fillRect(x + w - contentBorderInsets.right, y, contentBorderInsets.right,
        h);
  }

  @Override
  protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
      int selectedIndex, int x, int y, int w, int h) {
    g.setColor(getBgColor(true, selectedIndex).darker().darker());
    g.fillRect(x, y, w, contentBorderInsets.top);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
    buttonHighlightForTab = -1;
    if (lastIndex >= 0 && lastIndex < tabPane.getTabCount()) {
      WindowPaneUI.this.repaintButtons(lastIndex);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    // checkPopup(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // checkPopup(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    // check whether mouse is over close button
    int index = tabForCoordinate(tabPane, e.getX(), e.getY());
    if (index >= 0) {
      if (lastIndex != index) {
        repaintButtons(lastIndex);
      }
      lastIndex = index;

      // check whether there is a button to highlight
      for (int i = 0; i < buttons; i++) {
        // get bounds for tab
        Rectangle buttonRect = calculateButtonRect(index, i);
        buttonRect.y += getTabBounds(tabPane, index).y;

        if (buttonRect.contains(e.getX(), e.getY())) {
          buttonHighlightForTab = index;
          buttonToHighlight = i;
          tabPane.setToolTipTextAt(index, buttonToolTip[i]);
          repaintButtons(index);
          return;
        }
      }
      tabPane.setToolTipTextAt(index, null);
    }

    // only repaint if necessary
    if (buttonHighlightForTab >= 0) {
      buttonHighlightForTab = -1;
      if (lastIndex >= 0 && lastIndex < tabPane.getTabCount()) {
        tabPane.setToolTipTextAt(lastIndex, null);
        repaintButtons(lastIndex);
      }
    }
  }

  /**
   * Calculates the background of the given tab.
   * 
   * @param isSelected
   *          {@code true} if the tab is currently selected
   * @param tabIndex
   *          the tab's index
   * @return the background color for the specified tab
   */
  private Color getBgColor(boolean isSelected, int tabIndex) {
    if (tabIndex >= 0 && tabIndex < tabPane.getTabCount()) {

      Color bgColor = tabPane.getBackgroundAt(tabIndex);
      if (isSelected && tabPane.isEnabled()) {
        bgColor = UIManager.getColor("InternalFrame.activeTitleBackground");
        if (bgColor == null || UIManager.getLookAndFeel().isNativeLookAndFeel()) {
          bgColor = SystemColor.activeCaption;
        }
      } else {
        bgColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
        if (bgColor == null || UIManager.getLookAndFeel().isNativeLookAndFeel()) {
          bgColor = SystemColor.inactiveCaption;
        }
      }
      return bgColor;
    }
    return tabPane.getBackground();
  }

  /**
   * Calculates the foreground of the given tab.
   * 
   * @param isSelected
   *          {@code true} if the tab is currently selected
   * @param tabIndex
   *          the tab's index
   * @return the foreground color for the specified tab
   */
  private Color getFgColor(boolean isSelected, int tabIndex) {
    Color bgColor = getBgColor(isSelected, tabIndex);
    Color fgColor = tabPane.getForegroundAt(tabIndex);
    if (isSelected && tabPane.isEnabled()) {
      fgColor = UIManager.getColor("InternalFrame.activeTitleForeground");
      if (fgColor == null || UIManager.getLookAndFeel().isNativeLookAndFeel()) {
        fgColor = SystemColor.activeCaptionText;
      }
    } else {
      fgColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
      if (fgColor == null || UIManager.getLookAndFeel().isNativeLookAndFeel()) {
        fgColor = SystemColor.inactiveCaptionText;
      }
    }

    // check whether bgColor and fgColor are not to close
    int yF =
        (int) (fgColor.getBlue() * 0.114 + fgColor.getRed() * 0.299 + fgColor
            .getGreen() * 0.587);
    int yB =
        (int) (bgColor.getBlue() * 0.114 + bgColor.getRed() * 0.299 + bgColor
            .getGreen() * 0.587);

    if (Math.abs(yF - yB) < 20) {
      // invert fgColor
      fgColor =
          new Color(255 - fgColor.getRed(), 255 - fgColor.getGreen(),
              255 - fgColor.getBlue());

      yF =
          (int) (fgColor.getBlue() * 0.114 + fgColor.getRed() * 0.299 + fgColor
              .getGreen() * 0.587);
      // check again
      if (Math.abs(yF - yB) < 20) {
        fgColor = fgColor.brighter().brighter().brighter();
      }
    }

    return fgColor;
  }

  /**
   * Calculates the border of a tab and returns it as a {@link Polygon}.
   * 
   * @param tabPlacement
   *          the tab placement ({@link javax.swing.SwingConstants#TOP} ,
   *          {@link javax.swing.SwingConstants#BOTTOM},
   *          {@link javax.swing.SwingConstants#LEFT},
   *          {@link javax.swing.SwingConstants#RIGHT})
   * @param x
   *          the x coordinate of the upper left corner of the tab
   * @param y
   *          the y coordinate of the upper left corner of the tab
   * @param w
   *          the tabs width
   * @param h
   *          the tabs height
   * @param isSelected
   *          true if tab to create border for is selected
   * @return a {@link Polygon} representing the tab border
   */
  private Polygon calcBorder(int tabPlacement, int x, int y, int w, int h,
      boolean isSelected) {
    Polygon shape = new Polygon();

    if (tabPlacement == TOP) {
      shape.addPoint(x, y + 2);
      shape.addPoint(x + 2, y);
      shape.addPoint(x + w - 2, y);
      shape.addPoint(x + w, y + 2);
      if (isSelected) {
        shape.addPoint(x + w + tabInsets.left / 2, y + h / 3);
        shape.addPoint(x + w + tabInsets.left, y + h * 2 / 3);
        shape.addPoint(x + w + tabInsets.left * 2, y + h);
      } else {
        shape.addPoint(x + w, y + h);
      }

      shape.addPoint(x, y + h);
    }

    if (tabPlacement == LEFT) {
      shape.addPoint(x + 2, y + h);
      shape.addPoint(x, y + h - 2);
      shape.addPoint(x, y + 2);
      shape.addPoint(x + 2, y);
      shape.addPoint(x + w, y);
      shape.addPoint(x + w, y + h);
      if (isSelected) {
        shape.addPoint(x + w, y + h + tabInsets.top);
        shape.addPoint(x + w - w / 6, y + h + tabInsets.top / 2);
        shape.addPoint(x + w - w / 3, y + h + tabInsets.top / 3);
      }
    }

    if (tabPlacement == RIGHT) {
      shape.addPoint(x, y);
      shape.addPoint(x + w - 2, y);
      shape.addPoint(x + w, y + 2);
      shape.addPoint(x + w, y + h - 2);
      shape.addPoint(x + w - 2, y + h);
      if (isSelected) {
        shape.addPoint(x + w / 3, y + h + tabInsets.top / 3);
        shape.addPoint(x + w / 6, y + h + tabInsets.top / 2);
        shape.addPoint(x, y + h + tabInsets.top);
      } else {
        shape.addPoint(x, y + h);
      }
    }

    if (tabPlacement == BOTTOM) {
      shape.addPoint(x, y);
      shape.addPoint(x, y + h - 2);
      shape.addPoint(x + 2, y + h);
      shape.addPoint(x + w - 2, y + h);
      shape.addPoint(x + w, y + h - 2);
      if (isSelected) {
        shape.addPoint(x + w + tabInsets.left / 2, y + h * 2 / 3);
        shape.addPoint(x + w + tabInsets.left, y + h / 3);
        shape.addPoint(x + w + tabInsets.left * 2, y);
      } else {
        shape.addPoint(x + w, y);
      }

    }

    return shape;
  }

  @Override
  protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
      int x, int y, int w, int h, boolean isSelected) {
    // also draw tab background herein
    Color bgColor = getBgColor(isSelected, tabIndex);
    Color fgColor = getFgColor(isSelected, tabIndex);

    GradientPaint paint =
        new GradientPaint(x, y, bgColor, w + x, y, bgColor.darker());
    ((Graphics2D) g).setPaint(paint);

    Polygon shape = calcBorder(tabPlacement, x, y, w, h, isSelected);

    g.fillPolygon(shape);
    ((Graphics2D) g).setPaint(null);

    // draw buttons
    for (int i = 0; i < buttons; i++) {
      drawButton(g.create(), tabIndex, i,
          (buttonHighlightForTab == tabIndex && buttonToHighlight == i),
          bgColor, fgColor, x, y);
    }

    // paint border
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(bgColor.darker().darker());
    g.drawPolygon(shape);
  }

}