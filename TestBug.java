package org.jlab.calib;

import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.pad.TCanvas;
import org.root.pad.TGCanvas;

public class TestBug {

	public TestBug() {
		// TODO Auto-generated constructor stub
			GraphErrors g1, g2;
			H1D hist1;
			double[] x1 = {10.0, 20.0};
			double[] ex1 = {1.0, 0.0};
			double[] y1 = {10.0, 0.0};
			double[] ey1 = {1.0, 0.0};
			
			double[] x2 = {10.0, 20.0};
			double[] ex2 = {1.0, 1.0};
			double[] y2 = {10.0, 20.0};
			double[] ey2 = {1.0, 1.0};
			
			F1D fitfunc1 = new F1D("p0",9.0,21.0);
			fitfunc1.setName("test_of_fit1");
			fitfunc1.setParameter(0, 9.0);
			
			F1D fitfunc2 = new F1D("p0",9.0,11.0);
			fitfunc2.setName("test_of_fit2");
			fitfunc2.setParameter(0, 9.0);
			
			
		
			
			//test 1
			TGCanvas test1 = new TGCanvas("test1", "test1", 500, 500,1,1);
			g1 = new GraphErrors(x1,y1,ex1,ey1); //creates graph with a name and 1 point
			g1.fit(fitfunc1,"RQ");
			test1.draw(g1);
			test1.setAxisRange(0.0, 25.0, 0.0, 25.0);
			test1.draw(fitfunc1,"same");
			
			//test1.save("test1.png");
			
			//test 2
			TGCanvas test2 = new TGCanvas("test2", "test2", 500, 500,1,1);
			g2 = new GraphErrors(x2,y2,ex2,ey2);
			g2.fit(fitfunc2,"REQ"); //creates graph with a name and 2 points
			test2.draw(g2);
			test2.setAxisRange(0.0, 25.0, 0.0, 25.0);
			test2.draw(fitfunc2,"same");
			
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
			
			
			hist1.fit(fitfunc3,"REQ"); //creates graph with a name and 2 points
			
			test3.draw(hist1);
			test3.setAxisRange(0.0, 10.0, 0.0, 10.0);
			test3.draw(fitfunc3,"same");
			
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

	        hist4.fit(fitfunc4,"REQ"); //creates graph with a name and 2 points
	        test4.draw(hist4);
	        
	        //System.out.println("median: " + hist4.getMedian());
	        test4.setAxisRange(0.0, 10.0, 0.0, 5.0);
	        fitfunc4.setLineColor(2);
	        test4.draw(fitfunc4,"same");

	        //test4.save("test4.png");
			
		
	}
	
	public static void main(String[] args){   
		
		TestBug test = new TestBug();
	}

}
