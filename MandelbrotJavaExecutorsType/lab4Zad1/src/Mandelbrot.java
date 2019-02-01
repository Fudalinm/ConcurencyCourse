import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Mandelbrot extends JFrame {
    private final int MAX_ITER = 570;
    private final double ZOOM = 150;
    private BufferedImage I;

    public Mandelbrot(int nOT) {
        super("Mandelbrot Set");
        if(nOT < 1){
            System.out.println("Zly argument\n");
            return;
        }
        setBounds(100, 100, 800, 600);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        I = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        int numberOfThreads = nOT;
        int numberOfTasks = numberOfThreads * 10;
        ///tworzenie executora
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }
    public static void main(String[] args){

        new Mandelbrot(4).setVisible(true);

    }
}
