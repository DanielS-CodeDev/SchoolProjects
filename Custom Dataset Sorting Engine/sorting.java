/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: sorting.java
 * External Packages: java.util.ArrayList, java.util.Scanner, java.io.File
 *      java.io.FileNotFoundException, java.util.HashMap
 * Purpose: Main program file. Takes in a command line argument like:
 *      'java sorting input.txt' and reads an input file. Creates 3 sorted
 *      arrays. A1: doubles (Speed) from least to greatest which are all unique
 *      numbers [0,1]. A2: integer (damage) from greatest to least [1, 1
 *      million]. A3: String (type), 3 types sorted as: "lightning", "ice", 
 *      "fire" which are all lower cased. Then the decision process of how
 *      Logic will defend off Lord Bugs attacks method is called.
 * Class Constructor: N/A, main program file.
 * Class methods list:
 *      readFile()
 *      createHashMap(attacksList)
 *      createA1(attacksList)
 *      createA2(A1, attacksList, attacksHMap)
 *      createA3(A1, attacksList, attacksHMap)
 *      main(args)
 */

/*
 * Why did I choose the sorting algorithms that I did?
 *      I started picking the algorithms for A3, I picked Counting Sort since
 *      it was the best for sorting just three types of Strings and its Big-O
 *      is O(n+k). Since there are just three types of Strings is was not as
 *      memory costly for the array due to only needing three slots. 
 *      The next algorithm I choose was for A2. I wanted to pick a stable sort
 *      so I picked Merge Sort with all the cases being O(n*logn). I later
 *      realized being Stable did not matter for any of these arrays since I
 *      just need to remove the first occurence of the data that I am trying to
 *      find since the ordering of duplicates does not matter, just the data
 *      since removing one of the duplicates from either A2 or A3 has the same
 *      effect of selecting a specific one.
 *      For sorting the speeds I decided to pick QuickSort because I felt that
 *      the analysis on it was one of the better ones for sorting doubles
 *      with an unknownly amount of doubles. I focused on picking based on the
 *      average and worst cases because I felt that those would occur the most.
 *      QuickSort best case and avergae case is O(n*logn), its worst case is
 *      O(n^2). To imporve QuickSort I implemented the "Median of Three"
 *      approach to help prevent the worst case from happening. To implement
 *      the "Median of Three" approach, to find the median I used Bubble Sort
 *      on just three numbers and indexed the array at index 1. Bubble Sort is
 *      fine to use on just three numbers since at most 3 swaps are needed.
 *      The other option that I had was ShellSort which the worst case was the
 *      same but the average was O(n^(3/2)) which is worst for large n compared
 *      to QuickSort. 
 * 
 * Would I try anything different in retrospect?
 *      I overall felt that I picked very efficent algorithms for the set of
 *      data. For Merge Sort I could of grouped the data by runs or even did
 *      galloping for the merging of the lists. I feel that this would improve
 *      the efficency a bit for large data but I am assuming that this project
 *      is just getting small data sets but to combat the theortical unlimited
 *      amount of input I would implment these into Merge Sort. Also for the
 *      tombstones of removing the elements from their repsective arrays, for
 *      large data I could implement a counter of the number of tombstones and
 *      once it gets to that amount it would remove all of them and consolidate
 *      the data. This would improve on looping over numerous amounts of
 *      tombstones for large data. 
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class sorting {

    /*
     * readFile(filename) -- Takes in a String 'filename' and reads a file
     *      and creates Attack objects from each line of the input file. Those
     *      objects are then put into an ArrayList and then is returned at the
     *      end. Null is returns if the file was not found.
     */
    public static ArrayList<Attacks> readFile(String filename) {

        // Array to store all the Attack objects, unsorted
        ArrayList<Attacks> unSortAttackArray = new ArrayList<>();

        try {
            File fileObj = new File(filename); // File object to read file
            Scanner fileScanner = new Scanner(fileObj); // scan the file

            while (fileScanner.hasNextLine()) {

                String line = fileScanner.nextLine(); // txt line from file

                // Array of Attack Stats from line: [Type, Damage, Speed]
                String[] lineArray = line.strip().split(", ");

                // Attack Object, holds attack data from line
                Attacks attackObj = new Attacks(lineArray[0], lineArray[1],
                                                                 lineArray[2]);
                unSortAttackArray.add(attackObj); // put it in the array
            }
            fileScanner.close();
        }

        catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found.");
            return null;
        }
        return unSortAttackArray; 
    }

    /*
     * createHashMap(attacksList) -- Takes in a ArrayList of Attacks objects
     *      'attacksList' and creates a HashMap. The key's are the speeds of
     *      the attacks. Since the speeds are unique we can use them as keys.
     *      The values are the Attacks objects themselves. Returns the HashMap.
     */
    public static HashMap<Double, Attacks> createHashMap(ArrayList<Attacks> attacksList) {

        // Keys are speed (unique) -- values are the objects
        HashMap<Double, Attacks> attacksHMap = new HashMap<>();

        for (int i = 0; i < attacksList.size(); i++) {

            // Get the Attack object
            Attacks attackObj = attacksList.get(i);

            // Get the speed (used as key in Hash Map)
            double attackSpeed = attackObj.getSpeedAmount();

            // Put in Hash Map -- (key = speed, value = Attack object)
            attacksHMap.put(attackSpeed, attackObj);
        }
        return attacksHMap;
    }

    /*
     * createA1(attacksList) -- Takes in a ArrayList of Attacks objects
     *      'attacksList'. Method sorts the speeds of the attacks from least to
     *      greatest. Sorts using the QuickSort algorithm. Method returns the
     *      sorted list.
     */
    public static double[] createA1(ArrayList<Attacks> attacksList) {
        // Put all of the speeds in this list to be sorted - least to greatest
        double[] unSortedDoubleArray = new double[attacksList.size()];

        for (int i = 0; i < attacksList.size(); i++) {

            // Get the Attack object
            Attacks attackObj = attacksList.get(i);

            // Get the speed (used as key in Hash Map)
            double attackSpeed = attackObj.getSpeedAmount();

            // Add double to the array
            unSortedDoubleArray[i] = attackSpeed;
        }

        // Sort the unSortedDoubleArray
        SortSpeed quickSortSpeed = new SortSpeed();
        double[] A1 = quickSortSpeed.sortBySpeed(unSortedDoubleArray); 

        return A1; // return the sorted list
    }

    /*
     * createA2(A1, attacksList, attacksHMap) -- Takes in an array of doubles
     *      'A1', an ArrayList of Attacks objects 'attacksList' and a HashMap,
     *      keys are Doubles and values are attack objects, 'attacksHMap'.
     *      Method creates and sorts an integer array of the damages of the
     *      attacks. Uses merge sort to sort the damages from greatest to
     *      least. Method uses stable sort to keep the orderings of the speeds
     *      damages intact. Method returns a int[], sorted damages.
     */
    public static int[] createA2(double[] A1, ArrayList<Attacks> attacksList,
                                    HashMap<Double, Attacks> attacksHMap) {

        // Unsorted list of integers damage
        int[] unSortedDamageArray = new int[attacksList.size()];

        // Loop through A1, get the damage and then add it to the array
        for (int j = 0; j < A1.length; j++) {
            // Get the damage and add it to the unsorted damage array
            int damage = attacksHMap.get(A1[j]).getDamageAmount();
            unSortedDamageArray[j] = damage; // add the damage to the array
        }

        // Sort the unSortedDamageArray
        SortDamage mergeSortDamage = new SortDamage();
        int[] A2 = mergeSortDamage.sortDamage(unSortedDamageArray);

        return A2; // return the sorted damage array
    }

    /*
     * createA3(A1, attacksList, attacksHMap) -- Takes in an array of doubles
     *      'A1', an ArrayList of Attacks objects 'attacksList' and a HashMap,
     *      keys are Doubles and values are attack objects, 'attacksHMap'.
     *      Method creates and sorts a String array of Attacks types. To sort
     *      the method uses counting sort to sort the three types. The types
     *      are sorted by: "lightning", "ice", "fire". All are sorted and put
     *      into lowercase. Method returns a String[], sorted types.
     */
    public static String[] createA3(double[] A1, ArrayList<Attacks> attacksList,
                                    HashMap<Double, Attacks> attacksHMap) {
        // UnSorted list of types
        String[] unsortedTypesArray = new String[attacksList.size()];

        // Loop through A1, get the types and then add it to the array
        for (int l = 0; l < A1.length; l++) {
            // Get the damage and add it to the unsorted damage array
            String type = attacksHMap.get(A1[l]).getType();
            unsortedTypesArray[l] = type; // add the damage to the array
        }

        // Sort the unsortedTypesArray
        SortTypes countingSortTypes = new SortTypes();
        String[] A3 = countingSortTypes.sortTypes(unsortedTypesArray);

        return A3; // return the sorted types array
    }

    /*
     * main(args) -- Takes in a command line argument like:
     *      'java sorting input.txt' and reads and input file. Calls methods
     *      that create A1, A2, A3 and the Hash Map and calls methods that does
     *      the decision process as well. Method does not return anything. 
     */
    public static void main(String[] args) {

        // Read the file and create a list of Attacks object
        ArrayList<Attacks> testInputAttacks = readFile(args[0]);

        double[] A1 = createA1(testInputAttacks); // create A1 array

        // Create the HashMap: Key Double (Speed), Value: Attack Object
        HashMap<Double, Attacks> AttackHMap = createHashMap(testInputAttacks);

        // create the A2 array
        int[] A2 = createA2(A1, testInputAttacks, AttackHMap);

        // create the A3 array
        String[] A3 = createA3(A1, testInputAttacks, AttackHMap);

        // Perform the decision process
        DecisionProcess round = new DecisionProcess(AttackHMap, A1, A2, A3);
        round.performRound(); // Logic decisions of attacks in the round
    }
}