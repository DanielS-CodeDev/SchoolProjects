//package com.gradescope.garden;
/* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: Flowers.java
* Purpose: This java file is the Flowers class which is the subclass of the Plant class.
* This class defines a special grow method for Flowers which overrides the growPlant() method
* from the Plant class.
*/

public class Flowers extends Plant {
	
	/*
	 * Flowers(coordinate, treeType) -- The constructor of the Flowers class takes in a
	 * integer array of length two which is the 'coordinate' where the plot lives in
	 * the garden. It also takes in a String 'flowerType' which is the specific flower
	 * type. Defines all of the inherited fields from Plant needed for this class
	 * and its own field flowerType.
	 */
	public Flowers(int[] coordinate, String flowerType) {
		super(coordinate, flowerType);
		this.plantFirstLetter = flowerType.toLowerCase().charAt(0);
		this.plot[2][2] = this.getPlantFirstLetter();
	}

	/*
	 * growPlant(amount, plot) -- Takes in an integer 'amount' and a 2D array of
	 * characters 'plot'. This method overrides the super class method. This method
	 * does not return anything but it updates the 'plot' based on the integer
	 * 'amount' and updates the Flower's growth state based on 'amount'. Flowers
	 * bloom and if it meets its max growth state (covering the plot) it can not
	 * grow more. Nothing will happen if grow was called again.
	 */
	@Override
	public void growPlant(int amount, char[][] plot) {

		// Define the displacement for growing at each stage
		int[][] deltaRow = { { 0, -1, 0, 1 }, { 0, -1, -2, -1, 0, 1, 2, 1 }, { -1, -2, -2, -1, 1, 2, 2, 1 },
				{ -2, -2, 2, 2 } };
		int[][] deltaColumn = { { -1, 0, 1, 0 }, { -2, -1, 0, 1, 2, 1, 0, -1 }, { -2, -1, 1, 2, 2, 1, -1, -2 },
				{ -2, 2, 2, -2 } };

		// Grow one step at a time, break when reached max growth.
		for (int i = 0; i < amount; i++) {
			int currState = this.getGrowthState();

			// If the current growth state less than 5 can still grow, else break out
			if (currState < 5) {

				// Loop over the current displacement state of growth
				for (int j = 0; j < (deltaRow[currState - 1]).length; j++) {

					// Displacement for row & column, +2 because middle coord., then set plot
					int rowCoord = (deltaRow[currState - 1][j]) + 2;
					int colCoord = (deltaColumn[currState - 1][j]) + 2;
					plot[rowCoord][colCoord] = this.getPlantFirstLetter();
				}
				this.increaseGrowthState();
			} 
			else {
				break;
			}
		}
	}
}