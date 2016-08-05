/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.subvols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeUtils;
import org.jamesii.core.math.geometry.shapes.Sphere;
import org.jamesii.core.math.geometry.shapes.TorusSurface;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;

import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;

/**
 * Methods related to the initialization of a subvolume grid (static; may be
 * converted to instantiable class with interface to allow for different init
 * methods at a later point).
 *
 * @author Arne Bittig
 * @date Mar 20, 2012 (compiled from static methods in other classes, mostly
 *       {@link SubvolUtils})
 */
public final class SubvolInitializer {

  private SubvolInitializer() {
  }

  /**
   * Construct subvols underlying a given compartment structure, including
   * determination of useful subvol side length
   * 
   * @param compTree
   *          SpatialEntity tree / forest structure
   * @param nsmEntMap
   *          Dimensionless entities inside each compartment
   * @param minSvSize
   *          Minimum subvol size length
   * @param maxSvSize
   *          Maximum side length for subvols (actual size may be lower to
   *          guarantee lattice coverage of entities with small non-zero volume;
   *          use null or infinity for automatically determined size)
   * @param rand
   *          Random number generator (for marginal cases in distribution of
   *          content among subvolumes)
   * @return List of newly constructed subvols (with already distributed
   *         volumeless entities)
   */
  public static Map<SpatialEntity, Collection<Subvol>> constructSubvolGrid(
      IHierarchy<SpatialEntity> compTree,
      Map<SpatialEntity, ? extends Map<NSMEntity, Integer>> nsmEntMap,
      Double minSvSize, Double maxSvSize, IRandom rand) {

    List<Subvol> svGrid =
        createSvGridWithoutState(compTree, minSvSize, maxSvSize);
    if (svGrid.isEmpty()) {
      return new LinkedHashMap<>();
    }

    List<SpatialEntity> allCompsLargerFirst =
        new ArrayList<>(compTree.getAllNodes());
    Collections.sort(allCompsLargerFirst, GeoUtils.SIZE_COMPARATOR_DESCENDING);

    Map<SpatialEntity, Collection<Subvol>> compsSubvols = new LinkedHashMap<>();

    /*
     * assign subvols' parent compartments, first for larger comps (some
     * subvols' parents may be reassigned in later iterations of the loop to
     * smaller comps inside the larger ones)
     */
    Subvol someSv = svGrid.iterator().next();
    // CHECK: minor speed-up may be achieved by choosing more central sv
    for (SpatialEntity comp : allCompsLargerFirst) {
      assignSubvolsToComp(someSv, comp, compsSubvols);
    }
    placeNSMEntities(compsSubvols, nsmEntMap, rand);
    return compsSubvols;
  }

  /**
   * @param compTree
   * @param maxSvSize
   * @return
   */
  private static List<Subvol> createSvGridWithoutState(
      IHierarchy<SpatialEntity> compTree, Double minSvSize, Double maxSvSize) {
    Pair<SpatialEntity, IShape> rcas = getRootComp(compTree);
    SpatialEntity ctroot = rcas.getFirstValue();
    IShape rootShape = rcas.getSecondValue();

    // determine grid size, assuming compartments extending roughly equally
    // in all directions (spheres, cubes)
    SortedSet<Double> compSizes = new TreeSet<>();
    int dim = 0; // take it from first comp that comes along
    for (SpatialEntity comp : compTree.getAllNodes()) {
      compSizes.add(comp.getShape().getSize());
      if (dim == 0) {
        dim = comp.getPosition().getDimensions();
      }
    }
    if (compSizes.isEmpty()) {
      // return Collections.emptyList();
    }
    Double minCompSize = Collections.min(compSizes);
    double svSl =
        Sphere.calculateRadius(minCompSize, dim) * 2. / Math.sqrt(dim);
    svSl = adjustByGivenMinMax(svSl, minSvSize, maxSvSize);
    svSl = adjustByMinRootShapeExt(svSl, rootShape);
    svSl = unadjustForNSMOnly(svSl, minSvSize, maxSvSize, compTree);

    Subvol start = new Subvol(rootShape, null);
    // start.setEnclosingEntity(ctroot); // will be done later considering comp
    // Tree
    List<Subvol> svGrid = splitSubvolWithoutStateUpdate(start, svSl);
    ApplicationLogger.log(Level.INFO, svGrid.size() + " subvolumes created in "
        + "root compartment " + ctroot);
    return svGrid;
  }

