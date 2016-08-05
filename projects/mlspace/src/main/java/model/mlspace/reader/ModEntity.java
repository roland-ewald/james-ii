/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import model.mlspace.entities.AbstractEntity;
import model.mlspace.entities.Species;
import model.mlspace.entities.binding.RuleEntityWithBindings.BindingAction;

import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Container for modifications to an entity, i.e. items appearing on the right
 * hand side of a rule
 * 
 * @author Arne Bittig
 * @date 08.04.2014
 */
public class ModEntity extends AbstractEntity<ValueModifier> {

  private static final long serialVersionUID = 7302472587954440000L;

  private Map<String, BindingAction> bindMods = Collections.emptyMap();

  public ModEntity(String spec) {
    super(new Species(spec), new LinkedHashMap<String, ValueModifier>());
  }

  public Map<String, BindingAction> getBindMods() {
    return bindMods;
  }

  @Override
  public Map<String, ValueModifier> getAttributes() {
    return super.getAttributes();
  }

  public void addAttMod(String attName, ValueModifier mod) {
    ValueModifier oldVal = this.getAttributes().put(attName, mod);
    if (oldVal != null) {
      ApplicationLogger.log(Level.SEVERE, this
          + "had already a defined value for " + attName + ": " + oldVal);
    }
  }

  public void addBindMods(Map<String, BindingAction> ba) {
    this.bindMods = ba;
  }

  @Override
  public String toString() {
    return super.toString() + bindMods.toString();
  }

}
