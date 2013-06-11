/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.awt.Window;
import java.io.File;
import java.util.List;

import org.jamesii.core.data.IFileHandling;
import org.jamesii.gui.utils.dialogs.BrowseFSDialogViaFactories;

/**
 * Dialog top browse for experiments.
 * 
 * @author Gabriel Blum
 */
public class BrowseExperimentsFSDialog extends BrowseFSDialogViaFactories {
  /** Serialization ID. */
  private static final long serialVersionUID = 2519825375471874431L;

  /**
   * Instantiates a new browse experiments fs dialog.
   * 
   * @param owner
   *          the owner
   * @param factories
   *          The factories to browse for.
   */
  public BrowseExperimentsFSDialog(Window owner,
      List<? extends IFileHandling> factories) {
    super(owner, "Browse for Experiments", factories);
  }

  @Override
  protected ModelExperimentData getComponent(File f) {
    return new ModelExperimentData(f);
  }
}