/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.tutorials;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.gui.utils.AbstractComboBoxModel;

/**
 * Simple list model that provides a list of {@link ModelWindowFactory}s that
 * are suitable for a specified {@link ISymbolicModel}.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class EditorListModel extends AbstractComboBoxModel<ModelWindowFactory> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 5376015970611644024L;

  /**
   * Instantiates a new editor list model. Where the model contains all the
   * {@link ModelWindowFactory}s that can handle the provided symbolic model.
   * 
   * @param model
   *          the model all {@link ModelWindowFactory}s are requested for
   */
  public EditorListModel(ISymbolicModel<?> model) {
    // get editors available for formalism (if none disable next button)
    ParameterBlock amwfp =
        new ParameterBlock(model, AbstractModelWindowFactory.MODEL);

    final List<ModelWindowFactory> modelWindowFactories =
        SimSystem.getRegistry().getFactoryOrEmptyList(
            AbstractModelWindowFactory.class, amwfp);

    for (ModelWindowFactory f : modelWindowFactories) {
      addElement(f);
    }
  }
}
