public class Producer extends Thread {
    private int id;
    private int timesToProduce;
    private int m;
    private boolean bigProbabillityEqualToSmall;
    private Buffer buff;
    public long timeSpentOnWaiting;

    private Results Results;

    public Producer(int i,int n,boolean b,Buffer bu,Results r){
        this.id = i;
        this.m = n;
        this.bigProbabillityEqualToSmall = b;
        this.buff = bu;
        this.Results = r;
    }

    public void run(){
        timesToProduce = Rand.randBetween(bigProbabillityEqualToSmall,m);
        timeSpentOnWaiting = buff.signToProducersQueue(timesToProduce);
        //System.out.println(id +" Producent wylosowałem: " +timesToProduce + " wpisuje czas " + timeSpentOnWaiting);
        Results.addResult('P',id,timesToProduce,timeSpentOnWaiting);
    }
}
