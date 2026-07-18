/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: headNode.java
 * External Packages: java.util.LinkedList
 * Purpose: This class represents one Head Node object for the adjacency list.
 *      This head node references a Linked List which are the Rooms that the
 *      header is adjacent with. 
 * Class variables: The fields of the class are: 'name' - name of the head
 *      node, 'headsLinkedList' - the linked list of nodes the head Node points
 *      too. 
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      getHeadName()
 *      getHnodeLListLen()
 *      addToHeadLL()
 *      toString()
 */ 

import java.util.LinkedList;

public class headNode {

    private String name;    // name of the headNode
    private LinkedList<Node> headsLinkedList;    // LL the head node points too

    /*
     * headNode(name) -- Constructor of the class. Returns nothing. Takes in
     *      a parameter String 'name' which is the name of the headNode.
     *      constructor defines the fields of the class.
     */
    public headNode(String name) {

        this.name = name;   // define the name field
        this.headsLinkedList = new LinkedList<Node>(); // create the LL
    }

    /*
     * Methods below are simple getters and setters. Two to get the fields of
     *      the class and one to add a node to the LinkedList.
     */

    public String getHeadName() {
        return this.name;
    }

    public LinkedList<Node> getHeadLL() {
        return this.headsLinkedList;
    }

    public void addToHeadLL(Node newNode) {
        this.headsLinkedList.add(newNode);
    }

    /*
     * toString() -- No parameters. Returns a string representation of the head
     *      node class. It states the name of the head node and the names of
     *      the nodes that the LL holds. Has a special boolean toggle. If false
     *      the headNode prints its LL with just the names of the nodes, else
     *      it prints the string representation of the nodes.
     */
    @Override
    public String toString() {

        boolean printNodeVals = true; // bollean to turn on or off for node val

        String strFormat = this.name + ": LL: "; // string to return

        // Loop over the nodes
        for (Node node: this.headsLinkedList) {

            // Print the nodes without values
            if (!printNodeVals) {
                strFormat += node.getNodeName() + ", ";
            }

            // Print the nodes with the values
            else {
                strFormat += node.toString() + ",";
            }
        }
        return strFormat + "\n";
    }
}