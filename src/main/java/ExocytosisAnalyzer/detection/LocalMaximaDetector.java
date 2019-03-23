package ExocytosisAnalyzer.detection;


import ij.ImageStack;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.ProgressMonitor;

import ExocytosisAnalyzer.datasets.Secretion;
import ExocytosisAnalyzer.datasets.Vesicle;
import ExocytosisAnalyzer.datasets.VesicleParametres;

public class LocalMaximaDetector {
	public ImageStack is;
	public ImageStack lowPassStack;
	private final int image_width;
    private final int image_height;
    private final int kernel_width;
    private final int num_of_slices;
    private final int spot_radius;
	private final double threshold_percent;
	private boolean useWavelet;
	private boolean showlowpass;
	private boolean[] scaleList;
	private final int sensitivety;
	
	


	private final boolean mask[];
	private ImageStack normalized_float_is;
	private Vector<Vesicle> local_maxima = new Vector<Vesicle>();
	private Vector<Vesicle> detected_vesicle = new Vector<Vesicle>();

    

	
	public LocalMaximaDetector(ImageStack input, VesicleParametres v_paras){
		is = input;
		image_width = is.getWidth();
		image_height = is.getHeight();
		num_of_slices = is.getSize();

		spot_radius = v_paras.Radius;
		threshold_percent = v_paras.Percent;
		kernel_width = spot_radius * 2 + 1;
		normalized_float_is = new ImageStack(image_width,image_height);
		useWavelet = v_paras.useWavelet;
		scaleList = v_paras.scaleList;
		sensitivety = v_paras.sensitivity;
		showlowpass = v_paras.showLowpass;
		mask = new boolean[kernel_width*kernel_width];
	
		int index = 0;
 	    for (int i = -spot_radius; i <= spot_radius; i++) {
	        for (int j = -spot_radius; j <= spot_radius; j++) {
	            index = (i + spot_radius) * kernel_width + (j + spot_radius);
	            if ((i * i) + (j * j)  <= spot_radius * spot_radius) {
	                mask[index] = true;
	            }
	            else {
	                mask[index] = false;
	            }
	        }
	    }
	    
	}
	
	
	public Vector<Vesicle> Detection() {
	
 	
		//Progress monitor
		float counter = 1;
		ProgressWin pm = new ProgressWin();
		pm.setNote("Detecting vesicules ....");
		if (useWavelet) {
			if (showlowpass) {
				lowPassStack = new ImageStack(image_width,image_height);
				for (int slice = 1; slice <= num_of_slices; slice++){
					MicroWaveletDetector waveletDetector = new MicroWaveletDetector(is.getProcessor(slice).duplicate(),scaleList,sensitivety);
					float[] lowPass = waveletDetector.filter();
					FindLocalMaxima(lowPass, slice, 1);
					FloatProcessor LP = new FloatProcessor (image_width,image_height,lowPass);
					lowPassStack.addSlice(LP);
					pm.setProgress((int) ((counter/num_of_slices)*100));
					counter++;
				}
			}
			else {
				for (int slice = 1; slice <= num_of_slices; slice++){
					MicroWaveletDetector waveletDetector = new MicroWaveletDetector(is.getProcessor(slice).duplicate(),scaleList,sensitivety);
					float[] lowPass = waveletDetector.filter();
					FindLocalMaxima(lowPass, slice, 1);
					pm.setProgress((int) ((counter/num_of_slices)*100));
					counter++;
				}
			}
		}
		else {
			float[] normalized_float_img;
			for (int slice = 1; slice <= num_of_slices; slice++){
				normalized_float_img = getNormalizedImage(is.getProcessor(slice).duplicate());
				normalized_float_is.addSlice(null, normalized_float_img);
				final float threshold = CalculateThreshold(normalized_float_img);
				FindLocalMaxima(normalized_float_img, slice, threshold);
				pm.setProgress((int) ((counter/num_of_slices)*100));
				counter++;
			}
		}
			

		pm.close();
		RemoveDuplicata();
		
		int vNum = local_maxima.size();
    	//Progress monitor


		for (int i = 0; i < vNum; i++) {
			if (local_maxima.elementAt(i).isValid) {
				Vesicle new_vesicle = new Vesicle(local_maxima.elementAt(i).x, 
						                  local_maxima.elementAt(i).y, 
						                  local_maxima.elementAt(i).slice, 
						                  spot_radius, 
						                  is); 
				new_vesicle.setRef(i);
				detected_vesicle.addElement(new_vesicle);
			}

			counter++;
		}

	return detected_vesicle;
	}
	
	
	private float CalculateThreshold(float[] img) {
		
		float[] pixels = img;
	    float[] hist = new float[1000];
	    Arrays.fill(hist, 0);
	    float result = 0.0f;
	    float max_value = pixels[0];
	    float min_value = pixels[0];
	     
	    for (int i = 0; i < pixels.length; i++) {
	            
	        hist[(int) (pixels[i] * 999 )]++;
            max_value = Math.max(pixels[i],max_value);
	        min_value = Math.min(pixels[i],max_value);
	    }
	    
	   	for (int i = 998; i >= 0; i--) {
	     		hist[i] += hist[i + 1];
	   	}
	    
  
	   	for (int i = 999;i > 0; i--) {
	        
	   		if (hist[i] / hist[0] >= (threshold_percent/100)) {
	   			result = i;
	       		break;
	       	}
	    }
	    		    	

	    return ((float) (result / 1000) * (max_value - min_value) + min_value);
	}
   
