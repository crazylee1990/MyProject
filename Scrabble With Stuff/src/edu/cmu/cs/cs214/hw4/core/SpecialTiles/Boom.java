package edu.cmu.cs.cs214.hw4.core.SpecialTiles;
import java.util.ArrayList;

import edu.cmu.cs.cs214.hw4.core.Board;
import edu.cmu.cs.cs214.hw4.core.BoardFeature;
import edu.cmu.cs.cs214.hw4.core.BoardReferee;
import edu.cmu.cs.cs214.hw4.core.Location;
import edu.cmu.cs.cs214.hw4.core.Tile;
import edu.cmu.cs.cs214.hw4.core.player.Player;

public class Boom extends SpecialTile{
	public static final int COST = 3;
	public static final int ID = 3;
	public static final String SPECIALNAME = "BOOM";
	
	public Boom(Player player) {
		super(player, COST, SPECIALNAME,ID);
	}
	
	@Override
	public void applySpecialEffect(BoardReferee boardReferee, Location loc) {
		Board board = boardReferee.getBoard();
		ArrayList<Location> boomLoc = new ArrayList<Location>();
		int direction = boardReferee.getAlongXState();
		int score = boardReferee.getCurrentScore();
		if(direction == 1){
			for(int i = loc.getX()-3; i<= loc.getX()+3; i++){
				Location newLoc = new Location(i+1, loc.getY()+1);
				if(!boomLoc.contains(newLoc)){
					boomLoc.add(newLoc);
				}
			}
			
		}else if(direction == 0){
			for(int i = loc.getY()-3; i<= loc.getY()+3; i++){
				Location newLoc = new Location(loc.getX()+1,i+1);
				if(!boomLoc.contains(newLoc)){
					boomLoc.add(newLoc);
				}
			}
		}else{
			throw new IllegalArgumentException("The tiles are not aligned");
		}
		
		for(Location tempLoc:boomLoc){
			BoardFeature feature = board.getBoardFeature(tempLoc);
			int multiple = 1;
			Tile tile = board.getTile(tempLoc);
			if(feature == BoardFeature.DLS){
				multiple = 2;
			}else if(feature == BoardFeature.TLS){
				multiple = 3;
			}
			if(tile != null){
				score -= multiple * tile.getPoints();
			}
			else {
				continue;
			}	
		}		
		boardReferee.setCurrentScore(score);
		
		for(int i = loc.getX()-3; i<= loc.getX()+3; i++){
			for (int j = loc.getY()-3; j <= loc.getY()+3; j++) {
				Location tempLoc = new Location(i+1, j+1);
				if(board.getTile(tempLoc) != null){
					board.removeTile(tempLoc);
				}
				if(board.getSpecialTile(tempLoc) !=null){
					board.removeSpeicalTile(tempLoc);
				}
			}
		}
		if(board.isEmptyBoard()){
			boardReferee.setFirstRound(true);
		}
		
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
