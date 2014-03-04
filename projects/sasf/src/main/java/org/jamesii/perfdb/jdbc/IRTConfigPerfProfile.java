/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import java.io.Serializable;

/**
 * Interface for a runtime configuration's performance profile.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public interface IRTConfigPerfProfile extends Serializable {

  long getPerfID();

  long getConfigID();

  double getMinPerf();

  double getAvgPerf();

  double getMaxPerf();

  int getSampleSize();

  double getStdDev();

}