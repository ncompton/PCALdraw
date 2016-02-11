package org.jlab.calib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.TreeMap;

import org.root.attr.Attributes;
import org.root.base.DataRegion;
import org.root.base.EvioWritableTree;
import org.root.base.IDataSet;
import org.root.histogram.Axis;
//import org.root.histogram.MultiIndex;
import org.root.pad.TGCanvas;

/**
 * Specifies the methods to create a 2D Histogram and operations to fill it and
 * set values to its bins
 * 
 * @author Erin Kirby
 * @version 061714
 */
public class MyH4D implements EvioWritableTree {

	private String hName = "basic6D";
	private Axis xAxis = new Axis();
	private Axis yAxis = new Axis();
	private Axis zAxis = new Axis();
	private Axis aAxis = new Axis();
	private ArrayList<Double> hBuffer;
	private MyMultiIndex offset;
    private Attributes attr = new Attributes(); 
    private Double     maximumBinValue = 0.0;
        
	public MyH4D() {
		offset = new MyMultiIndex(xAxis.getNBins(), yAxis.getNBins(), 
							      zAxis.getNBins(), aAxis.getNBins());
		
		hBuffer = new ArrayList<Double>(offset.getArraySize());
		
        this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
        this.attr.getProperties().setProperty("atitle", "");
    }

    public void setName(String name){ this.hName = name;}
	/**
	 * Creates an empty 3D Histogram with 1 bin x,y, and z axes
	 * 
	 * @param name
	 *            the desired name of the 3D Histogram
	 */
	public MyH4D(String name) {
		hName = name;
		offset = new MyMultiIndex(xAxis.getNBins(), yAxis.getNBins(), 
				                  zAxis.getNBins(), aAxis.getNBins());
		hBuffer = new ArrayList<Double>(offset.getArraySize());
		
		this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
        this.attr.getProperties().setProperty("atitle", "");
	}

	/**
	 * Creates a 3D Histogram with the specified parameters.
	 * 
	 * @param name
	 *            the name of the histogram
	 * @param bx
	 *            the number of x axis bins
	 * @param xmin
	 *            the minimum x axis value
	 * @param xmax
	 *            the maximum x axis value
	 * @param by
	 *            the number of y axis bins
	 * @param ymin
	 *            the minimum y axis value
	 * @param ymax
	 *            the maximum y axis value
	 */
	public MyH4D(String name, int bx, double xmin, double xmax, 
			                  int by, double ymin, double ymax,
			                  int bz, double zmin, double zmax,
			                  int ba, double amin, double amax) {
		hName = name;
		this.set(bx, xmin, xmax, by, ymin, ymax, 
				 bz, zmin, zmax, ba, amin, amax);
		//offset = new MyMultiIndex(bx, by, bz, ba, bb, bc);
		//hBuffer = new ArrayList<Double>(offset.getArraySize());
		
		this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
        this.attr.getProperties().setProperty("atitle", "");
      	
      	//System.out.println("Offset size: "+ offset.getArraySize());
        //System.out.println("Array size: "+ hBuffer.size());
	}

