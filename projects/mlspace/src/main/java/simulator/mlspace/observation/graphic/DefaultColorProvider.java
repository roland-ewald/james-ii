/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.spatial.SpatialEntity;

/**
 * @author Arne Bittig
 * @date 11.02.2014
 */
public class DefaultColorProvider
    implements IColorProvider<AbstractModelEntity> {

  /** Transparency values for all {@link SpatialEntity}s */
  private static final int COMP_ALPHA = 0xff;

  private static final int EIGHT_BITS = 0xff;

  private static final int R_SHIFT = 23;

  private static final int G_SHIFT = 13;

  private static final int B_SHIFT = 03;

  private static final int TOO_BRIGHT_SUM = 256;

  private static final int TOO_DARK_SUM = 128;

  private static final int CHANGE_BY = (TOO_BRIGHT_SUM - TOO_DARK_SUM) / 3;

  private static Color getColorForInt(int attribHash, int alpha) {
    Color color;
    int green = Integer.rotateLeft(attribHash, R_SHIFT) & EIGHT_BITS;
    int red = Integer.rotateLeft(attribHash, G_SHIFT) & EIGHT_BITS;
    int blue = Integer.rotateLeft(attribHash, B_SHIFT) & EIGHT_BITS;
    int rgbSum = green + red + blue;
    if (rgbSum > TOO_BRIGHT_SUM) {
      green = Math.max(0, green - CHANGE_BY);
      red = Math.max(0, red - CHANGE_BY);
      blue = Math.max(0, blue - CHANGE_BY);
    } else if (rgbSum < TOO_DARK_SUM) {
      green = Math.max(0, green + CHANGE_BY);
      red = Math.max(0, red + CHANGE_BY);
      blue = Math.max(0, blue + CHANGE_BY);
    }
    color = new Color(red, green, blue, alpha);
    return color;
  }

  /** Color object for each attribute hash code & alpha value combination */
  private final Map<Integer, Color> entColors = new LinkedHashMap<>();

  @Override
  public Color getColor(AbstractModelEntity comp) {
    final int prime = 31;
    int hash =
        comp.getAttributesHashCode() + prime * comp.getSpecies().hashCode();
    Color color = entColors.get(hash);
    if (color == null) {
      color = getColorForInt(hash, COMP_ALPHA);
      entColors.put(hash, color);
    }
    return color;
  }

}