/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.jamesii.core.util.misc.Databases;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;

/**
 * A problem feature type, e.g. the size of a model or tree depth etc.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class FeatureType extends NamedDBEntity<FeatureType> implements
    IFeatureType {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8138135619287374562L;

  /** The column index for the name. */
  private static final int COLUMN_INDEX_NAME = 2;

  /** The column index for the description. */
  private static final int COLUMN_INDEX_DESC = 3;

  /** The column index for the feature extractor factory. */
  private static final int COLUMN_INDEX_EXTRACTOR_FACTORY = 4;

  /**
   * Reference to the feature extractor factory that is associated with this
   * feature.
   */
  private Class<? extends FeatureExtractorFactory> featureExtractorFactory;

  public FeatureType(String fName, String fDescription,
      Class<? extends FeatureExtractorFactory> fFactory) {
    setName(fName);
    setDescription(fDescription);
    featureExtractorFactory = fFactory;
  }

  public FeatureType(long id) throws SQLException {
    FeatureType f = this.getEntity(id);
    setName(f.getName());
    setDescription(f.getDescription());
    setID(id);
  }

  protected FeatureType() {
  }

  @Override
  public Class<? extends FeatureExtractorFactory> getFeatureExtractorFactory() {
    return featureExtractorFactory;
  }

  @Override
  public void setFeatureExtractorFactory(
      Class<? extends FeatureExtractorFactory> featureExtractorFactory) {
    this.featureExtractorFactory = featureExtractorFactory;
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "name", "description", "feature_generation" };
  }

  @Override
  protected String[] getColumnDataTypes() {
    return new String[] { "VARCHAR(63)", "TEXT", "VARCHAR(255)" };
  }

  @Override
  protected String[] getColumnValues() {
    return new String[] {
        Databases.toString(getName()),
        Databases.toString(getDescription()),
        Databases
            .toString(featureExtractorFactory != null ? featureExtractorFactory
                .getCanonicalName() : "") };
  }

  @Override
  protected FeatureType getCopy() {
    return new FeatureType(getName(), getDescription(),
        getFeatureExtractorFactory());
  }

  @Override
  @SuppressWarnings("unchecked")
  protected FeatureType getEntityByResultSet(ResultSet rs) {
    try {
      return new FeatureType(rs.getString(COLUMN_INDEX_NAME),
          rs.getString(COLUMN_INDEX_DESC),
          (Class<? extends FeatureExtractorFactory>) Class.forName(rs
              .getString(COLUMN_INDEX_EXTRACTOR_FACTORY)));
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  protected String getTableName() {
    return "feature_types";
  }

  /**
   * Looks up a feature type by its feature extractor factory.
   * 
   * @param factoryClass
   *          the class of the feature extractor factory
   * @return the feature, if exists (otherwise null)
   * @throws SQLException
   */
  public FeatureType lookUp(
      Class<? extends FeatureExtractorFactory> factoryClass)
      throws SQLException {
    return getUniqueEntity(getEntities("feature_generation='"
        + factoryClass.getCanonicalName() + "'"));
  }
}
