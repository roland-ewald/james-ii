/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.writer;

import java.io.IOException;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.model.IModel;

/**
 * "Writer" combining the work of one or more {@link IModelWriterModule}s to
 * {@link #writeModelTo(IModel, Appendable)}, e.g. for
 * {@link model.mlspace.writer.ModelFileWriter}.
 *
 * @author Arne Bittig
 * @param <M>
 *          Model type
 * @date 19.10.2012
 */
public class ModularModelWriter<M extends IModel> {

  private final Iterable<IModelWriterModule<M>> componentWriters;

  private final CharSequence componentSeparator;

  private final CharSequence componentNamePrefix;

  private final CharSequence componentNameSuffix;

  /**
   * Full constructor.
   * 
   * @param componentWriters
   *          Model writer modules
   * @param componentSeparator
   *          Seperator put between components/modules (e.g. new line)
   * @param componentNamePrefix
   *          When component/module names shall precede the respective block in
   *          the output, they may be prefixed with this (e.g. a comment start
   *          indicator). Null for no component/module names.
   * @param componentNameSuffix
   *          When component/module names shall precede the respective block in
   *          the output, they may be ended with this (e.g. a comment end
   *          indicator).
   */
  public ModularModelWriter(Iterable<IModelWriterModule<M>> componentWriters,
      CharSequence componentSeparator, CharSequence componentNamePrefix,
      CharSequence componentNameSuffix) {
    this.componentWriters = componentWriters;
    this.componentSeparator =
        componentSeparator != null ? componentSeparator : "";
    this.componentNamePrefix = componentNamePrefix;
    this.componentNameSuffix =
        componentNameSuffix == null && componentNamePrefix != null ? ""
            : componentNameSuffix;
  }

  /**
   * Minimal constructor writing all components directly one after the other
   * without separators or component names.
   * 
   * @param componentWriters
   *          Model writer modules
   */
  public ModularModelWriter(Iterable<IModelWriterModule<M>> componentWriters) {
    this(componentWriters, "", null, null);
  }

  /**
   * Write model directly to stream/StringBuilder/... . The result shall be the
   * same as if the string representation of the model (see
   * {@link #getModelText(IModel)}) were appended directly to given output
   * object, but the latter method may call this one instead of the other way
   * around).
   * 
   * @param model
   *          Model
   * @param output
   *          Where to write
   * @throws IOException
   *           if appending to output throws one
   */
  public void writeModelTo(M model, Appendable output) throws IOException {
    boolean isFirst = true;
    for (IModelWriterModule<M> cw : componentWriters) {
      if (!isFirst) {
        output.append(componentSeparator);
      } else {
        isFirst = false;
      }
      if (componentNamePrefix != null) {
        output.append(componentNamePrefix);
        output.append(cw.getComponentName());
        output.append(componentNameSuffix);
        output.append(componentSeparator);
      }
      cw.writeModelComponent(model, output);
    }
  }

  /**
   * Convenience method to get model as String.
   * 
   * @param model
   *          Model
   * @return String representation of model
   */
  public String getModelText(M model) {
    StringBuilder sb = new StringBuilder();
    try {
      writeModelTo(model, sb);
    } catch (IOException e) {
      SimSystem.report(Level.SEVERE,
          "#append(CharSequence) in StringBuilder cannot throw", e);
      return null;
    }
    return sb.toString();
  }

}