    public MyH4D(String name, String title, int bx, double xmin, double xmax, 
    									    int by, double ymin, double ymax,
    									    int bz, double zmin, double zmax,
    						                int ba, double amin, double amax) {
                
		hName = name;
        this.setTitle(title);
        this.set(bx, xmin, xmax, by, ymin, ymax, 
        		 bz, zmin, zmax, ba, amin, amax);
        
		//offset = new MyMultiIndex(bx, by, bz, ba);
		//hBuffer = new ArrayList<Double>(offset.getArraySize());
        
        this.attr.getProperties().setProperty("title", "");
        this.attr.getProperties().setProperty("xtitle", "");
        this.attr.getProperties().setProperty("ytitle", "");
        this.attr.getProperties().setProperty("ztitle", "");
        this.attr.getProperties().setProperty("atitle", "");
                
        //System.out.println("Offset size: "+ offset.getArraySize());
        //System.out.println("Array size: "+ hBuffer.size());
	}
	/**
	 * Sets the bins to the x and y axes and creates the buffer of the histogram
	 * 
	 * @param bx
	 *            number of bins on the x axis
	 * @param xmin
	 *            the minimum value on the x axis
	 * @param xmax
	 *            the maximum value on the x axis
	 * @param by
	 *            number of bins on the y axis
	 * @param ymin
	 *            the minimum value on the y axis
	 * @param ymax
	 *            the maximum value on the y axis
	 */
	public final void set(int bx, double xmin, double xmax, 
			              int by, double ymin, double ymax,
			              int bz, double zmin, double zmax,
			              int ba, double amin, double amax) {
		xAxis.set(bx, xmin, xmax);
		yAxis.set(by, ymin, ymax);
		zAxis.set(bz, zmin, zmax);
		aAxis.set(ba, amin, amax);

		offset = new MyMultiIndex(bx, by, bz, ba);
		int buff = offset.getArraySize();
		
		hBuffer = new ArrayList<Double>(buff);
		int divisor = 10000000;
		int buffnum = (int)(buff / divisor);
		for(int i =0; i < buffnum; ++i)
		{
			hBuffer.addAll(Collections.nCopies(divisor, 0.0));
		}
		hBuffer.addAll(Collections.nCopies(buff - (buffnum * divisor), 0.0));
		
		//hBuffer = new ArrayList<Double>(Collections.nCopies(buff, 0.0));
		//for(int i = 0; i < buff; ++i)
		//{
     	//	hBuffer.add(0.0);
		//}
	}

	/**
	 * 
	 * @return the name of the Histogram
	 */
	public String getName() {
		return hName;
	}

	/**
	 * 
	 * @return the x-axis of the 3D Histogram
	 */
	public Axis getXAxis() {
		return xAxis;
	}

	/**
	 * 
	 * @return the y-axis of the 3D Histogram
	 */
	public Axis getYAxis() {
		return yAxis;
	}
	
	/**
	 * 
	 * @return the z-axis of the 3D Histogram
	 */
	public Axis getZAxis() {
		return zAxis;
	}
	
	/**
	 * 
	 * @return the z-axis of the 3D Histogram
	 */
	public Axis getAAxis() {
		return aAxis;
	}
	
	


    public double getMaximum(){
    	double maximum = 0.0;
    	ListIterator<Double> litr = null;
		litr=hBuffer.listIterator();
     	while(litr.hasNext())
     		if(litr.next()>maximum) maximum = litr.next();
     	return maximum;
    }

	/**
	 * Checks if that bin is valid (exists)
	 * 
	 * @param bx
	 *            The x coordinate of the bin
	 * @param by
	 *            The y coordinate of the bin
	 * @param bz
	 *            The z coordinate of the bin
	 * @return The truth value of the validity of that bin
	 */
	private boolean isValidBins(int bx, int by, int bz, int ba) {
		if ((bx >= 0) && (bx < xAxis.getNBins()) 
		 && (by >= 0) && (by < yAxis.getNBins())
		 && (bz >= 0) && (bz < zAxis.getNBins())
		 && (ba >= 0) && (ba < aAxis.getNBins())){
			return true;
		}
		return false;
	}

	/**
	 * Finds the bin content at that bin
	 * 
	 * @param bx
	 *            The x coordinate of the bin
	 * @param by
	 *            The y coordinate of the bin
	 * @return The content at that bin
	 */
	public double getBinContent(int bx, int by, int bz, int ba) {
		if (this.isValidBins(bx, by, bz, ba)) {
			int buff = offset.getArrayIndex(bx, by, bz, ba);
            if(buff>=0 && buff< hBuffer.size())
            {
            	return hBuffer.get(buff);
            }
            else 
            {
            	System.out.println("[Index] error for binx = "+ bx +
                                    " biny = " + by +
                                    " binz = " + bz +
                                    " bina = " + ba);
            }
		}
		return 0.0;
	}
	
	
	/**
	* Sets the x-axis title to the specified parameter
	* @param xTitle		The desired title of the x-axis
	*/
	public final void setXTitle(String xTitle) {
		//this.getXaxis().setTitle(xTitle);
		this.attr.getProperties().setProperty("xtitle", xTitle);
	}
        
