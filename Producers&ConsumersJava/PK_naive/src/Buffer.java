import java.util.concurrent.locks.*;
public class Buffer {
    private final Lock lock = new ReentrantLock();
    private final Condition ProducersQueue = lock.newCondition();
    private final Condition ConsumersQueue = lock.newCondition();

    int m;
    int container[];
    int pointer;

    public Buffer(int nOE){
        m = nOE;
        container = new int[2*m];
        pointer = 0;
    }

    public void produce(){
        container[pointer] = 1;
        pointer ++;
    }

    public void consume(){
        pointer --;
        container[pointer] = 0;
    }

    public int numberOfElements(){
        return (pointer);
    }

    public long signToProducersQueue (int timesToProduce){
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        lock.lock();
        try{
            while(timesToProduce > 2*m - this.numberOfElements()) {//czekamy az bedzie wystarczajaca licza elementow
                ConsumersQueue.signal(); //to remove
                ProducersQueue.await();
            }
            endTime = System.nanoTime();
            for(int i =0;i<timesToProduce;i++){
                this.produce();
            }
            ConsumersQueue.signal();
        }catch (InterruptedException e){
            e.printStackTrace();//i'm not doing it
        }finally {
            lock.unlock();

        }
        return endTime-startTime;
    }

    public long signToConsumersQueue(int timesToConsume){
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        lock.lock();
        try{
            while(timesToConsume > this.numberOfElements()) {//czekamy az bedzie wystarczajaca licza elementow
                ProducersQueue.signal();//to remove
                ConsumersQueue.await();
            }
            endTime = System.nanoTime();
            for(int i =0;i<timesToConsume;i++){
                this.consume();
            }
            ProducersQueue.signal();
        }catch (InterruptedException e){
            e.printStackTrace();//i'm not doing it
        }finally {
            lock.unlock();
        }
        return endTime-startTime;
    }
}