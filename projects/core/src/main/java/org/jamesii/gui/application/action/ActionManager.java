/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import org.jamesii.SimSystem;
import org.jamesii.gui.base.URLTreeNode;
import org.jamesii.gui.base.URLTreeNodeURL;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Manages all available actions defined throughout the application. Actions are
 * organized by their specified paths and build therefore a tree like hierarchy
 * using {@link ActionTree}. The {@link ActionTree} can then be converted into
 * for instance a menu or toolbar. Furthermore not the entire ActionTree needs
 * to be converted but a sub tree can be converted as well. This way all actions
 * can be held in one tree and needed sub trees are extracted as necessary. <p>
 * Usage:
 * <code>
 * <pre>
 *  ActionManager.registerAction(new ActionSet("edit","Edit","menu.main?after=file"));
 *  ActionManager.registerAction(new ActionSet("file","File","menu.main"));
 *  ActionManager.registerAction(new QuitAction("quit","Quit",new String[]{"menu.main?last"}));
 *  ActionManager.registerAction(new SeparatorAction(new String[]{"menu.main?before=quit"}));
 *  ActionManager.registerAction(new NewAction("new","New...",new String[]{"menu.main/file?first"}));
 *  ActionManager.registerAction(new QuitAction("quit","Quit",new String[]{"menu.main/file?last"}));
 *  ActionManager.registerAction(new PasteAction("paste","Paste",new String[]{"menu.main/edit?after=copy"}));
 *  ActionManager.registerAction(new CopyAction("copy","Copy",new String[]{"menu.main/edit?after=cut"}));
 *  ActionManager.registerAction(new CutAction("cut","Cut",new String[]{"menu.main/edit?first"}));
 *
 *  JMenuBar menuBar = ActionManager.createMenuBarFor("menu.main");
 * </pre>
 * </code>
 *
 * @author Stefan Rybacki
 */

/*
 * TODO sr137: rework this class: - org.jamesii.new and org.jamesii.open are
 * removed when deselecting all perspectives even though it belongs to
 * DefaultPerspective - after reenabling perspectives org.jamesii.new and
 * org.jamesii.open reappear but icons are missing - implement a more
 * sophisticated way of updating menu and toolbars (only update what's
 * necessary) - sorting of elements in org.jamesii.file after disabling and
 * reenabling of perspectives is messed up
 */
public final class ActionManager implements TreeModelListener {

  /**
   *
   * LICENCE: JAMESLIC
   *
   * @author Stefan Rybacki
   *
   */
  private final class RebuildUI implements Runnable {

    @Override
    public void run() {
      rebuildToolBars();
      rebuildMenuBars();
      rebuildPopupMenus();
    }
  }

  /**
   *
   * LICENCE: JAMESLIC
   *
   * @author Stefan Rybacki
   *
   */
  private static final class ShowPopupAction extends javax.swing.AbstractAction {

    private final JPopupMenu popup;
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -7581716456837218776L;

    /**
     * @param name
     * @param icon
     * @param popup
     */
    private ShowPopupAction(String name, Icon icon, JPopupMenu popup) {
      super(name, icon);
      this.popup = popup;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JComponent c = (JComponent) e.getSource();
      popup.show(c, 0, c.getHeight());
    }
  }
  private static final String UNCHECKED = "unchecked";
  /**
   * instance used to drive the static methods
   */
  private static final ActionManager INSTANCE = new ActionManager();
  /**
   * list of registered actions (plain list)
   */
  private final List<IAction> actions = new ArrayList<>();
  /**
   * hierarchical representation of actions
   */
  private final ActionTree tree = new ActionTree();
  /**
   * list of generated menu bars (used to update them according to tree changes)
   */
  private final Map<String, JMenuBar> generatedMenuBars = new HashMap<>();
  /**
   * list of generated tool bars (used to update them according to tree changes)
   */
  private final Map<String, JToolBar> generatedToolBars = new HashMap<>();
  /**
   * list of generated popup menus (used to update them according to tree
   * changes)
   */
  private final Map<String, JPopupMenu> generatedPopupMenus = new HashMap<>();
  /**
   * Maps {@link IAction}s to Swing {@link Action}s with the option to reusing
   * already generated Swing {@link Action}s for a given {@link IAction}.
   */
  private final Map<IAction, Action> iActionActionMapping = new HashMap<>();

