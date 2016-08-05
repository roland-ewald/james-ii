/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.instrumentation;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IInfoMapProvider;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.observe.comparing.ComparingObserverStopPolicyFactory;
import org.jamesii.core.observe.comparing.EqualityObserver;
import org.jamesii.core.observe.comparing.EqualityObserver.InformationExtractor;
import org.jamesii.core.observe.csv.CSVUtils;
import org.jamesii.core.observe.csv.CSVUtils.CSVWriter;
import org.jamesii.core.observe.csv.SnapshotCSVObserver;
import org.jamesii.core.observe.snapshot.FollowUpWrapperSnapshotPolicy;
import org.jamesii.core.observe.snapshot.ISnapshotPolicy;
import org.jamesii.core.observe.snapshot.TimeHintSnapshotPolicy;
import org.jamesii.core.observe.snapshot.TimeSnapshotPolicy;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.util.ITime;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.logging.ApplicationLogger;

import examples.mlspace.ExpUtils;
import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.RuleEntity;
import model.mlspace.reader.MLSpaceModelReader;
import model.mlspace.writer.MLSpaceModelFileWriterFactory;
import model.mlspace.writer.ModelFileWriter;
import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.observation.EventRecordAndLegendObserver;
import simulator.mlspace.observation.EventRecordObserver;
import simulator.mlspace.observation.ModelWritingObserver;
import simulator.mlspace.observation.ReactionObserver;
import simulator.mlspace.observation.graphic.GraphicalObserverFactory;
import simulator.mlspace.observation.graphic.GraphicalOutputObserver;
import simulator.mlspace.observation.snapshot.ComplexChainSnapshotPlugin;
import simulator.mlspace.observation.snapshot.EffectiveDiffusionObserver;
import simulator.mlspace.observation.snapshot.MatchingSnapshotPlugin;
import simulator.mlspace.observation.snapshot.SnapshotCSVObserverWithInfo;

/**
 * Instrumenter for MLSpace simulation observer(s) (
 * {@link org.jamesii.core.observe.csv.SnapshotCSVObserver} with plugins and
 * {@link simulator.mlspace.observation.AbstractEffectObserver}s).
 * 
 * @author Arne Bittig
 * 
 */
public class MLSpaceInstrumenter implements IComputationInstrumenter {

  private static final long serialVersionUID = 2509754520894473261L;

  private static final char DEFAULT_CSV_SEP = ',';

  // private static final char CSV_SEP_ALT = ';'; // TODO: attempt to read from
  // environment / win registry?
  /** The parameter block setting "csv separator" id */
  public static final String CSV_SEP = "csvSeparator";

  /** The parameter block setting "snapshot times" id */
  public static final String SNAPSHOT_TIMES = "SnapshotTimes";

  /** The parameter block setting "flag whether to count no-react events" id */
  public static final String REACTION_COUNT_INCL_NONE = "ReactionCountInclNone";

  /** The parameter block setting "reaction count output to file" id */
  public static final String REACTION_TIME_FILENAME = "ReactionCountFilename";

  /** The parameter block setting "number of followups after shapshot" id */
  public static final String FOLLOW_UPS = "FollowUps";

  /** The parameter block setting "file path and name" id */
  public static final String FILE_NAME_PREFIX = "FileNamePrefix";

  /** The parameter block setting "create a subdirectory for each run" flag */
  public static final String SUBDIR_PER_RUN = "SubdirPerRun";

  /** The parameter block setting "formatter for floating-point numbers" id */
  public static final String DECIMAL_FORMAT = "DecimalFormat";

  /** The parameter block setting "effective diffusion interval" id */
  public static final String EFFECTIVE_DIFFUSION_PAST =
      "EffectiveDiffusionPastSnapshotComparisonNumber";

  /**
   * The parameter block setting for when to
   * "write model (in its current state) to file"
   */
  public static final String MODEL_TO_FILE_TIMES = "ModelToFileTimes";

  /**
   * Parameter block identifier for supression of writing to csv file (e.g. for
   * using trajectory-generation observer only for that purpose)
   */
  // TODO: individually for each?!
  public static final String SUPRESS_CSV_SNAPSHOT_OUTPUT = "SupressOutput";

  /** Parameter block identifier for "record complexes" flag */
  public static final String COMPLEXES = "ComplexesAndChains";

  private final List<IObserver<?>> observers = new ArrayList<>();

