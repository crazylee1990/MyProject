package edu.cmu.cs.cs214.hw4.core;


import java.util.ArrayList;


import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.dictionary.Dictionary;
import edu.cmu.cs.cs214.hw4.core.player.Player;

/**
 * Board Referee is the referee when the game is going on, it determines if the play
 * is valid, if the word is valid, how many scores the player will get and if the content on the board will
 * change by the effect of special tile;
 * @author Chao
 */
public class BoardReferee {
	private Dictionary dictionary;
	private Board board;
	private TileBag tileBag;
	//new Locs are spaces that player selects to put tiles
	public ArrayList<Location> newLocs;
	//new Tiles are the tiles player uses for this play.
	public ArrayList<Tile> newTiles;
	private ArrayList<SpecialTile> specialApplied;
	public ArrayList<String> newWords;
	
	//indicates the direction of the new tiles;
	//-1-no specific direction; 1-alongX; 0-alongY
	private int alongXState; 
	private int currentScore;
	private int tempScore;
	private boolean firstRound;
	//if the reverse order special tile is activated in this play
	public boolean reverseOrder;
	public ArrayList<Player> extraTurnPlayers;
	//private String errorInfo;
	
	public BoardReferee(Board board, TileBag tileBag) {
		this.board = board;
		this.tileBag = tileBag;
		newLocs = new ArrayList<Location>();
		newTiles = new ArrayList<Tile>();
		newWords = new ArrayList<String>();
		extraTurnPlayers = new ArrayList<Player>();
		initializeReferee();
	}
	
	private void initializeReferee() {
		if(board.isEmptyBoard()){
			firstRound = true;
		}else {
			firstRound = false;
		}
		alongXState = -1;
		currentScore = 0;
		tempScore = 0;
		reverseOrder = false;
		//errorInfo = "";
		dictionary = new Dictionary();
	}
	
	public Board getBoard() {
		return board;
	}
	