  /**
   * Hidden constructor due to use of singleton pattern for
   * {@link ActionManager}
   */
  private ActionManager() {
    tree.addTreeModelListener(this);
  }

  /**
   * Helper method that creates a {@link JMenu} for a given hierarchical set of
   * {@link IAction}s. It walks recursively through the set and creates
   * {@link JMenuItem}s and sub menus accordingly.
   *
   * @param set hierarchical set of {@link IAction}s
   * @return created {@link JMenu}
   */
  @SuppressWarnings(UNCHECKED)
  private static synchronized JMenu createJMenuFor(URLTreeNode<IAction> set) {
    if (set == null) {
      return null;
    }
    JMenu menu =
            new JMenu(set.getAttachedObject() == null ? set.getId() : set
            .getAttachedObject().getLabel());
    if (set.getAttachedObject() != null) {
      menu.setIcon(set.getAttachedObject().getIcon());
    }
    if (set.getAttachedObject() != null
            && set.getAttachedObject().getMnemonic() != null) {
      menu.setMnemonic(set.getAttachedObject().getMnemonic().intValue());
    }

    for (int i = 0; i < set.getChildCount(); i++) {
      URLTreeNode<IAction> a = (URLTreeNode<IAction>) set.getChildAt(i);
      if (!a.isLeaf()) {
        menu.add(createJMenuFor(a));
      } else if (a.getAttachedObject().getType() == ActionType.TOGGLEACTION) {
        menu.add(new JCheckBoxMenuItem(INSTANCE.iActionActionMapping.get(a
                .getAttachedObject())));
      } else if (a.getAttachedObject().getType() == ActionType.SEPARATOR) {
        menu.addSeparator();
      } else if (a.getAttachedObject() != null) {
        Action action =
                INSTANCE.iActionActionMapping.get(a.getAttachedObject());
        // in case we reach here but the action to add is an action
        // set (and therefore empty) disable it
        if (a.getAttachedObject().getType() == ActionType.ACTIONSET) {
          action.setEnabled(false);
        }
        menu.add(new JMenuItem(action));
      }
    }
    return menu;
  }

  /**
   * Use this method to generate a {@link JPopupMenu} for a given sub tree. The
   * subtree is given be the specified path. If the specified path is not
   * available {@code null} is returned.
   *
   * @param path the path to the sub tree (in format of {@link URLTreeNodeURL})
   * @return {@code null} if path in tree does not exist, a {@link JPopupMenu}
   * for the specified subtree else
   * @throws MalformedURLException if the given path does not conform to the
   * format of an {@link URLTreeNodeURL}
   * @throws UnsupportedEncodingException
   */
  public static synchronized JPopupMenu createJPopupMenuFor(String path)
          throws MalformedURLException, UnsupportedEncodingException {
    // check map of popups whether there was already one created for
    // this path
    if (INSTANCE.generatedPopupMenus.get(path) != null) {
      return INSTANCE.generatedPopupMenus.get(path);
    }

    URLTreeNode<IAction> set = INSTANCE.tree.getNode(path);
    JPopupMenu pm = createJPopupMenuFor(set, null);
    registerPopupMenu(path, pm);
    return pm;
  }

  /**
   * Helper method that registers a generated popup menu in the list of
   * {@link #generatedPopupMenus}
   *
   * @param path the path of the sub tree the popup menu was generated for
   * @param pm the generated popup menu
   */
  private static void registerPopupMenu(String path, JPopupMenu pm) {
    INSTANCE.generatedPopupMenus.put(path, pm);
  }

