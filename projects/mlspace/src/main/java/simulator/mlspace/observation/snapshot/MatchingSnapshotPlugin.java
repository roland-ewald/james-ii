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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.jamesii.core.math.statistics.univariate.MinMedianMeanMax;
import org.jamesii.core.observe.csv.SnapshotCSVObserver;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.logging.ApplicationLogger;

import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.subvols.Subvol;
import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.util.ExtendableList;

/**
 * ML-Space (snapshot) observer performing entity matching
 * 
 * @author Arne Bittig
 */
public class MatchingSnapshotPlugin implements
    SnapshotCSVObserver.SnapshotPlugin<AbstractMLSpaceProcessor<?, ?>> {

  private static final String MARKER_FOR_MULTIPLE_MATCHES = "*";

  /** Identifier of parameter setting for entities to observe */
  public static final String TO_OBSERVE = "EntitiesOfInterest";

  private final Map<List<? extends RuleEntity>, List<String>> toObserve;

  private final Map<List<? extends RuleEntity>, Map<String, ALS>> observations =
      new LinkedHashMap<>();

  private final Map<List<? extends RuleEntity>, ExtendableList<Number>> trajectories =
      new LinkedHashMap<>();

  /**
   * (To be created by instrumenter, which is part of a different package, hence
   * the need for a public constructor)
   * 
   * @param toObserve
   */
  public MatchingSnapshotPlugin(
      Map<List<? extends RuleEntity>, List<String>> toObserve) {
    this.toObserve = toObserve;
    initMaps();
  }

  private void initMaps() {
    Iterator<Entry<List<? extends RuleEntity>, List<String>>> tOIt =
        toObserve.entrySet().iterator();
    while (tOIt.hasNext()) {
      Entry<List<? extends RuleEntity>, List<String>> next = tOIt.next();
      List<? extends RuleEntity> toMatch = next.getKey();
      Set<String> obsTargets = new LinkedHashSet<>(next.getValue());
      if (obsTargets.size() == 0) {
        ApplicationLogger.log(Level.SEVERE,
            "No observation targets for " + toMatch + ". Removing entry.");
        tOIt.remove();
        continue;
      }
      if (obsTargets.size() < next.getValue().size()) {
        ApplicationLogger.log(Level.SEVERE,
            "Observation target defined several times for " + toMatch + ".");
      }
      int minSizeForOtherMatch = 0;
      if (obsTargets.contains(ObsTarget.AMOUNTS)) {
        trajectories.put(toMatch, new ExtendableList<Number>());
        minSizeForOtherMatch = 1;
      }
      if (obsTargets.size() > minSizeForOtherMatch) {
        observations.put(toMatch, new LinkedHashMap<String, ALS>());
      }
    }
  }

  @Override
  public void updateState(AbstractMLSpaceProcessor<?, ?> proc,
      int idxSnapshot) {
    updateComps(proc.getSpatialEntities(), idxSnapshot);
    updateSubvols(proc.getSubvols(), idxSnapshot);
  }

  private void updateComps(IHierarchy<SpatialEntity> compTree, int idx) {
    for (Map.Entry<List<? extends RuleEntity>, List<String>> toe : toObserve
        .entrySet()) {
      List<SpatialEntity> matchEnts =
          findMatchingSpatialEntities(toe.getKey(), compTree);
      // following shortcut is only useful if one observes a single run, or one
      // can be sure that matchEnts will be non-empty eventually (otherwise
      // multi-run results will have different column number and/or order)
      // if (matchEnts.isEmpty()) {
      // if (toe.getValue().contains(ObsTarget.AMOUNTS)) {
      // recordAmountObservation(matchEnts, trajectories.get(toe.getKey()),
      // idx);
      // } // records 0s instead of nulls
      // continue;
      // }

      for (String obsTarget : toe.getValue()) {
        if (obsTarget.equals(ObsTarget.AMOUNTS)) {
          recordAmountObservation(matchEnts, trajectories.get(toe.getKey()),
              idx);
          continue;
        }
        ALS listToUpdate =
            getOrPutNew(observations.get(toe.getKey()), obsTarget, ALS.class);
        if (obsTarget.equals(ObsTarget.COORDINATES)) {
          recordCoordObservation(matchEnts, listToUpdate, idx);
        } else {
          recordAttributeObservation(obsTarget, matchEnts, listToUpdate, idx);
        }
      }
    }
  }

  /**
   * Extracted method for reduction of cyclomatic complexity
   * 
   * @param toMatch
   *          "stack" of RuleEntities to match (i.e. spatial entities inside
   *          given spatial entities inside...)
   * @param compTree
   *          Spatial entity hierarchy
   * @return entities in comp tree matching toe
   */
  private static List<SpatialEntity> findMatchingSpatialEntities(
      List<? extends RuleEntity> toMatch, IHierarchy<SpatialEntity> compTree) {
    List<SpatialEntity> matchEnts = new ArrayList<>();
    for (SpatialEntity comp : compTree.getAllNodes()) {
      if (matchCompAndEnclosingsHierarchically(toMatch, comp)) {
        matchEnts.add(comp);
      }
    }
    return matchEnts;
  }

  private static void recordAmountObservation(List<SpatialEntity> matchEnts,
      ExtendableList<Number> traj, int idx) {
    traj.set(idx, matchEnts.size());
  }

  private static void recordAttributeObservation(String obsTarget,
      List<SpatialEntity> matchEnts, ALS listToUpdate, int idx) {
    IUpdateableMap<Object, Integer> attNumOcc = new UpdateableAmountMap<>();
    boolean allKeysNumeric = true;
    for (SpatialEntity me : matchEnts) {
      Object attVal = me.getAttribute(obsTarget);
      if (attVal != null) {
        attNumOcc.update(attVal, 1);
      }
      if (!(attVal instanceof Number)) {
        allKeysNumeric = false;
      }
    }
    if (attNumOcc.size() == 1 && attNumOcc.values().contains(1)) {
      listToUpdate.set(idx, attNumOcc.keySet().iterator().next().toString());
    } else {
      if (!attNumOcc.isEmpty() && allKeysNumeric) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        String miMeMaxStr =
            MinMedianMeanMax.minMedianMeanMaxStdAmount((Map) attNumOcc) + " ";
        listToUpdate.set(idx, miMeMaxStr + new TreeMap<>(attNumOcc));
        listToUpdate.setHasMultiValueEntries();
      } else {
        listToUpdate.set(idx, attNumOcc.toString());
      }
    }
  }

  private static void recordCoordObservation(List<SpatialEntity> matchEnts,
      ALS listToUpdate, int idx) {
    String str = matchEnts.get(0).getPosition().toString();
    if (matchEnts.size() > 1) {
      str += MARKER_FOR_MULTIPLE_MATCHES;
    }
    listToUpdate.set(idx, str);
  }

  private static boolean matchCompAndEnclosingsHierarchically(
      List<? extends RuleEntity> toMatch, SpatialEntity comp) {
    SpatialEntity currComp = comp;
    for (RuleEntity ruleEnt : toMatch) {
      // match along the hierarchy
      if (ruleEnt.matches(currComp, Collections.<String, Object> emptyMap())) {
        currComp = currComp.getEnclosingEntity();
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * @param subvols
   * @param idx
   */
  private void updateSubvols(Collection<Subvol> subvols, int idx) {
    for (Map.Entry<List<? extends RuleEntity>, List<String>> toe : toObserve
        .entrySet()) {
      IUpdateableMap<NSMEntity, Integer> matchingEnts =
          new UpdateableAmountMap<>();
      List<? extends RuleEntity> entsToMatch = toe.getKey();
      Subvol someSv =
          matchEntryAndAccumulateSvEnts(entsToMatch, subvols, matchingEnts);
      if (matchingEnts.isEmpty()) {
        continue;
      }
      for (String obsTarget : toe.getValue()) {
        if (obsTarget.equals(ObsTarget.AMOUNTS)) {
          trajectories.get(entsToMatch).set(idx, sum(matchingEnts.values()));
          continue;
        }
        ALS listToUpdate =
            getOrPutNew(observations.get(toe.getKey()), obsTarget, ALS.class);
        if (obsTarget.equals(ObsTarget.COORDINATES)) {
          recordCoordObservation(someSv, matchingEnts, listToUpdate, idx);
        } else { // attribute
          recordAttributeObservation(obsTarget, matchingEnts, listToUpdate,
              idx);
        }
      }
    }
  }

  private static Subvol matchEntryAndAccumulateSvEnts(
      List<? extends RuleEntity> entsToMatch, Collection<Subvol> subvols,
      IUpdateableMap<NSMEntity, Integer> matchingEnts) {
    RuleEntity toMatch = entsToMatch.isEmpty() ? null : entsToMatch.get(0);
    Subvol someSv = null; // some sv with matching entity (for
    // coordinate recording)
    for (Subvol sv : subvols) {
      if (sv.getState().isEmpty()) {
        continue;
      }
      if (!entsToMatch.isEmpty() // for matching everything
          && !matchCompAndEnclosingsHierarchically(
              entsToMatch.subList(1, entsToMatch.size()),
              sv.getEnclosingEntity())) {
        continue; // sv parents do not match toObserve entry
      }

      if (matchSingleEntityAndAccumulate(toMatch, sv, matchingEnts)) {
        someSv = sv;
      }
    }
    return someSv;
  }

  private static boolean matchSingleEntityAndAccumulate(RuleEntity toMatch,
      Subvol sv, IUpdateableMap<NSMEntity, Integer> matching) {
    boolean changed = false;
    for (Map.Entry<NSMEntity, Integer> svse : sv.getState().entrySet()) {
      if (toMatch == null
          || toMatch.matches(svse.getKey(), Collections.EMPTY_MAP)) {
        matching.update(svse.getKey(), svse.getValue());
        changed = true;
      }
    }
    return changed;
  }

  /**
   * @param someSv
   * @param matchingEnts
   * @param listToUpdate
   * @param idx
   */
  private static void recordCoordObservation(Subvol someSv,
      IUpdateableMap<NSMEntity, Integer> matchingEnts, ALS listToUpdate,
      int idx) {
    String str = someSv.toString();
    if (matchingEnts.size() > 1) {
      str += MARKER_FOR_MULTIPLE_MATCHES;
    }
    listToUpdate.set(idx, str);
  }

  private static void recordAttributeObservation(String obsTarget,
      Map<NSMEntity, Integer> matchEnts, ALS listToUpdate, int idx) {
    IUpdateableMap<String, Integer> attNumOcc = new UpdateableAmountMap<>();
    for (Map.Entry<NSMEntity, Integer> me : matchEnts.entrySet()) {
      Object attVal = me.getKey().getAttribute(obsTarget);
      if (attVal != null) {
        attNumOcc.update(attVal.toString(), me.getValue());
      }
    }
    listToUpdate.set(idx, attNumOcc.toString());
  }

  private static Integer sum(Collection<Integer> vals) {
    int sum = 0;
    for (Integer val : vals) {
      if (val == null) {
        return null;
      }
      sum += val;
    }
    return sum;
  }

  /**
   * Get value from map, or associate key with new value of the given type and
   * return this one (won't work if value class has generic type parameter(s)).
   * 
   * @param map
   *          Map to operate on
   * @param key
   *          Key whose value to get or create
   * @param newVClass
   *          Class for new value (works only for classes with argument-less
   *          constructors; null is returned in case of failure)
   * @return Key's corresponding value in the map (the new one if none available
   *         previously)
   */
  private static <K, V> V getOrPutNew(Map<K, V> map, K key,
      Class<? extends V> newVClass) {
    V val = map.get(key);
    if (val != null) {
      return val;
    }
    try {
      val = newVClass.getConstructor().newInstance();
      map.put(key, val);
      return val;
    } catch (Exception ex) { // NOSONAR: same effect for all ex
      return null;
    }
  }

  @Override
  public Map<String, List<?>> getObservationData() {
    Map<String, List<?>> rv = new LinkedHashMap<>();

    for (List<? extends RuleEntity> re : toObserve.keySet()) {
      Map<String, ALS> mapToTransform = observations.get(re);
      if (mapToTransform != null) {
        for (Map.Entry<String, ALS> attDatE : mapToTransform.entrySet()) {
          String heading = ObsTarget.asString(re, attDatE.getKey());
          ALS col = attDatE.getValue();
          if (col == null) {
            ApplicationLogger.log(Level.WARNING,
                "Null observations for " + heading);
          } else {
            if (col.hasMultiValueEntries()) {
              heading += " (min/median/mean/max/std/amount) {distribution}";
            }
            rv.put(heading, col);
          }
        }
      }
      ExtendableList<Number> traj = trajectories.get(re);
      if (traj != null) {
        String heading = ObsTarget.asString(re, ObsTarget.AMOUNTS);
        // "\"" + re + "\"";
        rv.put(heading, traj);
      }
    }
    return rv;
  }

  /**
   * @return the trajectories
   */
  public Map<List<? extends RuleEntity>, ExtendableList<Number>> getTrajectories() {
    return trajectories;
  }

  /**
   * Helper for describing what to observe, consisting first of the entity
   * hierarchy (type of entity to observe, entity in which it must be, entity in
   * which this in turn must be,...) and the attributes to observe ("#" for
   * aggregating the amounts).
   * 
   * @author Arne Bittig
   */
  public static class ObsTarget
      extends LinkedHashMap<List<? extends RuleEntity>, List<String>> {

    private static final long serialVersionUID = -5774785278287211588L;

    /**
     * Marker for "observe total amount of matching entities"; do not change
     * without changing lexer token/parsing symbol, too
     */
    public static final String AMOUNTS = "#";

    /** Marker for "observe coordinates of one matching entity */
    public static final String COORDINATES = "_coord_";

    /**
     * Default constructor for initially empty observation target definition
     */
    public ObsTarget() {
    }

    /**
     * Add entity to observe
     * 
     * @param ent
     *          Entity matching pattern
     * @param insideEnt
     *          Entity matching pattern for enclosing entity of ent
     * @param what
     *          Indentifier for what to oberve (e.g. attribute name; # for
     *          total)
     * @return Map to associate with parameter setting
     *         {@link MatchingSnapshotPlugin#TO_OBSERVE} or to add more targets
     *         to
     */
    public ObsTarget add(RuleEntity ent, RuleEntity insideEnt, String... what) {
      List<RuleEntity> toMatch = new LinkedList<>();
      toMatch.add(ent);
      if (insideEnt != null) {
        toMatch.add(insideEnt);
      }
      this.put(toMatch, new LinkedList<>(Arrays.asList(what)));
      return this;
    }

    /**
     * Add entity to observe
     * 
     * @param ent
     *          Entity matching pattern
     * @param insideEnts
     *          Entity matching pattern for enclosing entity of ent
     * @param what
     *          Indentifier for what to oberve (e.g. attribute name; # for
     *          total)
     * @return Map to associate with parameter setting
     *         {@link MatchingSnapshotPlugin#TO_OBSERVE} or to add more targets
     *         to
     */
    public ObsTarget add(RuleEntity ent, List<RuleEntity> insideEnts,
        String... what) {
      List<RuleEntity> toMatch = new LinkedList<>();
      toMatch.add(ent);
      toMatch.addAll(insideEnts);
      this.put(toMatch, new LinkedList<>(Arrays.asList(what)));
      return this;
    }

    /**
     * Add entity to observe
     * 
     * @param ent
     *          Entity matching pattern
     * @param what
     *          Indentifier for what to oberve (e.g. attribute name; # for
     *          total)
     * @return Map to associate with parameter setting
     *         {@link MatchingSnapshotPlugin#TO_OBSERVE} or to add more targets
     *         to
     */
    public ObsTarget add(RuleEntity ent, String... what) {
      List<RuleEntity> toMatch = new LinkedList<>();
      toMatch.add(ent);
      this.put(toMatch, new LinkedList<>(Arrays.asList(what)));
      return this;
    }

    /**
     * @return All defined observation targets
     */
    public Set<List<? extends RuleEntity>> getTargets() {
      return keySet();
    }

    /**
     * 
     * @param res
     *          Rule entities
     * @param what
     * @return String representation of nested rule entities
     */
    public static String asString(List<? extends RuleEntity> res, String what) {
      final String inside = " in ";
      StringBuilder str = new StringBuilder();
      for (RuleEntity re : res) {
        str.append(re.toString());
        str.append(inside);
      }
      if (str.length() > 0) {
        str.delete(str.length() - inside.length(), str.length());
      }
      int ipar = str.indexOf("()");
      while (ipar > -1) {
        str.delete(ipar, ipar + 2);
        ipar = str.indexOf("()");
      }

      if (what != null && !what.isEmpty()) {
        str.append(' ');
        str.append(what);
      }
      return str.toString();
    }
  }

  /**
   * ArrayList<String> helper class (which, unlike ArrayList<String> itself, can
   * easily be used in arrays)
   * 
   * @author Arne Bittig
   */
  public static final class ALS extends ExtendableList<String> {

    private static final long serialVersionUID = 630658226040090915L;

    private boolean hasMultiValueEntries;

    /**
     * Create empty, extendable list of {@link String}s
     */
    public ALS() {
      super();
    }

    /**
     * @return the hasMultiValueEntries
     */
    public final boolean hasMultiValueEntries() {
      return hasMultiValueEntries;
    }

    /**
     */
    public final void setHasMultiValueEntries() {
      this.hasMultiValueEntries = true;
    }
  }

}