package edu.cmu.cs.cs214.hw4.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.cmu.cs.cs214.hw4.core.Tile;
import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;
import edu.cmu.cs.cs214.hw4.core.player.Player;

/**
 * Operation Panel is the panel which holds the rack and special tile rack for the
 * current player; in addition, the current player can also buy special tile in this panel
 * @author Chao
 *
 */
public class OperationPanel extends JPanel{
	private static final long serialVersionUID = 4819162036004676580L;
	private Player currentPlayer;
	private JPanel specialPanel;
	private JPanel rackPanel;
	private ArrayList<Tile> rackTiles;
	private ArrayList<SpecialTile> specialTiles;
	private SpecialTile clickedSpecialTile;
	private Tile clickedTile;
	private Map<Tile, JButton> tileButtons;
	private Map<SpecialTile, JButton> specialButtons;
	private BufferedImage tileImg;
	private BufferedImage specialImg;
	
	public OperationPanel(Player p) {
		this.currentPlayer = p;
		
		rackPanel = new JPanel();
		specialPanel = new JPanel();
		rackTiles = p.getTiles();
		specialTiles = p.getSpecialTiles();
		
		updatePanel();
	}
	
	public void updatePanel(){
		revalidate();
		
		/** Update the tiles in the rack **/
		updateRack();
		clickedTile = null;
		/** Update the special tiles **/
		updateSpecial();
		clickedSpecialTile = null;
		
		setLayout(new BorderLayout());
		this.add(rackPanel, BorderLayout.NORTH);
		this.add(specialPanel, BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	/**
	 * update the regular tiles in rack for the current player
	 */
	private void updateRack(){
		tileButtons = new HashMap<Tile, JButton>();
		rackPanel.removeAll();
		//rackTiles.clear();
		rackTiles = currentPlayer.getTiles();
		JLabel rackJLabel = new JLabel("Tiles:");
		rackJLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		rackJLabel.setForeground(Color.WHITE);
		rackJLabel.setBounds(33,33, 30, 30);
		rackJLabel.setHorizontalTextPosition(0);
		
		
		for (final Tile t : rackTiles) {
			char letter = t.getLetter();
			try {
				tileImg = ImageIO.read(new File("assets/tiles/"+letter+".png"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			JButton tileButton = new JButton(new ImageIcon(tileImg));
			
			tileButton.setBorder(BorderFactory.createEmptyBorder());
			tileButton.setContentAreaFilled(true);
			tileButton.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					clickedTile = t;
					clickedSpecialTile = null;
				}
			});
			tileButtons.put(t,tileButton);
		}
		rackPanel.setBackground(Color.black);
		rackPanel.setLayout(new FlowLayout());
		rackPanel.add(rackJLabel);
		for(Map.Entry<Tile, JButton> entry : tileButtons.entrySet()){
			rackPanel.add(entry.getValue());
		}
		rackPanel.setVisible(true);
	}
	
	/**
	 * update the special tiles in rack for the current player
	 */
	private void updateSpecial(){
		specialButtons = new HashMap<SpecialTile, JButton>();
		specialPanel.removeAll();
		//specialTiles.clear();
		JLabel specialJLabel = new JLabel("Special Tiles:");
		specialJLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		specialJLabel.setForeground(Color.WHITE);
		specialJLabel.setBounds(33,33, 30, 30);
		specialJLabel.setHorizontalTextPosition(0);
		
		
		for (final SpecialTile s : specialTiles) {
			String name = s.toString();
			try {
				specialImg = ImageIO.read(new File("assets/tiles/"+name+".jpg"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			JButton specialButton = new JButton(new ImageIcon(specialImg));
			
			specialButton.setBorder(BorderFactory.createEmptyBorder());
			specialButton.setContentAreaFilled(true);
			specialButtons.put(s,specialButton);
			specialButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					clickedSpecialTile = s;
					clickedTile = null;
				}
			});
		}
		
		JButton buyNewButton = new JButton("BUY NEW");
		buyNewButton.setBackground(Color.yellow);
		buyNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buyNew();
			}
		});
		
		specialPanel.setBackground(Color.black);
		specialPanel.setLayout(new FlowLayout());
		specialPanel.add(specialJLabel);
		for(Map.Entry<SpecialTile, JButton> entry : specialButtons.entrySet()){
			specialPanel.add(entry.getValue());
		}
		specialPanel.add(buyNewButton);
		specialPanel.setVisible(true);	
	}

	private void buyNew(){
		try {
			String message = "Please input the number of special tile you want to buy\n"
					+ " 1. Negative Points Tile for 3 points\n"
					+ " 2. Reverse Order Tile for  3 points\n"
					+ " 3. Boom for 3 points\n"
					+ " 4. Bonus Turn for 3 points\n"
					+ " 5. Extra Turn for 3 points\n";
			int type = Integer.parseInt(JOptionPane.showInputDialog(message));
			//System.out.println(type);
			if (currentPlayer.buySpecialTile(type)) {
				updatePanel();
			} else
				JOptionPane.showMessageDialog(null,"You can't buy a special tile!");
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, "Invalid type");
		}

	}
	/**
	 * get the given player's inventory
	 * 
	 * @param player
	 */
	public void changePlayer(Player player) {
		currentPlayer = player;
		this.rackTiles = currentPlayer.getTiles();
		this.specialTiles = currentPlayer.getSpecialTiles();
		removeAll();
		updatePanel();
	}
	
	/**
	 * player can recall tiles by this method
	 */
	public void recall() {
		updatePanel();
	}
	
	/**
	 * After player uses a tile, this method removes the corresponding button
	 */
	public void remove() {
		updatePanel();
	}
	
	
	public SpecialTile getClickedSpecialTile() {
		return clickedSpecialTile;
	}
	
	public Tile getClickedTile() {
		return clickedTile;
	}
	
	@Override
	public void paintComponent(Graphics g){
		updatePanel();
		super.paintComponent(g);
	}
}