  /**
   * Helper method that creates a {@link JPopupMenu} for a given hierarchical
   * set of {@link IAction}s. This method walks recursively through the set of
   * {@link IAction}s and creates {@link JMenuItem}s and sub menus accordingly.
   *
   * @param set hierarchical set of {@link IAction}s
   * @param menu the already generated menu for the given path. If this is not
   * null the menu will be cleared and recreated reusing the given menu as root
   * @return the generated {@link JPopupMenu}
   */
  @SuppressWarnings(UNCHECKED)
  private static synchronized JPopupMenu createJPopupMenuFor(
          URLTreeNode<IAction> set, JPopupMenu menu) {
    JPopupMenu popupMenu = menu;

    if (set == null) {
      return null;
    }

    if (popupMenu == null) {
      popupMenu =
              new JPopupMenu(set.getAttachedObject() == null ? set.getId() : set
              .getAttachedObject().getLabel());
    } else {
      popupMenu.removeAll();
      popupMenu.setName(set.getAttachedObject() == null ? set.getId() : set
              .getAttachedObject().getLabel());
    }
    for (int i = 0; i < set.getChildCount(); i++) {
      URLTreeNode<IAction> a = (URLTreeNode<IAction>) set.getChildAt(i);
      if (!a.isLeaf()) {
        a.getAttachedObject().setEnabled(true);
        popupMenu.add(createJMenuFor(a));
      } else if (a.getAttachedObject().getType() == ActionType.TOGGLEACTION) {
        popupMenu.add(new JCheckBoxMenuItem(INSTANCE.iActionActionMapping.get(a
                .getAttachedObject())));
      } else if (a.getAttachedObject().getType() == ActionType.SEPARATOR) {
        popupMenu.addSeparator();
      } else if (a.getAttachedObject() != null) {
        Action action =
                INSTANCE.iActionActionMapping.get(a.getAttachedObject());
        // in case we reach here but the action to add is an action
        // set (and therefore empty) disable it
        if (a.getAttachedObject().getType() == ActionType.ACTIONSET
                && action != null) {
          action.setEnabled(false);
        }
        popupMenu.add(new JMenuItem(action));
      }
    }

    return popupMenu;
  }

  // TODO sr137: provide a way to support check box/radio box and
  // maybe even
  // input fields as menu items (therefore a system is needed that can
  // map
  // custom actions to menu items, for now only separators are custom
  // actions,
  // but more custom actions can be thought of)
  /**
   * Creates a {@link JMenuBar} for the given path to a subtree of registered
   * {@link IAction}s.
   *
   * @param path the path to a subtree (in format of {@link URLTreeNodeURL})
   * @return the created menu bar ({@code null} if path not found)
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  public static synchronized JMenuBar createJMenuBarFor(String path)
          throws MalformedURLException, UnsupportedEncodingException {
    // check map of popups whether there was already one created for
    // this path
    if (INSTANCE.generatedMenuBars.get(path) != null) {
      return INSTANCE.generatedMenuBars.get(path);
    }

    return createJMenuBarFor(path, null);
  }

  /**
   * Helper method that creates a swing {@link JMenuBar} for a given path within
   * the current {@link #tree} of {@link IAction}s. It also be used to update a
   * given bar after the {@link #tree} changed.
   *
   * @param path the path to create the {@link JMenuBar} for (in format of
   * {@link URLTreeNodeURL})
   * @param bar the bar to reuse if not {@code null}
   * @return the menu bar ({@code null} if path not found)
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings(UNCHECKED)
  private static synchronized JMenuBar createJMenuBarFor(String path,
          JMenuBar bar) throws MalformedURLException, UnsupportedEncodingException {
    URLTreeNode<IAction> set = INSTANCE.tree.getNode(path);
    JMenuBar menuBar = bar;

    if (set == null) {
      return null;
    }
    if (menuBar == null) {
      menuBar = new JMenuBar();
    } else {
      menuBar.removeAll();
    }

    for (int i = 0; i < set.getChildCount(); i++) {
      URLTreeNode<IAction> a = (URLTreeNode<IAction>) set.getChildAt(i);
      if (!a.isLeaf()) {
        if (a.getAttachedObject() != null) {
          a.getAttachedObject().setEnabled(true);
        }
        menuBar.add(createJMenuFor(a));
      } else if (a.getAttachedObject().getType() == ActionType.TOGGLEACTION) {
        menuBar.add(new JCheckBoxMenuItem(INSTANCE.iActionActionMapping.get(a
                .getAttachedObject())));
      } else if (a.getAttachedObject().getType() == ActionType.SEPARATOR) {
        menuBar.add(new JSeparator());
      } else if (a.getAttachedObject() != null) {
        Action action =
                INSTANCE.iActionActionMapping.get(a.getAttachedObject());
        // in case we reach here but the action to add is an action
        // set (and therefore empty) disable it
        if (a.getAttachedObject().getType() == ActionType.ACTIONSET) {
          action.setEnabled(false);
        }
        menuBar.add(new JMenuItem(action));
      }
    }

    registerMenubar(path, menuBar);
    return menuBar;
  }

  /**
   * Helper method that registers a generated menu bar in the list of
   * {@link #generatedMenuBars}
   *
   * @param path the path of the sub tree the menu bar was generated for
   * @param bar the generated menu bar
   */
  private static void registerMenubar(String path, JMenuBar bar) {
    INSTANCE.generatedMenuBars.put(path, bar);
  }

