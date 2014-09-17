package edu.cmu.cs.cs214.hw4.core.player;

import java.util.ArrayList;

import edu.cmu.cs.cs214.hw4.core.Tile;
import edu.cmu.cs.cs214.hw4.core.TileBag;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.BonusTurn;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.Boom;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.ExtraTurn;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.NegativePoints;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.ReverseOrder;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;

public class Player {
	private final String NAME;
	private int score;
	private Rack rack;
	private ArrayList<SpecialTile> specialTiles;
	private boolean isFinshied;
	private int bonusTurn;
	private int extraTurn;
	
	public Player(String name) {
		this.NAME = name;
		this.score = 0;
		this.rack = new Rack();
		this.specialTiles = new ArrayList<SpecialTile>();
		this.isFinshied = false;
		this.bonusTurn = 0;
	}

	public String getName() {
		return NAME;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getBonusTurn() {
		return bonusTurn;
	}
	
	public void setBonusTurn(int bonusTurn) {
		this.bonusTurn += bonusTurn;
	}
	
	public void setExtraTurn(int extraTurn) {
		this.extraTurn += extraTurn;
	}
	
	public int getExtraTurn() {
		return extraTurn;
	}
	
	public ArrayList<SpecialTile> getSpecialTiles() {
		return specialTiles;
	}
	
	public ArrayList<Tile> getTiles() {
		return rack.showTiles();	
	}
	
	public boolean isFinished(boolean newState) {
		boolean temp = isFinshied;//return the current state of the player
		isFinshied = newState;//set the new state for the player
		return temp;
	}
	
	public void addTiles(TileBag tileBag){
		rack.addTiles(tileBag);
	}
	
	public void recall(Tile tile) {
		if(rack.getNumOfTiles() < rack.getMaxtiels()){
			rack.recallTile(tile);
		}
		
	}
	
	public void addScore(int score) {
		this.score += score;
	}
	/**
	 * player can buy special tiles using the scores they win
	 * @param index is the number of special tile
	 */
	public boolean buySpecialTile(int index) {
		SpecialTile wanted;
		switch (index) {
		case 1:
			wanted = new NegativePoints(this);
			break;
		case 2:
			wanted = new ReverseOrder(this);
			break;
		case 3:
			wanted = new Boom(this);
			break;
		case 4:
			wanted = new BonusTurn(this);
			break;
		case 5: 
			wanted = new ExtraTurn(this);
			break;
		default:
			throw new IllegalArgumentException("we do not have such special Tile");
		}
		if (wanted.getPrice()>this.score){
			return false;
		}else{
			specialTiles.add(wanted);
			score -= wanted.getPrice();
			return true;
		}
	}
	
	public boolean isContained(char letter) {
		return rack.contains(letter);
	}
	
	public Tile useTile(int index) {
		if(index < rack.getNumOfTiles()){
			Tile temp = rack.getTile(index);
			rack.removeTile(index);
			return temp;
		}else {
			throw new IllegalArgumentException("Player" + this.NAME
					+ "does not have No."+ (index+1)+" Tile");
		}
	}
	
	public SpecialTile useSpecialTile(String special) {
		SpecialTile temp;
		for (SpecialTile s: specialTiles){
			if(s.toString().equals(special)){
				temp = s;
				specialTiles.remove(s);
				return temp;
			}
		}
		throw new IllegalArgumentException("Player" + this.NAME
					+ "does not have this special tile");
	}
	
	public boolean exchangeTiles(TileBag bag, int num) {
		return rack.exchangeTiles(bag, num);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Player:");
		s.append(this.NAME);
		s.append(" Score:");
		s.append(this.score);
		s.append(" Rack(");

		for (int i=0; i< rack.getNumOfTiles();i++){
			s.append(rack.getTile(i));
			s.append(" ");
		}
		s.append(")");
		s.append("SpecialTiles:");
		
		for (SpecialTile t : specialTiles) {
			s.append(t);
			s.append(" ");
		} 
		return s.toString(); 
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Player){
			return ((Player) o).getName().equals(NAME);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return NAME.hashCode();
	}
	
}
