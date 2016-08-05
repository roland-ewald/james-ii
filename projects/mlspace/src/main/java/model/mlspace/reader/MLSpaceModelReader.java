/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.antlr.runtime.RecognitionException;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.math.geometry.SpatialException;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.util.logging.ApplicationLogger;

import model.mlspace.IMLSpaceModel;
import model.mlspace.MLSpaceModel;
import model.mlspace.entities.RuleEntity;
import model.mlspace.reader.antlr.MLSpaceDirectParser;
import model.mlspace.reader.antlr.MLSpaceSmallParser;

/**
 * Reader for MLSpace models. The "meat" of model reading is in the
 * (antlr-auto-generated) {@link MLSpaceDirectParser} and in
 * {@link model.mlspace.reader.MLSpaceParserHelper}.
 * 
 * @author Arne Bittig
 */
public class MLSpaceModelReader implements IModelReader {

  /**
   * ID under which to "file" model pass-on information about variable/constant
   * definitions that were actually used during model parsing
   */
  public static final String USED_CONSTANTS = "used constants";

  /**
   * ID under which to "file" model pass-on information about variable/constant
   * definitions that were _not_ used during model parsing
   */
  public static final String UNUSED_CONSTANTS = "unused constants";

  /** ID for "filing" overridden constants */
  public static final String USED_OVERRIDES = "overridden constants";

  /** ID for "filing" supposed-to-be overridden constants */
  public static final String UNUSED_OVERRIDES =
      "unused constants to be overridden";

  /** paramter map key for strings to get observables from */
  public static final String OBSERVABLES_TO_PARSE =
      "observables to be identified by parser";

  /** model info key for parsed observables */
  public static final String OBSERVABLES_PARSED =
      "observables identified by parser";

  private int lastNumSyntaxErrors = -1;

  private int lastBacktrackingLevel = -1;

  private final boolean oldParserFallback;

  public MLSpaceModelReader() {
    this(false);
  }

  /**
   * @param oldParserFallback
   *          Re-throw {@link RecognitionException}s wrapped in a runtime
   *          exception or just log ex and return null in case of failure in
   *          {@link #read(URI, Map)} method?
   */
  public MLSpaceModelReader(boolean oldParserFallback) {
    this.oldParserFallback = oldParserFallback;
  }

  @Override
  public IMLSpaceModel read(URI source, Map<String, ?> parameters) {
    MLSpaceSmallParser parser;
    try {
      if (source.getScheme()
          .equals(MLSpaceModelReaderFactory.ML_SPACE_STRING)) {
        parser = new MLSpaceSmallParser(
            source.getSchemeSpecificPart().replace("%20", " "), false);
      } else {
        parser = new MLSpaceSmallParser(source.getPath(), parameters, false);
      }
    } catch (IOException e1) {
      ApplicationLogger.log(e1);
      return null;
    }
    try {
      MLSpaceModel model = parser.fullmodel().model;
      if ((model == null || parser.getNumberOfSyntaxErrors() > 0)
          && oldParserFallback) {
        ApplicationLogger.log(Level.WARNING,
            parser.getNumberOfSyntaxErrors() + " syntax error(s) for "
                + source.toString() + ". Trying old syntax parser.");
        return readWithOldParser(source, parameters);
      }
      addNewPassOnInfo(model, parser, parameters);
      ApplicationLogger.log(
          parser.getNumberOfSyntaxErrors() > 0 ? Level.SEVERE : Level.INFO,
          "Model parsed with " + parser.getNumberOfSyntaxErrors()
              + " syntax error(s). Info: " + model.getInfoMap());
      lastNumSyntaxErrors = parser.getNumberOfSyntaxErrors();
      lastBacktrackingLevel = parser.getBacktrackingLevel();
      return model;
    } catch (Exception e) {
      if (e instanceof SpatialException) {
        throw (SpatialException) e;
      } else {
        if (!oldParserFallback && e instanceof RuntimeException) {
          throw (RuntimeException) e;
        }
        ApplicationLogger.log(Level.SEVERE, "Exception(s) when parsing "
            + source.toString() + ". Trying old syntax parser.", e);
        return readWithOldParser(source, parameters);
      }
    }
  }

  private IMLSpaceModel readWithOldParser(URI source,
      Map<String, ?> parameters) {
    MLSpaceDirectParser parser;
    try {
      parser = new MLSpaceDirectParser(source.getPath(), parameters, false);
    } catch (IOException e1) {
      /* not actually possible - would have occurred earlier */
      assert false;
      throw new IllegalStateException(e1);
    }

    try {
      MLSpaceModel model = parser.fullmodel().model;
      addOldPassOnInfo(model, parser, parameters);
      ApplicationLogger.log(Level.INFO,
          "Model parsed with " + parser.getNumberOfSyntaxErrors()
              + " syntax error(s). Info: " + model.getInfoMap());
      lastNumSyntaxErrors = parser.getNumberOfSyntaxErrors();
      lastBacktrackingLevel = parser.getBacktrackingLevel();
      return model;
    } catch (RecognitionException e) {
      // if (throwRecognitionExceptionAsRuntimeEx) {
      throw new IllegalStateException(e);
      // } else {
      // ApplicationLogger.log(e);
      // return null;
      // }
    }
  }

  /**
   * Add information not strictly part of the model that is needed elsewhere
   * where parser and/or model reader are not easily accessible (e.g.
   * instrumenter, simulator)
   * 
   * @param model
   *          Model to add information to
   * @param parser
   *          Parser to get information from
   * @param parameters
   *          Parameters from which to extract pass-on info
   */
  private static void addOldPassOnInfo(MLSpaceModel model,
      MLSpaceDirectParser parser, Map<String, ?> parameters) {
    addOldConstantOverrideInfo(model, parser, parameters);
    addOldObservationTargetInfo(model, parser, parameters);
  }

