/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.flatsequential.eventforwarding;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.model.AbstractState;
import org.jamesii.core.processor.util.BasicHandler;

import model.devscore.IBasicAtomicModel;
import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;
import simulator.devscore.util.CopyHandler;
import simulator.devscore.util.DefaultCopyHandler;

/**
 * The Class HierarchicalExternalEventForwardingHandler.
 * 
 * @author Jan Himmelspach
 */
public class HierarchicalExternalEventForwardingHandler extends BasicHandler
    implements ExternalEventForwardingHandler {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6230007337190820267L;

  /** The copy handler. */
  @SuppressWarnings("rawtypes")
  private CopyHandler copyHandler = new DefaultCopyHandler();

  @SuppressWarnings("unchecked")
  @Override
  public void copyExternalEvents(Map<IBasicDEVSModel, Object> imminents,
      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencedAM) {
    // propagate upwards

    Map<IBasicCoupledModel, Object> involvedCM =
        new HashMap<>();

    // iterate over all internal and external out couplings of all imminent
    // atomic models
    for (IBasicDEVSModel m : imminents.keySet()) {
      IBasicAtomicModel<? extends AbstractState> am =
          (IBasicAtomicModel<? extends AbstractState>) m;
      IBasicCoupledModel cm = am.getParent();
      getCopyHandler()
          .copyValues(cm.getICIterator(am), cm, influencedAM, involvedCM);
      getCopyHandler().copyValues(cm.getEOCIterator(am), cm, influencedAM,
          involvedCM);
    }

    // array list which contains the involved coupled models per level
    List<Map<IBasicCoupledModel, Object>> coupledModels =
        new ArrayList<>();

    for (IBasicCoupledModel cm : involvedCM.keySet()) {
      int l = cm.getLevel();
      // if ((l == -1) && (cm.getParent() != null)) throw new
      // RuntimeException("Invalid level ("+l+") => wrong model
      // initialization");
      if (l < 0) {
        continue;
      }

      Map<IBasicCoupledModel, Object> t = null;
      if (l < coupledModels.size()) {
        t = coupledModels.get(l);
      } else {
        while (l > coupledModels.size()) {
          coupledModels.add(new HashMap<IBasicCoupledModel, Object>());
        }
        t = new HashMap<>();
        coupledModels.add(l, t);
      }

      if (t == null) {
        t = new HashMap<>();
      }
      t.put(cm, null);
    }

    doHierarchicalCopyingUp(coupledModels, involvedCM, influencedAM);

    doHierarchicalCopyingDown(coupledModels, involvedCM, influencedAM);

  }

  /**
   * Do hierarchical copying down.
   * 
   * @param involvedCM
   *          the involved cm
   * @param coupledModels
   *          the coupled models
   * @param influencedAM
   *          the influenced am
   */
  @SuppressWarnings("unchecked")
  protected void doHierarchicalCopyingDown(
      List<Map<IBasicCoupledModel, Object>> coupledModels,
      Map<IBasicCoupledModel, Object> involvedCM,
      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencedAM) {

    int upto = coupledModels.size() - 1;
    int level = 0;
    while (level <= upto) {

      Map<IBasicCoupledModel, Object> theLevel = coupledModels.get(level);

      Map<IBasicCoupledModel, Object> cm =
          new HashMap<>();

      for (IBasicCoupledModel model : theLevel.keySet()) {

        IBasicCoupledModel parent = model.getParent();

        getCopyHandler().copyValues(model.getEICIterator(model), parent,
            influencedAM, cm);
        model.clearInPorts();

      }

      // if there are invovled coupled models which have not been considered yet
      if (cm.size() > 0) {

        if ((coupledModels.size() <= level + 1)) {
          upto++;
        }

        if ((coupledModels.size() <= level + 1)
            || (coupledModels.get(level + 1) == null)) {
          coupledModels.add(level + 1,
              new HashMap<IBasicCoupledModel, Object>());
        }

        coupledModels.get(level + 1).putAll(cm);
        involvedCM.putAll(cm);
      }

      level++;
    }
  }

  /**
   * Do hierarchical copying up.
   * 
   * @param coupledModels
   *          the coupled models
   * @param involvedCM
   *          the involved cm
   * @param influencedAM
   *          the influenced am
   */
  @SuppressWarnings("unchecked")
  protected void doHierarchicalCopyingUp(
      List<Map<IBasicCoupledModel, Object>> coupledModels,
      Map<IBasicCoupledModel, Object> involvedCM,
      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencedAM) {

    for (int level = coupledModels.size() - 1; level >= 0; level--) {

      Map<IBasicCoupledModel, Object> theLevel = coupledModels.get(level);

      Map<IBasicCoupledModel, Object> cm =
          new HashMap<>();

      for (IBasicCoupledModel model : theLevel.keySet()) {

        IBasicCoupledModel parent = model.getParent();

        getCopyHandler().copyValues(parent.getEOCIterator(model), parent,
            influencedAM, involvedCM);

        if (level != 0) {
          coupledModels.get(level - 1).put(parent, null);
        }

        getCopyHandler().copyValues(parent.getICIterator(model), parent,
            influencedAM, cm);

        model.clearOutPorts();
      }

      theLevel.putAll(cm);
      involvedCM.putAll(cm);
    }

  }

  @Override
  public void init(IBasicDEVSModel model) {

  }

  /**
   * @return the copyHandler
   */
  public CopyHandler getCopyHandler() {
    return copyHandler;
  }

  /**
   * @param copyHandler the copyHandler to set
   */
  public void setCopyHandler(CopyHandler copyHandler) {
    this.copyHandler = copyHandler;
  }

}