	/**
	* Sets the y-axis title to the specified parameter
	* 
	* @param yTitle		The desired title of the y-axis
	*/
	public final void setYTitle(String yTitle) {
		//this.getYaxis().setTitle(yTitle);
		this.attr.getProperties().setProperty("ytitle", yTitle);
	}
	
	/**
	* Sets the z-axis title to the specified parameter
	* 
	* @param zTitle		The desired title of the z-axis
	*/
	public final void setZTitle(String zTitle) {
		//this.getYaxis().setTitle(zTitle);
		this.attr.getProperties().setProperty("ztitle", zTitle);
	}
	
	/**
	* Sets the z-axis title to the specified parameter
	* 
	* @param zTitle		The desired title of the z-axis
	*/
	public final void setATitle(String aTitle) {
		//this.getYaxis().setTitle(aTitle);
		this.attr.getProperties().setProperty("atitle", aTitle);
	}
	
        
	/**
	* The getter for the histogram title.
	* @return Title of the histogram.
	*/
	public String getTitle(){
		//return this.histTitle;
		return this.attr.getProperties().getProperty("title","");
	}
	
	/**
	* The getter for the x-axis title.
	* 
	* @return		The title of the x-axis as a string
	*/
	public String getXTitle() {
		return this.attr.getProperties().getProperty("xtitle", "");
		//return this.getXaxis().getTitle();
	}
        
	/**
	* The getter for the y-axis title.
	* 
	* @return		The title of the y-axis as a string
	*/
	public String getYTitle() {
		return this.attr.getProperties().getProperty("ytitle", "");
		//return this.getYaxis().getTitle();
	}
	
	/**
	* The getter for the z-axis title.
	* 
	* @return		The title of the z-axis as a string
	*/
	public String getZTitle() {
		return this.attr.getProperties().getProperty("ztitle", "");
		//return this.getZaxis().getTitle();
	}
	
	/**
	* The getter for the z-axis title.
	* 
	* @return		The title of the z-axis as a string
	*/
	public String getATitle() {
		return this.attr.getProperties().getProperty("atitle", "");
		//return this.getAaxis().getTitle();
	}
        
	/**
	* Sets the specified parameter as the title of the histogram
	* 
	* @param title		The desired title of the histogram
	*/
	public final void setTitle(String title) {
		//histTitle = title;
		this.attr.getProperties().setProperty("title", title);
	}
        
	/**
	* Sets the bin to that value
	* 
	* @param bx
	*            The x coordinate of the bin
	* @param by
	*            The y coordinate of the bin
	* @param bz
	*            The z coordinate of the bin
	* @param w
	*            The desired value to set the bin to
	*/
	public void setBinContent(int bx, int by, int bz, int ba, double w) {
		if (this.isValidBins(bx, by, bz, ba)) {
			int buff = offset.getArrayIndex(bx, by, bz, ba);
			hBuffer.set(buff, w);
		}
	}

	/**
	 * Adds 1.0 to the bin with that value
	 * 
	 * @param x
	 *            the x coordinate value
	 * @param y
	 *            the y coordinate value
	 * @param z
	 *            the z coordinate value
	 */
	public void fill(double x, double y, double z, double a) {
		int bin = this.findBin(x, y, z, a);
		if (bin >= 0)
			this.addBinContent(bin);
	}

	public void fill(double x, double y, double z, double a, double w) {
		int bin = this.findBin(x, y, z, a);
		if (bin >= 0) {
			this.addBinContent(bin, w);
		}
	}

	/**
	 * Increments the current bin by 1.0
	 * 
	 * @param bin
	 *            the bin in array indexing format to increment
	 */
	private void addBinContent(int bin) {
		hBuffer.set(bin,hBuffer.get(bin) + 1.0);
                if(hBuffer.get(bin)>this.maximumBinValue) 
                    this.maximumBinValue = hBuffer.get(bin);
	}

