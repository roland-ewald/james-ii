/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.dialogs;

import java.awt.Window;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.parameter.read.plugintype.ModelParameterFileReaderFactory;
import org.jamesii.core.data.model.parameter.read.plugintype.ModelParameterReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.BrowseFSDialogViaFactories;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogEntry;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogListener;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;

/**
 * Browse Dialog that browses for model parameter files.
 * 
 * @author Stefan Rybacki
 */
public class BrowseForModelParametersFromFileSystem implements
    IFactoryParameterDialog<ModelParameterFileReaderFactory> {

  /**
   * The chosen uri.
   */
  private URI chosenURI = null;

  /**
   * The factories handled in this dialog.
   */
  private final List<ModelParameterFileReaderFactory> factories =
      new ArrayList<>();

  /**
   * Instantiates a new browse for models from file system dialog.
   * 
   * @param factoryDialogParameter
   *          the factory dialog parameter
   */
  public BrowseForModelParametersFromFileSystem(
      ParameterBlock factoryDialogParameter) {
    List<ModelParameterFileReaderFactory> facs =
        factoryDialogParameter
            .getSubBlockValue(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES);

    if (facs == null) {
      throw new NullPointerException("Concrete factories are null");
    }

    factories.addAll(facs);

    // TODO sr137: in browse dialog show additional panel with
    // checkboxes for each factory
  }

  @Override
  public Pair<ParameterBlock, ModelParameterFileReaderFactory> getFactoryParameter(
      Window parentWindow) {
    chosenURI = null;

    final BrowseFSDialogViaFactories dialog =
        new BrowseFSDialogViaFactories(parentWindow,
            "Search for Model Parameter files", factories) {

          /**
           * Serialization ID
           */
          private static final long serialVersionUID = 4373755720333003469L;

          @Override
          protected boolean checkFile(File f) {
            try {
              for (ModelParameterReaderFactory fac : factories) {
                if (fac.supportsURI(Files.getURIFromFile(f))) {
                  System.out.println(f);
                  return true;
                }
              }
            } catch (Exception e) {
              SimSystem.report(e);
            }
            return false;
          }

          @Override
          protected ModelParameterTableData getComponent(File f) {
            System.out.println(f);
            return new ModelParameterTableData(f);
          }

        };
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

    List<ModelParameterFileReaderFactory> list = new ArrayList<>();

    // find factories that support the given uri
    for (ModelParameterFileReaderFactory f : factories) {
      if (f.supportsURI(chosenURI)) {
        list.add(f);
      }
    }

    if (list.isEmpty()) {
      return null;
    }

    ModelParameterFileReaderFactory factory = list.get(0);

    if (list.size() > 1) {
      // show choose to select the actual factory
      FactorySelectionDialog<ModelParameterFileReaderFactory> cdialog =
          new FactorySelectionDialog<>(null, list, null, "Select Format", true);
      if (cdialog.isOkButtonPressed()) {
        List<ModelParameterFileReaderFactory> selectedFactories =
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
