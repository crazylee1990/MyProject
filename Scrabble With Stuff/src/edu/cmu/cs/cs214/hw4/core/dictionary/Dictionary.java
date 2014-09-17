package edu.cmu.cs.cs214.hw4.core.dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Dictionary {
	private RedBlackTree dictionary = new RedBlackTree();
	
	public Dictionary() {
		setDictionary();
	}
	
	private void setDictionary() {
		try {
			BufferedReader br = new BufferedReader(
					new FileReader("assets/words.txt"));
			try {
				String line = br.readLine();
				while (line != null) {
					dictionary.insert(dictionary, line);
					line = br.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean contains(String word) {
		return dictionary.contains(word);
	}	
}
