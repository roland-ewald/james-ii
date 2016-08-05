/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * MLSpace model factory placeholder
 *
 * @author Arne Bittig
 */
public class MLSpaceModelFactory extends ModelFactory {

  private static final long serialVersionUID = -2289938669250172228L;

  @Override
  public ISymbolicModel<?> create() {
    return null;
    // TODO!
  }

  @Override
  public Formalism getFormalism() {
    return new MLSpaceFormalism();
  }

  @Override
  public List<Class<? extends IModel>> getSupportedInterfaces() {
    ArrayList<Class<? extends IModel>> al = new ArrayList<>();
    al.add(MLSpaceModel.class);
    return al;
  }

}
