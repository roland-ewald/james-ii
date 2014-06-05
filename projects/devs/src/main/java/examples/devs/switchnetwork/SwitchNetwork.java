/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.switchnetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.devs.CoupledModel;
import model.devscore.IBasicDEVSModel;
import examples.devs.processor.Job;
import examples.devs.processor.Processor;

/**
 * A simple coupled DEVS model specifying a switch network, adapted from the
 * switch network described by Zeigler et al. (2000, p. 88ff.).
 * 
 * @author Alexander Steiniger
 * 
 */
public class SwitchNetwork extends CoupledModel {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = 5648080637695377959L;

  // ---- port names ----------------------------------------------------------
  public static final String PORT_IN = "in";

  public static final String PORT_OUT = "out";

  // ---- model names ---------------------------------------------------------
  public static final String MODEL_SWITCH = "Switch";

  public static final String MODEL_PROCESSOR0 = "Processor0";

  public static final String MODEL_PROCESSOR1 = "Processor1";

  private void addPorts() {
    addInPort(PORT_IN, Job.class);
    addOutPort(PORT_OUT, Job.class);
  }

  private void addSubModels() {
    addModel(new Switch(MODEL_SWITCH));
    addModel(new Processor(MODEL_PROCESSOR0));
    addModel(new Processor(MODEL_PROCESSOR1));
  }

  private void addCouplings() {
    // external input couplings (EIC)
    addCoupling(this, PORT_IN, getModel(MODEL_SWITCH), Switch.PORT_IN);

    // internal couplings (IC)
    addCoupling(getModel(MODEL_SWITCH), Switch.PORT_OUT,
        getModel(MODEL_PROCESSOR0), Processor.PORT_IN);
    addCoupling(getModel(MODEL_SWITCH), Switch.PORT_OUT1,
        getModel(MODEL_PROCESSOR1), Processor.PORT_IN);

    // external output couplings (EOC)
    addCoupling(getModel(MODEL_PROCESSOR0), Processor.PORT_OUT, this, PORT_OUT);
    addCoupling(getModel(MODEL_PROCESSOR1), Processor.PORT_OUT, this, PORT_OUT);
  }

  @Override
  public void init() {
    super.init();
    addPorts();
    addSubModels();
    addCouplings();
  }

  public SwitchNetwork(String name) {
    super(name);
    init();
  }

  @Override
  public IBasicDEVSModel select(Collection<IBasicDEVSModel> imminents) {
    // processor have highest priority
    List<IBasicDEVSModel> copy = new ArrayList<>(imminents);
    for (IBasicDEVSModel model : copy) {
      if (model instanceof Processor) {
        return model;
      }
    }
    return copy.get(0);
  }

}