  /**
   * Creates a {@link JToolBar} for a given subtree path.
   *
   * @param path the path to create the tool bar for (in format of
   * {@link URLTreeNodeURL})
   * @return the generated tool bar, {@code null} if path could not be found
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  public static synchronized JToolBar createJToolBarFor(final String path)
          throws MalformedURLException, UnsupportedEncodingException {
    if (INSTANCE.generatedToolBars.get(path) != null) {
      return INSTANCE.generatedToolBars.get(path);
    }

    return createJToolBarFor(path, null);
  }

  /**
   * Helper method to create a {@link JToolBar} for a given path to a subtree
   * with the option to update an existing {@link JToolBar} if different
   * specified than {@code null}
   *
   * @param path the path to a subtree (in format of {@link URLTreeNodeURL})
   * @param tb the tool bar to update if different than {@code null}
   * @return the generated toolbar, null if path not found
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings(UNCHECKED)
  private static synchronized JToolBar createJToolBarFor(String path,
          JToolBar tb) throws MalformedURLException, UnsupportedEncodingException {
    JToolBar toolbar = tb;

    // check map of popups whether there was already one created for
    // this path

    URLTreeNode<IAction> set = INSTANCE.tree.getNode(path);
    if (set == null) {
      // TODO sr137: in this case no button is displayed in toolbar
      // make it invisible but in case it gets updated make it visible
      // again
      return new JToolBar();
    }

    // TODO sr137: there are still issues with the generated menubar
    // and toolbar with weird results on menu or toolbar change and or
    // look and feel changes (esp. using napkin laf)

    if (toolbar == null) {
      toolbar =
              new JToolBar(set.getAttachedObject() == null ? set.getId() : set
              .getAttachedObject().getLabel());
    } else {
      toolbar.setName(set.getAttachedObject() == null ? set.getId() : set
              .getAttachedObject().getLabel());
      toolbar.removeAll();
    }

    for (int i = 0; i < set.getChildCount(); i++) {
      URLTreeNode<IAction> a = (URLTreeNode<IAction>) set.getChildAt(i);
      if (!a.isLeaf()) {
        final JPopupMenu popup = createJPopupMenuFor(INSTANCE.tree.getPath(a));
        IAction attachedObject = a.getAttachedObject();
        toolbar.add(
                new ShowPopupAction(attachedObject != null ? attachedObject
                .getLabel() : a.getId(), attachedObject != null ? a
                .getAttachedObject().getIcon() : null, popup)).setFocusable(
                false);
      } else if (a.getAttachedObject().getType() == ActionType.SEPARATOR) {
        toolbar.addSeparator();
      } else if (a.getAttachedObject().getType() == ActionType.TOGGLEACTION) {
        toolbar.add(
                new JToggleButton(INSTANCE.iActionActionMapping.get(a
                .getAttachedObject()))).setFocusable(false);
      } else if (a.getAttachedObject() != null) {
        Action action =
                INSTANCE.iActionActionMapping.get(a.getAttachedObject());
        // in case we reach here but the action to add is an action
        // set (and therefore empty) disable it
        if (a.getAttachedObject().getType() == ActionType.ACTIONSET
                && action != null) {
          action.setEnabled(false);
        }
        toolbar.add(action).setFocusable(false);
      }
    }

    registerToolbar(path, toolbar);
    return toolbar;
  }

  /**
   * Helper method that registers a generated toolbar in the list of
   * {@link #generatedToolBars}
   *
   * @param path the path of the sub tree the toolbar as generated for
   * @param toolbar the generated toolbar
   */
  private static void registerToolbar(String path, JToolBar toolbar) {
    INSTANCE.generatedToolBars.put(path, toolbar);
  }

  /**
   * Registers a specific action in the JAMES II actions context and places it
   * according to its paths from {@link IAction#getPaths()} into the actions
   * hierarchy. If there exists any tool bar, menu bar or popup menu generated
   * using {@link #createJMenuBarFor(String)}
   * {@link #createJPopupMenuFor(String)} or {@link #createJToolBarFor(String)}
   * then those will be automatically updated according to the new registered
   * {@link IAction}.<br/> If you want to register an action relative to an
   * subtree path look at {@link #registerActionRelative(IAction, String)}.
   *
   * @param action the action to register
   * @see #registerActionRelative(IAction, String)
   */
  public static void registerAction(IAction action) {
    if (action == null) {
      return;
    }
    registerActionRelative(action, "");
  }

