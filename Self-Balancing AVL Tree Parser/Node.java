/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: Node.java
 * External Packages: N/A
 * Purpose: This class represents one Node for the AVL tree. It defines many
 *      fields that the Node will hold and it defines many getters, setters
 *      and a toString() as well.
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      getNodeName()
 *      getAmountOfIncrease()
 *      getParentNode()
 *      getLeftSubtreeHeight()
 *      getRightSubtreeHeight()
 *      getLeftChild()
 *      getRightChild()
 *      updateLeftSubtreeHeight()
 *      updateRightSubtreeHeight()
 *      updateLeftChildNode()
 *      updateRightChildNode()
 *      updateParentNode()
 *      toString()
 */

public class Node {

    private String nodeName; // Name of agency
    private double amountOfIncrease; // total amount (not percent) increase
    private Node parentNode; // reference to parent node

    private int leftSubtreeHeight; // left subtree height
    private int rightSubtreeHeight; // right subtree height
    private Node leftNode; // left child reference
    private Node rightNode; // right child reference

    /*
     * Node(nodeName, amountOfIncrease, parentNode) -- Constructor of the
     *      class. Returns nothing. Sets 3 fields of the class by taking in a
     *      String 'nodeName', integer 'amountOfIncrease' and a Node
     *      'parentNode'. The three parameters set their respective fields.
     *      Also sets the left and right subtree heights and references to
     *      the children to their default values.
     */
    public Node(String nodeName, double amountOfIncrease, Node parentNode) {
        this.nodeName = nodeName; // set the nodeName field
        this.amountOfIncrease = amountOfIncrease; // set amount increase field
        this.parentNode = parentNode; // set reference not parent of this node

        this.leftSubtreeHeight = 0; // set default left subtree height
        this.rightSubtreeHeight = 0; // set default right subtree height
        this.leftNode = null; // set default left child reference
        this.rightNode = null; // set default right child reference
    }

    /*
     * Below are simple getters for the fields of the class.
     */

    public String getNodeName() {
        return this.nodeName;
    }

    public double getAmountOfIncrease() {
        return this.amountOfIncrease;
    }

    public Node getParentNode() {
        return this.parentNode;
    }

    public int getLeftSubtreeHeight() {
        return this.leftSubtreeHeight;
    }

    public int getRightSubtreeHeight() {
        return this.rightSubtreeHeight;
    }

    public Node getLeftChild() {
        return this.leftNode;
    }

    public Node getRightChild() {
        return this.rightNode;
    }

    /*
     * Below are simple setters for 4 fields of the class: 'leftSubtreeHeight',
     * 'rightSubtreeHeight', 'leftNode' and 'rightNode'.
     */

    public void updateLeftSubtreeHeight(int newHeight) {
        this.leftSubtreeHeight = newHeight;
    }

    public void updateRightSubtreeHeight(int newHeight) {
        this.rightSubtreeHeight = newHeight;
    }

    public void updateLeftChildNode(Node newleftChild) {
        this.leftNode = newleftChild;
    }

    public void updateRightChildNode(Node newrightChild) {
        this.rightNode = newrightChild;
    }

    public void updateParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    /*
     * toString() -- Takes in no parameters. Returns a String representation of
     *      the current Node. The String has the Name of the Node, the total
     *      increase amount, Parent, Left Child and Right Child names.
     */
    @Override
    public String toString() {
        String str = "\n";
        int dashRepeatNum = 30;

        // Set up the string representation for this node
        str += "Node Name: " + getNodeName() + "\n";
        str += "Increase Amount: " + getAmountOfIncrease() + "\n";
        
        Node parentNode = getParentNode();
        Node leftChild = getLeftChild();
        Node rightChild = getRightChild();

        if (parentNode != null) {
            str += "Parent: " + parentNode.getNodeName() + "\n";
        }

        else {
            str += "Parent: NONE\n"; 
        }

        if (leftChild != null) {
            str += "Left Child: " + leftChild.getNodeName() + "\n";
        }

        else {
            str += "Left Child: NONE\n";
        }

        str += "Left Subtree Height: " + getLeftSubtreeHeight() + "\n";

        if (rightChild != null) {
            str += "Right Child: " + rightChild.getNodeName() + "\n";
        }

        else {
            str += "Right Child: NONE\n";
        }

        str += "Right Subtree Height: " + getRightSubtreeHeight() + "\n";

        return "-".repeat(dashRepeatNum) + str + "-".repeat(dashRepeatNum);
    }
}