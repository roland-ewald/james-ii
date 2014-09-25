/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.model.carules.symbolic.SymbolicCAModel;

/**
 * A factory for creating CA symbolic model objects.
 * 
 * @author Stefan Rybacki
 */
@Plugin(name = "CARulesModel", description = "Factory for CA models")
public class CAModelFactory extends ModelFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1605522512583763532L;

  @Override
  public ISymbolicModel<?> create() {
    SymbolicCAModel m = new SymbolicCAModel();
    m.setFromDocument(new CARulesAntlrDocument("@caversion 1;\r\n"
        + "wolframrule 0;"));
    return m;
  }

  @Override
  public Formalism getFormalism() {
    return new CA();
  }

  @Override
  public List<Class<? extends IModel>> getSupportedInterfaces() {
    ArrayList<Class<? extends IModel>> suppInterfaces =
        new ArrayList<>();
    suppInterfaces.add(ICARulesModel.class);
    return suppInterfaces;
  }
}
