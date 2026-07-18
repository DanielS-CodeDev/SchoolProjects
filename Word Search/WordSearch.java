/*
* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: WordSeach.java
* Purpose: Takes in a filename from the command line. Reads the file and creates a word search
* grid with all of the words in the file. Uses the WordGrid class to create the grid and this 
* program gets the grid in a String and creates and writes to an output file.
*/
package com.gradescope.wordsearch;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class WordSearch {

	/*
	 * main(args) -- Takes in String[] 'args'. args[0] is the inputed filename.
	 * Reads the file, creates the grid and writes to an output file. Returns
	 * nothing.
	 */
	public static void main(String[] args) {
		WordGrid wordGridObj = null;
		String filename = args[0];

		// Read the file
		try {
			File wordFile = new File(filename);
			Scanner fileScanner = new Scanner(wordFile);
			wordGridObj = setUpGame(fileScanner);
			fileScanner.close();
			wordGridObj.placeAllWords();

		} catch (FileNotFoundException e) {
			System.out.println("ERROR: FILE NOT FOUND.");
		}
		// Write output to the file
		try {
			FileWriter fileWritier = new FileWriter("output_" + filename);
			fileWritier.write(getGridStr(wordGridObj, true));
			fileWritier.close();
		} catch (IOException e) {
			System.out.println("ERROR: INPUT/ OUTPUT ERROR.");
		}
	}

	/*
	 * setUpGame(fileScanner) -- Takes Scanner Object 'fileScanner', reads the file
	 * creates WordGrid object based on file and returns WordGrid Object.
	 */
	public static WordGrid setUpGame(Scanner fileScanner) {

		// Get the dimension of grid then get the words in Arr List
		String[] gridDimen = fileScanner.nextLine().strip().split(" ");
		ArrayList<String> wordList = new ArrayList<>();

		while (fileScanner.hasNextLine()) {
			String word = fileScanner.nextLine().strip();
			wordList.add(word.toUpperCase());
		}

		// Get the width and height as Integer
		Integer width = Integer.valueOf(gridDimen[0]);
		Integer height = Integer.valueOf(gridDimen[1]);

		// Create word search grid and return it
		return new WordGrid(width, height, wordList);
	}

	/*
	 * getGridStr(gridObj, charOn) -- Takes in WordGrid object 'gridObj' and boolean
	 * 'charOn'. Returns a String representation of the grid. Calls strSlot for
	 * filling in slots with Characters. 'charOn' is a debug feature to remove the
	 * filler letters. True filler letters on else off.
	 */
	public static String getGridStr(WordGrid gridObj, boolean charOn) {
		// Create str and get the grid object
		String gridStr = "";
		Character[][] wordGrid = gridObj.gridGetter();

		// Loops over the number of rows
		for (int i = 0; i < wordGrid.length; i++) {
			String rowStr = "";

			// Loops within a specific row, column, -1 so last does not have a space
			for (int j = 0; j < wordGrid[i].length - 1; j++) {
				rowStr += strSlot(wordGrid[i][j], charOn) + " ";
			}
			rowStr += strSlot(wordGrid[i][wordGrid[i].length - 1], charOn);
			gridStr += rowStr + "\n";
		}
		return gridStr;
	}

	/*
	 * strSlot(letter, charOn) -- Takes Character 'letter', boolean 'charOn'. If
	 * 'letter' null fill in with random Character. If 'charOn' wills with alphabet
	 * else fills with '*' for debug mode. If letter not null return Character.
	 * Method returns Character no matter what.
	 */
	private static Character strSlot(Character letter, boolean charOn) {
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();

		// For debug empty space not char
		if ((letter == null) && (!charOn)) {
			return '*';
		}
		// Fill empty gaps with random character
		else if ((letter == null) && (charOn)) {
			return alpha.charAt(random.nextInt(26));
		} else {
			return letter;
		}
	}
}
