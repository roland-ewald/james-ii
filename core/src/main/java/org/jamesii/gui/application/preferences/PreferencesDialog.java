/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.ProgressDialog;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.TaskManager;
import org.jamesii.gui.base.URLTreeModel;
import org.jamesii.gui.base.URLTreeNode;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.PerspectivesManager;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Use this dialog in a static way to show a dialog providing access to
 * installed preferences pages.
 * 
 * @author Stefan Rybacki
 * @see #showPreferencesDialog(IWindowManager)
 */
public final class PreferencesDialog extends ApplicationDialog implements
    WindowListener, IPreferencesPageListener, TreeSelectionListener,
    ActionListener {

  private final class RestoreDefaultsTask extends AbstractTask {
    private RestoreDefaultsTask(String taskName) {
      super(taskName);
    }

    @Override
    protected void task() {
      for (IPreferencesPage p : pages) {
        if (p != null) {
          try {
            setTaskInfo(getName() + "...");
            p.restoreDefaults();
          } catch (Exception e) {
            SimSystem.report(e);
          }
        }
      }
    }

    @Override
    public boolean isBlocking() {
      return true;
    }

    @Override
    protected void cancelTask() {
    }
  }

  private final class ClosePrefPagesTask extends AbstractTask {
    private ClosePrefPagesTask(String taskName) {
      super(taskName);
    }

    @Override
    protected void task() {
      for (final IPreferencesPage p : pages) {
        if (p != null) {
          try {
            BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
              @Override
              public void run() {
                p.closed();
              }
            });
          } catch (Exception e) {
            SimSystem.report(e);
          }
        }
      }
    }

    @Override
    public boolean isBlocking() {
      return true;
    }

    @Override
    protected void cancelTask() {
    }
  }

  /** Serialization ID. */
  private static final long serialVersionUID = -2493241927922403787L;

  /** The Constant OK. */
  private static final String OK = "OK";

  /** The Constant APPLY. */
  private static final String APPLY = "APPLY";

  /** The Constant CANCEL. */
  private static final String CANCEL = "CANCEL";

  /** The Constant RESTOREDEFAULTS. */
  private static final String RESTOREDEFAULTS = "RESTOREDEFAULTS";

  /** The pages tree. */
  private final URLTreeModel<List<IPreferencesPage>> pagesTree =
      new URLTreeModel<>(new URLTreeNode<List<IPreferencesPage>>("Preferences",
          null));

  /**
   * the SWING components that are created for each list of
   * {@link IPreferencesPage} to be shown in the preference dialog
   */
  private final Map<List<IPreferencesPage>, Component> components =
      new HashMap<>();

  /** The pages. */
  private final List<IPreferencesPage> pages = new ArrayList<>();

  /** The apply button. */
  private JButton applyButton;

  /** The cancel button. */
  private JButton cancelButton;

  /** The ok button. */
  private JButton okButton;

  /** The tree showing the available option pages. */
  private JTree tree;

  /** The page panel. */
  private JPanel pagePanel;

  /** The restore defaults button. */
  private JButton restoreDefaultsButton;

  /**
   * Instantiates a new preferences dialog.
   * 
   * @param wm
   *          the window manager to use
   */
  private PreferencesDialog(IWindowManager wm) {
    super(wm.getMainWindow());
    setTitle("Preferences");
    // get all perspectives and get their preference pages
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(this);

    initUI();

    List<IPerspective> allPerspectives =
        PerspectivesManager.getAvailablePerspectives();

    for (IPerspective perspective : allPerspectives) {
      if (perspective != null) {
        List<IPreferencesPage> preferencesPages =
            perspective.getPreferencesPages();
        if (preferencesPages != null) {
          for (IPreferencesPage page : preferencesPages) {
            if (page != null) {
              URLTreeNode<List<IPreferencesPage>> path;
              try {
                path = pagesTree.createPath(page.getLocation());
                List<IPreferencesPage> node = path.getAttachedObject();
                if (node == null) {
                  node = new ArrayList<>();
                  path.setAttachedObject(node);
                }

                try {
                  page.init();
                } catch (Exception e) {
                  SimSystem.report(e);
                }
                node.add(page);
                pages.add(page);
                page.addPreferencePageListener(this);

              } catch (MalformedURLException | UnsupportedEncodingException e) {
                SimSystem.report(e);
              }
            }
          }
        }
      }
    }

  }

  /**
   * Inits the ui.
   */
  private void initUI() {
    // have a tree view on the left, and a button box on the bottom, the right
    // will contain the preferences pages
    JPanel main = new JPanel(new BorderLayout());

    main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    tree = new JTree(pagesTree);
    tree.setPreferredSize(new Dimension(200, 200));
    tree.addTreeSelectionListener(this);
    tree.setShowsRootHandles(false);
    tree.setCellRenderer(new PreferencesTreeCellRenderer());
    tree.setRootVisible(false);

    main.add(new JScrollPane(tree), BorderLayout.LINE_START);

    restoreDefaultsButton = new JButton("Restore Defaults");
    restoreDefaultsButton.setActionCommand(RESTOREDEFAULTS);
    restoreDefaultsButton.addActionListener(this);
    okButton = new JButton("Ok");
    okButton.setActionCommand(OK);
    okButton.addActionListener(this);
    cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(CANCEL);
    cancelButton.addActionListener(this);
    applyButton = new JButton("Apply");
    applyButton.setActionCommand(APPLY);
    applyButton.addActionListener(this);

    Box buttonBox = Box.createVerticalBox();
    buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    Box buttons = Box.createHorizontalBox();
    buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    buttons.add(Box.createHorizontalGlue());
    buttons.add(restoreDefaultsButton);
    buttons.add(Box.createHorizontalStrut(10));
    buttons.add(applyButton);
    buttons.add(Box.createHorizontalStrut(20));
    buttons.add(okButton);
    buttons.add(Box.createHorizontalStrut(10));
    buttons.add(cancelButton);

    buttonBox.add(buttons);

    main.add(buttonBox, BorderLayout.SOUTH);

    pagePanel = new JPanel(new BorderLayout());
    main.add(pagePanel, BorderLayout.CENTER);

    setContentPane(main);

    setSize(new Dimension(800, 600));
    setLocationRelativeTo(null);
  }

  /**
   * Shows the preferences dialog.
   * 
   * @param wm
   *          the window manager to use
   */
  public static final void showPreferencesDialog(final IWindowManager wm) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        PreferencesDialog dialog = new PreferencesDialog(wm);
        dialog.validStateChanged(null);
        dialog.setModal(true);
        dialog.setVisible(true);
      }
    });
  }

  @Override
  public void windowActivated(WindowEvent e) {
  }

  @Override
  public void windowClosed(WindowEvent e) {
  }

  @Override
  public void windowClosing(WindowEvent e) {
    // cancel dialog
    cancel();
  }

  /**
   * Helper method that is invoked when the cancel button is clicked. It
   * delegates to the {@link IPreferencesPage#cancelPreferences()} method of all
   * preference pages.
   */
  private void cancel() {
    for (final IPreferencesPage p : pages) {
      if (p != null) {
        try {
          BasicUtilities.invokeAndWaitOnEDT(new Runnable() {

            @Override
            public void run() {
              p.cancelPreferences();
            }
          });
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }
    }
    close();
  }

  /**
   * Helper method that is invoked when the preferences dialog is to be closed.
   * It delegates to the {@link IPreferencesPage#closed()} method of all
   * preference pages.
   */
  private void close() {
    ITask task = new ClosePrefPagesTask("Closing Preferences Pages");

    TaskManager.addTask(task);

    setVisible(false);
    dispose();
  }

  @Override
  public void windowDeactivated(WindowEvent e) {
  }

  @Override
  public void windowDeiconified(WindowEvent e) {
  }

  @Override
  public void windowIconified(WindowEvent e) {
  }

  @Override
  public void windowOpened(WindowEvent e) {
    for (int i = 0; i < tree.getRowCount(); i++) {
      tree.expandRow(i);
    }
  }

  @Override
  public synchronized void validStateChanged(IPreferencesPage page) {
    // check all pages
    boolean valid = true;
    for (IPreferencesPage p : pages) {
      valid = valid && p.isValid();
    }
    final boolean v = valid;
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        // repaint tree to adjust to validity state
        tree.repaint();
        okButton.setEnabled(v);
        applyButton.setEnabled(v);
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public void valueChanged(TreeSelectionEvent e) {
    // get the selected tree item and display the preferences page/s
    TreePath selectionPath = tree.getSelectionPath();
    if (selectionPath == null) {
      return;
    }

    Object element = selectionPath.getLastPathComponent();
    if (element instanceof URLTreeNode) {
      List<IPreferencesPage> listOfPages =
          ((URLTreeNode<List<IPreferencesPage>>) element).getAttachedObject();
      // look up the component to display in the components map
      Component c = components.get(listOfPages);
      if (c == null) {
        c = createComponent(listOfPages);
      }

      pagePanel.removeAll();

      if (c != null) {
        SwingUtilities.updateComponentTreeUI(c);
        pagePanel.add(c, BorderLayout.CENTER);
      }

      pagePanel.revalidate();
      pagePanel.repaint();
    }
  }

  /**
   * Creates the SWING component that is used for a list of
   * {@link IPreferencesPage}s where if there is more than one page a
   * {@link JTabbedPane} is used to provide acces to all of them.
   * 
   * @param listOfPages
   *          the list of preference pages
   * @return a SWING component providing access to given preference pages
   */
  private JComponent createComponent(List<IPreferencesPage> listOfPages) {
    if (listOfPages == null) {
      return null;
    }

    JTabbedPane pane =
        new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

    for (IPreferencesPage page : listOfPages) {
      try {
        pane.addTab(page.getTitle(), page.getPageContent());
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }

    components.put(listOfPages, pane);

    return pane;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (OK.equals(e.getActionCommand())) {
      apply();
      close();
    }

    if (APPLY.equals(e.getActionCommand())) {
      apply();
    }

    if (CANCEL.equals(e.getActionCommand())) {
      cancel();
    }

    if (RESTOREDEFAULTS.equals(e.getActionCommand())) {
      restoreDefaults();
    }
  }

  /**
   * Helper method that is invoked when the restore button is clicked and it
   * delegates to {@link IPreferencesPage#restoreDefaults()} of all registering
   * pages
   */
  private synchronized void restoreDefaults() {
    ITask task = new RestoreDefaultsTask("Restoring Defaults");

    ProgressDialog.runTask(task);
  }

  /**
   * Helper method that is invoked when the restore button is clicked and it
   * delegates to {@link IPreferencesPage#applyPreferences(IProgressListener)}
   * of all registered pages.
   */
  private synchronized void apply() {
    ITask task = new AbstractTask("Applying") {

      @Override
      protected void task() {
        for (final IPreferencesPage p : pages) {
          if (p != null) {
            try {
              setTaskInfo(getName() + "...");
              BasicUtilities.invokeAndWaitOnEDT(new Runnable() {

                @Override
                public void run() {
                  p.applyPreferences(new IProgressListener() {

                    @Override
                    public void finished(Object source) {
                      fireFinished();
                    }

                    @Override
                    public void progress(Object source, float progress) {
                      setProgress(progress);
                    }

                    @Override
                    public void started(Object source) {
                      fireStarted();
                    }

                    @Override
                    public void taskInfo(Object source, String info) {
                      setTaskInfo(info);
                    }

                  });
                }
              });
            } catch (Exception e) {
              SimSystem.report(e);
            }
          }
        }
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
}
