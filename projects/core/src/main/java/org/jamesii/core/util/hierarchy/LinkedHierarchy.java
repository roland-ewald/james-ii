/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.hierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * {@link IHierarchy} implementation using {@link LinkedHashMap}s internally for
 * reproducible iteration order.
 * 
 * @author Arne Bittig
 * @date 15.10.2012
 * @param <T>
 *          Node type (keys and values)
 */
public class LinkedHierarchy<T> extends AbstractHierarchy<T> {

  private static final long serialVersionUID = 797234580405171720L;

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(LinkedHierarchy.class,
        new IConstructorParameterProvider<LinkedHierarchy<?>>() {
          @Override
          public Object[] getParameters(LinkedHierarchy<?> hierarchy) {
            Object[] params =
                new Object[] { hierarchy.getChildToParentMap(),
                    hierarchy.getOrphans() };
            return params;
          }
        });
  }

  /** Minimal constructor, without orphan support */
  public LinkedHierarchy() {
    this(false);
  }

  /**
   * Constructor with orphans flag
   * 
   * @param supportOrphans
   *          Flag whether to allow orphans (i.e. unconnected single nodes)
   */
  public LinkedHierarchy(boolean supportOrphans) {
    super(new LinkedHashMap<T, Collection<T>>(), new LinkedHashMap<T, T>(),
        supportOrphans ? null : new LinkedHashSet<T>());
  }

  /** Constructor for XML-Bean delegate for constructor */
  private LinkedHierarchy(Map<T, T> childToParent, Set<T> orphans) {
    super(new LinkedHashMap<T, Collection<T>>(), new LinkedHashMap<T, T>(),
        orphans);
    for (Map.Entry<T, T> e : childToParent.entrySet()) {
      addChildParentRelation(e.getKey(), e.getValue());
    }
  }

  @Override
  protected Collection<T> getNewCollection() {
    return new ArrayList<>();
  }

  @Override
  public Set<T> getAllNodes() {
    Set<T> rval = new LinkedHashSet<>(getChildToParentMap().keySet());
    rval.addAll(getChildToParentMap().values());
    if (getOrphans() != null) {
      rval.addAll(getOrphans());
    }
    return rval;
  }
}
