/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.shapes.IModifiableShape;
import org.jamesii.core.math.geometry.shapes.ShapeCreator;
import org.jamesii.core.math.geometry.shapes.TorusSurface;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Periodic2DVectorFactory;
import org.jamesii.core.math.geometry.vectors.Periodic3DVectorFactory;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.collection.CollectionUtils;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Triple;

import model.mlspace.entities.binding.BindingSites;
import model.mlspace.entities.binding.IEntityWithBindings;
import model.mlspace.entities.binding.InitEntityWithBindings;
import model.mlspace.entities.spatial.Compartment;
import model.mlspace.entities.spatial.Region;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.entities.values.AbstractValueRange;
import model.mlspace.entities.values.VariableValue;

/**
 * Factory for classes implementing {@link AbstractModelEntity}, at the same
 * time container of species definitions. There should be one
 * {@link ModelEntityFactory} for each {@link model.mlspace.MLSpaceModel}.
 * 
 * The model entities should not be instantiated directly from an
 * {@link InitEntity} or {@link RuleEntity} as these need not contain definition
 * of all attribute values (i.e. some attributes may be omitted there, implying
 * that the value fixed in the species definition should be taken; e.g., all
 * entities of a certain species may have the same diffusion constant, and thus
 * the diffusion value need not be specified again when an entity of this
 * species occurs in rules). Also, one could allow attribute range definition
 * (via {@link AbstractValueRange}) in a species definition, {@link InitEntity}
 * or {@link RuleEntity} and require actual attribute values to be randomly
 * chosen from those if not defined explicitly, which is done here.
 * 
 * @author Arne Bittig
 */
public class ModelEntityFactory implements java.io.Serializable {

  private static final long serialVersionUID = -2518755604936150518L;

  /**
   * Name of compartment entity's attribute whose presence indicates that the
   * position of the comp has to be determined separately
   */
  public static final String WRONG_POS_MARKER = "_wrong_position_";

  /** aspect ratio attribute identifier TODO: to {@link SpatialAttribute}?! */
  public static final String ASPECT_RATIO = "aspectratio";

  /** relative position attribute identifier (converted here to absolute pos) */
  public static final String REL_POSITION = "relativeposition";

  private final Map<String, Species> speciesNameMap = new LinkedHashMap<>();

  private final Map<Species, Map<String, AbstractValueRange<?>>> speciesAttRangeMap =
      new LinkedHashMap<>();

  private final Map<Species, BindingSites> specBindingsMap =
      new LinkedHashMap<>();

  /**
   * Vector factory to use when creating spatial entities (more precisely, their
   * shapes and positions)
   */
  private IVectorFactory vecFac;

  private boolean perBoundsLateInit = false;

  /** Random number generator to use (to draw attribute values from ranges) */
  private final IRandom rand;

  /**
   * Flag whether last entity creation required some random attribute value
   * determination
   */
  private Boolean rngUsedInLastCreation = null;

  /**
   * Model entity factory (and species definition container) constructor
   * 
   * @param rand
   *          Random number generator (e.g. for attribute determination)
   * @param vecFac
   *          Vector factory (e.g. for positions of spatial entities)
   */
  public ModelEntityFactory(IRandom rand, IVectorFactory vecFac) {
    this.rand = rand;
    this.vecFac = vecFac;
  }

  /**
   * @return Vector factory used for spatial entities
   */
  public IVectorFactory getVectorFactory() {
    return vecFac;
  }

  /**
   * Lazy vector factory initialization
   * 
   * @param vecFac
   *          Vector factory
   */
  public void setVectorFactory(IVectorFactory vecFac) {
    if (this.vecFac != null) {
      throw new IllegalStateException("Vector factory has already been set");
    }
    this.vecFac = vecFac;
  }

  /**
   * Set flag for initializing vector factory with periodic boundaries according
   * to extension of first entity
   * 
   * @throws IllegalStateException
   *           if vector factory has already been set
   */
  public void setPeriodicBoundaries() {
    if (this.vecFac != null) {
      throw new IllegalStateException("Vector factory has already been set");
    }
    this.perBoundsLateInit = true;
  }