  /**
   * If calculated side length is larger than user-specified max, adjust
   * downward; if it is smaller than user-specified min, adjust upward
   * (TODO/CHECK: ignore & warn?!)
   * 
   * @param toAdjust
   * @param min
   * @param max
   * @return
   */
  private static double adjustByGivenMinMax(Double toAdjust, Double min,
      Double max) {
    if (min != null && max != null && max < min) {
      throw new IllegalArgumentException(min + " > " + max);
    }
    double adjusted = toAdjust;
    if (max != null) {
      adjusted = Math.min(toAdjust, max);
    }
    if (min != null) {
      adjusted = Math.max(toAdjust, min);
    }
    return adjusted;
  }

  /**
   * Subvolume side length must be no larger than the narrowest dimension of the
   * root shape, even if side length calculated from volume (or user-specified)
   * is larger
   * 
   * @param svSl
   * @param rootShape
   * @return
   */
  private static double adjustByMinRootShapeExt(double svSl, IShape rootShape) {
    double adjustedSvSl = svSl;
    double[] rootShapeExt = rootShape.getMaxExtVector().toArray();
    // maxExtVector.times(2) needed, but causes trouble if rootShape is
    // Torus
    for (double ext : rootShapeExt) {
      if (2 * ext < adjustedSvSl) {
        adjustedSvSl = 2 * ext;
      }
    }
    return adjustedSvSl;
  }

  private static double unadjustForNSMOnly(double svSl, Double minSvSize,
      Double maxSvSize, IHierarchy<SpatialEntity> compTree) {
    if (minSvSize == null && maxSvSize != null && svSl < maxSvSize
        && compTree.getAllNodes().size() == 1) {
      svSl = maxSvSize;
    }
    return svSl;
  }

  /**
   * Get root comp and shape from comp hierarchy
   * 
   * @param compTree
   * @return
   */
  private static <C extends IShapedComponent> Pair<C, IShape> getRootComp(
      IHierarchy<C> compTree) {
    Collection<C> ctroots = new ArrayList<>(compTree.getRoots());
    ctroots.addAll(compTree.getOrphans());
    if (ctroots.size() > 1) {
      // throw new IllegalStateException("Subvolume grid construction"
      // + " so far requires single root compartment.");
      return new Pair<>(null, GeoUtils.surroundingBox(ctroots));
    }
    C rootComp = ctroots.iterator().next();
    return new Pair<>(rootComp, rootComp.getShape());
  }

  /**
   * Find subvols sufficiently overlapped by given comp, assign parent comp of
   * these svs (and remove from subvols associated with previous parent, if
   * applicable -- sv may have orginally belonged to enclosing comp of the one
   * given here as parameter)
   * 
   * @param someCentralSv
   *          Subvol from which to start looking for first overlapped subvol
   *          (searching the neighborhood in the proper direction)
   * @param comp
   *          SpatialEntity
   * @param compsSubvols
   *          Map of subvols already assigned to (larger) spatial entities
   * @return the last parameter
   */
  private static Map<SpatialEntity, Collection<Subvol>> assignSubvolsToComp(
      Subvol someCentralSv, SpatialEntity comp,
      Map<SpatialEntity, Collection<Subvol>> compsSubvols) {
    Set<Subvol> compSubvols = findSubvolsForComp(comp, someCentralSv);
    for (Subvol compSv : compSubvols) {
      SpatialEntity oldParent = compSv.getEnclosingEntity();
      if (oldParent != null) {
        compsSubvols.get(oldParent).remove(compSv);
      }
      compSv.setEnclosingEntity(comp);
    }
    compsSubvols.put(comp, compSubvols);
    return compsSubvols;
  }

