package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
//        {1000,2000,4000,8000,16000,32000,64000,128000}''
        AList<Integer> N = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> ops = new AList<>();
        int count = 1000;
        for(int i = 0;i<8;i++){
            N.addLast(count);
            ops.addLast(count);
            count = count*2;
        }
        for(int i = 0;i < N.size();i++){
            double curTime = testOneNum(N.get(i));
            times.addLast(curTime);
        }
        printTimingTable(N,times,ops);
    }
    public static double testOneNum(int count){
        Stopwatch sw = new Stopwatch();
        AList<Integer> array = new AList<>();
        for(int i = 0;i < count;i++){
            array.addLast(0);
        }
        return sw.elapsedTime();
    }
}