  private static final Collection<String> perBoundsKeywords =
      Arrays.asList("torus", "torusSurface");

  private IVectorFactory getPeriodicVecFac(String shapeKW, double[] pos,
      Double size, double[] aR) {
    if (pos == null) {
      throw new IllegalArgumentException(
          "No position given for top level shape."
              + " Cannot determine whether system is 2D or 3D.");
    }

    if (!perBoundsLateInit && !perBoundsKeywords.contains(shapeKW)) {
      throw new IllegalStateException(
          "No periodic boundaries - vector factory should have been initialized earlier");
    }
    if (pos == null && aR == null) {
      throw new IllegalStateException(
          "No position or aspect ratio vector given for top-level shape (needed to tell 2D from 3D)");
    }
    if (aR == null) {
      if (pos.length == 2) {
        double ext = Math.sqrt(size) / 2.;
        return new Periodic2DVectorFactory(pos[0] - ext, pos[1] - ext,
            pos[0] + ext, pos[1] + ext);
      } else if (pos.length == 3) {
        double ext = Math.pow(size, 1. / 3.) / 2.;
        return new Periodic3DVectorFactory(pos[0] - ext, pos[1] - ext,
            pos[2] - ext, pos[0] + ext, pos[1] + ext, pos[2] + ext);
      } else {
        throw new IllegalArgumentException(
            "2D or 3D position vector expected for top-level shape");
      }
    }

    if (pos == null) {
      pos = new double[aR.length]; // all 0
    }
    if (pos.length != aR.length) {
      throw new IllegalArgumentException(
          "Different vector length for position and aspect ratio of top-level shape");
    }
    double aRFactor = aR[0];
    for (int i = aR.length - 1; i > 0; i--) {
      aRFactor *= aR[i];
    }
    aRFactor = size / aRFactor;
    if (pos.length == 2) {
      aRFactor = Math.sqrt(aRFactor) / 2.;
      return new Periodic2DVectorFactory(pos[0] - aRFactor * aR[0],
          pos[1] - aRFactor * aR[1], pos[0] + aRFactor * aR[0],
          pos[1] + aRFactor * aR[1]);
    } else if (pos.length == 3) {
      aRFactor = Math.pow(aRFactor, 1. / 3.) / 2.;
      return new Periodic3DVectorFactory(pos[0] - aRFactor * aR[0],
          pos[1] - aRFactor * aR[1], pos[2] - aRFactor * aR[2],
          pos[0] + aRFactor * aR[0], pos[1] + aRFactor * aR[1],
          pos[2] + aRFactor * aR[2]);
    } else {
      throw new IllegalArgumentException(
          "2D or 3D position vector expected for top-level shape");
    }

  }

  /**
   * @param name
   *          Name of the species
   * @param attRangeMap
   *          Attributes and their range
   * @param bindingSites
   */
  public void registerSpeciesDefinition(String name,
      Map<String, AbstractValueRange<?>> attRangeMap,
      BindingSites bindingSites) {
    if (speciesNameMap.containsKey(name)) {
      throw new IllegalArgumentException(
          "Species " + name + " already defined.");
    }
    Species species = new Species(name);
    speciesNameMap.put(name, species);

    speciesAttRangeMap.put(species, attRangeMap);
    if (bindingSites != null) {
      AbstractValueRange<?> boundAtt =
          attRangeMap.get(SpeciesLevelAttribute.BOUNDARIES.toString());
      if (boundAtt == null // NOSONAR: why should this be non-null always?
          && SpeciesLevelAttribute.HARD_BOUNDS_DEFAULT
          || boundAtt != null // NOSONAR:
              // is changed
              && boundAtt
                  .getRandomValue(null) == SpeciesLevelAttribute.HARD_BOUNDS) {
        specBindingsMap.put(species, bindingSites);
      } else {
        throw new IllegalArgumentException(
            "Regions (with soft " + "boundaries) must not have binding sites. "
                + "Illegal species definition for " + name + bindingSites);
      }
    }
  }

