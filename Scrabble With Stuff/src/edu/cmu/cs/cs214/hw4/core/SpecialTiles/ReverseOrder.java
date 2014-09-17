package edu.cmu.cs.cs214.hw4.core.SpecialTiles;

import edu.cmu.cs.cs214.hw4.core.BoardReferee;
import edu.cmu.cs.cs214.hw4.core.Location;
import edu.cmu.cs.cs214.hw4.core.player.Player;

public class ReverseOrder extends SpecialTile{
	public static final int COST = 3;
	public static final int ID = 2;
	public static final String SPECIALNAME = "REVERSEORDER";
	
	public ReverseOrder(Player player) {
		super(player, COST, SPECIALNAME,ID);
	}
	
	@Override
	public void applySpecialEffect(BoardReferee boardReferee, Location loc) {
		boardReferee.reverseOrder = ! boardReferee.reverseOrder;
	}
	
	@Override
	public String commit() {
		String comment = "Negative Points Special Tile\n" +
				"Cost: " + COST + " points per tile.\n" +
				"Function: The turn ends as usual, but after\n " +
				"this tile is activated play continues in the\n" +
				" reverse of the previous order.\n";
		return comment;
	}
}
