/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.factories.Context;
import org.jamesii.core.math.geometry.spatialindex.ISpatialIndex;
import org.jamesii.core.math.geometry.spatialindex.SpatialIndexFactory;
import org.jamesii.core.math.geometry.spatialindex.StaticGridSpatialIndex;
import org.jamesii.core.math.geometry.spatialindex.StaticGridSpatialIndexFactory;
import org.jamesii.core.math.geometry.spatialindex.plugintype.AbstractSpatialIndexFactory;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.JamesProcessorFactory;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.calendar.CalendarReQueueFactory;
import org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.ParameterUtils;

import model.mlspace.IMLSpaceModel;
import model.mlspace.MLSpaceModel;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.subvols.Subvol;
import model.mlspace.subvols.SubvolUtils;
import simulator.mlspace.brownianmotion.ContinuousPositionUpdaterFactory;
import simulator.mlspace.brownianmotion.DiscretePositionUpdaterFactory;
import simulator.mlspace.brownianmotion.DiscretePositionUpdaterMasked;
import simulator.mlspace.brownianmotion.IContinuousPositionUpdater;
import simulator.mlspace.brownianmotion.IDiscretePositionUpdater;
import simulator.mlspace.brownianmotion.IPositionUpdater;
import simulator.mlspace.brownianmotion.PositionUpdaterFactory;
import simulator.mlspace.brownianmotion.plugintype.AbstractPositionUpdaterFactory;
import simulator.mlspace.event.IMLSpaceEvent;
import simulator.mlspace.event.ISpatialEntityEvent;
import simulator.mlspace.event.NSMEvent;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.util.MLSpaceLogger.DebugLevel;

/**
 * Factory class for MLSpaceProcessor that checks the model for presence of
 * <ul>
 * <li>compartments with non-zero diffusion attribute
 * <li>explicit subvolumes or zero-volume entities
 * </ul>
 * and creates the appropriate processor (NSM with compartments, also used for
 * NSM-only models; continuous-space only; hybrid; or, for empty models, a dummy
 * processor so that processor observers are still notified once)
 * 
 * @author Arne Bittig
 */
public class MLSpaceProcessorFactory extends JamesProcessorFactory {

  /** Serialization ID */
  private static final long serialVersionUID = 9056657147228634650L;

  /** (rather arbitrary) return value for {@link #getEfficencyIndex()} */
  private static final double EFFICIENCY_INDEX = 0.5;

  /** Parameter setting ID for debug level */
  public static final String DEBUG_LEVEL = "debugLevel";

  /** Parameter setting ID for target subvol size */
  public static final String MIN_SV_SIDE_LENGTH = "minSubvolSide";

  /** Parameter setting ID for target subvol size */
  public static final String MAX_SV_SIDE_LENGTH = "maxSubvolSide";

  /** Parameter setting ID for number of attempts per move event */
  public static final String MAX_MOVE_ATTEMPTS = "maxMoveAttempts";

  /**
   * Parameter setting ID for flag for using deterministic diffusion (may work
   * only with few models; not applied when no population-entities present)
   */
  public static final String USE_PDEAPPROX_NOT_NSM = "UsePDEApproxNotNSM";

  /**
   * Parameter setting ID for using hybrid simulator also when subvolume
   * simulator would suffice
   */
  public static final String FORCE_HYBRID = "ForceHybrid";

  public static final String RNG_GENERATOR = "RNGGenerator";

