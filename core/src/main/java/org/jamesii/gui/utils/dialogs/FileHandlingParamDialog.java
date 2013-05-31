/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.awt.Window;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IFileHandling;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.FileChooser;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;

/**
 * Class to support input of file reader writer parameters via GUI.
 * 
 * @author Stefan Rybacki
 * @param <X>
 */
public class FileHandlingParamDialog<X extends Factory & IFileHandling>
    implements IFactoryParameterDialog<X> {

  /**
   * Choose a file.
   */
  private final JFileChooser fileChooser;

  /**
   * The filter factory mapping.
   */
  private final Map<FileFilter, X> filterFactoryMapping = new HashMap<>();

  /**
   * The enforce whether the file ending of the specified factory should be
   * enforced, this is only usefully for saving dialogs obviously
   */
  private boolean enforceFileEnding = false;

  /**
   * Default constructor.
   * 
   * @param parameter
   *          parameter for factory parameter dialog
   * @param historyID
   *          the id the last used directory should be saved to
   * @param dialogType
   *          either {@link JFileChooser#OPEN_DIALOG} or
   *          {@link JFileChooser#SAVE_DIALOG}
   * @param enforceEnding
   *          if true file endings are enforced according to the selected file
   *          type, this is obviously only useful for save dialogs
   */
  public FileHandlingParamDialog(ParameterBlock parameter, String historyID,
      int dialogType, boolean enforceEnding) {
    this.enforceFileEnding = enforceEnding;
    fileChooser = new FileChooser(historyID);
    fileChooser.setDialogType(dialogType);
    Collection<X> factories =
        parameter
            .getSubBlockValue(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES);

    if (factories == null) {
      throw new NullPointerException("Concrete factories are null");
    }

    FileFilter defaultFilter = null;

    // in case there are more than one file handling factories show a
    // filter that allows to select any of the file endings
    if (factories.size() > 1) {
      String[] endings = new String[factories.size()];
      int i = 0;
      for (IFileHandling f : factories) {
        endings[i] = f.getFileEnding();
        i++;
      }

      FileFilter filter =
          new FileNameExtensionFilter("-- All supported Files --", endings);

      fileChooser.addChoosableFileFilter(filter);
      defaultFilter = filter;
    }

    // now create a file chooser that holds a file filter for each
    // factory
    for (X f : factories) {
      FileNameExtensionFilter filter =
          new FileNameExtensionFilter(
              f.getDescription() != null ? f.getDescription()
                  : f.getFileEnding(), f.getFileEnding());

      fileChooser.addChoosableFileFilter(filter);
      filterFactoryMapping.put(filter, f);

      // Set filter for first factory in list as default
      if (defaultFilter == null) {
        defaultFilter = filter;
      }
    }

    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(false);

    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileFilter(defaultFilter);
  }

  /**
   * Gets the factory parameter.
   * 
   * @param parentWindow
   *          the parent window
   * @return the factory parameter
   */
  @Override
  public Pair<ParameterBlock, X> getFactoryParameter(Window parentWindow) {

    URI chosenURI = null;

    // Someone might has changed the L&F
    SwingUtilities.updateComponentTreeUI(fileChooser);

    if (fileChooser.showDialog(parentWindow, null) != JFileChooser.APPROVE_OPTION
        || fileChooser.getSelectedFile() == null) {
      return null;
    }

    try {
      File selectedFile = fileChooser.getSelectedFile();

      X factory = filterFactoryMapping.get(fileChooser.getFileFilter());

      if (factory == null) {
        // try to find a factory that supports chosenURI
        for (X e : filterFactoryMapping.values()) {
          if (e.supportsURI(Files.getURIFromFile(selectedFile))) {
            factory = e;
            break;
            // FIXME sr137: if there is more than one factory
            // supporting show selection window
          }
        }

        if (factory == null) {
          return null;
        }
      }

      if (enforceFileEnding) {
        selectedFile =
            Files.getFileWithEnding(selectedFile, factory.getFileEnding());
      }

      chosenURI = Files.getURIFromFile(selectedFile);

      return new Pair<>(new ParameterBlock(chosenURI, IURIHandling.URI),
          factory);
    } catch (URISyntaxException e) {
      SimSystem.report(e);
    }

    return null;
  }

  /**
   * Gets the menu description.
   * 
   * @return the menu description
   */
  @Override
  public String getMenuDescription() {
    return "File...";
  }

}
