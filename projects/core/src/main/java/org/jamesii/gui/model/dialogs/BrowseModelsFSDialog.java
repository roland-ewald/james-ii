/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import java.awt.Window;
import java.util.List;

import org.jamesii.core.data.IFileHandling;
import org.jamesii.gui.utils.dialogs.BrowseFSDialogViaFactories;

/**
 * Dialog to browse for Models.
 * 
 * @author Valerius Weigandt
 */
public class BrowseModelsFSDialog extends BrowseFSDialogViaFactories {

  /** The serialization */
  private static final long serialVersionUID = 3343971570945213564L;

  /**
   * Instantiates a new browse models fs dialog.
   * 
   * @param owner
   *          The owner
   * @param factories
   *          The factories to browse for.
   */
  public BrowseModelsFSDialog(Window owner,
      List<? extends IFileHandling> factories) {
    super(owner, "Browse for Models", factories);
  }
}
