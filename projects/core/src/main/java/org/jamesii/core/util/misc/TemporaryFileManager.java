/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * A handy utility class that can create temporary files and temporary folders
 * which are automatically deleted after application shutdown. Temporary folders
 * will be also deleted completely if possible even if there are non temp files
 * in there.
 * 
 * @author Stefan Rybacki
 */
public final class TemporaryFileManager extends Thread {

  /**
   * The singleton instance.
   */
  private static final TemporaryFileManager INSTANCE =
      new TemporaryFileManager();

  /**
   * The Constant MAX_ATTEMPS.
   */
  private static final int MAX_ATTEMPTS = 25;

  /**
   * lock used to synchronize shutdown and possible further temp file addings
   */
  private static final Lock usageLock = new ReentrantLock();

  /**
   * flag indicating whether the manager has already or is about to shutdown
   */
  private static boolean shutdown = false;

  /**
   * List of created files.
   */
  private final List<File> createdFiles = Collections
      .synchronizedList(new ArrayList<File>());

  /**
   * hidden constructor
   */
  private TemporaryFileManager() {
    // register shutdown hook which cleans up created temporary files
    // or directories
    Runtime.getRuntime().addShutdownHook(this);
  }

  @Override
  public void run() {
    usageLock.lock();
    try {
      shutdown = true;

      // clean up create files and directories
      boolean success = true;
      for (File f : createdFiles) {
        boolean s = Files.deleteRecursively(f);
        if (!s) {
          SimSystem.report(
              Level.WARNING,
              String.format("Could not completely delete temporary %s %s",
                  f.isDirectory() ? "directory" : "file", f.toString()));
        }
        success &= s;
      }

      if (!success) {
        SimSystem
            .report(Level.WARNING,
                "Some temporary files or directories could not be deleted completely");
      }
    } finally {
      usageLock.unlock();
    }

  }

  /**
   * In contrast to {@link File#createTempFile(String, String)} this file is
   * automatically registered for deletion on shutdown. Other than that the
   * behavior is exactly as of {@link File#createTempFile(String, String)}.
   * 
   * @see File#createTempFile(String, String)
   * @param prefix
   *          the temp file's prefix
   * @param suffix
   *          the temp file's suffix
   * @return the created temporary file
   * @throws IOException
   */
  public static File createTempFile(String prefix, String suffix)
      throws IOException {
    return createTempFile(prefix, suffix, null);
  }

  /**
   * In contrast to {@link File#createTempFile(String, String, File)} this file
   * is automatically registered for deletion on shutdown. Other than that the
   * behavior is exactly as of {@link File#createTempFile(String, String, File)}
   * .
   * 
   * @see File#createTempFile(String, String, File)
   * @param prefix
   *          the temp file's prefix
   * @param suffix
   *          the temp file's suffix
   * @param directory
   *          the directory to create the file in
   * @return the created temporary file
   * @throws IOException
   */
  public static File createTempFile(String prefix, String suffix, File directory)
      throws IOException {
    usageLock.lock();
    try {
      if (shutdown) {
        throw new UnsupportedOperationException(
            "Temporary File Manager is already shutdown!");
      }
      File tempFile = File.createTempFile(prefix, suffix, directory);
      INSTANCE.createdFiles.add(tempFile);
      return tempFile;
    } finally {
      usageLock.unlock();
    }
  }

  /**
   * Equivalent to {@link File#createTempFile(String, String)} with the
   * difference that a temporary directory instead of a file is created and also
   * registered for complete deletion on shutdown.
   * 
   * @param prefix
   *          the temp directories's prefix
   * @param suffix
   *          the temp directories's suffix
   * @return the created temporary directory
   * @throws IOException
   */
  public static File createTempDirectory(String prefix, String suffix)
      throws IOException {
    return createTempDirectory(prefix, suffix, null);
  }

  /**
   * Equivalent to {@link File#createTempFile(String, String, File)} with the
   * difference that a temporary directory instead of a file is created and also
   * registered for complete deletion on shutdown.<br/>
   * 
   * If the directory is not passed or if it is not a directory the
   * {@link org.jamesii.SimSystem#getTempDirectory()} is queried for the
   * directory to be used.
   * 
   * @param prefix
   *          the temp directories's prefix
   * @param suffix
   *          the temp directories's suffix
   * @param directory
   *          the directory to create the directory in
   * @return the created temporary directory
   * @throws IOException
   */
  public static synchronized File createTempDirectory(String prefix,
      String suffix, File directory) throws IOException {
    usageLock.lock();
    try {
      if (shutdown) {
        throw new UnsupportedOperationException(
            "Temporary File Manager is already shutdown!");
      }

      if (directory == null || !directory.isDirectory()) {
        directory = new File(SimSystem.getTempDirectory());
      }

      // make up temporary directory file name
      String name =
          prefix + UUID.randomUUID().toString().replace('-', '_') + suffix;
      File dir = new File(directory.getAbsolutePath(), name);

      // to avoid racing conditions try to create the directory without
      // checking for existence
      int attempts = 1;
      while (!dir.mkdir()) {
        name = prefix + UUID.randomUUID().toString().replace('-', '_') + suffix;
        dir = new File(directory.getAbsolutePath(), name);
        if (attempts > MAX_ATTEMPTS) {
          dir = null;
          break;
        }
        attempts++;
      }

      if (dir == null) {
        throw new IOException(String.format(
            "Couldn't create a new temporary directory using %d attempts.",
            MAX_ATTEMPTS));
      }

      INSTANCE.createdFiles.add(dir);
      return dir;
    } finally {
      usageLock.unlock();
    }
  }

}
