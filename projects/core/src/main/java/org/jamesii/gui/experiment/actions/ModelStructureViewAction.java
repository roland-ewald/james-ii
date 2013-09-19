/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JLabel;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.ComputationRuntimeState;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;
import org.jamesii.gui.visualization.modelbrowser.plugintype.AbstractModelBrowserFactory;
import org.jamesii.gui.visualization.modelbrowser.plugintype.ModelBrowserFactory;

/**
 * Action to view the model structure.
 * 
 * @author Jan Himmelspach
 * 
 */
public class ModelStructureViewAction extends AbstractSimAction {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 3478403937357417355L;

  /**
   * Instantiates a new run sim action.
   */
  public ModelStructureViewAction() {
    super("View model structure");
    setEnabled(false);
    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.FIND_SMALL, "View model structure"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    synchronized (this) {

      if (!SimSystem.getRegistry().hasFactories(
          AbstractModelBrowserFactory.class)) {
        return;
      }

      ParameterBlock pBlock = new ParameterBlock();

      pBlock.addSubBlock(AbstractModelBrowserFactory.MODEL, getModel());

      List<ModelBrowserFactory> factories =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelBrowserFactory.class, pBlock);

      ModelBrowserFactory mbFac = null;

      if (factories.size() == 0) {
        return;
      }

      if (factories.size() > 1) {
        FactorySelectionDialog<ModelBrowserFactory> dlg =
            new FactorySelectionDialog<>(WindowManagerManager
                .getWindowManager().getMainWindow(), factories, new JLabel(
                "Select a model browser to be used"),
                "Model browser selection", true);
        dlg.setVisible(true);
        if (dlg.getSelectedFactories().size() == 0) {
          return;
        }
        mbFac = dlg.getSelectedFactories().get(0);
      } else {
        mbFac = factories.get(0);
      }

      // ModelBrowserFactory mbFac =
      // SimSystem.getRegistry().getFactory(AbstractModelBrowserFactory.class,
      // pBlock);

      WindowManagerManager.getWindowManager().addWindow(mbFac.create(pBlock));

      // FIXME the window has to be closed! Btw what about these actions and
      // concurrent runs?

    }

  }

  @Override
  public void refreshIcon(ComputationTaskRuntimeInformation srti) {
    synchronized (this) {
      ComputationRuntimeState state = srti.getState();

      // if not paused we do not need to continue
      if (state != ComputationRuntimeState.PAUSED) {
        setEnabled(false);
        return;
      }

      // safety check #1: get out if there are no factories at all
      if (!SimSystem.getRegistry().hasFactories(
          AbstractModelBrowserFactory.class)) {
        setEnabled(false);
        return;
      }

      // safety check #2: get out if there is no matching factory
      ParameterBlock pBlock = new ParameterBlock();
      pBlock.addSubBlock(AbstractModelBrowserFactory.MODEL, srti
          .getComputationTask().getModel());
      try {
        SimSystem.getRegistry().getFactory(AbstractModelBrowserFactory.class,
            pBlock);
      } catch (Exception e) {
        setEnabled(false);
        return;
      }

      // there is at least one factory, let's check whether we are pausing
      // (again, ok ...)
      switch (state) {
      case PAUSED:
        setEnabled(true);
        break;
      default:
        setEnabled(false);
      }
    }
  }
}
