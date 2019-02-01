import java.util.concurrent.ThreadLocalRandom;
public class Rand {
    public static int randBetween(boolean bigProbabillityEqualToSmall,int max){
        if(bigProbabillityEqualToSmall){
            return ThreadLocalRandom.current().nextInt(1, max);
        }else{
            int flag = 1;
            int result = 0;
            while (flag > 0){
                result += flag;
                flag = ThreadLocalRandom.current().nextInt(0, 2);
            }
            return result;
        }
    }
}
