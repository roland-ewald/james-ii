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
import examples.devs.generator.Generator;

/**
 * A simple frame coupling a {@link Generator} with a {@link SwitchNetwork}.
 * 
 * @author Alexander Steiniger
 * 
 */
public class SwitchFrame extends CoupledModel {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = -1626021218711858205L;

  // ---- model names ---------------------------------------------------------
  public static final String MODEL_GENERATOR = "Generator";

  public static final String MODEL_SWITCH_NETWORK = "SwitchNetwork";

  private void addSubModels() {
    addModel(new Generator(MODEL_GENERATOR));
    addModel(new SwitchNetwork(MODEL_SWITCH_NETWORK));
  }

  private void addCouplings() {
    // internal couplings
    addCoupling(getModel(MODEL_GENERATOR), Generator.PORT_OUT,
        getModel(MODEL_SWITCH_NETWORK), SwitchNetwork.PORT_IN);
  }

  @Override
  public void init() {
    super.init();
    addSubModels();
    addCouplings();
  }

  public SwitchFrame(String name) {
    super(name);
    init();
  }

  @Override
  public IBasicDEVSModel select(Collection<IBasicDEVSModel> imminents) {
    // switch network has higher priority than generator
    List<IBasicDEVSModel> copy = new ArrayList<>(imminents);
    for (IBasicDEVSModel model : copy) {
      if (model instanceof SwitchNetwork) {
        return model;
      }
    }
    return copy.get(0);
  }

}