	/**
	 * Increments the bin with that value by that weight
	 * 
	 * @param bin
	 *            the bin to add the content to, in array indexing format
	 * @param w
	 *            the value to add to the bin content
	 */
	private void addBinContent(int bin, double w) {
		hBuffer.set(bin, hBuffer.get(bin) + w);
                if(hBuffer.get(bin)>this.maximumBinValue) 
                    this.maximumBinValue = hBuffer.get(bin);
	}
       /*
        public ArrayList<H1D>  getSlicesX(){
            ArrayList<H1D>  slices = new ArrayList<H1D>();
            for(int loop = 0; loop < this.getXAxis().getNBins(); loop++){
                H1D slice = this.sliceX(loop);
                slice.setName(this.getName()+"_"+loop);
                slices.add(slice);
            }
            return slices;
        }
        
        public ArrayList<H1D>  getSlicesY(){
            ArrayList<H1D>  slices = new ArrayList<H1D>();
            for(int loop = 0; loop < this.getYAxis().getNBins(); loop++){
                H1D slice = this.sliceY(loop);
                slice.setName(this.getName()+"_"+loop);
                slices.add(slice);
            }
            return slices;
        }
        
        public void add(H2D h){
            if(h.getXAxis().getNBins()==this.getXAxis().getNBins()&&
                    h.getYAxis().getNBins()==this.getYAxis().getNBins()){
                for(int loop = 0; loop < this.hBuffer.length; loop++){
                    this.hBuffer[loop] = this.hBuffer[loop] + h.hBuffer[loop];
                }
            } else {
                System.out.println("[warning] ---> error adding histograms " 
                        + this.getName() + "  " + h.getName()
                        + ". inconsistent bin numbers");
            }
        }
        
        
        public static H2D  divide(H2D h1, H2D h2){
            if((h1.getXAxis().getNBins()!=h2.getXAxis().getNBins())||
                    (h1.getYAxis().getNBins()!=h2.getYAxis().getNBins())
                    ){
                System.out.println("[H2D::divide] error : histograms have inconsistent bins");
                return null;
            }
            
            H2D h2div = new H2D(h1.getName()+"_DIV",
                    h1.getXAxis().getNBins(),h1.getXAxis().min(),h1.getXAxis().max(),
                    h1.getYAxis().getNBins(),h1.getYAxis().min(),h1.getYAxis().max()                    
            );
            for(int bx = 0; bx < h1.getXAxis().getNBins();bx++){
                for(int by = 0; by < h1.getYAxis().getNBins();by++){
                    double bc = 0;
                    if(h2.getBinContent(bx, by)!=0){
                        h2div.setBinContent(bx, by, h1.getBinContent(bx, by)/h2.getBinContent(bx, by));
                    }
                }    
            }
            return h2div;
        }
        
        public void divide(H2D h){
            if(h.getXAxis().getNBins()==this.getXAxis().getNBins()&&
                    h.getYAxis().getNBins()==this.getYAxis().getNBins()){
                for(int loop = 0; loop < this.hBuffer.length; loop++){
                    if(h.hBuffer[loop]==0){
                        this.hBuffer[loop] = 0.0;
                    } else {
                        this.hBuffer[loop] = this.hBuffer[loop]/h.hBuffer[loop];
                    }
                }
            } else {
                System.err.println("[H2D::divide] error the bins in 2d histogram do not match");
            }
        }
        */
	/**
	 * Finds which bin has that value.
	 * 
	 * @param x
	 *            The x value to search for
	 * @param y
	 *            The y value to search for
	 * @return The bin, in array indexing format, which holds that x-y value
	 */
	public int findBin(double x, double y, double z, double a) {
		int bx = xAxis.getBin(x);
		int by = yAxis.getBin(y);
		int bz = zAxis.getBin(z);
		int ba = aAxis.getBin(a);
		if (this.isValidBins(bx, by, bz, ba)) {
			return (offset.getArrayIndex(bx, by, bz, ba));
		}
		//else
		//{
		//	System.err.println("Invalid Bin");
		//}
		return -1;
	}


