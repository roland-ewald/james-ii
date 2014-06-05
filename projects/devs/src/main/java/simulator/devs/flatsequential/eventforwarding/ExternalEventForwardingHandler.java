/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.flatsequential.eventforwarding;


import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.model.AbstractState;

import model.devscore.IBasicAtomicModel;
import model.devscore.IBasicDEVSModel;

/**
 * The interface ExternalEventForwardingHandler.
 * 
 * @author Jan Himmelspach
 */
public interface ExternalEventForwardingHandler extends Serializable {

  /**
   * Copy external events. Copies the outputs of the imminent models to the
   * receivers which are returned in the influencedAM parameter.
   * 
   * @param imminents
   *          src of external events
   * @param influencedAM
   *          targets of external events (output)
   */
  void copyExternalEvents(
      Map<IBasicDEVSModel, Object> imminents,
      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencedAM);

  /**
   * Initializes the handler. This method might be overwritten in descendant
   * classes to initialize the handler. The model passed needs to be the top
   * most model the handler will be applied for, and maybe the root model of the
   * DEVS model tree.
   * 
   * @param model
   *          the model the handler shall be used for
   */
  void init(IBasicDEVSModel model);

}
