package Programming_Project1;

public class OrthogonalList {

    private HeadNode masterRowHead;
    private HeadNode masterColHead;

    public OrthogonalList(int rowSize, int colSize) {
        // Defining the fields as described in fields section
        this.masterRowHead = null;
        this.masterColHead = null;

        // Create the orthogonal list with rowSize and colSize
        createHeads(rowSize, "Row", masterRowHead);

    }

    private void createHeads(int size, String type, HeadNode master) {

        master = new HeadNode(type, 0);
        HeadNode prev = master;     // is the previous node added

        System.out.println("HE");

        for (int i = 1; i < size; i++) {

            HeadNode newHead = new HeadNode(type, i);
            prev.setNextHead(newHead);
            prev = newHead;

        }




    }

    public void strTest() {

        HeadNode current = masterRowHead;

        while (current != null) {

            System.out.println(current.getHeadType() + " " + current.getHeadNumber());

            if (current.getNextHead() != null) {}
                current.setNextHead(current.getNextHead());
            }
        }


}


