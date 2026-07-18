/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: labyrinth.java
 * External Packages: java.util.Scanner, java.io.File, java.util.HashMap
 *      java.io.FileNotFoundException
 * Purpose: This class is the main file of the project. This file reads in
 *      a input file from the command line. The command is:
 *      java labyrinth input.txt . This program uses other classes in this
 *      project to solve Logic's problems. This first thing this program
 *      does is create an Adjacency List from the input file and
 *      finds and prints the MCST of the tree and then performs dijkstra's
 *      algorithm on the tree to find the shortest path in weight that includes
 *      edge and vertex cost from SR to BL. 
 * Class variables: No fields to this class.
 * Class Constructor: No constructor of the class. Class is the main file of
 *      the project.
 * Class methods list:
 *      create_AL(filename)
 *      main(args)
 */

/*
 * Why did I choose to pick an adjacency list over a adjacency matrix?
 *      The reason I choose to pick a AL over a AM is because it is easier to
 *      hold and represent the data that I needed for this graph. Each vertex
 *      is a Node which holds its name, vertex weight and the edge weight of
 *      the edge that is incident to it. The adjacency list allows to have
 *      header Nodes as the start of the edge and the Node itself is within
 *      that head nodes linked list. Although the adjacency list might not be
 *      super efficent with dense graph I feel that the cons out weighed the
 *      pros of simplicity of storing the reqired data of the graph. In
 *      programming simplicity is what we strive for and with the required data
 *      that the tree holds it was better to use the AL.
 * 
 * Would I make the same choice of either a AL or AM?
 *      I would. I found it to be the most simplest way to represent the graph
 *      with the required data that I had. It would be interesting though to
 *      try and see how I could use a AM and still effectively store the
 *      required data of the tree so that dense graphs (ones with more edges)
 *      would be more efficient. I would mainly stick with the AL since in
 *      my opinion was the best way to represent this given graph with the
 *      required parameters of the graph.
 * 
 * Why did I choose Kruskal's Algorithm over Prims?
 *      I found that based on how we needed to determine the ties between
 *      edges that the edge who's first name listed must be closer to A in the
 *      alphabet tended to be more straight forward with Kruskal's. A lot of
 *      the requirements for the MCST was completed first by just sorting first
 *      by edge weights then by alphabet so then all I needed to do was go down
 *      the list and pick the smallest edge weights and check if adding them
 *      to the MCST with BFS would not create a cycle.
 * 
 * What else would I update/ change in the future?
 *      I would update the tuple class. The tuple class itself is straight
 *      forward and does it's purpose but I found when I got to the third part
 *      of this project the naming conventions that I used for fields and
 *      methods where not very universal for what this project required. This
 *      would be a simple fix. I would also break up some very long methods
 *      into some simple chunks for more readability. I would also find a
 *      better way at storing the known for dijstra's algorithm. It feels a bit
 *      clunky but over it works. As all code projects go they can always be
 *      improved.
 */

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class labyrinth {

    /*
     * create_AL(filename) -- Takes in a String 'filename' loops over the input
     *      file and creates an adjacency list. The first vertex listed is
     *      called the source vertex. The second vertex is called the node.
     *      This method creates the vertices and places them into an AL. First,
     *      it places the second vertex 'node' in the LL for the source vertex
     *      LL. Next it does the opposite by placing the source node in the
     *      nodes LL. This ensures that the full AL is created. With that
     *      process the vertices room weights are stored in a HashMap for when
     *      they are needed again later. The edge weights do not need to be
     *      saved since they will change depending on what to vertices we are
     *      dealing with. At the end of the method it returns the adjacency
     *      list. 
     */
    public static AdjacencyList create_AL(String filename) {

        // Create the adjacencyList
        AdjacencyList adjList = new AdjacencyList();

        // Create a hashmap to store the vertices room weights that are
        // Already found
        HashMap<String, Integer> nodeRoomValHmap = new HashMap<>();
        nodeRoomValHmap.put("SR", 0); // Put SR room weight 0 in
        nodeRoomValHmap.put("BL", 0); // Put BL room weight 0 in

        try {
            File inputFile = new File(filename);  // input file to read
            Scanner fileScanner = new Scanner(inputFile); // reading file

            // Read the file
            while (fileScanner.hasNextLine()) {

                // An array of the txt line split by the comma
                String[] inputArr = fileScanner.nextLine().strip().split(",");

                // The first vertex listed on the input file
                String sourceNodeName = inputArr[0].strip();

                // Second node listed on the input file
                String nodeName = inputArr[1].strip();

                // The room cost of the second node listed
                Integer nodeRoomCost = Integer.valueOf(
                                    inputArr[2].strip().split(" ")[2]);

                // The edge cost from the first node to the seconded listed
                Integer nodeHallWCost = Integer.valueOf(
                                    inputArr[3].strip().split(" ")[2]);

                // Node -- The second one listed in the input.
                Node newNode = new Node(nodeName, nodeRoomCost, nodeHallWCost);

                // Add the source Node and node to the AL
                adjList.addNode(sourceNodeName, newNode);


                // Add the Nodes room weight to a HashMap

                // If Node room cost not in hashmap add it
                if (!nodeRoomValHmap.containsKey(nodeName)) {
                    nodeRoomValHmap.put(nodeName, nodeRoomCost);
                }

                // Get the source node the node that is first in the line
                // Room cost. This is the first node listed in the input line
                Integer sourceNodeRCost = nodeRoomValHmap.get(sourceNodeName);

                // Create a node for the source node, same weight as previous
                // versions of source node but different distance cost. It is
                // the cost of distance between the two nodes on the input line
                Node sourceNode = new Node(sourceNodeName, sourceNodeRCost, 
                                                        nodeHallWCost);

                // Add the node to the head LL and put the source in that LL
                adjList.addNode(nodeName, sourceNode);
            }
            fileScanner.close(); // close the input file
        }

        catch (FileNotFoundException e) {
            System.out.println("ERROR: The file does not exist.");
        }
        return adjList;     // Return the Adjacency List
    }

    /*
     * main(args) -- The main method of the class & project. This method takes
     *      in a command line command like: "java labyrinth input.txt" and 
     *      it calls the methods that execute the functions of this project.
     *      Nothing is returned.
     */
    public static void main(String[] args) {
        AdjacencyList adjList = create_AL(args[0]); // Adjacency List

        kruskals kruskalsObj = new kruskals(adjList);   // kruskals object
        kruskalsObj.printMCSTEdgeList();    // print the MCST edges

        dijkstraAlgo dijkstrasObj = new dijkstraAlgo(adjList); // dijkstras obj
        dijkstrasObj.algo();       // Run dijkstras algorithm
        dijkstrasObj.printPathToBL();   // Print out the path from SR to BL
    }
} 