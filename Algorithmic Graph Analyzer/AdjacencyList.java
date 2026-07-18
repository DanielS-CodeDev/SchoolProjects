/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: AdjacencyList.java
 * External Packages: java.util.LinkedList
 * Purpose: This class represents the adjacency list of a graph. This class has
 *      many setters and a getter to help create the adjacency list. 
 * Class variables: Fields of the class are: 'headNodeLL' - Is a Linked List
 *      of headNodes. 
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      getHeadLL()
 *      addHead(name)
 *      getHeadNode(name)
 *      addNode(headNodeName, vertex)
 *      toString()
 */

import java.util.LinkedList;

public class AdjacencyList {

    public LinkedList<headNode> headNodeLL;     // Head Node LL

    /*
     * adjacencyList() -- Constructor of the class. No parameters. Defines
     *      the field of the class. 
     */
    public AdjacencyList() {
        this.headNodeLL = new LinkedList<headNode>();   // Set up the head LL
    }
    /*
     * getHeadLL() -- No parameters. Returns the headNode Linked List.
     */
    public LinkedList<headNode> getHeadLL() {
        return this.headNodeLL;
    }

    /*
     * addHead(name) -- Takes in a String 'name', addes a new headNode to the
     *      head node LL and then returns that new head node that was added.
     */
    private headNode addHead(String name) {
        headNode newHead = new headNode(name);  // Create a new headNode
        this.headNodeLL.add(newHead);   // Add the head node
        return newHead;
    }

    /*
     * getHeadNode(name) -- Takes in a String 'name' and loops over the head
     *      node LL and returns the node if a head node of the same name is in
     *      the LL else it returns null.
     */
    public headNode getHeadNode(String name) {

        for (headNode hNode: this.headNodeLL) {
            if (hNode.getHeadName().equals(name)) {
                return hNode;
            }
        }
        return null;
    }

    /*
     * addNode(headNodeName, vertex) -- Takes in a String 'headNodeName' and 
     *      it checks if there is a head of the same name. If not it creates
     *      the head and adds it to the LL. If it is in the list nothing
     *      happens at first. Next the vertex is added to the respective heads
     *      LL. Nothing is returned.
     */
    public void addNode(String headNodeName, Node vertex) {

        // Get the head Node with the inputed name
        headNode getHnode = getHeadNode(headNodeName);

        // If the head Node is not in the list add it
        if (getHnode == null) {
           getHnode = this.addHead(headNodeName);  // gets the head node
        }

        // Add the node to the head's LL
        getHnode.addToHeadLL(vertex);

    }

    /*
     * toString() -- No parameters. Returns a string representation of the 
     *      the adjacency list. 
     */
    @Override
    public String toString() {
        String strFormat = "";
        for (headNode hNode: this.headNodeLL) {
            strFormat += hNode.toString();
        }
        return strFormat;
    }
}