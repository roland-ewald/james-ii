/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.Set;

/**
 * Interface that represents a multiset, i.e. that this can contain multiple
 * equal keys. Note that, while it extends {@link java.util.Set}, classes
 * implementing this interface may violate the set semantics of just one
 * occurence of each element!
 * 
 * @author Stefan Leye
 * 
 * @param <E>
 *          type of the elements
 */
public interface IMultiSet<E> extends Set<E> {

}
