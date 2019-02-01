public class Threads extends Thread{
    private boolean ifInc;
    private int times;
    private Counter counter;
    private semBin sem;
    public Threads(boolean f,int t, Counter c,semBin s){
        ifInc = f;
        times = t;
        counter = c;
        sem = s;
    }

    public void run(){
        for(int i=0;i<times;i++){
            sem.take();
            if(ifInc){counter.inc();}else{counter.dec();}
            sem.release();
        }
    }

}
