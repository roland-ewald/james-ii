/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jamesii.core.model.IModel;
import org.jamesii.core.util.collection.ElementSet;
import org.jamesii.core.util.collection.list.ReusableListIterator;

import model.devscore.IBasicDEVSModel;
import model.devscore.NotUniqueException;
import model.devscore.ports.IPort;

/**
 * The Class CouplingSet.
 * 
 * @author Jan Himmelspach
 * @author Christian Ober
 * @version 1.0
 * 
 *          history 14.02.2004 Christian Ober added JavaDoc-comments. history
 *          25.03.2004 Christian Ober switched from ElementSet to HashMap.
 *          history 14.07.2004 Jan Himmelspach couplings are now stored ordered
 *          by their source models (double HashMap)
 */
public class CouplingSet extends ElementSet<BasicCoupling> implements
    ICouplingSet {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -3625720390455880837L;

  /** Holds the data of this CouplingSet. */
  private Map<IModel, Map<String, BasicCoupling>> content =
      new HashMap<>();

  /** Buffers a list of reusable iterators. */
  private Map<IModel, ReusableListIterator<BasicCoupling>> iterators =
      new HashMap<>();

  /**
   * Adds a BasicCoupling to the CouplingSet.
   * 
   * @param coupling
   *          The BasicCoupling to add.
   * 
   * @throws NotUniqueException
   *           If the BasicCoupling already exists in the CouplingSet.
   */
  @Override
  public void addCoupling(BasicCoupling coupling) {
    Map<String, BasicCoupling> addTo = null;
    if (!content.containsKey(coupling.getModel1())) {
      addTo = new HashMap<>();
      content.put(coupling.getModel1(), addTo);
    } else {
      addTo = content.get(coupling.getModel1());
      iterators.remove(coupling.getModel1());
    }

    if (!addTo.containsKey(coupling.getName())) {
      // put here by hash-introduction
      addTo.put(coupling.getName(), coupling);
      // former calculations still remain for performance-reasons
      getVelements().add(coupling);
      setElementIterator(null);
      changed();
    } else {
      StringBuilder buff = new StringBuilder();
      buff.append("A coupling between these models and ports already exist! (");
      buff.append(coupling.getName());
      buff.append(")");
      throw new NotUniqueException(buff.toString());
    }
  }

  @Override
  public boolean contains(BasicCoupling coupling) {
    if (content.containsKey(coupling.getModel1())) {
      if (((content.get(coupling.getModel1()))).containsKey(coupling.getName())) {
        return true;
      }
      return false;

    }
    return false;

  }

  /**
   * Checks whether the given coupling ident is in the CouplingSet or not.
   * 
   * @param ident
   *          The coupling to look for.
   * 
   * @return <code>true</code> if the BasicCoupling is in the CouplingSet, <br>
   *         <code>false</code> else.
   */
  @Override
  public boolean contains(String ident) {
    for (Map<String, BasicCoupling> stringBasicCouplingMap : content.values()) {
      if ((stringBasicCouplingMap).containsKey(ident)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Find the coupling specified by the given name.
   * 
   * @param name
   *          the name
   * 
   * @return the coupling
   */
  @Override
  public BasicCoupling getCoupling(String name) {

    for (Map<String, BasicCoupling> stringBasicCouplingMap : content.values()) {

      BasicCoupling bc = stringBasicCouplingMap.get(name);
      if (bc != null) {
        return bc;
      }

    }

    return null;
  }

  @Override
  public Iterator<BasicCoupling> getCouplingsIterator(IBasicDEVSModel model) {
    if (iterators.containsKey(model)) {
      ReusableListIterator<BasicCoupling> it = iterators.get(model);
      it.init();
      return it;
    }
    ReusableListIterator<BasicCoupling> it;
    if (content.containsKey(model)) {

      it =
          new ReusableListIterator<>(new ArrayList<>(
              (content.get(model)).values()));
      iterators.put(model, it);
    } else {
      it = new ReusableListIterator<>(null);
    }
    return it;

  }

  /**
   * Removes a BasicCoupling from the list.
   * 
   * @param coupling
   *          The BasicCoupling to remove.
   * 
   * @throws NoSuchElementException
   *           If the element to remove does not exist.
   */
  @Override
  public void removeCoupling(BasicCoupling coupling) {
    boolean err = false;
    if (content.containsKey(coupling.getModel1())) {
      iterators.remove(coupling.getModel1());
      Map<String, BasicCoupling> removeFrom =
          content.get(coupling.getModel1());
      if (removeFrom.containsKey(coupling.getName())) {

        // newly put here by hash-introduction
        removeFrom.remove(coupling.getName());
        // former calculations still remain for performance-reasons

        // former calculations still remain for performance-reasons
        for (int i = 0; i < getVelements().size(); i++) {

          BasicCoupling c = getVelements().get(i);
          if (c.getName().equals(coupling.getName())) {
            getVelements().remove(i);
            break;
          }

        }

        setElementIterator(null);
        changed();
      } else {
        err = true;
      }
    } else {
      err = true;
    }
    if (err) {
      StringBuilder buff = new StringBuilder();
      buff.append("coupling was not found: (");
      buff.append(coupling.getName());
      buff.append(")");
      throw new NoSuchElementException(buff.toString());
    }
  }

  /**
   * Removes a ClassCoupling, identified by the needed parameters.
   * 
   * @param model1
   *          The source-model of the to-remove-coupling.
   * @param port1
   *          The source-port of the to-remove-coupling.
   * @param model2
   *          The target-class of the to-remove-coupling.
   * @param port2
   *          The target-port of the to-remove-coupling.
   * 
   * @throws NoSuchElementException
   *           If the element to remove does not exist.
   */
  @Override
  public void removeCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, IPort port2) {

    // newly put here by hash-introduction
    removeCoupling(new ClassCoupling(model1, port1, model2, port2.getName()));
    // former calculations still remain for performance-reasons
  }

  /**
   * Removes a Coupling, identified by the needed parameters.
   * 
   * @param model1
   *          The source-model of the to-remove-coupling.
   * @param port1
   *          The source-port of the to-remove-coupling.
   * @param model2
   *          The target-model of the to-remove-coupling.
   * @param port2
   *          The target-port of the to-remove-coupling.
   * 
   * @throws NoSuchElementException
   *           If the element to remove does not exist.
   */
  @Override
  public void removeCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2) {

    removeCoupling(new Coupling(model1, port1, model2, port2));
  }

  /**
   * Removes a BasicCoupling, identified by the name.
   * 
   * @param couplingName
   *          The name of the to-remove-coupling.
   * 
   * @throws NoSuchElementException
   *           If the element to remove does not exist.
   */
  @Override
  public void removeCoupling(String couplingName) {

    throw new UnsupportedOperationException();

    // if (content.containsKey(couplingName)) {
    // // newly put here by hash-introduction
    // content.remove(couplingName);
    //
    // elementIterator = null;
    //
    // // former calculations still remain for performance-reasons
    // for (int i = 0; i < velements.size(); i++) {
    //
    // BasicCoupling c = velements.get(i);
    // if (c.getName().equals(couplingName)) {
    // velements.remove(i);
    // break;
    // }
    //
    // }
    // changed();
    // } else {
    // StringBuffer buff = new StringBuffer();
    // buff.append("Coupling could not be removed! (");
    // buff.append(couplingName);
    // buff.append(")");
    // throw new NoSuchElementException(buff.toString());
    // }
  }

  /**
   * Removes all Couplings that contain the given IBasicDEVSModel. Note: ONLY
   * Couplings are removed, no Class- or Multi-Couplings.
   * 
   * @param model
   *          The given model to remove all Coupling for.
   */
  @Override
  public void removeCouplings(IBasicDEVSModel model) {

    // buffer the model's name
    String modelName = model.getFullName();

    // first remove all couplings with the given model as source
    content.remove(model);
    iterators.remove(model);

    // iterate all existing couplings
    Iterator<BasicCoupling> it = getVelements().iterator();
    while (it.hasNext()) {

      BasicCoupling bc = it.next();

      // remove those which have the model as source
      if (bc.isModel1(modelName)) {
        // remove from the velements set, has already been removed from the
        // hashed set (content.remove)
        it.remove();
      } else {
        // if we have a multi coupling and the model is one of the targets
        // remove it
        if (bc instanceof MultiCoupling) {
          if (((MultiCoupling) bc).isModelIn(model)) {
            ((MultiCoupling) bc).removeTarget(model);
          }
        } else {
          // if we have a normal coupling and the model is the target
          if ((bc instanceof Coupling) && (((Coupling) bc).isModel2(modelName))) {
            Map<String, BasicCoupling> removeFrom =
                content.get(bc.getModel1());

            removeFrom.remove(bc.getName());

            iterators.remove(bc.getModel1());

            it.remove();

          }
        }
      }
    }

    setElementIterator(null);
    changed();
  }

  /**
   * Replace.
   * 
   * @param currentModel
   *          the current model
   * @param newModel
   *          the new model
   */
  @Override
  public void replace(IBasicDEVSModel currentModel, IBasicDEVSModel newModel) {

    // get the couplings the currentModel is the source of
    Map<String, BasicCoupling> couplings = content.get(currentModel);

    if (couplings != null) {

      // make sure that we get a list of references to the couplings which is
      // independent from the CouplingSet's methods
      List<BasicCoupling> al =
          new ArrayList<>(couplings.values());

      // Iterator<String> it = couplings.keySet().iterator();

      Iterator<BasicCoupling> ibc = al.iterator();

      while (ibc.hasNext()) {

        // remove the old coupling
        removeCoupling(ibc.next());
      }

      ibc = al.iterator();
      while (ibc.hasNext()) {
        // replace the source
        BasicCoupling bc = ibc.next();
        bc.setModel1(newModel);
        // store the new coupling
        addCoupling(bc);
      }
    }

    // now all couplings the currentModel is the source of have been adopted

    // find all couplings the currentModel is the target of and replace that by
    // the newModel
    List<BasicCoupling> al = new ArrayList<>();

    for (BasicCoupling bc : getVelements()) {
      if (bc instanceof Coupling) {
        if (((Coupling) bc).getModel2() == currentModel) {
          al.add(bc);
        }
      }
//      if (bc instanceof ClassCoupling) {
//        if (((ClassCoupling) bc).getModel2() == currentModel.getClass()) {
//          // here we do not need to do something, cause model2 should have the
//          // same class!!!!!!
//        }
//      }
      if (bc instanceof MultiCoupling) {
        if (((MultiCoupling) bc).isModelIn(currentModel)) {
          al.add(bc);
        }
      }
    }

    // now we have got a list of all couplings which have the currentModel as
    // target model

    // get rid of all those couplings
    for (BasicCoupling bc : al) {
      removeCoupling(bc);
    }

    for (BasicCoupling bc : al) {
      if (bc instanceof Coupling) {
        ((Coupling) bc).setModel2(newModel);
      }
      /*
       * if (bc instanceof ClassCoupling) { if (((ClassCoupling) bc).model2 ==
       * currentModel.getClass()) { } }
       */
      if (bc instanceof MultiCoupling) {
        String portName =
            ((MultiCoupling) bc).getTarget(currentModel).getPortName();
        ((MultiCoupling) bc).removeTarget(currentModel);

        ((MultiCoupling) bc).addTarget(new MultiCouplingTarget(newModel,
            portName));
      }
      addCoupling(bc);
    }

  }

  /**
   * Returns a list of all couplings in this set.
   * 
   * @return string containing all couplings
   */
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (BasicCoupling basicCoupling : getVelements()) {
      result.append(basicCoupling.toString());
      result.append("\n");
    }
    return result.toString();
  }

}
