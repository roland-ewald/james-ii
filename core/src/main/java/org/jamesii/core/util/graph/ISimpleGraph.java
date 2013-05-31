/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

/**
 * Shorter definition of a simple graph. This is a graph with {@link Integer}
 * vertices, {@link Double} labels, and {@link Object} annotations.
 * 
 * @author Roland Ewald
 */
public interface ISimpleGraph
    extends
    IAnnotatedGraph<Integer, AnnotatedEdge<Integer, Double, Object>, Double, Double, Object, Object> {

}
