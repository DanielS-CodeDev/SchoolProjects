//package com.gradescope.garden;
/* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: RunGarden.java
* Purpose: This java file is the main program. This file reads a file from the command line and
* based on commands in the file it will create and take care of a garden. There are methods that
* are called from other classes in this project to remove plants, grow plant, plant plants and 
* even print plants.  
*/

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class RunGarden {
	
	public static void main(String args[]) {
		
		File inputFile = new File(args[0]);
		Scanner fileScanner;
		
		try {
			fileScanner = new Scanner(inputFile);
			
			// Call the read file method that reads file
			readFile(fileScanner);
			fileScanner.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
	}
	
	/*
	 * readFile(fileScanner) -- Takes in a Scanner object 'fileScanner' and reads
	 * the file by creating a Garden Object and then calls the commandsFunc to
	 * continue to read the file for the commands. There is also error handling as
	 * well. This method does not return anything.
	 */
	public static void readFile(Scanner fileScanner) {
		
		// First two lines are the row and column numbers
		int rowNum = Integer.valueOf(fileScanner.nextLine().split(" ")[1]);
		int colNum = Integer.valueOf(fileScanner.nextLine().split(" ")[1]);
		
		// Skip the blank line
		fileScanner.nextLine();
		
		// Error handling
		if (colNum > 16) {
			System.out.println("Too many plot columns.");
			return; 
		}
		
		// Create the garden object
		Garden gardenObject = new Garden(rowNum, colNum);
		
		// Search the commands
		commandsFunc(fileScanner, gardenObject);
		
	}
	
	// Enum - Command Used for the switch statements in commandsFunc method
	enum Command {PLANT, PRINT, GROW, HARVEST, PICK, CUT};
	
	/*
	 * commandFunc(fileScanner, gardenObject) -- Takes in a Scanner object
	 * 'fileScanner' and a Garden object 'gardenObject'. This method reads over the
	 * file and executes other methods based on the first work of the command given
	 * in the file. This method used the enum class Command and this method calls
	 * many methods from RunGarden and Garden files. This method does not return
	 * anything. (Hard to be < 30 lines with a switch statement.)
	 */
	public static void commandsFunc(Scanner fileScanner, Garden gardenObject) {
		
		while (fileScanner.hasNextLine()) {
			
			// Read the line
			String fileLine = fileScanner.nextLine();
			String[] lineArr = splitLine(fileLine);
			
			// Print the command
			if (!lineArr[0].toLowerCase().equals("plant")) {
				printCommand(lineArr);
			}
			
			switch (Command.valueOf(lineArr[0].toUpperCase())) {
				
				case PLANT:
					int[] coordinate = getCoord(lineArr[1]);
					gardenObject.addPlant(coordinate, lineArr[2].toLowerCase());
					break;
					
				case PRINT: 
					gardenObject.printGarden();
					break;
				
				case GROW: 
					growGarden(lineArr, gardenObject);
					break;
					
				case HARVEST: 
					String[] harvestTypes = {"garlic", "zucchini", "tomato", "yam", "lettuce"};
					removeGarden(lineArr, gardenObject, harvestTypes);
					break;
					
				case PICK: 
					String[] pickTypes = {"iris", "lily", "rose", "daisy", "tulip", "sunflower"};
					removeGarden(lineArr, gardenObject, pickTypes);
					break;
					
				case CUT: 
					String[] cutTypes = {"oak", "willow", "banana", "coconut", "pine"};
					removeGarden(lineArr, gardenObject, cutTypes);
					break;
			}	
			
			if (!lineArr[0].toLowerCase().equals("plant")) {
				System.out.println();	
			}
		}
	}
	
	/*
	 * splitLine(line) -- Takes in a String 'line' and returns the String
	 * as a String array and with the first word in upper case. This method
	 * is called in the commandFunc method.
	 */
	public static String[] splitLine(String line) {
		
		String[] lineArr = line.split(" ");
		
		lineArr[0] = lineArr[0].toUpperCase();
		
		return lineArr;
	}
	
	/*
	 * printCommand(lineArr) -- Takes in a String array 'lineArr' and prints out the
	 * command said in the file in proper format. This method does not return
	 * anything. This method is called in the commandFunc method.
	 */
	public static void printCommand(String[] lineArr) {
		
		// Get the first work command. 
		String message = "> " + lineArr[0];
		
		// Lower case the rest of the command
		for (int i = 1; i < lineArr.length; i++) {
			message += " " + lineArr[i].toLowerCase();
		}
		System.out.println(message);
	}
	
	/*
	 * searchTheArray(arr, thing) -- Takes in a String array 'arr' and a String
	 * 'thing' and this method returns a boolean based on if the 'thing' is
	 * in the array 'arr'. True if the String is in the array, false otherwise. 
	 * This method is also used in Garden.java for the addPlant() method.
	 */
	public static boolean searchTheArray(String[] arr, String thing) {

		for (String item : arr) {
			if (item.equals(thing.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * plantGetStringRow(row) -- Takes in a character array 'row' and returns that
	 * character array as a single string. This method is used in the Garden.java
	 * class, it is used in zipRowsPrint method.
	 */
	public static String plantGetStringRow(char[] row) {
		String rowStr = "";
		for (char cell : row) {
			rowStr += cell;
		}
		return rowStr;
	}
	
	/*
	 * getCoord(strCoord) -- Takes in a String 'strCoord' and returns the String
	 * representation of the coordinate as a integer array of length two. This
	 * method is called in commandFunc, growGarden and removeGarden methods.
	 */
	public static int[] getCoord(String strCoord) {
		
		String[] strSplit = strCoord.split(",");
		String coord1 = strSplit[0].substring(1);
		
		// Up to length -1 want the last character ')' to not be in it
		String coord2 = strSplit[1].substring(0, strSplit[1].length() - 1);
		
		int[] intArr = new int[2];
		
		// Index the Strings at the points of digits, and unwrap them by putting them into the array
		intArr[0] = Integer.valueOf(coord1);
		intArr[1] = Integer.valueOf(coord2);
		
		return intArr;
	}
	
	/*
	 * growGarden(lineArr, gardenObject) -- Takes in a String array 'lineArr' and a
	 * Garden object 'gardenObject'. This method determines which Garden.java
	 * methods to call based on the files commands from 'lineArr' for growing. This
	 * method does not return anything.
	 */
	public static void growGarden(String[] lineArr, Garden gardenObject) {
		
		// Set up some variables used in the if statements
		int amount = Integer.valueOf(lineArr[1]);
		String[] plantType = { "tree", "flower", "vegetable" };
		String[] specificPlantType = { "iris", "lily", "rose", "daisy", "tulip", "sunflower", "oak", "willow", "banana",
				"coconut", "pine", "garlic", "zucchini", "tomato", "yam", "lettuce" };

		// Means it is grow all
		if (lineArr.length == 2) {
			gardenObject.growAll(amount);
		}
		
		// Grow general plant type
		else if (searchTheArray(plantType, lineArr[2])) {
			gardenObject.growPlantType(amount, lineArr[2]);
		}
		
		// Grow specific plant type
		else if (searchTheArray(specificPlantType, lineArr[2])) {
			gardenObject.growSpecificPlant(amount, lineArr[2]);
		}
		
		// Grow the coordinate
		else if (lineArr[2].charAt(0) == '(') {
			int[] coord = getCoord(lineArr[2]);
			gardenObject.growCoord(amount, coord);
		}
	}
	
	/*
	 * removeGarden(lineArr, gardenObject, typeList) -- Takes in a String array
	 * 'lineArr', a Garden object 'gardenObject' and a String array 'typeList'. This
	 * method determines which Garden.java methods to call based on the files
	 * commands from 'lineArr' for removing plants. This method does not return
	 * anything.
	 */
	public static void removeGarden(String[] lineArr, Garden gardenObject, String[] typeList) {
	
		// Means remove all based on HARVEST, PICK, CUT
		if (lineArr.length == 1) {
			gardenObject.removeAll(lineArr[0].toLowerCase());
		}
		
		// Remove the type of plant
		else if (searchTheArray(typeList, lineArr[1])) {
			gardenObject.removeType(lineArr[1].toLowerCase());
		}
		
		// remove the coordinate
		else if (lineArr[1].charAt(0) == '(') {
			int[] coord = getCoord(lineArr[1]);
			gardenObject.removeCoord(lineArr[0], coord);
		}
	}	
}