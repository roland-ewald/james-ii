/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.IActionAction;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 * 
 */
public class IActionActionTest extends TestCase {
  /**
   * The Class TestAction.
   */
  static class TestAction extends AbstractAction {

    /**
     * The flag indicating whether action was executed.
     */
    private boolean executed = false;

    /**
     * Instantiates a new test action.
     */
    public TestAction() {
      super(null, null, new String[0], null);
    }

    @Override
    public void execute() {
      executed = true;
    }

  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.IActionAction#IActionAction(org.jamesii.gui.application.action.IAction)}
   * .
   */
  public final void testIActionAction() {
    TestAction ta = new TestAction();
    IActionAction a = new IActionAction(ta);
    assertNotNull(a);

    Icon i =
        new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB));

    // see whether label changes are propagated
    ta.setLabel("test123");
    assertEquals("test123", a.getValue(Action.NAME));
    ta.setLabel("test");
    assertEquals("test", a.getValue(Action.NAME));

    // see whether enable changes are propagated to the IActionAction
    ta.setEnabled(!ta.isEnabled());
    assertTrue(a.isEnabled() == ta.isEnabled());
    ta.setEnabled(!ta.isEnabled());
    assertTrue(a.isEnabled() == ta.isEnabled());

    // see whether toggle state is propagated to the IActionAction
    ta.setToggleOn(true);
    assertTrue(Boolean.TRUE.equals(a.getValue(Action.SELECTED_KEY)));
    ta.setToggleOn(false);
    assertTrue(Boolean.FALSE.equals(a.getValue(Action.SELECTED_KEY)));

    // see whether enable changes are propagated to the IAction
    a.setEnabled(!a.isEnabled());
    assertTrue(a.isEnabled() == ta.isEnabled());
    a.setEnabled(!a.isEnabled());
    assertTrue(a.isEnabled() == ta.isEnabled());

    // see whether label changes are propagated to the IAction
    a.putValue(Action.NAME, "Hello");
    assertEquals("Hello", ta.getLabel());
    a.putValue(Action.NAME, "World");
    assertEquals("World", ta.getLabel());

    a.putValue(Action.SMALL_ICON, i);
    // assertEquals("Should it be propagated to ta, since there is no setIcon method yet?",
    // i, ta.getIcon());

    // see whether toggle state is propagated to the IAction
    a.putValue(Action.SELECTED_KEY, false);
    assertFalse(ta.isToggleOn());
    a.putValue(Action.SELECTED_KEY, true);
    assertTrue(ta.isToggleOn());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.IActionAction#actionPerformed(java.awt.event.ActionEvent)}
   * .
   */
  public final void testActionPerformed() {
    // check whether the execute method of the IAction object is called when
    // calling actionPerformed on the Action
    TestAction ta = new TestAction();
    IActionAction a = new IActionAction(ta);
    assertNotNull(a);

    assertFalse(ta.executed);
    a.actionPerformed(null);
    assertTrue(ta.executed);
  }

}
