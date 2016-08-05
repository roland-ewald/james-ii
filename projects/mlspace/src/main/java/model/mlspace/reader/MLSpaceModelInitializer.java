/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * 
 */
package model.mlspace.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import model.mlspace.entities.InitEntity;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.TransferInRule;
import model.mlspace.rules.match.Match;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.IModifiableShapedComponent;
import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.SpatialException;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeRelation;
import org.jamesii.core.math.geometry.spatialindex.ISpatialIndex;
import org.jamesii.core.math.geometry.spatialindex.StaticGridSpatialIndex;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.WrappedJamesRandom;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Method(s) for creating model from parsed information, incl. initialization of
 * comp tree (incl. random placement of entities if necessary)
 * 
 * @author Arne Bittig
 * @date 02.05.2014 (date of extraction from {@link MLSpaceParserHelper})
 */
public final class MLSpaceModelInitializer {

  private MLSpaceModelInitializer() {
  }

  private static final int MAX_TOT_ATTEMPTS = 16;

  private static final int ATTEMPTS_PER_SHAPE = 16;

  /**
   * create compartment hierarchy (fill into given variable) - TODO: may not
   * correctly record top-level entities without contained compartments
   * 
   * @param compTree
   *          SpatialEntity hierarchy data structure
   * @param init
   *          Entities to create (and amounts)
   * @param transferInRules
   *          Transfer rules for postponed region init (empty for normal region
   *          init; experimental)
   * @param modEntFac
   *          Model entity factory (container of species definitions)
   * @param rand
   *          Random number generator
   * @return Map compartments->contained non-compartment entities
   */
  public static NSMEntMap fillCompTree(IHierarchy<SpatialEntity> compTree,
      Map<InitEntity, Integer> init,
      Collection<TransferInRule> transferInRules, ModelEntityFactory modEntFac,
      IRandom rand) {
    NSMEntMap nsmEntMap = new NSMEntMap();
    Map<SpatialEntity, InitEntity> originalInitEnt = new LinkedHashMap<>();
    // map to keep track of which comp was created from which init entity,
    // as the sub-entities specified in the latter have to be created in
    // each of the former

    List<SpatialEntity> compsWellPlaced =
        new ArrayList<>(compTree.getChildren(null));
    // parent may already have children from previous transferIn
    List<SpatialEntity> compsAlreadyInitialized =
        new ArrayList<>(compsWellPlaced);
    List<SpatialEntity> compsToBePlaced = new ArrayList<>();
    fillCompTreeCurrentLevel(init, null, nsmEntMap, originalInitEnt,
        compsWellPlaced, compsToBePlaced, modEntFac, transferInRules, compTree,
        rand);

    handlePlacementOfTopLevelComps(compsWellPlaced, compsToBePlaced);

    compsWellPlaced.addAll(compsToBePlaced);

    for (SpatialEntity comp : compsWellPlaced) {
      if (compsAlreadyInitialized.contains(comp)) {
        continue;
      }
      compTree.addOrphan(comp);
      Map<InitEntity, Integer> subEntities =
          originalInitEnt.get(comp).getSubEntities();
      if (subEntities != null && !subEntities.isEmpty()) {
        NSMEntMap subEntMap =
            fillCompTreeRec(compTree, subEntities, comp, transferInRules,
                modEntFac, rand);
        nsmEntMap.addAll(subEntMap);
      }
    }

    return nsmEntMap;
  }

