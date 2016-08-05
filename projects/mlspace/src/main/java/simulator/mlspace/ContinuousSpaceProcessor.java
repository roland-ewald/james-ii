/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeRelation;
import org.jamesii.core.math.geometry.spatialindex.ISpatialIndex;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.distributions.AbstractDistribution;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.observe.IInfoMapProvider;
import org.jamesii.core.util.collection.CombinedIterator;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.hierarchy.Hierarchies;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.spatial.Compartment;
import model.mlspace.entities.spatial.Compartment.CompComplex;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.entities.values.AbstractValueRange;
import model.mlspace.reader.MLSpaceModelReader;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.NonTransferRule;
import model.mlspace.rules.TransferInRule;
import model.mlspace.rules.TransferOutRule;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.SuccessfulMatch;
import model.mlspace.util.ShapeHierarchyUtils;
import simulator.mlspace.brownianmotion.IPositionUpdater;
import simulator.mlspace.collision.BindingCollisionResolver;
import simulator.mlspace.collision.ICollisionResolver;
import simulator.mlspace.event.ISpatialEntityEvent;
import simulator.mlspace.event.MoveEvent;
import simulator.mlspace.eventrecord.ContSpaceEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.util.AttModUtils;
import simulator.mlspace.util.MLSpaceLogger.DebugLevel;

/**
 * ML-Space simulator for models containing only spatial entities
 * 
 * @author Arne Bittig
 */
public class ContinuousSpaceProcessor extends AbstractContinuousSpaceProcessor {

  private static final int REC_DEPTH_WARNING_THESHOLD = 10;

  /** Serialization ID */
  private static final long serialVersionUID = -981050541197231904L;

  /** Vector factory for coordinate creation / instantiation */
  private final IVectorFactory vecFac;

  /** Position update handler (to get null vectors of appropriate type) */
  private final IPositionUpdater posUpdater;

  /** Spatial index to keep track of comps and their collisions */
  private final ISpatialIndex<SpatialEntity> spatialIndex;

  /** Default value for simulator parameter maxMoveAttempts */
  public static final int DEFAULT_MAX_MOVE_ATTEMPTS = 4;

  /* Simulator parameters */
  /**
   * Number of attempts to place a moving compartment before concluding
   * "no space for move"
   */
  private final int maxMoveAttempts;

  /* Simulator member variables */

  /** Actually simulated entities */
  private final IHierarchy<SpatialEntity> compTree;

  /** Rules for transfer of entities between compartments */
  private final Collection<TransferInRule> transferInRules;

  /** Rules for transfer of entities between compartments */
  private final Collection<TransferOutRule> transferOutRules;

  /** Rules for second-order, i.e. collision-triggered reactions */
  private final Collection<CollisionReactionRule> secondOrderReactionRules;

  private final ICollisionResolver<SpatialEntity> collRes;

  private int recDepth;

  /**
   * Continuous Space Simulator (full constructor)
   * 
   * @param model
   *          ML-Space model
   * @param eventQueue
   *          EventQueue to use
   * @param rand
   *          Random number generator
   * @param spatialIndex
   *          Spatial index method to use
   * @param posUpdater
   *          Position update method
   * @param maxMoveAttempts
   *          Number of compartment move attempts before concluding
   *          "no space for move" (if null or <= 0, default value
   *          {@value #DEFAULT_MAX_MOVE_ATTEMPTS} is used)
   */
  public ContinuousSpaceProcessor(IMLSpaceModel model,
      IEventQueue<ISpatialEntityEvent, Double> eventQueue, IRandom rand,
      ISpatialIndex<SpatialEntity> spatialIndex, IPositionUpdater posUpdater,
      Integer maxMoveAttempts) {
    this(model, eventQueue, rand, spatialIndex, posUpdater, maxMoveAttempts,
        new Timer());
  }

  protected ContinuousSpaceProcessor(IMLSpaceModel model,
      IEventQueue<ISpatialEntityEvent, Double> eventQueue, IRandom rand,
      ISpatialIndex<SpatialEntity> spatialIndex, IPositionUpdater posUpdater,
      Integer maxMoveAttempts, Timer timer) {
    super(model, eventQueue, rand, posUpdater, timer);

    this.transferInRules = model.getTransferInRules();
    this.transferOutRules = model.getTransferOutRules();
    this.secondOrderReactionRules = initReactionRules(model);
    compTree = model.getCompartments();
    Collection<SpatialEntity> ctRoots = compTree.getRoots();
    IShape topLevelShape;
    if (ctRoots.size() == 1) {
      topLevelShape = ctRoots.iterator().next().getShape();
    } else {
      topLevelShape = GeoUtils.surroundingBox(ctRoots);
      ApplicationLogger.log(Level.INFO, "More than one root "
          + "compartment. Using root shape " + topLevelShape);
    }

    this.posUpdater = posUpdater;
    this.spatialIndex = spatialIndex;
    this.spatialIndex.init(topLevelShape);

    this.vecFac = model.getVectorFactory();

    if (maxMoveAttempts != null && maxMoveAttempts > 0) {
      this.maxMoveAttempts = maxMoveAttempts;
    } else {
      this.maxMoveAttempts = DEFAULT_MAX_MOVE_ATTEMPTS;
    }

    initSpatialEntities(ctRoots);
    collRes = new BindingCollisionResolver(spatialIndex, posUpdater, rand,
        // this.maxMoveAttempts,
        0.0, this.vecFac);
    IDistribution fixedInitialBindingAngle = getFixedInitialBindingAngle(model);
    ((BindingCollisionResolver) collRes)
        .setDefaultFirstAngle(fixedInitialBindingAngle);
  }

