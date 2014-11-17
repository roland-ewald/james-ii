/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class similar to the WeakHashMap class that stores all list items using
 * {@link WeakReference} objects. This way items can get garbage collected even
 * if they are still referenced in this list. The main use case for this kind of
 * list is a listener list, so that automatically generated listeners are
 * removed from the list if they are not longer available. This doesn't work
 * with anonymous classes though. So be aware of that when using anonymous
 * classes, because they get removed from the list almost immediately. Use this
 * class with caution and not with anonymous created instances because those
 * will be removed from this list even if they are still expected to be in use.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          generic attribute parameter specifying the type of objects stored in
 *          the list
 * 
 */
public class WeakArrayList<T> implements List<T> {

  /**
   * ListIterator wrapper for {@link WeakReference} to E
   * 
   * @author Stefan Rybacki
   * 
   * @param <E>
   *          generic attribute parameter
   */
  class WeakListIterator<E> implements ListIterator<E> {
    /**
     * iterator used for wrapping
     */
    private ListIterator<WeakReference<E>> listIterator;

    private ReferenceQueue<? super E> queue;

    /**
     * Creates a new instance using the given list of {@link WeakReference}s
     * 
     * @param c
     *          a list of {@link WeakReference}s
     */
    public WeakListIterator(List<WeakReference<E>> c,
        ReferenceQueue<? super E> queue) {
      this(c.listIterator(), queue);
    }

    /**
     * Creates a new instance using the given {@link ListIterator}
     * 
     * @param listIterator2
     *          the iterator
     * @param queue
     */
    public WeakListIterator(ListIterator<WeakReference<E>> listIterator2,
        ReferenceQueue<? super E> queue) {
      listIterator = listIterator2;
      this.queue = queue;
    }

    @Override
    public void add(E e) {
      listIterator.add(new WeakReference<>(e, queue));
    }

    @Override
    public boolean hasPrevious() {
      return listIterator.hasPrevious();
    }

    @Override
    public int nextIndex() {
      return listIterator.nextIndex();
    }

    @Override
    public E previous() {
      return listIterator.previous().get();
    }

    @Override
    public int previousIndex() {
      return listIterator.previousIndex();
    }

    @Override
    public void set(E e) {
      listIterator.set(new WeakReference<>(e, queue));
    }

    @Override
    public boolean hasNext() {
      return listIterator.hasNext();
    }

    @Override
    public E next() {
      return listIterator.next().get();
    }

    @Override
    public void remove() {
      listIterator.remove();
    }

  }

  /**
   * the list of {@link WeakReference}s
   */
  private List<WeakReference<T>> list = new ArrayList<>();

  /** The reference queue. */
  private final ReferenceQueue<? super T> queue = new ReferenceQueue<>();

  /** The timer. */
  private Timer timer;

  @Override
  public Iterator<T> iterator() {
    return new WeakListIterator<>(list, queue);
  }

  /**
   * Instantiates a new weak array list.
   */
  public WeakArrayList() {
    timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        cleanUp();
      }
    }, 0, 2000);
  }

  @Override
  protected void finalize() throws Throwable {
    timer.cancel();
    super.finalize();
  }

  /**
   * Helper method cleaning up unused {@link WeakReference}s.
   */
  private void cleanUp() {
    // remove all garbage collected references
    Reference<?> r;
    while ((r = queue.poll()) != null) {
      list.remove(r);
    }
  }

  @Override
  public int size() {
    cleanUp();
    return list.size();
  }

  @Override
  public T get(int index) {
    return list.get(index).get();
  }

  @Override
  public boolean add(T e) {
    return list.add(new WeakReference<>(e, queue));
  }

  @Override
  public void add(int index, T element) {
    list.add(index, new WeakReference<>(element, queue));
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    boolean res = false;
    for (T t : c) {
      res |= add(t);
    }
    return res;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    int i = index;
    for (T t : c) {
      add(i, t);
      i++;
    }
    return true;
  }

  @Override
  public void clear() {
    list.clear();
  }

  @Override
  public boolean contains(Object o) {
    for (WeakReference<T> r : list) {
      if (r == null) {
        return false;
      }
      T t = r.get();
      return t != null && t.equals(o);
    }
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object x : c) {
      if (!contains(x)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int indexOf(Object o) {
    for (int i = 0; i < list.size(); i++) {
      WeakReference<T> r = list.get(i);
      if (r == null) {
        continue;
      }
      T t = r.get();
      if (t != null && t.equals(o)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public int lastIndexOf(Object o) {
    for (int i = list.size() - 1; i >= 0; i--) {
      WeakReference<T> r = list.get(i);
      if (r == null) {
        continue;
      }
      T t = r.get();
      if (t != null && t.equals(o)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public ListIterator<T> listIterator() {
    return new WeakListIterator<>(list, queue);
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return new WeakListIterator<>(list.listIterator(index), queue);
  }

  @Override
  public boolean remove(Object o) {
    for (WeakReference<T> r : list) {
      if (r == null) {
        continue;
      }
      T t = r.get();
      if (t != null && t.equals(o)) {
        return list.remove(r);
      }
    }
    return false;
  }

  @Override
  public T remove(int index) {
    return list.remove(index).get();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    for (Object o : c) {
      remove(o);
    }
    return true;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    for (int i = list.size(); i >= 0; i--) {
      if (!c.contains(list.get(i).get())) {
        list.remove(i);
      }
    }
    return true;
  }

  @Override
  public T set(int index, T element) {
    return list.set(index, new WeakReference<>(element, queue)).get();
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    List<T> l = new WeakArrayList<>();
    for (int i = fromIndex; i <= toIndex; i++) {
      l.add(list.get(i).get());
    }
    return l;
  }

  @Override
  public Object[] toArray() {
    Object[] res = new Object[list.size()];
    for (int i = 0; i < list.size(); i++) {
      res[i] = list.get(i).get();
    }
    return res;
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public <F> F[] toArray(F[] a) {
    if (a.length < size()) {
      return (F[]) toArray();
    }
    System.arraycopy(toArray(), 0, a, 0, size());
    if (a.length > size()) {
      a[size()] = null;
    }
    return a;
  }

}