  private static NSMEntMap fillCompTreeRec(IHierarchy<SpatialEntity> compTree,
      Map<InitEntity, Integer> init, SpatialEntity parent,
      Collection<TransferInRule> transferInRules, ModelEntityFactory modEntFac,
      IRandom rand) {
    NSMEntMap nsmEntMap = new NSMEntMap();
    Map<SpatialEntity, InitEntity> originalInitEnt = new LinkedHashMap<>();
    // map to keep track of which comp was created from which init entity,
    // as the sub-entities specified in the latter have to be created in
    // each of the former

    List<SpatialEntity> compsWellPlaced =
        new ArrayList<>(compTree.getChildren(parent));
    // parent may already have children from previous transferIn
    List<SpatialEntity> compsAlreadyInitialized =
        new ArrayList<>(compsWellPlaced);
    List<SpatialEntity> compsToBePlaced = new ArrayList<>();
    fillCompTreeCurrentLevel(init, parent, nsmEntMap, originalInitEnt,
        compsWellPlaced, compsToBePlaced, modEntFac, transferInRules, compTree,
        rand);

    boolean placementSuccess =
        placeCompsInsideSameParent(compsToBePlaced, compsWellPlaced, parent,
            compTree, transferInRules, rand);
    if (!placementSuccess) {
      throw new SpatialInitException(
          "Could not place spatial entities without overlap");
    }

    compsWellPlaced.addAll(compsToBePlaced);

    for (SpatialEntity comp : compsWellPlaced) {
      if (compsAlreadyInitialized.contains(comp)) {
        continue;
      }
      Map<InitEntity, Integer> subEntities =
          originalInitEnt.get(comp).getSubEntities();
      if (subEntities != null && !subEntities.isEmpty()) {
        NSMEntMap subEntMap =
            fillCompTreeRec(compTree, subEntities, comp, transferInRules,
                modEntFac, rand);
        nsmEntMap.addAll(subEntMap);
      }
    }

    return nsmEntMap;
  }

  private static void handlePlacementOfTopLevelComps(
      List<SpatialEntity> compsWellPlaced, List<SpatialEntity> compsToBePlaced) {
    if (compsToBePlaced.size() == 1 && compsWellPlaced.isEmpty()) {
      compsToBePlaced.get(0).removeTempAttribute(
          ModelEntityFactory.WRONG_POS_MARKER);
    } else if (!compsToBePlaced.isEmpty()) {
      throw new SpatialInitException(
          "Neither position nor surrounding entity given for "
              + compsToBePlaced);
    }
  }

  /**
   * Create shapes on on top of current level, store next level in resp. map,
   * separate well-placed comps (i.e. those with given coordinates) from those
   * to be placed (extracted methods for less cyclomatic complexity)
   * 
   * @param init
   *          Map of InitEnities
   * @param parent
   *          Surrounding entity
   * @param nsmEntMap
   * @param originalInitEnt
   * @param compsWellPlaced
   * @param modEntFac
   * @param transferInRules
   *          Transfer rules to apply immediately if possible for comps placed
   *          on top of regions but not (yet) in them
   * @param compTree
   *          Comp tree for possible changes by immediately applied transfers
   * @param rand
   *          RNG for possible immediately applied transfers (see
   *          {@link #resolveByTransfer(SpatialEntity, SpatialEntity, Collection, IHierarchy, IRandom)}
   *          )
   */
  private static void fillCompTreeCurrentLevel(Map<InitEntity, Integer> init,
      SpatialEntity parent, NSMEntMap nsmEntMap,
      Map<SpatialEntity, InitEntity> originalInitEnt,
      List<SpatialEntity> compsWellPlaced, List<SpatialEntity> compsToBePlaced,
      ModelEntityFactory modEntFac, Collection<TransferInRule> transferInRules,
      IHierarchy<SpatialEntity> compTree, IRandom rand) {
    for (Map.Entry<InitEntity, Integer> e : init.entrySet()) {
      if (!modEntFac.isSpatial(e.getKey())) {
        nsmEntMap.add(parent,
            modEntFac.createNSMEntities(e.getKey(), e.getValue(), parent));
        continue;
      }
      List<SpatialEntity> comps =
          createAndPlaceComps(e.getKey(), e.getValue(), parent,
              compsWellPlaced, compsToBePlaced, modEntFac, transferInRules,
              compTree, rand);
      for (SpatialEntity comp : comps) {
        originalInitEnt.put(comp, e.getKey());
      }
    }
  }

