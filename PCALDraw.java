package org.jlab.calib;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeView2D;

public class PCALDraw {
	
	private double length;
	private double angle;
	private double anglewidth;
	private double slightshift;

	public PCALDraw() {
		length = 4.5;
		angle = 62.8941;
		anglewidth = length/Math.sin(Math.toRadians(angle));
		slightshift = length/Math.tan(Math.toRadians(angle));
	}
	
	public PCALDraw(double length, double angle) {
		anglewidth = length/Math.sin(Math.toRadians(angle));
		slightshift = length/Math.tan(Math.toRadians(angle));
	}
	
	//collects all possible pixels into a DetectorShapeView2D
	public DetectorShapeView2D drawAllPixels(int sector)
	{
		DetectorShapeView2D pixelmap= new DetectorShapeView2D("PCAL Pixels");
            for(int upaddle = 0; upaddle < 68; upaddle++){
            	for(int vpaddle = 0; vpaddle < 62; vpaddle++){
            		for(int wpaddle = 0; wpaddle < 62; wpaddle++){
            			if(isValidPixel(sector, upaddle, vpaddle, wpaddle))
            				pixelmap.addShape(getPixelShape(sector, upaddle, vpaddle, wpaddle));
            		}
            	}
            }
            return pixelmap;
	}
	
	//collects all possible UW intersections into a DetectorShapeView2D
	public DetectorShapeView2D drawUW(int sector)
	{
		DetectorShapeView2D UWmap= new DetectorShapeView2D("PCAL UW");
	    	for(int upaddle = 0; upaddle < 68; upaddle++){
	            for(int wpaddle = 0; wpaddle < 62; wpaddle++){
	            	if(isValidOverlap(sector, "u", upaddle, "w", wpaddle))
	            		UWmap.addShape(getOverlapShape(sector, "u", upaddle, "w", wpaddle));
	            }
	          
	    	}
	        return UWmap;
	}
	
	//collects all possible UW intersections into a DetectorShapeView2D
	public DetectorShapeView2D drawWU(int sector)
	{
		DetectorShapeView2D WUmap= new DetectorShapeView2D("PCAL WU");
		   	for(int upaddle = 0; upaddle < 68; upaddle++){
		   		for(int wpaddle = 0; wpaddle < 62; wpaddle++){
		            if(isValidOverlap(sector, "w", wpaddle, "u", upaddle))
		            	WUmap.addShape(getOverlapShape(sector, "w", wpaddle, "u", upaddle));
		        }
		          
		   	}
		    return WUmap;
	}
		
	
	//collects all possible UW intersections into a DetectorShapeView2D
	public DetectorShapeView2D drawVU(int sector)
	{
		DetectorShapeView2D UVmap= new DetectorShapeView2D("PCAL UV");
		   	for(int upaddle = 0; upaddle < 68; upaddle++){
		   		for(int vpaddle = 0; vpaddle < 62; vpaddle++){
		            if(isValidOverlap(sector, "v", vpaddle, "u", upaddle))
		            	UVmap.addShape(getOverlapShape(sector, "v", vpaddle, "u", upaddle));
		        }
		          
		   	}
		    return UVmap;
	}
	
