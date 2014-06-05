/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import java.util.logging.Level;

import org.jamesii.SimSystem;

import model.devscore.BasicCoupledModel;
import model.devscore.IBasicDEVSModel;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelChangeRequest.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class ModelChangeRequest extends ChangeRequest<IDynamicCoupledModel> {

  /** The add. */
  private boolean add;

  /** The context. */
  private IBasicDEVSModel context;

  /** The model. */
  private IBasicDEVSModel model;

  /** The model name. */
  private String modelName;

  /**
   * Creates a new model change request.
   * 
   * @param source
   *          the model requesting the change
   * @param model
   *          a pointer to the model to be added/removed
   * @param add
   *          if false the model will be removed otherwise it will be added
   */
  public ModelChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      boolean add) {
    super(source);
    this.model = model;
    this.modelName = model.getName();
    this.add = add;
    // by default the model will be added to / removed from the parent of the
    // sender model
    this.context = source.getParent();
  }

  /**
   * Creates a new model change request.
   * 
   * @param source
   *          the model requesting the change
   * @param model
   *          a pointer to the model to be added/removed
   * @param context
   *          a pointer to the model the model shall be added to/removed from
   * @param add
   *          if false the model will be removed otherwise it will be added
   */
  public ModelChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      IBasicDEVSModel context, boolean add) {
    super(source);
    this.model = model;
    this.modelName = model.getName();
    this.add = add;
    this.context = context;
  }

  /**
   * The Constructor.
   * 
   * @param source
   *          the source
   * @param modelName
   *          the model name
   * @param add
   *          the add
   * @param context
   *          the context
   */
  public ModelChangeRequest(IBasicDEVSModel source, IBasicDEVSModel context,
      String modelName, boolean add) {
    super(source);
    this.add = add;
    this.modelName = modelName;
    this.context = context;
  }

  /**
   * Gets the context.
   * 
   * @return the context
   */
  public IBasicDEVSModel getContext() {
    return context;
  }

  /**
   * Gets the model.
   * 
   * @return the model
   */
  public IBasicDEVSModel getModel() {
    return model;
  }

  /**
   * Get name of the model to be changed.
   * 
   * @return the model name
   */
  public String getModelName() {
    return modelName;
  }

  /**
   * Checks if is adds the request.
   * 
   * @return true, if is adds the request
   */
  public boolean isAddRequest() {
    return add;
  }

  /**
   * Applies the change request to the given model.
   * 
   * @param theModel
   *          the the model
   */
  @Override
  public void modifyModel(IDynamicCoupledModel theModel) {
    if (isAddRequest()) {
      theModel.addModel(getModel());
    } else {
      // remove the model from the coupled model it belongs to
      IBasicDEVSModel modelToBeRemoved = getModel();
      // Test whether the model has to be looked up
      if (theModel instanceof BasicCoupledModel) {
        if (modelToBeRemoved == null) {
          modelToBeRemoved = ((BasicCoupledModel) theModel).getModel(modelName);
        }
      } else {
        SimSystem
            .report(
                Level.WARNING,
                "Cannot remove the model as the model which shall contain the model to be removed is not a coupled devs model!");
      }

      theModel.removeCompleteModel(modelToBeRemoved);
      // a removed model has no parent!!!
      modelToBeRemoved.setParent(null);
    }

  }
}
