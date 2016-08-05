/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package examples.mlspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.experiments.variables.modifier.IVariableModifier;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.math.match.ValueMatch;
import org.jamesii.core.math.match.ValueMatches;
import org.jamesii.core.math.random.rnggenerator.pre.PreDefinedSeedsRandGenerator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStop;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.util.Hook;
import org.jamesii.core.util.collection.ListUtils;
import org.jamesii.core.util.logging.ApplicationLogger;

import model.mlspace.entities.AbstractModelEntity;
import simulator.mlspace.observation.graphic.DefaultColorProvider;
import simulator.mlspace.observation.graphic.GraphicalObserverFactory;
import simulator.mlspace.observation.graphic.GraphicalOutputObserver.GraphicalOutputParameters;
import simulator.mlspace.observation.graphic.IColorProvider;
import simulator.mlspace.observation.graphic.output.IImageProcessorFactory;
import simulator.mlspace.observation.graphic.output.ImageDisplayFactory;
import simulator.mlspace.observation.graphic.output.ImageToFileWriterFactory;

/**
 * Helper methods for ML-Space experiments (but few of them ML-Space specific)
 * 
 * @author Arne Bittig
 * @date May 14, 2012
 */
public final class ExpUtils {

  private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

  private ExpUtils() {
  }

  /**
   * Combinatorial explosion with parameter block arrays - returns a parameter
   * block array as long as the product of the lengths of the input arrays by
   * taking exactly one parameter block from each of the given arrays, merging
   * those, and do this for each possible combination.
   * 
   * @param blocks
   *          Arrays of parameter blocks
   * @return Combinations of all setups
   */
  public static ParameterBlock[] combineParameterBlocks(
      ParameterBlock[]... blocks) {
    if (blocks.length == 0) {
      return new ParameterBlock[0];
    } else if (blocks.length == 1) {
      return blocks[0];
    }
    ParameterBlock[] pairwFinalComb =
        combinePairwise(blocks[blocks.length - 2], blocks[blocks.length - 1]);
    if (blocks.length == 2) {
      return pairwFinalComb;
    }
    ParameterBlock[][] newPar =
        Arrays.copyOfRange(blocks, 0, blocks.length - 1);
    newPar[blocks.length - 2] = pairwFinalComb;
    return combineParameterBlocks(newPar);

  }

  private static ParameterBlock[] combinePairwise(ParameterBlock[] pbs1,
      ParameterBlock[] pbs2) {
    ParameterBlock[] rv = new ParameterBlock[pbs1.length * pbs2.length];
    int iRv = 0;
    for (ParameterBlock pb1 : pbs1) {
      for (ParameterBlock pb2 : pbs2) {
        rv[iRv++] = ParameterBlocks.merge(pb1, pb2);
      }
    }
    return rv;
  }

  /**
   * Invert order in which experiment variables are modified. Only suitable for
   * variables with not too many values (as a new {@link SequenceModifier} is
   * created with all values, even if the former expression was more concise),
   * and only for modifiers whose
   * {@link IVariableModifier#next(org.jamesii.core.experiments.variables.ExperimentVariables)}
   * method ignores the argument (as the ExperimentVariables have not been set
   * up at this stage).
   * 
   * @param list
   *          List of experiment variables. These are changed in place, if
   *          possible!
   */
  public static void reverseExpVarsInList(List<ExperimentVariable<?>> list) {
    ListIterator<ExperimentVariable<?>> listItr = list.listIterator();
    while (listItr.hasNext()) {
      ExperimentVariable<?> expVar = listItr.next();
      List<?> vars = getExpVarValuesFromModifier(expVar);
      if (vars == null || vars.isEmpty()) {
        continue;
      }
      Collections.reverse(vars);
      listItr.set(newExpVarSeqMod(expVar.getName(), vars));
    }
  }

  /**
   * Get all values and {@link ExperimentVariable} can take (e.g. for order
   * change). Only works for variables where the modification does not depend on
   * other variables/an environment.
   * 
   * @param expVar
   * @return all values expVar can take, or null if no evaluation possible
   */
  private static <T> List<T> getExpVarValuesFromModifier(
      ExperimentVariable<T> expVar) {
    IVariableModifier<T> modifier = expVar.getModifier();
    List<T> vars = new ArrayList<>();
    try {
      while (true) {
        vars.add(modifier.next(null));
      }
    } catch (NullPointerException npe) { // NOSONAR: unavoidable as
      // null works most of the time there is nothing else yet to pass
      // to next (that would not throw another Exception otherwise)
      ApplicationLogger.log(Level.SEVERE,
          "Experiment variable " + expVar.getName() + " with modifier "
              + expVar.getModifier()
              + " could not be reversed. Left unchanged.");
      vars = null;
    } catch (NoNextVariableException e) { /* loop terminated "normally" */
    }
    return vars;
  }

