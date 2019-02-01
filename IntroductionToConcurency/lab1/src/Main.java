public class Main {
    public static void main(String args[]) throws InterruptedException{
//        int threadNumber = 1000;
//        Counter counter = new Counter(0);
//        MyThread[] threads = new MyThread[threadNumber];
//
//        for(int i=0;i<threadNumber;i++){
//            threads[i] = new MyThread(counter);
//        }
//        for(int i=0;i<threadNumber;i++){
//            threads[i].start();
//        }
//
//        Thread.sleep(3000);
//        System.out.println(counter.i);

        Counter counter1 = new Counter(0);
        MyThread t1 = new MyThread(counter1,1);
        MyThread t2 = new MyThread(counter1,2);

        t1.start();
        t2.start();
        try{
            t1.join();
            t2.join();
        }catch (InterruptedException E){
            System.out.println("Blad joina\n");
        }
        System.out.println(counter1.i);



    }
}