  private IDistribution getFixedInitialBindingAngle(IMLSpaceModel model) {
    try {
      Object unusedConstants = ((IInfoMapProvider<?>) model).getInfoMap()
          .get(MLSpaceModelReader.UNUSED_CONSTANTS);
      AbstractValueRange<?> angle =
          (AbstractValueRange<?>) ((Map<String, ?>) unusedConstants)
              .get("initialAbsoluteAngle");
      if (angle == null) {
        return null;
      }
      final double value = (Double) angle.getRandomValue(null);
      AbstractValueRange<?> deviation =
          (AbstractValueRange<?>) ((Map<String, ?>) unusedConstants)
              .get("initialAbsoluteAngleDeviation");
      if (deviation == null) {
        return new AbstractDistribution(getRand()) {

          private static final long serialVersionUID = 1L;

          @Override
          public double getRandomNumber() {
            return value;
          }

          @Override
          public AbstractDistribution getSimilar(IRandom newRandomizer) {
            throw new UnsupportedOperationException();
          }
        };
      } else {
        NormalDistribution dist = new NormalDistribution(getRand());
        dist.setMean(value);
        dist.setDeviation((Double) deviation.getRandomValue(null));
        return dist;
      }
    } catch (ClassCastException | NullPointerException ex) {
      ApplicationLogger.log(Level.SEVERE,
          "Something went wrong when looking for certain special information in model "
              + model + ":\n" + ex);
      return null;
    }
  }

  /**
   * Init compartment events (position update and first order reactions) -
   * Starts with a compartment from the given collection, then recursively goes
   * through the children. Should also be suitable for newly created
   * compartments (i.e. single-element collections as arguments and init at time
   * index >0)
   * 
   * @param startingPoints
   *          Compartments with which to start, usually the top level
   */
  private void initSpatialEntities(Collection<SpatialEntity> startingPoints) {
    if (startingPoints == null) {
      initSpatialEntities(compTree.getChildren(null));
      return;
    }

    for (SpatialEntity ent : startingPoints) {
      spatialIndex.registerNewEntity(ent);

      getLogger().checkAndLog(
          compTree.getParent(ent) == null && ent.getDiffusionConstant() > 0,
          Level.INFO,
          "Moving top-level compartments are "
              + "really not recommended. Unbounded movement "
              + "will result and is probably not intended.");
      initMoveEvent(ent);
      initTimedReactEvent(ent);
      Collection<SpatialEntity> children = compTree.getChildren(ent);
      if (!children.isEmpty()) {
        initSpatialEntities(children);
      }
    }
  }

  /**
   * Register new entities with the compTree and spatialIndex
   * 
   * @param prodEnts
   *          the newly produced entities
   * @param parent
   *          SpatialEntity in which the new entities are
   */

  private void registerProducedEntities(List<SpatialEntity> prodEnts,
      SpatialEntity parent) {
    for (SpatialEntity prodEnt : prodEnts) {
      if (spatialIndex.getRegisteredPosition(prodEnt) != null) {
        spatialIndex.updateCompPos(prodEnt);
        // CHECK: does that ever happen?
      } else {
        spatialIndex.registerNewEntity(prodEnt);
      }
      compTree.addChildParentRelation(prodEnt, parent);
    }
  }

  @Override
  protected List<SpatialEntity> produceEntities(SpatialEntity parent,
      SpatialEntity nearComp, List<InitEntity> toProduce,
      Map<String, Object> variables) {

    if (toProduce.isEmpty()) {
      return Collections.emptyList();
    }

    if (nearComp != null && nearComp.getEnclosingEntity() != parent) {
      throw new IllegalArgumentException();
    }

    int psize = toProduce.size();
    List<SpatialEntity> rv = new ArrayList<>(psize);
    List<InitEntity> nonSpatial = new ArrayList<>(psize);
    for (InitEntity pEnt : toProduce) {
      if (!getModEntFac().isSpatial(pEnt)) {
        nonSpatial.add(pEnt);
        continue;
      }
      List<SpatialEntity> prodEnts =
          createSpatialEntities(pEnt, parent, nearComp, variables);
      if (prodEnts == null) {
        getLogger().checkAndLog(DebugLevel.COMP_CREATION_AND_DESTRUCTION,
            Level.WARNING, "Comp creation failed for rule entity " + pEnt
                + " in " + parent + " near " + nearComp);
        return null; // created components could not be placed
      }
      rv.addAll(prodEnts);
    }
    if (!nonSpatial.isEmpty()) {
      recordHybridEntityProduction(nonSpatial, parent, nearComp, variables);
    }
    registerProducedEntities(rv, parent);
    return rv;
  }

  /**
   * Create a single spatial entity
   * 
   * @param ruleEnt
   *          Rule entity out of which to create spatial entity
   * @param surroundingComp
   *          SpatialEntity in which to create entity
   * @param nearComp
   *          SpatialEntity near which to place new entity (optional)
   * @param variables
   * @return Created compartment, or null if not possible (no space)
   */
  private List<SpatialEntity> createSpatialEntities(InitEntity ruleEnt,
      SpatialEntity surroundingComp, SpatialEntity nearComp,
      Map<String, Object> variables) {
    List<SpatialEntity> prodEnts = getModEntFac().createSpatialEntities(ruleEnt,
        1, surroundingComp, variables);

    for (SpatialEntity prodEnt : prodEnts) {
      if (!((Boolean) prodEnt
          .getAttribute(ModelEntityFactory.WRONG_POS_MARKER))) {
        throw new IllegalStateException("Position of newly created "
            + "entities must not be provided by creating rule.");
      }
      if (!collRes.placeNewCompNear(prodEnt, nearComp)) {
        // CHECK: out-of-bounds check? spatial index update?!
        return null;
      }

      prodEnt.removeTempAttribute(ModelEntityFactory.WRONG_POS_MARKER);

      getLogger().checkAndLog(DebugLevel.COMP_CREATION_AND_DESTRUCTION,
          Level.INFO, "Created " + prodEnt);
    }
    return prodEnts;
  }

  @Override
  protected Collection<SpatialEntity> dissolveSpatialEntity(
      SpatialEntity comp) {
    SpatialEntity par = comp.getEnclosingEntity();
    if (par == null) {
      getLogger().log(Level.SEVERE, "Root compartment cannot dissolve!");
      return null; // CHECK: throw error?
    }

    Collection<SpatialEntity> formerChildren = compTree.removeNode(comp);

    for (SpatialEntity child : formerChildren) {
      child.setEnclosingEntity(par);
    }
    spatialIndex.unregisterComp(comp);
    if (getLogger().isDebugLevelSet(DebugLevel.COMP_MAPS_CORRECTNESS)) {
      checkPhantomComps(comp);
    }
    getLogger().checkAndLog(DebugLevel.COMP_CREATION_AND_DESTRUCTION,
        Level.INFO, "Dissolved " + comp + "; new parent for " + formerChildren);
    return formerChildren;
  }

