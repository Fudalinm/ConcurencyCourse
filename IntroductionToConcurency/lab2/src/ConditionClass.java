public class ConditionClass {
    private int threadNumber;
    public int flag;


    public ConditionClass(int threadNumbers){
        flag = 1;
        threadNumber = threadNumbers;
    }
    public void next(){
        flag += 1;
        if(flag == threadNumber + 1){ flag = 1;}
    }
}
