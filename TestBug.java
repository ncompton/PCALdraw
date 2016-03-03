package org.jlab.calib;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.geom.prim.Point3D;
import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.pad.EmbeddedCanvas;
import org.root.pad.TCanvas;
import org.root.pad.TGCanvas;

public class TestBug {

	public TestBug() {
		// TODO Auto-generated constructor stub
			GraphErrors g1, g2, g3;
			H1D hist1;
			double[] x1 = {10.0, 20.0};
			double[] ex1 = {1.0, 0.0};
			double[] y1 = {10.0, 0.0};
			double[] ey1 = {0.5, 1.0};
			
			double[] x2 = {10.0, 20.0};
			double[] ex2 = {1.0, 1.0};
			double[] y2 = {10.0, 20.0};
			double[] ey2 = {1.0, 1.0};
			
			double[] x3 = {0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5};
			double[] ex3 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			double[] y3 = {1, 1, 1, 1, 8, 1, 1, 1, 1, 1};
			double[] ey3 = {1, 1, 1, 1, Math.sqrt(8), 1, 1, 1, 1, 1};
			
			F1D fitfunc1 = new F1D("p0",9.0,21.0);
			fitfunc1.setName("test_of_fit1");
			fitfunc1.setParameter(0, 9.0);
			
			F1D fitfunc2 = new F1D("p0",9.0,11.0);
			fitfunc2.setName("test_of_fit2");
			fitfunc2.setParameter(0, 9.0);
			
			F1D fitfunc3g = new F1D("p0",0.0,10.0);
			fitfunc3g.setName("test_of_fit3");
			fitfunc3g.setParameter(0, 1.0);
			
			
		
			
			//test 1
			TGCanvas test1 = new TGCanvas("test1", "test1", 500, 500,1,1);
			g1 = new GraphErrors(x1,y1,ex1,ey1); //creates graph with a name and 1 point
			g1.fit(fitfunc1,"REQ");
			test1.draw(g1);
			test1.setAxisRange(0.0, 25.0, 0.0, 25.0);
			test1.draw(fitfunc1,"same");
			
			
			System.out.println("chi squared: " + fitfunc1.getChiSquare(g1,"E") + " ndf: " + fitfunc1.getNDF(g1));
			//test1.save("test1.png");
			
			//test 2
			TGCanvas test2 = new TGCanvas("test2", "test2", 500, 500,1,1);
			g2 = new GraphErrors(x2,y2,ex2,ey2);
			g2.fit(fitfunc2,"REQ"); //creates graph with a name and 2 points
			test2.draw(g2);
			test2.setAxisRange(0.0, 25.0, 0.0, 25.0);
			test2.draw(fitfunc2,"same");
			
			//test2.save("test2.png");
			
			
			//test 2
			TGCanvas test3g = new TGCanvas("test3g", "test3g", 500, 500,1,1);
			g3 = new GraphErrors(x3,y3,ex3,ey3);
			g3.fit(fitfunc3g,"REQ"); //creates graph with a name and 2 points
			test3g.draw(g3);
			test3g.setAxisRange(0.0, 25.0, 0.0, 25.0);
			test3g.draw(fitfunc3g,"same");
			
			//test2.save("test2.png");
			
			
			//test 3
			TGCanvas test3 = new TGCanvas("test3", "test3", 500, 500, 1, 1);
			
			F1D fitfunc3 = new F1D("p0",0.0,10.0);
			fitfunc3.setName("test_of_fit3");
			fitfunc3.setParameter(0, 1.0);
			fitfunc3.setLineColor(2);
			
			hist1 = new H1D("hist1",10,0,10);
			hist1.fill(0.5);
			hist1.fill(1.5);
			hist1.fill(2.5);
			hist1.fill(3.5);
			hist1.fill(4.5);
			hist1.fill(5.5);
			hist1.fill(6.5);
			hist1.fill(7.5);
			hist1.fill(8.5);
			hist1.fill(9.5);
			hist1.fill(4.5);
			hist1.fill(4.5);
			hist1.fill(4.5);
			hist1.fill(4.5);
			hist1.fill(4.5);
			hist1.fill(4.5);
			hist1.fill(4.5);
			
			
			hist1.fit(fitfunc3,"RE"); //creates graph with a name and 2 points
			
			test3.draw(hist1);
			test3.setAxisRange(0.0, 10.0, 0.0, 10.0);
			test3.draw(fitfunc3,"same");
			
			System.out.println("chi squared: " + fitfunc3.getChiSquare(hist1.getDataSet(),"E") + " ndf: " + fitfunc3.getNDF(hist1.getDataSet()));
			
			//for(int i = 0; i < 10; ++i)
			//{
			//	System.out.println("bin: " + i + " content: " + hist1.getBinContent(i)); 
			//	System.out.println("bin: " + i + " error: " + hist1.getBinError(i)); 
			//}
			
			//test3.save("/home/ncompton/Work/workspace/Calibration/test3.png");
			
	          //test 4
	        TGCanvas test4 = new TGCanvas("test4", "test4", 500, 500,1 ,1);

	        F1D fitfunc4 = new F1D("p0",1.5,4.5);
	        fitfunc4.setName("test_of_fit4");
	        fitfunc4.setParameter(0, 1.0);

	        H1D hist4 = new H1D("hist4",10,0,10);
	        hist4.fill(1.5);
	        hist4.fill(2.5);
	        //hist4.fill(3.5);
	        hist4.fill(4.5);
	        //hist4.fill(4.5,0.6);

	        hist4.fit(fitfunc4,"RE"); //creates graph with a name and 2 points
	        test4.draw(hist4);
	        
	        //System.out.println("median: " + hist4.getMedian());
	        test4.setAxisRange(0.0, 10.0, 0.0, 5.0);
	        fitfunc4.setLineColor(2);
	        test4.draw(fitfunc4,"same");
	        
	        
	        

	        //test4.save("test4.png");
			
		
	}
	
