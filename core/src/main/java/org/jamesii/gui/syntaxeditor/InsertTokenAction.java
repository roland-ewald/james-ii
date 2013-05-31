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
 * Simple {@link ITokenAction} implementation that can insert a given text at a
 * specified location in the input. If offset is out of the range of input
 * nothing will be inserted.
 * 
 * @author Stefan Rybacki
 */
public class InsertTokenAction extends AbstractTokenAction {

  /**
   * The offset.
   */
  private int offset;

  /**
   * The insertion.
   */
  private String insertion;

  /**
   * Instantiates a new insert token action.
   * 
   * @param description
   *          the action's description
   * @param offs
   *          the offset to insert at
   * @param what
   *          the text to insert
   */
  public InsertTokenAction(String description, int offs, String what) {
    super(description);
    offset = offs;
    insertion = what;
  }

  @Override
  public boolean run(Reader input, Writer output) {
    try {
      int c = 0;
      int read;

      while ((read = input.read()) != -1) {
        if (c == offset) {
          output.write(insertion);
        }

        output.write(read);

        c++;
      }
    } catch (Exception e) {
      SimSystem.report(e);
      return false;
    }

    return true;
  }

}
