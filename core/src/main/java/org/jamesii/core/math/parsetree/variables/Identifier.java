/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.variables;

import java.io.Serializable;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * An identifier/variable is a place holder for values. There has to be an
 * environment for each identifier from which its current value can be retrieved
 * during calculation.
 * 
 * @param <K>
 *          type of the ident
 * @author Jan Himmelspach
 */
public class Identifier<K extends Serializable> extends Node implements
    IDependancy {

  private static final long serialVersionUID = 6087831579931958887L;

  /** The name of the identifier. */
  private K ident;

  /**
   * The default value that is used if the given environment does not contain
   * values for this identifier.
   */
  private Object defaultValue;

  /**
   * Creates a new identifier. The identifier will be created with a default
   * value of <code>null</code>.
   * 
   * @param ident
   *          the ident
   */
  public Identifier(K ident) {
    this(ident, null);
  }

  /**
   * Creates a new identifier.
   * 
   * @param ident
   *          the identifier
   * @param defaultValue
   *          the default value used if environment does not contain value for
   *          given identifier
   */
  public Identifier(K ident, Object defaultValue) {
    super();
    this.ident = ident;
    this.defaultValue = defaultValue;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    Object o = defaultValue;
    if (cEnv != null
        && ((IEnvironment<Serializable>) cEnv).containsIdent(ident)) {
      o = ((IEnvironment<Serializable>) cEnv).getValue(ident);
    }
    if (o instanceof INode) {
      return ((INode) o).calc(cEnv);
    }
    return (N) new ValueNode<>(o);
  }

  @Override
  public boolean dependsOn(Integer time) {

    if (time == 0) {
      return true;
    }
    return false;
  }

  /**
   * Gets the ident.
   * 
   * @return the ident
   */
  public K getIdent() {
    return ident;
  }

  @Override
  public String toString() {
    return String.format("%s", ident);
  }

  public void setIdent(K id) {
    ident = id;
  }
}
