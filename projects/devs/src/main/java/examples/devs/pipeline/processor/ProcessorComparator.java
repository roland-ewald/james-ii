/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.pipeline.processor;

import java.util.Comparator;

import model.devscore.IBasicDEVSModel;
import examples.devs.processor.Processor;

/**
 * Compares two {@link Processor Processors} (DEVS models) according to their name (i.e., their index).
 * 
 * @author Alexander Steiniger
 * 
 */
public class ProcessorComparator implements Comparator<IBasicDEVSModel> {

  private static final ProcessorComparator INSTANCE = new ProcessorComparator();

  /**
   * Private constructor
   */
  private ProcessorComparator() {
  }

  @Override
  public int compare(IBasicDEVSModel o1, IBasicDEVSModel o2) {
    return o1.getName().compareTo(o2.getName());
  }

  /**
   * 
   * @return instance of the comparator
   */
  public static ProcessorComparator getInstance() {
    return INSTANCE;
  }

}
