/*
* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: WordGrid.java
* Purpose: This program creates a 2D Character array. Within the array it has words from
* wordList parameter in the constructor. This program creates a word search grid with those words
* and puts them into horizontal, vertical and/ or diagonal orientation. There will be at least one of
* each orientation in the grid.
*/
package com.gradescope.wordsearch;
import java.util.ArrayList;
import java.util.Random;

public class WordGrid {

	private int width;
	private int height;
	private ArrayList<String> wordList;
	private Character[][] grid;

	/*
	 * WordGrid(width, height, wordList) -- Constructor of class. Take in two
	 * integers 'width', 'height' and ArrayList<String> 'wordList'. Sets the fields
	 * of the class.
	 */
	public WordGrid(int width, int height, ArrayList<String> wordList) {
		this.width = width;
		this.height = height;
		this.wordList = wordList;
		this.grid = new Character[this.height][this.width];
	}

	/*
	 * gridGetter() -- No parameters. Returns a Character[][] the this.grid field.
	 */
	public Character[][] gridGetter() {
		return this.grid;
	}

	/*
	 * ternaryChar(isVert, row, col, index) -- Take boolean 'isVert', three integers
	 * 'row', 'col', 'index'. Recreates the ternary operator var = (condition) ?
	 * expressIfTrue : expresIfFalse. If isVert is true returns a Character specific
	 * column. Else returns Character from specific row.
	 */
	private Character ternaryChar(boolean isVert, int row, int col, int index) {
		if (isVert) {
			return this.grid[index][col];
		}
		return this.grid[row][index];
	}

	/*
	 * ternaryAssign(isVert, row, col, index, wordChar) -- Take boolean 'isVert',
	 * three integers 'row', 'col', 'index' and character 'wordChar'. Recreates the
	 * ternary operator var = (condition) ? expressIfTrue : expresIfFalse. If isVert
	 * is true the specific spot in column is assigned to wordChar. Else specific
	 * spot in row is assigned to wordChar.
	 */
	private void ternaryAssign(boolean isVert, int row, int col, int index, char wordChar) {
		if (isVert) {
			this.grid[index][col] = wordChar;
		} else {
			this.grid[row][index] = wordChar;
		}
	}

	/*
	 * verOrHor(word, xCoord, yCoord, isVert) -- Take in String 'word', two integers
	 * 'xCorrd', 'yCoord' and boolean isVert. If isVert true assigned starting and
	 * max accordingly for vertical else same for horizontal. Calls verOfHorHelper
	 * and returns boolean. True if placed else false.
	 */
	private boolean verOrHor(String word, int xCoord, int yCoord, boolean isVert) {
		// starting value for the for loops in helper
		int starting = 0;
		// Max index out of bound check
		int max = 0;

		if (isVert) {
			starting = xCoord;
			max = this.height;
		} else {
			starting = yCoord;
			max = this.width;
		}
		return verOrHorHelper(word, xCoord, yCoord, isVert, starting, max);
	}

	/*
	 * verOrHorHelper(word, row, col, isVert, starting, max) -- Take in String
	 * 'word', four integers 'row', 'col', 'starting', 'max' and boolean 'isVert'.
	 * If 'isVert' true places word vertically. If not possible, index out of max or
	 * collides with another word return false. If word placed in grid return true.
	 * If 'isVert' false place horizontally same logic as vertical placement.
	 */
	private boolean verOrHorHelper(String word, int row, int col, boolean isVert, int starting, int max) {
		// wordLength + row or col exclusive
		int stop = word.length() + starting;

		// Check if out of bounds
		if ((stop) > max) {
			return false;
		}

		// start at point end at stop, check if valid
		for (int i = starting; i < stop; i++) {

			// Get the character and check if empty of match with str
			Character inGridChar = ternaryChar(isVert, row, col, i);
			Character wordChar = word.charAt(i - starting);

			// return false if not null and character not overlap same
			if ((inGridChar != null) && (inGridChar != wordChar)) {
				return false;
			}
		}

		// start at point end at stop, if valid add the word in
		for (int i = starting; i < stop; i++) {
			Character wordChar = word.charAt(i - starting);
			// Add the character to the spot
			ternaryAssign(isVert, row, col, i, wordChar);
		}
		return true;
	}

