/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.dsplay;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.jamesii.core.util.eventset.AbstractEventQueue;
import org.jamesii.core.util.eventset.Entry;

/**
 * This Dsplay implementation is based on the article "DSplay: An Efficient
 * Dynamic Priority Queue Structure For Discrete Event Simulation" by Rick Siow
 * Mong Goh and Ian Li-Jin Thng.
 * 
 * @author Richard Knorrek (richard.knorrek@uni-rostock.de)
 * @param <E>
 *          Event type
 * 
 */
public class DsplayEventQueue<E> extends AbstractEventQueue<E, Double> {

  private static final long serialVersionUID = 5571048543515550749L;

  private DsplayEventQueueTier1<E> tier1;
  private DsplayEventQueueTier2<E> tier2;
  private DsplayEventQueueTier3<E> tier3;

  public DsplayEventQueue() {
    this.tier1 = new DsplayEventQueueTier1<>();
    this.tier2 = new DsplayEventQueueTier2<>();
    this.tier3 = new DsplayEventQueueTier3<>();
  }

  @Override
  public Entry<E, Double> dequeue() {
    if (isEmpty())
      return null;
    if (tier1.isEmpty()) {
      if (tier2.isEmpty()) {
        if (tier3.isEmpty()) {
          return null;
        }
        tier2.t3tot2(tier3);
      }
      tier1.t2tot1(tier2);
    }
    return tier1.dequeue();
  }

  @Override
  public Double dequeue(E event) {
    if (this.isEmpty())
      return null;
    Entry<E, Double> en;
    if ((en = tier1.dequeue(event)) != null) {
      return en.getTime();
    }
    if ((en = tier2.dequeue(event)) != null) {
      return en.getTime();
    }
    if ((en = tier3.dequeue(event)) != null) {
      return en.getTime();
    }
    return null;
  }

  @Override
  public List<E> dequeueAll() {
    LinkedList<E> res = new LinkedList<>();
    if (!isEmpty()) {
      if (tier1.isEmpty()) {
        if (tier2.isEmpty()) {
          tier2.t3tot2(tier3);
        }
        tier1.t2tot1(tier2);
      }
      LinkedList<E> entries = tier1.dequeueAll();
      if (entries != null)
        return entries;
    }
    return res;
  }

  @Override
  public List<E> dequeueAll(Double time) {
    LinkedList<E> res = new LinkedList<>();
    // LinkedList<Entry<E, Double>> entries =null;
    if (time > tier3.getCur()) {
      res = tier3.dequeueAll(time);
    } else if (time >= tier2.getCur()) {
      res = tier2.dequeueAll(time);
    } else {
      res = tier1.dequeueAll(time);
    }
    // if(entries!=null){
    // for(Entry<E, Double> en:entries){
    // res.add(en.getEvent());
    // }
    // }
    return res;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    List<E> in = dequeueAll();
    HashMap<E, Object> res = new HashMap<>(in.size());
    for (E e : in) {
      res.put(e, null);
    }
    return res;
  }

  @Override
  public void enqueue(E event, Double time) {
    if (time > tier3.getCur()) {
      tier3.enqueue(event, time);
    } else if (time >= tier2.getCur()) {
      tier2.enqueue(event, time);
    } else {
      tier1.enqueue(event, time);
    }

  }

  @Override
  public Double getMin() {
    if (isEmpty())
      return null;
    if (!tier1.isEmpty()) {
      return tier1.getMinTS();
    } else if (!tier2.isEmpty()) {
      return tier2.getMinTS();
    } else {
      return tier3.getMinTS();
    }
  }

  @Override
  public Double getTime(E event) {
    Entry<E, Double> en;
    if ((en = tier1.lookup(event)) != null)
      return en.getTime();
    if ((en = tier2.lookup(event)) != null)
      return en.getTime();
    if ((en = tier3.lookup(event)) != null)
      return en.getTime();
    return null;
  }

  @Override
  public boolean isEmpty() {
    if (this.size() == 0) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void requeue(E event, Double newTime) {
    dequeue(event);
    enqueue(event, newTime);

  }

  @Override
  public void requeue(E event, Double oldTime, Double newTime) {
    if (tier1.dequeue(event, oldTime) == null) {
      if (tier2.dequeue(event, oldTime) == null) {
        tier3.dequeue(event, oldTime);
      }
    }
    enqueue(event, newTime);

  }

  @Override
  public int size() {
    return tier1.size() + tier2.size() + tier3.size();
  }

}
