/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.pipeline.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.devs.CoupledModel;
import model.devscore.IBasicDEVSModel;
import examples.devs.generator.Generator;

/**
 * Simple frame (coupled DEVS model) that couples a {@link Generator} with a
 * {@link Pipeline}.
 * 
 * @author Alexander Steiniger
 * 
 */
public class Frame extends CoupledModel {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = -4440538233821142114L;

  public static final String MODEL_GENERATOR = "Generator";

  public static final String MODEL_PIPELINE = "Pipeline";

  public Frame(String name) {
    super(name);
    init();
  }

  private void addSubModels() {
    addModel(new Generator(MODEL_GENERATOR));
    addModel(new Pipeline(MODEL_PIPELINE));
  }

  private void addCouplings() {
    // internal couplings
    addCoupling(getModel(MODEL_GENERATOR), Generator.PORT_OUT,
        getModel(MODEL_PIPELINE), Pipeline.PORT_IN);
  }

  @Override
  public void init() {
    super.init();
    addSubModels();
    addCouplings();
  }

  @Override
  public IBasicDEVSModel select(Collection<IBasicDEVSModel> imminents) {
    List<IBasicDEVSModel> copy = new ArrayList<>(imminents);
    // find the generator (works only with one generator)
    for (IBasicDEVSModel model : copy) {
      if (model instanceof Generator) {
        return model;
      }
    }
    return copy.get(0);
  }

}
