import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class MandelbrotTask implements Callable<List<PixelResult>> {
    //wykonuje od <start,end)....
    int startWidth;
    int endWidth;

    int height;
    double ZOOM;
    int MAXITER;

    public MandelbrotTask(int start, int end, int height, double zoom, int max_iter) {
        this.startWidth = start;
        this.endWidth = end;
        this.height = height;
        this.ZOOM = zoom;
        this.MAXITER = max_iter;
    }


    public List<PixelResult> call() {
        List<PixelResult> results = new LinkedList<PixelResult>();
        for (int y = 0; y < height; y++) {
            for (int x = startWidth; x < endWidth; x++) {
                double zx = 0;
                double zy = 0;
                double cX = (x - 400) / ZOOM;
                double cY = (y - 300) / ZOOM;
                int iter = MAXITER;
                double tmp;
                while (zx * zx + zy * zy < 4 && iter > 0) {
                    tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    iter--;
                }
                results.add(new PixelResult(x, y, iter));
            }
        }
        return results;
    }

}