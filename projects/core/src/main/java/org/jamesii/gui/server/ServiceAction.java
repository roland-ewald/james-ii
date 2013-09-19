/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.services.IService;
import org.jamesii.core.services.ServiceInfo;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.AbstractAction;

/**
 * Creates actions to trigger the callMethodbyName(String, List<String[]>)
 * method of a remote {@link org.jamesii.core.services.TriggerableByName}
 * 
 * @author Stefan Leye
 * 
 */
public class ServiceAction extends AbstractAction {

  /**
   * The service information.
   */
  private ServiceInfo info;

  /**
   * The parameter description of the method. Contains the type of the
   * parameters and their description.
   */
  private List<String[]> paramDescription;

  /**
   * Instantiates a new service action.
   * 
   * @param id
   *          the id
   * @param label
   *          the label
   * @param paths
   *          the paths
   * @param info
   *          the info
   * @param params
   *          the parameters
   * @param w
   *          the window the action is used in
   */
  public ServiceAction(String id, String label, String[] paths,
      ServiceInfo info, List<String[]> params, IWindow w) {
    super(id, label, paths, w);
    this.info = info;
    this.paramDescription = params;
  }

  @Override
  public void execute() {
    synchronized (this) {

      IService service = null;
      String address =
          "rmi://" + info.getHostAddress().getHostName() + ":"
              + MasterServer.DEFAULT_PORT + "/" + info.getName();
      try {
        service = (IService) Naming.lookup(address);
        int size = paramDescription.size();
        List<String[]> params = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          String param = "";
          param =
              JOptionPane.showInputDialog(WindowManagerManager
                  .getWindowManager().getMainWindow(), "Please Enter the "
                  + paramDescription.get(i)[1], param);
          String[] parameter = new String[2];
          parameter[0] = paramDescription.get(i)[0];
          parameter[1] = param;
          params.add(parameter);
        }
        service.callMethodByName(getId(), params);
      } catch (Exception e) {
        SimSystem.report(Level.INFO, "Error on connecting to " + address);
        SimSystem.report(e);
      }
    }
  }

}
