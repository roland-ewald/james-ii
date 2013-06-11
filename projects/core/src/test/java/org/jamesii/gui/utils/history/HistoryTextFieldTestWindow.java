/* The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import javax.swing.JFrame;

import org.jamesii.gui.utils.history.HistoryTextFieldTest;

//import javax.swing.JPanel;

/**
 * Class to test {@link HistoryTextFieldTest}
 * 
 * @author Enrico Seib
 * 
 */
public class HistoryTextFieldTestWindow {

  /**
   * Main method to display the frame
   * 
   * @param args
   */
  public static void main(String[] args) {

    JFrame mainWindow = new JFrame();
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setSize(500, 200);

    // JPanel panel = new JPanel();
    // SimpleAutoText sat = new SimpleAutoText();
    // panel = sat.formLayout(panel);
    // mainWindow.add(panel);

    mainWindow.add(new HistoryTextFieldTest());

    mainWindow.setVisible(true);
  }

}

//