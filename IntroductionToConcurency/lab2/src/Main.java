public class Main {
    public static void main(String args[]) throws InterruptedException{
        ConditionClass condition = new ConditionClass(3);
        int timesToPrint = 1100;
        PrintChar t1 = new PrintChar(1,timesToPrint,condition);
        PrintChar t2 = new PrintChar(2,timesToPrint,condition);
        PrintChar t3 = new PrintChar(3,timesToPrint,condition);
        t2.start();
        t3.start();
        t1.start();
        try{
           t1.join();
           t2.join();
           t3.join();
        }catch (InterruptedException E){
           System.out.println("Blad joina\n");
        }
    }
}
