/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: kruskals.java
 * External Packages: java.util.ArrayList, java.util.Collections, 
 *      java.util.Queue, java.util.LinkedList
 * Purpose: This class performs Kruskals Algorithm on the Adjacency List
 *      representation of the tree. The algorithm only uses the edge costs and
 *      not the vertex costs. This class prints then prints the MCST edges.
 *      If there are edges with the same cost it picks the edge whose first
 *      vertex name is closer to A in the alphabet. To make sure there are no
 *      cycles in the tree the problem uses BFS on the adjacency list of the
 *      already found edges.
 * Class variables: Fields of the class are: 'edgeListSorted' which is an
 *      ArrayList of Tuples which is defined with a help of a method which
 *      sorts the tuples of the edges from least to greatest based on the
 *      edge weights. In those edge weights the ones with the same values are
 *      then sorted in alphabetical order. 'mcstTree' is a AdjacencyList
 *      object that is the representation of the MCST.
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      createNodeList(adjList)
 *      printMCSTEdgeList()
 *      krusjalsAlgo(tupleList)
 *      breathFirstSearch(nodeName, otherNodeName)
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;

public class kruskals {

    private ArrayList<Tuple> edgeListSorted;    // The sorted list of edges
    private AdjacencyList mcstTree;     // Adacency List of the MSCT Tree

    /*
     * kruskals(adjList) -- Constructor of the class. Takes in an adjacency
     *      list object 'adjList' and defines the feilds of the class.
     *      Returns nothing.
     */
    public kruskals(AdjacencyList adjList) {
        this.edgeListSorted = createNodeList(adjList); // Create edge list
        this.mcstTree = new AdjacencyList();  // Create AL MCST Obj
    }

    /*
     * createNodeList(adjList) -- Takes in a an adjacency list object 'adjList'
     *      and creates tuples for every edge in both directions. The format
     *      of the tuple is (A, B, #) which the # is the edge weight between
     *      vertices A and B. At the end before it returns an Array of tuples
     *      the Array is sorted using the .sort() method from Collections and
     *      the special tupleComparator() method from the tupleComparator
     *      class. The tuples are sorted first by the edge weights but then
     *      the first vertex listed is then in alphabetical order with keeping
     *      the edge weight orderings.
     */
    private ArrayList<Tuple> createNodeList(AdjacencyList adjList) {

        // List of tuples: (Vertex A, Vertex B, # edge cost)
        ArrayList<Tuple> tupleList = new ArrayList<>();

        // Loop over the AL to create the tuples of every edge
        for (headNode hNode: adjList.getHeadLL()) {
           String sourceVName = hNode.getHeadName(); // name of Vertex A

            // Loop over the LL rows of the Adjacency List
            for (Node currNode: hNode.getHeadLL()) {
                String vertexName = currNode.getNodeName(); // vertex B name
                int edgeCost = currNode.getHallwayCost(); // Edge cost

                // Create the tuple
                Tuple vertexTuple = new Tuple(sourceVName, vertexName, edgeCost);
                tupleList.add(vertexTuple); // Add the tuple to the list
            }
        }

        // Create tupleComparator(), it gives logic to how to sort the tuples
        tupleComparator compare = new tupleComparator();
        Collections.sort(tupleList, compare);   // Sort the list

        return tupleList;   // return the tuple list
    }

    /*
     * printMCSTEdgeList() -- Has no paramters. Returns nothing. This method
     *      prints the edge list ordering of the MCST.
     */
    public void printMCSTEdgeList() {
       System.out.println(kruskalsAlgo(edgeListSorted) + "End MCST\n");
    }

    /*
     * krusjalsAlgo(tupleList) -- Takes in a sorted Array of tuples 'tupleList'
     *      and creates a MCST. The sorted 'tupleList' is looped over and used
     *      to build the MCST with Kruskals Algorithmn. To make sure there are
     *      no cycles the method breathFirstSearch is called. If there is a
     *      cycle false is returned and the edge is not added. Else it is added
     *      to the tree. This MCST  is build with its own adjacency list. This
     *      method returns a String of the edges used in the MCST.
     */
    private String kruskalsAlgo(ArrayList<Tuple> tupleList) {

        // The vertices that have been added to the MCST
        ArrayList<String> setVertices = new ArrayList<>();
        String msct = "";  // String output of MCST Edges

        // Room cost does not matter here. Only the edges matter
        int roomCostDefault = -1;

        for (int i = 0; i < tupleList.size(); i++){

            Tuple currTuple = tupleList.get(i);
            String firstName = currTuple.getFirstVName();   // First node name
            String secondName = currTuple.getSecondVName(); // Second node name

            int edgeCost = currTuple.getEdgeWeight(); // edge cost between them

            if (breathFirstSearch(firstName, secondName)) {

                // MCST String for printing
                msct += firstName + ", " + secondName + "\n\n";
                setVertices.add(firstName); // Might not be needed. Old Version
                setVertices.add(secondName);// Might not be needed. Old Version

                // Add the edge to the AL for the MCST

                // secNode in tuple
                Node secNode = new Node(secondName, roomCostDefault, edgeCost);

                // Add the source Node and node to the AL
                this.mcstTree.addNode(firstName, secNode);

                // create a node for the first node label in the tuple
                Node firstNode = new Node(firstName, roomCostDefault, edgeCost);

                // Add the node to the head LL and put the source in that LL
                this.mcstTree.addNode(secondName, firstNode);
            }
        }
        return msct;  // Return the string of edges for the MCST
    }

    /*
     * breathFirstSearch(nodeName, otherNodeName) -- Takes in two Strings
     *      'nodeName' & 'otherNodeName'. This method does BFS on the MCST
     *      starting at 'nodeName'. If the 'otherNodeName' is found within
     *      the search that means there is a cycle so then false is returned.
     *      If the 'otherNodeName' is not found that means adding this edge
     *      from 'nodeName' and 'otherNodeName' will not create a cycle so
     *      true is returned.
     */
    private boolean breathFirstSearch(String nodeName, String otherNodeName) {

        // Create seenList for seen Nodes and the Queue for BFS
        ArrayList<String> seenList = new ArrayList<>();
        Queue<String> q = new LinkedList<String>();

        // Get head Node of MCST AL
        headNode getNodesHead = this.mcstTree.getHeadNode(nodeName);

        // If headNode/ Node not in MCST AL means you can add it
        if (getNodesHead == null) {
            // means that you can add this edge
            return true;
        }

        // Else need to search the list -- add the head name to the queue
        q.add(getNodesHead.getHeadName());

        // loop over until q is empty
        while (!q.isEmpty()) {

            String frontQName = q.remove(); // dequeue from BFS Q
            seenList.add(frontQName);   // Add to seen list

            // Get the head of the MCST AL
            headNode frontNodeHead = this.mcstTree.getHeadNode(frontQName);

            // get the nodes Linked List
            LinkedList<Node> frontQLL = frontNodeHead.getHeadLL();

            for (Node currNode: frontQLL) {

                String currNodeName = currNode.getNodeName();

                // See other node name, return false since that is a cycle
                if (currNodeName.equals(otherNodeName)) {
                    return false;
                }

                // If the currNode not in the seen list add it to the queue
                if (!seenList.contains(currNodeName)) {
                    // Add the adjacent nodes to the queue
                    q.add(currNode.getNodeName());
                }
            }
        }
        return true;
    }
}