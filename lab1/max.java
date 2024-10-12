public class max {
    /** Returns the maximum value from m. */
    public static int forMax(int[] m) {
        int maxNum=m[0];
        int length=m.length;
        for(int cnt=1;cnt<length;cnt++){
            if(maxNum<m[cnt]){
                maxNum=m[cnt];
            }
        }
        return maxNum;
    }
    public static void main(String[] args) {
        int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};
        System.out.println(forMax(numbers));
    }
}