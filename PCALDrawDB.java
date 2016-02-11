package org.jlab.calib;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clasrec.utils.CLASGeometryLoader;
import org.jlab.geom.component.ScintillatorPaddle;
import org.jlab.geom.detector.ec.ECDetector;
import org.jlab.geom.detector.ec.ECLayer;
import org.jlab.geom.prim.Point3D;
import org.root.pad.EmbeddedCanvas;
import org.root.pad.TGCanvas;

public class PCALDrawDB {
	
	private double length;
	private double angle;
	private double anglewidth;
	private double slightshift;
	
	private double[] xrotation = new double [6];
	private double[] yrotation = new double [6];
	
	private double[][][][] xPoint = new double [6][3][68][4];
	private double[][][][] yPoint = new double [6][3][68][4];

	public PCALDrawDB() {
		initVert();
		length = 4.5;
		angle = 62.8941;
		anglewidth = length/Math.sin(Math.toRadians(angle));
		slightshift = length/Math.tan(Math.toRadians(angle));
	}
	
	public PCALDrawDB(double inlength, double inangle) {
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
		double uyup, uydown;
        double vmup, vmdown, vbup, vbdown;
        double wmup, wmdown, wbup, wbdown;
        double x1, x2, y1, y2;
        int numpoints = 0;
        double x[] = new double [12];
        double y[] = new double [12];
        int numpointsA = 0;
        double a[] = new double [12];
        double b[] = new double [12];

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
        
        //maximum of 12 vertices for every 3 strips
        //most of which can be thrown out later
        //calculate all 12
        
        //udown
        a[0] = (uydown - vbdown)/(vmdown); //udown and vdown, u slope = 0
        b[0] = uydown; //udown and vdown, u slope = 0
        
        a[1] = (uydown - wbdown)/(wmdown); //udown and wdown, u slope = 0
        b[1] = uydown; //udown and vdown, u slope = 0
        
        a[2] = (uydown - vbup)/(vmup); //udown and vup, u slope = 0
        b[2] = uydown; //udown and vdown, u slope = 0
        
        a[3] = (uydown - wbup)/(wmup); //udown and wup, u slope = 0
        b[3] = uydown; //udown and vdown, u slope = 0
        
        //uup
        a[4] = (uyup - vbdown)/(vmdown); //uup and vdown, u slope = 0
        b[4] = uyup; //uyup and vdown, u slope = 0
        
        a[5] = (uyup - wbdown)/(wmdown); //uup and wdown, u slope = 0
        b[5] = uyup; //uyup and wdown, u slope = 0
        
        a[6] = (uyup - vbup)/(vmup); //uup and vup, u slope = 0
        b[6] = uyup; //uyup and vup, u slope = 0
        
        a[7] = (uyup - wbup)/(wmup); //uup and wup, u slope = 0
        b[7] = uyup; //uyup and wup, u slope = 0
        
        //vup
        a[8] = (vbup - wbdown)/(wmdown - vmup);
        b[8] = vmup * a[8] + vbup;
        
        a[9] = (vbup - wbup)/(wmup - vmup); 
        b[9] = vmup * a[9] + vbup; 
        
        //vdown
        a[10] = (vbdown - wbup)/(wmup - vmdown); 
        b[10] = vmdown * a[10] + vbdown; 
        
        a[11] = (vbdown - wbdown)/(wmdown - vmdown); 
        b[11] = vmdown * a[11] + vbdown;
        
        //veto bad points by setting them = 999
        for(numpointsA = 0; numpointsA < 12; ++numpointsA)
        {
        	//System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
        	if(b[numpointsA] < uydown - 0.0001)
        	{
        		a[numpointsA] = 999;
        		b[numpointsA] = 999;
        	}
        	if(b[numpointsA] > uyup + 0.0001)
        	{
            	a[numpointsA] = 999;
            	b[numpointsA] = 999;
        	}
            	
            if(b[numpointsA] < vmdown * a[numpointsA] + vbdown - 0.0001)
            {
            	a[numpointsA] = 999;
            	b[numpointsA] = 999;
            }
            if(b[numpointsA] > vmup * a[numpointsA] + vbup + 0.0001)
            {
               	a[numpointsA] = 999;
                b[numpointsA] = 999;
            }
            
            if(b[numpointsA] < wmdown * a[numpointsA] + wbdown - 0.0001)
            {
                a[numpointsA] = 999;
                b[numpointsA] = 999;
            }
            if(b[numpointsA] > wmup * a[numpointsA] + wbup + 0.0001)
            {
	            a[numpointsA] = 999;
	            b[numpointsA] = 999;
            }
            
        }
        
        //organize good points in x and y array, count with numpoints
        numpoints = 0;
        int count = 0;
        int count2 = 0;
        int index = 0;
        double distance= 0.0;
        double mindist = 9000.0;
        double slopediff;
        int pass = 0;
        while(count < 12)
        {
        	if(a[count] < 900)
        	{
        		
        		if(numpoints == 0)
        		{
        			//System.out.println("x: " + a[count] + " y: " + b[count]);
        			x[numpoints] = a[count];
        			y[numpoints] = b[count];
        			++numpoints;
        			a[count] = 999;
        			b[count] = 999;
        			
        		}
        		else
        		{
        			mindist = 9000.0;
        			for(int i = 0; i < 12; ++i)
        			{
        				if(a[i] < 900 && b[i] < 900)
        				{
            				distance = Math.sqrt(Math.pow(a[i] - x[numpoints - 1], 2) + Math.pow(b[i] - y[numpoints - 1], 2));
            				if(distance < 0.0001) //throws out the overlapping points...
            				{
            					a[i] = 999;
		                		b[i] = 999;
		                		distance = 999.0;
            				}
            				else if(distance > 20.0) //throws out points really far away
            				{
            					a[i] = 999;
		                		b[i] = 999;
            				}
            				else
            				{
            					//test for on a straight line
            					pass = 0;
                				slopediff = Math.abs((b[i]-y[numpoints - 1])/(a[i]-x[numpoints - 1]));
                				//System.out.println("slope: " + slopediff + " distance: " + distance);
                				//System.out.println("vslope: " + vmup);
                				//System.out.println("wslope: " + wmup);
                				if( Math.abs(slopediff - Math.abs(vmup)) < 0.001) 
            					{
            						pass = 1;
            					}
                				else if( Math.abs(slopediff - Math.abs(wmup)) < 0.001) 
            					{
            						pass = 1;
            					}
                				else if( Math.abs(slopediff) < 0.0001 && Math.abs(b[i]/((int)(b[i]/4.5) * 4.5)) - 1.0 < 0.00001) 
            					{
            						pass = 1;
            						//System.out.println("x: " + a[index] + " y: " + b[index]);
            					}
                				else
                				{
                					distance = 999.0;
                				}
            				}

            				if(distance < mindist) //keeping on a straight predefined line
            				{
            					mindist = distance;
            					index = i;
            				}
        				}
        			}
        			//System.out.println("x: " + a[index] + " y: " + b[index]);
        			if(mindist < 50.0)
        			{
        				//System.out.println("x: " + a[index] + " y: " + b[index]);
        				x[numpoints] = a[index];
            			y[numpoints] = b[index];
            			++numpoints;
            			a[index] = 999;
            			b[index] = 999;
        			}
        		}
        	}
        	else
        	{
        		++count;
        	}
        	
        	
        	++count2;
        	if(count2 == 12 && count != 12)
        	{
        		count = 0;
        	}
        	
        }
		
		return(new Object[]{numpoints, x, y});
	}
	
	
	//returns an Object array of size 3
	//first element is the number of verticies (n) (int)
	//second element is an array x-coordinates (double[]) of size n
	//third element is an array y-coordinates (double[]) of size n
	public Object[] getOverlapVerticiesLoc(int sector, String strip1, int paddle1, String strip2, int paddle2){
		int uPaddle = -1;
		int vPaddle = -1;
		int wPaddle = -1;
		
		
		double x1, x2, y1, y2;
		int numpoints = 0;
        double x[] = new double [12];
        double y[] = new double [12];
        int numpointsA = 0;
        double a[] = new double [12];
        double b[] = new double [12];
        double uyup, uydown;
        double vmup, vmdown, vbup, vbdown;
        double wmup, wmdown, wbup, wbdown;
		
		
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
		
		//case 1: UW plane
		if(uPaddle != -1 && wPaddle != -1)
		{ 
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
		                
		                //maximum of 8 vertices for every 3 strips
		                //most of which can be thrown out later
		                //calculate all 8
		                
		                //udown
		                a[0] = (uydown - wbdown)/(wmdown); //udown and wdown, u slope = 0
		                b[0] = uydown; //udown and vdown, u slope = 0
		                
		                a[1] = (uydown - vbup)/(vmup); //udown and vup, u slope = 0
		                b[1] = uydown; //udown and vdown, u slope = 0
		                
		                a[2] = (uydown - wbup)/(wmup); //udown and wup, u slope = 0
		                b[2] = uydown; //udown and vdown, u slope = 0
		                
		                //uup
		                a[3] = (uyup - wbdown)/(wmdown); //uup and wdown, u slope = 0
		                b[3] = uyup; //uyup and wdown, u slope = 0
		                
		                a[4] = (uyup - vbup)/(vmup); //uup and vup, u slope = 0
		                b[4] = uyup; //uyup and vup, u slope = 0
		                
		                a[5] = (uyup - wbup)/(wmup); //uup and wup, u slope = 0
		                b[5] = uyup; //uyup and wup, u slope = 0
		                
		                //vup
		                a[6] = (vbup - wbdown)/(wmdown - vmup);
		                b[6] = vmup * a[6] + vbup;
		                
		                a[7] = (vbup - wbup)/(wmup - vmup); 
		                b[7] = vmup * a[7] + vbup; 
		                
		                
		                //veto bad points by setting them = 999
		                for(numpointsA = 0; numpointsA < 8; ++numpointsA)
		                {
		                	//System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
		                	if(b[numpointsA] < uydown - 0.0001)
		                	{
		                		a[numpointsA] = 999;
		                		b[numpointsA] = 999;
		                	}
		                	if(b[numpointsA] > uyup + 0.0001)
		                	{
			                	a[numpointsA] = 999;
			                	b[numpointsA] = 999;
		                	}

			                if(b[numpointsA] > vmup * a[numpointsA] + vbup + 0.0001)
			                {
				               	a[numpointsA] = 999;
				                b[numpointsA] = 999;
			                }
			                
				            if(b[numpointsA] < wmdown * a[numpointsA] + wbdown - 0.0001)
				            {
				                a[numpointsA] = 999;
				                b[numpointsA] = 999;
				            }
				            if(b[numpointsA] > wmup * a[numpointsA] + wbup + 0.0001)
				            {
					            a[numpointsA] = 999;
					            b[numpointsA] = 999;
				            }
				            //System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
				            
		                }
		                
		                //organize good points in x and y array, count with numpoints
		                numpoints = 0;
		                int count = 0;
		                int count2 = 0;
		                int index = 0;
		                double distance= 0.0;
		                double mindist = 9000.0;
		                double slopediff;
		                int pass = 0;
		                while(count < 8)
		                {
		                	if(a[count] < 900)
		                	{
		                		
		                		if(numpoints == 0)
		                		{
		                			//System.out.println("x: " + a[count] + " y: " + b[count]);
		                			x[numpoints] = a[count];
		                			y[numpoints] = b[count];
		                			++numpoints;
		                			a[count] = 999;
		                			b[count] = 999;
		                			
		                		}
		                		else
		                		{
		                			mindist = 9000.0;
		                			for(int i = 0; i < 8; ++i)
		                			{
		                				if(a[i] < 900 && b[i] < 900)
		                				{
			                				distance = Math.sqrt(Math.pow(a[i] - x[numpoints - 1], 2) + Math.pow(b[i] - y[numpoints - 1], 2));
			                				if(distance < 0.0001) //throws out the overlapping points...
			                				{
			                					a[i] = 999;
						                		b[i] = 999;
						                		distance = 999.0;
			                				}
			                				else if(distance > 20.0) //throws out points really far away
			                				{
			                					a[i] = 999;
						                		b[i] = 999;
			                				}
			                				else
			                				{
			                					//test for on a straight line
			                					pass = 0;
				                				slopediff = Math.abs((b[i]-y[numpoints - 1])/(a[i]-x[numpoints - 1]));
				                				//System.out.println("slope: " + slopediff + " distance: " + distance);
				                				//System.out.println("vslope: " + vmup);
				                				//System.out.println("wslope: " + wmup);
				                				if( Math.abs(slopediff - Math.abs(vmup)) < 0.001) 
			                					{
			                						pass = 1;
			                					}
				                				else if( Math.abs(slopediff - Math.abs(wmup)) < 0.001) 
			                					{
			                						pass = 1;
			                					}
				                				else if( Math.abs(slopediff) < 0.0001 && Math.abs(b[i]/((int)(b[i]/4.5) * 4.5)) - 1.0 < 0.00001) 
			                					{
			                						pass = 1;
			                						//System.out.println("x: " + a[index] + " y: " + b[index]);
			                					}
				                				else
				                				{
				                					distance = 999.0;
				                				}
			                				}

			                				if(distance < mindist) //keeping on a straight predefined line
			                				{
			                					mindist = distance;
			                					index = i;
			                				}
		                				}
		                			}
		                			//System.out.println("x: " + a[index] + " y: " + b[index]);
		                			if(mindist < 50.0)
		                			{
		                				//System.out.println("x: " + a[index] + " y: " + b[index]);
		                				x[numpoints] = a[index];
			                			y[numpoints] = b[index];
			                			++numpoints;
			                			a[index] = 999;
			                			b[index] = 999;
		                			}
		                		}
		                	}
		                	else
		                	{
		                		++count;
		                	}
		                	
		                	
		                	++count2;
		                	if(count2 == 8 && count != 8)
		                	{
		                		count = 0;
		                	}
		                	
		                }
		                
			}
			//case 2: UV plane
			else if(uPaddle != -1 && vPaddle != -1)
			{ 
    			wPaddle = 61;
                
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
                
                //maximum of 8 vertices for every 3 strips
                //most of which can be thrown out later
                //calculate all 8
                
                //udown
                a[0] = (uydown - vbdown)/(vmdown); //udown and vdown, u slope = 0
                b[0] = uydown; //udown and vdown, u slope = 0
                
                
                a[1] = (uydown - vbup)/(vmup); //udown and vup, u slope = 0
                b[1] = uydown; //udown and vdown, u slope = 0
                
                a[2] = (uydown - wbup)/(wmup); //udown and wup, u slope = 0
                b[2] = uydown; //udown and vdown, u slope = 0
                
                //uup
                a[3] = (uyup - vbdown)/(vmdown); //uup and vdown, u slope = 0
                b[3] = uyup; //uyup and vdown, u slope = 0

                
                a[4] = (uyup - vbup)/(vmup); //uup and vup, u slope = 0
                b[4] = uyup; //uyup and vup, u slope = 0
                
                a[5] = (uyup - wbup)/(wmup); //uup and wup, u slope = 0
                b[5] = uyup; //uyup and wup, u slope = 0
                
                //vup
                a[6] = (vbup - wbup)/(wmup - vmup); 
                b[6] = vmup * a[6] + vbup; 
                
                //vdown
                a[7] = (vbdown - wbup)/(wmup - vmdown); 
                b[7] = vmdown * a[7] + vbdown; 

                
                //veto bad points by setting them = 999
                for(numpointsA = 0; numpointsA < 8; ++numpointsA)
                {
                	//System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
                	if(b[numpointsA] < uydown - 0.0001)
                	{
                		a[numpointsA] = 999;
                		b[numpointsA] = 999;
                	}
                	if(b[numpointsA] > uyup + 0.0001)
                	{
	                	a[numpointsA] = 999;
	                	b[numpointsA] = 999;
                	}
	                	
	                if(b[numpointsA] < vmdown * a[numpointsA] + vbdown - 0.0001)
	                {
	                	a[numpointsA] = 999;
	                	b[numpointsA] = 999;
	                }
	                if(b[numpointsA] > vmup * a[numpointsA] + vbup + 0.0001)
	                {
		               	a[numpointsA] = 999;
		                b[numpointsA] = 999;
	                }
	                
		            if(b[numpointsA] > wmup * a[numpointsA] + wbup + 0.0001)
		            {
			            a[numpointsA] = 999;
			            b[numpointsA] = 999;
		            }
		            
                }
                
                //organize good points in x and y array, count with numpoints
                numpoints = 0;
                int count = 0;
                int count2 = 0;
                int index = 0;
                double distance= 0.0;
                double mindist = 9000.0;
                double slopediff;
                int pass = 0;
                while(count < 8)
                {
                	if(a[count] < 900)
                	{
                		
                		if(numpoints == 0)
                		{
                			//System.out.println("x: " + a[count] + " y: " + b[count]);
                			x[numpoints] = a[count];
                			y[numpoints] = b[count];
                			++numpoints;
                			a[count] = 999;
                			b[count] = 999;
                			
                		}
                		else
                		{
                			mindist = 9000.0;
                			for(int i = 0; i < 8; ++i)
                			{
                				if(a[i] < 900 && b[i] < 900)
                				{
	                				distance = Math.sqrt(Math.pow(a[i] - x[numpoints - 1], 2) + Math.pow(b[i] - y[numpoints - 1], 2));
	                				if(distance < 0.0001) //throws out the overlapping points...
	                				{
	                					a[i] = 999;
				                		b[i] = 999;
				                		distance = 999.0;
	                				}
	                				else if(distance > 20.0) //throws out points really far away
	                				{
	                					a[i] = 999;
				                		b[i] = 999;
	                				}
	                				else
	                				{
	                					//test for on a straight line
	                					pass = 0;
		                				slopediff = Math.abs((b[i]-y[numpoints - 1])/(a[i]-x[numpoints - 1]));
		                				//System.out.println("slope: " + slopediff + " distance: " + distance);
		                				//System.out.println("vslope: " + vmup);
		                				//System.out.println("wslope: " + wmup);
		                				if( Math.abs(slopediff - Math.abs(vmup)) < 0.001) 
	                					{
	                						pass = 1;
	                					}
		                				else if( Math.abs(slopediff - Math.abs(wmup)) < 0.001) 
	                					{
	                						pass = 1;
	                					}
		                				else if( Math.abs(slopediff) < 0.0001 && Math.abs(b[i]/((int)(b[i]/4.5) * 4.5)) - 1.0 < 0.00001) 
	                					{
	                						pass = 1;
	                						//System.out.println("x: " + a[index] + " y: " + b[index]);
	                					}
		                				else
		                				{
		                					distance = 999.0;
		                				}
	                				}

	                				if(distance < mindist) //keeping on a straight predefined line
	                				{
	                					mindist = distance;
	                					index = i;
	                				}
                				}
                			}
                			//System.out.println("x: " + a[index] + " y: " + b[index]);
                			if(mindist < 50.0)
                			{
                				//System.out.println("x: " + a[index] + " y: " + b[index]);
                				x[numpoints] = a[index];
	                			y[numpoints] = b[index];
	                			++numpoints;
	                			a[index] = 999;
	                			b[index] = 999;
                			}
                		}
                	}
                	else
                	{
                		++count;
                	}
                	
                	
                	++count2;
                	if(count2 == 8 && count != 8)
                	{
                		count = 0;
                	}
                	
                }
		                
			}
		//System.out.println("numpoints: " + numpoints);            
		return(new Object[]{numpoints, x, y});
	}

	
	//returns an Object array of size 3
	//first element is the number of verticies (n) (int)
	//second element is an array x-coordinates (double[]) of size n
	//third element is an array y-coordinates (double[]) of size n
	public Object[] getOverlapVerticies(int sector, String strip1, int paddle1, String strip2, int paddle2){
		

		
		Object[] obj = getVerticies(getStripShape(sector, strip1, paddle1),getStripShape(sector, strip1, paddle1));
		
		int numpoints = (int)obj[0];
		//System.out.println("Strip let: " + strip1 + "Strip num: " + paddle1 + " Numpoints: " + numpoints);
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		System.arraycopy( (double[])obj[1], 0, x, 0, numpoints);
		System.arraycopy( (double[])obj[2], 0, y, 0, numpoints);
		
		Object[] obj2 = sortVerticies(numpoints, x, y);
		int nPoints = 0;
		double[] xnew = new double[numpoints];
		double[] ynew = new double[numpoints];
		for(int i = 0; i < numpoints; ++i)
		{
			xnew[nPoints] = x[i];
			ynew[nPoints] = y[i];
			++nPoints;
			while((Math.abs(x[i] - xnew[nPoints - 1]) < 0.00001 && Math.abs(y[i] - ynew[nPoints - 1]) < 0.00001) || i == numpoints)
			{
				++i;
			}
			
		}
		
		return(new Object[]{nPoints, xnew, ynew});
		/*
		int[] layer = {-1, -1, -1};
		int lastcomponent = 67;

		double x1, x2, y1, y2;
		int numpoints = 0;
        double x[] = new double [12];
        double y[] = new double [12];
        int numpointsA = 0;
        double a[] = new double [12];
        double b[] = new double [12];
        double umup, umdown, ubup, ubdown;
        double vmup, vbup;
        double wmup, wmdown, wbup, wbdown;
		
		
		if((strip1 == "u" || strip1 == "U"))
		{
			layer[0] = 0;
		}
		if((strip2 == "u" || strip2 == "U"))
		{
			layer[1] = 0;
		}
		if((strip1 == "v" || strip1 == "V"))
		{
			layer[0] = 1;
		}
		if((strip2 == "v" || strip2 == "V"))
		{
			layer[1] = 1;
		}
		if((strip1 == "w" || strip1 == "W"))
		{
			layer[0] = 2;
		}
		if((strip2 == "w" || strip2 == "W"))
		{
			layer[1] = 2;
		}

		if(layer[0] != 0 && layer[1] != 0) layer[2] = 0;
		if(layer[0] != 1 && layer[1] != 1) layer[2] = 1;
		if(layer[0] != 2 && layer[1] != 2) layer[2] = 2;
		
		if(layer[2] == 0)lastcomponent = 67;
		else lastcomponent = 61;
		
		System.out.println("pad1: " + paddle1 + " pad2: " + paddle2);
		
		x1 = xPoint[sector][layer[0]][paddle1][0];
        y1 = yPoint[sector][layer[0]][paddle1][0];
        x2 = xPoint[sector][layer[0]][paddle1][1];
        y2 = yPoint[sector][layer[0]][paddle1][1];
        
        umup = (y2-y1)/(x2-x1);
        ubup = y1 - umup*x1;
        
        x1 = xPoint[sector][layer[0]][paddle1][2];
        y1 = yPoint[sector][layer[0]][paddle1][2];
        x2 = xPoint[sector][layer[0]][paddle1][3];
        y2 = yPoint[sector][layer[0]][paddle1][3];
        
        umdown = (y2-y1)/(x2-x1);
        ubdown = y1 - umdown*x1;
        
        System.out.println("umup: " + umup + " ubup: " + ubup);
        System.out.println("umdown: " + umdown + " ubdown: " + ubdown);
        
        if(xPoint[sector][layer[2]][lastcomponent - 1][0] < xPoint[sector][layer[2]][lastcomponent][0] 
        		&& xPoint[sector][layer[2]][lastcomponent][2] < xPoint[sector][layer[2]][lastcomponent][0])
        {
	        x1 = xPoint[sector][layer[2]][lastcomponent][0];
	        y1 = yPoint[sector][layer[2]][lastcomponent][0];
	        x2 = xPoint[sector][layer[2]][lastcomponent][1];
	        y2 = yPoint[sector][layer[2]][lastcomponent][1];
	        
	        vmup = (y2-y1)/(x2-x1);
	        vbup = y1 - vmup*x1;
        }
        else if(xPoint[sector][layer[2]][lastcomponent - 1][0] < xPoint[sector][layer[2]][lastcomponent][0] 
        		&& xPoint[sector][layer[2]][lastcomponent][2] > xPoint[sector][layer[2]][lastcomponent][0])
        {
        	x1 = xPoint[sector][layer[2]][lastcomponent][2];
	        y1 = yPoint[sector][layer[2]][lastcomponent][2];
	        x2 = xPoint[sector][layer[2]][lastcomponent][3];
	        y2 = yPoint[sector][layer[2]][lastcomponent][3];
	        
	        vmup = (y2-y1)/(x2-x1);
	        vbup = y1 - vmup*x1;
        }
        else if(xPoint[sector][layer[2]][lastcomponent - 1][0] > xPoint[sector][layer[2]][lastcomponent][0] 
        		&& xPoint[sector][layer[2]][lastcomponent][2] < xPoint[sector][layer[2]][lastcomponent][0])
        {
        	x1 = xPoint[sector][layer[2]][lastcomponent][2];
	        y1 = yPoint[sector][layer[2]][lastcomponent][2];
	        x2 = xPoint[sector][layer[2]][lastcomponent][3];
	        y2 = yPoint[sector][layer[2]][lastcomponent][3];
	        
	        vmup = (y2-y1)/(x2-x1);
	        vbup = y1 - vmup*x1;
        }
        else
        {
        	x1 = xPoint[sector][layer[2]][lastcomponent][0];
	        y1 = yPoint[sector][layer[2]][lastcomponent][0];
	        x2 = xPoint[sector][layer[2]][lastcomponent][1];
	        y2 = yPoint[sector][layer[2]][lastcomponent][1];
	        
	        vmup = (y2-y1)/(x2-x1);
	        vbup = y1 - vmup*x1;
        }
        System.out.println("vmup: " + vmup + " vbup: " + vbup);

        x1 = xPoint[sector][layer[1]][paddle2][0];
        y1 = yPoint[sector][layer[1]][paddle2][0];
        x2 = xPoint[sector][layer[1]][paddle2][1];
        y2 = yPoint[sector][layer[1]][paddle2][1];
        
        wmup = (y2-y1)/(x2-x1);
        wbup = y1 - wmup*x1;
        
        
        x1 = xPoint[sector][layer[1]][paddle2][2];
        y1 = yPoint[sector][layer[1]][paddle2][2];
        x2 = xPoint[sector][layer[1]][paddle2][3];
        y2 = yPoint[sector][layer[1]][paddle2][3];
        
        wmdown = (y2-y1)/(x2-x1);
        wbdown = y1 - wmdown*x1;
        
        System.out.println("wmup: " + wmup + " wbup: " + wbup);
        System.out.println("wmdown: " + wmdown + " wbdown: " + wbdown);
        
        if(Math.abs(umup) > 90.0 || ((Double)umup).isInfinite() )
        {
        	//udown
            a[0] = xPoint[sector][layer[0]][paddle1][2]; //udown and wdown
            b[0] = wmdown * a[0] + wbdown; 
            
            a[1] = xPoint[sector][layer[0]][paddle1][2]; //udown and vup
            b[1] = vmup * a[1] + vbup;
            
            a[2] = xPoint[sector][layer[0]][paddle1][2]; //udown and wup
            b[2] = wmup * a[2] + wbup;
            
            //uup
            a[3] = xPoint[sector][layer[0]][paddle1][0]; //uup and wdown
            b[3] = wmdown * a[3] + wbdown;
            
            a[4] = xPoint[sector][layer[0]][paddle1][0]; //uup and vup
            b[4] = vmup * a[4] + vbup;
            
            a[5] = xPoint[sector][layer[0]][paddle1][0]; //uup and wup
            b[5] = wmup * a[5] + wbup;
            
            //vup
            a[6] = (vbup - wbdown)/(wmdown - vmup); //vup and wdown
            b[6] = vmup * a[6] + vbup;
            
            a[7] = (vbup - wbup)/(wmup - vmup); //vup and wup
            b[7] = vmup * a[7] + vbup; 
     
        }
        else if(Math.abs(wmup) > 90.0 || ((Double)wmup).isInfinite())
        {
        	//udown
            a[0] = xPoint[sector][layer[1]][paddle2][2]; //udown and wdown
            b[0] = umdown * a[0] + ubdown; 
            
            a[1] = (ubdown - vbup)/(vmup - umdown); //udown and vup
            b[1] = umdown * a[1] + ubdown;
            
            a[2] = xPoint[sector][layer[1]][paddle2][0]; //udown and wup
            b[2] = umdown * a[2] + ubdown;
            
            //uup
            a[3] = xPoint[sector][layer[1]][paddle2][2]; //uup and wdown
            b[3] = umup * a[3] + ubup;
            
            a[4] = (ubup - vbup)/(vmup - umup); //uup and vup
            b[4] = umup * a[4] + ubup;
            
            a[5] = xPoint[sector][layer[1]][paddle2][0]; //uup and wup
            b[5] = umup * a[5] + ubup;
            
            //vup
            a[6] = xPoint[sector][layer[1]][paddle2][2];
            b[6] = vmup * a[6] + vbup;
            
            a[7] = xPoint[sector][layer[1]][paddle2][0]; 
            b[7] = vmup * a[7] + vbup; 
     
        }
        else if(Math.abs(vmup) > 90.0 || ((Double)vmup).isInfinite())
        {
        	//udown
            a[0] = (ubdown - wbdown)/(wmdown - umdown); //udown and wdown
            b[0] = umdown * a[0] + ubdown; 
            
            a[1] = xPoint[sector][layer[2]][lastcomponent][0]; //udown and vup
            b[1] = umdown * a[1] + ubdown;
            
            a[2] = (ubdown - wbup)/(wmup - umdown); //udown and wup
            b[2] = umdown * a[2] + ubdown;
            
            //uup
            a[3] = (ubup - wbdown)/(wmdown - umup); //uup and wdown
            b[3] = umup * a[3] + ubup;
            
            a[4] = xPoint[sector][layer[2]][lastcomponent][0]; //uup and vup
            b[4] = umup * a[4] + ubup;
            
            a[5] = (ubup - wbup)/(wmup - umup); //uup and wup
            b[5] = umup * a[5] + ubup;
            
            //vup
            a[6] = xPoint[sector][layer[2]][lastcomponent][0];
            b[6] = wmdown * a[6] + wbdown;
            
            a[7] = xPoint[sector][layer[2]][lastcomponent][0]; 
            b[7] = wmup * a[7] + wbup; 
     
        }
        else
        {
            //udown
            a[0] = (ubdown - wbdown)/(wmdown - umdown); //udown and wdown
            b[0] = umdown * a[0] + ubdown; 
            
            a[1] = (ubdown - vbup)/(vmup - umdown); //udown and vup
            b[1] = umdown * a[1] + ubdown;
            
            a[2] = (ubdown - wbup)/(wmup - umdown); //udown and wup
            b[2] = umdown * a[2] + ubdown;
            
            //uup
            a[3] = (ubup - wbdown)/(wmdown - umup); //uup and wdown
            b[3] = umup * a[3] + ubup;
            
            a[4] = (ubup - vbup)/(vmup - umup); //uup and vup
            b[4] = umup * a[4] + ubup;
            
            a[5] = (ubup - wbup)/(wmup - umup); //uup and wup
            b[5] = umup * a[5] + ubup;
            
            //vup
            a[6] = (vbup - wbdown)/(wmdown - vmup);
            b[6] = vmup * a[6] + vbup;
            
            a[7] = (vbup - wbup)/(wmup - vmup); 
            b[7] = vmup * a[7] + vbup; 
        }
		
        System.out.println("       ");
        System.out.println("       ");
        //veto bad points by setting them = 999
        for(numpointsA = 0; numpointsA < 8; ++numpointsA)
        {
        	System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);

        	if(!this.isContained3Decimal(getStripShape(sector, strip1, paddle1), a[numpointsA], b[numpointsA]))
        	{
        		//System.out.println("This shouldn't fire");
        		a[numpointsA] = 999;
        		b[numpointsA] = 999;
        	}
        	if(!this.isContained3Decimal(getStripShape(sector, strip2, paddle2), a[numpointsA], b[numpointsA]))
        	{
        		//System.out.println("This shouldn't fire");
        		a[numpointsA] = 999;
        		b[numpointsA] = 999;
        	}
        	System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
        	
        }
		
        //organize good points in x and y array, count with numpoints
        numpoints = 0;
        int count = 0;
        int count2 = 0;
        int index = 0;
        double distance= 0.0;
        double mindist = 9000.0;
        double slopediff;
        while(count < 8)
        {
        	if(a[count] < 900)
        	{
        		
        		if(numpoints == 0)
        		{
        			//System.out.println("x: " + a[count] + " y: " + b[count]);
        			x[numpoints] = a[count];
        			y[numpoints] = b[count];
        			++numpoints;
        			a[count] = 999;
        			b[count] = 999;
        			
        		}
        		else
        		{
        			mindist = 9000.0;
        			for(int i = 0; i < 8; ++i)
        			{
        				if(a[i] < 900 && b[i] < 900)
        				{
            				distance = Math.sqrt(Math.pow(a[i] - x[numpoints - 1], 2) + Math.pow(b[i] - y[numpoints - 1], 2));
            				slopediff = Math.abs((b[i]-y[numpoints - 1])/(a[i]-x[numpoints - 1]));
            				if(distance < 0.0001) //throws out the overlapping points...
            				{
            					a[i] = 999;
		                		b[i] = 999;
		                		distance = 999.0;
            				}
            				if(Math.abs(slopediff) > 90.0) //undefined slope
            				{
            					if(Math.abs(umup) < 90.0 && Math.abs(vmup) < 90.0 && Math.abs(wmup) < 90.0)
                				{
            						//not allowed if no strips have it
            						distance = 999.0;
                				}
            				}
            				else
            				{
            					if(Math.abs(Math.abs(slopediff) - Math.abs(umup)) > 0.001 && Math.abs(Math.abs(slopediff) - Math.abs(vmup)) > 0.001 && Math.abs(Math.abs(slopediff) - Math.abs(wmup)) > 0.001)
            					{
            						//doesn't follow any of the slopes
            						distance = 999.0;
            					}
            				}
            				//System.out.println(" distance: " + distance);
            				if(distance < mindist) //keeping on a straight predefined line
            				{
            					mindist = distance;
            					index = i;
            				}
        				}
        			}
        			//System.out.println("x: " + a[index] + " y: " + b[index]);
        			if(mindist < 50.0)
        			{
        				//System.out.println("x: " + a[index] + " y: " + b[index]);
        				x[numpoints] = a[index];
            			y[numpoints] = b[index];
            			++numpoints;
            			a[index] = 999;
            			b[index] = 999;
        			}
        		}
        	}
        	else
        	{
        		++count;
        	}
        	
        	
        	++count2;
        	if(count2 == 8 && count != 8)
        	{
        		count = 0;
        	}
        	
        }
        */
		//System.out.println("numpoints: " + numpoints);            
		//return(new Object[]{numpoints, x, y});
	}
	
	
	//returns an Object array of size 3
	//first element is the number of verticies (n) (int)
	//second element is an array x-coordinates (double[]) of size n
	//third element is an array y-coordinates (double[]) of size n
	public Object[] getStripVerticiesLoc(int sector, String strip1, int paddle1){
		int uPaddle = -1;
		int vPaddle = -1;
		int wPaddle = -1;
		
		
		double x1, x2, y1, y2;
		int numpoints = 0;
        double x[] = new double [12];
        double y[] = new double [12];
        int numpointsA = 0;
        double a[] = new double [12];
        double b[] = new double [12];
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
		                
		                //maximum of 8 vertices for every 3 strips
		                //most of which can be thrown out later
		                //calculate all 8
		                
		                //udown
		                a[0] = (uydown - vbup)/(vmup); //udown and vup, u slope = 0
		                b[0] = uydown; //udown and vdown, u slope = 0
		                
		                a[1] = (uydown - wbup)/(wmup); //udown and wup, u slope = 0
		                b[1] = uydown; //udown and vdown, u slope = 0
		                
		                //uup
		                a[2] = (uyup - vbup)/(vmup); //uup and vup, u slope = 0
		                b[2] = uyup; //uyup and vup, u slope = 0
		                
		                a[3] = (uyup - wbup)/(wmup); //uup and wup, u slope = 0
		                b[3] = uyup; //uyup and wup, u slope = 0
		                
		                //vup
		                a[4] = (vbup - wbup)/(wmup - vmup); 
		                b[4] = vmup * a[4] + vbup; 
		                
		                
		                //veto bad points by setting them = 999
		                for(numpointsA = 0; numpointsA < 5; ++numpointsA)
		                {
		                	//System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
		                	if(b[numpointsA] < uydown - 0.0001)
		                	{
		                		a[numpointsA] = 999;
		                		b[numpointsA] = 999;
		                	}
		                	if(b[numpointsA] > uyup + 0.0001)
		                	{
			                	a[numpointsA] = 999;
			                	b[numpointsA] = 999;
		                	}

			                if(b[numpointsA] > vmup * a[numpointsA] + vbup + 0.0001)
			                {
				               	a[numpointsA] = 999;
				                b[numpointsA] = 999;
			                }
				            if(b[numpointsA] > wmup * a[numpointsA] + wbup + 0.0001)
				            {
					            a[numpointsA] = 999;
					            b[numpointsA] = 999;
				            }
				            //System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
				            
		                }
		                
		                //organize good points in x and y array, count with numpoints
		                numpoints = 0;
		                int count = 0;
		                int count2 = 0;
		                int index = 0;
		                double distance= 0.0;
		                double mindist = 9000.0;
		                double slopediff;
		                int pass = 0;
		                while(count < 5)
		                {
		                	//System.out.println("x: " + a[count] + " y: " + b[count]);
		                	if(a[count] < 900)
		                	{
		                		
		                		if(numpoints == 0)
		                		{
		                			//System.out.println("x: " + a[count] + " y: " + b[count]);
		                			x[numpoints] = a[count];
		                			y[numpoints] = b[count];
		                			++numpoints;
		                			a[count] = 999;
		                			b[count] = 999;
		                			
		                		}
		                		else
		                		{
		                			mindist = 9000.0;
		                			for(int i = 0; i < 5; ++i)
		                			{
		                				if(a[i] < 900 && b[i] < 900)
		                				{
			                				distance = Math.sqrt(Math.pow(a[i] - x[numpoints - 1], 2) + Math.pow(b[i] - y[numpoints - 1], 2));
			                				if(distance < 0.0001) //throws out the overlapping points...
			                				{
			                					a[i] = 999;
						                		b[i] = 999;
						                		distance = 999.0;
			                				}
			                				/*
			                				else if(distance > 20.0) //throws out points really far away
			                				{
			                					a[i] = 999;
						                		b[i] = 999;
			                				}
			                				*/
			                				else
			                				{
			                					//test for on a straight line
			                					pass = 0;
				                				slopediff = Math.abs((b[i]-y[numpoints - 1])/(a[i]-x[numpoints - 1]));
				                				//System.out.println("slope: " + slopediff + " distance: " + distance);
				                				//System.out.println("vslope: " + vmup);
				                				//System.out.println("wslope: " + wmup);
				                				if( Math.abs(slopediff - Math.abs(vmup)) < 0.001) 
			                					{
			                						pass = 1;
			                					}
				                				else if( Math.abs(slopediff - Math.abs(wmup)) < 0.001) 
			                					{
			                						pass = 1;
			                					}
				                				else if( Math.abs(slopediff) < 0.0001 && Math.abs(b[i]/((int)(b[i]/4.5) * 4.5)) - 1.0 < 0.00001) 
			                					{
			                						pass = 1;
			                						//System.out.println("x: " + a[index] + " y: " + b[index]);
			                					}
				                				else
				                				{
				                					distance = 999.0;
				                				}
			                				}

			                				if(distance < mindist) //keeping on a straight predefined line
			                				{
			                					mindist = distance;
			                					index = i;
			                				}
		                				}
		                			}
		                			//System.out.println("x: " + a[index] + " y: " + b[index]);
		                			
		                			if(mindist < 500.0)
		                			{
		                				//System.out.println("x: " + a[index] + " y: " + b[index]);
		                				x[numpoints] = a[index];
			                			y[numpoints] = b[index];
			                			++numpoints;
			                			a[index] = 999;
			                			b[index] = 999;
		                			}
		                			
		                		}
		                	}
		                	else
		                	{
		                		++count;
		                	}
		                	
		                	
		                	++count2;
		                	if(count2 == 5 && count != 5)
		                	{
		                		count = 0;
		                	}
		                	
		                }
		                
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
                
                //maximum of 8 vertices for every 3 strips
                //most of which can be thrown out later
                //calculate all 8
                
                //udown
                a[0] = (uydown - vbdown)/(vmdown); //udown and vdown, u slope = 0
                b[0] = uydown; //udown and vdown, u slope = 0
                
                a[1] = (uydown - vbup)/(vmup); //udown and vup, u slope = 0
                b[1] = uydown; //udown and vdown, u slope = 0
                
                a[2] = (uydown - wbup)/(wmup); //udown and wup, u slope = 0
                b[2] = uydown; //udown and vdown, u slope = 0

                //vup
                a[3] = (vbup - wbup)/(wmup - vmup); 
                b[3] = vmup * a[3] + vbup; 
                
                //vdown
                a[4] = (vbdown - wbup)/(wmup - vmdown); 
                b[4] = vmdown * a[4] + vbdown; 

                
                //veto bad points by setting them = 999
                for(numpointsA = 0; numpointsA < 5; ++numpointsA)
                {
                	//System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
                	if(b[numpointsA] < uydown - 0.0001)
                	{
                		a[numpointsA] = 999;
                		b[numpointsA] = 999;
                	}                	
	                if(b[numpointsA] < vmdown * a[numpointsA] + vbdown - 0.0001)
	                {
	                	a[numpointsA] = 999;
	                	b[numpointsA] = 999;
	                }
	                if(b[numpointsA] > vmup * a[numpointsA] + vbup + 0.0001)
	                {
		               	a[numpointsA] = 999;
		                b[numpointsA] = 999;
	                }
	                
		            if(b[numpointsA] > wmup * a[numpointsA] + wbup + 0.0001)
		            {
			            a[numpointsA] = 999;
			            b[numpointsA] = 999;
		            }
		            
                }
                
                //organize good points in x and y array, count with numpoints
                numpoints = 0;
                int count = 0;
                int count2 = 0;
                int index = 0;
                double distance= 0.0;
                double mindist = 9000.0;
                double slopediff;
                int pass = 0;
                while(count < 5)
                {
                	if(a[count] < 900)
                	{
                		
                		if(numpoints == 0)
                		{
                			//System.out.println("x: " + a[count] + " y: " + b[count]);
                			x[numpoints] = a[count];
                			y[numpoints] = b[count];
                			++numpoints;
                			a[count] = 999;
                			b[count] = 999;
                			
                		}
                		else
                		{
                			mindist = 9000.0;
                			for(int i = 0; i < 5; ++i)
                			{
                				if(a[i] < 900 && b[i] < 900)
                				{
	                				distance = Math.sqrt(Math.pow(a[i] - x[numpoints - 1], 2) + Math.pow(b[i] - y[numpoints - 1], 2));
	                				if(distance < 0.0001) //throws out the overlapping points...
	                				{
	                					a[i] = 999;
				                		b[i] = 999;
				                		distance = 999.0;
	                				}
	                				/*
	                				else if(distance > 20.0) //throws out points really far away
	                				{
	                					a[i] = 999;
				                		b[i] = 999;
	                				}
	                				*/
	                				else
	                				{
	                					//test for on a straight line
	                					pass = 0;
		                				slopediff = Math.abs((b[i]-y[numpoints - 1])/(a[i]-x[numpoints - 1]));
		                				//System.out.println("slope: " + slopediff + " distance: " + distance);
		                				//System.out.println("vslope: " + vmup);
		                				//System.out.println("wslope: " + wmup);
		                				if( Math.abs(slopediff - Math.abs(vmup)) < 0.001) 
	                					{
	                						pass = 1;
	                					}
		                				else if( Math.abs(slopediff - Math.abs(wmup)) < 0.001) 
	                					{
	                						pass = 1;
	                					}
		                				else if( Math.abs(slopediff) < 0.0001 && Math.abs(b[i]/((int)(b[i]/4.5) * 4.5)) - 1.0 < 0.00001) 
	                					{
	                						pass = 1;
	                						//System.out.println("x: " + a[index] + " y: " + b[index]);
	                					}
		                				else
		                				{
		                					distance = 999.0;
		                				}
	                				}

	                				if(distance < mindist) //keeping on a straight predefined line
	                				{
	                					mindist = distance;
	                					index = i;
	                				}
                				}
                			}
                			//System.out.println("x: " + a[index] + " y: " + b[index]);
                			if(mindist < 500.0)
                			{
                				//System.out.println("x: " + a[index] + " y: " + b[index]);
                				x[numpoints] = a[index];
	                			y[numpoints] = b[index];
	                			++numpoints;
	                			a[index] = 999;
	                			b[index] = 999;
                			}
                		}
                	}
                	else
                	{
                		++count;
                	}
                	
                	
                	++count2;
                	if(count2 == 5 && count != 5)
                	{
                		count = 0;
                	}
                	
                }
		                
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
		                
		                //maximum of 8 vertices for every 3 strips
		                //most of which can be thrown out later
		                //calculate all 8
		                
		                //udown
		                a[0] = (uydown - wbdown)/(wmdown); //udown and wdown, u slope = 0
		                b[0] = uydown; //udown and vdown, u slope = 0
		                
		                a[1] = (uydown - vbup)/(vmup); //udown and vup, u slope = 0
		                b[1] = uydown; //udown and vdown, u slope = 0
		                
		                a[2] = (uydown - wbup)/(wmup); //udown and wup, u slope = 0
		                b[2] = uydown; //udown and vdown, u slope = 0

		                //vup
		                a[3] = (vbup - wbdown)/(wmdown - vmup);
		                b[3] = vmup * a[3] + vbup;
		                
		                a[4] = (vbup - wbup)/(wmup - vmup); 
		                b[4] = vmup * a[4] + vbup; 
		                
		                
		                //veto bad points by setting them = 999
		                for(numpointsA = 0; numpointsA < 5; ++numpointsA)
		                {
		                	//System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
		                	if(b[numpointsA] < uydown - 0.0001)
		                	{
		                		a[numpointsA] = 999;
		                		b[numpointsA] = 999;
		                	}
			                if(b[numpointsA] > vmup * a[numpointsA] + vbup + 0.0001)
			                {
				               	a[numpointsA] = 999;
				                b[numpointsA] = 999;
			                }
			                
				            if(b[numpointsA] < wmdown * a[numpointsA] + wbdown - 0.0001)
				            {
				                a[numpointsA] = 999;
				                b[numpointsA] = 999;
				            }
				            if(b[numpointsA] > wmup * a[numpointsA] + wbup + 0.0001)
				            {
					            a[numpointsA] = 999;
					            b[numpointsA] = 999;
				            }
				            //System.out.println("x: " + a[numpointsA] + " y: " + b[numpointsA]);
				            
		                }
		                
		                //organize good points in x and y array, count with numpoints
		                numpoints = 0;
		                int count = 0;
		                int count2 = 0;
		                int index = 0;
		                double distance= 0.0;
		                double mindist = 9000.0;
		                double slopediff;
		                int pass = 0;
		                while(count < 5)
		                {
		                	if(a[count] < 900)
		                	{
		                		
		                		if(numpoints == 0)
		                		{
		                			//System.out.println("x: " + a[count] + " y: " + b[count]);
		                			x[numpoints] = a[count];
		                			y[numpoints] = b[count];
		                			++numpoints;
		                			a[count] = 999;
		                			b[count] = 999;
		                			
		                		}
		                		else
		                		{
		                			mindist = 9000.0;
		                			for(int i = 0; i < 5; ++i)
		                			{
		                				if(a[i] < 900 && b[i] < 900)
		                				{
			                				distance = Math.sqrt(Math.pow(a[i] - x[numpoints - 1], 2) + Math.pow(b[i] - y[numpoints - 1], 2));
			                				if(distance < 0.0001) //throws out the overlapping points...
			                				{
			                					a[i] = 999;
						                		b[i] = 999;
						                		distance = 999.0;
			                				}
			                				/*
			                				else if(distance > 20.0) //throws out points really far away
			                				{
			                					a[i] = 999;
						                		b[i] = 999;
			                				}
			                				*/
			                				else
			                				{
			                					//test for on a straight line
			                					pass = 0;
				                				slopediff = Math.abs((b[i]-y[numpoints - 1])/(a[i]-x[numpoints - 1]));
				                				//System.out.println("slope: " + slopediff + " distance: " + distance);
				                				//System.out.println("vslope: " + vmup);
				                				//System.out.println("wslope: " + wmup);
				                				if( Math.abs(slopediff - Math.abs(vmup)) < 0.001) 
			                					{
			                						pass = 1;
			                					}
				                				else if( Math.abs(slopediff - Math.abs(wmup)) < 0.001) 
			                					{
			                						pass = 1;
			                					}
				                				else if( Math.abs(slopediff) < 0.0001 && Math.abs(b[i]/((int)(b[i]/4.5) * 4.5)) - 1.0 < 0.00001) 
			                					{
			                						pass = 1;
			                						//System.out.println("x: " + a[index] + " y: " + b[index]);
			                					}
				                				else
				                				{
				                					distance = 999.0;
				                				}
			                				}

			                				if(distance < mindist) //keeping on a straight predefined line
			                				{
			                					mindist = distance;
			                					index = i;
			                				}
		                				}
		                			}
		                			
		                			//System.out.println("x: " + a[index] + " y: " + b[index]);
		                			if(mindist < 500.0)
		                			{
		                				//System.out.println("x: " + a[index] + " y: " + b[index]);
		                				x[numpoints] = a[index];
			                			y[numpoints] = b[index];
			                			++numpoints;
			                			a[index] = 999;
			                			b[index] = 999;
		                			}
		                			
		                		}
		                	}
		                	else
		                	{
		                		++count;
		                	}
		                	
		                	
		                	++count2;
		                	if(count2 == 8 && count != 8)
		                	{
		                		count = 0;
		                	}
		                	
		                }
		                
			}
		//System.out.println("numpoints: " + numpoints);            
		return(new Object[]{numpoints, x, y});
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
	
	
	private Boolean isWithinLines(double x, double y, double slope1, double b1, double slope2, double b2){
		
		Boolean value = false;
		
		//System.out.println("x: " + x + " y: " + y);
		double calcy1 = slope1 * x + b1;
		double calcy2 = slope2 * x + b2;
		//System.out.println("calc y1: " + calcy1);
		//System.out.println("calc y2: " + calcy2);
    	if(y < slope1 * x + b1 + 0.0001 && y > slope2 * x + b2 - 0.0001)
    	{
    		value = true;
    	}
    	else if(y > slope1 * x + b1 - 0.0001 && y < slope2 * x + b2 + 0.0001)
    	{
    		value = true;
    	}
    	
		return value;
	}
	
