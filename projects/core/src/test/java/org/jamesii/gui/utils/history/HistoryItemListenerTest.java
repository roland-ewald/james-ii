/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import org.jamesii.gui.utils.history.History;
import org.jamesii.gui.utils.history.HistoryItemEvent;
import org.jamesii.gui.utils.history.IHistoryItemListener;

import junit.framework.TestCase;

/**
 * 
 * @author Enrico Seib
 * 
 */
public class HistoryItemListenerTest extends TestCase {

  /**
   * 
   * static class to test HistoryItemListener
   */
  static class HistItemListener implements IHistoryItemListener {

    // Flags
    /**
     * Flag for added value
     */
    boolean valueAdded = false;

    /**
     * Flag for changed value
     */
    boolean valueChanged = false;

    /**
     * Flag for removed value
     */
    boolean valueRemoved = false;

    /**
     * Flag for completely removed items with id
     */
    boolean idRemoved = false;

    /**
     * Flag for completely cleaned History Map
     */
    boolean cleaned = false;

    @Override
    public void cleaned(HistoryItemEvent event) {
      cleaned = true;

    }

    @Override
    public void idRemoved(HistoryItemEvent event) {
      idRemoved = true;

    }

    @Override
    public void valueAdded(HistoryItemEvent event) {
      valueAdded = true;
    }

    @Override
    public void valueChanged(HistoryItemEvent event) {
      valueChanged = true;
    }

    @Override
    public void valueRemoved(HistoryItemEvent event) {
      valueRemoved = true;

    }

  }

  /**
   * @param name
   */
  public HistoryItemListenerTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    History.clear();

  }

  /**
   * Test all listener methods
   */
  public final void testAllListener() {
    final HistItemListener listener = new HistItemListener();
    History.addListener(listener);

    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/werte/exp1.exp"));
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/werte/exp2.exp"));
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/Daten/daten1.dat"));
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/Daten/daten2.dat"));
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/Daten/daten3.dat"));
    History.putValueIntoHistory("org.jamesii",
        new String("C:/Daten/daten1.dat"));
    assertFalse(listener.valueChanged);
    assertFalse(listener.valueRemoved);
    assertFalse(listener.idRemoved);
    assertFalse(listener.cleaned);
    assertTrue(listener.valueAdded);

    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/werte/exp2.exp"));
    assertFalse(listener.valueRemoved);
    assertFalse(listener.idRemoved);
    assertFalse(listener.cleaned);
    assertTrue(listener.valueAdded);
    assertTrue(listener.valueChanged);

    History.removeValueFromHistory("org.jamesii.exp", "C:/werte/exp2.exp");
    assertFalse(listener.idRemoved);
    assertFalse(listener.cleaned);
    assertTrue(listener.valueAdded);
    assertTrue(listener.valueChanged);
    assertTrue(listener.valueRemoved);

    History.putValueIntoHistory("org.jamesii",
        new String("C:/Daten/daten2.dat"));
    History.putValueIntoHistory("org.jamesii",
        new String("C:/Daten/daten3.dat"));
    History.putValueIntoHistory("org.jamesii",
        new String("C:/Daten/daten4.dat"));
    History.removeIDfromHistory("org.jamesii");
    assertFalse(listener.cleaned);
    assertTrue(listener.valueAdded);
    assertTrue(listener.valueChanged);
    assertTrue(listener.valueRemoved);
    assertTrue(listener.idRemoved);

    History.clear();
    assertTrue(listener.cleaned);
    assertTrue(listener.valueAdded);
    assertTrue(listener.valueChanged);
    assertTrue(listener.valueRemoved);
    assertTrue(listener.idRemoved);

  }

}
