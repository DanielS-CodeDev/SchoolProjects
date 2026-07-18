/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: TwoNodeQueue.java
 * External Packages: java.util.ArrayList
 * Purpose: This class implments a Queue but that can only whole two Nodes. If
 *      a new Node is inserted then the Node at the front is dequeued to make
 *      room for the new Node. There is a enqueue method and even a method to
 *      get the whole queue itself. There is also a toString() method.
 * Class Constructor: Uses the default constructor.
 * Class methods list:
 *      enqueueNode()
 *      getQueue()
 *      toString()
 */
import java.util.ArrayList;

public class TwoNodeQueue {

    private ArrayList<Node> q = new ArrayList<>(); // field: Queue 

    /*
     * enqueueNode(newNode) -- Takes in a Node 'newNode' and first determines
     *      if the two Node Queue is full. If so it dequeues the node at the 
     *      front of the Queue and then enqueues the 'newNode'. If the Queue is
     *      not full the 'newNode' is enqueued like normal. Nothing is 
     *      returned.
     */
    public void enqueueNode(Node newNode) {

        // If queue full
        if (q.size() >= 2) {
            q.remove(0);    // Dequeue
            q.add(newNode);     // Enqueue
            return;
        }
        q.add(newNode); // If q not full just enquque newNode
    }

    /*
     * getQueue() -- Has no parameters. Returns a ArrayList of Nodes, the two
     *      node Queue.
     */
    public ArrayList<Node> getQueue() {
        return this.q;
    }

    /*
     * toString() -- No parameters. Returns a String representation of the two
     *      Node Queue. The Names of the Nodes are shown to represent the Node
     *      itself.
     */
    @Override
    public String toString() {

        String str = "{";

        if (q.size() == 1) {
            str += q.get(0).getNodeName() + ", ";
        }

        if (q.size() == 2) {
            str += q.get(0).getNodeName() + ", ";
            str += q.get(1).getNodeName() + ", ";
        }
        return str + "}";
    }
}