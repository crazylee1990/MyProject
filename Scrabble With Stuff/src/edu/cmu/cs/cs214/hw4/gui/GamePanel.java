package edu.cmu.cs.cs214.hw4.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.cmu.cs.cs214.hw4.core.Location;
import edu.cmu.cs.cs214.hw4.core.ScrabbleGame;
import edu.cmu.cs.cs214.hw4.core.Tile;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.player.Player;

/**
 * This is the overall panel for the scrabble game, it includes display panel, board panel, and
 * operation panel; It also defines the action listeners of the buttons in the game
 * @author Chao
 *
 */
public class GamePanel extends JPanel {
	private static final long serialVersionUID = 4819162036004676580L;
	private final ScrabbleGame game;
	private final JFrame parentFrame;
	private final DisplayPanel displayPanel;
	private final BoardPanel boardPanel;
	private final OperationPanel operationPanel;
	private final JPanel bottomPanel;
	private final JPanel rightPanel;
//	private final JPanel scrabbleImg;
	private final JLabel jlpic;  

	public GamePanel(ScrabbleGame g,JFrame frame){
		this.game = g;
		this.parentFrame = frame;
		//instantiate a new display at the top of the game window
		displayPanel = new DisplayPanel(game.getPlayers(),game.getCurrentPlayer(),game.getTileBag());
		//instantiate a new board panel at the center of the game window
		boardPanel = new BoardPanel(game.getBoard(),game.getCurrentPlayer());
		//instantiate a new operation panel at the bottom of the game window
		operationPanel = new OperationPanel(game.getCurrentPlayer());
		
		//Button set panel means the buttons at the bottom
		final JPanel buttonSetPanel = new JPanel();
		JButton recallButton = new JButton("Recall");
		JButton exchangeButton = new JButton("Exchange");
		JButton playButton = new JButton("Play");
		JButton passButton = new JButton("Pass");
		
		//set the style of the buttons
		recallButton.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		playButton.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		passButton.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		exchangeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 36));

		//set the layout of the button set
		buttonSetPanel.setLayout(new FlowLayout());
		buttonSetPanel.add(recallButton);
		buttonSetPanel.add(exchangeButton);
		buttonSetPanel.add(playButton);
		buttonSetPanel.add(passButton);
		buttonSetPanel.setVisible(true);
		
		//add a listener for the recall button, which will call the recall
		//method to recall the tiles on the board
		recallButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				recall();
			}
		});
		
		//this listener will exchange tiles for the player from the tile bag
		exchangeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(game.exchangeTiles(game.getCurrentPlayer())){
					operationPanel.updatePanel();
				}
			}
		});
		
		//play button listener will call the play method in the game
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if(game.getBoardReferee().newLocs.size() != 0){
					if(game.newRoundPlay()){
						if(game.decideWinner()){
							String info = "Game Over! Winner is "+ game.getWinner();
							JOptionPane.showMessageDialog(parentFrame,info);
						}else {
							ArrayList<SpecialTile> specialApplied = game.getBoardReferee().getSpecialApplied();
							if( specialApplied != null){
								showSpecialTileInfo(specialApplied);
							}
							
							game.changePlayer();
							Player p = game.getCurrentPlayer();
							displayPanel.change(p);
							operationPanel.changePlayer(p);
							boardPanel.changePlayer(p);
							repaint();
						}
					}else {
						String info = "Error! This is not a valid play";
						JOptionPane.showMessageDialog(parentFrame, info);
						recall();
					}
				}else{
					String info = "Please at least put one tile down to finish the play!";
					JOptionPane.showMessageDialog(parentFrame, info);
				}
				
			}
		});
		
		//pass button will pass this round and change player
		passButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				game.changePlayer();
				String info = "You chose to pass, now it's " +game.getCurrentPlayer().getName()+" 's turn";
				JOptionPane.showMessageDialog(parentFrame, info);
				displayPanel.change(game.getCurrentPlayer());
				operationPanel.changePlayer(game.getCurrentPlayer());
				boardPanel.changePlayer(game.getCurrentPlayer());
				repaint();
			}
		});
		
		
		// add mouse listener to boardPanel
		boardPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				//System.out.println(x +" "+y);
				Tile t = operationPanel.getClickedTile();
				SpecialTile s = operationPanel.getClickedSpecialTile();
				Location clickedLoc = new Location(x/40+1, y/40+1);
