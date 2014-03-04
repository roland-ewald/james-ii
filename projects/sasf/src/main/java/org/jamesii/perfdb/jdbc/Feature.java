/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.database.SimpleDataBaseEntity;
import org.jamesii.core.util.misc.Databases;
import org.jamesii.core.util.misc.Generics;
import org.jamesii.perfdb.DatabaseAccessException;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.entities.IProblemDefinition;

/**
 * Class that represents a feature value.
 * 
 * @author Roland Ewald
 * 
 * @param <V>
 *          the value of the feature, equals the return type of
 *          {@link org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory#getFeatureClass()}
 *          .
 */
@Deprecated
public class Feature extends SimpleDataBaseEntity<Feature> implements IFeature {

  /** Serialisation ID. */
  private static final long serialVersionUID = -9170889919353686865L;

  /** The column index for the feature type id. */
  private static final int COLUMN_INDEX_FEAT_TYPE_ID = 3;

  /** The column index for the simulation problem id. */
  private static final int COLUMN_INDEX_SIM_PROB_ID = 2;

  /** The column index for the value. */
  private static final int COLUMN_INDEX_VALUE = 4;

  /** Reference to the feature that was extracted. */
  private IFeatureType feature = null;

  /** Reference to the simulation problem for which the feature was extracted. */
  private IProblemDefinition problemDefinition = null;

  /** The extracted value. */
  private Map<String, Serializable> value = null;

  public Feature(IProblemDefinition simProb, IFeatureType feat,
      Map<String, Serializable> val) {
    feature = feat;
    problemDefinition = simProb;
    value = val;
  }

  public Feature(Long id) throws SQLException {
    Feature fv = this.getEntity(id);
    problemDefinition =
        fv.getApplication().getProblemInstance().getProblemDefinition();
    value = fv.getValue();
    setID(id);
  }

  protected Feature() {

  }

  @Override
  public IFeatureType getFeatureType() {
    return feature;
  }

  @Override
  public void setFeatureType(IFeatureType feature) {
    this.feature = feature;
  }

  @Override
  public Map<String, Serializable> getValue() {
    return value;
  }

  @Override
  public void setValue(Map<String, Serializable> value) {
    this.value = value;
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "problem_id", "feature_type_id", "value" };
  }

  @Override
  protected String[] getColumnDataTypes() {
    return new String[] { SimpleDataBaseEntity.FOREIGN_KEY_TYPE,
        SimpleDataBaseEntity.FOREIGN_KEY_TYPE, "TEXT" };
  }

  @Override
  protected String[] getColumnValues() {
    try {
      return new String[] {
          Databases.toString(problemDefinition.getID()),
          Databases.toString(feature.getID()),
          Databases.toString(SerialisationUtils
              .serializeToB64String((Serializable) value)) };
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected Feature getCopy() {
    return new Feature(problemDefinition, feature, value);
  }

  @SuppressWarnings("unchecked")
  // Cast to V is ensured by serialization functions in core
  @Override
  protected Feature getEntityByResultSet(ResultSet resultSet) {
    try {
      return new Feature(
          new SimulationProblem(resultSet.getLong(COLUMN_INDEX_SIM_PROB_ID)),
          new FeatureType(resultSet.getLong(COLUMN_INDEX_FEAT_TYPE_ID)),
          (Map<String, Serializable>) SerialisationUtils
              .deserializeFromB64String(resultSet.getString(COLUMN_INDEX_VALUE)));
    } catch (SQLException | IOException | ClassNotFoundException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  protected String getTableName() {
    return "features";
  }

  @Override
  protected String getAdditionalCreationSQL() {
    return ", FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE"
        + ", FOREIGN KEY (feature_type_id) REFERENCES feature_types(id) ON DELETE CASCADE";
  }

  /**
   * Looks up a feature value, given a simulation problem and a feature.
   * 
   * @param problem
   *          the simulation problem
   * @param feat
   *          the feature
   * @return the feature value, if exists (otherwise null)
   * @throws SQLException
   *           if look up goes wrong
   */
  public Feature lookUp(IProblemDefinition problem, IFeatureType feat)
      throws SQLException {
    return getUniqueEntity(getEntities("problem_id=" + problem.getID()
        + " AND feature_type_id=" + feat.getID() + ""));
  }

  public List<IFeature> lookUp(IProblemDefinition problem) throws SQLException {
    return Generics
        .changeListType(getEntities("problem_id=" + problem.getID()));
  }

  @Override
  public IApplication getApplication() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setApplication(IApplication app) {
    throw new UnsupportedOperationException();
  }

}
