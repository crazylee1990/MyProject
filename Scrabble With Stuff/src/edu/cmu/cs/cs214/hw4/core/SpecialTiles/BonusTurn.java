package edu.cmu.cs.cs214.hw4.core.SpecialTiles;

import edu.cmu.cs.cs214.hw4.core.BoardReferee;
import edu.cmu.cs.cs214.hw4.core.Location;
import edu.cmu.cs.cs214.hw4.core.player.*;

public class BonusTurn extends SpecialTile {
	public static final int COST = 3;
	public static final int ID = 4;
	public static final String SPECIALNAME = "BONUSTURN";

	public BonusTurn(Player player) {
		super(player, COST, SPECIALNAME, ID);
	}
	
	@Override
	public String commit(){
		String comment = "Bonus Turn Tile\n" +
				"Cost: " + COST + " points per tile.\n" +
				"Function: this tile will give its owner" +
				"player one bonus turn to play when it is enabled";
		
		return comment;
	}

	@Override
	public void applySpecialEffect(BoardReferee boardReferee, Location loc) {
		Player.setBonusTurn(Player.getBonusTurn()+1);
	}
}
