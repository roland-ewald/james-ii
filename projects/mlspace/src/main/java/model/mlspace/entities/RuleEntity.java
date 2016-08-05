/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.math.match.ValueMatch;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.misc.Pair;

/**
 * Entity definition as it appears in reaction/transfer rules, incl. matching
 * function
 * 
 * @author Arne Bittig
 */
public class RuleEntity extends
    AbstractEntity<Pair<? extends ValueMatch, String>> {

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(RuleEntity.class,
        new IConstructorParameterProvider<RuleEntity>() {
          @Override
          public Object[] getParameters(RuleEntity ent) {
            Object[] params =
                new Object[] { ent.getSpecies(), ent.getAttributes() };
            return params;
          }
        });
  }

  private static final long serialVersionUID = -8532171464869166371L;

  private Collection<String> varNames;

  /**
   * Constructor without attribute range definitions
   * 
   * @param spec
   *          Species
   */
  public RuleEntity(Species spec) {
    this(spec, new LinkedHashMap<String, Pair<? extends ValueMatch, String>>());
  }

  /**
   * Full constructor with attribute range definitions and local variable names
   * 
   * @param spec
   *          Species
   * @param attributes
   *          Attribute name->range & var name map
   */
  public RuleEntity(Species spec,
      Map<String, Pair<? extends ValueMatch, String>> attributes) {
    super(spec, attributes);
  }

  /**
   * Constructor with attribute range definitions only
   * 
   * @param spec
   *          Species
   * @param attributes
   *          Attribute name->range & var name map
   */
  public RuleEntity(Species spec, Map<String, ValueMatch> attributes,
      Object dummy) {
    this(spec, singleAsMultiMap(attributes, (String) null));
  }

  /**
   * "Copy" constructor (attribute map not actually copied)
   * 
   * @param ruleEnt
   *          RuleEntity whose properties (species, attributes) to use
   */
  protected RuleEntity(RuleEntity ruleEnt) {
    this(ruleEnt.getSpecies(), ruleEnt.getAttributes());
  }

  /**
   * Local variables to bind to current attribute values
   * 
   * @return var->att name map (not to be modified)
   */
  public Collection<String> getVarNames() {
    if (varNames == null) {
      Map<String, Pair<? extends ValueMatch, String>> atts = getAttributes();
      varNames = new ArrayList<>(atts.size());
      for (Pair<? extends ValueMatch, String> pair : atts.values()) {
        String varName = pair.getSecondValue();
        if (varName != null) {
          varNames.add(varName);
        }
      }
    }
    return varNames;
  }

  /**
   * Check if this entity has attributes of a given other entity (usually from a
   * reaction rule)
   * 
   * @param mEnt
   *          Entity to match with
   * @param vars
   *          Variable map (to which to-be-extracted variables will be added!)
   * @return true if entities are of the same species, all attributes of this
   *         rule entity are present in mEnt and have a value in the specified
   *         range (which may be a finite set or a single value)
   */
  public boolean matches(AbstractModelEntity mEnt, Map<String, Object> vars) {
    if (mEnt == null || !this.getSpecies().equals(mEnt.getSpecies())) {
      return false;
    }
    for (Map.Entry<String, Pair<? extends ValueMatch, String>> ae : this
        .getAttributes().entrySet()) {
      String attName = ae.getKey();
      Pair<? extends ValueMatch, String> value = ae.getValue();
      ValueMatch range = value.getFirstValue();
      Object matchVal = mEnt.getAttribute(attName);
      if (range != null) {
        if (matchVal == null) {
          throw new IllegalArgumentException("Entity " + mEnt
              + " lacks the required attribute " + attName
              + ", but is of the same type as " + this);
        }
        if (!range.matches(matchVal, vars)) {
          return false; // else check next attribute
        }
      }
      String varName = value.getSecondValue();
      if (varName != null) {
        vars.put(varName, matchVal);
      }
    }
    return true;
  }

  /**
   * @return Attributes relevant for matching this entity
   */
  public Collection<String> attributesToMatch() {
    return getAttributes().keySet();
  }

  @Override
  protected void appendAttStr(StringBuilder str) {
    for (Map.Entry<String, Pair<? extends ValueMatch, String>> attE : getAttributes()
        .entrySet()) {
      String attName = attE.getKey();
      Pair<? extends ValueMatch, String> pair = attE.getValue();
      String varName = pair.getSecondValue();
      if (varName != null) {
        str.append(varName);
        str.append("=");
      }
      str.append(attName);
      ValueMatch range = pair.getFirstValue();
      if (range != null) {
        // str.append(':');
        str.append(range);
      }
      str.append(",");
    }
  }

  private int hashCode = 0;

  @Override
  public int hashCode() {
    if (hashCode == 0) {
      final int prime = 31;
      hashCode =
          this.getSpecies().hashCode() + prime * getAttributes().hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    return equalsSameClass((RuleEntity) obj);
  }

  protected boolean equalsSameClass(RuleEntity other) {
    return this.getSpecies().equals(other.getSpecies())
        && this.getAttributes().equals(other.getAttributes());
  }

  private static <K, V1, V2> Map<K, Pair<? extends V1, V2>> singleAsMultiMap(
      final Map<K, V1> map, final V2 secondVal) {
    if (map == null) {
      return null;
    }
    return new AbstractMap<K, Pair<? extends V1, V2>>() {

      @Override
      public Set<Map.Entry<K, Pair<? extends V1, V2>>> entrySet() {
        return new AbstractSet<Map.Entry<K, Pair<? extends V1, V2>>>() {

          @Override
          public Iterator<Map.Entry<K, Pair<? extends V1, V2>>> iterator() {
            final Iterator<Map.Entry<K, V1>> it = map.entrySet().iterator();
            return new Iterator<Map.Entry<K, Pair<? extends V1, V2>>>() {

              @Override
              public boolean hasNext() {
                return it.hasNext();
              }

              @Override
              public Map.Entry<K, Pair<? extends V1, V2>> next() {
                final Map.Entry<K, V1> next = it.next();
                return new Map.Entry<K, Pair<? extends V1, V2>>() {

                  @Override
                  public K getKey() {
                    return next.getKey();
                  }

                  @Override
                  public Pair<V1, V2> getValue() {
                    return new Pair<>(next.getValue(), secondVal);
                  }

                  @Override
                  public Pair<V1, V2> setValue(Pair<? extends V1, V2> value) {
                    Pair<V1, V2> oldVal = getValue();
                    next.setValue(value.getFirstValue());
                    return oldVal;
                  }
                };
              }

              @Override
              public void remove() {
                it.remove();
              }

            };
          }

          @Override
          public int size() {
            return map.size();
          }

        };
      }
    };
  }
}
