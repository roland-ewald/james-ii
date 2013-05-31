/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core;

import java.util.List;

import org.jamesii.core.Registry;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.rnggenerator.plugintype.AbstractRandomSeedGeneratorFactory;
import org.jamesii.core.model.plugintype.AbstractModelFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.eventset.SimpleEventQueueFactory;
import org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class TestRegistry. Tests the registry of JAMES II.
 * 
 * @author jan Himmelspach
 */
public class TestRegistry extends TestCase {

  /** The registry. */
  static volatile Registry reg = null;

  @Override
  protected void setUp() throws Exception {
    if (reg != null) {
      return;
    }
    reg = new Registry();
    reg.init();
  }

  @Override
  protected void tearDown() throws Exception {
    // reg = null;
  }

  /**
   * Test get info.
   */
  public void testGetInfo() {
    // might be empty but not null
    assertTrue(reg.getInfo() != null);
  }

  // public void testGetAbstractFactory() {
  // reg.getAbstractFactory(SimpleEventQueueFactory.class);
  // }

  /**
   * Test get abstract factory by name.
   */
  public void testGetAbstractFactoryByName() {
    assertTrue(
        "Is "
            + reg.getAbstractFactoryByName("org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory")
            + " and not the expected class ",
        reg.getAbstractFactoryByName("org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory") == AbstractEventQueueFactory.class);
    assertTrue(
        "Is "
            + reg.getAbstractFactoryByName("org.jamesii.core.model.plugintype.AbstractModelFactory")
            + " and not the expected class ",
        reg.getAbstractFactoryByName("org.jamesii.core.model.plugintype.AbstractModelFactory") == AbstractModelFactory.class);
  }

  /**
   * Test get abstract factory by plugin type name.
   */
  public void testGetAbstractFactoryByPluginTypeName() {
    assertTrue(AbstractRandomSeedGeneratorFactory.class.equals(reg
        .getAbstractFactoryByPluginTypeName("random seed generator plugins")));
  }

  /**
   * Test get abstract factory for base factory.
   */
  public void testGetAbstractFactoryForBaseFactory() {
    assertTrue(reg.getAbstractFactoryForBaseFactory(EventQueueFactory.class) == AbstractEventQueueFactory.class);
    assertTrue(!(reg
        .getAbstractFactoryForBaseFactory(SimpleEventQueueFactory.class) == AbstractEventQueueFactory.class));
  }

  /**
   * Test get base factory for abstract factory.
   */
  public void testGetBaseFactoryForAbstractFactory() {
    assertEquals(
        reg.getBaseFactoryForAbstractFactory(AbstractEventQueueFactory.class),
        EventQueueFactory.class);
    assertFalse(reg.getBaseFactoryForAbstractFactory(
        AbstractEventQueueFactory.class).equals(SimpleEventQueueFactory.class));
  }

  /**
   * Test get abstract factory for factory.
   */
  public void testGetAbstractFactoryForFactory() {
    assertTrue((reg.getAbstractFactoryForFactory(EventQueueFactory.class) == AbstractEventQueueFactory.class));
    assertTrue((reg.getAbstractFactoryForFactory(SimpleEventQueueFactory.class) == AbstractEventQueueFactory.class));
  }

  /**
   * Test get factories.
   */
  public void testGetFactories() {

    class MyFactory extends Factory<Object> {
      /** The Constant serialVersionUID. */
      private static final long serialVersionUID = 1L;

      @Override
      public Object create(ParameterBlock parameters) {
        return null;
      }
    }

    // this should be empty
    assertTrue(reg.getFactories(MyFactory.class).isEmpty());

    List<Factory<?>> fl = reg.getFactories(EventQueueFactory.class);
    assertTrue(fl != null);
    // there are event queues in the core, and thus this list should not be
    // empty ....
    assertTrue(fl.size() > 0);
  }

  /**
   * Test get factory.
   */
  public void testGetFactory() {
    assertTrue(reg.getFactory(AbstractEventQueueFactory.class, null) != null);
    ParameterBlock pb = new ParameterBlock();
    pb.setValue("org.jamesii.core97.util.eventqueue.SimpleEventQueueFactory");
    // if the evq requested is not available an alternative should be returned
    assertTrue(reg.getFactory(AbstractEventQueueFactory.class, pb) != null);
    // if the evq is available it should be returned ;-)
    pb.setValue(SimpleEventQueueFactory.class.getCanonicalName());
    assertTrue(reg.getFactory(AbstractEventQueueFactory.class, pb) instanceof SimpleEventQueueFactory);
  }

