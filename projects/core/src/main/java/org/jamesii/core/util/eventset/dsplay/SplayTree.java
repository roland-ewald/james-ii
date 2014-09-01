/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.dsplay;

import java.util.ArrayList;

/**
 * 
 * the splay tree data structure
 * (a self-adjusting binary search tree)
 * 
 * @author Richard Knorrek (richard.knorrek@uni-rostock.de)
 *
 * @param <E>
 */

public class SplayTree<E, K extends Comparable<K>> {
	
	private SplayTreeNode<E,K> root;
	
	public SplayTree(){
		this.root = null;
	}
	
	public SplayTree(SplayTreeNode<E,K> node){
		this.root=node;
	}
	
	
	/**
	 * returns true if empty, false if not
	 * 
	 * @return true if empty, false if not
	 */
	public boolean isEmpty(){
		return root==null;
	}
	
	/**
	 * return size of tree
	 * 
	 * @return size of the tree
	 */
	public int size(){
		if(this.root==null) return 0;
		else{
			return this.root.getSize();
		}
	}
	
	/**
	 * returns node containing k if k is in tree, else return null
	 * also splays node kontaining k to top if k in tree,
	 * otherwise splays largest elem < k or smallest elem > k to top
	 * 
	 * @param k the node to be accessed
	 * @return treenode containign k if k is in tree, else null
	 */
	public SplayTreeNode<E,K> access(K k){
		if(this.isEmpty())return null;
		else{
			//looking for k
			SplayTreeNode<E,K> current = this.root;
			while(true){
				if(current.getKey().compareTo(k)==0){
					splay(current);
					return current;
				}else if(current.getKey().compareTo(k)<0){
					if(current.getRight()!=null){
						current=current.getRight();
					}else{
						splay(current);
						return null;
					}
				}else{
					if(current.getLeft()!=null){
						current=current.getLeft();
					}else{
						splay(current);
						return null;
					}
				}
			}		
		}
	}
	
	
	/**
	 * split this tree into two subtrees
	 * first subtree contains all elements from this tree smaller or equal to e
	 * second subtree contains all elements greater than e
	 * DESTROYS THIS TREE!
	 * 
	 * @param k pivot element
	 * @return ArrayList containing two trees:
	 * first subtree contains all elements from this tree smaller or equal to e
	 * second subtree contains all elements greater than e
	 */
	public ArrayList<SplayTree<E,K>> split(K k){
		ArrayList<SplayTree<E,K>> res = new ArrayList<>();
		if(this.isEmpty()){	
			res.add(new SplayTree<E,K>());
			res.add(new SplayTree<E,K>());
		}else{
			access(k);
			if(this.root.getKey().compareTo(k)>0){
				SplayTreeNode<E,K> t1 = this.root.getLeft();
				if(t1!=null)t1.setParent(null);
				this.root.setLeft(null);
				res.add(new SplayTree<>(t1));
				res.add(this);
			}else{
				SplayTreeNode<E,K> t2 = this.root.getRight();
				if(t2!=null)t2.setParent(null);
				this.root.setRight(null);
				res.add(this);
				res.add(new SplayTree<>(t2));
			}
		}
		return res;
		
	}
	
	/**
	 * joins two splay trees together, assuming all elements of a are smaller than all elements in b
	 * returns null if both given trees are null
	 * returns empty tree if both given trees are empty or one is empty and the other is null
	 * DESTROYS THE TWO GIVEN TREES
	 * 
	 * @param a first tree
	 * @param b second tree
	 * @return joined tree of a and b
	 */
	public SplayTree<E,K> join(SplayTree<E,K> a, SplayTree<E,K> b){
		if(a==null){
			if(b==null)return null;
			return b;
		}else if(a.isEmpty()){
			if(b==null)return a;
			if(b.isEmpty())return a;
			return b;
		}else{
			if(b==null)return a;
			if(b.isEmpty())return a;
			SplayTreeNode<E,K> temp = a.getRoot();
			while(temp.getRight()!=null)temp=temp.getRight();
			a.splay(temp.getKey());
			a.getRoot().setRight(b.getRoot());
			b.getRoot().setParent(a.getRoot());
			return a;			
		}
	}
	
	
	/**
	 * insert e into tree
	 * 
	 * @param e elem to be inserted
	 */
	public void insert(E e, K k){
		if(isEmpty()){
			SplayTreeNode<E, K> n = new SplayTreeNode<>(e, k);
			root=n;
		}else{			
			splay(k);
			if(root.getKey().compareTo(k)==0){
				this.root.addToData(e);
			}else{
				ArrayList<SplayTree<E,K>> x = this.split(k);
				SplayTreeNode<E,K> ins = new SplayTreeNode<>(e,k);
				
				ins.setLeft(x.get(0).getRoot());
				if(!x.get(0).isEmpty())x.get(0).getRoot().setParent(ins);
				
				ins.setRight(x.get(1).getRoot());
				if(!x.get(1).isEmpty())x.get(1).getRoot().setParent(ins);
				
				this.root=ins;
			}
		}	
	}
	
