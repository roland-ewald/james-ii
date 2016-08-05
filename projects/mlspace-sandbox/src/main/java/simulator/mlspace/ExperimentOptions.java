package simulator.mlspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.core.experiments.execonfig.ExecutionConfigurationVariable;
import org.jamesii.core.experiments.execonfig.IParamBlockUpdate;
import org.jamesii.core.experiments.execonfig.ParamBlockUpdateModifier;
import org.jamesii.core.experiments.execonfig.SingularParamBlockUpdate;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.math.geometry.spatialindex.SpatialIndexFactory;
import org.jamesii.core.math.geometry.spatialindex.StaticGridSpatialIndexFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import simulator.mlspace.brownianmotion.ContinuousPositionUpdaterFactory;
import simulator.mlspace.brownianmotion.ContinuousPositionUpdaterFactory.Mode;
import simulator.mlspace.brownianmotion.PositionUpdaterFactory;

class ExperimentOptions {

  /** alias for model vars param (might be added from ini file) */
  public static final String OVERRIDEN_MODEL_VARS = "-override";

  /** ini file processing elsewhere */
  String iniFileName = null;

  @Argument(metaVar = "<file>", usage = "modelfile")
  File modelfile;

  @Option(name = "-h", aliases = "-help", hidden = true,
      usage = "print this message")
  boolean help;

  @Option(name = "-simend", metaVar = "number",
      aliases = { "-simendtime", "-simulationendtime" },
      usage = "simulation end time (default: 100)")
  Double simEndTime;

  @Option(name = "-reactcountstop", metaVar = "expression",
      aliases = { "-reactionCountStopExpression" },
      usage = "stop if sum/difference of reaction count reaches or exceeds threshold, e.g. \"R1-R2>=20\"")
  String reactCountExpr;

  @Option(name = "-out", metaVar = "<path>",
      aliases = { "-outputdir", "-outputdirectory" },
      usage = "specifies output path for csv and possibly image files")
  String out;

  {// static initializer blocks to prevent eclipse from making field final
    out = "";
  }

  @Option(name = "-adddate", metaVar = "boolean",
      aliases = { "-adddatetooutputdirectory" },
      usage = "add simulation start date (and computer name, if possible) to output directory name")
  boolean addDateToOutDir;

  @Option(name = "-csvsep", metaVar = "character",
      aliases = { "-csvSeparator" },
      usage = "separator for csv files (usually comma, semicolon may be preferable on some systems")
  public Character csvSep;

  static final List<String> obsTargetKeys =
      Arrays.asList("obs", "observe", "observationtargets");

  @Option(name = "-obs", aliases = { "-observe", "-observationtargets" },
      usage = "entities to observe (patterns as specified in model)")
  String obsToParse;

  {
    obsToParse = "";
  }

  @Option(name = "-graphics", metaVar = "none/screen/files/both",
      usage = "graphical output type (default none, files if "
          + "-imageres or -screentimes are specified; screen or both are "
          + "not a good idea for multi-parameter-combination experiments)")
  private String graphics;

  @Option(name = "-imgres", aliases = { "-imageres" }, metaVar = "number",
      usage = "graphical output: max width/height in pixels (default 1000)")
  int imagePixels;

  @Option(name = "-fadealpha", aliases = { "-imagefade" }, metaVar = "number",
      usage = "graphical output: alpha for trace to show in images (0: none, 255: full; default: 96 - some fading)")
  int imageFade = -1;

  boolean graphicsOnScreen() {
    return (this.graphics != null && Arrays.asList("screen", "both")
        .contains(this.graphics.toLowerCase()));
  }

  boolean graphicsToFile() {
    return (this.graphics != null
        && Arrays.asList("files", "both").contains(this.graphics))
        || this.imagePixels > 0 || !this.screenshotTimes.isEmpty()
        || (this.screenshotInterval != null
            && !Double.isNaN(screenshotInterval));
  }

  @Option(name = "-seed", aliases = { "-fixedseed" }, metaVar = "number",
      usage = "fixed seed for random number generator (for reproducing runs)")
  Long fixedRandSeed;

  @Option(name = "-rep", aliases = { "-replications" }, metaVar = "number",
      usage = "number of replications for each configuration (replications have priority over parameter combinations. Repeat values of the last overridden parameter to achieve \"combinations first, then replications\" effect")
  int replications = 1;

