/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: avl_tree.java
 * External Packages: java.util.Scanner, java.io.File, java.util.ArrayList,
 *      java.io.FileNotFoundException, java.util.HashMap
 * Purpose: This program is the main file of the project. This class reads an
 *      input file and creates Nodes from the input file while following
 *      certain logic. There are certain baselines that royal family members
 *      must follow and they can be increased or decreased depending on what
 *      there title is. Also if they send in another proposal it is rejected.
 *      So the only Nodes that are created are ones that follow those rules.
 *      Then an AVL tree is created and then traversed with an inorder
 *      traversal and then the height of the tree is printed out.
 * Class Constructor: N/A. Main program.
 * Class methods list:
 *      getTreeNodes()
 *      royalFamilyBaseLines()
 *      baselineIncrease()
 *      getBudgetIncrease()
 *      buildAVLtreeANDprint()
 *      main()
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * How and why did I implment the AVL tree rotations?
 *      The comments in the code for rotations in BST.java describe how the
 *      rotations work very well but I will describe it here as well. All of
 *      the rotations are all based on the Single Left rotation. After a node
 *      is inserted into the AVL tree is when the checking for the pivot
 *      occurs. When the recursive method goes down the call stack it checks
 *      the current nodes left and right subtree’s heights difference. If the
 *      difference is greater than 1 then we have a pivot else we do not. If we
 *      have a pivot the next thing that happens is that the program determines
 *      which rotation needs to be performed. When going down the call stack a
 *      Queue of fixed size at 2 is always updated. This helps to keep track of
 *      the last two seen nodes. The last two nodes that we have seen are
 *      helpful in finding out which rotation to perform. If the pivot’s right
 *      child is the most recently added Node in the Queue then we know we have
 *      a left rotation. Next we check what child the first Node in the Queue
 *      is based on the second Node in the Queue which is always the parent of
 *      the first Node in the Queue. If the second Node in the Queue’s left
 *      child is the first Node in the Queue then it is a Double Left Rotation.
 *      If the second Node in the Queue’s right child is the first Node in the
 *      then it is a Single Left Rotation. By checking where the first Node is
 *      it tells us which specific rotation we need to perform. The same logic
 *      applies to the Right Rotation but the logic behind the second Node in
 *      the Queue’s child is swapped. (See code for more detail).
 *
 *      After the type of rotation is picked for Single Left rotation we do the
 *      rotation around the pivot and the pivots right child which is the
 *      second Node in the Queue. This part of the rotation is not recursive,
 *      rather it is like moving LinkedList nodes around. The rotation is
 *      broken up into sections: Saving Node Moving Up left or right subtree
 *      depending on rotation, Pivot parent and Node moving up update section,
 *      update which subtrees go where section and the update height of Nodes
 *      section. This logic is the same for Single Right as well. For both
 *      double rotations they use some ordering of both single rotations. They
 *      both use the nodes from the Queue to help perform the rotation. For
 *      Double Right for example it performs a SL treating the second in the
 *      Queue as the pivot and the rotating up Node as the first Node in the
 *      Queue. Next a SR is performed treating the currNode as the pivot and
 *      the first Node in the Queue as the one moving upward. The same logic
 *      appliances for Double Left but the ordering of the single rotations 
 *      will swap. 
 * 
 *      The reason why I choose to do my rotations this way is because it is
 *      not recursive or iterative. It is constant time so it is super
 *      efficient. I also found it to be relatively straightforward to
 *      implement after drawing it out on paper. It was like moving Nodes
 *      around in a LinkedList. I also set up my Node’s class to have many
 *      fields to help streamline the rotations. One field is that the Node has
 *      a reference to its parent so we can easily get the pivots parent
 *      without going upward in the tree. Also the tree height math is constant
 *      as well since it is all updated when we move back up the tree. Overall,
 *      I found this method to be very effective and efficient and reality
 *      simple to implement. 
 */


public class avl_tree {

    /*
     * getTreeNodes(fileName, keyList, agencyNodeHmap) -- Takes in a String
     *      'fileName', ArrayList of Strings 'keyList' and a HashMap with
     *      String keys and Node values: 'agencyNodeHmap'. Method loops over
     *      the input file and determines if each royal agency is allowed to
     *      have a budget increase. Nothing is returned. Method just
     *      manipluates 'keyList' & 'agencyNodeHmap' the 'keyList' holds all of
     *      the already accepted agency titles and the 'agencyNodeHmap' keys
     *      are those titles with the values being the Nodes that need to be
     *      inserted into the AVL tree.
     */
    public static void getTreeNodes(String fileName, ArrayList<String> keyList, 
                                    HashMap<String, Node> agencyNodeHmap) {

        try {
            File readFile = new File(fileName); // Create file obj to read
            Scanner fileScanner = new Scanner(readFile); // Scanner obj to read

            // Loop over the text file
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine(); // get next line in file

                // Split at the '; ' can create the String array
                String[] lineArr = line.strip().split("; ");

                // Get bool to see if it is value to add node to AVL
                boolean canAddProposal = getBudgetIncrease(lineArr, keyList);

                if (canAddProposal) {

                    // agency title
                    String agencyTitle = lineArr[0].toLowerCase();

                    // Add full title lowercased to keyList
                    keyList.add(agencyTitle);

                    // Proposed Money Increase for the agency
                    double proposedMoneyIncrease = Double.valueOf(lineArr[1]);

                    // Create the Node for the AVL tree default parent to null
                    Node newNode = new Node(lineArr[0], 
                                    proposedMoneyIncrease, null);

                    agencyNodeHmap.put(agencyTitle, newNode); // Add to HashMap
                }
            }
            fileScanner.close(); // close the file
        }

