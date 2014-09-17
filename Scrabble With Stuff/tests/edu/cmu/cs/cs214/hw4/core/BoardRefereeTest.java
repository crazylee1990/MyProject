package edu.cmu.cs.cs214.hw4.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import edu.cmu.cs.cs214.hw4.core.SpecialTiles.NegativePoints;
import edu.cmu.cs.cs214.hw4.core.player.Player;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.*;

public class BoardRefereeTest {
	private BoardReferee boardReferee;
	private Board board;
	private Player player;
	private TileBag tileBag;
	
	/** Called before each test case method. */
	@Before
	public void setUp(){
		board = new Board();
		tileBag = new TileBag(); 
		addSomeTiles(board);//add SOFTWARE on the board
		boardReferee = new BoardReferee(board,tileBag); 
		player = new Player("Chao");
	}
	
	private void addSomeTiles(Board board){
		board.addTiles(new Tile('S'), new Location(4, 8));
		board.addTiles(new Tile('O'), new Location(5, 8));
		board.addTiles(new Tile('F'), new Location(6, 8));
		board.addTiles(new Tile('T'), new Location(7, 8));
		board.addTiles(new Tile('B'), new Location('H', 8));
		board.addTiles(new Tile('A'), new Location( 9, 8));
		board.addTiles(new Tile('L'), new Location(10, 8));
		board.addTiles(new Tile('L'), new Location(11, 8));
		
	}
	/** Called after each test case method.**/
	@After
	public void tearDown() throws Exception{
		// Don't need to do anything here.
	}
	
	@Test
	public void testGetBoard(){
		Board board = boardReferee.getBoard();
		assertEquals(board,boardReferee.getBoard());
	}
	
	@Test
	public void testGetCurrentScore(){
		assertEquals(0, boardReferee.getCurrentScore());
	}
	
	@Test
	public void testSetCurrentScore(){
		boardReferee.setCurrentScore(100);
		assertEquals(100, boardReferee.getCurrentScore());
	}
	
	@Test
	public void testGetAlongXState(){
		assertEquals(-1, boardReferee.getAlongXState());
	}
	
	@Test
	public void testAddTileOnBoard(){
		Tile tile = new Tile('C');
		Location location = new Location(3, 3);
		assertTrue(boardReferee.addTileOnBoard(tile, location));
	}
	
	@Test
	public void testAddSpecialTile(){
		Player player = new Player("Chao");
		player.addScore(100);
		player.buySpecialTile(1);
		SpecialTile specialTile = new NegativePoints(player);
		Location location = new Location(3, 3);
		assertTrue(boardReferee.addSpecialTile(specialTile, location));
	}
	
	@Test
	public void testAddMoreSpecialTiles(){
		player.addScore(100);
		player.buySpecialTile(1);
		player.buySpecialTile(1);
		SpecialTile specialTile = new NegativePoints(player);
		SpecialTile specialTile2 = new NegativePoints(player);
		Location location = new Location(3, 3);
		assertTrue(boardReferee.addSpecialTile(specialTile, location));
		assertTrue(boardReferee.addSpecialTile(specialTile2, location));
	}
	
	@Test
	public void testPlayFirstRound(){
		board = new Board();
		tileBag = new TileBag();
		boardReferee = new BoardReferee(board,tileBag);
		player = new Player("Li");
		boardReferee.addTileOnBoard(new Tile('H'), new Location('H', 8));
		boardReferee.addTileOnBoard(new Tile('E'), new Location('H', 9));
		boardReferee.addTileOnBoard(new Tile('L'), new Location('H', 10));
		boardReferee.addTileOnBoard(new Tile('L'), new Location('H', 11));
		boardReferee.addTileOnBoard(new Tile('O'), new Location('H', 12));
		assertTrue(boardReferee.play(player));		
		assertEquals(18, player.getScore());
	}
	
	@Test
	public void testPlayFirstRound2(){
		Board board = new Board();
		tileBag = new TileBag();
		boardReferee = new BoardReferee(board,tileBag);
		boardReferee.addTileOnBoard(new Tile('A'), new Location('H', 8));
		boardReferee.addTileOnBoard(new Tile('B'), new Location('H', 9));
		boardReferee.addTileOnBoard(new Tile('C'), new Location('H', 10));
		boardReferee.addTileOnBoard(new Tile('D'), new Location('H', 11));
		boardReferee.addTileOnBoard(new Tile('E'), new Location('H', 12));
		
		boardReferee.play(player);
	}
	
