/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.model.ModelPerspective;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;

/**
 * Action listener that opens a model.
 * 
 * @author Roland Ewald
 */
public class OpenModelAction implements ActionListener {

  /**
   * Factory parameter dialog to be used.
   */
  private final IFactoryParameterDialog<ModelReaderFactory> dialog;

  /**
   * Default constructor.
   * 
   * @param openModelDialog
   *          the dialog
   */
  public OpenModelAction(
      IFactoryParameterDialog<ModelReaderFactory> openModelDialog) {
    dialog = openModelDialog;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Pair<ParameterBlock, ModelReaderFactory> parameter =
        dialog.getFactoryParameter(null);

    if (parameter == null) {
      return;
    }

    ModelReaderFactory factory = parameter.getSecondValue();

    if (factory == null) {
      return;
    }

    ModelPerspective.getSymbolicModelWindowManager().addModel(factory,
        parameter.getFirstValue(), null);
  }
}
