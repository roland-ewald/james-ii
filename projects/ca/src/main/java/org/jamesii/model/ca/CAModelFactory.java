/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.model.ca.grid.ICAGrid;

@Plugin
public class CAModelFactory extends ModelFactory {

  private static final long serialVersionUID = -617787706630060482L;

  public CAModelFactory() {
    super();
  }

  @Override
  public ISymbolicModel<?> create() {
    return null;
  }

  @Override
  public Formalism getFormalism() {
    return new CA();
  }

  @Override
  public List<Class<? extends IModel>> getSupportedInterfaces() {
    ArrayList<Class<? extends IModel>> al =
        new ArrayList<>();
    al.add(ICAGrid.class);
    al.add(ICell.class);
    return al;
  }

}
