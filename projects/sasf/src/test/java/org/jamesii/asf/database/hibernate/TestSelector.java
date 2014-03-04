/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;

import java.net.URI;

import org.hibernate.Session;
import org.jamesii.asf.database.hibernate.Selector;

/**
 * The tests for the {@link Selector} entity.
 * 
 * @author Roland Ewald
 */
@Deprecated
public class TestSelector extends TestSelectionDBEntity<Selector> {

  /** The test uri. */
  static URI testURI = null;

  /** The string representation. */
  static final String TEST_URI_TEXT = "this://is.a.test";

  /** The Constant TEST_CODE. */
  static final String TEST_CODE = "code";

  /** The Constant TEST_PARAMETERS. */
  static final String TEST_PARAMETERS = "parameters";

  /**
   * Instantiates a new test selector.
   */
  public TestSelector() {
    super(true);
  }

  /**
   * Instantiates a new test selector.
   * 
   * @param s
   *          the session
   */
  public TestSelector(Session s) {
    super(s);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    testURI = new URI(TEST_URI_TEXT);
  }

  @Override
  protected void changeEntity(Selector entity) {
    entity.setCode("this is new code");
    entity.setParameters("this is a new parameter");
  }

  @Override
  protected void checkEquality(Selector entity) {
    assertNotNull(entity.getSelectorType());
    assertEquals(TEST_CODE, entity.getCode());
    assertEquals(TEST_PARAMETERS, entity.getParameters());
    assertEquals(testURI, entity.getUri());
  }

  @Override
  protected Selector getEntity(String name) throws Exception {
    Selector selector = new Selector();
    selector.setName(name);
    TestSelectorType tst = new TestSelectorType(session);
    selector.setSelectorType(tst.createEntity("selector type for selector "
        + name));
    selector.setUri(testURI);
    selector.setCode(TEST_CODE);
    selector.setParameters(TEST_PARAMETERS);
    return selector;
  }
}
