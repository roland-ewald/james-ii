/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.formalism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.base.InformationObject;

/**
 * The Class Formalisms.
 */
public class Formalisms extends InformationObject {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2967682367251083538L;

  /** The formalisms. */
  private Map<String, Formalism> formalisms = new HashMap<>();

  /**
   * Instantiates a new formalisms.
   */
  public Formalisms() {
    super("");
  }

  /**
   * Adds the formalism.
   * 
   * @param formalism
   *          the formalism
   * @param newIdent
   *          the new ident
   */
  public void addFormalism(String newIdent, Formalism formalism) {
    formalisms.put(newIdent, formalism);
  }

  /**
   * Gets the formalism by acronym.
   * 
   * @param acronym
   *          the acronym
   * 
   * @return the formalism by acronym
   */
  public List<Formalism> getFormalismByAcronym(String acronym) {
    ArrayList<Formalism> result = new ArrayList<>();
    for (Formalism f : formalisms.values()) {
      if (f.getAcronym().compareTo(acronym) == 0) {
        result.add(f);
      }
    }
    return result;
  }

  /**
   * Gets the formalism by ident.
   * 
   * @param theIdent
   *          the the ident
   * 
   * @return the formalism by ident
   */
  public Formalism getFormalismByIdent(String theIdent) {
    return formalisms.get(theIdent);
  }

  /**
   * Gets the ident set.
   * 
   * @return the ident set
   */
  public Set<String> getIdentSet() {
    return formalisms.keySet();
  }

  @Override
  public String getInfo() {
    // TODO Auto-generated method stub
    return null;
  }

}
