package org.jlab.calib;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.tools.benchmark.ProgressPrintout;
import org.jlab.clas12.basic.IDetectorProcessor;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clas12.calib.IDetectorListener;
import org.jlab.clasrec.main.DetectorEventProcessorDialog;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioSource;
import org.root.attr.ColorPalette;
import org.root.attr.TStyle;
import org.root.func.F1D;
import org.root.group.TBrowser;
import org.root.group.TDirectory;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.histogram.H2D;
import org.root.pad.EmbeddedCanvas;

/**
 *
 * @author gavalian
 * @edited by N. Compton
 */
public class PCALcalib extends JFrame implements IDetectorListener, IDetectorProcessor, ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DetectorCollection<H1D>  tdcH = new DetectorCollection<H1D>();
    DetectorCollection<H1D>  adcH = new DetectorCollection<H1D>();
    
    public String laba[] = {"monitor/pcal/adc","monitor/ecinner/adc","monitor/ecouter/adc"}; 
	public String labt[] = {"monitor/pcal/tdc","monitor/ecinner/tdc","monitor/ecouter/tdc"};
    
    DetectorShapeTabView  view   = new DetectorShapeTabView();
    EmbeddedCanvas        canvas = new EmbeddedCanvas();
    int                   nProcessed = 0;

    // ColorPalette class defines colors 
    ColorPalette         palette   = new ColorPalette();
    int numsectors = 1;
    int numpaddles = 68 * 10000 + 62 * 100 + 62;
    
    private String inputFileName = "/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/fc-muon-3M-s2.evio";
    //private int RunNumber = 4284;
    private int CurrentSector = 2;
    private static int iteration = 0;
    
    TDirectory mondirectory = new TDirectory();
    
    int hit[][][] = new int[68][62][62]; //bad pixel, good pixel, maybe good = 0, 1, 2
	double udpixel[][][] = new double[68][62][62]; //
	double vdpixel[][][] = new double[68][62][62]; //
	double wdpixel[][][] = new double[68][62][62]; //
	int pixelnumber[][][] = new int[68][62][62];
	
	double ugain[] = new double[68]; //
	double vgain[] = new double[62]; //
	double wgain[] = new double[62]; //
	
	double umaxX[] = new double[68]; //
	double vmaxX[] = new double[62]; //
	double wmaxX[] = new double[62]; //
	
	double uA[] = new double[68]; //
	double uB[] = new double[68]; //
	double uC[] = new double[68]; //
	
	double genuA[] = new double[68]; //
	double genuB[] = new double[68]; //
	double genuC[] = new double[68]; //
	
	double vA[] = new double[62]; //
	double vB[] = new double[62]; //
	double vC[] = new double[62]; //
	
	double genvA[] = new double[62]; //
	double genvB[] = new double[62]; //
	double genvC[] = new double[62]; //
	
	double wA[] = new double[62]; //
	double wB[] = new double[62]; //
	double wC[] = new double[62]; //
	
	double genwA[] = new double[62]; //
	double genwB[] = new double[62]; //
	double genwC[] = new double[62]; //
	
	int numiterations = 1;
    
    public TDirectory getDir(){
        return this.mondirectory;
    }

	
    public PCALcalib(){
        super();
        this.initDetector();
        //this.initHistograms();
        this.setLayout(new BorderLayout());
        JSplitPane  splitPane = new JSplitPane();
        splitPane.setLeftComponent(this.view);
        splitPane.setRightComponent(this.canvas);
        this.add(splitPane,BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        JButton process = new JButton("Process");
        buttons.setLayout(new FlowLayout());
        buttons.add(process);
        process.addActionListener(this);
        this.add(buttons,BorderLayout.PAGE_END);
        this.pack();
        this.setVisible(true);
    }
    
    
    /**
     * Creates a detector Shape.
     */
    private void initDetector(){
    	pixelpane();
    	UWpane();
    	VUpane();
    	WUpane();
    	//this.view.repaint();
    	
    }
    
    private void pixelpane(){
    	DetectorShapeView2D  dv2 = new DetectorShapeView2D("PCAL Pixels");
        for(int sector = 0; sector < this.numsectors; sector++){
            for(int upaddle = 0; upaddle < 68; upaddle++){
            	for(int vpaddle = 0; vpaddle < 62; vpaddle++){
            		for(int wpaddle = 0; wpaddle < 62; wpaddle++){
		                double length = 4.5;
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
		                double anglewidth = length/Math.sin(Math.toRadians(62.8941));
		                double slightshift = length/Math.tan(Math.toRadians(62.8941));
		                
		                System.out.println("Sector: " + sector + " u: " + upaddle + " v: " + vpaddle + " w: " + wpaddle);
		                
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

		                for(int i = 0; i< numpoints; ++i)
		                {
		                	y[i] -= 400.0;
		                }
		                //if(numpoints < 2)System.out.println("Didn't work");
		              
		                	
		                
		                //shape.createTrapXY(xhigh, xlow,  yhigh - ylow, 0.0, -(yhigh + ylow)/2.0 + 50.0); 
		                if(numpoints > 2) 
		                {
		                	DetectorShape2D  shape = new DetectorShape2D(DetectorType.PCAL,sector,2,upaddle * 10000 + vpaddle * 100 + wpaddle);
		                	shape.getShapePath().clear(); 
		                	for(int i = 0; i < numpoints; ++i){ 
		                		shape.getShapePath().addPoint(x[i],  y[i],  0.0); 
		                	} 
		                	//shape.createNXY(numpoints,x,y);
			                shape.getShapePath().rotateZ(Math.toRadians(sector*60.0));
			               
			                /*
			                if(paddle%2==0){
			                    shape.setColor(180, 255, 180);
			                } else {
			                    shape.setColor(180, 180, 255);
			                }
			                */
			                dv2.addShape(shape);   
		                }
            		}
            	}
            }
        }
        this.view.addDetectorLayer(dv2);
        view.addDetectorListener(this);
        
    }
 
    private void UWpane(){
    	DetectorShapeView2D  dv3 = new DetectorShapeView2D("PCAL UW");
        for(int sector = 0; sector < this.numsectors; sector++){
            for(int upaddle = 0; upaddle < 68; upaddle++){
            		for(int wpaddle = 0; wpaddle < 62; wpaddle++){
		                double length = 4.5;
		                double uyup, uydown;
		                double vmup, vmdown, vbup, vbdown;
		                double wmup, wmdown, wbup, wbdown;
		                int vpaddle = 61;
		                double x1, x2, y1, y2;
		                int numpoints = 0;
		                double x[] = new double [12];
		                double y[] = new double [12];
		                int numpointsA = 0;
		                double a[] = new double [12];
		                double b[] = new double [12];
		                double anglewidth = length/Math.sin(Math.toRadians(62.8941));
		                double slightshift = length/Math.tan(Math.toRadians(62.8941));
		                
		                System.out.println("Sector: " + sector + " u: " + upaddle + " w: " + wpaddle);
		                
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

		                for(int i = 0; i< numpoints; ++i)
		                {
		                	y[i] -= 400.0;
		                }
		                //if(numpoints < 2)System.out.println("Didn't work");
		              
		                	
		                
		                //shape.createTrapXY(xhigh, xlow,  yhigh - ylow, 0.0, -(yhigh + ylow)/2.0 + 50.0); 
		                if(numpoints > 2) 
		                {
		                	DetectorShape2D  shape = new DetectorShape2D(DetectorType.PCAL,sector,3,upaddle * 100 + wpaddle);
		                	shape.getShapePath().clear(); 
		                	for(int i = 0; i < numpoints; ++i){ 
		                		shape.getShapePath().addPoint(x[i],  y[i],  0.0); 
		                	} 
		                	//shape.createNXY(numpoints,x,y);
			                shape.getShapePath().rotateZ(Math.toRadians(sector*60.0));
			               
			                /*
			                if(paddle%2==0){
			                    shape.setColor(180, 255, 180);
			                } else {
			                    shape.setColor(180, 180, 255);
			                }
			                */
			                dv3.addShape(shape);   
		                }
            		}
            }
        }
        this.view.addDetectorLayer(dv3);
        view.addDetectorListener(this);
    
    }
    
    private void VUpane(){
    	DetectorShapeView2D  dv4 = new DetectorShapeView2D("PCAL VU");
        for(int sector = 0; sector < this.numsectors; sector++){
            for(int upaddle = 0; upaddle < 68; upaddle++){
            	for(int vpaddle = 0; vpaddle < 62; vpaddle++){
            			int wpaddle = 61;
		                double length = 4.5;
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
		                double anglewidth = length/Math.sin(Math.toRadians(62.8941));
		                double slightshift = length/Math.tan(Math.toRadians(62.8941));
		                
		                System.out.println("Sector: " + sector + " u: " + upaddle + " v: " + vpaddle);
		                
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

		                for(int i = 0; i< numpoints; ++i)
		                {
		                	y[i] -= 400.0;
		                }
		                //if(numpoints < 2)System.out.println("Didn't work");
		              
		                	
		                
		                //shape.createTrapXY(xhigh, xlow,  yhigh - ylow, 0.0, -(yhigh + ylow)/2.0 + 50.0); 
		                if(numpoints > 2) 
		                {
		                	DetectorShape2D  shape = new DetectorShape2D(DetectorType.PCAL,sector,4,upaddle * 100 + vpaddle);
		                	shape.getShapePath().clear(); 
		                	for(int i = 0; i < numpoints; ++i){ 
		                		shape.getShapePath().addPoint(x[i],  y[i],  0.0); 
		                	} 
		                	//shape.createNXY(numpoints,x,y);
			                shape.getShapePath().rotateZ(Math.toRadians(sector*60.0));
			               
			                /*
			                if(paddle%2==0){
			                    shape.setColor(180, 255, 180);
			                } else {
			                    shape.setColor(180, 180, 255);
			                }
			                */
			                dv4.addShape(shape);   
		                }
            		}
            	
            }
        }
        this.view.addDetectorLayer(dv4);
        view.addDetectorListener(this);
    
    }
    
    private void WUpane(){
    	DetectorShapeView2D  dv5 = new DetectorShapeView2D("PCAL WU");
        for(int sector = 0; sector < this.numsectors; sector++){
            for(int upaddle = 0; upaddle < 68; upaddle++){
            		for(int wpaddle = 0; wpaddle < 62; wpaddle++){
		                double length = 4.5;
		                double uyup, uydown;
		                double vmup, vmdown, vbup, vbdown;
		                double wmup, wmdown, wbup, wbdown;
		                int vpaddle = 61;
		                double x1, x2, y1, y2;
		                int numpoints = 0;
		                double x[] = new double [12];
		                double y[] = new double [12];
		                int numpointsA = 0;
		                double a[] = new double [12];
		                double b[] = new double [12];
		                double anglewidth = length/Math.sin(Math.toRadians(62.8941));
		                double slightshift = length/Math.tan(Math.toRadians(62.8941));
		                
		                System.out.println("Sector: " + sector + " u: " + upaddle + " w: " + wpaddle);
		                
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

		                for(int i = 0; i< numpoints; ++i)
		                {
		                	y[i] -= 400.0;
		                }
		                //if(numpoints < 2)System.out.println("Didn't work");
		              
		                	
		                
		                //shape.createTrapXY(xhigh, xlow,  yhigh - ylow, 0.0, -(yhigh + ylow)/2.0 + 50.0); 
		                if(numpoints > 2) 
		                {
		                	DetectorShape2D  shape = new DetectorShape2D(DetectorType.PCAL,sector,5,upaddle * 100 + wpaddle);
		                	shape.getShapePath().clear(); 
		                	for(int i = 0; i < numpoints; ++i){ 
		                		shape.getShapePath().addPoint(x[i],  y[i],  0.0); 
		                	} 
		                	//shape.createNXY(numpoints,x,y);
			                shape.getShapePath().rotateZ(Math.toRadians(sector*60.0));
			               
			                /*
			                if(paddle%2==0){
			                    shape.setColor(180, 255, 180);
			                } else {
			                    shape.setColor(180, 180, 255);
			                }
			                */
			                dv5.addShape(shape);   
		                }
            		}
            }
        }
        this.view.addDetectorLayer(dv5);
        view.addDetectorListener(this);
    
    }
    /**
     * When the detector is clicked, this function is called
     * @param desc 
     */
    public void detectorSelected(DetectorDescriptor desc){
        int u, v, w, uvwnum;
        String name;
        String namedir;
        H1D h1 = null;
        H1D hsum1 = null;
        H1D hsum2 = null;
        F1D gaus = null;
        F1D exp = null;
        F1D genexp = null;
        F1D genexp1 = null;
        GraphErrors g1;
        
        TStyle.setStatBoxFont("Helvetica", 12);
            
        
        //System.out.println("SELECTED = " + desc);
        
        namedir = String.format("pixelsignal%02d", iteration);
        if(desc.getLayer() == 2)
        {
        	canvas.divide(2,3);
	        uvwnum = (int)desc.getComponent();
	    	u = (int)(uvwnum/10000.0);
	    	uvwnum -= u*10000;
	    	v = (int)(uvwnum/100.0);
	    	uvwnum -= v*100;
	    	w = uvwnum;
	    	
	    	name = String.format("attu_%02d", u + 1);
		    g1  = (GraphErrors)getDir().getDirectory("attendir").getObject(name);
		    canvas.cd(0);
		    canvas.draw(g1);
	        
	        name = String.format("adu_%02d_%02d_%02d", u + 1, v + 1, w + 1);
	        h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(1);
	        canvas.draw(h1);
	        
	        
	        name = String.format("attv_%02d", v + 1);
	        g1  = (GraphErrors)getDir().getDirectory("attendir").getObject(name);
	        canvas.cd(2);
	        canvas.draw(g1);
	        
	        
	        name = String.format("adv_%02d_%02d_%02d", u + 1, v + 1, w + 1);
	        h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(3);
	        canvas.draw(h1);
	        
	        
	        name = String.format("attw_%02d", w + 1);
	        g1  = (GraphErrors)getDir().getDirectory("attendir").getObject(name);
	        canvas.cd(4);
	        canvas.draw(g1);
	        
	        
	        name = String.format("adw_%02d_%02d_%02d", u + 1, v + 1, w + 1);
	        h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(5);
	        canvas.draw(h1);
        }
        if(desc.getLayer() == 3) //UW
        {
        	//this.canvas = new EmbeddedCanvas();
        	//this.canvas.update();
	        canvas.divide(2,2);
	        uvwnum = (int)desc.getComponent();
	    	u = (int)(uvwnum/100.0);
	    	uvwnum -= u*100;
	    	w = uvwnum;
	    	v = 61;
	    	
	    	System.out.println("umaxX[u]: " + umaxX[u]);
	    	System.out.println("genuA[u]: " + genuA[u]);
	    	System.out.println("genuB[u]: " + genuB[u]);
	    	System.out.println("genuC[u]: " + genuC[u]);
	    	
	    	System.out.println("wmaxX[w]: " + umaxX[w]);
	    	System.out.println("genwA[w]: " + genwA[w]);
	    	System.out.println("genwB[w]: " + genwB[w]);
	    	System.out.println("genwC[w]: " + genwC[w]);
	    	
			//Draw U strip shape ADC value with gaussian fit
			namedir = String.format("Projection%02d", iteration);
			name = String.format("ProjHistU%02d_W%02d", u + 1, w + 1);
		    h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    namedir = String.format("GaussFit%02d", iteration);
			name = String.format("gaussU%02d_%02d", u + 1, w + 1);
			gaus  = (F1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(0);
	        //canvas.getPad().setStatBoxFont("Helvetica", 12);
	        canvas.draw(h1);
	        canvas.draw(gaus,"same");
	        
	        
	        //Draw W strip shape ADC value with gaussian fit
	        namedir = String.format("Projection%02d", iteration);
			name = String.format("ProjHistW%02d_U%02d", w + 1, u + 1);
		    h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    namedir = String.format("GaussFit%02d", iteration);
			name = String.format("gaussW%02d_%02d", w + 1, u + 1);
			gaus  = (F1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(1);
	        canvas.draw(h1);
	        canvas.draw(gaus,"same");
	        
	        
	        //Draw U attenuation with exponential
	        namedir = String.format("GraphE%02d", iteration);
			name = String.format("graphU_%02d", u + 1);
		    g1  = (GraphErrors)getDir().getDirectory(namedir).getObject(name);
		    g1.setMarkerSize(2);
		    namedir = String.format("ExpoFit%02d", iteration);
			name = String.format("expU_%02d", u + 1);
		    exp  = (F1D)getDir().getDirectory(namedir).getObject(name);
		    canvas.cd(2);
		    canvas.draw(g1);
		    canvas.getPad().setAxisRange(0.0, umaxX[u] + 20.0, 0.0, 120.0);
		    canvas.draw(exp,"same");
		    
		    genexp1 = new F1D("exp+p0",0.0,umaxX[u]);
		    genexp1.setParameter(0,genuA[u]);
		    genexp1.setParameter(1,genuB[u]);
		    genexp1.setParameter(2,genuC[u]);
		    genexp1.setLineColor(2);
		    genexp1.setLineStyle(2);
		    canvas.draw(genexp1,"same");
	        
		    
		    //Draw W attenuation with exponential
	        namedir = String.format("GraphE%02d", iteration);
			name = String.format("graphW_%02d", w + 1);
		    g1  = (GraphErrors)getDir().getDirectory(namedir).getObject(name);
		    g1.setMarkerSize(2);
		    namedir = String.format("ExpoFit%02d", iteration);
			name = String.format("expW_%02d", w + 1);
		    exp  = (F1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(3);
	        canvas.draw(g1);
	        canvas.getPad().setAxisRange(0.0, wmaxX[w] + 20.0, 0.0, 120.0);
	        canvas.draw(exp,"same");
	        
	        genexp = new F1D("exp+p0",0.0,wmaxX[w]);
		    genexp.setParameter(0,genwA[w]);
		    genexp.setParameter(1,genwB[w]);
		    genexp.setParameter(2,genwC[w]);
		    genexp.setLineColor(2);
		    genexp.setLineStyle(2);
		    canvas.draw(genexp,"same");
	          
        }
        if(desc.getLayer() == 4)
        {
	        canvas.divide(2,1);
	        uvwnum = (int)desc.getComponent();
	    	u = (int)(uvwnum/100.0);
	    	uvwnum -= u*100;
	    	v = uvwnum;
	    	w = 61;
	    	
//	    	//Draw U strip shape ADC value with gaussian fit
//			namedir = String.format("Projection%02d", iteration);
//			name = String.format("ProjHistU%02d_W%02d", u + 1, w + 1);
//		    h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
//		    namedir = String.format("GaussFit%02d", iteration);
//			name = String.format("gaussU%02d_%02d", u + 1, w + 1);
//			gaus  = (F1D)getDir().getDirectory(namedir).getObject(name);
//	        canvas.cd(0);
//	        canvas.draw(h1);
//	        canvas.draw(gaus,"same");
	        
	        
	        //Draw V strip shape ADC value with gaussian fit
	        namedir = String.format("Projection%02d", iteration);
			name = String.format("ProjHistV%02d_U%02d", v + 1, u + 1);
		    h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    namedir = String.format("GaussFit%02d", iteration);
			name = String.format("gaussV%02d_%02d", v + 1, u + 1);
			gaus  = (F1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(0);
	        canvas.draw(h1);
	        canvas.draw(gaus,"same");
	        
	        
//	        //Draw U attenuation with exponential
//	        namedir = String.format("GraphE%02d", iteration);
//			name = String.format("graphU_%02d", u + 1);
//		    g1  = (GraphErrors)getDir().getDirectory(namedir).getObject(name);
//		    namedir = String.format("ExpoFit%02d", iteration);
//			name = String.format("expU_%02d", u + 1);
//		    exp  = (F1D)getDir().getDirectory(namedir).getObject(name);
//		    canvas.cd(2);
//		    canvas.draw(g1);
//		    canvas.draw(exp,"same");
	        
		    
		    //Draw V attenuation with exponential
	        namedir = String.format("GraphE%02d", iteration);
			name = String.format("graphV_%02d", v + 1);
		    g1  = (GraphErrors)getDir().getDirectory(namedir).getObject(name);
		    g1.setMarkerSize(2);
		    namedir = String.format("ExpoFit%02d", iteration);
			name = String.format("expV_%02d", v + 1);
		    exp  = (F1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(1);
	        canvas.draw(g1);
	        canvas.getPad().setAxisRange(0.0, vmaxX[v] + 20.0, 0.0, 120.0);
	        canvas.draw(exp,"same");
	        
	        genexp = new F1D("exp+p0",0.0,vmaxX[v]);
		    genexp.setParameter(0,genvA[v]);
		    genexp.setParameter(1,genvB[v]);
		    genexp.setParameter(2,genvC[v]);
		    genexp.setLineColor(2);
		    genexp.setLineStyle(2);
		    canvas.draw(genexp,"same");
	        
        }
        if(desc.getLayer() == 5)
        {
        	//this.canvas = new EmbeddedCanvas();
        	//this.canvas.update();
	        canvas.divide(2,2);
	        uvwnum = (int)desc.getComponent();
	    	u = (int)(uvwnum/100.0);
	    	uvwnum -= u*100;
	    	w = uvwnum;
	    	v = 61;
	    	
	    	
//			//Draw U strip shape ADC value with gaussian fit
//			namedir = String.format("Projection%02d", iteration);
//			name = String.format("ProjHistU%02d_W%02d", u + 1, w + 1);
//		    h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
//		    namedir = String.format("GaussFit%02d", iteration);
//			name = String.format("gaussU%02d_%02d", u + 1, w + 1);
//			gaus  = (F1D)getDir().getDirectory(namedir).getObject(name);
//	        canvas.cd(0);
//	        canvas.draw(h1);
//	        canvas.draw(gaus,"same");
	    	
	    	hsum1 = new H1D("uA + uC",40, 90.0, 110.0);
	        for(int i = 0; i < 68; ++i)
	        {
	        	hsum1.fill(ugain[i]);
	        }
	        hsum2 = new H1D("ugainselect",40, 90.0, 110.0);
	        hsum2.fill(ugain[u]);
	        hsum2.setLineColor(2); //getAttributes().getProperties().setProperty("line-color", "2");
	        canvas.cd(0);
	        canvas.draw(hsum1);
	        canvas.draw(hsum2,"same");
	        
	        
	        //Draw W strip shape ADC value with gaussian fit
	        namedir = String.format("Projection%02d", iteration);
			name = String.format("ProjHistW%02d_U%02d", w + 1, u + 1);
		    h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    namedir = String.format("GaussFit%02d", iteration);
			name = String.format("gaussW%02d_%02d", w + 1, u + 1);
			gaus  = (F1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(1);
	        canvas.draw(h1);
	        canvas.draw(gaus,"same");
	        
	        
//	        //Draw U attenuation with exponential
//	        namedir = String.format("GraphE%02d", iteration);
//			name = String.format("graphU_%02d", u + 1);
//		    g1  = (GraphErrors)getDir().getDirectory(namedir).getObject(name);
//		    namedir = String.format("ExpoFit%02d", iteration);
//			name = String.format("expU_%02d", u + 1);
//		    exp  = (F1D)getDir().getDirectory(namedir).getObject(name);
//		    canvas.cd(2);
//		    canvas.draw(g1);
//		    canvas.setAxisRange(0.0, umaxX[u] + 5.0, 0.0, 120.0);
//		    canvas.draw(exp,"same");
	        
	        
	    	hsum1 = new H1D("wA + wC",40, 90.0, 110.0);
	        for(int i = 0; i < 62; ++i)
	        {
	        	hsum1.fill(wgain[i]);
	        }
	        hsum2 = new H1D("wgainselect",40, 90.0, 110.0);
	        hsum2.fill(wgain[w]);
	        hsum2.setLineColor(2);
	        canvas.cd(2);
	        canvas.draw(hsum1);
	        canvas.draw(hsum2, "same");
	        
		    
		    //Draw W attenuation with exponential
	        namedir = String.format("GraphE%02d", iteration);
			name = String.format("graphW_%02d", w + 1);
		    g1  = (GraphErrors)getDir().getDirectory(namedir).getObject(name);
		    g1.setMarkerSize(2);
		    namedir = String.format("ExpoFit%02d", iteration);
			name = String.format("expW_%02d", w + 1);
		    exp  = (F1D)getDir().getDirectory(namedir).getObject(name);
	        canvas.cd(3);
	        canvas.draw(g1);
	        canvas.getPad().setAxisRange(0.0, wmaxX[w] + 20.0, 0.0, 120.0);
	        canvas.draw(exp,"same");
	        
	        genexp = new F1D("exp+p0",0.0,wmaxX[w]);
		    genexp.setParameter(0,genwA[w]);
		    genexp.setParameter(1,genwB[w]);
		    genexp.setParameter(2,genwC[w]);
		    genexp.setLineColor(2);
		    genexp.setLineStyle(2);
		    canvas.draw(genexp,"same");
        }
	        
    }
    
    /**
     * Each redraw of the canvas passes detector shape object to this routine
     * and user can change the color of specific component depending
     * on accupancy or some other criteria.
     * @param shape 
     */
    public void update(DetectorShape2D shape) {
        int sector = shape.getDescriptor().getSector();
        int paddle = shape.getDescriptor().getComponent();
        int layer = shape.getDescriptor().getLayer();
        //shape.setColor(200, 200, 200);
        int nent = nProcessed;
        Color col = palette.getColor3D(nent, nProcessed, true);
        shape.setColor(col.getRed(),col.getGreen(),col.getBlue());
        /*
        if(this.tdcH.hasEntry(sector, 2,paddle)){
            int nent = this.tdcH.get(sector, 2,paddle).getEntries();
            Color col = palette.getColor3D(nent, nProcessed, true);
            //int colorRed = 240;
            //if(nProcessed!=0){
             //   colorRed = (255*nent)/(nProcessed);
            //}
            shape.setColor(col.getRed(),col.getGreen(),col.getBlue());
        }
        */
    }

    @Override
    public void processEvent(DataEvent de) 
    {
        EvioDataEvent event = (EvioDataEvent) de;
        int nh[][]         = new int[6][9];
		int strr[][][]     = new int[6][9][68]; 
		int adcr[][][]     = new int[6][9][68];
		float tdcr[][][]   = new float[6][9][68];
		int rs[]           = new int[9];
		int ad[]           = new int[9];
		float td[]         = new float[9];
		boolean good_lay[] = new boolean[9]; 
		boolean good_uv[]  = new boolean[3];
		boolean good_uw[]  = new boolean[3];
		boolean good_vw[]  = new boolean[3];
		boolean good_uvw[] = new boolean[3];		
		boolean good_uwt[] = new boolean[3];
		boolean good_vwt[] = new boolean[3];
		boolean good_wut[] = new boolean[3];
		boolean good_uwtt[]= new boolean[3];
		boolean good_vwtt[]= new boolean[3];
		boolean good_wutt[]= new boolean[3];
		int rscutuw[]      = {60,35,35};
		int rscutvw[]      = {67,35,35};
		int rscutwu[]      = {67,35,35};
		int rsw[]          = {0,1,1};
		int adcutuw[]      = {70,5,5};
		int adcutvw[]      = {70,5,5};
		int adcutwu[]      = {70,5,5};

		//int tid            = 100000;
		//int cid            = 10000;
		int thr            = 10; //threshold count
		int iis            = CurrentSector;	//Sector 2 hardwired for now
		
		float uvw=0;
		
		H2D histadu;
		H1D hDalitz, hDalitzMCut;
		H2D HnumhitsUV, HnumhitsUW, HnumhitsVW;
		String name;
		String namedir;
		H1D pixelfilling;
		
		for (int is=0 ; is<6 ; is++) //initialization
		{
			for (int il=0 ; il<9 ; il++) 
			{
				nh[is][il] = 0;
				for (int ip=0 ; ip<68 ; ip++) 
				{
					strr[is][il][ip] = 0;
					adcr[is][il][ip] = 0;
					tdcr[is][il][ip] = 0;
				}
			}
		}
		double mc_t=0.0;
		if(event.hasBank("PCAL::true")==true)
		{
			EvioDataBank bank = (EvioDataBank) event.getBank("PCAL::true");
			int nrows = bank.rows();
			for(int i=0; i < nrows; i++)
			{
				mc_t = bank.getDouble("avgT",i);
			}	
		}
		if(event.hasBank("PCAL::dgtz")==true)
		{
			int ic=0;	// ic=0,1,2 -> PCAL,ECinner,ECouter
			uvw=0;
			float tdcmax=100000;
            EvioDataBank bank = (EvioDataBank) event.getBank("PCAL::dgtz");
            
            for(int i = 0; i < bank.rows(); i++)
            {
            	float tdc = (float)bank.getInt("TDC",i)-(float)(mc_t)*1000;
            	if (tdc<tdcmax) tdcmax=tdc;
            }
            
            for(int i = 0; i < bank.rows(); i++)
            {
            	int is  = bank.getInt("sector",i);
            	int ip  = bank.getInt("strip",i);
            	int il  = bank.getInt("view",i);
            	int adc = bank.getInt("ADC",i);
            	float tdc =(float)(bank.getInt("TDC",i));
            	tdc=((tdc-(float)mc_t*1000)-tdcmax+1340000)/1000;
            	
                if(is==iis)//whatever sector mentioned
                {
            	   if (adc>thr) 
            	   {
            	     nh[is-1][il-1]++;
            	     int inh = nh[is-1][il-1];
            	     adcr[is-1][il-1][inh-1] = adc;
            	     //tdcr[is-1][il-1][inh-1] = tdc;
            	     strr[is-1][il-1][inh-1] = ip;
            	     uvw=uvw+uvw_dalitz(ic,ip,il);
            	   }
            	   namedir = String.format("dalitz%02d", iteration);
            	   hDalitz = (H1D) getDir().getDirectory(namedir).getObject("Dalitz Condition");
                   hDalitz.fill(uvw);
                }
            }
            

         }
        
        // Logic: Limit multiplicity to 1 hit per view
        for (int il=0 ; il<9 ; il++)
        {
        	good_lay[il]=nh[iis-1][il]==1;
        	if (good_lay[il]) 
        	{
        		rs[il]=strr[iis-1][il][0];
        		ad[il]=adcr[iis-1][il][0];
        		td[il]=tdcr[iis-1][il][0];
        	}
        }
        
        // Logic: Good two-view and three-view multiplicity (m2,m3 cut)
        for (int ic=0 ; ic<3; ic++)
        {
        	good_uv[ic]   = good_lay[0+ic*3]&good_lay[1+ic*3];
        	good_uw[ic]   = good_lay[0+ic*3]&good_lay[2+ic*3];
        	good_vw[ic]   = good_lay[1+ic*3]&good_lay[2+ic*3];
        	good_uvw[ic]  =      good_uv[ic]&good_lay[2+ic*3];
        	
        	good_uwt[ic]  =  good_uw[ic]&rs[2+ic*3]==rscutuw[ic];
        	good_vwt[ic]  =  good_uv[ic]&rs[0+ic*3]==rscutvw[ic];
        	good_wut[ic]  =  good_uw[ic]&rs[rsw[ic]+ic*3]==rscutwu[ic];
        	good_uwtt[ic] = good_uwt[ic]&ad[2+ic*3]>adcutuw[ic];
        	good_vwtt[ic] = good_vwt[ic]&ad[0+ic*3]>adcutvw[ic];
        	good_wutt[ic] = good_wut[ic]&ad[rsw[ic]+ic*3]>adcutwu[ic];        	
        }  
        
        String histNameFormat[] = {"histU_%02d", "histV_%02d", "histW_%02d"};
		String histname;
		
        for (int ic=0 ; ic<3 ; ic++)//ic = {PCAL, EC inner, EC outer}
        {
        	if (good_uvw[ic])//multiplicity
        	{
        		if(ic == 0)//PCAL
        		{
        			namedir = String.format("dalitz%02d", iteration);
        			hDalitzMCut = (H1D) getDir().getDirectory(namedir).getObject("Dalitz Multiplicity Cut");
        			hDalitzMCut.fill(uvw);
        			HnumhitsUV = (H2D) getDir().getDirectory(namedir).getObject("numHitsUV");
        			HnumhitsUV.fill(rs[0],rs[1]);
        			HnumhitsUW = (H2D) getDir().getDirectory(namedir).getObject("numHitsUW");
        			HnumhitsUW.fill(rs[0],rs[2]);
        			HnumhitsVW = (H2D) getDir().getDirectory(namedir).getObject("numHitsVW");
        			HnumhitsVW.fill(rs[1],rs[2]);
        			for(int il = 0; il < 3; il++)//three layers il = {u,v,w}
        			{
        				namedir = String.format("crossStripHisto%02d", iteration);
        				histname = String.format(histNameFormat[il], rs[il]);
            			histadu = (H2D) getDir().getDirectory(namedir).getObject(histname);
            			if(il == 0)histadu.fill(rs[2],ad[il]);
            			else histadu.fill(rs[0],ad[il]);
        			} 
        			
        			
        			//check if pixel is valid with hitmatrix
        			//fill pixel histograms initialized by Nickinit()
        			if(hit[rs[0] - 1][rs[1] - 1][rs[2] - 1] == 1)
        			{
        				namedir = String.format("pixelsignal%02d", iteration);
            			name = String.format("adu_%02d_%02d_%02d", rs[0], rs[1], rs[2]);
            			pixelfilling = (H1D)getDir().getDirectory(namedir).getObject(name);
            			pixelfilling.fill(ad[0]);
            			
            			name = String.format("adv_%02d_%02d_%02d", rs[0], rs[1], rs[2]);
            			pixelfilling = (H1D)getDir().getDirectory(namedir).getObject(name);
            			pixelfilling.fill(ad[1]);
            			
            			name = String.format("adw_%02d_%02d_%02d", rs[0], rs[1], rs[2]);
            			pixelfilling = (H1D)getDir().getDirectory(namedir).getObject(name);
            			pixelfilling.fill(ad[2]);
        			}
        		}
        	}
        }
        
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Process")==0){
            DetectorEventProcessorDialog dialog = new DetectorEventProcessorDialog(this);
        }
    }
  
    
    public void process(){
    	   ProgressPrintout printout = new ProgressPrintout("Calibration");
    	   printout.setInterval(1.0);
    	   
    	   EvioSource  reader = new EvioSource();
    	   reader.open(this.inputFileName);
    	   int icounter = 0;
    	   while(reader.hasEvent()){
    	       icounter++;
    	       EvioDataEvent event = (EvioDataEvent) reader.getNextEvent();
    	       try {
    	           processEvent(event);
    	       } catch (Exception e){
    	           System.err.println("[PCAL Calibration] ----> error in event " + icounter);
    	           e.printStackTrace();
    	       }
    	       
    	       printout.setAsInteger("nevents", icounter);
    	       printout.updateStatus();
    	    }
    	   //this.analyze();
    	}
    
    public void Nickanalyze() 
	{
		double centroids[] = new double[20000];
		double x[] = new double[20000];
		double ex[] = new double[20000];
		double ey[] = new double[20000];
		int counter = 0;
		//RootCanvas canvas = new RootCanvas();
		TDirectory fitdir = new TDirectory("attendir"); //creates directory
		H1D tempsignal;
		F1D myfunc2;
		GraphErrors attengraph;
		String name;
		String namedir;
		
		counter = 0;
		for(int ustrip = 0; ustrip < 68; ++ustrip)
		{
			counter = 0;
			for(int vstrip = 0; vstrip < 62; ++vstrip)
			{
				for(int wstrip = 0; wstrip < 62; ++wstrip)
				{
					if(hit[ustrip][vstrip][wstrip] == 1)// && udpixel[ustrip][vstrip][wstrip] > 0.0 && vdpixel[ustrip][vstrip][wstrip] > 0.0  && wdpixel[ustrip][vstrip][wstrip] > 0.0
					{
						namedir = String.format("pixelsignal%02d", iteration);
						name = String.format("adu_%02d_%02d_%02d", ustrip + 1, vstrip + 1, wstrip + 1);
						tempsignal  = (H1D)getDir().getDirectory(namedir).getObject(name);

						//centroids[counter] = myfunc.getParameter(1);
						if(tempsignal.getMean() > 1 && tempsignal.getEntries() > 10)
						{
							centroids[counter] = tempsignal.getMean();
							ey[counter] = 1.0; 
							
							x[counter] = udpixel[ustrip][vstrip][wstrip];
							ex[counter] = 1.0;
							++counter;
						}
						
					
					}
				}
			}
			name = String.format("attu_%02d", ustrip + 1);
			attengraph = graphn(name,counter,x,centroids,ex,ey);
		
			//create function and fit
			myfunc2 = new F1D("exp",0.0,500.0); //"mycustomfunc",
			myfunc2.parameter(0).setValue(100.0);
			//myfunc2.setParLimits(0, 0.0, 200.0);
			myfunc2.parameter(1).setValue(-0.002659574468);
			//myfunc2.setParLimits(1, 0.0, 105.0);
			//attengraph.fit(myfunc2);
			
			fitdir.add(attengraph);
		}
		
		
		for(int vstrip = 0; vstrip < 62; ++vstrip)
		{
			counter = 0;
			for(int ustrip = 0; ustrip < 68; ++ustrip)
			{
				for(int wstrip = 0; wstrip < 62; ++wstrip)
				{
					if(hit[ustrip][vstrip][wstrip] == 1)// && udpixel[ustrip][vstrip][wstrip] > 0.0 && vdpixel[ustrip][vstrip][wstrip] > 0.0  && wdpixel[ustrip][vstrip][wstrip] > 0.0
					{
						namedir = String.format("pixelsignal%02d", iteration);
						name = String.format("adv_%02d_%02d_%02d", ustrip + 1, vstrip + 1, wstrip + 1);
						tempsignal  = (H1D)getDir().getDirectory(namedir).getObject(name);

						//centroids[counter] = myfunc.getParameter(1);
						if(tempsignal.getMean() > 1)
						{
							centroids[counter] = tempsignal.getMean();
							ey[counter] = 1.0; 
							
							x[counter] = vdpixel[ustrip][vstrip][wstrip];
							ex[counter] = 1.0;
							++counter;
						}
						
					
					}
				}
			}
			name = String.format("attv_%02d", vstrip + 1);
			attengraph = graphn(name,counter,x,centroids,ex,ey);
		
			//create function and fit
			myfunc2 = new F1D("exp",0.0,500.0); //"mycustomfunc",
			myfunc2.parameter(0).setValue(100.0);
			//myfunc2.setParLimits(0, 0.0, 200.0);
			myfunc2.parameter(1).setValue(-0.002659574468);
			//myfunc2.setParLimits(1, 0.0, 105.0);
			//attengraph.fit(myfunc2);
			
			fitdir.add(attengraph);
		}
		
		
		for(int wstrip = 0; wstrip < 62; ++wstrip)
		{
			counter = 0;
			for(int vstrip = 0; vstrip < 62; ++vstrip)
			{
				for(int ustrip = 0; ustrip < 68; ++ustrip)
				{
					if(hit[ustrip][vstrip][wstrip] == 1)// && udpixel[ustrip][vstrip][wstrip] > 0.0 && vdpixel[ustrip][vstrip][wstrip] > 0.0  && wdpixel[ustrip][vstrip][wstrip] > 0.0
					{
						namedir = String.format("pixelsignal%02d", iteration);
						name = String.format("adw_%02d_%02d_%02d", ustrip + 1, vstrip + 1, wstrip + 1);
						tempsignal  = (H1D)getDir().getDirectory(namedir).getObject(name);

						//centroids[counter] = myfunc.getParameter(1);
						if(tempsignal.getMean() > 1)
						{
							centroids[counter] = tempsignal.getMean();
							ey[counter] = 1.0; 
							
							x[counter] = wdpixel[ustrip][vstrip][wstrip];
							ex[counter] = 1.0;
							++counter;
						}
					}
				}
			}
			name = String.format("attw_%02d", wstrip + 1);
			attengraph = graphn(name,counter,x,centroids,ex,ey);
		
			//create function and fit
			myfunc2 = new F1D("exp",0.0,500.0); //"mycustomfunc",
			myfunc2.parameter(0).setValue(100.0);
			//myfunc2.setParLimits(0, 0.0, 200.0);
			myfunc2.parameter(1).setValue(-0.002659574468);
			//myfunc2.setParLimits(1, 0.0, 105.0);
			//attengraph.fit(myfunc2);
			
			fitdir.add(attengraph);
		}
		
		getDir().addDirectory(fitdir);

		
	}
	
  //graphErrors constructor
  	public GraphErrors graphn(String name, int numberpoints, double x[], double y[], double xe[], double ye[])
    {
          double a[] = new double[numberpoints];
          double b[] = new double[numberpoints];
          double ae[] = new double[numberpoints];
          double be[] = new double[numberpoints];
          
          for(int i = 0; i < numberpoints; ++i)
          {
              a[i] = x[i];
              ae[i] = xe[i];
              b[i] = y[i];
              be[i] = ye[i];            
          }
          
          GraphErrors mygraph = new GraphErrors(a,b,ae,be);
          mygraph.setName(name);
          mygraph.setTitle(name);
          
          return mygraph;
    }
  	
  	
  	//crossstrip needs to be 1-62 or 1-68 not 0
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
      
      //xdistance needs to be 1-62 or 1-68 not 0
      public double[] CalcDistance(char stripletter, double xdistance, double xdistanceE)
      {
          double distperstrip = 5.055;
          
          if(stripletter == 'u' || stripletter == 'U')
          {
              //convert strip number to distance
              xdistance = Math.abs(xdistance - 77.0) * distperstrip;
              xdistanceE = xdistanceE * distperstrip;
          }
          else if(stripletter == 'v' || stripletter == 'w' || stripletter == 'V' || stripletter == 'W')
          {
              //convert strip number to distance
              xdistance = Math.abs(xdistance - 84.0) * distperstrip;
              xdistanceE = xdistanceE * distperstrip;
          }
          return new double[] {xdistance, xdistanceE};
      }
      
	

	
	public void NickInit() 
	{
		int ustrip, vstrip, wstrip;
		//int counter = 0;
		String name;
		String namedir;
		H1D sig;
		
		namedir = String.format("pixelsignal%02d", iteration);
		TDirectory pixelsignal = new TDirectory(namedir);
		
		//declare histograms
		//add to directory
		for(ustrip = 0; ustrip < 68; ++ustrip)
		{
			for(vstrip = 0; vstrip < 62; ++vstrip)
			{
				for(wstrip = 0; wstrip < 62; ++wstrip)
				{
					if(hit[ustrip][vstrip][wstrip] == 1)
					{
						name = String.format("adu_%02d_%02d_%02d", ustrip + 1, vstrip + 1, wstrip + 1);
						sig = new H1D(name,100,0.0,300.0);
						pixelsignal.add(sig);
						//adcH.add(0, 2, ustrip * 10000 + vstrip * 100 + wstrip, sig);
						
						name = String.format("adv_%02d_%02d_%02d", ustrip + 1, vstrip + 1, wstrip + 1);
						sig = new H1D(name,100,0.0,300.0);
						pixelsignal.add(sig);
						//adcH.add(0, 2, ustrip * 10000 + vstrip * 100 + wstrip, sig);
						
						name = String.format("adw_%02d_%02d_%02d", ustrip + 1, vstrip + 1, wstrip + 1);
						sig = new H1D(name,100,0.0,300.0);
						pixelsignal.add(sig);
						//adcH.add(0, 2, ustrip * 10000 + vstrip * 100 + wstrip, sig);
						
						
						
					}
				}
			}
		}
		
		getDir().addDirectory(pixelsignal);
	}
	
	public void init() 
	{
		                            //strip number
		String histNameFormat[] = {"histU_%02d", "histV_%02d", "histW_%02d"};
		String histname;
		String namedir;
		
		int stripMax[] = {68, 62, 62};//u, v, w
		int binNum[] = {62,68,68};//w, u, u
		double binMaxX[] = {62.5,68.5,68.5};
	
		namedir = String.format("crossStripHisto%02d", iteration);
		TDirectory geometry = new TDirectory(namedir);
		namedir = String.format("dalitz%02d", iteration);
		TDirectory calADC = new TDirectory(namedir);
		
		calADC.add(new H1D("Dalitz Condition",500,0.,3.0));
		calADC.add(new H1D("Dalitz Multiplicity Cut",500,0.,3.0));
		calADC.add(new H2D("numHitsUV",68,0.5,68.5,62,0.5,62.5));
		calADC.add(new H2D("numHitsUW",68,0.5,68.5,62,0.5,62.5));
		calADC.add(new H2D("numHitsVW",62,0.5,62.5,62,0.5,62.5));
		
		//declare histograms and add to directory
		//loop over u,v,w (i.e. u == 0, v == 1, and w == 2)
		for(int il = 0; il < 3; il++)
		{
			//loop over cross strips, in case of U it is W's
			//stripMax[il] = 68,62,62
			for(int strip = 0; strip < stripMax[il]; ++strip)
			{
				histname = String.format(histNameFormat[il], strip + 1);	
				geometry.add(new H2D(histname,binNum[il],0.5,binMaxX[il],100,0.0,300.0));
			}
		}
		//this directory is for attenuation 2D plots
		getDir().addDirectory(geometry);
		//this directory is for other interesting quanitities
		getDir().addDirectory(calADC);
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
	
	
	
	
	
	public void FillStaticArrays() 
	{
		int counter = 0;
		//read in
		Scanner scanner;
		try 
		{
			scanner = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/hitmatrix.txt"));
			while(scanner.hasNextInt())
			{
				   for (int is=0 ; is<68 ; is++) 
				   {
						for (int il=0 ; il<62 ; il++) 
						{
							for (int ip=0 ; ip<62 ; ip++) 
							{
								hit[is][il][ip] = scanner.nextInt();
								if(hit[is][il][ip] == 1)
								{
									pixelnumber[is][il][ip] = counter;
									++counter;
								}
							}
						}
				   }
			}
			scanner.close();
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//read in
		Scanner udist;
		try 
		{
			udist = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/udist.txt"));
			while(udist.hasNextDouble())
			{
				   for (int is=0 ; is<68 ; is++) 
				   {
						for (int il=0 ; il<62 ; il++) 
						{
							for (int ip=0 ; ip<62 ; ip++) 
							{
								udpixel[is][il][ip] = udist.nextDouble();
							}
						}
				   }
			}
			udist.close();
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//read in
				Scanner vdist;
				try 
				{
					vdist = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/vdist.txt"));
					while(vdist.hasNextDouble())
					{
						   for (int is=0 ; is<68 ; is++) 
						   {
								for (int il=0 ; il<62 ; il++) 
								{
									for (int ip=0 ; ip<62 ; ip++) 
									{
										vdpixel[is][il][ip] = vdist.nextDouble();
									}
								}
						   }
					}
					vdist.close();
				} 
				catch (FileNotFoundException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//read in
				Scanner wdist;
				try 
				{
					wdist = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/wdist.txt"));
					while(wdist.hasNextDouble())
					{
						   for (int is=0 ; is<68 ; is++) 
						   {
								for (int il=0 ; il<62 ; il++) 
								{
									for (int ip=0 ; ip<62 ; ip++) 
									{
										wdpixel[is][il][ip] = wdist.nextDouble();
									}
								}
						   }
					}
					wdist.close();
				} 
				catch (FileNotFoundException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
	}

	public void OutputStaticArrays() 
	{
	   //write out
	   PrintWriter writer = null;
		try 
		{
			writer = new PrintWriter("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/hitmatrix.dat");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int is=0 ; is<68 ; is++) 
		{
			for (int il=0 ; il<62 ; il++) 
			{
				for (int ip=0 ; ip<62 ; ip++) 
				{
					writer.print(hit[is][il][ip]);
					writer.print(" ");
				}
				writer.print("\n");
			}
			writer.print("\n");
			writer.print("\n");
		}
	   writer.close();
	}
    
	public void analyze() 
	{
		//create directories
		String namedir;
		namedir = String.format("Projection%02d", iteration);
		TDirectory projection = new TDirectory(namedir);
		namedir = String.format("GaussFit%02d", iteration);
		TDirectory gausFitDir = new TDirectory(namedir);
		namedir = String.format("ExpoFit%02d", iteration);
		TDirectory expofit = new TDirectory(namedir);
		namedir = String.format("GraphE%02d", iteration);
		TDirectory graph = new TDirectory(namedir);
		
		double centroids[] = new double[500];
		double centroidsErr[] = new double[500];
		
		double x[] = new double[500];
		double ex[] = new double[500];
		double ey[] = new double[500];
		double xTemp[]   = new double[500];
		double xTempEr[] = new double[500];
		double minx = 0.0;
		double maxx = 0.0;
		int counter = 0;
		
		//histograms
		H2D Hadc[] = new H2D[100];
		//projections
		H1D ProjHadc[] = new H1D[100];
		
		//fit function
		F1D expfit;
		F1D gausFit;//[] = new F1D[62*68];

		String histName;
		String projName;
		String graphName;
		//String plotName;
		String functionName;
		String gaussFuncName;

		
		PrintWriter writer = null;
	
		//U,V,W strip calibration
		//int count = 0;
		
		char stripLetter[] = {'u','v','w'};
		String histNameFormat[] = {"histU_%02d", "histV_%02d", "histW_%02d"};
		String projNameFormat[] = {"ProjHistU%02d_W%02d", "ProjHistV%02d_U%02d", "ProjHistW%02d_U%02d"};
		String gaussfuncNameFormat[] = {"gaussU%02d_%02d", "gaussV%02d_%02d", "gaussW%02d_%02d"};
		String graphNameFormat[] = {"graphU_%02d", "graphV_%02d", "graphW_%02d"};
		String functionNameFormat[] = {"expU_%02d", "expV_%02d", "expW_%02d"};
		String fileName[] = {"/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/UattenCoeff.dat", 
							 "/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/VattenCoeff.dat", 
							 "/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/WattenCoeff.dat"};
		
		int stripMax[] = {68, 62, 62};//u, v, w
		int crossStripMax[] = {62, 68, 68};//w, u, u
		
		//create gaussian function and fit
		
		for(int il = 0; il < 3; ++il)
		{
			try 
			{
				writer = new PrintWriter(fileName[il]);
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//TCanvas Ucan = new TCanvas("Ucan", "Ucan", 800, 600, 2, 2);
			for(int strip = 0; strip < stripMax[il]; ++strip)//calibrating u-strip
			{
				histName = String.format(histNameFormat[il], strip + 1);
				namedir = String.format("crossStripHisto%02d", iteration);
				Hadc[strip] = (H2D)getDir().getDirectory(namedir).getObject(histName);
				counter = 0;
				for(int crossStrip = 0; crossStrip < crossStripMax[il]; crossStrip++)
				{
					projName = String.format(projNameFormat[il], strip+1, crossStrip + 1);
					ProjHadc[crossStrip] = Hadc[strip].sliceX(crossStrip);
					ProjHadc[crossStrip].setName(projName);
								
					//create gaussian function and fit
					gausFit = new F1D("gaus",0.0,150.0);
					gaussFuncName = String.format(gaussfuncNameFormat[il], strip+1, crossStrip + 1);
					gausFit.setName(gaussFuncName);
					gausFit.setParameter(0, ProjHadc[crossStrip].getBinContent(ProjHadc[crossStrip].getMaximumBin()));
					gausFit.setParLimits(0, 0.0, 500.0);
					gausFit.setParameter(1, ProjHadc[crossStrip].getMean());
					gausFit.setParLimits(1, 0.0, 150.0);
					gausFit.setParameter(2, ProjHadc[crossStrip].getRMS());
					gausFit.setParLimits(2, 0.0, 200.0);
					
					
					if(ProjHadc[crossStrip].getEntries() >= 20)
					{
						projection.add(ProjHadc[crossStrip]);
						//System.out.println(il + "    " + strip + "    " + crossStrip);
						ProjHadc[crossStrip].fit(gausFit,"Q");
						gausFitDir.add(gausFit);
						
						centroids[counter] = gausFit.getParameter(1);
						centroidsErr[counter] = (float)gausFit.getParameter(2)/Math.sqrt(ProjHadc[crossStrip].getEntries());
					}
					else
					{
						centroids[counter] = ProjHadc[crossStrip].getMean();
						centroidsErr[counter] = (float)ProjHadc[crossStrip].getRMS()/Math.sqrt(ProjHadc[crossStrip].getEntries());
						
					}
					gausFit = null;
					if(centroids[counter] >= 1.0)
					{
						xTemp[counter] = CalcDistinStrips(stripLetter[il], crossStrip+1)[0];//calcDistinStrips
						xTempEr[counter] = CalcDistinStrips(stripLetter[il], crossStrip+1)[1];//calcDistinStrips
						
						x[counter] = CalcDistance(stripLetter[il], xTemp[counter], xTempEr[counter])[0];
						ex[counter] = CalcDistance(stripLetter[il], xTemp[counter], xTempEr[counter])[1];
						//centroids[counter] = ProjHadc[crossStrip].getMean();
						
						ey[counter] = 1.0;
						counter++;
					}
				}
				graphName = String.format(graphNameFormat[il], strip + 1);
				GraphErrors attengraph = graphn(graphName,counter,x,centroids,ex,centroidsErr);
				if(counter < 5)
				{
					minx = 500.0;
					maxx = 0.00;
				}
				else
				{
					minx = x[1];
					maxx = x[counter-2];
				}
				//Ucan.cd(count);
				//Ucan.draw(attengraph);
				
				//create function and fit
				functionName = String.format(functionNameFormat[il], strip + 1);
				//expfit = new F1D("exp+p0",maxx,minx);
				
				expfit = new F1D("exp+p0",maxx,minx);
				expfit.setName(functionName);
				expfit.parameter(0).setValue(100.0);
				expfit.setParLimits(0, -1000.0, 1000.0);
				expfit.parameter(1).setValue(-0.002659574468);
				expfit.parameter(2).setValue(10.0);
				
	
				attengraph.fit(expfit);
				//Ucan.draw(expfit,"same");
				graph.add(attengraph);
				expofit.add(expfit);

				int j = strip+1;
				writer.println(j  + "   " + x[0] + "   "
						+ expfit.getParameter(0) + "   " 
						+ expfit.getParameter(1) + "   " 
						+ expfit.getParameter(2));
				//++count;
			}
			writer.close();
		}
		getDir().addDirectory(projection);
		getDir().addDirectory(gausFitDir);
		getDir().addDirectory(expofit);
		getDir().addDirectory(graph);
		
	}
	
	
	public void getAttenuationCoefficients()
    {
			
    	int stripnum = 0;
    	int ijunk;
    	int counter = 0;
        Scanner scanner;
        //generted values
        try 
		{
			scanner = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/AttenCoeffSec4a.dat"));
			while(scanner.hasNextInt())
			{
				if(counter < 68)
				{
					ijunk = scanner.nextInt();
					stripnum = scanner.nextInt();
					//umaxX[stripnum - 1] = scanner.nextDouble();
					genuA[stripnum - 1] = scanner.nextDouble();
					genuB[stripnum - 1] = scanner.nextDouble();
					genuC[stripnum - 1] = scanner.nextDouble();
					ijunk = scanner.nextInt();
					
					genuA[stripnum - 1] *= 100.0/650.0;
					genuC[stripnum - 1] *= 100.0/650.0;
				}
				else if(counter < 130)
				{
					ijunk = scanner.nextInt();
					stripnum = scanner.nextInt();
					//umaxX[stripnum - 1] = scanner.nextDouble();
					genvA[stripnum - 1] = scanner.nextDouble();
					genvB[stripnum - 1] = scanner.nextDouble();
					genvC[stripnum - 1] = scanner.nextDouble();
					ijunk = scanner.nextInt();
					
					genvA[stripnum - 1] *= 100.0/650.0;
					genvC[stripnum - 1] *= 100.0/650.0;
				}
				else
				{
					ijunk = scanner.nextInt();
					stripnum = scanner.nextInt();
					//umaxX[stripnum - 1] = scanner.nextDouble();
					genwA[stripnum - 1] = scanner.nextDouble();
					genwB[stripnum - 1] = scanner.nextDouble();
					genwC[stripnum - 1] = scanner.nextDouble();
					ijunk = scanner.nextInt();
					
					genwA[stripnum - 1] *= 100.0/650.0;
					genwC[stripnum - 1] *= 100.0/650.0;
				}
				++counter;
				
			}
			scanner.close();
		}
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		try 
		{
			scanner = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/UattenCoeff.dat"));
			while(scanner.hasNextInt())
			{
				stripnum = scanner.nextInt();
				umaxX[stripnum - 1] = scanner.nextDouble();
				uA[stripnum - 1] = scanner.nextDouble();
				uB[stripnum - 1] = scanner.nextDouble();
				uC[stripnum - 1] = scanner.nextDouble();
				
				
				ugain[stripnum - 1] = (uA[stripnum - 1] + uC[stripnum - 1]);
				//uA[stripnum - 1] *= ugain[stripnum - 1];
				//uC[stripnum - 1] *= ugain[stripnum - 1];
			}
			scanner.close();
		}
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try 
		{
			scanner = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/VattenCoeff.dat"));
			while(scanner.hasNextInt())
			{
				stripnum = scanner.nextInt();
				vmaxX[stripnum - 1] = scanner.nextDouble();
				vA[stripnum - 1] = scanner.nextDouble();
				vB[stripnum - 1] = scanner.nextDouble();
				vC[stripnum - 1] = scanner.nextDouble();
				
				
				vgain[stripnum - 1] = (vA[stripnum - 1] + vC[stripnum - 1]);
				//vA[stripnum - 1] *= vgain[stripnum - 1];
				//vC[stripnum - 1] *= vgain[stripnum - 1];
			}
			scanner.close();
		}
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try 
		{
			scanner = new Scanner(new File("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/WattenCoeff.dat"));
			while(scanner.hasNextInt())
			{
				stripnum = scanner.nextInt();
				wmaxX[stripnum - 1] = scanner.nextDouble();
				wA[stripnum - 1] = scanner.nextDouble();
				wB[stripnum - 1] = scanner.nextDouble();
				wC[stripnum - 1] = scanner.nextDouble();
				
				
				wgain[stripnum - 1] = (wA[stripnum - 1] + wC[stripnum - 1]);
				//wA[stripnum - 1] *= wgain[stripnum - 1];
				//wC[stripnum - 1] *= wain[stripnum - 1];
			}
			scanner.close();
		}
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
    }
	
    public static void main(String[] args){    	
    	//PCALcalib detview = new PCALcalib("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/fc-muon-100k.evio");
    	//PCALcalib detview = new PCALcalib("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/fc-muon-500k.evio");
    	//PCALcalib detview = new PCALcalib("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/fc-muon-3M-s2.evio");
    	
    	//Draws detector views
    	PCALcalib detview = new PCALcalib();
    	
    	//import hitmatrix
    	//import general info based on runnumber, sector, and file
    	detview.FillStaticArrays();
    	
    	for(iteration = 0; iteration < detview.numiterations; ++iteration)
    	{
    		detview.getAttenuationCoefficients();
    		
	    	//initialize histograms that are sector and iteration dependent
	    	//sets up histograms A
	    	detview.NickInit();
	    	detview.init(); 
			   
	    	
	    	//fills sector and iteration dep. histogram
	    	//incorporate specfic cuts
			detview.process();
			  
			   
			
			//fits signal histograms
			//makes graph of centroids
			//fits graphs of centroids
			detview.Nickanalyze();
			detview.analyze();
			
			//calculate new gains and attenuation coefficients
			//detview.computegains();
    	}
		
    	iteration = 0;
		//output attenuation coefficients, gains, sector, and run number
		//detview.OutputStaticArrays();
		
		TBrowser browser = new TBrowser(detview.getDir()); //shows histograms  
    }




}