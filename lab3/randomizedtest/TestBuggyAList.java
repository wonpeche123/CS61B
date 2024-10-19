package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> comparedList = new AListNoResizing<>();
        BuggyAList<Integer> buggyList = new BuggyAList<>();

        comparedList.addLast(5);
        comparedList.addLast(10);
        comparedList.addLast(15);

        buggyList.addLast(5);
        buggyList.addLast(10);
        buggyList.addLast(15);

        assertEquals(comparedList.size(),buggyList.size());

        assertEquals(comparedList.removeLast(),buggyList.removeLast());
        assertEquals(comparedList.removeLast(),buggyList.removeLast());
        assertEquals(comparedList.removeLast(),buggyList.removeLast());
    }
    @Test
    public void randomizedtest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyList = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyList.addLast(randVal);

            } else if (operationNumber == 1) {
                // getLast
                int sizeL = L.size();
                int sizeBug = buggyList.size();
                if (sizeL > 0){
                    L.getLast();
                }
                if (sizeBug > 0){
                    buggyList.getLast();
                }
            }else if (operationNumber == 2) {
                // removeLast
                int size = L.size();
                int sizeBug = buggyList.size();
                if (size > 0){
                    L.removeLast();
                }
                if (sizeBug > 0){
                    buggyList.removeLast();
                }
            }else if (operationNumber == 3) {
                // size
                int size = L.size();
                int sizeBug = buggyList.size();
            }
        }
    }
}

