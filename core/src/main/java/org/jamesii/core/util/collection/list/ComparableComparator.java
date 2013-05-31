/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for comparing two classes implementing the comparable interface.
 * 
 * It will return
 * <table>
 * <tr>
 * <td>0</td>
 * <td>if both objects are null or the comparison of o1 with o2 returns 0</td>
 * </tr>
 * <tr>
 * <td>-1</td>
 * <td>if o1 is null and o2 is not null or if the comparison of o1 with o2
 * returns -1</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>if o1 is not null and o2 is null or if the comparison of o1 with o2
 * returns 1</td>
 * </tr>
 * <tr>
 * <td>?</td>
 * <td>value of the comparison of o1 compared with o2 if both are not null</td>
 * </tr>
 * </table>
 * 
 * @author Stefan Rybacki
 * 
 */
public class ComparableComparator<E extends Comparable<? super E>> implements
    Comparator<E>, Serializable {

  /**
   * The constant serial version uid.
   */
  private static final long serialVersionUID = -4808156889988185022L;

  @Override
  public int compare(E o1, E o2) {
    if (o1 == null) {
      if (o2 == null) {
        return 0;
      } else {
        return -1;
      }
    } else {
      if (o2 == null) {
        return 1;
      }
    }
    return o1.compareTo(o2);
  }

}