  /**
   * Find subvols sufficiently overlapped by given comp
   * 
   * @param comp
   *          Spatial entity to find Subvols for
   * @param someCentralSv
   *          Subvol from which to start looking for first overlapped subvol
   *          (searching the neighborhood in the proper direction)
   * 
   * @return found subvols (enclosing entity not yet changed)
   */
  public static Set<Subvol> findSubvolsForComp(SpatialEntity comp,
      Subvol someCentralSv) {
    Subvol startSv =
        SubvolUtils.findSubvolIncludingPoint(someCentralSv, comp.getPosition());
    if (!svBelongsToComp(startSv, comp)) {
      /* DEBUG */
      ApplicationLogger.log(Level.SEVERE,
          "Comp center in sv: "
              + startSv.getShape().includesPoint(comp.getPosition())
              + ", Sv center in comp: "
              + comp.getShape().includesPoint(startSv.getPosition()) + "\n"
              + startSv.getShape() + "\n" + comp.getShape());
      /* /DEBUG */
      ApplicationLogger.log(Level.SEVERE,
          comp + "'s reference point (center) is in a "
              + "subvol whose center is outside the compartment. "
              + "Cannot assign any subvols to it. This "
              + "will result in undesired behavior.");
      return new LinkedHashSet<>(0);
    }
    Set<Subvol> compSubvols = new LinkedHashSet<>();
    compSubvols.add(startSv);
    findBelongingSvsRec(comp, compSubvols, null, null);
    return compSubvols;
  }

  /**
   * Recursively find subvolumes belonging to comp, starting from a collection
   * of svs for which this relation is already known (checks their neighbors,
   * then recursively further neighbors if appropriate; does not (re-)assign
   * subvols' parent compartments)
   * 
   * @param comp
   *          SpatialEntity
   * @param start
   *          Subvols from which to start search (will be added to pos without
   *          check; method returns silently if empty)
   * @param pos
   *          Some subvols overlapped by comp (will be changed; if null, start
   *          will be used (and thus also changed))
   * @param neg
   *          Some subvols definitely not overlapped by comp (may be empty)
   *          (will be changed; if null, will be initialized automatically)
   * 
   */
  private static void findBelongingSvsRec(IShapedComponent comp,
      Collection<Subvol> start, Collection<Subvol> oriPos,
      Collection<Subvol> oriNeg) {
    if (start.isEmpty()) {
      return;
    }
    Collection<Subvol> neg =
        oriNeg != null ? oriNeg : new LinkedHashSet<Subvol>();
    Collection<Subvol> pos;
    if (oriPos != null) {
      pos = oriPos;
      pos.addAll(start);
    } else {
      pos = start;
    }
    // separately record newly discovered positives
    Collection<Subvol> newStart = new LinkedHashSet<>();
    for (Subvol psv : start) {
      Collection<Subvol> pneighs = psv.getNeighbors();
      for (Subvol pneigh : pneighs) {
        if (pos.contains(pneigh) || neg.contains(pneigh)) {
          continue; // already checked
        }
        if (svBelongsToComp(pneigh, comp)) {
          newStart.add(pneigh);
        } else {
          neg.add(pneigh);
        }
      }
    }
    // pos.addAll(newPos);

    findBelongingSvsRec(comp, newStart, pos, neg);
  }

  /**
   * Check whether given subvol belongs to given comp
   * 
   * @param sv
   *          Subvol
   * @param comp
   *          Comp
   * @return Is the center of sv inside comp?
   */
  private static boolean svBelongsToComp(Subvol sv, IShapedComponent comp) {
    return comp.getShape().includesPoint(sv.getPosition());
  }