  /**
   * @return Number of species definitions
   */
  public int getNumberOfDefinedSpecies() {
    return speciesAttRangeMap.size();
  }

  /**
   * Get all contained data
   * 
   * @return Collection of (species,attributes,bindingSites) (copy)
   */
  public Collection<Triple<Species, Map<String, AbstractValueRange<?>>, BindingSites>> getFullSpeciesDefinitions() {
    Collection<Triple<Species, Map<String, AbstractValueRange<?>>, BindingSites>> rv =
        new ArrayList<>(speciesAttRangeMap.size());
    for (Map.Entry<Species, Map<String, AbstractValueRange<?>>> e : speciesAttRangeMap
        .entrySet()) {
      Species species = e.getKey();
      rv.add(new Triple<>(species, e.getValue(), specBindingsMap.get(species)));
    }
    return rv;
  }

  /**
   * @param specName
   *          Species name
   * @return {@link Species} instance
   */
  public Species getSpeciesForName(String specName) {
    return speciesNameMap.get(specName);
  }

  /**
   * @param specName
   *          Species name
   * @param attName
   *          Attribute name
   * @return Allowed range for given attribute of given species
   */
  public AbstractValueRange<?> getAttRange(String specName, String attName) {
    Species spec = getSpeciesForName(specName);
    if (spec == null) {
      throw new IllegalArgumentException("No species with name " + specName);
    }
    return speciesAttRangeMap.get(spec).get(attName);
  }

  /**
   * 
   * @param specName
   *          Species name
   * @return Names of defined attribute (mostly for information/debugging)
   */
  public Set<String> getAttNames(String specName) {
    Species spec = getSpeciesForName(specName);
    if (spec == null) {
      throw new IllegalArgumentException("No species with name " + specName);
    }
    return speciesAttRangeMap.get(spec).keySet();
  }

  /**
   * @param specName
   *          Species name
   * @return Binding sites definition for species
   */
  public BindingSites getBindingSites(Species specName) {
    return specBindingsMap.get(specName);
  }

  /**
   * @param ent
   *          Entity definition
   * @return true if spatial attributes (shape, size,...) are fixed for all
   *         future entities of the respective species
   */
  public boolean isSpatial(AbstractEntity<?> ent) {
    if (ent instanceof IEntityWithBindings<?>
        && ((IEntityWithBindings<?>) ent).hasBoundEntities()) {
      return true;
    }
    Map<String, AbstractValueRange<?>> defAtts =
        speciesAttRangeMap.get(ent.getSpecies());
    // String shapeAttName = SpatialAttribute.SHAPE.toString();
    // if (!ent.hasAttribute(shapeAttName) &&
    // !defAtts.containsKey(shapeAttName)) {
    // return false;
    // }
    String sizeAttName = SpatialAttribute.SIZE.toString();
    if (ent.hasAttribute(sizeAttName)) {
      return checkValidSize(ent.getAttribute(sizeAttName));
    }
    return checkValidSize(defAtts.get(sizeAttName));
  }

  private boolean checkValidSize(Object obj) {
    if (obj == null) {
      return false;
    }
    Object size = obj;
    if (obj instanceof Pair<?, ?>) {
      size = ((Pair) obj).getFirstValue();
    }
    if (size instanceof Number) {
      double value = ((Number) size).doubleValue();
      if (value < 0) {
        ApplicationLogger.log(Level.SEVERE,
            "Negative size attribute value: " + size);
      }
      return value > 0.;
    }
    return checkValidSizeRange(size);
  }

  private boolean checkValidSizeRange(Object size) {
    if (size instanceof AbstractValueRange<?>) {
      AbstractValueRange<?> range = (AbstractValueRange<?>) size;
      try {
        for (Object val : range) {
          if (!checkValidSize(val)) {
            return false;
          }
          return true;
        }
      } catch (IllegalStateException ex) {
        // value intervals do not support iteration
        if (range.size() == 1) {
          return checkValidSize(range.iterator().next());
        }
        final int checkAttempts = 10; // TODO: parameterize
        for (int i = 0; i < checkAttempts; i++) {
          if (!checkValidSize(range.getRandomValue(rand))) {
            return false;
          }
        }
        return true;
      }
    }
    ApplicationLogger.log(Level.SEVERE,
        "Size attribute value of invalid type: " + size);
    return false;
  }