  /**
   * Basically this method does the same as {@link #registerAction(IAction)} but
   * registers the specified {@link IAction} according to its paths from
   * {@link IAction#getPaths()} where those paths are understood as relative to
   * the specified subtree path.
   *
   * @param action the action to register
   * @param rootPath the subtree path (in format of {@link URLTreeNodeURL})
   */
  @SuppressWarnings(UNCHECKED)
  public static void registerActionRelative(IAction action, String rootPath) {
    if (action == null) {
      throw new IllegalArgumentException("action can't be null");
    }
    INSTANCE.actions.add(action);

    Action a = INSTANCE.iActionActionMapping.get(action);
    if (a == null) {
      INSTANCE.iActionActionMapping.put(action, new IActionAction(action));
    }

    for (String p : action.getPaths()) {
      try {
        if (rootPath != null && rootPath.length() > 0) {
          p = rootPath + "/" + p;
        }

        // warn if an existing node with attached object is being
        // overwritten by
        // this action
        URLTreeNode<IAction> parentNode = INSTANCE.tree.getNode(p);

        if (parentNode != null) {
          // check children of parent node for existing child node
          // with same id
          for (int i = 0; i < parentNode.getChildCount(); i++) {
            URLTreeNode<IAction> childAt =
                    (URLTreeNode<IAction>) parentNode.getChildAt(i);
            if (childAt != null && childAt.getAttachedObject() != null
                    && action.getId().equals(childAt.getAttachedObject().getId())) {
              SimSystem.report(Level.WARNING, null, String.format(
                      "There is already an existing action %s at %s with the ID "
                      + "%s that will be overwritten by this action %s",
                      parentNode.getAttachedObject(), p, action.getId(), action),
                      null);
            }
          }
        }

        INSTANCE.tree.addNode(p,
                new URLTreeNode<>(action.getId(), null, action));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Returns the tree of {@link IAction}s as {@link TreeModel} so it can be used
   * as model for a {@link javax.swing.JTree}.
   *
   * @return the tree of actions as {@link TreeModel}
   */
  public static TreeModel getTreeModel() {
    return INSTANCE.tree;
  }

  /**
   * Removes the specified actions from the tree of {@link IAction}s. Used in
   * combination with {@link #registerAction(IAction)}.
   *
   * @param actions2 the actions to remove
   * @see #registerAction(IAction)
   */
  public static void remove(IAction[] actions2) {
    removeRelative(actions2, "");
  }

  /**
   * Helper method that cleans the {@link ActionTree} of unused, empty or
   * abandoned nodes.
   *
   * @param node the node to start from
   */
  @SuppressWarnings("unused")
  private static void cleanTraverse(Object node) {
    if (node instanceof URLTreeNode<?>) {
      URLTreeNode<?> n = (URLTreeNode<?>) node;

      // depth first traversal
      for (int i = n.getChildCount() - 1; i >= 0; i--) {
        URLTreeNode<?> child = (URLTreeNode<?>) n.getChildAt(i);
        cleanTraverse(child);

        // check child for children and or attached object if none is
        // present
        // delete node
        if (child.getChildCount() <= 0 && child.getAttachedObject() == null) {
          n.remove(i);
          // notify listeners
          INSTANCE.tree.nodesWereRemoved(n, new int[]{i},
                  new Object[]{child});
        }
      }
    }
  }

  /**
   * Similar to {@link #remove(IAction[])} but is used in combination with
   * {@link #registerActionRelative(IAction, String)}.
   *
   * @param actions2 the actions to remove
   * @param relativeTo a path to a subtree node that is the root node for the
   * provided actions (in format of {@link URLTreeNodeURL})
   * @see #registerActionRelative(IAction, String)
   */
  public static void removeRelative(IAction[] actions2, String relativeTo) {
    if (actions2 == null) {
      return;
    }

    for (IAction a : actions2) {
      // find actions in actions list by id + paths and remove them if
      // found
      for (int i = INSTANCE.actions.size() - 1; i >= 0; i--) {
        IAction action = INSTANCE.actions.get(i);
        // check id first and skip if not the same
        if (action != null && action.equals(a)) {
          INSTANCE.actions.remove(i);
          INSTANCE.iActionActionMapping.remove(action);
        }
      }

      for (String p : a.getPaths()) {
        try {
          URLTreeNodeURL url = new URLTreeNodeURL(p);
          String path = url.getLocation() + "/" + a.getId();
          INSTANCE.tree.removeNodeRelative(path, relativeTo);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
          SimSystem.report(e);
        }
      }
    }
  }

  @Override
  public void treeNodesChanged(TreeModelEvent e) {
    treeStructureChanged(e);
  }

  @Override
  public void treeNodesInserted(TreeModelEvent e) {
    treeStructureChanged(e);
  }

  @Override
  public void treeNodesRemoved(TreeModelEvent e) {
    treeStructureChanged(e);
  }

  @Override
  public void treeStructureChanged(TreeModelEvent e) {
    // if tree structure changed rebuild all generated menubars,
    // toolbars and popupmenus
    BasicUtilities.invokeLaterOnEDT(new RebuildUI());
  }

  /**
   * Helper method that recreates all generated menu bars according to tree
   * changes
   */
  private synchronized void rebuildMenuBars() {
    // TODO sr137: only update menubars that are affected by tree
    // change
    for (String p : INSTANCE.generatedMenuBars.keySet().toArray(
            new String[INSTANCE.generatedMenuBars.keySet().size()])) {
      try {
        final JMenuBar bar =
                createJMenuBarFor(p, INSTANCE.generatedMenuBars.get(p));
        BasicUtilities.invokeLaterOnEDT(new Runnable() {
          @Override
          public void run() {
            SwingUtilities.updateComponentTreeUI(bar);
            bar.revalidate();
            bar.repaint();
          }
        });
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Helper method that recreates all generated popup menus according to tree
   * changes
   */
  private synchronized void rebuildPopupMenus() {
    // TODO sr137: only update popupmenus that are affected by tree
    // change
    for (String p : INSTANCE.generatedPopupMenus.keySet().toArray(
            new String[INSTANCE.generatedPopupMenus.keySet().size()])) {
      try {
        URLTreeNode<IAction> set = INSTANCE.tree.getNode(p);
        final JPopupMenu bar =
                createJPopupMenuFor(set, INSTANCE.generatedPopupMenus.get(p));
        if (bar != null) {
          BasicUtilities.invokeLaterOnEDT(new Runnable() {
            @Override
            public void run() {
              SwingUtilities.updateComponentTreeUI(bar);
              bar.revalidate();
              bar.repaint();
            }
          });
        } else {
          INSTANCE.generatedPopupMenus.remove(p);
        }
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Helper method that recreates all generated tool bars according to tree
   * changes
   */
  private static synchronized void rebuildToolBars() {
    // TODO sr137: only update toolbars that are affected by tree
    // change
    for (String p : INSTANCE.generatedToolBars.keySet().toArray(
            new String[INSTANCE.generatedToolBars.keySet().size()])) {
      try {
        JToolBar bar = createJToolBarFor(p, INSTANCE.generatedToolBars.get(p));
        bar.revalidate();
        bar.repaint();
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Use this to update the ui of all generated toolbars, menubars and
   * popupmenus after a look and feel change
   */
  public static void updateUI() {
    for (JToolBar t : INSTANCE.generatedToolBars.values()) {
      if (t != null) {
        SwingUtilities.updateComponentTreeUI(t);
      }
    }
    for (JMenuBar t : INSTANCE.generatedMenuBars.values()) {
      if (t != null) {
        SwingUtilities.updateComponentTreeUI(t);
      }
    }
    for (JPopupMenu t : INSTANCE.generatedPopupMenus.values()) {
      if (t != null) {
        SwingUtilities.updateComponentTreeUI(t);
      }
    }
  }

  /**
   * Removes the given branch from the used {@link ActionTree}.
   *
   * @param branch the branch to remove
   */
  public static void removeBranch(String branch) {
    try {
      INSTANCE.tree.removeNode(branch);
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      SimSystem.report(e);
    }
    INSTANCE.generatedMenuBars.remove(branch);
    INSTANCE.generatedPopupMenus.remove(branch);
    INSTANCE.generatedToolBars.remove(branch);
  }
}
