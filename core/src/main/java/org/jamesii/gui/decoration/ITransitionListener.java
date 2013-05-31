/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

/**
 * Simple listener interface that is used to listen for transition events. The
 * listener is notified when a transition start was marked, the end was marked,
 * the transition starts as well as when the transition is completed.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface ITransitionListener {
  /**
   * called when a transition start was marked
   * 
   * @param deco
   *          the decoration to use for transition
   */
  void startMarked(TransitionDecoration deco);

  /**
   * called when a transition end was marked
   * 
   * @param deco
   *          the decoration to use for transition
   */
  void endMarked(TransitionDecoration deco);

  /**
   * Called when the actual transition animation started
   * 
   * @param deco
   *          the decoration that is used during animation
   */
  void transitionStarted(TransitionDecoration deco);

  /**
   * Called when the actual transition animation finished
   * 
   * @param deco
   *          the decoration that was used during animation
   */
  void transitionFinished(TransitionDecoration deco);
}
