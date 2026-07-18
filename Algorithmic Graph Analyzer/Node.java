/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: Node.java
 * External Packages: N/A
 * Purpose: This class is a single Node in a Linked List. This represents the
 *      vertex of a graph. It holds the name, weight of the vertex and the
 *      given edge weight between this Node and another. 
 * Class variables: Fields of the class are: 'name' - name of the node, 
 *      'roomCost' - weight of the Node, 'hallwayCost' - weight of the edge
 *      that connects to this Node from a given vertex.
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      getNodeName()
 *      getRoomCost()
 *      getHallwayCost()
 *      toString()
 */

public class Node {

    private String name;       // name of the node (the room letter)
    private int roomCost;      // the room cost
    private int hallwayCost;    // the edge weight

    /*
     * Node(name, roomCost, hallwayCost) -- Constructor of the class. Takes in
     *      a String 'name' (name of the Node), integer 'roomCost' (weight of
     *      the Node itself), integer 'hallwayCost' (weight of the edge
     *      incident to the Node from a given Node). Defines the fields of the
     *      class. Returns nothing.
     */
    public Node(String name, int roomCost, int hallwayCost) {

        // Define the fields of the class
        this.name = name; // name of the node
        this.roomCost = roomCost;   // vertex cost
        this.hallwayCost = hallwayCost; // edge cost
    }

    /*
     * The methods below are simple getters for the fields of the class.
     */

    public String getNodeName() {
        return this.name;
    }

    public int getRoomCost() {
        return this.roomCost;
    }

    public int getHallwayCost() {
        return this.hallwayCost;
    }

    /*
     * toString() -- No parameters. Returns a string representation of the node
     *      class. It has the name, room cost and hallway cost all in one
     *      string.
     */
    @Override
    public String toString() {
        String strFormat = "";
        strFormat += "|Name: " + this.name + ", RC: " + this.roomCost + 
                                         ", HwC: " + this.hallwayCost + "|";
        return strFormat;
    }
}