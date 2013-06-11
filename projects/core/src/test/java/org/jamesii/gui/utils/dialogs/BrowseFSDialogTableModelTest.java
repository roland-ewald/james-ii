/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.io.File;

import org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogEntry;

import junit.framework.TestCase;

/**
 * Test case for @link {@link BrowseFSDialogTableModel}.
 * 
 * @author Valerius Weigandt
 */
public class BrowseFSDialogTableModelTest extends TestCase {

  /** The model to test */
  private BrowseFSDialogTableModel model;

  @Override
  protected void setUp() throws Exception {
    model = new BrowseFSDialogTableModel();
  }

  /** Data for the test */
  IBrowseFSDialogEntry data1 = new IBrowseFSDialogEntry() {

    @Override
    public Object getValue(int xPosition) {
      switch (xPosition) {
      case 0: // A
        return 0;
      case 1: // B
        return 1;
      case 2: // C
        return 2;
      default:
        return null;
      }
    }

    @Override
    public String[] getColumnNames() {
      return new String[] { "A", "B", "C" };
    }

    @Override
    public File getFile() {
      return null;
    }
  };

  /** Data for the test */
  IBrowseFSDialogEntry data2 = new IBrowseFSDialogEntry() {

    @Override
    public Object getValue(int xPosition) {
      switch (xPosition) {
      case 0: // D
        return 0;
      case 1: // E
        return 1;
      default:
        return null;
      }
    }

    @Override
    public String[] getColumnNames() {
      return new String[] { "D", "E" };
    }

    @Override
    public File getFile() {
      return null;
    }
  };

  /** Data for the test */
  IBrowseFSDialogEntry data3 = new IBrowseFSDialogEntry() {

    @Override
    public Object getValue(int xPosition) {
      switch (xPosition) {
      case 0: // E
        return 0;
      case 1: // B
        return 1;
      case 2: // A
        return 2;
      default:
        return null;
      }
    }

    @Override
    public String[] getColumnNames() {
      return new String[] { "E", "B", "A" };
    }

    @Override
    public File getFile() {
      return null;
    }
  };

  /** Data for the test */
  IBrowseFSDialogEntry data4 = new IBrowseFSDialogEntry() {

    @Override
    public Object getValue(int xPosition) {
      switch (xPosition) {
      case 0:
        return 0;
      case 1:
        return 1;
      case 2:
        return 2;
      default:
        return null;
      }
    }

    @Override
    public String[] getColumnNames() {
      return new String[] { "D", "B", "F" };
    }

    @Override
    public File getFile() {
      return null;
    }
  };

