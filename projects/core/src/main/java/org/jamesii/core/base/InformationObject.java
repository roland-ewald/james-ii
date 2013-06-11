/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.base;

/**
 * The Class InformationObject. Base class for objects containing information
 * about something, typically identified by the ident attribute contained in
 * here.
 * 
 * @author Jan Himmelspach
 */
public abstract class InformationObject extends Entity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3725485951116907557L;

  /** The ident. */
  private String ident;

  /**
   * Instantiates a new information object.
   * 
   * @param ident
   *          the ident
   */
  public InformationObject(String ident) {
    super();
    this.ident = ident;
  }

  /**
   * Gets the contained info this object has been created for.
   * 
   * @return the info
   */
  public abstract String getInfo();

  /**
   * Sets the ident.
   * 
   * @param ident
   *          the new ident
   */
  public final void setIdent(String ident) {
    this.ident = ident;
  }

  /**
   * Gets the ident.
   * 
   * @return the ident
   */
  public String getIdent() {
    return ident;
  }

}
