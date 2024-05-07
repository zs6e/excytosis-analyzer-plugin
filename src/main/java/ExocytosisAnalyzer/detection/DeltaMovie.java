package ExocytosisAnalyzer.detection;

import ij.ImagePlus;

public class DeltaMovie {

    public static void getDeltaMovie(ImagePlus imp) {
        ImagePlus in = imp.duplicate();
		int num_of_slices = imp.getStackSize();

		
		double max = 0; // JunJun version for display scaling
		double min = 0; // JunJun version for display scaling
		
		
      

        // Process the first slice separately
		short[] previous = (short[]) in.getStack().getProcessor(1).getPixels();
		short[] current = (short[]) in.getStack().getProcessor(1).getPixels();
		short[] pixels = (short[]) imp.getStack().getProcessor(1).getPixels();
        for (int i = 0; i < current.length; i++){
        	pixels[i] =  (short) Math.abs(current[i] - previous[i]);
		}
  
        for (int n = 2; n <= num_of_slices; n++) {
        	previous = (short[]) in.getStack().getProcessor(n-1).getPixels();
        	current = (short[]) in.getStack().getProcessor(n).getPixels();
        	pixels = (short[]) imp.getStack().getProcessor(n).getPixels();
            // Calculate absolute difference (and not subtract because of probable negative values)
			for (int i = 0; i < current.length; i++){
				pixels[i] = (short) Math.abs(current[i] - previous[i]);
				if (pixels[i] > max) max = pixels[i];
				//if (current[i] < min) min = current[i];
			}

        }
       	
        // automatic scaling display: out.setDisplayRange(0, out.getStatistics().max);
        imp.setDisplayRange(min,max); // JunJun version  

    }
}