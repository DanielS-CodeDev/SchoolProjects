//package com.gradescope.garden;
/* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: Trees.java
* Purpose: This java file is the Trees class which is the subclass of the Plant class.
* This class defines a special grow method for Trees which overrides the growPlant()
* method from the Plant class.
*/

public class Trees extends Plant {

	/*
	 * Trees(coordinate, treeType) -- The constructor of the Trees class takes in a
	 * integer array of length two which is the 'coordinate' where the plot lives in
	 * the garden. It also takes in a String 'treeType' which is the specific tree
	 * type. Defines all of the inherited fields from Plant needed for this class
	 * and its own field treeType.
	 */
	public Trees(int[] coordinate, String treeType) {
		super(coordinate, treeType);
		this.plantFirstLetter = treeType.toLowerCase().charAt(0);
		this.plot[4][2] = this.getPlantFirstLetter();
	}

	/*
	 * growPlant(amount, plot) -- Takes in an integer 'amount' and a 2D array of
	 * characters 'plot'. This method overrides the super class method. This method
	 * does not return anything but it updates the 'plot' based on the integer
	 * 'amount' and updates the Trees growth state based on 'amount'. Trees grow up
	 * and if it grows over max height nothing happens.
	 */
	@Override
	public void growPlant(int amount, char[][] plot) {

		// Grow one step at a time, break when reached max growth.
		for (int i = 0; i < amount; i++) {
			int currState = this.getGrowthState();

			// If the current growth state less than 5 can still grow, else break out
			if (currState < 5) {
				plot[(5 - currState) - 1][2] = this.getPlantFirstLetter();
				this.increaseGrowthState();
			} 
			else {
				break;
			}
		}
	}
}