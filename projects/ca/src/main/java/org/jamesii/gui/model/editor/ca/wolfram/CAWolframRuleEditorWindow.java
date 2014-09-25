/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.wolfram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

import org.jamesii.gui.model.ISymbolicModelWindowManager;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;
import org.jamesii.gui.visualization.grid.AbstractGridCellRenderer;
import org.jamesii.gui.visualization.grid.Grid2D;
import org.jamesii.model.carules.ICARulesModel;
import org.jamesii.model.carules.symbolic.DefaultSymbolicCAModelInformation;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;
import org.jamesii.model.carules.symbolic.ISymbolicCAModelInformation;

/**
 * Simple editor for single dimension cellular automata (Wolfram's Rules).
 * 
 * @author Jan Himmelspach
 * @author Stefan Rybacki
 * 
 */
public class CAWolframRuleEditorWindow extends ModelWindow<ISymbolicCAModel> {

  /** Serial version UID. */
  private static final long serialVersionUID = -7548791836983824143L;

  private JPanel wrtPanel;

  private JPanel content;

  private CAWolframGridModel gridModel;

  private Grid2D grid;

  private JSpinner spnWolfram;

  private JLabel jLabel1;

  private int iWolframRule;

  /**
   * Constructor.
   * 
   * @param mod
   *          the internal CA model
   * @param mwManager
   *          the model window manager
   */
  public CAWolframRuleEditorWindow(ISymbolicCAModel mod,
      ISymbolicModelWindowManager mwManager) {
    super("CA (Wolfram's Rules) Model Editor: ", mod, mwManager);
  }

  /**
   * Initialize the user interface for creating or editing a CAModel.
   */
  protected void initUI() {
    content = new JPanel();
    content.setLayout(new BorderLayout());

    wrtPanel = new JPanel(new FlowLayout());

    gridModel = new CAWolframGridModel(getModel());
    grid = new Grid2D(gridModel);
    grid.setPreferredSize(new Dimension(399, 49));
    grid.setZoom(50);
    grid.setLinesVisible(true);
    grid.setForeground(Color.gray);
    grid.setCellRenderer(new AbstractGridCellRenderer() {
      private final String[] text = new String[] { "XXX", "XXO", "XOX", "XOO",
          "OXX", "OXO", "OOX", "OOO" };

      private final Color[] colors = new Color[] { Color.black,
          Color.lightGray.brighter() };

      @Override
      public void draw(Grid2D sender, Graphics g, int x, int y, int width,
          int height, Shape shape, int cellX, int cellY, Object value,
          boolean isSelected, boolean hasFocus) {
        int colorIndex = ((Integer) value).intValue();

        g.setColor(colors[colorIndex]);
        g.fillRect(x, y, width, height);

        // draw overlay
        int[] pos = { 0, width / 3, width * 2 / 3, width - 1 };
        String s = text[cellX];
        for (int i = 0; i < 3; i++) {
          g.setColor(colors[s.charAt(i) == 'O' ? 0 : 1]);
          g.fillRect(x + pos[i], y, pos[i + 1] - pos[i], pos[1]);
          g.setColor(Color.gray);
          g.drawRect(x + pos[i], y, pos[i + 1] - pos[i], pos[1]);
        }

        // draw focus overlay if necessary
        if (hasFocus) {
          g.setColor(new Color(0, 38, 255, 128));
          g.fillRect(x, y, width, height);
        }
      }
    });
    grid.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        Point cell = grid.getCellForCoordinates(e.getX(), e.getY());
        gridModel.setValueAt(cell.x, cell.y,
            1 - (Integer) gridModel.getValueAt(cell.x, cell.y));
        spnWolfram.setValue(gridModel.getIWolframRule());
      }
    });
    grid.setPanningAllowed(false);
    grid.setZoomingAllowed(false);

    spnWolfram = new javax.swing.JSpinner();
    jLabel1 = new javax.swing.JLabel();

    jLabel1.setText("Select Wolfram rule:");

    iWolframRule = gridModel.getIWolframRule();
    spnWolfram.setModel(new javax.swing.SpinnerNumberModel(iWolframRule, 0,
        255, 1));
    spnWolfram.addChangeListener(new javax.swing.event.ChangeListener() {
      @Override
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        spnWolframStateChanged(evt);
      }
    });

    wrtPanel.add(jLabel1);
    wrtPanel.add(spnWolfram);
    wrtPanel.add(grid);

    content.add(wrtPanel, BorderLayout.CENTER);

  }

  private void spnWolframStateChanged(@SuppressWarnings("unused")
  javax.swing.event.ChangeEvent evt) {
    iWolframRule = (Integer) spnWolfram.getValue();

    gridModel.setIWolframRule(iWolframRule);
  }

  protected void syncModel(ICARulesModel parsedModel) {
    getModel().setName(parsedModel.getName());
  }

  @Override
  public void modelChanged() {
    // TODO update grid model accordingly
  }

  @Override
  public void prepareModelSaving() {
    ISymbolicCAModelInformation ds = getModel().getAsDataStructure();

    DefaultSymbolicCAModelInformation mi =
        new DefaultSymbolicCAModelInformation(ds.getDimensions(),
            ds.getNeighborhood(), ds.getRules(), ds.getStates(),
            ds.isWolfram(), gridModel.getIWolframRule(), ds.getModelComment(),
            null);

    getModel().setFromDataStructure(mi);
  }

  @Override
  protected JComponent createContent() {
    initUI();
    return new JScrollPane(content);
  }

}