	private void initVert()
	{
		ScintillatorPaddle paddle;
		ECLayer  ecLayer;
		int sector = 0;
		int lastcomponent = 67;
		Point3D point1 = new Point3D(), point2 = new Point3D(), point3  = new Point3D();
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
		        Point3D ab = new Point3D(point2);
		        Point3D ac = new Point3D(point3);
		
		        ab.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        ac.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        
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
		        ab = new Point3D(point2);
		        ac = new Point3D(point3);
		
		        ab.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        ac.translateXYZ(-point1.x(),-point1.y(),-point1.z());
		        
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
	            	point1 = paddle2.getVolumePoint(0);
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
	            	//if(paddle2.getComponentId() == 61)System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
	            	//System.out.println("Component ID: " + paddle2.getComponentId());
	            	xPoint[sector][l][paddle2.getComponentId()][0] = point1.x();
	            	yPoint[sector][l][paddle2.getComponentId()][0] = point1.y();
	            	
	            	point1 = paddle2.getVolumePoint(4);
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
	            	//if(paddle2.getComponentId() == 61)System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
	            	//System.out.println("Component ID: " + paddle2.getComponentId());
	            	xPoint[sector][l][paddle2.getComponentId()][1] = point1.x();
	            	yPoint[sector][l][paddle2.getComponentId()][1] = point1.y();
	            	
	            	point1 = paddle2.getVolumePoint(5);
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
	            	//System.out.println("x: " + point1.x() + " y: " + point1.y() + " z: " + point1.z());
	            	//System.out.println("Component ID: " + paddle2.getComponentId());
	            	xPoint[sector][l][paddle2.getComponentId()][2] = point1.x();
	            	yPoint[sector][l][paddle2.getComponentId()][2] = point1.y();
	            	
	            	point1 = paddle2.getVolumePoint(1);
	            	point1.rotateX(xrotation[sector]);
	            	point1.rotateY(yrotation[sector]);
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
        boolean c = false;
        int nvert = shape.getShapePath().size();
        for (i = 0, j = nvert-1; i < nvert; j = i++) {
        	if ( ( ( shape.getShapePath().point(i).y()  > y ) !=  ( shape.getShapePath().point(j).y() > y  ) ) )
            {
                    if((        x  < 
                    		 (   shape.getShapePath().point(j).x() 
                    		           -shape.getShapePath().point(i).x()   ) 
                    		       * (  y 
                    		           -shape.getShapePath().point(j).y()   ) 
                    		       / (  shape.getShapePath().point(j).y()
                    		           -shape.getShapePath().point(i).y()   ) 
                    		       + shape.getShapePath().point(j).x()       )   )
                    {
                    	c = !c;
                    }
            }
        	if( (int)(shape.getShapePath().point(i).y() * 1000.0) == (int)(y * 1000.0) || (int)(shape.getShapePath().point(j).y() * 1000.0) == (int)(y * 1000.0)  )
            {
                    if((        (int)(x * 1000.0) == 
                    		 (int)(( (  shape.getShapePath().point(j).x() 
                    		           -shape.getShapePath().point(i).x()   ) 
                    		       * (  y 
                    		           -shape.getShapePath().point(j).y()   ) 
                    		       / (  shape.getShapePath().point(j).y()
                    		           -shape.getShapePath().point(i).y()   ) 
                    		       + shape.getShapePath().point(j).x()    ) * 1000.0)  )   )
                    {
                    	c = true;
                    }
            }
        }
        return c;
    }
	
