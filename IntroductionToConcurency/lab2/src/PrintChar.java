public class PrintChar extends Thread {
    private int intToPrint;
    private int timesToPrint;
    private ConditionClass condition;
    public PrintChar(int  a,int b,ConditionClass x){
        intToPrint = a;
        timesToPrint = b;
        condition = x;
    }
    public void run(){
        int k = 0;
        while(true){
            //czekamy na nasz kolej
            synchronized (condition) {
                while (condition.flag != intToPrint) {
                    try {
                        condition.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            k++;
            System.out.print(intToPrint); if(intToPrint == 3) {System.out.print('\n');}
            synchronized (condition){condition.next();condition.notifyAll();}
            if(k==timesToPrint){break;}
        }
    }
}
