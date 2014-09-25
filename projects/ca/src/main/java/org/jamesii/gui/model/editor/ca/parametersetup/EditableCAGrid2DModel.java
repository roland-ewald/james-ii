/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.parametersetup;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.gui.visualization.grid.AbstractGrid2DModel;
import org.jamesii.gui.visualization.grid.IEditableGrid2DModel;
import org.jamesii.model.carules.ICARulesModel;
import org.jamesii.model.carules.grid.ICARulesGrid;

/**
 * An editable model for the Grid2D which is based on data from an
 * {@link ICARulesModel}.
 * 
 * @author Johannes Rössel
 */
public class EditableCAGrid2DModel extends AbstractGrid2DModel implements
    IEditableGrid2DModel {

  /** Boundaries of the model's data. */
  private Rectangle bounds;

  /**
   * The explicitly set values in this model. Every point not in this map
   * defaults to the first state from the CA model.
   */
  private Map<Point, Integer> data;

  // private IGrid caGrid;

  /**
   * Constructor that creates this model from a given CA model.
   * 
   * @param caGrid
   *          The CA model.
   */
  public EditableCAGrid2DModel(ICARulesGrid caGrid) {
    super();
    data = new HashMap<>();
    int[] size = caGrid.getSize();
    setBounds(new Rectangle(0, 0, size[0], size.length > 1 ? size[1] : 1));

    Rectangle b = getBounds();
    for (int y = b.y; y < b.height; y++) {
      for (int x = b.x; x < b.width; x++) {
        setValueAt(x, y, caGrid.getState(new int[] { x, y }));
      }
    }
  }

  @Override
  public void setBounds(Rectangle newBounds) {
    bounds = newBounds;
    fireBoundsChanged();
  }

  @Override
  public void setValueAt(int x, int y, Object newValue) {
    // TODO: this check may be removed in future when the CA models allow for
    // other types than string
    if (!(newValue instanceof Integer)) {
      throw new IllegalArgumentException("Value must be an Integer");
    }
    // ignore values set outside the bounds
    if (x < bounds.x || x > bounds.x + bounds.width || y < bounds.y
        || y > bounds.y + bounds.height) {
      return;
    }
    data.put(new Point(x, y), (Integer) newValue);
    fireCellChanged(x, y);
  }

  @Override
  public Rectangle getBounds() {
    return bounds;
  }

  @Override
  public Object getValueAt(int x, int y) {
    Object retval = data.get(new Point(x, y));
    if (retval == null) {
      return 0;
    }
    return retval;
  }

  @Override
  public void clear() {
    data.clear();
  }
}
