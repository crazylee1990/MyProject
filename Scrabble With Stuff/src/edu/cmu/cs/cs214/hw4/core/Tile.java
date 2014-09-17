package edu.cmu.cs.cs214.hw4.core;

/**
 * This is the regular tile class with letter and points as variables
 * @author Chao
 */
public class Tile {
	private char letter;
	private int points;
	
	public Tile(char letter) {
		this.letter = letter;
		this.points = pointsOfLetter(letter);
	}
	
	public char getLetter() {
		return letter;
	}
	
	public int getPoints() {
		return points;
	}
	
	public String toString() {
		return letter + "(" + points + ")";
	}
	
	private int pointsOfLetter(char letter) {
		switch(letter){
		case 'A': return  1;
		case 'B': return  3;
		case 'C': return  3;
		case 'D': return  2;
		case 'E': return  1;
		case 'F': return  4;
		case 'G': return  2;
		case 'H': return  4;
		case 'I': return  1;
		case 'J': return  8;
		case 'K': return  5;
		case 'L': return  1;
		case 'M': return  3;
		case 'N': return  1;
		case 'O': return  1;
		case 'P': return  3;
		case 'Q': return 10;
		case 'R': return  1;
		case 'S': return  1;
		case 'T': return  1;
		case 'U': return  1;
		case 'V': return  4;
		case 'W': return  4;
		case 'X': return  8;
		case 'Y': return  4;
		case 'Z': return 10;
		default:
			throw new IllegalArgumentException("illegal character "+letter);
		}
	}
}
