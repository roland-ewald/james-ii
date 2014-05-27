/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.dsplay;


import java.util.ArrayList;
import java.util.LinkedList;

import org.jamesii.core.util.eventset.Entry;

/**
 * 
 * Dsplay Event Queue Tier 2
 * an array of unsorted buckets
 * 
 * @author Richard Knorrek (richard.knorrek@uni-rostock.de)
 *
 */

public class DsplayEventQueueTier2<E> {
	
	//Set equal to t3_min whenever events are being transferred from T3 to T2 
	//It is used to calculate the correct bucket-index when an event is enqueued in T2
	private double start;
	
	//Minimum timestamp which events can be enqueued in T2.
	//This is updated when T2 transfers a bucket of events to T1
	private double cur;
			
	//Number of events in T2
	private int num;
	
	//Bucketwidth of T2
	private double bw;
			
	//Bucket-index of T2 when events are transferred from T2 to T1
	private int idx;
	
	private double max;
	
	//array of unsorted buckets
	private ArrayList<LinkedList<Entry <E, Double>>> tier2;
	
	public DsplayEventQueueTier2(){
		this.start=Double.NEGATIVE_INFINITY;
		this.cur=Double.NEGATIVE_INFINITY;
		this.bw=0.0;
		this.num=0;
		this.idx=0;
		this.tier2=null;
		this.max=0.0;
	}
	
	//size of tier 2
	public int size(){
		return num;
	}
	
	public boolean isEmpty(){
		return num==0;
	}
	
	public Entry<E, Double> lookup(E event){
		if(isEmpty())return null;
		for(int i=idx;i<tier2.size();i++){
			if(tier2.get(i)!=null && !tier2.get(i).isEmpty()){
				for(Entry<E, Double> en : tier2.get(i)){
					if(en.getEvent()==event){
						return en;
					}
				}
			}
		}
		return null;
	}
	
	public void enqueue(E event, Double time){
		int index = 0;
		if(bw!=0.0){			
			index= (int)((time-start)/bw);
		}
		if(time.doubleValue()==max)index=tier2.size()-1;
//		if(index<idx)System.out.println("INDEXFEHLER! time "+time+" cur "+cur+" max "+max+" bw "+bw+" index "+index+" idx "+idx);
		tier2.get(index).add(new Entry<>(event, time));
		num++;
	}
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	public Entry<E, Double> dequeue(E event){
		if(isEmpty())return null;
		for(int i=idx;i<tier2.size();i++){
			for(Entry<E, Double> en:tier2.get(i)){
				if(en.getEvent()==event){
					tier2.get(i).remove(en);
					num--;
					return en;
				}
			}
		}
		return null;
	}
	
	public LinkedList<E> dequeueAll(Double time){
		LinkedList<E> res = new LinkedList<>();
		int index=new Double(((time-start)/bw)).intValue();
		if(time==max)index--;
		if(index>=idx){
			for(Entry<E, Double> en: tier2.get(index)){
				if(en.getTime().compareTo(time)==0){
					res.add(en.getEvent());
				}
			}
			for(E en:res){
				tier2.get(index).remove(new Entry<>(en,time));
				num--;
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param event
	 * @param time
	 * @return
	 */
	public Entry<E, Double> dequeue(E event, Double time){
		if(time.doubleValue()<start||time.doubleValue()>max)return null;
		if(isEmpty())return null;
		int index = new Double(((time-start)/bw)).intValue();
		if(time==max)index--;
		for(Entry<E, Double> en:tier2.get(index)){
			if(en.getEvent()==event&&en.getTime()==time){
				tier2.get(index).remove(en);
				num--;
				return en;
			}
		}
		return null;
	}
	
	//method for transferring all entries from tier 3 to tier 2
	//after this method is finished, tier 3 will be empty with all values appropriately set
	public void t3tot2(DsplayEventQueueTier3<E> tier3){
		//setting values
		bw=(tier3.getMax()-tier3.getMin())/tier3.size();
		tier2 = new ArrayList<>(tier3.size());
		num=tier3.size();
		start=tier3.getMin();
		idx=0;
		cur=tier3.getMin();
		max=tier3.getMax();
		
		//creating buckets
		for(int i=0; i<num;i++){
			tier2.add(new LinkedList<Entry<E,Double>>());
		}
		
		//putting entries from tier 3 in their appropriate buckets in tier 2
		int index=0;
		LinkedList<Entry<E, Double>> t3 = tier3.t3tot2();
		for(Entry<E, Double> en: t3){
			if(bw!=0.0)index=(int)((en.getTime().doubleValue()-start) /bw);
			if(en.getTime()==max)index=num-1;
//			if(index<0||index>=tier2.size())System.out.println("index:"+index+"erg:"+(int)((en.getTime().doubleValue()-start) /bw)+"en.time"+en.getTime()+"start"+start+"bw"+bw);
			tier2.get(index).add(en);
		}
	}
	
	/**
	 * moves index to next nonempty bucket, if all remaining buckets are empty, moves index to last bucket
	 * returns bucket of entries at current index in the bucket-array, deletes that bucket
	 * 
	 * @return
	 */
	public LinkedList<Entry<E, Double>> t2tot1(){
		while(tier2.get(idx).isEmpty()&&idx<tier2.size()-1){
			idx++;
			cur=cur+bw;
		}
		if(tier2.get(idx).size()==0)return null;
		num=num-tier2.get(idx).size();
		LinkedList<Entry<E, Double>> result=tier2.get(idx);
		tier2.set(idx, new LinkedList<Entry<E,Double>>());
		if(idx<tier2.size()-1){
			idx++;
			cur=cur+bw;
		}
		return result;
		
	}

	public Double getMinTS(){
		if(this.isEmpty())return null;
		int index = idx;
		while(this.tier2.get(index).isEmpty()){
			index++;
		}
		Double d = Double.POSITIVE_INFINITY;
		for(Entry<E, Double> en : this.tier2.get(index)){
			if(en.getTime()<d){
				d=en.getTime();
			}
		}
		return d;
	}
	
	public double getCur() {
		return cur;
	}
}
