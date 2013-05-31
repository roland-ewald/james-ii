/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jamesii.gui.utils.history.HistoryTextFieldTest;

/**
 * Class to test SimpleAutoText or HistoryTextFieldtest
 * 
 * @author Enrico Seib
 * 
 */
public class SimpleLayoutTest {

  /**
   * Main method to display the frame
   * 
   * @param args
   */
  public static void main(String[] args) {

    JFrame mainWindow = new JFrame();
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setSize(500, 200);

    JPanel panel = new JPanel();
    SimpleAutoText sat = new SimpleAutoText();
    panel = sat.formLayout(panel);
    mainWindow.add(panel);

    mainWindow.add(new HistoryTextFieldTest());

    mainWindow.setVisible(true);
  }

}
