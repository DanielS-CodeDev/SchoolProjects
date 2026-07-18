/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: Tuple.java
 * External Packages: N/A
 * Purpose: This class represents a tuple that holds data about an edge. It
 *      is: (name of first vertex, name of second vertex, edge weight). 
 * Class variables: The fields of the class are: f'firstVName' which is a
 *      String that holds the first vertex name. 'secondVName' a String holds
 *      the name of the second vertex. 'edge' an integer holds the edge weight
 *      value.
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      getFirstVName()
 *      getSecondVName()
 *      getEdgeWeight()
 *      updateEdgeWeight(newValue)
 *      updateSecondVName(name)
 *      toString()
 */ 

public class Tuple {

    private String firstVName;  // The first vertex name
    private String secondVName; // The second vertex name
    private int edgeWeight; // edge weight

    /*
     * tuple(firstVName, secondVName, edgeWeight) -- Constructor. Returns
     *      nothing. Takes in two Strings 'firstVName' & 'secondVName' which
     *      are the first and second vertices names. 'edge' is an integer that
     *      is the edge weight between the two vertices. The constructor
     *      defines the fields of the class.
     */
    public Tuple(String firstVName, String secondVName, int edgeWeight) {

        // Set the fields of the class
        this.firstVName = firstVName;
        this.secondVName = secondVName;
        this.edgeWeight = edgeWeight;

    }

    /*
     * These methods below are simple getters of the fields for the Tuple.
     */

    public String getFirstVName() {
        return this.firstVName;
    }

    public String getSecondVName() {
        return this.secondVName;
    }

    public int getEdgeWeight() {
        return this.edgeWeight;
    }

    public void updateEdgeWeight(int newValue) {
        this.edgeWeight = newValue;
    }

    public void updateSecondVName(String name) {
        this.secondVName = name;
    }

    /*
     * toString() -- No parameters. Returns a string representation of the
     *      which includes all the fields of the class seprated by commas.
     */
    @Override
    public String toString() {
        return "(" + this.firstVName + "," + this.secondVName + "," + 
                                                        this.edgeWeight + ")";
    }
}