	/**
	 * delete E from tree
	 * 
	 * @param e elem to be deleted
	 */
	public void delete (K k){
		splay(k);
		if(root.getKey().compareTo(k)==0){
			SplayTreeNode<E,K> l,r;
			l=root.getLeft();
			r=root.getRight();
			if(l!=null)l.setParent(null);
			root.setLeft(null);
			if(r!=null)r.setParent(null);
			root.setRight(null);
			root=join(new SplayTree<>(l), new SplayTree<>(r)).getRoot();
		}
	}
	
	
	public void splay(K k){
		if(!this.isEmpty()){
			SplayTreeNode<E, K>temp=this.root;
			while(true){
				if(temp.getKey().compareTo(k)==0){
					break;
				}else if(temp.getKey().compareTo(k)>0){
					if(temp.getLeft()!=null)temp=temp.getLeft();
					else break;
				}else{
					if(temp.getRight()!=null)temp=temp.getRight();
					else break;
				}
			}
			splay(temp);
		}
	}
	
	/**
	 * splay tree so that node is at root
	 * 
	 * @param node node at which tree is splayed
	 */
	public void splay(SplayTreeNode<E,K> node){
		//node already root
		while(node!=this.root){
			if(node.getParent()==this.root){
				//simple zig or zag cases
				rotate(node);
			}else if((node.getParent().getLeft()==node && node.getParent().getParent().getLeft()==node.getParent()) ||
					(node.getParent().getRight()==node && node.getParent().getParent().getRight()==node.getParent())){
				//zig-zig or zag-zag cases
				rotate(node.getParent());
				rotate(node);
			}else{
				rotate(node);
				rotate(node);
			}
		}
	}
	
	//rotate node to left (node is right child of parent)
	//ROOT NODES CAN NOT BE ROTATED!
	public void rotateleft(SplayTreeNode<E,K> node){
		if(node==null) {
    }
		else if(node.getParent()==null) {
    }
		else{
			SplayTreeNode<E,K> p = node.getParent();
			SplayTreeNode<E,K> l = node.getLeft();
			//updating grandparent references
			if(p.getParent()!=null){
				if(p.getParent().getLeft()==p){
					p.getParent().setLeft(node);
				}else if(p.getParent().getRight()==p){
					p.getParent().setRight(node);
				}
			} else{
				this.root=node;
			}			
			node.setParent(p.getParent());
			
			//stitching old parent to nodes left
			node.setLeft(p);
			p.setParent(node);
			
			//stitching old left to old parents right
			p.setRight(l);
			if(l!=null)l.setParent(p);
		}
	}
	
	//rotate node to right (node is left child of parent)
	//ROOT NODES CAN NOT BE ROTATED!
	public void rotateright(SplayTreeNode<E,K> node){
		if(node==null) {
    }
		else if(node.getParent()==null) {
    }
		else{
			SplayTreeNode<E,K> p = node.getParent();
			SplayTreeNode<E,K> r = node.getRight();
			//updating grandparent references
			if(p.getParent()!=null){
				if(p.getParent().getLeft()==p){
					p.getParent().setLeft(node);
					
				}else if(p.getParent().getRight()==p){
					p.getParent().setRight(node);
				}
				
			} else{
				this.root=node;
			}
			node.setParent(p.getParent());
			
			//stitching old parent to nodes right
			node.setRight(p);
			p.setParent(node);
			
			//stitching old right to old parents left
			p.setLeft(r);
			if(r!=null)r.setParent(p);
		}
	}
	
	//rotation convenience method
	//(rotates left if node is right child,
	//rotates right if node is left child)
	//ROOT NODES CAN NOT BE ROTATED!
	public void rotate(SplayTreeNode<E,K> node){
		if(node==null) {
    }
		else if(node.getParent()==null) {
    }
		else if(node.getParent().getRight()==node){
			rotateleft(node);
		}else if(node.getParent().getLeft()==node){
			rotateright(node);
		}
	}
	
	
	public SplayTreeNode<E,K> getRoot(){
		return this.root;
	}
	
	
	public K getMin(){
		SplayTreeNode<E,K> t = this.root;
		if(t==null)return null;
		while(t.getLeft()!=null){
			t=t.getLeft();
		}
		splay(t);
		return t.getKey();
	}
	
//	public void printTree(){
//		if(this.root==null)System.out.println("empty");
//		else System.out.println("size: "+ this.size()+" "+printnode(this.root));
//	}
//	
//	public String printnode(SplayTreeNode<E> node){
//		String left,right;
//		if(node.getLeft()==null)left="empty";
//		else left=printnode(node.getLeft());
//		if(node.getRight()==null)right="empty";
//		else right=printnode(node.getRight());
//		
//		return ("("+node.getData().toString()+", "+left+", "+right+")");
//	}
//	
//	public static void main(String[] args){
//		SplayTree<Double> test = new SplayTree<Double>();
//		test.printTree();
//		test.insert(new Double(1));
//		test.printTree();
//		test.insert(new Double(4));
//		test.printTree();
//		test.insert(new Double(32));
//		test.printTree();
//		test.insert(new Double(12));
//		test.printTree();
//		test.insert(new Double(2));
//		test.printTree();
//		test.insert(new Double(8));
//		test.printTree();
//		test.access(new Double(12));
//		test.printTree();
//		test.delete(new Double(2));
//		test.printTree();
//		
//		
//	}
}
