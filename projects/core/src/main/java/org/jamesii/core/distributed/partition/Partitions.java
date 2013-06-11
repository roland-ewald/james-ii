/*
 * Partitions.java
 *
 * Created on 2. Oktober 2003, 10:53
 */

package org.jamesii.core.distributed.partition;

import java.io.Serializable;
import java.io.ObjectInputStream.GetField;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.Model;

/**
 * A set of partitions. Each partitionizing results in a tree of partitions of
 * which each sub level is represented by a list of partitions. (n - ary tree)
 * 
 * @author Jan Himmelspach
 */
public class Partitions implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 6235703317977413444L;

  /**
   * Internal list of partitions. Can be accessed by the various accessing
   * methods defined in this class.
   */
  private List<Partition> partitions = new Vector<>();

  /**
   * Creates a new instance of Partitions.
   */
  public Partitions() {
  }

  /**
   * Adds a partitions to the list of partitions.
   * 
   * @param partition
   *          the partition
   */
  public void addPartition(Partition partition) {
    partitions.add(partition);
  }

  /**
   * Returns true if there is no partition for the given model.
   * 
   * @param model
   *          the model
   * 
   * @return true, if contains partition for model
   */
  public boolean containsPartitionForModel(Model model) {
    return (getPartitionForModel(model) == null);
  }

  /**
   * Returns the partition with the given index.
   * 
   * @param index
   *          the index
   * 
   * @return the partition
   */
  public Partition getPartition(int index) {
    return partitions.get(index);
  }

  /**
   * Returns the number of partitions.
   * 
   * @return the partition count
   */
  public int getPartitionCount() {
    return partitions.size();
  }

  /**
   * Returns the partition for the given model, if there is one - otherwise it
   * returns null.
   * 
   * @param model
   *          the model
   * 
   * @return the partition for model
   */
  public Partition getPartitionForModel(IModel model) {

    Iterator<Partition> it = iterator();
    Partition p;
    while (it.hasNext()) {
      p = it.next();

      // TODO(re027): make sure that this works:
      if (model.equals(p.getModel())) {
        // ...instead of:
        // if (((IBasicDEVSModel) model).getFullName().equals(
        // ((IBasicDEVSModel) p.getModel()).getFullName()))
        return p;
      }
    }

    return null;
  }

  /**
   * Returns an iterator for the list of partitions. Wrapper for the iterator()
   * getter of the embedded list.
   * 
   * @return the iterator< partition>
   */
  public Iterator<Partition> iterator() {
    return partitions.iterator();
  }

  /**
   * Removes the partition at the given index.
   * 
   * @param index
   *          of the partition
   * 
   * @return true, if removes the partition
   */
  public boolean removePartition(int index) {

    if (index >= 0 && index < partitions.size()) {
      partitions.remove(index);
      return true;
    }
    return false;

  }

  protected final List<Partition> getPartitions() {
    return partitions;
  }

}
