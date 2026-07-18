 /*
Author: Daniel Shapiro
Course: CSC 345, Spring 2026
Program name: Node.java
External Packages: Programming_Project1
Purpose: This class is the node class for individual node of the 
    orthogonal list class. It defines specific fields and getters and setters
    for the node. 
Class variables: The fields of the class are: 'row', 'col' which are the coordiante
    values of the node which are integers. 'isSearched' is a boolean, true if 
    node has been searched, false otherwise. The references to the other
    nodes in the orthogonal list are 'north', 'south', 'east', 'west'. The names 
    describe their direction of reference in the orthogonal list.
Class Constructor: The constructor defines the fields stated in the
    class variables section except for isSearched which is defined in the 
    field section on its own. 
Class methods list:
    check_isSearched()
    search_node()
    get_north()
    get_south())
    get_east()
    get_west()
    get_coordinate()
    set_north()
    set_south()
    set_east()
    set_west()
 */
package Programming_Project1;

public class Node {

    private int row;    // Row value for the coordinate of the node
    private int col;    // Column value for the coordinate of the node
    private boolean isSearched = false; // Boolean, check if seached
    private Node north; // Reference to the node north this current node
    private Node south; // Reference to the node south this current node
    private Node east;  // Reference to the node east this current node
    private Node west;  // Reference to the node west this current node

    /*
    Node(inputRow, inputCol) -- Takes in two integer values 'inputRow' and
        'inputCol' and sets the fields of the class. The fields for 'row' and
        'column' are set based on input of the constructor and the directional
        references are set to null. Constructor does not return.
    */
    public Node(int inputRow, int inputCol) {
        // Defining the fields as described in fields section
        this.row = inputRow;
        this.col = inputCol;
        this.north = null;
        this.south = null;
        this.east = null;
        this.west = null;
    }

    /*
    search_node() -- Takes in no parameters. This is a setter for the 
        isSeached field to be set to true if the node is looked at. Method
        does not return.
    */
    public void search_node() {
        this.isSearched = true;
    }

    /*
    The methods below are all getters of the fields of the class. There are
    no inputs for any other getters but all return their respective fields
    values from the Node class.
    */
    public boolean check_isSearched() {
        return isSearched;
    }

    public Node get_north() {
        return this.north;
    }

    public Node get_south() {
        return this.south;
    }

    public Node get_east() {
        return this.east;
    }

    public Node get_west() {
        return this.west;
    }

    /*
    get_coordinate() -- Takes no parameters. Method creates a 1D array of
        length 2 to store and return the coordinates in (row, col) form. This
        method returns the 1D array.
    */
    public int[] get_coordinate() {
        int[] coord = new int[2];   // 1D len 2 array to store coordinates
        coord[0] = this.row;
        coord[1] = this.col;
        return coord;
    }

    /*
    The methods below are all setters for the specific direction references
    to nodes near this node in the (N, E, S, W) directions. Each method takes
    in a node reference and sets it their respective field values. None of 
    these methods return anything.
    */
    public void set_north(Node northernNode) {
        this.north = northernNode;
    }

    public void set_east(Node easternNode) {
        this.east = easternNode;
    }

    public void set_south(Node southernNode) {
        this.south = southernNode;
    }

    public void set_west(Node westernNode) {
        this.west = westernNode;
    }
}