/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.offline.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.AbstractDataStorageFactory;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;
import org.jamesii.gui.visualization.offline.IOfflineVisualizer;
import org.jamesii.gui.visualization.offline.plugintype.AbstractOfflineVisFactory;
import org.jamesii.gui.visualization.offline.plugintype.OfflineVisFactory;

/**
 * Dialog to configure off-line visualisation.
 * 
 * @author Roland Ewald
 * 
 */
public class OfflineVisDialog extends JDialog {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -527280198020881915L;

  /**
   * Panel to configure data storage factory.
   */
  private ConfigureFactoryPanel<DataStorageFactory> confDSPanel;

  /**
   * Panel to configure off-line visualiser factory.
   */
  private ConfigureFactoryPanel<OfflineVisFactory> confOVPanel;

  /**
   * Button to start the off-line visualisation.
   */
  private JButton runOfflineVis = new JButton("Run");
  {
    runOfflineVis.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        runOfflineVis();
      }
    });
  }

  /**
   * Instantiates a new offline vis dialog.
   */
  public OfflineVisDialog() {
    setSize(400, 400);
    getContentPane().setLayout(
        new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    // Init UI
    confDSPanel =
        new ConfigureFactoryPanel<>(SimSystem.getRegistry()
            .getFactoryOrEmptyList(AbstractDataStorageFactory.class, null),
            "Select data storage to feed visualization:", null, null);

    this.getContentPane().add(confDSPanel);

    List<OfflineVisFactory> choosableFactories = null;

    choosableFactories =
        SimSystem.getRegistry().getFactoryOrEmptyList(
            AbstractOfflineVisFactory.class, null);

    confOVPanel =
        new ConfigureFactoryPanel<>(choosableFactories,
            "Select Offline Visualizer:", null, null);

    this.getContentPane().add(confOVPanel);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(runOfflineVis);
    this.getContentPane().add(buttonPanel);

    setLocationRelativeTo(null);
  }

  /**
   * Starts thread containing the off-line visualisation as configured by the
   * user.
   */
  protected void runOfflineVis() {
    this.setVisible(false);

    ParameterizedFactory<DataStorageFactory> dsFactoryPair =
        confDSPanel.getSelectedFactoryAndParameter();

    IDataStorage dataStorage = null;

    if (dsFactoryPair.getFactory() != null) {
      dataStorage =
          dsFactoryPair.getFactory().create(dsFactoryPair.getParameters(), SimSystem.getRegistry().createContext());
    }

    ParameterizedFactory<OfflineVisFactory> ovFactoryPair =
        confOVPanel.getSelectedFactoryAndParameter();

    final IOfflineVisualizer olVisualiser =
        ovFactoryPair.getFactory().create(dataStorage,
            ovFactoryPair.getParameters());

    List<IWindow> displayWindows = olVisualiser.getDisplayWindows();

    for (IWindow win : displayWindows) {
      WindowManagerManager.getWindowManager().addWindow(win);
    }

    Thread visThread = new Thread() {
      @Override
      public void run() {
        olVisualiser.run();
      }
    };
    visThread.start();
  }
}
