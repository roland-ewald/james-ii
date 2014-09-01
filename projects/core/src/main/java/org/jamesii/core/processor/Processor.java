/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.Entity;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.IContext;
import org.jamesii.core.model.AccessRestriction;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.Hook;

/**
 * The Class Processor.
 * 
 * Base and abstract implementation of the IProcessor interface. This class is
 * not an implementation of the {@link IRunnable} interface, and thus any
 * computation algorithm based on this class is "not executable" on its own.
 * However, you can easily implement this interface on your own in a descendant
 * class. <br/>
 * Remote communication issues have to be hidden from the basic simulation
 * algorithms: this gives the freedom to exchange the mechanism used later on,
 * and may allow to reuse the simulation algorithm on a broader variety of
 * hardware infrastructures.
 * 
 * @author Jan Himmmelspach
 */
public abstract class Processor<TimeBase extends Comparable<TimeBase>> extends
    Entity implements IProcessor<TimeBase>, IContext {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4211086838445451497L;

  /**
   * The list of sub contexts.
   */
  private List<IContext> childContexts;

  /**
   * This method returns a list of all model classes which are supported by this
   * processor.
   * 
   * @return the supported model classes
   */
  protected static List<Class<? extends IModel>> getSupportedModelClasses() {
    return new ArrayList<>();
  }

  /**
   * This method returns true if and only if the given model class can be
   * computed by using this processor.
   * 
   * @param model
   *          class of the model to be checked
   * 
   * @return true if the model class can be computed by using this processor
   */
  public static boolean isSupported(Class<? extends IModel> model) {
    List<Class<? extends IModel>> classes = getSupportedModelClasses();
    boolean result = false;
    for (int i = 0; i < classes.size(); i++) {
      if (classes.get(i) == model) {
        result = true;
      }
    }
    return result;
  }

  /**
   * This method returns true if and only if the given model can be computed by
   * using this processor.
   * 
   * @param model
   *          the model
   * 
   * @return true, if checks if is supported
   */
  public static boolean isSupported(IModel model) {
    return isSupported(model.getClass());
  }

  /**
   * This method returns a comma separated string of all supported model
   * classes.
   * 
   * @return the string
   */
  public static String supportedModels() {
    List<Class<? extends IModel>> classes = getSupportedModelClasses();
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < classes.size(); i++) {
      if (s.length() == 0) {
        s.append(classes.get(i).getName());
      } else {
        s.append(",");
        s.append(classes.get(i).getName());
      }
    }
    return s.toString();
  }

  /**
   * A reference to an access restriction object. This object will usually be
   * shared between a processor and the model(s) to be simulated.
   */
  private AccessRestriction accessRestriction;

  /** A reference to the (top most) model this processor shall execute. */
  private IModel model;

  /** The post next step hook. */
  private Hook<? extends Serializable> postNextStepHook = null;

  /** The pre next step hook. */
  private Hook<? extends Serializable> preNextStepHook = null;

  /** A reference to the computation task this processor belongs to. */
  private IComputationTask computationTask;

  /**
   * Each processor holds a state in which model execution related information
   * should be stored, i.e. information which must be present in the next
   * simulation step
   */
  private ProcessorState state;

  /**
   * models must be local.
   * 
   * @param model
   *          the model
   */
  public Processor(IModel model) {
    super();
    this.model = model;
  }

  /**
   * do next simulation step, to be called by "run method".
   */
  @Override
  public void executeNextStep() {
    if (preNextStepHook != null) {
      preNextStepHook.execute(null);
    }
    nextStep();
    if (postNextStepHook != null) {
      postNextStepHook.execute(null);
    }
  }

  @Override
  public void cleanUp() {
    computationTask = null;
  }

  @Override
  public String getClassName() {
    return getClass().getName();
  }

  @Override
  public String getCompleteInfoString() {
    return getClass().getName();
  }

  /**
   * Return the model associated with this processor. This maybe the top most
   * model of a hierarchical model or just a single model.
   * 
   * @param <M>
   *          the model type to be returned
   * 
   * @return IModel a reference to the (top most) model which is processed by
   *         this processor
   */
  @Override
  @SuppressWarnings("unchecked")
  public <M extends IModel> M getModel() {
    return (M) model;
  }

  /**
   * This method will return the name of the associated model, thereby handling
   * a RemoteException.
   * 
   * @return the name of the associated model
   */
  protected String getModelName() {
    String s = "";
    if (model != null) {
      s = model.getName();
    }
    return s;
  }

  /**
   * Gets the post next step hook.
   * 
   * @return the post next step hook
   */
  public Hook<? extends Serializable> getPostNextStepHook() {
    return preNextStepHook;
  }

  /**
   * Gets the pre next step hook.
   * 
   * @return the pre next step hook
   */
  public Hook<? extends Serializable> getPreNextStepHook() {
    return preNextStepHook;
  }

  /**
   * Returns the computation task object this processor runs in.
   * 
   * @return the computation task which is run by this processor
   */
  public IComputationTask getComputationTask() {
    return computationTask;
  }

  /**
   * Returns the starttime of the simulation.
   * 
   * @return 0 if no simulation has been set, otherwise get the start time from
   *         the set simulation
   */
  public double getStartTime() {
    if (computationTask != null) {
      if (computationTask instanceof ISimulationRun) {
        return ((ISimulationRun) computationTask).getStartTime();
      } else {
        return -1;
      }

    }
    return 0;
  }

  /**
   * Returns the state of the processor. In the state all processor attributes
   * should be included which are needed for restarting the processor at a new
   * location.
   * 
   * @return the processor state of this processor
   */
  @Override
  public ProcessorState getState() {
    return state;
  }

  /**
   * Gets the time.
   * 
   * @return the actual time of the simulation (at this processor)
   */
  @Override
  public abstract TimeBase getTime();

  /**
   * Install post next step hook.
   * 
   * @param hook
   *          the hook
   */
  public void installPostNextStepHook(Hook<? extends Serializable> hook) {
    postNextStepHook = hook;
  }

  /**
   * Install pre next step hook.
   * 
   * @param hook
   *          the hook
   */
  public void installPreNextStepHook(Hook<? extends Serializable> hook) {
    preNextStepHook = hook;
  }

  /**
   * The next step method executes the for parts of a computation step in a
   * fixed sequential order. If this granularity is not exact enough its upon a
   * developer of descendant processors to refine each of the methods. This
   * "way" of splitting up a method is called a template pattern (see Gamma et
   * alii).
   */
  protected abstract void nextStep();

  /**
   * Sets the model to be simulated by using this processor to the given IModel
   * model reference.
   * 
   * @param model
   *          the model
   */
  @Override
  public void setModel(IModel model) {
    this.model = model;
  }

  /**
   * Sets the simulation this processor belongs to.
   * 
   * @param simulation
   *          the simulation
   */
  @Override
  public void setComputationTask(IComputationTask simulation) {
    this.computationTask = simulation;
    setContext(simulation);
  }

  /**
   * Sets the passed state as new processor state, thereby replacing the old
   * one. A user of this method must be sure that this replacing does not harm
   * the model processing in any way.
   * 
   * @param state
   *          The state which shall be used as processor state from now own
   */
  public void setState(ProcessorState state) {
    this.state = state;
  }

  /**
   * @return the accessRestriction
   */
  protected final AccessRestriction getAccessRestriction() {
    return accessRestriction;
  }

  /**
   * @param accessRestriction
   *          the accessRestriction to set
   */
  protected final void setAccessRestriction(AccessRestriction accessRestriction) {
    this.accessRestriction = accessRestriction;
  }

  @Override
  public void setContext(IContext context) {
    this.computationTask = (IComputationTask) context;
  }

  @Override
  public IContext getContext() {
    return computationTask;
  }

  @Override
  public void registerContext(IContext context) {
    childContexts.add(context);
  }

  @Override
  public List<IContext> getChildContexts() {
    return childContexts;
  }

  @Override
  public <O> O create(String pluginType, ParameterBlock block) {    
    return Context.createInstance (pluginType, block, this);
  }
  
}
