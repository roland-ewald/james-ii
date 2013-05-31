/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.TaskManager;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.FlatButton;

/**
 * Simple component that displays the currently used memory compared to the one
 * that is available at most. The display is being updated every second so you
 * don't need to care about that.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class MemoryMonitor extends JPanel {

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class MemoryOverviewTimerTask extends TimerTask {
    @Override
    public void run() {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {
        @Override
        public void run() {
          // calculate used memory and display in relation to max memory
          final long freeMemory = Runtime.getRuntime().freeMemory();
          final long totalMemory = Runtime.getRuntime().totalMemory();
          final long maxMemory = Runtime.getRuntime().maxMemory();

          String text = getMemoryInfo();
          setToolTipText(text);
          MemoryMonitor.this.progressBar
              .setValue((int) ((totalMemory - freeMemory)
                  * MemoryMonitor.this.progressBar.getMaximum() / maxMemory));
          MemoryMonitor.this.progressBar.setString(text);
          MemoryMonitor.this.progressBar.revalidate();
        }
      });
    }
  }

  /**
   * Executing the garbage collector as task.
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class RunGCTask extends AbstractTask {
    private RunGCTask(String taskName) {
      super(taskName);
    }

    @Override
    protected void task() {
      try {
        BasicUtilities.invokeAndWaitOnEDT(new Runnable() {

          @Override
          public void run() {
            gcButton.setEnabled(false);
          }

        });
        System.gc(); // NOSONAR: garbage collection button
        Thread.sleep(100);
        BasicUtilities.invokeAndWaitOnEDT(new Runnable() {

          @Override
          public void run() {
            gcButton.setEnabled(true);
          }

        });
      } catch (InterruptedException | InvocationTargetException e) {
      }
    }

    @Override
    protected void cancelTask() {
    }
  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -4735443327535317719L;

  /**
   * progress bar used for memory consumption visualization
   */
  private final JProgressBar progressBar;

  /**
   * garbage collector button
   */
  private final JButton gcButton;

  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
    // return new Dimension(200, size.height);
  }

  /**
   * Creates a new memory monitor with or without button for manual garbage
   * collection
   * 
   * @param displayGC
   *          true if button for garbage collection should be enabled
   */
  public MemoryMonitor(boolean displayGC) {
    this();
    setDisplayGC(displayGC);
  }

  /**
   * Creates a new memory monitor display
   */
  public MemoryMonitor() {
    super(new BorderLayout());
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);

    super.addImpl(progressBar, BorderLayout.CENTER, 0);

    Icon gcIcon = IconManager.getIcon(IconIdentifier.DELETE_SMALL, "GC");

    gcButton = new FlatButton(gcIcon);
    gcButton.setToolTipText("Run Garbage Collector");
    gcButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ITask task = new RunGCTask("Running GC");
        TaskManager.addTask(task);
      }

    });

    super.addImpl(gcButton, BorderLayout.EAST, 0);

    // schedule timer to update monitor every second
    Timer timer = new Timer(true);
    timer.scheduleAtFixedRate(new MemoryOverviewTimerTask(), 100, 2000);
  }

  /**
   * Enables or disables the garbage collection button
   * 
   * @param x
   *          true if manual garbage collection should be allowed
   */
  public void setDisplayGC(boolean x) {
    gcButton.setVisible(x);
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException(
        "Can't add components to this component!");
  }

  public static String getMemoryInfo() {
    final long freeMemory = Runtime.getRuntime().freeMemory();
    final long totalMemory = Runtime.getRuntime().totalMemory();
    final long maxMemory = Runtime.getRuntime().maxMemory();

    String text =
        String.format("using %s of %s",
            Files.convertToHumanReadableSize(totalMemory - freeMemory, "B", 0),
            Files.convertToHumanReadableSize(maxMemory, "B", 0));

    return text;
  }
}
