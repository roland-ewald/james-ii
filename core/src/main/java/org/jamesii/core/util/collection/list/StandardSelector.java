package org.jamesii.core.util.collection.list;

import java.util.List;

/**
 * The Class StandardSelector.
 * 
 * This is a simple default selector, which can be used if no other is given.
 * This selector does not select anything: it just returns the complete list
 * passed to select from as selected value.
 * 
 * @author Christian Ober
 * @param <M>
 *          the type of the elements
 * 
 */
public class StandardSelector<M> implements ISelector<M> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3086496875008008557L;

  /**
   * Creates a new StandardSelector.
   */
  public StandardSelector() {
    super();
  }

  /**
   * Selects all possible target elements.<br>
   * <b>Note: Due to performance-factors and other reasons of
   * implementation-issues it is needed to return a list of models by executing
   * the selection, but due to the formalism it is not allowed to use the
   * information included in the models of the list. It is not allowed to change
   * any of the information.</b>
   * 
   * @param elements
   *          The Vector, containing all "interesting" elements.
   * 
   * @return The Vector containing all results (1 result).
   */
  @Override
  public synchronized List<M> executeSelection(List<M> elements) {
    return elements;
  }

}
