/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Iterator;

import model.mlspace.IMLSpaceModel;
import model.mlspace.writer.ModelFileWriter;

import org.jamesii.SimSystem;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.util.misc.Files;

import simulator.mlspace.AbstractMLSpaceProcessor;

/**
 * Observer writing model in its current state to file (unfinished, larger parts
 * cannot be read back, not adapted to non-trivial attribute modification rules,
 * ...)
 *
 * @author Arne Bittig
 * @date 19.10.2012
 */
public class ModelWritingObserver implements
    IObserver<AbstractMLSpaceProcessor<?, ?>> {

  private Double nextOutputTime;

  private final Iterator<Double> nextOutputTimeItr;

  private final ModelFileWriter<IMLSpaceModel> modelFileWriter;

  private final String filename;

  /**
   * @param outputTimes
   * @param modelFileWriter
   * @param filename
   */
  public ModelWritingObserver(Iterable<Double> outputTimes,
      ModelFileWriter<IMLSpaceModel> modelFileWriter, String filename) {
    if (outputTimes == null) {
      this.nextOutputTimeItr = null;
      this.nextOutputTime = Double.POSITIVE_INFINITY;
    } else {
      this.nextOutputTimeItr = outputTimes.iterator();

      if (!this.nextOutputTimeItr.hasNext()) {
        this.nextOutputTime = Double.POSITIVE_INFINITY;
      } else {
        this.nextOutputTime = this.nextOutputTimeItr.next();
      }
    }
    this.modelFileWriter = modelFileWriter;
    this.filename = filename;
  }

  @Override
  public void update(AbstractMLSpaceProcessor<?, ?> proc) {
    if (proc.getTime() >= nextOutputTime) {
      writeModel(proc);

    }
  }

  private void writeModel(AbstractMLSpaceProcessor<?, ?> proc) {
    double time = proc.getTime();
    try {
      modelFileWriter.write((IModel)proc.getModel(),
          Files.getURIFromFile(new File(getFilename(time))));
    } catch (URISyntaxException e) {
      SimSystem.report(e);
    }
    while (nextOutputTime <= time) {
      nextOutputTime =
          nextOutputTimeItr.hasNext() ? nextOutputTimeItr.next()
              : Double.POSITIVE_INFINITY;
    }
  }

  private String getFilename(Double time) {
    int lastDot = filename.lastIndexOf('.');
    int length = filename.length();
    if (lastDot != length - 4) {
      return filename + '-' + String.format("%.4f", time) + ".mls";
    } else {
      return filename.substring(0, length - 4) + '-'
          + String.format("%.4f", time) + filename.substring(length - 3);
    }
  }

  @Override
  public void update(AbstractMLSpaceProcessor<?, ?> entity, Object hint) {
    if (hint instanceof Boolean && (Boolean) hint) {
      writeModel(entity);
    } else {
      update(entity);
    }
  }

}
