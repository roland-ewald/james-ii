/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.jamesii.gui.application.action.ActionIAction;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 */
public class ActionIActionTest extends TestCase {
  /**
   * @author Stefan Rybacki
   */
  static class TestAction extends AbstractAction {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 9222539707500981908L;

    /**
     * The flag indicating whether action was performed.
     */
    private boolean actionPerformed = false;

    /**
     * Instantiates a new test action.
     * 
     * @param label
     *          the label
     */
    public TestAction(String label) {
      super(label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      actionPerformed = true;
    }

  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#getLabel()}.
   */
  public final void testGetLabel() {
    TestAction ta = new TestAction("label1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);
    assertEquals("label1", a.getLabel());

    // check whether label is propagated from a to ta
    a.setLabel("label2");
    assertEquals("label2", ta.getValue(Action.NAME));
    assertEquals("label2", a.getLabel());

    // check whether label is propagated from ta to a
    ta.putValue(Action.NAME, "label3");
    assertEquals("label3", a.getLabel());
    assertEquals("label3", ta.getValue(Action.NAME));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#getDescription()} .
   */
  public final void testGetDescription() {
    TestAction ta = new TestAction("label1");
    ta.putValue(Action.LONG_DESCRIPTION, "description1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);
    assertEquals("description1", a.getDescription());

    // check whether description is propagated from ta to a
    ta.putValue(Action.LONG_DESCRIPTION, "description2");
    assertEquals("description2", a.getDescription());
    assertEquals("description2", ta.getValue(Action.LONG_DESCRIPTION));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#getIcon()}.
   */
  public final void testGetIcon() {
    TestAction ta = new TestAction("label1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);

    ta.putValue(Action.SMALL_ICON, null);
    assertNull(ta.getValue(Action.SMALL_ICON));
    assertNull(a.getIcon());

    Icon i =
        new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB));
    ta.putValue(Action.SMALL_ICON, i);
    assertEquals(i, ta.getValue(Action.SMALL_ICON));
    assertEquals(i, a.getIcon());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#getShortDescription()}
   * .
   */
  public final void testGetShortDescription() {
    TestAction ta = new TestAction("label1");
    ta.putValue(Action.SHORT_DESCRIPTION, "description1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);
    assertEquals("description1", a.getShortDescription());

    // check whether description is propagated from ta to a
    ta.putValue(Action.SHORT_DESCRIPTION, "description2");
    assertEquals("description2", a.getShortDescription());
    assertEquals("description2", ta.getValue(Action.SHORT_DESCRIPTION));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#isEnabled()}. Test
   * method for
   * {@link org.jamesii.gui.application.action.ActionIAction#setEnabled(boolean)}
   * .
   */
  public final void testIsEnabledSetEnabled() {
    TestAction ta = new TestAction("label1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);

    // check whether enabled is propagated from a to ta
    a.setEnabled(true);
    assertTrue(ta.isEnabled());
    assertTrue(a.isEnabled());
    a.setEnabled(false);
    assertFalse(ta.isEnabled());
    assertFalse(a.isEnabled());

    // check whether enabled is propagated from ta to a
    ta.setEnabled(true);
    assertTrue(ta.isEnabled());
    assertTrue(a.isEnabled());
    ta.setEnabled(false);
    assertFalse(ta.isEnabled());
    assertFalse(a.isEnabled());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#getKeyStroke()} .
   */
  public final void testGetKeyStroke() {
    TestAction ta = new TestAction("label1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);

    // check whether keystroke is propagate from ta to a
    ta.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
    assertEquals(KeyStroke.getKeyStroke("ctrl A"),
        KeyStroke.getKeyStroke(a.getKeyStroke()));
    ta.putValue(Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke("ctrl alt shift ENTER"));
    assertEquals(KeyStroke.getKeyStroke("ctrl alt shift ENTER"),
        KeyStroke.getKeyStroke(a.getKeyStroke()));
    ta.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("A"));
    assertEquals(KeyStroke.getKeyStroke("A"),
        KeyStroke.getKeyStroke(a.getKeyStroke()));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#getMnemonic()}.
   */
  public final void testGetMnemonic() {
    TestAction ta = new TestAction("label1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);

    // check whether keystroke is propagate from ta to a
    ta.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_0);
    assertEquals(KeyEvent.VK_0, a.getMnemonic().intValue());
    ta.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_1);
    assertEquals(KeyEvent.VK_1, a.getMnemonic().intValue());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#isToggleOn()}.
   */
  public final void testIsToggleOn() {
    TestAction ta = new TestAction("label1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);

    // check whether keystroke is propagate from ta to a
    ta.putValue(Action.SELECTED_KEY, false);
    assertEquals(false, a.isToggleOn());
    ta.putValue(Action.SELECTED_KEY, true);
    assertEquals(true, a.isToggleOn());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#ActionIAction(Action, String, String[], org.jamesii.gui.application.IWindow)}
   * .
   */
  public final void testActionIAction() {
    assertNotNull(new ActionIAction(new TestAction(""), "testAction",
        new String[0], null));

    try {
      new ActionIAction(new TestAction(""), "testAction", null, null);
      fail("Created IAction with paths == null");
    } catch (Exception e) {
    }
    try {
      new ActionIAction(null, "testAction", new String[0], null);
      fail("Created IAction from null Action");
    } catch (Exception e) {
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.action.ActionIAction#execute()}.
   */
  public final void testExecute() {
    TestAction ta = new TestAction("label1");
    ActionIAction a = new ActionIAction(ta, "testAction", new String[0], null);
    assertNotNull(a);

    assertFalse(ta.actionPerformed);
    a.execute();
    assertTrue(ta.actionPerformed);
  }

}
