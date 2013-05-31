package org.jamesii.core.util.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Implementation of {@link IHierarchy} backed by two maps (but which does not
 * itself implement the {@link Map} interface) and possibly a set (if orphans,
 * i.e. unconnected nodes, are allowed by using the appropriate constructor).
 * 
 * This is the original implementation based on {@link HashMap}s, a
 * {@link HashSet} for the orphans (if applicable) and {@link ArrayList}s for
 * the collections of children of a node. HashMap use implies that when the same
 * sequence of operations were performed on two initially empty instances, some
 * Collection-returning methods (especially {@link #getAllNodes()}) may return
 * results that are equal from the {@link Collection#equals(Object)} point of
 * view, but have different iteration order. Most of the content of this class
 * was thus extracted to {@link AbstractHierarchy} to allow for subclasses using
 * Collections and Maps with guaranteed iteration order.
 * 
 * @author Arne Bittig
 * @date Jan 11, 2012
 * 
 * @param <T>
 *          Node type (keys and values)
 */
public class TwoMapHierarchy<T> extends AbstractHierarchy<T> {

  private static final long serialVersionUID = 6367372084817712285L;

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(TwoMapHierarchy.class,
        new IConstructorParameterProvider<TwoMapHierarchy<?>>() {
          @Override
          public Object[] getParameters(TwoMapHierarchy<?> hierarchy) {
            Object[] params =
                new Object[] { hierarchy.getChildToParentMap(),
                    hierarchy.getOrphans() };
            return params;
          }
        });
  }

  /** Minimal constructor, without orphan support */
  public TwoMapHierarchy() {
    this(false);
  }

  /**
   * Constructor with orphans flag
   * 
   * @param supportOrphans
   *          Flag whether to allow orphans (i.e. unconnected single nodes)
   */
  public TwoMapHierarchy(boolean supportOrphans) {
    this(Collections.<T, T> emptyMap(), supportOrphans ? Collections
        .<T> emptySet() : null);
  }

  /**
   * Full constructor using a child-parent relation map (see also
   * {@link org.jamesii.core.util.graph.trees.ITree#getChildToParentMap()}) and
   * a collection of unconnected single nodes (if it is null, none shall ever be
   * recorded by the hierarchy; if it is not null, a copy will be created with
   * elements occurring as key or value in the child-parent map removed).
   * 
   * @param childToParent
   *          Map associating each child with its parent
   * @param orphans
   *          Unconnected single nodes (null if none shall be allowed, empty
   *          collection if none are present, but addition shall be allowed)
   */
  public TwoMapHierarchy(Map<T, T> childToParent, Collection<T> orphans) {
    super(new HashMap<T, Collection<T>>(), new HashMap<T, T>(),
        orphans == null ? null : new HashSet<>(orphans));
    for (Map.Entry<T, T> e : childToParent.entrySet()) {
      addChildParentRelation(e.getKey(), e.getValue());
      // will remove nodes also present in orphans appropriately
    }
  }

  @Override
  protected Collection<T> getNewCollection() {
    return new ArrayList<>();
  }

  @Override
  public Set<T> getAllNodes() {
    Set<T> rval = new HashSet<>(getChildToParentMap().keySet());
    rval.addAll(getChildToParentMap().values());
    if (getOrphans() != null) {
      rval.addAll(getOrphans());
    }
    return rval;
  }
}
