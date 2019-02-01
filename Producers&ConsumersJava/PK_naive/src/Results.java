import org.jfree.ui.RefineryUtilities;
import java.util.LinkedList;

public class Results{
    private int numberOfProducersAndConsumers;
    private Result[] Producers;
    private Result[] Consumers;

    private boolean randType;
    private int bufferSize;

    private LinkedList<ResultType2> consumersProcceededResults;
    private LinkedList<ResultType2> producersProcceededResults;

    public Results(int nOPAC,boolean randType,int bufferSize){
        this.numberOfProducersAndConsumers = nOPAC;

        this.Producers = new Result[numberOfProducersAndConsumers];
        this.Consumers = new Result[numberOfProducersAndConsumers];
        for(int i = 0;i<numberOfProducersAndConsumers;i++){
            Producers[i] = new Result(-1,-1);
            Consumers[i] = new Result(-1,-1);
        }

        this.randType = randType;
        this.bufferSize = bufferSize;

        this.consumersProcceededResults = new LinkedList<ResultType2>();
        this.producersProcceededResults = new LinkedList<ResultType2>();
    }

    public void addResult(char threadType,int threadId,int numberToProceed,long timeSpent){
        if(threadType == 'C'){
            try {
//                Consumers[threadId] = new Result(numberToProceed,timeSpent);
                Consumers[threadId].numberToProceed = numberToProceed;
                Consumers[threadId].timeSpent = timeSpent;
            }catch (NullPointerException e ){
                System.out.println("C: " + threadId + " długość tablicy: " + Consumers.length);
            }
        }else if (threadType == 'P'){
            try{
//                Producers[threadId] = new Result(numberToProceed,timeSpent);
                Producers[threadId].numberToProceed = numberToProceed;
                Producers[threadId].timeSpent = timeSpent;
            }catch (NullPointerException e){
                System.out.println("P: " + threadId + " długość tablicy: " + Producers.length);
            }
        }else{
            System.out.println("Invalid thread type");
        }
    }

    public void proceedResults(){
        for (int i=0;i<numberOfProducersAndConsumers;i++){
            boolean flag = true;//czy wstawiac
            for (ResultType2 x : consumersProcceededResults){
                if(x.numberToProceed == Consumers[i].numberToProceed){
                    flag = false;
                    x.scale += 1;
                    x.timeSpent += Consumers[i].timeSpent;
                    break;
                }
            }
            if (flag) {//oznacza ze nie zapisalismy jeszcze wyniku z m elementami
                if(Consumers[i].numberToProceed != -1){
                    consumersProcceededResults.add(new ResultType2(Consumers[i].numberToProceed,Consumers[i].timeSpent));
                }
            }
        }
        for (int i=0;i<numberOfProducersAndConsumers;i++){
            boolean flag = true;//czy wstawiac
            for (ResultType2 x : producersProcceededResults){
                if(x.numberToProceed == Producers[i].numberToProceed){
                    flag = false;
                    x.scale += 1;
                    x.timeSpent += Producers[i].timeSpent;
                    break;
                }
            }
            if (flag) {//oznacza ze nie zapisalismy jeszcze wyniku z m elementami
                if (Producers[i].numberToProceed != -1) {
                    producersProcceededResults.add(new ResultType2(Producers[i].numberToProceed, Producers[i].timeSpent));
                }
            }
        }
        //teraz nalezy odpowiednio przeskalowac kazdy z wynikow
        for (ResultType2 x : producersProcceededResults){
            x.timeSpent /= x.scale;
        }
        for (ResultType2 x : consumersProcceededResults){
            x.timeSpent /= x.scale;
        }
    }

    public void printChart(){
        //mamy zainicjalizowane nasze listy terz musimy je wyprintowac
        //https://stackoverflow.com/questions/16714738/xy-plotting-with-java
        final GraphPrinter producerGraph = new GraphPrinter(
                 "Producergraph: " + numberOfProducersAndConsumers + " Buffer size: " + bufferSize + " Equal random: " + randType ,
                producersProcceededResults
        );
        final GraphPrinter consumerGraph = new GraphPrinter(
                "ConsumerGraph: " + numberOfProducersAndConsumers + " Buffer size: " + bufferSize + " Equal random: " + randType ,
                consumersProcceededResults
        );

//        producerGraph.pack();RefineryUtilities.centerFrameOnScreen(producerGraph);
//        consumerGraph.pack();RefineryUtilities.centerFrameOnScreen(consumerGraph);
//        producerGraph.setVisible(true); consumerGraph.setVisible(true);
    }
    private class Result{
        int numberToProceed;
        long timeSpent;

        public Result(int n,long timeSpent){
            this.numberToProceed = n;
            this.timeSpent = timeSpent;
        }
    }

    public class ResultType2{
        int numberToProceed;
        long timeSpent;
        int scale;

        public ResultType2(int n,long timeSpent){
            this.numberToProceed = n;
            this.timeSpent = timeSpent;
            this.scale = 1;
        }
    }
}