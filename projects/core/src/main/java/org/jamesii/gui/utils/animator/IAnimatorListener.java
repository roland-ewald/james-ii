/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

/**
 * Listener interface used by Animator and SwingAnimator.
 * 
 * @author Stefan Rybacki
 */
public interface IAnimatorListener {
  /**
   * called when the animation starts
   */
  void started();

  /**
   * called continuously during animation where frac indicates the current
   * progress within the animation, where 0 means beginning and 1 means ending.
   * 
   * @param frac
   *          the position within the animation
   */
  void animate(double frac);

  /**
   * called when the animation was stopped or finished (not if it was aborted)
   */
  void stopped();
}
