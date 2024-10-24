public class SLList {
    private static class IntNode{
        public int item;
        public IntNode next;

        public IntNode (int first , IntNode node ){
            item = first;
            next = node;
        }
    }

    private IntNode first;

    public SLList(int x) {
        first = new IntNode(x, null);
    }

    /** Adds an item to the front of the list. */
    public void addFirst(int x) {
        first = new IntNode(x, first);
    }

    public void addLast(int x) {
        ;
    }

    public int size(){
        int len = 0;
        return len;
    }
}