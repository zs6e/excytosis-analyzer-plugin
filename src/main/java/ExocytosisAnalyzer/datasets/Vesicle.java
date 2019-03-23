package ExocytosisAnalyzer.datasets;

import ij.ImageStack;
import ij.measure.CurveFitter;
import ij.process.ImageProcessor;

public class Vesicle {
	
	public int ref_ID;

    public int x, y, slice;
    public int max_Den, min_Den, int_Den;
    public int radius;
    public ImageProcessor vesicleImage; 
    public boolean isValid = true;
    public boolean isLinked = false;
    
    /**
     * @param x -  x coordinates
     * @param y -  y coordinates
     * @param slice - the number of the slice this vesicle belongs to
     * @param max_Den -  maximal pixel density
     * @param int_Den -  integrated pixel density
     */
    public Vesicle() {

    }
    
    public Vesicle(int aX, int aY, int aSlice) {
        x = aX;
        y = aY;
        slice = aSlice;
    }

    
    public Vesicle(int aX, int aY, int aSlice, int aRadius, ImageStack is) {
        x = aX;
        y = aY;
        slice = aSlice;
        radius = aRadius;
        ImageProcessor slice_of_corrent_vesicle = is.getProcessor(slice).duplicate();
		int roi_x = x - 2*radius;
		int roi_y = y - 2*radius;
		if (roi_x < 0)  roi_x = 0;
		if (roi_y < 0)  roi_y = 0;
		
		int roi_w = 4*radius+1;
		int roi_h = 4*radius+1;
		if ((roi_x + roi_w) > slice_of_corrent_vesicle.getWidth()) {
			roi_x = slice_of_corrent_vesicle.getWidth() - roi_w;
		}
		if ((roi_y + roi_h) > slice_of_corrent_vesicle.getHeight()) {
			roi_y = slice_of_corrent_vesicle.getHeight() - roi_h;
		}
		
		slice_of_corrent_vesicle.setRoi(roi_x, roi_y, roi_w, roi_h);
		
		vesicleImage = slice_of_corrent_vesicle.crop();
		float[] pixels = (float[]) vesicleImage.duplicate().convertToFloatProcessor().getPixels();
        float max_value = pixels[0];
        float min_value = pixels[0];
	    for(int i=1;i < pixels.length;i++){
	    	if(pixels[i] > max_value){
	    		max_value = pixels[i];
	    	}
	    	if(pixels[i] < min_value){
	    		min_value = pixels[i]; 
	    	}
	    } 
		max_Den = (int)max_value; 
		min_Den = (int)min_value;
    }
    
    public boolean isDuplicata(Vesicle aVesicle, int aRadius) {
    	
    	if ( Math.abs( x - aVesicle.x) <= aRadius &&
    		 Math.abs( y - aVesicle.y) <= aRadius &&
    		 slice == aVesicle.slice) {
    		return true;
    	}
    	else {
    		return false;
    	}			
    }
    
    public boolean isNext(Vesicle aVesicle, int aRadius) {
    	
    	if ( Math.abs( x - aVesicle.x) <= aRadius &&
    		 Math.abs( y - aVesicle.y) <= aRadius &&
    		 slice == (aVesicle.slice + 1) ) {
    		return true;
    	}
    	else {
    		return false;
    	}			
    }
    
    public boolean isPrior(Vesicle aVesicle, int aRadius) {
    	
    	if ( Math.abs( x - aVesicle.x) <= aRadius &&
    		 Math.abs( y - aVesicle.y) <= aRadius &&
    		 slice == (aVesicle.slice - 1) ) {
    		return true;
    	}
    	else {
    		return false;
    	}			
    }

   
    @Override
    public String toString() {
        return toStringBuffer().toString();
    }

  
    public StringBuffer toStringBuffer() {
        final StringBuffer result = new StringBuffer();
        result.append(slice + "	" + x + "	" + y + "\n");
        return result;
    }

 
    public void setSlice(int aSlice) {
        this.slice = aSlice;
    }
    public int getSlice() {
        return slice;
    }

    public void setx(int aX) {
        x = aX;
    }
    public int getx() {
        return x;
    }

    public void sety(int aY) {
        y =  aY;
    }
    public int gety() {
        return y;
    }

    
    public void setImage(ImageProcessor aImageProcessor) {
    	vesicleImage =  aImageProcessor;
		float[] pixels = (float[]) vesicleImage.duplicate().convertToFloatProcessor().getPixels();
        float max_value = pixels[0];
        float min_value = pixels[0];
	    for(int i=1;i < pixels.length;i++){
	    	if(pixels[i] > max_value){
	    		max_value = pixels[i];
	    	}
	    	if(pixels[i] < min_value){
	    		min_value = pixels[i]; 
	    	}
	    } 
		max_Den = (int)max_value; 
		min_Den = (int)min_value;
    }
    
