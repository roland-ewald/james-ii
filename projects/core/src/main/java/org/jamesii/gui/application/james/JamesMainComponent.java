/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.preferences.Preferences;

/**
 * The main component for the JAMES II GUI. It consists of the left, right,
 * editor and bottom areas for {@link IWindow}s as well as the drag and drop
 * feature. Plus it manages creating, layouting and so on for SWING components
 * out of {@link IWindow}s.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
// TODO sr137: rework this component
class JamesMainComponent extends JPanel implements IWindowCreator {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4809024005452822653L;

  /**
   * bottom view pane
   */
  private final WindowPane bottomViewPane;

  /**
   * left view pane
   */
  private final WindowPane leftViewPane;

  /**
   * right view pane
   */
  private final WindowPane rightViewPane;

  /**
   * editor view pane
   */
  private final WindowPane editorPane;

  /**
   * stores the components enclosing a specific {@link IWindow}
   */
  private final Map<IWindow, Component> components = new HashMap<>();

  /**
   * list of open dialogs containing {@link IWindow}s
   */
  private final List<DialogIWindow> dialogs = new ArrayList<>();

  private Map<IWindow, JamesContribution> overriddenContributions;

  /**
   * Creates a new instance using the given application
   * 
   */
  public JamesMainComponent() {
    super(new BorderLayout());

    overriddenContributions =
        Preferences.get("org.jamesii.windowmanager.contributionoverrides");
    if (overriddenContributions == null) {
      overriddenContributions = new HashMap<>();
    }

    // try to extract previous sizes of panes from preferences
    Integer bottomSize = Preferences.get("org.jamesii.views.size.bottom");
    Integer leftSize = Preferences.get("org.jamesii.views.size.left");
    Integer rightSize = Preferences.get("org.jamesii.views.size.right");

    if (bottomSize == null || bottomSize <= 0) {
      bottomSize = 150;
    }
    if (leftSize == null || leftSize <= 0) {
      leftSize = 150;
    }
    if (rightSize == null || rightSize <= 0) {
      rightSize = 150;
    }

    // Separate layout into editor area and view area
    bottomViewPane = new WindowPane(this);
    bottomViewPane.setPreferredSize(new Dimension(150, bottomSize));
    bottomViewPane.setVisible(false);
    leftViewPane = new WindowPane(this);
    leftViewPane.setPreferredSize(new Dimension(leftSize, 150));
    leftViewPane.setVisible(false);
    rightViewPane = new WindowPane(this);
    rightViewPane.setPreferredSize(new Dimension(rightSize, 150));
    rightViewPane.setVisible(false);
    editorPane = new WindowPane(this);

    bottomViewPane.setMinimumSize(new Dimension(150, 150));
    leftViewPane.setMinimumSize(new Dimension(150, 150));
    rightViewPane.setMinimumSize(new Dimension(150, 150));

    JPanel bottomSplitPanel = new JPanel(new BorderLayout());
    bottomSplitPanel.add(new SplitterPanel(SplitterPanel.HORIZONTAL,
        bottomViewPane, false), BorderLayout.NORTH);
    bottomSplitPanel.add(bottomViewPane, BorderLayout.SOUTH);

    JPanel leftSplitPanel = new JPanel(new BorderLayout());
    leftSplitPanel.add(new SplitterPanel(SplitterPanel.VERTICAL, leftViewPane,
        true), BorderLayout.EAST);
    leftSplitPanel.add(leftViewPane, BorderLayout.WEST);

    JPanel rightSplitPanel = new JPanel(new BorderLayout());
    rightSplitPanel.add(new SplitterPanel(SplitterPanel.VERTICAL,
        rightViewPane, false), BorderLayout.WEST);
    rightSplitPanel.add(rightViewPane, BorderLayout.EAST);

    add(editorPane, BorderLayout.CENTER);
    add(leftSplitPanel, BorderLayout.WEST);
    add(rightSplitPanel, BorderLayout.EAST);
    add(bottomSplitPanel, BorderLayout.SOUTH);
  }

  @Override
  public Component showWindow(IWindow window) {
    if (window != null) {
      if (getWindowContribution(window).equals(Contribution.BOTTOM_VIEW)) {
        bottomViewPane.setVisible(true);
        components.put(window, bottomViewPane.addWindow(window));
        return components.get(window);
      } else

      if (getWindowContribution(window).equals(Contribution.LEFT_VIEW)) {
        leftViewPane.setVisible(true);
        components.put(window, leftViewPane.addWindow(window));
        return components.get(window);
      } else

      if (getWindowContribution(window).equals(Contribution.RIGHT_VIEW)) {
        rightViewPane.setVisible(true);
        components.put(window, rightViewPane.addWindow(window));
        return components.get(window);
      } else

      if (getWindowContribution(window).equals(Contribution.DIALOG)) {
        DialogIWindow dialog;
        components.put(window, dialog = DialogIWindow.create(window, this));
        dialogs.add(dialog);
        return components.get(window);
      } else

      /*
       * if (true || windowManager.getContribution(window).equals(
       * Contribution.EDITOR))
       */
      {
        components.put(window, editorPane.addWindow(window));
        return components.get(window);
      }
    }
    return null;
  }

  @Override
  public void closeWindow(final IWindow window) {
    components.remove(window);
    bottomViewPane.removeWindow(window);
    leftViewPane.removeWindow(window);
    rightViewPane.removeWindow(window);
    editorPane.removeWindow(window);

    bottomViewPane.setVisible(bottomViewPane.getWindowCount() > 0);
    leftViewPane.setVisible(leftViewPane.getWindowCount() > 0);
    rightViewPane.setVisible(rightViewPane.getWindowCount() > 0);

    for (int i = dialogs.size() - 1; i >= 0; i--) {
      DialogIWindow d = dialogs.get(i);
      if (d.closeWindow(window)) {
        dialogs.remove(i);
        d.dispose();
      }
    }
  }

  @Override
  public void makeVisible(final IWindow window) {
    bottomViewPane.setSelectedWindow(window);
    leftViewPane.setSelectedWindow(window);
    rightViewPane.setSelectedWindow(window);
    editorPane.setSelectedWindow(window);

    for (DialogIWindow d : dialogs) {
      d.setSelectedWindow(window);
    }
  }

  @Override
  public Component changeContribution(IWindow window,
      Contribution toContribution) {
    if (window == null) {
      return null;
    }

    // get actual contribution the window is in and only change
    // contribution if
    // window contributes to another view
    Contribution currentContribution = getWindowContribution(window);
    if (currentContribution.equals(toContribution)) {
      return components.get(window);
    }

    closeWindow(window);
    /*
     * // remove actions of window try {
     * ActionManager.removeRelative(window.getActions(), windowManager
     * .getRelativeURLFor(window)); } catch (Throwable e) { SimSystem.report(e);
     * }
     */

    return showWindow(window);
  }

  @Override
  public void exitingApplication() {
    if (!leftViewPane.isVisible()) {
      leftViewPane.setVisible(true);
    }
    if (!rightViewPane.isVisible()) {
      rightViewPane.setVisible(true);
    }
    if (!bottomViewPane.isVisible()) {
      bottomViewPane.setVisible(true);
    }
    revalidate();
    Preferences.put("org.jamesii.views.size.left",
        Integer.valueOf(leftViewPane.getWidth()));
    Preferences.put("org.jamesii.views.size.bottom",
        Integer.valueOf(bottomViewPane.getHeight()));
    Preferences.put("org.jamesii.views.size.right",
        Integer.valueOf(rightViewPane.getWidth()));
    Preferences.put("org.jamesii.windowmanager.contributionoverrides",
        (Serializable) overriddenContributions);
  }

  @Override
  public Contribution getWindowContribution(IWindow window) {
    if (window == null) {
      return null;
    }

    if (leftViewPane.getWindowIndex(window) >= 0) {
      return Contribution.LEFT_VIEW;
    }
    if (rightViewPane.getWindowIndex(window) >= 0) {
      return Contribution.RIGHT_VIEW;
    }
    if (bottomViewPane.getWindowIndex(window) >= 0) {
      return Contribution.BOTTOM_VIEW;
    }
    if (editorPane.getWindowIndex(window) >= 0) {
      return Contribution.EDITOR;
    }

    // if we reach here we also need to check all dialogs whether any
    // of it
    // contains the given window
    for (DialogIWindow d : dialogs) {
      if (d.getWindow().equals(window)) {
        return Contribution.DIALOG;
      }
    }

    return window.getContribution();
  }

  @Override
  public JComponent createContainer(IWindow window, String actionPath) {
    return new JamesWindow(window, actionPath);
  }

}
