/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

/**
 * An edge for the {@link AnnotatedGraph}.
 * 
 * @param <V>
 *          type of vertices
 * @param <L>
 *          type f label
 * @param <N>
 *          type of annotation
 * 
 *          Date: 12.12.2006
 * @author Roland Ewald
 */
public class AnnotatedEdge<V, L, N> extends LabeledEdge<V, L> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7745195283798378827L;

  /** Annotation to be stored. */
  private N annotation = null;

  /**
   * Minimal constructor with vertices, without label or annotation.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   */
  public AnnotatedEdge(V first, V second) {
    super(first, second);
  }

  /**
   * The Constructor without annotation, but with label.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   * @param label
   *          the label
   */
  public AnnotatedEdge(V first, V second, L label) {
    super(first, second, label);
  }

  /**
   * Default constructor with label and annotation.
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
  public AnnotatedEdge(V first, V second, L label, N annotation) {
    super(first, second, label);
    this.annotation = annotation;
  }

  /**
   * Get annotation.
   * 
   * @return annotation
   */
  public N getAnnotation() {
    return annotation;
  }

  /**
   * Set annotation.
   * 
   * @param annotation
   *          the annotation
   */
  public void setAnnotation(N annotation) {
    this.annotation = annotation;
  }
}
