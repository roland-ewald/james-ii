/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.flatsequential.eventforwarding;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamesii.core.model.AbstractState;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.processor.util.BasicHandler;

import model.devscore.IBasicAtomicModel;
import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;
import model.devscore.couplings.BasicCoupling;
import model.devscore.couplings.Coupling;
import model.devscore.ports.IPort;

/**
 * This event forwarding handler transforms all existing couplings into "direct"
 * couplings. I.e., during copying events all coupled models are omitted and
 * outputs are directly copied from one atomic model out port to another atomic
 * model's in port. <br>
 * 
 * This handler requires more memory and a longer startup time as a handler
 * relying directly on the information stored in the coupled models:
 * <ul>
 * <li>We need to hold a list of direct connections between ports, there might
 * many of those</li>
 * <li>The initialization time can be relatively lenghty, because all potential
 * paths have to be found, and for each of these path a new coupling has to be
 * created</li>
 * </ul>
 * 
 * <b>NOTE</b>: This handler only works for PDEVS models, and can only handle
 * couplings of type {@link model.devscore.couplings.Coupling}! If there are
 * other couplings you have to use another forwarding handler.
 * 
 * @author Jan Himmelspach
 */
public class DirectExternalEventForwardingHandler extends BasicHandler
    implements ExternalEventForwardingHandler {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1331492914456569222L;

  /** the map containing all direct couplings. */
  private Map<IBasicDEVSModel, Map<IPort, List<BasicCoupling>>> couplings =
      new HashMap<>();

  /**
   * Add the couplings retrievable by using the iterator "it", to the hash map
   * passed.
   * 
   * All couplings are automatically grouped by the source models, and ports in
   * the hash map.
   * 
   * @param it
   *          the iterator to iterate over all couplings
   * @param cl
   *          the hash map containing the couplings per model and per port.
   */
  protected void addCouplings(Iterator<BasicCoupling> it,
      Map<IBasicDEVSModel, Map<IPort, List<BasicCoupling>>> cl) {
    // get one coupling after the other
    while (it.hasNext()) {
      BasicCoupling bc = it.next();
      // fetch the sub map for the model
      Map<IPort, List<BasicCoupling>> sl = cl.get(bc.getModel1());
      // check wether we already have such a sub map, if not create
      if (sl == null) {
        sl = new HashMap<>();
        cl.put(bc.getModel1(), sl);
      }
      // now we have a sub map, and we can try to fetch the list for the source
      // port
      List<BasicCoupling> basicCouplings = sl.get(bc.getPort1());
      // if there is no such list, create
      if (basicCouplings == null) {
        basicCouplings = new ArrayList<>();
        sl.put(bc.getPort1(), basicCouplings);
      }

      // add the current coupling the the list of coupling of the model + port
      basicCouplings.add(bc);

    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void copyExternalEvents(Map<IBasicDEVSModel, Object> imminents,
      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencedAM) {
    // for all imminent models do
    for (IBasicDEVSModel src : imminents.keySet()) {
      // get the couplings related with that model
      Map<IPort, List<BasicCoupling>> portToCouplingsMap = couplings.get(src);
      if (portToCouplingsMap == null) {
        continue;
      }
      // for all ports do
      for (List<BasicCoupling> coups : portToCouplingsMap.values()) {
        // if there are couplings for the current port and if the port has a
        // value
        if ((coups.size() > 0) && (coups.get(0).getPort1().hasValue())) {
          // System.out.println("The model "+src.getFullName()+" has outputs on
          // port "+coups.get(0).getPort1().getName());
          // copy the port's values according to the couplings, thus for each
          // coupling do
          for (int i = 0; i < coups.size(); i++) {
            Coupling c = (Coupling) coups.get(i);
            c.getPort2().writeAll(c.getPort1().readAll());
            influencedAM.put(
                (IBasicAtomicModel<? extends AbstractState>) c.getModel2(),
                null);
          }
        }
      }
    }
  }

  /**
   * Called from {@link #init(IBasicDEVSModel)} method, collects all couplings
   * of the given coupled model into the couplingsList parameter passed.
   * 
   * @param model
   *          the model the couplings shall be retrieved from
   * @param couplingsList
   *          a simple list of couplings
   */
  private void getAllCouplings(IBasicCoupledModel model,
      Map<IBasicDEVSModel, Map<IPort, List<BasicCoupling>>> couplingsList) {

    // do this for all sub models
    Iterator<IBasicDEVSModel> it = model.getSubModelIterator();
    while (it.hasNext()) {
      IBasicDEVSModel m = it.next();
      // if sub model is a coupled model recursively call this method
      if (m instanceof IBasicCoupledModel) {
        getAllCouplings((IBasicCoupledModel) m, couplingsList);
      }
    }

    // collect couplings of this model
    addCouplings(model.getEICIterator(), couplingsList);
    addCouplings(model.getEOCIterator(), couplingsList);
    addCouplings(model.getICIterator(), couplingsList);
  }

  /**
   * This method calls the
   * {@link #searchEndModelsAndPorts(IBasicDEVSModel, IPort, IBasicDEVSModel, IPort, HashMap)}
   * method for the given coupling "coupling".
   * 
   * @param coupling
   *          coupling to find direct replacements for
   * @param allCouplings
   *          list of all couplings defined in the model
   * 
   * @return the couplings
   */
  private List<BasicCoupling> getCouplings(Coupling coupling,
      Map<IBasicDEVSModel, Map<IPort, List<BasicCoupling>>> allCouplings) {

    IBasicDEVSModel startModel = coupling.getModel1();
    IPort startPort = coupling.getPort1();

    // this is okay because we only allow couplings of type Coupling here
    IBasicDEVSModel endModel = coupling.getModel2();
    IPort endPort = coupling.getPort2();

    // find the targets of the given coupling, these are the models finally
    // receiving events transferred by this coupling
    List<BasicCoupling> result =
        searchEndModelsAndPorts(startModel, startPort, endModel, endPort,
            allCouplings);

    return result;
  }

  @Override
  public void init(IBasicDEVSModel model) {
    Map<IBasicDEVSModel, Map<IPort, List<BasicCoupling>>> allCouplings =
        new HashMap<>();

    // get all couplings defined in the model, store them in allCouplings
    getAllCouplings((IBasicCoupledModel) model, allCouplings);

    // now all couplings are stored in one list

    // we'll now take each coupling starting at an atomic model and compute its
    // target

    for (Map.Entry<IBasicDEVSModel, Map<IPort, List<BasicCoupling>>> iBasicDEVSModelMapEntry : allCouplings.entrySet
            ()) {
      IBasicDEVSModel m = iBasicDEVSModelMapEntry.getKey();

      // if the model m is an atomic one we have found a potential source of
      // external events
      if (!(m instanceof IBasicCoupledModel)) {
        // get all couplings (per port) of this event source
        for (Map.Entry<IPort, List<BasicCoupling>> iPortListEntry : allCouplings.get(m).entrySet()) {
          IPort p = iPortListEntry.getKey();

          List<BasicCoupling> allCouplingsOfPort = allCouplings.get(m).get(p);

          // for all couplings search for direct coupling replacements
          for (int i = 0; i < allCouplingsOfPort.size(); i++) {
            BasicCoupling bc = allCouplingsOfPort.get(i);

            // this coupling is our starting point, let's find it final
            // destination

            List<BasicCoupling> nc = null;

            if (bc instanceof Coupling) {
              Coupling c = (Coupling) bc;

              // get the replacements for the coupling c
              nc = getCouplings(c, allCouplings);

              // add the direct replacements to the list of direct couplings
              addCouplings(nc.iterator(), couplings);

            } else {
              throw new InvalidModelException(
                      "DMR only supports std DEVS couplings!!!");
            }

          }

        }
      }

    }

  }

  /**
   * This methods iterates (recursively) over all concatenated couplings and
   * finally returns a list of direct couplings.
   * 
   * @param startModel
   *          the original start model (always atomic)
   * @param startPort
   *          the original start port
   * @param endModel
   *          the current end model (coupled or atomic)
   * @param endPort
   *          the current end port
   * @param allCouplings
   *          list of all couplings defined in the model
   * 
   * @return the array list< basic coupling>
   */
  private List<BasicCoupling> searchEndModelsAndPorts(
      IBasicDEVSModel startModel, IPort startPort, IBasicDEVSModel endModel,
      IPort endPort,
      Map<IBasicDEVSModel, Map<IPort, List<BasicCoupling>>> allCouplings) {

    List<BasicCoupling> result = new ArrayList<>();

    if (!(endModel instanceof IBasicCoupledModel)) {
      // create new direct coupling if the endModel is an atomic one
      Coupling c = new Coupling(startModel, startPort, endModel, endPort);
      // System.out.println("got new direct coupling "+c);
      result.add(c);
    } else {
      // recursively call this method if the current end model is still a
      // coupled one
      Map<IPort, List<BasicCoupling>> portCouplingMap =
          allCouplings.get(endModel);
      if (portCouplingMap != null) {
        List<BasicCoupling> coups = portCouplingMap.get(endPort);

        if (coups != null) {

          Iterator<BasicCoupling> basicCouplingIterator = coups.iterator();
          if (basicCouplingIterator != null) {
            while (basicCouplingIterator.hasNext()) {
              BasicCoupling bc = basicCouplingIterator.next();
              Coupling c;
              if (bc instanceof Coupling) {
                c = (Coupling) bc;
                result.addAll(searchEndModelsAndPorts(startModel, startPort,
                    c.getModel2(), c.getPort2(), allCouplings));
              }
            }
          }
        }
      }
    }
    return result;
  }

}
