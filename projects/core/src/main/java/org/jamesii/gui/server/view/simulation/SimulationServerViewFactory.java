/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server.view.simulation;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.factories.Context;
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
  public IServiceView create(ParameterBlock params, Context context) {
    ISimulationServer server = null;
    Contribution contr = Contribution.EDITOR;
    if (params != null) {
      server =
              params
                  .getSubBlockValue(ServiceViewFactory.SERVICE);
      contr =
              params
                  .getSubBlockValue(ServiceViewFactory.CONTRIBUTAION);
    }
    if (server != null) {
      return new SimulationServerView(server, contr);
    }

    SimSystem.report(Level.WARNING, "No simulation server given to create view!");
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
