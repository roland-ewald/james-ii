/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * Auxiliary class that can be used to buffer information about targets while
 * resolving {@link LooseCoupling}s to concrete model couplings, as loose
 * couplings are generic communication patterns without references to models or
 * concrete port instances.
 * 
 * @author Alexander Steiniger
 * 
 */
public class LooseCouplingTarget {

  public static final boolean INPUT_PORT = true;

  public static final boolean OUTPUT_PORT = false;

  /**
   * Reference to the associated model
   */
  private final IBasicDEVSModel model;

  /**
   * Reference to the associated port
   */
  private final IPort port;

  /**
   * Flag that indicates whether the port of this target refers to an in- or
   * output port
   */
  private final boolean input;

  /**
   * Creates a new instance of {@link LooseCouplingTarget} and assigns the
   * specified arguments to it.
   * 
   * @param model
   *          a reference to the associated model
   * @param port
   *          a reference to the port
   * @param input
   *          a flag indicating whether the specified port refers to an in- or
   *          output port
   */
  public LooseCouplingTarget(IBasicDEVSModel model, IPort port, boolean input) {
    this.model = model;
    this.port = port;
    this.input = input;
  }

  /**
   * The copy constructor for {@link LooseCouplingTarget}
   * 
   * @param target
   *          the target object to copy
   */
  public LooseCouplingTarget(LooseCouplingTarget target) {
    this.model = target.getModel();
    this.port = target.getPort();
    this.input = target.isInput();
  }

  /**
   * Gets the model reference.
   * 
   * @return the model
   */
  public IBasicDEVSModel getModel() {
    return model;
  }

  /**
   * Gets the port reference.
   * 
   * @return the port
   */
  public IPort getPort() {
    return port;
  }

  /**
   * Checks if associated port refers to an input port or not.
   * 
   * @return <code>true</code> if the associated port refers to an input port,
   *         <code>false</code> otherwise
   */
  public boolean isInput() {
    return input;
  }

}
