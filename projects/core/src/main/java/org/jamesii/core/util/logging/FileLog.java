/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.jamesii.SimSystem;
import org.jamesii.core.util.caching.ILowMemoryListener;
import org.jamesii.core.util.caching.MemoryObserver;
import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * The Class FileLog. Provides very simple archiving functionality for the log.
 * For each instance of the software a unique log file is created. The log file
 * size is not limited. The archive file is created in the current working
 * directory, the software wide default charset encoding is used. Each instance
 * of this class will register a so called shut down hook which will empty the
 * internal buffer to the file in case of a shutdown of the JVM. This hook might
 * not be executed in case of a JVM crash. If you expect something like this to
 * happen, and if you need the logging information to examine the bug you should
 * use a different logging class or set the character buffer used to 0.
 * 
 * @author Jan Himmelspach
 */
public final class FileLog implements ILogListener, ILowMemoryListener {

  /**
   * The formatter is used to format the log records so that they are human
   * readable.
   */
  private SimpleFormatter formatter = new SimpleFormatter();

  /**
   * The buffered writer. If the buffer size is > 0 the buffer helps to reduce
   * the number of I/O accesses significantly.
   */
  private BufferedWriter writer = null;

  /**
   * Delayedwrite determines whether the log records are directly written to the
   * {@link #writer} or whether they are written to the {@link #pendingRecords}
   * queue. If false the records are directly written which means that the
   * synchronized call to the {@link #formatter} will block concurrent processes
   * writing to the log. If true the {@link #writeThread} will call the
   * synchronized method and the concurrent processes will immediately be able
   * to continue their computation (after the insertion of the record into the
   * {@link #pendingRecords}),
   */
  private boolean delayedwrite = false;

  /**
   * The thread taking care of writing the entries stored in the
   * {@link #pendingRecords} data structure if {@link #delayedwrite} is set to
   * true.
   */
  private WriteThread writeThread;

  /**
   * The list of log records to be written to the file. Only used if
   * {@link #delayedwrite} is set to true. The {@link #writeThread} waits on
   * this queue and writes the records sequentially to the file.
   */
  private BlockingQueue<LogRecord> pendingRecords;

  /**
   * The name of the file the log is written to.
   */
  private String filename;

  /**
   * Will be set to true if closed. If closed the file log will no longer store
   * log entries.
   */
  private boolean closed = false;

  /**
   * The shutdown hook.
   */
  private Thread shutdownThread = new Thread() {
    @Override
    public void run() {
      close();
    }
  };

  /**
   * Instantiates a new file log. In a file named JAMESII.log (made
   * automatically unique per run), stored in the working directory.
   */
  public FileLog() {
    this(false);
  }

  /**
   * Create a file log with the default name but with delayed writing activated.
   * 
   * @param delayedwrite
   *          can be set to true if you want to use an extra thread dealing with
   *          the write process. On machines with multiple cores this can be
   *          used to remove the synchronized formatting of log records of
   *          concurrent computing threads.
   */
  public FileLog(boolean delayedwrite) {
    this("JAMESII.log", -1, delayedwrite);
  }

  /**
   * Instantiates a new file log. In a file named accordingly the parameter
   * fName (made automatically unique per run), stored in the working directory.
   * 
   * @param fName
   *          filename to be used (will be made unique)
   */
  public FileLog(String fName) {
    this(fName, -1, false);
  }

