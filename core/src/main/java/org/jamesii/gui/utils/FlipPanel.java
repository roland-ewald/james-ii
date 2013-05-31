/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jamesii.gui.decoration.Decorator;
import org.jamesii.gui.decoration.FlipTransition;
import org.jamesii.gui.decoration.TransitionDecoration;

/**
 * This panel can hold two components where those are placed on either side of
 * the panel. That means one on the back and one on the front. So that they
 * share the same space and can be exchanged by rotating the panel. This is
 * useful if you want to provide say settings to a component, just place it on
 * the back of the component.
 * 
 * @author Stefan Rybacki
 */
public class FlipPanel extends JPanel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -3532843132257953634L;

  /**
   * holds the component representing the front
   */
  private JComponent frontComponent;

  /**
   * holds the component representing the back
   */
  private JComponent backComponent;

  /**
   * flag indicating whether back or front is showing
   */
  private boolean backShowing;

  /**
   * the decorator used to provide a transition effect when flipping panel
   */
  private Decorator facade;

  /**
   * the transition decoration
   */
  private TransitionDecoration decoration;

  /**
   * Creates the {@link FlipPanel}. Use setFrontComponent and setBackComponent
   * to set the back and front of the panel.
   */
  public FlipPanel() {
    super(new BorderLayout());
    backShowing = false;
    // use a simple FlipTransition
    decoration = new TransitionDecoration(new FlipTransition(), 500);
    facade = new Decorator(null, decoration);
    super.addImpl(facade, BorderLayout.CENTER, 0);
  }

  @Override
  protected final void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException(
        "add() not supported! Use setBackComponent() and setFrontComponent() instead.");
  }

  /**
   * sets the component that is showing on the backside of the panel
   * 
   * @param component
   *          the component to attach to the back
   */
  public final void setBackComponent(JComponent component) {
    backComponent = component;
    if (backShowing) {
      facade.setComponent(component);
    }
  }

  /**
   * sets the component that is showing on the frontside of the panel
   * 
   * @param component
   *          the component to attach to the front
   */
  public final void setFrontComponent(JComponent component) {
    frontComponent = component;
    if (!backShowing) {
      facade.setComponent(component);
    }
  }

  /**
   * Flips the panel. That means it exchanges the back and front components of
   * the panel using a transition effect
   */
  public final void flip() {
    decoration.markTransitionStart();

    // change components
    if (backShowing) {
      facade.setComponent(frontComponent);
      backShowing = !backShowing;
    } else {
      facade.setComponent(backComponent);
      backShowing = !backShowing;
    }
    revalidate();

    decoration.markTransitionEnd();
  }

  /**
   * @return true if back is showing
   */
  public final boolean isBackShowing() {
    return backShowing;
  }

  /**
   * @return true if front is showing
   */
  public final boolean isFrontShowing() {
    return !isBackShowing();
  }
}
