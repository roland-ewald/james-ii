/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

/**
 * Edge for a {@link SimpleGraph}.
 * 
 * @author Roland Ewald Date: 12.12.2006
 */
public class SimpleEdge extends AnnotatedEdge<Integer, Double, Object> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6880750770909297579L;

  /**
   * The Constructor.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   */
  public SimpleEdge(Integer first, Integer second) {
    super(first, second);
  }

  /**
   * The Constructor.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   * @param label
   *          the label
   */
  public SimpleEdge(Integer first, Integer second, Double label) {
    super(first, second, label);
  }

  /**
   * Default constructor.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   * @param label
   *          the label
   * @param annotation
   *          the annotation
   */
  public SimpleEdge(Integer first, Integer second, Double label,
      Object annotation) {
    super(first, second, label, annotation);
  }

}