  /**
   * Instantiates a new file log. In a file named accordingly the parameter
   * fName (made automatically unique per run), stored in the working directory.
   * 
   * @param fName
   *          filename to be used (will be made unique)
   * @param bSize
   *          -1 means default size; size of the buffer (in number of chars)
   * @param delayedwrite
   *          can be set to true if you want to use an extra thread dealing with
   *          the write process. On machines with multiple cores this can be
   *          used to remove the synchronized formatting of log records of
   *          concurrent computing threads.
   */
  public FileLog(String fName, int bSize, boolean delayedwrite) {
    filename = fName;
    if (!Files.isAbsoluteFilename(filename)) {
      filename = Files.composeFilename(SimSystem.getWorkingDirectory(), fName);
    }

    filename = Files.getUniqueName(filename);

    if (delayedwrite) {
      this.delayedwrite = true;
      pendingRecords = new LinkedBlockingQueue<>();
      writeThread = new WriteThread();
      writeThread.start();
    }

    try {
      if (bSize == -1) {
        writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                filename), SimSystem.getEncoding()));
      } else {
        writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                filename), SimSystem.getEncoding()), bSize);
      }

    } catch (Exception e) {
      SimSystem.report(Level.SEVERE, "Creating the file log failed.", e);
    }
    // make sure that the file log is written to the disc in case of a
    // shutdown of the JVM
    Runtime.getRuntime().addShutdownHook(shutdownThread);

    // register the file log at the memory observer; this should help in case of
    // many pending log records adding to a low memory situation
    MemoryObserver.INSTANCE.register(this);
  }

  @Override
  public void publish(LogRecord record) {
    if (closed) {
      return;
    }
    try {
      if (!delayedwrite) {
        write(record);
      } else {
        pendingRecords.add(record);
      }

    } catch (IOException e) {
      System.err.println("Writing to log file failed! " + e.getMessage());
    }

  }

  private void write(LogRecord record) throws IOException {
    writer.write(formatter.format(record));
  }

  @Override
  public void flush() {
    try {

      if (delayedwrite) {
        // if we use the delayed write mechanism we need to empty the queue of
        // pending records before we can continue
        while (!pendingRecords.isEmpty()) {
          // this loops operates in concurrency to the write thread
          LogRecord record = pendingRecords.poll();
          // due to the concurrency we might retrieve null which we should not
          // write
          if (record != null) {
            write(record);
          }
        }
      }
      writer.flush();
    } catch (IOException e) {
      System.err.println("Writing to log file failed while flushing! " + e);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (writer != null) {
      cancelDelayedWrite();
      BasicUtilities.close(writer);
    }
    super.finalize();
  }

  /**
   * Cancel delayed writing, empty the buffer and close the file stream. The
   * FileLog cannot be used any more after calling this method. Consider using
   * {@link #cancelDelayedWrite()} if you only want to stop delayed writing.
   */
  public void close() {
    closed = true;
    Runtime.getRuntime().removeShutdownHook(shutdownThread);
    MemoryObserver.INSTANCE.unregister(this);

    cancelDelayedWrite();
    BasicUtilities.close(writer);
    writer = null;
  }

  /**
   * Cancel the delayed write (which means to stop the thread and to flush the
   * buffer); afterwards the log can still be used without delayed writing.
   */
  public void cancelDelayedWrite() {
    if (delayedwrite) {
      writeThread.cancel();
      delayedwrite = false;
    }
    writeThread = null;
    flush();
    writeThread = null;
  }

  /**
   * A thread for the delayed writing to the file log. The format method of the
   * Formatter class used might be synchronized and thus writing is essentially
   * a sequential job.
   * 
   * @author Jan Himmelspach
   * 
   */
  private class WriteThread extends Thread {

    /**
     * If true the thread will be stopped ASAP.
     */
    boolean cancelled = false;

    /**
     * The poison pill.
     */
    final LogRecord poisonPill = new LogRecord(Level.OFF, null);

    /**
     * Create a new instance of the write thread. Only one instance should be
     * created as writing to the file is a sequential operation.
     */
    public WriteThread() {
      super("file log (" + Files.getFileName(filename) + ")");
    }

    @Override
    public void run() {
      while (!cancelled) {
        try {
          LogRecord record = pendingRecords.take();
          if (record != poisonPill) {
            write(record);
          }
        } catch (InterruptedException e) {
          return; // get out here
        } catch (IOException e) {
          System.err
              .println("Exception on waiting for / writing pending records to be written (FileLog) "
                  + e.getMessage());
        }

      }
    }

    /**
     * Cancel the delayed writing.
     */
    public void cancel() {
      cancelled = true;
      Collection<LogRecord> records = new LinkedList<>();
      pendingRecords.drainTo(records);
      try {
        for (LogRecord record : records) {
          write(record);
        }
      } catch (IOException e) {
        SimSystem.report(e);
      }
      pendingRecords.add(poisonPill);
    }
  }

  @Override
  public synchronized void lowMemory() {

    if (!delayedwrite || (pendingRecords.size() == 0)) {
      return;
    }

    SimSystem
        .report(
            Level.INFO,
            "Low memory detected. Pending log entries in this file log instance are flushed to disc to reduce memory usage.");
    flush();
  }

}
