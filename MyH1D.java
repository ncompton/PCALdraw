package org.jlab.calib;
//made to fix fitting method
//and added a GetMedian() method



import java.util.Arrays;

import org.root.data.DataSetXY;
import org.root.fitter.DataFitter;
import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;



/**
 * Defines the class to create a basic 1D Histogram
 * 
 * @author Erin Kirby
 * @version 062614
 */
public class MyH1D extends H1D {


    public MyH1D() {
    	super();
    }
    
    /**
     * Creates a Histogram1D object using the specified name, axis titles, number of bins, 
     * and minimum and maximum x axis values.
     * 
     * @param hName		the desired name of the 1-D Histogram
     * @param xTitle	the desired x-axis title
     * @param yTitle	the desired y-axis title
     * @param bins		the desired number of bins
     * @param xMin		the desired minimum value on the x axis
     * @param xMax		the desired maximum value on the x axis
     */
    public MyH1D(String hName, String xTitle, String yTitle, int bins, double xMin, double xMax) {    	
    	super(hName, xTitle, yTitle, bins, xMin, xMax);
    }
    
    /**
     * Creates a 1-D Histogram with the specified name, minimum and maximum x-axis values,
     * and the bin heights
     * 
     * @param name			the desired name of the histogram
     * @param xMin			the desired minimum x-axis value
     * @param xMax			the desired maximum x-axis value
     * @param binHeights	a double array of the heights of the bins
     */
    public MyH1D(String name, double xMin, double xMax, double[] binHeights) {
    	super(name, xMin, xMax, binHeights);
    }
    
    /**
     * Creates a 1-D Histogram with the specified name, number of bins, and minimum and maximum
     * x-axis values
     * 
     * @param name		The desired name of the histogram
     * @param bins		The desired number of bins
     * @param xMin		The desired minimum x-axis value
     * @param xMax		The desired maximum x-axis value
     */
    public MyH1D(String name, int bins, double xMin, double xMax) {
    	super(name, bins, xMin, xMax);
    }
    
    /**
     * Creates a 1-D Histogram with the specified name, title, number of bins, and minimum
     * and maximum x-axis values
     * 
     * @param name		The desired name of the histogram
     * @param title		The desired title of the histogram
     * @param bins		The desired number of bins
     * @param xMin		The desired minimum x-axis value
     * @param xMax		The desired maximum x-axis value
     */
    public MyH1D(String name, String title, int bins, double xMin, double xMax) {
    	super( name,  title,  bins,  xMin,  xMax);
    }
    
    /**
     * Creates a new histogram using the name, title, minimum, and maximum.
     * 
     * @param name		The desired name of the histogram
     * @param title		The desired title of the histogram
     * @param xMin		The desired minimum x-axis value
     * @param xMax		The desired maximum x-axis value
     */
    public MyH1D(String name, String title, double xMin, double xMax) {
    	super( name,  title,  xMin,  xMax);
    }


    /**
     * Calculates the median of the data in the histogram
     * 
     * @return		the median of the histogram data as a double
     */
    public double getMedian() {
        //round every bincontent to the nearest integer
        //add those integers to get an array size
        double bincontent = 0.0;
        int    count = 0;
        int i;
        for(i = 0; i < this.getAxis().getNBins(); i++){
            bincontent =  this.getBinContent(i);
            if(bincontent!=0){
                count += (int)(bincontent + 0.5);
            }
        }
        
        double median  = 0.0;
        double numArray[] = new double[count];
        int currentindex = 0;
        int j = 0;
        for(i = 0; i < this.getAxis().getNBins(); i++){
            bincontent =  this.getBinContent(i);
            if(bincontent > 0.5){
                //reject any leftovers under 0.5
                for(j = 0; j + 0.5 < bincontent; ++j){
                    numArray[currentindex] = this.getAxis().getBinCenter(i);
                    ++currentindex;
                }
            }
        }
        
        Arrays.sort(numArray);
        if(numArray.length % 2 == 0)
            median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
        else
            median = (double) numArray[numArray.length/2];
        
        return median;
    }
    
    @Override
    public void fit(F1D func){
        this.fit(func, "*");
    }
    
    @Override
    public void fit(F1D func, String options){
         MyH1D fithist = histClone("fithist");
         //MyH1D fithist = this;
         double[] x = fithist.getAxis().getBinCenters();
         double[] y = fithist.getData();
         int datasize = (int)fithist.getDataSize();
         double[] ye = new double[datasize];
         int counter;
         
         for(int i = 0; i < datasize; ++i)
         {
        	 ye[i] = Math.sqrt(y[i]);
         }

        //ignore points with ye = 0, unless option "0" is used
        if(options.contains("0")==false){
            counter = 0;
            for(int i = 0; i < datasize; ++i){
                if(ye[i] != 0.0){
                    x[counter] = x[i];
                    y[counter] = y[i];
                    ye[counter] = ye[i];
                    ++counter;
                }
            }
            datasize = counter;
        }

        //use function range when fitting
        if(options.contains("R")==true){
            counter = 0;
            for(int i = 0; i < datasize; ++i){
                if(x[i] > func.getMin() && x[i] < func.getMax()){
                    x[counter] = x[i];
                    y[counter] = y[i];
                    ye[counter] = ye[i];
                    ++counter;
                }
            }
            datasize = counter;
        }

        //create final dataset
        double[] xfinal = new double[datasize];
        double[] xefinal = new double[datasize];
        double[] yfinal = new double[datasize];
        double[] yefinal = new double[datasize];
        for(int i = 0; i < datasize; ++i){
            xfinal[i] = x[i];
            xefinal[i] = 0.0;
            yfinal[i] = y[i];
            yefinal[i] = ye[i];
        }
        //DataSetXY finaldataset = new DataSetXY(xfinal, yfinal, xefinal, yefinal);
        GraphErrors finaldataset = new GraphErrors(xfinal, yfinal, xefinal, yefinal);
        
        //DataFitter.fit(finaldataset, func);
        finaldataset.fit(func,options);
        //if(options.contains("Q")==false){
        //    func.show();
        //}
    }
    
    /**
     * returns a copy of the histogram with different name.
     * @param name
     * @return 
     */
    public MyH1D histClone(String name){
    	MyH1D hclone = new MyH1D(name, this.getXTitle(), this.getYTitle(),
        this.getXaxis().getNBins(),this.getXaxis().min(),this.getXaxis().max());
        for(int loop = 0; loop < this.getXaxis().getNBins(); loop++){
            hclone.setBinContent(loop, this.getBinContent(loop));
            hclone.setBinError(loop, this.getBinError(loop));
        }
        return hclone;
    }
   
}

