public class triangle {
    public static void main(String[] args) {
        int count =1;
        int height=8;
        while(count<=height){
            int start=0;
            while(start<count){
                System.out.print("*");
                start+=1;
            }
            System.out.println(" ");
            count+=1;
        }
    }
}