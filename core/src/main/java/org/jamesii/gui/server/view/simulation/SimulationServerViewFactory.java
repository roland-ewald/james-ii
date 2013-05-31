package org.jamesii.gui.server.view.simulation;

import java.util.logging.Level;

import org.jamesii.core.base.Entity;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.service.view.IServiceView;
import org.jamesii.gui.service.view.plugintype.ServiceViewFactory;

/**
 * Factory to create a
 * {@link org.jamesii.gui.server.view.simulation.SimulationServerView}.
 * 
 * @author Stefan Leye
 * 
 */
public class SimulationServerViewFactory extends ServiceViewFactory {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -604266211071106087L;

  @Override
  public IServiceView create(ParameterBlock params) {
    ISimulationServer server = null;
    Contribution contr = Contribution.EDITOR;
    if (params != null) {
      server =
          (ISimulationServer) params
              .getSubBlockValue(ServiceViewFactory.SERVICE);
      contr =
          (Contribution) params
              .getSubBlockValue(ServiceViewFactory.CONTRIBUTAION);
    }
    if (server != null) {
      return new SimulationServerView(server, contr);
    }

    Entity.report(Level.WARNING, "No simulation server given to create view!");
    return null;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {

    if ((params != null)
        && (params.getSubBlockValue(ServiceViewFactory.SERVICE) instanceof ISimulationServer)) {
      return 1;
    }
    return 0;
  }
}
