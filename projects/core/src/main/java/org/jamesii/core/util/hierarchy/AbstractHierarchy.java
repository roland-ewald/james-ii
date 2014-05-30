/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.hierarchy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Abstract hierarchy implementation based on two maps and one set (the latter
 * only if orphans are enabled) that does not rely on any concrete map or
 * collection implementation: The maps have to be passed in by the subclass'
 * constructor and the collections to be used (in the parent-to-children map and
 * the return value of {@link #getRoots()}) are taken from the (here) abstract
 * methos {@link #getNewCollection()}.
 * 
 * @author Arne Bittig
 * @param <T>
 *          Element type
 * @date 15.10.2012
 */
public abstract class AbstractHierarchy<T> implements IHierarchy<T>,
    java.io.Serializable {

  private static final long serialVersionUID = 126070361186021138L;

  /** Map associating each parent with its children (one-to-many) */
  private final Map<T, Collection<T>> parentToChildren;

  /** Map associating each child with its parent (one-to-one) */
  private final Map<T, T> childToParent;

  private final Set<T> orphans;

  /**
   * Constructor where the internal maps/set to use are set. If any of them are
   * non-empty, it is left to the subclass' implementation to ensure that the
   * content is consistent.
   * 
   * @param parentToChildren
   *          Parent-to-child-collection map
   * @param childToParent
   *          Child-to-parent map
   * @param orphans
   *          Orphan set
   */
  // protected <MTCT extends Map<T, Collection<T>> & Serializable, MTT extends
  // Map<T, T> & Serializable, ST extends Set<T> & Serializable>
  // AbstractHierarchy(MTCT parentToChildren, MTT childToParent, ST orphans) {
  protected AbstractHierarchy(Map<T, Collection<T>> parentToChildren,
      Map<T, T> childToParent, Set<T> orphans) {
    this.parentToChildren = parentToChildren;
    this.childToParent = childToParent;
    this.orphans = orphans;
  }

  @Override
  public T addChildParentRelation(T child, T parent) {
    if (orphans != null) {
      orphans.remove(child);
      orphans.remove(parent);
    }
    T prevParent = childToParent.get(child);
    if (parent.equals(prevParent)) {
      return prevParent; // shortcut
    }
    if (child.equals(parent)) {
      throw new IllegalArgumentException("Node must not be parent of itself: "
          + child);
    }
    if (prevParent != null) {
      removeRelationParentPart(child, prevParent, true);
    }
    addRelationWithoutCheck(child, parent);
    return prevParent;
  }

  /**
   * Add child-parent relation without checking whether child has a different
   * registered parent
   * 
   * @param child
   * @param parent
   */
  private void addRelationWithoutCheck(T child, T parent) {
    childToParent.put(child, parent);
    Collection<T> collV = parentToChildren.get(parent);
    if (collV == null) {
      collV = getNewCollection();
      parentToChildren.put(parent, collV);
    }
    collV.add(child);
  }

  /**
   * Create a new collection to be used in the parent-to-children map (and for
   * the return value of {@link #getRoots()}). The returned collection type may
   * or may not support duplicates (i.e. it is taken care of that no element
   * already present will ever be inserted, so a List would do, too).
   * 
   * @return Empty modifiable collection
   */
  protected abstract Collection<T> getNewCollection();

  @Override
  public T removeChildParentRelation(T child) {
    if (!childToParent.containsKey(child)) {
      return null;
    }
    T parent = removeRelationChildPart(child);
    removeRelationParentPart(child, parent, true);
    return parent;
  }

  /**
   * Remove child-to-parent reference, but not parent-to-child reference
   * 
   * @param child
   *          Child node
   * @return Parent
   */
  private T removeRelationChildPart(T child) {
    T prevParent = childToParent.remove(child);
    if (orphans != null && isLeaf(child)) {
      orphans.add(child);
    }
    return prevParent;
  }

  /**
   * Remove parent-to-child reference, but not child-to-parent reference (which
   * may have been removed already)
   * 
   * @param child
   *          Child node
   * @param parent
   *          Parent node
   * @param orphanCheck
   *          if parent becomes orphaned and orphans are stored, store parent --
   *          usually true, but false if called as part of
   *          {@link #removeInner(Object, Object, Collection)}
   * @return true if child was the only child of parent, i.e. if parent became
   *         orphaned or leaf; false if parent still has other children
   */
  private boolean removeRelationParentPart(T child, T parent,
      boolean orphanCheck) {
    Collection<T> silblings = parentToChildren.get(parent);
    // not using CollectionUtils.removeFromMultiMap due to orphans-check
    if (silblings.size() == 1) {
      parentToChildren.remove(parent);
      if (orphanCheck && orphans != null && isRoot(parent)) {
        orphans.add(parent);
      }
      return true;
    } else {
      silblings.remove(child);
      return false;
    }
  }

  /**
   * Check whether node has a parent (only to be used internally! - only checks
   * whether node has no parent, not whether it actually has children)
   * 
   * @param node
   *          Node to check
   * @return True if node has no parent
   */
  private boolean isRoot(T node) {
    return !childToParent.containsKey(node);
  }

  @Override
  public T getParent(T child) {
    return childToParent.get(child);
  }

  @Override
  public Collection<T> getChildren(T parent) {
    Collection<T> children = parentToChildren.get(parent);
    if (children == null) {
      return Collections.emptyList();
    }
    return children;
  }

  @Override
  public Map<T, T> getChildToParentMap() {
    return childToParent;
  }

  /**
   * Check whether node is a leaf (or not present at all)
   * 
   * @param node
   *          Node to check
   * @return True if node has no children (i.e. is leaf)
   */
  private boolean isLeaf(T node) {
    return !parentToChildren.containsKey(node);
  }

  @Override
  public Collection<T> removeNode(T node) {
    if (orphans != null && orphans.remove(node)) {
      return Collections.emptySet();
    }

    T nodeParent = childToParent.remove(node);

    Collection<T> nodeChildren = parentToChildren.remove(node);
    if (nodeChildren == null) { // node was leaf
      if (nodeParent != null) {
        removeRelationParentPart(node, nodeParent, true);
      }
      return Collections.emptySet();
    }

    if (nodeParent == null) {
      for (T child : nodeChildren) {
        removeRelationChildPart(child);
      }
      return nodeChildren;
    }
    removeInner(node, nodeParent, nodeChildren);
    return nodeChildren;
  }

  /**
   * Helper method for {@link #removeNode(Object)}, handling non-orphan,
   * non-root, non-leaf node's removal
   * 
   * @param node
   *          Node to remove
   * @param nodeParent
   *          node's parent
   * @param nodeChildren
   *          node's children
   */
  private void removeInner(T node, T nodeParent, Collection<T> nodeChildren) {
    boolean soleChild = removeRelationParentPart(node, nodeParent, false);
    // if isRoot(nodeParent) => parent orphan

    if (soleChild) {
      parentToChildren.put(nodeParent, nodeChildren);
      // ^ re-uses nodeChildren as nodeParent's child collection
      for (T child : nodeChildren) {
        childToParent.put(child, nodeParent);
      }
    } else {
      for (T child : nodeChildren) {
        addRelationWithoutCheck(child, nodeParent);
        // ^ updates nodeParent's children, too
      }
    }
  }

  /**
   * Parents that do not have a parent themselves, i.e. nodes that have children
   * but no parent; excludes orphans
   * 
   * @return roots of the parent-child tree / forest
   * @see #getOrphans()
   */
  @Override
  public Collection<T> getRoots() {
    Collection<T> parentless = getNewCollection();
    for (T par : parentToChildren.keySet()) {
      if (!childToParent.containsKey(par)) {
        parentless.add(par);
      }
    }
    if (parentless.contains(null)) {
      parentless.remove(null);
      parentless.addAll(parentToChildren.get(null));
    }
    return parentless;
  }

  @Override
  public final boolean addOrphan(T orphan) {
    if (orphans == null || orphan == null || childToParent.containsKey(orphan)
        || parentToChildren.containsKey(orphan)) {
      return false;
    }
    orphans.add(orphan);
    return true;
  }

  @Override
  public Collection<T> getOrphans() {
    return orphans;
  }

  /** @return All nodes with children */
  protected Set<T> getAllParents() {
    return parentToChildren.keySet();
  }

  @Override
  public boolean isEmpty() {
    return childToParent.isEmpty() && (orphans == null || orphans.isEmpty());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IHierarchy)) {
      return false;
    }
    IHierarchy<?> other = (IHierarchy<?>) obj;
    if (!childToParent.equals(other.getChildToParentMap())) {
      return false;
    }
    if (orphans == null) {
      return other.getOrphans() == null;
    }
    return orphans.equals(other.getOrphans());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    return childToParent.hashCode()
        + (orphans == null ? 0 : prime * orphans.hashCode());
  }

  @Override
  public String toString() {
    return "TwoMapHierarchy [parentToChildren=" + parentToChildren
        + ", childToParent=" + childToParent + "]";
  }

}
