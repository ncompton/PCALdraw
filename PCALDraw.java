package org.jlab.calib;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.geom.prim.Point3D;

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
	
	public PCALDraw(double inlength, double inangle) {
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

	        stripShape.getShapePath().rotateZ(Math.toRadians(sector*60.0));
	           
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
	public Object[] getOverlapVerticies(int sector, String strip1, int paddle1, String strip2, int paddle2){
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
	public Object[] getStripVerticies(int sector, String strip1, int paddle1){
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
    
}