  /** Main test method */
  public final void testBrowseFSDialogTableModel() {
    // table clean?
    assertEquals(0, model.getColumnCount());
    assertEquals(0, model.getRowCount());

    IBrowseFSDialogEntry data;
    // adds row for tests
    model.addRow(data = new IBrowseFSDialogEntry() {

      @Override
      public Object getValue(int xPosition) {
        switch (xPosition) {
        case 0:
          return 0;
        case 1:
          return 1;
        case 2:
          return 2;
        default:
          return null;
        }
      }

      @Override
      public String[] getColumnNames() {
        return new String[] { "A", "B", "C" };
      }

      @Override
      public File getFile() {
        return null;
      }
    });

    // column and row count test
    assertEquals(3, model.getColumnCount());
    assertEquals(1, model.getRowCount());

    // column names test
    assertEquals("A", model.getColumnName(0));
    assertEquals("B", model.getColumnName(1));
    assertEquals("C", model.getColumnName(2));

    // value test
    assertEquals(0, model.getValueAt(0, 0));
    assertEquals(1, model.getValueAt(0, 1));
    assertEquals(2, model.getValueAt(0, 2));

    // data test
    assertEquals(data, model.getData(0));

    // adds another row with different column names too the previous
    model.addRow(data = new IBrowseFSDialogEntry() {

      @Override
      public Object getValue(int xPosition) {
        switch (xPosition) {
        case 0:
          return 0;
        case 1:
          return 1;
        default:
          return null;
        }
      }

      @Override
      public String[] getColumnNames() {
        return new String[] { "D", "E" };
      }

      @Override
      public File getFile() {
        return null;
      }
    });

    // column and row count test
    assertEquals(5, model.getColumnCount());
    assertEquals(2, model.getRowCount());

    // column names test
    assertEquals("A", model.getColumnName(0));
    assertEquals("B", model.getColumnName(1));
    assertEquals("C", model.getColumnName(2));
    assertEquals("D", model.getColumnName(3));
    assertEquals("E", model.getColumnName(4));

    // value test 1st row
    assertEquals(0, model.getValueAt(0, 0));
    assertEquals(1, model.getValueAt(0, 1));
    assertEquals(2, model.getValueAt(0, 2));
    assertEquals(null, model.getValueAt(0, 4));
    assertEquals(null, model.getValueAt(0, 4));

    // value test 2nd row
    assertEquals(null, model.getValueAt(1, 0));
    assertEquals(null, model.getValueAt(1, 1));
    assertEquals(null, model.getValueAt(1, 2));
    assertEquals(0, model.getValueAt(1, 3));
    assertEquals(1, model.getValueAt(1, 4));

    // data test
    assertEquals(data, model.getData(1));

    // same data again
    model.addRow(data);

    // column and row count test
    assertEquals(5, model.getColumnCount());
    assertEquals(3, model.getRowCount());

    // value test 3nd row
    assertEquals(null, model.getValueAt(2, 0));
    assertEquals(null, model.getValueAt(2, 1));
    assertEquals(null, model.getValueAt(2, 2));
    assertEquals(0, model.getValueAt(2, 3));
    assertEquals(1, model.getValueAt(2, 4));

    // data test
    assertEquals(data, model.getData(2));

    model.addRow(data = new IBrowseFSDialogEntry() {

      @Override
      public Object getValue(int xPosition) {
        switch (xPosition) {
        case 0:
          return 0;
        case 1:
          return 1;
        case 2:
          return 2;
        default:
          return null;
        }
      }

      @Override
      public String[] getColumnNames() {
        return new String[] { "A", "C", "E" };
      }

      @Override
      public File getFile() {
        return null;
      }
    });

    // column and row count test
    assertEquals(5, model.getColumnCount());
    assertEquals(4, model.getRowCount());

    // value test 3nd row
    assertEquals(0, model.getValueAt(3, 0)); // A
    assertEquals(null, model.getValueAt(3, 1)); // B
    assertEquals(1, model.getValueAt(3, 2)); // C
    assertEquals(null, model.getValueAt(3, 3)); // D
    assertEquals(2, model.getValueAt(3, 4)); // E

    assertEquals(data, model.getData(3));

    model.addRow(data = new IBrowseFSDialogEntry() {

      @Override
      public Object getValue(int xPosition) {
        switch (xPosition) {
        case 0:
          return 0;
        case 1:
          return 1;
        case 2:
          return 2;
        default:
          return null;
        }
      }

      @Override
      public String[] getColumnNames() {
        return new String[] { "C", "A", "F" };
      }

      @Override
      public File getFile() {
        return null;
      }
    });

    // column and row count test
    assertEquals(6, model.getColumnCount());
    assertEquals(5, model.getRowCount());

    // value test 3nd row
    assertEquals(1, model.getValueAt(4, 0)); // A
    assertEquals(null, model.getValueAt(4, 1)); // B
    assertEquals(0, model.getValueAt(4, 2)); // C
    assertEquals(null, model.getValueAt(4, 3)); // D
    assertEquals(null, model.getValueAt(4, 4)); // E
    assertEquals(2, model.getValueAt(4, 5)); // F

    assertEquals(data, model.getData(4));

  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel#getColumnCount()}
   * .
   */
  public final void testGetColumnCount() {
    assertEquals(0, model.getColumnCount());
    model.addRow(data1);
    assertEquals(3, model.getColumnCount());
    model.addRow(data2);
    assertEquals(5, model.getColumnCount());
    model.addRow(data3);
    assertEquals(5, model.getColumnCount());
    model.addRow(data4);
    assertEquals(6, model.getColumnCount());
    model.clearTable();
    assertEquals(0, model.getColumnCount());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel#getColumnName(int)}
   * .
   */
  public final void testGetColumnName() {
    model.addRow(data1);
    assertEquals("A", model.getColumnName(0));
    assertEquals("B", model.getColumnName(1));
    assertEquals("C", model.getColumnName(2));
    model.addRow(data2);
    assertEquals("D", model.getColumnName(3));
    assertEquals("E", model.getColumnName(4));
    model.addRow(data4);
    assertEquals("F", model.getColumnName(5));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel#getRowCount()}
   * .
   */
  public final void testGetRowCount() {
    assertEquals(0, model.getRowCount());
    model.addRow(data1);
    assertEquals(1, model.getRowCount());
    model.addRow(data2);
    assertEquals(2, model.getRowCount());
    model.addRow(data3);
    assertEquals(3, model.getRowCount());
    model.addRow(data4);
    assertEquals(4, model.getRowCount());
    model.clearTable();
    assertEquals(0, model.getRowCount());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel#getValueAt(int, int)}
   * .
   */
  public final void testGetValueAt() {
    model.addRow(data1);
    assertEquals(0, model.getValueAt(0, 0));
    assertEquals(1, model.getValueAt(0, 1));
    assertEquals(2, model.getValueAt(0, 2));

    model.addRow(data2);
    assertNull(model.getValueAt(1, 0));
    assertNull(model.getValueAt(1, 1));
    assertNull(model.getValueAt(1, 2));
    assertEquals(0, model.getValueAt(1, 3));
    assertEquals(1, model.getValueAt(1, 4));

    model.addRow(data3);
    assertEquals(2, model.getValueAt(2, 0));
    assertEquals(1, model.getValueAt(2, 1));
    assertNull(model.getValueAt(2, 2));
    assertNull(model.getValueAt(2, 3));
    assertEquals(0, model.getValueAt(2, 4));

    model.addRow(data4);
    assertNull(model.getValueAt(3, 0));
    assertEquals(1, model.getValueAt(3, 1));
    assertNull(model.getValueAt(3, 2));
    assertEquals(0, model.getValueAt(3, 3));
    assertNull(model.getValueAt(3, 4));
    assertEquals(2, model.getValueAt(3, 5));

  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel#addRow(org.jamesii.gui.utils.dialogs.IBrowseFSDialogEntry)}
   * .
   */
  public final void testAddRow() {
    assertEquals(0, model.getColumnCount());
    assertEquals(0, model.getRowCount());

    model.addRow(data1);
    assertEquals(3, model.getColumnCount());
    assertEquals(1, model.getRowCount());

    model.addRow(data2);
    assertEquals(5, model.getColumnCount());
    assertEquals(2, model.getRowCount());

    model.addRow(data3);
    assertEquals(5, model.getColumnCount());
    assertEquals(3, model.getRowCount());

    model.addRow(data4);
    assertEquals(6, model.getColumnCount());
    assertEquals(4, model.getRowCount());

  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel#clearTable()}
   * .
   */
  public final void testClearTable() {
    assertEquals(0, model.getRowCount());
    assertEquals(0, model.getColumnCount());

    model.addRow(data1);
    model.addRow(data2);
    model.addRow(data3);
    model.addRow(data4);

    assertEquals(6, model.getColumnCount());
    assertEquals(4, model.getRowCount());

    model.clearTable();
    assertEquals(0, model.getRowCount());
    assertEquals(0, model.getColumnCount());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.dialogs.BrowseFSDialogTableModel#getData(int)}
   * .
   */
  public final void testGetData() {
    model.addRow(data1);
    model.addRow(data2);
    model.addRow(data3);
    model.addRow(data4);

    assertSame(data1, model.getData(0));
    assertSame(data2, model.getData(1));
    assertSame(data3, model.getData(2));
    assertSame(data4, model.getData(3));
  }

}
