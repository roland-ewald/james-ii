/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jamesii.SimSystem;
import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.utils.ProgressAdapter;

/**
 * Class that can execute {@link ITask}s using a thread pool while respecting
 * blocking requirement of given tasks. This executor is thread safe.
 * <p>
 * Because of using a thread pool and the ability to block The user must take
 * care of the following requirements:
 * <ul>
 * <li>tasks should not synchronize between each other due to the risk of dead
 * locks</li>
 * <li>blocking tasks should only be used if they are really needed because they
 * are blocking subsequent tasks from executing</li>
 * </ul>
 * 
 * @author Stefan Rybacki
 * 
 * @see ITask
 * @see TaskManager
 */
class TaskExecutor {
  /**
   * queue that holds all tasks that are sent to the thread pool but not yet
   * assigned to a thread
   */
  private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

  /**
   * flag indicating whether there is currently a blocking task running
   */
  private boolean blocked = false;

  /**
   * read write lock for blocked flag
   */
  private final Lock lock = new ReentrantLock();

  /**
   * write lock for all queues so that there can only be one thread that works
   * on the queues at a time
   */
  private final Lock updateLock = new ReentrantLock();

  /**
   * thread pool executor used to execute tasks
   */
  private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 1,
      TimeUnit.MINUTES, tasks);

  /**
   * additional queue that holds all tasks that are added while a blocking task
   * is running those tasks are added to the thread pool once the blocking task
   * is finished.
   */
  private final Queue<ITask> delayedTasks = new ArrayDeque<>();

  /**
   * support for progress listeners
   */
  private final ListenerSupport<IProgressListener> listeners =
      new ListenerSupport<>();

  /**
   * list of running tasks
   */
  private final List<ITask> runningTasks = new ArrayList<>();

  /**
   * write lock for running tasks list
   */
  private final Lock runningTasksLock = new ReentrantLock();

  /**
   * Creates a new task executor
   */
  public TaskExecutor() {
    executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

      @Override
      public void rejectedExecution(Runnable r, ThreadPoolExecutor ex) {
        // if rejected delay task
        System.out.println("Rejected: " + r + " <-- delayed!");

        // TODO sr137: this only works if there is no blocking task otherwise a
        // blocking
        // rejected task might not be delayed and the order of other tasks
        // changes
        // so use a maximum blocking queue to avoid rejection

        // task is discarded

      }

    });
  }

  /**
   * Adds a progress listener
   * 
   * @param l
   *          the listener to add
   */
  public void addProgressListener(IProgressListener l) {
    listeners.addListener(l);
  }

  /**
   * Removes a previously attached progress listener
   * 
   * @param l
   *          the listener to remove
   */
  public void removeProgressListener(IProgressListener l) {
    listeners.removeListener(l);
  }

  /**
   * Fires a task started event on all registered listeners
   * 
   * @param task
   *          the task that has been started
   */
  private synchronized void fireTaskStarted(ITask task) {
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.started(task);
      }
    }
  }

  /**
   * Fires a task finished event on all registered listeners
   * 
   * @param task
   *          the task that has finished
   */
  private synchronized void fireTaskFinished(ITask task) {
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.finished(task);
      }
    }
  }

  /**
   * Fires a progress changed event on all registered listeners
   * 
   * @param task
   *          the task whose progress state changed
   * @param progress
   *          the new progress value (can also be retrieved by
   *          {@link ITask#getProgress()})
   */
  private synchronized void fireTaskProgress(ITask task, float progress) {
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.progress(task, progress);
      }
    }
  }

  /**
   * Fires a task info change event on all registered listeners
   * 
   * @param task
   *          the task whose task info changed
   * @param info
   *          the new info (can also be retrieved by {@link ITask#getTaskInfo()}
   *          )
   */
  private synchronized void fireTaskTaskInfo(ITask task, String info) {
    for (IProgressListener l : listeners.getListeners()) {
      if (l != null) {
        l.taskInfo(task, info);
      }
    }
  }

  /**
   * Adds the specified task to the queue of tasks that are supposed to be
   * executed. Tasks are enqueued to a thread pool and are executed once a spot
   * is open in the pool. An exception exists. Once there is a blocking task
   * running/added all subsequent added tasks are not executed before that
   * blocking task has finished.
   * 
   * @param task
   *          the task to add to the queue
   */
  public void addTask(ITask task) {
    // lock for queue update
    updateLock.lock();
    // lock for blocked instance
    lock.lock();
    try {
      if (blocked) {
        delayTask(task);
      } else {
        blocked = task.isBlocking();
        executeTask(task);
      }
    } finally {
      lock.unlock();
      updateLock.unlock();
    }

  }

  /**
   * Helper method that encapsulates a task in a {@link TaskRunnable} object to
   * be able to determine task start, task end, to reenqueue delayed tasks due
   * to a blocking task and to unblock after a blocking task has finished resp.
   * to block if a blocking task has started. That {@link TaskRunnable} object
   * is then sent to the thread pool queue.
   * 
   * @param task
   *          the task to execute.
   */
  private void executeTask(final ITask task) {
    final Runnable r = new TaskRunnable() {
      @Override
      public ITask getTask() {
        return task;
      }

      @Override
      public void run() {
        fireTaskStarted(task);
        runningTasksLock.lock();
        try {
          runningTasks.add(task);
        } finally {
          runningTasksLock.unlock();
        }
        IProgressListener l = new ProgressAdapter() {
          @Override
          public void progress(Object source, float progress) {
            fireTaskProgress(task, progress);
          }

          @Override
          public void taskInfo(Object source, String info) {
            fireTaskTaskInfo(task, info);
          }
        };
        task.addProgressListener(l);
        // make sure the task can't crash the executor
        try {
          task.run();
        } catch (Throwable e) {
          SimSystem.report(e);
        }

        if (task.isBlocking()) {
          // lock until done so that there is no concurrent
          updateLock.lock();

          lock.lock();
          try {
            blocked = false;
          } finally {
            lock.unlock();
          }

          // re-enqueue delayed tasks
          try {
            int c = delayedTasks.size();
            for (int i = 0; i < c; i++) {
              addTask(delayedTasks.poll());
            }
          } finally {
            updateLock.unlock();
          }
        }
        task.removeProgressListener(l);
        fireTaskFinished(task);
        runningTasksLock.lock();
        try {
          runningTasks.remove(task);
        } finally {
          runningTasksLock.unlock();
        }
      }
    };

    executor.execute(r);
  }

  /**
   * Helper method that puts the specified task on the delayed queue. Which is
   * needed if the thread pool is currently blocked by a blocking task and no
   * newly added tasks are supposed to execute until that task has unblocked;
   * 
   * @param task
   *          the task to delay
   */
  private void delayTask(ITask task) {
    if (task != null && !delayedTasks.contains(task)) {
      delayedTasks.add(task);
    }
  }

  /**
   * @return all tasks that are currently running or enqueued
   */
  public List<ITask> getTasks() {
    List<ITask> list = new ArrayList<>();
    runningTasksLock.lock();
    updateLock.lock();
    try {
      list.addAll(getRunningTasks());
      list.addAll(getQueuedTasks());
    } finally {
      updateLock.unlock();
      runningTasksLock.unlock();
    }
    return list;
  }

  /**
   * @return all currently running tasks
   */
  public List<ITask> getRunningTasks() {
    runningTasksLock.lock();
    try {
      return new ArrayList<>(runningTasks);
    } finally {
      runningTasksLock.unlock();
    }
  }

  /**
   * @return all currently enqueued tasks
   */
  public List<ITask> getQueuedTasks() {
    List<ITask> list = new ArrayList<>();
    updateLock.lock();
    try {
      // add tasks from tasks queue
      Runnable rs[] = tasks.toArray(new Runnable[tasks.size()]);
      for (Runnable r : rs) {
        if (r instanceof TaskRunnable) {
          list.add(((TaskRunnable) r).getTask());
        }
      }
      list.addAll(delayedTasks);
    } finally {
      updateLock.unlock();
    }
    return list;
  }

  /**
   * Helper interface that is used to encapsulate a task as runnable which is
   * able to return the actual task object attached.
   * 
   * @author Stefan Rybacki
   * 
   */

  private interface TaskRunnable extends Runnable {
    /**
     * @return the attached task
     */
    ITask getTask();
  }

  /**
   * @param task
   *          the task in question
   * @return true if specified task is currently running, false else (also
   *         false, if that task is not in the task list at all)
   */
  public boolean isRunning(ITask task) {
    return runningTasks.contains(task);
  }

  /**
   * @param task
   *          the task in question
   * @return true if the task is still on the queue (therefore not running),
   *         false else (also false if not in the task list at all)
   */
  public boolean isQueued(ITask task) {
    return tasks.contains(task) || delayedTasks.contains(task);
  }

  /**
   * @return the number of tasks (running + enqueued)
   */
  public int getTaskCount() {
    int result = 0;
    runningTasksLock.lock();
    updateLock.lock();
    try {
      result = tasks.size() + runningTasks.size() + delayedTasks.size();
    } finally {
      updateLock.unlock();
      runningTasksLock.unlock();
    }

    return result;
  }

  /**
   * Removes a queued task (running tasks are not affected!) if the task can be
   * found in the queued tasks. If the specified task cannot be found queued no
   * action will be performed.
   * 
   * @param task
   *          the task to remove if queued
   */
  public void removeTask(ITask task) {
    runningTasksLock.lock();
    updateLock.lock();
    try {
      // add tasks from tasks queue
      Runnable rs[] = tasks.toArray(new Runnable[tasks.size()]);
      for (Runnable r : rs) {
        if (r instanceof TaskRunnable) {
          if ((((TaskRunnable) r).getTask()) == task) {
            tasks.remove(r);
          }
        }
      }
    } finally {
      updateLock.unlock();
      runningTasksLock.unlock();
    }

  }
}