  private void undoDissolve(
      Map<SpatialEntity, Collection<SpatialEntity>> dissolvedAndChildren) {
    for (Map.Entry<SpatialEntity, Collection<SpatialEntity>> e : dissolvedAndChildren
        .entrySet()) {
      SpatialEntity comp = e.getKey();
      spatialIndex.registerNewEntity(comp);
      compTree.addChildParentRelation(comp, comp.getEnclosingEntity());
      for (SpatialEntity child : e.getValue()) {
        child.setEnclosingEntity(comp);
        compTree.addChildParentRelation(child, comp);
      }
    }
  }

  /* DEBUG */
  private SpatialEntity lastRemovedComp = null;

  private SpatialEntity checkPhantomComps(SpatialEntity removed) {
    if (lastRemovedComp == null) {
      lastRemovedComp = removed;
      return null;
    }
    SpatialEntity rv = null;
    getLogger().checkAndLog(lastRemovedComp.equals(removed), Level.SEVERE,
        removed + " twice removed");
    final String remvd = "Removed ";
    if (getLogger().checkAndLog(
        Hierarchies.getNodeStatus(compTree,
            lastRemovedComp) != Hierarchies.NodeStatus.ABSENT,
        Level.SEVERE,
        remvd + lastRemovedComp + " is still "
            + Hierarchies.getNodeStatus(compTree, lastRemovedComp)
            + " in compTree.")) {
      rv = lastRemovedComp;
    }
    if (getLogger().checkAndLog(
        compTree.getChildren(lastRemovedComp.getEnclosingEntity())
            .contains(lastRemovedComp),
        Level.SEVERE,
        remvd + lastRemovedComp + " is still in compTree as child.")) {
      rv = lastRemovedComp;
    }
    if (getLogger().checkAndLog(
        spatialIndex.getRegisteredPosition(lastRemovedComp) != null,
        Level.SEVERE,
        lastRemovedComp + " not correctly removed from spatial index")) {
      rv = lastRemovedComp;
      spatialIndex.getRegisteredPosition(lastRemovedComp);
    }
    if (!isRemovedFromEventMaps(lastRemovedComp)) {
      rv = lastRemovedComp;
    }
    lastRemovedComp = removed;
    return rv;
  }

  /**
   * {@inheritDoc}. Outer method in the position update handling method
   * "hierarchy": determines an update vector for the current event compartment,
   * calls appropriate methods to perform the actual update, if this fails
   * repeats both steps the appropriate number of times, and updates the event
   * in the event queue.
   */
  @Override
  protected IContSpaceEventRecord handleMoveEvent(MoveEvent event) {
    if (getLogger().isDebugLevelSet(DebugLevel.COMP_MAPS_CORRECTNESS)) {
      checkCompMapsCorrectness();
      checkHierarchyInclusionConsistency();
    }
    SpatialEntity comp = event.getTriggeringComponent();
    ContSpaceEventRecord effect = null;
    int moveAttempts = 0;
    do {
      moveAttempts++;
      IDisplacementVector posUpd = posUpdater
          .getPositionUpdate(getTime() - event.getTimeOfLastUpdate(), comp);
      getLogger().checkAndLog(DebugLevel.MOVE_ATTEMPT, Level.INFO,
          "Attempt " + moveAttempts + " for " + comp + ": " + posUpd);
      recDepth = 0;
      if (comp instanceof Compartment
          && ((Compartment) comp).getComplex() != null) {
        effect = handleComplexPositionUpdate((Compartment) comp, posUpd);
      } else {
        effect = handleSinglePositionUpdate(comp, posUpd, null);
      }
      if (comp.getDiffusionConstant() == 0.) {
        break; // for directed movement, no need for multiple attempts
      }
    } while (!effect.getState().isSuccess() && moveAttempts < maxMoveAttempts);
    effect.setNumInfo(
        effect.getState().isSuccess() ? moveAttempts - 1 : moveAttempts);
    return effect;
  }

  private boolean showNoCompMapCorrCheckWarning = true;

  private void checkCompMapsCorrectness() {
    try {
      Collection<SpatialEntity> n =
          ShapeHierarchyUtils.notUpToDateInHierarchy(compTree);
      getLogger().checkAndLog(!n.isEmpty(), Level.SEVERE,
          "CT Up-to-date Error: " + n);
    } finally {
      try {
        Collection<SpatialEntity> l = spatialIndex.notUpToDate();
        getLogger().checkAndLog(!l.isEmpty(), Level.SEVERE,
            "SI Up-to-date Error: " + l);
      } catch (UnsupportedOperationException e) {
        if (getLogger().checkAndLog(showNoCompMapCorrCheckWarning,
            Level.WARNING,
            "Cannot check for comp map correctness: " + spatialIndex)) {
          showNoCompMapCorrCheckWarning = false;
        }
      }
    }
  }

  private void checkHierarchyInclusionConsistency() {
    for (Map.Entry<SpatialEntity, SpatialEntity> e : compTree
        .getChildToParentMap().entrySet()) {
      SpatialEntity comp = e.getKey();
      SpatialEntity parent = e.getValue();
      if (comp.getEnclosingEntity() != parent) {
        getLogger().log(Level.SEVERE,
            "Incorrect parent of " + comp + ": " + parent);
      }
      if (parent.isHardBounded()) {
        if (!parent.getShape().getRelationTo(comp.getShape())
            .equals(ShapeRelation.SUPERSET)) {
          getLogger().log(Level.SEVERE, comp + " not entirely in " + parent);
        }
      } else if (!parent.getShape().includesPoint(comp.getPosition())) {
        getLogger().log(Level.SEVERE, comp + " not entirely in " + parent);
      }
    }
  }