	public int getCurrentScore() {
		return currentScore;
	}
	
	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}
	
	public int getAlongXState() {
		return alongXState;
	}
	
	public ArrayList<SpecialTile> getSpecialApplied() {
		return specialApplied;
	}
	
	public void resetSpecialApplied() {
		this.specialApplied = null;
	}
	
	public void setFirstRound(boolean firstRound) {
		this.firstRound = firstRound;
	}
	
	/**
	 * This method adds tiles to new tile list one by one while player
	 * is choosing the tiels they want to use
	 * @param tile- the tile player wants to use
	 * @param loc- the location player wants to put
	 * @return true if the operation succeeds
	 */
	public boolean addTileOnBoard(Tile tile, Location loc) {
		boolean isTrue = board.addTiles(tile, loc);
		if(isTrue){
			newTiles.add(tile);
			newLocs.add(loc);
		}
		return isTrue;
	}
	
	/**
	 * This method adds special tiles one by one while player
	 * is choosing the special tiles they want to use
	 * @param special- the special tile player wants to use
	 * @param loc- the location player wants to put
	 * @return true if the operation succeeds
	 */
	public boolean addSpecialTile(SpecialTile special, Location loc) {
		special.getWhoseTile().useSpecialTile(special.toString());
		board.addSpecialTiles(special, loc);
		return true;
	}
	
	public void recall(Player player) {
		for (Location loc: newLocs){
			Tile tile = board.getTile(loc);
			player.recall(tile);
			board.removeTile(loc);
		}
		newTiles.clear();
		newLocs.clear();
	}
	
	/**
	 * After a player chooses the location and tiles they want to use,
	 * this method will validate the play and decide if the player can get the score
	 * this method has two branches to distinguish the first round or not
	 * and one private method for each, 
	 * @param player- the current player to play
	 * @return true- if the operation succeeds;
	 */
	public boolean play(Player player) {
		reverseOrder = false;
		if(firstRound){
			firstRound = !firstRound(player);
			return !firstRound;
		}else{
			return nextRound(player);
		}	
	}
	
	/**
	 * For the first round, this method first check if the player puts a tile on the star
	 * then check if the tiles are aligned and connected; if so, then get the word the player
	 * want to form, and check if it is valid word; if so, this play is valid and the player
	 * can add the score by the word
	 * @param player- the current player to play
	 * @return true- if the operation succeeds;
	 */
	private boolean firstRound(Player player){
		boolean crossStar = false;
		for(Location loc:newLocs){
			if(loc.toString().equals("H8")){
				crossStar = true;
				break;
			}
		}
		if(crossStar && checkDirection(newLocs)){
			newWords.clear();
			newWords = getNewWords(newLocs.get(0));
			if(!isValidWord()){
				return false;
			}
			currentScore = tempScore;
			player.addScore(currentScore);
			player.addTiles(tileBag);
			//System.out.println(player.getTiles());
			newTiles.clear();
			newLocs.clear();
			return true;
		}
		return false;
	}
	
	/**
	 * if this is not the first round, this method check if it is a valid play,
	 * if so, the player can get the score; see below for the specification of isValidPlay() method
	 * @param player- the current player to play
	 * @return true- if the operation succeeds;
	 */
	private boolean nextRound(Player player){
		if(isValidPlay()){
			newWords.clear();
			currentScore = 0;
			tempScore = 0;
			newWords = getNewWords(newLocs.get(0));
			if(isValidWord()){
				currentScore = tempScore;
			}
			specialApplied = enableSpecialEffects();
			player.addScore(currentScore);
			player.addTiles(tileBag);
			newTiles.clear();
			newLocs.clear();
			return true;
		}
		return false;
	}
	
	/**
	 * This method decides if the tiles are along X first and then check 
	 * if they are aligned without spot
	 * @param newLocs- are all the locations the player chose
	 * @return true if the new Locations are aligned and connected
	 */
	private boolean checkDirection(ArrayList<Location> newLocs){
		if(newTiles.isEmpty()){
			throw new IllegalArgumentException("Please at least put one tile");
		}else if(newTiles.size()==1){
			alongXState = 1;
			return true;
		}else{
			Location temp1 = newLocs.get(0);
			Location temp2 = newLocs.get(1);
			boolean alongX;
			if(temp1.getX()==temp2.getX()){
				alongX = false;
				return checkAlign(temp1,alongX);
			}else if (temp1.getY()==temp2.getY()) {
				alongX = true;
				return checkAlign(temp1,alongX);
			}else {
				return false;
			}
		}
	}
	 
	/**
	 * To check if the tiles are aligned, this method first sort the location into a new
	 * number array in increasing order, and then get the tiles on these locations one by one;
	 * if the player put the tile connected and aligned, the returned value should not null 
	 * @param base the first location in new location list
	 * @param alongX- true if the tiles are along x
	 * @return true if the operation is correct
	 */
	private boolean checkAlign(Location base, boolean alongX){
		int index = 0;
		int[] num= new int[newLocs.size()];
		if(alongX){
			for(Location loc: newLocs){
				if(base.getY()==loc.getY()){
					num[index] = loc.getX();
					index++;
				}else{	return false;}
			}
		}else{
			for(Location loc: newLocs){
				if(base.getX()==loc.getX()){
					num[index] = loc.getY();
					index++;
				}else{	return false;}
			}
		}
		int min = num[0],max = num[0];
		for(int i=0; i< num.length; i++){
			if(num[i] < min){
				min = num[i];
			}
			if(num[i] > max){
				max = num[i];
			}
		}
		
		for(int i=min; i<= max; i++){
			if(alongX){
				Location temp = new Location(i+1,base.getY()+1);
				if(board.getTile(temp)== null){
					return false;
				}
			}else{
				Location temp = new Location(base.getX()+1,i+1);
				if(board.getTile(temp)== null){
					return false;
				}
			}
		}
		
		if(alongX){
			alongXState = 1;// the direction of the new tiles is along the X axis
		}else {
			alongXState = 0;// the direction of the new tiles is along the Y axis
		}
		return true;	
	}
	
	/**
	 * This method gets words list that is formed by this play. Note: it will get not along
	 * the main along the new tiles, but also the words perpendicular to that direction.
	 * @param firstLoc- first location in the new locations list
	 * @return String list of new words
	 */
	public ArrayList<String> getNewWords(Location firstLoc){
		if(alongXState == -1){
			throw new IllegalArgumentException("the input is not aligned or no input is received");
		}
		if(firstLoc == null)
			throw new NullPointerException("Locaiton cannot be null");
		if(board.isEmptyLocation(firstLoc) )
			throw new IllegalArgumentException("Location cannot be empty");
		ArrayList<String> tempWords = new ArrayList<String>(); 
		String string = getWord(firstLoc,alongXState);
		if(string != null)
			tempWords.add(string);
		
		int neighbor_dir;
		if(alongXState ==1){
			neighbor_dir = 0;
		}else {
			neighbor_dir = 1;
		}
		for(Location loc:newLocs){
			String string1 = getWord(loc,neighbor_dir);
			if(string1 != null)
				tempWords.add(string1);
		}
		return tempWords;
	}
	
	/**This method is part of the method above, it only get a word, 
	 * calculate its score, returns one word a time*/
	private String getWord(Location firstLoc, int direction){
		Tile tile = board.getTile(firstLoc);
		Calculator calculator = new Calculator();
		StringBuilder word = new StringBuilder();
		if(direction == 1){
			int base = firstLoc.getY()+1;
			for (int i = firstLoc.getX()+1; i > 0; i--) {
				Location tempLoc = new Location(i, base);
				tile = board.getTile(tempLoc);
				if(tile == null)break;
				word.insert(0,tile.getLetter());
				calculator.scoreCalculator(tempLoc, tile);
			}
			for(int i= firstLoc.getX()+2; i<15; i++){
				Location tempLoc = new Location(i, base);
				tile = board.getTile(tempLoc);
				if(tile == null)break;
				word.append(tile.getLetter());
				calculator.scoreCalculator(tempLoc, tile);
			}			
		}else {
			int base = firstLoc.getX()+1;
			for (int i = firstLoc.getY()+1; i > 0; i--) {
				Location tempLoc = new Location(base,i);
			 	tile = board.getTile(tempLoc);
				if(tile == null)break;
				word.insert(0,tile.getLetter());
				calculator.scoreCalculator(tempLoc, tile);
			}
			for(int i= firstLoc.getY()+2; i<15; i++){
				Location tempLoc = new Location(base,i);
				tile = board.getTile(tempLoc);
				if(tile == null)break;
				word.append(tile.getLetter());
				calculator.scoreCalculator(tempLoc, tile);
			}
		}
		calculator.multiScore();
		if(!(word.length()==1)){// ignore word with length equals to 1
			tempScore += calculator.getWordScore();
			return word.toString().toLowerCase();
		}
		return null;
	}
	
	/**
	 * Look up to the dictionary for all the words in new Word list
	 * @return true if all of the words are correct
	 */
	public boolean isValidWord() {
		for(String word: newWords){
			if (!dictionary.contains(word))
				return false;
		}
		return true;
	}
	
	/**
	 * This method decides if the current play is valid;
	 * It first checks if player does not put anything, this is allowed;
	 * then it will see if there is at least on tile connecting with tiles
	 * already on board;Next, it check if the tiles are aligned; Finally,
	 * it looks up the dictionary to check word validity
	 * @return true if all the rules are obeyed
	 */
	private boolean isValidPlay(){
		currentScore = 0;
		if(newTiles.isEmpty()){// the player did not place any tile in this round
			return true;
		}

		/* if there is no neighbors around this play, it violates the rule*/
		if(!checkNeighbors()){
			return false;
		}
		/*if new tiles are not in a line ,return false*/
		if(!checkDirection(newLocs)){
			return false;
		}
		Location firsLoc = newLocs.get(0);
		newWords = getNewWords(firsLoc);
		if(! isValidWord()){
			return false;
		}
		return true;
	}
	
	private boolean checkNeighbors(){
		for(Location loc: newLocs){
			ArrayList<Location> neighbors = loc.getNeighbors();
			for(Location temp:neighbors){
				//there should at least one location that is occupied 
				//by the tiles already on the board
				if(!board.isEmptyLocation(temp) && !newTiles.contains(temp)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * enable the special tiles if the player put some tiles on them
	 */
	private ArrayList<SpecialTile> enableSpecialEffects(){
		ArrayList<SpecialTile> specialApplied = new ArrayList<SpecialTile>();
		ArrayList<SpecialTile> temp = new ArrayList<SpecialTile>();
		for(Location loc:newLocs){
			temp = board.applySpecialEffect(loc, this);
			if(temp!= null){
				for (SpecialTile s:temp){
					specialApplied.add(s);
				}
			}
		}
		if(specialApplied.size() == 0){
			return null;
		}else{
			return specialApplied;
		}
	}

	/**
	 * This is a private class for board referee because it not only holds
	 * some variables but also has some methods to calculate the score;
	 * @author Chao
	 */
	private class Calculator{
		private int wordScore;
		private int multiply;
		
		public Calculator() {
			wordScore = 0;
			multiply = 1;
		}
		
		public int getWordScore() {
			return wordScore;
		}
		
		public void multiScore() {
			wordScore *= multiply;
		}
		
		public void scoreCalculator(Location loc, Tile tile){
			int letterScore = tile.getPoints();
			BoardFeature feature = board.getBoardFeature(loc);
			
			switch (feature) {
			case STAR:
				multiply *= 2;
				break;
			case DLS:
				letterScore *= 2;
				break;
			case TLS:
				letterScore *= 3;
				break;
			case DWS:
				multiply *= 2;
				break;
			case TWS:
				multiply *= 3;
				break;
			case Normal:
				break;
			}
			this.wordScore += letterScore;	
		}	
	}	
}
