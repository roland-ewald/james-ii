/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devs;

import org.jamesii.core.model.AbstractState;

import model.devscore.IBasicAtomicModel;

/**
 * The Interface IAtomicModel.
 * 
 * @author Jan Himmelspach
 */
public interface IAtomicModel<S extends AbstractState> extends
    IBasicAtomicModel<S> {

}
