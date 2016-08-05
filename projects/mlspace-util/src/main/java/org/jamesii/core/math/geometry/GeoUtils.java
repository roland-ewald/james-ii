/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Assortment of static methods providing some tools for dealing with
 * coordinates and shapes
 *
 * @author Arne Bittig
 */
public final class GeoUtils {

  private GeoUtils() {
  }

  /**
   * Get components from a collection whose shape is larger, as large as, and
   * smaller than that of a given component
   *
   * @param comps
   *          Components to compare to (list will be sorted!)
   * @param refVolume
   *          Volume of component to use as reference
   * @return 3-element array with larger, same-size, smaller components (empty
   *         lists for those of which there are none)
   */
  @SuppressWarnings("unchecked")
  public static <C extends IShapedComponent> List<C>[] getLargerSameSmallerComps(
      List<C> comps, double refVolume) {
    Collections.sort(comps, GeoUtils.SIZE_COMPARATOR_DESCENDING);

    int numComps = comps.size();
    int idxFirstSameSize = 0;
    while (idxFirstSameSize < numComps
        && comps.get(idxFirstSameSize).getShape().getSize() > refVolume) {
      idxFirstSameSize++;
    }
    if (idxFirstSameSize == numComps) { // shortcut
      return new List[] { comps, Collections.EMPTY_LIST, Collections.EMPTY_LIST };
    }
    int idxFirstSmaller = idxFirstSameSize;
    while (idxFirstSmaller < numComps
        && comps.get(idxFirstSmaller).getShape().getSize() >= refVolume) {
      idxFirstSmaller++;
    }
    return new List[] { comps.subList(0, idxFirstSameSize),
        comps.subList(idxFirstSameSize, idxFirstSmaller),
        comps.subList(idxFirstSmaller, numComps) };
  }

  /**
   * Find components that overlap one given component (from collection of
   * potentially colliding components, e.g. compartments with the same parent --
   * comp itself is ignored if contained in the collection)
   *
   * @param comp
   *          Component that moved recently (and may now collide)
   * @param otherComps
   *          Collection of compartments to check for overlap / collision
   * @return Components comp collides with (empty collection if none)
   */
  public static <C extends IShapedComponent> List<C> checkCollisionsPairwise(
      IShapedComponent comp, Collection<C> otherComps) {
    List<C> rVal = new ArrayList<>();
    for (C otherComp : otherComps) {
      if (comp != otherComp
          && comp.getShape().getRelationTo(otherComp.getShape()).isCollision()) {
        rVal.add(otherComp);
      }
    }
    return rVal;
  }

  /**
   * Find components that overlap one given component, checking the maximal
   * extensions (bounding boxes) of each shape first (from collection of
   * potentially colliding components, e.g. compartments with the same parent --
   * comp itself is ignored if contained in the collection)
   *
   * @param comp
   *          Component that moved recently (and may now collide)
   * @param otherComps
   *          Collection of compartments to check for overlap / collision
   * @return Components comp collides with (empty collection if none)
   */
  public static <C extends IShapedComponent> List<C> checkCollisionsPairwiseBB(
      IShapedComponent comp, Collection<C> otherComps) {
    int dim = comp.getPosition().getDimensions();
    List<C> rVal = new ArrayList<>();
    nextOtherComp: for (C otherComp : otherComps) {
      if (comp != otherComp) {
        IShape cs = comp.getShape();
        IShape os = otherComp.getShape();
        for (int d = 1; d <= dim; d++) {
          if (cs.getMax(d) < os.getMin(d) || cs.getMin(d) > os.getMax(d)) {
            continue nextOtherComp;
          }
        }
        if (comp.getShape().getRelationTo(otherComp.getShape()).isCollision()) {
          rVal.add(otherComp);
        }
      }
    }
    return rVal;
  }

  /**
   * Remove enclosing spatial components of given comp from given list of comps
   *
   * @param comp
   *          Spatial component
   * @param otherComps
   *          Collection of other comps to remove comp's parent, parent's
   *          parent, ... from (will be modified!)
   */
  public static void removeEnclosingComps(IShapedComponent comp,
      Collection<? extends IShapedComponent> otherComps) {
    // remove enclosing comps from collComps
    IShapedComponent encComp = comp.getEnclosingEntity();
    while (encComp != null && !otherComps.isEmpty()) {
      otherComps.remove(encComp);
      encComp = encComp.getEnclosingEntity();
    }
  }

  /**
   * Place one spatial component inside (the bounding box implied by) a given
   * shape. Changes position of first argument, and, by extension, the
   * properties of its shape (a reference to which is returned, e.g. for
   * subsequent collision checking). If the surrounding shape is not an
   * axis-aligned box, a check for proper inclusion should be performed
   * subsequently!
   *
   * @param compToPlace
   *          Spatial entity to move
   * @param surroundingShape
   *          Area into which to move compToPlace
   * @param rand
   *          Random number generator
   * @return Shape of compToPlace
   */
  public static <C extends IModifiableShapedComponent> IShape randomlyPlaceCompInside(
      C compToPlace, IShape surroundingShape, IRandom rand) {
    IShape shape = compToPlace.getShape();

    // random new position for shape determination in bounding box of
    // surrounding:
    for (int d = 1; d <= surroundingShape.getCenter().getDimensions(); d++) {
      double min = surroundingShape.getMin(d) + shape.getExtension(d) / 2;
      // TODO? insert xpos, ypos... extraction from attributes for easier init?
      // type of compToPlace would need to be modified (attributes unavailable)
      double max = surroundingShape.getMax(d) - shape.getExtension(d) / 2;
      if (max < min) {
        throw new SpatialException("Shape " + shape
            + " cannot fit into container " + surroundingShape);
      }
      compToPlace.moveAlongDimTo(d, min + rand.nextDouble() * (max - min));
    }
    return shape;
  }

