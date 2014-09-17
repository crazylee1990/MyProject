package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;



/**
 * Location indicates the index of the slot in the board
 * @author Chao
 */
public class Location {
	private int x;
	private int y;
	
	public Location(int x, int y) {
		this.x = check(x-1);//set the x from 0-14
		this.y = check(y-1);//set the y from 0-14
	}
	
	public Location(char column ,int row) {
		this.x = column2X(column);
		this.y = row-1;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	private int check(int p) {
		if(p > 14) return 14;
		if(p < 0)  return 0;
		return p;
	}
	
	private int column2X(char c) {
		if (c < 'A') c = 'A';
		if (c > 'O') c = 'O';
		return c - 'A'; //y from 0-14
	}
	
	private String x2Column(int x) {
		return (String) ""+(char)(x + 'A'); 
	}
	
	/**
	 * This method get the neighbors of a specific location
	 * @return array list of locations 
	 */
	public ArrayList<Location> getNeighbors() {
		ArrayList<Location> neighbors = new ArrayList<Location>();
		if(x>0){// add the left neighbor
			neighbors.add(new Location(this.x, this.y));
		}
		if(x<14){//add the right neighbor
			neighbors.add(new Location(this.x+2, this.y));
		}
		if(y>0){//add the top neighbor
			neighbors.add(new Location(this.x, this.y));
		}
		if(y<14){// add the bottom neighbor
			neighbors.add(new Location(this.x, this.y+2));
		}
		return neighbors;
	}
	
	@Override
	public String toString() {
		String rc = ""+x2Column(this.x)+ (this.y+1);
		return rc;
	}
}
