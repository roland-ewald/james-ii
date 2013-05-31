/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import org.jamesii.SimSystem;
import org.jamesii.core.util.ICallBack;
import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.utils.ProgressAdapter;

/**
 * Task manager that manages {@link ITask}s utilizing a {@link TaskExecutor} to
 * execute the added tasks. It provides a facade to the internals of the
 * executor and wraps it in a static class using the singleton pattern.
 * Basically you'd use the task manager as follows:
 * <p>
 * <code><pre>
 *  ITask someTask=new SomeTask();
 *  
 *  TaskManager.addTask(someTask);</pre>
 *  </code>
 * <p>
 * {@code someTask} will be executed as soon as possible, competing with other
 * tasks added. Possible implies that tasks added after a "blocking" task, are
 * not executed before that task has finished its execution. <br/>
 * To follow the tasks execution, that means to follow when it was added,
 * started, finished and when the progress changes attach a
 * {@link ITaskManagerListener} to the task manager. This has the advantage over
 * using an {@link IProgressListener} directly on the task because it does not
 * rely on the correct implementation of the progress notification by the task
 * in order to provide the added, started and finished events.
 * 
 * @author Stefan Rybacki
 */
// FIXME sr137: if the app closes and there are still tasks in queue
// this should
// be handled somehow
// TODO sr137: implement a more sophisticated version of task manager
// where you
// can specify the priority of a task (e.g. immediate)
// and where you can specify which task blocks which or which task has
// to wait
// for which, try to use a locking mechanism so that tasks only block
// on the
// same locks where those locks
// might belong to a resource
public final class TaskManager extends ProgressAdapter {
  /**
   * the task executor used to execute the added tasks
   */
  private final TaskExecutor executor = new TaskExecutor();

  /**
   * support for {@link ITaskManagerListener}s
   */
  private final ListenerSupport<ITaskManagerListener> taskListeners =
      new ListenerSupport<>();

  /**
   * the singleton instance
   */
  private static final TaskManager instance = new TaskManager();

  /**
   * Omitted constructor to avoid multiple instances
   */
  private TaskManager() {
    executor.addProgressListener(this);
  }

  /**
   * Adds a given {@link ITaskManagerListener}
   * 
   * @param l
   *          the listener to add
   */
  public static synchronized void addTaskListener(ITaskManagerListener l) {
    instance.taskListeners.addListener(l);
  }

  /**
   * Removes a previously added listener
   * 
   * @param l
   *          the listener to remove
   */
  public static synchronized void removeTaskListener(ITaskManagerListener l) {
    instance.taskListeners.addListener(l);
  }

  /**
   * Adds the given task and tries to execute it according to blocking tasks and
   * already added tasks.
   * <p>
   * <b>important:</b>
   * <ul>
   * <li>tasks should not synchronize between each other due to the risk of dead
   * locks</li>
   * <li>blocking tasks should only be used if they are really needed because
   * they are blocking subsequent tasks from executing</li>
   * </ul>
   * 
   * @param task
   *          the task to add
   */
  public static synchronized void addTask(ITask task) {
    if (task.isFinished() || task.isCancelled() || isRunning(task)) {
      throw new RuntimeException(
          "Task can't be added because it might be already running, already finished or was canceld before!");
    }
    instance.executor.addTask(task);
    instance.fireTaskAdded(task);
  }

  /**
   * Helper method that fires a task added event
   * 
   * @param task
   *          the task that was added
   */
  private synchronized void fireTaskAdded(ITask task) {
    for (ITaskManagerListener l : taskListeners.getListeners()) {
      if (l != null) {
        l.taskAdded(task);
      }
    }
  }

  /**
   * Helper method that fires a task finished event
   * 
   * @param task
   *          the task that has finished
   */
  private synchronized void fireTaskFinished(ITask task) {
    for (ITaskManagerListener l : taskListeners.getListeners()) {
      if (l != null) {
        l.taskFinished(task);
      }
    }
  }

  /**
   * Helper method that forwards the {@link IProgressListener#finished(Object)}
   * events
   * 
   * @param task
   *          the task sending the event
   */
  private synchronized void fireFinished(ITask task) {
    for (ITaskManagerListener l : taskListeners.getListeners()) {
      if (l != null) {
        l.finished(task);
      }
    }
  }

  /**
   * Helper method that forwards the {@link IProgressListener#started(Object)}
   * events
   * 
   * @param task
   *          the task that started
   */
  private synchronized void fireStarted(ITask task) {
    for (ITaskManagerListener l : taskListeners.getListeners()) {
      if (l != null) {
        l.taskStarted(task);
        l.started(task);
      }
    }
  }

