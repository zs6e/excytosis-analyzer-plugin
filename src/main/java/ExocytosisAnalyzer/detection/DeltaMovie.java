package ExocytosisAnalyzer.detection;

import ij.ImagePlus;

public class DeltaMovie {

    public static ImagePlus getDeltaMovie(ImagePlus imp) {
        ImagePlus out = imp.duplicate();
		int num_of_slices = imp.getStackSize();

		
		int max = 0; // JunJun version for display scaling
		int min = 0; // JunJun version for display scaling

        // Process the first slice separately
		short[] previous = (short[]) out.getStack().getProcessor(1).getPixels();
		short[] current = (short[]) out.getStack().getProcessor(1).getPixels();
		
		short[][] pixels = new short[num_of_slices][current.length];

        for (int i = 0; i < pixels[0].length; i++){
        	pixels[0][i] = 0;
        }
				
				
        for (int n = 2; n <= num_of_slices; n++) {
        	previous = (short[]) out.getStack().getProcessor(n-1).getPixels();
        	current = (short[]) out.getStack().getProcessor(n).getPixels();
			for (int i = 0; i < current.length; i++){
				pixels[n-1][i] = (short) (current[i] - previous[i]);
				if (pixels[n-1][i] > max) max = pixels[n-1][i];
				if (pixels[n-1][i] < min) min = pixels[n-1][i];
			}

        }
        if (min < 0) {
        	min = Math.abs(min);
        	max = max+min;
            for (int n = 0; n < pixels.length; n++) {
                for (int i = 0; i < pixels[n].length; i++) {
                	pixels[n][i] += min;
                }
            }
        }
		for (int n = 1; n <= num_of_slices; n++) {

			short[] intensity = (short[]) out.getStack().getProcessor(n).getPixels();
			
				for (int i = 0; i < intensity.length; i++){
					intensity[i] = (short)pixels[n-1][i] ;
				}
		}
		out.setDisplayRange(0,max); 
		out.setTitle("dF movie");
        return out;

    }
    public static ImagePlus getAbsDeltaMovie(ImagePlus imp) {
        ImagePlus out = imp.duplicate();
		int num_of_slices = imp.getStackSize();

		
		int max = 0; // JunJun version for display scaling

        // Process the first slice separately
		short[] previous = (short[]) out.getStack().getProcessor(1).getPixels();
		short[] current = (short[]) out.getStack().getProcessor(1).getPixels();
		
		short[][] pixels = new short[num_of_slices][current.length];

        for (int i = 0; i < pixels[0].length; i++){
        	pixels[0][i] = 0;
        }
				
				
        for (int n = 2; n <= num_of_slices; n++) {
        	previous = (short[]) out.getStack().getProcessor(n-1).getPixels();
        	current = (short[]) out.getStack().getProcessor(n).getPixels();
			for (int i = 0; i < current.length; i++){
				pixels[n-1][i] = (short) (current[i] - previous[i]);
				if (pixels[n-1][i] > max) max = pixels[n-1][i];
			}

        }

		for (int n = 1; n <= num_of_slices; n++) {

			short[] intensity = (short[]) out.getStack().getProcessor(n).getPixels();
			
				for (int i = 0; i < intensity.length; i++){
					intensity[i] = (short)Math.abs( pixels[n-1][i] );
				}
		}
		out.setDisplayRange(0,max); 
		out.setTitle("dF movie");
        return out;

    }
}