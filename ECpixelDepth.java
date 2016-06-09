package org.jlab.calib;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.jlab.clas12.calib.DetectorShape2D;

public class ECpixelDepth {

	public ECpixelDepth() {
		// TODO Auto-generated constructor stub
	}

	
	public static void main(String[] args){ 
		
		//Find Pixel distance
		double[][][][] total1 = new double[36][36][36][3];
		double[] totalfinal = new double[3];
		double totaldist;
		double time;
		double deltazin = 1.238 * 15.0;
		double deltaztot = 1.238 * 39.0;
		int num1, num2, num3;

		
		
		//get list of centers for EC inner
		CalDrawDB pcaltestdist1 = new CalDrawDB("ECin");
		DetectorShape2D shape1 = new DetectorShape2D();
		for(int sector = 0; sector < 1; sector++)
    	{
	    	for(int uPaddle = 0; uPaddle < 36; uPaddle++)
	    	{
	    		for(int vPaddle = 0; vPaddle < 36; vPaddle++)
	            {
		            for(int wPaddle = 0; wPaddle < 36; wPaddle++)
		            {
		            	//System.out.println("u: " + uPaddle + " v: " + vPaddle + " w: " + wPaddle);
		            	if(pcaltestdist1.isValidPixel(sector, uPaddle, vPaddle, wPaddle))
		            	{
		            		shape1 = pcaltestdist1.getPixelShape(sector, uPaddle, vPaddle, wPaddle);
		            		System.arraycopy( (double[])pcaltestdist1.getShapeCenter(shape1), 0, total1[uPaddle][vPaddle][wPaddle], 0, 3);
		            	}
		            }
	            }
	
	    	}
    	}

		
		//get list of centers for ECouter
		CalDrawDB pcaltestdist2 = new CalDrawDB("ECout");
		DetectorShape2D shape2 = new DetectorShape2D();
		double[][][][] total2 = new double[36][36][36][3];

		for(int sector = 0; sector < 1; sector++)
    	{
	    	for(int uPaddle = 0; uPaddle < 36; uPaddle++)
	    	{
	    		for(int vPaddle = 0; vPaddle < 36; vPaddle++)
	            {
		            for(int wPaddle = 0; wPaddle < 36; wPaddle++)
		            {
		            	//System.out.println("u: " + uPaddle + " v: " + vPaddle + " w: " + wPaddle);
		            	if(pcaltestdist2.isValidPixel(sector, uPaddle, vPaddle, wPaddle))
		            	{
		            		shape2 = pcaltestdist2.getPixelShape(sector, uPaddle, vPaddle, wPaddle);
		            		System.arraycopy( (double[])pcaltestdist2.getShapeCenter(shape2), 0, total2[uPaddle][vPaddle][wPaddle], 0, 3);
		            		total2[uPaddle][vPaddle][wPaddle][2] = deltazin;
		            	}
		            }
	            }
	
	    	}
    	}
		
		
		
		
		//extrapolate centers to end of outer
		PrintWriter writerdist = null;
		try 
		{
			writerdist = new PrintWriter("ECpixdepthtotal.dat");
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int sector = 0; sector < 1; sector++)
    	{
	    	for(int uPaddle = 0; uPaddle < 36; uPaddle++)
	    	{
	    		for(int vPaddle = 0; vPaddle < 36; vPaddle++)
	            {
		            for(int wPaddle = 0; wPaddle < 36; wPaddle++)
		            {
		            	//System.out.println("u: " + uPaddle + " v: " + vPaddle + " w: " + wPaddle);
		            	if(pcaltestdist2.isValidPixel(sector, uPaddle, vPaddle, wPaddle))
		            	{
		            		
		            		/////////////////////////////////////////////////////////////
		            		
		            		//z = z0 + ct
		            		//t= (z-z0)/c
		            		time = deltaztot/(deltazin);
		            		
		            		//x = x0 + at
		            		totalfinal[0] = total1[uPaddle][vPaddle][wPaddle][0] + (total2[uPaddle][vPaddle][wPaddle][0]-total1[uPaddle][vPaddle][wPaddle][0])*time;
		            		//y = y0 + bt
		            		totalfinal[1] = total1[uPaddle][vPaddle][wPaddle][1] + (total2[uPaddle][vPaddle][wPaddle][1]-total1[uPaddle][vPaddle][wPaddle][1])*time;
		            		//z = z0 + ct
		            		totalfinal[2] = total1[uPaddle][vPaddle][wPaddle][2] + (total2[uPaddle][vPaddle][wPaddle][2]-total1[uPaddle][vPaddle][wPaddle][2])*time;
		            		
		            		totaldist = Math.sqrt(Math.pow(totalfinal[0]-total1[uPaddle][vPaddle][wPaddle][0],2) + Math.pow(totalfinal[1]-total1[uPaddle][vPaddle][wPaddle][1],2) + Math.pow(totalfinal[2]-total1[uPaddle][vPaddle][wPaddle][2],2));
		            		
		            		num1 = uPaddle + 1;
		            		num2 = vPaddle + 1;
		            		num3 = wPaddle + 1;
		            		
		            		writerdist.println(num1  + "   " + num2 + "   " + num3 + "   "+ totaldist);
		            	}
		            }
	            }
	
	    	}
    	}
		writerdist.close();
		
		
		/*
		
		DetectorShape2D shape1 = new DetectorShape2D();
		DetectorShape2D shape2 = new DetectorShape2D();
		PrintWriter writerdist = null;
		double[] total1 = new double[3];
		double[] total2 = new double[3];
		double[] totalfinal = new double[3];
		double totaldist;
		double time;
		double deltazin = 1.238 * 15.0;
		double deltaztot = 1.238 * 39.0;
		try 
		{
			writerdist = new PrintWriter("ECpixdepthtotal.dat");
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int sector = 0; sector < 1; sector++)
    	{
	    	for(int uPaddle = 0; uPaddle < 36; uPaddle++)
	    	{
	    		for(int vPaddle = 0; vPaddle < 36; vPaddle++)
	            {
		            for(int wPaddle = 0; wPaddle < 36; wPaddle++)
		            {
		            	//System.out.println("u: " + uPaddle + " v: " + vPaddle + " w: " + wPaddle);
		            	if(pcaltest.isValidPixel(sector, uPaddle, vPaddle, wPaddle))
		            	{
		            		
		            		//System.out.println("                       ");
		            		//System.out.println("u: " + uPaddle + " v: " + vPaddle + " w: " + wPaddle);
		            		shape1 = pcaltestdist1.getPixelShape(sector, uPaddle, vPaddle, wPaddle);
		            		System.arraycopy( (double[])pcaltestdist1.getShapeCenter(shape1), 0, total1, 0, 3);
		            		System.out.println("x1:  " + total1[0] + " y1:  " + total1[1] + " z1:  " + total1[2]);
		            		pcaltestdist1 = null;
		            		
		            		CalDrawDB pcaltestdist2 = new CalDrawDB("ECout");
		            		shape2 = pcaltestdist2.getPixelShape(sector, uPaddle, vPaddle, wPaddle);
		            		System.arraycopy( (double[])pcaltestdist2.getShapeCenter(shape2), 0, total2, 0, 3);
		            		total2[2] = deltazin;
		            		System.out.println("x2:  " + total2[0] + " y2:  " + total2[1] + " z2:  " + total2[2]);
		            		pcaltestdist2 = null;
		            		/////////////////////////////////////////////////////////////
		            		
		            		//z = z0 + ct
		            		//t= (z-z0)/c
		            		time = deltaztot/(deltazin);
		            		
		            		//x = x0 + at
		            		totalfinal[0] = total1[0] + (total2[0]-total1[0])*time;
		            		//y = y0 + bt
		            		totalfinal[1] = total1[1] + (total2[1]-total1[1])*time;
		            		//z = z0 + ct
		            		totalfinal[2] = total1[2] + (total2[2]-total1[2])*time;
		            		
		            		totaldist = Math.sqrt(Math.pow(totalfinal[0]-total1[0],2) + Math.pow(totalfinal[1]-total1[1],2) + Math.pow(totalfinal[2]-total1[2],2));
		            		
		            		if(uPaddle == 0 ) 
		            		{
		            			System.out.println("x1:  " + total1[0] + " x2:  " + total2[0]);
		            			System.out.println("xfinal:  " + totalfinal[0] + " yfinal:  " + totalfinal[1] + " zfinal:  " + totalfinal[2]);
		            			System.out.println(totaldist);
		            		}
		            		num1 = uPaddle + 1;
		            		num2 = vPaddle + 1;
		            		num3 = wPaddle + 1;
		            		
		            		writerdist.println(num1  + "   " + num2 + "   " + num3 + "   "+ totaldist);
		            	}
		            }
	            }
	
	    	}
    	}
		writerdist.close();
		*/
		
		System.out.println("Done!");
	}
}
