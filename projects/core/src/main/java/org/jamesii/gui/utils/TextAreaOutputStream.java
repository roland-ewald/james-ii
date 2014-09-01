/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.jamesii.SimSystem;

/**
 * This outputstream uses a {@link JTextArea} to display the output in.
 * 
 * Created on 05.05.2004
 * 
 * @author Jan Himmelspach
 */
public class TextAreaOutputStream extends OutputStream {

  /** Flag for whether autoscrolling is enabled. */
  private boolean autoscroll;

  /** Maximal number of lines. */
  private int maximalLineCount = 1000;

  /**
   * Removal factor. I.e., fraction of the document that is going to be removed
   * if the line count is exceeded, e.g., 0.5 leads to the removal of half the
   * document.
   */
  private double removalFactor = 0.5;

  /** The text area. */
  private JTextArea textArea;

  /**
   * Standard constructor.
   * 
   * @param text
   *          TextArea that will be filled with the output
   * @param autoscroll
   *          flag if the textarea will be scrolled down automatically
   */
  public TextAreaOutputStream(JTextArea text, boolean autoscroll) {
    super();
    this.textArea = text;
    this.autoscroll = autoscroll;
  }

  /**
   * Gets the maximal line count.
   * 
   * @return the maximal line count
   */
  public int getMaximalLineCount() {
    return maximalLineCount;
  }

  /**
   * Gets the removal factor.
   * 
   * @return the removal factor
   */
  public double getRemovalFactor() {
    return removalFactor;
  }

  /**
   * Gets the text area.
   * 
   * @return the text area
   */
  public JTextArea getTextArea() {
    return textArea;
  }

  /**
   * Sets the maximal line count.
   * 
   * @param maximalLineCount
   *          the new maximal line count
   */
  public void setMaximalLineCount(int maximalLineCount) {
    this.maximalLineCount = maximalLineCount;
  }

  /**
   * Sets the removal factor.
   * 
   * @param removalFactor
   *          the new removal factor
   */
  public void setRemovalFactor(double removalFactor) {
    this.removalFactor = removalFactor;
  }

  /**
   * Tests whether text area is too large.
   */
  void testForLength() {
    if (maximalLineCount < textArea.getLineCount()) {
      try {
        textArea.getDocument().remove(0,
            (int) (removalFactor * textArea.getDocument().getLength()));
      } catch (BadLocationException ex) {
        SimSystem.report(ex);
      }
    }
  }

  /**
   * Function to use for writing data into the stream.
   * 
   * @param b
   *          array of data bytes
   * @param off
   *          offset
   * @param len
   *          length
   * 
   * @throws IOException
   *           see {@link OutputStream}
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {

    if (b == null) {
      throw new NullPointerException();
    } else if ((off < 0) || (off > b.length) || (len < 0)
        || ((off + len) > b.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return;
    }

    // new byte array is initialized
    byte[] outArray = new byte[len - off];
    if (off > 0 || len < b.length) {
      System.arraycopy(b, off + 0, outArray, 0, len);
    } else {
      outArray = b; // won't need to copy input from a buffered stream
    }

    textArea.append(new String(outArray));
    testForLength();

    if (autoscroll) {
      textArea.setCaretPosition(textArea.getText().length());
    }

  }

  /**
   * Write b top the out text area.
   * 
   * @param b
   *          the character to be written
   */
  @Override
  public void write(int b) {
    String s = String.valueOf((char) b);
    textArea.append(s);
    testForLength();
  }

}
