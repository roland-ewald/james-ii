/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore.dynamic;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.processor.util.BasicHandler;

import model.devscore.IBasicDEVSModel;
import model.devscore.dynamic.BasicCouplingChangeRequest;
import model.devscore.dynamic.ChangeRequest;
import model.devscore.dynamic.IDynamicAtomicModel;
import model.devscore.dynamic.InvalidChangeRequestException;
import model.devscore.dynamic.ModelChangeRequest;
import model.devscore.dynamic.PortChangeRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class DynamicStructureChangeHandler. Base class for all dynamic structure
 * change handlers (dynamic DEVS variants).
 * 
 * @author Jan Himmelspach
 */
public class DynamicStructureChangeHandler extends BasicHandler {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -6296294528424447127L;

  /**
   * If silentExpressions is true a structure change change exception will not
   * cause the simulation to stop - a user will not get aware of the exception
   * at all.
   */
  private boolean silentExceptions = false;

  /**
   * Instantiates a new dynamic structure change handler.
   */
  public DynamicStructureChangeHandler() {
    super();

  }

  /**
   * get structure change requests store them in bags for the models they have
   * to be realized by.
   * 
   * @param am
   *          the am
   * @param strucChangesPerModel
   *          the struc changes per model
   */
  protected void organizeStrucChanges(IDynamicAtomicModel am,
      Map<IBasicDEVSModel, List<ChangeRequest<?>>> strucChangesPerModel) {

    IBasicDEVSModel entry;

    ChangeRequest<?>[] amchgs = new ChangeRequest[1];

    amchgs = am.getChangeRequests().toArray(amchgs);

    // Object amchgs[] = am.REMOTEgetChangeRequests().toArray();

    // clear the change request list of the atomic model ...
    am.getChangeRequests().clear();

    // hash map with the models to be added
    HashMap<IBasicDEVSModel, ChangeRequest<?>> modelsToAdd =
        new HashMap<>();

    CompareChangeRequests comChgReq = new CompareChangeRequests();
    Arrays.sort(amchgs, comChgReq);

    // for speed up we buffer the model's to be added
    for (int i = 0; i < amchgs.length; i++) {

      if (amchgs[i] instanceof ModelChangeRequest) {
        if (((ModelChangeRequest) amchgs[i]).isAddRequest()) {
          modelsToAdd.put(((ModelChangeRequest) amchgs[i]).getModel(),
              amchgs[i]);
        } else {
          // add requests are the first requests, there is no need to search
          // behind the first non addRequest
          break;
        }
      } else {
        // change requests are already sorted we can break the search as soon as
        // we find a non Model/Port-ChangeRequest (because the
        // Model/Port-ChangeRequests are
        // the first ones in the list)
        break;
      }
    }

    // Iterator it2 = amchgs.iterator();
    for (int i = 0; i < amchgs.length; i++)
    /* while (it2.hasNext()) */{
      // ChangeRequest cr = (ChangeRequest) it2.next();
      ChangeRequest cr = amchgs[i];
      if (cr instanceof BasicCouplingChangeRequest) {

        entry = ((BasicCouplingChangeRequest) cr).getContext();

        // It may be that the context is a model that needs to be added before
        // (has to be looked up in the hash map)
        if (entry == null) {
          // try to fix the context

          // System.out.println("Need to fix the context!!");

          ModelChangeRequest cr2 =
              (ModelChangeRequest) modelsToAdd
                  .get(((BasicCouplingChangeRequest) cr).getModel1());

          if (cr2 != null) {
            entry = cr2.getContext();
          }

          if (entry == null) {

            throw new InvalidChangeRequestException(
                "Coupling change request from model ["
                    + ((BasicCouplingChangeRequest) cr).getModel1()
                        .getFullName()
                    + "] and port ["
                    + ((BasicCouplingChangeRequest) cr).getPort1().getName()
                    + "]. There is no valid context in which this change can take place");
          }
        }

        // System.out.println("CR in context "+entry.REMOTEgetFullName()+" type
        // "+cr.getClass().getName()+" \n "+cr.toString());

        List<ChangeRequest<?>> schg = strucChangesPerModel.get(entry);
        if (schg == null) {
          schg = new ArrayList<>();
          schg.add(cr);
          strucChangesPerModel.put(entry, schg);
        } else {
          schg.add(cr);
        }
      } else if (cr instanceof ModelChangeRequest) {
        entry = ((ModelChangeRequest) cr).getContext();

        if (entry == null) {
          throw new InvalidChangeRequestException("Model change request for "
              + ((ModelChangeRequest) cr).getModelName()
              + " But this model has no context model to be inserted in!!");
        }

        List<ChangeRequest<?>> schg = strucChangesPerModel.get(entry);
        if (schg == null) {
          schg = new ArrayList<>();
          schg.add(cr);
          strucChangesPerModel.put(entry, schg);
        } else {
          schg.add(cr);
        }
      } else if (cr instanceof PortChangeRequest) {

        // Test whether model already exists
        entry = ((PortChangeRequest) cr).getModel();

        // It may be that the model whose ports should be changed needs to be
        // added before (has to be looked up in the hash map)
        if (entry == null) {
          ModelChangeRequest cr2 =
              (ModelChangeRequest) modelsToAdd.get(((PortChangeRequest) cr)
                  .getModel());

          if (cr2 != null) {
            entry = cr2.getContext();
          }

          if (entry == null) {
            throw new InvalidChangeRequestException(
                "Port change request from model ["
                    + ((PortChangeRequest) cr).getSource().getFullName()
                    + "] for model ["
                    + ((PortChangeRequest) cr).getModel().getFullName()
                    + "] and port ["
                    + ((PortChangeRequest) cr).getPort().getName()
                    + "]. There is no valid context in which this change can take place");
          }
        }

        // Add change request to list
        List<ChangeRequest<?>> schg = strucChangesPerModel.get(entry);
        if (schg == null) {
          schg = new ArrayList<>();
          schg.add(cr);
          strucChangesPerModel.put(entry, schg);
        } else {
          schg.add(cr);
        }
      }
    }
  }

  /**
   * This method applied the given changerequest (cr) to the given model
   * (model).
   * 
   * @param cr
   *          the cr
   * @param model
   *          the model
   */
  protected <M extends IBasicDEVSModel> void processChange(ChangeRequest<M> cr, M model) {

    // apply change (change request takes care about this by themselve)
    if (!isSilentExceptions()) {
      cr.modifyModel(model);
    } else {
      // ignore whatever went wrong on executing a structual change
      try {
        cr.modifyModel(model);
      } catch (Exception e) {
        SimSystem.report (e);
      }
    }
  }

  /**
   * @return the silentExceptions
   */
  public boolean isSilentExceptions() {
    return silentExceptions;
  }

  /**
   * @param silentExceptions the silentExceptions to set
   */
  public void setSilentExceptions(boolean silentExceptions) {
    this.silentExceptions = silentExceptions;
  }

}
