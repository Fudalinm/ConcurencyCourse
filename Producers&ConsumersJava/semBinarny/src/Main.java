public class Main {
    public static void main(String[] args){
        Counter c = new Counter();
        semBin s = new semBin(true);
        Threads t1 = new Threads(true,10000,c,s);
        Threads t2 = new Threads(false,10000,c,s);
        t1.start();
        t2.start();
        try{
            t1.join();
            t2.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        c.print();
    }
}
