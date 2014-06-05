/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;


import java.util.Iterator;
import java.util.Set;

import org.jamesii.core.util.collection.IElementSet;

/**
 * The interface {@link ILooseCouplingSet}.
 * 
 * @author Alexander Steiniger
 * 
 */
public interface ILooseCouplingSet extends IElementSet<LooseCoupling> {

  /**
   * Adds the given coupling to the internal set of loose couplings. If there is
   * already such an coupling in the internal set, the call will be ignored.
   * 
   * @param coupling
   *          the loose coupling that shall be added to the set
   */
  void addLooseCoupling(LooseCoupling coupling);

  /**
   * Adds the given coupling specified by the names of the source and target
   * ports to the internal set of loose couplings. If there is already such an
   * coupling in the internal set, the call will be ignored.
   * 
   * @param source
   *          the name of the source port of the loose coupling that shall be
   *          added to the set
   * @param target
   *          the name of the target port of the loose coupling that shall be
   *          added to the set
   */
  void addLooseCoupling(String source, String target);

  /**
   * Checks whether the internal set of loose couplings already contains the
   * given coupling or not.
   * 
   * @param coupling
   *          the loose coupling to check
   * @return <code>true</code> if the given coupling is in the internal set,
   *         <code>false</code> otherwise
   */
  boolean contains(LooseCoupling coupling);

  /**
   * Checks whether the internal set of loose couplings already contains the
   * given coupling specified by the names of the source and target port or not.
   * 
   * @param source
   *          the name of the source port of the loose coupling to check
   * @param target
   *          the name of the target port of the loose coupling to check
   * @return <code>true</code> if the given coupling is in the internal set,
   *         <code>false</code> otherwise
   */
  boolean contains(String source, String target);

  /**
   * Gets a loose coupling specified by the given names of source and target
   * port from the internal set of loose couplings if there is such a coupling.
   * If there is no such loose coupling <code>null</code> is returned.
   * 
   * @param source
   *          the name of the source port to get the loose coupling for
   * @param target
   *          the name of the target port to get the loose coupling for
   * @return the loose coupling from the internal list if there is one,
   *         <code>null</code> otherwise
   */
  LooseCoupling get(String source, String target);

  /**
   * Gets the coupling iterator for all loose couplings whose source ports
   * matches one of those in the given set of port names.
   * 
   * @param portNames
   *          the source port names of the loose couplings to get the iterator
   *          for
   * @return the iterator
   */
  Iterator<LooseCoupling> getCouplingsIterator(Set<String> portNames);

  /**
   * Removes the given coupling from the internal set of loose couplings. If
   * such a coupling does not exist, the call will be ignored.
   * 
   * @param coupling
   *          the loose coupling that shall be removed from the internal
   *          coupling set
   */
  void removeCoupling(LooseCoupling coupling);

  /**
   * Removes the given coupling specified by the names of the source and target
   * port from the internal set of loose couplings. If such a coupling does not
   * exist, the call will be ignored.
   * 
   * @param source
   *          the name of the source port of the loose coupling that shall be
   *          removed from the internal coupling set
   * @param target
   *          the name of the target port of the loose coupling that shall be
   *          removed from the internal coupling set
   */
  void removeCoupling(String source, String target);

  /**
   * Remove all couplings from the internal set of loose couplings in which the
   * given port name is involved, either as source or as target port.
   * 
   * @param portName
   *          the port name to remove all associated couplings for
   */
  void removeCouplings(String portName);

}
