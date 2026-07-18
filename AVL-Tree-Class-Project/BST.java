/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: BST.java
 * External Packages: java.util.ArrayList, java.text.DecimalFormat
 * Purpose: This class is the AVL tree. This class defines methods to insert
 *      Nodes into the AVL tree. After each insertion it checks if a pivot was
 *      created. If so then it decides which of the four rotations (Single
 *      Left, Single Right, Double Left or Double Right) needs to be
 *      performed to meet the AVL tree balance condition which is that each
 *      Nodes left and right subtree heights must only differ by a value of 1.
 *      There is also a method that prints the AVL tree with traversing the
 *      tree with a inorder traversal. There is also a method that gets the
 *      height of the tree.
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      inserNode()
 *      insertNodeHelper()
 *      traverseBSTInsert()
 *      pivotChecker()
 *      singleLeftRotation()
 *      singleRightRotation()
 *      updateParents()
 *      printInorder()
 *      printInorderHelper()
 *      getTreeHeight()
 */
import java.util.ArrayList;
import java.text.DecimalFormat;

public class BST {

    private Node root; // reference to the root of the tree
    private boolean rotationHappend; // flag to tell if a rotation has happend

    /*
     * BST() -- Constructor of the class. Returns nothing. Sets the default
     *      value of the 'root' field to null since initally the tree is empty.
     *      Also the 'rotationHappened' field is set to its default state of
     *      flase. This flag is used in this class to signal if true that a
     *      rotation has already occured so checking for a pivot does not need
     *      to occur.
     */
    public BST() {
        this.root = null; // default the tree is empty
        this.rotationHappend = false; // set flag to false
    }

    /*
     * inserNode(newNode) -- Takes in a Node 'newNode' and calls
     *      insertNodeHelper() on it to insert the 'newNode' into the AVL tree.
     *      This method defines the TwoNodeQueue which will be used to
     *      remember the last two Nodes seen when going down the recursive
     *      stack. Method also resets the rotationHappened field flag to false.
     *      This flag when true signals that a rotation has already happened so
     *      checking for a pivot does not need to occur anymore. That flag
     *      needs to be set to false at the beginning of every insertion
     *      because the insertion could create a unbalanced AVL tree. This
     *      method does not return anything, it just manipulates the AVL tree.
     */
    public void insertNode(Node newNode) {

        // Remembers the last 2 Nodes seen when unravling the stack
        TwoNodeQueue last2NodeQ = new TwoNodeQueue();
        this.rotationHappend = false; // reset flag to false before insertion

        // Call the insertNodeHelper
        insertNodeHelper(newNode, this.root, last2NodeQ);
    }

    /*
     * insertNodeHelper(newNode, currNode, last2NodeQ) -- Takes in two Nodes
     *      'newNode' & 'currNode' and TwoNodeQueue 'last2NodeQ'. This method
     *      is the helper method to insertNode() and it contains the base cases
     *      and calls the traverseBSTInsert to insert the 'newNode' into the
     *      AVL tree. This method does not return anything, it just manipulates
     *      the AVL tree.
     */
    private void insertNodeHelper(Node newNode, Node currNode, 
                                                TwoNodeQueue last2NodeQ) {
        // One base case -- Tree empty set the 'newNode' to be root
        if (this.root == null) {
            this.root = newNode;
            return;
        }

        // Second base case -- Leaf Node
        if (currNode == null) {
            return;
        }

        // Get the current Node value & newNode value
        double currNodeVal = currNode.getAmountOfIncrease();
        double newNodeVal = newNode.getAmountOfIncrease();

        // Determine where to go: Left or Right
        traverseBSTInsert(newNode, currNode, newNodeVal, 
                                currNodeVal, last2NodeQ);
    }