  /**
   * Shortcut for creating experiment variable with sequence modifier
   * 
   * @param name
   *          Variable name
   * @param vals
   *          Variable value sequence
   * @return new {@link ExperimentVariable} with {@link SequenceModifier}
   */
  @SafeVarargs
  public static <T> ExperimentVariable<T> newExpVarSeqMod(String name,
      T... vals) {
    return new ExperimentVariable<>(name, new SequenceModifier<>(vals));
  }

  /**
   * Turn list of values into {@link ExperimentVariable} (extracted method to
   * avoid rawtypes usage, as it may be used in a loop where the type parameter
   * differs between iterations).
   * 
   * @param name
   *          Name of new {@link ExperimentVariable}
   * @param values
   *          Values (to create {@link SequenceModifier} from)
   * @return {@link ExperimentVariable}
   */
  public static <T> ExperimentVariable<T> newExpVarSeqMod(String name,
      List<T> values) {
    return new ExperimentVariable<>(name, new SequenceModifier<>(values));
  }

  /**
   * Extract {@link ExperimentVariable} values to list, create all combinations
   * between several experiment variables, shuffle list of combinations, create
   * list of jointly modified experiment variables for random execution order of
   * the same experiment variable combinations. May create quite large lists if
   * the number of combinations is large, any does not work if an experiment
   * variable depends on other variables/an environment.
   * 
   * IMPORTANT: The return value is supposed to be added to a list which is then
   * passed to
   * {@link org.jamesii.core.experiments.BaseExperiment#setupVariablesStructure(List)}
   * , NOT to be passed itself to
   * {@link org.jamesii.core.experiments.BaseExperiment#setupVariables(List)}!
   * Other, not-to-be-shuffled {@link ExperimentVariable}s can be put into a
   * single-element list each and be added to that list of lists for
   * {@link org.jamesii.core.experiments.BaseExperiment#setupVariablesStructure(List)}
   * .
   * 
   * @param expVars
   *          {@link ExperimentVariable}s to change order of
   * @param rand
   *          Random number generator (see also
   *          {@link org.jamesii.core.math.random.generators.WrappedJamesRandom}
   *          )
   * @return List of exp var combinations (see important note above)
   */
  public static List<ExperimentVariable<?>> shuffleExpVarCombOrder(
      List<ExperimentVariable<?>> expVars, Random rand) {
    int numVars = expVars.size();
    List<List<?>> nestedList = new ArrayList<>(numVars);
    for (ExperimentVariable<?> expVar : expVars) {
      List<?> expVarValues = getExpVarValuesFromModifier(expVar);
      if (expVarValues == null) {
        throw new UnsupportedOperationException("Experiment variable "
            + "values can not be extracted for " + expVar);
      }
      nestedList.add(expVarValues);
    }
    List<? extends List<Object>> allCombinations =
        ListUtils.combinations(nestedList);
    ApplicationLogger.log(Level.CONFIG,
        "Shuffling " + allCombinations.size() + " exp var combinations");
    Collections.shuffle(allCombinations, rand);
    List<List<Object>> newExpVarsSequences =
        ListUtils.transpose(allCombinations);
    List<ExperimentVariable<?>> rv = new ArrayList<>(numVars);
    for (int i = 0; i < numVars; i++) {
      ExperimentVariable<?> expVar = expVars.get(i);
      rv.add(newExpVarSeqMod(expVar.getName(), newExpVarsSequences.get(i)));
    }
    return rv;
  }

  /**
   * create attribute name -> value(s) singleton map for defining matching
   * targets (type parameter not in return value as currently not yet relevant
   * for {@link simulator.mlspace.observation.snapshot.MatchingSnapshotPlugin})
   * 
   * @param attName
   *          Attribute name
   * @param attVals
   *          Attribute value(s)
   * @return Singleton collection
   */
  public static <T> Map<String, ValueMatch> attValForMatch(String attName,
      @SuppressWarnings("unchecked") T... attVals) {
    if (attVals.length == 1) {
      return Collections.<String, ValueMatch> singletonMap(attName,
          new ValueMatches.EqualsValue(attVals[0]));
    } else {
      return Collections.<String, ValueMatch> singletonMap(attName,
          new ValueMatches.AnyOf(new LinkedHashSet<>(Arrays.asList(attVals))));
    }
  }