  private final List<Double> snapshotTimes = new ArrayList<>();

  @Option(name = "-snap", metaVar = "numbers", aliases = { "-snapshottimes" },
      usage = "time points where to take snapshots (oberservation targets and possibly image files)")
  private void addSnapshotTime(final String value) {
    snapshotTimes.addAll(toDouble(value.split(",")));
  }

  @Option(name = "-snapint", metaVar = "number",
      aliases = { "-snapshotinterval" },
      usage = "sim time intervals between snapshots (oberservation targets and possibly image files)")
  private Double snapshotInterval;

  List<Double> getSnapshotTimes() {
    if (snapshotInterval != null && !Double.isNaN(snapshotInterval)) {
      List<Double> prevSnapshotTimes = new ArrayList<>(snapshotTimes);
      for (double d = 0; d <= simEndTime; d += snapshotInterval) {
        if (!prevSnapshotTimes.contains(d)) {
          snapshotTimes.add(d);
        }
      }
    }
    Collections.sort(snapshotTimes);
    snapshotInterval = Double.NaN;
    return snapshotTimes;
  }

  private final List<Double> screenshotTimes = new ArrayList<>();

  @Option(name = "-screentimes", metaVar = "numbers",
      aliases = { "-screenshottimes" },
      usage = "time points for screenshots images")
  private void addScreenshotTime(final String value) {
    screenshotTimes.addAll(toDouble(value.split(",")));
  }

  @Option(name = "-screenint", metaVar = "number",
      aliases = { "-screenshotinterval" },
      usage = "sim time intervals between screenshot images")
  private Double screenshotInterval;

  List<Double> getScreenshotTimes() {
    if (screenshotInterval != null && !Double.isNaN(screenshotInterval)) {
      List<Double> prevScreenshotTimes = new ArrayList<>(screenshotTimes);
      for (double d = 0; d <= simEndTime; d += screenshotInterval) {
        if (!prevScreenshotTimes.contains(d)) {
          screenshotTimes.add(d);
        }
      }
      screenshotInterval = Double.NaN;
    }
    Collections.sort(screenshotTimes);
    return screenshotTimes;
  }

  @Option(name = "-compsize", aliases = { "-complexsizes" },
      metaVar = "boolean",
      usage = "count complex sizes (min/mean/median/max/amount) at snapshot times")
  boolean complexSizes;

  @Option(name = "-par", aliases = { "-parallelthreads" }, metaVar = "number",
      usage = "Number of threads to run in parallel\n"
          + "(-1 for number of present cores (incl. virtual); default: all but one, i.e. -2)")
  int threads;

  {
    threads = -2;
  }

  Level logLevel = Level.WARNING;

  @Option(name = "-lvl", metaVar = "numbers", aliases = { "-loglevel" },
      usage = "log level (ignore log messages of lower priority; default: WARNING)")
  private void setLogLevel(final String value) {
    try {
      logLevel = Level.parse(value);
    } catch (IllegalArgumentException ex) {
      System.err.println("Unknown log level value \"" + value + "\" ignored.");
    }
  }

  int numSimSetups = 1;

  List<ExperimentVariable<?>> expModelVars = new ArrayList<>();

  @Option(name = "-O", aliases = { OVERRIDEN_MODEL_VARS },
      metaVar = "<property>=<values>",
      usage = "override model constant with given values (option may be used multiple times)")
  private void setProperty(final String property) {
    String[] arr = property.split("=");
    if (arr.length != 2) {
      throw new IllegalArgumentException(
          "Properties must be specified in the form:" + "<property>=<values>");
    }
    String[] split = arr[1].split(",");
    List<Double> numList = toDouble(split);
    if (numList.size() == split.length) {
      // numProps.put(arr[0], numList);
      expModelVars.add(
          new ExperimentVariable<>(arr[0], new SequenceModifier<>(numList)));
      numSimSetups *= numList.size();
    } else if (numList.isEmpty()) {
      // stringProps.put(arr[0], Arrays.asList(split));
      expModelVars.add(new ExperimentVariable<>(arr[0],
          new SequenceModifier<>(Arrays.asList(split))));
      numSimSetups *= split.length;
    } else {
      throw new IllegalArgumentException(arr[1] + " value for " + arr[0]
          + " contains both numerical and textual values.");
    }
  }

