/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import model.mlspace.entities.InitEntity;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.NSMReactionRule;
import model.mlspace.rules.NonTransferRule;
import model.mlspace.rules.RuleCollection;
import model.mlspace.rules.TimedReactionRule;
import model.mlspace.rules.TransferInRule;
import model.mlspace.rules.TransferOutRule;
import model.mlspace.subvols.Subvol;
import model.mlspace.subvols.SubvolInitializer;
import model.mlspace.subvols.SubvolUtils;
import model.mlspace.util.ShapeHierarchyUtils;

import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.model.Model;
import org.jamesii.core.observe.IInfoMapProvider;
import org.jamesii.core.util.hierarchy.Hierarchies;
import org.jamesii.core.util.hierarchy.IHierarchy;

/**
 * ML-Space model. Applicable to nsm-only, continuous-space-only, and hybrid
 * simulation
 * 
 * @author Arne Bittig
 * @date 06.01.2011
 */
public class MLSpaceModel extends Model implements IMLSpaceModel,
    IInfoMapProvider<Object> {

  /** Serialization ID */
  private static final long serialVersionUID = -3474252393471402377L;

  /** Subvols in the model (for NSM-only models) */
  private Collection<Subvol> subvols = null;

  /** Spatial entities in the model and their hierarchical organization */
  private IHierarchy<SpatialEntity> compTree = null;

  /** Subvols grouped by the spatial entities overlapping each */
  private Map<SpatialEntity, Collection<Subvol>> compSvMap = null;

  private final Map<SpatialEntity, ? extends Map<NSMEntity, Integer>> nsmEntMap;

  /** Model entity factory */
  private final ModelEntityFactory modEntFac;

  private RuleCollection rules;

  /**
   * Constructor for NSM-only model with subvolumes given
   * 
   * @param name
   *          Model name
   * @param subvols
   *          Subvolumes
   * @param modEntFac
   *          ModelEntityFactory
   */
  public MLSpaceModel(String name, Collection<Subvol> subvols,
      ModelEntityFactory modEntFac) {
    this(name, Hierarchies.<SpatialEntity> emptyHierarchy(), null, null,
        modEntFac);
    this.subvols = subvols;
  }

  /**
   * Constructor for continuous-only model or hybrid model with automatically
   * created subvolume structure (the latter if NSM-entities are present)
   * 
   * @param modelName
   *          Model name
   * @param compTree
   *          SpatialEntity hierarchy
   * @param rules
   *          Reaction & transfer rules
   * @param nsmEntMap
   *          NSM-entities in each compartment
   * @param modEntFac
   *          Model entity factory
   */
  public MLSpaceModel(String modelName, IHierarchy<SpatialEntity> compTree,
      RuleCollection rules,
      Map<SpatialEntity, ? extends Map<NSMEntity, Integer>> nsmEntMap,
      ModelEntityFactory modEntFac) {
    super(modelName);
    this.compTree = compTree;
    this.rules = rules != null ? rules : new RuleCollection();
    this.nsmEntMap = nsmEntMap;
    this.modEntFac = modEntFac;
  }

  @Override
  public final IVectorFactory getVectorFactory() {
    return modEntFac.getVectorFactory(); // vecFac;
  }

  @Override
  public final ModelEntityFactory getModelEntityFactory() {
    return modEntFac;
  }

  @Override
  public IHierarchy<SpatialEntity> getCompartments() {
    return compTree;
  }

  /**
   * For models created in Java, initialize comp tree manually (normally done in
   * constructor, but compartments may have been added later)
   */
  protected void setCompartments(Collection<SpatialEntity> comps) {
    compTree = ShapeHierarchyUtils.makeHierarchy(comps);
  }

  @Override
  public boolean initSubvolumes(IRandom rand, Double minSvSize, Double maxSvSize) {
    if (subvols != null || compSvMap != null) {
      if (compSvMap == null) {
        compSvMap = SubvolUtils.createCompSubvolMap(subvols);
      }
      assert nsmEntMap == null || nsmEntMap.isEmpty();
      return true; // already initialized
    }
    compSvMap =
        SubvolInitializer.constructSubvolGrid(compTree, nsmEntMap, minSvSize,
            maxSvSize, rand);
    assert !compSvMap.isEmpty();
    return true;
  }

  @Override
  public final Map<SpatialEntity, Collection<Subvol>> getCompSvMap() {
    return compSvMap;
  }

  @Override
  public Collection<Subvol> getSubvolumes() {
    if (subvols == null && compSvMap != null) {
      subvols = new ArrayList<>();
      for (Collection<Subvol> svs : compSvMap.values()) {
        subvols.addAll(svs);
      }
    }
    return subvols;
  }

  /**
   * Set subvolumes manually - USE WITH CAUTION; primarily intended for
   * java-based NSM/NCM-only models (i.e. those without moving compartments)
   * 
   * @param subvols
   */
  protected void setSubvolumes(Collection<Subvol> subvols) {
    assert compSvMap == null;
    this.subvols = subvols;
  }

  @Override
  public Collection<TimedReactionRule> getTimedReactionRules() {
    return rules.getTimedRules();
  }

  @Override
  public Collection<NSMReactionRule> getNSMReactionRules() {
    return rules.getNsmRules();
  }

  @Override
  public final Collection<CollisionReactionRule> getCollisionTriggeredRules() {
    return rules.getCollRules();
  }

  @Override
  public Collection<TransferInRule> getTransferInRules() {
    return rules.getTransInRules();
  }

  @Override
  public Collection<TransferOutRule> getTransferOutRules() {
    return rules.getTransOutRules();
  }

  /**
   * Setter for later rule addition in java-based models (i.e. direct subclasses
   * of this one)
   * 
   * @param rules
   *          Rule collection
   */
  protected void setRules(RuleCollection rules) {
    this.rules = rules;
  }

  /**
   * @return the modEntFac
   */
  public final ModelEntityFactory getModEntFac() {
    return modEntFac;
  }

  /** Map with information about the model (e.g. parameters->values) */
  private Map<String, Object> infoMap = null; // not initialized to cause NPE if
                                              // #getInfoIDs() is called before
                                              // #setInfo

  @Override
  public Map<String, Object> getInfoMap() {
    return infoMap;
  }

  /**
   * Set one piece of information returned by {@link #getInfoMap()}
   * 
   * @param id
   *          Identifier of the piece of information
   * @param val
   *          Value
   */
  public void setInfo(String id, Object val) {
    if (infoMap == null) {
      infoMap = new TreeMap<>();
    }
    infoMap.put(id, val);
  }

  @Override
  public Collection<String> getInfoIDs() {
    return infoMap.keySet(); // NPE-prone if called to early
  }

  @Override
  public String getCompleteInfoString() {
    return infoMap.toString() + " " + super.getCompleteInfoString();
  }

  @Override
  public String toString() {
    int numComps = compTree.getAllNodes().size();
    int numRules = rules.size();
    StringBuilder str = new StringBuilder();
    str.append(this.getName() == null ? "Unnamed" : this.getName());
    str.append(" (with initially ");
    str.append(numComps);
    str.append(" spatial ");
    str.append(numComps == 1 ? "entity" : "entities");
    if (subvols != null) {
      str.append(", ");
      str.append(subvols.size());
      str.append(subvols.size() == 1 ? " subvolume" : " subvolumes");
    }
    str.append(" and ");
    str.append(numRules);
    str.append(" rule");
    if (numRules != 1) {
      str.append("s");
    }
    str.append(')');
    return str.toString();
  }

  /**
   * Check whether the model has compartments that can move or react in any
   * other way. May be used to test whether the model can be simulated by the
   * Next-Subvolume-Method-with-fixed-compartments (NCM) (which is the case if
   * this method returns false and
   * {@link #initSubvolumes(IRandom, Double, Double)} returns true) NOTE: Checks
   * for all first-order reactions whether they apply to any compartment. If
   * this is the case, even an immobile compartment may become mobile during the
   * simulation and NCM is not applicable, i.e. this method returns false.
   * 
   * @param model
   *          ML-Space model to analyze
   * @return whether there are moving or (first-order) reacting compartments
   */
  public static boolean hasActiveCompartments(IMLSpaceModel model) {
    if (model.getCompartments() == null) { // NSM-only model
      return false;
    }
    for (SpatialEntity comp : model.getCompartments().getAllNodes()) {
      if (comp.getDiffusionConstant() != 0) {
        return true;
      }
      if (comp.getDrift() != null && !comp.getDrift().isNullVector()) {
        return true;
      }
    }
    return !model.getTimedReactionRules().isEmpty();
  }

  /**
   * Check whether model has explicit subvolumes or model entities that should
   * be placed in such (which may yet have to be generated via
   * {@link #initSubvolumes(IRandom, Double, Double)})
   * 
   * @param model
   *          ML-Space model to analyze
   * @return true if simulation of model involves {@link Subvol subvolumes}
   */
  public static boolean hasNSMPart(IMLSpaceModel model) {
    if (model.getSubvolumes() != null || model.getCompSvMap() != null) {
      return true; // subvols present
    }
    for (NonTransferRule rule : model.getNSMReactionRules()) {
      for (InitEntity prod : rule.getProduced()) {
        if (!model.getModelEntityFactory().isSpatial(prod)) {
          return true;
        }
      }
    }
    Map<?, ?> nsmEntMap = ((MLSpaceModel) model).nsmEntMap; // TODO
    return nsmEntMap != null && !nsmEntMap.isEmpty();
  }
}
