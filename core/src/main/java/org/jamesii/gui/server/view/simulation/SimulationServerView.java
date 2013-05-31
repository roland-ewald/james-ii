/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server.view.simulation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.distributed.simulationserver.SimulationManagementObserver;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.Mediator;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.server.ServerView;
import org.jamesii.gui.server.view.util.NodeInfo;
import org.jamesii.gui.server.view.util.UserMutableTreeNode;
import org.jamesii.gui.utils.FilteredTreeModel;
import org.jamesii.gui.utils.TextFilter;

/**
 * Basic view that displays simulation servers.
 * 
 * @author Stefan Leye
 * 
 */
public class SimulationServerView extends ServerView {

  /**
   * A tree model providing the plugins as tree like structure for use in a
   * {@link JTree}.
   * 
   * @author Jan Himmelspach
   */
  private static class SimulationTreeModel extends DefaultTreeModel {

    /**
     * The serialization ID.
     */
    private static final long serialVersionUID = -5865979347685444702L;

    /** Map that maps ids to the nodes containing the service info's data. */
    private Map<ComputationTaskIDObject, UserMutableTreeNode> simulationMapping;

    /**
     * Creates a new instance.
     * 
     * @param server
     *          the server
     */
    public SimulationTreeModel(ISimulationServer server) {
      super(new DefaultMutableTreeNode(
          "<html><b>Simulations running on the server</b></html>"), true);

      simulationMapping = new HashMap<>();

      // maybe better not the real services ...
      List<ComputationTaskIDObject> simulations = null;
      try {
        simulations = server.getRunningSimulations();
      } catch (RemoteException e) {
        SimSystem.report(e);
      }

      if (simulations == null) {
        return;
      }

      for (ComputationTaskIDObject info : simulations) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getRoot();
        UserMutableTreeNode node = simulationInfoToNode(info, server);
        insertNodeInto(node, rootNode, 0);
        simulationMapping.put(info, node);
      }
    }

