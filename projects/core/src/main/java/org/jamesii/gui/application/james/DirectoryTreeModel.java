/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.jamesii.SimSystem;
import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.IProgressListener;

/**
 * Internal class that implements {@link javax.swing.tree.TreeModel} and
 * provides the directory structure of a given directory as tree.
 * 
 * @author Stefan Rybacki
 */
class DirectoryTreeModel extends DefaultTreeModel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4323452962538909278L;

  /**
   * tree's root
   */
  private final TreeNode root;

  /**
   * map of workers that load the directory structure of sub nodes in background
   */
  private final Map<Object, SwingWorker<?, ?>> workers = new HashMap<>();

  /**
   * list of progress listeners
   */
  private final ListenerSupport<IProgressListener> progressListeners =
      new ListenerSupport<>();

  /**
   * Creates a new directory tree model for the given directory.
   * 
   * @param directory
   *          the directory to provide structure for
   */
  public DirectoryTreeModel(File directory) {
    this(directory, null);
  }

  /**
   * Creates a new directory tree mode for the given directory registering the
   * provided progress listener.
   * 
   * @param directory
   *          the directory to provide structure for
   * @param l
   *          a listener to register
   */
  public DirectoryTreeModel(File directory, IProgressListener l) {
    super(null, true);
    if (directory == null) {
      throw new IllegalArgumentException("directory can't be null!");
    }
    root = new DefaultMutableTreeNode(directory, directory.isDirectory());
    addProgressListener(l);
  }

  @Override
  public Object getRoot() {
    return root;
  }

  @Override
  public synchronized int getChildCount(final Object parent) {
    if (workers.get(parent) != null) {
      return ((TreeNode) parent).getChildCount();
    }

    // start swing worker to retrieve actual children
    SwingWorker<int[], Object> getChildrenWorker =
        new SwingWorker<int[], Object>() {

          @Override
          protected int[] doInBackground() {
            fireProgressStarted(this);

            if (parent instanceof DefaultMutableTreeNode
                && ((DefaultMutableTreeNode) parent).getUserObject() instanceof File) {
              if (((File) ((DefaultMutableTreeNode) parent).getUserObject())
                  .isDirectory()) {
                File[] files =
                    ((File) ((DefaultMutableTreeNode) parent).getUserObject())
                        .listFiles();

                if (files == null) {
                  return new int[0];
                }
                List<DefaultMutableTreeNode> children =
                    new ArrayList<>(files.length);

                for (File f : files) {
                  DefaultMutableTreeNode node =
                      new DefaultMutableTreeNode(f, f.isDirectory());
                  ((DefaultMutableTreeNode) parent).add(node);
                  children.add(node);
                }

                // create int array of indices
                int[] indices = new int[files.length];
                for (int i = 0; i < indices.length; i++) {
                  indices[i] = getIndexOfChild(parent, children.get(i));
                }
                Arrays.sort(indices);
                children.clear();

                return indices;
              }

            }

            return new int[0];
          }

          @Override
          protected void done() {
            fireProgressFinished(this);

            super.done();

            try {
              nodesWereInserted((TreeNode) parent, get());
            } catch (InterruptedException | ExecutionException e) {
              SimSystem.report(e);
            }
          }
        };

    workers.put(parent, getChildrenWorker);

    getChildrenWorker.execute();

    return 0;
  }

  /**
   * Adds a progress listener, used to notify about the progress of started
   * background tasks that load the directory structure.
   * 
   * @param l
   *          the listener to add
   */
  public final void addProgressListener(IProgressListener l) {
    progressListeners.addListener(l);
  }

  /**
   * Removes a previously registered progress listener.
   * 
   * @param l
   *          the listener to remove
   */
  public final void removeProgressListener(IProgressListener l) {
    progressListeners.removeListener(l);
  }

  /**
   * notifies registered listeners that a background task started
   * 
   * @param worker
   *          the worker representing the background task
   */
  private synchronized void fireProgressStarted(
      SwingWorker<int[], Object> worker) {
    for (IProgressListener l : progressListeners.getListeners()) {
      if (l != null) {
        l.started(worker);
      }
    }
  }

  /**
   * notifies registered listeners that a background task finished
   * 
   * @param worker
   *          the worker representing the background task
   */
  private synchronized void fireProgressFinished(
      SwingWorker<int[], Object> worker) {
    for (IProgressListener l : progressListeners.getListeners()) {
      if (l != null) {
        l.finished(worker);
      }
    }
  }
}