  /**
   * Create given number of instances of given initial (spatial) entity
   * 
   * @param ent
   *          Definition of spatial entity
   * @param amount
   *          Amount
   * @param parent
   *          Surrounding entity
   * @param compsWellPlaced
   *          List to add fixed-position entities to
   * @param compsToBePlaced
   *          List to add to-be-positioned entities to
   * @param modEntFac
   * @param transferInRules
   * @param rand
   * @param compTree
   * @return List of created entities
   */
  private static List<SpatialEntity> createAndPlaceComps(InitEntity ent,
      int amount, SpatialEntity parent, List<SpatialEntity> compsWellPlaced,
      List<SpatialEntity> compsToBePlaced, ModelEntityFactory modEntFac,
      Collection<TransferInRule> transferInRules,
      IHierarchy<SpatialEntity> compTree, IRandom rand) {
    List<SpatialEntity> comps =
        modEntFac.createSpatialEntities(ent, amount, parent,
            Collections.<String, Object> emptyMap());
    for (SpatialEntity comp : comps) {
      if (comp.hasAttribute(ModelEntityFactory.WRONG_POS_MARKER)) {
        compsToBePlaced.add(comp);
      } else {
        validatePosAndTransferComp(comp, parent, compsWellPlaced,
            transferInRules, compTree, rand);
        compsWellPlaced.add(comp);
      }
    }
    Collections.sort(compsToBePlaced, GeoUtils.SIZE_COMPARATOR_DESCENDING);
    return comps;
  }

  private static final double OCC_THRESHOLD_FOR_GRID_ATTEMPT = 0.8;

  /**
   * @param compsToBePlaced
   *          Spatial entities to put at random positions without overlap
   * @param compsWellPlaced
   *          Spatial entities already present not to be overlapped
   * @param parent
   *          Common surrounding spatial entity (i.e. boundary for random
   *          placement)
   * @param transferInRules
   *          Rules to apply if smaller entity ends up in larger one (empty
   *          collection if none -- placement then will be attempted anew)
   * @param compTree
   * @param rand
   * @return success value
   */
  private static boolean placeCompsInsideSameParent(
      List<SpatialEntity> compsToBePlaced,
      Collection<SpatialEntity> compsWellPlaced, SpatialEntity parent,
      IHierarchy<SpatialEntity> compTree,
      Collection<TransferInRule> transferInRules, IRandom rand) {
    if (compsToBePlaced.isEmpty()) {
      // comps.isEmpty: nothing to place <- always possible
      return true;
    }

    double occupancyRatio =
        checkOccupancyRatio(compsToBePlaced, parent, compsWellPlaced,
        /* parent.isHardBounded() */true);
    Collections.sort(compsToBePlaced, GeoUtils.SIZE_COMPARATOR_DESCENDING);

    if (occupancyRatio >= OCC_THRESHOLD_FOR_GRID_ATTEMPT
        && placeShapesWithoutOverlapGrid(compsToBePlaced, parent,
            compsWellPlaced, compTree, transferInRules, rand).isEmpty()) {
      return true;
    }

    int lastTotPlacementAttempts = 0;
    while (lastTotPlacementAttempts < MAX_TOT_ATTEMPTS
        && !randomlyPlaceShapesWithoutOverlap(compsToBePlaced, parent,
            compsWellPlaced, compTree, transferInRules, rand,
            ATTEMPTS_PER_SHAPE).isEmpty()) {
      lastTotPlacementAttempts++;
    }
    if (lastTotPlacementAttempts == MAX_TOT_ATTEMPTS
        && (occupancyRatio >= OCC_THRESHOLD_FOR_GRID_ATTEMPT || !placeShapesWithoutOverlapGrid(
            compsToBePlaced, parent, compsWellPlaced, compTree,
            transferInRules, rand).isEmpty())) {
      ApplicationLogger.log(Level.SEVERE, "Error inside " + parent + "(vol: "
          + parent.getShape().getSize() + ". " + lastTotPlacementAttempts
          + " attempts to place " + numAndTotalSizeStr(compsToBePlaced)
          + " in presence of " + numAndTotalSizeStr(compsWellPlaced)
          + " failed.");
      return false;
    }
    for (SpatialEntity ctbp : compsToBePlaced) {
      ctbp.removeTempAttribute(ModelEntityFactory.WRONG_POS_MARKER);
    }
    // TODO: bindings
    return true;
  }

  private static String numAndTotalSizeStr(
      Iterable<? extends IShapedComponent> comps) {
    int num = 0;
    double size = 0.;
    for (IShapedComponent comp : comps) {
      size += comp.getShape().getSize();
      num++;
    }
    return num + " comps with total size " + size;

  }

