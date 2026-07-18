/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: sortTypes.java
 * External Packages: N/A
 * Purpose: This class has one public method that implments the counting sort
 *      algorithmn. This class will sort the types in order: "lightning",
 *      "ice", "fire". The input Strings can be in any case. The sorted output
 *      Strings will be in lowercase.
 * Class Constructor: N/A. This class is just a method that implements the
 *      counting sort algorithm. 
 * Class methods list:
 *      sortTypes(unSortedTypes)
 *      countingSort(unSortedTypes)
 *      createCountingSort(tallyArray, lenOfArray)
 *      printer(list)
 */

public class SortTypes {

    /*
     * sortTypes(unSortedTypes) -- Takes in a String array 'unSortedTypes' and
     *      calls countingSort() to perform the counting sort algorithm on the
     *      unsorted String[]. Method returns the sorted String[]. The types
     *      are sorted in order: "lightning", "ice", "fire". 
     */
    public String[] sortTypes(String[] unSortedTypes) {
        return countingSort(unSortedTypes);
    }

    /*
     * countingSort(unSortedTypes) -- Takes in a String array of String types
     *      'unSortedTypes'and tallies up all three types. Index 0 holds the
     *      tallies of lightning, index 1 holds tallies for ice then index 2 is
     *      for fire. Method calls createCountingSort() to create the sorted
     *      array off of the tallies. This method returns the output from 
     *      createCountingSort() which is a String[] that is sorted. The types
     *      are sorted in order: "lightning", "ice", "fire". 
     */
    private String[] countingSort(String[] unSortedTypes) {

        // 0 - Lightning, 1 - Ice, 2 - Fire
        int[] countArray = new int[3];

        // Loop over the unsorted String[] tally up the types
        for (int i = 0; i < unSortedTypes.length; i++) {
            String type = unSortedTypes[i].toLowerCase(); // get the type

            if (type.equals("lightning")) {
                countArray[0] += 1; // Increment count for lightning
            }

            else if (type.equals("ice")) {
                countArray[1] += 1; // Increment count for ice
            }

            else if (type.equals("fire")) {
                countArray[2] += 1; // Increment count for fire
            }
        }

        // Call and return the a sorted array
        return createCountingSort(countArray, unSortedTypes.length);
    }

    /*
     * createCountingSort(tallyArray, lenOfArray) -- Takes in a integer array
     *      'tallyArray' and an integer 'lenOfArray'. Method loops over the
     *      tallies and creates the sorted list off of the tallies. The tallies
     *      in index 0 is for lightning, index 1 is for ice and index 2 is for
     *      fire. A String array of those three types are returned sorted in
     *      order: "lightning", "ice", "fire". 
     */
    private String[] createCountingSort(int[] tallyArray, int lenOfArray) {

        // Create the sortedTypes array
        String[] sortedTypes = new String[lenOfArray];

        int index = 0; // index counter for the sortedTypes array

        // Loop over the countArray to get the tallies
        for (int j = 0; j < tallyArray.length; j++) {

            // Loop over the tally count and add those strings to the sort list
            for (int l = 0; l < tallyArray[j]; l++) {

                // 0 - Lightning, 1 - Ice, 2 - Fire   (index of str count)

                if (j == 0) {
                    sortedTypes[index] = "lightning";
                }

                if (j == 1) {
                    sortedTypes[index] = "ice";
                }

                if (j == 2) {
                    sortedTypes[index] = "fire";
                }
                index++;
            }
        }
        return sortedTypes;
    }

    /*
     * printer(list) -- Takes in an array of integers, 'list' and prints out
     *      a string representation of the array. This is used for debugging.
     *      Method does not return anything.
     */
    public void printer(int[] list) {
        String str = "["; // string representation of the array

        for (int i = 0; i < list.length - 1; i++) {
            str += list[i] + ", "; // add the numbers to the string
        }

        // Add the last number and print the string representation
        System.out.println(str + list[list.length-1] + "]");
    }
}