  private final ParameterBlock params;

  /**
   * snapshot plugins
   */
  private final List<SnapshotCSVObserver.SnapshotPlugin<AbstractMLSpaceProcessor<?, ?>>> snapshotPlugins =
      new ArrayList<>();

  /**
   * @param params
   */
  public MLSpaceInstrumenter(ParameterBlock params) {
    this.params = params;
  }

  @Override
  public List<? extends IObserver<?>> getInstantiatedObservers() {
    return observers;
  }

  @Override
  public void instrumentComputation(IComputationTask simulation) {

    IProcessor<Double> proc = simulation.getProcessorInfo().getLocal();
    Mediator.create(proc);

    String filePathAndPrefix = getFilePathAndPrefix(simulation, params);
    String filePAPForCSV =
        params.getSubBlockValue(MLSpaceInstrumenter.SUPRESS_CSV_SNAPSHOT_OUTPUT,
            false) ? null : filePathAndPrefix;
    CSVUtils.CSVWriter csvWriter = getCSVWriter(params);

    Iterable<Double> snapshotTimes = getSnaphshotTimes(params);
    Integer followUps = params.getSubBlockValue(FOLLOW_UPS);

    addMatchingObserverPlugin(proc);
    addComplexSnapshotPlugin();

    addEventRecordObserver(proc, filePathAndPrefix);
    addEffectiveDiffusionObserver(proc);
    addReactCountObserver(proc, filePathAndPrefix);

    addGraphicObserver(proc, filePathAndPrefix);

    addComparingObserver(proc, simulation);
    addModelToFileObserver(proc, filePathAndPrefix);

    // handleStopPolicy(simulation);

    if (!snapshotPlugins.isEmpty()) {
      if (snapshotTimes == null) {
        if (snapshotPlugins.size() > 1
            || !(snapshotPlugins.get(0) instanceof ReactionObserver)) {
          throw new IllegalStateException(
              "Snapshot observer plugins defined but no snapshot times!");
        }
      } else {
        addSnapshotObserver(proc, filePAPForCSV + "-snapshots.csv", csvWriter,
            snapshotTimes, followUps == null ? 0 : followUps);
      }
    }
  }

  private CSVUtils.CSVWriter getCSVWriter(ParameterBlock params) {
    NumberFormat nf = ParameterBlocks.getSubBlockValue(params, DECIMAL_FORMAT);
    char csvSep = params.getSubBlockValue(CSV_SEP, DEFAULT_CSV_SEP);
    if (nf == null) {
      nf = NumberFormat.getNumberInstance(Locale.getDefault());
      nf.setMinimumFractionDigits(4); // NOSONAR: clear from context
      nf.setMaximumFractionDigits(6); // NOSONAR: clear from context
      // nf.setGroupingUsed(false);
      // if (nf instanceof DecimalFormat && ((DecimalFormat)
      // nf).getDecimalFormatSymbols().getDecimalSeparator()==csvSep) {
      // csvSep = CSV_SEP_ALT;
      // }
    }
    return new CSVUtils.CSVWriter(csvSep, nf);
  }

  /**
   * Register observer with the processor and this instrumenter
   * 
   * @param obs
   * @param proc
   */
  private void addObserver(IObserver<? extends IProcessor<?>> obs,
      IProcessor<?> proc) {
    proc.registerObserver(obs);
    observers.add(obs);
  }

  private void addMatchingObserverPlugin(IProcessor<Double> proc) {
    Map<List<? extends RuleEntity>, List<String>> toObserve =
        getObsTarget(proc);
    if (toObserve == null) {
      return;
    }
    snapshotPlugins.add(new MatchingSnapshotPlugin(toObserve));
  }

  private Map<List<? extends RuleEntity>, List<String>> getObsTarget(
      IProcessor<Double> proc) {
    Map<List<? extends RuleEntity>, List<String>> toObserve = null;
    Object moObsObj =
        params.getSubBlockValue(MatchingSnapshotPlugin.TO_OBSERVE);
    if (moObsObj instanceof Map) {
      toObserve = new LinkedHashMap<>(
          (Map<List<? extends RuleEntity>, List<String>>) moObsObj);
    } else if (moObsObj != null) {
      ApplicationLogger.log(Level.SEVERE, "Observation target for matching "
          + "observer ill defined: " + moObsObj + ". Skipped.");
    }
    IModel model = proc.getModel();
    if (model instanceof IInfoMapProvider<?>) {
      Map<String, ?> infoMap = ((IInfoMapProvider<?>) model).getInfoMap();
      if (infoMap == null) {
        return toObserve;
      }
      Object ot = infoMap.get(MLSpaceModelReader.OBSERVABLES_PARSED);
      if (ot instanceof Map<?, ?>) {
        toObserve = (Map<List<? extends RuleEntity>, List<String>>) ot;
      }
    }
    return toObserve;
  }

