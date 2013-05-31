/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.distributed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for the
 * 
 * {@link org.jamesii.core.simulation.distributed.model.AbstractREMOTEModelFactory}
 * and the
 * {@link org.jamesii.core.simulation.distributed.processor.AbstractREMOTEProcessorFactory}
 * classes.
 * 
 * For filtering, the parameter has to contain a sub block named
 * {@link AbstractREMOTEFactory#INTERFACES}.
 * 
 * @author Jan Himmelspach
 * @param <R>
 *          the remote factory
 * @param <L>
 *          the local object interface
 */
public abstract class AbstractREMOTEFactory<R extends RemoteFactory<?, L, ?>, L>
    extends AbstractFactory<R> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3537124771510740551L;

  /**
   * Interfaces that are supported, type: {@link ArrayList} of {@link Class}.
   */
  public static final String INTERFACES = "createdInterfaces";

  /**
   * This criteria selects factories according to their support of sub
   * partitions
   * 
   * @author Jan Himmelspach
   * @param <RR>
   * 
   */
  private class InterfaceCriteria<RR extends RemoteFactory<?, ?, ?>> extends
      FactoryCriterion<R> {

    @Override
    public List<R> filter(List<R> factories, ParameterBlock parameter) {
      // iterate over all available factories
      Iterator<R> iPF = factories.iterator();

      while (iPF.hasNext()) {
        RemoteFactory<?, L, ?> pf = iPF.next();
        List<Class<?>> al = parameter.getSubBlockValue(INTERFACES);
        List<Class<? extends L>> supInt = pf.getSupportedInterfaces(); // this
        // causes
        // an unchecked
        // error, seems
        // to be an
        // eclipse bug,
        // revisit once
        boolean ok = false;
        for (Class<?> c : al) {
          if (supInt.contains(c)) {
            ok = true;
          }
        }
        if (!ok) {
          iPF.remove();
        }
      }
      return factories;
    }

  }

  /**
   * Instantiates a new abstract remote factory.
   */
  public AbstractREMOTEFactory() {
    super();
    addCriteria(new InterfaceCriteria<R>());
  }

}
