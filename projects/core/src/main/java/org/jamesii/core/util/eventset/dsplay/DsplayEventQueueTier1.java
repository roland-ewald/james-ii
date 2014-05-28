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
 * Dsplay Event Queue Tier 1
 * Splay tree for close future events
 * @author Richard Knorrek (richard.knorrek@uni-rostock.de)
 *
 * @param <E>
 */

public class DsplayEventQueueTier1<E> {
	
	//
	private SplayTree<E,Double> tier1;
	
	private int num;
	
	public DsplayEventQueueTier1(){
		this.tier1 = new SplayTree<>();
		num=0;
	}
	
	public void enqueue(E event, Double time){
		tier1.insert(event,time);
		num++;
	}
	
	public Entry<E, Double> dequeue(){
		if(isEmpty())return null;
		tier1.getMin();
		E res = tier1.getRoot().getData().remove();
		Double t = tier1.getRoot().getKey();
		if(tier1.getRoot().getData().isEmpty()){
			tier1.delete(t);
		}
		num--;
		return new Entry<>(res,t);
	}
	
	public LinkedList<E> dequeueAll(){
		if(isEmpty())return null;
		tier1.getMin();
		LinkedList<E> res = tier1.getRoot().getData();
		tier1.delete(tier1.getRoot().getKey());
		num=num-res.size();
		return res;
	}
	
	public LinkedList<E> dequeueAll(Double time){
		if(isEmpty())return null;
		tier1.splay(time);
		if(tier1.getRoot().getKey().compareTo(time)==0){
			LinkedList<E> res= tier1.getRoot().getData();
			tier1.delete(time);
			num=num-res.size();
			return res;
		}
		return null;
	}
	
	//vastly inefficient without a timestamp!
	public Entry<E, Double> dequeue(E event){
		Entry<E, Double> res=null;
		res=lookup(event);
		if(res!=null){
			dequeue(res.getEvent(),res.getTime());
		}
		return res;
	}
	
	public Entry<E, Double> dequeue(E event, Double time){
		if(this.isEmpty())return null;
		E res=null;
		tier1.splay(time);
		if(tier1.getRoot().getKey().compareTo(time)==0){
			for(E en : tier1.getRoot().getData()){
				if(en==event)res=en;
				tier1.getRoot().getData().remove(res);
				if(tier1.getRoot().getData().isEmpty())tier1.delete(time);
				break;
			}
		}
		if(res!=null){
			num--;
			return new Entry<>(res,time);
		}
		return null;
	}
	
	public Entry<E, Double> lookup(E event){
		if(this.isEmpty())return null;
		return lookup(event, this.tier1.getRoot());
	}
	
	public Entry<E, Double> lookup (E event, SplayTreeNode<E,Double> n){
		for(E en : n.getData()){
			if(en==event)return new Entry<>(en,n.getKey());
		}

		Entry<E, Double> r,l;
		if(n.getLeft()!=null)l=lookup(event, n.getLeft());
		else l=null;
		if(n.getRight()!=null)r=lookup(event, n.getRight());
		else r=null;
		if(l!=null)return l;
		else return r;
	}
	
	//size of tier 1
	public int size(){
		return num;
		//return tier1.size();
	}
	
	public boolean isEmpty(){
		return this.size()==0;
	}
	
	public void t2tot1(DsplayEventQueueTier2<E> tier2){
		LinkedList<Entry<E, Double>> data = tier2.t2tot1();
		if(data!=null){			
			for(Entry<E, Double> en : data){
				tier1.insert(en.getEvent(), en.getTime());
				num++;
			}
		}
	}
	
	public Double getMinTS(){
		if(isEmpty())return null;
		SplayTreeNode<E,Double> temp= this.tier1.getRoot();
		while(temp.getLeft()!=null){
			temp=temp.getLeft();
		}
		return temp.getKey();
	}
}
