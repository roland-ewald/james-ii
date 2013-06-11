/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jamesii.SimSystem;
import org.jamesii.core.util.WaitingThread;

/**
 * Thread that checks (and recreates) the connection of a data base after a
 * given interval in a loop.
 * 
 * @author Stefan Leye
 * 
 */
public class KeepConnectionAliveThread extends WaitingThread {

  /**
   * The database.
   */
  private SimpleDataBase db;

  /**
   * The statement which should be executed to wake up the connection.
   */
  private String statement;

  /**
   * Default constructor.
   * 
   * @param time
   *          the waiting time
   * 
   * @param db
   *          the data base
   */
  public KeepConnectionAliveThread(long time, SimpleDataBase db,
      String statement) {
    super(time);
    this.db = db;
    this.statement = statement;
  }

  @Override
  protected void execute() {
    synchronized (this) {

      Connection connection;
      try {
        connection = db.getConnection();
        try {
          try (Statement s = connection.createStatement()) {
            s.execute(statement);
          }
        } catch (SQLException e) {
          SimSystem.report(e);
          db.openBase();
        }
      } catch (ClassNotFoundException | SQLException e1) {
        SimSystem.report(e1);
      }

    }
  }

}
