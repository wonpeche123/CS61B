package deque;

import edu.princeton.cs.algs4.BST;

public class test {

    static BST insert(BST T, Key ik){
        if (T == null) {
            return new BST(ik);
        } else if (ik < T.key) {
            return insert(T.left, ik);
        }
    }
}
