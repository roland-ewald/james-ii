/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.dsplay;


import java.util.LinkedList;

import org.jamesii.core.util.eventset.Entry;

/**
 * 
 * Tier 3 of the Dsplay Event Queue:
 * a simple unsorted list for far future events
 * 
 * @author Richard Knorrek (richard.knorrek@uni-rostock.de)
 *
 * @param <E>
 */

public class DsplayEventQueueTier3<E> {
	
	//Minimum timestamp of an event that can be enqueued in T3 
	//This value will be set equal to T3_max at each transfer of events from T3 to T2
	private double cur;
		
	//Minimum timestamp in T3
	private double min;
		
	//Maximum timestamp in T3
	private double max;
		
	//Number of events in T3
	private int num=0;
	
	//unsorted list of events
	private LinkedList<Entry <E, Double>> tier3;
	
	public DsplayEventQueueTier3(){
		tier3 = new LinkedList<>();
		min=Double.POSITIVE_INFINITY;
		max=Double.NEGATIVE_INFINITY;
		cur=Double.NEGATIVE_INFINITY;
	}

	public Entry<E, Double> dequeue(E event) {
		if(tier3.isEmpty())return null;
		for(Entry<E, Double> en : tier3){
			if(en.getEvent()==event){
				tier3.remove(en);
				num--;
				return en;
			}
		}
		return null;
	}
	
	public Entry<E, Double> dequeue(E event, Double time) {
		if(tier3.isEmpty())return null;
		for(Entry<E, Double> en : tier3){
			if(en.getEvent()==event && en.getTime().compareTo(time)==0){
				tier3.remove(en);
				num--;
				return en;
			}
		}
		return null;
	}
	
	public LinkedList<E> dequeueAll(Double time){
		LinkedList<E> res = new LinkedList<>();
		if(!tier3.isEmpty()){
			for(Entry<E, Double> en:tier3){
				if(en.getTime().compareTo(time)==0){
					res.add(en.getEvent());		
				}
			}
			for(E en:res){
				tier3.remove(new Entry<>(en,time));
				num--;
			}
		}
		return res;
	}
	
	public void enqueue(E event, Double time){
		tier3.add(new Entry<>(event, time));
		num++;
		if(time>max)max=time;
		if(time<min)min=time;
	}
	
	//is tier 3 empty?
	public boolean isEmpty(){
		return num==0;
	}
	
	public Entry<E, Double> lookup(E event){
		if(tier3.isEmpty())return null;
		for(Entry<E, Double> en : tier3){
			if(en.getEvent()==event){
				return en;
			}
		}
		return null;
	}
	
	//returns current data and resets everything
	public LinkedList<Entry<E, Double>> t3tot2(){
		LinkedList<Entry<E, Double>> result = tier3;
		tier3=new LinkedList<>();
		num=0;
		cur=max;
		max=Double.NEGATIVE_INFINITY;
		min=Double.POSITIVE_INFINITY;
		return result;
	}
	
	
	//size of tier 3
	public int size(){
		return num;
	}

	public Double getMinTS(){
		if(this.isEmpty())return null;
		Double d = Double.POSITIVE_INFINITY;
		for(Entry<E, Double> en : tier3){
			if(en.getTime()<d){
				d=en.getTime();
			}
		}
		return d;
	}
	
	//various getters
	public double getCur() {
		return cur;
	}

	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}

}
