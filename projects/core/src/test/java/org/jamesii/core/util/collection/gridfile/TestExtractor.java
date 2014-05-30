/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.gridfile;

import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

public class TestExtractor implements IDimensionExtractor<TestClass> {

  @Override
  public double getData(TestClass element, int dimension) {
    switch (dimension) {
    case 0:
      return element.getDim0();
    case 1:
      return element.getDim1();
    case 2:
      return element.getDim2();
    case 3:
      return element.getDim3();
    case 4:
      return element.getDim4();
    case 5:
      return element.getDim5();
    case 6:
      return element.getDim6();
    case 7:
      return element.getDim7();
    case 8:
      return element.getDim8();
    }
    return 0;
  }

  @Override
  public double[] getAllData(TestClass element) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getNumberOfDimensions(TestClass element) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Pair<double[], List<Integer>> getData(ParameterBlock params) {
    // TODO Auto-generated method stub
    return null;
  }

}
