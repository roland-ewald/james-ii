/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.id;

/**
 * Interface to be implemented by those classes providing a unique identifier.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IUniqueIdentifier {

  /**
   * Get the unique identifier. A unique identifier contains parts which should
   * make the identifier unique, even cross instances of the virtual machine.
   * 
   * @return the unique identifier
   */
  IUniqueID getUniqueIdentifier();
}