//				System.out.print(clickedLoc);
//				System.out.println(clickedLoc.getX()+" "+clickedLoc.getY());
				if(t != null){
					try {
						if(game.addTilesOnBoard(t, clickedLoc)){
							operationPanel.remove();
							repaint();
						}else {
							String info = "You can not put this tile here";
							JOptionPane.showMessageDialog(parentFrame, info);
						}
					} catch (IllegalArgumentException e2) {
						String info = "Please choose an empty location";
						JOptionPane.showMessageDialog(parentFrame, info);
					}
					
				}else if(s != null) {
					game.addSpecialTile(s, clickedLoc);
					operationPanel.remove();
					repaint();
				}else{
					String info = "Please select a tile first";
					JOptionPane.showMessageDialog(parentFrame, info);
				}
			}
		});
		
		
		
//		scrabbleImg = new JPanel() {  
//			  
//            protected void paintComponent(Graphics g) {  
//                ImageIcon icon = new ImageIcon("assets\\scrabble.png");  
//                Image img = icon.getImage();  
//                g.drawImage(img, 0, -50, icon.getIconWidth(),icon.getIconHeight()*2, icon.getImageObserver());  
////                jF.setSize(icon.getIconWidth(), icon.getIconHeight());  
//            }  
//  
//        };  
//        scrabbleImg.setVisible(true);
		jlpic = new JLabel();
		ImageIcon icon = new ImageIcon("assets\\scrabble.png");  
        icon.setImage(icon.getImage().getScaledInstance(icon.getIconWidth(),  
                icon.getIconHeight(), Image.SCALE_DEFAULT));  
        System.out.println(icon.getIconHeight() + "" + icon.getIconWidth());  
        jlpic.setBounds(0, 0, 550, 300);  
        jlpic.setHorizontalAlignment(0);  
        jlpic.setIcon(icon);  
		
		
		
		
		//put the button set panel and operation Panel together as bottom panel
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(operationPanel,BorderLayout.NORTH);
		bottomPanel.add(buttonSetPanel,BorderLayout.SOUTH);
		bottomPanel.setVisible(true);

		
		
		
		
		
		
//		combine the bottomPanel, displayPanel, scrabbleImg together;
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		
		rightPanel.add(displayPanel,BorderLayout.NORTH);
		rightPanel.add(jlpic,BorderLayout.CENTER);
		rightPanel.add(bottomPanel,BorderLayout.SOUTH);
		rightPanel.setVisible(true);
		
		// set the overall layout of the game panel
		setLayout(new FlowLayout());
		add(boardPanel);
		add(rightPanel);
		
		
//        setLayout(new BorderLayout());
//        add(displayPanel,BorderLayout.NORTH);
//        add(boardPanel, BorderLayout.CENTER);
//        add(bottomPanel,BorderLayout.SOUTH);
        setVisible(true);

	}
	
	private void recall(){
		game.recall();
		operationPanel.recall();
		boardPanel.recall();
		repaint();
	}
	
	private void showSpecialTileInfo(ArrayList<SpecialTile> specials){
		String info = "Oops! You touched the ";
		for(SpecialTile s:specials){
			switch (s.getID()) {
			case 1:
				info = info + s.toString().toLowerCase()+", you will get negative " +
						"negative points for this turn, sorry!";
				break;
			case 2:
				info = info + s.toString().toLowerCase()+", the order of the game" +
						"will be reversed. Good Luck guys~";
				break;
			case 3:
				info = info + s.toString().toLowerCase()+",a intensive explosion" +
						"happened at the 3*3 radius locations";
				break;
			case 4:
				info = info + s.toString().toLowerCase()+", player "+ s.getWhoseTile().getName()+
				 		" will get a bonus turn next time he play";
				break;

			case 5:
				info = info + s.toString().toLowerCase()+", player "+ s.getWhoseTile().getName()+
		 		" will take over after you finish the play";
				break;
			
			}
			//System.out.println(s.getID());
		}
		JOptionPane.showMessageDialog(parentFrame, info);
		game.getBoardReferee().resetSpecialApplied();
	}
}
