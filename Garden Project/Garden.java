//package com.gradescope.garden;
/* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: Garden.java
* Purpose: This java file is the Garden class and it implements many methods that use the Plant
* class hierarchy. This class represents the whole garden itself which holds all of the Plant
* plots and it has methods to manipulate those plant plots. 
*/

public class Garden {

	private Plant[][] plantArray;
	private int rowNum;
	private int columnNum;
	enum Command {HARVEST, PICK, CUT};

	/*
	 * Garden(row, column) -- Constructor of Garden class. Takes in two integers
	 * 'row' and 'column'. The constructor creates a 2D array of plant objects based
	 * on the parameters 'row' and 'column'. It does set the fields of the class.
	 */
	public Garden(int row, int column) {
		this.plantArray = new Plant[row][column];
		this.rowNum = row;
		this.columnNum = column;
	}

	/*
	 * getPlantArray() -- Has no parameters. Getter method that returns plantArray.
	 */
	private Plant[][] getPlantArray() {
		return plantArray;
	}

	/*
	 * printGarden() -- Has no parameters. It calls zipRowsPrint method to get a
	 * String representation of every plot in the correct place. Method does not
	 * return anything, it prints all the objects plots in the intended coordinate
	 * order.
	 */
	public void printGarden() {
		String garden = "";

		// Loops over all objects by row then adds objects string chunk to garden string
		for (int i = 0; i < rowNum; i++) {
			garden += zipRowsPrint(i);
		}
		System.out.print(garden);
	}

	/*
	 * zipRowsPrint(mainIndex) -- Takes in integer 'mainIndex', the index of the row
	 * of the plantArray. Method loops over the entire plantArray row. Every plot's
	 * rows are then zipped together into a single String. Every object at
	 * 'mainIndex' row is turned into a String representation plot. Method returns
	 * row objects plots as Strings formatted correctly. This method is called by
	 * the printGarden method.
	 */
	private String zipRowsPrint(int mainIndex) {
		String plantArrayRowStr = "";
		char[][] subPlot;

		// Loops 5 times since that is number of rows a plot
		for (int i = 0; i < 5; i++) {
			String subRowStr = "";

			// loop over how many columns there are in plantArray
			for (int col = 0; col < columnNum; col++) {

				// Index object array and get the plot at the point
				Plant subPlotObject = this.getPlantArray()[mainIndex][col];

				if (subPlotObject == null) {
					subPlot = Plant.buildPlot();
				} else {
					subPlot = subPlotObject.getPlot();
				}
				// Get the row of the plot of the object, call string getter for row
				subRowStr += RunGarden.plantGetStringRow(subPlot[i]);
			}
			plantArrayRowStr += subRowStr + "\n";
		}
		// Returns the object rows plots as all strings together
		return plantArrayRowStr;
	}

	/*
	 * addPlant(coordinate, plantName) -- Takes in an integer array of length 2
	 * 'coordinate' and a String 'plantName'. Based on the 'plantName' this method
	 * will create and add the plant that is specified in 'plantName' to the
	 * plantArray of Objects at the correct location as well. This method does not
	 * return anything, it manipulates the plantArray. This method calls
	 * searchInArray method to check if the string is in the array of plant types.
	 */
	public void addPlant(int[] coordinate, String plantName) {
		String[] flowerTypes = { "iris", "lily", "rose", "daisy", "tulip", "sunflower" };
		String[] treeTypes = { "oak", "willow", "banana", "coconut", "pine" };
		String[] vegTypes = { "garlic", "zucchini", "tomato", "yam", "lettuce" };

		if (RunGarden.searchTheArray(flowerTypes, plantName)) {
			plantArray[coordinate[0]][coordinate[1]] = new Flowers(coordinate, plantName);
		} else if (RunGarden.searchTheArray(treeTypes, plantName)) {
			plantArray[coordinate[0]][coordinate[1]] = new Trees(coordinate, plantName);
		} else if (RunGarden.searchTheArray(vegTypes, plantName)) {
			plantArray[coordinate[0]][coordinate[1]] = new Vegetables(coordinate, plantName);
		}
	}

	/*
	 * growAll(amount) -- Takes in integer 'amount', number that all of the plants
	 * need to grow in the plantArray. Method does not return anything. This method
	 * calls the growCoord method to help grow at specific coordinates.
	 */
	public void growAll(int amount) {

		// Row, Loop over the plantArray
		for (int i = 0; i < this.getPlantArray().length; i++) {

			// Column, Loop over the plantArray
			for (int j = 0; j < this.getPlantArray()[i].length; j++) {

				// Grow at that coordinate
				int[] coordinate = { i, j };
				this.growCoord(amount, coordinate);
			}
		}
	}

