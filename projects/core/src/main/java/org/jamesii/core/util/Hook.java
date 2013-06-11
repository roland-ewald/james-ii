/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.io.Serializable;

/**
 * The Class Hook. The system provides at some well-defined points the
 * possibility to install a hook, thus any functionality can be added into the
 * execution flow of the system at these points. This is pretty useful for
 * higher level functionality as certain measurements, debugging, etc ..
 * 
 * A concrete hook has to use this class here as super class. The actions to be
 * undertaken by the hook have to be implemented in the {@link #executeHook}
 * method.
 * 
 * Usually hooks do not replaced each other if installed for the same thing:
 * they form a chain of hooks. This chain is completely executed.
 * 
 * @author Jan Himmelspach
 * @param <I>
 *          the type of the information to be forwarded to the hook
 */
public abstract class Hook<I extends Serializable> implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8341797581695865214L;

  /**
   * The old hook. If a new hook gets registered the old hook is saved here -
   * i.e., 2nd hook does not replace an already existing one, a "chain" is
   * created, and all old hooks are executed as well.
   */
  private Hook<I> oldHook;

  /**
   * If more than hook has to be installed this constructor can be used. Just
   * pass the lastly installed hook while creating the new one.
   * 
   * @param oldHook
   *          the old hook
   */
  public Hook(Hook<I> oldHook) {
    super();
    this.oldHook = oldHook;
  }

  /**
   * Instantiates a new hook.
   */
  public Hook() {
    oldHook = null;
  }

  /**
   * This method will be called by the system, and will execute at first the old
   * hook (chain of old hooks, followed by this).
   * 
   * @param information
   *          the information
   */
  public final void execute(I information) {
    executeOldHook(information);
    executeHook(information);
  }

  /**
   * Execute hook. This method has to be modified according to the job the hook
   * has to do (e.g., sending an E-Mail).
   * 
   * @param information
   *          the information
   */
  protected abstract void executeHook(I information);

  /**
   * Executes the old hook, if there is one. Used internally by the
   * {@link #execute(Serializable)} method before it calls the
   * {@link #executeHook(Serializable)} implementation.
   * 
   * @param information
   *          the information
   */
  private void executeOldHook(I information) {
    if (oldHook != null) {
      oldHook.execute(information);
    }
  }

  /**
   * Gets the old hook.
   * 
   * @return the old hook
   */
  public Hook<I> getOldHook() {
    return oldHook;
  }

}
