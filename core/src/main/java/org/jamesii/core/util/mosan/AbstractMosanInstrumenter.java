package org.jamesii.core.util.mosan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * Abstract super class for all instrumenters that attach observers to generate
 * Mosa-compatible output. It is thread-safe and manages the correct structure
 * of the output folder etc.
 * 
 * @author Roland Ewald
 * @author Jan Himmelspach
 * 
 */
public abstract class AbstractMosanInstrumenter implements IModelInstrumenter {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 107677316848924151L;

  /** The name of the model file. */
  public static final String MODEL_FILE = "model.csv";

  /** Pattern of the file names to be used. */
  public static final String OUTPUT_FILE_NAME = "Run_.csv";

  /** The name of the sub-directory to store the trajectories in. */
  public static final String RUN_DIR = "exp";

  /** The output directory. */
  private final String outputDir;

  /** List with observers to be added. */
  private List<IObserver<? extends IObservable>> observers = null;

  /**
   * Instantiates a new abstract Mosan instrumenter.
   * 
   * @param outputDirectory
   *          the output directory
   */
  public AbstractMosanInstrumenter(String outputDirectory) {
    outputDir = outputDirectory;
  }

  @Override
  public void instrumentModel(IModel model,
      IComputationTaskConfiguration simConfig) {
    synchronized (AbstractMosanInstrumenter.class) {

      checkForDirectory(outputDir);
      checkForDirectory(outputDir + File.separator + RUN_DIR);

      tryToWriteModelFile(model);
      this.observers = new ArrayList<>();

      try {
        IObserver<? extends IObservable> observer =
            createMosanCompatibleObserver(createUniqueFile(), outputDir, model);
        model.registerObserver(observer);
        this.observers.add(observer);
      } catch (IOException ex) {
        SimSystem.report(Level.SEVERE, "Can't write output trajectories.", ex);
      }
    }
  }

  /**
   * Creates an empty unique file for the Mosan-compatible observer to store the
   * trajectory in.
   * 
   * @return the file name
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private String createUniqueFile() throws IOException {
    String observerFileName =
        org.jamesii.core.util.misc.Files.getUniqueNumberedName(
            org.jamesii.core.util.misc.Files.composeFilename(outputDir
                + File.separator + RUN_DIR, OUTPUT_FILE_NAME), 0);
    (new File(observerFileName)).createNewFile();
    return observerFileName;
  }

  /**
   * Try to write model file. File is not written if
   * {@link AbstractMosanInstrumenter}{@link #createModelRepresentation(IModel)}
   * returns <code>null</code>.
   * 
   * @param model
   *          the model to be written to a file
   */
  private void tryToWriteModelFile(IModel model) {
    if (!new File(org.jamesii.core.util.misc.Files.composeFilename(outputDir,
        MODEL_FILE)).exists()) {
      String modelString = createModelRepresentation(model);
      if (modelString != null) {
        org.jamesii.core.util.misc.Files.writeASCIIFile(
            org.jamesii.core.util.misc.Files.composeFilename(outputDir,
                MODEL_FILE), modelString, "UTF8");
      }
    } else {
      SimSystem.report(Level.INFO, "Model file already exists in directory '"
          + outputDir + "', skipping to write the model.");
    }
  }

  /**
   * Creates a directory.
   * 
   * @param dir
   *          the directory name
   */
  protected void checkForDirectory(String dir) {
    File outDir = new File(dir);
    if (!outDir.exists() && !outDir.mkdir()) {
      throw new RuntimeException("Can't create target directory '" + dir + "'.");
    }
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return observers;
  }

  /**
   * Creates a Mosan-compatible observer, to write its trajectory in the given
   * file. The observer will be attached automatically to the model, so only
   * attach the observer to certain sub-models if this is really necessary.
   * 
   * @param fileName
   *          the name of the target file
   * @param outputDirectory
   *          the output directory
   * @param model
   *          the model to be observed
   * @return the Mosan-compatible observer
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected abstract IObserver createMosanCompatibleObserver(String fileName,
      String outputDirectory, IModel model) throws IOException;

  /**
   * Creates a Mosan-compatible model representation.
   * 
   * @param model
   *          the model to be written to a file
   * @return the string representing the model, will be written to a 'model.csv'
   *         file, or null if no representation can be generated
   */
  protected abstract String createModelRepresentation(IModel model);

}