  /**
   * Check whether compartment is completely within parent and does not overlap
   * other compartments; warn otherwise
   * 
   * @param comp
   *          SpatialEntity
   * @param parent
   *          SpatialEntity comp should be in
   * @param otherComps
   *          Compartments comp should not collide with
   * @param transferInRules
   * @param rand
   * @param compTree
   * @return true iff immediate transfer was applied
   */
  private static boolean validatePosAndTransferComp(SpatialEntity comp,
      SpatialEntity parent, Collection<SpatialEntity> otherComps,
      Collection<TransferInRule> transferInRules,
      IHierarchy<SpatialEntity> compTree, IRandom rand) {
    List<SpatialEntity> collComps =
        GeoUtils.checkCollisionsPairwise(comp, otherComps);
    int collRes =
        transferOrNonOverlap(comp, collComps, transferInRules, compTree, rand);
    if (collRes < 0) {
      ApplicationLogger.log(Level.WARNING, comp + " overlaps " + collComps
          + "; Transfer attempt failed at position " + -collRes);
    }
    if (parent != null) {
      ShapeRelation rel = parent.getShape().getRelationTo(comp.getShape());
      if (rel != ShapeRelation.SUPERSET) {
        ApplicationLogger.log(Level.SEVERE, comp
            + " is not entirely within parent " + parent);
      }
      if (compTree.getParent(comp) == null) {
        compTree.addChildParentRelation(comp, parent);
      }
    }
    return collRes > 0 || collRes < -1;
  }

  /**
   * Randomly place given shaped components so that no two of them overlap, but
   * all are still contained within another given shape. Performs volume check
   * and sorts comps to be placed by size (descending).
   * 
   * @param comps
   *          Shaped components to move (current coordinates are ignored &
   *          changed)
   * @param container
   *          Shaped component containing the others
   * @param otherComps
   *          Other shaped components in container (not to be moved)
   * @param rand
   *          Random number generator
   * @param compTree
   *          Hierarchy of compartments (to add placed comps to)
   * @param transferInRules
   *          Transfer rules to apply immediately if appropriate
   * @param attemptsPerShape
   *          Average number of attempts to place shape to exceed before
   *          retrying the entire placement
   * @return List of comps that could not be placed (empty if successful)
   * @throws SpatialInitException
   *           if a shape is too large to fit into the container or all comps'
   *           sizes combined is more than the container's size
   */
  public static <C extends SpatialEntity> List<C> randomlyPlaceShapesWithoutOverlap(
      List<C> comps, SpatialEntity container, Collection<C> otherComps,
      IHierarchy<SpatialEntity> compTree,
      Collection<TransferInRule> transferInRules, IRandom rand,
      int attemptsPerShape) {

    IShape contShape = container.getShape();
    int placingAttemptsMax = attemptsPerShape * comps.size();
    int placingAttemptsSoFar = 0;

    Collection<C> placedComps = new ArrayList<>(otherComps);
    ISpatialIndex<C> spatialIndex = null;
    if (comps.size() > 100) {
      spatialIndex =
          new StaticGridSpatialIndex<>(contShape, (int) Math.sqrt(comps.size()));
      for (C comp : placedComps) {
        spatialIndex.registerNewEntity(comp);
      }
    }
    for (int i = 0; i < comps.size(); i++) {
      C c = comps.get(i);
      IShape shape = c.getShape();
      boolean wellPlaced = false;
      while (!wellPlaced) {
        placingAttemptsSoFar++;
        if (placingAttemptsSoFar > placingAttemptsMax) {
          return comps.subList(i, comps.size());
        }
        GeoUtils.randomlyPlaceCompInside(c, contShape, rand);
        // check whether shape is still inside container
        if (contShape.getRelationTo(shape) != ShapeRelation.SUPERSET) {
          continue; // this placing attempt failed
        }
        List<C> collComps;
        if (spatialIndex == null) {
          collComps = GeoUtils.checkCollisionsPairwise(c, placedComps);
        } else {
          collComps = spatialIndex.collidingComps(c);
        }
        int collRes =
            transferOrNonOverlap(c, collComps, transferInRules, compTree, rand);
        wellPlaced = collRes >= 0;
      }
      // we got here => shape still inside container, no collisions
      if (spatialIndex != null) {
        spatialIndex.registerNewEntity(c);
      }
      placedComps.add(c);
      if (compTree.getParent(c) == null) {
        compTree.addChildParentRelation(c, container);
      }
    }
    // we got here => all comps placed in less then placingAttemptsMax
    return Collections.emptyList();
  }

