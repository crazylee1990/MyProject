package edu.cmu.cs.cs214.hw4.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import edu.cmu.cs.cs214.hw4.core.Board;
import edu.cmu.cs.cs214.hw4.core.Location;
import edu.cmu.cs.cs214.hw4.core.Tile;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.player.Player;

/**
 * The board panel shows the content in the board, including the board image, all tiles
 * that have been placed down, and the special tiles that belong to the current player
 * @author Chao
 *
 */
public class BoardPanel extends JPanel{
	private static final int TILESIZE = 40;
	private static final long serialVersionUID = 1L;
	private Board board;
	private Player currentPlayer;
	private ImageIcon boardImg = new ImageIcon("assets/board.png");
	private ImageIcon tileImg = new ImageIcon();
	private ImageIcon specialImg = new ImageIcon();
	
	public BoardPanel(Board board, Player player) {
		this.board = board;
		this.currentPlayer = player;
		
		int width = boardImg.getIconWidth();
		int height = boardImg.getIconHeight();
		this.setPreferredSize(new Dimension(width, height));
		this.setBackground(Color.LIGHT_GRAY);
	}
	
	public void changePlayer(Player p) {
		this.currentPlayer = p;
		repaint();
	}
	
	public void recall() {
		repaint();
	}
	
	/**
	 * override the paintComponent method since we need to update the content
	 * after we put tile on the board
	 */
	@Override
        public void paintComponent(Graphics g) {
		super.paintComponent(g);
		boardImg.paintIcon(this, g, 0, 0);
		
		/** show the special tiles that belongs to current player**/
		Map<SpecialTile, Location> spec2loc = board.getAllSpecialTiles();
		if(!spec2loc.isEmpty()){
			for (Map.Entry<SpecialTile, Location> entry : spec2loc.entrySet()){
				Location loc = entry.getValue();
				SpecialTile special = entry.getKey();
				if(special.getWhoseTile().getName().equals(currentPlayer.getName())){
					int x = (loc.getX()) * TILESIZE;//
					int y = (loc.getY()) * TILESIZE;
					specialImg = new ImageIcon("assets/tiles/"+ special.toString()+".jpg");
					specialImg.paintIcon(this, g, x, y);
				}
			}
		}
		
		/** show all tiles on board**/
		if(! board.isEmptyBoard()){
			for(Location l:board.getAllOccupiedLoc()){
				Tile tile = board.getTile(l);
				int x = (l.getX()) * TILESIZE;
				int y = (l.getY()) * TILESIZE;
				tileImg = new ImageIcon("assets/tiles/"+ tile.getLetter()+".png");
				tileImg.paintIcon(this, g, x, y);
			}
		}
		
        }
}
