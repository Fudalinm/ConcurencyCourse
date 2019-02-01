import java.io.*;
import java.util.LinkedList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;


public class GraphPrinter extends ApplicationFrame {
    public GraphPrinter(String applicationTitle, LinkedList<Results.ResultType2> consumersProcceededResults){
        super(applicationTitle);
        final XYSeries consumerSeries = new XYSeries("Time spent on waiting");//jakos nazwac ta
        for(Results.ResultType2 x : consumersProcceededResults){
            consumerSeries.add((double) x.numberToProceed,(double) x.timeSpent);
        }
        final XYSeriesCollection data = new XYSeriesCollection(consumerSeries);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Zależność czasowa od liczby elementow",
                "Ilosc elementów",
                "Czas oczekiwania",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1400, 700));
        setContentPane(chartPanel);
        //
        final XYPlot plot = chart.getXYPlot();
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0,true);
        plot.setRenderer(0,renderer);
        //
        String filepath = applicationTitle.replace(' ','_').replace(':','_') + ".png";
        File file = new File(filepath);
        try{
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            ChartUtilities.writeChartAsPNG(out,chart,1400,700);
            out.close();
        }catch (IOException e){
            System.out.println(filepath);
            e.printStackTrace();
        }
    }
}
