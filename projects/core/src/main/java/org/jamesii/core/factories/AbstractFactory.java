/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * The AbstractFactory class used in the plug-in mechanism.
 * 
 * This abstract factory uses FactoryCriterias for filtering the list of
 * available factories. The filtering can be supported by passing a special
 * parameter block. There has to be an abstract factory for all plug-in types
 * supported by the simulation system. Thereby the factories in the passed list
 * of factories create instances of the plug-ins. The factory actually used is
 * either determined by its name (manual selection) or by successively applying
 * so called filtering criteria. These criteria have to be developed per
 * AbstractFactory and must be added (e.g., in the constructor) to the list of
 * criteria (addCriteria).
 * 
 * @param <F>
 *          The type of factories to be maintained by this factory
 * 
 * @author Jan Himmelspach
 */
public abstract class AbstractFactory<F extends Factory<?>> extends Factory<F> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -5572211870012278536L;

  /** List of selection criteria. */
  private List<FactoryCriterion<F>> criteria = new ArrayList<>();

  /** List of factories which can be returned by this abstract factory. */
  private List<F> factories = new ArrayList<>();

  /**
   * Instantiates a new abstract factory.
   */
  public AbstractFactory() {
    super();
  }

  /**
   * Add an additional criteria for filtering.
   * 
   * @param fc
   *          the fc
   */
  public void addCriteria(FactoryCriterion<F> fc) {
    criteria.add(fc);
  }

  /**
   * Add a new factory of type F.
   * 
   * @param factory
   *          the factory
   */
  public void addFactory(F factory) {
    factories.add(factory);
  }

  /**
   * An abstract factory returns a factory to be used for creating the instances
   * to be used.
   * 
   * @param parameter
   *          parameter. May contain additional information used by one of the
   *          criteria for the selection of the factory to be returned
   * 
   * @return a factory for the creation of an instance of a certain object type
   */
  @Override
  public F create(ParameterBlock parameter) {

    List<F> facs = getFactoryList(parameter);

    // if no factory is left we raise an exception
    if (facs.isEmpty()) {
      throw new NoFactoryFoundException(
          "Was not able to determine a factory for the current setting!!!\n "
              + SimSystem.getExceptionTraceInformation());
    }

    // if (parameter.hasSubBlock(IReport.REPORT)) {
    // IReport report = parameter.getSubBlockValue(IReport.REPORT);
    // report.addEntry("Factory selected", facs.get(0), parameter);
    // }

    return facs.get(0);
  }

  /**
   * Overwrite this method for being able to select a factory directly by a
   * parameter (default: select by name).
   * 
   * Prints a hint if a concrete factory shall be used but could not be found
   * (this should never happen, because the default parameter parsing should
   * have said this before ;-) )
   * 
   * @param parameter
   *          The factory selection parameter block, The id has to be the value
   *          of the parameter block!!
   * 
   * @return a factory or null
   */
  protected F createFactoryDirectly(ParameterBlock parameter) {

    String id = null;
    try {
      id = ParameterBlocks.getValue(parameter);
    } catch (ClassCastException ex) {
      SimSystem.report(Level.SEVERE,
          ParameterBlocks.getValue(parameter) == null ? "null"
              : ParameterBlocks.getValue(parameter).toString(), ex);
    }

    if (id == null) {
      return null;
    }
    for (F f : factories) {
      if (f.getName() == null) {
        continue;
      }
      if (f.getName().compareTo(id) == 0) {
        return f;
      }
    }
    if (id.compareTo("") != 0) {
      SimSystem.report(Level.WARNING,
          "Was not able to find the given factory (" + id
              + "), trying to use another one.");
    }
    return null;
  }

  /**
   * An abstract factory returns a list of factories that can be used for
   * creating the needed instances (another factory might be in charge for the
   * final decision). If there is no factory left an exception is thrown.
   * 
   * @param parameter
   *          the parameter block which is passed to the filtering criteria
   * 
   * @return A list of factories which could be used
   */
  public List<F> getFactoryList(ParameterBlock parameter) {
    // first we try to get a factory directly (e.g. because of a direct
    // selection of a factory)
    F result = createFactoryDirectly(parameter);

    List<F> facs = new ArrayList<>();
    // we have not been able to fetch a factory according to the direct
    // selection
    // thus we'll have to search for another one
    if (result == null) {
      // make a copy of all available factories
      facs.addAll(factories);
    } else {
      // we have been able, add this one to the list (this enabling the
      // filtering process
      // for this factory -> maybe it does not fit
      facs.add(result);
    }

    // apply all filters, even if the parameter is null!
    for (FactoryCriterion<F> fc : criteria) {

      try {
        facs = fc.filter(facs, parameter);
      } catch (Exception e) {
        SimSystem.report(Level.WARNING, "Problem on filtering, filter " + fc + " is not applied : "
        + e.getClass().getName() + " - " + e.getMessage());
      }

      // as soon as no factory is left we raise an exception
      if (facs.isEmpty()) {
        String s = "null";
        if (parameter != null) {
          s = parameter.toString();
        }
        throw new NoFactoryFoundException(
            "Was not able to determine a factory for the current setting!!! ("
                + this.getClass() + ")\n" + "Latest criteria applied (" + fc
                + ")\n" + "The parameter(s) (" + s + ")");
      }

    }

    return facs;
  }

  /**
   * Gets a list of factories which can be returned by this abstract factory.
   * 
   * @return the factories
   */
  protected List<F> getFactories() {
    return Collections.unmodifiableList(factories);
  }
}
