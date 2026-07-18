/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: SortDamage.java
 * External Packages: java.util.ArrayList
 * Purpose: 
 * Class Constructor: N/A. This class is just for a sorting algorithm. This
 *      class has a method that uses Merge Sort which is a stable sort that
 *      sorts and integer array from greatest to least.
 * Class methods list:
 *      sortDamage(damageArray)
 *      Mergesort(damageArray, low, high)
 *      merge(damageArray, low, mid, high)
 *      printer(list)
 */
import java.util.ArrayList;

public class SortDamage {

    /*
     * sortDamage(damageArray) -- Takes in an unsorted integer array
     *      'damageArray' and calls Mergesort on the array and returns the
     *      array sorted from greatest to least with stable sorting.
     */
    public int[] sortDamage(int[] damageArray) {
        Mergesort(damageArray, 0, damageArray.length -1);
        return damageArray;
    }

    /*
     * Mergesort(damageArray, low, high) -- Takes in an integer array
     *      'damageArray' and two integers 'low' and 'high'. This method does
     *      the Merge Sort algorithmn on the int[]. Method calls merge() and
     *      method does not return anything. The inputed array is sorted.
     */
    private void Mergesort(int[] damageArray, int low, int high) {

        if (low < high) {

            // Get the 'mid' index
            int mid = (low + high) / 2;

            // MergeSort on the left then the right of the list
            Mergesort(damageArray, low, mid);
            Mergesort(damageArray, mid+1, high);

            // Merge the two halfs of the list
            merge(damageArray, low, mid, high);
        }
    }

    /*
     * merge(damageArray, low, mid, high) -- Takes in an integer array
     *      'damageArray' and three integers 'low', 'mid' and 'high'. This
     *      method does the "zipping" together of the two already sorted
     *      arrays. The first array is [low to mid] (inclusive) and the second
     *      array is [mid+1 to high] (inclusive). Method sorts the two lists
     *      together into one ArrayList from greatest to least and then at the
     *      end the inputed array 'damageArray' is updated for the current
     *      section. Nothing is returned.
     */
    private void merge(int[] damageArray, int low, int mid, int high) {

        // ArrayList to temporarly hold the sorted chunk
        ArrayList<Integer> tempSorted = new ArrayList<>();

        int leftIndex = low; // Left starting index for left list
        int rightIndex = mid+1; // right starting index for right list

        while (true) {

            // Means that all of the left side has been sorted away
            if (leftIndex > mid) { // > since mid is included here

                // Add the rest of the right side to the sorted array
                for (int i = rightIndex; i < high+1; i++) { // includes high
                    tempSorted.add(damageArray[i]); // Add the rest to list
                }
                break; // exit loop -- done sorting
            }

            // Means that all of the right side has been sorted array
            if (rightIndex >= high+1) {

                // Need to insert to the start of the arrayList for left side
                for (int i = leftIndex; i < mid+1; i++) {
                    tempSorted.add(damageArray[i]); // add to list
                }
                break;
            }

            // If left side >= add to sort list -- stable sorting
            if (damageArray[leftIndex] >= damageArray[rightIndex]) {
                tempSorted.add(damageArray[leftIndex]);
                leftIndex++; // increase the left list index
            }

            // Else right list is greater than the left list add to sort list
            else {
                tempSorted.add(damageArray[rightIndex]);
                rightIndex++; // increase the right list index
            }
        }
        // put the sorted arraylist into the damageArray
        for (int j = low; j < high+1; j++) {
            damageArray[j] = tempSorted.get(j - low);
        }
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