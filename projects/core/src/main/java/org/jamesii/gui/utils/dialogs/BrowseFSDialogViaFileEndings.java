/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.application.preferences.Preferences;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.TaskManager;
import org.jamesii.gui.tooltip.StyledToolTip;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.ExpandingPanel;
import org.jamesii.gui.utils.FileChooser;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.utils.history.History;
import org.jamesii.gui.utils.history.HistoryTextField;

/**
 * Super class for dialogs that scan the file system for eligible files via a
 * list of file endings. The derived version {@link BrowseFSDialogViaFactories}
 * also allows to scan over a list of
 * {@link org.jamesii.core.data.IFileHandling}.
 * 
 * @author Gabriel Blum
 * @author Valerius Weigandt
 */
public class BrowseFSDialogViaFileEndings extends ApplicationDialog {

  /**
   * Thread to traverse the directories below the search path recursively.
   */
  class FileSearchingTask extends AbstractTask {

    /**
     * Instantiates a new file searching task.
     */
    public FileSearchingTask() {
      super("Scanning Files");
    }

    /** A flag if the expadingPanel was shown during a scan */
    private boolean expanded = false;

    /**
     * Recursive function to traverse through the directory tree.
     * 
     * @param path
     *          the path to the (sub-)tree root
     * @param fromPercent
     *          the from percent
     * @param toPercent
     *          the to percent
     */
    private void getFiles(File path, double fromPercent, double toPercent) {
      setTaskInfo(String.format("Scanning: %s", path.getName()));
      File[] list;
      list = path.listFiles();

      if (list == null || list.length <= 0) {
        return;
      }

      final double percentStep = (toPercent - fromPercent) / list.length;

      for (int i = 0; i < list.length; i++) {
        final File f = list[i];
        setProgress((float) (fromPercent + i * percentStep));
        if (isCancelled()) {
          return;
        }
        if (checkFile(f)) {
          try {
            BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
              @Override
              public void run() {
                model.addRow(getComponent(f));
              }
            });
          } catch (InterruptedException ex) {
            return;
          } catch (Exception ex) {
            SimSystem.report(ex);
            continue;
          }
        }
        if (f.isDirectory()) {
          try {
            BasicUtilities.invokeAndWaitOnEDT(new Runnable() {

              @Override
              public void run() {
                searchingDirectory.setText(f.getPath());
              }

            });
          } catch (InterruptedException | InvocationTargetException e) {
            SimSystem.report(e);
          }
          getFiles(f, fromPercent + i * percentStep, fromPercent + (i + 1)
              * percentStep);
        }
      }

      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          if (!expandingPanel.isExpanded() && model.isDataAvailable()
              && !expanded) {
            expanded = true;
            expandingPanel.setExpanded(true);
          }
        }
      });

    }

    @Override
    protected void task() {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          cancelButton.setEnabled(true);
        }
      });

      getFiles(new File(searchPath), 0f, 1f);
      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          cancelButton.setEnabled(false);
          if (model.isDataAvailable()) {
            searchingDirectory.setText("Finished.");
          } else {
            searchingDirectory.setText("Finished - nothing found");
          }
          expanded = false;
        }
      });
    }

    @Override
    protected void cancelTask() {
    }
  }

  /**
   * An derived version of the ExpandingPanel which also controls the size of
   * BrowseFSDialog.
   * 
   * @author Valerius Weigandt
   */
  private class InternExpandingPanel extends ExpandingPanel {

    /** The serialization */
    private static final long serialVersionUID = 3117095434509981568L;

    /** Saves the last high of the window */
    private int lastHigh = 0;

    /**
     * Initializes a new instance of the {@link InternExpandingPanel} class
     * using the given caption and expanding direction.
     * 
     * @param caption
     *          The caption for the panel.
     * @param expandingDirection
     *          The direction for the panel to expand to.
     */
    public InternExpandingPanel(String caption, int expandingDirection) {
      super(caption, expandingDirection);
    }

    @Override
    public void setExpanded(boolean isExpanded) {
      super.setExpanded(isExpanded);
      if (super.isExpanded()) {
        if (lastHigh <= 500) {
          BrowseFSDialogViaFileEndings.this.setSize(
              BrowseFSDialogViaFileEndings.this.getWidth(), 500);
        } else {
          BrowseFSDialogViaFileEndings.this.setSize(
              BrowseFSDialogViaFileEndings.this.getWidth(), lastHigh);
        }
      } else {
        lastHigh = BrowseFSDialogViaFileEndings.this.getHeight();
        BrowseFSDialogViaFileEndings.this.setSize(
            BrowseFSDialogViaFileEndings.this.getWidth(),
            BrowseFSDialogViaFileEndings.this.getPreferredSize().height);
      }
    }
  }

  /** Serialization ID. */
  private static final long serialVersionUID = 6516643861627073421L;

  /** Button to browse the file system for the search path. */
  private JButton browseButton = new JButton("Select...");

  /** Button to cancel the file thread. */
  private JButton cancelButton = new JButton("Cancel");

  /** File chooser. */
  private JFileChooser chooser = new FileChooser("org.jamesii.browse.select");

  /** Panel that holds UI for directory selection. */
  private JPanel dirSelectionPanel = new JPanel();

  /** The thread that traverses through the sub-directories. */
  private FileSearchingTask fileScanTask;

  /** Table model for the results. */
  private BrowseFSDialogTableModel model = new BrowseFSDialogTableModel();

  /** Table to hold the results. */
  private JTable results = new JTable(model);

  /** Button to start scanning. */
  private JButton scanButton = new JButton("Scan");

  /**
   * Label that displays the directory which is currently scanned (in the status
   * bar).
   */
  private JLabel searchingDirectory = new JLabel();

  /** The search path. */
  private String searchPath = null;

  /** Text field to displays the search path. */
  private HistoryTextField searchPathField = new HistoryTextField(25,
      "org.jamesii.used.directories", true, 10, History.LATEST);
  {
    searchPathField.setFilterPopUp(false);
  }

  /** Expanding panel for the results */
  private InternExpandingPanel expandingPanel = new InternExpandingPanel(
      "Results", javax.swing.SwingConstants.NORTH);

  /**
   * Configuration panel
   */
  private JPanel configPanel = new JPanel(new BorderLayout());

  /** ActionListener management */
  private ListenerSupport<IBrowseFSDialogListener> listenerSupport =
      new ListenerSupport<>();

  /**
   * Map: {@code file ending => check box}, to check its status to determine
   * whether the user has selected.
   */
  private Map<String, JCheckBox> checkBoxMap;

  /**
   * Map: {@code file ending => flag} that determines whether this file ending
   * should be included or not. Needs to be {@link Serializable}.
   */
  private Map<String, Boolean> checkMap;

  /** Panel for configuration of the file endings to display. */
  private JPanel fileEndingPanel;

  /** Size manager for then BrowseFSDialog */
  private transient ComponentAdapter frameAdapter = new ComponentAdapter() {
    @Override
    public void componentResized(java.awt.event.ComponentEvent e) {
      if (!expandingPanel.isExpanded()) {
        setSize(getWidth(),
            BrowseFSDialogViaFileEndings.this.getPreferredSize().height);
      }
    }
  };

  {
    results.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          if (results.getSelectedRow() != -1) {
            int index = results.getSelectedRow();
            IBrowseFSDialogEntry element = model.getData(index);
            for (IBrowseFSDialogListener l : listenerSupport) {
              l.elementChosen(element);
            }
          }
        }
      }
    });

  }

  {
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  }

  {
    browseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setSearchDirectory();
      }
    });
  }

  {
    scanButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchPath = searchPathField.getText();
        if (fileScanTask != null && !fileScanTask.isFinished()) {
          fileScanTask.cancel();
        }

        if (!searchPath.trim().equals("")) {
          model.clearTable();
          searchPathField.commit();
          fileScanTask = new FileSearchingTask();
          TaskManager.addTask(fileScanTask);
        }
      }
    });
  }

  {// Save the checkMap for the next start
    scanButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        for (String key : checkBoxMap.keySet()) {
          JCheckBox tempBox = checkBoxMap.get(key);
          checkMap.put(key, tempBox.isSelected());
        }
        Preferences.put("org.jamesii.browseModels.checkMap",
            (Serializable) checkMap);
      }
    });
  }

  {
    cancelButton.setEnabled(false);
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fileScanTask.cancel();
        cancelButton.setEnabled(false);
      }
    });
  }

  /**
   * Instantiates a new browse file search dialog.
   * 
   * @param owner
   *          the owner
   * @param title
   *          the title
   * @param fileEndings
   *          The file endings to browse for.<br>
   *          fileEnding->Description
   */
  public BrowseFSDialogViaFileEndings(Window owner, String title,
      Map<String, String> fileEndings) {
    super(owner);
    setTitle(title);
    setModal(true);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    JLabel directoryLabel = new JLabel("Current Directory: ");
    // results.setAutoCreateRowSorter(true);
    results.setFillsViewportHeight(true);
    results.setShowVerticalLines(false);

    results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    GroupLayout layout = new GroupLayout(dirSelectionPanel);
    dirSelectionPanel.setLayout(layout);

    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    // Top Control
    GroupLayout.SequentialGroup horizGroup = layout.createSequentialGroup();
    horizGroup.addComponent(directoryLabel);
    horizGroup.addComponent(searchPathField);
    horizGroup.addComponent(browseButton);
    horizGroup.addComponent(scanButton);
    horizGroup.addComponent(cancelButton);

    GroupLayout.ParallelGroup vertGroup =
        layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
    vertGroup.addComponent(directoryLabel);
    vertGroup.addComponent(searchPathField);
    vertGroup.addComponent(browseButton);
    vertGroup.addComponent(scanButton);
    vertGroup.addComponent(cancelButton);

    layout.setHorizontalGroup(horizGroup);
    layout.setVerticalGroup(vertGroup);

    configPanel.add(dirSelectionPanel, BorderLayout.CENTER);

    // contentPane.setLayout(new BorderLayout());
    contentPane.add(configPanel, BorderLayout.NORTH);
    // Center
    expandingPanel.getInnerPanel().setLayout(new BorderLayout());
    expandingPanel.getInnerPanel().add(new JScrollPane(results),
        BorderLayout.CENTER);
    results.setFillsViewportHeight(true);
    expandingPanel.setOpaque(true);

    results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));

    contentPane.add(expandingPanel, BorderLayout.CENTER);

    expandingPanel.getInnerPanel().setLayout(new BorderLayout());
    expandingPanel.getInnerPanel().add(new JScrollPane(results),
        BorderLayout.CENTER);
    expandingPanel.setOpaque(true);

    contentPane.add(expandingPanel, BorderLayout.CENTER);

    searchingDirectory.setText("Click scan to search");
    contentPane.add(searchingDirectory, BorderLayout.SOUTH);

    addComponentListener(frameAdapter);

    setConfigComponent(fileEndings);
  }

  /**
   * Adds additional components to the configuration panel.
   * 
   * @param component
   *          The component to add.
   */
  protected void setAddionalConfigComponent(Component component) {
    if (component != null) {
      configPanel.add(component, BorderLayout.SOUTH);
    }
  }

  /**
   * Generates a configuration panel with check boxes for the given file
   * endings. It is also possible to add own components via
   * {@link #setAddionalConfigComponent} instead using this function.
   * 
   * @param fileEndings
   *          List of strings with the file endings to browse for.
   */
  protected final void setConfigComponent(Map<String, String> fileEndings) {
    if (fileEndings == null) {
      return;
    }
    fileEndingPanel = new JPanel();
    checkBoxMap = new HashMap<>();
    checkMap = new HashMap<>();
    Map<String, Boolean> lastCheckMap =
        Preferences.get("org.jamesii.browseModels.checkMap");
    JCheckBox box;
    for (Entry<String, String> next : fileEndings.entrySet()) {
      String key = next.getKey().trim().toLowerCase();

      box = new JCheckBox(key, true) {
        private static final long serialVersionUID = 9030725250888084156L;

        @Override
        public JToolTip createToolTip() {
          return new StyledToolTip();
        }
      };
      if (lastCheckMap != null && lastCheckMap.containsKey(key)) {
        box.setSelected(lastCheckMap.get(key));
        if (next.getValue() != null) {
          box.setToolTipText(next.getValue());
        }
      }

      checkBoxMap.put(key, box);
      fileEndingPanel.add(box);

    }
    configPanel.add(fileEndingPanel, BorderLayout.SOUTH);
  }

  /**
   * Adds a listener. A listener is only added if it is not {@code null} and is
   * not already registered.
   * 
   * @param l
   *          the listener to add
   */
  public final void addBrowseFSDialogListener(IBrowseFSDialogListener l) {
    listenerSupport.addListener(l);
  }

  /**
   * Removes a previously registered listener. If the listener was not
   * registered before no action will be performed.
   * 
   * @param l
   *          the listener to remove
   */
  public final void removeBrowseFSDialogListener(IBrowseFSDialogListener l) {
    listenerSupport.removeListener(l);
  }

  @Override
  public final void setVisible(boolean visible) {
    if (!visible && fileScanTask != null && !fileScanTask.isFinished()) {
      fileScanTask.cancel();
    }
    if (visible) {
      pack();
      setMinimumSize(getPreferredSize());
      setLocationRelativeTo(getOwner());
    }
    super.setVisible(visible);
  }

  /**
   * Check file.
   * 
   * @param f
   *          the file
   * @return true, if successful
   */
  protected boolean checkFile(File f) {
    String ending = Files.getFileEnding(f).toLowerCase();
    return f.isFile() && checkMap.containsKey(ending) && checkMap.get(ending);
  }

  /**
   * Gets the component.
   * 
   * @param f
   *          the f
   * @return the component data
   */
  protected IBrowseFSDialogEntry getComponent(File f) {
    return new BrowseDialogFileComponent(f);
  }

  /**
   * Set the search directory via a file chooser.
   */
  private void setSearchDirectory() {

    if (this.chooser.showDialog(this, "Choose Directory") != JFileChooser.APPROVE_OPTION) {
      return;
    }

    searchPath =
        chooser.getSelectedFile() == null ? chooser.getCurrentDirectory()
            .getPath() : chooser.getSelectedFile().getPath();
    searchPathField.setText(searchPath);
  }
}
