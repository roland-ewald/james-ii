/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jamesii.ChattyTestCase;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.TaskExecutor;

/**
 * @author Stefan Rybacki
 * 
 */
public class SequentialTaskTest extends ChattyTestCase {
  private static final class FailRunnable implements Runnable {

    private final InterruptedException e;

    /**
     * @param e
     */
    private FailRunnable(InterruptedException e) {
      this.e = e;
    }

    @Override
    public void run() {
      fail(e.getMessage());
    }
  }

  private static final class AssertTrueRunnable implements Runnable {
    private final boolean r;

    /**
     * @param e
     */
    private AssertTrueRunnable(boolean r) {
      this.r = r;
    }

    @Override
    public void run() {
      assertTrue(r);
    }
  }

  /**
   * Milliseconds in Minute
   */
  private static final int MSINMINUTE = 60000;

  /**
   * The executor used for testing.
   */
  private TaskExecutor executor;

  private BlockingQueue<Runnable> assertQueue = new LinkedBlockingQueue<>();

  /**
   * The Constant runTime of each task simulating a operation that takes a
   * specific time.
   */
  private static final int RUNTIME = 100;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    executor = new TaskExecutor();
  }

  /**
   * Helper method that executes collected asserts in main thread
   */
  private void checkAssertsInThreads() {
    Runnable r;
    while ((r = assertQueue.poll()) != null) {
      r.run();
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.task.TaskExecutor#TaskExecutor()}.
   */
  public final void testTaskExecutor() {
    assertNotNull(executor);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.task.TaskExecutor#addTask(org.jamesii.gui.application.task.ITask)}
   * .
   */
  public final void testAddTaskSimple() {
    // first test tasks added from one thread only and test whether tasks added
    // after a blocking task are executed after
    // the blocking task has finished
    final Semaphore s = new Semaphore(0);
    final Semaphore blocking = new Semaphore(0);

    ITask basicTaskBeforeBlocking = new AbstractTask() {
      @Override
      public void task() {
        try {
          Thread.sleep(RUNTIME);
        } catch (final InterruptedException e) {
          assertQueue.add(new FailRunnable(e));
        }
      }

      @Override
      protected void cancelTask() {
      }

    };

    ITask blockingTask = new AbstractTask() {

      @Override
      public void task() {
        try {
          Thread.sleep(RUNTIME);
          blocking.release();
        } catch (final InterruptedException e) {
          assertQueue.add(new FailRunnable(e));
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

    ITask basicTaskAfterBlocking = new AbstractTask() {

      @Override
      public void task() {
        // check whether blocking task already finished
        try {
          assertQueue.add(new AssertTrueRunnable(blocking.tryAcquire()));
          Thread.sleep(RUNTIME);
        } catch (final InterruptedException e) {
          assertQueue.add(new FailRunnable(e));
        } finally {
          s.release();
        }

      }

      @Override
      protected void cancelTask() {
      }

    };

    executor.addTask(basicTaskBeforeBlocking);
    executor.addTask(blockingTask);
    executor.addTask(basicTaskAfterBlocking);

    try {
      // wait at least for runtime of all tasks + a minute
      assertQueue.add(new AssertTrueRunnable(s.tryAcquire(RUNTIME * 3
          + MSINMINUTE, TimeUnit.MILLISECONDS)));
    } catch (InterruptedException e) {
      assertQueue.add(new FailRunnable(e));
    }

    checkAssertsInThreads();
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.task.TaskExecutor#addTask(org.jamesii.gui.application.task.ITask)}
   * .
   */
  public final void testAddTaskComplex() {
    // test tasks added from different threads over and over to test whether
    // tasks are really executed
    // after a blocking task has finished
    final AtomicInteger threadCount = new AtomicInteger();
    int count = 100;
    for (int i = 0; i < count; i++) {
      Runnable r = new Runnable() {

        @Override
        public void run() {
          final Semaphore s = new Semaphore(0);
          final Semaphore blocking = new Semaphore(0);

          ITask basicTaskBeforeBlocking = new AbstractTask() {

            @Override
            public void task() {
              try {
                Thread.sleep(RUNTIME);
              } catch (InterruptedException e) {
                assertQueue.add(new FailRunnable(e));
              }
            }

            @Override
            protected void cancelTask() {
            }

          };

          ITask blockingTask = new AbstractTask() {

            @Override
            public void task() {
              try {
                Thread.sleep(RUNTIME);
                blocking.release();
              } catch (InterruptedException e) {
                assertQueue.add(new FailRunnable(e));
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

          ITask basicTaskAfterBlocking = new AbstractTask() {

            @Override
            public void task() {
              // check whether blocking task already finished
              try {
                assertQueue.add(new AssertTrueRunnable(blocking.tryAcquire()));
                Thread.sleep(RUNTIME);
              } catch (InterruptedException e) {
                assertQueue.add(new FailRunnable(e));
              } finally {
                s.release();
              }

            }

            @Override
            protected void cancelTask() {
            }

          };

          executor.addTask(basicTaskBeforeBlocking);
          executor.addTask(blockingTask);
          executor.addTask(basicTaskAfterBlocking);

          try {
            assertQueue.add(new AssertTrueRunnable(s.tryAcquire(RUNTIME * 3
                + MSINMINUTE, TimeUnit.MILLISECONDS)));
          } catch (InterruptedException e) {
            assertQueue.add(new FailRunnable(e));
          }

          threadCount.incrementAndGet();
        }

      };

      Thread t = new Thread(r);
      t.start();
    }

    // we need count times releases of the semaphore
    while (threadCount.get() < count) {
      Thread.yield();
    }

    checkAssertsInThreads();
  }

  /**
   * Tests whether tasks are executed within the sequential order they where
   * added.
   */
  public final void testSequentialOrder() {
    // first we need a list of semaphores we use to track whether all previously
    // added tasks
    // where already started
    final List<Semaphore> semaphores = new ArrayList<>();

    // each ITask that is added to the executor gets its own semaphore where for
    // a specific task
    // to execute it is necessary to release all semaphores with a smaller
    // number as the tasks
    // semaphore
    final AtomicInteger threadCounter = new AtomicInteger();
    int count = 100;

    for (int i = 0; i < count; i++) {
      final int j = i;
      semaphores.add(new Semaphore(0));

      final ITask task = new AbstractTask() {
        private final int index = j;

        private final boolean blocking = Math.random() < 0.3;

        @Override
        public boolean isBlocking() {
          return blocking;
        }

        @Override
        public void task() {
          try {
            semaphores.get(index).release();
            // check whether all semaphores with a smaller number as task number
            // are released
            if (index > 0) {
              Semaphore s = semaphores.get(index - 1);
              assertQueue.add(new AssertTrueRunnable(s.tryAcquire(
                  RUNTIME + 1000, TimeUnit.MILLISECONDS)));
            }

            Thread.sleep((long) (Math.random() * RUNTIME));
            threadCounter.incrementAndGet();
          } catch (InterruptedException e) {
            assertQueue.add(new FailRunnable(e));
          }
        }

        @Override
        protected void cancelTask() {
        }

      };
      executor.addTask(task);
    }

    while (threadCounter.get() < count) {
      Thread.yield();
    }

    checkAssertsInThreads();
  }

}
