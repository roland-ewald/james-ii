/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import org.jamesii.gui.utils.ExpandingPanel;

/**
 * Represents an information view that can show information tokens provided by
 * supplied {@link IInfoProvider}s. It is meant to be used internally only by
 * {@link SyntaxEditor}.
 * 
 * @author Stefan Rybacki
 */
class SyntaxEditorInfoView extends ExpandingPanel {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 3575838490404437529L;

  /**
   * The tree table use for displaying information.
   */
  private SyntaxEditorInfoTable table;

  /**
   * Instantiates a new syntax editor info view.
   * 
   * @param editor
   *          the editor the information are provided for
   * @param caption
   *          the caption
   * @param expandingDirection
   *          the expanding direction
   * @param resizable
   *          flag indicating whether the panel is resizable or not
   */
  public SyntaxEditorInfoView(JTextComponent editor, String caption,
      int expandingDirection, boolean resizable) {
    super(caption, expandingDirection, resizable);
    setExpanded(true);
    setCanExpand(true);
    setCanCollapse(true);

    table = new SyntaxEditorInfoTable(editor);

    JScrollPane pane = new JScrollPane(table);
    pane.setPreferredSize(new Dimension(100, 120));
    super.getInnerPanel().setLayout(new BorderLayout());
    super.getInnerPanel().add(pane, BorderLayout.CENTER);
  }

  /**
   * Adds an info provider.
   * 
   * @param p
   *          the provider to add
   */
  public void addInfoProvider(IInfoProvider p) {
    table.addInfoProvider(p);
  }

  /**
   * Removes an info provider.
   * 
   * @param p
   *          the provider to remove
   */
  public void removeInfoProvider(IInfoProvider p) {
    table.removeInfoProvider(p);
  }

  @Override
  public JComponent getInnerPanel() {
    throw new UnsupportedOperationException("Not allowed!");
  }
}
