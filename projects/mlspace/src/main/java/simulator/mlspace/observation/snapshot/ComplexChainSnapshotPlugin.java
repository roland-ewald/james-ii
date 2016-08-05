/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.snapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.statistics.univariate.MinMedianMeanMax;
import org.jamesii.core.observe.csv.SnapshotCSVObserver;
import org.jamesii.core.util.logging.ApplicationLogger;

import model.mlspace.entities.binding.IEntityWithBindings;
import model.mlspace.entities.spatial.SpatialEntity;
import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.util.Orientation;

/**
 * @author Arne Bittig
 * @date 25.04.2013
 */
public class ComplexChainSnapshotPlugin implements
    SnapshotCSVObserver.SnapshotPlugin<AbstractMLSpaceProcessor<?, ?>> {

  private final List<String> sizeRecords = new ArrayList<>();

  private final List<String> branchesRecords = new ArrayList<>();

  private final List<String> endsRecords = new ArrayList<>();

  private final List<String> dispRecords = new ArrayList<>();

  /** Default constructor (no parameters needed) */
  public ComplexChainSnapshotPlugin() {
  }

  @Override
  public void updateState(AbstractMLSpaceProcessor<?, ?> proc, int idx) {
    // note: relies on collection being a copy, not a view of hierarchy data
    Collection<SpatialEntity> spatialEntities =
        proc.getSpatialEntities().getAllNodes();
    Collection<ComplexInfo<?>> complexes = new LinkedList<>();
    while (!spatialEntities.isEmpty()) {
      ComplexInfo<?> complexInfo = removeNextComplex(spatialEntities);
      if (complexInfo != null) {
        complexes.add(complexInfo);
      }
    }
    recordComplexes(complexes);
  }

  private void recordComplexes(Collection<ComplexInfo<?>> complexes) {
    int numComplexes = complexes.size();
    int[] sizes = new int[numComplexes];
    int[] branches = new int[numComplexes];
    int[] ends = new int[numComplexes];
    Collection<double[]> segmentLengthsAndAngles = new ArrayList<>();
    int idx = 0;
    for (ComplexInfo<?> ci : complexes) {
      sizes[idx] = ci.getEntities().size();
      branches[idx] = ci.getBranches().size();
      ends[idx] = ci.getEnds().size();
      segmentLengthsAndAngles.addAll(ci.getLengthsAndAngles());
      idx++;
    }
    if (!complexes.isEmpty()) {
      sizeRecords.add(
          MinMedianMeanMax.minMedianMeanMaxStdAmountMutating(sizes).toString());
      branchesRecords.add(MinMedianMeanMax
          .nonZeroMinMedianMeanMaxStdAmount(branches).toString());
      endsRecords.add(
          MinMedianMeanMax.nonZeroMinMedianMeanMaxStdAmount(ends).toString());
      double orientationMean =
          Orientation.meanOrientation(segmentLengthsAndAngles);
      dispRecords.add(Arrays
          .asList(orientationMean, Orientation
              .angularDeviation(segmentLengthsAndAngles, orientationMean))
          .toString());
    } else {
      sizeRecords.add(null);
      branchesRecords.add(null);
      endsRecords.add(null);
      dispRecords.add(null);
    }
  }

  /**
   * Remove entities in the first encountered complex from given collection of
   * spatial entities, and all non-bound entities encountered along the way
   * (i.e. when searching for the first bound entity)
   * 
   * @param spatialEntities
   *          Non-empty, modifiable collection of spatial entities
   * @return Entities linked via a binding chain / in a complex (will be removed
   *         from given collection); null if none remaining
   */
  private static <E extends IEntityWithBindings<?>> ComplexInfo<E> removeNextComplex(
      Collection<SpatialEntity> spatialEntities) {
    E foundEnt = filterForAnchor(spatialEntities);
    if (foundEnt == null) {
      return null;
    }
    ComplexInfo<E> info = new ComplexInfo<>(foundEnt);
    spatialEntities.removeAll(info.getEntities());
    return info;
  }

  @SuppressWarnings("unchecked")
  private static <E extends IEntityWithBindings<?>> E filterForAnchor(
      Collection<SpatialEntity> spatialEntities) {
    Iterator<SpatialEntity> it = spatialEntities.iterator();
    E foundEnt = null;
    while (it.hasNext() && foundEnt == null) {
      SpatialEntity spEnt = it.next();
      it.remove();
      if (spEnt instanceof IEntityWithBindings<?>
          && ((E) spEnt).hasBoundEntities()) {
        foundEnt = (E) spEnt;
      }
    }
    return foundEnt;
  }

  private static final String SIZE_REC_DESCR =
      "Complex sizes (min/median/mean/max/std/amount)";

  private static final String BRANCES_REC_DESCR =
      "Complex branches # (min/median/mean/max/std/amount)";

  private static final String ENDS_REC_DESCR =
      "Complex branch ends (min/median/mean/max/std/amount)";

  private static final String ANG_DEV_REC_DESCR =
      "Orientation (mean/angular-deviation)";

  @Override
  public Map<String, List<?>> getObservationData() {
    Map<String, List<?>> rv = new LinkedHashMap<>(4);
    rv.put(SIZE_REC_DESCR, sizeRecords);
    rv.put(BRANCES_REC_DESCR, branchesRecords);
    rv.put(ENDS_REC_DESCR, endsRecords);
    rv.put(ANG_DEV_REC_DESCR, dispRecords);
    return rv;
  }

  /**
   * Container of all to-be-recorded information about a complex
   * 
   * TODO: type argument is probably not necessary (Compartments only...)
   * 
   * @author Arne Bittig
   */
  private static class ComplexInfo<E extends IEntityWithBindings<?>> {
    private final Collection<E> entities;

    private final Collection<E> ends;

    private final Collection<E> branches;

    private final Collection<double[]> lengthsAndAngles;

    /**
     * Contructor that actually determines all relevant information (i.e.
     * contains or triggers all calculations)
     * 
     * @param ent
     *          One entity in the to-be-analyzed complex
     */
    ComplexInfo(E ent) {
      entities = new LinkedHashSet<>();
      ends = new LinkedHashSet<>();
      branches = new LinkedHashSet<>();
      lengthsAndAngles = new ArrayList<>();
      Set<? extends Map.Entry<String, ? extends IEntityWithBindings<?>>> bindingEntries =
          ent.bindingEntries().entrySet();
      Map<E, Queue<E>> segmentsToFollow = new LinkedHashMap<>();
      entities.add(ent);

      if (bindingEntries.size() == 2) { // get first segment by searching from a
                                        // middle protein in both directions
        Iterator<? extends Map.Entry<String, ? extends IEntityWithBindings<?>>> bIt =
            bindingEntries.iterator();
        E left =
            followSegment(ent, (E) bIt.next().getValue(), segmentsToFollow);
        E right =
            followSegment(ent, (E) bIt.next().getValue(), segmentsToFollow);
        if (left != null && right != null) {
          lengthsAndAngles.add(getDistanceAndAngle((IShapedComponent) left,
              (IShapedComponent) right));
        } else {
          ApplicationLogger.log(Level.SEVERE,
              "Segment following error (detailed message should be above)");
        }
      } else {
        Queue<E> toFollow = new LinkedList<>();
        for (Map.Entry<String, ? extends IEntityWithBindings<?>> e : bindingEntries) {
          toFollow.add((E) e.getValue());
        }
        if (bindingEntries.size() > 2) {
          branches.add(ent);
        } else {
          assert bindingEntries.size() == 1;
          ends.add(ent);
        }
        segmentsToFollow.put(ent, toFollow);
      }

      while (!segmentsToFollow.isEmpty()) {
        Iterator<Map.Entry<E, Queue<E>>> stfIt =
            segmentsToFollow.entrySet().iterator();
        Map.Entry<E, Queue<E>> currentEntry = stfIt.next();
        E nextToFollow = currentEntry.getValue().remove();
        if (currentEntry.getValue().isEmpty()) {
          stfIt.remove();
        }
        E otherEnd = followSegment(currentEntry.getKey(), nextToFollow,
            segmentsToFollow);
        lengthsAndAngles
            .add(getDistanceAndAngle((IShapedComponent) currentEntry.getKey(),
                (IShapedComponent) otherEnd));
      }
    }

    private static double[] getDistanceAndAngle(IShapedComponent left,
        IShapedComponent right) {
      IDisplacementVector disp =
          left.getPosition().displacementTo(right.getPosition());
      double angle = Math.atan2(disp.get(2), disp.get(1));
      if (disp.getDimensions() >= 3 && disp.get(3) != 0.) {
        ApplicationLogger.log(Level.SEVERE,
            "Angular deviation only calculated from x and y; wrong "
                + "result due to non-0 z between " + left + " and " + right);
      }
      return new double[] { disp.length(), angle };
    }

    /**
     * Collect entities in (presumably) straight segment until branch or end
     * reached
     * 
     * @param start
     *          Starting entity
     * @param following
     *          Entity in direction to follow
     * @param coll
     *          Collection to add followed entities to (incl. following, not
     *          incl. start, i.e. start is assumed to be already contained in
     *          coll)
     * @return next branching or end point (i.e. those with not exactly two
     *         binding partners; may be following itself)
     */
    private E followSegment(E start, E following,
        Map<E, Queue<E>> segmentsStillToFollow) {
      if (entities.contains(following)) {
        ApplicationLogger.log(Level.SEVERE,
            "Following already followed branch. Suspicious.");
      }
      E prevFoll = start;
      while (following.bindingEntries().size() == 2) {
        boolean added = entities.add(following);
        if (!added) {
          ApplicationLogger.log(Level.SEVERE,
              "Apparently circular bindings invloving " + following + " and "
                  + start);
          return null;
        }
        E tmp = following;
        Iterator<? extends Map.Entry<String, ? extends IEntityWithBindings<?>>> bIt =
            following.bindingEntries().entrySet().iterator();
        following = (E) bIt.next().getValue();
        if (following == prevFoll) {
          following = (E) bIt.next().getValue();
        }
        prevFoll = tmp;
      }
      boolean alreadyInBranch = false;
      boolean alreadyInEnds = false;
      if (following.bindingEntries().size() > 2) {
        Queue<E> toFollow = new LinkedList<>();
        for (Map.Entry<String, ? extends IEntityWithBindings<?>> e : following
            .bindingEntries().entrySet()) {
          if (e.getValue() != prevFoll) {
            toFollow.add((E) e.getValue());
          }
          segmentsStillToFollow.put(following, toFollow);
        }
        alreadyInBranch = !branches.add(following);
      } else {
        assert following.bindingEntries().size() == 1;
        alreadyInEnds = !ends.add(following);
      }
      boolean alreadyInEnts = !entities.add(following);
      if (alreadyInEnts || alreadyInBranch || alreadyInEnds) {
        ApplicationLogger.log(Level.SEVERE,
            "Apparently circular bindings invloving " + following + " and "
                + start);
      }
      return following;
    }

    /**
     * @return Spatial entities that form this comples
     */
    public Collection<E> getEntities() {
      return entities;
    }

    /**
     * @return Entities in complex bound to exactly one other entity
     */
    public Collection<E> getEnds() {
      return ends;
    }

    /**
     * @return the branches
     */
    public Collection<E> getBranches() {
      return branches;
    }

    public Collection<double[]> getLengthsAndAngles() {
      return lengthsAndAngles;
    }
  }

}