  /**
   * @param compsToBePlaced
   * @param parent
   * @param compsWellPlaced
   * @param compTree
   * @param transferInRules
   * @param rand
   * @return
   */
  private static List<SpatialEntity> placeShapesWithoutOverlapGrid(
      List<SpatialEntity> compsToBePlaced, SpatialEntity parent,
      Collection<SpatialEntity> compsWellPlaced,
      IHierarchy<SpatialEntity> compTree,
      Collection<TransferInRule> transferInRules, IRandom rand) {
    // should already be sorted, but another attempt probably does not hurt
    Collections.sort(compsToBePlaced, GeoUtils.SIZE_COMPARATOR_DESCENDING);

    double[] parMaxExt = parent.getShape().getMaxExtVector().toArray();
    double[] largestCompMaxExt =
        compsToBePlaced.get(0).getShape().getMaxExtVector().toArray();
    int[] fits = fitsInto(largestCompMaxExt, parMaxExt);
    int numPoints = prod(fits);
    if (numPoints < compsToBePlaced.size()) {
      return compsToBePlaced;
    }
    double[][] points =
        generatePoints(parMaxExt, fits, parent.getPosition().toArray());
    Collections.shuffle(Arrays.asList(points), new WrappedJamesRandom(rand));

    return placeCompsAt(compsToBePlaced, points, parent, compsWellPlaced,
        compTree, transferInRules, rand);
  }

  /**
   * @param parMaxExt
   * @param numPerDim
   * @return
   */
  private static double[][] generatePoints(double[] parMaxExt, int[] numPerDim,
      double[] offset) {
    int nDim = numPerDim.length;
    int nElements = prod(numPerDim);
    double[][] rv = new double[nElements][nDim];
    int nRepPerRun = 1;
    int nRuns = nElements;
    int runLength = 1;
    for (int d = 0; d < nDim; d++) {
      double min = -parMaxExt[d];
      int numThisDim = numPerDim[d];
      double step = -2 * min / numThisDim;
      nRuns /= numThisDim;
      runLength *= numThisDim;
      double current = min + step / 2 + offset[d];
      for (int i = 0; i < numThisDim; i++) {
        for (int iRun = 0; iRun < nRuns; iRun++) {
          for (int iRep = 0; iRep < nRepPerRun; iRep++) {
            rv[iRun * runLength + i * nRepPerRun + iRep][d] = current;
          }
        }
        current += step;
      }
      nRepPerRun *= numThisDim;
    }
    return rv;
  }

  // public static void main(String[] args) {
  // generatePoints(new double[] { 2.1, 3., 2.5 }, new int[] { 3, 4, 5 }); }

  /**
   * Number of times each dimension of a vector "fits" into another (floor)
   * 
   * @param vecsmall
   * @param veclarge
   * @return
   */
  private static int[] fitsInto(double[] vecsmall, double[] veclarge) {
    int[] rv = new int[vecsmall.length];
    for (int i = 0; i < rv.length; i++) {
      rv[i] = (int) (veclarge[i] / vecsmall[i]);
    }
    return rv;
  }

  private static int prod(int[] vec) {
    int rv = 1;
    for (int v : vec) {
      rv *= v;
    }
    return rv;
  }

