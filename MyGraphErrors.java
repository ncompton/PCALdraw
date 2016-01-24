package org.jlab.calib;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.root.func.F1D;
import org.root.histogram.GraphErrors;



/**
 *
 * @author gavalian
 */
public class MyGraphErrors extends GraphErrors {
  
    
    public MyGraphErrors(){
        super();    
        this.setName("MyGraphErrors");  
    } 
    
    public MyGraphErrors(double[] x, double[] y){
        super(x,y);   
    } 
    
    public MyGraphErrors(String name,double[] x, double[] y){
        super(name,x,y);   
    } 
    
    
    public MyGraphErrors(double[] x, double[] y, double[] ex, double[] ey){
        super(x,y,ex,ey);
        this.setName("MyGraphErrors");        
    } 
    
    public MyGraphErrors(String name, double[] x, double[] y, double[] ex, double[] ey){
        super(x,y,ex,ey);
        setName(name);
    }

    public void fit(F1D func, String options){
        if(options.contains("R")==false){
            this.fit(func);
        }
        else
        {
            double[] a = new double[this.getDataSize()];
            double[] b = new double[this.getDataSize()];
            double[] ae = new double[this.getDataSize()];
            double[] be = new double[this.getDataSize()];
            int counter = 0;
            for(int loop = 0; loop < this.getDataSize(); loop++){
                if(this.getDataX(loop) > func.getMin() && this.getDataX(loop) < func.getMax())
                {
                    a[counter] = this.getDataX(loop);
                    b[counter] = this.getDataY(loop);
                    ae[counter] = this.getErrorX(loop);
                    be[counter] = this.getErrorX(loop);
                    ++counter;
                }
            }
            //technically unneccessary loop if all elements of a/b are 0
            //but to be safe, since there isn't a graph constructor with a count
            double[] x = new double[counter];
            double[] y = new double[counter];
            double[] xe = new double[counter];
            double[] ye = new double[counter];
            for(int loop = 0; loop < counter; loop++)
            {
                x[loop] = a[loop];
                y[loop] = b[loop];
                xe[loop] = ae[loop];
                ye[loop] = be[loop];
            }
            MyGraphErrors g1 = new MyGraphErrors(x,y,xe,ye);
            g1.fit(func);
        }
    }
     

}

