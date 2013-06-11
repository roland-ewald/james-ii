/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.perspective.AbstractPerspective;
import org.jamesii.gui.visualization.offline.dialogs.OfflineVisDialog;

/**
 * Perspective providing means for visual data analysis.
 * 
 * @author Stefan Rybacki
 * 
 */
public class VisDataAnalysisPerspective extends AbstractPerspective {

  /**
   * Dialog to configure offline visualization.
   */
  private OfflineVisDialog offlineVisDialog = new OfflineVisDialog();

  /**
   * Open config dialog for offline visualization.
   */
  protected void offlineVis() {
    offlineVisDialog.setVisible(true);
  }

  @Override
  public String getDescription() {
    return "Visual Data Analysis Perspective";
  }

  @Override
  public String getName() {
    return "Visual Data Analysis Perspective";
  }

  @Override
  protected List<IAction> generateActions() {
    List<IAction> actions = new ArrayList<>();

    actions.add(new ActionSet("org.jamesii.visualization", "Visualization",
        "org.jamesii.menu.main?after=org.jamesii.edit", null));

    actions.add(new AbstractAction("org.jamesii.visualization.offline",
        "Offline visualization...",
        new String[] { "org.jamesii.menu.main/org.jamesii.visualization" },
        null) {
      private ActionListener action = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          offlineVis();
        }
      };

      @Override
      public void execute() {
        action.actionPerformed(null);
      }

    });

    return actions;
  }

}
