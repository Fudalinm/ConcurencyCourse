public class Consumer extends Thread {
    private int id;
    private int timesToConsume;
    private int m;
    private boolean bigProbabillityEqualToSmall;
    private Buffer buff;
    public long timeSpentOnWaiting;

    private Results Results;

    public Consumer(int i,int n,boolean b,Buffer bu,Results r){
        this.id = i;
        this.m = n;
        this.bigProbabillityEqualToSmall = b;
        this.buff = bu;
        this.Results = r;
    }

    public void run(){
        timesToConsume = Rand.randBetween(bigProbabillityEqualToSmall,m);
        timeSpentOnWaiting = buff.signToConsumersQueue(timesToConsume);
        //System.out.println(id +" Konsument wylosowałem: " + timesToConsume + " wpisuje czas " + timeSpentOnWaiting);
        Results.addResult('C',id,timesToConsume,timeSpentOnWaiting);
    }
}