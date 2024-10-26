package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;


public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();
        int N = 1000000;
        String message ="";
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0,6);
             if (operationNumber == 0) {
                 int randVal = StdRandom.uniform(0,100);
                 sad1.addFirst(randVal);
                 correct.addFirst(randVal);
                 message =message + "addFirst(" + randVal + ")" + "\n";
                 assertEquals(message,sad1.size(), correct.size());

             }else if (operationNumber == 1) {
                 int randVal = StdRandom.uniform(0,100);
                 sad1.addLast(randVal);
                 correct.addLast(randVal);
                 message =message + "addLast(" + randVal + ")" + "\n";
                 assertEquals(message,sad1.size(), correct.size());

             }else if (sad1.size() == 0) {
                 assertTrue("isEmpty error",sad1.isEmpty());
             }else if (operationNumber == 2) {
                 assertTrue("size error",sad1.size() > 0);
                 assertEquals("size error",sad1.size(), correct.size());
             } else if (operationNumber == 3) {
                 Integer a = sad1.removeFirst();
                 Integer b = correct.removeFirst();
                 message =message + "removeFirst()" + "\n";
                 assertEquals(message,sad1.size(), correct.size());
                 assertEquals(message,a, b);

             } else if (operationNumber == 4) {
                 Integer a = sad1.removeLast();
                 Integer b = correct.removeLast();
                 message =message + "removeLast()" + "\n";
                 assertEquals(message,sad1.size(), correct.size());
                 assertEquals(message,a, b);

             } else if (operationNumber == 5) {
                 int randIndex = StdRandom.uniform(0, sad1.size());
                 Integer a = sad1.get(randIndex);
                 Integer b = correct.get(randIndex);
                 message =message + "get(" + randIndex + ")" + "\n";
                 assertEquals(message,a , b);

             }
        }
    }
}
