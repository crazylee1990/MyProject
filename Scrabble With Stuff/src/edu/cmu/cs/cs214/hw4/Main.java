package edu.cmu.cs.cs214.hw4;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


import edu.cmu.cs.cs214.hw4.gui.UserInputPanel;


public class Main {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Input the player names");
				frame.add(new UserInputPanel(frame));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
				frame.setLocation(800,100);
			}
		});
	}
}
