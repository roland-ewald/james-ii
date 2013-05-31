/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jamesii.SimSystem;
import org.jamesii.core.cmdparameters.Parameters;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;

// TODO: Auto-generated Javadoc
/**
 * An experiment is a set of simulation runs with a model. Thus setting up an
 * experiment requires the following steps: - Select the model to be used -
 * initialise the model parameters - initialise the simulation parameters -
 * Define how the model and simulation parameters shall be adapted from run to
 * run
 * 
 * @author Jan Himmelspach
 */
public class EditSimulationParameter extends EditExperimentPanel {

  /** Serialization ID. */
  private static final long serialVersionUID = -4975604223289909253L;

  /** Panel to configure random number generator factories. */
  private ConfigureFactoryPanel<RandomGeneratorFactory> confRandNumGenPanel;

  /** The proc factories. */
  private JComboBox procFactories = null;

  /** The scale. */
  private JTextField scale = new JTextField(10);
  {
    scale.setText("1");
  }

  /** The scale label. */
  private JLabel scaleLabel = new JLabel("Scale:");

  /** The end time. */
  private JTextField endTime = new JTextField(10);

  /** Master server (for fine-grained parallel sim). */
  private JTextField masterServer = new JTextField("");

  /**
   * Default constructor.
   * 
   * @param exp
   *          reference to experiment to be edited
   */
  public EditSimulationParameter(BaseExperiment exp) {
    super(exp);

    if (exp.getParameters().useMasterServer()) {
      masterServer.setText(exp.getParameters().getMasterServerName());
    }

    if (exp.getDefaultSimStopTime() != null) {
      endTime.setText(Double.toString(exp.getDefaultSimStopTime()));
    } else {
      endTime.setText(Double.valueOf(Double.POSITIVE_INFINITY).toString());
    }

    setLayout(new GridLayout(20, 1));

    // simulation system parameters
    JLabel simLab =
        new JLabel(
            "Simulation parameters (TAKE CARE: Depending on simulator, these might be deprecated): ");
    add(simLab);

    // TODO: Remove this when parameter handling works:
    JPanel scalePanel = new JPanel(new BorderLayout());
    scalePanel.add(scaleLabel, BorderLayout.WEST);
    scalePanel.add(scale, BorderLayout.CENTER);
    add(scalePanel);

    JPanel endTimePanel = new JPanel(new BorderLayout());
    endTimePanel.add(new JLabel("End time:"), BorderLayout.WEST);
    endTimePanel.add(endTime, BorderLayout.CENTER);
    add(endTimePanel);

    JPanel mServPanel = new JPanel(new BorderLayout());
    mServPanel.add(new JLabel("Simulation Master Server:"), BorderLayout.WEST);
    mServPanel.add(masterServer);
    add(mServPanel);

  }

  @Override
  public void closeDialog() {
    if (getExperiment().getParameters() == null) {
      getExperiment().setParameters(new Parameters());
    }

    // if (procFactories != null)
    // experiment.getParameters().setProc(
    // procFactories.getSelectedItem().toString());
    try {
      getExperiment().setDefaultSimStopTime(
          Double.parseDouble(endTime.getText()));
    } catch (Exception ex) {
      SimSystem.report(ex);
      // experiment.setDefaultSimStopTime(Double.POSITIVE_INFINITY);
    }
    getExperiment().getParameters().setMasterServerName(
        masterServer.getText().trim());
  }

  /**
   * Gets the combo box.
   * 
   * @param factoriesList
   *          the factories list
   * 
   * @return the combo box
   */
  private JComboBox getComboBox(List<? extends Factory<?>> factoriesList) {

    List<String> factoriesName = new ArrayList<>();
    if (factoriesList != null) {
      factoriesName = SimSystem.getRegistry().getFactoryNames(factoriesList);
    }

    JComboBox combobox = new JComboBox();

    if (factoriesName.isEmpty()) {
      combobox.setEnabled(false);
      return combobox;
    }

    for (String pf : factoriesName) {
      combobox.addItem(pf);
    }

    return combobox;
  }

  /**
   * Gets the combo box.
   * 
   * @param abstractFactory
   *          the abstract factory
   * @param <V>
   *          the base factory F
   * @return the combo box
   */
  <V extends Factory<?>> JComboBox getComboBox(
      Class<? extends AbstractFactory<V>> abstractFactory) {
    return getComboBox(SimSystem.getRegistry().getFactoryOrEmptyList(
        abstractFactory, null));
  }

  @Override
  public String getName() {
    return "Simulation Parameters";
  }
}
