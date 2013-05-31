/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.perspective.AbstractPerspective;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.wizard.Wizard;
import org.jamesii.gui.workflow.experiment.SimulationSetupWorkflow;
import org.jamesii.gui.workflow.tutorials.BasicWorkflow;

/**
 * Perspective that handles the UI elements required for workflowing.
 * 
 * @author Stefan Rybacki
 */
final class WorkflowPerspective extends AbstractPerspective {
  private static final IPerspective instance = new WorkflowPerspective();

  /**
   * Instantiates a new workflow perspective.
   */
  private WorkflowPerspective() {
  }

  @Override
  protected List<IAction> generateActions() {
    List<IAction> actions = new ArrayList<>();

    actions.add(new ActionSet("org.jamesii.workflow", "Workflow",
        "org.jamesii.menu.main?before=org.jamesii.help", null));
    actions.add(new AbstractAction("org.jamesii.workflow.overview",
        "Start simple M&S",
        new String[] { "org.jamesii.menu.main/org.jamesii.workflow" }, null) {

      @Override
      public void execute() {
        // show wizard
        Wizard wizard =
            new Wizard(getWindowManager().getMainWindow(), new BasicWorkflow());
        wizard.showWizard();
      }

    });

    actions.add(new AbstractAction("org.jamesii.workflow.simulation",
        "Start simulation",
        new String[] { "org.jamesii.menu.main/org.jamesii.workflow" }, null) {

      @Override
      public void execute() {
        // show wizard
        Wizard wizard =
            new Wizard(getWindowManager().getMainWindow(),
                new SimulationSetupWorkflow());
        wizard.showWizard();
      }

    });

    return actions;
  }

  @Override
  public String getDescription() {
    return "Workflow Perspective";
  }

  @Override
  public String getName() {
    return "Workflow Perspective";
  }

  /**
   * Gets the single instance of WorkflowPerspective.
   * 
   * @return single instance of WorkflowPerspective
   */
  public static IPerspective getInstance() {
    return instance;
  }

}
