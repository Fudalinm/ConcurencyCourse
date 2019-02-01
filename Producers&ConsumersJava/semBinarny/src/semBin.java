public class semBin {

    private boolean signal; //
    public semBin(boolean f){
        signal = f;
    }

    //jeśli semafor jest podniesiony, opuść go. Jeśli jest już opuszczony, wstrzymaj działanie procesu wykonującego tę operację.
    public synchronized void release(){//opuszczenie semafora
        this.signal = true;
        this.notify();
    }

    //jeśli są procesy wstrzymane w wyniku opuszczania semafora S, to wznów jeden z nich, w przeciwnym razie podnieś semafor.
    public synchronized void take(){//podniesienie semafora
        while(!this.signal) try{wait();}catch (InterruptedException e){e.printStackTrace();}
        this.signal = false;
    }

}
