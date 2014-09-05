/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server.view.master;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.distributed.masterserver.ServiceRegistryObserver;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.core.hosts.system.IMSSystemHostInformation;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.services.IService;
import org.jamesii.core.services.ServiceInfo;
import org.jamesii.core.util.logging.remote.RemoteLogObserver;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.james.PlugInView;
import org.jamesii.gui.application.logging.BasicLogView;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.server.ServerView;
import org.jamesii.gui.server.ServiceAction;
import org.jamesii.gui.server.view.util.NodeInfo;
import org.jamesii.gui.server.view.util.UserMutableTreeNode;
import org.jamesii.gui.service.view.IServiceView;
import org.jamesii.gui.service.view.plugintype.AbstractServiceViewFactory;
import org.jamesii.gui.service.view.plugintype.ServiceViewFactory;
import org.jamesii.gui.utils.FilteredTreeModel;
import org.jamesii.gui.utils.TextFilter;

/**
 * Basic view that displays information about services registered at a master
 * server. The view is automatically updated if a service gets
 * registered/unregistered at the viewed master server. By default the services
 * are grouped by their types. If available a log view can be be opened per
 * service, the list of plug-ins installed at the modeling and simulation system
 * the services is started with can be retrieved, and methods marked with the
 * {@link org.jamesii.core.services.TriggerableByName} interface are added as
 * actions which can then be send to the services.
 * 
 * @author Jan Himmelspach
 * @author Stefan Leye
 */
public class MasterServerView extends ServerView {

  private final class ManageServiceAction extends AbstractAction {
    private ManageServiceAction(String id, String label, Icon icon,
        String[] paths, String keyStroke, Integer mnemonic, IWindow window) {
      super(id, label, icon, paths, keyStroke, mnemonic, window);
    }

    @Override
    public void execute() {
      synchronized (this) {
        IService service = null;
        String address = getAddressForNode(getSelectedNodeInfo());
        try {

          service = (IService) Naming.lookup(address);
        } catch (Exception e) {
          SimSystem.report(Level.INFO, "Error on connecting to " + address);
          SimSystem.report(e);
        }
        ParameterBlock viewParams = new ParameterBlock();
        viewParams.addSubBlock(ServiceViewFactory.SERVICE, service);
        ServiceViewFactory factory =
            SimSystem.getRegistry().getFactory(
                AbstractServiceViewFactory.class, viewParams);
        IServiceView view = factory.create(viewParams, SimSystem.getRegistry().createContext());
        view.setupObservers();
        getWindowManager().addWindow(view);
      }
    }
  }

  private final class ShowPluginsAction extends AbstractAction {
    private ShowPluginsAction(String id, String label, Icon icon,
        String[] paths, String keyStroke, Integer mnemonic, IWindow window) {
      super(id, label, icon, paths, keyStroke, mnemonic, window);
    }

    @Override
    public void execute() {
      synchronized (this) {
        IService service = null;
        String address = getAddressForNode(getSelectedNodeInfo());
        try {

          service = (IService) Naming.lookup(address);
        } catch (Exception e) {
          SimSystem.report(Level.INFO, "Error on connecting to " + address);
          SimSystem.report(e);
        }

        if (service instanceof IMSSystemHostInformation) {
          IMSSystemHostInformation host = (IMSSystemHostInformation) service;
          PlugInView view;
          try {
            String name = "";
            if (service instanceof IMSSystemHost) {
              name = service.getName();
            }
            view =
                new PlugInView(name, host.getPluginInfo(), Contribution.EDITOR);

            getWindowManager().addWindow(view);

          } catch (RemoteException e) {
            SimSystem.report(e);
          }
        }

      }
    }
  }

  private final class ShowLogAction extends AbstractAction {
    private ShowLogAction(String id, String label, Icon icon, String[] paths,
        String keyStroke, Integer mnemonic, IWindow window) {
      super(id, label, icon, paths, keyStroke, mnemonic, window);
    }

    @Override
    public void execute() {
      synchronized (this) {
        IService service = null;
        String address = getAddressForNode(getSelectedNodeInfo());
        try {

          service = (IService) Naming.lookup(address);
        } catch (Exception e) {
          SimSystem.report(Level.INFO, "Error on connecting to " + address);
          SimSystem.report(e);
        }

        if (service instanceof IMSSystemHost) {
          IMSSystemHost host = (IMSSystemHost) service;

          BasicLogView logView;
          try {
            logView =
                new BasicLogView(service.getName(), Contribution.EDITOR, null);

            IRemoteObserver listener = new RemoteLogObserver(logView, true);
            host.registerRemoteObserver(listener);
            getWindowManager().addWindow(logView);
          } catch (RemoteException e) {
            SimSystem.report(e);
          }
        }

      }
    }
  }