  @Override
  public IProcessor<Double> create(IModel model,
      IComputationTask computationTask, Partition partition,
      ParameterBlock params, Context context) {
    if (!(model instanceof MLSpaceModel)) {
      throw new IllegalArgumentException("Only for MLSpace models");
    }
    ApplicationLogger.log(Level.CONFIG, "Creating processor for " + model
        + " with parameters\n" + ParameterBlocks.toMultilineString(params));
    MLSpaceModel mlsModel = (MLSpaceModel) model;

    AbstractMLSpaceProcessor<?, ?> processor;
    final IRandom random = SimSystem.getRNGGenerator().getNextRNG();

    final boolean usePDEApprox =
        params.getSubBlockValue(USE_PDEAPPROX_NOT_NSM, false);

    if (!MLSpaceModel.hasActiveCompartments(mlsModel) && !ParameterBlocks
        .getSubBlockValueOrDefault(params, FORCE_HYBRID, false)) {
      if (MLSpaceModel.hasNSMPart(mlsModel)) {
        processor =
            createSubvolumeProcessor(params, mlsModel, random, usePDEApprox);
      } else {
        ApplicationLogger.log(Level.SEVERE, "Nothing to do: no "
            + "compartments or subvolumes in model " + mlsModel);
        processor = new DummyProcessor(mlsModel);
      }
    } else { // hybrid or continuous model
      // parameters needed for these two: spatialIndex, positionUpdater
      final ISpatialIndex<SpatialEntity> spatialIndex =
          getOrCreateSpatialIndex(params, mlsModel);
      final IPositionUpdater positionUpdater =
          getOrCreatePositionUpdater(params, mlsModel, random);
      Object mma = params.getSubBlockValue(MAX_MOVE_ATTEMPTS);
      Integer maxMoveAttempts = mma == null ? null : ((Number) mma).intValue();

      if (MLSpaceModel.hasNSMPart(mlsModel)) { // hybrid model
        processor = createHybridProcessor(params, mlsModel, random,
            usePDEApprox, spatialIndex, positionUpdater, maxMoveAttempts);
      } else {
        processor = new ContinuousSpaceProcessor(mlsModel,
            MLSpaceProcessorFactory
                .<ISpatialEntityEvent> getOrCreateEventQueue(params),
            random, spatialIndex, positionUpdater, maxMoveAttempts);
      }
    }

    setProcDebugLevel(processor, params,
        computationTask.getUniqueIdentifier().asString());

    processor.setComputationTask(computationTask);
    ((ISimulationRun) computationTask)
        .setProcessorInfo(new ProcessorInformation(processor));
    return processor;
  }

  private AbstractMLSpaceProcessor<?, ?> createSubvolumeProcessor(
      ParameterBlock params, MLSpaceModel mlsModel, final IRandom random,
      final boolean usePDEApprox) {
    AbstractMLSpaceProcessor<?, ?> processor;
    mlsModel.initSubvolumes(random,
        params.<Double> getSubBlockValue(MIN_SV_SIDE_LENGTH),
        params.<Double> getSubBlockValue(MAX_SV_SIDE_LENGTH));
    // subvolume-only model (fixed parent compartments possible)
    processor = usePDEApprox
        ? new StepwiseDeterministicDiffusionProcessor(mlsModel)
        : new NCMProcessor(mlsModel,
            MLSpaceProcessorFactory.<NSMEvent> getOrCreateEventQueue(params),
            random);
    return processor;
  }

  private AbstractMLSpaceProcessor<?, ?> createHybridProcessor(
      ParameterBlock params, MLSpaceModel mlsModel, final IRandom random,
      final boolean usePDEApprox,
      final ISpatialIndex<SpatialEntity> spatialIndex,
      final IPositionUpdater positionUpdater, Integer maxMoveAttempts) {
    AbstractMLSpaceProcessor<?, ?> processor;
    mlsModel.initSubvolumes(random,
        params.<Double> getSubBlockValue(MIN_SV_SIDE_LENGTH),
        params.<Double> getSubBlockValue(MAX_SV_SIDE_LENGTH));
    processor =
        usePDEApprox ? new StepwiseDeterministicDiffusionProcessor(mlsModel)
            : createHybridProcessor(mlsModel,
                MLSpaceProcessorFactory
                    .<IMLSpaceEvent<?>> getOrCreateEventQueue(params),
                random, spatialIndex, positionUpdater, maxMoveAttempts);
    return processor;
  }

  private static <E extends IMLSpaceEvent<?>> IEventQueue<E, Double> getOrCreateEventQueue(
      ParameterBlock params) {
    ParameterBlock eqfp = ParameterUtils.getFactorySubBlock(params,
        EventQueueFactory.class, CalendarReQueueFactory.class);
    EventQueueFactory eqf = SimSystem.getRegistry()
        .getFactory(AbstractEventQueueFactory.class, eqfp);
    final IEventQueue<E, Double> eventQueue = eqf.<E> createDirect(eqfp);
    ApplicationLogger.log(Level.CONFIG,
        "Using event queue of type " + eventQueue.getClass().getName());
    return eventQueue;
  }

  private static ISpatialIndex<SpatialEntity> getOrCreateSpatialIndex(
      ParameterBlock params, MLSpaceModel mlsModel) {
    ParameterBlock sifp = ParameterUtils.getFactorySubBlock(params,
        SpatialIndexFactory.class, StaticGridSpatialIndexFactory.class);
    SpatialIndexFactory sif = SimSystem.getRegistry()
        .getFactory(AbstractSpatialIndexFactory.class, sifp);
    addGridCellParam(sif, sifp, mlsModel);
    final ISpatialIndex<SpatialEntity> spatialIndex =
        sif.<SpatialEntity> createDirect(sifp);
    return spatialIndex;
  }

  /**
   * Exponent for calculating default number of grid cells from number of
   * spatial entities in model (e.g. 0.5 = square root)
   */
  private static final double NUM_GRID_CELL_POW = 0.7;

