package edu.cmu.cs.cs214.hw4.core.dictionary;

public class RedBlackTree extends java.lang.Object {
	private static final int BLACK = 0;
	private static final int RED = 1;
	private RedBlackNode rootNode;
	private RedBlackNode externalNull;

	/**
	 * construct a empty red black tree with root node pointing to nil
	 */
	public RedBlackTree() {
		externalNull = new RedBlackNode(null, BLACK, null, null, null);
		rootNode = externalNull;
	}

	/**
	 * Time Analysis: the method will search from root node and go to
	 * left/right child until it find the v or return false, so the best
	 * case big O(1), worst case is O(log(n)) The boolean contains() returns
	 * true if the String v is in the RedBlackTree and false otherwise. It
	 * counts each comparison it makes in the variable recentCompares.
	 * 
	 * @param v
	 *                the value to search for
	 * @return true if v is in the tree, false otherwise;
	 */
	public boolean contains(java.lang.String v) {
		RedBlackNode currentNode = rootNode;
		while (currentNode != externalNull) {
			if (currentNode.getData() == null) {
				return false;
			} else if (currentNode.getData().equals(v)) {
				return true;
			} else if (currentNode.getData().compareTo(v) < 0) {
				currentNode = currentNode.getRc();
			} else {
				currentNode = currentNode.getLc();
			}
		}
		return false;
	}

	/**
	 * Time analysis: best case is one step, namely O(1), worst case is
	 * O(log(n)) The insert() method places a data item into the tree.
	 * pre-condition:memory is available for insertion
	 * 
	 * @param value
	 *                is an integer to be inserted
	 */
	public void insert(RedBlackTree T, java.lang.String value) {
		RedBlackNode z = new RedBlackNode(value, RED, externalNull,
				externalNull, externalNull);
		RedBlackNode y = this.externalNull;
		RedBlackNode x = this.rootNode;
		while (x != this.externalNull) {
			y = x;
			if (z.getData().compareTo(x.getData()) < 0) {
				x = x.getLc();
			} else {
				x = x.getRc();
			}
		}
		z.setP(y);
		if (y == externalNull) {
			rootNode = z;
		} else {
			if (z.getData().compareTo(y.getData()) < 0) {
				y.setLc(z);
			} else {
				y.setRc(z);
			}
		}
		z.setLc(externalNull);
		z.setRc(externalNull);
		z.setColor(RED);
		this.RBInsertFixup(this, z);
	}

	/**
	 * Time Analysis: since it is only for one node every time, the steps
	 * are finite so it is bit Theta (1),no best or worst cases Fixing up
	 * the tree so that Red Black Properties are preserved. This would
	 * normally be a private method.
	 * 
	 * @param z is the new node
	 */
	private void RBInsertFixup(RedBlackTree T, RedBlackNode z) {
		RedBlackNode y;
		// System.out.print(z.getData()+' ');
		while (z.getP().getColor() == RED) {
			if (z.getP() == z.getP().getP().getLc()) {
				y = z.getP().getP().getRc();
				if (y.getColor() == RED) {
					z.getP().setColor(BLACK);
					y.setColor(BLACK);
					z.getP().getP().setColor(RED);
					z = z.getP().getP();
				} else {// the uncle is black
					if (z == z.getP().getRc()) {
						z = z.getP();
						leftRotate(T, z);
					}
					z.getP().setColor(BLACK);
					z.getP().getP().setColor(RED);
					rightRotate(T, z.getP().getP());
				}
			} else {
				y = z.getP().getP().getLc();
				if (y.getColor() == RED) {
					z.getP().setColor(BLACK);
					y.setColor(BLACK);
					z.getP().getP().setColor(RED);
					z = z.getP().getP();
				} else {
					if (z == z.getP().getLc()) {
						z = z.getP();
						rightRotate(T, z);
					}
					z.getP().setColor(BLACK);
					z.getP().getP().setColor(RED);
					leftRotate(T, z.getP().getP());
				}
			}

		}
		T.rootNode.setColor(BLACK);
	}

	/**
	 * Time Analysis: since it is only for one node every time, the steps
	 * are finite so it is bit Theta (1),no best or worst cases leftRotate()
	 * performs a single left rotation. This would normally be a private
	 * method. pre-condition: the right child of X can not be nil && the
	 * parent of root node is nil
	 * 
	 * @param x
	 *                is the new node need to rotate
	 */
	private void leftRotate(RedBlackTree T, RedBlackNode x) {
		RedBlackNode y;
		if (x.getRc() != T.externalNull
				&& T.rootNode.getP() == T.externalNull) {
			y = x.getRc();
			x.setRc(y.getLc());
			y.getLc().setP(x);
			y.setP(x.getP());

			if (x.getP() == T.externalNull) {
				T.rootNode = y;
			} else {
				if (x == x.getP().getLc()) {
					x.getP().setLc(y);
				} else {
					x.getP().setRc(y);
				}
			}
			y.setLc(x);
			x.setP(y);
		}
	}

	/**
	 * Time Analysis: since it is only for one node every time, the steps
	 * are finite so it is bit Theta (1),no best or worst cases
	 * rightRotate() performs a single right rotation This would normally be
	 * a private method. pre-condition: x's left child cannot be nil; and
	 * the parent of the root is nil;
	 * 
	 * @param x
	 *                is the new node need to rotate
	 */
	private void rightRotate(RedBlackTree T, RedBlackNode x) {
		RedBlackNode y;
		if (x.getLc() != T.externalNull
				&& T.rootNode.getP() == T.externalNull) {
			y = x.getLc();
			x.setLc(y.getRc());
			y.getRc().setP(x);
			y.setP(x.getP());

			if (x.getP() == T.externalNull) {
				T.rootNode = y;
			} else {
				if (x == x.getP().getLc()) {
					x.getP().setLc(y);
				} else {
					x.getP().setRc(y);
				}
			}
			y.setRc(x);
			x.setP(y);
		}
	}
}