  /**
   * A tree model providing the plug+ins as tree like structure for use in a
   * JTree.
   * 
   * @author Jan Himmelspach
   */
  private static class ServiceTreeModel extends DefaultTreeModel {

    /**
     * Map that maps ids to the nodes containing the service info's data.
     */
    private Map<Integer, UserMutableTreeNode> serviceMapping;

    /**
     * The type mapping - maps service types to the tree nodes which hold the
     * services of this type.
     */
    private Map<String, UserMutableTreeNode> typeMapping;

    /**
     * Converts a service info entry into a tree node.
     * 
     * @param si
     *          the si
     * @return the default mutable tree node
     */
    public static UserMutableTreeNode serviceInfoToNode(ServiceInfo si) {
      UserMutableTreeNode node2;
      node2 =
          new UserMutableTreeNode(new NodeInfo<>(String.format(
              "<html><b>%s</b> <i> %s</i></html>", si.getName(),
              si.getDescription()), si));

      node2.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
          "<html><b>Concurrent jobs: </b> <i> %d</i></html>",
          si.getConcurrent()), si)));

      node2.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
          "<html><b>Host address: </b> <i> %s</i></html>", si.getHostAddress()
              .getHostAddress()), si)));

      node2.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
          "<html><b>Host name: </b> <i> %s</i></html>", si.getHostAddress()
              .getHostName()), si)));

      return node2;
    }

    /**
     * Checks whether the tree model already contains a node for the given type.
     * 
     * @param st
     *          the st
     * @return true, if successful
     */
    public boolean containsTypeNode(Class<?> st) {
      return typeMapping.containsKey(st.toString());
    }

    /**
     * Checks whether the tree model already contains a node for the given type.
     * 
     * @param info
     *          the service information
     * @return true, if successful
     */
    public boolean containsServiceNode(ServiceInfo info) {
      return serviceMapping.containsKey(info.getLocalID());
    }

    /**
     * Add a node for service types.
     * 
     * @param st
     *          the type of the service
     * @return the new node or the reference to the already existing tree node
     */
    protected UserMutableTreeNode addTypeNode(Class<?> st) {
      if (containsTypeNode(st)) {
        return typeMapping.get(st.toString());
      }

      DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getRoot();

      // otherwise add the type node
      UserMutableTreeNode node;

      insertNodeInto(
          node =
              new UserMutableTreeNode(String.format("<html><b>%s</b> </html>",
                  st)), rootNode, 0);

      // remember the service type root node
      typeMapping.put(st.toString(), node);

      return node;
    }

    /**
     * Update. Adds the service info if its not existing, removes the
     * information if it is already in there. This method is currently based on
     * the correctness of the localid field of service infos.
     * 
     * @param info
     *          the info
     */
    public void update(ServiceInfo info) {

      DefaultMutableTreeNode typeNode = null;
      // add a new type node or retrieve the node of the type
      typeNode = addTypeNode(info.getType());

      // let's see whether we have to remove (it already exists) or to
      // add the
      // node
      if (containsServiceNode(info)) {
        // remove
        removeNodeFromParent(serviceMapping.get(info.getLocalID()));

        if (typeNode.getChildCount() == 0) {
          removeNodeFromParent(typeNode);
        }
      } else {
        // add
        // let's add the new service
        UserMutableTreeNode nameNode = ServiceTreeModel.serviceInfoToNode(info);

        serviceMapping.put(info.getLocalID(), nameNode);
        insertNodeInto(nameNode, typeNode, 0);
      }

    }

    /**
     * Creates a new instance.
     * 
     * @param server
     *          the server
     */
    public ServiceTreeModel(IMasterServer server) {
      super(new DefaultMutableTreeNode(
          "<html><b>Registered services at master server</b></html>"), true);
      root = (DefaultMutableTreeNode) getRoot();

      serviceMapping = new HashMap<>();

      typeMapping = new HashMap<>();

      // maybe better not the real services ...
      List<ServiceInfo> services = null;
      List<Class<?>> serviceTypes = null;
      try {
        services = server.getRegisteredServices();
        // for (Object o : services) {
        // System.out.println(o);
        // }
        serviceTypes = server.getRegisteredServiceTypes();
      } catch (RemoteException e) {
        SimSystem.report(e);
      }

      if (serviceTypes == null) {
        return;
      }

      for (Class<?> st : serviceTypes) {
        UserMutableTreeNode node = addTypeNode(st);

        for (ServiceInfo si : services) {
          // try {
          // System.out.println(s.getServiceType());
          if (si.getType() == st) {
            UserMutableTreeNode node2 = serviceInfoToNode(si);
            node.add(node2);
            // store the node - service info relation
            serviceMapping.put(si.getLocalID(), node2);
          }
          // } catch (RemoteException e) {
          // SimSystem.report (e);
          // }
        }

      }
    }

    /** Serialization ID. */
    private static final long serialVersionUID = -7755537722696968072L;

  }

  /** The action to show the manager of a service. */
  private IAction manageAction;

  /** The log action. */
  private IAction logAction;

  /** The action to show the plugins of a service. */
  private IAction pluginAction;

  /**
   * Creates a new service view.
   * 
   * @param contribution
   *          the vies's contribution
   * @param server
   *          the server
   */
  public MasterServerView(IMasterServer server, Contribution contribution) {
    super("Service Inspector", server, contribution);
  }

  @Override
  protected FilteredTreeModel<String> createModel() {
    return new FilteredTreeModel<>(new ServiceTreeModel(
        (IMasterServer) getServer()), new TextFilter());
  }

  /**
   * Register event. Fired if a services is registered / unregister. According
   * to the event the view is updated.
   * 
   * @param info
   *          the info
   */
  public void registerEvent(ServiceInfo info) {

    ((ServiceTreeModel) getModel().getModel()).update(info);

  }

  @Override
  public void setupObservers() {
    IMediator mediator = new Mediator();

    try {
      IMasterServer masterServer = (IMasterServer) getServer();
      masterServer.setManagementMediator(mediator);
      masterServer.registerRemoteObserver(new ServiceRegistryObserver(this));
    } catch (RemoteException e) {
      SimSystem.report(e);
    }
  }

  @Override
  protected IAction[] generateActions() {

    IAction[] inh = super.generateActions();

    List<IAction> actions = new ArrayList<>(Arrays.asList(inh));

    Icon manageIcon =
        IconManager.getIcon(IconIdentifier.OPEN_SMALL, "manage service");
    Icon logIcon = IconManager.getIcon(IconIdentifier.FOLDER_SMALL, "Show Log");
    Icon pluginIcon =
        IconManager.getIcon(IconIdentifier.INFO_SMALL, "Inspect Plug-ins");

    manageAction = createShowServiceViewAction(manageIcon);
    manageAction.setEnabled(getSelectedNodeInfo() != null);
    actions.add(manageAction);

    logAction = createShowLogViewAction(logIcon);
    logAction.setEnabled(getSelectedNodeInfo() != null);
    actions.add(logAction);

    pluginAction = createShowPlugInViewAction(pluginIcon);
    pluginAction.setEnabled(getSelectedNodeInfo() != null);
    actions.add(pluginAction);

    if (getSelectedNodeInfo() != null) {
      ServiceInfo info = (ServiceInfo) getSelectedNodeInfo().getInfo();
      Map<String, List<String[]>> commandList = info.getPossibleCommands();
      for (Entry<String, List<String[]>> entry : commandList.entrySet()) {
        String command = entry.getKey();
        IAction action =
            new ServiceAction(command, command, new String[] { "" }, info,
                entry.getValue(), this);
        action.setEnabled(getSelectedNodeInfo() != null);
        actions.add(action);
      }
    }
    return actions.toArray(new IAction[actions.size()]);
  }

  /**
   * Creates an action to show the service view of the selected service.
   * 
   * @param icon
   *          the icon for the action
   * @return the action
   */
  private IAction createShowServiceViewAction(Icon icon) {
    return new ManageServiceAction("manag.show", "manage service", icon,
        new String[] { "" }, null, null, this);
  }

  /**
   * Creates an action to show the log of the selected service.
   * 
   * @param icon
   *          the icon for the action
   * @return the action
   */
  private IAction createShowLogViewAction(Icon icon) {
    return new ShowLogAction("log.show", "Show Log", icon, new String[] { "" },
        null, null, this);
  }

  /**
   * Creates an action to show the plug-ins of a service.
   * 
   * @param icon
   *          the icon for the action
   * @return the action
   */
  private IAction createShowPlugInViewAction(Icon icon) {
    return new ShowPluginsAction("plugins.show", "Inspect Plug-ins", icon,
        new String[] { "" }, null, null, this);
  }

  /**
   * Retrieves the address of a node, representing a service.
   * 
   * @param o
   *          the node
   * @return the address
   */
  @SuppressWarnings("unchecked")
  private String getAddressForNode(NodeInfo o) {
    if (o == null) {
      return "";
    }
    ServiceInfo info = (ServiceInfo) o.getInfo();
    return "rmi://" + info.getHostAddress().getHostName() + ":"
        + MasterServer.DEFAULT_PORT + "/" + info.getName();
  }

}
