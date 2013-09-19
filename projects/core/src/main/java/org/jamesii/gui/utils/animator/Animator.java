/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

import java.util.Timer;
import java.util.TimerTask;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * This class is used for animation purposes. It allows for an easy animation
 * setup by just specifying the animation duration and the way the steps of the
 * animation are interpolated. The animator then notifies an
 * {@link IAnimatorListener} continuously until the specified animation duration
 * is reached.
 * 
 * 
 * @author Stefan Rybacki
 */
public class Animator extends TimerTask {
  /**
   * the interpolator used to interpolated from start to end
   */
  private IInterpolator interpolator;

  /**
   * stores all registered {@link IAnimatorListener}s
   */
  private ListenerSupport<IAnimatorListener> listeners =
      new ListenerSupport<>();

  /**
   * animation duration
   */
  private int duration;

  /**
   * Java timer used to time listener notifications
   */
  private Timer timer;

  /**
   * sets the timer resolution and therefore affects the number of notification
   * calls during animation
   */
  private long resolution;

  /**
   * internal variable that stores the start of the animation
   */
  private long startTime = 0;

  /**
   * flag indicating the animation is running or not
   */
  private boolean running;

  /**
   * Creates an animator that runs for the specified duration using the
   * specified interpolator for interpolation.
   * 
   * @param duration
   *          the animations duration in milliseconds
   * @param interpolator
   *          the interpolator to use
   * 
   * @see LinearInterpolator
   * @see AlternatingInterpolator
   * @see SplineInterpolator
   * @see DiscreteInterpolator
   */
  public Animator(int duration, IInterpolator interpolator) {
    this.duration = duration;
    this.interpolator = interpolator;
    this.resolution = 10;
  }

  /**
   * starts the animation
   */
  public final void start() {
    if (timer != null) {
      timer.cancel();
    }
    timer = new Timer(true);

    for (IAnimatorListener l : listeners) {
      if (l != null) {
        l.started();
      }
    }

    running = true;
    timer.schedule(this, 10, resolution);
  }

  /**
   * aborts animation (note: stopped for listeners is NOT called)
   */
  public final void abort() {
    if (timer != null) {
      timer.cancel();
    }
    running = false;
  }

  /**
   * stops the animation (stopped for listener IS called)
   */
  public final void stop() {
    if (timer != null) {
      timer.cancel();
    }
    running = false;
    for (IAnimatorListener l : listeners) {
      if (l != null) {
        l.stopped();
      }
    }
  }

  /**
   * adds an AnimationListener to the animator
   * 
   * @param listener
   *          the listener to add
   */
  public final synchronized void addAnimatorListener(IAnimatorListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * removes an AnimationListener from the listeners list
   * 
   * @param listener
   *          the listener to remove
   */
  public final synchronized void removeAnimatorListener(
      IAnimatorListener listener) {
    listeners.remove(listener);
  }

  /**
   * Indicates whether the animator is still running the animation
   * 
   * @return true if the animation is still running false else
   */
  public final boolean isRunning() {
    return (timer != null && running);
  }

  @Override
  public void run() {
    if (startTime == 0) {
      startTime = System.nanoTime();
    }
    long step = System.nanoTime() - startTime;

    double frac = Math.max(0.0, Math.min(1.0, step / 1000000.0 / duration));

    // notify listeners
    for (IAnimatorListener l : listeners) {
      if (l != null) {
        l.animate(interpolator.interpolate(frac));
      }
    }

    if (frac >= 1.0) {
      stop();
    }
  }
}