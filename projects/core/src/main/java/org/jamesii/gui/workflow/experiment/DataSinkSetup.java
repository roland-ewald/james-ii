/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * The Class DataSinkSetup.
 * 
 * @author Stefan Rybacki
 */
public class DataSinkSetup extends AbstractWizardPage {

  /** The page. */
  private JPanel page;

  private ConfigureFactoryPanel<DataStorageFactory> configuration;

  public static final String DATASINK_FACTORY = "datasink_factory";

  public static final String DATASINK_PARAMETERS = "datasink_parameters";

  /**
   * Instantiates a new model loader editor.
   */
  public DataSinkSetup() {
    super("Setup data sink",
        "Select a data sink to write the simulation data to");
  }

  @Override
  protected JComponent createPage() {
    page = new JPanel();
    page.setLayout(new BorderLayout());

    return page;
  }

  @Override
  protected void persistData(IWizard wizard) {
    // TODO Auto-generated method stub
    wizard.putValue(DATASINK_FACTORY, configuration
        .getSelectedFactoryAndParameter().getFactory());
    wizard.putValue(DATASINK_PARAMETERS, configuration
        .getSelectedFactoryAndParameter().getParameters());
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    // TODO Auto-generated method stub
    page.removeAll();

    // read params for data sink and selected factory
    DataStorageFactory oldFactory = wizard.getValue(DATASINK_FACTORY);
    ParameterBlock params = wizard.getValue(DATASINK_PARAMETERS);

    List<DataStorageFactory> choosableFactories = new ArrayList<>();
    try {
      // now get available factories
      choosableFactories =
          SimSystem.getRegistry().getFactories(DataStorageFactory.class);
    } catch (Exception e) {

    }

    page.add(configuration =
        new ConfigureFactoryPanel<>(choosableFactories, "Select Data Sink",
            oldFactory, params), BorderLayout.CENTER);

    page.revalidate();
    page.repaint();
  }

  @Override
  public boolean canBack(IWizard wizard) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return true;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    // TODO Auto-generated method stub
    return null;
  }

}
