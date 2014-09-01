/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.formalism;

import org.jamesii.core.base.InformationObject;

/**
 * The Class Formalism. Instances of this class summarize information about the
 * formalism. It can be used, e.g., by user interfaces to return readable names
 * and comments to a user.
 * 
 * <br>
 * The {@link #timeBase}, {@link #timeProgress}, and
 * {@link #systemSpecification} attributes van be used to retrieve a simple
 * ontology, if there are more than one formalism in a system. This information
 * can be used, e.g., to guide a potential user.
 * 
 * @author Jan Himmelspach
 */
public class Formalism extends InformationObject {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4085920997518928479L;

  /** The acronym. */
  private String acronym;

  /** The comment. */
  private String comment;

  /** The name. */
  private String name;

  /** The time base. */
  private TimeBase timeBase;

  /** The system specification. */
  private SystemSpecification systemSpecification;

  /** The time progress. */
  private TimeProgress timeProgress;

  /**
   * The Enum TimeBase. Can be used to indicate whether the time base is of
   * continuous or of discrete nature.
   */
  public static enum TimeBase {
    /** The continuous time, typically represented by a real number. */
    CONTINUOUS,
    /** The discrete time, typically represented by an ordinal number. */
    DISCRETE
  }

  /**
   * The Enum SystemSpecification. Can be used to indicate whether the model is
   * a continuous, a discrete or hybrid picture of the system, if this formalism
   * is used.
   */
  public static enum SystemSpecification {

    /** The continuous modeling paradigm -- differential equations based. */
    CONTINUOUS,

    /** The discrete modeling paradigm -- stepwise / event driven view. */
    DISCRETE,
    /**
     * The hybrid modeling paradigm -- a combination of continuous and discrete
     * modeling.
     */
    HYBRID
  }

  /**
   * The Enum TimeProgress. Can be used to indicate whether time in the model is
   * increased by a constant value (stepwise), or by time stamps of arbitrarily
   * distributed events.
   */
  public static enum TimeProgress {
    /**
     * The stepwise time increment. The step size is usually fixed (at least in
     * the discrete modeling paradigm).
     */
    STEPWISE,
    /**
     * The event based time increment. The step size depends on the time
     * difference from the event processed last to the next one with the minimal
     * time stamp.
     */
    EVENT
  }

  /**
   * The Constructor.
   * 
   * @param acronym
   *          the acronym
   * @param name
   *          the name
   * @param comment
   *          the comment
   * @param ident
   *          the ident
   * @param tBase
   *          the time base of the formalism
   * @param sysSpec
   *          the kind of system specification
   * @param tProgress
   *          the kind of time progress
   */
  public Formalism(String ident, String acronym, String name, String comment,
      TimeBase tBase, SystemSpecification sysSpec, TimeProgress tProgress) {
    super(ident);
    this.acronym = acronym;
    this.name = name;
    this.comment = comment;
    this.timeBase = tBase;
    this.systemSpecification = sysSpec;
    this.timeProgress = tProgress;
  }

  /**
   * Gets the acronym.
   * 
   * @return the acronym
   */
  public String getAcronym() {
    return acronym;
  }

  /**
   * Gets the comment.
   * 
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  @Override
  public String getInfo() {
    return name + " (" + acronym + ") formalism\n" + comment;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the time base used in the modeling formalism. Typically on out of the
   * {@link TimeBase} enumeration types. Maybe null if not specified.
   * 
   * @return the time base
   */
  public TimeBase getTimeBase() {
    return timeBase;
  }

  /**
   * Gets the time progress. Typically on out of the {@link TimeProgress}
   * enumeration types. Maybe null if not specified.
   * 
   * @return the time progress
   */
  public TimeProgress getTimeProgress() {
    return timeProgress;
  }

  /**
   * Gets the system specification. Typically on out of the
   * {@link SystemSpecification} enumeration types. Maybe null if not specified.
   * 
   * @return the system specification
   */
  public SystemSpecification getSystemSpecification() {
    return systemSpecification;
  }

}