    /*
     * traverseBSTInsert(newNode, currNode, newNodeVal, currNodeVal, 
     *      last2NodeQ) -- Takes in two Nodes: 'newNode' & 'currNode', it takes
     *      in two doubles: 'newNodeVal' & 'currNodeVal' and a TwoNodeQueue
     *      'last2NodeQ'. This method loops over the AVL tree and inserts the
     *      'newNode' into the tree. After insertion the method when the
     *      recursion starts to go down the stack it calls pivotChecker() to
     *      see if a pivot is at the 'currNode' if so then the proper rotations
     *      occur. The 'last2NodeQ' is used to keep track of the previous 2
     *      nodes that we have seen by going down the recursive stack. The
     *      two doubles 'newNodeVal' and 'currNodeVal' are used to know which
     *      branch (left or right) to take. At the very end of the method
     *      there is a boolean value (rotationHappend field) that if true means
     *      that a rotation has already happened so they checking for a pivot
     *      does not need to happen. This method does not return anything, it
     *      just manipulates the AVL tree.
     */
    private void traverseBSTInsert(Node newNode, Node currNode, 
        double newNodeVal, double currNodeVal, TwoNodeQueue last2NodeQ) {
        // If the newNode value less than or equal (tie) currNode value go left
        if (newNodeVal <= currNodeVal) {

            // Recursive call left
            insertNodeHelper(newNode, currNode.getLeftChild(), last2NodeQ);

            // If current Node has open left spot added currNode
            if (currNode.getLeftChild() == null) {

                // Set currNode as left child
                currNode.updateLeftChildNode(newNode);

                // Set newNodes parent -- currNode
                newNode.updateParentNode(currNode);

                // Enqueue the new node, don't nned to check for pivot here
                last2NodeQ.enqueueNode(newNode);
            }

            // Get the left child
            Node currLchild = currNode.getLeftChild();

            // Get the max of the left child left and right heights
            int maxLchildHeight = Math.max(currLchild.getLeftSubtreeHeight(), 
                                        currLchild.getRightSubtreeHeight());
            // currNode the max + 1
            currNode.updateLeftSubtreeHeight(maxLchildHeight + 1);

        }

        // If the newNode value greater than currNode value go right
        if (newNodeVal > currNodeVal) {

            // Recursive call right
            insertNodeHelper(newNode, currNode.getRightChild(), last2NodeQ);

            // If current Node has open right spot added currNode
            if (currNode.getRightChild() == null) {

                // Set currNode as right child
                currNode.updateRightChildNode(newNode);

                // Set newNodes parent -- currNode
                newNode.updateParentNode(currNode);

                // Enqueue the new node, don't nned to check for pivot here
                last2NodeQ.enqueueNode(newNode);
            }

            // Get the right child
            Node currRchild = currNode.getRightChild();

            // Get the max of the left child left and right heights
            int maxRchildHeight = Math.max(currRchild.getLeftSubtreeHeight(), 
                                        currRchild.getRightSubtreeHeight());
            // currNode the max + 1
            currNode.updateRightSubtreeHeight(maxRchildHeight + 1);
        }

        // Pivot checker -- if rotation already happened tree is balance
        if (!this.rotationHappend) {
            this.rotationHappend = pivotChecker(currNode, last2NodeQ);
        }
        last2NodeQ.enqueueNode(currNode); // Enqueue node
    }

