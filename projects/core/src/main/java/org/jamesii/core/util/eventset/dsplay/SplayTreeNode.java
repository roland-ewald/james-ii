/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.dsplay;

import java.util.LinkedList;

/**
 * 
 * class for a node in a splay tree
 * 
 * @author Richard Knorrek (richard.knorrek@uni-rostock.de)
 *
 * @param <E>
 */
public class SplayTreeNode<E,K> {
	
	private LinkedList<E> data;
	private K key;
	private SplayTreeNode<E,K> parent, left, right;
	
	//size of the subtree that has this node as its root
	
	//various getters and setters
	
	public SplayTreeNode(E data, K key){
		parent=null;
		left=null;
		right=null;
		this.data= new LinkedList<>();
		this.data.add(data);
		this.key=key;
	}
	
	public LinkedList<E> getData() {
		return data;
	}
	
	public void addToData(E e){
		this.data.add(e);
	}
	
	public E getfirstData(){
		return this.data.getFirst();
	}
	
	public void setData(LinkedList<E> newdata){
		this.data=newdata;
	}
	
	public boolean dataIsEmpty(){
		return this.data.isEmpty();
	}
	
	public K getKey(){
		return key;
	}
	
	public void setKey(K key){
		this.key=key;
	}
	
	public SplayTreeNode<E,K> getParent() {
		return parent;
	}
	public void setParent(SplayTreeNode<E,K> parent) {
		this.parent = parent;
	}
	
	
	public SplayTreeNode<E,K> getLeft() {
		return left;
	}
	
	
	public void setLeft(SplayTreeNode<E,K> left) {
		this.left = left;
	}
	
	
	public SplayTreeNode<E,K> getRight() {
		return right;
	}
	public void setRight(SplayTreeNode<E,K> right) {
		this.right = right;
	}
	
	public int getSize(){
		int l,r;
		if(this.getLeft()==null)l=0;
		else l=this.getLeft().getSize();
		if(this.getRight()==null)r=0;
		else r=this.getRight().getSize();
		return data.size()+l+r;
	}

}
