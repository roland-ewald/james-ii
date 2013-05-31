/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

import org.jamesii.gui.utils.BasicUtilities;

/**
 * Same as Animator with the difference, that it executes the TimerTask.run
 * method in the EDT, this way it is more suitable for Swing based animations.
 * 
 * @author Stefan Rybacki
 * 
 */
public class SwingAnimator extends Animator {
  /**
   * Creates an Animator that executes the run method within the EDT which means
   * it is more suitable for animations in the Swing UI
   * 
   * @param duration
   *          the animation's duration in milliseconds
   * @param interpolator
   *          the interpolator to use
   */
  public SwingAnimator(int duration, IInterpolator interpolator) {
    super(duration, interpolator);
  }

  @Override
  public final void run() {
    // execute the Animator.run() method within the EDT
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        SwingAnimator.super.run();
      }
    });
  }
}
