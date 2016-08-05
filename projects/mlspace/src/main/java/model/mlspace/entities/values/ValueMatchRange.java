package model.mlspace.entities.values;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.match.ValueMatch;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Legacy adapter to make {@link model.mlspace.entities.RuleEntity} with new
 * {@link ValueMatch} usage compatible to old {@link AbstractValueRange} match
 * approach.
 * 
 * @author Arne Bittig
 * @date 08.08.2014
 */
public class ValueMatchRange extends AbstractValueRange<Object> implements
    ValueMatch {

  private static final long serialVersionUID = 8414927694443103160L;

  private final AbstractValueRange<?> range;

  /**
   * @param range
   *          value range
   */
  public ValueMatchRange(AbstractValueRange<?> range) {
    this.range = range;
  }

  @Override
  public boolean matches(Object value, Map<String, ?> variables) {
    return range.contains(value);
  }

  /**
   * @return the range
   */
  public AbstractValueRange<?> getRange() {
    return range;
  }

  @Override
  public String toString() {
    return " in " + range;
  }

  /**
   * @return
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<Object> iterator() {
    return (Iterator<Object>) range.iterator();
  }

  /**
   * @param obj
   * @return
   * @see model.mlspace.entities.values.AbstractValueRange#contains(java.lang.Object)
   */
  @Override
  public boolean contains(Object obj) {
    return range.contains(obj);
  }

  /**
   * @return
   * @see model.mlspace.entities.values.AbstractValueRange#size()
   */
  @Override
  public double size() {
    return range.size();
  }

  /**
   * @param rand
   * @return
   * @see model.mlspace.entities.values.AbstractValueRange#getRandomValue(org.jamesii.core.math.random.generators.IRandom)
   */
  @Override
  public Object getRandomValue(IRandom rand) {
    return range.getRandomValue(rand);
  }

  /**
   * @return
   * @see model.mlspace.entities.values.AbstractValueRange#toList()
   */
  @Override
  public List<Object> toList() {
    return (List<Object>) range.toList();
  }

  /**
   * @param r
   * @return
   * @see model.mlspace.entities.values.AbstractValueRange#containsAll(model.mlspace.entities.values.AbstractValueRange)
   */
  @Override
  public boolean containsAll(AbstractValueRange<?> r) {
    if (r instanceof ValueMatchRange) {
      return range.containsAll(((ValueMatchRange) r).getRange());
    }
    return range.containsAll(r);
  }

}