  /**
   * Tell the {@link SimSystem} to always use the same seed, or cycle repeatedly
   * though the same list of seeds
   * 
   * @param seeds
   *          One or more seeds
   */
  public static void setFixedRandSeeds(Long... seeds) {
    if (seeds.length == 0) {
      throw new IllegalArgumentException("At least one seed required!");
    }
    final Long[] storedSeed = seeds;
    SimSystem.setRandSeedGenerator(new PreDefinedSeedsRandGenerator(
        Arrays.<Long> asList(), new Hook<PreDefinedSeedsRandGenerator>() {

          private static final long serialVersionUID = -8533151330094500267L;

          @Override
          protected void executeHook(PreDefinedSeedsRandGenerator rngg) {
            rngg.setSeeds(Arrays.asList(storedSeed));
          }
        }));
  }

  /**
   * @param modelFileRelPath
   * @return Model URI
   * @throws URISyntaxException
   */
  public static URI getModelURIFromRelPath(String modelFileRelPath)
      throws URISyntaxException {
    File file = new File(modelFileRelPath);
    String fp = file.getAbsolutePath().replace('\\', '/');
    return new URI("file-mls:///" + fp);
  }

  // /**
  // * Create data storage factory that creates data storages that write to
  // * given path
  // *
  // * @param path
  // * @return data storage factory
  // */
  // public static ParameterizedFactory<DataStorageFactory>
  // createDataStorageFactory(
  // String path) {
  // if (path == null) {
  // return null;
  // }
  // return new ParameterizedFactory<DataStorageFactory>(
  // new DirectFileDataStorageFactory(),
  // new MemoryDataStorageFactory(),
  // new ParameterBlock(path, "path")));
  // }

  /**
   * Create directory for given path (and all parent dirs if not present). Log
   * error if unsuccessful.
   * 
   * @param path
   *          Path of directory to create
   * @return true iff dir was not present and was created
   */
  public static boolean createDirIfNotPresent(String path) {
    try {
      if (new File(path).mkdirs()) {
        ApplicationLogger.log(Level.CONFIG, "Created dir(s) " + path);
        return true;

      } else {
        ApplicationLogger.log(Level.CONFIG, "Did not create dir " + path
            + " (maybe because it already existed)");
        return false;
      }
    } catch (SecurityException e) {
      ApplicationLogger.log(Level.SEVERE,
          "Security exception when trying to create dir " + path
              + "\nThis is probably bad news for observers that should write to that directory.");
      ApplicationLogger.log(e);
      return false;
    }
  }

  /**
   * Copy given file to given directory
   * 
   * @param filePath
   *          input file path and name
   * @param targetPath
   *          output path
   * @return true if successfully copied, false if in and out identical
   *         (exception if attempt unsuccessful)
   * @throws IOException
   */
  public static boolean copyFile(String filePath, String targetPath)
      throws IOException {
    File inFile = new File(filePath);
    File outFile = new File(targetPath + inFile.getName());
    if (inFile.getAbsolutePath().equals(outFile.getAbsolutePath())) {
      return false;
    }
    try (FileInputStream fileInputStream = new FileInputStream(inFile);
        FileOutputStream fileOutputStream = new FileOutputStream(outFile);) {
      FileChannel source = fileInputStream.getChannel();
      FileChannel dest = fileOutputStream.getChannel();
      dest.transferFrom(source, 0, source.size());
    } catch (IOException e) {
      ApplicationLogger.log(e);
      throw e;
    }
    return true;
  }

  /**
   * Add computer name and current time (in ISO 8601 format) and final '/' to
   * given path
   * 
   * @param path
   * @return path with attached computer name and time
   */
  public static String addComputerAndDate(String path) {
    return addComputerAndDate(path, DEFAULT_DATE_PATTERN);
  }

  public static String addComputerAndDate(String path, String datePattern) {
    int last = path.length();
    if (path.charAt(last - 1) == '/') {
      last--;
    }
    if (path.charAt(last - 1) == '-') {
      last--;
    }

    String computerName = "";
    try {
      computerName = System.getenv("COMPUTERNAME");
    } catch (SecurityException e) { /* fail silently */
    }
    if (computerName.isEmpty()) {
      try {
        computerName = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e1) { /* fail silently */
      }
    }

    String timeString = DateTimeFormatter.ofPattern(datePattern)
        .withZone(ZoneId.systemDefault()).format(Instant.now())
        .replaceAll("[^\\w\\+-]", "_");
    return path.substring(0, last) + '-' + computerName + '-' + timeString
        + '/';
  }

  /**
   * Convenience method to create {@link SimTimeStop} policy
   * 
   * @param stopTime
   * @return Stop policy
   */
  public static ParameterizedFactory<ComputationTaskStopPolicyFactory<?>> getSimTimeStopPolicy(
      double stopTime) {
    return new ParameterizedFactory<ComputationTaskStopPolicyFactory<?>>(
        new SimTimeStopFactory(),
        new ParameterBlock(stopTime, SimTimeStopFactory.SIMEND));
  }

