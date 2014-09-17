package edu.cmu.cs.cs214.hw4.core.SpecialTiles;


import edu.cmu.cs.cs214.hw4.core.player.Player;
import edu.cmu.cs.cs214.hw4.core.BoardReferee;
import edu.cmu.cs.cs214.hw4.core.Location;

public class NegativePoints extends SpecialTile{
	public static final int COST = 3;
	public static final int ID = 1;
	public static final String SPECIALNAME = "NEGATIVEPOINTS";
	
	public NegativePoints(Player player) {
		super(player, COST, SPECIALNAME,ID);
	}
	
	@Override
	public void applySpecialEffect(BoardReferee boardReferee, Location loc) {
		int currentScore = boardReferee.getCurrentScore();
		boardReferee.setCurrentScore(currentScore*(-1));
	}
	
	@Override
	public String commit() {
		String comment = "Negative Points Special Tile\n" +
				"Cost: " + COST + " points per tile.\n" +
				"Function: The word that activated this tile is scored negatively \n" +
				"for the player who activated the tile; i.e., the player loses \n"+
				"(rather than gains) the points for the played word.;\n";
		return comment;
	}
}
