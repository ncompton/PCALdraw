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
import org.jlab.clasrec.rec.CLASMonitoring;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioSource;
import org.root.attr.ColorPalette;
import org.root.func.F1D;
import org.root.group.TBrowser;
import org.root.group.TDirectory;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.histogram.H2D;
import org.root.pad.EmbeddedCanvas;
import org.root.pad.TCanvas;

/**
 *
 * @author gavalian
 * edited by N. Compton
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
    
    private String inputFileName = "/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/fc-muon-500k.evio";
    private int RunNumber = 4284;
    private int CurrentSector = 5;
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
	
	double uA[] = new double[68]; //
	double uB[] = new double[68]; //
	double uC[] = new double[68]; //
	
	double vA[] = new double[62]; //
	double vB[] = new double[62]; //
	double vC[] = new double[62]; //
	
	double wA[] = new double[62]; //
	double wB[] = new double[62]; //
	double wC[] = new double[62]; //
	
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
    	UVpane();
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
    
    private void UVpane(){
    	DetectorShapeView2D  dv4 = new DetectorShapeView2D("PCAL UV");
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
    
    /**
     * When the detector is clicked, this function is called
     * @param desc 
     */
    public void detectorSelected(DetectorDescriptor desc){
        int u, v, w, uvwnum;
        String name;
        String namedir;
        H1D h1, hsum = null;
        GraphErrors g1;
        
        
        System.out.println("SELECTED = " + desc);
        
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
        if(desc.getLayer() == 3)
        {
        	//this.canvas = new EmbeddedCanvas();
        	//this.canvas.update();
	        canvas.divide(2,2);
	        uvwnum = (int)desc.getComponent();
	    	u = (int)(uvwnum/100.0);
	    	uvwnum -= u*100;
	    	w = uvwnum;
	    	v = 61;
	    	int count = 0;
	    	
	    	for(v = 0; v < 62; ++v)
	    	{
	    		if(hit[u][v][w] == 1)
	    		{
		    		name = String.format("adu_%02d_%02d_%02d", u + 1, v + 1, w + 1);
		    		h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    		if(count == 0){
		    			hsum = h1;
		    			++count;
		    		}
		    		else hsum.add(h1);
	    		}
	    	}
	        canvas.cd(0);
	        canvas.draw(hsum);
	        
	        for(v = 0; v < 62; ++v)
	    	{
	        	if(hit[u][v][w] == 1)
	    		{
		    		name = String.format("adw_%02d_%02d_%02d", u + 1, v + 1, w + 1);
		    		h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    		if(count == 0){
		    			hsum = h1;
		    			++count;
		    		}
		    		else hsum.add(h1);
	    		}
	    	}
	        canvas.cd(1);
	        canvas.draw(hsum);
	        
	        name = String.format("attu_%02d", u + 1);
		    g1  = (GraphErrors)getDir().getDirectory("attendir").getObject(name);
		    canvas.cd(2);
		    canvas.draw(g1);
	        
	        name = String.format("attw_%02d", w + 1);
	        g1  = (GraphErrors)getDir().getDirectory("attendir").getObject(name);
	        canvas.cd(3);
	        canvas.draw(g1);
	          
        }
        if(desc.getLayer() == 4)
        {
	        canvas.divide(2,2);
	        uvwnum = (int)desc.getComponent();
	    	u = (int)(uvwnum/100.0);
	    	uvwnum -= u*100;
	    	v = uvwnum;
	    	w = 61;
	    	int count = 0;
	    	
	    	for(w = 0; w < 62; ++w)
	    	{
	    		if(hit[u][v][w] == 1)
	    		{
		    		name = String.format("adu_%02d_%02d_%02d", u + 1, v + 1, w + 1);
		    		h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    		if(count == 0){
		    			hsum = h1;
		    			++count;
		    		}
		    		else hsum.add(h1);
	    		}
	    	}
	        canvas.cd(0);
	        canvas.draw(hsum);
	        
	        for(w = 0; w < 62; ++w)
	    	{
	        	if(hit[u][v][w] == 1)
	    		{
		    		name = String.format("adv_%02d_%02d_%02d", u + 1, v + 1, w + 1);
		    		h1  = (H1D)getDir().getDirectory(namedir).getObject(name);
		    		if(count == 0){
		    			hsum = h1;
		    			++count;
		    		}
		    		else hsum.add(h1);
	    		}
	    	}
	        canvas.cd(1);
	        canvas.draw(hsum);
	        
	        name = String.format("attu_%02d", u + 1);
		    g1  = (GraphErrors)getDir().getDirectory("attendir").getObject(name);
		    canvas.cd(2);
		    canvas.draw(g1);
	        
	        name = String.format("attv_%02d", v + 1);
	        g1  = (GraphErrors)getDir().getDirectory("attendir").getObject(name);
	        canvas.cd(3);
	        canvas.draw(g1);
	        
	        
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
		
		int tid            = 100000;
		int cid            = 10000;
		int lid            = 100;		

		int thr            = 15;
		int iis            = CurrentSector;	//Sector 5 hardwired for now
		
		int hid,hidd;

		
		
		H2D hadc,htdc;
		H1D hpix, whatever;
		String name;
		String namedir;
		//H1D usig[] = new H1D[6000];
		//H1D vsig[] = new H1D[6000];
		//H1D wsig[] = new H1D[6000];
		
		for (int is=0 ; is<6 ; is++) 
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
			float uvw=0;
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
            	
                if(is==iis)
                {
            	   if (adc>thr) 
            	   {
            	     nh[is-1][il-1]++;
            	     int inh = nh[is-1][il-1];
            	     adcr[is-1][il-1][inh-1] = adc;
            	     tdcr[is-1][il-1][inh-1] = tdc;
            	     strr[is-1][il-1][inh-1] = ip;
            	     uvw=uvw+uvw_dalitz(ic,ip,il);
            	   }
            	   
               	   hid = (int) (1e7*is+10*tid+ic*cid+il*lid);
               	   namedir = String.format(laba[ic]+ "%02d", iteration);
            	   hadc = (H2D) getDir().getDirectory(namedir).getObject("A"+hid);
            	   namedir = String.format(labt[ic]+ "%02d", iteration);
            	   htdc = (H2D) getDir().getDirectory(namedir).getObject("T"+hid);
                   hadc.fill(adc,ip);
                   htdc.fill(tdc,ip);
            	   hid = (int) (1e7*is+11*tid+ic*cid);
            	   namedir = String.format(laba[ic]+ "%02d", iteration);
            	   hpix = (H1D) getDir().getDirectory(namedir).getObject("A"+hid);
                   hpix.fill(uvw);
               }
            }
         }		
		
		mc_t=0.0;
		if(event.hasBank("EC::true")==true)
		{
			EvioDataBank bank = (EvioDataBank) event.getBank("EC::true");
			int nrows = bank.rows();
			for(int i=0; i < nrows; i++)
			{
				mc_t = bank.getDouble("avgT",i);
			}	
		}
		
		if(event.hasBank("EC::dgtz")==true)
		{
        	float uvw=0;
            float tdcmax=100000;
            EvioDataBank bank = (EvioDataBank) event.getBank("EC::dgtz");
            
            for(int i = 0; i < bank.rows(); i++)
            {
            	float tdc = (float)bank.getInt("TDC",i)-(float)mc_t*1000;
            	if (tdc<tdcmax) tdcmax=tdc;
            }
            
            for(int i = 0; i < bank.rows(); i++)
            {
            	int  is = bank.getInt("sector",i); //sector
            	int  ip = bank.getInt("strip",i); //strip 68,62,62, 35,35
             	int  ic = bank.getInt("stack",i); //pcal = 0, ecin = 1, ecout = 2
            	int  il = bank.getInt("view",i); //u=1, v = 2, w =3
            	int adc = bank.getInt("ADC",i);
            	int tdc = bank.getInt("TDC",i);
            	
            	float tdcc=(((float)tdc-(float)mc_t*1000)-tdcmax+1340000)/1000;
            	
            	int  iv = ic*3+il;
                
                if(is==iis)
                {
            	   if (adc>thr) 
            	   {
            	     nh[is-1][iv-1]++;
            	     int inh = nh[is-1][iv-1];
            	     adcr[is-1][iv-1][inh-1] = adc;
            	     tdcr[is-1][iv-1][inh-1] = tdcc;
            	     strr[is-1][iv-1][inh-1] = ip;
            	     uvw=uvw+uvw_dalitz(ic,ip,il);
            	   }
            	   
                   hid = (int) (1e7*is+10*tid+ic*cid+il*lid);
                   namedir = String.format(laba[ic]+ "%02d", iteration);
            	   hadc = (H2D) getDir().getDirectory(namedir).getObject("A"+hid);
            	   namedir = String.format(labt[ic]+ "%02d", iteration);
            	   htdc = (H2D) getDir().getDirectory(namedir).getObject("T"+hid);
                   hadc.fill(adc,ip);
                   htdc.fill(tdcc,ip);
            	   hid = (int) (1e7*is+11*tid+ic*cid);
            	   namedir = String.format(laba[ic]+ "%02d", iteration);
            	   hpix = (H1D) getDir().getDirectory(namedir).getObject("A"+hid);
                   hpix.fill(uvw);
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
        
        // Histo: Check plots using trigger condition (here u.v coincidence) (TAG=15)
        
        hid  = (int) (1e7*iis);
        
        for (int ic=0 ; ic<3 ; ic++)
        {
        	if (good_uv[ic]) 
        	{
        		for (int il=1 ; il<4 ; il++) 
        		{
        			hidd = hid+15*tid+ic*cid+il*lid;
        			namedir = String.format(laba[ic]+ "%02d", iteration);
        			hadc = (H2D) getDir().getDirectory(namedir).getObject("A"+hidd);
        			int ill=ic*3+il-1;
        			hadc.fill(ad[ill],rs[ill]);
        		}
        	}
        }
        
        // Histo: MIP plots using m2 and s cuts (TAG=20)
 
        H2D hadca[] = new H2D[3];
        
        for (int ic=0 ; ic<3 ; ic++)
        { 	
        	for (int il=1 ; il<4 ; il++) 
        	{
        		hidd = hid+20*tid+ic*cid+il*lid;   
        		namedir = String.format(laba[ic]+ "%02d", iteration);
        		hadca[il-1] = (H2D) getDir().getDirectory(namedir).getObject("A"+hidd);
        	}
        	if(good_uwt[ic]) hadca[0].fill(ad[ic*3+0],rs[ic*3+0]);
            if(good_vwt[ic]) hadca[1].fill(ad[ic*3+1],rs[ic*3+1]);
            if(good_wut[ic]) hadca[2].fill(ad[ic*3+2],rs[ic*3+2]);
        } 
        
        // Histo: MIP plots using Dalitz m3 cut (TAG=21) and s cut (TAG=22)
        
        //ic = 0 -> PCAL
        for (int ic=0 ; ic<3 ; ic++) 
        {
        	if (good_uvw[ic]) 
        	{
        		if(ic == 0 && hit[rs[0] - 1][rs[1] - 1][rs[2] - 1] == 1)
        		{
        			if(rs[0] == 67 && rs[2] % 5 == 0)
        			{
        				namedir = String.format(laba[ic]+ "%02d", iteration);
	        			whatever = (H1D) getDir().getDirectory(namedir).getObject("u67w" + rs[2] + "adu");
	        			whatever.fill(ad[0]);	
        			}
        				
        			//hit[rs[0] - 1][rs[1] - 1][rs[2] - 1] = 1;
        			
        			namedir = String.format("pixelsignal%02d", iteration);
        			name = String.format("adu_%02d_%02d_%02d", rs[0], rs[1], rs[2]);
        			whatever = (H1D)getDir().getDirectory(namedir).getObject(name);
        			whatever.fill(ad[0]);
        			
        			name = String.format("adv_%02d_%02d_%02d", rs[0], rs[1], rs[2]);
        			whatever = (H1D)getDir().getDirectory(namedir).getObject(name);
        			whatever.fill(ad[1]);
        			
        			name = String.format("adw_%02d_%02d_%02d", rs[0], rs[1], rs[2]);
        			whatever = (H1D)getDir().getDirectory(namedir).getObject(name);
        			whatever.fill(ad[2]);
        			
        			   
        		}
            	for (int il=1 ; il<4 ; il++) 
            	{
            		namedir = String.format(laba[ic]+ "%02d", iteration);
            		hidd = hid+21*tid+ic*cid+il*lid;   
            		hadca[il-1] = (H2D) getDir().getDirectory(namedir).getObject("A"+hidd);
            	}
                if(good_uwt[ic]) hadca[0].fill(ad[ic*3+0],rs[ic*3+0]);
                if(good_vwt[ic]) hadca[1].fill(ad[ic*3+1],rs[ic*3+1]);
                if(good_wut[ic]) hadca[2].fill(ad[ic*3+2],rs[ic*3+2]);
            	for (int il=1 ; il<4 ; il++) 
            	{
            		namedir = String.format(laba[ic]+ "%02d", iteration);
            		hidd = hid+22*tid+ic*cid+il*lid;   
            		hadca[il-1] = (H2D) getDir().getDirectory(namedir).getObject("A"+hidd);
            	}
                if(good_uwtt[ic]) hadca[0].fill(ad[ic*3+0],rs[ic*3+0]);
                if(good_vwtt[ic]) hadca[1].fill(ad[ic*3+1],rs[ic*3+1]);
                if(good_wutt[ic]) hadca[2].fill(ad[ic*3+2],rs[ic*3+2]);
                
        // Histo: U vs V, U vs W, V vs W (used for detector map)
                
                double rs1=rs[ic*3+0] ; double rs2=rs[ic*3+1] ; double rs3=rs[ic*3+2];
                double ad1=ad[ic*3+0] ; double ad2=ad[ic*3+1] ; double ad3=ad[ic*3+2];
                double td1=td[ic*3+0] ; double td2=td[ic*3+1] ; double td3=td[ic*3+2];
                
                hidd = hid+40*tid+ic*cid;
                
                namedir = String.format(laba[ic]+ "%02d", iteration);
        		H2D hadc121 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+12*lid+1));
        		H2D hadc122 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+12*lid+2));
        		H2D hadc131 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+13*lid+1));
        		H2D hadc132 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+13*lid+2));
        		H2D hadc133 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+13*lid+3));
        		H2D hadc231 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+23*lid+1));
        		H2D hadc232 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+23*lid+2));
        		H2D hadc321 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+32*lid+1));
        		H2D hadc322 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+32*lid+2));
                      
                                
                for (int i=0 ; i<nh[iis-1][ic*3+0] ; i++) 
                {
                	double ris1=strr[iis-1][ic*3+0][i];
                	for (int k=0 ; k<nh[iis-1][ic*3+1] ; k++) 
                	{
                		double ris2=strr[iis-1][ic*3+1][k];
                		hadc121.fill(ris1,ris2,1.0);
                		hadc122.fill(ris1,ris2,ad1);
                	}
                	for (int k=0 ; k<nh[iis-1][ic*3+2] ; k++) 
                	{
                		double ris3=strr[iis-1][ic*3+2][k];
                		hadc131.fill(ris1,ris3,1.0);
                		hadc132.fill(ris1,ris3,ad1);
                		hadc133.fill(ris1,ris3,td3-td1);
                	}
                }
                
                for (int i=0 ; i<nh[iis-1][ic*3+1] ; i++) 
                {
                	double ris2=strr[iis-1][ic*3+1][i];
                	for (int k=0 ; k<nh[iis-1][ic*3+2] ; k++) 
                	{
                		double ris3=strr[iis-1][ic*3+2][k];
                		hadc231.fill(ris2,ris3,1.0);
                		hadc232.fill(ris2,ris3,ad2);
                		hadc321.fill(ris3,ris2,1.0);
                		hadc322.fill(ris3,ris2,ad3);                		
                	}
                }
                
                
        // Histo: Attenuation lengths from ADC vs strip (TAG=50)
                
                hidd=hid+50*tid+ic*cid;
                
                namedir = String.format(laba[ic]+ "%02d", iteration);
        		H2D hadc21 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+21*lid+rs1));
        		H2D hadc12 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+12*lid+rs2));
        		H2D hadc31 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+31*lid+rs1));
        		H2D hadc13 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+13*lid+rs3));
        		H2D hadc32 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+32*lid+rs2));
        		H2D hadc23 = (H2D) getDir().getDirectory(namedir).getObject("A"+(int)(hidd+23*lid+rs3));
               
                if (good_uv[ic]) 
                {
                	if(ad2>10) hadc21.fill(rs2,ad1); // U MIP VS V STRIP
                	if(ad1>10) hadc12.fill(rs1,ad2); // V MIP VS U STRIP
                }
                if (good_uw[ic]) 
                {
                	if(ad3>10) hadc31.fill(rs3,ad1); // U MIP VS W STRIP
                	if(ad1>10) hadc13.fill(rs1,ad3); // W MIP VS U STRIP
                }
                if (good_vw[ic]) 
                {
                	if(ad3>10) hadc32.fill(rs3,ad2); // V MIP VS W STRIP
                	if(ad2>10) hadc23.fill(rs2,ad3); // W MIP VS V STRIP
                }
                
        // Histo: Attenuation lengths from ADC vs strip (TAG=60)
                
                hidd=hid+60*tid+ic*cid;
                
                namedir = String.format(labt[ic]+ "%02d", iteration);
        		H2D htdc21 = (H2D) getDir().getDirectory(namedir).getObject("T"+(int)(hidd+21*lid+rs1));
        		H2D htdc12 = (H2D) getDir().getDirectory(namedir).getObject("T"+(int)(hidd+12*lid+rs2));
        		H2D htdc31 = (H2D) getDir().getDirectory(namedir).getObject("T"+(int)(hidd+31*lid+rs1));
        		H2D htdc13 = (H2D) getDir().getDirectory(namedir).getObject("T"+(int)(hidd+13*lid+rs3));
        		H2D htdc32 = (H2D) getDir().getDirectory(namedir).getObject("T"+(int)(hidd+32*lid+rs2));
        		H2D htdc23 = (H2D) getDir().getDirectory(namedir).getObject("T"+(int)(hidd+23*lid+rs3));
               
                if (good_uv[ic]) 
                {
                	if(ad2>5) htdc21.fill(rs2,td1-td2); // U MIP VS V STRIP
                	if(ad1>5) htdc12.fill(rs1,td2-td1); // V MIP VS U STRIP
                }
                if (good_uw[ic]) 
                {
                	if(ad3>5) htdc31.fill(rs3,td1-td3); // U MIP VS W STRIP
                	if(ad1>5) htdc13.fill(rs1,td3-td1); // W MIP VS U STRIP
                }
                if (good_vw[ic]) 
                {
                	if(ad3>5) htdc32.fill(rs3,td2-td3); // V MIP VS W STRIP
                	if(ad2>5) htdc23.fill(rs2,td3-td2); // W MIP VS V STRIP
                }                
                
      // Histo: Time Walk (Delta t vs ADC) (TAG=61)
                
                
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
    
    public void analyze() 
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
		
	
		//JFrame frame1 = new JFrame();
		//TCanvas u67can = new TCanvas("u67can", "u67can", 800, 600, 1, 1);
		//GraphErrors attengraph;
		//attengraph = graphn("attenplot_u67",counter,x,centroids,ex,ey);
	
		//create function and fit
		//myfunc2 = new F1D("exp",0.0,500.0); //"mycustomfunc",
		//myfunc2.parameter(0).setValue(100.0);
		//myfunc2.setParLimits(0, 0.0, 200.0);
		//myfunc2.parameter(1).setValue(-0.002659574468);
		//myfunc2.setParLimits(1, 0.0, 105.0);
		//attengraph.fit(myfunc2);
		
		//System.out.println(myfunc2.getParameter(0));
		//System.out.println(myfunc2.getParameter(1));
		//myfunc2.getParameter(0);
		//myfunc2.getParameter(1);
		
		
		//u67can.draw(attengraph);
		//u67can.draw(myfunc2, "same");
		//frame1.add(u67can);
		//frame1.setVisible(true);
		//frame1.export("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/testattenALLu.png");
		//u67can.save("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/testattenu67.png");
		
		//.saveAs("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/testattenu67.eps");
		//.save("/home/ncompton/Work/workspace/Calibration/src/org/jlab/calib/testattenALLu.png");
		
		
		getDir().addDirectory(fitdir);

		
	}
	
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
		
		return mygraph;
	}
	
	//xdistance[i] needs to be 1-62 or 1-68 not 0
	public void CalcDistance(double xdistance[], double xdistanceE[], int numpoints, char stripletter)
	{
	    int i;
	    double distperstrip = 5.055;
	    
	    if(stripletter == 'u' || stripletter == 'U')
	    {
	        //convert strip number to distance
	        for(i = 0; i < numpoints; ++i)
	        {
	            xdistance[i] = Math.abs(xdistance[i] - 77.0) * distperstrip;
	            xdistanceE[i] = xdistanceE[i] * distperstrip;
	        }
	    }
	    else if(stripletter == 'v' || stripletter == 'w' || stripletter == 'V' || stripletter == 'W')
	    {
	        //convert strip number to distance
	        for(i = 0; i < numpoints; ++i)
	        {
	            xdistance[i] = Math.abs(xdistance[i] - 84.0) * distperstrip;
	            xdistanceE[i] = xdistanceE[i] * distperstrip;
	        }
	    }

	}
	
	//crossstrip needs to be 1-62 or 1-68 not 0
	void CalcDistinStrips(char stripletter, int crossstrip, double x[], double xE[], int pointnum)
	{
	    if(stripletter == 'u' || stripletter == 'U')
	    {
	        if(crossstrip <= 15)
	        {
	            //converts to 77 strips
	            x[pointnum] = 2.0* crossstrip - 1.0;
	            xE[pointnum] = 1.0;
	        }
	        else if(crossstrip > 15)
	        {
	            //converts to 77 strips
	            x[pointnum] = (30.0 + (crossstrip - 15.0)) - 0.5;
	            xE[pointnum] = 1.0/2.0;
	        }
	    }
	    else if(stripletter == 'v' || stripletter == 'w' || stripletter == 'V' || stripletter == 'W')
	    {
	        if(crossstrip <= 52)
	        {
	            //converts to 84 strips
	            x[pointnum] = crossstrip - 0.5;
	            xE[pointnum] = 1.0/2.0;
	            }
	            else if(crossstrip > 52)
	            {
	                //converts to 84 strips
	                x[pointnum] = (52.0 + 2.0*(crossstrip - 52.0)) - 1.0;
	                xE[pointnum] = 1.0;
	            }
	    }
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
		
		//Histogram ID convention
		//hid=is*1e7 + tag*1e5 + ic*1e4 + uv*1e2 + pmt
		//is=1-6 tag=0-99 ic=0-2 uv=12,13,23 pmt=1-68
		//                    
		int    nbn1[] = {68,36,36}; //PCAL, EC inner, EC outer
		double nbn2[] = {69.0,37.0,37.0}; //PCAL, EC inner, EC outer
		String name;
		
		int hid;
		int tid       = 100000; //Tag number
		int cid       = 10000; //EC inner, EC outer, pcal
		int lid       = 100; //u,v,w
		int is        = CurrentSector; //sector number
		int iss       = (int) (is*1e7); //sector number scale

	    TDirectory calADC[] = new TDirectory[3]; //creates adc directory for each cid
	    TDirectory calTDC[] = new TDirectory[3]; //creates tdc directory for each cid
	    
    	for (int ic=0 ; ic<3 ; ic++)
    	{  
    		//ic=0,1,2 -> PCAL,ECALinner,ECALouter
    		
    		name = String.format(laba[ic]+ "%02d", iteration);
    		calADC[ic] = new TDirectory(name);
    		name = String.format(labt[ic]+"%02d", iteration);
    		calTDC[ic] = new TDirectory(name);
 
    		hid=iss+11*tid+ic*cid; //Dalitz test
    		calADC[ic].add(new H1D("A"+hid,50,0.,3.0));
    		
    		hid=iss+50*tid+ic*cid; //Light attenuation vs crossing strip
    		for (int ip=1 ; ip<nbn1[ic]+1 ; ip++) 
    		{    		 
    			calADC[ic].add(new H2D("A"+(int)(hid+21*lid+ip),nbn1[ic],1.0,nbn2[ic],30,0.0,200.0));
    			calADC[ic].add(new H2D("A"+(int)(hid+12*lid+ip),nbn1[ic],1.0,nbn2[ic],30,0.0,200.0));     
    			calADC[ic].add(new H2D("A"+(int)(hid+31*lid+ip),nbn1[ic],1.0,nbn2[ic],30,0.0,200.0));    	 
    			calADC[ic].add(new H2D("A"+(int)(hid+13*lid+ip),nbn1[ic],1.0,nbn2[ic],30,0.0,200.0));    	 
    			calADC[ic].add(new H2D("A"+(int)(hid+32*lid+ip),nbn1[ic],1.0,nbn2[ic],30,0.0,200.0));    		 
    			calADC[ic].add(new H2D("A"+(int)(hid+23*lid+ip),nbn1[ic],1.0,nbn2[ic],30,0.0,200.0));	
    		}
    		
    		if(ic == 0) //PCAL
    		{
    			calADC[ic].add(new H1D("u67w60adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w55adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w50adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w45adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w40adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w35adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w30adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w25adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w20adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w15adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w10adu",75,0.0,400.0));
    			calADC[ic].add(new H1D("u67w5adu",75,0.0,400.0));
    		}
    		
    		hid=iss+60*tid+ic*cid; //Time Difference vs crossing strip
    		for (int ip=1 ; ip<nbn1[ic]+1 ; ip++) 
    		{    		 
    			calTDC[ic].add(new H2D("T"+(int)(hid+21*lid+ip),nbn1[ic],1.0,nbn2[ic],80,-40.0,40.0));
    			calTDC[ic].add(new H2D("T"+(int)(hid+12*lid+ip),nbn1[ic],1.0,nbn2[ic],80,-40.0,40.0));     
    			calTDC[ic].add(new H2D("T"+(int)(hid+31*lid+ip),nbn1[ic],1.0,nbn2[ic],80,-40.0,40.0));    	 
    			calTDC[ic].add(new H2D("T"+(int)(hid+13*lid+ip),nbn1[ic],1.0,nbn2[ic],80,-40.0,40.0));    	 
    			calTDC[ic].add(new H2D("T"+(int)(hid+32*lid+ip),nbn1[ic],1.0,nbn2[ic],80,-40.0,40.0));    		 
    			calTDC[ic].add(new H2D("T"+(int)(hid+23*lid+ip),nbn1[ic],1.0,nbn2[ic],80,-40.0,40.0));	
    		}
    		hid=iss+40*tid+ic*cid; //Detector map
    		for (int il=1 ; il<4 ; il++) 
    		{    	 
    			calADC[ic].add(new H2D("A"+(int)(hid+12*lid+il),nbn1[ic],1.0,nbn2[ic],nbn1[ic],1.0,nbn2[ic]));     
    			calADC[ic].add(new H2D("A"+(int)(hid+13*lid+il),nbn1[ic],1.0,nbn2[ic],nbn1[ic],1.0,nbn2[ic]));    	 
    			calADC[ic].add(new H2D("A"+(int)(hid+23*lid+il),nbn1[ic],1.0,nbn2[ic],nbn1[ic],1.0,nbn2[ic]));    		 
    			calADC[ic].add(new H2D("A"+(int)(hid+32*lid+il),nbn1[ic],1.0,nbn2[ic],nbn1[ic],1.0,nbn2[ic]));
    		}
    		hid=iss+ic*cid; //strip versus FADC MIP
    		for (int il=1 ; il<4 ; il++) 
    		{
    			calADC[ic].add(new H2D("A"+(int)(hid+10*tid+il*lid),50,0.0,200.0,nbn1[ic],1.0,nbn2[ic])); 
    			calTDC[ic].add(new H2D("T"+(int)(hid+10*tid+il*lid),70,1330.0,1420.0,nbn1[ic],1.0,nbn2[ic]));     		 
    			calADC[ic].add(new H2D("A"+(int)(hid+15*tid+il*lid),50,0.0,200.0,nbn1[ic],1.0,nbn2[ic]));     		 
    			calADC[ic].add(new H2D("A"+(int)(hid+20*tid+il*lid),50,0.0,200.0,nbn1[ic],1.0,nbn2[ic]));     		 
    			calADC[ic].add(new H2D("A"+(int)(hid+21*tid+il*lid),50,0.0,200.0,nbn1[ic],1.0,nbn2[ic]));     		 
    			calADC[ic].add(new H2D("A"+(int)(hid+22*tid+il*lid),50,0.0,200.0,nbn1[ic],1.0,nbn2[ic])); 
    		}
    		getDir().addDirectory(calADC[ic]);
    		getDir().addDirectory(calTDC[ic]);
    	}
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
