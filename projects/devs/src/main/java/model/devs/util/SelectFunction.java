/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devs.util;

/**
 * This abstract SelectFunction can be used as base class for a concrete select
 * function to be used in a standard DEVS CoupledModel. At least the getNext()
 * method must be overwritten for any concrete SelectFunction. If used an 
 * object of this class should be created by constructing the coupled model.
 * 
 * @author       Jan Himmelspach
 * @version      1.0
 * 
 */
import java.util.List;

import model.devscore.IBasicDEVSModel;

public abstract class SelectFunction {

  /**
   * counter for the models already processed
   */
  private int i;

  /**
   * buffered list of imminents - a concrete SelectFunction will always work on
   * this list
   */
  private List<IBasicDEVSModel> imminents;

  /**
   * Flag indicating whether this concrete select function is currently working
   * on a set of imminents or not
   */
  private boolean initialized = false;

  /**
   * buffer for the number of imminent models to be processed
   */
  private int numberOfImminents;

  /**
   * Instantiates a new select function.
   */
  public SelectFunction() {
    super();
  }

  /**
   * This method can be overwritten for doing further things while the select
   * function is initialized. A possible use would be to sort the list of
   * model's to be processed so that a simple iterator can be used for
   * determining the next model to be processed. If this method is not used the
   * list of imminents will remain in the order the processor passed them over -
   * which can be different from run to run!!!
   */
  protected void doInit() {
  }

  /**
   * This method is automatically called by the getNextModelToBeProcessed method
   * if the last model to be processed has been selected
   */
  private void done() {
    initialized = false;
  }

  /**
   * This method must be overwritten in any concrete select function. From the
   * list of imminents (see attribute imminents) the next model to be processed
   * must be selected. Thereby every model must be selected exactly once.
   * Therefore this method may modify the list of imminents (e.g. remove already
   * processed models). The list of imminent models can be prepared for
   * selection by using the doInit() method.
   * 
   * @return the next model to be processed
   */
  protected abstract IBasicDEVSModel getNext();

  /**
   * Returns the next model to be processed, calls done if this model is the
   * last one to be processed out of the current set of imminents
   * 
   * @return
   */
  private IBasicDEVSModel getNextModelToBeProcessed() {
    IBasicDEVSModel model = getNext();
    i++;
    if (i == numberOfImminents) {
      done();
    }
    return model;
  }

  /**
   * This method is automatically called by the select(ArrayList) method if the
   * first model has to be processed
   */
  private void init(List<IBasicDEVSModel> theImminents) {
    initialized = true;
    imminents = theImminents;
    // buffer the number of imminent models to be processed so that the list can
    // be modified
    numberOfImminents = getImminents().size();
    i = 0;
    doInit();
  }

  /**
   * This method must be called from inside the coupled model's select function.
   * The body of the coupled model's select function can look like this: {
   * return mySelectFunction.select (imminents); }
   * 
   * @param imminents
   *          The set of imminents passed to the coupled model's select function
   * @return The next model to be processed
   */
  public IBasicDEVSModel select(List<IBasicDEVSModel> theImminents) {
    if (!initialized) {
      init(theImminents);
    }
    return getNextModelToBeProcessed();
  }

  /**
   * @return the imminents
   */
  protected List<IBasicDEVSModel> getImminents() {
    return imminents;
  }

}