	/**
	 * Specifies the region in the 2D histogram with those attributes
	 * 
	 * @param name
	 *            The name of the histogram
	 * @param bx_start
	 *            The x coordinate beginning
	 * @param bx_end
	 *            The x coordinate end
	 * @param by_start
	 *            The y coordinate beginning
	 * @param by_end
	 *            The y coordinate end
	 * @return A 2D histogram with the entered specifications
	 */
	/*
	public H2D getRegion(String name, int bx_start, int bx_end,
			int by_start, int by_end) {
		double xBinWidth = xAxis.getBinWidth(bx_start);
		double newXMin = xAxis.min() + (xBinWidth * bx_start);
		double newXMax = xAxis.min() + (xBinWidth * bx_end);

		double yBinWidth = yAxis.getBinWidth(by_start);
		double newYMin = yAxis.min() + (yBinWidth * by_start);
		double newYMax = yAxis.min() + (yBinWidth * by_end);
		H2D regHist = new H2D(name, bx_end - bx_start, newXMin,
				newXMax, by_end - by_start, newYMin, newYMax);

		double content = 0.0;
		for (int y = by_start; y < by_end; y++) {
			for (int x = bx_start; x < bx_end; x++) {
				content = this.getBinContent(x, y);
				regHist.setBinContent(x, y, content);
			}
		}
		return regHist;
	}
        */
	
	
	/*
        public H2D histClone(String name){
            H2D hclone = new H2D(name,
                    this.xAxis.getNBins(),this.xAxis.min(),this.xAxis.max(),
                    this.yAxis.getNBins(),this.yAxis.min(),this.yAxis.max()
            );
            for(int loop = 0; loop < this.hBuffer.length; loop++){                
                hclone.hBuffer[loop] = this.hBuffer[loop];
            }
            return hclone;
        }
        
        */
	/**
	 * Creates a projection of the 2D histogram onto the X Axis, adding up all
	 * the y bins for each x bin
	 * 
	 * @return a H1D object that is a projection of the Histogram2D
	 *         object onto the x-axis
	 */
	public MyH1D projectionX() {
		String name = "X Projection";
		double xMin = xAxis.min();
		double xMax = xAxis.max();
		int xNum = xAxis.getNBins();
		
		MyH1D projX = new MyH1D(name, xNum, xMin, xMax);

		double height = 0.0;
		for (int x = 0; x < xAxis.getNBins(); x++) {
			height = 0.0;
			for (int y = 0; y < yAxis.getNBins(); y++) {
				for (int z = 0; z < zAxis.getNBins(); z++) {
					for (int a = 0; a < aAxis.getNBins(); a++) {
								height += this.getBinContent(x, y, z, a);
					}
				}
			}
			projX.setBinContent(x, height);
			projX.setBinError(x, Math.sqrt(height));
		}

		return projX;
	}
	
	/**
	 * Creates a projection of the 3D histogram onto the X Axis, adding up all
	 * the y and z bins (in a range, inclusive) for each x bin
	 * 
	 * @return a H1D object that is a projection of the Histogram2D
	 *         object onto the x-axis
	 */
	public MyH1D projectionX(String name, int bymin, int bymax, int bzmin, int bzmax,
			                              int bamin, int bamax) {
		//String name = "X Projection";
		double xMin = xAxis.min();
		double xMax = xAxis.max();
		int xNum = xAxis.getNBins();
		
		if(bymin < 0) bymin = 0;
		if(bymax >= yAxis.getNBins()) bymax = yAxis.getNBins() - 1;
		if(bzmin < 0) bzmin = 0;
		if(bzmax >= zAxis.getNBins()) bzmax = zAxis.getNBins() - 1;
		if(bamin < 0) bamin = 0;
		if(bamax >= aAxis.getNBins()) bamax = aAxis.getNBins() - 1;
		
		MyH1D projX = new MyH1D(name, xNum, xMin, xMax);

		double height = 0.0;
		for (int x = 0; x < xAxis.getNBins(); x++) {
			height = 0.0;
			for (int y = bymin; y <= bymax; y++) {
				for (int z = bzmin; z <= bzmax; z++) {
					for (int a = bamin; a <= bamax; a++) {
						height += this.getBinContent(x, y, z, a);
					}
				}
			}
			projX.setBinContent(x, height);
			projX.setBinError(x, Math.sqrt(height));
		}

		return projX;
	}

