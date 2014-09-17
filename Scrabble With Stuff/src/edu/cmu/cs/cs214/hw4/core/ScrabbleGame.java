package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import edu.cmu.cs.cs214.hw4.core.player.*;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.*;;

public class ScrabbleGame {
	private ArrayList<Player> players;
	private Board board;
	private BoardReferee boardReferee;
	private TileBag tileBag;
	
	private Player currentPlayer;
	private Queue<Player> playingOnes;
	private Player winner;
	
	/**
	 * Set up the game here;
	 * @param names- all player names of the this game
	 */
	public ScrabbleGame(ArrayList<String> names) {
		board = new Board();
		
		players = new ArrayList<Player>();
		tileBag = new TileBag();
		boardReferee = new BoardReferee(board,tileBag);
		playingOnes = new LinkedList<Player>();
		
		if(names.size() < 2 || names.size() > 10){
			throw new IllegalArgumentException("Invalid Number of Players");
		}
		for(String name:names){
			Player player = new Player(name);
			players.add(player);
			playingOnes.add(player);
			try {
				player.addTiles(tileBag);
			} catch (Exception e) {
				e.getMessage();
			}
		}
		currentPlayer = playingOnes.poll();
	}
	
	public Board getBoard() {
		return board;
	}
	
	public TileBag getTileBag() {
		return tileBag;
	}
	
	public BoardReferee getBoardReferee() {
		return boardReferee;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public Queue<Player> getPlayingOnes() {
		return playingOnes;
	}
	
	public Player getWinner() {
		return winner;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Every time the player click the OK button, a new round play begins
	 * @return true if the play is valid
	 */
	public boolean newRoundPlay() {
		try{
			return boardReferee.play(currentPlayer);
		}catch (NullPointerException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * When one player finishes his round, this method will change another 
	 * player to play;
	 */
	public void changePlayer() {
		//synchronized (ScrabbleGame.class) {
			currentPlayer.addTiles(tileBag);
			if(boardReferee.reverseOrder){
				reverseOrder();
			}
			
			int bonus = currentPlayer.getBonusTurn();
			if(bonus > 0){
				currentPlayer.setBonusTurn(-1);
			}
			else {
				playingOnes.add(currentPlayer);
				// pop out the new player
				currentPlayer = playingOnes.poll();
			}
			for(Player p: players){
				//dSystem.out.println(p.getExtraTurn());
				if(p.getExtraTurn() > 0){
					currentPlayer = p;
					p.setExtraTurn(-1);
					break;
				}
			}
	}
	
	/**
	 * exchagne tiles for player; if the tilebag does not have enough tiles to 
	 * exchange, exchange the number of tiles in the bag, if empty, just return false
	 * @param player- to player who wants to change tiles
	 * @return true if operation succeeds
	 */
	public boolean exchangeTiles(Player player) {
		int numOfTiles = player.getTiles().size();
		if (tileBag.isEmpty()) {
			return false;
		}else if(tileBag.numOfTiles()< numOfTiles){
			return player.exchangeTiles(tileBag,tileBag.numOfTiles());
		}else {
			return player.exchangeTiles(tileBag,numOfTiles);
		}
		
	}
	
	/**
	 * player can choose one location and one tile a time to add it
	 * on the board
	 * @param t- tile the player adds
	 * @param loc- the location the player chooses
	 * @return true if the operation succeeds
	 */
	public boolean addTilesOnBoard(Tile t, Location loc){
		if(t == null || loc == null) 
			// if the tile or location is null, just return false
			return false;
		boolean flag = boardReferee.addTileOnBoard(t, loc);
		
		if(flag){
			int index = currentPlayer.getTiles().indexOf(t);
			currentPlayer.useTile(index); //remove the tile put on board
		}
		return flag;
	}

	/**
	 * Player can add special tiles on board using this method
	 * @param t - the special tile they want to use
	 * @param loc- location they want to put
	 * @return true if the operation succeeds
	 */
	public boolean addSpecialTile(SpecialTile t, Location loc){
		return boardReferee.addSpecialTile(t, loc);
	}
	
	/**
	 * this method is to set the winner of the game
	 * @return true if the game ends
	 */
	public boolean decideWinner() {
		if (tileBag.isEmpty()) { // if the bag is empty
			winner = players.get(0);
			for (Player p : players) {
				if (p.getScore() > winner.getScore()) {
					winner = p;
				}
			}
			System.out.println("Winner is " + winner.toString());
			return true;
		}
		return false;
	}
	
	/**
	 * recall the tiles that have been put on the board
	 */
	public void recall() {
		boardReferee.recall(currentPlayer);
	}
	
	/**
	 * This method reverse the order of players
	 */
	private void reverseOrder(){
		Stack<Player> tempStack = new Stack<Player>();
		while(! playingOnes.isEmpty()){
			tempStack.push(playingOnes.poll());
		}
		while(! tempStack.isEmpty()){
			playingOnes.add(tempStack.pop());
		}
		boardReferee.reverseOrder = false; //disable reverse order
	}
	
	public boolean buySpecialTile(int index) {
		return currentPlayer.buySpecialTile(index);
	}
}
