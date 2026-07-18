/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: dijkstraAlgo.java
 * External Packages: java.util.ArrayList, java.util.LinkedList
 * Purpose: This class uses Dijkstra's Algorithmn to search for the shortest
 *      path from SR to BL the includes edge weights and the vertex weights
 *      as well. The total count for the weights goes as followed. If the tree
 *      looks like this: A --> B the total weight for that pair is the edge
 *      weight and the B vertex cost. The graph is undirectional but this
 *      example explains the idea of moving to find BL and how the cost works.
 * Class variables: Fields of the class are: 'known' -- ArrayList of tuple
 *      objects that are the set/ found shortest distances from the SR to the
 *      current vertex. 'fringe' -- ArrayList of tuples. It is the tuples that
 *      we know exist and can get to but are still in the process of finding 
 *      the shortest path. 'adjList' -- AdjacencyList object, which is a
 *      adjacency list representation of the tree.
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      algo()
 *      getTuple(vertexName)
 *      getLowestCost()
 *      getCloseToA(A, B)
 *      isInArray(aList, str)
 *      printTest()
 *      printPathToBL()
 */

import java.util.ArrayList;
import java.util.LinkedList;

public class dijkstraAlgo {

    private ArrayList<Tuple> known; // Vertices of shortest distance to start

    // In process Vertices to find its shortest distance
    private ArrayList<Tuple> fringe;
    private AdjacencyList adjList; // AL of the tree
    
    /*
     * dijkstraAlo(adjList) -- Constructor. Returns Nothing. Takes in an
     *      AdjacencyList 'adjList' and defines the fields of the class.
     */
    public dijkstraAlgo(AdjacencyList adjList) {
        this.known = new ArrayList<>(); // Define the known field
        this.fringe = new ArrayList<>(); // Define the fringe field
        this.adjList = adjList; // Define AL with the parameter of the class
    }

    /*
     * algo() -- Has no parameters. Performs Dijkstras Algorithmn on the AL
     *      of the tree. This method uses the Tuple class for Vertex Name and
     *      the subscript notation. The Vertex Name is the firstVName for the
     *      tuple and the subscript (the previous) vertex is the secondVname.
     *      The numerical value from getting to that vertex from the SR is the
     *      edgeWeight field of the Tuple class. This value holds the edge
     *      weight and the vertex weight summed together. At the end of this
     *      method an Array List of Tuple is returned which represents the
     *      Known of Dijkstras Algorithmn.
     */
    public ArrayList<Tuple> algo() {

        // Tuple -- (Node, Prev, cost)

        // Create the Starting Tuple.
        Tuple srTuple = new Tuple("SR", "-", 0);
        known.add(srTuple); // Add it to the Known.

        // number of vertices in the graph
        int numVertices = this.adjList.getHeadLL().size();

        // Loop until the known size is the same as number of vertices
        while (known.size() < numVertices) {

            // Get the latest vertex from the known
            Tuple currVertex = known.get(known.size()-1);

            // This is the main vertex not subscript
            String currVName = currVertex.getFirstVName();

            // get the current vertex head of adj list
            headNode currVHead = this.adjList.getHeadNode(currVName);

            // Get the weight (edge and vertex) from the latest known vertex
            int currVcost = currVertex.getEdgeWeight();

            // Get the LL of the latest Known Vertex of the AL
            LinkedList<Node> currVHeadLL = currVHead.getHeadLL();

            // Loop over that LL
            for (Node currNode: currVHeadLL) {

                // Get a Vertex from that LL
                String currNodeName = currNode.getNodeName();

                // Checks if the currNodeName is in the known
                if ((!isInArray(this.known, currNodeName))) {

                    // gets the hallway cost of the vertex
                    int currNodeEdgeCost = currNode.getHallwayCost();

                    // gets the room cost of the vertex
                    int currNodeVertexCost = currNode.getRoomCost();

                    // Sums them both up together
                    int totalCost = currNodeEdgeCost + currNodeVertexCost + currVcost;

                    // Ex: A --> B sums the edge and the B rooms cost. 

                    // if node in fringe can it be updated?

                    // get the tuple if this vertex is in the fringe
                    Tuple inFringeTuple = getTuple(currNodeName);
                    if (inFringeTuple != null) {

                        // Get the current weight of the tuple
                        int inFringeEdgeCost = inFringeTuple.getEdgeWeight();

                        // if totalCost < its current update the fringe
                        if (inFringeEdgeCost > totalCost) {

                            // update the fringe value
                            inFringeTuple.updateEdgeWeight(totalCost);

                            // Update the previous vertex name
                            inFringeTuple.updateSecondVName(currVName);
                        }
                    }

                    // If not in the fringe yet

                    else {

                        // Create the tuple
                        Tuple currNodeTuple = new Tuple(currNodeName, currVName, totalCost);
                        fringe.add(currNodeTuple); // Add the tuple to fringe

                    }
                }
            }

            // Get the lowest Cost edge pair to move to known list
            Tuple moveToKnown = getLowestCost(); // lowest cost tuple
            fringe.remove(moveToKnown); // Remove it from the fringe
            known.add(moveToKnown); // add it to the known
        }
        return known; // return known list
    }

