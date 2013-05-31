/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic;

import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.model.symbolic.convert.IDocument;

/**
 * The Interface ISymbolicModel. This interface has to be implemented by all
 * symbolic model class in the framework. A symbolic model is usually a model
 * which cannot be directly executed - e.g., it is ok to have "invalid" /
 * "incomplete" models in a symbolic model instance. Thus symbolic models are
 * the representation for a model during the editing process. <br>
 * Symbolic models have to have an internal data structure (parameter <D>). This
 * data structure can be of any type - typically that's where the model is kept
 * in memory. If your executable model classes can hold incomplete models you
 * can simply reuse this data structure here - the framework never directly
 * executes the data structure, so you will not run into problems here. But the
 * latter means that no one ever tries to directly execute symbolic models,
 * which can thus be considered to be an essential design contract here. A
 * symbolic model description does not only contain the information required for
 * the model as such (states, maths, etc...) but in addition comments and meta
 * information which can be edited! This may prevent the usage of the executable
 * classes, however, if there are no comments, meta data, or if the executable
 * classes hold these as well then you can still use them. Although not all
 * modeling languages contain an explicit association of comments to elements
 * this is something nice to have - because it means that these comments can be
 * displayed and edited in graphical editors as well. One way out for these
 * languages is to use the comment before an element (if there is any) and
 * "link" this one to the element. <br>
 * The document operations defined in here allow to retrieve a document based
 * representation of the model, e.g., to be used in a (syntax highlighting)
 * editor or to write the model to a string (e.g., as preparation for saving the
 * model somewhere). This methods may be used by a graphical user interface to
 * synchronise textual and graphical views on the model as well.
 * 
 * @param <D>
 *          the type of the data structure used internally. If the executable
 *          model can be a incomplete model the executable model may be used
 *          here, otherwise any data structure to hold the model definition is
 *          fine as well. It is good practice to define an interface to be used
 *          here - this allows different implementations of the data structure
 *          in the end - without the need to modify any reader or writer.
 */
public interface ISymbolicModel<D> extends INamedEntity {

  /**
   * Gets a text representation of the model. If there is no text representation
   * of the model this method should return null.
   * 
   * @param targetFormat
   *          - the target format to be used
   * 
   * @return a text representation of the model
   */
  IDocument<?> getAsDocument(Class<? extends IDocument<?>> targetFormat);

  /**
   * Sets the model from text. This method typically forces an update of any
   * internal data structure based representation of the model.
   * 
   * @param model
   *          the new textual representation of the model
   * @return true, if setting the model from text was fine
   */
  boolean setFromDocument(IDocument<?> model);

  /**
   * Gets the model as an object instance representing the model. If there is no
   * representation of the model as data structure this method should return
   * null.
   * 
   * @return the model as data structure
   */
  D getAsDataStructure();

  /**
   * Sets model the from data structure. This method typically forces an update
   * of any internal textual representation of the model.
   * 
   * @param model
   *          the new data structure based version of the model
   * 
   * @return true, if setting the model from data was fine
   */
  boolean setFromDataStructure(D model);

  /**
   * Removes the attaches source and mimetype. This is needed for instance for
   * editors only updating the datastructure or the model itself respectively
   * but not the source in the specified mimetype. Call this whenever you update
   * the model but not its source.
   */
  void removeSource();

  /**
   * Sets the source for the model. This is used in combination with the models
   * datastructure. Usually a text based editor creates the datastructure but
   * also has the complete source for the model already available (with
   * comments, formatting, incompleteness which can not yet be put into the
   * datastructure). This editor can then provide this source via this method
   * additionally to the datastructure it provides.
   * 
   * @param src
   *          the models source
   * @param mime
   *          the mime type of the source (basically specifies the format the
   *          source is in so a potential model writer can handle the source
   *          directly if it knows the mime type rather than generating source
   *          from the datastructure)
   */
  void setSource(String src, IMIMEType mime);

  /**
   * Checks if is source available and mimetype are available.
   * 
   * @return true, if is source available
   */
  boolean isSourceAvailable();

  /**
   * Gets the actual source.
   * 
   * @return the source
   */
  String getSource();

  /**
   * Gets the source's mime type.
   * 
   * @return the source's mime type
   */
  IMIMEType getSourceMimeType();

}
