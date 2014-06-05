/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore.util;


import java.util.Iterator;
import java.util.List;

import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.processor.util.BasicHandler;

import model.devscore.BasicDEVSModel;
import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;
import model.devscore.couplings.BasicCoupling;
import model.devscore.couplings.ClassCoupling;
import model.devscore.couplings.Coupling;
import model.devscore.couplings.MultiCoupling;
import model.devscore.ports.IPort;

/**
 * The Class CopyHandler.
 * 
 * @author Jan Himmelspach
 */
public abstract class CopyHandler<AM, CM> extends BasicHandler {

  /** serialisation ID. */
  private static final long serialVersionUID = -1708635130972146300L;

  /**
   * If true readAll and writeAll are used, otherwise per message copying is
   * enabled.
   */
  private boolean copyAtOnce = true;

  /**
   * Default constructor.
   */
  public CopyHandler() {
    super();
  }

  /**
   * Constructor. If copyAtOnce is true the fast readAll and writeAll operations
   * are used. Otherwise per message copying is done by using single read and
   * write operations.
   * 
   * @param copyAtOnce
   *          the copy at once
   */
  public CopyHandler(boolean copyAtOnce) {
    this();
    this.copyAtOnce = copyAtOnce;
  }

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
  protected abstract void addToInfluenceeSet(IBasicDEVSModel model,
      AM influencees, CM involvedCM, IBasicDEVSModel targetModel);

  /**
   * Copy port values.
   * 
   * @param source
   *          the source
   * @param target
   *          the target
   * @param numberOfElements
   *          the number of elements
   */
  private void copyPortValues(IPort source, IPort target, int numberOfElements) {
    if (copyAtOnce) {
      target.writeAll(source.readAll());
    } else {
      for (int i = 0; i < numberOfElements; i++) {
        // write the current element into the target port
        target.write(source.read(i));
      }
    }
  }

  /**
   * This method copies the values in the outports of the model according to the
   * couplings to be iterated by using the couplingIterator.
   * 
   * @param couplingIterator
   *          the coupling iterator
   * @param model
   *          the model
   * @param influencees
   *          the influencees
   * @param involvedCM
   *          the involved cm
   */
  public final void copyValues(Iterator<BasicCoupling> couplingIterator,
      IBasicCoupledModel model, AM influencees, CM involvedCM) {
    while (couplingIterator.hasNext()) {
      BasicCoupling coupling = couplingIterator.next();
      if (coupling instanceof Coupling) {
        // get the next coupling
        Coupling currentCoupling = (Coupling) coupling;
        // this is an out coupling of the current childModel
        // backup a reference to the port
        IPort a = currentCoupling.getPort1();

        // System.out.println("Copying in "+model.getFullName());

        // get the number of elements stored at this out port
        int numberOfElements = a.getValuesCount();
        // if there are any elements
        if (numberOfElements > 0) {
          // we want handle only real messages and not empty (null) messages
          // backup the target port

          // System.out.println("copy to
          // "+currentCoupling.getModel2().getFullName());

          IPort b = currentCoupling.getPort2();

          // copy all elements to the target port, but do not remove them
          // from the source port (they may have to copied more than once!!)
          copyPortValues(a, b, numberOfElements);

          addToInfluenceeSet(model, influencees, involvedCM,
              currentCoupling.getModel2());
        }
      } // if coupling
      else if (coupling instanceof ClassCoupling) {
        // get the next coupling
        ClassCoupling currentCoupling = (ClassCoupling) coupling;
        // this is an out coupling of the current childModel
        // backup a reference to the port
        IPort a = currentCoupling.getPort1();
        // get the number of elements stored at this out port
        int numberOfElements = a.getValuesCount();
        // if there are any elements
        if (numberOfElements > 0) {
          // retrieve list containing the selected targets of this classcoupling
          List<IBasicDEVSModel> targets =
              currentCoupling.getSelector().executeSelection(
                  model.getSubmodelsByClass(currentCoupling.getModel2()));
          // create iterator for retrieved elements
          for (int j = 0; j < targets.size(); j++) {

            // retrieve current targetmodel
            BasicDEVSModel targetModel = (BasicDEVSModel) targets.get(j);
            // retrieve inputport for the given model
            IPort b = targetModel.getInPort(currentCoupling.getPort2());

            copyPortValues(a, b, numberOfElements);

            // the the target model is an atomic model remember that it got
            // an external message

            addToInfluenceeSet(model, influencees, involvedCM, targetModel);
          }
        }
      } // if classcoupling
      else if (coupling instanceof MultiCoupling) {
        // get the next coupling
        MultiCoupling currentCoupling = (MultiCoupling) coupling;
        // this is an out coupling of the current childModel
        // backup a reference to the port
        IPort a = currentCoupling.getPort1();
        // get the number of elements stored at this out port
        int numberOfElements = a.getValuesCount();
        // if there are any elements
        if (numberOfElements > 0) {
          // retrieve vector containing the selected targets of this
          // classcoupling
          List<IBasicDEVSModel> targets =
              currentCoupling.getSelector().executeSelection(
                  currentCoupling.getTargetsAsArrayList());
          // create iterator for retrieved elements
          // Iterator targetIterator = targets.iterator();
          // System.out.println(currentCoupling.getTargetsAsArrayList().size());
          for (int j = 0; j < targets.size(); j++) {
            // retrieve current targetmodel
            IBasicDEVSModel targetModel = targets.get(j);
            // retrieve inputport for the given model
            String portname =
                (currentCoupling.getTargets().getTarget(targetModel))
                    .getPortName();
            IPort b = targetModel.getInPort(portname);

            copyPortValues(a, b, numberOfElements);

            // the the target model is an atomic model remember that it got
            // an external message

            addToInfluenceeSet(model, influencees, involvedCM, targetModel);
          }
        }
      } // if multicoupling
      else {
        throw new InvalidModelException(
            "This coupling class is not yet supported!! ("
                + coupling.getClass().getName() + ")");
      }
    } // end of while(coupIter.hasNext())
  }

}