  /**
   * Helper method that forwards the
   * {@link IProgressListener#taskInfo(Object, String)} events.
   * 
   * @param task
   *          The task the information is provided for.
   * @param info
   *          The information about specified task.
   */
  private synchronized void fireTaskInfo(ITask task, String info) {
    for (ITaskManagerListener l : taskListeners.getListeners()) {
      if (l != null) {
        l.taskInfo(task, info);
      }
    }
  }

  /**
   * Helper method that forwards the
   * {@link IProgressListener#progress(Object, float)} events
   * 
   * @param task
   *          The task the progress is provided for.
   * @param progress
   *          The progress the specified task has made where 0 means no progress
   *          and 1 means task completed. (Note: if progress is >=1
   *          {@link #fireTaskFinished(ITask)} is not automatically executed)
   */
  private synchronized void fireProgress(ITask task, float progress) {
    for (ITaskManagerListener l : taskListeners.getListeners()) {
      if (l != null) {
        l.progress(task, progress);
      }
    }
  }

  /**
   * @return a list of currently running tasks
   */
  public static synchronized List<ITask> getRunningTasks() {
    return instance.executor.getRunningTasks();
  }

  /**
   * @return a list of currently enqueued tasks
   */
  public static synchronized List<ITask> getWaitingTasks() {
    return instance.executor.getQueuedTasks();
  }

  /**
   * @return a list of all tasks (running+queued)
   */
  public static synchronized List<ITask> getTasks() {
    return instance.executor.getTasks();
  }

  /**
   * @param task
   *          the task in question
   * @return true if given task is currently running
   */
  public static synchronized boolean isRunning(ITask task) {
    return instance.executor.isRunning(task);
  }

  /**
   * @param task
   *          the task in question
   * @return true if given task is currently enqueued (false doesn't mean it is
   *         running)
   */
  public static synchronized boolean isWaiting(ITask task) {
    return instance.executor.isQueued(task);
  }

  /**
   * Cancels the specified task. If the specified task is not currently running
   * but scheduled to run it will be removed from schedule and cancellation on
   * that task is still performed.
   * 
   * @param task
   *          the task to cancel
   */
  public static synchronized void cancelTask(ITask task) {
    if (task.isFinished() || task.isCancelled()) {
      return;
    }
    task.cancel();
    instance.executor.removeTask(task);
  }

  /**
   * Cancels the specified task. If the specified task is not currently running
   * but scheduled to run it will be removed from schedule and cancellation on
   * that task is still performed.
   * 
   * @param task
   *          the task to cancel
   * @param timeout
   *          the timeout in milliseconds specifying when the given callback is
   *          notified
   * @param callback
   *          the callback that is notified when the timeout reached when
   *          canceling this task
   */
  public static synchronized void cancelTask(final ITask task, long timeout,
      final ICallBack<ITask> callback) {
    cancelTask(task);
    Timer timer = new Timer(true);
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        if (task.isFinished() || callback.process(task)) {
          // TODO sr137: maybe the task manager should kill the task
          // if process returns false and
          // wait has to be implemented by the callback?!
          cancel();
        }
      }

    }, timeout, timeout);
  }

  /**
   * @return the number of current tasks
   */
  public static int getTaskCount() {
    return instance.executor.getTaskCount();
  }

  @Override
  public void progress(Object source, float progress) {
    if (source instanceof ITask) {
      fireProgress((ITask) source, progress);
    }
  }

  @Override
  public void finished(Object source) {
    if (source instanceof ITask) {
      fireFinished((ITask) source);
      fireTaskFinished((ITask) source);
    }
  }

  @Override
  public void started(Object source) {
    if (source instanceof ITask) {
      fireStarted((ITask) source);
    }
  }

  @Override
  public void taskInfo(Object source, String info) {
    if (source instanceof ITask) {
      fireTaskInfo((ITask) source, info);
    }
  }

  /**
   * Adds the specific task and blocks until the task finished executing.
   * 
   * @param taskToAdd
   *          the task to add, execute and to wait for
   */
  public static void addTaskAndWait(final ITask taskToAdd) {
    final Semaphore taskEnd = new Semaphore(0);

    final ITaskManagerListener listener = new TaskManagerAdapter() {
      @Override
      public void taskFinished(ITask task) {
        if (task == taskToAdd) {
          taskEnd.release();
        }
      }
    };

    addTaskListener(listener);
    addTask(taskToAdd);
    try {
      taskEnd.acquire();
    } catch (InterruptedException e) {
      SimSystem.report(e);
    }
    removeTaskListener(listener);
  }
}
