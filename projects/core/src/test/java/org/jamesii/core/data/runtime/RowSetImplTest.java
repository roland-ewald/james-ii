package org.jamesii.core.data.runtime;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.RowSet;

import org.jamesii.core.data.runtime.RowSetImpl;

import junit.framework.TestCase;

/**
 * Tests the {@link RowSetImpl} class.
 * <p>
 * TODO: This is currently merely a reminder that the class does some things
 * quite wrong, breaking the interface it implements. As long as this unit test
 * fails, the problem is unresolved.
 * 
 * @author Johannes Rössel
 */
public class RowSetImplTest extends TestCase {

  private RowSet rs;

  @Override
  protected void setUp() throws Exception {
    rs = new RowSetImpl();
  }

  /** Tests the {@link RowSetImpl#setConcurrency(int)} method. */
  public void testSetConcurrency() {
    // currently RowSetImpl uses its own values for those constants and won't
    // recognise the ones defined in ResultSet.

    boolean t = false;
    try {
      rs.setConcurrency(ResultSet.CONCUR_READ_ONLY);
    } catch (SQLException e) {
      t = true;
    }
    assertTrue("Mode not supported, exception expected", t);
    t = false;
    try {
      rs.setConcurrency(ResultSet.CONCUR_UPDATABLE);
    } catch (SQLException e) {
      t = true;
    }
    assertTrue("Mode not supported, exception expected", t);
  }

  /** Tests the {@link RowSetImpl#setType(int)} method. */
  public void testSetType() {
    // TODO: currently RowSetImpl uses its own values for those constants and
    // won't
    // recognise the ones defined in ResultSet.
    boolean t = false;
    try {
      rs.setType(ResultSet.TYPE_FORWARD_ONLY);
    } catch (SQLException e) {
      t = true;
    }
    assertTrue("Mode not supported, exception expected", t);
    t = false;
    try {
      rs.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
    } catch (SQLException e) {
      t = true;
    }
    assertTrue("Mode not supported, exception expected", t);
    t = false;
    try {
      rs.setType(ResultSet.TYPE_SCROLL_SENSITIVE);
    } catch (SQLException e) {
      t = true;
    }
    assertTrue("Mode not supported, exception expected", t);
  }

}
