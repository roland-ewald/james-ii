/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import model.devscore.IBasicDEVSModel;

/**
 * The Class MultiCouplingTargetList. Class for handling target-elements (
 * {@link MultiCouplingTarget} of a MultiCoupling.
 * 
 * @author Christian Ober
 */
public class MultiCouplingTargetList implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3805425185525676889L;

  /** Holds the content of the list. */
  private Map<IBasicDEVSModel, MultiCouplingTarget> content =
      new HashMap<>();

  /**
   * Creates a new MultiCouplingTargetList. No double occurences of models are
   * allowed.
   */
  public MultiCouplingTargetList() {
    super();
  }

  /**
   * Adds a MultiCouplingTarget to the list. If the model already exists, the
   * according port will be overwritten.
   * 
   * @param target
   *          The new item to add.
   * 
   * @return <code>true</code> if the model already exists,<br>
   *         <code>false</code> else.
   */
  public boolean addTarget(MultiCouplingTarget target) {
    if (content.containsKey(target.getModel())) {
      content.put(target.getModel(), target);
      return true;
    }
    content.put(target.getModel(), target);
    return false;

  }

  /**
   * Retrieves the MultiCouplingTarget according to the given IBasicDEVSModel.
   * 
   * @param model
   *          The IBasicDEVSModel to look for.
   * 
   * @return The corresponding MultoCouplingTarget for the given
   *         IBasicDEVSModel.
   * 
   * @throws NoSuchElementException
   *           If the MultiCouplingTarget does not exist.
   * 
   * @todo handle RemoteException
   */
  public MultiCouplingTarget getTarget(IBasicDEVSModel model) {
    if (!content.containsKey(model)) {

      StringBuffer buff = new StringBuffer();
      buff.append("The Model: ");
      buff.append(model.getFullName());
      buff.append(" could no be found!");
      throw new NoSuchElementException(buff.toString());

    }
    return content.get(model);
  }

  /**
   * Gets the target.
   * 
   * @param key
   *          the key
   * 
   * @return the target
   */
  public MultiCouplingTarget getTarget(Object key) {
    return content.get(key);
  }

  /**
   * Retrieves the content of the underlying MultiCouplingTargetList as an
   * ArrayList (only the IBasicDEVSModels). This is needed for the Selector.
   * 
   * @return The content of the underlying MultiCouplingTargetList as an
   *         ArrayList of IBasicDEVSModels.
   */
  public List<IBasicDEVSModel> getTargetListAsArrayList() {
    return new ArrayList<>(content.keySet());
  }

  /**
   * Checks whether an IBasicDEVSModel is in the MultiCouplingTargetList.
   * 
   * @param model
   *          The model to look for.
   * 
   * @return <code>true</code> if the model is in the MultiCouplingTargetList.
   *         <code>false</code> else.
   */
  public boolean isModelIn(IBasicDEVSModel model) {
    if (content.containsKey(model)) {
      return true;
    }
    return false;

  }

  /**
   * Key iterator.
   * 
   * @return the iterator< i basic devs model>
   */
  public Iterator<IBasicDEVSModel> keyIterator() {
    return content.keySet().iterator();
  }

  /**
   * Removes the specified target from the list. Note: No check for the correct
   * port is done. Only the model is important, because no double occurences of
   * models are allowed.
   * 
   * @param target
   *          The target-model.
   * 
   * @throws NoSuchElementException
   *           If the specified MultiCouplingTarget does not exist.
   * 
   * @todo handle RemoteException
   */
  public void removeTarget(IBasicDEVSModel target) {
    if (content.containsKey(target)) {
      content.remove(target);
    } else {

      StringBuffer buff = new StringBuffer();
      buff.append("The Model: ");
      buff.append(target.getFullName());
      buff.append(" could no be found!");
      throw new NoSuchElementException(buff.toString());

    }
  }

  /**
   * Removes the specified target from the list.
   * 
   * @param target
   *          The item to remove.<br>
   *          Note: No check for the correct port is done. Only the model is
   *          important, because no double occurences of models are allowed.
   * 
   * @throws NoSuchElementException
   *           If the specified MultiCouplingTarget does not exist.
   */
  public void removeTarget(MultiCouplingTarget target) {
    if (content.containsKey(target.getModel())) {
      content.remove(target.getModel());
    } else {

      StringBuffer buff = new StringBuffer();
      buff.append("The Model: ");
      buff.append(target.getModel().getName());
      buff.append(" could no be found!");
      throw new NoSuchElementException(buff.toString());

    }
  }

  /**
   * Creates the string-representation of the content of the
   * MultiCouplingTargetList.
   * 
   * @return The string-representation of the content of the
   *         MultiCouplingTargetList.
   */
  @Override
  public String toString() {
    StringBuffer buff = new StringBuffer();
    Iterator<MultiCouplingTarget> keyIterator = content.values().iterator();
    boolean firstElement = true;

    while (keyIterator.hasNext()) {

      if (!firstElement) {
        buff.append(",");
      }
      MultiCouplingTarget target = keyIterator.next();
      buff.append(target.getModel().getFullName());
      buff.append(":");
      buff.append(target.getPortName());
    }

    return buff.toString();
  }

  /**
   * Value iterator.
   * 
   * @return the iterator< multi coupling target>
   */
  public Iterator<MultiCouplingTarget> valueIterator() {
    return content.values().iterator();
  }
}