  /**
   * Distribute dimensionless subentities of comps to the subvols belonging to
   * them, paying attention not to include subvols acutally belonging to
   * children of a comp.
   * 
   * @param compsSubvols
   * @param nsmEntMap
   * @param rand
   */
  private static void placeNSMEntities(
      Map<SpatialEntity, Collection<Subvol>> compsSubvols,
      Map<SpatialEntity, ? extends Map<NSMEntity, Integer>> nsmEntMap,
      IRandom rand) {

    placeNSMEntitiesWithGivenPosition(compsSubvols, nsmEntMap);

    distributeRemainingNSMEntities(compsSubvols, nsmEntMap, rand);
  }

  private static void placeNSMEntitiesWithGivenPosition(
      Map<SpatialEntity, Collection<Subvol>> compsSubvols,
      Map<SpatialEntity, ? extends Map<NSMEntity, Integer>> nsmEntMap) {
    // first, place NSM entities that have explicit position given
    for (Map.Entry<SpatialEntity, ? extends Map<NSMEntity, Integer>> cse : nsmEntMap
        .entrySet()) {
      SpatialEntity comp = cse.getKey();
      Subvol someSvInComp = compsSubvols.get(comp).iterator().next();
      Iterator<Map.Entry<NSMEntity, Integer>> nsmEntIt =
          cse.getValue().entrySet().iterator();
      while (nsmEntIt.hasNext()) {
        Map.Entry<NSMEntity, Integer> nsmE = nsmEntIt.next();
        NSMEntity ent = nsmE.getKey();
        Integer amount = nsmE.getValue();
        if (ent.hasAttribute(SpatialAttribute.POSITION.toString())) {
          boolean possible =
              placeNSMEntitiesAtGivenPosition(comp, someSvInComp, ent, amount);
          if (possible) {
            nsmEntIt.remove();
          } else {
            ApplicationLogger.log(Level.SEVERE,
                ent + " could not be placed inside " + comp
                    + "at given position"
                    + " (subvolume there belongs to different spatial entity). "
                    + amount + " will be placed randomly inside " + comp);
          }
        }
      }
    }
  }

  private static boolean placeNSMEntitiesAtGivenPosition(SpatialEntity comp,
      Subvol someSvInComp, NSMEntity ent, Integer amount) {
    Object val = ent.getAttribute(SpatialAttribute.POSITION.toString());
    boolean possible = false;
    if (val instanceof IPositionVector) {
      IPositionVector pos = (IPositionVector) val;
      Subvol subvol = SubvolUtils.findSubvolIncludingPoint(someSvInComp, pos);
      if (subvol.getEnclosingEntity() == comp) {
        possible = true;
        NSMEntity ent2 = new NSMEntity(ent);
        ent2.removeTempAttribute(SpatialAttribute.POSITION.toString());
        subvol.updateState(ent2, amount);
      }
    }
    return possible;
  }

  private static void distributeRemainingNSMEntities(
      Map<SpatialEntity, Collection<Subvol>> compsSubvols,
      Map<SpatialEntity, ? extends Map<NSMEntity, Integer>> nsmEntMap,
      IRandom rand) {
    // then, distribute remaining NSMEntities in subvols of belonging comps
    for (Map.Entry<SpatialEntity, Collection<Subvol>> cse : compsSubvols
        .entrySet()) {
      SpatialEntity comp = cse.getKey();
      if (nsmEntMap.containsKey(comp)) {
        // slightly inefficient as #distributeSubvolContent does
        // additional calculations to deal with subvols of different
        // volume, while here all have the same volume
        distributeNSMEntities(nsmEntMap.get(comp), cse.getValue(), rand);
      }
    }
  }

