/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.table.AbstractTableModel;

/**
 * Simple table model encapsulating the {@link TaskManager} to observer tasks
 * from point of addition, over starting, progressing and finishing.
 * 
 * @author Stefan Rybacki
 * 
 */
final class TaskManagerTableModel extends AbstractTableModel implements
    ITaskManagerListener {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7824704561721304559L;

  /**
   * singleton instance of table model
   */
  private static final TaskManagerTableModel instance =
      new TaskManagerTableModel();

  /**
   * column with task name
   */
  public static final int NAME_COLUMN = 0;

  /**
   * column in which the task information are provided
   */
  public static final int TASK_COLUMN = 1;

  /**
   * column in which the progress information are provided
   */
  public static final int PROGRESS_COLUMN = 2;

  /**
   * column in which the blocking property of a task is populated
   */
  public static final int BLOCKING_COLUMN = 3;

  /**
   * cached list of tasks that is adjusting according to added and finished
   * events sent by the task manager
   */
  private final List<ITask> tasks;

  /**
   * A map of already finished tasks that are removed after a certain amount of
   * being finished
   */
  private final Map<ITask, Calendar> oldTasks = new HashMap<>();

  /**
   * used to remove finished tasks from model after a certain amount of time
   */
  private final Timer timer = new Timer(true);

  /**
   * lock for old task map
   */
  private final Lock oldTaskLock = new ReentrantLock();

  /**
   * Omitted constructor.
   */
  private TaskManagerTableModel() {
    super();
    tasks = TaskManager.getTasks();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Calendar now = Calendar.getInstance();
        oldTaskLock.lock();
        try {
          ITask[] keySet =
              oldTasks.keySet().toArray(new ITask[oldTasks.size()]);
          for (ITask t : keySet) {
            // remove finished tasks older than 5 seconds from table model
            if (now.getTimeInMillis() - oldTasks.get(t).getTimeInMillis() > 5000) {
              removeTask(t);
            }
          }
        } finally {
          oldTaskLock.unlock();
        }

      }
    }, 100, 250);
    TaskManager.addTaskListener(this);
    fireTableDataChanged();
  }

  /**
   * Helper method that removes a task from the model
   * 
   * @param task
   *          the task to remove
   */
  private synchronized void removeTask(ITask task) {
    int i = tasks.indexOf(task);
    if (i >= 0) {
      tasks.remove(i);
      oldTasks.remove(task);
      fireTableRowsDeleted(i, i);
    }
  }

  @Override
  public synchronized void finished(Object source) {
    int i = tasks.indexOf(source);
    if (i >= 0) {
      oldTaskLock.lock();
      try {
        oldTasks.put((ITask) source, Calendar.getInstance());
      } finally {
        oldTaskLock.unlock();
      }

      fireTableRowsUpdated(i, i);
    }
  }

  @Override
  public synchronized void progress(Object source, float progress) {
    int i = tasks.indexOf(source);
    if (i >= 0) {
      fireTableCellUpdated(i, PROGRESS_COLUMN);
    }
  }

  @Override
  public synchronized void started(Object source) {
  }

  @Override
  public synchronized void taskInfo(Object source, String info) {
    int i = tasks.indexOf(source);
    if (i >= 0) {
      fireTableCellUpdated(i, TASK_COLUMN);
    }
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public String getColumnName(int column) {
    switch (column) {
    case NAME_COLUMN:
      return "Task";
    case PROGRESS_COLUMN:
      return "Progress";
    case TASK_COLUMN:
      return "Current action";
    case BLOCKING_COLUMN:
      return "Blocking";
    default:
      return null;
    }
  }

  @Override
  public synchronized int getRowCount() {
    return tasks.size();
  }

  @Override
  public synchronized Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex >= 0 && rowIndex < tasks.size()) {
      ITask task = tasks.get(rowIndex);
      if (task.isFinished()) {
        if (columnIndex == NAME_COLUMN) {
          return task.getName();
        }
        if (columnIndex == TASK_COLUMN) {
          return "Done " + (task.isCancelled() ? "(Cancelled)" : "");
        }
        return null;
      }
      switch (columnIndex) {
      case NAME_COLUMN:
        return task.getName();
      case PROGRESS_COLUMN:
        return task;
      case TASK_COLUMN:
        return task.getTaskInfo();
      case BLOCKING_COLUMN:
        return task.isBlocking();
      }
    }
    return null;
  }

  @Override
  public void taskAdded(ITask task) {
    synchronized (tasks) {
      tasks.add(task);
      fireTableRowsInserted(tasks.size() - 1, tasks.size() - 1);
    }
  }

  @Override
  public void taskFinished(ITask task) {
    finished(task);
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
    case PROGRESS_COLUMN:
      return IProgress.class;
    case BLOCKING_COLUMN:
      return Boolean.class;
    default:
      return super.getColumnClass(columnIndex);
    }
  }

  /**
   * @return a singleton instance of this table model
   */
  public static TaskManagerTableModel getInstance() {
    return instance;
  }

  @Override
  public void taskStarted(ITask task) {
    int i = tasks.indexOf(task);
    if (i >= 0) {
      fireTableRowsUpdated(i, i);
    }
  }

}