	/*
	 * diagonal(word, row, col) -- Takes in String 'word', two integers 'row',
	 * 'col'. Checks if the word can be diagonal with the 'row' and 'col' coordinate
	 * input. If not, out of bounds or overlap with another word not correctly
	 * return false. If word is put in the grid return true.
	 */
	private boolean diagonal(String word, int row, int col) {
		// Check if the word is out of bounds for diag
		int stopHoriz = word.length() + col;
		int stopVert = word.length() + row;
		if (stopHoriz > this.width || stopVert > this.height) {
			return false;
		}
		int currRowCheck = row;
		int currColCheck = col;

		// loop through by length of word check if valid placement
		for (int i = 0; i < word.length(); i++) {
			Character charInGrid = this.grid[currRowCheck][currColCheck];

			if ((charInGrid != null) && (charInGrid != word.charAt(i))) {
				return false;
			}
			currRowCheck += 1;
			currColCheck += 1;
		}
		int currRowPlace = row;
		int currColPlace = col;

		// loop through by length of word, place word in grid
		for (int j = 0; j < word.length(); j++) {
			this.grid[currRowPlace][currColPlace] = word.charAt(j);
			currRowPlace += 1;
			currColPlace += 1;
		}
		return true;
	}

	/*
	 * placeAllWords() -- No parameters. Places the first three words of wordList in
	 * each orientation and then the rest of the words are placed but orientation is
	 * randomized. Returns nothing.
	 */
	public void placeAllWords() {

		// Get the first three words
		int wordListSize = this.wordList.size();
		int slice = 3;

		if (wordListSize < 3) {
			slice = wordListSize;
		}

		// Have the first three words placed in each orientation
		for (int i = 0; i < slice; i++) {
			placeWord(this.wordList.get(i), true, i);

		}

		// Rest of the words random orientation
		for (int j = slice; j < wordListSize; j++) {
			placeWord(this.wordList.get(j), false, -1);
		}
	}

	/*
	 * placeWord(word, isFirstThree, index) -- Takes in String 'word', boolean
	 * 'isFirstThree', integer 'index'. If 'isFirstThree' is true it places the word
	 * in a certain orientation based on the index + 1 using specificDirec method.
	 * Else gets a random orientation number to start with and then calls the
	 * placeWordHelper method to place the word in a certain orientation. Returns
	 * nothing.
	 */
	private void placeWord(String word, boolean isFirstThree, int index) {
		Random random = new Random();
		boolean placed = false;

		// While word has not be placed
		while (!placed) {

			int row = random.nextInt(this.height);
			int col = random.nextInt(this.width);

			if (isFirstThree) {
				placed = specificDirec(this.wordList.get(index), row, col, index + 1);
			} else {
				int orientation = random.nextInt(3);
				
				// Orientation + 1 since randomInt is from 0 - 2 but method takes 1 - 3
				placed = placeWordHelper(word, row, col, orientation + 1);
			}
		}
	}

	/*
	 * placeWordHelper(word, row, col, orientation) -- Takes in String 'word', three
	 * integers 'row', 'col', 'orientation'. While specificDirec returns false keep
	 * looping until all three orientation numbers are used to try every type of
	 * orientation for that specific coordinate. Method return false is 'word' was
	 * not place. Return true if 'word' was placed.
	 */
	private boolean placeWordHelper(String word, int row, int col, int orientation) {

		// Random start for orientation of a word
		int oriStart = orientation;

		// Loop 3 times, try each orientation
		while (!specificDirec(word, row, col, orientation)) {
			orientation += 1;

			// If out of amount of orientation change back to 1
			if (orientation > 3) {
				orientation = 1;
			}
			// meaning tried all three need new point
			if (orientation == oriStart) {
				return false;
			}
		}
		// placed move to next word
		return true;
	}

	/*
	 * specificDirec(word, row, col, orientation) -- Takes in String 'word', three
	 * integers 'row', 'col', 'orientation'. Based on 'orientation' number it will
	 * try that specific orientation. If all three orientations do not work for the
	 * specific 'row', 'col' method returns false. If one orientation did work
	 * method returns true.
	 */
	private boolean specificDirec(String word, int row, int col, int orientation) {

		if (orientation == 1 && verOrHor(word, row, col, true)) {
			return true;
		} else if (orientation == 2 && verOrHor(word, row, col, false)) {
			return true;
		} else if (orientation == 3 && diagonal(word, row, col)) {
			return true;
		}
		return false;
	}
}
