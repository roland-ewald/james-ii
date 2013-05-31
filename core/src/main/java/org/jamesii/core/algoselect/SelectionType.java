/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.algoselect;

/**
 * Types of algorithm selection.
 * 
 * @author Roland Ewald
 */
public enum SelectionType {

  /** The SINGLE mode. Ignore surrounding algorithms. */
  SINGLE,

  /** The TREE mode. Select whole tree of algorithms at once. */
  TREE,

  /** The DISABLED mode. Do not use automatic selection. */
  DISABLED
}
