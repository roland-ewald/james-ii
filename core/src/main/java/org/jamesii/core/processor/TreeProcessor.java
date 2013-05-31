/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.IModel;

/**
 * Title: TreeProcessor Description: The TreeProcessor is a basic processor
 * class for simulators which have a tree structure. Every processor class which
 * is part of a tree structure (e.g., DEVS, BetaBinders...) should inherit from
 * this class. Furthermore the methods for setting/adding parent and child nodes
 * should include calls of the appropriate methods offered by the TreeProcessor
 * class. Copyright: Copyright (c) 2008 Company: University of Rostock, Faculty
 * of Computer Science Modeling and Simulation group
 * 
 * @author Stefan Leye
 * @version 1.0
 */

public abstract class TreeProcessor<TimeBase extends Comparable<TimeBase>>
    extends Processor<TimeBase> implements ITreeProcessor<TimeBase> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1185246454539976677L;

  /**
   * Parent node in the tree.
   */
  private IProcessor<TimeBase> parent;

  /**
   * Set of child nodes in the tree.
   */
  private List<IProcessor<TimeBase>> children = new ArrayList<>();

  /**
   * Flag that shows, whether this node has no (remote) references anymore, this
   * is necessary to prevent infinite loops during cleanup. In general this flag
   * should be set to false.
   */
  private boolean cleaned = false;

  /**
   * Instantiates a new tree processor.
   * 
   * @param model
   *          the model
   */
  public TreeProcessor(IModel model) {
    super(model);
  }

  /**
   * Cleans up (hopefully after the execution of a simulation), i.e., destroys
   * the references which form the tree. This is especially important, if the
   * nodes of the tree communicate remotely.
   */
  @Override
  public void cleanUp() {
    if (!isCleaned()) {
      setCleaned(true);
      for (IProcessor<TimeBase> child : children) {
        child.cleanUp();
      }
      setParent(null);
      super.cleanUp();
    }
  }

  /**
   * Gets the parent.
   * 
   * @return the parent
   */
  @Override
  public IProcessor<TimeBase> getParent() {
    return parent;
  }

  /**
   * Sets the parent.
   * 
   * @param parent
   *          the new parent
   */
  @Override
  public void setParent(IProcessor parent) {
    this.parent = parent;
  }

  /**
   * Adds the child.
   * 
   * @param proc
   *          the proc
   */
  @Override
  public void addChild(IProcessor proc) {
    children.add(proc);
  }

  /**
   * Removes the child.
   * 
   * @param proc
   *          the proc
   */
  @Override
  public void removeChild(IProcessor proc) {
    children.remove(proc);
  }

  /**
   * Checks if is cleaned.
   * 
   * @return true, if is cleaned
   */
  public boolean isCleaned() {
    return cleaned;
  }

  /**
   * Sets the cleaned.
   * 
   * @param cleaned
   *          the new cleaned
   */
  private void setCleaned(boolean cleaned) {
    this.cleaned = cleaned;
  }
}
