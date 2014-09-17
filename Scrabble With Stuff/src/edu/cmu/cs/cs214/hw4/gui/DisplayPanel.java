package edu.cmu.cs.cs214.hw4.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.cmu.cs.cs214.hw4.core.TileBag;
import edu.cmu.cs.cs214.hw4.core.player.Player;

/**
 * This display panel shows the number of remaining tiles, player list and their scores
 * for the current player, the display panel will paint its name yellow
 * @author Chao
 *
 */
public class DisplayPanel extends JPanel{
	private static final long serialVersionUID = 4819162036004676580L;
	private final ArrayList<Player> players;
	private ArrayList<JLabel> displayScores;
	private Player currentPlayer;
	private TileBag tileBag;
	
	public DisplayPanel(ArrayList<Player> players, Player currPlayer, TileBag bag) {
		this.tileBag = bag;
		this.players = players;
		this.currentPlayer = currPlayer;
		updateDisplay();
	}
	
	/**
	 * Change player to update the display board
	 * @param p is the player who plays next
	 */
	public void change(Player p) {
		this.currentPlayer = p;
		removeAll();
		updateDisplay();
	}
	
	private void updateDisplay() {
		JPanel displayNumOfTiles = new JPanel();
		JLabel contentJLabel = new JLabel("Tiles Remaining: "+tileBag.numOfTiles(),JLabel.CENTER);
		contentJLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		contentJLabel.setForeground(Color.red);
		displayNumOfTiles.setBackground(Color.black);
		displayNumOfTiles.add(contentJLabel);
		
		
		JPanel showScore = new JPanel();
		displayScores = new ArrayList<JLabel>();
		showScore.setLayout(new GridLayout(players.size()/2,2));
		
		for(Player p : this.players){
			JLabel display = new JLabel(p.getName()+":"+p.getScore(),JLabel.CENTER);
			display.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
			display.setForeground(Color.white);
			displayScores.add(display);
			
			
			// paint the label to yellow for the current player
			if(currentPlayer.getName().equals(p.getName())){
				display.setForeground(Color.yellow);
				
			}
			showScore.add(display);
		}
		showScore.setBackground(Color.black);
		showScore.setVisible(true);
		
		setLayout(new BorderLayout());
		add(displayNumOfTiles,BorderLayout.CENTER);
		add(showScore,BorderLayout.SOUTH);
	}
}