  /**
   * Test get factory class.
   */
  public void testGetFactoryClass() {
    assertEquals(SimpleEventQueueFactory.class,
        reg.getFactoryClass(SimpleEventQueueFactory.class.getCanonicalName()));
    assertNull(reg
        .getFactoryClass("org.jamesii.core.util.thisisnovalidpackage.SimpleEventQueueFactory"));
  }

  /**
   * Test get factory list.
   */
  public void testGetFactoryList() {

    List<EventQueueFactory> fl2 =
        reg.getFactoryList(AbstractEventQueueFactory.class, null);

    assertTrue(fl2 != null);
    assertTrue(fl2.size() > 0);

    List<Factory<?>> fl = reg.getFactories(EventQueueFactory.class);

    assertTrue("Sizes of these lists should be equal ", fl.size() == fl2.size());

    // all items in one list should be in the other
    for (int i = 0; i < fl.size(); i++) {
      assertTrue(fl2.contains(fl.get(i)));
    }

    for (int i = 0; i < fl2.size(); i++) {
      assertTrue(fl.contains(fl2.get(i)));
    }

  }

  /**
   * Test get factory or empty list.
   */
  public void testGetFactoryOrEmptyList() {
    List<EventQueueFactory> fl =
        reg.getFactoryOrEmptyList(AbstractEventQueueFactory.class, null);

    assertTrue(fl != null);
    assertTrue(fl.size() > 0);

    List<ProcessorFactory> fl2 =
        reg.getFactoryOrEmptyList(AbstractProcessorFactory.class, null);
    assertTrue(fl2 != null);

  }

  // /**
  // * Test get factory names.
  // */
  // public void testGetFactoryNames() {
  // // reg.getFactoryNames()
  // fail("Not yet implemented");
  // }
  //
  // /**
  // * Test get factory selection hook.
  // */
  // public void testGetFactorySelectionHook() {
  // fail("Not yet implemented");
  // }

  /**
   * Test get information.
   */
  public void testGetInformation() {
    // will never be null
    assertTrue(reg.getInformation("") != null);
  }

  // /**
  // * Test get initialized abstract factory.
  // */
  // public void testGetInitializedAbstractFactory() {
  // fail("Not yet implemented");
  // }

  /**
   * Test get known factory classes.
   */
  public void testGetKnownFactoryClasses() {
    assertTrue(reg.getKnownFactoryClasses() != null);
    assertTrue(reg.getKnownFactoryClasses().size() > 0);
  }

  /**
   * Test get plugin list.
   */
  public void testGetPluginList() {
    assertTrue(reg.getPluginList() != null);
    assertTrue(reg.getPluginList().length() > 0);
  }

  /**
   * Test get plugins.
   */
  public void testGetPlugins() {
    assertTrue(reg.getPlugins() != null);
    assertTrue(reg.getPlugins().size() > 0);
  }

  // /**
  // * Test get plugins class of qextends abstract factory of q.
  // */
  // public void testGetPluginsClassOfQextendsAbstractFactoryOfQ() {
  // fail("Not yet implemented");
  // }

  /**
   * Test get plugin type.
   */
  public void testGetPluginType() {
    assertTrue(reg.getPluginType(AbstractEventQueueFactory.class) != null);
  }

  // /**
  // * Test install factory selection hook.
  // */
  // public void testInstallFactorySelectionHook() {
  // fail("Not yet implemented");
  // }
  //
  // /**
  // * Test register factory.
  // */
  // public void testRegisterFactory() {
  // fail("Not yet implemented");
  // }
  //
  // /**
  // * Test register factory class.
  // */
  // public void testRegisterFactoryClass() {
  // fail("Not yet implemented");
  // }
  //
  // /**
  // * Test register formalism.
  // */
  // public void testRegisterFormalism() {
  // fail("Not yet implemented");
  // }
  //
  // /**
  // * Test get factory info.
  // */
  // public void testGetFactoryInfo() {
  // fail("Not yet implemented");
  // }
  //
  // /**
  // * Test get custom plugin directory.
  // */
  // public void testGetCustomPluginDirectory() {
  // fail("Not yet implemented");
  // }
  //
  // /**
  // * Test set custom plugin directory.
  // */
  // public void testSetCustomPluginDirectory() {
  // fail("Not yet implemented");
  // }

}
