/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.plugintype;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Abstract model factory. Sorts out all factories that are not able to create
 * empty models.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractModelFactory extends AbstractFactory<ModelFactory> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 5186689980653849403L;

  /**
   * Instantiates a new abstract model factory.
   */
  public AbstractModelFactory() {
    super();
    addCriteria(new NoFactoriesWithoutModelCreation());
  }

}

/**
 * Filter all model factories that are not able to provide means for creation of
 * an empty model (that is, a default model).
 */
class NoFactoriesWithoutModelCreation extends FactoryCriterion<ModelFactory> {

  @Override
  public List<ModelFactory> filter(List<ModelFactory> factories,
      ParameterBlock parameter) {

    List<ModelFactory> filteredFacs = new ArrayList<>();

    for (ModelFactory factory : factories) {
      ISymbolicModel<?> model = null;
      try {
        model = factory.create();
      } catch (Throwable ex) {
        SimSystem.report(Level.SEVERE, "The factory "
            + factory.getClass().getName()
            + " caused the following exception on executing createEmptyModel:",
            ex);
        continue;
      }
      if (model != null) {
        filteredFacs.add(factory);
      }
    }

    return filteredFacs;
  }

}