package org.jamesii.simspex.exploration.ils.termination;

/**
 * The Interface TerminationIndicator which provides information for the
 * termination of the process.
 * 
 * @param <X>
 *          the generic type
 * 
 * @author Robert Engelke
 */
public interface ITerminationIndicator<X> {

  /**
   * Checks whether the element should terminate.
   * 
   * @param element
   *          the element which has to be checked
   * @return true, if element should terminate
   */
  boolean shallTerminate(X element);

  /**
   * Updates termination criterion for the given element.
   * 
   * @param element
   *          the element the termination criterion is updated for.
   */
  void updateTerminationCriterion(X element);

}
