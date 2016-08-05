/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.spatial;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.math.geometry.shapes.IModifiableShape;
import org.jamesii.core.math.statistics.univariate.HarmonicMean;

import model.mlspace.entities.Species;
import model.mlspace.entities.binding.BindingSites;
import model.mlspace.entities.binding.IEntityWithBindings;

/**
 * Compartments are spatial entities with hard boundaries, collisions between
 * them (which may trigger reactions) must always be resolved such that no two
 * of them overlap.
 * 
 * Compartments may contain other entities, but if so, they must contain them
 * completely (due to the hard boundaries). They may themselves overlap the
 * (soft) boundaries of a {@link Region}. If an entity with hard boundaries
 * collides with a compartment and there is an applicable collision rule, it can
 * be transferred into the compartment. If an entity inside a compartment
 * collides with (i.e., moves against) its boundary from inside and there is an
 * applicable collision rule, it can be transferred out of the compartment.
 * 
 * @author Arne Bittig
 * @date 28.06.2012 (date of separation of Compartment and Region)
 */
public class Compartment extends SpatialEntity
    implements IEntityWithBindings<Object> {

  private static final long serialVersionUID = 6189136795239633829L;

  private final Map<String, Compartment> bindings = new LinkedHashMap<>();

  private final BindingSites bs;

  private CompComplex complex = null;

  /**
   * @param spec
   *          Species of the compartment
   * @param shape
   *          Shape of the compartment
   * @param attributes
   *          Attributes (name->values map)
   * @param bindingSites
   *          Binding sites definition (null if none)
   * @param enclosingEntity
   *          SpatialEntity this one is situated in (null if none)
   */
  public Compartment(Species spec, IModifiableShape shape,
      Map<String, Object> attributes, BindingSites bindingSites,
      SpatialEntity enclosingEntity) {
    super(shape, spec, attributes, enclosingEntity);
    this.bs = bindingSites;
  }

  @Override
  public boolean isHardBounded() {
    return true;
  }

  @Override
  public boolean hasBindingSite(String name) {
    return bs != null && bs.contains(name);
  }

  public boolean isFixedAngleBindingSite(String name) {
    return bs.isFixedAngleBindingSite(name);
  }

  @Override
  public Compartment getBoundEntity(String name) {
    if (bs == null || !bs.contains(name)) {
      throw new IllegalArgumentException(
          "No binding site " + name + " in " + this);
    }
    return bindings.get(name);
  }

  @Override
  public boolean hasBoundEntities() {
    return bindings.size() > 0;
  }

  /**
   * @return bound entities
   */
  public Collection<Compartment> getBoundEntities() {
    return bindings.values();
  }

  @Override
  public Map<String, ? extends IEntityWithBindings<Object>> bindingEntries() {
    return Collections.unmodifiableMap(bindings);
  }

  /**
   * Get name of binding site where given entity is bound, if any
   * 
   * @param entity
   *          Entity bound
   * @return Name of site where entity is bound, null if none
   */
  // @Override
  public String getBindingSite(Compartment entity) {
    for (Map.Entry<String, Compartment> e : bindings.entrySet()) {
      if (e.getValue().equals(entity)) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * 
   * @param site
   *          Binding site with (pending or recent) change
   * @return Entities occupying other sites and relative angles
   */
  public Map<Compartment, Double> getRelativeAnglesToOccupiedSites(
      String site) {
    Map<Compartment, Double> rv = new LinkedHashMap<>(bindings.size());
    for (Map.Entry<String, Compartment> e : bindings.entrySet()) {
      String site2 = e.getKey();
      if (!site2.equals(site) && this.isFixedAngleBindingSite(site2)) {
        rv.put(e.getValue(), bs.getRelativeAngle(site, site2));
      }
    }
    return rv;
  }

  /**
   * Bind to other compartment (one way only, as the other compartment may have
   * a differently named binding site for binding this compartment)
   * 
   * @param bindingSiteName
   *          Name of the binding site
   * @param compToBind
   *          Entity to bind
   * @throws IllegalArgumentException
   *           if there is no binding site of the given name
   * @throws IllegalStateException
   *           if given binding site is not free
   */
  public void bind(String bindingSiteName, Compartment compToBind) {
    if (bs == null || !bs.contains(bindingSiteName)) {
      throw new IllegalArgumentException(
          "No binding site " + bindingSiteName + " in " + this);
    }
    if (bindings.get(bindingSiteName) != null) {
      throw new IllegalStateException(
          "Binding site " + bindingSiteName + " is not free in " + this);
    }
    CompComplex complexToBind = compToBind.getComplex();
    if (complex == null) {
      if (complexToBind != null) {
        complexToBind.add(this);
      } else {
        CompComplex.makeNewComplex(this, compToBind);
      }
    } else if (complexToBind != complex) {
      complex.add(compToBind);
    } // else comps are already in same complex (bind is always called
      // twice, once for each participant)
    bindings.put(bindingSiteName, compToBind);
  }

  /**
   * Free binding site, i.e. release whatever is bound to it (one way, i.e. this
   * compartment is not automatically released from the other, as the respective
   * binding site there may have a different name)
   * 
   * @param bindingSiteName
   *          Name of the binding site
   * @return Entity previously bound there
   * @throws IllegalArgumentException
   *           if there is no binding site of the given name
   * @throws IllegalStateException
   *           if given binding site is already free
   */
  public Compartment release(String bindingSiteName) {
    if (bs == null || !bs.contains(bindingSiteName)) {
      throw new IllegalArgumentException(
          "No binding site " + bindingSiteName + " in " + this);
    }

    Compartment removed = bindings.remove(bindingSiteName);
    if (removed != null) {
      // null check for rules specifying release of bindings sites that
      // may or may not be occupied (i.e. that are not matched beforehand)
      CompComplex.split(complex, this, removed);
    }
    return removed;
  }

  @Override
  public int getAttributesHashCode() {
    if (bs == null || bindings.isEmpty()) {
      return super.getAttributesHashCode();
    }
    final int prime = 23;
    int entHashCode = super.absEntStr().toString().hashCode();
    Set<String> keySet = bindings.keySet();
    int bindingHashCode = keySet.hashCode();
    return prime * entHashCode + bindingHashCode;
  }

  @Override
  public String toString() {
    return longString();
  }

  private String longString() {
    if (bs == null) {
      return super.toString();
    }
    String[] split = super.toString().split(" at ");
    StringBuilder sb = new StringBuilder(split[0]);
    sb.append("<");
    for (Map.Entry<String, Compartment> e : bindings.entrySet()) {
      sb.append(e.getKey());
      sb.append(':');
      sb.append(e.getValue().idString());
      sb.append(',');
    }
    if (sb.charAt(sb.length() - 1) == ',') {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append(">");
    sb.append(" at ");
    sb.append(split[1]);
    return sb.toString();
  }

  /**
   * Get information on complex this entity is in (null if none)
   * 
   * @return Complex information
   */
  public CompComplex getComplex() {
    return complex;
  }

  private void setComplex(CompComplex compComplex) {
    this.complex = compComplex;
  }

  /**
   * Container for information on entity complex
   * 
   * @author Arne Bittig
   * @date 31.01.2014
   */
  public static final class CompComplex {

    private final Compartment anchor;

    /**
     * @param anchor
     * @param numOfParticles
     * @param diffusion
     */
    protected CompComplex(Compartment anchor, int numOfParticles,
        double diffusion) {
      this.anchor = anchor;
      this.numOfParticles = numOfParticles;
      this.diffusion = diffusion;
    }

    private double diffusion;

    private int numOfParticles;

    /**
     * Convenience method to create new complex information container from given
     * comps, and set it there, too
     * 
     * @param comp1
     * @param comp2
     */
    private static void makeNewComplex(Compartment comp1, Compartment comp2) {
      CompComplex complex = new CompComplex(comp1, 2,
          1. / HarmonicMean.sumOfReciprocals(comp1.getDiffusionConstant(),
              comp2.getDiffusionConstant()));
      comp1.setComplex(complex);
      comp2.setComplex(complex);
    }

    private void add(Compartment comp) {
      assert numOfParticles > 0;
      CompComplex complex2 = comp.getComplex();
      assert complex2 != this;
      if (complex2 != null) {
        if (complex2.getNumOfParticles() <= this.getNumOfParticles()) {
          join(this, complex2);
        } else {
          join(complex2, this);
        }
        return;
      }
      numOfParticles++;
      this.diffusion =
          getNewDiffusion(this.diffusion, comp.getDiffusionConstant());
      comp.setComplex(this);

    }

    private static double getNewDiffusion(double... diffs) {
      return 1. / HarmonicMean.sumOfReciprocals(diffs);
    }

    private static double getDiffusionAfterRemoval(double diffBefore,
        double diffRemove) {
      return 1. / HarmonicMean.sumOfReciprocals(diffBefore, -diffRemove);
    }

    /**
     * Join comps of complex2 with those of complex1 (i.e. assigning complex1 to
     * all) NOTE: join must be called before the new binding between an entity
     * of complex1 and an entity of complex2 is registered (while
     * {@link #split(CompComplex, Compartment, Compartment)} must be called
     * after the binding is removed)
     * 
     * @param complex1
     * @param complex2
     */
    private static void join(CompComplex complex1, CompComplex complex2) {
      int numPart2 = complex2.getNumOfParticles();
      assert numPart2 > 0;
      Collection<Compartment> comps2 = new LinkedHashSet<>(numPart2);
      recCollectBindingPartners(complex2.getAnchor(), comps2);
      assert comps2.size() == numPart2;
      for (Compartment c2 : comps2) {
        c2.setComplex(complex1);
      }
      complex1.diffusion =
          getNewDiffusion(complex1.getDiffusion(), complex2.getDiffusion());
      complex1.numOfParticles += numPart2;
      complex2.numOfParticles = 0;
    }

    private static void recCollectBindingPartners(Compartment startComp,
        Collection<Compartment> coll) {
      coll.add(startComp);
      for (Compartment bc : startComp.getBoundEntities()) {
        if (!coll.contains(bc)) {
          recCollectBindingPartners(bc, coll);
        }
      }
    }

    /**
     * Split complex containing given, previously bound comps, in two. NOTE:
     * binding between comp1 and comp2 must have been removed already (both
     * ways) (while {@link #join(CompComplex, CompComplex)} must be called
     * before binding is applied)
     * 
     * @param complex
     * @param comp1
     * @param comp2
     */
    private static void split(CompComplex complex, Compartment comp1,
        Compartment comp2) {
      Collection<Compartment> compsToRemove = new LinkedHashSet<>();
      recCollectBindingPartners(comp2, compsToRemove);
      int numToRemove = compsToRemove.size();
      if (numToRemove == complex.getNumOfParticles()) {
        return; // entities still bound indirectly elsewhere
      }

      Compartment splitOffAnchor = comp2;
      if (compsToRemove.contains(complex.getAnchor())) {
        compsToRemove.clear();
        recCollectBindingPartners(comp1, compsToRemove);
        assert compsToRemove.size() + numToRemove == complex
            .getNumOfParticles();
        assert!compsToRemove.contains(complex.getAnchor());
        numToRemove = compsToRemove.size();
        splitOffAnchor = comp1;
      }

      double splitOffDiff;
      if (numToRemove == 1) {
        Compartment splitOffComp = compsToRemove.iterator().next();
        splitOffComp.setComplex(null);
        splitOffDiff = splitOffComp.getDiffusionConstant();
      } else { // CHECK: special handling of numToRemove ==
               // complex.getNumOfParticles()-1 ?
        double[] diffs2 = new double[numToRemove];
        CompComplex complex2 =
            new CompComplex(splitOffAnchor, numToRemove, -1.);
        int i = 0;
        for (Compartment c2 : compsToRemove) {
          c2.setComplex(complex2);
          diffs2[i++] = c2.getDiffusionConstant();
        }
        splitOffDiff = getNewDiffusion(diffs2);
        complex2.diffusion = splitOffDiff;
      }

      complex.numOfParticles -= numToRemove;
      if (complex.numOfParticles == 1) {
        complex.getAnchor().setComplex(null);
      } else {
        complex.diffusion =
            getDiffusionAfterRemoval(complex.getDiffusion(), splitOffDiff);
      }
    }

    /**
     * @return Participating entity which shall be used to identify this complex
     */
    public Compartment getAnchor() {
      return anchor;
    }

    /**
     * @return Diffusion of bound entity
     */
    public double getDiffusion() {
      return diffusion;
    }

    /**
     * @return Number of participating entities
     */
    public int getNumOfParticles() {
      return numOfParticles;
    }

    @Override
    public String toString() {
      return "CompComplex [numOfParticles=" + numOfParticles + ", diffusion="
          + diffusion + ",  anchor=" + anchor.idString() + "]";
    }
  }

}