  /**
   * Factor for calculating max number of grid cells from number of spatial
   * entities in model
   */
  private static final double NUM_GRID_CELL_MAX_FACTOR = 0.25;

  /**
   * Check if spatial index factory is for {@link StaticGridSpatialIndex}, and
   * if no {@link StaticGridSpatialIndexFactory.NUMBER_OF_GRID_CELLS} param is
   * specified, add it based on number of entities in model.
   * 
   * @param sif
   * @param sifp
   * @param mlsModel
   */
  private static void addGridCellParam(SpatialIndexFactory sif,
      ParameterBlock sifp, MLSpaceModel mlsModel) {
    if (sif instanceof StaticGridSpatialIndexFactory
        && !sifp.hasSubBlock(StaticGridSpatialIndexFactory.NUMBER_OF_GRID_CELLS)
        && !sifp
            .hasSubBlock(StaticGridSpatialIndexFactory.GRID_CELL_SIDE_LENGTH)) {
      int numSpatialEntites =
          mlsModel.getCompartments().getChildToParentMap().size();
      sifp.addSubBlock(StaticGridSpatialIndexFactory.NUMBER_OF_GRID_CELLS,
          Math.max(1,
              (int) Math.min(Math.pow(numSpatialEntites, NUM_GRID_CELL_POW),
                  NUM_GRID_CELL_MAX_FACTOR * numSpatialEntites)));
    }
  }

  private static IPositionUpdater getOrCreatePositionUpdater(
      ParameterBlock params, MLSpaceModel mlsModel, final IRandom random) {
    ParameterBlock pufp = ParameterUtils.getFactorySubBlock(params,
        PositionUpdaterFactory.class, null);
    // add common parameters whose values may not be known previously
    if (!pufp.hasSubBlock(PositionUpdaterFactory.RANDOM)) {
      pufp.addSubBlock(PositionUpdaterFactory.RANDOM, random);
    }
    if (!pufp.hasSubBlock(PositionUpdaterFactory.VECTOR_FACTORY)) {
      pufp.addSubBlock(PositionUpdaterFactory.VECTOR_FACTORY,
          mlsModel.getVectorFactory());
    }

    // if steps parameter is defined, create discrete pos updater
    if (pufp.hasSubBlock(DiscretePositionUpdaterFactory.STEPS)) {
      return new DiscretePositionUpdaterFactory().create(pufp,
          SimSystem.getRegistry().createContext());
    }

    // else create continuous one; get reasonable travel distance if not set
    if (!pufp.hasSubBlock(ContinuousPositionUpdaterFactory.TRAVEL_DISTANCE)) {
      pufp.addSubBlock(ContinuousPositionUpdaterFactory.SPATIAL_ENTITIES,
          mlsModel.getCompartments().getAllNodes());
    }
    PositionUpdaterFactory puf = SimSystem.getRegistry()
        .getFactory(AbstractPositionUpdaterFactory.class, pufp);
    return puf.create(pufp, SimSystem.getRegistry().createContext());
  }

  private static IDiscretePositionUpdater makePositionUpdaterDiscrete(
      IPositionUpdater posUpdater, IDisplacementVector steps) {
    if (posUpdater instanceof IDiscretePositionUpdater) {
      IDiscretePositionUpdater discPosUpdater =
          (IDiscretePositionUpdater) posUpdater;
      if (!discPosUpdater.getStepSize().isEqualTo(steps)) {
        ApplicationLogger.log(Level.WARNING,
            "Using step size " + steps + " for hybrid simulation instead of "
                + discPosUpdater.getStepSize()
                + " set in existing discrete position updater.");
        if (!discPosUpdater.overrideStepSize(steps)) {
          throw new IllegalArgumentException("...failed");
        }
      }
      return discPosUpdater;
    }
    return new DiscretePositionUpdaterMasked(steps,
        (IContinuousPositionUpdater) posUpdater);
  }

  private static void setProcDebugLevel(
      AbstractMLSpaceProcessor<?, ?> processor, ParameterBlock params,
      String id) {
    processor.getLogger().setIDString(id);
    if (params.hasSubBlock(DEBUG_LEVEL)) {
      Object dbgLvl = params.getSubBlockValue(DEBUG_LEVEL);
      if (dbgLvl instanceof Collection) {
        Collection<?> dbgLvlC = (Collection<?>) dbgLvl;
        for (DebugLevel dbg : DebugLevel.values()) {
          processor.getLogger().setDebugLevel(dbg, dbgLvlC.contains(dbg));
        }
      } else if (dbgLvl instanceof Integer) {
        int i = 0, maxLvl = (Integer) dbgLvl;
        for (DebugLevel dbg : DebugLevel.values()) {
          processor.getLogger().setDebugLevel(dbg, i++ < maxLvl);
        }
      } else {
        ApplicationLogger.log(Level.WARNING,
            "MLSpace...Processor"
                + " debug level paramter value not understood. "
                + "Setting ignored.");
      }

    }
  }

