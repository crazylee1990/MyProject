package edu.cmu.cs.cs214.hw4.core;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the  characteristics of the game board. Basically,
 * the board is a container for board content, we can operate or get access
 * to tile, special tile through the index on the board;
 * @author Chao
 */
public class Board {
	private final static int SIZE = 15;
	private BoardContent[][] boardContent;
	
	public Board() {
		boardContent = new BoardContent[SIZE][SIZE];
		for(int i=0; i<SIZE; i++){
			for(int j=0; j<SIZE; j++){
				boardContent[i][j] = new BoardContent();
			}
		}
	}
	
	/**
	 * this method add a regular tile to the specific location
	 * pre-condition: the tile cannot be null, loc can not be null
	 * pre-condition: the location must be valid and empty;
	 * @param tile- the tile you want to add
	 * @param loc- is the location you want to put tile
	 * @return true if the operation succeeds.
	 */
	public boolean addTiles(Tile tile,Location loc){
		if (tile == null){
			throw new NullPointerException("Tile cannot be null");
		}else if (loc == null){
			throw new NullPointerException("Location cannot be null");
		}
		if(isEmptyLocation(loc) && isValidLocation(loc)){
			int x = loc.getX();
			int y = loc.getY();
			boardContent[x][y].setTile(tile);
			return true;
		}else{
			throw new IllegalArgumentException("Please enter legal location");
		}
	}
	
	/**
	 * this method add a special tile to the specific location
	 * pre-condition: the tile cannot be null, loc can not be null
	 * pre-condition: the location must be valid and empty;
	 * @param special tile- the tile you want to add
	 * @param loc- is the location you want to put tile
	 * @return true if the operation succeeds.
	 */
	public boolean addSpecialTiles(SpecialTile specialTile,Location loc) {
		if (specialTile == null){
			throw new NullPointerException("Tile cannot be null");
		}else if (loc == null){
			throw new NullPointerException("Location cannot be null");
		}
		if(isValidLocation(loc)){
			int x = loc.getX();
			int y = loc.getY();
			boardContent[x][y].setSpecialTile(specialTile);
			return true;
		}else{
			throw new IllegalArgumentException("Please enter legal location");
		}
	}
	
	public Tile getTile(Location loc) {
		int x = loc.getX();
		int y = loc.getY();
		return boardContent[x][y].getTile();
	}
	
	public ArrayList<Location> getAllOccupiedLoc() {
		ArrayList<Location> occupiedLoc = new ArrayList<Location>();
		for(int i=0; i<SIZE; i++){
			for (int j=0; j<SIZE; j++){
				if(boardContent[i][j].getTile()!= null){
					occupiedLoc.add(new Location(i+1, j+1));
				}
			}
		}
		return occupiedLoc;
	}
	
	public ArrayList<SpecialTile> getSpecialTile(Location loc) {
		int x = loc.getX();
		int y = loc.getY();
		return boardContent[x][y].getSpecialTile();
	}
	
	public Map<SpecialTile, Location> getAllSpecialTiles() {
		Map<SpecialTile, Location> special2Loc = new HashMap<SpecialTile, Location>();
		for(int i=0; i<SIZE; i++){
			for (int j=0; j<SIZE; j++){
				ArrayList<SpecialTile> specials = boardContent[i][j].getSpecialTile();
				if(specials != null){
					for(SpecialTile s : specials){
						special2Loc.put(s,new Location(i+1, j+1));
					}
				}
			}
		}
		return special2Loc;
	}
	/**
	 * This method returns the feature on the board such as double word score
	 * @param loc- location you want to check
	 * @return boardFeature- an enum object
	 */
	public BoardFeature getBoardFeature(Location loc) {
		int x = loc.getX();
		int y = loc.getY();
		return boardContent[x][y].getBoardFeature(loc);
	}
	
	/**
	 * This method returns if the board is empty so that the game can 
	 * decide if this is the first round;
	 * @return true if it is true;
	 */
	public boolean isEmptyBoard() {
		if(getAllOccupiedLoc()==null || getAllOccupiedLoc().size()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * @param loc- the location needed to check
	 * @return true- if this loc is not occupied.
	 */
	public boolean isEmptyLocation(Location loc) {
		int x = loc.getX();
		int y = loc.getY();
		if(x>14 || x<0 || y>14 || y<0){
			return false;
		}else{
			return boardContent[x][y].getTile() == null;
		}
	}
	
	/**
	 * This method removes a regular tile on the board;
	 * pre-condition: the location itself is not empty
	 * @param loc- the location you want to remove
	 * @return true if this operation succeeds.
	 */
	public boolean removeTile(Location loc) {
		if(!isEmptyLocation(loc)){
			int x = loc.getX();
			int y = loc.getY();
			boardContent[x][y].setTile(null);
			return true;
		}else {
			throw new IllegalArgumentException("This location is empty");
		}
	}
	
	/**
	 * This method removes a special tile on the board;
	 * @param loc- the location you want to remove
	 * @return true if this operation succeeds.
	 */
	public boolean removeSpeicalTile(Location loc) {
		int x = loc.getX();
		int y = loc.getY();
		boardContent[x][y].setSpecialTile(null);
		return true;
	}
	
	/**
	 * This method checks if the location is valid.
	 * Valid loc should be between 0-14 with both of its coordinates;
	 * @param loc- location you want to check
	 * @return true if it is valid
	 */
	public boolean isValidLocation(Location loc) {
		return loc.getX()<15 && loc.getX()>=0 && 
		       loc.getY()<15 && loc.getY()>=0;
	}
	
	/**
	 * This method applies special effect of the special tiles on a specific location
	 */
	public ArrayList<SpecialTile> applySpecialEffect(Location loc,BoardReferee boardReferee) {
		int x = loc.getX();
		int y = loc.getY();
		ArrayList<SpecialTile> specialTiles = boardContent[x][y].getSpecialTile();
		if (specialTiles == null){
			return null;
		}
		ArrayList<SpecialTile> returnList = new ArrayList<SpecialTile>(specialTiles);
		Object[] specials = specialTiles.toArray();
		for(int i=0; i<specials.length; i++){
			if(specials[i] instanceof SpecialTile){
				SpecialTile s = (SpecialTile)specials[i];
				s.applySpecialEffect(boardReferee, loc);
			}
		}
		specialTiles.clear();
		return returnList;
	}
	
	/**
	 * This method shows all contents on the board as a string;
	 */ 
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (int j = 0; j < SIZE; j++){
			for (int i = 0; i < SIZE; i++) {
				if (boardContent[i][j].getTile() == null) {
					Location loc = new Location(i+1,j+1);
				 	res.append(loc);
					res.append("\t");
				} else{
					res.append(boardContent[i][j].getTile());
					res.append("\t");
				}
			}
			res.append("\n");
		}
		res.append("Special Tiles:\n");
		for (int j = 0; j < SIZE; j++)  {
			for (int i = 0; i < SIZE; i++){
				if (boardContent[i][j].getSpecialTile() == null) {
					Location loc = new Location(i+1,j+1);
					res.append(loc);
					res.append("\t");
				} else{
					ArrayList<SpecialTile> contents = new ArrayList<SpecialTile>();
					contents = boardContent[i][j].getSpecialTile();
					for (SpecialTile content:contents){
						res.append(content.toString());
						res.append("\t");
					}
				}
			}
			res.append("\n");
		}
		return res.toString();
	}
}
