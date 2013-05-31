/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server;

import java.rmi.RemoteException;

import javax.swing.JComponent;
import javax.swing.tree.DefaultTreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.hosts.system.IMSSystemHostInformation;
import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.james.DefaultTreeView;
import org.jamesii.gui.perspective.SystemInfoView;

/**
 * View of the system information for a server.
 * 
 * @author Stefan Leye
 * 
 */
public class ServerInfoView extends DefaultTreeView {

  /**
   * Reference to the server.
   */
  private IMSSystemHostInformation server;

  /**
   * Name of the server.
   */
  private String serverName;

  /**
   * Creates a new service view.
   * 
   * @param contribution
   *          the vies's contribution
   * @param serverInfo
   *          the server info
   * @param serverName
   *          the server name
   */
  public ServerInfoView(IMSSystemHostInformation serverInfo, String serverName,
      Contribution contribution) {
    super("System information Inspector", new DefaultTreeModel(null),
        contribution, null);
    server = serverInfo;
    this.serverName = serverName;
    setTitle(serverName + ":" + getTitle());
  }

  @Override
  public JComponent createContent() {
    JavaInfo info = new JavaInfo();
    if (server != null) {
      try {
        info = server.getVMinfo();
      } catch (RemoteException e) {
        SimSystem.report(e);
      }
    }
    return new SystemInfoView(info, serverName, Contribution.EDITOR)
        .getContent();
  }

}
