/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: tupleComparator.java
 * External Packages: java.util.Comparator
 * Purpose: This class implments the Comparator interface and defines the
 *      compare method. Takes in two tuples and compares there edge weights.
 *      If the edge weights are the same value then it compares the
 *      first letters of the names of the first vertex in each tuple.
 *      If those letters are the same then it compares the second vertices
 *      in both tuples. If the edge weights are different it compares them
 *      based off of that. This method returns an integer. If -1 is
 *      returned means that tupleA comes before tupleB. If 1 is returned
 *      means that tupleB comes before tupleA. This is used for sorting
 *      the tuples. This is for sorting the kruskals edge list.
 * Class variables: N/A
 * Class Constructor: None. Just the default. Only need to implement the
 *      compare method.
 * Class methods list:
 *      compare(A, B)
 */ 

import java.util.Comparator;

public class tupleComparator implements Comparator<Tuple> {

    /*
     * compare(A, B) -- Takes in two tuples and compares there edge weights.
     *      If the edge weights are the same value then it compares the
     *      first letters of the names of the first vertex in each tuple.
     *      If those letters are the same then it compares the second vertices
     *      in both tuples. If the edge weights are different it compares them
     *      based off of that. This method returns an integer. If -1 is
     *      returned means that tupleA comes before tupleB. If 1 is returned
     *      means that tupleB comes before tupleA. This is used for sorting
     *      the tuples.
     */
    public int compare(Tuple A, Tuple B) {

        int tupleAval = A.getEdgeWeight(); // tuple A edge weight
        int tupleBval = B.getEdgeWeight(); // tuple B edge weight

        // If they equal compare the numerical values
        if (tupleAval == tupleBval) {

            // Get the first vertex name from each tuple
            String tupleAString1 = A.getFirstVName();
            String tupleBString1 = B.getFirstVName();

            // int: if negative A comes before B, positive B comes before A
            int compareFirstVertex = tupleAString1.compareTo(tupleBString1);

            // If the result is not 0 return else compare next vertex names
            if (compareFirstVertex != 0) {
                return compareFirstVertex;
            }

            // Get the second vertex name from each tuple
            String tupleAString2 = A.getSecondVName();
            String tupleBString2 = B.getSecondVName();

            // int: if negative A comes before B, positive B comes before A
            int compareSecondVertex = tupleAString2.compareTo(tupleBString2);

            return compareSecondVertex;
        }

        // Return -1, so tupleA comes before tupleB in sorting
        else if (tupleAval < tupleBval) {
            return -1;
        }

        // Return 1, so tupleB comes before tupleA in sorting
        else {
            return 1;
        }
    }
}