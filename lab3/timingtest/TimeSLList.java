package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> N = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int M = 10000;
        int count = 1000;
        for(int i = 0; i < 8; i++){
            N.addLast(count);
            opCounts.addLast(M);
            count = count * 2;
        }
        for(int i = 0; i < 8; i++){
            double curTime = timeOneNum(N.get(i), M);
            times.addLast(curTime);
        }
        printTimingTable(N, times, opCounts);
    }
    public static double timeOneNum(int count, int m){
        SLList<Integer>array = constructASLList(count);
        Stopwatch sw = new Stopwatch();
        for(int i = 0; i < m; i++){
            int temp = array.getLast();
        }
        return sw.elapsedTime();
    }
    public static SLList<Integer> constructASLList(int count){
        SLList<Integer> list = new SLList<>();
        for( int i = 0; i < count; i++){
            list.addLast(0);
        }
        return list;
    }

}
