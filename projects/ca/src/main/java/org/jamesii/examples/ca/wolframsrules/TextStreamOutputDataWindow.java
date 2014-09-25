/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.examples.ca.wolframsrules;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.TextAreaOutputStream;

/**
 * Handles a data output stream of a simulation. Created on 05.05.2004
 * 
 * @author Jan Himmelspach
 */
@Deprecated
public class TextStreamOutputDataWindow extends javax.swing.JFrame {

  static final long serialVersionUID = -4262113682230420156L;

  static final int DEFAULT_BUFFER_SIZE = 1024;

  private JTextArea text = new JTextArea();

  private transient TextAreaOutputStream fdout = new TextAreaOutputStream(text,
      false);

  private transient BufferedOutputStream bos = new BufferedOutputStream(fdout,
      DEFAULT_BUFFER_SIZE);

  private transient PrintStream ps = new PrintStream(bos, true);

  private JScrollPane scrollPane = new JScrollPane();

  /** Creates a new instance of TextStreamOutputDataWindow */
  public TextStreamOutputDataWindow(String title) {

    // simulation.setOut(ps); REMOVED by JH, printStream!!!

    setTitle(title);
    setSize(BasicUtilities.RESOLUTION_VGA.width,
        BasicUtilities.RESOLUTION_VGA.height);
    text.setEditable(false);

    try {

      text.setFont(new Font("COURIER", Font.PLAIN, 6));
    } catch (Exception e) {
      SimSystem.report(e);
    }

    getContentPane().setLayout(new BorderLayout());
    scrollPane.getViewport().add(text);

    getContentPane().add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * clear he display
   * 
   */
  public void clear() {
    try {
      text.getDocument().remove(0, text.getDocument().getLength());
    } catch (Exception e) {
      SimSystem.report(e);
    }

  }

  /**
   * 
   * @return
   */
  public PrintStream getOutStream() {
    return ps;
  }

  public void onDataChanged() {
    // @TODO (re027) update if there is new data in the stream ...
  }

}
