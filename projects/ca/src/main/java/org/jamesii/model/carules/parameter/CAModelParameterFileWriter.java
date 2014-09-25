/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.parameter.IModelParameterWriter;
import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.grid.ICARulesGrid;

/**
 * The Class CAModelParameterFileWriter.
 * 
 * @author Jan Himmelspach
 */
class CAModelParameterFileWriter implements IModelParameterWriter {

  /**
   * Creates the state mapping.
   * 
   * @param o
   *          the o
   * @return list of states
   * 
   */
  private List<Integer> createStateMapping(ICARulesGrid o) {
    // get all different states
    List<ICACell> cellList = o.getCellList();
    List<Integer> stateList = new ArrayList<>();
    for (ICACell<?> cell : cellList) {
      if (!stateList.contains(cell.getState())) {
        stateList.add(cell.getState());
      }
    }

    return stateList;
  }

  @Override
  public void write(URI location, Object parameters) {
    if (!(parameters instanceof Map)) {
      return;
    }

    Map<?, ?> map = (Map<?, ?>) parameters;

    Object iS = map.get("initialState");
    if (!(iS instanceof ICARulesGrid)) {
      return;
    }

    ICARulesGrid grid = (ICARulesGrid) iS;
    // dimension
    int[] dimensions = grid.getSize();

    // TODO sr137: change return type to boolean or result set or similar
    if (dimensions.length != 2) {
      return;
    }

    File f = Files.getFileFromURI(location);
    try {
      BufferedWriter writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
              "UTF8"));

      try {

        for (int d = 0; d < dimensions.length; d++) {
          if (d != 0) {
            writer.write(" ");
          }
          writer.write(String.valueOf(dimensions[d]));
        }
        writer.write("\n");

        // states
        List<Integer> states = createStateMapping(grid);
        for (int p = 0; p < states.size(); p++) {
          if (p != 0) {
            writer.write("\t");
          }
          writer.write((char) (states.get(p) + 'a') + ";" + states.get(p));
        }
        writer.write("\n");

        // grid
        // TODO sr137: store grid

        for (int y = 0; y < dimensions[1]; y++) {
          for (int x = 0; x < dimensions[0]; x++) {
            writer.write(grid.getState(new int[] { x, y }) + 'a');
          }
          writer.write("\n");
        }
      } catch (IOException e) {
        SimSystem.report(e);
      } finally {
        BasicUtilities.close(writer);
      }

    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      SimSystem.report(e);
    }
  }
}
