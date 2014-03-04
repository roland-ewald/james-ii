/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;

import org.hibernate.Session;
import org.jamesii.asf.database.hibernate.SelectorType;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;


/**
 * Tests {@link SelectorType}.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class TestSelectorType extends TestSelectionDBEntity<SelectorType> {

  static final Class<? extends PerformancePredictorGeneratorFactory> SELECTOR_GENERATOR_FACTORY =
      PerformancePredictorGeneratorFactory.class;

  public TestSelectorType() {
    super(true);
  }

  public TestSelectorType(Session session) {
    super(session);
  }

  @Override
  protected void changeEntity(SelectorType entity) {
    entity.setName("Another selector type");
  }

  @Override
  protected void checkEquality(SelectorType entity) {
    assertEquals(SELECTOR_GENERATOR_FACTORY,
        entity.getSelectorGeneratorFactory());
  }

  @Override
  protected SelectorType getEntity(String name) {
    SelectorType st = new SelectorType();
    st.setName(name);
    st.setSelectorGeneratorFactory(SELECTOR_GENERATOR_FACTORY);
    return st;
  }

}
