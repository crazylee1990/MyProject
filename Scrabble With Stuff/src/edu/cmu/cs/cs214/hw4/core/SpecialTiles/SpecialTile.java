package edu.cmu.cs.cs214.hw4.core.SpecialTiles;

import edu.cmu.cs.cs214.hw4.core.BoardReferee;
import edu.cmu.cs.cs214.hw4.core.Location;
import edu.cmu.cs.cs214.hw4.core.player.Player;

public abstract class SpecialTile {
	private final String SpecialName;
	public final Player Player;
	private final int Price;
	private int ID;
	
	public SpecialTile(Player player, int price, String name, int ID) {
		this.Player = player;
		this.Price = price;
		this.SpecialName = name;
		this.ID = ID;
	}
	
	public Player getWhoseTile() {
		return Player;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getPrice() {
		return Price;
	}
	
	public abstract String commit();
	
	public abstract void applySpecialEffect(BoardReferee boardReferee, Location loc);
	
	@Override
	public String toString(){
		return SpecialName;
	}
	
	public boolean same(SpecialTile tile) {
		return SpecialName.equals(tile.toString());
	}
}
