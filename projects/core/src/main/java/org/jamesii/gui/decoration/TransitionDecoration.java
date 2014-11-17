/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;

import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.animator.IAnimatorListener;
import org.jamesii.gui.utils.animator.SplineInterpolator;
import org.jamesii.gui.utils.animator.SwingAnimator;

/**
 * Decoration used in combination with a {@link Decorator} to simulate
 * transition effects on GUI components. Basically you assign this decoration to
 * any component and before you want the transition to happen (for instance if
 * you change the content of the component) you mark the current component state
 * as start. Then you make all the changes you want to appear after transition
 * and mark this as end. After that the transition using the specified
 * transition effect takes place.
 * 
 * @author Stefan Rybacki
 */
public final class TransitionDecoration extends DefaultDecoration implements
    IAnimatorListener {

  /**
   * Serialization proxy for this decoration.
   * 
   * @author Stefan Rybacki
   */
  private static final class SerializationProxy implements Serializable {

    /** Serialization ID. */
    private static final long serialVersionUID = 7475193929097166470L;

    /** The duration. */
    private final int duration;

    /** The transition. */
    private final ITransition transition;

    /**
     * Instantiates a new serialization proxy.
     * 
     * @param t
     *          the t
     */
    private SerializationProxy(TransitionDecoration t) {
      duration = t.transitionDuration;
      transition = t.transition;
    }

    /**
     * Read resolve.
     * 
     * @return the object
     */
    private Object readResolve() {
      return new TransitionDecoration(transition, duration);
    }

  }

  /** Serialization ID. */
  private static final long serialVersionUID = 3742505293809761432L;

  /** Cache used to store current component state. */
  private transient SoftReference<BufferedImage> captureCache =
      new SoftReference<>(null);

  /** indicating how far the animation is gone. */
  private transient double aniStep = 0;

  /** the transition effect to use. */
  private transient ITransition transition;

  /** stores the image the transition starts from. */
  private transient BufferedImage fromImage;

  /** flag indicating whether the start of the transition was already marked. */
  private transient boolean startMarked;

  /** the image the transition ends with. */
  private transient BufferedImage toImage;

  /** duration of transition animation. */
  private transient int transitionDuration;

  /** flag indicating whether transition takes place. */
  private transient boolean transitionRunning;

  /** holds the registered transition liseteners. */
  private transient ListenerSupport<ITransitionListener> listeners =
      new ListenerSupport<>();

  /**
   * Creates a new transition decoration using the specified transition effect
   * and the transition duration.
   * 
   * @param transition
   *          the transition effect to apply during transition
   * @param duration
   *          the time the transtion takes
   */
  public TransitionDecoration(ITransition transition, int duration) {
    super();
    this.transition = transition;
    this.transitionDuration = duration;
  }

  @Override
  protected void paintDecoration(Graphics2D g, Decorator d) {
    // always draw into captureCache
    int imageWidth = d.getWidth();
    int imageHeight = d.getHeight();

    BufferedImage capture = captureCache.get();
    // check cache
    Graphics2D gr;
    if (capture != null && imageWidth == capture.getWidth()
        && imageHeight == capture.getHeight() && d.getComponent().isOpaque()) {
      gr = capture.createGraphics();
      gr.setClip(g.getClip());
    } else {
      capture =
          BasicUtilities.createCompatibleImage(imageWidth, imageHeight,
              Transparency.TRANSLUCENT);
      gr = capture.createGraphics();
      captureCache = new SoftReference<>(capture);
    }

    // draw into capture buffer
    super.paintDecoration(gr, d);

    gr.dispose();

    // if transition is running use the transition object to draw the
    // transition
    if (isTransitionRunning() && transition != null) {
      transition.drawTransition(g, fromImage, toImage, aniStep, imageWidth,
          imageHeight);
      return;
    }

    // if transition start is marked don't draw anymore updates but the
    // fromImage
    if (startMarked && transition != null) {
      g.drawImage(fromImage, 0, 0, d);
      return;
    }

    // draw content as usual
    g.drawImage(capture, 0, 0, d);
  }

  /**
   * use this method to mark the transition start state.
   */
  public void markTransitionStart() {
    if (!isTransitionRunning()) {
      BufferedImage tmp = captureCache.get();
      if (tmp != null) {
        fromImage =
            BasicUtilities.createCompatibleImage(tmp.getWidth(),
                tmp.getHeight(), Transparency.TRANSLUCENT);
        Graphics g = fromImage.getGraphics();
        ((Graphics2D) g).drawRenderedImage(tmp, null);
        g.dispose();
      }
      startMarked = fromImage != null;
      if (startMarked) {
        notifyListeners(Type.START_MARKED);
      }
    }
  }

  /**
   * use this method to mark the transition end state and to start the actual
   * transition animation using the specified transition effect.
   */
  public void markTransitionEnd() {
    if (!isTransitionRunning()) {
      toImage = captureCache.get();
      // start flipping animation only if flipEnd and flipStart != null
      if (toImage != null && fromImage != null) {
        startMarked = false;
        notifyListeners(Type.END_MARKED);
        startTransition();
      }
    }
  }

  /**
   * helper method that sets up and starts the transiton animation.
   */
  private synchronized void startTransition() {
    transitionRunning = true;
    SwingAnimator animator =
        new SwingAnimator(transitionDuration, new SplineInterpolator(0.1, 0.1));
    animator.addAnimatorListener(this);
    animator.start();
  }

  @Override
  public void animate(double frac) {
    aniStep = frac;
    getDecorator().repaint();
  }

  @Override
  public synchronized void started() {
    notifyListeners(Type.TRANSITION_STARTED);
    aniStep = 0;
  }

  @Override
  public void stopped() {
    notifyListeners(Type.TRANSITION_FINISHED);
    synchronized (this) {
      transitionRunning = false;
    }
    fromImage = null;
    toImage = null;
    startMarked = false;
  }

  /**
   * Determines whether the actual transition takes place right now.
   * 
   * @return true if transition is currently running
   */
  public synchronized boolean isTransitionRunning() {
    return transitionRunning;
  }

  /**
   * Sets the transition used during a transition.
   * 
   * @param transition
   *          the transition to use in next transition
   */
  public synchronized void setTransition(ITransition transition) {
    if (!transitionRunning) {
      this.transition = transition;
    } else {
      throw new IllegalStateException("Can't change transition while running!");
    }
  }

  /**
   * Write replace.
   * 
   * @return the object
   */
  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  /**
   * Read object.
   * 
   * @param stream
   *          the stream
   * 
   * @throws InvalidObjectException
   *           the invalid object exception
   */
  private void readObject(ObjectInputStream stream) // NOSONAR
      throws InvalidObjectException {
    throw new InvalidObjectException("Proxy needed!");
  }

  /**
   * Adds the specified listener to the list of listeners and is notified
   * whenever a transition event occurs.
   * 
   * @param listener
   *          the listener
   */
  public synchronized void addTransitionListener(ITransitionListener listener) {
    listeners.addListener(listener);
  }

  /**
   * Removes a transition listener.
   * 
   * @param listener
   *          the listener to remove
   */
  public synchronized void removeTransitionListener(ITransitionListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * Notify listeners.
   * 
   * @param type
   *          the type of notification
   */
  private void notifyListeners(Type type) {
    for (ITransitionListener l : listeners.getListeners()) {
      if (l != null) {
        switch (type) {
        case START_MARKED:
          l.startMarked(this);
          break;
        case END_MARKED:
          l.endMarked(this);
          break;
        case TRANSITION_STARTED:
          l.transitionStarted(this);
          break;
        case TRANSITION_FINISHED:
          l.transitionFinished(this);
          break;
        default:
        }
      }
    }
  }

  /**
   * The enum type specifying the current state of transition.
   */
  enum Type {

    /** The STAR t_ marked. */
    START_MARKED,
    /** The EN d_ marked. */
    END_MARKED,
    /** The TRANSITIO n_ started. */
    TRANSITION_STARTED,
    /** The TRANSITIO n_ finished. */
    TRANSITION_FINISHED
  }
}
