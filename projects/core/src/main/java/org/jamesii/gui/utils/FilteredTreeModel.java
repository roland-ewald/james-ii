/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.TaskManager;

/**
 * A {@link TreeModel} implementation that wraps another {@link TreeModel}
 * implementation and adds filtering capabilities to that model.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          type parameter specifying the filter value type
 */
public class FilteredTreeModel<E> extends AbstractTreeModel {

  /**
   * the filter to use
   */
  private IFilter<E> filter;

  /**
   * the model to wrap
   */
  private TreeModel model = null;

  /**
   * The nodes cache.
   */
  private Map<Object, List<Object>> nodeCache = new HashMap<>();

  /**
   * The listener used to react on filter changes.
   */
  private final IFilterChangeListener<E> listener;

  /**
   * The filtering task.
   */
  private ITask filteringTask;

  /**
   * The structure update lock.
   */
  private final Lock structureUpdateLock = new ReentrantLock();

  /**
   * Creates a new instance of a filterable tree model wrapping the specified
   * {@link javax.swing.table.TableModel}.
   * 
   * @param wrappedModel
   *          the model to filter
   */
  public FilteredTreeModel(TreeModel wrappedModel) {
    this(wrappedModel, null);
  }

  /**
   * Creates a new instance of a filterable tree model wrapping the specified
   * {@link javax.swing.table.TableModel} using the given {@link IFilter}.
   * 
   * @param wrappedModel
   *          the model to filter
   * @param filter
   *          the filter to use
   */
  public FilteredTreeModel(TreeModel wrappedModel, IFilter<E> filter) {
    model = wrappedModel;
    listener = new IFilterChangeListener<E>() {

      @Override
      public void filterChanged(IFilter<E> filterToUse, E oldValue, E newValue) {
        FilteredTreeModel.this.filterChanged(filterToUse, oldValue, newValue);
      }

    };
    // TODO sr137: react more specifically to each event, because
    // otherwise the
    // complete tree is redrawn again and again
    model.addTreeModelListener(new TreeModelListener() {
      @Override
      public void treeNodesChanged(TreeModelEvent e) {
        recreateCache();
      }

      @Override
      public void treeNodesInserted(TreeModelEvent e) {
        recreateCache();
      }

      @Override
      public void treeNodesRemoved(TreeModelEvent e) {
        recreateCache();
      }

      @Override
      public void treeStructureChanged(TreeModelEvent e) {
        recreateCache();
      }
    });
    setFilter(filter);
    recreateCache();
  }

  /**
   * @return the used filter ({@code null} if none)
   */
  public IFilter<?> getFilter() {
    return filter;
  }

  /**
   * Sets the filter to use
   * 
   * @param filter
   *          the filter to use
   */
  public void setFilter(IFilter<E> filter) {
    if (filter == null) {
      return;
    }

    if (this.filter != null) {
      this.filter.removeFilterChangeListener(listener);
    }

    filter.addFilterChangeListener(listener);
    this.filter = filter;

    recreateCache();
  }

  /**
   * Recreates cache.
   */
  private void recreateCache() {
    if (filteringTask != null) {
      filteringTask.cancel();
      filteringTask = null;
    }

    // check if there is another recreation running
    filteringTask = new AbstractTask("Filtering...") {
      private boolean canceled = false;

      private final E filterValue = filter != null ? filter.getFilterValue()
          : null;

      private final IFilter<E> usedFilter = filter;

      private final Map<Object, List<Object>> cache = new HashMap<>();

      @Override
      protected void task() {
        getCachedNodesWithFilter(getRoot(), false);

        // lock structure change so that no other filtering task can
        // change structure before this structure change is complete
        // (including listener notifications)
        structureUpdateLock.lock();
        try {
          synchronized (nodeCache) {
            if (!canceled) {
              // there might be a racing condition in case the tree is
              // redrawing and running from 0 to getChildCount(...)
              // while
              // the task finishes and getChildCount(...) returns a
              // smaller value than before which results in a child
              // that
              // is not
              // accessible therefore might throw a
              // "Null child not allowed" exception of the tree ui.
              nodeCache.clear();
              nodeCache.putAll(cache);
              cache.clear();
            }
          }
          if (!canceled) {
            fireTreeStructureChanged(this, new Object[] { getRoot() }, null,
                null);
          }
        } finally {
          structureUpdateLock.unlock();
        }
      }

      /**
       * Helper method that creates the cacheable filtered tree nodes
       * 
       * @param object
       *          the parent node
       * @param alreadyAccepted
       *          if true the parent or one of its parents are already accepted
       *          by the used filter
       * @return the child count after filtering the children
       */
      private boolean getCachedNodesWithFilter(Object object,
          boolean alreadyAccepted) {
        List<Object> children = new ArrayList<>();
        cache.put(object, children);

        for (int i = 0; i < model.getChildCount(object); i++) {
          Object node = model.getChild(object, i);
          if (usedFilter == null || filterValue == null
              || !usedFilter.filteredWithValue(filterValue, node)
              || alreadyAccepted) {
            children.add(node);
            getCachedNodesWithFilter(node, true);
          } else {
            // check whether a child or subchild of node meets the
            // filter criteria in which case this node is even though
            // filtered to be added to children list
            if (getCachedNodesWithFilter(node, false)) {
              children.add(node);
            }
          }
        }

        return children.size() > 0;
      }

      @Override
      protected void cancelTask() {
        canceled = true;
      }

    };

    TaskManager.addTask(filteringTask);
  }

  @Override
  public synchronized Object getChild(Object parent, int index) {
    List<Object> list = nodeCache.get(parent);
    if (list != null && list.size() > index) {
      return list.get(index);
    }

    return null;
  }

  @Override
  public int getChildCount(Object parent) {
    synchronized (nodeCache) {
      List<Object> list = nodeCache.get(parent);

      if (list == null) {
        return 0;
      }
      return list.size();
    }
  }

  /**
   * Listener method called by anonymous listener
   * 
   * @param f
   *          the filter that changed
   * @param oldValue
   *          the old filter value
   * @param newValue
   *          the new filter value
   */
  private void filterChanged(IFilter<E> f, E oldValue, E newValue) {
    if (filter == f) {
      // traverse tree to check what nodes have been changed in
      // structure
      checkChangesAfterFilterChange((TreeNode) getRoot(), oldValue, newValue);
    }
  }

  /**
   * Helper method that checks the changes after filter change for the given
   * node.
   * 
   * @param node
   *          the node to check
   * @param oldValue
   *          old filter value
   * @param newValue
   *          new filter value
   */
  private void checkChangesAfterFilterChange(TreeNode node, E oldValue,
      E newValue) {
    if (node == null || (oldValue != null && oldValue.equals(newValue))) {
      return;
    }

    recreateCache();
  }

  @Override
  public int getIndexOfChild(Object parent, Object child) {
    return -1;
  }

  @Override
  public Object getRoot() {
    return model.getRoot();
  }

  @Override
  public boolean isLeaf(Object node) {
    List<Object> list = nodeCache.get(node);
    return (list == null || list.size() == 0);
  }

  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    model.valueForPathChanged(path, newValue);
  }

  /**
   * @return the wrapped model
   */
  public TreeModel getModel() {
    return model;
  }
}