    /*
     * getTuple(vertexName) -- Get a String 'vertexName' and returns the tuple
     *      whose first name in the tuple is 'vertexName'. Returns the tuple
     *      if it is found, else null.
     */
    private Tuple getTuple(String vertexName) {

        vertexName = vertexName.toLowerCase(); // lowercase VertexName

        // Loop over the fringe
        for (int i = 0; i < this.fringe.size(); i++) {

            Tuple currTuple = this.fringe.get(i); // get a tuple from fringe

            // get the tuples first name listed and lowercase it
            String currV = currTuple.getFirstVName().toLowerCase();

            // Return true if the names match
            if (currV.equals(vertexName.toLowerCase())) {
                return currTuple;
            }
        }
        return null;
    }

    /*
     * getLowestCost() -- No parameters. Method searches the fringe Array of
     *      tuples and returns the Tuple that has the lowest cost in the
     *      fringe.
     */
    private Tuple getLowestCost() {

        int lowestCost = -1;   // Lowest cost default. No negative edge weights
        Tuple lowestTuple = null;   // default tuple value

        // Loop over the fringe
        for (int i = 0; i < fringe.size(); i++) {

            Tuple currTuple = fringe.get(i); // get a tuple from the fringe
            int currTupleVal = currTuple.getEdgeWeight(); // get the weight

            // If lowestCost is -1 set the first tuple to me lowest
            if ((lowestCost == -1)) {
                lowestCost = currTupleVal; // update lowestCost
                lowestTuple = currTuple; // update lowestTuple
            }

            // If true update to new lowest 
            else if (currTupleVal <= lowestCost) {

                // If the cost equals make the lowestTuple the one whose first
                // Name is closer to A in the alphabet
                if (currTupleVal == lowestCost) {
                    // Tuple closest to A in alpha
                    lowestTuple = getCloseToA(lowestTuple, currTuple);
                }

                else {
                    lowestTuple = currTuple; // Update lowestTuple with current
                }
                lowestCost = currTupleVal; // Update the lowest cost
            }
        }
        return lowestTuple; // return the lowest cost tuple
    }

    /*
     * getCloseToA(A, B) -- Takes in two Tuple objects 'A' & 'B' and returns
     *      the tuple whoses first char from the first name listed in the tuple
     *      is closer to A in the alphabet. Method compares ASCII values.
     */
    private Tuple getCloseToA(Tuple A, Tuple B) {

        // Get the first characters from the first names in the tuple
        char charA = A.getFirstVName().charAt(0);
        char charB = B.getFirstVName().charAt(0);

        // Compare the values
        if (charA > charB) {
            return B; // return tuple whose name is closer to a
        }
        return A;
    }

    /*
     * isInArray(aList, str) -- Takes in an ArrayList of Tuples 'aList' and a
     *      String 'str' and returns a boolean if the str is a first name
     *      listed in a tuple from the given list. Returns true if there is a
     *      vertex with that first name else false.
     */
    private boolean isInArray(ArrayList<Tuple> aList, String str) {

        // Loops over the array
        for (int i = 0; i < aList.size(); i++) {

            Tuple knownTuple = aList.get(i); // get a tuple from indexing
            String knownTupleName = knownTuple.getFirstVName(); // get name

            // Determine if 'str' equals the tuples first name
            if (knownTupleName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /*
     * printTest() -- No parameters. Returns Nothing. This method is used for
     *      debugging and to see what is in the Known list. This method loops
     *      over the known list and prints out the tuples.
     */
    public void printTest() {

        System.out.println("Known:     (Node, Prev, cost)");
        for (int i = 0; i < this.known.size(); i++) {
            System.out.println(this.known.get(i));
        }
    }

    /*
     * printPathToBL() -- No parameters. Returns nothing. Method loops over the
     *      known list and finds the shortest path from SR to BL. Method puts
     *      these string pairs into a String ArrayList and then prints them out
     *      in reverse so it is in proper format. Method searches backwards
     *      starting at BL then moves backwards to find SR. Method does save
     *      the total cost which is listed in the BL tuple and prints it last
     *      after all of the pairs of edges. 
     */
    public void printPathToBL() {

        int totalCost = 0;  // default value of total cost from SR to BL
        boolean stopSearching = false; // stop searching boolean
        String searchFor = "BL"; // first thing to search for is BL

        // String array of edges from SR to BL
        ArrayList<String> strArray = new ArrayList<>();

        // Loop to find the bath from SR to BL
        while (!stopSearching) {

            // Loop over the known list
            for (int i = 0; i < this.known.size(); i++) {

                // Get a tuple from this list
                Tuple currTuple = this.known.get(i);

                // Get the first name listed in the tuple
                String currTupleFirstName = currTuple.getFirstVName();

                // If the first name in tuple equals what we are 'searchFor'
                if (currTupleFirstName.equals(searchFor)) {

                    // Get the tuple second name
                    String currTupleSecName = currTuple.getSecondVName();

                    // Create the String for path but in reverse
                    String edgeStr = currTupleSecName + ", " + currTupleFirstName;

                    // Add to strArry
                    strArray.add(edgeStr);

                    // If 'searchFor' == BL get the total cost of the path
                    if (searchFor.equals("BL")) {
                        totalCost = currTuple.getEdgeWeight();
                    }

                    // If the second name is SR we have finished searching
                    if (currTupleSecName.equals("SR")) {
                        stopSearching = true; // Get the total cost
                    }

                    // Set 'searchFor' to the second name in tuple
                    searchFor = currTupleSecName; // search for the prev
                    break; // break out of loop
                }
            }
        }

        // Loop through strArry backwards to print correctly
        for (int j = strArray.size() - 1; j > -1; j--) {
            System.out.println(strArray.get(j) + "\n");
        }
        System.out.print(totalCost); // Print the total cost of the path to BL
    }
}