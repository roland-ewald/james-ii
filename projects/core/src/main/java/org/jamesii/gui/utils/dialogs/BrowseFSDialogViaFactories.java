/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.awt.Window;
import java.util.HashMap;
import java.util.List;

import org.jamesii.core.data.IFileHandling;

/**
 * Class for dialogs that scan the file system for eligible files via a list of
 * factories.
 * 
 * @author Valerius Weigandt
 */
public abstract class BrowseFSDialogViaFactories extends
    BrowseFSDialogViaFileEndings {

  /** The serialization */
  private static final long serialVersionUID = 4488900663652859793L;

  /** The factories to browse for. */
  private List<? extends IFileHandling> factories;

  /**
   * Instantiates a new browse file search dialog.
   * 
   * @param owner
   *          The owner
   * @param title
   *          The title
   * @param factories
   *          The factories to browse for. Can't be {@code null}.
   */
  public BrowseFSDialogViaFactories(Window owner, String title,
      List<? extends IFileHandling> factories) {
    super(owner, title, null);

    this.factories = factories;
    HashMap<String, String> fileEndings = new HashMap<>();

    if (factories != null) {
      for (IFileHandling factory : factories) {
        fileEndings.put(factory.getFileEnding(), factory.getDescription());
      }
      setConfigComponent(fileEndings);
    } else {
      throw new NullPointerException("Factories can't be null");
    }

  }
}