  private static <T> Map<String, T> joinAttributeMaps(
      Map<String, ? extends T> am1, Map<String, ? extends T> am2) {
    Map<String, T> joinedAttributes = new LinkedHashMap<>(am1);
    joinedAttributes.putAll(am2);
    return joinedAttributes;
  }

  /**
   * Check whether internal random number generator was used in the creation of
   * the last entity (i.e. last call to
   * {@link #createNSMEntity(InitEntity, Map)} or
   * {@link #createSpatialEntities(InitEntity, int, SpatialEntity, Map)}), which
   * is means some attribute values of the returned {@link AbstractModelEntity}
   * were not given precisely (only a range was known) and determined randomly.
   * 
   * @return true if last created entity had randomly determined attributes
   * @throws NullPointerException
   *           if none of the create...Entity methods has been called yet
   */
  public boolean rngUsedInLastCreation() {
    return rngUsedInLastCreation;
  }

  /**
   * Create {@link NSMEntity} from given {@link InitEntity} or
   * {@link RuleEntity} and registered species definition. Determines attribute
   * values randomly if a range is given (see also
   * {@link #rngUsedInLastCreation()}). Does not check whether shape and size
   * are defined (in which case rather a spatial entity should be created using
   * {@link #createSpatialEntities(InitEntity, int, SpatialEntity, Map)}).
   * 
   * @param ent
   *          Entity definition
   * @param variables
   * @return Entity usable in simulation
   */
  public NSMEntity createNSMEntity(InitEntity ent,
      Map<String, Object> variables) {
    Map<String, Object> attributes = createAttributeValueMap(ent, variables);
    handleNSMSizeAttribute(ent, attributes);
    return new NSMEntity(ent.getSpecies(), attributes);
  }

  /**
   * Create {@link NSMEntity}s from given {@link InitEntity} or
   * {@link RuleEntity}, registered species definition and given amount.
   * Determines attribute values randomly if a range is given (see also
   * {@link #rngUsedInLastCreation()}). Does not check whether shape and size
   * are defined (in which case rather a spatial entity should be created using
   * {@link #createSpatialEntities(InitEntity, int, SpatialEntity, Map)}).
   * 
   * TODO: rather inefficient for large amounts so far
   * 
   * @param ent
   *          Entity definition
   * @param amount
   *          Amount
   * @param parent
   *          Surrounding spatial entity IF position attribute is allowed (null
   *          if not)
   * @return Entities usable in simulation (as state vector)
   */
  public Map<NSMEntity, Integer> createNSMEntities(InitEntity ent,
      Integer amount, SpatialEntity parent) {
    IUpdateableMap<Map<String, Object>, Integer> attMap =
        new UpdateableAmountMap<>();
    for (int i = 0; i < amount; i++) {
      Map<String, Object> attributeValueMap =
          createAttributeValueMap(ent, Collections.EMPTY_MAP);
      handleNSMPositionAttribute(ent, attributeValueMap, parent);
      handleNSMSizeAttribute(ent, attributeValueMap);
      attMap.add(attributeValueMap);
    }
    Map<NSMEntity, Integer> rv = new LinkedHashMap<>(attMap.size());
    for (Map.Entry<Map<String, Object>, Integer> e : attMap.entrySet()) {
      rv.put(new NSMEntity(ent.getSpecies(), e.getKey()), e.getValue());
    }
    return rv;
  }

  private void handleNSMPositionAttribute(AbstractEntity<?> ent,
      Map<String, Object> attributeValueMap, SpatialEntity parent) {
    if (!attributeValueMap.containsKey(SpatialAttribute.POSITION.toString())
        && !attributeValueMap.containsKey(REL_POSITION)) {
      return;
    }
    if (parent == null) {
      throw new IllegalStateException(
          ent + "'s position attribute not allowed at this point!");
    }
    IPositionVector position = getEntityPosition(attributeValueMap, parent);
    attributeValueMap.remove(REL_POSITION);
    attributeValueMap.put(SpatialAttribute.POSITION.toString(), position);
  }

