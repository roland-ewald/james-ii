/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingConstants;

import org.jamesii.gui.utils.ExpandingPanel;

import junit.framework.TestCase;

/** Tests the {@link ExpandingPanel} class. */
public class ExpandingPanelTest extends TestCase {

  /** The {@link ExpandingPanel} to test. */
  private ExpandingPanel ep;

  @Override
  protected void setUp() throws Exception {
    ep = new ExpandingPanel("Caption", SwingConstants.SOUTH);
  }

  @Override
  protected void tearDown() throws Exception {
    ep = null;
  }

  private void testPropertyChangeImpl(final String property,
      final Object expected, Runnable r, boolean listenerShouldHaveBeenCalled) {
    final AtomicBoolean listenerVisited = new AtomicBoolean(false);

    PropertyChangeListener listener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        listenerVisited.set(true);
        assertEquals(property, evt.getPropertyName());
        assertEquals(expected, evt.getNewValue());
      }
    };

    ep.addPropertyChangeListener(listener);
    r.run();
    ep.removePropertyChangeListener(listener);

    assertEquals(listenerShouldHaveBeenCalled, listenerVisited.get());
  }

  /**
   * Tests whether changing one of the {@link ExpandingPanel}'s unique
   * properties yields a {@link PropertyChangeEvent}
   */
  public void testPropertyChange() {
    testPropertyChangeImpl("caption", "Foo", new Runnable() {
      @Override
      public void run() {
        ep.setCaption("Foo");
      }
    }, true);

    testPropertyChangeImpl("expanded", true, new Runnable() {
      @Override
      public void run() {
        ep.setExpanded(true);
      }
    }, true);

    testPropertyChangeImpl("expanded", false, new Runnable() {
      @Override
      public void run() {
        ep.setExpanded(false);
      }
    }, true);

    testPropertyChangeImpl("expanded", false, new Runnable() {
      @Override
      public void run() {
        ep.setExpanded(false);
      }
    }, false);

    testPropertyChangeImpl("expandingDirection", SwingConstants.NORTH,
        new Runnable() {
          @Override
          public void run() {
            ep.setExpandingDirection(SwingConstants.NORTH);
          }
        }, true);

  }

}
