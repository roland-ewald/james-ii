/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import java.io.IOException;

import org.jamesii.core.model.IModel;

/**
 * Interface for classes that extract part (or all) of a model and create write
 * it to a given output, e.g. for a model writer.
 *
 * @author Arne Bittig
 * @param <M>
 *          Model type
 * @date 19.10.2012
 */
public interface IModelWriterModule<M extends IModel> {

  /**
   * Write a string representation of a part (or all of) of the model to given
   * output
   * 
   * @param model
   *          Model
   * @param output
   *          Stream, StringBuilder or similar to append model component string
   * @throws IOException
   *           if {@link Appendable#append(CharSequence)} of output throws
   */
  void writeModelComponent(M model, Appendable output) throws IOException;

  // /**
  // * Get a string representation of a part (or all of) of the model. The
  // * result should always be equal to the string contained in a previously
  // * empty {@link java.lang.StringBuilder StringBuilder} right after
  // * {@link #modelComponentToString(IModel, Appendable)} was applied to it.
  // *
  // * @param model
  // * Model
  // * @return String representation of model component
  // */
  // String modelComponentToString(M model);

  /**
   * @return Name of the model component covered (usually static, i.e. equal for
   *         instances of the same class; may be empty)
   */
  String getComponentName();
}
