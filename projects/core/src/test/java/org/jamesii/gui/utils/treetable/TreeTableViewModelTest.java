/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import junit.framework.TestCase;

/**
 * Tests the {@link OldTreeTableViewModel} class.
 * 
 * @author Johannes Rössel
 */
public class TreeTableViewModelTest extends TestCase {

  public void testTreeTableViewModel() {
    // TODO adapt test cases to new TreeTableViewModel
  }
  // /**
  // * The {@link IOldTreeTableModel} for this test:
  // *
  // * <pre>
  // * a Root node 1
  // * b Child node 1.1
  // * c Child node 1.2
  // * d Child child node 1.2.1
  // * e Child child node 1.2.1
  // * f Child node 1.3
  // * g Child node 1.4
  // * h Root node 2
  // * i Child node 2.1
  // * j Child node 2.2
  // * k Child child node 2.2.1
  // * l Child node 2.3
  // * m Child child node 2.3.1
  // * n Child child node 2.3.1
  // * o Child node 2.4
  // * p Root node 3
  // * q Root node 4
  // * </pre>
  // *
  // * The entire first column is not editable, in the second column
  // every row
  // * that has a prime index is editable. The column class is {@link
  // Object} for
  // * the first column, {@link String} for the second one.
  // */
  // private AbstractTreeTableModel ttm;
  //
  // /** The {@link OldTreeTableViewModel} that is actually tested
  // here. */
  // private TreeTableViewModel ttvm;
  //
  // /**
  // * Creates the {@link IOldTreeTableModel} for this test. See
  // {@link #ttm} for an
  // * illustration of how the model itself looks like.
  // *
  // * @return A new instance of the {@link IOldTreeTableModel} used
  // in most of the
  // * tests.
  // */
  // private AbstractTreeTableModel getTreeTableModel() {
  // return new AbstractTreeTableModel() {
  //
  // private static final long serialVersionUID = 1L;
  //
  // private TreeNode root = new TreeNode("", "",
  // Arrays
  // .asList(new TreeNode[] {
  // new TreeNode("a", "Root node 1", Arrays
  // .asList(new TreeNode[] {
  // new TreeNode("b", "Child node 1.1", Arrays
  // .asList(new TreeNode[] {})),
  // new TreeNode("c", "Child node 1.2", Arrays
  // .asList(new TreeNode[] {
  // new TreeNode("d", "Child node 1.2.1", Arrays
  // .asList(new TreeNode[] {})),
  // new TreeNode("e", "Child node 1.2.2", Arrays
  // .asList(new TreeNode[] {})) })),
  // new TreeNode("f", "Child node 1.3", Arrays
  // .asList(new TreeNode[] {})),
  // new TreeNode("g", "Child node 1.4", Arrays
  // .asList(new TreeNode[] {})) })),
  // new TreeNode("h", "Root node 2", Arrays
  // .asList(new TreeNode[] {
  // new TreeNode("i", "Child node 2.1", Arrays
  // .asList(new TreeNode[] {})),
  // new TreeNode("j", "Child node 2.2", Arrays
  // .asList(new TreeNode[] { new TreeNode("k",
  // "Child node 2.2.1", Arrays
  // .asList(new TreeNode[] {})) })),
  // new TreeNode("l", "Child node 2.3", Arrays
  // .asList(new TreeNode[] {
  // new TreeNode("m", "Child node 2.3.1", Arrays
  // .asList(new TreeNode[] {})),
  // new TreeNode("n", "Child node 2.3.2", Arrays
  // .asList(new TreeNode[] {})) })),
  // new TreeNode("o", "Child node 2.4", Arrays
  // .asList(new TreeNode[] {})) })),
  // new TreeNode("p", "Root node 3", Arrays
  // .asList(new TreeNode[] {})),
  // new TreeNode("q", "Root node 4", Arrays
  // .asList(new TreeNode[] {})) }));
  //
  // @Override
  // public int getColumnCount() {
  // return 2;
  // }
  //
  // @Override
  // public Object getValueAt(Object node, int column) {
  // switch (column) {
  // case 0:
  // return ((TreeNode) node).getFirstColumn();
  // case 1:
  // return ((TreeNode) node).getSecondColumn();
  // default:
  // throw new IndexOutOfBoundsException();
  // }
  //
  // }
  //
  // @Override
  // public boolean isCellEditable(Object node, int columnIndex) {
  // if (columnIndex == 0)
  // return false;
  //
  // if (columnIndex == 1) {
  // TreeNode tn = (TreeNode) node;
  // if (tn.getFirstColumn().equals("c")
  // || tn.getFirstColumn().equals("d")
  // || tn.getFirstColumn().equals("f")
  // || tn.getFirstColumn().equals("h")
  // || tn.getFirstColumn().equals("l")
  // || tn.getFirstColumn().equals("n"))
  // return true;
  // }
  //
  // return false;
  // }
  //
  // @Override
  // public void setValueAt(Object aValue, Object node, int
  // columnIndex) {
  // // TODO Auto-generated method stub
  //
  // }
  //
  // @Override
  // public void addTreeModelListener(TreeModelListener l) {
  // // TODO Auto-generated method stub
  //
  // }
  //
  // @Override
  // public Object getChild(Object parent, int index) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // public int getChildCount(Object parent) {
  // // TODO Auto-generated method stub
  // return 0;
  // }
  //
  // @Override
  // public int getIndexOfChild(Object parent, Object child) {
  // // TODO Auto-generated method stub
  // return 0;
  // }
  //
  // @Override
  // public Object getRoot() {
  // return root;
  // }
  //
  // @Override
  // public boolean isLeaf(Object node) {
  // // TODO Auto-generated method stub
  // return false;
  // }
  //
  // @Override
  // public void removeTreeModelListener(TreeModelListener l) {
  // // TODO Auto-generated method stub
  //
  // }
  //
  // @Override
  // public void valueForPathChanged(TreePath path, Object newValue) {
  // // TODO Auto-generated method stub
  //
  // }
  //
  // @Override
  // public int getRowCount() {
  // // TODO Auto-generated method stub
  // return 0;
  // }
  //
  // @Override
  // public Object getValueAt(int rowIndex, int columnIndex) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // };
  // }
  //
  // // @Override
  // protected void setUp() throws Exception {
  // ttm = getTreeTableModel();
  // ttvm = new TreeTableViewModel(ttm);
  // }
  //
  // // @Override
  // protected void tearDown() throws Exception {
  // ttvm = null;
  // }
  //
  // private void reset() {
  // try {
  // this.tearDown();
  // this.setUp();
  // } catch (Exception e) {
  // }
  // }
  //
  // /**
  // * Tries to ensure that the tree table model accurately models the
  // tree I've
  // * had in mind for testing this.
  // */
  // public void testTTModel() {
  // assertEquals(17, ttm.getRowCount());
  // assertEquals(2, ttm.getColumnCount());
  // assertEquals("a", ttm.getValueAt(0, 0));
  // assertEquals("d", ttm.getValueAt(3, 0));
  // assertEquals("Root node 1", ttm.getValueAt(0, 1));
  // assertEquals("Root node 4", ttm.getValueAt(16, 1));
  // assertEquals(null, ttm.getValueAt(-1, -1));
  // assertEquals(null, ttm.getValueAt(0, -1));
  // assertEquals(null, ttm.getValueAt(-1, 0));
  // assertEquals(null, ttm.getValueAt(17, 0));
  // assertEquals(null, ttm.getValueAt(0, 2));
  // assertEquals(null, ttm.getValueAt(17, 2));
  // }
  //
  // /**
  // * Tests some values that remain unchanged by the view model, such
  // as the
  // * number of columns.
  // */
  // public void testBasicProperties() {
  // assertEquals(ttm.getColumnCount(), ttvm.getColumnCount());
  // assertEquals(ttm, ttvm.getModel());
  // }
  //
  // /**
  // * Tests whether an empty {@link IOldTreeTableModel} gets handled
  // correctly by
  // * the wrapping {@link OldTreeTableViewModel}. This previously was
  // a bug. This
  // * test is intended to avoid regressions.
  // */
  // public void testEmptyModel() {
  // ttm = new OldAbstractTreeTableModel() {
  // private static final long serialVersionUID =
  // -126142113794563488L;
  //
  // @Override
  // public Object getValueAt(int rowIndex, int columnIndex) {
  // return null;
  // }
  //
  // @Override
  // public int getRowCount() {
  // return 0;
  // }
  //
  // @Override
  // public int getColumnCount() {
  // return 0;
  // }
  //
  // @Override
  // public boolean isLeaf(int row) {
  // return false;
  // }
  //
  // @Override
  // public int getDepth(int row) {
  // return 0;
  // }
  // };
  // ttvm = new OldTreeTableViewModel(ttm);
  //
  // assertEquals(0, ttvm.getColumnCount());
  // assertEquals(0, ttvm.getRowCount());
  // assertEquals(-1, ttvm.getModelIndex(0));
  // assertEquals(-1, ttvm.getViewIndex(0));
  // }
  //
  // /**
  // * Tests whether the {@link OldTreeTableViewModel} works correctly
  // with a model
  // * that has a node at another than the first indentation level at
  // the end.
  // * This has caused an exception previously. This test is here to
  // avoid
  // * regressions.
  // */
  // public void testModelHavingNonZeroDepthAtTheEnd() {
  // ttm = new OldAbstractTreeTableModel() {
  // private static final long serialVersionUID =
  // 7655665185442924146L;
  //
  // private String[][] cells = { { "A", "B", "C", "D", "E", "F" },
  // { "a", "b", "c", "d", "e", "f" } };
  // private int[] depths = { 0, 1, 1, 0, 1, 1 };
  //
  // @Override
  // public Object getValueAt(int rowIndex, int columnIndex) {
  // return cells[columnIndex][rowIndex];
  // }
  //
  // @Override
  // public int getRowCount() {
  // return depths.length;
  // }
  //
  // @Override
  // public int getColumnCount() {
  // return cells.length;
  // }
  //
  // @Override
  // public boolean isLeaf(int row) {
  // return row == getRowCount() - 1 || getDepth(row) >= getDepth(row
  // + 1);
  // }
  //
  // @Override
  // public int getDepth(int row) {
  // return depths[row];
  // }
  // };
  //
  // // checking the model itself
  // for (int i = 0; i < ttm.getRowCount(); i++) {
  // assertEquals(new Character((char) ('A' + i)).toString(),
  // ttm.getValueAt(
  // i, 0));
  // assertEquals(new Character((char) ('a' + i)).toString(),
  // ttm.getValueAt(
  // i, 1));
  // }
  //
  // assertEquals(0, ttm.getDepth(0));
  // assertEquals(1, ttm.getDepth(1));
  // assertEquals(1, ttm.getDepth(2));
  // assertEquals(0, ttm.getDepth(3));
  // assertEquals(1, ttm.getDepth(4));
  // assertEquals(1, ttm.getDepth(5));
  //
  // assertEquals(false, ttm.isLeaf(0));
  // assertEquals(true, ttm.isLeaf(1));
  // assertEquals(true, ttm.isLeaf(2));
  // assertEquals(false, ttm.isLeaf(3));
  // assertEquals(true, ttm.isLeaf(4));
  // assertEquals(true, ttm.isLeaf(5));
  //
  // // assign new view model
  // ttvm = new OldTreeTableViewModel(ttm);
  //
  // // test the view model
  // assertEquals(2, ttvm.getRowCount());
  // assertEquals(ttm.getColumnCount(), ttvm.getColumnCount());
  // assertEquals(false, ttvm.isExpanded(0));
  // assertEquals(false, ttvm.isExpanded(1));
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(3, ttvm.getModelIndex(1));
  // }
  //
  // /**
  // * Tests whether the view model retuns the same column classes as
  // the
  // * underlying model when queried.
  // */
  // public void testGetColumnClass() {
  // for (int i : new int[] { 0, 1 }) {
  // assertEquals(ttm.getColumnClass(i), ttvm.getColumnClass(i));
  // }
  // }
  //
  // /**
  // * Tests whether the column names of the underlying {@link
  // IOldTreeTableModel}
  // * are propagated. This previously was a bug. The test is intended
  // to avoid
  // * regressions.
  // */
  // public void testGetColumnName() {
  // for (int i : new int[] { 0, 1 }) {
  // assertEquals(ttm.getColumnName(i), ttvm.getColumnName(i));
  // }
  // }
  //
  // /**
  // * Tests whether the editable state of the cells gets passed on
  // correctly from
  // * the underlying model.
  // */
  // public void testIsCellEditable() {
  // // check the model first, as my criteria are a little odd
  // for (int i = 0; i < ttm.getRowCount(); i++)
  // assertFalse(ttm.isCellEditable(i, 0));
  //
  // assertFalse(ttm.isCellEditable(0, 1));
  // assertFalse(ttm.isCellEditable(1, 1));
  // assertTrue(ttm.isCellEditable(2, 1));
  // assertTrue(ttm.isCellEditable(3, 1));
  // assertFalse(ttm.isCellEditable(4, 1));
  // assertTrue(ttm.isCellEditable(5, 1));
  // assertFalse(ttm.isCellEditable(6, 1));
  // assertTrue(ttm.isCellEditable(7, 1));
  // assertFalse(ttm.isCellEditable(8, 1));
  // assertFalse(ttm.isCellEditable(9, 1));
  // assertFalse(ttm.isCellEditable(10, 1));
  // assertTrue(ttm.isCellEditable(11, 1));
  // assertFalse(ttm.isCellEditable(12, 1));
  // assertTrue(ttm.isCellEditable(13, 1));
  // assertFalse(ttm.isCellEditable(14, 1));
  // assertFalse(ttm.isCellEditable(15, 1));
  //
  // // check the collapsed view model
  // for (int i = 0; i < ttvm.getRowCount(); i++)
  // assertFalse(ttm.isCellEditable(i, 0));
  //
  // assertFalse(ttvm.isCellEditable(0, 1));
  // assertTrue(ttvm.isCellEditable(1, 1));
  // assertFalse(ttvm.isCellEditable(2, 1));
  // assertFalse(ttvm.isCellEditable(3, 1));
  //
  // // expand a node and check again
  // ttvm.setExpanded(1, true);
  //
  // for (int i = 0; i < ttvm.getRowCount(); i++)
  // assertFalse(ttm.isCellEditable(i, 0));
  //
  // assertFalse(ttvm.isCellEditable(0, 1));
  // assertTrue(ttvm.isCellEditable(1, 1));
  // assertFalse(ttvm.isCellEditable(2, 1));
  // assertFalse(ttvm.isCellEditable(3, 1));
  // assertTrue(ttvm.isCellEditable(4, 1));
  // assertFalse(ttvm.isCellEditable(5, 1));
  // assertFalse(ttvm.isCellEditable(6, 1));
  // assertFalse(ttvm.isCellEditable(7, 1));
  // }
  //
  // /**
  // * Tests whether setting a value in the view model causes the
  // correct value in
  // * the underlying model to be changed.
  // */
  // public void testSetValueAt() {
  // // try to set a value in the first row
  // ttvm.setValueAt("foo", 0, 0);
  // assertEquals("foo", ttvm.getValueAt(0, 0));
  // assertEquals("h", ttvm.getValueAt(1, 0));
  // assertEquals("p", ttvm.getValueAt(2, 0));
  // assertEquals("q", ttvm.getValueAt(3, 0));
  // assertEquals("foo", ttm.getValueAt(0, 0));
  //
  // ttvm.setValueAt("bar", 1, 1);
  // assertEquals("bar", ttvm.getValueAt(1, 1));
  // assertEquals("bar", ttm.getValueAt(7, 1));
  //
  // // expand a node
  // ttvm.setExpanded(1, true);
  //
  // ttvm.setValueAt("baz", 5, 0);
  // assertEquals("baz", ttvm.getValueAt(5, 0));
  // assertEquals("baz", ttm.getValueAt(14, 0));
  // }
  //
  // /**
  // * Checks whether the view model conforms to the given
  // expectations.
  // *
  // * @param num
  // * The number of visible rows.
  // * @param col1
  // * The first column.
  // * @param col2
  // * The second column.
  // * @param depths
  // * The depths of each row.
  // * @param expanded
  // * The expanded states of each row.
  // * @param leaf
  // * The leaf state of each row.
  // */
  // private void testViewModelImpl(int num, String[] col1, String[]
  // col2,
  // int[] depths, boolean[] expanded, boolean[] leaf) {
  //
  // assertEquals("Array length doesn't match: col1", num,
  // col1.length);
  // assertEquals("Array length doesn't match: col2", num,
  // col2.length);
  // assertEquals("Array length doesn't match: depths", num,
  // depths.length);
  // assertEquals("Array length doesn't match: expanded", num,
  // expanded.length);
  // assertEquals("Array length doesn't match: leaf", num,
  // leaf.length);
  //
  // assertEquals(num, ttvm.getRowCount());
  //
  // for (int i = 0; i < num; i++) {
  // assertEquals(col1[i], ttvm.getValueAt(i, 0));
  // assertEquals(col2[i], ttvm.getValueAt(i, 1));
  // assertEquals(depths[i], ttvm.getDepth(i));
  // assertEquals(expanded[i], ttvm.isExpanded(i));
  // assertEquals(leaf[i], ttvm.isLeaf(i));
  // }
  // }
  //
  // /**
  // * Tests whether the ViewModel is correct when everything is
  // collapsed.
  // */
  // public void testAllCollapsed() {
  // int num = 4;
  // String[] col1 = { "a", "h", "p", "q" };
  // String[] col2 = { "Root node 1", "Root node 2", "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 0, 0, 0 };
  // boolean[] expanded = { false, false, false, false };
  // boolean[] leaf = { false, false, true, true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /**
  // * Tests whether the ViewModel is correct when expanding a single
  // node in the
  // * middle right after start.
  // */
  // public void testExpandSingleNodeInTheMiddle() {
  // // at first everything is collapsed. Now we expand the "h" node
  // (index 1,
  // // currently).
  // ttvm.setExpanded(1, true);
  //
  // int num = 8;
  // String[] col1 = { "a", "h", "i", "j", "l", "o", "p", "q" };
  // String[] col2 = { "Root node 1", "Root node 2", "Child node 2.1",
  // "Child node 2.2", "Child node 2.3", "Child node 2.4",
  // "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 0, 1, 1, 1, 1, 0, 0 };
  // boolean[] expanded = { false, true, false, false, false, false,
  // false,
  // false };
  // boolean[] leaf = { false, false, true, false, false, true, true,
  // true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /**
  // * Tests whether expanding a sub-node of an already expanded node
  // in the
  // * middle works.
  // */
  // public void testExpandSubnodeOfExpendedNodeInTheMiddle() {
  // // at first everything is collapsed. Now we expand the "h" node
  // (index 1,
  // // currently).
  // ttvm.setExpanded(1, true);
  // // now expand the "l" node, which should be at index 4
  // ttvm.setExpanded(4, true);
  //
  // int num = 10;
  // String[] col1 = { "a", "h", "i", "j", "l", "m", "n", "o", "p",
  // "q" };
  // String[] col2 = { "Root node 1", "Root node 2", "Child node 2.1",
  // "Child node 2.2", "Child node 2.3", "Child child node 2.3.1",
  // "Child child node 2.3.2", "Child node 2.4", "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 0, 1, 1, 1, 2, 2, 1, 0, 0 };
  // boolean[] expanded = { false, true, false, false, true, false,
  // false,
  // false, false, false };
  // boolean[] leaf = { false, false, true, false, false, true, true,
  // true,
  // true, true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /** Tests when another sub-node is expanded. */
  // public void testExpandAnotherSubnode() {
  // // at first everything is collapsed. Now we expand the "h" node
  // (index 1,
  // // currently).
  // ttvm.setExpanded(1, true);
  // // now expand the "l" node, which should be at index 4
  // ttvm.setExpanded(4, true);
  // // now expand the "j" node, which should be at index 3
  // ttvm.setExpanded(3, true);
  //
  // int num = 11;
  // String[] col1 = { "a", "h", "i", "j", "k", "l", "m", "n", "o",
  // "p", "q" };
  // String[] col2 = { "Root node 1", "Root node 2", "Child node 2.1",
  // "Child node 2.2", "Child child node 2.2.1", "Child node 2.3",
  // "Child child node 2.3.1", "Child child node 2.3.2",
  // "Child node 2.4",
  // "Root node 3", "Root node 4" };
  // int[] depths = { 0, 0, 1, 1, 2, 1, 2, 2, 1, 0, 0 };
  // boolean[] expanded = { false, true, false, true, false, true,
  // false, false,
  // false, false, false };
  // boolean[] leaf = { false, false, true, false, true, false, true,
  // true,
  // true, true, true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /**
  // * Tests whether collapsing a sub-node again works.
  // */
  // public void testCollapseSubnode() {
  // // at first everything is collapsed. Now we expand the "h" node
  // (index 1,
  // // currently).
  // ttvm.setExpanded(1, true);
  // // now expand the "l" node, which should be at index 4
  // ttvm.setExpanded(4, true);
  // // now expand the "j" node, which should be at index 3
  // ttvm.setExpanded(3, true);
  // // now collapse the "j" node again
  // ttvm.setExpanded(3, false);
  //
  // int num = 10;
  // String[] col1 = { "a", "h", "i", "j", "l", "m", "n", "o", "p",
  // "q" };
  // String[] col2 = { "Root node 1", "Root node 2", "Child node 2.1",
  // "Child node 2.2", "Child node 2.3", "Child child node 2.3.1",
  // "Child child node 2.3.2", "Child node 2.4", "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 0, 1, 1, 1, 2, 2, 1, 0, 0 };
  // boolean[] expanded = { false, true, false, false, true, false,
  // false,
  // false, false, false };
  // boolean[] leaf = { false, false, true, false, false, true, true,
  // true,
  // true, true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /**
  // * Tests whether expanding the first node before the already
  // expanded one
  // * works.
  // */
  // public void testExpandFirstNode() {
  // // at first everything is collapsed. Now we expand the "h" node
  // (index 1,
  // // currently).
  // ttvm.setExpanded(1, true);
  // // now expand the "l" node, which should be at index 4
  // ttvm.setExpanded(4, true);
  // // now expand the "j" node, which should be at index 3
  // ttvm.setExpanded(3, true);
  // // now collapse the "j" node again
  // ttvm.setExpanded(3, false);
  // // collapse the "l" node again, which is at index 4
  // ttvm.setExpanded(4, false);
  // // now expand the "a" node, which is at index 0
  // ttvm.setExpanded(0, true);
  //
  // int num = 12;
  // String[] col1 = { "a", "b", "c", "f", "g", "h", "i", "j", "l",
  // "o", "p",
  // "q" };
  // String[] col2 = { "Root node 1", "Child node 1.1",
  // "Child node 1.2",
  // "Child node 1.3", "Child node 1.4", "Root node 2",
  // "Child node 2.1",
  // "Child node 2.2", "Child node 2.3", "Child node 2.4",
  // "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0 };
  // boolean[] expanded = { true, false, false, false, false, true,
  // false,
  // false, false, false, false, false };
  // boolean[] leaf = { false, true, false, true, true, false, true,
  // false,
  // false, true, true, true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /** Tests whether collapsing the middle node again works. */
  // public void testCollapseMiddleNodeAgain() {
  // // at first everything is collapsed. Now we expand the "h" node
  // (index 1,
  // // currently).
  // ttvm.setExpanded(1, true);
  // // now expand the "l" node, which should be at index 4
  // ttvm.setExpanded(4, true);
  // // now expand the "j" node, which should be at index 3
  // ttvm.setExpanded(3, true);
  // // now collapse the "j" node again
  // ttvm.setExpanded(3, false);
  // // collapse the "l" node again, which is at index 4
  // ttvm.setExpanded(4, false);
  // // now expand the "a" node, which is at index 0
  // ttvm.setExpanded(0, true);
  // // now collapse the "h" node again
  // ttvm.setExpanded(5, false);
  //
  // int num = 8;
  // String[] col1 = { "a", "b", "c", "f", "g", "h", "p", "q" };
  // String[] col2 = { "Root node 1", "Child node 1.1",
  // "Child node 1.2",
  // "Child node 1.3", "Child node 1.4", "Root node 2", "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 1, 1, 1, 1, 0, 0, 0 };
  // boolean[] expanded = { true, false, false, false, false, false,
  // false,
  // false };
  // boolean[] leaf = { false, true, false, true, true, false, true,
  // true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /** Tests whether collapsing a node twice doesn't to anything. */
  // public void testCollapseTwice() {
  // // default state is to have everything collapsed
  // // try to collapse "a" even though it is already:
  // try {
  // ttvm.setExpanded(0, false);
  // } catch (Exception e) {
  // fail();
  // }
  //
  // // the view model shouldn't look different now
  // int num = 4;
  // String[] col1 = { "a", "h", "p", "q" };
  // String[] col2 = { "Root node 1", "Root node 2", "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 0, 0, 0 };
  // boolean[] expanded = { false, false, false, false };
  // boolean[] leaf = { false, false, true, true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /** Tests whether expanding a node twice doesn't do anything. */
  // public void testExpandTwice() {
  // // default state is to have everything collapsed, so expand "a"
  // first
  // ttvm.setExpanded(0, true);
  //
  // // check whether the view model looks correct
  // // the view model shouldn't look different now
  // int num = 8;
  // String[] col1 = { "a", "b", "c", "f", "g", "h", "p", "q" };
  // String[] col2 = { "Root node 1", "Child node 1.1",
  // "Child node 1.2",
  // "Child node 1.3", "Child node 1.4", "Root node 2", "Root node 3",
  // "Root node 4" };
  // int[] depths = { 0, 1, 1, 1, 1, 0, 0, 0 };
  // boolean[] expanded = { true, false, false, false, false, false,
  // false,
  // false };
  // boolean[] leaf = { false, true, false, true, true, false, true,
  // true };
  //
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  //
  // // try to expand "a" even though it is already:
  // try {
  // ttvm.setExpanded(0, true);
  // } catch (Exception e) {
  // fail();
  // }
  //
  // // the view model shouldn't look different now
  // testViewModelImpl(num, col1, col2, depths, expanded, leaf);
  // }
  //
  // /**
  // * Tests whether the {@link
  // OldTreeTableViewModel#getModelIndex(int)} method
  // * works as expected.
  // */
  // public void testViewToModel() {
  // // try for known present values
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(15, ttvm.getModelIndex(2));
  // assertEquals(16, ttvm.getModelIndex(3));
  //
  // // try for not-present values
  // assertEquals(-1, ttvm.getModelIndex(-1));
  // assertEquals(-1, ttvm.getModelIndex(4));
  //
  // // expand a node
  // ttvm.setExpanded(1, true);
  //
  // // check again, present values
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(8, ttvm.getModelIndex(2));
  // assertEquals(11, ttvm.getModelIndex(4));
  // assertEquals(14, ttvm.getModelIndex(5));
  // assertEquals(16, ttvm.getModelIndex(7));
  //
  // // not-present ones
  // assertEquals(-1, ttvm.getModelIndex(-1));
  // assertEquals(-1, ttvm.getModelIndex(8));
  //
  // // collapse the node again
  // ttvm.setExpanded(1, false);
  //
  // // try for known present values
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(15, ttvm.getModelIndex(2));
  // assertEquals(16, ttvm.getModelIndex(3));
  //
  // // try for not-present values
  // assertEquals(-1, ttvm.getModelIndex(-1));
  // assertEquals(-1, ttvm.getModelIndex(4));
  // }
  //
  // /**
  // * Tests whether the {@link
  // OldTreeTableViewModel#getViewIndex(int)} method works
  // * as expected.
  // */
  // public void testModelToView() {
  // // try for known present values
  // assertEquals(0, ttvm.getViewIndex(0));
  // assertEquals(1, ttvm.getViewIndex(7));
  // assertEquals(2, ttvm.getViewIndex(15));
  // assertEquals(3, ttvm.getViewIndex(16));
  //
  // // try for not-present values
  // assertEquals(-1, ttvm.getViewIndex(-1));
  // assertEquals(-1, ttvm.getViewIndex(4));
  // assertEquals(-1, ttvm.getViewIndex(6));
  // assertEquals(-1, ttvm.getViewIndex(8));
  // assertEquals(-1, ttvm.getViewIndex(11));
  // assertEquals(-1, ttvm.getViewIndex(12));
  //
  // // expand a node
  // ttvm.setExpanded(1, true);
  //
  // // check again, present values
  // assertEquals(0, ttvm.getViewIndex(0));
  // assertEquals(1, ttvm.getViewIndex(7));
  // assertEquals(2, ttvm.getViewIndex(8));
  // assertEquals(4, ttvm.getViewIndex(11));
  // assertEquals(5, ttvm.getViewIndex(14));
  // assertEquals(7, ttvm.getViewIndex(16));
  //
  // // not-present ones
  // assertEquals(-1, ttvm.getViewIndex(-1));
  // assertEquals(-1, ttvm.getViewIndex(3));
  // assertEquals(-1, ttvm.getViewIndex(5));
  // assertEquals(-1, ttvm.getViewIndex(6));
  // assertEquals(-1, ttvm.getViewIndex(10));
  // assertEquals(-1, ttvm.getViewIndex(13));
  //
  // // collapse the node again
  // ttvm.setExpanded(1, false);
  //
  // // try for known present values
  // assertEquals(0, ttvm.getViewIndex(0));
  // assertEquals(1, ttvm.getViewIndex(7));
  // assertEquals(2, ttvm.getViewIndex(15));
  // assertEquals(3, ttvm.getViewIndex(16));
  //
  // // try for not-present values
  // assertEquals(-1, ttvm.getViewIndex(-1));
  // assertEquals(-1, ttvm.getViewIndex(4));
  // assertEquals(-1, ttvm.getViewIndex(6));
  // assertEquals(-1, ttvm.getViewIndex(8));
  // assertEquals(-1, ttvm.getViewIndex(11));
  // assertEquals(-1, ttvm.getViewIndex(12));
  // }
  //
  // /**
  // * Helper code for testing whether a listener was called or not.
  // Specifically
  // * for a cell update.
  // */
  // private void testListenerUpdateCellImpl(final int row, final int
  // col,
  // final int viewRow, final Object newValue,
  // boolean shouldHaveVisitedListener) {
  // Object oldValue = ttm.getValueAt(row, col);
  //
  // final AtomicBoolean listenerVisited = new AtomicBoolean(false);
  //
  // TableModelListener cellUpdatedListener = new TableModelListener()
  // {
  // @Override
  // public void tableChanged(TableModelEvent e) {
  // assertEquals(TableModelEventType.CELL_UPDATED, getEventType(e));
  // assertEquals(viewRow, e.getFirstRow());
  // assertEquals(viewRow, e.getLastRow());
  // assertEquals(col, e.getColumn());
  // assertEquals(newValue, ttvm.getValueAt(viewRow, col));
  // listenerVisited.set(true);
  // }
  // };
  //
  // try {
  // ttvm.addTableModelListener(cellUpdatedListener);
  // ttm.setValueAt(newValue, row, col);
  // ttvm.removeTableModelListener(cellUpdatedListener);
  // } finally {
  // // reset the model to its old state
  // try {
  // ttm.setValueAt(oldValue, row, col);
  // } catch (Exception e) {
  // }
  // }
  //
  // if (shouldHaveVisitedListener)
  // assertTrue("Listener should have been invoked once but wasn't.",
  // listenerVisited.get());
  // else
  // assertFalse("Listener shouldn't have been invoked but was.",
  // listenerVisited.get());
  // }
  //
  // /**
  // * Helper code for testing whether a listener was called or not.
  // Specifically
  // * for deleting rows.
  // */
  // private void testListenerDeleteRowsImpl(Runnable initCode,
  // final int firstModelRow, final int lastModelRow, final int
  // firstViewRow,
  // final int lastViewRow, boolean shouldHaveVisitedListener) {
  // final AtomicBoolean listenerVisited = new AtomicBoolean(false);
  //
  // reset();
  // initCode.run();
  //
  // TableModelListener rowsDeletedListener = new TableModelListener()
  // {
  // @Override
  // public void tableChanged(TableModelEvent e) {
  // assertEquals(TableModelEventType.ROWS_DELETED, getEventType(e));
  // assertEquals(firstViewRow, e.getFirstRow());
  // assertEquals(lastViewRow, e.getLastRow());
  // assertEquals(TableModelEvent.ALL_COLUMNS, e.getColumn());
  // listenerVisited.set(true);
  // }
  // };
  //
  // ttvm.addTableModelListener(rowsDeletedListener);
  // ttm.fireTableRowsDeleted(firstModelRow, lastModelRow);
  // ttvm.removeTableModelListener(rowsDeletedListener);
  //
  // if (shouldHaveVisitedListener)
  // assertTrue("Listener should have been invoked once but wasn't.",
  // listenerVisited.get());
  // else
  // assertFalse("Listener shouldn't have been invoked but was.",
  // listenerVisited.get());
  // }
  //
  // /**
  // * Helper code for testing whether a listener was called or not.
  // Specifically
  // * for updating rows.
  // */
  // private void testListenerUpdateRowsImpl(final int firstModelRow,
  // final int lastModelRow, final int firstViewRow, final int
  // lastViewRow,
  // boolean shouldHaveVisitedListener) {
  // final AtomicBoolean listenerVisited = new AtomicBoolean(false);
  //
  // TableModelListener rowsUpdatedListener = new TableModelListener()
  // {
  // @Override
  // public void tableChanged(TableModelEvent e) {
  // assertEquals(TableModelEventType.ROWS_UPDATED, getEventType(e));
  // assertEquals(firstViewRow, e.getFirstRow());
  // assertEquals(lastViewRow, e.getLastRow());
  // assertEquals(TableModelEvent.ALL_COLUMNS, e.getColumn());
  // listenerVisited.set(true);
  // }
  // };
  //
  // ttvm.addTableModelListener(rowsUpdatedListener);
  // ttm.fireTableRowsUpdated(firstModelRow, lastModelRow);
  // ttvm.removeTableModelListener(rowsUpdatedListener);
  //
  // if (shouldHaveVisitedListener)
  // assertTrue("Listener should have been invoked once but wasn't.",
  // listenerVisited.get());
  // else
  // assertFalse("Listener shouldn't have been invoked but was.",
  // listenerVisited.get());
  // }
  //
  // /**
  // * Helper code for testing whether a listener was called or not.
  // Specifically
  // * for inserting rows.
  // */
  // private void testListenerInsertRowsImpl(Runnable initCode,
  // final int firstModelRow, final int lastModelRow, final int
  // firstViewRow,
  // final int lastViewRow, boolean shouldHaveVisitedListener) {
  // final AtomicBoolean listenerVisited = new AtomicBoolean(false);
  //
  // reset();
  // initCode.run();
  //
  // TableModelListener rowsAddedListener = new TableModelListener() {
  // @Override
  // public void tableChanged(TableModelEvent e) {
  // assertEquals(TableModelEventType.ROWS_INSERTED, getEventType(e));
  // assertEquals(firstViewRow, e.getFirstRow());
  // assertEquals(lastViewRow, e.getLastRow());
  // assertEquals(TableModelEvent.ALL_COLUMNS, e.getColumn());
  // listenerVisited.set(true);
  // }
  // };
  //
  // ttvm.addTableModelListener(rowsAddedListener);
  // ttm.fireTableRowsInserted(firstModelRow, lastModelRow);
  // ttvm.removeTableModelListener(rowsAddedListener);
  //
  // if (shouldHaveVisitedListener)
  // assertTrue("Listener should have been invoked once but wasn't.",
  // listenerVisited.get());
  // else
  // assertFalse("Listener shouldn't have been invoked but was.",
  // listenerVisited.get());
  // }
  //
  // /**
  // * Tests whether changes in the model get propagated correctly to
  // the
  // * ViewModel.
  // */
  // public void testListenerUpdateVisibleCell() {
  // // very first value, necessarily visible
  // testListenerUpdateCellImpl(0, 0, 0, "Abc", true);
  // // second visible value, although index 7 in the underlying model
  // testListenerUpdateCellImpl(7, 0, 1, "foo", true);
  // testListenerUpdateCellImpl(15, 0, 2, "bar", true);
  // testListenerUpdateCellImpl(16, 0, 3, "bar", true);
  // }
  //
  // /**
  // * Tests whether changes in the model on invisible items won't
  // trigger events
  // * in the ViewModel.
  // */
  // public void testListenerUpdateInvisibleCell() {
  // testListenerUpdateCellImpl(1, 0, -1, "B", false);
  // testListenerUpdateCellImpl(10, 0, -1, "X", false);
  // }
  //
  // /**
  // * Tests whether updating the entire table does relay the event
  // properly to
  // * the ViewModel.
  // */
  // public void testListenerUpdateTableData() {
  // final AtomicBoolean listenerVisited = new AtomicBoolean(false);
  //
  // TableModelListener tableDataChangedListener = new
  // TableModelListener() {
  // @Override
  // public void tableChanged(TableModelEvent e) {
  // assertEquals(TableModelEventType.TABLE_DATA_CHANGED,
  // getEventType(e));
  // assertEquals(0, e.getFirstRow());
  // assertEquals(Integer.MAX_VALUE, e.getLastRow());
  // assertEquals(TableModelEvent.ALL_COLUMNS, e.getColumn());
  // listenerVisited.set(true);
  // }
  // };
  //
  // ttm.addTableModelListener(tableDataChangedListener);
  // ttm.fireTableDataChanged();
  // ttm.removeTableModelListener(tableDataChangedListener);
  //
  // /** listener should have been called */
  // assertTrue(listenerVisited.get());
  // }
  //
  // /**
  // * Tests whether updating the table structure will cause the event
  // to be
  // * relayed correctly to the ViewModel.
  // */
  // public void testListenerUpdateTableStructure() {
  // final AtomicBoolean listenerVisited = new AtomicBoolean(false);
  //
  // TableModelListener tableStructureChangedListener = new
  // TableModelListener() {
  // @Override
  // public void tableChanged(TableModelEvent e) {
  // assertEquals(TableModelEventType.TABLE_STRUCTURE_CHANGED,
  // getEventType(e));
  // assertEquals(TableModelEvent.HEADER_ROW, e.getFirstRow());
  // assertEquals(TableModelEvent.HEADER_ROW, e.getLastRow());
  // assertEquals(TableModelEvent.ALL_COLUMNS, e.getColumn());
  // listenerVisited.set(true);
  // }
  // };
  //
  // ttm.addTableModelListener(tableStructureChangedListener);
  // ttm.fireTableStructureChanged();
  // ttm.removeTableModelListener(tableStructureChangedListener);
  //
  // /** listener should have been called */
  // assertTrue(listenerVisited.get());
  // }
  //
  // /** A runnable doing nothing. Needed for some tests that don't
  // touch the tree. */
  // private Runnable emptyRunnable = new Runnable() {
  // @Override
  // public void run() {
  // }
  // };
  //
  // /**
  // * Tests whether deleting rows from completely invisible subtrees
  // (i. e.
  // * without overlapping of the tree structure) works as expected
  // (i. e. no
  // * event will be raised).
  // */
  // public void testListenerDeleteRowsInvisibleSubtree() {
  // testListenerDeleteRowsImpl(emptyRunnable, 4, 4, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(6, ttvm.getModelIndex(1));
  // assertEquals(14, ttvm.getModelIndex(2));
  // assertEquals(15, ttvm.getModelIndex(3));
  // assertEquals(-1, ttvm.getModelIndex(4));
  // assertEquals(1, ttvm.getViewIndex(6));
  // assertEquals(-1, ttvm.getViewIndex(7));
  // assertEquals(3, ttvm.getViewIndex(15));
  // assertEquals(-1, ttvm.getViewIndex(16));
  //
  // testListenerDeleteRowsImpl(emptyRunnable, 2, 6, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(2, ttvm.getModelIndex(1));
  // assertEquals(10, ttvm.getModelIndex(2));
  // assertEquals(11, ttvm.getModelIndex(3));
  // assertEquals(-1, ttvm.getViewIndex(7));
  // assertEquals(-1, ttvm.getViewIndex(15));
  // assertEquals(-1, ttvm.getViewIndex(16));
  //
  // testListenerDeleteRowsImpl(emptyRunnable, 9, 10, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(13, ttvm.getModelIndex(2));
  // assertEquals(14, ttvm.getModelIndex(3));
  // assertEquals(1, ttvm.getViewIndex(7));
  // assertEquals(-1, ttvm.getViewIndex(15));
  // assertEquals(-1, ttvm.getViewIndex(16));
  //
  // testListenerDeleteRowsImpl(emptyRunnable, 8, 8, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(14, ttvm.getModelIndex(2));
  // assertEquals(15, ttvm.getModelIndex(3));
  // assertEquals(1, ttvm.getViewIndex(7));
  // assertEquals(3, ttvm.getViewIndex(15));
  // assertEquals(-1, ttvm.getViewIndex(16));
  //
  // testListenerDeleteRowsImpl(emptyRunnable, 12, 13, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(13, ttvm.getModelIndex(2));
  // assertEquals(14, ttvm.getModelIndex(3));
  // assertEquals(1, ttvm.getViewIndex(7));
  // assertEquals(-1, ttvm.getViewIndex(15));
  // assertEquals(3, ttvm.getViewIndex(14));
  // }
  //
  // /**
  // * Tests whether deleting rows from completely visible subtrees
  // (i. e. without
  // * overlapping of the tree structure) works as expected (i. e. an
  // event will
  // * be raised, containing all rows that were deleted).
  // */
  // public void testListenerDeleteRowsVisibleSubtree() {
  // Runnable init = new Runnable() {
  // @Override
  // public void run() {
  // // expand all nodes, so the entire tree is visible
  // ttvm.setExpanded(0, true);
  // ttvm.setExpanded(2, true);
  // ttvm.setExpanded(7, true);
  // ttvm.setExpanded(9, true);
  // ttvm.setExpanded(11, true);
  // }
  // };
  //
  // // check "function" to avoid repeating myself too much
  // Runnable check = new Runnable() {
  // @Override
  // public void run() {
  // // since all nodes are expanded, the view model looks exactly the
  // same
  // // as the model
  // for (int i = 0; i < ttvm.getRowCount(); i++)
  // assertEquals(i, ttvm.getModelIndex(i));
  // assertEquals(-1, ttvm.getModelIndex(ttvm.getRowCount()));
  // }
  // };
  //
  // testListenerDeleteRowsImpl(init, 4, 4, 4, 4, true);
  // assertEquals(16, ttvm.getRowCount());
  // check.run();
  //
  // testListenerDeleteRowsImpl(init, 2, 6, 2, 6, true);
  // assertEquals(12, ttvm.getRowCount());
  // check.run();
  //
  // testListenerDeleteRowsImpl(init, 9, 10, 9, 10, true);
  // assertEquals(15, ttvm.getRowCount());
  // check.run();
  //
  // testListenerDeleteRowsImpl(init, 8, 8, 8, 8, true);
  // assertEquals(16, ttvm.getRowCount());
  // check.run();
  //
  // testListenerDeleteRowsImpl(init, 12, 13, 12, 13, true);
  // assertEquals(15, ttvm.getRowCount());
  // check.run();
  //
  // testListenerDeleteRowsImpl(init, 15, 16, 15, 16, true);
  // check.run();
  // }
  //
  // /**
  // * Tests whether deleting rows from completely visible subtrees
  // (i. e. without
  // * overlapping of the tree structure) works as expected (i. e. an
  // event will
  // * be raised, containing all rows that were deleted).
  // */
  // public void testListenerDeleteRowsPartiallyVisibleSubtree() {
  // Runnable init = new Runnable() {
  // @Override
  // public void run() {
  // // expand some nodes, so parts of the tree are visible
  // ttvm.setExpanded(0, true);
  // ttvm.setExpanded(5, true);
  // ttvm.setExpanded(8, true);
  // }
  // };
  //
  // testListenerDeleteRowsImpl(init, 2, 6, 2, 4, true);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(1, ttvm.getModelIndex(1));
  // assertEquals(2, ttvm.getModelIndex(2));
  // assertEquals(3, ttvm.getModelIndex(3));
  // assertEquals(6, ttvm.getModelIndex(5));
  // assertEquals(8, ttvm.getModelIndex(7));
  // assertEquals(11, ttvm.getModelIndex(10));
  // assertEquals(-1, ttvm.getModelIndex(11));
  //
  // testListenerDeleteRowsImpl(init, 9, 10, 7, 7, true);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(1, ttvm.getModelIndex(1));
  // assertEquals(2, ttvm.getModelIndex(2));
  // assertEquals(5, ttvm.getModelIndex(3));
  // assertEquals(7, ttvm.getModelIndex(5));
  // assertEquals(9, ttvm.getModelIndex(7));
  // assertEquals(12, ttvm.getModelIndex(10));
  // assertEquals(-1, ttvm.getModelIndex(13));
  // }
  //
  // /**
  // * Probably as radical as we can get with this event. Tests
  // deleting all rows.
  // */
  // public void testListenerDeleteAllRows() {
  // testListenerDeleteRowsImpl(emptyRunnable, 0, 16, 0, 3, true);
  // assertEquals(-1, ttvm.getModelIndex(0));
  // assertEquals(-1, ttvm.getViewIndex(0));
  // }
  //
  // /**
  // * Tests whether updating completely invisible rows in the model
  // works
  // * correctly and does not cause an event to be propagated from the
  // view model.
  // */
  // public void testListenerUpdateInvisibleRows() {
  // // subtrees
  // testListenerUpdateRowsImpl(2, 6, -1, -1, false);
  // testListenerUpdateRowsImpl(4, 4, -1, -1, false);
  // testListenerUpdateRowsImpl(9, 10, -1, -1, false);
  // testListenerUpdateRowsImpl(11, 13, -1, -1, false);
  // testListenerUpdateRowsImpl(12, 12, -1, -1, false);
  //
  // // arbitrary rows
  // testListenerUpdateRowsImpl(1, 3, -1, -1, false);
  // testListenerUpdateRowsImpl(4, 5, -1, -1, false);
  // testListenerUpdateRowsImpl(9, 11, -1, -1, false);
  // testListenerUpdateRowsImpl(13, 14, -1, -1, false);
  // testListenerUpdateRowsImpl(8, 9, -1, -1, false);
  // }
  //
  // public void testListenerUpdatePartiallyVisibleRows() {
  // // expand some nodes
  // ttvm.setExpanded(1, true);
  // ttvm.setExpanded(0, true);
  //
  // // subtrees
  // testListenerUpdateRowsImpl(2, 6, 2, 4, true);
  // testListenerUpdateRowsImpl(9, 10, 7, 7, true);
  // testListenerUpdateRowsImpl(2, 5, 2, 3, true);
  // testListenerUpdateRowsImpl(8, 13, 6, 8, true);
  //
  // // arbitrary rows
  // testListenerUpdateRowsImpl(1, 3, 1, 2, true);
  // testListenerUpdateRowsImpl(4, 5, 3, 3, true);
  // testListenerUpdateRowsImpl(9, 11, 7, 8, true);
  // testListenerUpdateRowsImpl(13, 14, 9, 9, true);
  // testListenerUpdateRowsImpl(13, 15, 9, 10, true);
  // }
  //
  // public void testListenerUpdateVisibleRows() {
  // // expand all nodes
  // ttvm.setExpanded(0, true);
  // ttvm.setExpanded(2, true);
  // ttvm.setExpanded(7, true);
  // ttvm.setExpanded(9, true);
  // ttvm.setExpanded(11, true);
  //
  // // every row is visible, so the mapping should be identical
  // for (int a = 0; a < ttm.getRowCount(); a++)
  // for (int b = a; b < ttm.getRowCount(); b++)
  // testListenerUpdateRowsImpl(a, b, a, b, true);
  // }
  //
  // public void testListenerInsertInvisibleSingleRow() {
  // // insert a single row at index 4
  // testListenerInsertRowsImpl(emptyRunnable, 4, 4, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(8, ttvm.getModelIndex(1));
  // assertEquals(16, ttvm.getModelIndex(2));
  // assertEquals(17, ttvm.getModelIndex(3));
  //
  // // insert a single row at index 9
  // testListenerInsertRowsImpl(emptyRunnable, 9, 9, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(16, ttvm.getModelIndex(2));
  // assertEquals(17, ttvm.getModelIndex(3));
  // }
  //
  // public void testListenerInsertInvisibleMultipleRows() {
  // // insert some (5) rows between "d" and "e"
  // testListenerInsertRowsImpl(emptyRunnable, 4, 8, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(12, ttvm.getModelIndex(1));
  // assertEquals(20, ttvm.getModelIndex(2));
  // assertEquals(21, ttvm.getModelIndex(3));
  //
  // // insert some rows between "i" and "j"
  // testListenerInsertRowsImpl(emptyRunnable, 9, 11, -1, -1, false);
  // assertEquals(0, ttvm.getModelIndex(0));
  // assertEquals(7, ttvm.getModelIndex(1));
  // assertEquals(18, ttvm.getModelIndex(2));
  // assertEquals(19, ttvm.getModelIndex(3));
  // }
  // }
  //
  // /**
  // * Small class for implementing the {@link ITreeTableModel} in a
  // nice tree
  // * fashion.
  // *
  // * @author Johannes Rössel
  // */
  // class TreeNode {
  // private Object firstColumn;
  // private Object secondColumn;
  // private List<TreeNode> children;
  //
  // public TreeNode(Object firstColumn, Object secondColumn,
  // List<TreeNode> children) {
  // this.setFirstColumn(firstColumn);
  // this.setSecondColumn(secondColumn);
  // this.setChildren(children);
  // }
  //
  // public void setFirstColumn(Object firstColumn) {
  // this.firstColumn = firstColumn;
  // }
  //
  // public Object getFirstColumn() {
  // return firstColumn;
  // }
  //
  // public void setSecondColumn(Object secondColumn) {
  // this.secondColumn = secondColumn;
  // }
  //
  // public Object getSecondColumn() {
  // return secondColumn;
  // }
  //
  // public void setChildren(List<TreeNode> children) {
  // this.children = children;
  // }
  //
  // public List<TreeNode> getChildren() {
  // return children;
  // }
}