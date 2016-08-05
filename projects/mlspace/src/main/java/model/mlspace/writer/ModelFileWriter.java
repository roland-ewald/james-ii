/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.IModelWriter;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.util.misc.Files;

/**
 * @author Arne Bittig
 * @param <M>
 *          Model type
 * @date 19.10.2012
 */
public class ModelFileWriter<M extends IModel> implements IModelWriter {

  private final ModularModelWriter<M> modularModelWriter;

  /**
   * @param modularModelWriter
   *          Container of writer modules, doing the actual work
   */
  public ModelFileWriter(ModularModelWriter<M> modularModelWriter) {
    this.modularModelWriter = modularModelWriter;
  }

  /**
   * Workaround for {@link IModelWriter} and especially
   * {@link #write(IModel, URI)} not being generic, but implementations usually
   * being suitable only for specific subclasses of {@link IModel}.
   * 
   * @param modelToWrite
   *          Model as {@link IModel}
   * @return Same model cast to expected type
   * @throws ClassCastException
   *           if modelToWrite is not of correct type
   */
  @SuppressWarnings("unchecked")
  private M castOrThrow(IModel modelToWrite) {
    return (M) modelToWrite;
  }

  @Override
  public void write(IModel modelToWrite, URI target) {
    M model = castOrThrow(modelToWrite);
    Writer writer = null;
    try {
      final FileOutputStream fs =
          new FileOutputStream(Files.getFileFromURI(target), false);
      writer = new OutputStreamWriter(fs, "UTF-8");

      modularModelWriter.writeModelTo(model, writer);
    } catch (final IOException e) {
      SimSystem.report(e);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          SimSystem.report(e);
        }
      }
    }
  }

  @Override
  public void write(ISymbolicModel<?> model, URI ident) {
    throw new UnsupportedOperationException("No symbolic model support yet");
  }

}