    /*
     * pivotChecker(currNode, last2NodeQ) -- Takes in a Node 'currNode' and
     *      a TwoNodeQueue 'last2NodeQ' and determines if the 'currNode' is a
     *      pivot. If so, then it determines which rotation is performed and
     *      then calls those rotation methods. A boolean is returned, true
     *      if a rotation is performed so the tree is balanced. Else, false
     *      if no rotation is performed. 
     */
    private boolean pivotChecker(Node currNode, TwoNodeQueue last2NodeQ) {

        // Check current node left and right heights to see if pivot
        int currNodeLeftHeight = currNode.getLeftSubtreeHeight();
        int currNodeRightHeight = currNode.getRightSubtreeHeight();

        // Determine if the currNode is pivot then Figure out which rotation
        if (Math.abs(currNodeLeftHeight - currNodeRightHeight) > 1) {

            ArrayList<Node> qList = last2NodeQ.getQueue(); // Get the queue

            // Get both Nodes from the Queue
            Node secondInQ = qList.get(1); // Parent of 'firstInQ'
            Node firstInQ = qList.get(0); // Child of 'secondInQ'

            // Check which child 'secondInQ' is with the pivot (currNode)

            // Means its a right rotation 
            if (currNode.getLeftChild() == secondInQ) {

                // Which right rotation by checking which child is 'firstInQ'

                // Single right rotation
                if (secondInQ.getLeftChild() == firstInQ) {
                    singleRightRotation(currNode, secondInQ);
                }
                // Double Right rotation
                if (secondInQ.getRightChild() == firstInQ) {

                    // SL around pivot and node moving up
                    singleLeftRotation(secondInQ, firstInQ);

                    // SR around child and grandchild of pivot
                    singleRightRotation(currNode, firstInQ);
                }
            }

            // Means its a left rotation 
            if (currNode.getRightChild() == secondInQ) {

                // Which left rotation by checking which child is 'firstInQ'

                // Double left rotation
                if (secondInQ.getLeftChild() == firstInQ) {

                    // SR around child and grandchild of pivot
                    singleRightRotation(secondInQ, firstInQ);

                    // SL around pivot and node moving up
                    singleLeftRotation(currNode, firstInQ);
                }
                // Single left rotation
                if (secondInQ.getRightChild() == firstInQ) {
                    singleLeftRotation(currNode, secondInQ);
                }
            }
            return true; // that a pivot has occured, you can stop looking
        }
        return false; // pivot has not occured, keep looking
    }

    /*
     * singleLeftRotation(pivotNode, rotatingNode) -- Takes in two Nodes 
     *      'pivotNode' and 'rotatingNode'. This method performs the single
     *      left rotation for an AVL tree. This method is broken into sections
     *      of how the rotation is handled. The method does update 'pivotNode'
     *      and 'rotatingNode's left and right subtree heights. Method does not
     *      return anything. Just performs a Single Left rotation on the tree.
     */
    private void singleLeftRotation(Node pivotNode, Node rotatingNode) {
        // -- Save Node Moving up left subtree stuff -- 

            // Get the Node moving up: left subtree
            Node lSubtree = rotatingNode.getLeftChild();

            // Get the Nodes moving up left subtree height
            int lSubtreeHeight = rotatingNode.getLeftSubtreeHeight();

        // -- Pivot parent & Node moving up update section --

            // Get the pivots parent and update the parents
            Node pivotParent = pivotNode.getParentNode(); 
            updateParents(pivotNode, rotatingNode, pivotParent);

        // -- Update which subtrees go where section -- 

            // Node moving up make its left subtree the pivot
            rotatingNode.updateLeftChildNode(pivotNode);

            // update the pivots right subtree (pass over)
            pivotNode.updateRightChildNode(lSubtree);

            // Set the passover Parent to the pivot
            if (lSubtree != null) {
                lSubtree.updateParentNode(pivotNode);
            }

        // -- Update height of Nodes section --

            // Pivot right height is updated based on the passOver
            pivotNode.updateRightSubtreeHeight(lSubtreeHeight);

            // Update node moving up's height by taking max of pivots 
            // left and right subtree height
            int rotatingNodeLeftHeight = Math.max(
                pivotNode.getLeftSubtreeHeight(), 
                pivotNode.getRightSubtreeHeight()) + 1;

            rotatingNode.updateLeftSubtreeHeight(rotatingNodeLeftHeight);
    }

