/* The general modelling and simulation framework JAMES II. Copyright by the
 * University of Rostock.
 *
 * LICENCE: JAMESLIC */
package simulator.mlspace;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.ini4j.InvalidFileFormatException;
import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.math.random.generators.java.JavaRandomGeneratorFactory;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwisterGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.math.random.generators.randu.RANDUGeneratorFactory;
import org.jamesii.core.math.random.generators.securerandom.SecureRandomGeneratorFactory;
import org.jamesii.core.math.random.rnggenerator.pre.PreDefinedSeedsRandGenerator;
import org.jamesii.core.observe.listener.ExperimentStopListener;
import org.jamesii.core.observe.listener.ExperimentSummaryListener;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.simulationrun.stoppolicy.CompositeCompTaskStopPolicyFactory;
import org.jamesii.core.simulationrun.stoppolicy.DisjunctiveSimRunStopPolicyFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStop;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.util.Hook;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;
import org.kohsuke.args4j.CmdLineException;

import examples.mlspace.ExpUtils;
//import james.misc.random.generators.well.WELL1024aGeneratorFactory;
//import james.misc.random.generators.well.WELL19937aGeneratorFactory;
//import james.misc.random.generators.well.WELL44497aGeneratorFactory;
//import james.misc.random.generators.well.WELL512aGeneratorFactory;
import model.mlspace.reader.MLSpaceModelReader;
import model.mlspace.reader.MLSpaceModelReaderFactory;
import simulator.mlspace.instrumentation.MLSpaceInstrumenter;
import simulator.mlspace.instrumentation.MLSpaceInstrumenterFactory;
import simulator.mlspace.observation.ReactionCountStopFactory;
import simulator.mlspace.observation.graphic.DefaultColorProvider;
import simulator.mlspace.util.MLSpaceLogger.DebugLevel;

/**
 * Experiment file getting info from main args
 *
 * @author Arne Bittig
 */
public final class CmdLineExperiment {

  private static final Double DEFAULT_SIM_END_TIME = 100.;

  private CmdLineExperiment() {}

  /**
   * @param args
   *          Main method args incl. model file & sim, obs & output spec
   * @throws CmdLineException
   * @throws URISyntaxException
   * @throws IOException
   * @throws InvalidFileFormatException
   */
  public static void main(String[] args) throws CmdLineException,
      URISyntaxException, InvalidFileFormatException, IOException {
    final ExperimentOptions options = ExperimentOptionsHelper.getOptions(args);

    BaseExperiment exp = new BaseExperiment();

    exp.setModelLocation(new URI("file-mls:///"
        + options.modelfile.getAbsolutePath().replace('\\', '/')));
    if (options.oldSyntax) {
      exp.setModelRWParameters(new ParameterBlock(true,
          MLSpaceModelReaderFactory.OLD_SYNTAX_FALLBACK));
    }

    String outputdir = prepareOutputDirectory(options);

    setupObservation(exp, outputdir, options);
    setupProcessor(exp, outputdir, options);
    exp.setRepeatRuns(options.replications);
    exp.setupVariables(options.expModelVars);

    ApplicationLogger.log(Level.INFO,
        options.numSimSetups * options.replications
            + " simulation runs to be executed.\n");
    if (!ApplicationLogger.getLogLevel().equals(options.logLevel)) {
      // following call prints message,previous check to avoid message if
      // current level is already the desired one
      ApplicationLogger.setLogLevel(options.logLevel);
    }

    // dummy call to not have init messages in log
    SimSystem.getRegistry();
    String fileName = outputdir + exp.getUniqueIdentifier();
    PrintStream oldOut = System.out;
    PrintStream outLog = null;
    PrintStream errLog = null;
    if (!outputdir.isEmpty()) {
      System.out.println("Redirecting log and error messages to " + fileName
          + ".log and -err.log");
      errLog = new PrintStream(fileName + "-err.log");
      System.setErr(errLog);
      outLog = new PrintStream(fileName + ".log");
      System.setOut(outLog);
    }
    exp.execute();
    if (errLog != null) {
      errLog.close();
    }
    if (outLog != null) {
      outLog.close();
      System.setOut(oldOut);
    }
    System.out.println("Execution finished.");
  }

  private static String prepareOutputDirectory(
      final ExperimentOptions options) {
    String outputdir = options.out;
    if (outputdir.contains("\\")) {
      outputdir = outputdir.replace('\\', '/');
    }
    if (options.addDateToOutDir) {
      outputdir = ExpUtils.addComputerAndDate(outputdir);
    } else if (!outputdir.isEmpty() && !outputdir.endsWith("/")) {
      outputdir += '/';
    }
    ExpUtils.createDirIfNotPresent(outputdir);
    try {
      ExpUtils.copyFile(options.modelfile.getAbsolutePath(), outputdir);
      if (options.iniFileName != null) {
        ExpUtils.copyFile(options.iniFileName, outputdir);
      }
    } catch (IOException e) {
      System.err.println("Model (or ini) file not copied due to " + e);
    }
    return outputdir;
  }