  /**
   * Distribute entities from given state map among given subvolumes,
   * proportional to the volume of the latter
   * 
   * @param map
   *          State map (as returned by {@link Subvol#getState()})
   * @param svs
   *          Collection of target subvolumes
   * @param rand
   *          Random number generator for distribution of marginal entities
   */
  private static void distributeNSMEntities(Map<NSMEntity, Integer> map,
      Collection<Subvol> svs, IRandom rand) {
    double volSum = 0.0;
    for (Subvol sv : svs) {
      volSum += sv.getVolume();
    }
    for (Subvol sv : svs) {
      // TODO: why not use sv.updateAll?
      for (Map.Entry<NSMEntity, Integer> e : UpdateableAmountMap
          .split(map, sv.getVolume() / volSum, rand).entrySet()) {
        sv.updateState(e.getKey(), e.getValue());
      }
      volSum -= sv.getVolume();
    }
  }

  /**
   * Split subvol, creating subdivisions (with correct neighborhood), and
   * distribute content equally over these subdivisions
   * 
   * @param sv
   *          Subvol to split (shape required, rectangular shape not)
   * @param size
   *          Side length of the resulting subvols (squares/cubes)
   * @param rand
   *          Random number generator for distribution of marginal entities
   * @return List of resulting subvols
   */
  public static List<Subvol> splitSubvol(Subvol sv, double size, IRandom rand) {
    // actual split
    List<Subvol> newChildSvs = splitSubvolWithoutStateUpdate(sv, size);
    if (newChildSvs.size() == 1) {
      return Arrays.asList(sv);
    }

    // update state, i.e. distribute content of sv over new ones --
    // #distributeSubvolContent could be used, but needs more calculation
    // (as it can not assume equal volume of all targets)
    int numLeft = newChildSvs.size();
    for (Subvol newSv : newChildSvs) {
      newSv.updateState(sv.splitState(1. / numLeft, rand));
      numLeft--;
    }
    assert sv.getState().isEmpty();
    return newChildSvs;

  }

  /**
   * Split subvol, creating subdivisions (with correct neighborhood) only,
   * leaving particles in the original (now neighborless) subvol
   * 
   * @param sv
   *          Subvol to split (shape required, rectangular shape not)
   * @param sideLength
   *          Side length of the resulting subvols (squares/cubes)
   * @return List of resulting subvols
   * @see #splitSubvol(Subvol, double, IRandom)
   * @see #distributeNSMEntities(UpdateableAmountMap, Collection, IRandom)
   */
  private static List<Subvol> splitSubvolWithoutStateUpdate(Subvol sv,
      double sideLength) {
    Collection<Subvol> oldNeighbors = new ArrayList<>(sv.getNeighbors());

    // splitting of shapes (boundingBox of AxisAlignedBox is the box itself)
    ShapeUtils.SplitResult splitRes = ShapeUtils
        .splitAllDim((AxisAlignedBox) sv.getShape().boundingBox(), sideLength);
    List<AxisAlignedBox> newShapes = splitRes.getBoxes();
    // subvol creation and neighborhood processing
    List<Subvol> newChildSvs = new ArrayList<>();
    for (int newIdx = 0; newIdx < newShapes.size(); newIdx++) {
      AxisAlignedBox splitShape = newShapes.get(newIdx);
      Subvol newSv = new Subvol(splitShape, null);
      newSv.setEnclosingEntity(sv.getEnclosingEntity());
      newChildSvs.add(newSv);
    }
    int[] splitsPerDim = splitRes.getNumberPerDimension();
    renameSubvolsAfterSplit(newChildSvs, splitsPerDim);

    addNeighborRelationsAfterSplit(newChildSvs, splitsPerDim,
        sv.getShape() instanceof TorusSurface);

    if (!(sv.getShape() instanceof AxisAlignedBox)) { // shortcut
      removeNotCoveredSubvols(newChildSvs, sv.getShape());
    }

    for (Subvol oldNeigh : oldNeighbors) {
      oldNeigh.removeNeighbor(sv);
    }

    return newChildSvs;

  }

