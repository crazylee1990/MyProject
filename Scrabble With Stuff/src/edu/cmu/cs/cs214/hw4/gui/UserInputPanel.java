package edu.cmu.cs.cs214.hw4.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.cmu.cs.cs214.hw4.core.ScrabbleGame;

public class UserInputPanel extends JPanel{
	private static final long serialVersionUID = 6647535125556634481L;
	private final JFrame parentFrame;
	private final ArrayList<String> names;

	public UserInputPanel(JFrame frame) {
		this.parentFrame = frame;
		this.names = new ArrayList<String>();

		JPanel participantPanel = new JPanel();
		// Create the components to add to the panel.
		JLabel participantLabel = new JLabel("Name: ");
		// Must be final to be accessible to the anonymous class.
		final JTextField participantText = new JTextField(20);
		JButton participantButton = new JButton("Add a player");

		participantPanel.setLayout(new BorderLayout());
		participantPanel.add(participantLabel, BorderLayout.WEST);
		participantPanel.add(participantText, BorderLayout.CENTER);
		participantPanel.add(participantButton, BorderLayout.EAST);

		final JTextArea playerList = new JTextArea("Please Input 2-10 players:\n", 10, 5);
		playerList.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(playerList);
		 // Defines an anonymous class to handle.
		ActionListener newParticipantListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = participantText.getText();
				if (!name.isEmpty() && !names.contains(name)) {
					if(names.size()<10){
						names.add(name);
					}else {
						String info = "You can not add more players";
						JOptionPane.showMessageDialog(parentFrame, info);
					}
				}else if(names.contains(name)){
					String infoString = "This player Has already exist;";
					JOptionPane.showMessageDialog(parentFrame, infoString);
				}
				playerList.setText("");
				playerList.append("Please Input 2-9 players:\n");
				for(String na: names){
					playerList.append("Player"+(names.indexOf(na)+1)+": "+na+"\n");
				}
				participantText.setText("");
				participantText.requestFocus();
			}
		};

        // notify the action listener when participantButton or
        // participantText-related events happen.
		participantButton.addActionListener(newParticipantListener);
		participantText.addActionListener(newParticipantListener);

		JButton createButton = new JButton("Start a new game");
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!names.isEmpty() && names.size() >= 2) {
                    // Starts a new game when the createButton is clicked.
					startScrabbleGame();
				}
				else {
					showDialog(parentFrame, "Warning","Please input at least two players' names");
				}
			}
		});
		
		
		
		
		// Adds the components we've created to the panel (and to the window).
		setLayout(new BorderLayout());
		add(participantPanel, BorderLayout.NORTH);
		add(createButton, BorderLayout.CENTER);
		add(scrollPane,BorderLayout.SOUTH);
		setVisible(true);
	}
	
	/**
	 * Starts a new chat, opening one window for each participant.
	 */
	private void startScrabbleGame(){
		final ScrabbleGame game = new ScrabbleGame(names);
		final String title = "SCRABBLE";

        // Removes the initial dialog panel and starts the first participant's
        // chat in the existing window.
		parentFrame.setTitle(title);
		parentFrame.remove(this);
	
		parentFrame.add(new GamePanel(game,parentFrame));

		parentFrame.setResizable(true);
		parentFrame.setVisible(true);
		parentFrame.pack();
	}
	
	private static void showDialog(Component component, String title, String message) {
	        JOptionPane.showMessageDialog(component, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
