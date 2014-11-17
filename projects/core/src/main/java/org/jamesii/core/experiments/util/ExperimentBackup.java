/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.IExperimentWriter;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.experiment.write.plugintype.AbstractExperimentWriterFactory;
import org.jamesii.core.data.experiment.write.plugintype.ExperimentWriterFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.BasicUtilities;

/**
 * The experiment backup class is used from the BaseExperiment and encapsulates
 * functionality to backup the experiment. The backup of the experiment is
 * written to an XML file - so it can be restored if a computer crashes while
 * the experiment is running.
 * 
 * @author Jan Himmelspach
 * 
 */
public class ExperimentBackup implements Serializable {

  /**
   * the constant serial version ID.
   */
  private static final long serialVersionUID = 4299740415299547092L;

  /**
   * The experiment to backup.
   */
  private final BaseExperiment experiment;

  /** Flag to determine whether experiment backups are enabled. */
  private boolean enabled = true;

  /**
   * Create a new instance of the backup helper class.
   * 
   * @param experiment
   */
  public ExperimentBackup(BaseExperiment experiment) {
    super();
    this.experiment = experiment;
  }

  /**
   * Gets the backup enabled - flag.
   * 
   * @return whether experiment backups (as XML) are enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Set the enabled flag - only if enabled the backup will be written to a
   * file.
   * 
   * @param enabled
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Uses java.beans to write the BaseExperiment to an XML. This is used for
   * backup.
   * 
   * @param thePath
   *          the path
   */
  public void writeExperimentToFile(String thePath) {

    String path = thePath.endsWith(".exp") ? thePath : thePath + ".exp";

    File file = new File(path);
    if (!file.exists() || file.canWrite()) {
      // get an ExperimentWriter from a factory
      ParameterBlock parameter =
          new ParameterBlock(new ExperimentInfo(file.toURI(), null),
              AbstractExperimentReaderFactory.EXPERIMENT_INFO);

      ExperimentWriterFactory erwf =
          SimSystem.getRegistry().getFactory(
              AbstractExperimentWriterFactory.class, parameter);
      if (erwf == null) {
        SimSystem.report(Level.SEVERE, "Can't get ExperimentWriterFactory!");
        return;
      }

      IExperimentWriter writer = erwf.create(parameter, SimSystem.getRegistry().createContext());

      // write BaseExperiment to the file
      try {
        writer.writeExperiment(parameter, experiment);
        SimSystem.report(Level.CONFIG, "Experiment backup saved to: "
            + BasicUtilities.getExpLocation(parameter));
      } catch (IOException e) {
        SimSystem.report(Level.WARNING,
            "Can't write backup of BaseExperiment:", e);
      }
    }
  }

}
