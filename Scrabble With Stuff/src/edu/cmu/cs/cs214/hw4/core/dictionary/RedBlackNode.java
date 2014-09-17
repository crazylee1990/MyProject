package edu.cmu.cs.cs214.hw4.core.dictionary;


public class RedBlackNode extends java.lang.Object{
	
	private String data;
	private int color;
	private RedBlackNode p;
	private RedBlackNode lc;
	private RedBlackNode rc; 
	
	
	/**
	 * Construct a RedBlackNode with data, color, 
	 * parent pointer, left child pointer and right child pointer.
	 * @param data: a simple value held in the tree
	 * @param color: either RED or BLACK
	 * @param p: the parent pointer
	 * @param lc: the pointer to the left child (will be null only for the node that represents all external nulls.)
	 * @param rc: the pointer to the right child (will be null only for the node that represents all external nulls.
	 */
	public RedBlackNode(java.lang.String data, int color, 
			RedBlackNode p, RedBlackNode lc, RedBlackNode rc) {
		this.data = data;
		this.color = color;
		this.p = p;
		this.lc = lc;
		this.rc = rc;
	}
	
	/**
	 * @return The getColor() method returns RED or BLACK.
	 */
	public int getColor() {
		return this.color;
	}
	
	/**
	 * @return The getData() method returns the data in the node.
	 */
	public java.lang.String getData() {
		return this.data;
	}
	
	/**
	 * @return The getLc() method returns the left child of the RedBlackNode.
	 */
	public RedBlackNode getLc() {
		return this.lc;
		
	}

	/**
	 * @return The getP() method returns the parent of the RedBlackNode.
	 */
	public RedBlackNode getP() {
		return this.p;
	}
	
	/**
	 * @return The getRc() method returns the right child of the RedBlackNode.
	 */
	public RedBlackNode getRc() {
		return this.rc;
	}
	
	/**
	 * The setColor() method sets the color of the RedBlackNode.
	 */
	public void setColor(int color) {
		this.color =color;
	}
	
	/**
	 *  The setData() method sets the data or key of the RedBlackNode.
	 */
	public void setData(java.lang.String data) {
		this.data = data;
	}
	
	/**
	 * The setLc() method sets the left child of the RedBlackNode.
	 */
	public void setLc(RedBlackNode lc) {
		this.lc = lc;
	}
	
	/**
	 * The setP() method sets the parent of the RedBlackNode.
	 */
	public void setP(RedBlackNode p) {
		this.p = p;
	}
	
	/**
	 * The setRc() method sets the right child of the RedBlackNode.
	 */
	public void setRc(RedBlackNode rc) {
		this.rc = rc;
	}
	
	/**
	 * The toString() methods returns a string representation of the RedBlackNode.
	 */
	@Override
	public java.lang.String toString() {
		return this.data;
	}
	
}
