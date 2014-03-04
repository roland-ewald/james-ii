/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.core.serialization.SerialisationUtils;


/**
 * Super class for selectors, implements idea of using a predictor (see
 * {@link IPerfComparisonPredictor}) to sort a list.
 * 
 * @author Roland Ewald
 * 
 */
public class Selector implements ISelector {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4861317547940949268L;

  /**
   * List of configuration entries, to be resorted by a given a performance
   * predictor.
   */
  private List<ConfigurationEntry> configs =
      new ArrayList<>();

  /** Performance predictor to be used (size of the list). */
  private IPerformancePredictor performancePredictor;

  /** Number of configurations to be returned. */
  private int listSize;

  /**
   * The flag as to minimise or maximise the performance. In case of
   * maximisation, the sorting order will be reversed.
   */
  private boolean maximisePerformance;

  /**
   * Default constructor.
   * 
   * @param configurations
   *          set of configurations that are available
   * @param perfPredictor
   *          the performance predictor to be used
   * @param resultSize
   *          maximal size of the resulting list
   * @param maximisePerf
   *          flag to maximise performance instead of minimising it (reverses
   *          order)
   */
  public Selector(Set<Configuration> configurations,
      IPerformancePredictor perfPredictor, int resultSize, boolean maximisePerf) {
    performancePredictor = perfPredictor;
    maximisePerformance = maximisePerf;
    listSize = resultSize;
    for (Configuration config : configurations) {
      configs.add(new ConfigurationEntry(config));
    }
  }

  /**
   * Instantiates a new selector (empty).
   */
  public Selector() {
  }

  @Override
  public List<Configuration> selectConfigurations(Features features) {

    Collections.sort(configs, new PerformancePredictingComparator(features,
        performancePredictor));

    if (maximisePerformance) {
      Collections.reverse(configs);
    }

    ArrayList<Configuration> result = new ArrayList<>();
    for (int i = 0; i < Math.min(listSize, configs.size()); i++) {
      result.add(configs.get(i).getConfig());
    }

    return result;
  }

  @Override
  public IPerformancePredictor getPerformancePredictor() {
    return performancePredictor;
  }

  public List<ConfigurationEntry> getConfigs() {
    return configs;
  }

  public void setConfigs(List<ConfigurationEntry> configs) {
    this.configs = configs;
  }

  public int getListSize() {
    return listSize;
  }

  public void setListSize(int listSize) {
    this.listSize = listSize;
  }

  public void setBinaryPerfComparisonPredictor(String binaryPredictor)
      throws IOException, ClassNotFoundException {
    this.performancePredictor =
        (IPerformancePredictor) SerialisationUtils
            .deserializeFromB64String(binaryPredictor);
  }

  public String getBinaryPerfComparisonPredictor() throws IOException {
    return SerialisationUtils.serializeToB64String(performancePredictor);
  }

  public boolean isMaximisePerformance() {
    return maximisePerformance;
  }

  public void setMaximisePerformance(boolean maximisePerformance) {
    this.maximisePerformance = maximisePerformance;
  }

}
