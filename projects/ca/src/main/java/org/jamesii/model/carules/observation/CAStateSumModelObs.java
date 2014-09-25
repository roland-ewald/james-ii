/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.observation;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.observe.NotifyingObserver;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.visualization.chart.plotter.IPlotableObserver;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.ICARulesModel;

/**
 * The Class CAStateSumModelObs.
 * 
 * @author Roland Ewald
 */
public class CAStateSumModelObs extends NotifyingObserver<ICARulesModel>
    implements IPlotableObserver<ICARulesModel> {

  /** Serialization ID. */
  private static final long serialVersionUID = -7480172059656189149L;

  /** The var names. */
  private List<String> varNames = new ArrayList<>(); // NOSONAR

  /** The data. */
  private List<Pair<? extends Number, ? extends Number>> data = // NOSONAR
      new ArrayList<>();

  /** The counter. */
  private int counter = 0;

  /**
   * Instantiates a new cA state sum model obs.
   */
  public CAStateSumModelObs() {
    varNames.add("a");
  }

  @Override
  public void handleUpdate(ICARulesModel entity) {

    int stateCount = 0;
    for (ICACell<?> cell : ((ICARulesModel) entity).getGrid().getCellList()) {
      stateCount += cell.getState() == 0 ? 1 : 0;
    }
    data.add(new Pair<>(counter, stateCount));
    System.out.println(counter + ":" + stateCount);
    counter++;
  }

  @Override
  public String[] getAxisNames() {
    return new String[] { "Time", "Number of cells in state 'a'" };
  }

  @Override
  public String[] getAxisUnits() {
    return new String[] { "t", "#a" };
  }

  @Override
  public String getPlotTitle() {
    return "Some bogus output";
  }

  @Override
  public List<Pair<? extends Number, ? extends Number>> getVariableData(
      String varName) {
    return data;
  }

  @Override
  public List<String> getVariableNames() {
    return varNames;
  }

}