    /*
     * singleRightRotation(pivotNode, rotatingNode) -- Takes in two Nodes 
     *      'pivotNode' and 'rotatingNode'. This method performs the single
     *      right rotation for an AVL tree. This method is broken into sections
     *      of how the rotation is handled. The method does update 'pivotNode'
     *      and 'rotatingNode's left and right subtree heights. Method does not
     *      return anything. Just performs a Single Right rotation on the tree.
     */
    private void singleRightRotation(Node pivotNode, Node rotatingNode) {
        // -- Save Node Moving up right subtree stuff -- 

            // Get the Node moving up: right subtree
            Node rSubtree = rotatingNode.getRightChild();

            // Get the Nodes moving up right subtree height
            int rSubtreeHeight = rotatingNode.getRightSubtreeHeight();

        // -- Pivot parent & Node moving up update section --

            // Get the pivots parent and update the parents
            Node pivotParent = pivotNode.getParentNode(); 
            updateParents(pivotNode, rotatingNode, pivotParent);

        // -- Update which subtrees go where section -- 

            // Node moving up make its right subtree the pivot
            rotatingNode.updateRightChildNode(pivotNode);

            // update the pivots left subtree (pass over)
            pivotNode.updateLeftChildNode(rSubtree);

            // Set the passover Parent to the pivot
            if (rSubtree != null) {
                rSubtree.updateParentNode(pivotNode);
            }

        // -- Update height of Nodes section --

            // Pivot left height is updated based on the passOver
            pivotNode.updateLeftSubtreeHeight(rSubtreeHeight);

            // Update node moving up's height by taking max of pivots 
            // left and right subtree height
            int rotatingNodeRightHeight = Math.max(
                pivotNode.getLeftSubtreeHeight(), 
                pivotNode.getRightSubtreeHeight()) + 1;

            rotatingNode.updateRightSubtreeHeight(rotatingNodeRightHeight);
    }

    /*
     * updateParents(pivotNode, rotatingNode, pivotParent) -- Method takes in
     *      three Nodes, 'pivotNode', 'rotatingNode' & 'pivotParent'. This
     *      method is used in both the single left and right rotation methods.
     *      This method updates the parents of the pivot's parent to the
     *      rotating node and it updates the pivots parent to the rotating node
     *      as well. The rotating Nodes parent is also updated to the pivots
     *      old parent before the rotation. Nothing is returned, the AVL tree
     *      is manipulated.
     */
    private void updateParents(Node pivotNode, Node rotatingNode, 
                                                        Node pivotParent) {
            // Know which child pivot is for its parent, update that to
            // Node moving up. AND check if pivot is root
            if (pivotParent != null) {
                if (pivotParent.getLeftChild() == pivotNode) {
                pivotParent.updateLeftChildNode(rotatingNode);
                }

                if (pivotParent.getRightChild() == pivotNode) {
                    pivotParent.updateRightChildNode(rotatingNode);
                }
            }

            // If pivot is the root
            else {
                this.root = rotatingNode;
            }

            // Make the node moving up parent the pivots old parent
            rotatingNode.updateParentNode(pivotParent);

            // Update pivot's parent to the node moving up
            pivotNode.updateParentNode(rotatingNode);
    }

    /*
     * printInorder() -- No parameters. Returns nothing. Calls 
     *      printInorderHelper() with the root. The helper method loops over
     *      the BST with an inorder traversal and prints the Nodes.
     */
    public void printInorder() {
        // create DecimalFormat class
        DecimalFormat df1 = new DecimalFormat("#.###");
        DecimalFormat df2 = new DecimalFormat(".###");
        printInorderHelper(this.root, df1, df2); // call helper method
    }

    /*
     * printInorderHelper(currNode, df) -- Takes in a Node 'currNode' and loops 
     *      over the the BST with an inorder traversal and prints the Nodes.
     *      The other two parameters are DecimalFormat's 'df1' & 'df2'. 'df1'
     *      is used to remove the '.0' in numbers and 'df2' is used to remove
     *      the leading zero for decimal points. Method does not return
     *      anything.
     */
    private void printInorderHelper(Node currNode, DecimalFormat df1, DecimalFormat df2) {

        // Base Case -- If null (leaf Node) return
        if (currNode == null) {
            return;
        }

        printInorderHelper(currNode.getLeftChild(), df1, df2); // Go left

        // Print Node name
        System.out.print(currNode.getNodeName() + "; ");

        // Get the proposed increase in dollars
        double num = currNode.getAmountOfIncrease();

        // If num is less than 1 use df2
        if (num < 1) {
            System.out.println(df2.format(num));
        }

        else {
            System.out.println(df1.format(num));
        }

        printInorderHelper(currNode.getRightChild(), df1, df2); // Go Right
    }

    /*
     * getTreeHeight() -- No parameters. Returns an integer of the tree height.
     *      Method takes the max of the left subtree height and the right
     *      subtree height from the root.
     */
    public int getTreeHeight() {
        return Math.max(this.root.getLeftSubtreeHeight(), 
                        this.root.getRightSubtreeHeight());
    }
}