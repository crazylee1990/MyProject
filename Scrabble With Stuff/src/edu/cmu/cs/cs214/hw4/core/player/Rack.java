package edu.cmu.cs.cs214.hw4.core.player;

import java.util.ArrayList;

import edu.cmu.cs.cs214.hw4.core.Tile;
import edu.cmu.cs.cs214.hw4.core.TileBag;

public class Rack {
	private static final int MAXTIELS = 7;
	private int numOfTiles;
	private ArrayList<Tile> personalTiles;
	
	public Rack() {
		personalTiles = new ArrayList<Tile>();
		this.numOfTiles = personalTiles.size();
	}
	
	public void addTiles(TileBag tileBag) {
		numOfTiles = getNumOfTiles();
		if(numOfTiles == MAXTIELS){
			return;
		}
		int need = MAXTIELS - numOfTiles;
		for(int i=0; i<need; i++){
			if(!tileBag.isEmpty()){
				personalTiles.add(tileBag.getTile());
			}
		}
		numOfTiles = personalTiles.size();
	}
	
	public void recallTile(Tile t){
		personalTiles.add(t);
	}
	
	public int getNumOfTiles() {
		return personalTiles.size();
	}
	
	public int getMaxtiels() {
		return MAXTIELS;
	}
	
	public void removeTile(int index){
		personalTiles.remove(index);
	}
	
	public Tile getTile(int index) {
		return personalTiles.get(index);
	}
	
	public boolean contains(char letter) {
		for (Tile iteratorTile:personalTiles){
			if (iteratorTile.getLetter() == letter){
				return true;
			}
		}
		return false;
	}
	
	public boolean exchangeTiles(TileBag bag, int num) {
		if(num>personalTiles.size()){
			return false;
		}else{
			for(int i=0; i<num; i++){
				bag.addTile(personalTiles.get(0));
				removeTile(0);
				personalTiles.add(bag.getTile());
			}
			return true;
		}
	}
	
	public  ArrayList<Tile> showTiles() {
		return personalTiles;
	}
}
