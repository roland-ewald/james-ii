package org.jamesii.core.base;

import org.jamesii.core.base.NamedEntity;

/**
 * The Class EntityTest.
 * 
 * @author Jan Himmelspach
 */
public class NamedEntityTest extends EntityTest {

  /**
   * Test get complete info string.
   */
  @Override
  public void testGetCompleteInfoString() {
    assertTrue(new NamedEntity("").getCompleteInfoString().compareTo("") == 0);
    assertTrue(new NamedEntity().getCompleteInfoString() == null);
    assertTrue(new NamedEntity("test name").getCompleteInfoString().compareTo(
        "test name") == 0);
  }

  /**
   * Test constructor.
   */
  public void testConstructor() {
    assertTrue(new NamedEntity("").getName().compareTo("") == 0);
    assertTrue(new NamedEntity().getName() == null);
    assertTrue(new NamedEntity("test name").getName().compareTo("test name") == 0);
  }

  /**
   * Test set name.
   */
  public void testSetName() {
    NamedEntity ne = new NamedEntity();
    assertTrue(ne.getName() == null);
    ne.setName("test name");
    assertTrue(ne.getName().compareTo("test name") == 0);
    ne.setName("");
    assertTrue(ne.getName().compareTo("") == 0);
    ne.setName("test name2");
    ne.setName("test name3");
    ne.setName("test name4");
    assertTrue(ne.getName().compareTo("test name4") == 0);
  }

  /**
   * Test compare to.
   */
  public void testCompareTo() {
    NamedEntity ne = new NamedEntity();
    assertTrue(ne.compareTo(ne) == 0);
    NamedEntity ne2 = new NamedEntity();
    assertTrue(ne.compareTo(ne2) == 0);
    ne.setName("test name 1");
    assertTrue(ne.compareTo(ne2) != 0);
    assertTrue(ne2.compareTo(ne) != 0);
    ne2.setName("test name 2");
    assertTrue(ne.compareTo(ne2) != 0);
    assertTrue(ne2.compareTo(ne) != 0);
    ne2.setName("test name 1");
    assertTrue(ne.compareTo(ne2) == 0);
    assertTrue(ne2.compareTo(ne) == 0);
  }

}
