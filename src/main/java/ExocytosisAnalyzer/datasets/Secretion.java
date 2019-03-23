/**
 * 
 */
package ExocytosisAnalyzer.datasets;

import java.util.Vector;

import ij.ImageStack;
import ij.measure.CurveFitter;

import ij.process.ImageProcessor;


/**
 * @author ABC
 *
 */
public class Secretion {
	
	public int ref_ID;	
	public ImageStack film;
	public int start_slice , start_x, start_y, fin_slice, fin_x, fin_y, peakTime, peakX, peakY;
	
	public Vector<Vesicle> secretion_event = new Vector<Vesicle>(); 
		
	public double frame_rate;
	public boolean isValid = true;
	public boolean isImmobile = true;

	public double Decay_tau, Docking_tau, Decay_R2, Docking_R2;
	public double Decay_bk, Docking_bk, Decay_peak, Docking_peak;


	public Secretion() {
		
	}
	
	
	public Secretion(Vector<Vesicle> aEvent) {
		secretion_event = aEvent;
		int num_of_objects = aEvent.size();
		film = new ImageStack(aEvent.elementAt(0).vesicleImage.getWidth(),
							  aEvent.elementAt(0).vesicleImage.getHeight()
							  );	
		for (int i = 0; i < num_of_objects; i++ ) {
			film.addSlice(aEvent.elementAt(i).vesicleImage);
		}
		start_slice = aEvent.firstElement().slice;
		fin_slice = aEvent.lastElement().slice;
    	start_x = aEvent.firstElement().x;
    	start_y = aEvent.firstElement().y;
    	fin_x = aEvent.lastElement().x;
    	fin_y = aEvent.lastElement().y;
		
	}
    public void setRef(int aID) {
    	ref_ID =  aID;
    }
    public int getRef() {
    	return ref_ID;
    }
    
    public void addVesicle(Vesicle aVesicle) {
    	secretion_event.addElement(aVesicle);
    	film.addSlice(aVesicle.vesicleImage);
    	fin_slice = aVesicle.slice;
    	fin_x = aVesicle.x;
    	fin_y = aVesicle.y;
    }
    
    public void addVesicleAt1st(Vesicle aVesicle) {
    	secretion_event.add(0, aVesicle);
    	film.addSlice(null, aVesicle.vesicleImage, 0);
    	start_slice = aVesicle.slice;
    	start_x = aVesicle.x;
    	start_y = aVesicle.y;
    }
    
    public int getDuration() {
    	return secretion_event.size();
    }
    
    public int[] getXcorr() {
    	int[] x_corr = new int[secretion_event.size()];   
        
    	for ( int i = 0 ; i < secretion_event.size() ; i++ ) {
    		x_corr[i] = secretion_event.elementAt(i).x;
    	}
    	return x_corr;
    }
    
    public int[] getYcorr() {
    	int[] y_corr = new int[secretion_event.size()];   
        
    	for ( int i = 0 ; i < secretion_event.size() ; i++ ) {
    		y_corr[i] = secretion_event.elementAt(i).y;
    	}
    	return y_corr;
    }
    
    public double[] getCurve() {
    	double[] peak = new double[secretion_event.size()];   
        
    	for ( int i = 0 ; i < secretion_event.size(); i++ ) {
    		peak[i] = secretion_event.elementAt(i).max_Den;
    	}
    	return peak;
    }
    public Vector<double[]> getRingSimplingCurve() {
    	Vector<double[]> RingSimplingCurve = new Vector<double[]>();   

    	int spot_radius =secretion_event.elementAt(0).radius;
    	boolean[] mask = new boolean[(4*spot_radius+1) * (4*spot_radius+1)]; 
    	int index;
    	
    	for (int ring_radius = 0 ; ring_radius < 2*spot_radius; ring_radius++) {
        	double[] xdata = new double[secretion_event.size()];	
    		for (int i = -ring_radius; i <= ring_radius; i++) {
		        for (int j = -ring_radius; j <= ring_radius; j++) { 	
		            index = (i + 2*spot_radius) * (4*spot_radius+1) + (j + 2*spot_radius) ;
					if (   (  (i * i + j * j)  >= (ring_radius * ring_radius) ) && ( (i * i + j * j) < (ring_radius+1) * (ring_radius+1) )   ) {
		                mask[index] = true;
		            }
		            else {
		                mask[index] = false;
		            }
		        }
		    }
    		
    		for (int i = 0; i < xdata.length ; i++) {
    			final short[] pixels = (short[]) film.getProcessor(i+1).duplicate().getPixels();
    			xdata[i] = 0;
    			int n = 0;
	    		for (int j = 0; j < mask.length ; j++) {	    			
	    			if(mask[j]) {
	    				xdata[i] = xdata[i] + pixels[j];
	    				n++;
	    			}
	    		}
	    		xdata[i] = xdata[i]/n;
    		}		
    		RingSimplingCurve.addElement(xdata);
    	}
    	return RingSimplingCurve;
    }
    public double[] getTimeCorr() {
    	double[] times = new double[secretion_event.size()];   
        
    	for ( int i = 0 ; i < secretion_event.size(); i++ ) {
    		times[i] = secretion_event.elementAt(i).slice;
    	}
    	return times;
    }
    