	private void FindLocalMaxima(float[] img , int slice, float threshold) {
              
        final float[] dilated_img = getDilatedImage(img);
        for (int y = 0; y < image_height; y++) {
            for (int x = 0; x < image_width; x++) {
                // check if pixel is a local maximum
                if (img[y * image_width + x] > threshold && img[y * image_width + x] == dilated_img[y * image_width + x]) {     	
                    Vesicle current_local_maxima = new Vesicle(x, y, slice);
                	local_maxima.addElement(current_local_maxima);
                }
            	               
            }
            
        }
	}

    public float[] getNormalizedImage(ImageProcessor ip) {
      	   	
        // Convert to float and normalizing (0-1) in all frames
        FloatProcessor fp = new FloatProcessor(image_width, image_height);
	    float[] normalized_float_image = new float[image_width*image_height];
	   
	    fp = ip.duplicate().convertToFloatProcessor();
	    float[] pixels = (float[]) fp.getPixels();
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
	    	
        // Normalization
       	fp.add(-min_value);
        fp.multiply(1/(max_value-min_value));

        normalized_float_image = (float[]) fp.getPixels();	    		
	             
	    return normalized_float_image;  
    }
    public float[] getNormalizedImage(float[] pixels) {
  	   	
        // Convert to float and normalizing (0-1) in all frames
	    float[] normalized_float_image = new float[image_width*image_height];
	   


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
	    	
	    for(int i=1;i < pixels.length;i++){
	    	normalized_float_image[i]=(pixels[i]-min_value)/(max_value-min_value);
	    }
              
	    return normalized_float_image;  
    }
    
	public float[] getDilatedImage(float[] img) {

        float max;
        float[] output = new float[image_width * image_height];
      	for (int y = 0; y < image_height; y++) {
           	for (int x = 0; x < image_width; x++) {
           		if (img[y*image_width + x] == 0) continue;
                max = 0.0f;
                // i,j are the kernel coordinates corresponding to x,y
                 for (int j = -spot_radius; j <= spot_radius; j++) {
                    if (y + j < 0 || y + j >= image_height) continue;
                    for (int i = -spot_radius; i <= spot_radius; i++) {
                        if (x + i < 0 || x + i >= image_width) continue;
                        if (mask[(j + spot_radius) * kernel_width + (i + spot_radius)]) {
                            max = Math.max(max, img[(y + j) * image_width + (x + i)]);
                        }
                    }
                }
                output[y * image_width + x] = max;
            }
        }
        return output;
    }

    private void RemoveDuplicata() {
    	
    	final int neighborhood_radius = spot_radius;
        Vesicle current_vesicle;
        Vesicle next_vesicle; 
        for (int i = 0; i < local_maxima.size(); i++) {
        	if (!local_maxima.elementAt(i).isValid) continue;
        	current_vesicle = local_maxima.elementAt(i);     	
        	for (int j = i + 1; j < local_maxima.size(); j++) {
        		if (local_maxima.elementAt(j).slice != current_vesicle.slice) continue;
            	if (!local_maxima.elementAt(j).isValid) continue;
        		next_vesicle = local_maxima.elementAt(j);
        		if (current_vesicle.isDuplicata(next_vesicle, neighborhood_radius)) {
        			local_maxima.elementAt(j).isValid = false;
        		}
        	}
        }
    }
    public Vector<Secretion>  tracking() {       // Just a vesicle tracking in Z 
    	
    	Vector<Secretion> detected_vesicle_seqs = new Vector<Secretion>();
    	int vNum = detected_vesicle.size();
    	
    	//Progress monitor
    	ProgressMonitor PM = new ProgressMonitor(null, "Please wait", "", 0, 100);
		PM.setNote("Recongnazing Secretion ....");
		PM.setMaximum(vNum);
		
    	for (int i = 0; i < vNum; i++) {
  		
        	if (detected_vesicle.elementAt(i).isLinked) continue;
        	
    		Vector<Vesicle> vesicle_sequence = new Vector<Vesicle>();
    		boolean isNewEvent = true;
    		boolean hasNext;
    		Vesicle current_vesicle = detected_vesicle.elementAt(i);
    		
    		
    		do {
    			hasNext = false;    		
    			for (int j = 0; j < vNum; j++) {
    				if (detected_vesicle.elementAt(j).slice != (current_vesicle.slice + 1)) continue;
    				if (detected_vesicle.elementAt(j).isLinked) continue;
    				Vesicle next_vesicle = detected_vesicle.elementAt(j);

    				if (current_vesicle.isPrior(next_vesicle, spot_radius)) {
    					if (isNewEvent) {
    						detected_vesicle.elementAt(i).isLinked = true;
    						vesicle_sequence.addElement(current_vesicle);
    						isNewEvent = false;
    					}
    					detected_vesicle.elementAt(j).isLinked = true;
    					vesicle_sequence.addElement(next_vesicle);
    					current_vesicle = next_vesicle;
    	    			hasNext =true;
    	    			break;
    				}
    			}
    		} while (hasNext);
    		

			PM.setProgress(i);
    		
    		if (vesicle_sequence.size() >= 3) {
    			detected_vesicle_seqs.addElement(new Secretion(vesicle_sequence));
    		}
    		
    	}
    	return detected_vesicle_seqs;

   }
    
 }



