/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.parametersetup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.model.editor.ca.cellrenderer.ColorCellRenderer;
import org.jamesii.gui.model.editor.ca.cellrenderer.ColorCellRendererFactory;
import org.jamesii.gui.model.editor.ca.cellrenderer.ICACellRenderer;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ValueMapper;
import org.jamesii.gui.model.parametersetup.plugintype.ModelParameterWindow;
import org.jamesii.gui.utils.SimpleFormLayout;
import org.jamesii.gui.visualization.grid.Grid2D;
import org.jamesii.gui.visualization.grid.IEditableGrid2DModel;
import org.jamesii.model.carules.grid.GridProvider;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;
import org.jamesii.model.carules.symbolic.ISymbolicCAModelInformation;

/**
 * Window for editing model parameters for cellular automata. Parameters are in
 * this case size of the grid and initial state.
 * 
 * @author Stefan Rybacki
 * @author Johannes Rössel
 */
public class CAModelParameterWindow extends
    ModelParameterWindow<ISymbolicCAModel> {

  private ICARulesGrid caGrid;

  private ISymbolicCAModelInformation modelInfo;

  private final class MouseAdapterExtension extends MouseAdapter {

    private CAModelParameterWindow window;

    public MouseAdapterExtension(CAModelParameterWindow window) {
      this.window = window;
    }

    private void setValue(MouseEvent e, Object value) {
      Rectangle bounds = window.gridModel.getBounds();
      Point coords = window.grid.getCellForCoordinates(e.getX(), e.getY());
      int x = coords.x, y = coords.y;
      if (x < bounds.x || x > bounds.x + bounds.width - 1) {
        return;
      }
      if (y < bounds.y || y > bounds.y + bounds.height - 1) {
        return;
      }
      if (value == null) {
        return;
      }
      window.gridModel.setValueAt(x, y, value);
    }

    private void flood(int x, int y, Object oldValue) {
      final Rectangle bounds = window.gridModel.getBounds();
      Deque<Point> stack = new ArrayDeque<>(bounds.width * bounds.height);

      stack.add(new Point(x, y));

      while (!stack.isEmpty()) {
        Point e = stack.pop();

        x = e.x;
        y = e.y;

        if (window.gridModel.getValueAt(x, y).equals(oldValue)) {
          window.gridModel.setValueAt(x, y, window.drawingState);

          if (x + 1 <= bounds.x + bounds.width - 1) {
            stack.add(new Point(x + 1, y));
          }
          if (y + 1 <= bounds.y + bounds.height - 1) {
            stack.add(new Point(x, y + 1));
          }
          if (x - 1 >= bounds.x) {
            stack.add(new Point(x - 1, y));
          }
          if (y - 1 >= bounds.y) {
            stack.add(new Point(x, y - 1));
          }
        }
      }

    }

    private void floodFill(MouseEvent e) {
      Rectangle bounds = window.gridModel.getBounds();
      Point coords = window.grid.getCellForCoordinates(e.getX(), e.getY());
      int x = coords.x, y = coords.y;
      if (x < bounds.x || x > bounds.x + bounds.width - 1) {
        return;
      }
      if (y < bounds.y || y > bounds.y + bounds.height - 1) {
        return;
      }
      if (window.gridModel.getValueAt(x, y).equals(window.drawingState)) {
        return;
      }

      flood(x, y, window.gridModel.getValueAt(x, y));
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        if (floodFill) {
          floodFill(e);
        } else {
          setValue(e, window.drawingState);
        }
      }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK) {
        setValue(e, window.drawingState);
      }
    }
  }

  /**
   * @param mod
   */
  public CAModelParameterWindow(ISymbolicCAModel mod) {
    super("Parameter Setup for Model " + mod.getName(), mod);
    modelInfo = mod.getAsDataStructure();
  }

  /** The editable model underlying the grid. */
  private IEditableGrid2DModel gridModel;

  /** The grid to display the model data and change it. */
  private Grid2D grid;

  /** The cell renderer, jointly used by the grid and state list. */
  private ICACellRenderer cellRenderer;

  /** List of available states */
  private JStateList stateList;

  /** State currently selected and used for drawing */
  private Integer drawingState = 0;

  private boolean floodFill = false;

  @Override
  protected JComponent createContent() {
    JComponent p = new JPanel();
    p.setLayout(new BorderLayout());

    JPanel p2 = new JPanel();
    p2.setLayout(new SimpleFormLayout(2, 3));

    Rectangle b = new Rectangle();
    b.x = b.y = 0;
    b.width = 20;
    b.height = 1;
    if (modelInfo.getDimensions() == 2) {
      b.height = 20;
    }

    // Use a numberOfStates, that should work with most models
    caGrid =
        GridProvider.createGrid(modelInfo.getDimensions(), new int[] { b.width,
            b.height }, 0, 15);
    // TODO I think we should directly work on the model here, because
    // currently
    // we have to do everything in two different data structures
    gridModel = new EditableCAGrid2DModel(caGrid);

    // grid
    grid = new Grid2D(gridModel);
    // gridModel.addGridCellListener(grid);
    p.add(grid, BorderLayout.CENTER);

    // state list, just for instantiation
    stateList = new JStateList(null, this.getModel());

    // cell renderer stuff
    setCellRenderer(new ColorCellRendererFactory().create(null, SimSystem.getRegistry().createContext()));

    // only two states usually indicate a simple dead/alive state
    // model
    // black and white are a safe bet for colors in that case
    if (cellRenderer instanceof ColorCellRenderer) {
      List<String> states = modelInfo.getStates();
      ValueMapper vm = new ValueMapper(Color.class);
      if (states.size() == 2) {
        vm.setMappingFor(0, Color.BLACK);
        vm.setMappingFor(1, Color.WHITE);
      } else {
        Random r = new Random();
        for (int i = 0; i < states.size(); i++) {
          vm.setMappingFor(i, new Color(r.nextInt()));
        }
      }
      cellRenderer.setMapper(0, vm);
    }

    cellRenderer.addRenderingChangedListener(grid);

    // width control
    JLabel lblWidth = new JLabel("Width: ");
    p2.add(lblWidth, SimpleFormLayout.FormConstraint.cellXY(0, 0));

    final JSpinner spnWidth = new JSpinner();
    spnWidth.setModel(new SpinnerNumberModel(gridModel.getBounds().width, 1,
        Integer.MAX_VALUE, 1));
    spnWidth.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        gridModel.setBounds(new Rectangle(0, 0, (Integer) spnWidth.getValue(),
            gridModel.getBounds().height));
      }
    });
    p2.add(spnWidth, SimpleFormLayout.FormConstraint.cellXY(1, 0,
        SimpleFormLayout.FormConstraint.CENTER,
        SimpleFormLayout.FormConstraint.HORIZONTAL));

    // height control
    JLabel lblHeight = new JLabel("Height: ");
    p2.add(lblHeight, SimpleFormLayout.FormConstraint.cellXY(0, 1));

    final JSpinner spnHeight = new JSpinner();

    // unbounded height, except for dimension = 1; can be hidden then,
    // anyway
    int maxH = Integer.MAX_VALUE;
    if (modelInfo.getDimensions() == 1) {
      maxH = 1;
      spnHeight.setVisible(false);
      lblHeight.setVisible(false);
    }
    spnHeight.setModel(new SpinnerNumberModel(gridModel.getBounds().height, 1,
        maxH, 1));
    spnHeight.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        gridModel.setBounds(new Rectangle(0, 0, gridModel.getBounds().width,
            (Integer) spnHeight.getValue()));
      }
    });
    p2.add(spnHeight, SimpleFormLayout.FormConstraint.cellXY(1, 1,
        SimpleFormLayout.FormConstraint.CENTER,
        SimpleFormLayout.FormConstraint.HORIZONTAL));

    // state list
    p2.add(new JScrollPane(stateList), SimpleFormLayout.FormConstraint.cellXY(
        0, 2, SimpleFormLayout.FormConstraint.CENTER,
        SimpleFormLayout.FormConstraint.BOTH, 2, 1));

    JToggleButton floodFillButton = new JToggleButton("Flood fill");
    floodFillButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        floodFill = e.getStateChange() == ItemEvent.SELECTED;
      }

    });
    JButton randomButton = new JButton("Fill randomly");
    randomButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Rectangle bounds = gridModel.getBounds();
        int states = getModel().getAsDataStructure().getStates().size();

        for (int x = 0; x < bounds.width; x++) {
          for (int y = 0; y < bounds.height; y++) {
            int value = (int) (Math.random() * states);
            gridModel.setValueAt(x - bounds.x, y - bounds.y, value);
          }
        }
      }

    });

    p2.add(randomButton, SimpleFormLayout.FormConstraint.cellXY(0, 3));
    p2.add(floodFillButton, SimpleFormLayout.FormConstraint.cellXY(1, 3));

    p.add(p2, BorderLayout.LINE_END);

    stateList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        drawingState = Math.max(stateList.getSelectedIndex(), 0);
      }
    });

    MouseAdapterExtension ma = new MouseAdapterExtension(this);
    grid.addMouseListener(ma);
    grid.addMouseMotionListener(ma);

    return p;
  }

  @Override
  protected IAction[] generateActions() {
    // TODO Auto-generated method stub
    return null;
  }

  private void setCellRenderer(ICACellRenderer cellRenderer) {
    this.cellRenderer = cellRenderer;
    grid.setCellRenderer(cellRenderer);
    stateList.setCACellRenderer(cellRenderer);
  }

  @Override
  public Map<String, ?> getParameters() {
    // copy from window.gridModel
    Rectangle bounds = gridModel.getBounds();
    caGrid.setSize(new int[] { bounds.width, bounds.height });
    for (int x = 0; x < bounds.width; x++) {
      for (int y = 0; y < bounds.height; y++) {
        caGrid.setState((Integer) gridModel.getValueAt(x, y),
            new int[] { x, y });
      }
    }

    Map<String, ICARulesGrid> result = new HashMap<>();
    result.put("initialState", caGrid);
    return result;
  }

  @Override
  public Object getUserParameters() {
    return cellRenderer;
  }

  @Override
  public void setParameters(Map<String, ?> parameters) {
    caGrid = (ICARulesGrid) parameters.get("initialState");
    if (caGrid != null) {
      gridModel = new EditableCAGrid2DModel(caGrid);
      grid.setModel(gridModel);
      grid.repaint();
    }
    // TODO Johannes: update spinners values as well
  }
}