  /**
   * Handle position update of (ostentibly) one single compartment by given
   * update vector. If the move is possible, all compartments inside the given
   * single compartment will be appropriately moved with the given single comp.
   * 
   * Additionally, if a larger comp moves over smaller comps, it may "swallow"
   * (i.e. incorporate) them, resulting in these compartments being moved, too
   * (by a different update vector). The third parameter, given as Pair of List
   * of compartments (to move) and target compartment, is used in those cases
   * and will result in call to
   * {@link #handlePotentialTransfersIn(List, SpatialEntity)}, which may call
   * this method again (needed to handle correct rollback if any incorporation
   * fails).
   * 
   * @param comp
   *          SpatialEntity whose position to update
   * @param posUpd
   *          Displacement to apply to comp's position
   * @param pendingTransfersIn
   *          Transfers still to check/handle (null if none)
   * @return actually applied position (may vary due to collisions/reactions) or
   *         null if applying update not possible
   */
  private ContSpaceEventRecord handleSinglePositionUpdate(SpatialEntity comp,
      IDisplacementVector posUpd,
      Pair<List<SpatialEntity>, SpatialEntity> pendingTransfersIn) {
    recDepth++;
    getLogger().checkAndLog(
        recDepth > REC_DEPTH_WARNING_THESHOLD && pendingTransfersIn == null,
        recDepth < 2 * REC_DEPTH_WARNING_THESHOLD ? Level.INFO : Level.SEVERE,
        "Recursion depth " + recDepth + ";  moving: " + comp + "; move: "
            + posUpd);
    Collection<SpatialEntity> alsoMoved = applyMove(comp, posUpd);
    if (alsoMoved == null) {
      return ContSpaceEventRecord.newFailedMove(comp);
    }

    ContSpaceEventRecord effect =
        new ContSpaceEventRecord(comp, alsoMoved, posUpd);
    // first, check possible transfer out of current parent comp
    if (movedOutOfParent(comp)) {
      getLogger().checkAndLog(pendingTransfersIn != null, Level.SEVERE,
          "There must not be any "
              + "pending transfer when a compartment moves "
              + "out! (conceptual bug)");
      ContSpaceEventRecord furtherEffect =
          handlePotentialTransferOut(comp, comp.getEnclosingEntity());
      return processFurtherEffect(effect, furtherEffect, comp, posUpd,
          alsoMoved);
    }
    // no transfer out during this call evaluation:
    // check for collisions with other comps

    List<SpatialEntity> collComps = getCollidingComps(comp);
    if (collComps.size() != 0) { // handle collisions
      if (ContinuousSpaceProcessor.isImmobileOrBound(comp)) {
        assert recDepth > 1;
        // CHECK: try to move colliding comp(s)? (trouble down the line
        // so far)
        comp.move(posUpd.times(-1));
        return ContSpaceEventRecord.newFailedMove(comp);
      }
      ContSpaceEventRecord furtherEffect =
          handleCollisionGeneral(comp, collComps, pendingTransfersIn, posUpd);
      return processFurtherEffect(effect, furtherEffect, comp, posUpd,
          alsoMoved);
    }
    // no transfers/collisions whatsoever during this call evaluation
    // (alone, not considering pending transfers in)
    if (pendingTransfersIn != null) {
      ContSpaceEventRecord furtherEffect =
          handlePotentialTransfersIn(pendingTransfersIn.getFirstValue(),
              pendingTransfersIn.getSecondValue());
      return processFurtherEffect(effect, furtherEffect, comp, posUpd,
          alsoMoved);
    }
    return effect;
  }

  /**
   * Move entity and nested entities, returning the latter (for further handling
   * if necessary), updating spatial index in the process. Performs
   * out-of-bounds-check on entity after move: if out-of-bounds, it is moved
   * back and null is returned!
   * 
   * @param spEnt
   *          Spatial entity
   * @param posUpd
   *          Update vector
   * @return null if out-of-bounds (posUpd not applied!), collection of also
   *         moved nested comps otherwise (at least empty collection!)
   */
  private Collection<SpatialEntity> applyMove(SpatialEntity spEnt,
      IDisplacementVector posUpd) {
    // method may be called from indirectly #handlePotentialTransferOut to
    // check for transfer across several levels; then, posUpd may be the 0
    // vector
    if (posUpd.isNullVector()) {
      return Collections.emptySet();
    }
    // tentatively apply move
    spEnt.move(posUpd);
    if (spatialIndex.isOutOfBounds(spEnt)) {
      spEnt.move(posUpd.times(-1));
      return null;
    }
    spatialIndex.updateCompPos(spEnt);
    // tentatively move nested comps, too
    // (for correct internal collision check)
    Collection<SpatialEntity> nestedCompsMoved =
        moveNestedCompsRec(spEnt, posUpd);
    if (nestedCompsMoved == null) {
      spEnt.move(posUpd.times(-1));
      spatialIndex.updateCompPos(spEnt);
    }
    return nestedCompsMoved;
  }

  private void revertMoves(IDisplacementVector posUpd,
      Collection<Compartment> moved, Collection<SpatialEntity> movedNested) {
    IDisplacementVector negPosUpd = posUpd.times(-1);
    for (SpatialEntity c : CombinedIterator.join(moved, movedNested)) {
      c.move(negPosUpd);
      spatialIndex.updateCompPos(c);
    }
  }

  /**
   * @param comp
   *          Spatial entity (usually compartment)
   * @return true iff comp does not diffuse or has at least one occupied binding
   *         site
   */
  private static boolean isImmobileOrBound(SpatialEntity comp) {
    if (comp instanceof Compartment
        && ((Compartment) comp).hasBoundEntities()) {
      return true;
    }
    Object vel = comp.getAttribute(SpatialAttribute.VELOCITY.toString());
    boolean hasVelocity = vel != null && (Double) vel > 0.;
    return !hasVelocity && comp.getDiffusionConstant() == 0.;
  }