  private static void setupObservation(BaseExperiment exp, String outputdir,
      final ExperimentOptions options) {
    ParameterBlock pbObs = new ParameterBlock();
    pbObs.addSubBlock(MLSpaceInstrumenter.FILE_NAME_PREFIX, outputdir);
    setupSnapshotObservation(exp, options, pbObs);
    setupGraphicsObservation(options, pbObs);
    if (options.numSimSetups > 1) {
      pbObs.addSubBlock(MLSpaceInstrumenter.SUBDIR_PER_RUN,
          options.graphicsToFile());
    }
    if (options.csvSep != null) {
      pbObs.addSubBlock(MLSpaceInstrumenter.CSV_SEP, options.csvSep);
    }
    if (options.effDiffPast > 0) {
      pbObs.addSubBl(MLSpaceInstrumenter.EFFECTIVE_DIFFUSION_PAST,
          options.effDiffPast);
    }
    if (options.summaryOnly) {
      pbObs.addSubBlock(MLSpaceInstrumenter.SUPRESS_CSV_SNAPSHOT_OUTPUT, true);
    }

    if (options.reactCountExpr != null) {
      pbObs.addSubBlock(MLSpaceInstrumenter.REACTION_COUNT_INCL_NONE, false);
    }

    exp.setComputationInstrumenterFactory(
        new ParameterizedFactory<ComputationInstrumenterFactory>(
            new MLSpaceInstrumenterFactory(), pbObs));
  }

  private static void setupSnapshotObservation(BaseExperiment exp,
      final ExperimentOptions options, ParameterBlock pbObs) {
    String obsTargetsToParse = options.obsToParse;
    if (obsTargetsToParse != null && !obsTargetsToParse.isEmpty()) {
      pbObs.addSubBlock(MLSpaceInstrumenter.SNAPSHOT_TIMES,
          options.getSnapshotTimes());
      pbObs.addSubBlock(MLSpaceInstrumenter.FOLLOW_UPS, 0);
      pbObs.addSubBlock(MLSpaceInstrumenter.SUPRESS_CSV_SNAPSHOT_OUTPUT, false);
      obsTargetsToParse = ExperimentOptionsHelper.dequote(obsTargetsToParse);
      exp.setFixedModelParameters(Collections.<String, Object> singletonMap(
          MLSpaceModelReader.OBSERVABLES_TO_PARSE, obsTargetsToParse));
    }
    if (!options.getSnapshotTimes().isEmpty()) {
      pbObs.addSubBlock(MLSpaceInstrumenter.REACTION_COUNT_INCL_NONE, true);
    }
    if (options.complexSizes) {
      pbObs.addSubBlock(MLSpaceInstrumenter.COMPLEXES, true);
    }
  }

  private static void setupGraphicsObservation(final ExperimentOptions options,
      ParameterBlock pbObs) {
    List<Double> screenshotTimes = options.getScreenshotTimes();
    boolean graphicsToFile = options.graphicsToFile();
    if (options.graphicsOnScreen() || graphicsToFile) {
      if (screenshotTimes.isEmpty() && graphicsToFile) {
        screenshotTimes = options.getSnapshotTimes();
      }
      int[] gop;
      if (options.imagePixels > 0) {
        if (options.imageFade >= 0) {
          gop = new int[] { options.imagePixels, options.imagePixels,
              options.imageFade };
        } else {
          gop = new int[] { options.imagePixels, options.imagePixels };
        }
      } else if (options.imageFade >= 0) {
        gop = new int[] { options.imageFade };
      } else {
        gop = new int[] {};
      }
      ExpUtils.addGraphicalOutput(pbObs, options.graphicsOnScreen(),
          graphicsToFile ? screenshotTimes : null, new DefaultColorProvider(),
          gop, new int[] { 1, 1 }, false, 10);
    }
  }