  private void addGraphicObserver(IProcessor<Double> mlSpaceSim,
      String filePathAndPrefix) {
    GraphicalObserverFactory gof =
        params.getSubBlockValue(GraphicalObserverFactory.class.getName());
    if (gof != null) {
      GraphicalOutputObserver<?> obs =
          gof.create((IMLSpaceModel) mlSpaceSim.getModel(), filePathAndPrefix);
      addObserver(obs, mlSpaceSim);
    }
  }

  private void addEventRecordObserver(IProcessor<Double> mlSpaceSim,
      String filePathAndPrefix) {
    Boolean skipDiffAttr =
        params.getSubBlockValue(EventRecordObserver.SKIP_DIFFUSION_IN_SV_STATE);
    Integer flushThreshold =
        params.<Integer> getSubBlockValue(EventRecordObserver.FLUSH_THRESHOLD);
    boolean simpleEROPropsUndefined =
        skipDiffAttr == null && flushThreshold == null
            && !params.hasSubBlock(EventRecordObserver.class.getSimpleName());
    boolean addLegend = params
        .hasSubBlock(EventRecordAndLegendObserver.RECORD_EVENTS_WITH_LEGEND)
        || params
            .hasSubBlock(EventRecordAndLegendObserver.FINAL_STATE_FILE_SUFFIX);
    if (simpleEROPropsUndefined && !addLegend) {
      return;
    }
    String filenameForRecords = filePathAndPrefix + "-records.csv";

    if (skipDiffAttr == null) {
      skipDiffAttr = false;
    }
    EventRecordObserver recObs;
    if (addLegend) {
      String finalStateFileSuffix = params.getSubBlockValue(
          EventRecordAndLegendObserver.FINAL_STATE_FILE_SUFFIX);
      String filenameFinalState;
      if (finalStateFileSuffix == null) {
        filenameFinalState = filenameForRecords;
      } else {
        if (!finalStateFileSuffix.endsWith(".csv")) {
          finalStateFileSuffix += ".csv";
        }
        filenameFinalState = filePathAndPrefix + finalStateFileSuffix;
      }
      recObs = new EventRecordAndLegendObserver(filenameForRecords,
          filenameFinalState, filePathAndPrefix + "-legend.csv", flushThreshold,
          skipDiffAttr);
    } else {
      recObs = new EventRecordObserver(filenameForRecords, flushThreshold,
          skipDiffAttr);
    }
    addObserver(recObs, mlSpaceSim);
  }

  private void addEffectiveDiffusionObserver(IProcessor<Double> proc) {
    if (params.hasSubBlock(EFFECTIVE_DIFFUSION_PAST)) {
      // TODO: actual param value
      EffectiveDiffusionObserver obs = new EffectiveDiffusionObserver(
          (Integer) params.getSubBlockValue(EFFECTIVE_DIFFUSION_PAST));
      snapshotPlugins.add(obs);
      addObserver(obs, proc);
    }
  }

  /**
   * @param proc
   * @param filePathAndPrefix
   */
  private void addReactCountObserver(IProcessor<Double> proc,
      String filePathAndPrefix) {
    if (// params.hasSubBlock(REACTION_COUNT_MESSAGE_TIME_STEPS) ||
    params.hasSubBlock(REACTION_COUNT_INCL_NONE)
        || params.hasSubBlock(REACTION_TIME_FILENAME)) {
      boolean inclNone =
          params.getSubBlockValue(REACTION_COUNT_INCL_NONE, false);
      // double sysoutInterval =
      // params.getSubBlockValue(REACTION_COUNT_MESSAGE_TIME_STEPS,
      // Double.POSITIVE_INFINITY);
      String filename = params.getSubBlockValue(REACTION_TIME_FILENAME);
      if (filename != null) {
        filename = filePathAndPrefix + filename;
      }
      ReactionObserver rcObs = new ReactionObserver(inclNone, true, filename);
      // , sysoutInterval);
      addObserver(rcObs, proc); // must observe at every time step
      snapshotPlugins.add(rcObs); // output should be recorded only at some..
    }
  }