  @Option(name = "-svsize", metaVar = "number(s)",
      aliases = { "-subvolumesize", "-maxsubvolumesize" },
      usage = "(max) side length of a subvolume (if any dimensionless entities present in model)")
  private void setSvSizes(final String sizes) {
    String[] split = sizes.split(",");
    List<Double> svSizes = toDouble(split);
    if (svSizes.size() < split.length) {
      throw new IllegalArgumentException(
          "svsize contains non-numeric value(s): " + Arrays.toString(split));
    }
    numSimSetups *= svSizes.size();
    expModelVars.add(new ExecutionConfigurationVariable<>("maxSvSideLength",
        ExecutionConfigurationVariable.createParamBlockSequenceModifier(
            new String[] { ProcessorFactory.class.getName(),
                MLSpaceProcessorFactory.MAX_SV_SIDE_LENGTH },
            svSizes.toArray())));
  }

  @Option(name = "-minsvsize", metaVar = "number(s)",
      aliases = { "-minsubvolumesize" }, hidden = true,
      usage = "minimum side length of a subvolume (if any dimensionless entities present in model); avoid using when -svsize (-maxsubvolumesize) is specified")
  private void setMinSvSizes(final String sizes) {
    String[] split = sizes.split(",");
    List<Double> svSizes = toDouble(split);
    if (svSizes.size() < split.length) {
      throw new IllegalArgumentException(
          "svsize contains non-numeric value(s): " + Arrays.toString(split));
    }
    numSimSetups *= svSizes.size();
    expModelVars.add(new ExecutionConfigurationVariable<>("minSvSideLength",
        ExecutionConfigurationVariable.createParamBlockSequenceModifier(
            new String[] { ProcessorFactory.class.getName(),
                MLSpaceProcessorFactory.MIN_SV_SIDE_LENGTH },
            svSizes.toArray())));
  }

  @Option(name = "-sicells", metaVar = "number(s)",
      aliases = { "-spatialindexgridcells" },
      usage = "number of grid cells used for spatial indexing of continuous-space entities (i.e. those with extensions)")
  private void setSICells(final String sizes) {
    String[] split = sizes.split(",");
    List<Double> siCells = toDouble(split);
    if (siCells.size() < split.length) {
      throw new IllegalArgumentException(
          "svsize contains non-numeric value(s): " + Arrays.toString(split));
    }
    numSimSetups *= siCells.size();
    List<IParamBlockUpdate> pbUpdateMods = new ArrayList<>(siCells.size());
    String targetName = ProcessorFactory.class.getName();
    for (Double cells : siCells) {
      ParameterBlock val =
          new ParameterBlock().addSubBl(SpatialIndexFactory.class.getName(),
              new ParameterBlock(cells.intValue(),
                  StaticGridSpatialIndexFactory.NUMBER_OF_GRID_CELLS)
                      .setVal(StaticGridSpatialIndexFactory.class.getName()));
      pbUpdateMods
          .add(new SingularParamBlockUpdate(new String[0], targetName, val));
    }
    expModelVars.add(new ExecutionConfigurationVariable<>("gridCellsInSI",
        new ParamBlockUpdateModifier<>(pbUpdateMods)));
  }

  @Option(name = "-randvec", metaVar = "identifier",
      aliases = { "-randomvectormethod" },
      usage = "INDEPENDENT, SCALED_UNIFORM or POLAR_SPHERICAL (or several of them, separated by comma)")
  private void setRandVec(final String randvecs) {
    List<Mode> modes = new ArrayList<Mode>();
    for (String randvec : randvecs.split(",")) {
      modes.add(Mode.valueOf(randvec));
    }
    numSimSetups *= modes.size();
    expModelVars
        .add(
            new ExecutionConfigurationVariable<>("randomVectorMode",
                ExecutionConfigurationVariable.createParamBlockSequenceModifier(
                    new String[] { ProcessorFactory.class.getName(),
                        PositionUpdaterFactory.class.getName(),
                        ContinuousPositionUpdaterFactory.MODE },
                    modes.toArray())));
  }

