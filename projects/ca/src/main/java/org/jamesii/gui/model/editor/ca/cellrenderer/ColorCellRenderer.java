/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ICAValueMapper;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.IMappingChangedListener;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ValueMapper;
import org.jamesii.gui.visualization.grid.AbstractGridCellRenderer;
import org.jamesii.gui.visualization.grid.Grid2D;

/**
 * A CA cell renderer that displays individual cells by simply filling them with
 * a solid color. The only input is said color.
 * 
 * @author Stefan Rybacki
 * @author Johannes Rössel
 */
public class ColorCellRenderer extends AbstractGridCellRenderer implements
    ICACellRenderer, IMappingChangedListener {
  /** The value mapper for converting CA states into colors. */
  private ICAValueMapper mapper = new ValueMapper(Color.class);

  /**
   * Private constructor to prevent unwanted instantiation.
   */
  public ColorCellRenderer() {
  }

  @Override
  public void draw(Grid2D sender, Graphics g, int x, int y, int width,
      int height, Shape shape, int cellX, int cellY, Object value,
      boolean isSelected, boolean hasFocus) {

    g.setColor(Color.BLACK);

    g.setColor((Color) mapper.getMappingFor(value));
    g.fillRect(x, y, width, height);

    if (hasFocus) {
      g.setColor(new Color(0x805492FF, true));
      g.fillRect(x, y, width, height);
    }
  }

  @Override
  public String getInputName(int inputIndex) {
    return "Color";
  }

  @Override
  public List<Class<?>> getInputs() {
    List<Class<?>> list = new ArrayList<>();
    list.add(Color.class);
    return list;
  }

  @Override
  public void setMapper(int inputIndex, ICAValueMapper m) {
    if (mapper != null) {
      mapper.removeMappingChangedListener(this);
    }
    mapper = m;
    mapper.addMappingChangedListener(this);
    fireRenderingChanged();
  }

  @Override
  public List<ICAValueMapper> getMappers() {
    return new ArrayList<ICAValueMapper>() {
      /** Serial Version ID. */
      private static final long serialVersionUID = 5665113288940530543L;
      {
        add(mapper);
      }
    };
  }

  @Override
  public void mappingChanged() {
    fireRenderingChanged();
  }
}
