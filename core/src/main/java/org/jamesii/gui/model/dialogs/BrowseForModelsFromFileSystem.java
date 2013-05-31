/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import java.awt.Window;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.ModelFileReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogEntry;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogListener;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;

/**
 * @author Stefan Rybacki
 */
public class BrowseForModelsFromFileSystem implements
    IFactoryParameterDialog<ModelFileReaderFactory> {

  /**
   * The chosen uri.
   */
  private URI chosenURI = null;

  /**
   * The factories.
   */
  private final List<ModelFileReaderFactory> factories = new ArrayList<>();

  /**
   * Instantiates a new browse for models from file system dialog.
   * 
   * @param factoryDialogParameter
   *          the factory dialog parameter
   */
  public BrowseForModelsFromFileSystem(ParameterBlock factoryDialogParameter) {
    List<ModelFileReaderFactory> facs =
        factoryDialogParameter
            .getSubBlockValue(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES);

    if (facs == null) {
      throw new NullPointerException("Concrete factories are null");
    }

    factories.addAll(facs);
  }

  @Override
  public Pair<ParameterBlock, ModelFileReaderFactory> getFactoryParameter(
      Window parentWindow) {
    chosenURI = null;

    final BrowseModelsFSDialog dialog =
        new BrowseModelsFSDialog(parentWindow, factories);
    dialog.addBrowseFSDialogListener(new IBrowseFSDialogListener() {

      @Override
      public void elementChosen(IBrowseFSDialogEntry element) {
        try {
          chosenURI = Files.getURIFromFile(element.getFile());
        } catch (URISyntaxException e1) {
          SimSystem.report(e1);
        }
        dialog.setVisible(false);
      }

    });

    dialog.setVisible(true);

    if (chosenURI == null) {
      return null;
    }

    List<ModelFileReaderFactory> list = new ArrayList<>();

    // find factories that support the given uri
    for (ModelFileReaderFactory f : factories) {
      if (f.supportsURI(chosenURI)) {
        list.add(f);
      }
    }

    if (list.isEmpty()) {
      return null;
    }

    ModelFileReaderFactory factory = list.get(0);

    if (list.size() > 1) {
      // show choose to select the actual factory
      FactorySelectionDialog<ModelFileReaderFactory> cdialog =
          new FactorySelectionDialog<>(null, list, null, "Select Format", true);
      if (cdialog.isOkButtonPressed()) {
        List<ModelFileReaderFactory> selectedFactories =
            cdialog.getSelectedFactories();
        if (selectedFactories != null && !selectedFactories.isEmpty()) {
          factory = selectedFactories.get(0);
        }
      }
    }

    return new Pair<>(new ParameterBlock(chosenURI, IURIHandling.URI), factory);
  }

  @Override
  public String getMenuDescription() {
    return "Search Files...";
  }

}