    public void Fit(int min_points_num) {
    
    	double[] xData = this.getTimeCorr();
    	double[] yData = this.getCurve();
    	
    	double peak = yData[0];
    	double baseline = yData[0];
    	int peakposition = (int) xData[0];
    	
    	int position = 0;
    	for(int i = 0 ; i < yData.length ; i++){
    		if(yData[i] > peak) {
    			peak = yData[i];
    			position = i;
    			peakposition = (int) xData[i];
    		}
    		if (yData[i] < baseline) {
    			baseline = yData[i];
    		}
    	}
    	if (xData.length - position  >= min_points_num && position >= min_points_num) {  // minimal points necessary for fitting, position : peak position
    	
    		double[] xData_increase = new double[position +1];
    		double[] yData_increase = new double[position +1];
    		double[] xData_decrease = new double[yData.length - position ];
    		double[] yData_decrease = new double[yData.length - position ];
    	
    		for(int i = 0 ; i <= position ; i++){ // reverse array
    			xData_increase[i]=i;
    			yData_increase[i]=yData[position  - i];
    		}
    		for(int i = 0 ; i < yData.length - position; i++){   
    			xData_decrease[i]=i;
    			yData_decrease[i]=yData[i + position];
    		}

    		String decay = "y = a + b*exp(-(x/c))";
    		double[] initilParas = { baseline, (peak-baseline),1 };
    	
    	
    		CurveFitter dockingFitter = new CurveFitter(xData_increase, yData_increase);
    		dockingFitter.doCustomFit(decay, initilParas, false);
    		double[] dockingParas = dockingFitter.getParams();
    	
    	
    		CurveFitter releaseFitter = new CurveFitter(xData_decrease, yData_decrease);
    		releaseFitter.doCustomFit(decay, initilParas, false);
    		double[] releaseParas = releaseFitter.getParams();
    	
    		Docking_tau = dockingParas[2];
    		Decay_tau = releaseParas[2];
    		Decay_bk = releaseParas[0];
    		Decay_peak = releaseParas[1];
    		Decay_R2 = releaseFitter.getRSquared();
    		peakTime = peakposition;
    		peakX = secretion_event.elementAt(position).x;
    		peakY = secretion_event.elementAt(position).y;
    	}
    	else this.Decay_R2 = 0;
    }
    public double getVesicleSize() {
    	return secretion_event.elementAt(peakTime - start_slice).EstimateSize();
    }

	public double getMax() {
		double[] Curve = this.getCurve();
		double max_value = Curve[0];

	    for(int i=1;i < Curve.length;i++){
	    	if(Curve[i] > max_value){
	    		max_value = Curve[i];
	    	}

	    } 
	    return max_value;
	}
	public double getMin() {
		double[] Curve = this.getCurve();
		double min_value = Curve[0];
	    for(int i=1;i < Curve.length;i++){
	    	if(Curve[i] < min_value){
	    		min_value = Curve[i]; 
	    	}
	    } 
	    return min_value;
	}
	public Secretion clone() {
		
		final int spot_radius = this.secretion_event.elementAt(0).getRadius();
		final int ref = this.ref_ID;
		Vector<Vesicle> aEvent = new Vector<Vesicle>();
		for (int index = 0; index< this.secretion_event.size(); index++) {
			int vref = this.secretion_event.elementAt(index).getRef();
			int slice = this.secretion_event.elementAt(index).getSlice();
			int x = this.secretion_event.elementAt(index).getx();
			int y = this.secretion_event.elementAt(index).gety();
			ImageProcessor ip = this.secretion_event.elementAt(index).getImage().duplicate();
			Vesicle aVesicle = new Vesicle(x,y,slice);
			aVesicle.setImage(ip);
			aVesicle.setRadius(spot_radius);
			aVesicle.setRef(vref);
			aEvent.addElement(aVesicle);
		}
		Secretion aSecretion = new Secretion(aEvent);
		aSecretion.setRef(ref);
		return aSecretion;
	}


}