  private static void renameSubvolsAfterSplit(List<Subvol> newChildSvs,
      int[] splitsPerDim) {
    int[] idxDim = new int[splitsPerDim.length];
    int maxSvIdx = newChildSvs.size() - 1;
    for (int newIdx = 0; newIdx <= maxSvIdx; newIdx++) {
      Subvol subvol = newChildSvs.get(newIdx);
      subvol.setName(intArrToString(idxDim));
      if (newIdx == maxSvIdx) {
        break;
      }
      int dim = idxDim.length - 1;
      while (++idxDim[dim] == splitsPerDim[dim]) {
        idxDim[dim] = 0;
        dim--;
      }
    }
  }

  /**
   * @param arr
   *          Array with at least one element
   * @return String representation using parenthesis and no spaces
   */
  private static String intArrToString(int[] arr) {
    StringBuilder sb = new StringBuilder("(");
    sb.append(arr[0]);
    for (int i = 1; i < arr.length; i++) {
      sb.append(',');
      sb.append(Integer.toString(arr[i]));
    }
    return sb.append(')').toString();

  }

  private static void removeNotCoveredSubvols(List<Subvol> subvols,
      IShape shape) {
    for (Iterator<Subvol> it = subvols.iterator(); it.hasNext();) {
      Subvol childSv = it.next();
      if (!shape.includesPoint(childSv.getPosition())) {
        for (Subvol neigh : childSv.getNeighbors()) {
          // childSv.removeNeighbor(neigh); causes ConcurrentModEx!
          neigh.removeNeighbor(childSv);
        }
        it.remove();
      }
    }
  }

  /**
   * Add neighbor relations to subvols resulting from splitting a bigger subvol
   * (i.e. assuming equal extension of all subvols)
   * 
   * @param subvols
   * @param splitsPerDim
   */
  private static void addNeighborRelationsAfterSplit(List<Subvol> subvols,
      int[] splitsPerDim, boolean connectOppositeEnds) {
    IDisplacementVector maxExtVector =
        subvols.get(0).getShape().getMaxExtVector();
    int stepsInDim = 1;
    for (int iDim = maxExtVector.getDimensions() - 1; iDim >= 0; iDim--) {
      if (splitsPerDim[iDim] <= 1) {
        continue; // no neighbors along this dimension
      }
      double diffCorrFac = .5 / maxExtVector.get(iDim + 1);
      diffCorrFac *= diffCorrFac;
      int modVal = stepsInDim * splitsPerDim[iDim];
      for (int iSv = subvols.size() - 1; iSv >= 0; iSv--) {
        Subvol subvolAtI = subvols.get(iSv);
        if ((iSv + stepsInDim) / modVal == iSv / modVal) {
          // normal connection to next element in row/column/...
          Subvol neigh = subvols.get(iSv + stepsInDim);
          subvolAtI.addNeighborOneWay(neigh, diffCorrFac);
          neigh.addNeighborOneWay(subvolAtI, diffCorrFac);
        } else
          // iSv refers to final sv of row/column/stack(?!)
          if (connectOppositeEnds) {
          if (splitsPerDim[iDim] > 2) {
            Subvol neigh = subvols.get(iSv - modVal + stepsInDim);
            subvolAtI.addNeighborOneWay(neigh, diffCorrFac);
            neigh.addNeighborOneWay(subvolAtI, diffCorrFac);
          } else {
            // subvols are "neighbors" on both sided
            // already added once => now add with twice the rate
            Subvol neigh = subvols.get(iSv - stepsInDim);
            subvolAtI.addNeighborOneWay(neigh, 2. * diffCorrFac);
            neigh.addNeighborOneWay(subvolAtI, 2. * diffCorrFac);
          }
        }
      }
      stepsInDim = modVal;
    }
  }
}
