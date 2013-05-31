/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.plugintype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.algoselect.AlgorithmSelection;
import org.jamesii.core.algoselect.SelectionType;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.Model;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The AbstractProcessorFactory selects one out of the list of processor
 * factories. The selection is based on criteria which are 1) the model type to
 * be simulated 2) whether the simulation shall be executed in a distributed
 * manner last) the first factory in the sorted (efficiency index) list of
 * remaining factories Later these criteria may be extended or replaced (e.g. by
 * a knowledge based approach)
 * 
 * @author Jan Himmelspach
 */
@AlgorithmSelection(SelectionType.TREE)
public class AbstractProcessorFactory extends AbstractFactory<ProcessorFactory> {

  /**
   * the partition to be processed, type is {@link Partition}.
   */
  public static final String PARTITION = "partition";

  /**
   * Sorts the remaining factories according to their efficiency index.
   * 
   * @author Jan Himmelspach
   */
  private static class EfficiencySortingCriteria extends
      FactoryCriterion<ProcessorFactory> {

    @Override
    public List<ProcessorFactory> filter(List<ProcessorFactory> factories,
        ParameterBlock parameter) {
      Collections.sort(factories, new FacComp());
      return factories;
    }

    private static class FacComp implements Comparator<ProcessorFactory>,
        Serializable {
      /**
       * The constant serial version uid.
       */
      private static final long serialVersionUID = -5466350481628178270L;

      @Override
      public int compare(ProcessorFactory o1, ProcessorFactory o2) {
        return Double.valueOf(o2.getEfficencyIndex()).compareTo(
            o1.getEfficencyIndex());
      }
    }
  }

  /**
   * Criteria that filters factories that don't support the given model even
   * though the models interface is supported.
   */
  private static class SupportingModelCriteria extends
      FactoryCriterion<ProcessorFactory> {

    @Override
    public List<ProcessorFactory> filter(List<ProcessorFactory> factories,
        ParameterBlock parameter) {

      Partition partition = parameter.getSubBlockValue(PARTITION);

      // System.out.println("the part " + partition);
      // System.out.println("the model " + partition.getModel());

      // If model could not be instantiated, it cannot be simulated
      IModel model = partition.getModel();
      if (model == null) {
        SimSystem
            .report(Level.SEVERE,
                "The model is null! Selecting of a processor factory is not possible.");
        factories.clear();
        return factories;
      }

      for (int i = factories.size() - 1; i >= 0; i--) {
        if (!factories.get(i).supportsModel(model)) {
          factories.remove(i);
        }
      }

      return factories;
    }
  }

  /**
   * This criteria selects factories according to the formalisms they support.
   * Therefore the factories have to return a list of interfaces for which they
   * can create processors (e.g. for PDEVS this would have to be IAtomicModel
   * and ICoupledModel)
   * 
   * @author Jan Himmelspach
   */
  private static class FormalismCriteria extends
      FactoryCriterion<ProcessorFactory> {

    @Override
    public List<ProcessorFactory> filter(List<ProcessorFactory> factories,
        ParameterBlock parameter) {

      Partition partition = parameter.getSubBlockValue(PARTITION);

      // System.out.println("the part " + partition);
      // System.out.println("the model " + partition.getModel());

      // If model could not be instantiated, it cannot be simulated
      if (partition.getModel() == null) {
        SimSystem.report(Level.SEVERE, "ModelNull", "The model is null!");
        factories.clear();
        return factories;
      }

      // get the interfaces supported by this modeling formalism
      List<Class<?>> modelInterfaces =
          getMostSpecializedInterfaces(partition.getModel().getClass(),
              new ArrayList<Class<?>>());

      // iterate over all available factories
      Iterator<ProcessorFactory> iPF = factories.iterator();

      while (iPF.hasNext()) {
        ProcessorFactory pf = iPF.next();
        List<Class<?>> factoryInterfaces = pf.getSupportedInterfaces();
        if (factoryInterfaces == null) {
          SimSystem.report(Level.SEVERE,
              "Invalid plugin implementation! No supported interfaces are given! In: "
                  + pf);
          iPF.remove();
          continue;
        }
        boolean supports = false;
        for (Class<?> modelInterface : modelInterfaces) {

          // if the implemented interface is an IModel interface
          if (IModel.class.isAssignableFrom(modelInterface)) {
            for (Class<?> interfaceSupportedByFactory : factoryInterfaces) {
              if (interfaceSupportedByFactory == modelInterface) {

                // a supported interface has been found
                supports = true;
              }
            }
          }
        }
        // does not contain a supported interface, thus remove it
        if (!supports) {
          iPF.remove();
        }

      }

      return factories;
    }

    /**
     * Retrieves recursively all interfaces implemented by a model class 'cls'
     * or its super classes (except from Model or above). Only the most
     * specialized interfaces will be returned.
     * 
     * @param interfaces
     *          the interfaces
     * @param cls
     *          the cls
     * @return the most specialized interfaces
     */
    private List<Class<?>> getMostSpecializedInterfaces(Class<?> cls,
        List<Class<?>> interfaces) {

      Class<?>[] modelInterfaces = cls.getInterfaces();

      for (Class<?> modelInterface : modelInterfaces) {
        boolean isMostSpecialized = true;
        for (Class<?> i : interfaces) {
          if (modelInterface.isAssignableFrom(i)
              || modelInterface.isAssignableFrom(Model.class)) {
            isMostSpecialized = false;
            break;
          }
        }
        if (isMostSpecialized) {
          interfaces.add(modelInterface);
        }
      }

      if (cls != Model.class && cls != Object.class) {
        getMostSpecializedInterfaces(cls.getSuperclass(), interfaces);
      }

      return interfaces;
    }
  }

  /**
   * This criteria selects factories according to their support of sub
   * partitions.
   * 
   * @author Jan Himmelspach
   */
  private static class PartitionCriteria extends
      FactoryCriterion<ProcessorFactory> {

    @Override
    public List<ProcessorFactory> filter(List<ProcessorFactory> factories,
        ParameterBlock parameter) {
      // iterate over all available factories
      Iterator<ProcessorFactory> iPF = factories.iterator();

      Partition partition = parameter.getSubBlockValue(PARTITION);

      while (iPF.hasNext()) {
        ProcessorFactory pf = iPF.next();

        if ((partition.hasSubPartitions()) && (!pf.supportsSubPartitions())) {
          iPF.remove();
        }
      }
      return factories;
    }

  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5654244496658089932L;

  /**
   * Create a new instance of the AbstractProcessorFactory Adds several criteria
   * for filtering the list of available factories
   */
  public AbstractProcessorFactory() {
    super();

    // criterias used for filtering the list of available processor
    // factories
    addCriteria(new FormalismCriteria());
    addCriteria(new PartitionCriteria());
    addCriteria(new SupportingModelCriteria());
    // add further criterias here

    // add further criterias before the EfficencySortingCriteria!!!
    addCriteria(new EfficiencySortingCriteria());
  }

}
