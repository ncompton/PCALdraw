package org.jlab.calib;

import java.awt.BorderLayout;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import math.geom2d.*;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clasrec.utils.CLASGeometryLoader;
import org.jlab.geom.component.ScintillatorPaddle;
import org.jlab.geom.detector.ec.ECDetector;
import org.jlab.geom.detector.ec.ECLayer;
import org.jlab.geom.prim.Point3D;
import org.root.pad.TEmbeddedCanvas;
import org.root.pad.TGCanvas;

public class PCALDrawDBscratch {
	
	private double length;
	private double angle;
	private double anglewidth;
	private double slightshift;
	
	private double[] xrotation = new double [6];
	private double[] yrotation = new double [6];
	
	private static double[][][][] xPoint = new double [6][3][68][4];
	private static double[][][][] yPoint = new double [6][3][68][4];

	public PCALDrawDBscratch() {
		initVert();
		length = 4.5;
		angle = 62.8941;
		anglewidth = length/Math.sin(Math.toRadians(angle));
		slightshift = length/Math.tan(Math.toRadians(angle));
	}
	
	public PCALDrawDBscratch(double inlength, double inangle) {
		initVert();
		length = inlength;
		angle = inangle;
		anglewidth = length/Math.sin(Math.toRadians(angle));
		slightshift = length/Math.tan(Math.toRadians(angle));
	}
	
	
	
	
	//collects all possible pixels into a DetectorShapeView2D
	public DetectorShapeView2D drawAllPixels(int sector)
	{
		DetectorShapeView2D pixelmap= new DetectorShapeView2D("PCAL Pixels");
            for(int uPaddle = 0; uPaddle < 68; uPaddle++){
            	for(int vPaddle = 0; vPaddle < 62; vPaddle++){
            		for(int wPaddle = 0; wPaddle < 62; wPaddle++){
            			if(isValidPixel(sector, uPaddle, vPaddle, wPaddle))
            				pixelmap.addShape(getPixelShape(sector, uPaddle, vPaddle, wPaddle));
            		}
            	}
            }
            return pixelmap;
	}
	
	
	
	
	
	
	//collects all possible UW intersections into a DetectorShapeView2D
	public DetectorShapeView2D drawUW(int sector)
	{
		DetectorShapeView2D UWmap= new DetectorShapeView2D("PCAL UW");
	    	for(int uPaddle = 0; uPaddle < 68; uPaddle++){
	            for(int wPaddle = 0; wPaddle < 62; wPaddle++){
	            	if(isValidOverlap(sector, "u", uPaddle, "w", wPaddle))
	            		UWmap.addShape(getOverlapShape(sector, "u", uPaddle, "w", wPaddle));
	            }
	          
	    	}
	        return UWmap;
	}
	
	//collects all possible UW intersections into a DetectorShapeView2D
	public DetectorShapeView2D drawWU(int sector)
	{
		DetectorShapeView2D WUmap= new DetectorShapeView2D("PCAL WU");
		   	for(int uPaddle = 0; uPaddle < 68; uPaddle++){
		   		for(int wPaddle = 0; wPaddle < 62; wPaddle++){
		            if(isValidOverlap(sector, "w", wPaddle, "u", uPaddle))
		            	WUmap.addShape(getOverlapShape(sector, "w", wPaddle, "u", uPaddle));
		        }
		          
		   	}
		    return WUmap;
	}
	
	//collects all possible UW intersections into a DetectorShapeView2D
	public DetectorShapeView2D drawVU(int sector)
	{
		DetectorShapeView2D UVmap= new DetectorShapeView2D("PCAL UV");
		   	for(int uPaddle = 0; uPaddle < 68; uPaddle++){
		   		for(int vPaddle = 0; vPaddle < 62; vPaddle++){
		            if(isValidOverlap(sector, "v", vPaddle, "u", uPaddle))
		            	UVmap.addShape(getOverlapShape(sector, "v", vPaddle, "u", uPaddle));
		        }
		          
		   	}
		    return UVmap;
	}
	
	
	
	
	
	//collects all U strips into a DetectorShapeView2D
	public DetectorShapeView2D drawUStrips(int sector)
	{
		DetectorShapeView2D Umap= new DetectorShapeView2D("PCAL U Strips");
		   	for(int uPaddle = 0; uPaddle < 68; uPaddle++){
		   		//System.out.println(uPaddle);
		   		Umap.addShape(getStripShape(sector, "u", uPaddle));
		   	}
		    return Umap;
	}
	
	//collects all V strips into a DetectorShapeView2D
	public DetectorShapeView2D drawVStrips(int sector)
	{
		DetectorShapeView2D Vmap= new DetectorShapeView2D("PCAL V Strips");
		   	for(int vPaddle = 0; vPaddle < 62; vPaddle++){
		   		//System.out.println(vPaddle);
		   		Vmap.addShape(getStripShape(sector, "v", vPaddle));
		   	}
		    return Vmap;
	}

	//collects all W strips into a DetectorShapeView2D
	public DetectorShapeView2D drawWStrips(int sector)
	{
		DetectorShapeView2D Wmap= new DetectorShapeView2D("PCAL W Strips");
		   	for(int wPaddle = 0; wPaddle < 62; wPaddle++){
		   		//System.out.println(wPaddle);
		   		Wmap.addShape(getStripShape(sector, "w", wPaddle));
		   	}
		    return Wmap;
	}
	
	
	
	
	
	//calls getPixelVerticies
	//uses those 3 verticies to make a shape
	public DetectorShape2D getPixelShape(int sector, int uPaddle, int vPaddle, int wPaddle){
		
		Object[] obj = getPixelVerticies(sector, uPaddle, vPaddle, wPaddle);
		int numpoints = (int)obj[0];
		//double[] x = (double[])obj[1];//new double[numpoints];
		//double[] y = (double[])obj[2];//new double[numpoints];
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		//System.out.println("numpoints: " + numpoints);
		System.arraycopy( (double[])obj[1], 0, x, 0, numpoints);
		System.arraycopy( (double[])obj[2], 0, y, 0, numpoints);
		
		/*
        for(int i = 0; i< numpoints; ++i)
        {
        	y[i] -= 400.0;
        }
        */
        //if(numpoints < 2)System.out.println("Didn't work");
      
        	
        
        DetectorShape2D  pixel = new DetectorShape2D(DetectorType.PCAL,sector,2,uPaddle * 10000 + vPaddle * 100 + wPaddle);
    	pixel.getShapePath().clear(); 
        if(numpoints > 2) 
        {
        	for(int i = 0; i < numpoints; ++i){ 
        		pixel.getShapePath().addPoint(x[i],  y[i],  0.0); 
        	} 

        	//pixel.getShapePath().rotateZ(Math.toRadians(sector*60.0));
           
            /*
            if(paddle%2==0){
                shape.setColor(180, 255, 180);
            } else {
                shape.setColor(180, 180, 255);
            }
            */
        }
		return pixel;		
	}

