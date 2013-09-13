/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.distributed.simulationserver.SimulationClientThread;
import org.jamesii.core.hosts.Host;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.core.hosts.system.IMSSystemHostInformation;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.services.IService;
import org.jamesii.core.util.logging.remote.RemoteLogObserver;
import org.jamesii.core.util.misc.exec.SimulationServerVMThread;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.SeparatorAction;
import org.jamesii.gui.application.james.PlugInView;
import org.jamesii.gui.application.logging.BasicLogView;
import org.jamesii.gui.perspective.AbstractPerspective;
import org.jamesii.gui.service.view.IServiceView;
import org.jamesii.gui.service.view.plugintype.AbstractServiceViewFactory;
import org.jamesii.gui.service.view.plugintype.ServiceViewFactory;

/**
 * Perspective that provides means to manage simulation servers
 * 
 * @author Stefan Rybacki
 */
public class ServerManagementPerspective extends AbstractPerspective {

  private final class ViewServerLogAction extends AbstractAction {
    private ViewServerLogAction(String id, String label, String[] paths,
        IWindow window) {
      super(id, label, paths, window);
    }

    @Override
    public void execute() {
      String serverAdress = "";
      try {
        serverAdress = getServerAddress();
        if (serverAdress == null) {
          return;
        }

        IMSSystemHost server = (IMSSystemHost) Naming.lookup(serverAdress);
        BasicLogView logView =
            new BasicLogView(server.getName(), Contribution.EDITOR, null);
        IRemoteObserver listener = new RemoteLogObserver(logView, true);
        server.registerRemoteObserver(listener);
        getWindowManager().addWindow(logView);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  private final class InspectServerPlugInsAction extends AbstractAction {
    private InspectServerPlugInsAction(String id, String label, String[] paths,
        IWindow window) {
      super(id, label, paths, window);
    }

    @Override
    public void execute() {
      String serverAdress = "";
      try {
        serverAdress = getServerAddress();
        if (serverAdress == null) {
          return;
        }

        IMSSystemHostInformation server =
            (IMSSystemHostInformation) Naming.lookup(serverAdress);
        String name = "";
        if (server instanceof IMSSystemHost) {
          name = ((IMSSystemHost) server).getName();
        }
        PlugInView view =
            new PlugInView(name, server.getPluginInfo(), Contribution.EDITOR);
        getWindowManager().addWindow(view);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  private final class ShowSeverSystemInformationAction extends AbstractAction {
    private ShowSeverSystemInformationAction(String id, String label,
        String[] paths, IWindow window) {
      super(id, label, paths, window);
    }

    @Override
    public void execute() {

      String serverAdress = getServerAddress();
      // if (serverAdress == null)
      // return;

      IMSSystemHostInformation serverInfo = null;
      IMSSystemHost server = null;
      String serverName = "";
      try {
        serverInfo = (IMSSystemHostInformation) Naming.lookup(serverAdress);
        server = (IMSSystemHost) Naming.lookup(serverAdress);
        serverName = (server != null) ? server.getName() : "local host";
        getWindowManager().addWindow(
            new ServerInfoView(serverInfo, serverName, Contribution.EDITOR));
      } catch (Exception e) {
        SimSystem.report(e);
      }

    }
  }

  private static final class StartSimulationServersAction extends
      AbstractAction {
    private StartSimulationServersAction(String id, String label,
        String[] paths, IWindow window) {
      super(id, label, paths, window);
    }

    @Override
    public void execute() {

      SimulationServerCreationDialog dlg =
          new SimulationServerCreationDialog("Create simulation servers");

      dlg.setVisible(true);

      if (!dlg.isCancelled()) {

        // let's ask for the ms to be used, this should be integrated into
        // the other dlg ...
        String serverAddress = dlg.getMasterServerAdress();

        // let's create the servers according to the settings ...
        for (int i = 0; i < dlg.getNumberOfServer(); i++) {

          String name = dlg.getBaseName() + SimSystem.getUId();

          // do we have to use an extra vm per server?
          if (dlg.getUseExtraVM()) {

            // create server in extra virtual machine
            SimulationServerVMThread scvmt =
                new SimulationServerVMThread("-server=" + serverAddress, name);
            scvmt.start();

            SimSystem.report(Level.INFO, "Created a simulation server in an extra VM +("
            + name + ")");

          } else {

            // create server in extra thread
            SimulationClientThread sct =
                new SimulationClientThread("-server=" + serverAddress, name);
            sct.start();

            SimSystem.report(Level.INFO, "Created a simulation server in a separate thread +("
            + name + ")");

          }

        }

      }

    }
  }

  private final class StartManageServerAction extends AbstractAction {
    private StartManageServerAction(String id, String label, String[] paths,
        IWindow window) {
      super(id, label, paths, window);
    }

    @Override
    public void execute() {
      try {
        // start new master server as thread
        LocateRegistry.createRegistry(MasterServer.DEFAULT_PORT);
        MasterServer server =
            new MasterServer(MasterServer.DEFAULT_BINDING_NAME);
        Host.publish(server, MasterServer.DEFAULT_PORT);
        ParameterBlock viewParams = new ParameterBlock();
        viewParams.addSubBlock(ServiceViewFactory.SERVICE, server);
        ServiceViewFactory factory =
            SimSystem.getRegistry().getFactory(
                AbstractServiceViewFactory.class, viewParams);
        IServiceView view = factory.create(viewParams);
        view.setupObservers();
        getWindowManager().addWindow(view);
        view.setupObservers();
        SimSystem.report(Level.INFO, "Created a master server in a separate thread +(rmi://localhost:"
        + MasterServer.DEFAULT_PORT
        + "/"
        + MasterServer.DEFAULT_BINDING_NAME + ")");
        getWindowManager().addWindow(view);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  private final class ManageServerAction extends AbstractAction {
    private ManageServerAction(String id, String label, String[] paths,
        IWindow window) {
      super(id, label, paths, window);
    }

    @Override
    public void execute() {
      IService server = null;
      try {
        String serverAdress = getServerAddress();
        if (serverAdress == null) {
          return;
        }

        server = (IService) Naming.lookup(serverAdress);
        ParameterBlock viewParams = new ParameterBlock();
        viewParams.addSubBlock(ServiceViewFactory.SERVICE, server);
        ServiceViewFactory factory =
            SimSystem.getRegistry().getFactory(
                AbstractServiceViewFactory.class, viewParams);
        IServiceView view = factory.create(viewParams);
        view.setupObservers();
        getWindowManager().addWindow(view);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  @Override
  protected List<IAction> generateActions() {
    List<IAction> actions = new ArrayList<>();

    actions.add(new ActionSet("org.jamesii.server", "Server Management",
        "org.jamesii.menu.main/?before=org.jamesii.help", null));
    actions
        .add(new ManageServerAction("org.jamesii.server.msmanagement",
            "Manage a server...",
            new String[] { "org.jamesii.menu.main/org.jamesii.server?first" },
            null));

    actions.add(new StartManageServerAction("org.jamesii.server.mssmanagement",
        "Start & manage a main server...",
        new String[] { "org.jamesii.menu.main/org.jamesii.server" }, null));

    actions
        .add(new StartSimulationServersAction(
            "org.jamesii.server.ssmanagement",
            "Start simulation servers ...",
            new String[] { "org.jamesii.menu.main/org.jamesii.server?after=org.jamesii.server.mssmanagement" },
            null));

    IAction seperator =
        SeparatorAction
            .getSeparatorFor(
                "org.jamesii.menu.main/org.jamesii.server?after=org.jamesii.server.ssmanagement",
                null);
    actions.add(seperator);

    actions.add(new ShowSeverSystemInformationAction(
        "org.jamesii.server.mssysteminfo", "Show server system information...",
        new String[] { String.format(
            "org.jamesii.menu.main/org.jamesii.server?after=%s",
            seperator.getId()) }, null));

    actions
        .add(new InspectServerPlugInsAction(
            "org.jamesii.server.pluginview",
            "Inspect plug-ins of a server...",
            new String[] { "org.jamesii.menu.main/org.jamesii.server?after=org.jamesii.server.mssysteminfo" },
            null));

    actions
        .add(new ViewServerLogAction(
            "org.jamesii.server.logview",
            "View log of a server...",
            new String[] { "org.jamesii.menu.main/org.jamesii.server?after=org.jamesii.server.pluginview" },
            null));

    return actions;
  }

  /**
   * Helper method that displays a input dialog where the user has to enter a
   * server address which is returned.
   * 
   * @return the entered server address, {@code null} if cancel was hit, if an
   *         empty address was entered a default address is returned
   */
  private String getServerAddress() {
    String serverAdress =
        "rmi://localhost:" + MasterServer.DEFAULT_PORT + "/"
            + MasterServer.DEFAULT_BINDING_NAME;

    serverAdress =
        JOptionPane.showInputDialog(WindowManagerManager.getWindowManager()
            .getMainWindow(), "Please Enter Server Address: ", serverAdress);

    if (serverAdress == null) {
      return null;
    }

    if (serverAdress.length() == 0) {
      // default master
      // server
      serverAdress =
          "rmi://localhost:" + MasterServer.DEFAULT_PORT + "/"
              + MasterServer.DEFAULT_BINDING_NAME;
    }

    return serverAdress;
  }

  @Override
  public String getDescription() {
    return "Manages simulation servers";
  }

  @Override
  public String getName() {
    return "Server Management Perspective";
  }

}
