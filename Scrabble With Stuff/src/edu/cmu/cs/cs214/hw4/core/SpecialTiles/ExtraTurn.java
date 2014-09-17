package edu.cmu.cs.cs214.hw4.core.SpecialTiles;

import edu.cmu.cs.cs214.hw4.core.BoardReferee;
import edu.cmu.cs.cs214.hw4.core.Location;
import edu.cmu.cs.cs214.hw4.core.player.Player;

public class ExtraTurn extends SpecialTile{
	public static final int COST = 3;
	public static final int ID = 5;
	public static final String SPECIALNAME = "EXTRATURN";
	
	public ExtraTurn(Player player) {
		super(player, COST, SPECIALNAME,ID);
	}
	
	/**
	 * do nothing here because we just need to know the owner of this tile
	 */
	@Override
	public void applySpecialEffect(BoardReferee boardReferee, Location loc) {
		getWhoseTile().setExtraTurn(1);
	}
	
	@Override
	public String commit() {
		String comment = "Extra-turn: When this tile is triggered, the player who originally\n " +
				"played this tile gets an extra turn. The extra-turn tile has no immediate\n" +
				" effect when the tile is played, and it has no effect on the player who\n " +
				"triggers the tile. Suppose that Alice plays an extra-turn tile and Bob later" +
				" triggers the tile. When Bob triggers the tile, Bob's turn is completed and\n" +
				" is scored as usual, then Alice gets a turn in which she can take any normal\n" +
				" game actions (i.e. buying and placing a special tile and placing letter tiles).\n" +
				" After Alice's extra turn is complete, play continues with whomever would have \n" +
				"ordinarily followed Bob, the triggering player. (If Alice and Bob are playing a \n" +
				"two player game, this means Alice gets two turns in a row.) If multiple extra-turn \n" +
				"tiles are triggered in a single turn, you may award the associated players their \n" +
				"extra turns in any arbitrary deterministic order. A player may not trigger her own\n" +
				" extra-turn tile.";
		return comment;
	}
}
