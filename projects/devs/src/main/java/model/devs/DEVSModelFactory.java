/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devs;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;

import model.devs.symbolic.SymbolicDEVSModel;

/**
 * A factory for creating DEVSModel objects.
 * 
 * @author Jan Himmelspach
 */
public class DEVSModelFactory extends ModelFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3933997297963877226L;

  @Override
  public ISymbolicModel<?> create() {
    return new SymbolicDEVSModel();
  }

  @Override
  public Formalism getFormalism() {
    return new DEVSFormalism();
  }

  @Override
  public List<Class<? extends IModel>> getSupportedInterfaces() {
    List<Class<? extends IModel>> suppInterfaces =
        new ArrayList<>();
    suppInterfaces.add(ICoupledModel.class);
    suppInterfaces.add(IAtomicModel.class);
    return suppInterfaces;
  }
}
