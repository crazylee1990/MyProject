package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;


/**
 * This is the Tile bag class with 100 tiles at first and 
 * pops one tile a time randomly every time the player draw
 * one tile
 * @author Chao
 *
 */
public class TileBag {
	private static final int TOTALTILES = 100;
	private ArrayList<Tile> tiles;
	
	public TileBag() {
		tiles = new ArrayList<Tile>(TOTALTILES);
		createTileBag();
	}
	
	private void createTileBag() {
		// 100 tiles in total;
		addTile('A', 9);
		addTile('B', 2);
		addTile('C', 2);
		addTile('D', 6);
		addTile('E', 12);
		addTile('F', 2);
		addTile('G', 3);
		addTile('H', 2);
		addTile('I', 9);
		addTile('J', 1);
		addTile('K', 1);
		addTile('L', 4);
		addTile('M', 2);
		addTile('N', 6);
		addTile('O', 8);
		addTile('P', 2);
		addTile('Q', 1);
		addTile('R', 6);
		addTile('S', 4);
		addTile('T', 6);
		addTile('U', 4);
		addTile('V', 2);
		addTile('W', 2);
		addTile('X', 1);
		addTile('Y', 2);
		addTile('Z', 1);
	}
	
	/**
	 * this private method is called to add tile in the tile bag 
	 * pre-condition: the number of tiles in the bag is less than 100;
	 * @param letter is the specific letter tile you want to add
	 * @param num- the num of tiles you want to add with the letter
	 */
	private void addTile(char letter, int num) {
		if ((tiles.size() + num) < 100){
			for (int i=0; i<num; i++){
				tiles.add(new Tile(letter));
			}
		}
	}
	
	public boolean isEmpty() {
		return tiles.size()==0;
	}
	
	public int numOfTiles() {
		return tiles.size()+1;
	}
	
	public boolean addTile(Tile t) {
		if(tiles.size()>TOTALTILES){
			return false;
		}else {
			tiles.add(t);
			return true;
		}
	}
	
	public Tile getTile() {
		int index;
		if (!isEmpty()){
			index = (int)(Math.random()*(numOfTiles()-1));
			return tiles.remove(index);
		}else{
			throw new NullPointerException("The tiles Bag has been empty");
		}	
	}
	
	@Override
	public String toString(){
		return tiles.size() + " tiles left";
	}
	
}
