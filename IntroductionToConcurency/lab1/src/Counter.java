public class Counter {
    public int i;

    public Counter(int ix){
        i = ix;
    }

    public /*synchronized*/ void inc(){
        synchronized (this){
            this.i++;
        }
    }
    public /*synchronized*/ void dec(){
        synchronized (this){
            this.i--;
        }
    }

}
