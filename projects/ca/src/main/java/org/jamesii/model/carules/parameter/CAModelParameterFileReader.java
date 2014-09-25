/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.parameter;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jamesii.core.data.model.parameter.IModelParameterReader;
import org.jamesii.core.util.misc.Files;
import org.jamesii.model.carules.grid.GridProvider;
import org.jamesii.model.carules.grid.ICARulesGrid;

/**
 * The Class CAModelParameterFileReader.
 * 
 * @author Jan Himmelspach
 */
class CAModelParameterFileReader implements IModelParameterReader {
  private final static int DEFAULT_NUMBER_OF_STATES=25;

  @Override
  public Map<String, ?> read(URI location) {
    String s = Files.readASCIIFile(Files.getFileFromURI(location), "UTF8");

    StringTokenizer st = new StringTokenizer(s, "\n");

    // first line dimension
    String dim = st.nextToken();

    StringTokenizer stDim = new StringTokenizer(dim, " \n");

    List<Integer> dimList = new ArrayList<>();
    int[] dimension;

    while (stDim.hasMoreTokens()) {
      dimList.add(Integer.valueOf(stDim.nextToken()));
    }

    if (dimList.size() != 2) {
      return null;
    }

    dimension = new int[dimList.size()];
    for (int i = 0; i < dimList.size(); i++) {
      dimension[i] = dimList.get(i);
    }

    // fetch the state list mapping
    HashMap<Character, Integer> states = new HashMap<>();

    String stateMappingLine = st.nextToken();
    StringTokenizer stSM = new StringTokenizer(stateMappingLine, "\t");
    while (stSM.hasMoreTokens()) {
      String stateMapping = stSM.nextToken();
      // should be something like char";"state number
      Character c = stateMapping.charAt(0);
      Integer i = Integer.valueOf(stateMapping.substring(2));
      states.put(c, i);
    }

    // Should be enough states for most models
    ICARulesGrid result =
        GridProvider.createGrid(dimension.length, dimension, 0, DEFAULT_NUMBER_OF_STATES);

    int y = -1;
    int x;

    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      y++;
      x = -1;
      for (int c = 0; c < line.length(); c++) {
        x++;
        Character ch = line.charAt(c);
        // if (!states.containsKey(ch)) {
        // states.put(ch, ++lastState);
        // }
        result.setState(states.get(ch), x, y);
      }
    }

    Map<String, Object> res = new HashMap<>();
    res.put("initialState", result);

    return res;

  }

}
