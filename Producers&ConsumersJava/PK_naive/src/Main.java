public class Main {
    public static void main(String[] args){
        for (int k = 1000; k > 0;k /=10){//liczba kosumentow i producentow
            int numberOfProducers = k;
            int numberOfConsumers = k;
            for (int l = 100000; l > 999; l/=10){//wielkosc buffora
                int m = l;
                for(int o = 0;o<2;o++){
                    boolean typeOfRand = (o==0);
                    Consumer consumers[] = new Consumer[numberOfConsumers];
                    Producer producers[] = new Producer[numberOfProducers];
                    Buffer buffer = new Buffer(m);
                    Results Results = new Results(numberOfProducers,typeOfRand,m);

                    for (int i=0;i<numberOfConsumers;i++){
                        consumers[i] = new Consumer(i,m,typeOfRand,buffer,Results);
                    }
                    for (int i=0;i<numberOfProducers;i++){
                        producers[i] = new Producer(i,m,typeOfRand,buffer,Results);
                    }
                    System.out.printf( "Rzpoczynam próbe m:'%d' PC:'%d' randtybe:'%b'",m,numberOfConsumers,typeOfRand );
                    System.out.println("Stworzylem");
                    for(int i =0; i<numberOfProducers;i++){
                        producers[i].start();
                    }
                    for(int i =0; i<numberOfConsumers;i++){
                        consumers[i].start();
                    }
                    System.out.println("Wystartowałem");
                    for(int i =0; i<numberOfConsumers;i++){
                        try{
                            consumers[i].join(800);
                        }catch (InterruptedException e){
                            e.printStackTrace();//I'm not doing it!
                        }
                    }
                    for(int i =0; i<numberOfProducers;i++){
                        try{
                            producers[i].join(800);
                        }catch (InterruptedException e){
                            e.printStackTrace();//I'm not doing it!
                        }
                    }
                    System.out.println("Wszystkie watki skonczyly,obrabiam wyniki");
                    Results.proceedResults();
                    System.out.println("Skonczylem obrabiac bede rysowac");
                    Results.printChart();
                    return;
                }
            }
        }
    }
}