/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore.dynamic;

import java.io.Serializable;
import java.util.Comparator;

import model.devscore.dynamic.BasicCouplingChangeRequest;
import model.devscore.dynamic.ChangeRequest;
import model.devscore.dynamic.ModelChangeRequest;
import model.devscore.dynamic.MultiCouplingChangeRequest;
import model.devscore.dynamic.PortChangeRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class CompareChangeRequests.
 * 
 * @author Jan Himmelspach
 */
public class CompareChangeRequests implements Comparator<ChangeRequest<?>>,
    Serializable {

  /**
   * The constant serial version uid. 
   */
  private static final long serialVersionUID = -6682233482112321151L;

  /**
   * Gets the score.
   * 
   * @param changeRequest
   *          the change request
   * 
   * @return the score
   * 
   * @author Jan Himmelspach * Small helper class for sorting structure change
   *         requests The requests are ordered in the following way 1.
   *         ModelChangeRequests (add - requests) 2. PortChangeRequests
   *         (add-requests) 3. CouplingChangeRequests (add - requests) 4.
   *         CouplingChangeRequests (remove - requests) 5. PortChangeRequests
   *         (remove - requests) 6. ModelChangeRequests (remove - requests) (see
   *         getScore(...) function)
   * 
   *         The reasons for this ordering process are:
   * 
   *         1 < 2 < 3: it shall be possible to add models, ports and coupling
   *         where these models are part of at the same time 5 < 6 < 7: if a
   *         model is removed all couplings are removed where this model is a
   *         part of, this ordering ensures that no runtime exception is thrown
   *         because of the circumstance that a coupling/port is removed and the
   *         model is removed 2,3,4 < 7: someone could add a coupling/port to a
   *         model to be removed 3,4 < 5, 2 < 6, and 1 < 7: creating already
   *         present things shall give an error!
   */

  /**
   * Determines the kind of request to set a priority (see comment above)
   * 
   * @param changeRequest
   * @return the score
   */
  private static int getScore(ChangeRequest<?> changeRequest) {

    if (changeRequest instanceof ModelChangeRequest) {
      if (((ModelChangeRequest) changeRequest).isAddRequest()) {
        return 1;
      }
      return 7;
    }

    if (changeRequest instanceof PortChangeRequest) {
      if (((PortChangeRequest) changeRequest).isAddRequest()) {
        return 2;
      }
      return 6;
    }

    if (changeRequest instanceof BasicCouplingChangeRequest) {

      // Modifications of multi-couplings should be executed aftetr adding and
      // before removing couplings
      if (changeRequest instanceof MultiCouplingChangeRequest
          && ((MultiCouplingChangeRequest) changeRequest)
              .isModificationRequest()) {
        return 4;
      }

      if (((BasicCouplingChangeRequest<?>) changeRequest).isAddRequest()) {
        return 3;
      }

      return 5;
    }

    return 0;
  }

  /**
   * Instantiates a new compare change requests.
   */
  public CompareChangeRequests() {
    super();
  }

  /**
   * Compare.
   * 
   * @param cr1
   *          the cr1
   * @param cr2
   *          the cr2
   * 
   * @return the int
   */
  @Override
  public int compare(ChangeRequest<?> cr1, ChangeRequest<?> cr2) {

    int val1 = getScore(cr1);
    int val2 = getScore(cr2);

    if (val1 == val2) {
      return 0;
    }
    if (val1 < val2) {
      return -1;
    }

    return 1;
  }
}
