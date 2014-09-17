package edu.cmu.cs.cs214.hw4.core;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.FileNotFoundException;
import java.io.IOException;

import edu.cmu.cs.cs214.hw4.core.dictionary.Dictionary;


public class DictionaryTest{
	private Dictionary dictionary;
	
	/** Called before each test case method. */
	@Before
	public void setUp() throws IOException,FileNotFoundException {
		// Start each test case method with a bran new RedBlackTree object.
		dictionary = new Dictionary();
	}

	/** Called after each test case method. */
	@After
	public void tearDown() throws Exception {
		// Don't need to do anything here.
	}
	
	@Test
	public void testContains(){
		assertTrue(dictionary.contains("empty"));
		assertFalse(dictionary.contains("a"));
		assertFalse(dictionary.contains("z"));
	}
	
}
