/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.io.Serializable;

import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;


@Deprecated
public interface IPerformaneProfile extends Serializable {

  IProblemDefinition getSimulationProblem();

  void setSimulationProblem(IProblemDefinition problemDefinition);

  IPerformanceType getPerformanceMeasure();

  void setPerformanceMeasure(IPerformanceType performanceMeasure);

  IPerformance getMax();

  void setMax(IPerformance max);

  IPerformance getMin();

  void setMin(IPerformance min);

  int getSampleSize();

  void setSampleSize(int sampleSize);

  double getMean();

  void setMean(double mean);

  double getStdDeviation();

  void setStdDeviation(double stdDeviation);

}