	/*
	 * growCoord(amount, coordinate) -- Takes integer 'amount' and integer array
	 * length two, 'coordinate'. Method grows a plant at a specific coordinate with
	 * a specific amount. Method is called in the growAll() but does not return
	 * anything and.
	 */
	public void growCoord(int amount, int[] coordinate) {
		int c1 = coordinate[0];
		int c2 = coordinate[1];

		if (c1 < this.rowNum && c2 < this.columnNum) {

			// Get the plant object and then its plot and check if not null
			Plant pObject = getPlantArray()[c1][c2];

			// Make sure you are not growing a null object
			if (pObject != null) {

				char[][] plantPlot = pObject.getPlot();

				// Grow the plot based in the amount
				pObject.growPlant(amount, plantPlot);
			}
		} else {
			// Error handling if coordinates are out of range
			System.out.println("\nCan't grow there.");
		}
	}

	/*
	 * growCoord(amount, specificPlant) -- Takes in integer 'amount' and String
	 * 'specificPlant'. Method grows all the plants that have the 'specificPlant'
	 * name and grows them all 'amount' of times. Method does not return anything.
	 */
	public void growSpecificPlant(int amount, String specificPlant) {

		// Row, Loop over the plantArray
		for (int i = 0; i < this.getPlantArray().length; i++) {

			// Column, Loop over the plantArray
			for (int j = 0; j < this.getPlantArray()[i].length; j++) {

				// Get the plant object
				Plant pObject = getPlantArray()[i][j];

				// Might be an empty plot
				if (pObject != null) {
					String pObjectName = pObject.getSpecificPlantType();

					if (specificPlant.equals(pObjectName)) {

						// Then get plot and grow it
						char[][] plantPlot = pObject.getPlot();
						pObject.growPlant(amount, plantPlot);
					}
				}
			}
		}
	}

	/*
	 * growPlantType(amount, plantType) -- Takes in integer 'amount' and String
	 * 'plantType'. This method grows all the plants that have 'plantType' and grows
	 * them all 'amount' of times. This method does not return anything.
	 */
	public void growPlantType(int amount, String plantType) {
		// Row, Loop over the plantArray
		for (int i = 0; i < this.getPlantArray().length; i++) {

			// Column, Loop over the plantArray
			for (int j = 0; j < this.getPlantArray()[i].length; j++) {

				// Get the plant object
				Plant pObject = getPlantArray()[i][j];

				// Also check, make sure pObject not null
				if ((((plantType.equals("flower")) && pObject instanceof Flowers)
						|| ((plantType.equals("tree")) && pObject instanceof Trees)
						|| ((plantType.equals("vegetable")) && pObject instanceof Vegetables)) && pObject != null) {

					// Then get plot and grow it
					char[][] plantPlot = pObject.getPlot();
					pObject.growPlant(amount, plantPlot);
				}
			}
		}
	}

	/*
	 * removeAll(command) -- Takes in String 'command' and based on command will
	 * remove all of the plants that are allowed to be removed with that command.
	 * This method does not return anything.
	 */
	public void removeAll(String command) {

		// Row, Loop over the plantArray
		for (int i = 0; i < this.getPlantArray().length; i++) {

			// Column, Loop over the plantArray
			for (int j = 0; j < this.getPlantArray()[i].length; j++) {

				// Get the plant object
				Plant pObject = getPlantArray()[i][j];

				// Also check, make sure pObject not null
				if (((command.equals("harvest") && pObject instanceof Vegetables)
						|| (command.equals("pick") && pObject instanceof Flowers)
						|| (command.equals("cut") && pObject instanceof Trees)) && pObject != null) {
					pObject.remove();
				}
			}
		}
	}

	/*
	 * removeCoord(commandStr, coordinate) -- Takes in String 'commandStr' and
	 * integer array length two, 'coordinate'. Method removes the plant at
	 * the coordinate based on 'command'. If the plant does not take that command
	 * then an error message is printed. This method does not return anything.
	 */
	public void removeCoord(String commandStr, int[] coordinate) {

		// Get the plant object & make sure object is not null
		Plant pObject = getPlantArray()[coordinate[0]][coordinate[1]];
		if (pObject != null) {

			switch (Command.valueOf(commandStr.toUpperCase())) {
			case HARVEST:
				if (pObject instanceof Vegetables) {
					pObject.remove();
				} else {System.out.println("\nCan't harvest there.");}
				break;

			case PICK:
				if (pObject instanceof Flowers) {
					pObject.remove();
				} else {System.out.println("\nCan't pick there.");}
				break;

			case CUT:
				if (pObject instanceof Trees) {
					pObject.remove();
				} else {System.out.println("\nCan't cut there.");}
				break;
			}
		}
	}

	/*
	 * removeType(command, specificType) -- Takes in a String 'commandStr' and a
	 * String 'specificType'. This method removes all of the plants that have that
	 * specific type. If there are no plants with that specific type, nothing
	 * happens. This method does not return anything.
	 */
	public void removeType(String specificType) {
		// Row, Loop over the plantArray
		for (int i = 0; i < this.getPlantArray().length; i++) {

			// Column, Loop over the plantArray
			for (int j = 0; j < this.getPlantArray()[i].length; j++) {

				// Get the plant object
				Plant pObject = getPlantArray()[i][j];

				// Make sure not null, so plot is not empty
				if (pObject != null && pObject.getSpecificPlantType().equals(specificType)) {
					pObject.remove();
				}
			}
		}
	}
}