  private static void setupProcessor(BaseExperiment exp, String outputdir,
      final ExperimentOptions options) {
    if (options.numSimSetups > 1 || options.replications > 1) {
      exp.getExecutionController()
          .addExecutionListener(options.stopSummary
              ? new ExperimentStopListener(outputdir + ".csv", true,
                  SimTimeStop.class)
              : new ExperimentSummaryListener(outputdir + ".csv", true));
      exp.setTaskRunnerFactory(new ParameterizedFactory<TaskRunnerFactory>(
          new ParallelComputationTaskRunnerFactory(),
          new ParameterBlock(options.threads,
              ParallelComputationTaskRunnerFactory.NUM_CORES)));
    }

    ParameterBlock params = new ParameterBlock(
        new ArrayList<>(Arrays.asList(
            // DebugLevel.APPLIED_RULE_INFO,
            // DebugLevel.PROCESSED_SV_EVENT_INFO,
            DebugLevel.BASIC_START_AND_END_INFO)),
        MLSpaceProcessorFactory.DEBUG_LEVEL);

    Long fixedRandSeed = options.fixedRandSeed;
    String rng = options.rng;
    setupRNG(fixedRandSeed, rng);

    exp.setProcessorFactoryParameters(params);

    ParameterizedFactory<ComputationTaskStopPolicyFactory<?>> simTimeStopFac =
        null;
    ParameterizedFactory<ComputationTaskStopPolicyFactory<?>> reactCountStopFac =
        null;
    if (options.simEndTime == null && options.reactCountExpr == null) {
      simTimeStopFac =
          new ParameterizedFactory<ComputationTaskStopPolicyFactory<?>>(
              new SimTimeStopFactory(), new ParameterBlock(DEFAULT_SIM_END_TIME,
                  SimTimeStopFactory.SIMEND));
    }
    if (options.simEndTime != null) {
      simTimeStopFac =
          new ParameterizedFactory<ComputationTaskStopPolicyFactory<?>>(
              new SimTimeStopFactory(), new ParameterBlock(options.simEndTime,
                  SimTimeStopFactory.SIMEND));
    }
    if (options.reactCountExpr == null) {
      exp.setComputationTaskStopPolicyFactory(simTimeStopFac);
    } else {
      String[] split = options.reactCountExpr.split(">=");
      reactCountStopFac =
          new ParameterizedFactory<ComputationTaskStopPolicyFactory<?>>(
              new ReactionCountStopFactory(),
              new ParameterBlock(Integer.valueOf(split[1]),
                  ReactionCountStopFactory.REACTION_COUNT_THRESHOLD).addSubBl(
                      ReactionCountStopFactory.REACTION_COUNT_EXPRESSION,
                      split[0]));

      if (simTimeStopFac == null) {
        exp.setComputationTaskStopPolicyFactory(reactCountStopFac);
      } else {
        List<Pair<ComputationTaskStopPolicyFactory<?>, ParameterBlock>> stopPolList =
            CompositeCompTaskStopPolicyFactory.listOfParFacsToListOfPairs(
                Arrays.asList(simTimeStopFac, reactCountStopFac));

        exp.setComputationTaskStopPolicyFactory(
            new ParameterizedFactory<ComputationTaskStopPolicyFactory<?>>(
                new DisjunctiveSimRunStopPolicyFactory(),
                new ParameterBlock(stopPolList,
                    CompositeCompTaskStopPolicyFactory.POLICY_FACTORY_LIST)));
      }
    }

    exp.setBackupEnabled(false);
  }

  private static void setupRNG(Long fixedRandSeed, String rng) {
    if (fixedRandSeed != null) {
      final Long[] storedSeed = new Long[] { fixedRandSeed };
      SimSystem.setRandSeedGenerator(new PreDefinedSeedsRandGenerator(
          Arrays.<Long> asList(), new Hook<PreDefinedSeedsRandGenerator>() {

            private static final long serialVersionUID = -23L;

            @Override
            protected void executeHook(PreDefinedSeedsRandGenerator rngg) {
              rngg.setSeeds(Arrays.asList(storedSeed));
            }
          }));
    }

    if (rng != null) {
      RandomGeneratorFactory rngFac = null;
      switch (rng.toUpperCase()) {
      case "MT":
      case "MERSENNE":
      case "MT19937":
        rngFac = new MersenneTwisterGeneratorFactory();
        break;
        //      case "WELL512A":
        //        rngFac = new WELL512aGeneratorFactory();
        //        break;
        //      case "WELL1024A":
        //        rngFac = new WELL1024aGeneratorFactory();
        //        break;
        //      case "WELL19937A":
        //        rngFac = new WELL19937aGeneratorFactory();
        //        break;
        //      case "WELL44497A":
        //        rngFac = new WELL44497aGeneratorFactory();
        //        break;
      case "RANDU":
        rngFac = new RANDUGeneratorFactory();
        break;
      case "JAVA":
        rngFac = new JavaRandomGeneratorFactory();
        break;
      case "SECURE":
        rngFac = new SecureRandomGeneratorFactory();
        break;
      default:
        ApplicationLogger.log(Level.SEVERE,
            "Unknown RNG " + rng + "; JamesII default used: "
                + SimSystem.getRNGGenerator().getRNGFactory().getFactory());
      }

      if (rngFac != null) {
        SimSystem.getRNGGenerator().setRNGFactory(
            new ParameterizedFactory<RandomGeneratorFactory>(rngFac));
      }
    }
  }
}