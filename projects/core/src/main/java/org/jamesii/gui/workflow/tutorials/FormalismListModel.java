/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.tutorials;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.model.plugintype.AbstractModelFactory;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.gui.utils.AbstractComboBoxModel;

/**
 * Simple list model that provides access to all installed
 * {@link org.jamesii.core.model.formalism.Formalism}s.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class FormalismListModel extends AbstractComboBoxModel<ModelFactory> {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -9069456446245647260L;

  /**
   * Constructor
   */
  public FormalismListModel() {
    Registry registry = SimSystem.getRegistry();

    List<ModelFactory> factories = null;

    factories =
        registry.getFactoryOrEmptyList(AbstractModelFactory.class, null);

    Collections.sort(factories, new Comparator<ModelFactory>() {

      @Override
      public int compare(ModelFactory o1, ModelFactory o2) {
        if (o1 == null || o1.getFormalism().getName() == null) {
          return -1;
        }
        if (o2 == null || o2.getFormalism().getName() == null) {
          return 1;
        }
        return o1.getFormalism().getName()
            .compareTo(o2.getFormalism().getName());
      }
    });

    for (ModelFactory f : factories) {
      addElement(f);
    }
  }
}