  @Option(name = "-step", metaVar = "number(s)",
      aliases = { "-stepsize", "-randomvectormeanlength" },
      usage = "average length of moves in continuous-space mode")
  private void setStepSize(final String steps) {
    String[] split = steps.split(",");
    List<Double> stepSizes = toDouble(split);
    if (stepSizes.size() < split.length) {
      throw new IllegalArgumentException(
          "stepsize contains non-numeric value(s): " + Arrays.toString(split));
    }
    numSimSetups *= stepSizes.size();
    expModelVars
        .add(new ExecutionConfigurationVariable<>("randomVectorMeanLength",
            ExecutionConfigurationVariable.createParamBlockSequenceModifier(
                new String[] { ProcessorFactory.class.getName(),
                    PositionUpdaterFactory.class.getName(),
                    ContinuousPositionUpdaterFactory.TRAVEL_DISTANCE },
                stepSizes.toArray())));
  }

  @Option(name = "-relstep", metaVar = "number(s)",
      aliases = { "-relativestepsize" },
      usage = "average length of moves in continuous-space mode relative to extension (diameter) of smallest model entity")
  private void setRelStepSize(final String steps) {
    String[] split = steps.split(",");
    List<Double> stepSizes = toDouble(split);
    if (stepSizes.size() < split.length) {
      throw new IllegalArgumentException(
          "stepsize contains non-numeric value(s): " + Arrays.toString(split));
    }
    numSimSetups *= stepSizes.size();
    expModelVars
        .add(new ExecutionConfigurationVariable<>("randomVectorMeanLength",
            ExecutionConfigurationVariable.createParamBlockSequenceModifier(
                new String[] { ProcessorFactory.class.getName(),
                    PositionUpdaterFactory.class.getName(),
                    ContinuousPositionUpdaterFactory.TRAVEL_DISTANCE_SCALING },
                stepSizes.toArray())));
  }

  @Option(name = "-attempts", metaVar = "number(s)",
      aliases = { "-maxMoveAttempts" },
      usage = "max number of position update attempts for each move event (i.e. retries in case of unresolveable collision + 1)")
  private void setMoveAttempts(final String attempts) {
    String[] split = attempts.split(",");
    List<Integer> numAttempts = toInt(split);
    if (numAttempts.size() < split.length) {
      throw new IllegalArgumentException(
          "move attempts setting contains non-integer value(s): "
              + Arrays.toString(split));
    }
    numSimSetups *= numAttempts.size();
    expModelVars.add(new ExecutionConfigurationVariable<>("randomVectorMode",
        ExecutionConfigurationVariable.createParamBlockSequenceModifier(
            new String[] { ProcessorFactory.class.getName(),
                MLSpaceProcessorFactory.MAX_MOVE_ATTEMPTS },
            numAttempts.toArray())));
  }

  @Option(name = "-effdiff", metaVar = "number",
      aliases = { "-effectiveDiffusionPast" }, hidden = true,
      usage = "attempt calculation of effective diffusion (using given number of recent snapshots)")
  int effDiffPast;

  /**
   * Auxiliary method to extract numerical values from strings
   *
   * @param split
   *          String array (e.g. from splitting comma-separated list)
   * @return List of numbers specified by strings (skips non-numbers)
   */
  private static List<Double> toDouble(String[] split) {
    List<Double> rv = new ArrayList<>(split.length);
    for (String str : split) {
      try {
        rv.add(Double.parseDouble(str));
      } catch (NumberFormatException ex) {
        /* continue */
      }
    }
    return rv;
  }

  /**
   * Auxiliary method to extract integer values from strings
   *
   * @param split
   *          String array (e.g. from splitting comma-separated list)
   * @return List of numbers specified by strings (skips non-numbers)
   */
  private static List<Integer> toInt(String[] split) {
    List<Integer> rv = new ArrayList<>(split.length);
    for (String str : split) {
      try {
        rv.add(Integer.parseInt(str));
      } catch (NumberFormatException ex) {
        /* continue */
      }
    }
    return rv;
  }

  @Option(name = "-oldsyntax", aliases = "-oldsyntaxfallback",
      usage = "try parsing model in old syntax (2014Q3 and earlier) if new parser produces syntax errors")
  boolean oldSyntax;

  @Option(name = "-sumonly", metaVar = "boolean", aliases = { "-summaryonly" },
      usage = "do not write csv file for each simulation run, only one summary.csv")
  public boolean summaryOnly;

  @Option(name = "-earlystoponly",
      usage = "shorter experiment summary including only experiments not stopped by sim time")
  public boolean stopSummary;

  @Option(name = "-rng",
      metaVar = "MT19937,WELL512a,WELL1024a,WELL19937a,WELL44497a,Secure (Java),RANDU (discouraged)",
      usage = "Random number generator to use")
  public String rng = null;

}