package org.jlab.calib;
//only made to accommodate projections to MyH1D

import org.root.histogram.H2D;

/**
 * Specifies the methods to create a 2D Histogram and operations to fill it and
 * set values to its bins
 * 
 * @author Erin Kirby
 * @version 061714
 */
public class MyH2D extends H2D {
        
	public MyH2D(){
		super();
    }

   
	public MyH2D(String name) {
		super(name);
	}

	/**
	 * Creates a 2D Histogram with the specified parameters.
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
	public MyH2D(String name, int bx, double xmin, double xmax, int by,
			double ymin, double ymax) {
		super(name, bx, xmin, xmax, by,	ymin, ymax);
	}

     public MyH2D(String name, String title, int bx, double xmin, double xmax, int by,
			double ymin, double ymax) {
        super(name, title, bx, xmin, xmax, by, ymin, ymax);
	}

	/**
	 * Creates a projection of the 2D histogram onto the X Axis, adding up all
	 * the y bins for each x bin
	 * 
	 * @return a MyH1D object that is a projection of the Histogram2D
	 *         object onto the x-axis
	 */
	public MyH1D projectionX() {
		String name = "X Projection";
		double xMin = this.getXAxis().min();
		double xMax = this.getXAxis().max();
		int xNum = this.getXAxis().getNBins();
		MyH1D projX = new MyH1D(name, xNum, xMin, xMax);

		double height = 0.0;
		for (int x = 0; x < this.getXAxis().getNBins(); x++) {
			height = 0.0;
			for (int y = 0; y < this.getYAxis().getNBins(); y++) {
				height += this.getBinContent(x, y);
			}
			projX.setBinContent(x, height);
		}

		return projX;
	}

	/**
	 * Creates a projection of the 2D histogram onto the Y Axis, adding up all
	 * the x bins for each y bin
	 * 
	 * @return a MyH1D object that is a projection of the Histogram2D
	 *         object onto the y-axis
	 */
	public MyH1D projectionY() {
		String name = "Y Projection";
		double yMin = this.getYAxis().min();
		double yMax = this.getYAxis().max();
		int yNum = this.getYAxis().getNBins() ;
		MyH1D projY = new MyH1D(name, yNum, yMin, yMax);

		double height = 0.0;
		for (int y = 0; y < this.getYAxis().getNBins(); y++) {
			height = 0.0;
			for (int x = 0; x < this.getXAxis().getNBins(); x++) {
				height += this.getBinContent(x, y);
			}
			projY.setBinContent(y, height);
		}

		return projY;
	}

	/**
	 * Creates a 1-D Histogram slice of the specified y Bin
	 * 
	 * @param xBin		the bin on the y axis to create a slice of
	 * @return 			a slice of the x bins on the specified y bin as a 1-D Histogram
	 */
	public MyH1D sliceX(int xBin) {
		String name = "Slice of " + xBin + " X Bin";
		double xMin = this.getYAxis().min();
		double xMax = this.getYAxis().max();
		int xNum    = this.getYAxis().getNBins();
		MyH1D sliceX = new MyH1D(name, name, xNum, xMin, xMax);

		for (int x = 0; x < xNum; x++) {
			sliceX.setBinContent(x, this.getBinContent(xBin,x));
		}
		return sliceX;
	}

	/**
	 * Creates a 1-D Histogram slice of the specified x Bin
	 * 
	 * @param yBin			the bin on the x axis to create a slice of
	 * @return 				a slice of the y bins on the specified x bin as a 1-D Histogram
	 */
	public MyH1D sliceY(int yBin) {
		String name = "Slice of " + yBin + " Y Bin";
		double xMin = this.getXAxis().min();
		double xMax = this.getXAxis().max();
		int    xNum = this.getXAxis().getNBins();
		MyH1D sliceY = new MyH1D(name, name, xNum, xMin, xMax);

		for (int y = 0; y < xNum; y++) {
			sliceY.setBinContent(y, this.getBinContent(y,yBin));
		}

		return sliceY;
	}

}