	//calls getOverlapVerticies
	//uses those 3 verticies to make a shape
	public DetectorShape2D getOverlapShape(int sector, String strip1, int paddle1, String strip2, int paddle2){
			
		int uPaddle = -1;
		int vPaddle = -1;
		int wPaddle = -1;
		if((strip1 == "u" || strip1 == "U"))
		{
			uPaddle = paddle1;
		}
		if((strip2 == "u" || strip2 == "U"))
		{
			uPaddle = paddle2;
		}
		if((strip1 == "v" || strip1 == "V"))
		{
			vPaddle = paddle1;
		}
		if((strip2 == "v" || strip2 == "V"))
		{
			vPaddle = paddle2;
		}
		if((strip1 == "w" || strip1 == "W"))
		{
			wPaddle = paddle1;
		}
		if((strip2 == "w" || strip2 == "W"))
		{
			wPaddle = paddle2;
		}
		
		Object[] obj = getOverlapVerticies(sector, strip1, paddle1, strip2, paddle2);
		int numpoints = (int)obj[0];
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		System.arraycopy( (double[])obj[1], 0, x, 0, numpoints);
		System.arraycopy( (double[])obj[2], 0, y, 0, numpoints);
			
		/*
	    for(int i = 0; i< numpoints; ++i)
	    {
	        y[i] -= 400.0;
	    }
	    */
		/*
		System.out.println("Blah!");
		for(int i = 0; i < numpoints; ++i){ 
        	System.out.println("i: " + i + " x: " + x[i] + " y: " + y[i]);
        } 
        */

	      
	        	
		DetectorShape2D  overlapShape;
	    if(uPaddle == paddle1 && wPaddle == paddle2)
	    	overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,3,uPaddle * 100 + wPaddle);
	    else if(vPaddle == paddle1 && uPaddle == paddle2)
    		overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,4,uPaddle * 100 + vPaddle);
	    else if(wPaddle == paddle1 && uPaddle == paddle2)
	    	overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,5,uPaddle * 100 + wPaddle);
	    else
	    	overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,6,vPaddle * 100 + wPaddle);
	    
	    overlapShape.getShapePath().clear(); 
	    if(numpoints > 2) 
	    {
	        for(int i = 0; i < numpoints; ++i){ 
	        	overlapShape.getShapePath().addPoint(x[i],  y[i],  0.0); 
	        	//System.out.println("i: " + i + " x: " + x[i] + " y: " + y[i]);
	        } 

	        //overlapShape.getShapePath().rotateZ(Math.toRadians(sector*60.0));
	           
	         /*
	         if(paddle%2==0){
	             shape.setColor(180, 255, 180);
	         } else {
	             shape.setColor(180, 180, 255);
	         }
	         */
	     }
	    return overlapShape;		
	}
	
	
	//calls getOverlapVerticies
	//uses those 3 verticies to make a shape
	public DetectorShape2D getStripShape(int sector, String strip1, int paddle1){
			
		int uPaddle = -1;
		int vPaddle = -1;
		int wPaddle = -1;
		if((strip1 == "u" || strip1 == "U"))
		{
			uPaddle = paddle1;
		}
		if((strip1 == "v" || strip1 == "V"))
		{
			vPaddle = paddle1;
		}
		if((strip1 == "w" || strip1 == "W"))
		{
			wPaddle = paddle1;
		}

		
		Object[] obj = getStripVerticies(sector, strip1, paddle1);
		int numpoints = (int)obj[0];
		//System.out.println("Strip let: " + strip1 + "Strip num: " + paddle1 + " Numpoints: " + numpoints);
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		System.arraycopy( (double[])obj[1], 0, x, 0, numpoints);
		System.arraycopy( (double[])obj[2], 0, y, 0, numpoints);
			
		/*
	    for(int i = 0; i< numpoints; ++i)
	    {
	        y[i] -= 400.0;
	    }
	    */

	      
	        	
		DetectorShape2D  stripShape;
	    if(uPaddle == paddle1)
	    	stripShape = new DetectorShape2D(DetectorType.PCAL,sector,7,uPaddle);
	    else if(vPaddle == paddle1)
	    	stripShape = new DetectorShape2D(DetectorType.PCAL,sector,8,vPaddle);
	    else if(wPaddle == paddle1)
	    	stripShape = new DetectorShape2D(DetectorType.PCAL,sector,9,wPaddle);
	    else
	    {
	    	stripShape = new DetectorShape2D();
	    	System.out.println("Either " + strip1 + " is not a valid strip letter, or " + paddle1 + " is not a valid number.");
	    }
	    
	    stripShape.getShapePath().clear(); 
	    if(numpoints > 2) 
	    {
	        for(int i = 0; i < numpoints; ++i){ 
	        	stripShape.getShapePath().addPoint(x[i],  y[i],  0.0); 
	        } 
	           
	         /*
	         if(paddle%2==0){
	             shape.setColor(180, 255, 180);
	         } else {
	             shape.setColor(180, 180, 255);
	         }
	         */
	     }
	    return stripShape;		
	}
	
	
	
	
	
	
	//calls getPixelVerticies to check
	//that at least 3 points exist,
	// if so it is marked as true, else false
	public Boolean isValidPixel(int sector, int uPaddle, int vPaddle, int wPaddle){
		Object[] obj = getPixelVerticies(sector, uPaddle, vPaddle, wPaddle);
		int numpoints = (int)obj[0];
		
        if(numpoints > 2) 
        	return true;
        else
        	return false;		
	}
	
	//calls getPixelVerticies to check
	//that at least 3 points exist,
	// if so it is marked as true, else false
	public Boolean isValidOverlap(int sector, String strip1, int paddle1, String strip2, int paddle2){
		Object[] obj = getOverlapVerticies(sector, strip1, paddle1, strip2, paddle2);
		int numpoints = (int)obj[0];
		//System.out.println("Blah!" + numpoints);
		//System.out.println("numpoints: " + numpoints);
        if(numpoints > 2) 
        	return true;
        else
        	return false;		
	}

	
	

	
	
	//returns an Object array of size 3
	//first element is the number of verticies (n) (int)
	//second element is an array x-coordinates (double[]) of size n
	//third element is an array y-coordinates (double[]) of size n
	//                                     0-5         0-67         0-61          0-61
	public Object[] getPixelVerticies(int sector, int uPaddle, int vPaddle, int wPaddle){
		
		//if(isValidOverlap(sector, "u", uPaddle,"w",wPaddle) && isValidOverlap(sector, "u", uPaddle,"v",wPaddle) && isValidOverlap(sector, "v", uPaddle,"w",wPaddle))
		//{
			Object[] obj = getVerticies(getOverlapShape(sector, "u", uPaddle,"w",wPaddle),getStripShape(sector,"v",vPaddle));
			
			int numpoints = (int)obj[0];
			//System.out.println("Strip let: " + strip1 + "Strip num: " + paddle1 + " Numpoints: " + numpoints);
			double[] x = new double[numpoints];
			double[] y = new double[numpoints];
			System.arraycopy( (double[])obj[1], 0, x, 0, numpoints);
			System.arraycopy( (double[])obj[2], 0, y, 0, numpoints);
			return(new Object[]{numpoints, x, y});
	/*
			Object[] obj2 = sortVerticies(numpoints, x, y);
			
			int nPoints = (int)obj2[0];
			//System.out.println("Strip let: " + strip1 + "Strip num: " + paddle1 + " Numpoints: " + numpoints);
			double[] xnew = new double[nPoints];
			double[] ynew = new double[nPoints];
			System.arraycopy( (double[])obj2[1], 0, xnew, 0, nPoints);
			System.arraycopy( (double[])obj2[2], 0, ynew, 0, nPoints);
			
			return(new Object[]{nPoints, xnew, ynew});
			*/
		//}
		//else
		//{
		//	return(new Object[]{0, 0.0, 0.0});
		//}
		
	}
	
	
	//returns an Object array of size 3
	//first element is the number of verticies (n) (int)
	//second element is an array x-coordinates (double[]) of size n
	//third element is an array y-coordinates (double[]) of size n
	public Object[] getOverlapVerticies(int sector, String strip1, int paddle1, String strip2, int paddle2){
	
		Object[] obj = getVerticies(getStripShape(sector, strip1, paddle1),getStripShape(sector, strip2, paddle2));
		
		int numpoints = (int)obj[0];
		//System.out.println("Strip let: " + strip1 + "Strip num: " + paddle1 + " Numpoints: " + numpoints);
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		System.arraycopy( (double[])obj[1], 0, x, 0, numpoints);
		System.arraycopy( (double[])obj[2], 0, y, 0, numpoints);
		
		return(new Object[]{numpoints, x, y});
		/*
		Object[] obj2 = sortVerticies(numpoints, x, y);
		
		int nPoints = (int)obj2[0];
		//System.out.println("Strip let: " + strip1 + "Strip num: " + paddle1 + " Numpoints: " + numpoints);
		double[] xnew = new double[nPoints];
		double[] ynew = new double[nPoints];
		System.arraycopy( (double[])obj2[1], 0, xnew, 0, nPoints);
		System.arraycopy( (double[])obj2[2], 0, ynew, 0, nPoints);

		return(new Object[]{nPoints, xnew, ynew});
		*/
	}
	

	public Object[] getStripVerticies(int sector, String strip1, int paddle1){
		int numpoints = 4;
		int l = 0;
		
		if((strip1 == "u" || strip1 == "U"))
		{
			l = 0;
		}
		if((strip1 == "v" || strip1 == "V"))
		{
			l = 1;
		}
		if((strip1 == "w" || strip1 == "W"))
		{
			l = 2;
		}
        
        return(new Object[]{numpoints, xPoint[sector][l][paddle1], yPoint[sector][l][paddle1]});
	}
	
	
	
	
	
	//estimates the shape center by calculating average x, y, z
	//from all verticies in the shape
	public double[] getShapeCenter(DetectorShape2D shape){
		double[] center = new double[3];
		int numpoints = shape.getShapePath().size();
		Point3D[] points = new Point3D[numpoints];
		Point3D centerp;
		for(int i = 0; i < numpoints; ++i)
		{
			points[i] = shape.getShapePath().point(i);
		}
		centerp = Point3D.average(points);
		center[0] = centerp.x();
		center[1] = centerp.y();
		center[2] = centerp.z();
		
		return center;
	}
	
	//assuming PMT is right at edge of PCAL
	//there is actually tens of centimeters of fibers between.
	public double[] getPMTLocation(String strip1, int paddle1){
		double[] center = new double[3];
		
		int uPaddle = -1;
		int vPaddle = -1;
		int wPaddle = -1;
		
		
		double x1, x2, y1, y2;
        double x = 0.0;
        double y = 0.0;
        double uyup, uydown;
        double vmup, vmdown, vbup, vbdown;
        double wmup, wmdown, wbup, wbdown;
		
		
		if((strip1 == "u" || strip1 == "U"))
		{
			uPaddle = paddle1;
		}
		if((strip1 == "v" || strip1 == "V"))
		{
			vPaddle = paddle1;
		}
		if((strip1 == "w" || strip1 == "W"))
		{
			wPaddle = paddle1;
		}
		
		//case 1: U strip
		if(uPaddle != -1)
		{ 
			vPaddle = 61;
		    wPaddle = 61;
		                		                

		                //convert strip numbers to slopes and intercepts
		                // rsu 1-68  
		                if(uPaddle + 1 > 52)
		                {
		                	uyup = (uPaddle + 1) - 52.0;
		                	uyup = uyup * 2.0;
		                	uyup = uyup + 52;
		                	uyup = uyup - 1;
		                	uydown = (83 - (uyup)) * length;
		                	uyup = (83 - (uyup - 2)) * length;
		                	//System.out.println("uyup: " + uyup);
		                	//System.out.println("uydown: " + uydown);
		                }
		                else
		                {
		                	uyup = uPaddle + 1;
		                	uydown = (84 - (uyup)) * length;
		                	uyup = (84 - (uyup - 1)) * length;
		                }
		                // rsv 1-62  
		                if(vPaddle + 1 >= 16)
		                {
		                	x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmup = (y2 - y1)/(x2 - x1);
		            		vbup = -x1*vmup;
		            		
		            		//System.out.println("vxright: " + x1);
		            		
		            		x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15 - 1))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15 - 1))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmdown = (y2 - y1)/(x2 - x1);
		            		vbdown = -x1*vmdown;
		            		
		            		//System.out.println("vxleft: " + x1);
		                }
		                else
		                {
		                	x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0) + 2.0)*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0) + 2.0)*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmdown = (y2 - y1)/(x2 - x1);
		            		vbdown = -x1*vmdown;
		            		
		            		x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmup = (y2 - y1)/(x2 - x1);
		            		vbup = -x1*vmup;
		                }
		                // rsw 1-62  
		                if(wPaddle + 1 >= 16)
		                {
		                	x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmup = (y2 - y1)/(x2 - x1);
		            		wbup = -x1*wmup;
		            		
		            		//System.out.println("wxright: " + x1);
		            		
		            		x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15 - 1))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15 - 1))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmdown = (y2 - y1)/(x2 - x1);
		            		wbdown = -x1*wmdown;
		            		
		            		//System.out.println("wxleft: " + x1);
		                }
		                else
		                {
		                	x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0) + 2.0)*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0) + 2.0)*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmdown = (y2 - y1)/(x2 - x1);
		            		wbdown = -x1*wmdown;
		            		
		            		//System.out.println("wxright: " + x1);
		            		
		            		x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmup = (y2 - y1)/(x2 - x1);
		            		wbup = -x1*wmup;
		            		
		            		//System.out.println("wxleft: " + x1);
		                }
		                
		                y = (uydown + uyup)/2.0; 
		                x = (y - wbup)/(wmup); 
		                
		}
		//case 2: V strip
		else if(vPaddle != -1)
		{ 
    			wPaddle = 61;
    			uPaddle = 67;
                
                //System.out.println("Sector: " + sector + " u: " + uPaddle + " v: " + vPaddle);
                
                //convert strip numbers to slopes and intercepts
                // rsu 1-68  
                if(uPaddle + 1 > 52)
                {
                	uyup = (uPaddle + 1) - 52.0;
                	uyup = uyup * 2.0;
                	uyup = uyup + 52;
                	uyup = uyup - 1;
                	uydown = (83 - (uyup)) * length;
                	uyup = (83 - (uyup - 2)) * length;
                	//System.out.println("uyup: " + uyup);
                	//System.out.println("uydown: " + uydown);
                }
                else
                {
                	uyup = uPaddle + 1;
                	uydown = (84 - (uyup)) * length;
                	uyup = (84 - (uyup - 1)) * length;
                }
                // rsv 1-62  
                if(vPaddle + 1 >= 16)
                {
                	x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15))*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15))*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmup = (y2 - y1)/(x2 - x1);
            		vbup = -x1*vmup;
            		
            		//System.out.println("vxright: " + x1);
            		
            		x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15 - 1))*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15 - 1))*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmdown = (y2 - y1)/(x2 - x1);
            		vbdown = -x1*vmdown;
            		
            		//System.out.println("vxleft: " + x1);
                }
                else
                {
                	x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0) + 2.0)*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0) + 2.0)*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmdown = (y2 - y1)/(x2 - x1);
            		vbdown = -x1*vmdown;
            		
            		x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0))*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0))*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmup = (y2 - y1)/(x2 - x1);
            		vbup = -x1*vmup;
                }
                // rsw 1-62  
                if(wPaddle + 1 >= 16)
                {
                	x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15))*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15))*anglewidth + slightshift;
            		y1 = 0.0;
            		y2 = length;
            		wmup = (y2 - y1)/(x2 - x1);
            		wbup = -x1*wmup;
            		
            		//System.out.println("wxright: " + x1);
            		
            		x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15 - 1))*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15 - 1))*anglewidth + slightshift;
            		y1 = 0.0;
            		y2 = length;
            		wmdown = (y2 - y1)/(x2 - x1);
            		wbdown = -x1*wmdown;
            		
            		//System.out.println("wxleft: " + x1);
                }
                else
                {
                	x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0) + 2.0)*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0) + 2.0)*anglewidth + slightshift;
            		y1 = 0.0;
            		y2 = length;
            		wmdown = (y2 - y1)/(x2 - x1);
            		wbdown = -x1*wmdown;
            		
            		//System.out.println("wxright: " + x1);
            		
            		x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0))*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0))*anglewidth + slightshift;
            		y1 = 0.0;
            		y2 = length;
            		wmup = (y2 - y1)/(x2 - x1);
            		wbup = -x1*wmup;
            		
            		//System.out.println("wxleft: " + x1);
                }
                
                y = uydown;
                x1 = (y - vbdown)/(vmdown);
                x2 = (y - vbup)/(vmup);
                x = (x1 + x2)/2.0;
                
            
		}
		//case 3: W strip
		else if(wPaddle != -1)
		{ 
			uPaddle = 67;
		    vPaddle = 61;
		                		                
		                //System.out.println("Sector: " + sector + " u: " + uPaddle + " w: " + wPaddle);
		                
		                //convert strip numbers to slopes and intercepts
		                // rsu 1-68  
		                if(uPaddle + 1 > 52)
		                {
		                	uyup = (uPaddle + 1) - 52.0;
		                	uyup = uyup * 2.0;
		                	uyup = uyup + 52;
		                	uyup = uyup - 1;
		                	uydown = (83 - (uyup)) * length;
		                	uyup = (83 - (uyup - 2)) * length;
		                	//System.out.println("uyup: " + uyup);
		                	//System.out.println("uydown: " + uydown);
		                }
		                else
		                {
		                	uyup = uPaddle + 1;
		                	uydown = (84 - (uyup)) * length;
		                	uyup = (84 - (uyup - 1)) * length;
		                }
		                // rsv 1-62  
		                if(vPaddle + 1 >= 16)
		                {
		                	x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmup = (y2 - y1)/(x2 - x1);
		            		vbup = -x1*vmup;
		            		
		            		//System.out.println("vxright: " + x1);
		            		
		            		x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15 - 1))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vPaddle + 15 - 1))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmdown = (y2 - y1)/(x2 - x1);
		            		vbdown = -x1*vmdown;
		            		
		            		//System.out.println("vxleft: " + x1);
		                }
		                else
		                {
		                	x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0) + 2.0)*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0) + 2.0)*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmdown = (y2 - y1)/(x2 - x1);
		            		vbdown = -x1*vmdown;
		            		
		            		x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vPaddle + 1) * 2.0))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmup = (y2 - y1)/(x2 - x1);
		            		vbup = -x1*vmup;
		                }
		                // rsw 1-62  
		                if(wPaddle + 1 >= 16)
		                {
		                	x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmup = (y2 - y1)/(x2 - x1);
		            		wbup = -x1*wmup;
		            		
		            		//System.out.println("wxright: " + x1);
		            		
		            		x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15 - 1))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wPaddle + 15 - 1))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmdown = (y2 - y1)/(x2 - x1);
		            		wbdown = -x1*wmdown;
		            		
		            		//System.out.println("wxleft: " + x1);
		                }
		                else
		                {
		                	x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0) + 2.0)*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0) + 2.0)*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmdown = (y2 - y1)/(x2 - x1);
		            		wbdown = -x1*wmdown;
		            		
		            		//System.out.println("wxright: " + x1);
		            		
		            		x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wPaddle + 1) * 2.0))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmup = (y2 - y1)/(x2 - x1);
		            		wbup = -x1*wmup;
		            		
		            		//System.out.println("wxleft: " + x1);
		                }
		                
		                y = uydown;
		                x1 = (y - wbdown)/(wmdown);
		                x2 = (y - wbup)/(wmup);
		                x = (x1 + x2)/2.0;

			}
		
		center[0] = x;
		center[1] = y;
		center[2] = 0.0;
		
		return center;
	}
	
	//get attenuation distance
	public double getUPixelDistance(int uPaddle, int vPaddle, int wPaddle){
		double distance = 0;
		double[] shapecenter = new double[3];
		double[] PMTloc = new double[3];
		
		
		shapecenter = getShapeCenter(getPixelShape(0,uPaddle,vPaddle,wPaddle));
		PMTloc = getPMTLocation("u", uPaddle);
		
		distance = Math.sqrt(Math.pow(shapecenter[0] - PMTloc[0],2) + Math.pow(shapecenter[1] - PMTloc[1],2) + Math.pow(shapecenter[2] - PMTloc[2],2));
		
		return distance;
	}
	
	//get attenuation distance
	public double getVPixelDistance(int uPaddle, int vPaddle, int wPaddle){
		double distance = 0;
		double[] shapecenter = new double[3];
		double[] PMTloc = new double[3];
		
		
		shapecenter = getShapeCenter(getPixelShape(0,uPaddle,vPaddle,wPaddle));
		PMTloc = getPMTLocation("v", vPaddle);
		
		distance = Math.sqrt(Math.pow(shapecenter[0] - PMTloc[0],2) + Math.pow(shapecenter[1] - PMTloc[1],2) + Math.pow(shapecenter[2] - PMTloc[2],2));
		
		return distance;
	}
	
	//get attenuation distance
	public double getWPixelDistance(int uPaddle, int vPaddle, int wPaddle){
		double distance = 0;
		double[] shapecenter = new double[3];
		double[] PMTloc = new double[3];
		
		
		shapecenter = getShapeCenter(getPixelShape(0,uPaddle,vPaddle,wPaddle));
		PMTloc = getPMTLocation("w", wPaddle);
		
		distance = Math.sqrt(Math.pow(shapecenter[0] - PMTloc[0],2) + Math.pow(shapecenter[1] - PMTloc[1],2) + Math.pow(shapecenter[2] - PMTloc[2],2));
		
		return distance;
	}
	
	
	
	
	//get attenuation distance
	//                              main strip let,    main num, cross strip let, cross num
	//                                      "u"        0-67         "w"             0-61
	public double getOverlapDistance(String strip1, int paddle1, String strip2, int paddle2){
		double distance = 0;
		double[] shapecenter = new double[3];
		double[] PMTloc = new double[3];
		
		String s1 = "u";
		String s2 = "w";
		if(strip1.contains("u") || strip1.contains("U")) s1 = "u";
		if(strip1.contains("v") || strip1.contains("V")) s1 = "v";
		if(strip1.contains("w") || strip1.contains("W")) s1 = "w";
		if(strip2.contains("u") || strip2.contains("U")) s2 = "u";
		if(strip2.contains("v") || strip2.contains("V")) s2 = "v";
		if(strip2.contains("w") || strip2.contains("W")) s2 = "w";

		System.arraycopy( (double[])getShapeCenter(getOverlapShape(0, s1, paddle1, s2, paddle2)), 0, shapecenter, 0, 3);
		System.arraycopy( (double[])getPMTLocation(s1, paddle1), 0, PMTloc, 0, 3);
		
		distance = Math.sqrt(Math.pow(shapecenter[0] - PMTloc[0],2) + Math.pow(shapecenter[1] - PMTloc[1],2) + Math.pow(shapecenter[2] - PMTloc[2],2));
		
		return distance;
	}
	
	
	
  	//crossstrip needs to be 1-62 or 1-68 not 0
	//returns a strip number in element 0
	// and half the strip bin 0.5 for singles and 1 for doubles in element 1
    public double[] CalcDistinStrips(char stripletter, int crossstrip)
    {
  	  double x=0;
  	  double xE = 0.0;
        if(stripletter == 'u' || stripletter == 'U')
        {
            if(crossstrip <= 15)
            {
                //converts to 77 strips
                x = 2.0* crossstrip - 1.0;
                xE = 1.0;
            }
            else if(crossstrip > 15)
            {
                //converts to 77 strips
                x = (30.0 + (crossstrip - 15.0)) - 0.5;
                xE = 1.0/2.0;
            }
        }
        else if(stripletter == 'v' || stripletter == 'w' || stripletter == 'V' || stripletter == 'W')
        {
            if(crossstrip <= 52)
            {
                //converts to 84 strips
                x = crossstrip - 0.5;
                xE = 1.0/2.0;
                }
                else if(crossstrip > 52)
                {
                    //converts to 84 strips
                    x = (52.0 + 2.0*(crossstrip - 52.0)) - 1.0;
                    xE = 1.0;
                }
        }
        return new double[] {x, xE};
    }
	
    //xdistance needs to be 1-77 or 1-84 not 0
    //meant for use with the output of CalcDistinStrips
    public double[] CalcDistance(char stripletter, double xdistance, double xdistanceE)
    {
        
        if(stripletter == 'u' || stripletter == 'U')
        {
            //convert strip number to distance
            xdistance = Math.abs(xdistance - 77.0) * anglewidth;
            xdistanceE = xdistanceE * anglewidth;
        }
        else if(stripletter == 'v' || stripletter == 'w' || stripletter == 'V' || stripletter == 'W')
        {
            //convert strip number to distance
            xdistance = Math.abs(xdistance - 84.0) * anglewidth;
            xdistanceE = xdistanceE * anglewidth;
        }
        return new double[] {xdistance, xdistanceE};
    }
    
    
	public float uvw_dalitz(int ic, int ip, int il) {
		float uvw=0;
		switch (ic) {
		case 0: //PCAL
			if (il==1&&ip<=52) uvw=(float)ip/84;
			if (il==1&&ip>52)  uvw=(float)(52+(ip-52)*2)/84;
			if (il==2&&ip<=15) uvw=(float) 2*ip/77;
			if (il==2&&ip>15)  uvw=(float)(30+(ip-15))/77;
			if (il==3&&ip<=15) uvw=(float) 2*ip/77;
			if (il==3&&ip>15)  uvw=(float)(30+(ip-15))/77;
			break;
		case 1: //ECALinner
			uvw=(float)ip/36;
			break;
		case 2: //ECALouter
			uvw=(float)ip/36;
			break;
		}
		return uvw;
		
	}
	
	
	private void initVert()
	{
		ScintillatorPaddle paddle;
		ECLayer  ecLayer;
		int sector = 0;
		int lastcomponent = 67;
		Point3D point1 = new Point3D();
		Point3D point2 = new Point3D();
		Point3D point3 = new Point3D();
        ECDetector detector  = (ECDetector) CLASGeometryLoader.createDetector(DetectorType.EC, 10, "default", "local");
        for(sector = 0; sector < 6; ++sector)
        {
		        //                                          PCAL ==0        u,v,w
		        ecLayer = detector.getSector(sector).getSuperlayer(0).getLayer(0);
		
		        //get point 1
		        paddle = ecLayer.getComponent(0); //strip num
		        point1.copy(paddle.getVolumePoint(0)); //one of 8
		        //if(l == 0 && sector) zarray[0] = point1.z();
		        //System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
		            	
		        //get point 2
		        paddle = ecLayer.getComponent(lastcomponent);
		        point2.copy(paddle.getVolumePoint(0));
		        //System.out.println("x: " + point2.x() + " y: " + point2.y() + " z: " + point2.z());
			
		        //get point 3
		        paddle = ecLayer.getComponent((lastcomponent - 1)/2);
		        point3.copy(paddle.getVolumePoint(4));
		        //System.out.println("x: " + point3.x() + " y: " + point3.y() + " z: " + point3.z());
		
		        
		        //calculate plane from 3 points
		        //Point3D ab = new Point3D(point2);
		        //Point3D ac = new Point3D(point3);
		
		        //ab.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        //ac.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        
		        double a = (point2.y() - point1.y())*(point3.z() - point1.z()) - (point3.y() - point1.y())*(point2.z() - point1.z());
		        double b = (point2.z() - point1.z())*(point3.x() - point1.x()) - (point3.z() - point1.z())*(point2.x() - point1.x());
		        double c = (point2.x() - point1.x())*(point3.y() - point1.y()) - (point3.x() - point1.x())*(point2.y() - point1.y());
		        double d = -(a * point1.x() + b * point1.y() + c * point1.z());
		        
		        //System.out.println("a: " + a + " b: " + b + " c: " + c + " d: " + d);
		        
		        //find x rotation
            	if(-b/c > 0)
            	{
            		xrotation[sector] = -Math.atan(-b/c);
            	}
            	else
            	{
            		xrotation[sector] = Math.atan(Math.abs(-b/c));
            	}
            	
            	//apply x rotation
		        point1.rotateX(xrotation[sector]);
		        point2.rotateX(xrotation[sector]);
		        point3.rotateX(xrotation[sector]);
		        
		        //calculate 2nd plane from the rotated points
		        //ab = new Point3D(point2);
		        //ac = new Point3D(point3);
		
		        //ab.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        //ac.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        
		        a = (point2.y() - point1.y())*(point3.z() - point1.z()) - (point3.y() - point1.y())*(point2.z() - point1.z());
		        b = (point2.z() - point1.z())*(point3.x() - point1.x()) - (point3.z() - point1.z())*(point2.x() - point1.x());
		        c = (point2.x() - point1.x())*(point3.y() - point1.y()) - (point3.x() - point1.x())*(point2.y() - point1.y());
		        d = -(a * point1.x() + b * point1.y() + c * point1.z());
		        
		        //System.out.println("a: " + a + " b: " + b + " c: " + c + " d: " + d);
		        
		        //find y rotation
		        if(-c/a > 0)
            	{
            		yrotation[sector] = Math.PI/2.0 - Math.atan(-c/a);
            	}
            	else
            	{
            		yrotation[sector] = Math.PI/2.0 - Math.atan(Math.abs(-c/a));
            		yrotation[sector] *= -1.000;
            	}
		        
		        //use plane and slopes to find rotation quantities of PCAL unit
		        // constant z term
		        //xrotation[sector] = Math.atan(b/c); //slope for z of y
		        //yrotation[sector] = Math.atan(-a/c);
		       
		    //apply both rotations to all geometry points
		    for(int l = 0; l <3; l++)
		    {
	        	//	                                       PCAL ==0           u,v,w
	        	ecLayer = detector.getSector(sector).getSuperlayer(0).getLayer(l);
	            for(ScintillatorPaddle paddle2 : ecLayer.getAllComponents())
	            {													//0-67
	            	point1.copy(paddle2.getVolumePoint(0));
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
	            	//point1.rotateZ(Math.PI/2.0);
	            	//if(paddle2.getComponentId() == 61)System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
	            	//System.out.println("Component ID: " + paddle2.getComponentId());
	            	xPoint[sector][l][paddle2.getComponentId()][0] = point1.x();
	            	yPoint[sector][l][paddle2.getComponentId()][0] = point1.y();
	            	
	            	point1.copy(paddle2.getVolumePoint(4));
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
	            	//point1.rotateZ(Math.PI/2.0);
	            	//if(paddle2.getComponentId() == 61)System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
	            	//System.out.println("Component ID: " + paddle2.getComponentId());
	            	xPoint[sector][l][paddle2.getComponentId()][1] = point1.x();
	            	yPoint[sector][l][paddle2.getComponentId()][1] = point1.y();
	            	
	            	point1.copy(paddle2.getVolumePoint(5));
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
	            	//point1.rotateZ(Math.PI/2.0);
	            	//System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
	            	//System.out.println("Component ID: " + paddle2.getComponentId());
	            	xPoint[sector][l][paddle2.getComponentId()][2] = point1.x();
	            	yPoint[sector][l][paddle2.getComponentId()][2] = point1.y();
	            	
	            	point1.copy(paddle2.getVolumePoint(1));
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
	            	//point1.rotateZ(Math.PI/2.0);
	            	//System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
	            	//System.out.println("Component ID: " + paddle2.getComponentId());
	            	xPoint[sector][l][paddle2.getComponentId()][3] = point1.x();
	            	yPoint[sector][l][paddle2.getComponentId()][3] = point1.y();
	            }
	        }
        }
	}
	
	
	public boolean isContained3Decimal(DetectorShape2D shape, double x, double y){
        int i, j;
        double scale = 1.01;
        boolean c = false;
        int nvert = shape.getShapePath().size();
        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( (( shape.getShapePath().point(i).y()*scale>y) != (shape.getShapePath().point(j).y()*scale>y)) &&
                    (x < ( shape.getShapePath().point(j).x()*scale-shape.getShapePath().point(i).x()*scale) * 
                    (y-shape.getShapePath().point(i).y()*scale) / (shape.getShapePath().point(j).y()*scale-shape.getShapePath().point(i).y()*scale) +
                    shape.getShapePath().point(i).x()*scale))
                c = !c;
        }
        return c;
        //return false;
    }
	
	public Object[] getVerticies(DetectorShape2D shape1, DetectorShape2D shape2){
		int numpoints = 0;
		int count = 0;
		int nPoints = 0;
		Point3D point1A = new Point3D();
		Point3D point1B = new Point3D();
		Point3D point2A = new Point3D();
		Point3D point2B = new Point3D();
		double m1, b1, m2, b2;
		double[] xtemp = new double[shape1.getShapePath().size() * shape2.getShapePath().size()];
		double[] ytemp = new double[shape1.getShapePath().size() * shape2.getShapePath().size()];
		
		for(int i = 0; i < shape1.getShapePath().size(); ++i)
		{
			if(i+1 < shape1.getShapePath().size())
			{
				point1A.copy(shape1.getShapePath().point(i));
				point1B.copy(shape1.getShapePath().point(i + 1));
			}
			else
			{
				point1A.copy(shape1.getShapePath().point(i));
				point1B.copy(shape1.getShapePath().point(0));
			}
			for(int j = 0; j < shape2.getShapePath().size(); ++j)
			{
				if(j+1 < shape2.getShapePath().size())
				{
					point2A.copy(shape2.getShapePath().point(j));
					point2B.copy(shape2.getShapePath().point(j + 1));
				}
				else
				{
					point2A.copy(shape2.getShapePath().point(j));
					point2B.copy(shape2.getShapePath().point(0));
				}
				
				
				//calculate line 1
				m1 = (point1A.y()-point1B.y())/(point1A.x()-point1B.x());
		        b1 = point1A.y() - m1*point1A.x();
		        if(Math.abs(point1A.x() - point1B.x()) < 0.0001) m1 = 9999.0;
				
				//calculate line 2
		        m2 = (point2A.y()-point2B.y())/(point2A.x()-point2B.x());
		        b2 = point2A.y() - m2*point2A.x();
		        if(Math.abs(point2A.x() - point2B.x()) < 0.0001) m2 = 9999.0;
		        
		        //System.out.println( "i: " + i + " j: " + j);
		        //System.out.println( "points1a: " +"x: " + point1A.x() + " y: " + point1A.y());
		        //System.out.println( "points1b: " +"x: " + point1B.x() + " y: " + point1B.y());
		        //System.out.println( "                                                      ");
		        //System.out.println( "points2a: " +"x: " + point2A.x() + " y: " + point2A.y());
		        //System.out.println( "points2b: " +"x: " + point2B.x() + " y: " + point2B.y());
		        //System.out.println( "                                                      ");
		        
		        //System.out.println( "m2: " + m2 + " m1: " + m1);
		        //System.out.println( "                                                      ");
		        if(Math.abs(m1 - m2) > 0.0001 && !(  (((Double)m1).isInfinite() || Math.abs(m1) > 9000.0)  && (((Double)m2).isInfinite()  || Math.abs(m2) > 9000.0) )  )
		        {
		        	//not parallel
			        if(((Double)m1).isInfinite() || Math.abs(m1) > 9000.0)
			        {
			        	xtemp[numpoints] = point1A.x();
			        	ytemp[numpoints] = m2 * xtemp[numpoints] + b2;
			        	//System.out.println( "Inf slope1: " +"x: " + xtemp[numpoints] + " y: " + ytemp[numpoints]);
			        	++numpoints;
			        }
			        else if(((Double)m2).isInfinite() || Math.abs(m2) > 9000.0)
			        {
			        	xtemp[numpoints] = point2A.x();
			        	ytemp[numpoints] = m1 * xtemp[numpoints] + b1;
			        	//System.out.println( "Inf slope2: " +"x: " + xtemp[numpoints] + " y: " + ytemp[numpoints]);
			        	++numpoints;
			        }
			        else
			        {
			        	xtemp[numpoints] = (b1 - b2)/(m2 - m1); 
			        	ytemp[numpoints] = m1 * xtemp[numpoints] + b1; 
			        	//System.out.println( "Norm slope: " +"x: " + xtemp[numpoints] + " y: " + ytemp[numpoints]);
			        	++numpoints;
			        }
			        
		        }
		        ++count;
				
				
			}
		}
		
		
		
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		
		double xinside, yinside;
		
		double prec1d = 0.001;
		double prec = prec1d/Math.sqrt(2.0);
		double scale = 1.01;
		
		for(int i = 0; i < numpoints; ++i)
		{
			Point3D point = new Point3D();
			Point3D centerA = new Point3D();
			centerA.set((double)getShapeCenter(shape1)[0],(double)getShapeCenter(shape1)[1],0.0);
			Point3D centerB = new Point3D();
			centerB.set((double)getShapeCenter(shape2)[0],(double)getShapeCenter(shape2)[1],0.0);
			DetectorShape2D shapeA = new DetectorShape2D();
			DetectorShape2D shapeB = new DetectorShape2D();
			for(int j = 0; j < shape1.getShapePath().size(); ++j)
			{
				point.copy(shape1.getShapePath().point(j));
				point.translateXYZ(-centerA.x(), -centerA.y(), 0.0);
				point.set(point.x()*scale,point.y()*scale,0.0);
				shapeA.getShapePath().addPoint(point.x(),point.y(),point.z());
			}
			for(int j = 0; j < shape2.getShapePath().size(); ++j)
			{
				point.copy(shape2.getShapePath().point(j));
				point.translateXYZ(-centerB.x(), -centerB.y(), 0.0);
				point.set(point.x()*scale,point.y()*scale,0.0);
				shapeB.getShapePath().addPoint(point.x(),point.y(),point.z());
			}
			//System.out.println( "Found: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
			/*
			
			if(isContained3Decimal(shapeA, xtemp[i]-centerA.x(), ytemp[i]-centerA.y()))// || wn_PnPoly( new Point3D(xtemp[i]-(double)getShapeCenter(shape1)[0],ytemp[i]-(double)getShapeCenter(shape1)[1],0.0), shapeA) != 0)
			{
				//System.out.println( "Accepted1: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
				if(isContained3Decimal(shapeB, xtemp[i]-centerB.x(), ytemp[i]-centerB.y()))// || wn_PnPoly( new Point3D(xtemp[i]-(double)getShapeCenter(shape2)[0],ytemp[i]-(double)getShapeCenter(shape2)[1],0.0), shapeB) != 0)
				{
					//System.out.println( "Accepted-Both: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
					x[nPoints] = xtemp[i];
					y[nPoints] = ytemp[i];
					++nPoints;
				}
				
			}
			*/
			
			xinside = xtemp[i]-centerA.x();
			yinside = ytemp[i]-centerA.y();
			Boolean duplicatepoint = false;
			if(shapeA.isContained( xinside, yinside))/*) || 
			   shape1.isContained( xinside - prec1d, yinside)|| 
			   shape1.isContained( xinside + prec1d, yinside)|| 
			   shape1.isContained( xinside, yinside - prec1d)|| 
			   shape1.isContained( xinside, yinside + prec1d)|| 
			   shape1.isContained( xinside - prec, yinside - prec)|| 
			   shape1.isContained( xinside - prec, yinside + prec)|| 
			   shape1.isContained( xinside + prec, yinside - prec)|| 
			   shape1.isContained( xinside + prec, yinside + prec))*/
			{
				
				xinside = xtemp[i]-centerB.x();
				yinside = ytemp[i]-centerB.y();
				//System.out.println( "Accepted1: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
				if(shapeB.isContained( xinside, yinside))/* || 
				   shape2.isContained( xinside - prec1d, yinside)|| 
				   shape2.isContained( xinside + prec1d, yinside)|| 
		           shape2.isContained( xinside, yinside - prec1d)|| 
				   shape2.isContained( xinside, yinside + prec1d)|| 
				   shape2.isContained( xinside - prec, yinside - prec)|| 
				   shape2.isContained( xinside - prec, yinside + prec)|| 
				   shape2.isContained( xinside + prec, yinside - prec)|| 
				   shape2.isContained( xinside + prec, yinside + prec))*/
				{
					//System.out.println( "Accepted-Both: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
					for(int j = 0; j < nPoints; ++j)
					{
						if(Math.abs(xtemp[i] - x[j]) < 0.001 && Math.abs(ytemp[i] - y[j]) < 0.001)
							duplicatepoint = true;
					}
					if(!duplicatepoint)
					{
						//System.out.println( "Accepted-Both: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
						x[nPoints] = xtemp[i];
						y[nPoints] = ytemp[i];
						++nPoints;
					}
				}
				
			}
		}
		//System.out.println( "count: " + count + " numpoints: " + numpoints + " nPoints: " + nPoints);
		
		/////////////////////////////////////////////////////////////
		
		
		double[] xes1 = new double[shape1.getShapePath().size()];
		double[] yes1 = new double[shape1.getShapePath().size()];
		for(int i = 0; i < shape1.getShapePath().size(); ++i)
		{
			xes1[i] = shape1.getShapePath().point(i).x();
			yes1[i] = shape1.getShapePath().point(i).y();
		}
		
		double[] xes2 = new double[shape2.getShapePath().size()];
		double[] yes2 = new double[shape2.getShapePath().size()];
		for(int i = 0; i < shape2.getShapePath().size(); ++i)
		{
			xes2[i] = shape2.getShapePath().point(i).x();
			yes2[i] = shape2.getShapePath().point(i).y();
		}
		
		SimplePolygon2D pol1 = new SimplePolygon2D(xes1,yes1);
		SimplePolygon2D pol2 = new SimplePolygon2D(xes2,yes2);
		Polygon2D pol3 = Polygons2D.intersection(pol1,pol2);
		
		nPoints = pol3.vertexNumber();
		for(int i = 0; i < pol3.vertexNumber(); ++i)
		{
			x[i] = pol3.vertex(i).getX();
			y[i] = pol3.vertex(i).getY();
			
			//System.out.println("x: " + pol3.vertex(i).getX() + " y: " + pol3.vertex(i).getY());
		}
		
		/////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////
		
		/*
		double[] xes1 = new double[shape1.getShapePath().size()];
		double[] yes1 = new double[shape1.getShapePath().size()];
		
		for(int i = 0; i < shape1.getShapePath().size(); ++i)
		{
			xes1[i] = shape1.getShapePath().point(i).x();
			yes1[i] = shape1.getShapePath().point(i).y();
		}
		
		double[] xes2 = new double[shape2.getShapePath().size()];
		double[] yes2 = new double[shape2.getShapePath().size()];
		for(int i = 0; i < shape2.getShapePath().size(); ++i)
		{
			xes2[i] = shape2.getShapePath().point(i).x();
			yes2[i] = shape2.getShapePath().point(i).y();
		}
		
		Path2D path1 = new Path2D.Double();
		path1.moveTo(xes1[0], yes1[0]);
		for(int i = 1; i < shape1.getShapePath().size(); ++i) {
		   path1.lineTo(xes1[i], yes1[i]);
		}
		path1.closePath();
		Shape pol1 = (Shape)path1;
		
		Path2D path2 = new Path2D.Double();
		path2.moveTo(xes2[0], yes2[0]);
		for(int i = 1; i < shape2.getShapePath().size(); ++i) {
		   path2.lineTo(xes2[i], yes2[i]);
		}
		path2.closePath();
		Shape pol2 = (Shape)path2;
		
		Area a1 = new Area(pol1);
		Area a2 = new Area(pol2);
		
		a1.intersect(a2);
		
		//a1.isPolygonal()
		nPoints = 0;
		double[] data = new double[6];
		if(a1.isEmpty())
			nPoints = 0;
		else
		{
			//pol1 = (Shape) a1;
			PathIterator pi = a1.getPathIterator(null);
			while(!pi.isDone())
			{
				pi.currentSegment(data);
				//int type = pi.currentSegment(data);
				x[nPoints] = data[0];
				y[nPoints] = data[1];
				++nPoints;
				pi.next();
			}
		}
		
		*/
		/////////////////////////////////////////////////////////////////
		return(new Object[]{nPoints, x, y});
	}
	
	public Object[] sortVerticies(int num, double[] x, double[] y)
	{
		System.out.println("Sorting points");
		double[] xnew = new double[num];
		double[] ynew = new double[num];
		int[] used = new int[num];
		
		double ymin = 0;
		double xmin = 0;
		double ymax = 0;
		double xmax = 0;
		double minangle = 0;
		double prevdist = 9000;
		double curdist = 9000;
		int index = 0;
		int xmini = 0;
		int ymaxi = 0;
		int xmaxi = 0;
		int numpoints = 0;
		int i = 0;
		
		for(i = 0; i < num; ++i)
		{
			if(i == 0)
			{
				xmax = x[i];
				xmin = x[i];
				ymin = y[i];
				ymax = y[i];
				index = i;
				xmini = i;
			}
			
			if(ymin > y[i])
			{
				ymin = y[i];
				index = i;
			}
			
			if(ymax < y[i])
			{
				ymax = y[i];
				ymaxi = i;
			}

			if(xmin > x[i])
			{
				xmin = x[i];
				xmini = i;
			}
			
			if(xmax < x[i])
			{
				xmax = x[i];
				xmaxi = i;
			}
			
			used[i] = 0;
			//System.out.println("ymin: " + ymin + " y: " + y[i]);
			//System.out.println("xmin: " + xmin + " x: " + x[i]);
		}
		
		//start with minimum y
		//look to -x as a function of theta
		numpoints = 0;
		int count = 0;
		int count2 = 0;
		boolean xminreached = false;
		boolean ymaxreached = false;
		boolean xmaxreached = false;
		while(count < num)
		{
			++count2;
			//make starting point
			for(i = 0; i < num; ++i)
			{
				if(used[i] == 0) //only loop through unused points
				{
					if(index == i)
					{
						//first point
						ynew[numpoints] = y[i];
						xnew[numpoints] = x[i];
						used[i] = 1;
						if(index == ymaxi) ymaxreached = true;
						if(index == xmaxi) xmaxreached = true;
						if(index == xmini) xminreached = true;
						index = -1;
						++numpoints;
						++count;
						//System.out.println("truths: " + xminreached + "  " + ymaxreached + "  " + xmaxreached);
						//System.out.println("Sorted: " + "x: " + xnew[numpoints - 1] + " y: " + ynew[numpoints -1]);
					}
					//if(count2 == 50)System.out.println("Stuck: " + "x: " + x[i] + " y: " + y[i]);
				}
			}
			
			curdist = 90000.0;
			prevdist = 90000.0;
			minangle = 90000.0;
			if(!xminreached)
			{
				//lowerleft
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001)
							{
								//duplicate
								used[i] = 1;
								++count;
							}
							else if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && x[i] - xnew[numpoints - 1] < 0.0)
							{
								minangle = 0;
								index = i;
								i = num;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(  (x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )*10.0) ))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//upper left
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && x[i] - xnew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
								i = num;
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(   (y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1]))  *10.0)  ) )
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//upper right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001 && y[i] - ynew[numpoints - 1] > 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1])) *10.0)))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//lower right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001 && y[i] - ynew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1])) *10.0))  )
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && minangle > Math.abs(Math.atan(Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				if(index == ymaxi) ymaxreached = true;
				if(index == xmaxi) xmaxreached = true;
				if(index == xmini) xminreached = true;
			}
			else if(!ymaxreached)
			{				
				//upper left
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && x[i] - xnew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
								i = num;
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(   (y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1]))  *10.0)  ) )
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//upper right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001 && y[i] - ynew[numpoints - 1] > 0)
							{
								minangle = 0;
								index = i;
								i = num;
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1])) *10.0)))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//lower right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001 && y[i] - ynew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1])) *10.0)) )
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && minangle > Math.abs(Math.atan(Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//lowerleft
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001)
							{
								//duplicate
								used[i] = 1;
								++count;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(  (x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )*10.0) ))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				if(index == ymaxi) ymaxreached = true;
				if(index == xmaxi) xmaxreached = true;
				if(index == xmini) xminreached = true;
			}
			else if(!xmaxreached)
			{				
				//upper right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001 && y[i] - ynew[numpoints - 1] > 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1])) *10.0)))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//lower right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001 && y[i] - ynew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && minangle > Math.abs(Math.atan(Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1])) *10.0)))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//lowerleft
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001)
							{
								//duplicate
								used[i] = 1;
								++count;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(  (x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )*10.0) ))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//upper left
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && x[i] - xnew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(   (y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1]))  *10.0)  ) )
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				if(index == ymaxi) ymaxreached = true;
				if(index == xmaxi) xmaxreached = true;
				if(index == xmini) xminreached = true;
			}
			else
			{
				
				
				//lower right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.001 && y[i] - ynew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan(  Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]))   *10.0 )) )
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && minangle > Math.abs(Math.atan(Math.abs(y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//lowerleft
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001)
							{
								//duplicate
								used[i] = 1;
								++count;
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )*10.0) ))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				//upper left
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(y[i] - ynew[numpoints - 1]) < 0.0001 && x[i] - xnew[numpoints - 1] < 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1])*10.0))  ) )
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] >= 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/Math.abs(x[i] - xnew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) + Math.pow((y[i] - ynew[numpoints - 1]),2) );
							}
						}
					}
				}
				
				
				//upper right
				if(index == -1)
				{
					for(i = 0; i < num; ++i)
					{
						if(used[i] == 0) //only loop through unused points
						{
							if(Math.abs(x[i] - xnew[numpoints - 1]) < 0.0001 && y[i] - ynew[numpoints - 1] > 0)
							{
								minangle = 0;
								index = i;
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && (int)(minangle*10.0) == (int)(Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]))*10.0 )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								curdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) );
								if(curdist < prevdist)
								{
									prevdist = curdist;
									index = i;
								}
							}
							else if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] >= 0 && minangle > Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) )))
							{
								//look in clockwise direction from 6 o' clock
								minangle = Math.abs(Math.atan((x[i] - xnew[numpoints - 1])/(y[i] - ynew[numpoints - 1]) ));
								index = i;
								prevdist = Math.sqrt( Math.pow((x[i] - xnew[numpoints - 1]),2) );
							}
						}
					}
				}
			
			}
		}
		//System.out.println(" Done sorting points");
		
		for(i = 0; i < numpoints; ++i)
		{
			System.out.println("i: " + i + " x: " + xnew[i] + " y: " + ynew[i]);
		}
		
		return(new Object[]{numpoints, xnew, ynew});
	}
	
	public static void main(String[] args){ 
		
		PCALDrawDBscratch pcaltest = new PCALDrawDBscratch();
		
		
		char stripLetter[] = {'u','v','w'};
		char stripLetter2[] = {'w','u','u'};
		String cstring1 = ""+stripLetter[0];//Character.toString(stripLetter[0]);
		String cstring2 = ""+stripLetter2[0];//Character.toString(stripLetter2[0]);
		int strip = 38;
		int crossStrip = 31;
		double x,y;
		//System.out.println("pad1: " + strip + " pad2: " + crossStrip);
		//double x = pcaltest.getOverlapDistance(cstring1,strip,cstring2,crossStrip);
		//System.out.println("x: " + x);
		
		
		//x = pcaltest.CalcDistinStrips('u',32)[0];
		//x = pcaltest.CalcDistance('u',x,0)[0];
		//System.out.println("x: " + x);
		
		
		TEmbeddedCanvas canvas = new TEmbeddedCanvas();
		
		DetectorShapeTabView  view   = new DetectorShapeTabView();
		
		
		//draw U strips
		
		/*
		DetectorShape2D shape = new DetectorShape2D();
	 	DetectorShapeView2D Umap= new DetectorShapeView2D("PCAL U");
	 	for(int sector = 0; sector < 1; sector++)
    	{
	 		for(int vPaddle = 0; vPaddle < 68; vPaddle++)
	 		{
	            shape = pcaltest.getStripShape(sector, "v", vPaddle);
	            for(int i = 0; i < shape.getShapePath().size(); ++i)
        		{
	            	
	            	if(vPaddle == 0)System.out.println(shape.getShapePath().point(i).x());
	            	if(vPaddle == 0)System.out.println(xPoint[sector][1][vPaddle][i]);
	            	
        			if(vPaddle == 0)System.out.println(shape.getShapePath().point(i).y());
	            	
	            	x = shape.getShapePath().point(i).x();// * 1000.0;
	            	y = shape.getShapePath().point(i).y();// * 1000.0;
	            	if(sector == 0){ x += 302.0; y += 0.0;}
	            	if(sector == 1){ x += 141.0; y += 259.0;}
	            	if(sector == 2){ x += -141.0; y += 259.0;}
	            	if(sector == 3){ x += -302.0; y += 0.0;}
	            	if(sector == 4){ x += -141.0; y += -259.0;}
	            	if(sector == 5){ x += 141.0; y += -259.0;}
	            	x *= 1000.0;
	            	y *= 1000.0;
        			shape.getShapePath().point(i).set(x, y, 0.0);
        			//if(i == 0 && uPaddle == 67)System.out.println(shape.getShapePath().point(i).x());
        			
        		}
	            Umap.addShape(shape);


	 		}
    	}
	    view.addDetectorLayer(Umap);
		*/
		
		/*
		Object[] obj = pcaltest.getOverlapVerticies(2, "u", 67, "w", 42);
		int numpoints = (int)obj[0];
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		System.arraycopy( (double[])obj[1], 0, x, 0, numpoints);
		System.arraycopy( (double[])obj[2], 0, y, 0, numpoints);
		System.out.println("numpoints: " + numpoints);
		*/
		
		
		//draw UW pane
		/*
	    DetectorShape2D shape = new DetectorShape2D();
	 	DetectorShapeView2D UWmap= new DetectorShapeView2D("PCAL UW");
	 	for(int sector = 0; sector < 1; sector++)
    	{
    	for(int uPaddle = 0; uPaddle < 68; uPaddle++)
    	{
    		for(int vPaddle = 0; vPaddle < 62; vPaddle++)
            {
	            //for(int wPaddle = 0; wPaddle < 62; wPaddle++)
	            //{
	            	if(pcaltest.isValidOverlap(sector, "u", uPaddle, "v", vPaddle))
	            	{
	            		
	            		System.out.println("u: " + uPaddle + " v: " + vPaddle);
	            		shape = pcaltest.getOverlapShape(sector, "u", uPaddle, "v", vPaddle);
	            		for(int i = 0; i < shape.getShapePath().size(); ++i)
        				{
	        				x = shape.getShapePath().point(i).x();// * 1000.0;
			            	y = shape.getShapePath().point(i).y();// * 1000.0;
			            	
			            	//if(sector == 0){ x += 302.0; y += 0.0;}
			            	//if(sector == 1){ x += 140.0; y += 260.0;}
			            	//if(sector == 2){ x += -140.0; y += 260.0;}
			            	//if(sector == 3){ x += -302.0; y += 0.0;}
			            	//if(sector == 4){ x += -140.0; y += -260.0;}
			            	//if(sector == 5){ x += 140.0; y += -260.0;}
			            	//x *= 1000.0;
			            	//y *= 1000.0;
			            	
        					shape.getShapePath().point(i).set(x, y, 0.0);
        					//if(i == 0 && vPaddle == 67 && wPaddle == 30)System.out.println(shape.getShapePath().point(i).x());
        				}
	            		UWmap.addShape(shape);
	            	//}
	            }
            }

    	}
    	}
	    view.addDetectorLayer(UWmap);
	    */
		
		
		
		//Draw pixels
		
		DetectorShape2D shape = new DetectorShape2D();
    	 	DetectorShapeView2D UWmap= new DetectorShapeView2D("PCAL UW");
    	 	for(int sector = 2; sector < 3; sector++)
	    	{
	    	for(int uPaddle = 0; uPaddle < 68; uPaddle++)
	    	{
	    		for(int vPaddle = 0; vPaddle < 62; vPaddle++)
	            {
		            for(int wPaddle = 0; wPaddle < 62; wPaddle++)
		            {
		            	//System.out.println("u: " + uPaddle + " v: " + vPaddle + " w: " + wPaddle);
		            	if(pcaltest.isValidPixel(sector, uPaddle, vPaddle, wPaddle))
		            	{
		            		//System.out.println("u: " + uPaddle + " v: " + vPaddle + " w: " + wPaddle);
		            		shape = pcaltest.getPixelShape(sector, uPaddle, vPaddle, wPaddle);
		            		for(int i = 0; i < shape.getShapePath().size(); ++i)
	        				{
		            			x = shape.getShapePath().point(i).x();
				            	y = shape.getShapePath().point(i).y();
		            			if(sector == 0){ x +=  302.0; y +=    0.0;}
				            	if(sector == 1){ x +=  140.0; y +=  265.0;}
				            	if(sector == 2){ x += -140.0; y +=  265.0;}
				            	if(sector == 3){ x += -302.0; y +=    0.0;}
				            	if(sector == 4){ x += -140.0; y += -265.0;}
				            	if(sector == 5){ x +=  140.0; y += -265.0;}
	        					shape.getShapePath().point(i).set(x * 1000.0, y * 1000.0, 0.0);
	        				}
		            		UWmap.addShape(shape);
		            	}
		            }
	            }

	    	}
	    	}
	    	view.addDetectorLayer(UWmap);
	    	
	       // return UWmap;
	    	
	    	JFrame hi = new JFrame();
			hi.setLayout(new BorderLayout());
		    JSplitPane  splitPane = new JSplitPane();
		    splitPane.setLeftComponent(view);
		    splitPane.setRightComponent(canvas);
		    hi.add(splitPane,BorderLayout.CENTER);
		    hi.pack();
		    hi.setVisible(true);
	    	//canvas.add(view);
	    	//canvas1.draw(view);
	       // return UWmap;
    	 
	    	System.out.println("Done!");
	
	}
    
}