  /**
   * Convenience method to add graphical output specification to instrumenter's
   * parameter block
   * 
   * @param params
   *          Parameter block where to add {@link GraphicalObserverFactory}
   * @param displayOnScreen
   *          Flag whether to add
   *          {@link simulator.mlspace.observation.graphic.output.ImageDisplay
   *          output on screen}
   * @param fileTimes
   *          Time point when
   *          {@link simulator.mlspace.observation.graphic.output.ImageToFileWriter
   *          image-to-file-writer} should do its work (null for none, empty for
   *          start & end)
   * @param gop
   *          Further graphical output parameters (optional): image size (2
   *          values: width and height) and/or fading factor (1 value)
   * @return Modifiable list of {@link IImageProcessorFactory image processor
   *         factories} for addition of others not covered by this method
   */
  public static List<IImageProcessorFactory> addGraphicalOutput(
      ParameterBlock params, boolean displayOnScreen,
      Iterable<Double> fileTimes, int... gop) {
    return addGraphicalOutput(params, displayOnScreen, fileTimes,
        new DefaultColorProvider(), gop, null, false, null);
  }

  /**
   * Convenience method like
   * {@link #addGraphicalOutput(ParameterBlock, boolean, Iterable, int...)} with
   * additional parameters for customizing subvol viz
   * 
   * @param params
   *          ParameterBlock to add graphical observer to
   * @param displayOnScreen
   *          Flag whether to display window during sim
   * @param fileTimes
   *          Time indices for when to write image to file
   * @param gop
   *          Graphical output parameters
   * @param subvolSubdivisions
   *          two factors related to number of different entities to expect in
   *          one subvol
   * @param hideSubvolBoundaries
   *          flag whether to show no or strong subvol boundaries
   * @param amountForMaxIntensity
   * @return Modifiable list of image processors (for easier addition of custom
   *         ones)
   */
  public static List<IImageProcessorFactory> addGraphicalOutput(
      ParameterBlock params, boolean displayOnScreen,
      Iterable<Double> fileTimes, int[] gop, int[] subvolSubdivisions,
      boolean hideSubvolBoundaries, Integer amountForMaxIntensity) {
    return addGraphicalOutput(params, displayOnScreen, fileTimes,
        new DefaultColorProvider(), gop, subvolSubdivisions,
        hideSubvolBoundaries, amountForMaxIntensity);
  }

  /**
   * Convenience method like
   * {@link #addGraphicalOutput(ParameterBlock, boolean, Iterable, int...)} with
   * additional parameters for customizing subvol viz
   * 
   * @param params
   *          ParameterBlock to add graphical observer to
   * @param displayOnScreen
   *          Flag whether to display window during sim
   * @param fileTimes
   *          Time indices for when to write image to file
   * @param colorProvider
   *          TODO
   * @param gop
   *          Graphical output parameters (x*y dimensions, fade alpha or both)
   * @param subvolSubdivisions
   *          two factors related to number of different entities to expect in
   *          one subvol
   * @param hideSubvolBoundaries
   *          flag whether to show no or strong subvol boundaries
   * @param amountForMaxIntensity
   * @return Modifiable list of image processors (for easier addition of custom
   *         ones)
   */
  public static List<IImageProcessorFactory> addGraphicalOutput(
      ParameterBlock params, boolean displayOnScreen,
      Iterable<Double> fileTimes,
      IColorProvider<AbstractModelEntity> colorProvider, int[] gop,
      int[] subvolSubdivisions, boolean hideSubvolBoundaries,
      Integer amountForMaxIntensity) {

    GraphicalOutputParameters goParams = new GraphicalOutputParameters();
    switch (gop.length) {
    case 0:
      break;
    case 1:
      goParams.setFadeAlpha(gop[0]);
      break;
    case 3:
      goParams.setFadeAlpha(gop[2]); // NOSONAR: intentional
      //$FALL-THROUGH$
    case 2:
      goParams.setMaxWidth(gop[0]);
      goParams.setMaxHeight(gop[1]);
      break;
    default:
      throw new IllegalArgumentException(
          "Too many arguments (3 max): " + Arrays.toString(gop));
    }

    List<IImageProcessorFactory> imgProcs = new LinkedList<>();
    if (displayOnScreen) {
      imgProcs.add(new ImageDisplayFactory());
    }
    if (fileTimes != null) {
      imgProcs.add(new ImageToFileWriterFactory(fileTimes));
    }

    params.addSubBlock(GraphicalObserverFactory.class.getName(),
        new GraphicalObserverFactory(imgProcs, goParams, colorProvider,
            subvolSubdivisions, hideSubvolBoundaries, amountForMaxIntensity));
    return imgProcs;
  }
}
