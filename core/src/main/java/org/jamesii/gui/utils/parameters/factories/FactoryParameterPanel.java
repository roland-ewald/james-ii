/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.gui.utils.objecteditor.ObjectEditorComponent;
import org.jamesii.gui.utils.objecteditor.property.editor.lists.ListPropertyEditor;
import org.jamesii.gui.utils.parameters.factories.implementationprovider.PluginTypeImplementationProvider;
import org.jamesii.gui.utils.parameters.factories.propertyprovider.FactoryParameterPropertyProvider;
import org.jamesii.gui.utils.parameters.factories.propertyprovider.ListParametersPropertyProvider;
import org.jamesii.gui.utils.parameters.factories.propertyprovider.PluginTypeParameterPropertyProvider;

/**
 * Panel to display and edit the parameters of a factory.
 * 
 * @author Stefan Rybacki
 */
public class FactoryParameterPanel extends ObjectEditorComponent {

  /** Serialization ID. */
  private static final long serialVersionUID = 7509265937335712232L;

  /**
   * The parameters wrapper class usable by {@link ObjectEditorComponent}
   */
  private FactoryParameters parameters;

  /**
   * Default constructor.
   * 
   * @param factoryToBeEdited
   *          the factory parameter information
   * @param parameterBlock
   *          parameter block for this factory
   */
  public FactoryParameterPanel(IFactoryInfo factoryToBeEdited,
      ParameterBlock parameterBlock) {
    super(null, "Parameter", "Value");

    if (factoryToBeEdited != null) {
      parameters =
          new FactoryParameters(factoryToBeEdited.getClassname(),
              parameterBlock);
    }

    setValueToEdit(parameters);

    registerPropertyProvider(FactoryParameters.class,
        new FactoryParameterPropertyProvider());
    registerPropertyProvider(PluginTypeParameters.class,
        new PluginTypeParameterPropertyProvider());
    registerPropertyProvider(ListParameters.class,
        new ListParametersPropertyProvider());
    registerImplementationProvider(new PluginTypeImplementationProvider());
    registerEditor(ListParameters.class, new ListPropertyEditor());
  }

  /**
   * Gets the factory.
   * 
   * @return the factory
   */
  public String getFactory() {
    return parameters.getFactory();
  }

  /**
   * Get parameter block as edited by the user.
   * 
   * @return the parameter block
   */
  public ParameterBlock getParameterBlock() {
    return parameters.getAsParameterBlock();
  }
}
