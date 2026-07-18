/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: SortSpeed.java
 * External Packages: N/A
 * Purpose: The class implements the QuickSort algorithm with using the median
 *      of three approach for choosing a pivot. It uses this approach to ensure
 *      the speed of the algorithmn is consistent no matter what input it
 *      takes. If the list was already sorted, using the median of three does
 *      not slow down the algorithm rather it keeps the speed consistent. This
 *      class is used to sort the speeds of attacks.
 * Class Constructor: N/A. This class is just for a sorting algorithm. This
 *      class is used to sort the speeds from least to greatest from the input
 *      file.
 * Class methods list:
 *      sortBySpeed(array)
 *      quickSort(list, lowIndex, highIndex)
 *      partition(list, lowIndex, highIndex)
 *      medianOfThreeSwap(list, lowIndex, highIndex)
 *      bubbleSort(nums)
 *      printer(list)
 */

public class SortSpeed {

    /*
     * sortBySpeed(array) -- Takes in an array of doubles called 'array'.
     *      method returns the array but sorted from least to greatest. Method
     *      calls the quickSort() method to sort. 
     */
    public double[] sortBySpeed(double[] array) {

        // Call quickSort to sort the double[] array
        quickSort(array, 0, array.length-1);
        return array;
    }

    /*
     * quickSort(list, lowIndex, highIndex) -- Takes in an a double array,
     *      'list' and two integers 'lowIndex' & 'highIndex'. This is method
     *      performs the quickSort algorithm. Method calls partition() to help.
     *      Method does not return anything. Method just manipluates 'list'.
     */
    private void quickSort(double[] list, int lowIndex, int highIndex) {

        // Do not do algo if lowIndex < highIndex
        if (lowIndex < highIndex) {
            // Call partion return index for pivot
            int pivot = partition(list, lowIndex, highIndex); // call partition

            // If pivot == -1, base case so return
            if (pivot == -1) {
                return;
            }
            quickSort(list, lowIndex, pivot-1); // quicksort on left of pivot
            quickSort(list, pivot+1, highIndex); // quicksort on right of pivot
        }
    }

    /*
     * partition(list, lowIndex, highIndex) -- Takes in an array of doubles
     *      'list' and two integers 'lowIndex' & 'highIndex'. This method
     *      performs the partitioning part of the QuickSort algorithm. The
     *      method returns an integer (new pivot). This method is used called
     *      in the quickSort().
     */
    private int partition(double[] list, int lowIndex, int highIndex) {

        // set list with pivot using median of three method
        list = medianOfThreeSwap(list, lowIndex, highIndex); 
        double pivot = list[lowIndex]; // Get pivot value
        int L = lowIndex + 1; // Get L (index)
        int R = highIndex; // Get R (index)

        // If the array list size < 2
        if ((highIndex - lowIndex) < 2) {

            // If lowIndex num > highIndex num swap them
            if (list[lowIndex] > list[highIndex]) {
                double temp = list[lowIndex]; // Hold L value
                list[lowIndex] = list[highIndex]; // Put R val in L spot
                list[highIndex] = temp; // Put L val in R spot
            }
            return -1; // means can't make array small, hit base case
        }

        // Keep looping until L and R index cross
        while (true) {

            // Move L right to find a value > the pivot
            for (int i = L; i < list.length; i++) {
                L = i; // Update R with that index

                // If value > pivot
                if (list[i] > pivot) {
                    break;
                }
            }

            // Move R left to find a value < the pivot
            for (int j = R; j > -1; j--) {
                R = j; // Update R with that index

                // If value < pivot
                if (list[j] < pivot) {
                    break;
                }
            }

            // If L and R index cross break out of loop
            if (R < L) {
                break;
            }

            // Swap the values at the L and R locations
            double temp = list[L]; // Hold L value
            list[L] = list[R]; // Put R val in L spot
            list[R] = temp; // Put L val in R spot
        }

        // Swap the pivot with the value at R
        list[lowIndex] = list[R]; // set pivot index R index value
        list[R] = pivot; // set R index with pivot value
        return R; // return new index of pivot
    }

    /*
     * medianOfThreeSwap(list, lowIndex, highIndex) -- Takes in an array of
     *      doubles, 'list' and two integers 'lowIndex' and 'highIndex'. This
     *      method finds the number that is not the max or the min number out
     *      of three numbers. This method is used in partition() to choose the
     *      pivot. Method returns the double[] of the median number swapped to
     *      the 'lowIndex' for it to be the new pivot. 
     */
    private double[] medianOfThreeSwap(double[] list, int lowIndex, int highIndex) {

        // If the array is not three numbers return the list
        if ((highIndex - lowIndex) < 2) {
            return list;
        }

        double[] nums = new double[3]; // list to hold the 3 nums
        double low = list[lowIndex]; // low value
        double middle = list[(lowIndex + highIndex)/2]; // middle value
        double high = list[highIndex]; // high value
        nums[0] = low; nums[1] = middle; nums[2] = high; // put nums in array

        double medianNum = bubbleSort(nums)[1]; // Get the median num

        // If median is middle number swap those numbers
        if (medianNum == middle) {
            list[lowIndex] = medianNum;
            list[(lowIndex + highIndex)/2] = low;
        }

        // If median is high number swap those numbers
        if (medianNum == high) {
            list[lowIndex] = medianNum;
            list[highIndex] = low;
        }
        return list;
    }

    /*
     * bubbleSort(nums) -- Takes in an array of doubles, 'nums'. Sorts the
     *      numbers from least to greatest. The sorted array is returned. This
     *      is not the most effiecent algorithm but the array length is only 3
     *      numbers since it is used in the medianOfThreeSwap() method.
     */
    private double[] bubbleSort(double[] nums) {

        // Only need to loop through the sort size-1 times
        for (int i = 0; i < (nums.length-1); i++) {

            // Inner loop only needs to loop up till the set numbers
            int innerArrayLoopSize = (nums.length-1) - i;

            // This loop does the swaping
            for (int j = 0; j < innerArrayLoopSize; j++) {

                double leftNum = nums[j]; // left number in the list
                double rightNum = nums[j+1]; // the number to the right in list

                // If leftNum > rightNum swap them
                if (leftNum > rightNum) {
                    nums[j] = rightNum;
                    nums[j+1] = leftNum;
                }
            }
        }
        return nums;
    }

    /*
     * printer(list) -- Takes in an array of doubles, 'list' and prints out
     *      a string representation of the array. This is used for debugging.
     *      Method does not return anything.
     */
    public void printer(double[] list) {
        String str = "["; // string representation of the array

        for (int i = 0; i < list.length - 1; i++) {
            str += list[i] + ", "; // add the numbers to the string
        }

        // Add the last number and print the string representation
        System.out.println(str + list[list.length-1] + "]");
    }
}