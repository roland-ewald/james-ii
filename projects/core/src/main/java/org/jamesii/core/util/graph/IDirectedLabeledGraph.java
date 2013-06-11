/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

/**
 * This interface is implemented by graphs which are directed and labelled.
 * 
 * @see IDirectedGraph
 * @see ILabeledGraph
 * 
 * @param <V>
 *          type of the vertices
 * @param <E>
 *          type of the edges (must extend LabeledEdge)
 * @param <LV>
 *          type of extra data (label) to be stored at the vertices
 * @param <LE>
 *          type of extra data (label) to be stored at the edges
 * 
 * @author Jan Himmelspach
 */
public interface IDirectedLabeledGraph<V, E extends LabeledEdge<V, LE>, LV, LE>
    extends ILabeledGraph<V, E, LV, LE>, IDirectedGraph<V, E> {

}
