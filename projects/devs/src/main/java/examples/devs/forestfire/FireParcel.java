/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;


import java.io.Serializable;

import org.jamesii.core.util.misc.Pair;

/**
 * The Class FireParcel.
 * 
 * @author Jan Himmelspach
 */
public class FireParcel implements Cloneable, Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2570671564223215504L;

  /** The sender. */
  public Pair<Integer, Integer> sender;

  /** The value. */
  public int value;

  /**
   * Instantiates a new fire parcel.
   * 
   * @param sender
   *          the sender
   * @param value
   *          the value
   */
  public FireParcel(Pair<Integer, Integer> sender, int value) {
    super();

    this.sender = sender;
    this.value = value;
  }
}