    /**
     * Converts a simulation info entry into a tree node.
     * 
     * @param si
     *          the simulation id
     * @param server
     *          where the simulation is situated on
     * @return the default mutable tree node
     */
    public static UserMutableTreeNode simulationInfoToNode(
        ComputationTaskIDObject si, ISimulationServer server) {
      UserMutableTreeNode node =
          new UserMutableTreeNode(new NodeInfo<>(String.format(
              "<html><b>Simulation: </b> <i> %s</i></html>", si), si));

      try {

        node.add(new UserMutableTreeNode(new NodeInfo<>(
            String.format("<html><b>experiment ID: </b> <i> %s</i></html>",
                server.getSimulationRunProperty(si,
                    "CONFIGRUATION.EXPERIMENTNUMBER")), si)));

        node.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
            "<html><b>configuration ID: </b> <i> %s</i></html>",
            server.getSimulationRunProperty(si, "CONFIGRUATION.NUMBER")), si)));

        node.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
            "<html><b>Started at: </b> <i> %s</i></html>",
            new Date((Long) server.getSimulationRunProperty(si,
                "STARTTIME.WALLCLOCK"))), si)));

        node.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
            "<html><b>model class: </b> <i> %s</i></html>",
            server.getSimulationRunProperty(si, "MODEL.CLASS")), si)));

        node.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
            "<html><b>model name: </b> <i> %s</i></html>",
            server.getSimulationRunProperty(si, "MODEL.NAME")), si)));

        node.add(new UserMutableTreeNode(new NodeInfo<>(String.format(
            "<html><b>processor class: </b> <i> %s</i></html>",
            server.getSimulationRunProperty(si, "PROCESSOR.CLASS")), si)));

      } catch (RemoteException e) {
        SimSystem.report(e);
      }

      return node;
    }

    /**
     * Checks whether the tree model already contains a node for the given
     * simulation.
     * 
     * @param id
     *          the id of the simulation
     * 
     * @return true, if successful
     */
    public boolean containsSimulationNode(ComputationTaskIDObject id) {
      return simulationMapping.containsKey(id);
    }

    /**
     * Update.
     * 
     * Adds the service info if its not existing, removes the information if it
     * is already in there.
     * 
     * This method is currently based on the correctness of the localid field of
     * service infos.
     * 
     * @param info
     *          the info
     * @param server
     *          the server
     */
    public void update(ComputationTaskIDObject info, ISimulationServer server) {

      // let's see whether we have to remove (it already exists) or to add the
      // node
      if (containsSimulationNode(info)) {
        // remove
        removeNodeFromParent(simulationMapping.get(info));
        simulationMapping.remove(info);
      } else {
        // add
        // let's add the new service
        UserMutableTreeNode simNode = simulationInfoToNode(info, server);
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getRoot();
        simulationMapping.put(info, simNode);
        insertNodeInto(simNode, rootNode, 0);
      }

    }

  }

  /**
   * Default constructor.
   * 
   * @param server
   *          the server to be viewed
   * @param contribution
   *          the contribution in the perspective
   */
  public SimulationServerView(ISimulationServer server,
      Contribution contribution) {
    super("Simulation Inspector", server, contribution);
  }

  @Override
  protected FilteredTreeModel<String> createModel() {
    return new FilteredTreeModel<>(new SimulationTreeModel(
        (ISimulationServer) getServer()), new TextFilter());
  }

  @Override
  public void setupObservers() {
    IMediator mediator = new Mediator();
    try {
      ISimulationServer simServer = (ISimulationServer) getServer();
      simServer.setManagementMediator(mediator);
      simServer.registerRemoteObserver(new SimulationManagementObserver(this));
    } catch (RemoteException e) {
      SimSystem.report(e);
    }

  }

  /**
   * Simulation event.
   * 
   * @param info
   *          the info
   */
  public void simulationEvent(ComputationTaskIDObject info) {
    ((SimulationTreeModel) getModel().getModel()).update(info,
        (ISimulationServer) getServer());
  }

  @Override
  protected IAction[] generateActions() {

    IAction[] inh = super.generateActions();

    List<IAction> actions = new ArrayList<>(Arrays.asList(inh));

    Icon startIcon = IconManager.getIcon(IconIdentifier.PLAY_SMALL, "start");
    Icon pauseIcon = IconManager.getIcon(IconIdentifier.PAUSE_SMALL, "pause");
    Icon stopIcon = IconManager.getIcon(IconIdentifier.STOP_SMALL, "stop");

    IAction action =
        new AbstractAction("simulation.start", "start", startIcon,
            new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            synchronized (this) {
              ISimulationServer simServer = (ISimulationServer) getServer();
              ComputationTaskIDObject info =
                  (ComputationTaskIDObject) getSelectedNodeInfo().getInfo();
              try {
                simServer.startSimulationRun(info);
              } catch (RemoteException e) {
                SimSystem.report(e);
              }
            }
          }
        };
    action.setEnabled(getSelectedNodeInfo() != null);
    actions.add(action);

    action =
        new AbstractAction("simulation.pause", "pause", pauseIcon,
            new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            synchronized (this) {
              ISimulationServer simServer = (ISimulationServer) getServer();
              ComputationTaskIDObject info =
                  (ComputationTaskIDObject) getSelectedNodeInfo().getInfo();
              try {
                simServer.executeRunnableCommand(info, "pause", null);
              } catch (RemoteException e) {
                SimSystem.report(e);
              }
            }
          }
        };
    action.setEnabled(getSelectedNodeInfo() != null);
    actions.add(action);

    action =
        new AbstractAction("simulation.stop", "stop", stopIcon,
            new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            synchronized (this) {
              ISimulationServer simServer = (ISimulationServer) getServer();
              ComputationTaskIDObject info =
                  (ComputationTaskIDObject) getSelectedNodeInfo().getInfo();
              try {
                simServer.stopProc(info);
              } catch (RemoteException e) {
                SimSystem.report(e);
              }
            }
          }
        };
    action.setEnabled(getSelectedNodeInfo() != null);
    actions.add(action);

    return actions.toArray(new IAction[actions.size()]);
  }

}