	/**
	 * Creates a projection of the 2D histogram onto the Y Axis, adding up all
	 * the x bins for each y bin
	 * 
	 * @return a H1D object that is a projection of the Histogram2D
	 *         object onto the y-axis
	 */
	/*
	public MyH1D projectionY() {
		String name = "Y Projection";
		double yMin = yAxis.min();
		double yMax = yAxis.max();
		int yNum = yAxis.getNBins();
		MyH1D projY = new MyH1D(name, yNum, yMin, yMax);

		double height = 0.0;
		for (int y = 0; y < yAxis.getNBins(); y++) {
			height = 0.0;
			for (int x = 0; x < xAxis.getNBins(); x++) {
				for (int z = 0; z < zAxis.getNBins(); z++) {
					height += this.getBinContent(x, y, z);
				}
			}
			projY.setBinContent(y, height);
		}

		return projY;
	}
	*/
	
	/**
	 * Creates a projection of the 3D histogram onto the Y Axis, adding up all
	 * the x and z bins (in a range, inclusive) for each x bin
	 * 
	 * @return a H1D object that is a projection of the Histogram3D
	 *         object onto the y-axis
	 */
	/*
	public MyH1D projectionY(String name, int bxmin, int bxmax, int bzmin, int bzmax) {
		//String name = "X Projection";
		double yMin = yAxis.min();
		double yMax = yAxis.max();
		int yNum = yAxis.getNBins();
		
		if(bxmin < 0) bxmin = 0;
		if(bxmax >= xAxis.getNBins()) bxmax = xAxis.getNBins() - 1;
		if(bzmin < 0) bzmin = 0;
		if(bzmax >= zAxis.getNBins()) bzmax = zAxis.getNBins() - 1;
		
		MyH1D projY = new MyH1D(name, yNum, yMin, yMax);

		double height = 0.0;
		for (int y = 0; y < yAxis.getNBins(); y++) {
			height = 0.0;
			for (int x = bxmin; x <= bxmax; x++) {
				for (int z = bzmin; z <= bzmax; z++) {
					height += this.getBinContent(x, y, z);
				}
			}
			projY.setBinContent(y, height);
		}

		return projY;
	}
	*/

	/**
	 * Creates a 1-D Histogram slice of the specified y Bin
	 * 
	 * @param xBin		the bin on the y axis to create a slice of
	 * @return 			a slice of the x bins on the specified y bin as a 1-D Histogram
	 */
	/*
	public H1D sliceX(int xBin) {
		String name = "Slice of " + xBin + " X Bin";
		double xMin = yAxis.min();
		double xMax = yAxis.max();
		int xNum    = yAxis.getNBins();
		H1D sliceX = new H1D(name, name, xNum, xMin, xMax);

		for (int x = 0; x < xNum; x++) {
			sliceX.setBinContent(x, this.getBinContent(xBin,x));
		}
		return sliceX;
	}
	*/

	/**
	 * Creates a 1-D Histogram slice of the specified x Bin
	 * 
	 * @param yBin			the bin on the x axis to create a slice of
	 * @return 				a slice of the y bins on the specified x bin as a 1-D Histogram
	 */
	/*
	public H1D sliceY(int yBin) {
		String name = "Slice of " + yBin + " Y Bin";
		double xMin = xAxis.min();
		double xMax = xAxis.max();
		int    xNum = xAxis.getNBins();
		H1D sliceY = new H1D(name, name, xNum, xMin, xMax);

		for (int y = 0; y < xNum; y++) {
			sliceY.setBinContent(y, this.getBinContent(y,yBin));
		}

		return sliceY;
	}
*/

	
	/**
	* Resets the content of the histogram, sets all bin contents to 0
	*/
	public void reset(){
		for(int bin = 0; bin < this.hBuffer.size(); bin++){
			this.hBuffer.set(bin,0.0);
		}
	}
        
