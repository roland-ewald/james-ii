/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.serialization;

import org.jamesii.core.experiments.RunInformation;
import org.jamesii.simspex.util.JamesSimDataProvider;


/**
 * The mock sub-class of the {@link JamesSimDataProvider}.
 * 
 * @author Roland Ewald
 */
public class MyJamesSimDataProvider extends JamesSimDataProvider<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4962064354223396802L;

  /**
   * Instantiates a new mock sim data provider.
   */
  public MyJamesSimDataProvider() {
    super();
  }

  /**
   * Instantiates a new mock sim data provider.
   * 
   * @param runInfo
   *          the run info
   */
  public MyJamesSimDataProvider(RunInformation runInfo) {
    super(runInfo);
  }

  @Override
  public String getData(Object... parameters) {
    return "data";
  }

  @Override
  public Class<String> getDataType() {
    return String.class;
  }

}
