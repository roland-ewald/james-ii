/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA: Wolfram's Rule 22 Launcher
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 09.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.examples.ca.wolframsrules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.jamesii.SimSystem;
import org.jamesii.core.cmdparameters.InvalidParameterException;
import org.jamesii.core.observe.Mediator;
import org.jamesii.gui.visualization.grid.AbstractGridCellRenderer;
import org.jamesii.gui.visualization.grid.Grid2D;
import org.jamesii.simulator.cacore.Grid1DLauncher;

public class RuleLauncherEO extends Grid1DLauncher {

  /**
   * Create and launch the simulation.
   * 
   * @param args
   *          command line parameters
   */
  public static void main(String args[]) {
    // default argument parsing
    RuleLauncherEO launcher = new RuleLauncherEO();

    // std parameter parsing, any non std parameters have to be parsed before
    // a call to parseArgs (and have to be thereby removed from or set to an
    // empty string in the list of args)
    try {
      launcher.parseArgs(args);
    } catch (InvalidParameterException ipe) {
      launcher.printInvalidParameterNote(ipe.getMessage());
      System.exit(0);
    }

    // create an instance of the model
    RuleGrid rule22Grid = null;

    int[] rule = new int[8];

    int wrule = launcher.rule;

    for (int i = 0; i < rule.length; i++) {
      rule[rule.length - i - 1] = ((wrule % 2) == 0) ? 0 : 1;
      wrule = wrule / 2;
    }

    // int[] rule = {1,1,1,1,1,0,1,0};

    rule22Grid =
        new RuleGrid("Wolfram's Rule " + launcher.rule, launcher.getWidth(),
            rule);

    // // TextStreamOutputDataWindow sow = new TextStreamOutputDataWindow(
    // // "Wolfram's Rules");
    // //
    // // Mediator medi = new Mediator();
    // // CAGridObserver go = new CAGridObserver(sow.getOutStream());
    // // rule22Grid.getState().setMediator(medi);
    // // rule22Grid.getState().registerObserver(go);
    //
    // sow.setVisible(true);

    Grid1DObserver obs = new Grid1DObserver(rule22Grid);

    JFrame frame = new JFrame("Wolfram's rules");

    Grid2D grid = new Grid2D(obs);

    grid.setCellRenderer(new AbstractGridCellRenderer() {
      @Override
      public void draw(Grid2D sender, Graphics g, int x, int y, int width,
          int height, Shape shape, int cellX, int cellY, Object value,
          boolean isSelected, boolean hasFocus) {
        Color c;
        if (value == null) {
          c = Color.BLACK;
        } else {
          c = ((Boolean) value) ? Color.WHITE : Color.BLACK;
        }

        g.setColor(c);
        g.fillRect(x, y, width, height);

        if (hasFocus) {
          g.setColor(new Color(0x805492FF, true));
          g.fillRect(x, y, width, height);
        }
      }
    });

    frame.setPreferredSize(new Dimension(1000, 800));

    frame.add(grid);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();

    Mediator medi = new Mediator();
    rule22Grid.getState().setMediator(medi);
    rule22Grid.getState().registerObserver(obs);

    rule22Grid.init();

    frame.setVisible(true);
    // example: how to write a model structure to a file by using the filer
    // Filer filer = new XMLFiler("model.xml");
    // filer.write(forestFire);

    // create the simulation
    launcher.createSimulation(rule22Grid);

    SimSystem.report(Level.INFO, "Created model with rule \n"
        + " -------------------------------\n"
        + "|XXX|XXO|XOX|XOO|OXX|OXO|OOX|OOO|\n" + "| " + rule[0] + " | "
        + rule[1] + " | " + rule[2] + " | " + rule[3] + " | " + rule[4] + " | "
        + rule[5] + " | " + rule[6] + " | " + rule[7] + " |\n"
        + " -------------------------------\n" +

        "Width of the automata: " + launcher.getWidth());

    // run the model
    launcher.executeModel();
  }

  private int[] b = { 128, 64, 32, 16, 8, 4, 2, 1 };

  private String binrule;

  private int rule = 22;

  public RuleLauncherEO() {
    super();

  }

  /**
   * Print the default arguments plus the model specific ones!
   */
  @Override
  public String extArgsToString() {
    return "\nModel specific parameters:"
        + "\n-width=        [4] width of the grid (integer)"
        + "\n-rule=         [22] rule to use (integer) (0..255)"
        + "\n-binrule=      rule to use, binary representation (e.g. 01011010 for rule 90)";
  }

  /**
   * Checks wether the given parameter is known, if known the parameter is
   * interpreted and the function returns true, otherwise it will return the
   * inherited function's implementation return code
   * 
   * @return true if the parameter was handled, false otherwise
   */
  @Override
  public boolean handleParameter(String param, String value) {
    if (param.compareTo("rule") == 0) {
      rule = Integer.valueOf(value).intValue();
      // width parameter found => set argument to an emtpy string
      return true;
    }
    if (param.compareTo("binrule") == 0) {
      binrule = value;
      rule = 0;
      for (int i = 0; i < binrule.length(); i++) {
        rule = (binrule.charAt(i) == '1') ? rule + b[i] : rule;
      }
      // width parameter found => set argument to an emtpy string
      return true;
    }
    return super.handleParameter(param, value);
  }

}