  private static void addOldObservationTargetInfo(MLSpaceModel model,
      MLSpaceDirectParser parser, Map<String, ?> parameters) {
    if (parameters.containsKey(OBSERVABLES_TO_PARSE)) {
      List<? extends List<? extends RuleEntity>> obs;
      Object toParse = parameters.get(OBSERVABLES_TO_PARSE);
      try {
        parser.setTokenStream(
            MLSpaceDirectParser.stringToTokenStream((String) toParse));
        obs = parser.observationTargets().obs;
      } catch (RecognitionException | IOException | ClassCastException e) {
        ApplicationLogger.log(Level.SEVERE,
            "Un-parse-able observation targets: " + toParse, e);
        return;
      }
      model.setInfo(OBSERVABLES_PARSED, obs);
    }
  }

  private static void addOldConstantOverrideInfo(MLSpaceModel model,
      MLSpaceDirectParser parser, Map<String, ?> parameters) {
    Set<String> usedConstantIDs = parser.getUsedConstants();
    Map<String, ?> allConstants =
        new LinkedHashMap<>(parser.getDefinedConstants());

    Map<String, Object> usedConstants = new LinkedHashMap<>();
    Map<String, Object> allOverrides = new LinkedHashMap<>(parameters);
    Map<String, Object> usedOverrides = new LinkedHashMap<>();
    for (String id : usedConstantIDs) {
      usedConstants.put(id, allConstants.remove(id));
      if (allOverrides.containsKey(id)) {
        usedOverrides.put(id, allOverrides.remove(id));
      }
    }
    allOverrides.remove(OBSERVABLES_TO_PARSE);

    model.setInfo(USED_CONSTANTS, usedConstants);
    model.setInfo(UNUSED_CONSTANTS, allConstants);
    if (!allConstants.isEmpty()) {
      ApplicationLogger.log(Level.WARNING,
          "Unused constants " + allConstants + " in model " + model);
    }
    model.setInfo(USED_OVERRIDES, usedOverrides);
    model.setInfo(UNUSED_OVERRIDES, allOverrides);
    if (!allOverrides.isEmpty()) {
      ApplicationLogger.log(Level.SEVERE,
          "Unused overrides: " + allOverrides + " not used in model " + model);
    }
  }

  private static void addNewPassOnInfo(MLSpaceModel model,
      MLSpaceSmallParser parser, Map<String, ?> parameters) {
    addNewConstantOverrideInfo(model, parser, parameters);
    addNewObservationTargetInfo(model, parser, parameters);
  }

  private static void addNewObservationTargetInfo(MLSpaceModel model,
      MLSpaceSmallParser parser, Map<String, ?> parameters) {
    if (parameters.containsKey(OBSERVABLES_TO_PARSE)) {
      Map<List<? extends RuleEntity>, List<String>> obs;
      Object toParse = parameters.get(OBSERVABLES_TO_PARSE);
      try {
        parser.setTokenStream(
            MLSpaceDirectParser.stringToTokenStream((String) toParse));
        obs = parser.observationTargets().obs;
      } catch (RecognitionException | IOException | ClassCastException
          | IllegalStateException e) {
        ApplicationLogger.log(Level.SEVERE,
            "Un-parse-able observation targets: " + toParse, e);
        return;
      }
      model.setInfo(OBSERVABLES_PARSED, obs);
    }
  }

  private static void addNewConstantOverrideInfo(MLSpaceModel model,
      MLSpaceSmallParser parser, Map<String, ?> parameters) {
    Set<String> usedConstantIDs = parser.getUsedConstants();
    Map<String, ?> allConstants =
        new LinkedHashMap<>(parser.getDefinedConstants());

    Map<String, Object> usedConstants = new LinkedHashMap<>();
    Map<String, Object> usedOverrides = new LinkedHashMap<>();
    Map<String, Object> allOverrides = new LinkedHashMap<>(parameters);
    allOverrides.remove(OBSERVABLES_TO_PARSE);
    for (String id : usedConstantIDs) {
      usedConstants.put(id, allConstants.remove(id));
      if (allOverrides.containsKey(id)) {
        usedOverrides.put(id, allOverrides.remove(id));
      }
    }

    model.setInfo(USED_CONSTANTS, usedConstants);
    model.setInfo(UNUSED_CONSTANTS, allConstants);
    if (!allConstants.isEmpty()) {
      ApplicationLogger.log(Level.WARNING,
          "Unused constants " + allConstants + " in " + model);
    }
    model.setInfo(USED_OVERRIDES, usedOverrides);
    model.setInfo(UNUSED_OVERRIDES, allOverrides);
    if (!allOverrides.isEmpty()) {
      ApplicationLogger.log(Level.SEVERE,
          "Unused overrides: " + allOverrides + " in " + model);
    }
  }

  /**
   * @return number of syntax errors in last {@link #read(URI, Map) read} model
   */
  public final int getLastNumSyntaxErrors() {
    return lastNumSyntaxErrors;
  }

  /**
   * @return backtracking level for last {@link #read(URI, Map) read} model
   */
  public final int getLastBacktrackingLevel() {
    return lastBacktrackingLevel;
  }

  @Override
  public ISymbolicModel<?> read(URI ident) {
    throw new UnsupportedOperationException(
        "No symbolic MLSpace model defined yet!");
    // TODO
  }

}
