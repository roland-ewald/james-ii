/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.examples.ca.tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jamesii.model.cacore.neighborhood.NeumannNeighborhood;
import org.jamesii.model.carules.CARulesModel;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.reader.antlr.CAModelReaderFactory;

/**
 * A sample {@link CARulesModel}.
 * 
 * @author Roland Ewald
 */
public class BogusModel extends CARulesModel {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2072161310628045506L;

  /** The Constant S1. */
  private static final String S1 = "A";

  /** The Constant S2. */
  private static final String S2 = "B";

  /**
   * Instantiates a new bogus model.
   * 
   * @param parameters
   *          the parameters
   */
  public BogusModel(Map<String, Object> parameters) {
    super("Bogus Model", 1, Arrays.asList(CAModelReaderFactory
        .createSingleRule(S1, S2, new String[] { S1, S1 }, new String[] { S1,
            S2 }), CAModelReaderFactory.createSingleRule(S1, S1, new String[] {
        S1, S2 }, new String[] { S1, S2 }), CAModelReaderFactory
        .createSingleRule(S1, S1, new String[] { S2, S2 }, new String[] { S1,
            S2 }), CAModelReaderFactory.createSingleRule(S2, S1, new String[] {
        S1, S1 }, new String[] { S1, S2 }), CAModelReaderFactory
        .createSingleRule(S2, S1, new String[] { S1, S2 }, new String[] { S1,
            S2 }), CAModelReaderFactory.createSingleRule(S2, S1, new String[] {
        S2, S2 }, new String[] { S1, S2 })), Arrays.asList(S1, S2),
        new NeumannNeighborhood(1, "Well... it's a neighbourhood."),
        new ArrayList<ICACell>(), new int[] { 10 }, false);

    List<String> states = new ArrayList<>();
    states.add(S1);
    states.add(S2);
  }

}
