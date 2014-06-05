/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.abstractsequential.util;


import java.util.Map;

import org.jamesii.core.model.AbstractState;

import model.devscore.IBasicAtomicModel;
import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;

/**
 * The Class CopyHandler.
 * 
 * @author Jan Himmelspach
 */
public class AbstractSequentialCopyHandler
    extends
    simulator.devscore.util.CopyHandler<Map<IBasicAtomicModel<? extends AbstractState>, Object>, Map<IBasicCoupledModel, Object>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1259625731166017775L;

  /**
   * Adds the to influencee set.
   * 
   * @param model
   *          the model
   * @param influencees
   *          the influencees
   * @param involvedCM
   *          the involved cm
   * @param targetModel
   *          the target model
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void addToInfluenceeSet(IBasicDEVSModel model,
      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencees,
      Map<IBasicCoupledModel, Object> involvedCM, IBasicDEVSModel targetModel) {
    // System.out.println("Copied to " + targetModel.getFullName());
    if (targetModel instanceof IBasicAtomicModel) {
      influencees.put((IBasicAtomicModel<? extends AbstractState>) targetModel,
          null);
    } else {
      if (involvedCM != null) {
        involvedCM.put((IBasicCoupledModel) targetModel, null);
      }
    }
  }

}
