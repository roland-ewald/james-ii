/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.pipeline.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import model.devs.CoupledModel;
import model.devscore.IBasicDEVSModel;
import examples.devs.processor.Job;
import examples.devs.processor.Processor;

/**
 * A simple coupled DEVS model of a pipeline of three {@link Processor Processors}, adapted from the pipeline described by
 * Zeiger et al. (2000, p. 86ff.).
 *
 * @author Alexander Steiniger
 *
 */
public class Pipeline extends CoupledModel {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = -7837301560866768470L;

  // ---- model names ---------------------------------------------------------
  public static final String MODEL_P0 = "processor0";
  
  public static final String MODEL_P1 = "processor1";
  
  public static final String MODEL_P2 = "processor2";
  
  // ---- port names ----------------------------------------------------------
  public static final String PORT_IN = "in";
  
  public static final String PORT_OUT = "out";
  
  /**
   * Adds ports
   */
  private void addPorts() {
    addInPort(PORT_IN, Job.class);
    addOutPort(PORT_OUT, Job.class);
  }
  
  /**
   * Adds sub-models
   */
  private void addSubModels() {
    // add components
    addModel(new Processor(MODEL_P0));
    addModel(new Processor(MODEL_P1));
    addModel(new Processor(MODEL_P2));
  }
  
  /**
   * Add couplings
   */
  private void addCouplings() {
    // external input couplings (EIC)
    addCoupling(this, PORT_IN, getModel(MODEL_P0), Processor.PORT_IN);
    
    // external output couplings (EOC)
    addCoupling(getModel(MODEL_P2), Processor.PORT_OUT, this, PORT_OUT);
    
    // internal couplings (IC)
    addCoupling(getModel(MODEL_P0), Processor.PORT_OUT, getModel(MODEL_P1), Processor.PORT_IN);
    addCoupling(getModel(MODEL_P1), Processor.PORT_OUT, getModel(MODEL_P2), Processor.PORT_IN);
  }
  
  @Override
  public void init() {
    super.init();
    addPorts();
    addSubModels();
    addCouplings();
  }
  
  public Pipeline(String name) {
    super(name);
    init();
  }
  
  @Override
  public IBasicDEVSModel select(Collection<IBasicDEVSModel> imminents) {
    // the processor with the lowest index has the highest priority (hopefully)
    List<IBasicDEVSModel> copy = new ArrayList<>(imminents);
    Collections.sort(copy, ProcessorComparator.getInstance());
    return copy.get(0);
  }

}
