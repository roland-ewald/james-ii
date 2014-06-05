/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.jamesii.core.util.collection.ElementSet;

/**
 * The class {@link LooseCouplingSet}.
 * 
 * @author Alexander Steiniger
 * 
 */
public class LooseCouplingSet extends ElementSet<LooseCoupling> implements
    ILooseCouplingSet {

  /**
   * serialization id
   */
  private static final long serialVersionUID = 2936294852364754367L;

  /**
   * @throws IllegalArgumentException
   *           if the given loose coupling that shall be added is
   *           <code>null</code>
   */
  @Override
  public void addLooseCoupling(LooseCoupling coupling) {
    // check if the loose coupling to add is null
    if (coupling == null) {
      throw new IllegalArgumentException(
          "The given loose coupling that shall be added to the internal coupling set is null.");
    }

    // check if given loose coupling is not already in the list
    if (!getVelements().contains(coupling)) {
      getVelements().add(coupling);
    }
  }

  @Override
  public void addLooseCoupling(String source, String target) {
    add(new LooseCoupling(source, target));
  }

  /**
   * @throws IllegalArgumentException
   *           if the given loose coupling to look for is <code>null</code>
   */
  @Override
  public boolean contains(LooseCoupling coupling) {
    // check if the loose coupling to look for is null
    if (coupling == null) {
      throw new IllegalArgumentException(
          "The given loose coupling to look for in the internal coupling set is null.");
    }

    return getVelements().contains(coupling);
  }

  @Override
  public boolean contains(String source, String target) {
    return getVelements().contains(new LooseCoupling(source, target));
  }

  /**
   * @throws IllegalArgumentException
   *           if at least one of the given arguments is <code>null</code>
   */
  @Override
  public LooseCoupling get(String source, String target) {
    // check if at least one of the given arguments is null
    if ((source == null) || (target == null)) {
      throw new IllegalArgumentException(
          "Source and/or target port name to get the loose coupling from the internal coupling set for are/is null.");
    }

    // iterate all loose couplings from the internal set
    for (LooseCoupling coupling : getVelements()) {
      // check if the given names matches the port names of the current coupling
      if (coupling.getSourcePort().equals(source)
          && coupling.getTargetPort().equals(target)) {
        return coupling;
      }
    }

    return null;
  }

  /**
   * @throws IllegalArgumentException
   *           if the given set of port names is <code>null</code>
   */
  @Override
  public Iterator<LooseCoupling> getCouplingsIterator(Set<String> portNames) {
    // check if the given list of port names is null
    if (portNames == null) {
      throw new IllegalArgumentException(
          "The list of portNames to get the coupling iterator for is null");
    }

    ArrayList<LooseCoupling> couplingsOfInterest =
        new ArrayList<>();
    // iterate all loose couplings
    for (LooseCoupling coupling : getVelements()) {
      // iterate all given port names to match
      for (String portName : portNames) {
        // check if current port name matches the source port name of the
        // current loose coupling
        if (coupling.getSourcePort().equals(portName)) {
          couplingsOfInterest.add(coupling);
          break;
        }
      }
    }

    // return the iterator for the couplings of interest
    return couplingsOfInterest.iterator();
  }

  /**
   * @throws IllegalArgumentException
   *           if the given loose coupling that shall be removed is
   *           <code>null</code>
   */
  @Override
  public void removeCoupling(LooseCoupling coupling) {
    // check if given coupling to remove is null
    if (coupling == null) {
      throw new IllegalArgumentException(
          "The given loose coupling that shall be removed from the intenral coupling set is null.");
    }

    getVelements().remove(coupling);
  }

  @Override
  public void removeCoupling(String source, String target) {
    getVelements().remove(new LooseCoupling(source, target));
  }

  /**
   * @throws IllegalArgumentException
   *           if the port name to remove all associated loose couplings for is
   *           <code>null</code>
   */
  @Override
  public void removeCouplings(String portName) {
    // check if given port name is null
    if (portName == null) {
      throw new IllegalArgumentException(
          "The port name to remove associated loose couplings for is null.");
    }

    // iterate all couplings
    ArrayList<LooseCoupling> couplingsToRemove = new ArrayList<>();
    for (LooseCoupling coupling : getVelements()) {
      // check if given name is involved in current coupling
      if (coupling.getSourcePort().equals(portName)
          || coupling.getTargetPort().equals(portName)) {
        couplingsToRemove.add(coupling);
      }
    }

    // remove associated couplings
    getVelements().removeAll(couplingsToRemove);
  }

}
