import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Mandelbrot extends JFrame {
    private int MAX_ITER = 10000;
    private double ZOOM = 250;
    private BufferedImage I;

    public Mandelbrot(int nOT,ExecutorService e,int it) {
        super("Mandelbrot Set");
        if(nOT < 1){
            System.out.println("Zly argument\n");
            return;
        }
        setBounds(100, 100, 800, 600);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        I = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        //zmiana max iter
        MAX_ITER = it;

        //ile zadan do wykonania
        int numberOfTasks = nOT;

        ///tworzenie executora
        ExecutorService executor = e;

        //zaczynamy mierzyc tuz przed dodaniem zadan
        long sTime = System.nanoTime();


        ///lista wynikow od taska
        List<Future<List<PixelResult>>> Results = new LinkedList<Future<List<PixelResult>>>();
        //dodajemy zadanie do wykonania
        for (int i = 0; i < numberOfTasks; i++) {
            if( i == (numberOfTasks - 1)){
                Results.add(
                        executor.submit(
                            new MandelbrotTask(
                                    i*(I.getWidth()/numberOfTasks),
                                    getWidth(),
                                    I.getHeight(),
                                    ZOOM,
                                    MAX_ITER
                            )
                        )
                );
            }else{
                Results.add(
                        executor.submit(
                                new MandelbrotTask(
                                        i*(I.getWidth()/numberOfTasks),
                                        (i+1)*(I.getWidth()/numberOfTasks),
                                        I.getHeight(),
                                        ZOOM,
                                        MAX_ITER
                                )
                        )
                );
            }
        }
        ///dodawanie wynikow
        for (Future<List<PixelResult>> x : Results) {
            try {
                List<PixelResult> singleTaskResults = x.get();
                for (PixelResult singleresult : singleTaskResults) {
                    I.setRGB(singleresult.x, singleresult.y, singleresult.iter | (singleresult.iter << 8));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //konczymy mierzyc po obrobieniu wszystkich wynikow
        long eTime = System.nanoTime();
        System.out.printf("Mandelbrot: max_iter: %d , number_of_tasks: %d, time_spent: %d\n",MAX_ITER,numberOfTasks,eTime-sTime);
    }
    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }



    public static void main(String[] args){
        //dla roznych wartosci max iter
        for(int iter = 100; iter < 10001 ; iter *= 10){
            //dla roznych ilosci taskow
            for(int numberOfTasks = 10;numberOfTasks<101;numberOfTasks +=30){
                System.out.println("\n##############");

                //dla roznych typow executorow
                System.out.print("FixedThreadPool");
                ExecutorService e = Executors.newFixedThreadPool(numberOfTasks/10);
                new Mandelbrot(numberOfTasks,e,iter);

                System.out.print("SingleThreadExecutor");
                e = Executors.newSingleThreadExecutor();
                new Mandelbrot(numberOfTasks,e,iter);

                System.out.print("CachedThreadPool");
                e = Executors.newCachedThreadPool();
                new Mandelbrot(numberOfTasks,e,iter);

                System.out.print("WorkStealingPool");
                e = Executors.newWorkStealingPool();
                new Mandelbrot(numberOfTasks,e,iter);

                System.out.println("##############\n");
            }
        }
    }
}