  private void addComparingObserver(IProcessor<Double> proc,
      IComputationTask simulation) {
    Boolean compare = params.getSubBlockValue(
        ComparingObserverStopPolicyFactory.COMPARE_EXP_OR_CONFIGS);
    InformationExtractor<? extends IObservable, ?> infoEx = params
        .getSubBlockValue(ComparingObserverStopPolicyFactory.INFO_EXTRACTOR);
    if (compare != null) {
      IUniqueID id = compare ? simulation.getConfig().getExperimentID()
          : simulation.getConfig().getConfigurationID();
      EqualityObserver<? extends IProcessor<?>> compObs =
          (EqualityObserver<? extends IProcessor<?>>) EqualityObserver
              .getInstanceFor(id, infoEx);
      compObs.setLoggingProperties(true, 100000);
      addObserver(compObs, proc);
    }
  }

  private void addModelToFileObserver(IProcessor<Double> proc,
      String filePathAndPrefix) {
    if (!params.hasSubBlock(MODEL_TO_FILE_TIMES)) {
      return;
    }
    Iterable<Double> toFileTimes = params.getSubBlockValue(MODEL_TO_FILE_TIMES);
    ModelFileWriter<IMLSpaceModel> mfw =
        new MLSpaceModelFileWriterFactory().create(null, null);
    ModelWritingObserver mtfObs =
        new ModelWritingObserver(toFileTimes, mfw, filePathAndPrefix);
    addObserver(mtfObs, proc);
  }

  /**
   */
  private void addComplexSnapshotPlugin() {
    if (params.hasSubBlock(COMPLEXES)) {
      snapshotPlugins.add(new ComplexChainSnapshotPlugin());
    }
  }

  // /**
  // * Test whether stop policy needs to observe/listen to the simulation,
  // perform
  // * the necessary setup operations -- commented out while SteadyStateSP has
  // been moved to a different package
  // *
  // * @param simulation
  // */
  // private void handleStopPolicy(IComputationTask simulation) {
  // IComputationTaskStopPolicy<?> stopPol =
  // ((ISimulationRun) simulation).getStopPolicy();
  // if (stopPol instanceof SteadyStateStopPolicy) {
  // SteadyStateStopPolicyFactory.connectWithTrajectoryProvider(
  // (SteadyStateStopPolicy) stopPol, observers);
  // } else if (stopPol instanceof CompositeCompTaskStopPolicy) {
  // for (IComputationTaskStopPolicy<?> singlePol :
  // ((CompositeCompTaskStopPolicy<IComputationTask>) stopPol)
  // .getSimRunStopPolicies()) {
  // if (singlePol instanceof SteadyStateStopPolicy) {
  // SteadyStateStopPolicyFactory.connectWithTrajectoryProvider(
  // (SteadyStateStopPolicy) singlePol, observers);
  // }
  // }
  // }
  // }

  /**
   * Add part of the unique ID to the output file name, if present (otherwise
   * set output file name to part of the unique ID)
   * 
   * @param simulation
   * @param params
   */
  private static String getFilePathAndPrefix(IComputationTask simulation,
      ParameterBlock params) {
    String idStr = simulation.getUniqueIdentifier().toString();
    String fn = params.getSubBlockValue(FILE_NAME_PREFIX, "");
    final int finalIDPartSize = 9;
    simulation.getConfig();
    String[] substrings =
        idStr.substring(idStr.length() - finalIDPartSize).split("-");
    String idSubStr = substrings[1] + '-' + substrings[0];
    if (params.getSubBlockValue(SUBDIR_PER_RUN, false)) {
      Map<String, ?> parCopy =
          new LinkedHashMap<>(simulation.getConfig().getParameters());
      parCopy.remove(MLSpaceModelReader.OBSERVABLES_TO_PARSE);
      String path = fn + idSubStr + '_' + mapToString(parCopy, "-", "_", true);
      ExpUtils.createDirIfNotPresent(path);
      return path + '/' + simulation.getModel().getName() + idSubStr;

    }
    return fn + simulation.getModel().getName() + "-" + idSubStr;
  }

