/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;


import java.util.Iterator;
import java.util.List;

import org.jamesii.core.util.collection.list.ISelector;
import org.jamesii.core.util.collection.list.StandardSelector;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Class MultiCoupling.
 * 
 * This class contains the functionality of MultiCouplings: This type of
 * Coupling connects a source-model and one of its outputports to a set of
 * submodels and inputports. The associated Selector specifies the number of
 * items to select and selects the receivers of a message send over this
 * MultiCoupling.
 * 
 * @author Jan Himmelspach
 * @author Christian Ober
 */
public class MultiCoupling extends BasicCoupling {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 1667603454782735236L;

  /** The associated {@link ISelector} to select several Items. */
  private ISelector<IBasicDEVSModel> selector;

  /**
   * This HashMap holds the set of targetmodels: key = model --- value =
   * portname as string.
   */
  private MultiCouplingTargetList targets = new MultiCouplingTargetList();

  /**
   * Creates a new coupling object - from the given.
   * 
   * @param model1
   *          The sourcemodel.
   * @param port1
   *          The sourceport.
   * @param targets
   *          The list of targetmodels and -ports.
   * @param selector
   *          The associated {@link ISelector}.
   * @param name
   *          The name of the MultiCoupling to create.
   * 
   * @model1,
   * @port1.
   */
  public MultiCoupling(IBasicDEVSModel model1, IPort port1,
      MultiCouplingTargetList targets, String name,
      ISelector<IBasicDEVSModel> selector) {
    super(model1, port1);
    this.targets = targets;
    if (selector != null) {
      this.selector = selector;
    } else {
      this.selector = new StandardSelector<>();
    }
    this.setName(name);
  }

  /**
   * Adds a target to this Mutlicoupling.<br>
   * Note: No check for overwriting is done!!! The element will be overwritten
   * silently.
   * 
   * @param target
   *          The MultiCouplingTarget to add.
   */
  public void addTarget(MultiCouplingTarget target) {
    targets.addTarget(target);
  }

  /**
   * Adds the target list.
   * 
   * @param targetsToAdd
   *          the targets to add
   */
  public void addTargetList(MultiCouplingTargetList targetsToAdd) {
    Iterator<IBasicDEVSModel> it = targetsToAdd.keyIterator();
    while (it.hasNext()) {
      this.targets.addTarget(targetsToAdd.getTarget(it.next()));
    }
  }

  /**
   * Compares to couplings and returns<br>
   * 0 if the source and target models and ports match<br>
   * 1 if model1 > model2 or (model1 = model) and port1 > port2<br>
   * -1 if model1 < model2 or (model1 = model) and port1 < port2<br>
   * .
   * 
   * @param o
   *          The Object to be compared to.
   * 
   * @return 0 if the source and target models and ports match<br>
   *         1 if model1 > model2 or (model1 = model) and port1 > port2<br>
   *         -1 if model1 < model2 or (model1 = model) and port1 < port2<br>
   */
  @Override
  public int compareTo(BasicCoupling o) {
    if (o instanceof Coupling) {
      int i = getModel1().compareTo(o.getModel1());
      // int j = ((Comparable)getModel2()).compareTo(((Coupling)o).getModel2());
      int j = 0;
      int k = getPort1().compareTo(o.getPort1());
      // int l = ((Comparable)getPort2()).compareTo(((Coupling)o).getPort2());
      int l = 0;
      if ((i == j) && (i == k) && (i == l) && (i == 0)) {
        return 0;
      } else if ((i > j) || (i == j) && (k > l)) {
        return 1;
      } else {
        return -1;
      }
    }
    return 0;
  }

  /**
   * Retrieves the {@link ISelector} of this ClassCoupling.
   * 
   * @return The {@link ISelector} of this ClassCoupling.
   */
  public ISelector<IBasicDEVSModel> getSelector() {
    return this.selector;
  }

  /**
   * Returns the target entry of the given model.
   * 
   * @param model
   *          the model
   * 
   * @return the target
   */
  public MultiCouplingTarget getTarget(IBasicDEVSModel model) {
    return targets.getTarget(model);
  }

  /**
   * Returns the MultiCouplingTargetList of the MultiCoupling.
   * 
   * @return The targets of the MultiCoupling.
   */
  public MultiCouplingTargetList getTargets() {
    return targets;
  }

  /**
   * Method to retrieve the targets as a vector (needed to use "Selector").
   * 
   * @return The MultiCouplingTargets as Vector.
   */
  public List<IBasicDEVSModel> getTargetsAsArrayList() {
    return targets.getTargetListAsArrayList();
  }


  /**
   * Checks whether an IBasicDEVSModel is in the MultiCouplingTargetList of the
   * MultiCoupling.
   * 
   * @param model
   *          The model to look for.
   * 
   * @return <code>true</code> if the model is in the MultiCouplingTargetList.
   *         <code>false</code> else.
   */
  public boolean isModelIn(IBasicDEVSModel model) {
    if (targets.isModelIn(model)) {
      return true;
    }
    return false;
  }

  /**
   * Removes a IBasicDEVSModel from the MultiCoupling. Note: Only one port can
   * be entered into the list of targets -> only this one can be removed!!!<br>
   * Note: No check is done whether the given model exists in the list or not.
   * 
   * @param model
   *          The IBasicDEVSModel to remove.
   */
  public void removeTarget(IBasicDEVSModel model) {
    targets.removeTarget(model);
  }

  /**
   * Removes the target list.
   * 
   * @param targetsToRemove
   *          the targets to remove
   */
  public void removeTargetList(MultiCouplingTargetList targetsToRemove) {
    Iterator<IBasicDEVSModel> it = targetsToRemove.keyIterator();
    while (it.hasNext()) {
      this.targets.removeTarget(targetsToRemove.getTarget(it.next()));
    }
  }

  /**
   * Sets the {@link ISelector} for this ClassCoupling.
   * 
   * @param selector
   *          The new {@link ISelector}.
   */
  public void setSelector(ISelector<IBasicDEVSModel> selector) {
    this.selector = selector;
  }

  /**
   * Calculates the string-representation of the MultiCoupling.
   * 
   * @return The string-representation of the MultiCoupling.
   */
  @Override
  public String toString() {

    StringBuffer buff = new StringBuffer();
    buff.append(getModel1().getName());
    buff.append(":");
    buff.append(getPort1().getName());
    buff.append(" - (");
    buff.append(targets.toString());
    buff.append(")");
    return buff.toString();

  }
}