  /**
   * @param compsToBePlaced
   * @param points
   * @param parent
   * @param compsWellPlaced
   * @param compTree
   * @param transferInRules
   * @param rand
   * @return
   */
  private static List<SpatialEntity> placeCompsAt(List<SpatialEntity> comps,
      double[][] points, SpatialEntity container,
      Collection<SpatialEntity> previouslyPlacedComps,
      IHierarchy<SpatialEntity> compTree,
      Collection<TransferInRule> transferInRules, IRandom rand) {
    IShape contShape = container.getShape();
    // IShape contShape = container.getShape();
    Collection<SpatialEntity> placedComps =
        new ArrayList<>(previouslyPlacedComps);
    int idxPoints = 0; // while loop: while available pos try placing
                       // next-to-be-placed comp there
    int idxComps = 0;
    while (idxPoints < points.length && idxComps < comps.size()) {
      SpatialEntity c = comps.get(idxComps);
      moveCompTo(c, points[idxPoints++]);
      IShape shape = c.getShape();
      if (contShape.getRelationTo(shape) == ShapeRelation.SUPERSET) {
        List<SpatialEntity> collComps = // newly placedComps should not overlap
            GeoUtils.checkCollisionsPairwise(c, previouslyPlacedComps);
        int collRes =
            transferOrNonOverlap(c, collComps, transferInRules, compTree, rand);
        if (collRes >= 0) {
          // we got here => shape still inside container, no collisions
          placedComps.add(c);
          if (compTree.getParent(c) == null) {
            compTree.addChildParentRelation(c, container);
          }
          idxComps++; // continue with next comp (pos counter already increased)
        }
      }
    }
    return comps.subList(idxComps, comps.size()); // empty if all comps placed
  }

  private static void moveCompTo(IModifiableShapedComponent comp, double[] pos) {
    for (int i = 0; i < pos.length; i++) {
      comp.moveAlongDimTo(i + 1, pos[i]);
    }
  }

  /**
   * Check whether two given entities do not overlap or whether the first can be
   * transferred into the second
   * 
   * @param comp
   *          First spatial entity
   * @param other
   *          Other spatial entity
   * @param transferInRules
   *          Potentially applicable transfer rules
   * @return number of transfers for collision resolution if successful (thus 0
   *         for non-overlap), negation of number of failed transfer if
   *         unsuccessful)
   */
  private static int transferOrNonOverlap(SpatialEntity comp,
      List<? extends SpatialEntity> collComps,
      Collection<TransferInRule> transferInRules,
      IHierarchy<SpatialEntity> compTree, IRandom rand) {
    int i = 0;
    for (; i < collComps.size(); i++) {
      CollResolution res =
          resolveByTransfer(comp, collComps.get(i), transferInRules, compTree,
              rand);
      if (res == CollResolution.UNRESOLVABLE
          || res == CollResolution.REGION_OVERLAP) {
        // not allowing region overlap for now because then overlap with other
        // entities inside the region must be tested for (and resolved), too
        return -i - 1;
      }
      // if (res == CollResolution.REGION_OVERLAP && i + 1 == collComps.size())
      // {
      // return i;
      // }
      if (res == CollResolution.NO_COLLISION) {
        throw new SpatialInitException("Collision resolution called"
            + " for no collision (program bug)");
      }
    }
    return i;
  }

  private static CollResolution resolveByTransfer(SpatialEntity comp,
      SpatialEntity other, Collection<TransferInRule> transferInRules,
      IHierarchy<SpatialEntity> compTree, IRandom rand) {
    ShapeRelation shapeRel = other.getShape().getRelationTo(comp.getShape());
    if (!shapeRel.isCollision()) {
      return CollResolution.NO_COLLISION;
    }
    if (other.isHardBounded() || !comp.isHardBounded()) {
      if (shapeRel != ShapeRelation.SUPERSET) {
        return CollResolution.UNRESOLVABLE; // no collision resolution attempts
                                            // here
      }
    } else if (!other.getShape().includesPoint(comp.getPosition())) {
      return CollResolution.REGION_OVERLAP; // overlap of actual comp with
                                            // region boundary may be fine
    }
    for (TransferInRule rule : transferInRules) {
      Match<SpatialEntity> match = rule.match(comp, other);
      if (match.isSuccess() && rand.nextDouble() < match.getRate()) {
        match.apply();
        comp.setEnclosingEntity(other);
        compTree.addChildParentRelation(comp, other);
        return CollResolution.TRANSFER;
      }
    }
    return CollResolution.UNRESOLVABLE;
  }

  private static enum CollResolution {
    NO_COLLISION, REGION_OVERLAP, TRANSFER, UNRESOLVABLE
  }