  /**
   * When one event triggered another one, add the record of the second as
   * triggered effect to the first one, and * if the second was successful, also
   * add all entities moved along with the original triggering entity to the
   * record. * If not successful, undo all moves and spatial index updates.
   * 
   * @param effect
   *          Triggering effect
   * @param furtherEffect
   *          Triggered effect
   * @param comp
   *          Spatial entity that triggered the effect
   * @param posUpd
   *          Move vector of comp that triggered furtherEffect
   * @param alsoMoved
   *          Nested entities of comp that were moved along
   * @return The original effect (now with furtherEffect added as triggered
   *         effect and with state set to FAILED the latter failed, too).
   */
  private ContSpaceEventRecord processFurtherEffect(ContSpaceEventRecord effect,
      ContSpaceEventRecord furtherEffect, SpatialEntity comp,
      IDisplacementVector posUpd, Collection<SpatialEntity> alsoMoved) {
    if (!furtherEffect.getState().isSuccess()) {
      rollbackMoves(alsoMoved, posUpd);
      comp.move(posUpd.times(-1));
      spatialIndex.updateCompPos(comp);
    }
    furtherEffect.setTrigger(effect); // will set state to FAILED if
    // necessary
    return effect;
  }

  /**
   * Check whether comp moved recently in a way that it may end up outside its
   * parent (which is the case if the parent has hard boundaries and comp now
   * overlaps a boundary of the parent, or if it has soft boundaries and comp's
   * center is now outside the parent)
   * 
   * @param comp
   *          SpatialEntity that moved recently (move already applied)
   * @return true if new position triggers check for outgoing transfer
   */
  private static boolean movedOutOfParent(SpatialEntity comp) {
    SpatialEntity parent = comp.getEnclosingEntity();
    if (parent == null) {
      return false;
    }
    if (!parent.isHardBounded()) {
      return !parent.getShape().includesPoint(comp.getPosition());
    } else {
      return parent.getShape()
          .getRelationTo(comp.getShape()) != ShapeRelation.SUPERSET;
    }
  }

  private static final double BOUNDARY_JUMP_ADD = 1e-6; // CHECK: better way?

  /**
   * If a moving compartment collides with a hard boundary, it may jump over it,
   * i.e. be placed completely on the respective other side of the boundary
   * after the move.
   * 
   * @param comp
   *          SpatialEntity that shall cross boundary
   * @param sourceComp
   *          SpatialEntity whose boundary to cross
   * @return Update vector for correct placement (null vector if targetComp has
   *         soft boundaries)
   */
  private IDisplacementVector getBoundaryJumpOut(IShapedComponent comp,
      SpatialEntity sourceComp) {
    if (!sourceComp.isHardBounded()) {
      return vecFac.nullVector();
    }
    IDisplacementVector disp = sourceComp.getShape()
        .dispForTouchOutside(comp.getShape()).times(BOUNDARY_JUMP_ADD + 1.);

    return posUpdater.adjustUpdateVector(disp);
  }

  /**
   * If a moving compartment collides with a hard boundary, it may jump over it,
   * i.e. be placed completely on the respective other side of the boundary
   * after the move.
   * 
   * @param comp
   *          SpatialEntity that shall cross boundary
   * @param targetComp
   *          SpatialEntity whose boundary to cross
   * @return Update vector for correct placement (null vector if targetComp has
   *         soft boundaries)
   */
  private IDisplacementVector getBoundaryJumpIn(IShapedComponent comp,
      SpatialEntity targetComp) {
    if (!targetComp.isHardBounded()) {
      return vecFac.nullVector();
    }
    IDisplacementVector disp = targetComp.getShape()
        .dispForTouchInside(comp.getShape()).times(BOUNDARY_JUMP_ADD + 1.);

    return posUpdater.adjustUpdateVector(disp);
  }

  /**
   * Move compartments nested inside a given comp by given position update,
   * including, recursively, all compartments nested inside these, and so on;
   * update spatial index for these, do not check for move out of bounds
   * (assuming the move was valid for the surrounding comp)
   * 
   * This may fail if regions move and comps near their boundary go out of
   * bounds or out of the region's parent, in which case null is returned.
   * 
   * @param comp
   *          SpatialEntity whose inside to move
   * @param posUpd
   *          Displacement to move by
   * @return Collection of all moved compartments
   */
  private Collection<SpatialEntity> moveNestedCompsRec(SpatialEntity comp,
      IDisplacementVector posUpd) {
    Collection<SpatialEntity> movedComps =
        new ArrayList<>(compTree.getChildren(comp).size());
    boolean failed = false;
    if (comp.isHardBounded()) {
      for (SpatialEntity cc : compTree.getChildren(comp)) {
        cc.move(posUpd);
        movedComps.add(cc);
        spatialIndex.updateCompPos(cc);
        Collection<SpatialEntity> movedNestedComps =
            moveNestedCompsRec(cc, posUpd);
        if (movedNestedComps == null) {
          failed = true;
          break;
        }
        movedComps.addAll(movedNestedComps);
      }
    } else {
      for (SpatialEntity cc : compTree.getChildren(comp)) {
        cc.move(posUpd);
        movedComps.add(cc);
        if (spatialIndex.isOutOfBounds(cc) || collidedByRegionMove(cc)) {
          failed = true;
          break;
        }
        spatialIndex.updateCompPos(cc);
        Collection<SpatialEntity> movedNestedComps =
            moveNestedCompsRec(cc, posUpd);
        if (movedNestedComps == null) {
          failed = true;
          break;
        }
        movedComps.addAll(movedNestedComps);
      }
    }
    if (failed) {
      IDisplacementVector undoPosUpd = posUpd.times(-1);
      for (SpatialEntity mc : movedComps) {
        mc.move(undoPosUpd);
        spatialIndex.updateCompPos(mc);
      }
      return null;
    }
    return movedComps;
  }

