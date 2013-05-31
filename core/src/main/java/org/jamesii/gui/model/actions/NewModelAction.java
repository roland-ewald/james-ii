/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.actions;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.model.ModelPerspective;

// TODO: Auto-generated Javadoc
/**
 * Action that is used to create new symbolic models using a given
 * {@link ModelFactory} and is utilized in the {@link ModelPerspective}.
 * 
 * @author Roland Ewald
 */
public class NewModelAction extends AbstractAction {
  /**
   * The factory to use for symbolic model creation.
   */
  private final ModelFactory factory;

  /**
   * Instantiates a new new model action.
   * 
   * @param formalism
   *          the formalism the model is created for
   * @param modelFactory
   *          the model factory the factory that is used to create a symbolic
   *          model for given formalism
   * @param window
   *          the window the action is used in
   */
  public NewModelAction(Formalism formalism, ModelFactory modelFactory,
      IWindow window) {
    super("org.jamesii.model.new." + formalism.getName()
        + formalism.getSimpleId(), formalism.getName(), new String[] {
        "org.jamesii.menu.main/org.jamesii.file/org.jamesii.new/model.new",
        "org.jamesii.toolbar.main/org.jamesii.new/model.new" }, null, null,
        window);
    factory = modelFactory;
  }

  @Override
  public void execute() {
    ISymbolicModel<?> model = factory.create();
    if (model == null) {
      SimSystem.report(Level.SEVERE,
          "Model creation failed: The model factory ' " + factory.getName()
              + "' return null instead of a new model.");
      return;
    }
    ModelPerspective.getSymbolicModelWindowManager().addModel(model);
  }

}