	//calls getPixelVerticies
	//uses those 3 verticies to make a shape
	public DetectorShape2D getPixelShape(int sector, int upaddle, int vpaddle, int wpaddle){
		
		Object[] obj = getPixelVerticies(sector, upaddle, vpaddle, wpaddle);
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
      
        	
        
        DetectorShape2D  pixel = new DetectorShape2D(DetectorType.PCAL,sector,2,upaddle * 10000 + vpaddle * 100 + wpaddle);
    	pixel.getShapePath().clear(); 
        if(numpoints > 2) 
        {
        	for(int i = 0; i < numpoints; ++i){ 
        		pixel.getShapePath().addPoint(x[i],  y[i],  0.0); 
        	} 

        	pixel.getShapePath().rotateZ(Math.toRadians(sector*60.0));
           
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

	//calls getPixelVerticies to check
	//that at least 3 points exist,
	// if so it is marked as true, else false
	public Boolean isValidPixel(int sector, int upaddle, int vpaddle, int wpaddle){
		Object[] obj = getPixelVerticies(sector, upaddle, vpaddle, wpaddle);
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
	public Object[] getPixelVerticies(int sector, int upaddle, int vpaddle, int wpaddle){
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
        if(upaddle + 1 > 52)
        {
        	uyup = (upaddle + 1) - 52.0;
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
        	uyup = upaddle + 1;
        	uydown = (84 - (uyup)) * length;
        	uyup = (84 - (uyup - 1)) * length;
        }
        // rsv 1-62  
        if(vpaddle + 1 >= 16)
        {
        	x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15))*anglewidth;
    		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15))*anglewidth - slightshift;
    		y1 = 0.0;
    		y2 = length;
    		vmup = (y2 - y1)/(x2 - x1);
    		vbup = -x1*vmup;
    		
    		//System.out.println("vxright: " + x1);
    		
    		x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15 - 1))*anglewidth;
    		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15 - 1))*anglewidth - slightshift;
    		y1 = 0.0;
    		y2 = length;
    		vmdown = (y2 - y1)/(x2 - x1);
    		vbdown = -x1*vmdown;
    		
    		//System.out.println("vxleft: " + x1);
        }
        else
        {
        	x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0) + 2.0)*anglewidth;
    		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0) + 2.0)*anglewidth - slightshift;
    		y1 = 0.0;
    		y2 = length;
    		vmdown = (y2 - y1)/(x2 - x1);
    		vbdown = -x1*vmdown;
    		
    		x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0))*anglewidth;
    		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0))*anglewidth - slightshift;
    		y1 = 0.0;
    		y2 = length;
    		vmup = (y2 - y1)/(x2 - x1);
    		vbup = -x1*vmup;
        }
        // rsw 1-62  
        if(wpaddle + 1 >= 16)
        {
        	x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15))*anglewidth;
    		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15))*anglewidth + slightshift;
    		y1 = 0.0;
    		y2 = length;
    		wmup = (y2 - y1)/(x2 - x1);
    		wbup = -x1*wmup;
    		
    		//System.out.println("wxright: " + x1);
    		
    		x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15 - 1))*anglewidth;
    		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15 - 1))*anglewidth + slightshift;
    		y1 = 0.0;
    		y2 = length;
    		wmdown = (y2 - y1)/(x2 - x1);
    		wbdown = -x1*wmdown;
    		
    		//System.out.println("wxleft: " + x1);
        }
        else
        {
        	x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0) + 2.0)*anglewidth;
    		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0) + 2.0)*anglewidth + slightshift;
    		y1 = 0.0;
    		y2 = length;
    		wmdown = (y2 - y1)/(x2 - x1);
    		wbdown = -x1*wmdown;
    		
    		//System.out.println("wxright: " + x1);
    		
    		x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0))*anglewidth;
    		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0))*anglewidth + slightshift;
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
	
	
	//calls getOverlapVerticies
	//uses those 3 verticies to make a shape
	public DetectorShape2D getOverlapShape(int sector, String strip1, int paddle1, String strip2, int paddle2){
			
		int upaddle = -1;
		int vpaddle = -1;
		int wpaddle = -1;
		if((strip1 == "u" || strip1 == "U"))
		{
			upaddle = paddle1;
		}
		if((strip2 == "u" || strip2 == "U"))
		{
			upaddle = paddle2;
		}
		if((strip1 == "v" || strip1 == "V"))
		{
			vpaddle = paddle1;
		}
		if((strip2 == "v" || strip2 == "V"))
		{
			vpaddle = paddle2;
		}
		if((strip1 == "w" || strip1 == "W"))
		{
			wpaddle = paddle1;
		}
		if((strip2 == "w" || strip2 == "W"))
		{
			wpaddle = paddle2;
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
	    if(upaddle == paddle1 && wpaddle == paddle2)
	    	overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,3,upaddle * 100 + wpaddle);
	    else if(vpaddle == paddle1 && upaddle == paddle2)
    		overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,4,upaddle * 100 + vpaddle);
	    else if(wpaddle == paddle1 && upaddle == paddle2)
	    	overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,5,upaddle * 100 + wpaddle);
	    else
	    	overlapShape = new DetectorShape2D(DetectorType.PCAL,sector,6,vpaddle * 100 + wpaddle);
	    
	    overlapShape.getShapePath().clear(); 
	    if(numpoints > 2) 
	    {
	        for(int i = 0; i < numpoints; ++i){ 
	        	overlapShape.getShapePath().addPoint(x[i],  y[i],  0.0); 
	        } 

	        overlapShape.getShapePath().rotateZ(Math.toRadians(sector*60.0));
	           
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
	
	
	//returns an Object array of size 3
	//first element is the number of verticies (n) (int)
	//second element is an array x-coordinates (double[]) of size n
	//third element is an array y-coordinates (double[]) of size n
	public Object[] getOverlapVerticies(int sector, String strip1, int paddle1, String strip2, int paddle2){
		int upaddle = -1;
		int vpaddle = -1;
		int wpaddle = -1;
		
		
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
			upaddle = paddle1;
		}
		if((strip2 == "u" || strip2 == "U"))
		{
			upaddle = paddle2;
		}
		if((strip1 == "v" || strip1 == "V"))
		{
			vpaddle = paddle1;
		}
		if((strip2 == "v" || strip2 == "V"))
		{
			vpaddle = paddle2;
		}
		if((strip1 == "w" || strip1 == "W"))
		{
			wpaddle = paddle1;
		}
		if((strip2 == "w" || strip2 == "W"))
		{
			wpaddle = paddle2;
		}
		
		//case 1: UW plane
		if(upaddle != -1 && wpaddle != -1)
		{ 
		                vpaddle = 61;
		                		                
		                //System.out.println("Sector: " + sector + " u: " + upaddle + " w: " + wpaddle);
		                
		                //convert strip numbers to slopes and intercepts
		                // rsu 1-68  
		                if(upaddle + 1 > 52)
		                {
		                	uyup = (upaddle + 1) - 52.0;
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
		                	uyup = upaddle + 1;
		                	uydown = (84 - (uyup)) * length;
		                	uyup = (84 - (uyup - 1)) * length;
		                }
		                // rsv 1-62  
		                if(vpaddle + 1 >= 16)
		                {
		                	x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmup = (y2 - y1)/(x2 - x1);
		            		vbup = -x1*vmup;
		            		
		            		//System.out.println("vxright: " + x1);
		            		
		            		x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15 - 1))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15 - 1))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmdown = (y2 - y1)/(x2 - x1);
		            		vbdown = -x1*vmdown;
		            		
		            		//System.out.println("vxleft: " + x1);
		                }
		                else
		                {
		                	x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0) + 2.0)*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0) + 2.0)*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmdown = (y2 - y1)/(x2 - x1);
		            		vbdown = -x1*vmdown;
		            		
		            		x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0))*anglewidth;
		            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0))*anglewidth - slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		vmup = (y2 - y1)/(x2 - x1);
		            		vbup = -x1*vmup;
		                }
		                // rsw 1-62  
		                if(wpaddle + 1 >= 16)
		                {
		                	x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmup = (y2 - y1)/(x2 - x1);
		            		wbup = -x1*wmup;
		            		
		            		//System.out.println("wxright: " + x1);
		            		
		            		x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15 - 1))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15 - 1))*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmdown = (y2 - y1)/(x2 - x1);
		            		wbdown = -x1*wmdown;
		            		
		            		//System.out.println("wxleft: " + x1);
		                }
		                else
		                {
		                	x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0) + 2.0)*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0) + 2.0)*anglewidth + slightshift;
		            		y1 = 0.0;
		            		y2 = length;
		            		wmdown = (y2 - y1)/(x2 - x1);
		            		wbdown = -x1*wmdown;
		            		
		            		//System.out.println("wxright: " + x1);
		            		
		            		x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0))*anglewidth;
		            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0))*anglewidth + slightshift;
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
			else if(upaddle != -1 && vpaddle != -1)
			{ 
    			wpaddle = 61;
                
                //System.out.println("Sector: " + sector + " u: " + upaddle + " v: " + vpaddle);
                
                //convert strip numbers to slopes and intercepts
                // rsu 1-68  
                if(upaddle + 1 > 52)
                {
                	uyup = (upaddle + 1) - 52.0;
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
                	uyup = upaddle + 1;
                	uydown = (84 - (uyup)) * length;
                	uyup = (84 - (uyup - 1)) * length;
                }
                // rsv 1-62  
                if(vpaddle + 1 >= 16)
                {
                	x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15))*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15))*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmup = (y2 - y1)/(x2 - x1);
            		vbup = -x1*vmup;
            		
            		//System.out.println("vxright: " + x1);
            		
            		x1 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15 - 1))*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (76.0 - (vpaddle + 15 - 1))*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmdown = (y2 - y1)/(x2 - x1);
            		vbdown = -x1*vmdown;
            		
            		//System.out.println("vxleft: " + x1);
                }
                else
                {
                	x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0) + 2.0)*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0) + 2.0)*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmdown = (y2 - y1)/(x2 - x1);
            		vbdown = -x1*vmdown;
            		
            		x1 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0))*anglewidth;
            		x2 = 77.0 * anglewidth / 2.0 - (77.0 - ((vpaddle + 1) * 2.0))*anglewidth - slightshift;
            		y1 = 0.0;
            		y2 = length;
            		vmup = (y2 - y1)/(x2 - x1);
            		vbup = -x1*vmup;
                }
                // rsw 1-62  
                if(wpaddle + 1 >= 16)
                {
                	x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15))*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15))*anglewidth + slightshift;
            		y1 = 0.0;
            		y2 = length;
            		wmup = (y2 - y1)/(x2 - x1);
            		wbup = -x1*wmup;
            		
            		//System.out.println("wxright: " + x1);
            		
            		x1 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15 - 1))*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (76.0 - (wpaddle + 15 - 1))*anglewidth + slightshift;
            		y1 = 0.0;
            		y2 = length;
            		wmdown = (y2 - y1)/(x2 - x1);
            		wbdown = -x1*wmdown;
            		
            		//System.out.println("wxleft: " + x1);
                }
                else
                {
                	x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0) + 2.0)*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0) + 2.0)*anglewidth + slightshift;
            		y1 = 0.0;
            		y2 = length;
            		wmdown = (y2 - y1)/(x2 - x1);
            		wbdown = -x1*wmdown;
            		
            		//System.out.println("wxright: " + x1);
            		
            		x1 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0))*anglewidth;
            		x2 = -77.0 * anglewidth / 2.0 + (77.0 - ((wpaddle + 1) * 2.0))*anglewidth + slightshift;
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

}