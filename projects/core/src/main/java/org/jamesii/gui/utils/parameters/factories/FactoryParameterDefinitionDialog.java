/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;

/**
 * Dialog that displays a {@link FactoryParameterPanel} and returns the results.
 * 
 * @author Roland Ewald
 */
public abstract class FactoryParameterDefinitionDialog extends JDialog
    implements IFactoryParameterDialog<Factory> {

  /** Serialization ID. */
  private static final long serialVersionUID = 1244045217932478346L;

  /** The factory parameter panel. */
  private FactoryParameterPanel fpPanel;

  /** OK button. */
  private JButton okButton = new JButton("OK");
  {
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
  }

  /**
   * Default constructor.
   * 
   * @param factoryClass
   *          the class of the factory whose parameter should be given by the
   *          user
   */
  public FactoryParameterDefinitionDialog(Class<? extends Factory> factoryClass) {
    super();
    setModal(true);
    setSize(500, 300);
    setTitle("Set parameters to load experiment from COMO database:");
    getContentPane().setLayout(new BorderLayout());

    IFactoryInfo factoryInfo =
        SimSystem.getRegistry().getFactoryInfo(factoryClass.getCanonicalName());
    fpPanel = new FactoryParameterPanel(factoryInfo, new ParameterBlock());

    getContentPane().add(new JScrollPane(fpPanel), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.add(okButton, BorderLayout.EAST);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    setLocationRelativeTo(null);
  }

  @Override
  public Pair<ParameterBlock, Factory> getFactoryParameter(Window parentWindow) {
    // This is a model dialog, it blocks until the dialog is not visible anymore
    this.setVisible(true);
    return new Pair<>(getParametersFromBlock(fpPanel.getParameterBlock()), null); // TODO:
                                                                                  // Ceck
                                                                                  // what
                                                                                  // to
                                                                                  // return
                                                                                  // here
  }

  /**
   * Converts the parameter block into a valid parameter object.
   * 
   * @param parameterBlock
   *          the parameter block to be converted
   * 
   * @return the suitable parameter object
   */
  // FIXME(PARAMETERBLOCKS): This seems to be pretty obsolete now, let's see :)
  protected abstract ParameterBlock getParametersFromBlock(
      ParameterBlock parameterBlock);

}
