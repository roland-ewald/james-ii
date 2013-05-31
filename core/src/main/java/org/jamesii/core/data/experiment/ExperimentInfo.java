/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment;

import java.io.Serializable;
import java.net.URI;

/**
 * Information block. Contains information about one experiment.
 * 
 * @author Mathias Röhl
 */

/*
 * FIXME : check whether this class is needed and follows the current paradigms
 * with passing URIs to a reader for instance
 */
public class ExperimentInfo implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4009249519177704195L;

  /** The datasource's identification. */
  private URI dataBase;

  /** The description of the experiment. */
  private String description;

  /** The experiment's identification. */
  private URI ident;

  /**
   * Constructor for beans compliance.
   */
  public ExperimentInfo() {
  }

  /**
   * Instantiates a new experiment info.
   * 
   * @param id
   *          the id
   * @param pathToDb
   *          the path to db
   */
  public ExperimentInfo(URI id, URI pathToDb) {
    ident = id;
    dataBase = pathToDb;
  }

  /**
   * Gets the data base.
   * 
   * @return the data base
   */
  public URI getDataBase() {
    return dataBase;
  }

  /**
   * Gets the description.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the ident.
   * 
   * @return the ident
   */
  public URI getIdent() {
    return ident;
  }

  /**
   * Sets the data base.
   * 
   * @param db
   *          the new data base
   */
  public void setDataBase(URI db) {
    this.dataBase = db;
  }

  /**
   * Sets the description. Each experiment might have a description explaining
   * what it is good for.
   * 
   * @param description
   *          the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the ident of the experiment. This can be, e.g., the URI where the
   * experiment can be retrieved from.
   * 
   * @param ident
   *          the new ident
   */
  public void setIdent(URI ident) {
    this.ident = ident;
  }

}
