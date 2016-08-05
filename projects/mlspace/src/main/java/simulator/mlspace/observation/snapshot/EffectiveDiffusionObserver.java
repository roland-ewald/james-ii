package simulator.mlspace.observation.snapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.spatial.SpatialEntity;

import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.statistics.univariate.MinMedianMeanMax;
import org.jamesii.core.observe.csv.SnapshotCSVObserver;
import org.jamesii.core.util.collection.ArrayMap;

import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.HybridEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.observation.AbstractEffectObserver;
import simulator.mlspace.util.ExtendableList;

/**
 * Observer tracking number of move attempts at each time step and mean squared
 * displacement of particles between snapshot times.
 * 
 * May not be useful (and create big output files) if there are particles of
 * many different sizes and/or many different diffusion constants in the system.
 * 
 * IGNORES DRIFT, and does not distinguish between actual moves of an entity and
 * moves along with the surrounding entity!
 * 
 * @author Arne Bittig
 * @date 28.11.2014
 */
public class EffectiveDiffusionObserver extends
    AbstractEffectObserver<IContSpaceEventRecord> implements
    SnapshotCSVObserver.SnapshotPlugin<AbstractMLSpaceProcessor<?, ?>> {

  private int numTotalFailures = 0;

  /** {@link numTotalFailures} at snapshot times */
  private final List<Integer> failures = new ArrayList<>();

  /**
   * counter for number of pos upd successful without retry / with 1 retry/ with
   * 2 retries...
   */
  private final List<Integer> numAttempts = new ExtendableList<>();

  /** {@link numAttempts} state at snapshot times */
  private final List<List<Integer>> attempts = new ArrayList<>();

  private final Deque<Map<SpatialEntity, OldPosInfo>> oldPositions;

  /** msds extracted from {@link oldPositions} at snapshot times */
  private final List<List<String>> dispInfos = new ArrayList<>();

  /**
   * parameter for number of previous snapshots to compare to; thus also length
   * of {@link oldPositions} (when at least as many snapshots were taken)
   */
  private final int numSnaphotsToKeep;

  private final Map<SpatialEntity, Double> lastUpdateTime =
      new LinkedHashMap<>();

  /**
   * @param numSnaphotsToKeep
   *          number of past snapshots to compare current position to
   */
  public EffectiveDiffusionObserver(int numSnaphotsToKeep) {
    oldPositions = new LinkedList<>();
    this.numSnaphotsToKeep = numSnaphotsToKeep;
  }

  @Override
  protected boolean init(AbstractMLSpaceProcessor<?, IContSpaceEventRecord> proc) {
    Collection<SpatialEntity> allSpatialEntities =
        proc.getSpatialEntities().getAllNodes();
    for (SpatialEntity se : allSpatialEntities) {
      lastUpdateTime.put(se, 0.);
    }
    oldPositions.addFirst(extractPositions(allSpatialEntities));
    return true;
  }

  @Override
  protected void recordEffect(Double time, IContSpaceEventRecord effect) {
    if (effect instanceof HybridEventRecord
        && ((HybridEventRecord) effect).isWrappedSubvolRecord()) {
      return;
    }
    if (!effect.isSuccess()) {
      numTotalFailures++; // update attempt counts towards last update time
      lastUpdateTime.put((SpatialEntity) effect.getTriggeringComponent(), time);
      return;
    }
    recordNumOfAttempts(effect);

    for (SpatialEntity se : effect.getCompMoves().keySet()) {
      lastUpdateTime.put(se, time);
      // CHECK: distinguish between "moved along" and "moved on its own"?
    }

    for (SpatialEntity se : effect.getCompCreations()) {
      lastUpdateTime.put(se, time);
      oldPositions.getFirst().put(se, new OldPosInfo(se, time));
    }
  }

  private void recordNumOfAttempts(IContSpaceEventRecord effect) {
    int num = effect.getNumInfo();
    if (numAttempts.size() <= num || numAttempts.get(num) == null) {
      numAttempts.set(num, 1);
    } else {
      numAttempts.set(num, numAttempts.get(num) + 1);
    }
  }

  @Override
  protected void cleanUp(AbstractMLSpaceProcessor<?, IContSpaceEventRecord> proc) {
    /* nothing to do */}

  @Override
  public void updateState(AbstractMLSpaceProcessor<?, ?> proc, int i) {
    updateNumAttempts(i);
    if (proc.getTime() == 0.) {
      return; // work done in init already
    }

    Collection<SpatialEntity> allSpatialEntities =
        proc.getSpatialEntities().getAllNodes();
    Map<SpatialEntity, OldPosInfo> currPosMap =
        extractPositions(allSpatialEntities);

    extractAllDisplacements(allSpatialEntities, i);

    oldPositions.addFirst(currPosMap);
    if (oldPositions.size() > numSnaphotsToKeep) {
      oldPositions.removeLast();
    }
  }

  private void extractAllDisplacements(
      Collection<SpatialEntity> allSpatialEntities, int i) {
    List<Map<Double, Map<Double, List<Double>>>> displacements =
        new ArrayList<>(oldPositions.size());
    int idxOld = 0;
    for (Map<SpatialEntity, OldPosInfo> oldPosMap : oldPositions) {
      Map<Double, Map<Double, List<Double>>> dispMap =
          aggregateDisplacements(allSpatialEntities, displacements, idxOld,
              oldPosMap);
      List<String> infoList;
      if (dispInfos.size() <= idxOld) {
        assert dispInfos.size() == idxOld;
        infoList = new ExtendableList<>();
        dispInfos.add(infoList);
      } else {
        infoList = dispInfos.get(idxOld);
      }
      infoList.add(i, collectDisplacementsInfos(dispMap));
      idxOld++;
    }
  }

  private Map<Double, Map<Double, List<Double>>> aggregateDisplacements(
      Collection<SpatialEntity> allSpatialEntities,
      List<Map<Double, Map<Double, List<Double>>>> displacements, int idxOld,
      Map<SpatialEntity, OldPosInfo> oldPosMap) {
    Map<Double, Map<Double, List<Double>>> dispMap;
    if (displacements.size() <= idxOld) {
      dispMap = new LinkedHashMap<>();
      displacements.add(dispMap);
    } else {
      dispMap = displacements.get(idxOld);
    }

    for (SpatialEntity se : allSpatialEntities) {
      OldPosInfo oldPosInfo = oldPosMap.get(se);
      if (oldPosInfo != null) {
        if (oldPosInfo.stillMatches(se)) {
          double squaredDisplacement =
              se.getPosition().distanceSquared(oldPosInfo.getPosition());
          if (lastUpdateTime.get(se) != oldPosInfo.getTime()) {
            getNestedList(dispMap, se.getDiffusionConstant(),
                se.getShape().getSize()).add(
                squaredDisplacement == 0. ? 0. : squaredDisplacement
                    / (lastUpdateTime.get(se) - oldPosInfo.getTime()));
            // } else {
            // System.err.println(oldPosInfo + " " + se);
          }
        } else {
          oldPosMap.remove(se);
        }
      }
    }
    return dispMap;
  }

  private static <T> List<T> getNestedList(
      Map<Double, Map<Double, List<T>>> dispMap, double diff, double seSize) {
    Map<Double, List<T>> diffMap = dispMap.get(diff);
    if (diffMap == null) {
      diffMap = new LinkedHashMap<>();
      dispMap.put(diff, diffMap);
    }
    List<T> list = diffMap.get(seSize);
    if (list == null) {
      list = new ArrayList<>();
      diffMap.put(seSize, list);
    }
    return list;
  }

  private static String collectDisplacementsInfos(
      Map<Double, Map<Double, List<Double>>> dispMap) {
    StringBuilder info = new StringBuilder();
    for (Map.Entry<Double, Map<Double, List<Double>>> eDiff : dispMap
        .entrySet()) {
      for (Map.Entry<Double, List<Double>> eSize : eDiff.getValue().entrySet()) {
        info.append("d=");
        info.append(eDiff.getKey().toString());
        info.append(" s=");
        info.append(eSize.getKey().toString());
        info.append(' ');
        info.append(MinMedianMeanMax.minMedianMeanMaxStdAmountMutating(eSize
            .getValue()));
        info.append("; ");
      }
    }
    return info.toString();
  }

  private Map<SpatialEntity, OldPosInfo> extractPositions(
      Collection<SpatialEntity> allSpatialEntities) {
    Map<SpatialEntity, OldPosInfo> oldPosMap = new LinkedHashMap<>();
    for (SpatialEntity se : allSpatialEntities) {
      oldPosMap.put(se, new OldPosInfo(se, lastUpdateTime.get(se)));
    }
    return oldPosMap;
  }

  private void updateNumAttempts(int i) {
    assert failures.size() == i;
    failures.add(numTotalFailures);
    assert attempts.size() == i;
    attempts.add(new ArrayList<>(numAttempts));
  }

  private static final String FAILURES = "#Position update failures";

  private static final String ATTEMPTS = "#Position update attempts";

  private static final String MSD_DATA =
      "squared displacement since snapshot -";

  @Override
  public Map<String, ? extends List<?>> getObservationData() {
    Map<String, List<?>> rv = new ArrayMap<>(2 + numSnaphotsToKeep);
    rv.put(ATTEMPTS, attempts);
    rv.put(FAILURES, failures);
    int idx = 0;
    for (List<String> dispInfo : dispInfos) {
      rv.put(MSD_DATA + (++idx), dispInfo);
    }
    return rv;
  }

  private static class OldPosInfo {

    private final IPositionVector position;

    private final double size;

    private final double time;

    private final double diff;

    /**
     * @param position
     * @param size
     * @param time
     */
    private OldPosInfo(IPositionVector position, double size, double diff,
        double time) {
      this.position = position;
      this.size = size;
      this.diff = diff;
      this.time = time;
    }

    @Override
    public String toString() {
      return "OldPosInfo [position=" + position + ", size=" + size + ", time="
          + time + ", diff=" + diff + "]";
    }

    public OldPosInfo(SpatialEntity se, double time) {
      this(se.getPosition().copy(), se.getShape().getSize(), se
          .getDiffusionConstant(), time);
    }

    /**
     * @param se
     * @return true iff diffusion and size have not changed
     */
    public boolean stillMatches(SpatialEntity se) {
      return se.getShape().getSize() == this.size
          && se.getDiffusionConstant() == this.diff;
    }

    /**
     * @return the position
     */
    public final IPositionVector getPosition() {
      return position;
    }

    /**
     * @return the time
     */
    public final double getTime() {
      return time;
    }

  }
}
