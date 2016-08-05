/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.hierarchy.LinkedHierarchy;

/**
 * @author Arne Bittig
 * @date 24.11.2012
 */
public final class ShapeHierarchyUtils {

  private ShapeHierarchyUtils() {
  }

  /**
   * Determine child-parent relation tree ({@link IHierarchy}) structure from
   * collection of shaped components (assumes a component of the same type is
   * returned by their {@link IShapedComponent#getEnclosingEntity()} method)
   * 
   * @param comps
   *          Components
   * @return Tree / forest structure of the components
   */
  public static <C extends IShapedComponent> IHierarchy<C> makeHierarchy(
      Collection<? extends C> comps) {
    IHierarchy<C> compTree = new LinkedHierarchy<>();
    for (C comp : comps) {
      // "null" allowed as parent
      compTree.addChildParentRelation(comp, (C) comp.getEnclosingEntity());
    }
    return compTree;
  }

  /**
   * Check whether all info in the hierarchy is up to date
   * 
   * @param hierarchy
   *          Hierarchy of {@link IShapedComponent}s
   * @return Components for which {@link IShapedComponent#getEnclosingEntity()} is
   *         different from registered parent
   */
  public static <C extends IShapedComponent> Collection<C> notUpToDateInHierarchy(
      IHierarchy<C> hierarchy) {
    Collection<C> rv = new LinkedHashSet<>();
    for (Map.Entry<C, C> childParentEntry : hierarchy.getChildToParentMap()
        .entrySet()) {
      if (((IShapedComponent) childParentEntry.getKey()).getEnclosingEntity() != childParentEntry
          .getValue()) {
        rv.add(childParentEntry.getKey());
      }
    }
    for (C parent : hierarchy.getChildToParentMap().values()) {
      for (C child : hierarchy.getChildren(parent)) {
        if (child.getEnclosingEntity() != parent) {
          rv.add(child);
        }
      }
    }
    return rv;
  }

}