  private static AbstractMLSpaceProcessor<?, ?> createHybridProcessor(
      MLSpaceModel mlsModel,
      final IEventQueue<IMLSpaceEvent<?>, Double> eventQueue,
      final IRandom random, final ISpatialIndex<SpatialEntity> spatialIndex,
      final IPositionUpdater positionUpdater, Integer maxMoveAttempts) {
    AbstractMLSpaceProcessor<?, ?> processor;
    Timer timer = new Timer();
    Collection<Subvol> subvols = mlsModel.getSubvolumes();
    IDisplacementVector steps = SubvolUtils.getCommonSubvolExt(subvols);
    setModelSvInfo(mlsModel, steps);
    IDiscretePositionUpdater discPosUpdater =
        makePositionUpdaterDiscrete(positionUpdater, steps);
    IEventQueue<?, Double> eqToCast = eventQueue;
    // workaround: The hybrid processor is the only one to take events out
    // of the queue, the cont- and NSM-subprocessors only add to it (with
    // their #updateEventQueue methods), hence they can be passed an event
    // queue where the event type is a supertype of the one they actually
    // expect, even when the necessary cast is unsafe in general
    @SuppressWarnings("unchecked")
    AbstractContinuousSpaceProcessor contProc = new ContinuousSpaceProcessor(
        mlsModel, (IEventQueue<ISpatialEntityEvent, Double>) eqToCast, random,
        spatialIndex, discPosUpdater, maxMoveAttempts, timer);
    @SuppressWarnings("unchecked")
    NCMProcessor nsmProc = new NCMProcessor(mlsModel,
        (IEventQueue<NSMEvent, Double>) eqToCast, random, timer);
    processor = new HybridProcessor<>(contProc, nsmProc, NSMEvent.class,
        eventQueue, timer);
    contProc.setLogger(processor.getLogger());
    nsmProc.setLogger(processor.getLogger());
    return processor;
  }

  private static void setModelSvInfo(MLSpaceModel mlsModel,
      IDisplacementVector steps) {
    Map<String, Integer> svNumInfo = new LinkedHashMap<>();
    Map<String, Double> svVolInfo = new LinkedHashMap<>();
    int numSvs = 0;
    for (Map.Entry<SpatialEntity, Collection<Subvol>> e : mlsModel
        .getCompSvMap().entrySet()) {
      Collection<Subvol> svs = e.getValue();
      String spec = e.getKey().getSpecies().toString();
      Integer num = svNumInfo.get(spec);
      if (num == null) {
        num = svs.size();
      } else {
        num += svs.size();
      }
      numSvs += svs.size();
      svNumInfo.put(spec, num);
      Double vol = svVolInfo.get(spec);
      if (vol == null) {
        vol = 0.;
      }
      for (Subvol sv : svs) {
        vol += sv.getVolume();
      }
      svVolInfo.put(spec, vol);
    }
    mlsModel.setInfo("Sv number per parent species", svNumInfo);
    mlsModel.setInfo("Sv volume of per parent species", svVolInfo);
    mlsModel.setInfo("Sv amount", numSvs);
    mlsModel.setInfo("Sv side length", steps);
  }

  @Override
  public double getEfficencyIndex() {
    return EFFICIENCY_INDEX;
  }

  @Override
  public List<Class<?>> getSupportedInterfaces() {
    ArrayList<Class<?>> interfaces = new ArrayList<>();
    interfaces.add(IMLSpaceModel.class);
    return interfaces;
  }

  @Override
  public boolean supportsSubPartitions() {
    return false;
  }

  /**
   * Dummy processor to return for empty models (so that model emptiness and
   * related settings can still be recorded somewhere)
   */
  private static final class DummyProcessor
      extends AbstractMLSpaceProcessor<IMLSpaceEvent<Void>, IEventRecord> {

    /**
     * Constructor to be called only from the processor factory
     * 
     * @param model
     *          Model without spatial or NSM entities
     */
    DummyProcessor(IModel model) {
      super(model, null, null, new Timer());
    }

    private static final long serialVersionUID = -1778380748462214890L;

    @Override
    public IEventRecord handleEvent(IMLSpaceEvent<Void> event) {
      throw new IllegalStateException("Dummy processor is for empty models");
    }

    @Override
    public void updateEventQueue(IMLSpaceEvent<Void> event,
        IEventRecord effect) {
      throw new IllegalStateException("Dummy processor is for empty models");
    }
  }
}
