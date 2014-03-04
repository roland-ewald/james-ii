/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.AbstractPredictorGenerator;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.util.PredictorGenerators;
import org.jamesii.core.util.misc.Pair;

import shared.AttrInfo;
import shared.AttrValue;
import shared.Categorizer;
import shared.Instance;
import shared.InstanceList;
import shared.NominalAttrInfo;
import shared.RealAttrInfo;
import shared.Schema;

/**
 * Creates a predictor based on on MLJ {@link Categorizer}.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class MLJPredictorGenerator extends AbstractPredictorGenerator {

  /** Name prefix of the (nominal) performance classes. */
  private static final String PERF_CLASS = "perfClass";

  /** Number of different performance classes. */
  private int numOfClasses;

  /**
   * Names of performance classes. That this is specifically a
   * {@link LinkedList} is required by the MLJ API.
   */
  private LinkedList<String> perfClasses = new LinkedList<>(); // NOSONAR

  /** Borders of performance classes. */
  private Double[] perfBorders;

  /** Reference to generated schema. */
  private Schema schema;

  /**
   * Default constructor.
   * 
   * @param numPerfClasses
   *          number of performance classes the inducer shall distinguish
   */
  public MLJPredictorGenerator(int numPerfClasses) {
    numOfClasses = numPerfClasses;
    for (int i = 0; i < numOfClasses; i++) {
      perfClasses.add(PERF_CLASS + i);
    }
    perfClasses.add("dummy"); // Necessary because of a bug in MLJ
  }

  /**
   * Returns categoriser that is trained on the list of given instances.
   * 
   * @param instances
   *          list of instances
   * @return trained categoriser
   */
  public abstract Categorizer getCategorizer(InstanceList instances);

  @Override
  public <T extends PerformanceTuple> IPerformancePredictor generatePredictor(
      List<T> trainingData, PerfTupleMetaData metaData) {

    // Calculate performance borders for later classification
    perfBorders =
        PredictorGenerators.performanceLevels(trainingData, numOfClasses);

    // Analyse meta data, translate them for MLJ
    LinkedList<AttrInfo> attrInfos = new LinkedList<>();
    Map<String, Set<String>> nominalAttribs = metaData.getNominalAttribs();
    for (Entry<String, Set<String>> nominalAttrib : nominalAttribs.entrySet()) {
      LinkedList<String> posssibleValues =
          new LinkedList<>(nominalAttrib.getValue());
      // No explicit support for missing values, synthetic field is added
      // manually
      posssibleValues.add(MLJUtils.MISSING_NOM_VAL);
      attrInfos
          .add(new NominalAttrInfo(nominalAttrib.getKey(), posssibleValues));
    }

    Set<String> numericAttribs = metaData.getNumericAttribs();
    for (String numericAttrib : numericAttribs) {
      attrInfos.add(new RealAttrInfo(numericAttrib));
    }

    Map<String, Pair<AttrInfo, Integer>> attribMap =
        new HashMap<>();
    for (int i = 0; i < attrInfos.size(); i++) {
      attribMap.put(attrInfos.get(i).name(), new Pair<>(
          attrInfos.get(i), i));
    }

    // Create Attribute Information on label
    AttrInfo labelAttrib = new NominalAttrInfo(PERFORMANCE_CLASS, perfClasses);

    // Create MLJ meta information
    schema = new Schema(attrInfos, labelAttrib);
    InstanceList instances = createInstances(trainingData, attribMap);

    // Train categoriser
    Categorizer categorizer = getCategorizer(instances);

    // Build predictor
    return new MLJCategorizerPredictor(categorizer, attribMap, schema);
  }

  /**
   * Create instances.
   * 
   * @param trainingData
   *          training instances to be converted
   * @param attrMap
   *          map name -> (attribute info, attribute number)
   */
  protected <T extends PerformanceTuple> InstanceList createInstances(
      List<T> trainingData, Map<String, Pair<AttrInfo, Integer>> attrMap) {

    InstanceList instances = new InstanceList(schema);

    for (PerformanceTuple perfTuple : trainingData) {

      Set<String> setAttribs = new HashSet<>();

      Instance instance = new Instance(instances.get_schema());

      MLJUtils.setAttributeValues(instance, attrMap,
          perfTuple.getConfiguration());
      setAttribs.addAll(perfTuple.getConfiguration().keySet());

      MLJUtils.setAttributeValues(instance, attrMap, perfTuple.getFeatures());
      setAttribs.addAll(perfTuple.getFeatures().keySet());

      // Care for missing values
      Set<String> missingAttribs = new HashSet<>(attrMap.keySet());
      missingAttribs.removeAll(setAttribs);
      for (String missingAttrib : missingAttribs) {
        MLJUtils.setMissingAttribute(instance, attrMap.get(missingAttrib));
      }

      labelInstance(instance, schema.label_info(), perfTuple.getPerformance());
      instances.add_instance(instance);
    }

    return instances;
  }

  /**
   * Assigns performance label according to real value.
   * 
   * @param instance
   * @param performance
   */
  protected void labelInstance(Instance instance, AttrInfo labelInfo,
      double performance) {
    AttrValue perfLabel = new AttrValue();
    labelInfo.set_nominal_string(perfLabel, perfClasses.get(PredictorGenerators
        .classifyPerformance(perfBorders, performance)), true);
    instance.set_label(perfLabel);
  }

}
