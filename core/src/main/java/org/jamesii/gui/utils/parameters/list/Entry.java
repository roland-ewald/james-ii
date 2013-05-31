/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.list;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Represents a single entry consisting of a factory class name and the
 * parameters associated with it.
 * 
 * @author Johannes Rössel
 */
public class Entry {
  /** Backing field for the factory name property. */
  private String factoryName;

  /** Backing field for the parameters property. */
  private ParameterBlock parameters;

  public Entry() {
  }

  public Entry(String factoryName, ParameterBlock parameters) {
    this();
    this.factoryName = factoryName;
    this.parameters = parameters;
  }

  /**
   * Gets the factory class name.
   * 
   * @return The factory class name.
   */
  public String getFactoryName() {
    return factoryName;
  }

  /**
   * Sets the factory class name.
   * 
   * @param factoryName
   *          The new factory class name.
   */
  public void setFactoryName(String factoryName) {
    this.factoryName = factoryName;
  }

  /**
   * Gets the parameter block.
   * 
   * @return The parameter block.
   */
  public ParameterBlock getParameters() {
    return parameters;
  }

  /**
   * Sets the parameter block.
   * 
   * @param parameters
   *          The new parameter block.
   */
  public void setParameters(ParameterBlock parameters) {
    this.parameters = parameters;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Entry)) {
      return false;
    }

    Entry entry = (Entry) obj;
    return this.getFactoryName().equals(entry.getFactoryName())
        && this.getParameters().equals(entry.getParameters());
  }

  @Override
  public int hashCode() {
    return this.getFactoryName().hashCode() ^ this.getParameters().hashCode();
  }

  @Override
  public String toString() {
    return factoryName != null ? BasicUtilities
        .makeFactoryClassNameReadable(factoryName) : "null";
  }
}