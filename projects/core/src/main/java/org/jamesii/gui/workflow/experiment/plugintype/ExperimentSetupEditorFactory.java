/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.plugintype;

import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class for all factories to setup a specialized experiment editor.
 * 
 * @author Jan Himmelspach
 */
public abstract class ExperimentSetupEditorFactory extends
    Factory<IExperimentSetup> implements IParameterFilterFactory {

  /**
   * The identifier for the MODEL to pass in parameter blocks to create setup.
   */
  public static final String MODEL = AbstractModelReaderFactory.MODEL;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3751984415855886452L;

  /**
   * Creates specialized experiment editor for use in experiment workflow.
   * 
   * @param params
   *          the parameter block to configure the wizard page
   * @return newly created editor
   */
  @Override
  public abstract IExperimentSetup create(ParameterBlock params);

}
