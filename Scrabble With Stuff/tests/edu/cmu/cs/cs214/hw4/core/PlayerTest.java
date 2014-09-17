package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.player.*;

public class PlayerTest {
	private Player player;
	
	/** Called before each test case method. */
	@Before
	public void setUp(){
		player = new Player("Chao"); 
	}
	
	/** Called after each test case method.**/
	@After
	public void tearDown() throws Exception{
		// Don't need to do anything here.
	}
	
	@Test
	public void testGetName(){
		assertEquals("Chao", player.getName());
	}
	
	@Test
	public void testGetScore(){
		assertEquals(0,player.getScore());
	}
	
	@Test
	public void testGetSpecial(){
		assertEquals(0, player.getSpecialTiles().size());
	}
	
	@Test
	public void testGetTiles(){
		assertEquals(0, player.getTiles().size());
		TileBag bag = new TileBag();
		player.addTiles(bag);
		assertEquals(7, player.getTiles().size());
	}
	
	@Test
	public void testIsFinished(){
		assertFalse(player.isFinished(true));
		assertTrue(player.isFinished(false));
	}
	
	@Test
	public void testAddScore(){
		player = new Player("CrazyLee");
		player.addScore(200);
		assertEquals(200, player.getScore());
	}
	
	@Test
	public void testBuySpecial(){
		player.addScore(500);
		player.buySpecialTile(1);
		player.buySpecialTile(2);
		player.buySpecialTile(3);
		player.buySpecialTile(4);
		assertEquals(4, player.getSpecialTiles().size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBuyWrongSpecialTile(){
		player.addScore(200);
		player.buySpecialTile(6);
	}
	
	@Test
	public void testLackingMoney(){
		player = new Player("Chao");
		player.addScore(1);
		assertFalse(player.buySpecialTile(5));
	}
	
	@Test
	public void testContains(){
		TileBag bag = new TileBag();
		player.addTiles(bag);
		assertTrue(player.isContained(player.getTiles().get(0).getLetter()));
	}
	
	@Test
	public void testNotContain(){
		TileBag bag = new TileBag();
		player.addTiles(bag);
		Tile tile = new Tile('A');
		while(player.isContained(tile.getLetter())){
			tile = new Tile((char)(tile.getLetter()+1));
		}
		assertFalse(player.isContained(tile.getLetter()));
	}
	
	@Test
	public void testUseTile(){
		TileBag bag = new TileBag();
		player.addTiles(bag);
		Tile temp = player.getTiles().get(6);
		assertEquals(temp,player.useTile(6));
		assertEquals(6, player.getTiles().size());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testIllegalUseTile(){
		TileBag bag = new TileBag();
		player.addTiles(bag);
		player.useTile(8);
	}
	
	@Test
	public void testUseSpecialTile(){
		player.addScore(500);
		player.buySpecialTile(1);
		player.buySpecialTile(2);
		player.useSpecialTile("NEGATIVEPOINTS");
		assertEquals(1, player.getSpecialTiles().size());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testIllegalUseSpecialTile(){
		player.addScore(500);
		player.buySpecialTile(1);
		player.useSpecialTile("ReverseOrder");
	}
	
	@Test
	public void testToString(){
		TileBag bag = new TileBag();
		Player player = new Player("Chao");
		player.addTiles(bag);
		player.addScore(500);
		ArrayList<SpecialTile> specials = new ArrayList<SpecialTile>();
		player.buySpecialTile(1);
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		tiles = player.getTiles();
		String s = "Player:Chao Score:497 Rack(";
		for (Tile tile: tiles){
			s = s + tile.toString()+" ";
		}
		s = s + ")SpecialTiles:";
		specials = player.getSpecialTiles();
		for (SpecialTile t : specials) {
			s = s+ t.toString()+" ";
		}
		assertEquals(s, player.toString());
	}
	
	
	
	
	
}
