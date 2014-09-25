/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr;

import java.util.List;

import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.reader.antlr.parser.CAProblemToken;
import org.jamesii.model.carules.reader.antlr.parser.CaruleParser;
import org.jamesii.model.carules.reader.antlr.parser.CaruleParser.camodel_return;
import org.jamesii.model.carules.symbolic.ISymbolicCAModelInformation;

/**
 * {@link ISymbolicCAModelInformation} implementation for the CA language
 * implemented here {@link CaruleParser}.
 * 
 * @author Stefan Rybacki
 */
public class SymbolicCAModelInformation implements ISymbolicCAModelInformation {
  /**
   * The parsed info.
   */
  private camodel_return info;

  /**
   * The model source.
   */
  private String source;

  /**
   * Instantiates a new symbolic model information object for CA.
   * 
   * @param info
   *          the antlr parsing infos
   */
  public SymbolicCAModelInformation(camodel_return info, String source) {
    this.info = info;
    this.source = source;
  }

  @Override
  public int getDimensions() {
    return info.dimensions;
  }

  @Override
  public boolean isWolfram() {
    return info.isWolfram;
  }

  @Override
  public INeighborhood getNeighborhood() {
    return info.neighborhood;
  }

  /**
   * Gets the problems.
   * 
   * @return the problems
   */
  public List<CAProblemToken> getProblems() {
    return info.problems;
  }

  @Override
  public List<CARule> getRules() {
    return info.rules;
  }

  /**
   * Gets the version.
   * 
   * @return the version
   */
  public double getVersion() {
    return info.version;
  }

  @Override
  public int getWolframRule() {
    return info.wolframRule;
  }

  @Override
  public List<String> getStates() {
    return info.states;
  }

  @Override
  public String getModelComment() {
    return info.versionComment;
  }

  @Override
  public String getModelSource() {
    return source;
  }

}
