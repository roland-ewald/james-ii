/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.plugintype;

import java.util.List;

import org.jamesii.core.algoselect.AlgorithmSelection;
import org.jamesii.core.algoselect.SelectionType;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Select an event queue factory.
 * 
 * @author Jan Himmelspach *
 */
@AlgorithmSelection(SelectionType.TREE)
public class AbstractEventQueueFactory extends
    AbstractFactory<EventQueueFactory> {

  /**
   * Sorts the remaining factories according to their efficiency index.
   * 
   * @author Jan Himmelspach
   * 
   */
  private static class EfficiencySortingCriteria extends
      FactoryCriterion<EventQueueFactory> {

    @Override
    public List<EventQueueFactory> filter(List<EventQueueFactory> factories,
        ParameterBlock parameter) {
      // TODO efficient sorting!!!!
      for (int i = 0; i < factories.size(); i++) {
        for (int j = i; j < factories.size(); j++) {
          if (factories.get(i).getEfficencyIndex() < factories.get(j)
              .getEfficencyIndex()) {
            EventQueueFactory help = factories.get(i);
            factories.set(i, factories.get(j));
            factories.set(j, help);
          }
        }
      }
      return factories;
    }

  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4444046611311030984L;

  /**
   * Instantiates a new abstract event queue factory.
   */
  public AbstractEventQueueFactory() {
    super();
    this.addCriteria(new EfficiencySortingCriteria());
    // TODO (general) add criterias!!!!
  }

}
