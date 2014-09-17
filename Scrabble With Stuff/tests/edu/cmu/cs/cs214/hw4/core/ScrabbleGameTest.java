package edu.cmu.cs.cs214.hw4.core;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cmu.cs.cs214.hw4.core.SpecialTiles.NegativePoints;
import edu.cmu.cs.cs214.hw4.core.player.Player;

public class ScrabbleGameTest {
	private ScrabbleGame game;
	private ArrayList<String> names;
	
	/** Called before each test case method. */
	@Before
	public void setUp(){
		names =new ArrayList<String>();
		names.add("Chao");
		names.add("Shuchang");
		names.add("Xiangyu");
		names.add("Siyu");
		game  = new ScrabbleGame(names);
		
	}
	
	/** Called after each test case method.**/
	@After
	public void tearDown() throws Exception{
		// Don't need to do anything here.
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidGame(){
		names = new ArrayList<String>();
		names.add("Shuchang");
		game  = new ScrabbleGame(names);
	}
	
	@Test
	public void testGetPlayers(){
		ArrayList<String> list = new ArrayList<String>();
		for(Player p: game.getPlayers()){
			list.add(p.getName());
		}
		assertEquals(names, list);
	}
	
	@Test
	public void testGetCurrentPlayer(){
		Player player = new Player("Chao");
		assertEquals(player.getName(), game.getCurrentPlayer().getName());
	}
	
	@Test
	public void testPlaceTile(){
		Player player = game.getCurrentPlayer();
		ArrayList<Tile> rack = player.getTiles();
		assertTrue(game.addTilesOnBoard(rack.get(1), new Location('H', 8)));
	}
	
	@Test
	public void testPlaceNull(){
		Player player = game.getCurrentPlayer();
		ArrayList<Tile> rack = player.getTiles();
		assertFalse(game.addTilesOnBoard(null, new Location('H', 8)));
		assertFalse(game.addTilesOnBoard(rack.get(0), null));
	}
	
	@Test
	public void testPlay(){
		Player player = game.getCurrentPlayer();
		ArrayList<Tile> rack = player.getTiles();
		game.addTilesOnBoard(rack.get(0), new Location('H', 8));
		game.addTilesOnBoard(rack.get(1), new Location('H', 9));
		game.addTilesOnBoard(rack.get(2), new Location('H', 10));
		game.addTilesOnBoard(rack.get(3), new Location('H', 11));
		game.addTilesOnBoard(rack.get(2), new Location('H', 12));
		assertFalse(game.newRoundPlay());
	}
	
	@Test
	public void testTurn(){
		game.changePlayer();
		assertEquals(new Player("Shuchang").getName(), game.getCurrentPlayer().getName());
	}
	
	@Test
	public void testReverseOrder(){
		BoardReferee boardReferee = game.getBoardReferee();
		boardReferee.reverseOrder = true;
		game.changePlayer();
		
		ArrayList<String> strings = new ArrayList<String>();
		strings.add("Xiangyu");
		strings.add("Shuchang");
		strings.add("Chao");
		
		Queue<Player> queue = new LinkedList<Player>();
		ArrayList<String> players = new ArrayList<String>();
		queue = game.getPlayingOnes();
		for(Player p:queue){
			players.add(p.getName());
		}
		assertEquals(strings,players);
	}
	
	@Test
	public void testBonusTurn(){
		Player player = game.getCurrentPlayer();
		player.setBonusTurn(1);
		game.changePlayer();
		assertEquals(player, game.getCurrentPlayer());
	}
	
	@Test
	public void testUseSpecialTile(){
		Player player = game.getCurrentPlayer();
		player.addScore(200);
		player.buySpecialTile(1);
		assertTrue(game.addSpecialTile(new NegativePoints(player), new Location('H', 8)));
	}

}