    @Override
    public TreeMap<Integer, Object> toTreeMap() {
        TreeMap<Integer, Object> hcontainer = new TreeMap<Integer, Object>();
        hcontainer.put(1, new int[]{4});     
        byte[] nameBytes = this.hName.getBytes();
        hcontainer.put(2, nameBytes);
        hcontainer.put(3, new int[]{this.getXAxis().getNBins(),this.getYAxis().getNBins(),this.getZAxis().getNBins(),this.getAAxis().getNBins()});
        hcontainer.put(4, new double[]{
            this.getXAxis().min(),this.getXAxis().max(),
            this.getYAxis().min(),this.getYAxis().max(),
            this.getZAxis().min(),this.getZAxis().max(),
            this.getAAxis().min(),this.getAAxis().max(),
        });
        hcontainer.put(5, this.hBuffer);
        return hcontainer;
    }

    @Override
    public void fromTreeMap(TreeMap<Integer, Object> map) {
        if(map.get(1) instanceof int[]){
            if(  ((int[]) map.get(1))[0]==3){
                int[]    nbins      = ((int[]) map.get(3));
                double[] binsrange  = ((double[]) map.get(4));
                byte[] name     = (byte[]) map.get(2);
                hName = new String(name);                
                this.set(nbins[0], binsrange[0],binsrange[1],
                         nbins[1], binsrange[2],binsrange[3],
                         nbins[2], binsrange[4],binsrange[5],
                         nbins[3], binsrange[6],binsrange[7]);
                
                double[] binc = (double[]) map.get(5);
                //double[] bine = (double[]) map.get(5);
                System.arraycopy(binc, 0, hBuffer, 0, binc.length);
            }
        }
    }

    public Integer getDataSize() {
        return this.xAxis.getNBins()*this.yAxis.getNBins()*this.zAxis.getNBins();
    }
    
    public Double getDataX(int index) {
        return 1.0;
    }

    public Double getDataY(int index) {
        return 1.0;
    }

    public Double getErrorX(int index) {
        return 1.0;

    }

    public Double getErrorY(int index) {
        return 1.0;
    }

    public Attributes getAttributes() {
        return this.attr;
    }

    public Double getData(int x, int y, int z, int a) {
        return this.getBinContent(x, y, z, a);
    }

    public Integer getDataSize(int axis) {
        if(axis==0) return this.getXAxis().getNBins();
        if(axis==1) return this.getYAxis().getNBins();
        if(axis==2) return this.getZAxis().getNBins();
        if(axis==3) return this.getAAxis().getNBins();
        return 0;
    }
	
	public static void main(String[] args){ 
		MyH4D testh4 = new MyH4D("test1", 3, 0.0, 300.0,
					                      68, 0.5, 68.5,
					                      62, 0.5, 62.5,
				                          62, 0.5, 62.5);
		
		
		testh4.fill(50, 1, 1, 1);
		testh4.fill(50, 2, 2, 2);
		testh4.fill(125.0, 3, 3, 3);
		
		testh4.fill(225, 4, 4, 4);
		testh4.fill(225, 5, 5, 5);
		
		//System.out.println("bin: " + 0 + " content: " + testh3.getBinContent(0, 0, 0, 0));
		//System.out.println("bin: " + 1 + " content: " + testh3.getZAxis().getBin(1.5));
		TGCanvas testcanv = new TGCanvas();
		MyH1D testh1 = new MyH1D();
		
		testh1 = testh4.projectionX("test",67,67,61,61,61,61).histClone("newtest");
		//testh1.fill(5.0);
		testcanv.draw(testh1);
		
	}
	
}
