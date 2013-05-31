package org.jamesii.gui.experiment.dialogs;

import java.awt.Window;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IFileHandling;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.experiment.read.plugintype.ExperimentFileReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogEntry;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogListener;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;

/**
 * Dialog that enables browsing for experiment files.
 * 
 * @author Stefan Rybacki
 */
public class BrowseExperimentsFSFactoryDialog implements
    IFactoryParameterDialog<ExperimentFileReaderFactory> {

  /**
   * The chosen uri.
   */
  private URI chosenURI;

  /**
   * The factories.
   */
  private List<? extends IFileHandling> factories = new ArrayList<>();

  /**
   * Instantiates a new browse experiments fs factory dialog.
   * 
   * @param parameters
   *          the parameters
   */
  public BrowseExperimentsFSFactoryDialog(ParameterBlock parameters) {
    factories =
        parameters
            .getSubBlockValue(AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES);
  }

  @Override
  public Pair<ParameterBlock, ExperimentFileReaderFactory> getFactoryParameter(
      Window parentWindow) {
    final BrowseExperimentsFSDialog dialog =
        new BrowseExperimentsFSDialog(parentWindow, factories);
    dialog.addBrowseFSDialogListener(new IBrowseFSDialogListener() {

      @Override
      public void elementChosen(IBrowseFSDialogEntry element) {
        dialog.setVisible(false);
        try {
          chosenURI = Files.getURIFromFile(element.getFile());
        } catch (URISyntaxException e1) {
          SimSystem.report(e1);
        }
      }
    });

    dialog.setVisible(true);

    if (chosenURI == null) {
      return null;
    }

    List<ExperimentFileReaderFactory> list = new ArrayList<>();

    // find factories that support the given uri
    for (IFileHandling f : factories) {
      if (f.supportsURI(chosenURI)) {
        list.add((ExperimentFileReaderFactory) f);
      }
    }

    if (list.isEmpty()) {
      return null;
    }

    ExperimentFileReaderFactory factory = list.get(0);

    if (list.size() > 1) {
      // show choose to select the actual factory
      FactorySelectionDialog<ExperimentFileReaderFactory> cdialog =
          new FactorySelectionDialog<>(null, list, null, "Select Format", true);
      if (cdialog.isOkButtonPressed()) {
        List<ExperimentFileReaderFactory> selectedFactories =
            cdialog.getSelectedFactories();
        if (selectedFactories != null && !selectedFactories.isEmpty()) {
          factory = selectedFactories.get(0);
        }
      }
    }

    ExperimentInfo info = new ExperimentInfo(chosenURI, null);
    return new Pair<>(new ParameterBlock(info,
        AbstractExperimentReaderFactory.EXPERIMENT_INFO), factory);

  }

  @Override
  public String getMenuDescription() {
    return "Search Files...";
  }

}
