/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules.attributemodification;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;

/**
 * Consumption: Singleton special case that is encoded as kind of attribute
 * modification (without actually modifying attribute values)
 * 
 * @author Arne Bittig
 */
public enum SpecialAttributeModification implements
    List<IAttributeModification> {

  /**
   * Marker for destroyed entities (workaround that allows use with the existing
   * List structure that specifies regular attribute modifications; singleton
   * list with dummy att mod ise used so other lists of attribute modifications
   * are never equal to this one).
   */
  CONSUMED {
    private final List<IAttributeModification> dummy = Collections
        .<IAttributeModification> singletonList(new IAttributeModification() {
          private static final long serialVersionUID = 1089636851760847606L;

          @Override
          public String getAttribute() {
            throw new UnsupportedOperationException();
          }

          @Override
          public Object modifyAttVal(AbstractModelEntity ent,
              Map<String, Object> env) {
            throw new UnsupportedOperationException();
          }
        });

    @Override
    public int size() {
      return dummy.size();
    }

    @Override
    public boolean isEmpty() {
      return dummy.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return dummy.contains(o);
    }

    @Override
    public Iterator<IAttributeModification> iterator() {
      return dummy.iterator();
    }

    @Override
    public Object[] toArray() {
      return dummy.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return dummy.toArray(a);
    }

    @Override
    public boolean add(IAttributeModification e) {
      return dummy.add(e);
    }

    @Override
    public boolean remove(Object o) {
      return dummy.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      return dummy.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends IAttributeModification> c) {
      return dummy.addAll(c);
    }

    @Override
    public boolean addAll(int index,
        Collection<? extends IAttributeModification> c) {
      return dummy.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      return dummy.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      return dummy.retainAll(c);
    }

    @Override
    public void clear() {
      dummy.clear();
    }

    @Override
    public IAttributeModification get(int index) {
      return dummy.get(index);
    }

    @Override
    public IAttributeModification set(int index, IAttributeModification element) {
      return dummy.set(index, element);
    }

    @Override
    public void add(int index, IAttributeModification element) {
      dummy.add(index, element);
    }

    @Override
    public IAttributeModification remove(int index) {
      return dummy.remove(index);
    }

    @Override
    public int indexOf(Object o) {
      return dummy.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
      return dummy.lastIndexOf(o);
    }

    @Override
    public ListIterator<IAttributeModification> listIterator() {
      return dummy.listIterator();
    }

    @Override
    public ListIterator<IAttributeModification> listIterator(int index) {
      return dummy.listIterator(index);
    }

    @Override
    public List<IAttributeModification> subList(int fromIndex, int toIndex) {
      return dummy.subList(fromIndex, toIndex);
    }
  }

}