  /**
   * Check whether spatial entity in a region that moved along with this region
   * overlaps said regions boundary and by moving along collided with something
   * outside this region, including the boundary of the regions surrounding
   * entity (even if that is itself a region)
   * 
   * @param spEnt
   * @return
   */
  private boolean collidedByRegionMove(SpatialEntity spEnt) {
    SpatialEntity parent = spEnt.getEnclosingEntity();
    if (spEnt.getShape().getRelationTo(parent.getShape())
        .equals(ShapeRelation.SUBSET)) {
      return false;
    }
    if (parent.getEnclosingEntity() != null && !spEnt.getShape()
        .getRelationTo(parent.getEnclosingEntity().getShape())
        .equals(ShapeRelation.SUBSET)) {
      return true;
    }
    List<SpatialEntity> collidingComps = getCollidingComps(spEnt);
    for (SpatialEntity cc : collidingComps) {
      if (!cc.getEnclosingEntity().equals(parent)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Roll back previous tentative compartment moves
   * 
   * @param movedComps
   *          Collection of compartments that were moved tentatively
   * @param posUpd
   *          Displacement vector they were moved by
   */
  private void rollbackMoves(Collection<SpatialEntity> movedComps,
      IDisplacementVector posUpd) {
    if (!posUpd.isNullVector()) {
      IDisplacementVector revPosUpd = posUpd.times(-1.);
      for (SpatialEntity movedComp : movedComps) {
        movedComp.move(revPosUpd);
        spatialIndex.updateCompPos(movedComp);
      }
    }
  }

  private List<SpatialEntity> getCollidingComps(SpatialEntity comp) {
    List<SpatialEntity> collComps;
    collComps = spatialIndex.collidingComps(comp);
    GeoUtils.removeEnclosingComps(comp, collComps);
    collComps.removeAll(compTree.getChildren(comp));
    // TODO: recursively! otherwise "collision" of moving big comp with
    // small comps two or more levels below!

    // previous removal of same-complex entities removed too much (this method
    // may be called recursively to check whether there is space for binding)
    // CompComplex complex =
    // comp instanceof Compartment ? ((Compartment) comp).getComplex() : null;

    Collection<? extends SpatialEntity> boundEnts = comp instanceof Compartment
        ? ((Compartment) comp).getBoundEntities() : Collections.EMPTY_SET;

    // remove soft boundary components whose boundary was not crossed
    Iterator<SpatialEntity> ccIt = collComps.iterator();
    while (ccIt.hasNext()) {
      SpatialEntity cc = ccIt.next();
      if (cc.isHardBounded()
          || cc.getShape().includesPoint(comp.getPosition())) {
        if (!comp.isHardBounded()
            && !comp.getShape().includesPoint(cc.getPosition())) {
          ccIt.remove();
          continue;
        }
        // TODO: what to do if moving comps can be larger than regions?!

        if (boundEnts.contains(cc)) {
          ccIt.remove();
          // if (complex != null && cc instanceof Compartment
          // && complex == ((Compartment) cc).getComplex()) {
          // ccIt.remove();
        }
      } else {
        // disallow Region-Region-overlap for Regions of same species
        // TODO: make that a flag defined by user
        if (!cc.getSpecies().equals(comp.getSpecies())) {
          ccIt.remove(); // check!
        }
      }
    }
    return collComps;
  }

  private ContSpaceEventRecord handleCollisionGeneral(SpatialEntity comp,
      List<SpatialEntity> collComps,
      Pair<List<SpatialEntity>, SpatialEntity> pendingTransfersIn,
      IDisplacementVector posUpd) {
    // try collision-triggered (same-level) reactions first
    if (pendingTransfersIn == null && collComps.size() == 1) {
      SpatialEntity collComp;
      collComp = collComps.get(0);
      List<Match<SpatialEntity>> potentialSecondOrderReactions =
          CollisionReactionRule.getPotentialSecondOrderReactions(
              comp.getEnclosingEntity(), comp, collComp,
              secondOrderReactionRules, getRand());
      for (Match<SpatialEntity> e : potentialSecondOrderReactions) {
        ContSpaceEventRecord effect =
            handlePotentialSecondOrderReaction(comp, collComp, e, posUpd);
        if (effect.getState().isSuccess()) {
          return effect; // no further collisions to process
        }
      }
    }

    // try transfer if the former failed
    ContSpaceEventRecord effect =
        handleCollisionTriggeredTransfer(comp, collComps);
    if (!effect.getState().isSuccess()) {
      return effect;
    }
    if (pendingTransfersIn != null) {
      ContSpaceEventRecord furtherEffects =
          handlePotentialTransfersIn(pendingTransfersIn.getFirstValue(),
              pendingTransfersIn.getSecondValue());
      if (!furtherEffects.getState().isSuccess()) {
        furtherEffects.setTrigger(effect);
        return effect;
      }
      furtherEffects.setTrigger(effect);
    }
    return effect;
  }

  /**
   * Handling of recently moved compartment colliding with other compartments,
   * i.e. possible transfers INTO ("this into other" and "other into this")
   * 
   * @param comp
   *          Recently moved compartment
   * @param collComps
   *          Compartments comp collides with (must be at the same
   *          organisational level)
   * @return
   */
  private ContSpaceEventRecord handleCollisionTriggeredTransfer(
      SpatialEntity comp, List<SpatialEntity> collComps) {

    List<SpatialEntity>[] largerSameSmallerComps = GeoUtils
        .getLargerSameSmallerComps(collComps, comp.getShape().getSize());

    // possible transfers INTO first
    for (SpatialEntity collComp : largerSameSmallerComps[0]) {
      // // check for all larger colliding compartments (pairwise non-
      // overlapping by assumption) whether current comp can enter them
      ContSpaceEventRecord effect =
          handlePotentialTransfersIn(Arrays.asList(comp), collComp);
      if (effect.getState().isSuccess()) {
        return effect; // ...-> we're done here
      }
    }

    int numNotSmaller =
        largerSameSmallerComps[0].size() + largerSameSmallerComps[1].size();
    if (numNotSmaller >= 1) {
      // (ab-)use failed move attempt record to indicate failed collision
      ContSpaceEventRecord rv = ContSpaceEventRecord.newFailedMove(comp);
      rv.addAlsoMoved(collComps, null);
      return rv;
    }

    // try to incorporate smaller comps (existence now assured)
    ContSpaceEventRecord effect = handlePotentialTransfersIn(collComps, comp);
    if (!effect.isSuccess() && collComps.size() == 1
        && !collComps.get(0).isHardBounded()) {
      // possiblity to use soft bounded entity for "region of certain interest"
      // in which larger entity can have their center
      ContSpaceEventRecord newEffect =
          handlePotentialTransfersIn(Arrays.asList(comp), collComps.get(0));
      if (newEffect.isSuccess()) {
        return newEffect;
      }
    }
    return effect;
  }

  /**
   * Handle collision with parent compartment's boundary, i.e. potential
   * transfer OUT
   * 
   * @param comp
   *          Recently moved compartment
   * @param oriParComp
   *          Comp's parents compartment before move
   * @return null if move not valid, additional position update otherwise (if
   *         comp needs to be placed _completely_ outside its old parent; side
   *         effect: comp's attributes may be modified according to applied
   *         transfer rule)
   */
  private ContSpaceEventRecord handlePotentialTransferOut(SpatialEntity comp,
      SpatialEntity oriParComp) {
    for (TransferOutRule tr : transferOutRules) {
      // TODO: SAME_LEVEL transfers !?!
      Match<SpatialEntity> match = tr.match(comp, comp.getEnclosingEntity());
      if (match.isSuccess() && getRand().nextDouble() < match.getRate()) {
        // tentatively set new parent and apply rule
        comp.setEnclosingEntity(oriParComp.getEnclosingEntity());
        compTree.addChildParentRelation(comp, comp.getEnclosingEntity());
        SuccessfulMatch.ModRecord<SpatialEntity> mods = match.apply();
        ContSpaceEventRecord effect =
            new ContSpaceEventRecord(comp, oriParComp, mods, tr);

        IDisplacementVector posUpdNew;
        if (comp.getShape().getRelationTo(oriParComp.getShape())
            .isCollision()) {
          posUpdNew = getBoundaryJumpOut(comp, oriParComp);
        } else {
          posUpdNew = vecFac.nullVector();
        }
        // tentative move outside parent is handled in next call
        ContSpaceEventRecord furtherEffect =
            handleSinglePositionUpdate(comp, posUpdNew, null);

        if (!furtherEffect.getState().isSuccess()) { // rollback
          comp.setEnclosingEntity(oriParComp);
          compTree.addChildParentRelation(comp, oriParComp);
          AttModUtils.rollbackRuleModifications(mods);
          return furtherEffect;
        }
        furtherEffect.setTrigger(effect);
        return effect;
      }
    }
    // (ab-)use failed move attempt record to indicate failed collision
    ContSpaceEventRecord rv = ContSpaceEventRecord.newFailedMove(comp);
    rv.addAlsoMoved(Collections.singleton(oriParComp), null);
    return rv; // no matching rule, or incorporation failed
  }

  private ContSpaceEventRecord handlePotentialTransfersIn(
      List<SpatialEntity> movingComps, SpatialEntity targetComp) {
    if (movingComps == null || movingComps.isEmpty()) {
      throw new IllegalStateException();
    }
    SpatialEntity movingComp = movingComps.get(0);
    List<SpatialEntity> otherMovingComps =
        movingComps.subList(1, movingComps.size());

    for (TransferInRule tr : transferInRules) {
      // does the transfer rule apply to ent?
      Match<SpatialEntity> match = tr.match(movingComp, targetComp);
      if (match.isSuccess() && getRand().nextDouble() < match.getRate()) {
        ContSpaceEventRecord effect = handleTransferInWithRule(movingComp,
            targetComp, match, tr, otherMovingComps);
        if (effect.getState().isSuccess()) {
          return effect;
        }
      }
    }
    // (ab-)use failed move attempt record to indicate failed collision
    ContSpaceEventRecord rv = ContSpaceEventRecord.newFailedMove(movingComp);
    rv.addAlsoMoved(Collections.singleton(targetComp), null);
    return rv; // no matching rule, or incorporation failed
  }

  /**
   * Apply given transfer rule INTO to given compartments. Return effect if
   * successful, null unsuccessful due to spatial contraints or otherMovingComps
   * not successfully transferred.
   * 
   * @param movingComp
   *          Spatial entity that moves into...
   * @param targetComp
   *          Spatial entity the moving one moves into
   * @param match
   *          Rule match result
   * @param rule
   *          Transfer rule (for the record)
   * @param otherMovingComps
   *          Other compartments also to be moved into targetComp (recursively
   *          via
   *          {@link #handleSinglePositionUpdate(SpatialEntity, IDisplacementVector, boolean, Pair)}
   *          )
   * @return Effect record if successful, null if not
   */
  private ContSpaceEventRecord handleTransferInWithRule(
      SpatialEntity movingComp, SpatialEntity targetComp,
      Match<SpatialEntity> match, MLSpaceRule rule,
      List<SpatialEntity> otherMovingComps) {
    // tentatively set new parent (up to here:
    // comp.getEnclosingEntity() == targetComp.getEnclosingEntity())
    movingComp.setEnclosingEntity(targetComp);
    compTree.addChildParentRelation(movingComp, targetComp);
    // tentatively apply attribute modifications
    SpatialEntity oriParComp = targetComp.getEnclosingEntity();
    SuccessfulMatch.ModRecord<SpatialEntity> mods = match.apply();
    ContSpaceEventRecord effect =
        new ContSpaceEventRecord(movingComp, oriParComp, mods, rule);

    ContSpaceEventRecord furtherEffect;
    IDisplacementVector posUpdNew;
    if (targetComp.getShape()
        .getRelationTo(movingComp.getShape()) == ShapeRelation.SUPERSET) {
      posUpdNew = vecFac.nullVector();
    } else {
      posUpdNew = getBoundaryJumpIn(movingComp, targetComp);
    }
    if (!otherMovingComps.isEmpty()) {
      getLogger().checkAndLog(DebugLevel.PROCESSED_COMP_EVENT_INFO, Level.INFO,
          "More than one colliding comp: " + targetComp + "<-" + movingComp
              + "+" + otherMovingComps);

      furtherEffect = handleSinglePositionUpdate(movingComp, posUpdNew,
          new Pair<>(otherMovingComps, targetComp));
    } else {
      furtherEffect = handleSinglePositionUpdate(movingComp, posUpdNew, null);
    }
    if (!furtherEffect.getState().isSuccess()) {
      // roll back tentative parent assignment
      movingComp.setEnclosingEntity(oriParComp);
      compTree.addChildParentRelation(movingComp, oriParComp);
      AttModUtils.rollbackRuleModifications(mods);
      return furtherEffect;
    } // else successful move into
    furtherEffect.setTrigger(effect);
    return effect;
  }

  /**
   * @param comp
   * @param collComp
   * @param match
   * @param posUpd
   */
  private ContSpaceEventRecord handlePotentialSecondOrderReaction(
      SpatialEntity comp, SpatialEntity collComp, Match<SpatialEntity> match,
      IDisplacementVector posUpd) {

    // actual rule application here:
    SuccessfulMatch.ModRecord<SpatialEntity> mods = match.apply();
    // formerly: AttModUtils.applyRuleModifications(react, comp, collComp,
    // match);

    Collection<SpatialEntity> consumed = match.getConsumed();
    NonTransferRule react = (NonTransferRule) match.getRule(); // cast for
                                                               // getProduced()
                                                               // call later
    ContSpaceEventRecord effect =
        new ContSpaceEventRecord(comp, collComp, mods, react);
    Map<SpatialEntity, Collection<SpatialEntity>> dissolvedAndChildren =
        new LinkedHashMap<>();
    ContSpaceEventRecord furtherEffect = null;
    if (consumed.isEmpty()) {
      furtherEffect = resolveCollision(comp, collComp, posUpd);
      furtherEffect.setTrigger(effect);
      if (!effect.getState().isSuccess()) {
        AttModUtils.rollbackRuleModifications(mods);
        return effect;
      }
    } else {
      for (SpatialEntity ce : consumed) {
        dissolvedAndChildren.put(ce, dissolveSpatialEntity(ce));
      }
    }
    // FIXME: check for collision of formerNested, resolve or rollback!

    List<SpatialEntity> prodEnts = produceEntities(comp.getEnclosingEntity(),
        comp, react.getProduced(), match.getEnv());

    if (prodEnts == null) {// unsuccessful
      undoDissolve(dissolvedAndChildren);
      AttModUtils.rollbackRuleModifications(mods);
      if (furtherEffect != null) { // from collision resolution
        comp.move(
            furtherEffect.getCompChanges().get(comp).getPosUpd().times(-1.));
        // TODO/CHECK: furtherEffect may include EnclosingComp changes!
      }
      ContSpaceEventRecord.newFailedMove(comp).setTrigger(effect);
    } else {
      for (Map.Entry<SpatialEntity, Collection<SpatialEntity>> e : dissolvedAndChildren
          .entrySet()) {
        effect.addDestroyed(e.getKey(), e.getValue());
      }
      effect.addAllCreated(prodEnts);
    }

    return effect;
  }

  private ContSpaceEventRecord resolveCollision(SpatialEntity comp,
      SpatialEntity collComp, IDisplacementVector posUpd) {
    CompComplex complex1 =
        comp instanceof Compartment ? ((Compartment) comp).getComplex() : null;
    if (complex1 != null) {
      CompComplex complex2 = collComp instanceof Compartment
          ? ((Compartment) collComp).getComplex() : null;
      if (complex2 != null && complex2 != complex1) {
        return ContSpaceEventRecord.newFailedMove(comp);
      } else if (complex2 != complex1) {
        // keep comp in complex fixed, try resolving by moving collComp
        return resolveCollision(collComp, comp, null);
        // TODO: this may lead to collComp pushed against system boundary
        // (and maybe infinite collision resolution loops, too)
      }
    }
    IDisplacementVector disp;
    if (posUpd == null) {
      disp = collRes.resolveCollision(comp, collComp);
    } else {
      disp = collRes.resolveCollision(comp, collComp, posUpd);
    }
    if (disp == null) {
      return ContSpaceEventRecord.newFailedMove(comp);
    }
    disp = posUpdater.adjustUpdateVector(disp);
    // FIXME: adjustment kinda fishy (gaps may appear)
    return handleSinglePositionUpdate(comp, disp, null);
  }

  /**
   * @param complexAnchor
   * @param posUpd
   * @return
   */
  private ContSpaceEventRecord handleComplexPositionUpdate(Compartment anchor,
      IDisplacementVector posUpd) {
    CompComplex complex = anchor.getComplex();
    assert complex.getAnchor() == anchor;

    Queue<Compartment> toMove = new LinkedList<>();
    toMove.add(anchor);
    Collection<Compartment> moved = new ArrayList<>();
    Collection<SpatialEntity> movedNested = new ArrayList<>();
    Map<Compartment, Collection<SpatialEntity>> collisions =
        new LinkedHashMap<>();

    while (!toMove.isEmpty()) {
      Compartment movingComp = toMove.remove();
      // TODO: not only simple parallel movement
      Collection<SpatialEntity> alsoMoved = applyMove(movingComp, posUpd);
      // TODO: move out of parent?
      if (alsoMoved == null) {
        revertMoves(posUpd, moved, movedNested);
        return ContSpaceEventRecord.newFailedMove(anchor);
      }
      movedNested.addAll(alsoMoved);
      List<SpatialEntity> collidingComps = getCollidingComps(movingComp);
      moved.add(movingComp);
      if (collidingComps.size() > 1 || !collisions.isEmpty()) {
        revertMoves(posUpd, moved, movedNested);
        return ContSpaceEventRecord.newFailedMove(anchor);
      } else if (!collidingComps.isEmpty()) {
        collisions.put(movingComp, collidingComps);
      }
      for (Compartment bound : movingComp.getBoundEntities()) {
        if (!moved.contains(bound)) {
          toMove.add(bound);
        }
      }
    }
    assert moved.size() == complex.getNumOfParticles();

    ContSpaceEventRecord collResEffect = null;
    if (!collisions.isEmpty()) {
      // TODO: adapt for more than 1 collision
      Map.Entry<Compartment, Collection<SpatialEntity>> e =
          collisions.entrySet().iterator().next();
      collResEffect = // TODO/CHECK: push of coll comp correct & useful?
          resolveCollision(e.getValue().iterator().next(), e.getKey(), posUpd);
      if (!collResEffect.getState().isSuccess()) {
        revertMoves(posUpd, moved, movedNested);
        return ContSpaceEventRecord.newFailedMove(anchor);
      }
    }

    moved.remove(anchor);
    movedNested.addAll(moved);
    ContSpaceEventRecord rv =
        new ContSpaceEventRecord(anchor, movedNested, posUpd);
    if (collResEffect != null) {
      collResEffect.setTrigger(rv);
    }
    return rv;
  }

  @Override
  public IHierarchy<SpatialEntity> getSpatialEntities() {
    return compTree;
  }
}
