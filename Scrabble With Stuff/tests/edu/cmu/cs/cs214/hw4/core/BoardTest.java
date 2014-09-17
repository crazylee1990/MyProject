package edu.cmu.cs.cs214.hw4.core;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import edu.cmu.cs.cs214.hw4.core.SpecialTiles.NegativePoints;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.player.Player;


public class BoardTest {
	private Board board;
	
	/** Called before each test case method. */
	@Before
	public void setUp(){
		board = new Board(); 
	}
	
	/** Called after each test case method.**/
	@After
	public void tearDown() throws Exception{
		// Don't need to do anything here.
	}
	
	@Test
	public void testAddTile(){
		Tile tile = new Tile('C');
		Location loc = new Location(2, 3);
		assertTrue(board.addTiles(tile, loc));
	}
	
	@Test(expected = NullPointerException.class)
	public void testAddNullTile(){
		Location loc = new Location(1, 1);
		board.addTiles(null, loc);
	}
	
	@Test(expected = NullPointerException.class)
	public void testAddTileNullLoc(){
		Tile tile = new Tile('C');
		board.addTiles(tile, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddInvalidLoc(){
		Tile tile = new Tile('C');
		Location loc = new Location(2, 3);
		loc.setX(15);
		loc.setY(-1);
		board.addTiles(tile, loc);
	}
	
	@Test
	public void testAddSpecialTile() {
		Player player = new Player("Chao");
		SpecialTile specialTile = new NegativePoints(player);
		Location loc = new Location(2, 3);
		assertTrue(board.addSpecialTiles(specialTile, loc));
	}
	
	@Test(expected = NullPointerException.class)
	public void testAddSpecialTileWithNullLoc(){
		Player player = new Player("Chao");
		SpecialTile specialTile = new NegativePoints(player);
		board.addSpecialTiles(specialTile, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testAddNullSpecialTile(){
		Location loc = new Location(2, 3);
		board.addSpecialTiles(null, loc);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddSpecialTileWithInvalidLoc() {
		Player player = new Player("Chao");
		SpecialTile specialTile = new NegativePoints(player);
		Location loc = new Location(2, 3);
		loc.setX(16);
		loc.setY(-2);
		board.addSpecialTiles(specialTile, loc);
	}
	
	@Test
	public void testGetTile() {
		Tile tile = new Tile('C');
		Location loc = new Location(2, 3);
		board.addTiles(tile, loc);
		assertEquals(tile, board.getTile(loc));
	}
	
	@Test
	public void testGetSpecialTile(){
		Player player = new Player("Chao");
		ArrayList<SpecialTile>specialTiles = new ArrayList<SpecialTile>();
		specialTiles.add(new NegativePoints(player));
		Location loc = new Location(2, 3);
		board.addSpecialTiles(specialTiles.get(0), loc);
		assertEquals(specialTiles, board.getSpecialTile(loc));
	}
	
	@Test
	public void testGetBoardFeature(){
		Location loc1 = new Location('H',8);
		Location loc2 = new Location('A', 1);
		Location loc3 = new Location('D', 1);
		Location loc4 = new Location('F', 2);
		Location loc5 = new Location('B', 2);
		Location loc6 = new Location('A', 2);
		assertEquals(BoardFeature.STAR,  board.getBoardFeature(loc1));
		assertEquals(BoardFeature.TWS,   board.getBoardFeature(loc2));
		assertEquals(BoardFeature.DLS,   board.getBoardFeature(loc3));
		assertEquals(BoardFeature.TLS,   board.getBoardFeature(loc4));
		assertEquals(BoardFeature.DWS,   board.getBoardFeature(loc5));
		assertEquals(BoardFeature.Normal,board.getBoardFeature(loc6));
	}
	
	@Test
	public void testIsEmptyBoard(){
		board = new Board();
		assertTrue(board.isEmptyBoard());
	}
	
	@Test
	public void testNotEmptyBoard(){
		Tile tile = new Tile('C');
		Location loc = new Location(2, 3);
		board.addTiles(tile, loc);
		assertFalse(board.isEmptyBoard());
	}
	
	@Test
	public void testIsEmptyLoc() {
		board = new Board();
		Location loc = new Location(2,3);
		Tile tile = new Tile('C');
		assertTrue(board.isEmptyLocation(loc));
		board.addTiles(tile, loc);
		assertFalse(board.isEmptyLocation(loc));
	}
	
	@Test
	public void testRemoveTile(){
		Location loc = new Location(2, 3);
		Tile tile = new Tile('C');
		board.addTiles(tile, loc);
		assertTrue(board.removeTile(loc));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveEmptyTile(){
		board = new Board();
		Location loc = new Location(2, 3);
		board.removeTile(loc);
	}
	
	@Test
	public void testRemoveSpecialTile(){
		Player player = new Player("Chao");
		SpecialTile specialTile = new NegativePoints(player);
		Location loc = new Location(2, 3);
		board.addSpecialTiles(specialTile, loc);
		assertTrue(board.removeSpeicalTile(loc));
	}
}
