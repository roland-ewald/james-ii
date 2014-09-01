/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server.view.master;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.service.view.IServiceView;
import org.jamesii.gui.service.view.plugintype.ServiceViewFactory;

/**
 * The factory to create a master server view.
 * 
 * @author Stefan Leye
 * 
 */
public class MasterServerViewFactory extends ServiceViewFactory {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 4152098805224354294L;

  @Override
  public IServiceView create(ParameterBlock params) {
    IMasterServer server = null;
    Contribution contr = Contribution.EDITOR;
    if (params != null) {
      server =
              params.getSubBlockValue(ServiceViewFactory.SERVICE);
      contr =
              params
                  .getSubBlockValue(ServiceViewFactory.CONTRIBUTAION);
    }
    if (server != null) {
      return new MasterServerView(server, contr);
    }

    SimSystem.report(Level.WARNING, "No master server given to create view!");
    return null;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (params != null) {
      if (params.getSubBlock(ServiceViewFactory.SERVICE).getValue() instanceof IMasterServer) {
        return 1;
      }
    }
    return 0;
  }

}
