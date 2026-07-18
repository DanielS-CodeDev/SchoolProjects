//package com.gradescope.garden;
/* Author: Daniel Shapiro
* Course: CSC 210 - Fall 2025
* File: Vegetables.java
* Purpose: This java file is the Vegetables class which is the subclass of the Plant class.
* This class defines a special grow method for Vegetables which overrides the growPlant()
* method from the Plant class.
*/

public class Vegetables extends Plant{
	
	/*
	 * Vegetables(coordinate, treeType) -- The constructor of the Vegetables class takes in a
	 * integer array of length two which is the 'coordinate' where the plot lives in
	 * the garden. It also takes in a String 'vegType' which is the specific vegetable
	 * type. Defines all of the inherited fields from Plant needed for this class
	 * and its own field vegType.
	 */
	public Vegetables(int[] coordinate, String vegType) {
		super(coordinate, vegType);
		this.plantFirstLetter = vegType.toLowerCase().charAt(0); 
		this.plot[0][2] = this.getPlantFirstLetter();
	}
	
	/*
	 * growPlant(amount, plot) -- Takes in an integer 'amount' and a 2D array of
	 * characters 'plot'. This method overrides the super class method. This method
	 * does not return anything but it updates the 'plot' based on the integer
	 * 'amount' and updates the Trees growth state based on 'amount'. Vegetables grow down
	 * and if it grows over max depth nothing happens.
	 */
	@Override
	public void growPlant(int amount, char[][] plot) {

		// Grow one step at a time, break when reached max growth.
		for (int i = 0; i < amount; i++) {
			int currState = this.getGrowthState();

			// If the current growth state less than 5 can still grow, else break out
			if (currState < 5) {
				plot[(currState-1) + 1][2] = this.getPlantFirstLetter();
				this.increaseGrowthState();
			} 
			else {
				break;
			}
		}
	}
}