  /**
   * Get ratio of volume of some components to free volume in container
   * (subtracts some given other components from volume without checking whether
   * they are completely included in container!)
   * 
   * @param comps
   *          Components whose volume to check
   * @param container
   *          Container where to fit components
   * @param otherComps
   *          Components already in container, occupying space
   * @param throwIfLarger1
   *          Flag whether to throw exception if comps do not fit
   * @return occupancy ratio ( vol(comps)/(vol(container)-vol(otherComps)) )
   */
  private static <C extends IShapedComponent> double checkOccupancyRatio(
      List<C> comps, IShapedComponent container, Collection<C> otherComps,
      boolean throwIfLarger1) {
    double volSum = 0.;
    for (C c : comps) {
      volSum += c.getShape().getSize();
    }
    double otherVolSum = 0.;
    for (C c : otherComps) {
      otherVolSum += c.getShape().getSize();
    }
    double containerVol = container.getShape().getSize();
    double occupancyRatio = volSum / (containerVol - otherVolSum);
    if (throwIfLarger1 && occupancyRatio > 1) {
      throw new SpatialInitException("Surrounding shape " + "(vol: "
          + containerVol + ") is too small for the "
          + (otherComps.size() + comps.size())
          + " shapes to be placed inside (vol: " + volSum + ").\nContainer: "
          + container + "\nTo be placed shapes: " + comps
          + "\nAlready present shapes: " + otherComps);
    }
    return occupancyRatio;
  }

  /**
   * Exception during spatial initialization (for better distinction from
   * parsing problems during model creation)
   * 
   * @author Arne Bittig
   */
  public static class SpatialInitException extends SpatialException {

    private static final long serialVersionUID = -318442951317309558L;

    public SpatialInitException(String message) {
      super(message);
    }

  }

  /**
   * Map of NSM entities and their respective amounts in each compartment
   * (wrapper class)
   * 
   * @author Arne Bittig
   */
  static class NSMEntMap implements
      Map<SpatialEntity, IUpdateableMap<NSMEntity, Integer>> {

    private static final String MOD_ERROR_MSG =
        "Modifiable only through add(All) and remove";

    private final Map<SpatialEntity, IUpdateableMap<NSMEntity, Integer>> map;

    NSMEntMap() {
      map = new LinkedHashMap<>();
    }

    void add(SpatialEntity comp, Map<NSMEntity, Integer> subMap) {
      IUpdateableMap<NSMEntity, Integer> compMap = map.get(comp);
      if (compMap == null) {
        compMap = new UpdateableAmountMap<>();
      }
      compMap.updateAll(subMap);
      map.put(comp, compMap);
    }

    void addAll(Map<SpatialEntity, ? extends Map<NSMEntity, Integer>> map2) {
      for (Map.Entry<SpatialEntity, ? extends Map<NSMEntity, Integer>> e : map2
          .entrySet()) {
        this.add(e.getKey(), e.getValue());
      }

    }

    @Override
    public int size() {
      return map.size();
    }

    @Override
    public IUpdateableMap<NSMEntity, Integer> get(Object key) {
      return map.get(key);
    }

    @Override
    public boolean isEmpty() {
      return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return map.containsValue(value);
    }

    @Override
    public IUpdateableMap<NSMEntity, Integer> put(SpatialEntity key,
        IUpdateableMap<NSMEntity, Integer> value) {
      throw new UnsupportedOperationException(MOD_ERROR_MSG);
    }

    @Override
    public void putAll(
        Map<? extends SpatialEntity, ? extends IUpdateableMap<NSMEntity, Integer>> m) {
      throw new UnsupportedOperationException(MOD_ERROR_MSG);
    }

    @Override
    public IUpdateableMap<NSMEntity, Integer> remove(Object key) {
      return map.remove(key);
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException(MOD_ERROR_MSG);
    }

    @Override
    public Set<SpatialEntity> keySet() {
      return map.keySet();
    }

    @Override
    public Collection<IUpdateableMap<NSMEntity, Integer>> values() {
      return map.values();
    }

    @Override
    public Set<Map.Entry<SpatialEntity, IUpdateableMap<NSMEntity, Integer>>> entrySet() {
      return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof NSMEntMap)) {
        return false;
      }
      return map.equals(((NSMEntMap) o).map);
    }

    @Override
    public int hashCode() {
      return map.hashCode();
    }
  }

}
