/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import org.joone.engine.BiasedLinearLayer;
import org.joone.engine.ContextLayer;
import org.joone.engine.GaussLayer;
import org.joone.engine.Layer;
import org.joone.engine.LinearLayer;
import org.joone.engine.LogarithmicLayer;
import org.joone.engine.RbfGaussianLayer;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.SineLayer;
import org.joone.engine.TanhLayer;

/**
 * Enumeration for available types of (hidden) layers.
 * 
 * @author Roland Ewald
 * 
 */
public enum NNLayer {

  /** Linear layer. */
  LINEAR,

  /** Sigmoid layer. */
  SIGMOID,

  /** Radial Basis Function (RBF). */
  RBF_GAUSSIAN,

  /** Biased-linear layer. */
  BIASED_LINEAR,

  /** Linear with connection between out- and input. */
  CONTEXT,

  /** Gaussian curve. */
  GAUSS,

  /** Logarithmic function. */
  LOGARITHMIC,

  /** Sine as transfer function. */
  SINE,

  /** Hyperbolic tangent as transfer function. */
  TANH;

  /**
   * New instance.
   * 
   * @param layerType
   *          the layer type
   * @return the layer
   */
  public static Layer newInstance(NNLayer layerType) {
    switch (layerType) {
    case LINEAR:
      return new LinearLayer();
    case SIGMOID:
      return new SigmoidLayer();
    case RBF_GAUSSIAN:
      return new RbfGaussianLayer();
    case BIASED_LINEAR:
      return new BiasedLinearLayer();
    case CONTEXT:
      return new ContextLayer();
    case GAUSS:
      return new GaussLayer();
    case LOGARITHMIC:
      return new LogarithmicLayer();
    case SINE:
      return new SineLayer();
    case TANH:
      return new TanhLayer();
    default:
      return new LinearLayer();
    }
  }

  /**
   * Get type by a number (to support integer parameter).
   * 
   * @param index
   *          the index of the layer type
   * @return the corresponding layer type
   */
  public static NNLayer getLayerType(int index) {
    return values()[index];
  }
}
