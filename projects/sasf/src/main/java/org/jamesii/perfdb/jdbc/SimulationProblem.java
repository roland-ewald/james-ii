/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.database.SimpleDataBaseEntity;
import org.jamesii.core.util.misc.Databases;
import org.jamesii.core.util.misc.Generics;
import org.jamesii.perfdb.ConstraintException;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemScheme;

/**
 * This class represents a simulation problem in the performance database.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class SimulationProblem extends SimpleDataBaseEntity<SimulationProblem>
    implements IProblemDefinition {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -6173060844830089898L;

  /**
   * Benchmark model to be simulated.
   */
  private IProblemScheme benchmarkModel = null;

  /**
   * Model parameters.
   */
  private Map<String, Serializable> parameters = null;

  protected SimulationProblem() {

  }

  public SimulationProblem(long id) throws SQLException {
    SimulationProblem simProb = this.getEntity(id);
    benchmarkModel = simProb.getProblemScheme();
    parameters = simProb.getParameters();
    setID(id);
  }

  /**
   * Default constructor.
   * 
   * @param bmModel
   *          the benchmark model
   * @param params
   *          the parameters for the benchmark models
   */
  public SimulationProblem(IProblemScheme bmModel,
      Map<String, Serializable> params) {
    benchmarkModel = bmModel;
    parameters = params;
  }

  public Map<String, Serializable> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Serializable> parameters) {
    this.parameters = parameters;
  }

  @Override
  public IProblemScheme getProblemScheme() {
    return benchmarkModel;
  }

  @Override
  public void setProblemScheme(IProblemScheme problemScheme) {
    this.benchmarkModel = problemScheme;
  }

  // TODO: Problems table will have to be a bit more complex...

  @Override
  protected String[] getColumnNames() {
    return new String[] { "model_id", "parameters" };
  }

  @Override
  protected String[] getColumnDataTypes() {
    return new String[] { SimpleDataBaseEntity.FOREIGN_KEY_TYPE, "TEXT",
        SimpleDataBaseEntity.FOREIGN_KEY_TYPE, "LONG" };
  }

  @Override
  protected String[] getColumnValues() throws SQLException {
    try {
      return new String[] {
          ((Long) benchmarkModel.getID()).toString(),
          Databases.toString(SerialisationUtils
              .serializeToB64String((Serializable) parameters)) };
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected SimulationProblem getCopy() {
    return new SimulationProblem(getProblemScheme(), getParameters());
  }

  @Override
  @SuppressWarnings("unchecked")
  // De-serialisation
  protected SimulationProblem getEntityByResultSet(ResultSet resultSet) {
    try {
      return new SimulationProblem(new BenchmarkModel(resultSet.getLong(2)),
          (HashMap<String, Serializable>) SerialisationUtils
              .deserializeFromB64String(resultSet.getString(3)));
    } catch (SQLException | IOException | ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  protected String getTableName() {
    return "problems";
  }

  /**
   * Looks up a simulation problem for a given benchmark model and parameters.
   * 
   * @param bModel
   *          the benchmark model
   * @param params
   *          the parameters
   * @return the matching simulation problem, otherwise null
   * @throws SQLException
   *           the SQL exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public SimulationProblem lookUp(IProblemScheme bModel,
      Map<String, Serializable> params) throws SQLException, IOException {
    // TODO: Integrate config information

    if (!(params instanceof Serializable)) {
      throw new IllegalArgumentException();
    }

    List<SimulationProblem> simProblems =
        getEntities("model_id=" + bModel.getID() + " AND parameters='"
            + SerialisationUtils.serializeToB64String((Serializable) params)
            + "'");

    if (simProblems.size() > 1) {
      throw new ConstraintException(
          "There are more than one simulation problems with the same parameters defined for model with id:"
              + bModel.getID());
    }

    return simProblems.size() == 0 ? null : simProblems.get(0);
  }

  /**
   * Looks up all simulation problems for a given benchmark model.
   * 
   * @param bModel
   *          the benchmark model
   * @return all simulation problems associated with this model
   * @throws SQLException
   *           if DB look-up goes wrong
   */
  public List<IProblemDefinition> lookUp(IProblemScheme bModel)
      throws SQLException {
    return Generics.changeListType(getEntities("model_id=" + bModel.getID()));
  }

  @Override
  protected String getAdditionalCreationSQL() {
    return ", FOREIGN KEY (model_id) REFERENCES models(id) ON DELETE CASCADE";
  }

  @Override
  public long getSchemeParametersHash() {
    return -1L;
  }

  @Override
  public long getDefinitionParametersHash() {
    return -1L;
  }

  @Override
  public Map<String, Serializable> getSchemeParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setSchemeParameters(Map<String, Serializable> parameters) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Serializable> getDefinitionParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setDefinitionParameters(Map<String, Serializable> parameters) {
    throw new UnsupportedOperationException();
  }

}
