//package com.gradescope.garden;
/* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: Plant.java
* Purpose: This java file is the Plant class which is the Super class of the plant hierarchy. This class
* defines many methods and fields that are inherited by all of its sub class (Flowers, Trees, Vegetables). The 
* only method that is overrided is the growPlant() method since each subclass has a different way of implementation. 
*/

public class Plant {

	protected int[] coordinate;
	protected int growthState;
	protected char[][] plot;
	protected char plantFirstLetter;
	protected String specificPlantType;

	/*
	 * Plant(coordinate) -- The constructor of the Plant class takes in a integer
	 * array of length two which is the 'coordinate' where the plot lives in the
	 * garden. Also defines the fields of the class where they will be inherited
	 * by the subclasses.
	 */
	protected Plant(int[] coordinate, String specifcPlantType) {
		this.coordinate = coordinate;
		this.specificPlantType = specifcPlantType;
		this.growthState = 1;
		this.plot = buildPlot();
	}

	/*
	 * buildPlot() -- Has no parameters. This method creates and returns a 2D array
	 * of characters with '.' at each index. This method is called in the
	 * constructor of the Plant class and it is a static method which it can be used
	 * outside of the class as well.
	 */
	public static char[][] buildPlot() {
		char[][] grid = new char[5][5];

		// Each index set a '.' at it.
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				grid[i][j] = '.';
			}
		}
		return grid;
	}

	/*
	 * getCoordinate() -- Has no parameters. This method is a getter method that
	 * returns the integer array for the coordinate of the plant.
	 */
	public int[] getCoordinate() {
		return coordinate;
	}

	/*
	 * getGrowthState() -- Has no parameters. This method is a getter method that
	 * returns an integer for the growth stage of the plant.
	 */
	protected int getGrowthState() {
		return growthState;
	}

	/*
	 * increaseGrowthState() -- Has no parameters. This method is a setter method
	 * that that increase the growthState value by 1. This method does not return
	 * anything.
	 */
	protected void increaseGrowthState() {
		growthState += 1;
	}

	/*
	 * getPlantFirstLetter() -- Has no parameters. This method is a getter method
	 * that returns a single character which is the first letter of the specific
	 * plant type.
	 */
	protected char getPlantFirstLetter() {
		return plantFirstLetter;
	}

	/*
	 * getSpecificPlantType() -- Has no parameters. This method is a getter method
	 * that returns a String of the specific plant types name.
	 */
	protected String getSpecificPlantType() {
		return specificPlantType;
	}

	/*
	 * getPlot() -- Has no parameters. This method is a getter method that returns a
	 * 2D array of characters which is the plants plot.
	 */
	public char[][] getPlot() {
		return plot;
	}

	/*
	 * growPlant(amount, plot) -- Takes in an integer 'amount' and a 2D array of
	 * characters 'plot'. This method is overridden by each subclass. This method
	 * does not return anything but it updates the 'plot' based on the integer
	 * 'amount' and updates the plants growth state based on 'amount'.
	 */
	public void growPlant(int amount, char[][] plot) {
		System.out.println("Method is overriden by subclasses: Flowers, Trees, Vegetables.");
	}

	/*
	 * toString() -- Has no parameters. This method returns a String representation
	 * of a lot a 5 x 5 grid of chars.
	 */
	public String toString() {
		String message = "";

		for (char[] row : this.getPlot()) {
			String rowStr = "";

			// Each row is added to a string then to the main string with '\n'
			for (char character : row) {
				rowStr += character;
			}
			message += rowStr + "\n";
		}
		return message;
	}
	
	/*
	 * remove() -- Has no parameters. This method removes all of the plant from the plot. 
	 * This method does not return anything, rather it resets the plot to be blank.
	 */
	public void remove() {
		this.plot = buildPlot();
	}
	
}