/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

/**
 * Abstract implementation of {@link ITokenAction} to provide basic
 * functionality.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractTokenAction implements ITokenAction {

  /**
   * The description.
   */
  private String description;

  /**
   * Instantiates a new abstract token action.
   * 
   * @param description
   *          the action's description
   */
  public AbstractTokenAction(String description) {
    this.description = description;
  }

  @Override
  public final String getDescription() {
    return description;
  }

}