	public boolean isContained3Decimal(DetectorShape2D shape, double x, double y){
        int i, j;
        boolean c = false;
        int nvert = shape.getShapePath().size();
        for (i = 0, j = nvert-1; i < nvert; j = i++) {
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
                    	return true;
                    }
            }
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
        	if( (int)(shape.getShapePath().point(i).x() * 1000.0) == (int)(x * 1000.0) || (int)(shape.getShapePath().point(j).x() * 1000.0) == (int)(x * 1000.0)  )
            {
	                if((        y  < 
	               		 (   shape.getShapePath().point(j).y() 
	               		           -shape.getShapePath().point(i).y()   ) 
	               		       * (  x 
	               		           -shape.getShapePath().point(j).x()   ) 
	               		       / (  shape.getShapePath().point(j).x()
	               		           -shape.getShapePath().point(i).x()   ) 
	               		       + shape.getShapePath().point(j).y()       )   )
	               {
	               	c = !c;
	               }
            }
        	
        }
        return c;
    }
	public boolean isContained(DetectorShape2D shape, double x, double y){
        int i, j;
        boolean c = false;
        int nvert = shape.getShapePath().size();
        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( (( shape.getShapePath().point(i).y()*1.0001>y) != (shape.getShapePath().point(j).y()*1.0001>y)) &&
                    (x < ( shape.getShapePath().point(j).x()*1.0001-shape.getShapePath().point(i).x()*1.0001) * 
                    (y-shape.getShapePath().point(i).y()*1.0001) / (shape.getShapePath().point(j).y()*1.0001-shape.getShapePath().point(i).y()*1.0001) +
                    shape.getShapePath().point(i).x()*1.0001))
                c = !c;
        }
        return c;
        //return false;
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
			if(isContained3Decimal(shape1, xtemp[i], ytemp[i]) && isContained3Decimal(shape2, xtemp[i], ytemp[i]))
			{
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
	
	
	// isLeft(): tests if a point is Left|On|Right of an infinite line.
//  Input:  three points P0, P1, and P2
//  Return: >0 for P2 left of the line through P0 and P1
//          =0 for P2  on the line
//          <0 for P2  right of the line
//  See: Algorithm 1 "Area of Triangles and Polygons"
public int isLeft( Point3D P0, Point3D P1, Point3D P2 )
{
  return ( (int)((P1.x()*1001.0 - P0.x()*1001.0) * (P2.y()*1000.0 - P0.y()*1001.0)
          - (P2.x()*1000.0 -  P0.x()*1001.0) * (P1.y()*1001.0 - P0.y()*1001.0)) );
}
//===================================================================



//wn_PnPoly(): winding number test for a point in a polygon
//    Input:   P = a point,
//             V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
//    Return:  wn = the winding number (=0 only when P is outside)
public int wn_PnPoly( Point3D P, DetectorShape2D shape)
{
	Point3D[] V = new Point3D[shape.getShapePath().size() + 1];
	for(int i=0; i<shape.getShapePath().size() + 1; i++)
	{
		V[i] = new Point3D();
	}
	int n = shape.getShapePath().size();
	for(int i=0; i<shape.getShapePath().size(); i++)
	{
		if(i == 0) 
		{
			V[0].copy(shape.getShapePath().point(i));
			V[shape.getShapePath().size()].copy(shape.getShapePath().point(0));
		}
		else
		{
			V[i].copy(shape.getShapePath().point(i));
		}
	}
	
	int    wn = 0;    // the  winding number counter

  // loop through all edges of the polygon
  for(int i=0; i<n; i++) {   // edge from V[i] to  V[i+1]
      if(V[i].y()*1.001 <= P.y()) {          // start y <= P.y
          if(V[i+1].y()*1.001  > P.y())      // an upward crossing
               if (isLeft( V[i], V[i+1], P) > 0)  // P left of  edge
                   ++wn;            // have  a valid up intersect
      }
      else 
      {                        // start y > P.y (no test needed)
          if (V[i+1].y()*1.001  <= P.y())     // a downward crossing
               if (isLeft( V[i], V[i+1], P) < 0)  // P right of  edge
                   --wn;            // have  a valid down intersect
      }
  }
  return wn;
}
//===================================================================
	
	public void testcontained()
	{
    	TGCanvas canvas = new TGCanvas();
		EmbeddedCanvas canvas1 = new EmbeddedCanvas();
		
		DetectorShapeTabView  view   = new DetectorShapeTabView();
		DetectorShapeView2D dv1 = new DetectorShapeView2D("testiscont");


		
		DetectorShape2D  shape = new DetectorShape2D(DetectorType.PCAL,0,2,0);
    	shape.getShapePath().clear(); 

    	shape.getShapePath().addPoint(0.0,  0.0,  0.0); 
    	shape.getShapePath().addPoint(1.0,  0.0,  0.0); 
    	shape.getShapePath().addPoint(1.0,  1.0,  0.0); 
    	shape.getShapePath().addPoint(0.0,  1.0,  0.0); 
    	
    	DetectorShape2D  shape1 = new DetectorShape2D(DetectorType.PCAL,0,2,1);
    	shape1.getShapePath().clear(); 

    	shape1.getShapePath().addPoint(1.0,  0.0,  0.0); 
    	shape1.getShapePath().addPoint(2.0,  0.0,  0.0); 
    	shape1.getShapePath().addPoint(2.0,  1.0,  0.0); 
    	shape1.getShapePath().addPoint(1.0,  1.0,  0.0); 
    	
    	
    	DetectorShape2D  shape2 = new DetectorShape2D(DetectorType.PCAL,0,2,2);
    	shape2.getShapePath().clear(); 

    	shape2.getShapePath().addPoint(0.0,  1.0,  0.0); 
    	shape2.getShapePath().addPoint(1.0,  1.0,  0.0); 
    	shape2.getShapePath().addPoint(1.0,  2.0,  0.0); 
    	shape2.getShapePath().addPoint(0.0,  2.0,  0.0); 
    	
    	
    	DetectorShape2D  shape3 = new DetectorShape2D(DetectorType.PCAL,0,2,3);
    	shape3.getShapePath().clear(); 

    	shape3.getShapePath().addPoint(1.0,  1.0,  0.0); 
    	shape3.getShapePath().addPoint(2.0,  1.0,  0.0); 
    	shape3.getShapePath().addPoint(2.0,  2.0,  0.0); 
    	shape3.getShapePath().addPoint(1.0,  2.0,  0.0); 
    	
    	
    	DetectorShape2D  shape4 = new DetectorShape2D(DetectorType.PCAL,0,2,4);
    	shape4.getShapePath().clear(); 

    	shape4.getShapePath().addPoint(0.0,  0.0,  0.0); 
    	shape4.getShapePath().addPoint(2.0,  0.0,  0.0); 
    	shape4.getShapePath().addPoint(1.0,  2.0,  0.0); 

        if(isContained(shape4,-0.1, 0.0))System.out.println("Contained!");
        System.out.println(wn_PnPoly( new Point3D(-0.1,0.0,0.0), shape4));
        
    	//dv1.addShape(shape); 
    	//dv1.addShape(shape1);  
    	//dv1.addShape(shape2);  
    	//dv1.addShape(shape3);  
        dv1.addShape(shape4); 
        
    	view.addDetectorLayer(dv1);
    	
    	


		JFrame hi = new JFrame();
		hi.setLayout(new BorderLayout());
	    JSplitPane  splitPane = new JSplitPane();
	    splitPane.setLeftComponent(view);
	    splitPane.setRightComponent(canvas1);
	    hi.add(splitPane,BorderLayout.CENTER);

	        //JPanel buttons = new JPanel();
	        //JButton process = new JButton("Process");
	        //buttons.setLayout(new FlowLayout());
	        //buttons.add(process);
	        //process.addActionListener(this);
	        //this.add(buttons,BorderLayout.PAGE_END);
	    hi.pack();
	    hi.setVisible(true);
    	canvas1.add(view);
    	//canvas1.draw(view);
       // return UWmap;
	 
    	System.out.println("Done!");
		
	}
	
	public static void main(String[] args){   
		
		TestBug test = new TestBug();
		//test.testcontained();
	}

}