	@Test
	public void testNextPlay1(){
		boardReferee.addTileOnBoard(new Tile('O'), new Location(4, 9));
		boardReferee.addTileOnBoard(new Tile('I'), new Location(4, 10));
		boardReferee.addTileOnBoard(new Tile('L'), new Location(4, 11));
		assertTrue(boardReferee.play(player));
		assertEquals(5, player.getScore());
	}
	
	@Test
	public void testNextPlay2(){
		boardReferee.addTileOnBoard(new Tile('A'), new Location(4, 7));
		boardReferee.addTileOnBoard(new Tile('S'), new Location(4, 9));
		assertTrue(boardReferee.play(player));
		assertEquals(4, player.getScore());
	}
	
	@Test
	public void testInvalidPlay1(){
		boardReferee.addTileOnBoard(new Tile('O'), new Location(4, 9));
		boardReferee.addTileOnBoard(new Tile('O'), new Location(5, 9));
		assertFalse(boardReferee.play(player));
	}
	
	@Test()
	public void testInvalidPlay2(){
		boardReferee.addTileOnBoard(new Tile('O'), new Location(5, 9));
		assertFalse(boardReferee.play(player));
	}
	
	@Test
	public void testNegativePoints(){
		Player newPlayer = new Player("CrazyLee");
		newPlayer.addScore(200);
		newPlayer.buySpecialTile(1);
		Location loc = new Location(4, 9);
		boardReferee.addSpecialTile(new NegativePoints(newPlayer), loc);
		boardReferee.addTileOnBoard(new Tile('O'), new Location(4, 9));
		boardReferee.addTileOnBoard(new Tile('I'), new Location(4, 10));
		boardReferee.addTileOnBoard(new Tile('L'), new Location(4, 11));
		assertTrue(boardReferee.play(player));
		assertEquals(-5, player.getScore());
	}
	
	@Test
	public void testReverseOrder(){
		Player newPlayer = new Player("CrazyLee");
		Location loc = new Location(4, 9);
		newPlayer.addScore(200);
		newPlayer.buySpecialTile(2);
		boardReferee.addSpecialTile(new ReverseOrder(newPlayer), loc);
		boardReferee.addTileOnBoard(new Tile('O'), new Location(4, 9));
		boardReferee.addTileOnBoard(new Tile('I'), new Location(4, 10));
		boardReferee.addTileOnBoard(new Tile('L'), new Location(4, 11));
		assertTrue(boardReferee.play(player));
		assertTrue(boardReferee.reverseOrder);
	}
	
	
	@Test
	public void testBoom(){
		Player newPlayer = new Player("CrazyLee");
		Location loc = new Location(4, 9);
		newPlayer.addScore(200);
		newPlayer.buySpecialTile(3);
		
		boardReferee.addSpecialTile(new Boom(newPlayer), loc);
		boardReferee.addTileOnBoard(new Tile('O'), new Location(4, 9));
		boardReferee.addTileOnBoard(new Tile('L'), new Location(4, 10));
		boardReferee.addTileOnBoard(new Tile('I'), new Location(4, 11));
		boardReferee.addTileOnBoard(new Tile('D'), new Location(4, 12));
		boardReferee.addTileOnBoard(new Tile('E'), new Location(4, 13));
		boardReferee.addTileOnBoard(new Tile('R'), new Location(4, 14));
		assertTrue(boardReferee.play(player));
		assertEquals(11, player.getScore());
	}
	
	@Test
	public void testBonusTurn(){
		Player newPlayer = new Player("CrazyLee");
		Location loc = new Location(4, 9);
		newPlayer.addScore(200);
		newPlayer.buySpecialTile(4);
		
		boardReferee.addSpecialTile(new BonusTurn(newPlayer), loc);
		boardReferee.addTileOnBoard(new Tile('O'), new Location(4, 9));
		boardReferee.addTileOnBoard(new Tile('I'), new Location(4, 10));
		boardReferee.addTileOnBoard(new Tile('L'), new Location(4, 11));
		assertTrue(boardReferee.play(player));
		assertEquals(1, newPlayer.getBonusTurn());
	}	
}
