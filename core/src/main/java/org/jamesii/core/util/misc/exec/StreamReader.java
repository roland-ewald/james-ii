/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.exec;

import java.io.IOException;
import java.io.InputStream;

import org.jamesii.core.base.Entity;

/**
 * The Class StreamReader.
 * 
 * Read a given input stream and if print is true, print everything read to
 * System.out, otherwise just forget the characters read.
 * 
 * @author Jan Himmelspach
 */
public class StreamReader extends Thread {

  /** The input stream. */
  private InputStream is = null;

  /** Set to false if the stream read shall not be printed. */
  private boolean print = true;

  /** If printed the ident is written at the start of the line */
  private String ident = "";

  /**
   * Instantiates a new stream reader.
   * 
   * @param stream
   *          the stream
   */
  public StreamReader(String ident, InputStream stream, boolean print) {
    this.ident = ident;
    this.is = stream;
    this.print = print;
  }

  public StreamReader(InputStream stream) {
    this("", stream, false);
  }

  /**
   * Instantiates a new stream reader.
   * 
   * @param stream
   *          the stream
   * @param print
   *          - if true is passed (default) the chars read are printed to system
   *          out, otherwise they are not
   */
  public StreamReader(InputStream stream, boolean print) {
    this("", stream, print);
  }

  @Override
  public void run() {
    int c;

    StringBuffer sb = null;

    // only create the instance of we'll use that later
    if (print) {
      sb = new StringBuffer();
    }

    try {
      while ((c = getIs().read()) != -1) {
        if (print) {
          sb.append((char) c);
        }
      }
    } catch (IOException e) {
      // be silent
    }
    if ((sb != null) && (sb.length() >= 0)) {
      Entity.report(ident + ": " + sb.toString());
    }

    // remove all characters in the buffer, so that, if this thread remains in
    // memory the mem consumption is lowered
    if (sb != null) {
      sb.delete(0, sb.length());
    }

  }

  /**
   * @return the is
   */
  public InputStream getIs() {
    return is;
  }

}
