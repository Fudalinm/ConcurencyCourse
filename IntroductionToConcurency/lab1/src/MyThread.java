public class MyThread extends Thread {
    private Counter id;
    private int toDo;

    public MyThread (Counter i,int in){
        id = i;
        toDo = in;
    }

    public void run(){
       if (toDo == 1){
           for (int y=0;y<10000000;y++){
               id.inc();
           }
       }else{
           for (int y=0;y<10000000;y++){
               id.dec();
           }
       }
    }
}
