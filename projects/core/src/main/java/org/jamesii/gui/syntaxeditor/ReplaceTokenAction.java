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
 * A default implementation of an {@link ITokenAction} that replaces a given
 * range within the input with the specified {@link String}.
 * 
 * @author Stefan Rybacki
 */
public class ReplaceTokenAction extends AbstractTokenAction {

  /**
   * The replacement.
   */
  private String replacement;

  /**
   * The to.
   */
  private int to;

  /**
   * The from.
   */
  private int from;

  /**
   * Instantiates a new replace token action.
   * 
   * @param description
   *          the action's description
   * @param from
   *          the from index
   * @param to
   *          the to index
   * @param with
   *          the replacement text
   */
  public ReplaceTokenAction(String description, int from, int to, String with) {
    super(description);
    this.from = from;
    this.to = to;
    this.replacement = with;
  }

  /**
   * Sets the replacement.
   * 
   * @param replacement
   *          the new replacement
   */
  public void setReplacement(String replacement) {
    this.replacement = replacement;
  }

  /**
   * Gets the replacement.
   * 
   * @return the replacement
   */
  public String getReplacement() {
    return replacement;
  }

  @Override
  public boolean run(Reader input, Writer output) {
    // basically pass input to output char by char except for the
    // region from to to
    try {
      int c = 0;
      int read;

      while ((read = input.read()) != -1) {
        if (c == from) {
          // write replacement
          output.write(replacement);
        }

        if (c < from || c >= to) {
          output.write(read);
        }

        c++;
      }

      output.flush();
    } catch (Exception e) {
      SimSystem.report(e);
      return false;
    }

    return true;
  }

}