  // private static final String[] dimPosAtt = { "", "xpos", "ypos", "zpos",
  // "" /* ;-) */, "" };

  /**
   * Comparator for sorting shaped components based on their (centers') distance
   * to a given point
   *
   * @param p
   *          CoordVector to some point
   * @return Comparator comparing two IShapedComponents depending on whether the
   *         first one is closer to p or the second. Note: this comparator
   *         imposes orderings that are inconsistent with equals: both may be
   *         equally far from p, yet not equal to each other (i.e.
   *         compare(o1,o2) may return 0 when o1.equals(o2) would be false).
   */
  public static Comparator<IShapedComponent> distanceToPointComparator(
      IPositionVector p) {
    final IPositionVector pc = p.copy();
    return new Comparator<IShapedComponent>() {
      @Override
      public int compare(IShapedComponent c1, IShapedComponent c2) {
        if (c1.equals(c2)) {
          return 0;
        }
        Double d1s = pc.distanceSquared(c1.getPosition());
        return d1s.compareTo(pc.distanceSquared(c2.getPosition()));
      }
    };
  }

  /**
   * Comparator for sorting {@link IShapedComponent} by the size of their shapes
   * (see {@link IShape#getSize()}) in ascending order.
   *
   * Note: this comparator imposes orderings that are inconsistent with equals.
   * (Components may have the same size without being equal.)
   */
  public static final Comparator<IShapedComponent> SIZE_COMPARATOR_ASCENDING =
      new VolumeComparatorAscending();

  /**
   * Comparator for sorting {@link IShapedComponent} by the size of their shapes
   * (see {@link IShape#getSize()}) in ascending order.
   *
   * Note: this comparator imposes orderings that are inconsistent with equals.
   * (Components may have the same size without being equal.)
   *
   * @author Arne Bittig
   */
  static class VolumeComparatorAscending implements
  Comparator<IShapedComponent>, Serializable {

    private static final long serialVersionUID = 2463088173761840320L;

    /**
     * {@inheritDoc}
     *
     * Note: this comparator imposes orderings that are inconsistent with
     * equals. (Components may have the same size without being equal.)
     */
    @Override
    public int compare(IShapedComponent c1, IShapedComponent c2) {
      Double c1Vol = c1.getShape().getSize();
      return c1Vol.compareTo(c2.getShape().getSize());
    }
  }

  /**
   * Comparator for sorting {@link IShapedComponent} by the size of their shapes
   * (see {@link IShape#getSize()}) in descending order.
   *
   * Note: this comparator imposes orderings that are inconsistent with equals.
   * (Components may have the same size without being equal.)
   */
  public static final Comparator<IShapedComponent> SIZE_COMPARATOR_DESCENDING =
      new VolumeComparatorDescending();

  /**
   * Comparator for sorting {@link IShapedComponent} by the size of their shapes
   * (see {@link IShape#getSize()}) in descending order, i.e. one that returns a
   * negative value for c1.compareTo(c2) if c1's size is larger than c2's, 0 if
   * it is equal and a positive value otherwise.
   *
   * Note: this comparator imposes orderings that are inconsistent with equals.
   * (Components may have the same size without being equal.)
   *
   * @author Arne Bittig
   */
  static class VolumeComparatorDescending implements
  Comparator<IShapedComponent>, Serializable {

    private static final long serialVersionUID = -7349066594812701130L;

    /**
     * {@inheritDoc}
     *
     * Note: this comparator imposes orderings that are inconsistent with
     * equals. (Components may have the same size without being equal.)
     */
    @Override
    public int compare(IShapedComponent c1, IShapedComponent c2) {
      Double c2Vol = c2.getShape().getSize();
      return c2Vol.compareTo(c1.getShape().getSize());
    }
  }

  /**
   * Comparator comparing {@link IShapedComponent}s by the size of their
   * enclosing components' shapes
   */
  public static final Comparator<IShapedComponent> PARENT_SIZE_COMPARATOR =
      new ParentSizeComparator();

  /**
   * Comparator for sorting Subvols by the size (size) of their enclosing
   * compartments
   *
   * @author Arne Bittig
   */
  static final class ParentSizeComparator implements
  Comparator<IShapedComponent>, java.io.Serializable {

    private static final long serialVersionUID = -447274820994369545L;

    @Override
    public int compare(IShapedComponent subvol1, IShapedComponent subvol2) {
      return Double.compare(subvol1.getEnclosingEntity().getShape().getSize(),
          subvol2.getEnclosingEntity().getShape().getSize());
    }

  }

  /**
   * Get {@link AxisAlignedBox} just big enough to comprise all shapes of given
   * shaped components
   *
   * @param comps
   *          Shaped components to include
   * @return Box containing all comps, null if any components' shape is null
   */
  public static IShape surroundingBox(
      Collection<? extends IShapedComponent> comps) {
    if (comps.isEmpty()) {
      return null;
    }
    Iterator<? extends IShapedComponent> svIt = comps.iterator();
    IShape svShape = svIt.next().getShape();
    if (svShape == null) {
      return null;
    }
    IPositionVector min = svShape.getCenter().minus(svShape.getMaxExtVector());
    IPositionVector max = svShape.getCenter().plus(svShape.getMaxExtVector());
    while (svIt.hasNext()) {
      svShape = svIt.next().getShape();
      min =
          Vectors
          .min(min, svShape.getCenter().minus(svShape.getMaxExtVector()));
      max =
          Vectors.max(max, svShape.getCenter().plus(svShape.getMaxExtVector()));
    }
    return new AxisAlignedBox(min, max);
  }
}
