/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.Reader;
import java.io.Writer;

import org.jamesii.SimSystem;

/**
 * Very simple {@link ITokenAction} which just passes the given input to the
 * given output. This can be used to provide external actions that only react on
 * tokens but don't change the actual input. Just override
 * {@link #run(Reader, Writer)} trigger your action and call super.
 * {@link #run(Reader, Writer)} afterwards.
 * 
 * @author Stefan Rybacki
 */
public class PasstroughTokenAction extends AbstractTokenAction {

  /**
   * Instantiates a new passtrough token action.
   * 
   * @param description
   *          the description
   */
  public PasstroughTokenAction(String description) {
    super(description);
  }

  @Override
  public boolean run(Reader input, Writer output) {
    try {
      int read;
      while ((read = input.read()) != -1) {
        output.write(read);
      }
    } catch (Exception e) {
      SimSystem.report(e);
      return false;
    }
    return true;
  }

}