	public Object[] getVerticies(DetectorShape2D shape1, DetectorShape2D shape2){
		int numpoints = 0;
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
			for(int j = 0; j < shape2.getShapePath().size() - 1; ++j)
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
				
				//calculate line 2
		        m2 = (point2A.y()-point2B.y())/(point2A.x()-point2B.x());
		        b2 = point2A.y() - m2*point2A.x();
				
		        if(Math.abs(m1 - m2) > 0.00001)
		        {
		        	//not parallel
			        if(((Double)m1).isInfinite())
			        {
			        	xtemp[numpoints] = point1A.x();
			        	ytemp[numpoints] = m2 * xtemp[numpoints] + b2;
			        }
			        else if(((Double)m2).isInfinite())
			        {
			        	xtemp[numpoints] = point2A.x();
			        	ytemp[numpoints] = m1 * xtemp[numpoints] + b1;
			        }
			        else
			        {
			        	xtemp[numpoints] = (b1 - b2)/(m2 - m1); 
			        	ytemp[numpoints] = m1 * xtemp[numpoints] + b1; 
			        }
			        ++numpoints;
		        }
				
				
			}
		}
		
		double[] x = new double[numpoints];
		double[] y = new double[numpoints];
		
		for(int i = 0; i < numpoints; ++i)
		{
			System.out.println( "Found: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
			if(isContained3Decimal(shape1, xtemp[i], ytemp[i]) && isContained3Decimal(shape2, xtemp[i], ytemp[i]))
			{
				System.out.println( "Accepted: " +"x: " + xtemp[i] + " y: " + ytemp[i]);
				x[nPoints] = xtemp[i];
				y[nPoints] = ytemp[i];
				++nPoints;
				
			}
		}
		
		return(new Object[]{numpoints, x, y});
	}
	
	public Object[] sortVerticies(int num, double[] x, double[] y)
	{
		double[] xnew = new double[num];
		double[] ynew = new double[num];
		int[] used = new int[num];
		
		double ymin = 0;
		double minangle = 0;
		int index = 0;
		int numpoints = 0;
		int i = 0;
		
		for(i = 0; i < num; ++i)
		{
			if(i == 0)
			{
				ymin = y[i];
				index = i;
			}
			else if(ymin > y[i])
			{
				ymin = y[i];
				index = i;
			}
			used[i] = 0;
		}
		
		//start with minimum y
		//look to -x as a function of theta
		while(numpoints < num)
		{
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
						index = -1;
						++numpoints;
						System.out.println("x: " + xnew[numpoints] + " y: " + ynew[numpoints]);
					}
				}
			}
			
			minangle = 90000.0;
			for(i = 0; i < num; ++i)
			{
				if(used[i] == 0) //only loop through unused points
				{
					if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
					{
						//look in clockwise direction from 6 o' clock
						minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
						index = i;
					}
				}
			}
			
			if(index == -1)
			{
				for(i = 0; i < num; ++i)
				{
					if(used[i] == 0) //only loop through unused points
					{
						if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] < 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
						{
							//look in clockwise direction from 6 o' clock
							minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
							index = i;
						}
					}
				}
			}
			
			if(index == -1)
			{
				for(i = 0; i < num; ++i)
				{
					if(used[i] == 0) //only loop through unused points
					{
						if(y[i] - ynew[numpoints - 1] > 0 && x[i] - xnew[numpoints - 1] > 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
						{
							//look in clockwise direction from 6 o' clock
							minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
							index = i;
						}
					}
				}
			}
			
			if(index == -1)
			{
				for(i = 0; i < num; ++i)
				{
					if(used[i] == 0) //only loop through unused points
					{
						if(y[i] - ynew[numpoints - 1] < 0 && x[i] - xnew[numpoints - 1] > 0 && minangle > Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) )))
						{
							//look in clockwise direction from 6 o' clock
							minangle = Math.abs(Math.atan((y[i] - ynew[numpoints - 1])/(x[i] - xnew[numpoints - 1]) ));
							index = i;
						}
					}
				}
			}
		}
		
		return(new Object[]{numpoints, xnew, ynew});
	}
	
	public static void main(String[] args){ 
		
		PCALDrawDB pcaltest = new PCALDrawDB();
		
		
		char stripLetter[] = {'u','v','w'};
		char stripLetter2[] = {'w','u','u'};
		String cstring1 = ""+stripLetter[0];//Character.toString(stripLetter[0]);
		String cstring2 = ""+stripLetter2[0];//Character.toString(stripLetter2[0]);
		int strip = 38;
		int crossStrip = 31;
		//System.out.println("pad1: " + strip + " pad2: " + crossStrip);
		//double x = pcaltest.getOverlapDistance(cstring1,strip,cstring2,crossStrip);
		//System.out.println("x: " + x);
		
		
		//x = pcaltest.CalcDistinStrips('u',32)[0];
		//x = pcaltest.CalcDistance('u',x,0)[0];
		//System.out.println("x: " + x);
		
		
		EmbeddedCanvas canvas = new EmbeddedCanvas();
		
		DetectorShapeTabView  view   = new DetectorShapeTabView();
		//draw U strips
		/*
    	DetectorShapeView2D[]  dv = new DetectorShapeView2D[6];
    	for(int i = 0; i < 2; ++i)
    	{
    		dv[i] = new DetectorShapeView2D("PCAL UW " + i);
    		dv[i] = pcaltest.drawWU(i);
    		view.addDetectorLayer(dv[i]);
    	}
    	canvas.add(view);
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
	    //DetectorShapeView2D  dv5 = new DetectorShapeView2D("PCAL WU");
	    //dv5 = pcaltest.drawWU(0);
    	
    	
		DetectorShape2D shape = new DetectorShape2D();
    	 	DetectorShapeView2D UWmap= new DetectorShapeView2D("PCAL UW");
	    	for(int uPaddle = 0; uPaddle < 1; uPaddle++){
	            for(int wPaddle = 61; wPaddle < 62; wPaddle++){
	            	if(pcaltest.isValidOverlap(0, "u", uPaddle, "w", wPaddle))
	            		shape = pcaltest.getOverlapShape(0, "u", uPaddle, "w", wPaddle);
	            		for(int i = 0; i < shape.getShapePath().size(); ++i)
        				{
        					shape.getShapePath().point(i).set(shape.getShapePath().point(i).x() * 1000.0, shape.getShapePath().point(i).y() * 1000.0, 0.0);
        				}
	            		UWmap.addShape(shape);
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
