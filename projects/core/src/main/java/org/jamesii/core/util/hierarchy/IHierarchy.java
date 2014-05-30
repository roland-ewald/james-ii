/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.hierarchy;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for representation of a hierarchy of objects. Technically, this
 * data structure corresponds to a directed graph with maximum out degree of 1
 * (i.e. each node has one edge pointing to a parent, or is a root and has no
 * outgoing edge at all). The graph need not be connected. Unconnected nodes may
 * or may not be allowed by implementations. Cycle-free-ness is not required.
 * 
 * When interpreted as a child-to-parent mapping, a hierarchy is similar to a
 * Map with same key- and value-type and additional inverse lookup (
 * {@link #getChildren(Object)}; {@link java.util.Map#put(Object, Object)}
 * corresponding to {@link #addChildParentRelation(Object, Object)},
 * {@link java.util.Map#get(Object)} to {@link #getParent(Object)}).
 * 
 * @author Arne Bittig
 * @param <T>
 *          Type of elements / nodes
 * @see Hierarchies
 * @date Jan 06, 2012
 */
public interface IHierarchy<T> {

  /**
   * Add "edge" to the tree / forest. Note if the child node was child of
   * another parent before, and that node becomes orphaned (no parent and no
   * other children) when it is no longer parent of child, it will be removed if
   * the implementation does not allow orphaned nodes.
   * 
   * @param child
   *          Child node
   * @param parent
   *          Parent node
   * @return Parent previously associated with child (null if none)
   * @throws IllegalArgumentException
   *           if child and parent are identical
   */
  T addChildParentRelation(T child, T parent);

  /**
   * Add an orphaned element (i.e. without children or parent) (optional
   * operation)
   * 
   * The method should not change the hierarchy and return false if orphans are
   * not supported, the given orphan is null, or the given "orphan" actually has
   * a parent or children in the hierarchy. If it is already registered as an
   * orphan, the result should be true, however.
   * 
   * @param orphan
   *          Element to add
   * @return true if the element is in the hierarchy as an orphan afterwards
   */
  boolean addOrphan(T orphan);

  /**
   * @return Collection of all nodes: roots, leafs, inner nodes, orphans
   */
  Collection<T> getAllNodes();

  /**
   * Children of given parent (i.e., in terms of a the Map view, all (children)
   * keys associated with the given (parent) value). The returned collection
   * must not be modified (even although it may be modifiable), as the reverse
   * lookup (of the parent of an element added to the returned collection) will
   * not return the element given as parameter of this method.
   * 
   * @param parent
   *          Parent (need not be present in collection at all)
   * @return Children of parent (empty collection if none)
   */
  Collection<T> getChildren(T parent);

  /**
   * Get a map with all elements in the hierarchy that have a parent, associates
   * with the respective parent (i.e. whose keys and values together includes
   * all elements in the hierarchy except orphans)
   * 
   * @return Child-Parent map (may be unmodifiable view)
   */
  Map<T, T> getChildToParentMap();

  /**
   * Nodes without parent or children. Orphans may have had parents or children
   * or both, but the relations were removed. Optional operation, but subclasses
   * not supporting orphans shall return null here rather than throwing an
   * {@link java.lang.UnsupportedOperationException}.
   * 
   * References to orphans may be kept for inclusion in the return value of
   * {@link #getAllNodes()}, allowing the hierarchy to be used without the need
   * to keep a separate collection of nodes that (maybe temporarily) have no
   * hierarchical relation to other nodes. The returned collection may be
   * modifiable, but should not be modified (removal of elements should be safe
   * and have the same effect as {@link #removeNode(Object)}, but addition of
   * elements already present in the hierarchy may lead to an inconsistent
   * internal state).
   * 
   * @return Orphaned nodes (empty collection if none; null if not supported)
   */
  Collection<T> getOrphans();

  /**
   * Get node's parent. If the node is a root, an orphan (if supported) or not
   * present at all in the hierarchy, the return value will be null.
   * 
   * @param child
   *          Child node
   * @return Parent of child (null if none)
   */
  T getParent(T child);

  /**
   * Get nodes without parent
   * 
   * @return Collection of nodes without parent (may be internally represented
   *         as null value parent)
   */
  Collection<T> getRoots();

  /**
   * Check whether tree is empty
   * 
   * @return true if no child-parent relation is registered
   */
  boolean isEmpty();

  /**
   * Remove a node as child of its parent and as parent of its children (if
   * any). Set new children's parent to node's previous parent, if present.
   * 
   * If an implementation does not allow orphaned nodes (i.e. hierarchy elements
   * that are roots without children) but this operation would create one or
   * more, these nodes are also removed from the hierarchy.
   * 
   * @param element
   *          Node to remove
   * @return former children that now have a new parent (or became roots if the
   *         element was a root itself; empty {@link Collection} if the element
   *         was a leaf, an orphan, or not present)
   */
  Collection<T> removeNode(T element);

  /**
   * Remove "edge" from tree / forest, i.e. make the given element no longer be
   * a child of its parent, without defining a new parent. Child becomes a root
   * if it has any children, otherwise an orphan or is removed from the
   * hierarchy if orphans are not supported. In the latter case, the parent will
   * also be removed if it has no other children and no parent in the hierarchy
   * (i.e. becomes orphaned, too).
   * 
   * @param child
   *          Child of child-parent relation to remove
   * @return Previous parent of child
   */
  T removeChildParentRelation(T child);

  /**
   * Compare the specified object with this hierarchy for equality. The object
   * is equal if and only if
   * <ul>
   * <li>it is also a hierarchy
   * <li>both hierarchies do not support orphans, or both do and contain the
   * same orphans (i.e. {@link Collection#equals(Object)} returns true for the
   * results of both hierarchies' {@link #getOrphans()})
   * <li>it contains the same child-parent-relations (which is true if
   * {@link Map#equals(Object)} returns true for the results of both
   * hierarchies' {@link #getChildToParentMap()}).
   * </ul>
   * 
   * @param obj
   *          {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  boolean equals(Object obj);

  /**
   * Returns a hash code value for this hierarchy. To ensure consistency with
   * {@link #equals(Object)}, which must work across different implementations
   * of {@link IHierarchy}, the hash code shall be the result of the following
   * calculation:
   * 
   * <pre>
   * {@link #getChildToParentMap()}.hashCode()
   * + ({@link #getOrphans()} == null ? 0 : 31 * {@link #getOrphans()}.hashCode())
   * </pre>
   * 
   * @return {@inheritDoc}
   */
  @Override
  int hashCode();

}
