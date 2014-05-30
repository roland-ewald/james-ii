/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import java.io.FileWriter;
import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.model.variables.LongTrajectory;
import org.jamesii.core.model.variables.LongTrajectoryVariable;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Calculates curve fitting on a given set of input values.
 * 
 * TODO: This task should be delegated to a class in org.jamesii.core.test.
 * 
 * @author Antje Samland
 * @author Roland Ewald
 * 
 */
public class CurveFittingObjective implements IOptimizationObjective,
    Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(CurveFittingObjective.class,
        new IConstructorParameterProvider<CurveFittingObjective>() {
          @Override
          public Object[] getParameters(CurveFittingObjective obj) {
            return new Object[] { obj.getName(), obj.getRealData(),
                obj.getRealTimes() };
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8675070275210554559L;

  /** The name of the parameter. */
  private final String paramName;

  /** The real data. */
  private final Long[] realData;

  /** The real times. */
  private final Double[] realTimes;

  /** The file writer. */
  private transient FileWriter fileWriter;

  /**
   * Factor by which the generated random reference data may std-deviate.
   */
  private double derivationFactor = 0.1;

  /**
   * Instantiates a new curve fitting objective.
   * 
   * @param name
   *          the name
   * @param data
   *          the data
   * @param times
   *          the times
   */
  public CurveFittingObjective(String name, Long[] data, Double[] times) {
    paramName = name;
    realData = data;
    realTimes = times;
  }

  @Override
  public double calcObjective(Configuration configuration,
      Map<String, BaseVariable<?>> response) {

    LongTrajectoryVariable trajectoryVar =
        (LongTrajectoryVariable) response.get(paramName);
    LongTrajectory trajectory = trajectoryVar.getValue();

    long[] data = trajectory.getData(); // new length - old length
    double[] times = trajectory.getTimes();

    // objective
    int leastSquares = 0;
    for (int i = 0; i < Math.min(data.length, realData.length); i++) {

      // search the nearest time
      int t = 0;
      while (times[t] < realTimes[i] && t < times.length - 1) {
        t++;
      }

      leastSquares += (data[t] - realData[i]) * (data[t] - realData[i]);
    }

    // least-squares fit
    return leastSquares;
  }

  /**
   * Gets the real data.
   * 
   * @return the realData
   */
  public Long[] getRealData() {
    return realData;
  }

  /**
   * Gets the real times.
   * 
   * @return the realTimes
   */
  public Double[] getRealTimes() {
    return realTimes;
  }

  /**
   * Gets the param name.
   * 
   * @return the paramName
   */
  @Override
  public String getName() {
    return paramName;
  }

}