        catch (FileNotFoundException e) {
            System.out.println("ERROR: FILE NOT FOUND");
        }
    }
    /*
     * royalFamilyBaseLines(agencyTitle) -- Takes in a String 'agencyTitle'
     *      based on that String depending on what title is in the string a
     *      integer is returned which is the default baseline percentage for
     *      an agency ran by that certain royal person.
     */
    private static int royalFamilyBaseLines(String agencyTitle) {

        if (agencyTitle.contains("crown prince")) {
            return 2;
        }

        if (agencyTitle.contains("princess")) {
            return 3;
        }

        if (agencyTitle.contains("prince")) {
            return 4;
        }

        if (agencyTitle.contains("queen")) {
            return 5;
        }

        if (agencyTitle.contains("duke")) {
            return 6;
        }

        System.out.println("No Royale baseline was found.");
        return 0; // if nothing is found
    }

    /*
     * baselineIncrease(royalBaseline, agencyTitle) -- Takes in an integer
     *      'royalBaseline' and a String 'agencyTitle' and determines if the
     *      'royalBaseline' should be increased by certain keywords. Method
     *      returns 'royalBaseline' either increased if the keyword(s) are in
     *      the 'agencyTitle' or not changed.
     */
    private static int baselineIncrease(int royalBaseline, String agencyTitle){

        if (agencyTitle.contains("research")) {
            royalBaseline += 10; // increase by 10%
        }

        if (agencyTitle.contains("education")) {
            royalBaseline += 10; // increase by 10%
        }

        if (agencyTitle.contains("lapidary")) {
            royalBaseline += 10; // increase by 10%
        }

        return royalBaseline;
    }

    /*
     * getBudgetIncrease(lineArr, keyList) -- Method takes in a String array
     *      'lineArr' and a ArrayList of Strings 'keyList'. This method
     *      implements the three decision cases if a agencies proposal can go
     *      through. The first case that I test (case 3 in the spec) is if the
     *      agency already has an approved proposal. If so then false is
     *      returned. The second case that I test is (case 1 in the spec) and
     *      it finds the default baseline of percentage increase for that 
     *      current agency based on the royal title in the agency name. The
     *      last case that I test is (case 2 in the spec) and it determines if
     *      the baseline is increase/ decreased. Then the method determines if
     *      the proposed percentage increase is within the range of the
     *      baseline. If so then true is returned else false.
     */
    private static boolean getBudgetIncrease(String[] lineArr,
                                                 ArrayList<String> keyList) {

        String agencyTitle = lineArr[0].toLowerCase(); // agency title

        // Proposed Money Increase for the agency
        double proposedMoneyIncrease = Double.valueOf(lineArr[1]);

        // current agency budget
        double currentBudget = Double.valueOf(lineArr[2]);

        // Case 3 in the spec. If agency already got approved budget increase
        // deny the new proposal
        if (keyList.contains(agencyTitle)) {
            return false;
        }

        // Case 1 in spec. Get the default baseline
        int royalBaselinePerc = royalFamilyBaseLines(agencyTitle);

        // Case 2 check if baseline changes (increase or decrease)

        // Check title has the three keywords that can increase the baseline
        int currBaseline = baselineIncrease(royalBaselinePerc, agencyTitle);

        // Check if 'personal' is in the title
        if (agencyTitle.contains("personal")) {
            currBaseline -=2; // decrease by 2 is so
        }

        // Get the proposed percent
        double proposedPercent = (proposedMoneyIncrease/ currentBudget) * 100;

        // if propsal % > baseline return false if <= return true
        // if subtraction very small assumed to be true
        if ((Math.abs(proposedPercent - currBaseline) < Math.pow(10, -10)) ||
                (proposedPercent < currBaseline) ) {
                    return true; // proposal approved
                }

        return false; // proposal denied
    }

    /*
     * buildAVLtreeANDprint(keyList, agencyNodeHmap) -- Takes in a ArrayList of
     *      Strings which are the keys to the HashMap (key: String, 
     *      value: Node) 'agencyNodeHmap'. This method loops over the 'keyList'
     *      and gets the Nodes of the agencies and inserts them into a AVL
     *      tree. Then the tree is printed with the name of the agency and the
     *      proposed increase in dollars using inorder traversal. Lastly, the
     *      tree height is printed. Nothing is returned.
     */
    public static void buildAVLtreeANDprint(ArrayList<String> keyList, 
                                    HashMap<String, Node> agencyNodeHmap) {
        BST avlTree = new BST(); // avl tree for the agencies

        // Loop over the key list and insert Node into the AVL tree
        for (int i = 0; i < keyList.size(); i++) {

            String key = keyList.get(i);
            Node agencyNode = agencyNodeHmap.get(key);

            avlTree.insertNode(agencyNode); // Insert the agency Node
        }

        avlTree.printInorder(); // Print the tree using inorder traversal
        System.out.print(avlTree.getTreeHeight()); // print tree height
    }

    /*
     * main(args) -- Takes in a String[] 'args'. Main method of the class and
     *      project. Method takes in arguments from the command line like:
     *      "java avl_tree input.txt" and takes the input file name and calls
     *      getTreeNodes() and buildAVLtreeANDprint(). Nothing is returned.
     */
    public static void main(String[] args) {

        // List to hold all of the agency titles (keys) in order
        ArrayList<String> keyList = new ArrayList<>();

        // HashMap (keys: agency titles, value: Node) 
        HashMap<String, Node> agencyNodeHmap = new HashMap<>();

        // get the Nodes that will be put into the AVL tree
        getTreeNodes(args[0], keyList, agencyNodeHmap); // Uses command line

        // build the AVL tree and print the output of the program
        buildAVLtreeANDprint(keyList, agencyNodeHmap); 
    }
}