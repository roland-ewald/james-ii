/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.model;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.formalism.Formalism.SystemSpecification;
import org.jamesii.core.model.formalism.Formalism.TimeBase;
import org.jamesii.core.model.formalism.Formalism.TimeProgress;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * A factory for bogus.
 * 
 * @author Roland Ewald
 * 
 */
public class BogusModelFactory extends ModelFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7990329561457673790L;

  @Override
  public ISymbolicModel<?> create() {
    return null;
  }

  @Override
  public Formalism getFormalism() {
    return new Formalism(
        "SASFTEST",
        "ASFTEST",
        "SASF Testing Formalism",
        "Artifial models for large-scale testing of simulation algorithm selection framework.",
        TimeBase.DISCRETE, SystemSpecification.DISCRETE, TimeProgress.EVENT);

  }

  @Override
  public List<Class<? extends IModel>> getSupportedInterfaces() {
    List<Class<? extends IModel>> interfaces = new ArrayList<>();
    interfaces.add(IBogusModel.class);
    return interfaces;
  }

}
