 /*
Author: Daniel Shapiro
Course: CSC 345, Spring 2026
Program name: HeadNode.java
External Packages: Programming_Project1
Purpose: This class is the Head Node class which defines the heads of either
    the rows or columns of the orthogonal list. This class can and will be
    used for both the heads of each row and columns of the orthogonal list.
    This class defines specific fields and getters and setters for the class. 
Class variables: The fields of the class are: 'nextHead' which is a HeadNode
    that references the next HeadNode either row or column. 'firstInternalNode'
    is a Node which is the first node that the head node points too. 'headType'
    is a string that states if it is a row or column head. 'headNumber' is a 
    integer that states which row or column (0 indexed) the head is.
Class Constructor: The constructor of the class deines all the fields of the
    class. 
Class methods list:
    getNextHead()
    setNextHead()
    getInternalNode()
    setInternalNode()
    getHeadType()
    getHeadNumber()
 */

package Programming_Project1;

public class HeadNode {

    private HeadNode nextHead;  // nextHead node in the orthogonal list
    private Node firstInternalNode; // first node the head points too
    private String headType;    // "Row", "Column" type for head 
    private int headNumber;     // Which row or column head it is 

    /*
    HeadNode(headType, headNumber) -- Takes in a String 'headType' and integer
        'headNumber' which helps to set the values of the 'headType' and
        'headNumber' fields. The constructor also sets default values for
        'nextHead' and 'firstInternalNode' to null. Constructor does not 
        return anything.
    */
    public HeadNode (String headType, int headNumber) {
        // Defining the fields as described in fields section
        this.nextHead = null;
        this.firstInternalNode = null;
        this.headType = headType;
        this.headNumber = headNumber;
    }

    /*
    Below are all getters and setters for the fields of the class. The setters
    have parameters that define what the new set value for the field will be.
    The getters do not have any paramters. The setters do not return anything.
    The getters return there specific field value.
    */
    public HeadNode getNextHead() {
        return nextHead;
    }

    public void setNextHead(HeadNode next) {
        this.nextHead = next;
    }

    public Node getInternalNode() {
        return this.firstInternalNode;
    }

    public void setInternalNode(Node internNode) {
        this.firstInternalNode = internNode;
    }

    public String getHeadType() {
        return this.headType;
    }

    public int getHeadNumber() {
        return this.headNumber;
    }
}