  /**
   * String representation of map entries without surrounding brackets or
   * anything else, with custom separator between key and value and between
   * entries
   * 
   * @param map
   *          Map to represent as string
   * @param eqRep
   *          separator between keys and values (equal sign in default toString)
   * @param commaRep
   *          separator between entries (comma and space in default toString)
   * @param shortenKeys
   *          Flag whether to use only first letter and caps of map keys for
   *          shorter strings
   * @return String representation of map
   */
  private static String mapToString(Map<String, ?> map, String eqRep,
      String commaRep, boolean shortenKeys) {
    if (map.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (Entry<String, ?> e : map.entrySet()) {
      if (shortenKeys) {
        appendShortenedString(sb, e.getKey());
      } else {
        sb.append(e.getKey());
      }
      sb.append(eqRep);
      Object val = e.getValue();
      if (val instanceof Number) {
        Number n = (Number) val;
        if (n.doubleValue() - n.longValue() == 0) {
          sb.append(n.longValue());
        } else {
          sb.append(n);
        }
      } else {
        sb.append(val);
      }
      sb.append(commaRep);
    }
    return sb.substring(0, sb.length() - commaRep.length());
  }

  private static void appendShortenedString(StringBuilder sb, String str) {
    boolean first = true;
    boolean second = false;
    boolean lastLowerCase = false;
    for (char c : str.toCharArray()) {
      boolean thisLowerCase = !Character.isUpperCase(c);
      if (first || second || lastLowerCase && !thisLowerCase) {
        sb.append(c);
        first = false;
        second = !second;
      }
      lastLowerCase = thisLowerCase;
    }
  }

  /**
   * @param params
   *          parameter block from which to get number format
   * @return nf or default decimal format if not in params
   */
  public static NumberFormat getDecimalFormat(ParameterBlock params) {
    NumberFormat nf = ParameterBlocks.getSubBlockValue(params, DECIMAL_FORMAT);
    if (nf == null) {
      nf = NumberFormat.getNumberInstance(Locale.getDefault());
      nf.setMinimumFractionDigits(4); // NOSONAR: clear from context
      nf.setMaximumFractionDigits(6); // NOSONAR: clear from context
      nf.setGroupingUsed(false);
    }
    return nf;
  }

  /**
   * Get snapshot times from parameter block, if defined. Returns null if the
   * given parameter block does not have a sub-block {@value #SNAPSHOT_TIMES},
   * otherwise the time values defined there (as a collection of numbers or a
   * double array) as a {@link List} of {@link Double}s.
   * 
   * @param params
   *          Parameter block
   * @return Snapshot times, null if relevant sub-block not present
   */
  @SuppressWarnings("unchecked")
  private static Iterable<Double> getSnaphshotTimes(ParameterBlock params) {
    if (!params.hasSubBlock(SNAPSHOT_TIMES)) {
      return null;
    }
    Object sbv = params.getSubBlockValue(SNAPSHOT_TIMES);
    if (sbv instanceof Iterable) {
      return (Iterable<Double>) sbv;
    } else if (sbv instanceof double[]) {
      List<Double> snapshotTimes = new ArrayList<>();
      for (double d : (double[]) sbv) {
        snapshotTimes.add(d);
      }
      return snapshotTimes;
    } else {
      throw new IllegalArgumentException("Parameter subblock " + SNAPSHOT_TIMES
          + " does not have a valid value");
    }
  }

  private void addSnapshotObserver(IProcessor<Double> proc,
      String filePAPForCSV, CSVWriter csvWriter, Iterable<Double> snapshotTimes,
      int followUps) {
    SnapshotCSVObserverWithInfo<AbstractMLSpaceProcessor<?, ?>, Double> obs =
        new SnapshotCSVObserverWithInfo<>(
            createSnapshotPolicy(proc, snapshotTimes, followUps),
            snapshotPlugins, filePAPForCSV, csvWriter);
    addObserver(obs, proc);
  }

  /**
   * @param snapshotTimes
   * @param followUps
   * @return
   */
  private static <T extends Comparable<T>> ISnapshotPolicy<T> createSnapshotPolicy(
      ITime<T> timeProvider, Iterable<T> snapshotTimes, int followUps) {
    TimeSnapshotPolicy<T> sp =
        new TimeHintSnapshotPolicy<>(timeProvider, snapshotTimes);
    if (followUps <= 0) {
      return sp;
    }
    return new FollowUpWrapperSnapshotPolicy<>(sp, followUps);
  }
}