  private static void handleNSMSizeAttribute(AbstractEntity<?> ent,
      Map<String, Object> attributeValueMap) {
    if (attributeValueMap.containsKey(SpatialAttribute.SIZE.toString())) {
      Object size = attributeValueMap.get(SpatialAttribute.SIZE.toString());
      if (!(size instanceof Number && ((Number) size).doubleValue() == 0.)) {
        throw new IllegalStateException(
            "NSM entity creation" + " requested but size not 0 for " + ent);
      }
      attributeValueMap.remove(SpatialAttribute.SIZE.toString());
      // workaround to allow specification of size and shape in model,
      // but override size with value 0 to use formerly cont.-space
      // model with NSM entities
      attributeValueMap.remove(SpatialAttribute.SHAPE.toString());
    }
  }

  /**
   * Create {@link SpatialEntity spatial entities} from given {@link InitEntity}
   * or {@link RuleEntity} and registered species definition. Determines
   * attribute values randomly if a range is given (see also
   * {@link #rngUsedInLastCreation()}). Returns null if required spatial
   * attributes are undefined. If the position attribute (not required) is
   * undefined, it is set to the null vector and the attribute
   * {@link #WRONG_POS_MARKER} is set to indicate that correct positioning /
   * placement must be handled elsewhere.
   * 
   * @param pEnt
   *          Entity definition
   * @param amount
   *          Amount of said entity to produce
   * @param parent
   *          Surrounding spatial entity (null for none;
   *          {@link SpatialEntity#setEnclosingEntity(SpatialEntity)} is called
   *          on produced entities)
   * @param variables
   * @return SpatialEntity usable in simulation, null if info missing
   */
  public List<SpatialEntity> createSpatialEntities(InitEntity pEnt, int amount,
      SpatialEntity parent, Map<String, Object> variables) {
    List<SpatialEntity> rv = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      SpatialEntity ent = createSpatialEntity(pEnt, parent, variables);
      rv.add(ent);
      if (pEnt instanceof InitEntityWithBindings) {
        createBindings(ent, (InitEntityWithBindings) pEnt, parent, rv,
            variables);
      }
    }
    return rv;
  }

  private void createBindings(SpatialEntity ent, InitEntityWithBindings iEnt,
      SpatialEntity parent, List<SpatialEntity> rv,
      Map<String, Object> variables) {
    if (iEnt.bindingEntries().size() != 1) {
      throw new IllegalStateException("Cannot yet deal with "
          + "more than one bound entity of " + iEnt + iEnt.bindingEntries());
    }
    // if (!(ent instanceof Compartment)) {
    // throw new IllegalStateException(ent +
    // " is a region, but should bind something");
    // }
    Compartment cEnt = (Compartment) ent;
    for (Map.Entry<String, ? extends IEntityWithBindings<?>> e : iEnt
        .bindingEntries().entrySet()) {
      Compartment bEnt =
          (Compartment) createSpatialEntity((InitEntity) e.getValue(), parent,
              variables);
      cEnt.bind(e.getKey(), bEnt);
      bEnt.bind(e.getValue().bindingEntries().keySet().iterator().next(), cEnt);
      rv.add(bEnt);
    }
  }

  private SpatialEntity createSpatialEntity(InitEntity pEnt,
      SpatialEntity parent, Map<String, Object> variables) {
    Map<String, Object> attributes = createAttributeValueMap(pEnt, variables);
    Species species = pEnt.getSpecies();
    boolean[] isSpatialWithPosition = checkSpatialityAndPosition(attributes);
    if (!isSpatialWithPosition[0]) {
      throw new IllegalArgumentException("Spatial attributes missing in "
          + attributes + " for " + pEnt + " in " + parent);
    }
    IModifiableShape shape =
        createEntityShape(parent, attributes, isSpatialWithPosition);
    handleDriftAttribute(attributes);
    // handle species-level attributes
    SpatialEntity spEnt;
    if (isHardBound(
        attributes.remove(SpeciesLevelAttribute.BOUNDARIES.toString()))) {
      spEnt = new Compartment(species, shape, attributes,
          specBindingsMap.get(species), null);
    } else {
      spEnt = new Region(shape, species, attributes, null);
    }
    spEnt.setEnclosingEntity(parent);
    return spEnt;
  }

  /**
   * 
   * @param attributes
   *          Attribute map to extract position from
   * @param parent
   *          Surrounding entity in case attributes contains only relative
   *          coordinates
   * @return
   */
  private IPositionVector getEntityPosition(Map<String, Object> attributes,
      SpatialEntity parent) {

    double[] pos =
        (double[]) attributes.remove(SpatialAttribute.POSITION.toString());
    if (pos != null) {
      if (vecFac == null) {
        vecFac = new AVectorFactory(pos.length);
      }
      return vecFac.newPositionVector(pos);
    } else {
      pos = (double[]) attributes.remove(REL_POSITION);
      if (pos == null) {
        throw new IllegalStateException("position actually not defined for "
            + attributes + " in " + parent);
      }
      if (parent == null) {
        ApplicationLogger.log(Level.SEVERE,
            "No absolute coordinates given, but no surrounding entity either: "
                + attributes + ". Treating coordinates as relative to origin.");
        return vecFac.newPositionVector(pos);
      } else {
        IDisplacementVector disp = vecFac.newDisplacementVector(pos);
        return parent.getPosition().plus(disp);
      }
    }
  }

  /**
   * Check whether value of {@link SpeciesLevelAttribute#BOUNDARIES} attribute
   * indicates hard boundaries or not
   * 
   * CHECK: remove and use species-centric method?
   * 
   * @param bound
   *          attribute value
   * @return true bound indicates hard boundaries (default value from
   *         {@link SpeciesLevelAttribute} if null)
   */
  private static boolean isHardBound(Object bound) {
    if (bound == null) {
      return SpeciesLevelAttribute.HARD_BOUNDS_DEFAULT;
    }
    if (!(bound instanceof String)
        || !SpeciesLevelAttribute.BOUNDARIES.isValidValue((String) bound)) {
      throw new IllegalArgumentException("Illegal value for attribute "
          + SpeciesLevelAttribute.BOUNDARIES.toString() + ": " + bound);
    }
    return SpeciesLevelAttribute.HARD_BOUNDS.equalsIgnoreCase((String) bound);
  }

  private IModifiableShape createEntityShape(SpatialEntity parent,
      Map<String, Object> attributes, boolean[] isSpatialWithPosition) {
    // get spatial attributes from map
    IPositionVector center;
    if (isSpatialWithPosition[1]) {
      center = getEntityPosition(attributes, parent);
    } else if (vecFac == null) {
      center = null; // top level shape, lazy per bound vecFac init
    } else {
      center = vecFac.origin();
      attributes.put(WRONG_POS_MARKER, true);
    }
    double[] aR = (double[]) attributes.remove(ASPECT_RATIO);

    String shapeKW =
        ((String) attributes.remove(SpatialAttribute.SHAPE.toString()))
            .toLowerCase();
    Double size = (Double) attributes.remove(SpatialAttribute.SIZE.toString());

    if (vecFac == null) {
      assert center == null;
      this.vecFac = getPeriodicVecFac(shapeKW,
          (double[]) attributes.remove(SpatialAttribute.POSITION.toString()),
          size, aR);
    }

    IDisplacementVector aspectRatio =
        aR != null ? vecFac.newDisplacementVector(aR) : null;
    IModifiableShape shape =
        createShape(shapeKW, center, size, aspectRatio, vecFac);
    // attributes.remove(SpatialAttribute.SHAPE.toString());
    // attributes.remove(SpatialAttribute.SIZE.toString());
    attributes.remove(ASPECT_RATIO);
    return shape;
  }

  /**
   * Drift attribute may be specified directly or in two parts: velocity and
   * direction
   * 
   * @param attributes
   */
  private void handleDriftAttribute(Map<String, Object> attributes) {
    double[] drift =
        (double[]) attributes.remove(SpatialAttribute.DRIFT.toString());
    Object velocity = attributes.remove(SpatialAttribute.VELOCITY.toString());
    if (drift != null) {
      IDisplacementVector driftVec = vecFac.newDisplacementVector(drift);
      if (velocity != null) {
        double driftLength = Vectors.vecNormEuclid(drift);
        if (driftLength > 0.) {
          driftVec.scale((double) velocity / driftLength);
        } else if ((double) velocity != 0.) {
          ApplicationLogger.log(Level.SEVERE,
              "Velocity value " + velocity
                  + " value ignored in favor of drift value"
                  + Arrays.toString(drift));
        }
      }
      if (attributes.remove(SpatialAttribute.DIRECTION.toString()) != null) {
        ApplicationLogger.log(Level.SEVERE,
            "Direction attribute value ignored in favor of drift value"
                + Arrays.toString(drift));
      }
      attributes.put(SpatialAttribute.DRIFT.toString(), driftVec);
    } else {
      Object vel = velocity;
      Object dir = attributes.remove(SpatialAttribute.DIRECTION.toString());
      if (vel == null != (dir == null)) {
        ApplicationLogger.log(Level.SEVERE,
            "Velocity & direction attribute must be defined together, but were "
                + vel + " & " + dir + ". Drift ignored.");
      } else if (vel != null) {
        if (vecFac.getDimension() == 2) {
          attributes.put(SpatialAttribute.DRIFT.toString(),
              vecFac.newDisplacementVector(
                  Vectors.polarToCartesian((Double) vel, (Double) dir)));
        } else {
          Object phi = attributes
              .remove(SpatialAttribute.SPERICAL_COORDINATES_ADDITIONAL_ATT_NAME
                  .toString());
          if (phi == null) {
            ApplicationLogger.log(Level.SEVERE,
                "Velocity & direction (angle) attribute defined for 3D case, but not "
                    + SpatialAttribute.SPERICAL_COORDINATES_ADDITIONAL_ATT_NAME
                        .toString()
                    + "; using 0");
            phi = 0.;
          }
          attributes.put(SpatialAttribute.DRIFT.toString(),
              vecFac.newDisplacementVector(Vectors.sphericalToCartesian(
                  (Double) vel, (Double) dir, (Double) phi)));
        }
      }
    }
  }

  /**
   * Create attribute map from given {@link InitEntity} or {@link RuleEntity}
   * and registered species definition. Determines attribute values randomly if
   * a range is given (see also {@link #rngUsedInLastCreation()}).
   * 
   * @param pEnt
   *          Entity definition
   * @param variables
   * @return New attribute->value map
   */
  private Map<String, Object> createAttributeValueMap(
      AbstractEntity<AbstractValueRange<?>> pEnt,
      Map<String, Object> variables) {
    Map<String, AbstractValueRange<?>> attRanges = joinAttributeMaps(
        speciesAttRangeMap.get(pEnt.getSpecies()), pEnt.getAttributes());
    Map<String, Object> rv = new LinkedHashMap<>();
    rngUsedInLastCreation = false;
    for (Map.Entry<String, AbstractValueRange<?>> e : attRanges.entrySet()) {
      AbstractValueRange<?> valueRange = e.getValue();
      String attribute = e.getKey();
      if (valueRange instanceof VariableValue) {
        rv.put(attribute,
            ((VariableValue) valueRange).calculateValue((Map) variables));
        continue;
      }
      if (valueRange.size() != 1) {
        rngUsedInLastCreation = true;
      }
      rv.put(attribute, valueRange.getRandomValue(rand));
    }
    return rv;
  }

  /**
   * Check for presence of all required spatial attributes and position
   * attribute (not required), put default values into attribute map for absent
   * non-required spatial attributes
   * 
   * @param attributes
   *          Attribute map (may be changed!)
   * @return {req. spat. attr. present?, position present?}
   */
  private static boolean[] checkSpatialityAndPosition(
      Map<String, Object> attributes) {
    boolean[] isSpatialWithPosition = new boolean[] { true, true };
    for (SpatialAttribute sa : SpatialAttribute.values()) {
      if (attributes.get(sa.toString()) == null) {
        if (sa.defaultValue() != null || sa.isAccessible()) {
          if (sa.defaultValue() != null) {
            attributes.put(sa.toString(), sa.defaultValue());
          }
        } else if (sa == SpatialAttribute.POSITION) {
          isSpatialWithPosition[1] = attributes.containsKey(REL_POSITION);
        } else {
          // some required spatial attribute undefined
          isSpatialWithPosition[0] = false;
        }
      }
    }
    return isSpatialWithPosition;
  }

  private static final double DELTA_FOR_VOL_CHECK = 1e-10;

  /**
   * Create shape
   * 
   * @param shapeKW
   *          Keyword describing shape
   * @param center
   *          Center coordinates
   * @param size
   *          Desired size
   * @param aspectRatio
   *          Aspect Ratio, if applicable (ignored otherwise)
   * @return Shape
   * @throws IllegalArgumentException
   *           if shape keyword is not known
   */
  private static IModifiableShape createShape(String shapeKW,
      IPositionVector center, Double size, IDisplacementVector aspectRatio,
      IVectorFactory vecFac) {
    IDisplacementVector period = vecFac.getPeriod();
    if (period != null
        && Math.abs(Vectors.prod(period) - size) / size < DELTA_FOR_VOL_CHECK) {
      warnIfNotPotentialTorus(shapeKW);
      // CHECK? assert correct aspect ratio?!
      return new TorusSurface(vecFac);
    }

    if (!shapeCreators.containsKey(shapeKW)) {
      throw new IllegalArgumentException(
          shapeKW + " is not a known shape keyword");
    }
    IModifiableShape shape =
        shapeCreators.get(shapeKW).create(center, size, vecFac, aspectRatio);
    if (Math.abs(shape.getSize() - size) / size > DELTA_FOR_VOL_CHECK) {
      ApplicationLogger.log(Level.SEVERE, "Volume not set " + "correctly"
          + " in " + shape + ": is: " + shape.getSize() + ", should: " + size);
    }

    return shape;
  }

  private static void warnIfNotPotentialTorus(String shapeKW) {
    ShapeCreator<? extends IModifiableShape> shapeCreator =
        shapeCreators.get(shapeKW);
    if (!shapeCreator.equals(cuboidCreator)
        && !shapeCreator.equals(cubeCreator)) {
      ApplicationLogger.log(Level.SEVERE,
          shapeKW + " is not a keyword for a torus (surface), "
              + "but periodic boundaries were defined. "
              + "Creating a torus (surface) anyway!");
    }
  }

  private static final ShapeCreator<? extends IModifiableShape> cubeCreator =
      new ShapeCreator.CubeCreator();

  private static final ShapeCreator<? extends IModifiableShape> cuboidCreator =
      new ShapeCreator.CuboidCreator();

  private static final ShapeCreator<? extends IModifiableShape> sphereCreator =
      new ShapeCreator.SphereCreator();

  // private static final ShapeCreator<? extends IModifiableShape>
  // torusCreator = new ShapeCreator.TorusCreator();
  // torus is created directly, as additional check is required before anyway

  private static final Map<String, ShapeCreator<? extends IModifiableShape>> shapeCreators =
      CollectionUtils.fillMap(
          new LinkedHashMap<String, ShapeCreator<? extends IModifiableShape>>(),
          "rectangle", cuboidCreator, "rect", cuboidCreator, "box",
          cuboidCreator, "cuboid", cuboidCreator, "cube", cubeCreator, "square",
          cubeCreator, "sphere", sphereCreator, "ball", sphereCreator, "circle",
          sphereCreator, "disk", sphereCreator);
}
