/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package examples.devs;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import org.jamesii.core.model.State;

public class JamesState extends State implements Cloneable {

  static final long serialVersionUID = 380418087013638309L;

  /**
   * This slot holds the knowledgebase.
   */
  private String beliefs = null;

  /**
   * This slot carries a string symbol, textually describing and representing
   * the actual physical state.
   */
  private String phase = null;

  /**
   * Generates a state without slots phase, beliefs and result set.
   */
  public JamesState() {
    this(null, null);
  }

  /**
   * Generates a state with the given phase and no beliefs and no result.
   */
  public JamesState(String phase) {
    this(phase, null);
  }

  /**
   * Generates a state with the given phase and beliefs.
   */
  public JamesState(String phase, String beliefs) {
    this.phase = phase;
    this.beliefs = beliefs;
  }

  // --- CLASS-HANDLING --------------------------------------------------


  public String getBeliefs() {
    return beliefs;
  }

  // --- CONSTRUCTOR-HANDLING --------------------------------------------

  /**
   * Returns a clone of this State. The slot <CODE>result</CODE> is NOT cloned
   * but set to <CODE>null</CODE>. <BR>
   * When defining subclass of State this method must be overwritten to ensure
   * correct copying of states during simulation.
   */
  /*
   * public synchronized Object clone(){ return new JamesState((phase == null) ?
   * null : new String(this.phase), (beliefs == null) ? null : new
   * String(this.beliefs)); }
   */

  // ------AccessorMethoden--------------------------------------------------------

  public String getPhase() {
    return phase;
  }


  public void setBeliefs(String beliefs) {
    this.beliefs = beliefs;
    this.changed();
  }


  public void setPhase(String phase) {
    this.phase = phase;
    changed();

  }

  /**
   * Returns a textual description of this state.
   */
  @Override
  public String toString() {
    return ("phase: " + phase + (beliefs == null ? "" : ", beliefs: "
        + beliefs.toString()));
  }

}