    public void setImage(ImageStack is) {
        ImageProcessor slice_of_corrent_vesicle = is.getProcessor(slice);
		int roi_x = x - 2*radius;
		int roi_y = y - 2*radius;
		if (roi_x < 0)  roi_x = 0;
		if (roi_y < 0)  roi_y = 0;
		
		int roi_w = 4*radius+1;
		int roi_h = 4*radius+1;
		if ((roi_x + roi_w) > slice_of_corrent_vesicle.getWidth()) {
			roi_x = slice_of_corrent_vesicle.getWidth() - roi_w;
		}
		if ((roi_y + roi_h) > slice_of_corrent_vesicle.getHeight()) {
			roi_y = slice_of_corrent_vesicle.getHeight() - roi_h;
		}
		
		slice_of_corrent_vesicle.setRoi(roi_x, roi_y, roi_w, roi_h);
		vesicleImage = slice_of_corrent_vesicle.crop().duplicate();
		float[] pixels = (float[]) vesicleImage.duplicate().convertToFloatProcessor().getPixels();
        float max_value = pixels[0];
        float min_value = pixels[0];
	    for(int i=1;i < pixels.length;i++){
	    	if(pixels[i] > max_value){
	    		max_value = pixels[i];
	    	}
	    	if(pixels[i] < min_value){
	    		min_value = pixels[i]; 
	    	}
	    } 
		max_Den = (int)max_value; 
		min_Den = (int)min_value;
    }
    
    public ImageProcessor getImage() {
        return vesicleImage;
    }
    public void setIntDen(int aInt_Den) {
    	int_Den =  aInt_Den;
    }
    public int getIntDen() {
        return int_Den;
    }
    public void setMaxDen(int aMax_Den) {
    	max_Den =  aMax_Den;
    }
    public double getMaxDen() {
        return max_Den;
    }
    public void setMinDen(int aMin_Den) {
    	min_Den =  aMin_Den;
    }
    public double getMinDen() {
        return min_Den;
    }
    public void setRadius(int aRadius) {
    	radius =  aRadius;
    }
    public int getRadius() {
        return radius;
    }
    public void setRef(int aID) {
    	ref_ID =  aID;
    }
    public int getRef() {
    	return ref_ID;
    }
    
    public double EstimateSize() {
    	double[] ParametresH = this.getHorizontalGaussFitter();
    	double[] ParametresV =  this.getVerticalGaussFitter();
    	return (ParametresH[3]+ParametresV[3])/2;
    }
    public double[] getVerticalProfile() {
    	double[] profile_v;
    	profile_v = vesicleImage.getLine(2*radius,0, 2*radius, 4*radius);
    	return profile_v;
    }
    public double[] getHorizontalProfile() {
    	double[] profile_h;
    	profile_h = vesicleImage.getLine(0, 2*radius, 4*radius, 2*radius);
    	return profile_h;
    }
    public double[] getVerticalGaussFitter() {
    	double[] profile_v = this.getVerticalProfile();
    	double[] xdata = new double[4*radius+1];
    	double j = -2*radius;
    	for (int i=0; i<xdata.length; i++) {
    		xdata[i]= j;
    		j++;
    	}
    	double[] initialParams = {min_Den, max_Den, 0, radius};
    	CurveFitter gaussianfitterV = new CurveFitter(xdata,profile_v);
    	gaussianfitterV.setInitialParameters(initialParams);
    	gaussianfitterV.doFit(CurveFitter.GAUSSIAN);
    	double[] ParametresV =  gaussianfitterV.getParams();
    	return ParametresV;

    }
    public double[] getHorizontalGaussFitter() {
    	double[] profile_h = this.getHorizontalProfile();
    	double[] xdata = new double[4*radius+1];
    	double j = -2*radius;
    	for (int i=0; i<xdata.length; i++) {
    		xdata[i]= j;
    		j++;
    	}
    	double[] initialParams = {min_Den, max_Den, 0, radius};
    	CurveFitter gaussianfitterH = new CurveFitter(xdata,profile_h);
    	gaussianfitterH.setInitialParameters(initialParams);
    	gaussianfitterH.doFit(CurveFitter.GAUSSIAN);
    	double[] ParametresH =  gaussianfitterH.getParams();
    	return ParametresH;
    }
}