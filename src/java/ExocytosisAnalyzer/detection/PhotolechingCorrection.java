package ExocytosisAnalyzer.detection;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.process.ImageProcessor;

public class PhotolechingCorrection {
	
	public static void MeshBleachingCorrection(ImagePlus imp, Roi roi) {
		
		ImagePlus in = imp.duplicate();
		in.setRoi(roi);
		in.crop();
		
		
		int image_width = imp.getWidth();
		int image_height = imp.getHeight();
		int num_of_slices = imp.getStackSize();
		float[] mean = new float[num_of_slices];

		
		float[] weight_mesh = new float[image_width*image_height];
		float[] temp = new float[image_width*image_height];
		
		float[] pixels = (float[]) in.getStack().getProcessor(1).convertToFloat().getPixels();
		
		float sum = 0;
		float number = 0;
		

		
		// calculate the first image
		for (int i = 0; i < pixels.length; i++) {
			sum += pixels[i];
			number++;
		}

		mean[0] = sum / number;
		float min = mean[0];
		float max = mean[0];
		
		// calculate all the images
		for (int n = 2; n <= num_of_slices; n++) {

			pixels = (float[]) in.getStack().getProcessor(n).convertToFloat().getPixels();
			sum = 0;
			number = 0;
			
			for (int i = 0; i < pixels.length; i++) {			
				sum += pixels[i];
				number++;
			}

			mean[n-1] = sum / number;

			if (mean[n-1] < min) min = mean[n-1];
			if (mean[n-1] > max) max = mean[n-1];

		}
		double mean_global = 0;
		for (int i = 0; i < mean.length; i++) {
			mean_global = mean_global + mean[i];
		}
			
		mean_global = mean_global/mean.length;	
			
			
		
		double mean_photobleaching_range = max-min;
		
		// calculate the differance between the 1st image and the last image
		//calculate the Weight Mesh
		
		weight_mesh = (float[]) imp.getStack().getProcessor(1).convertToFloatProcessor().getPixels();
		temp = (float[]) imp.getStack().getProcessor(num_of_slices).convertToFloatProcessor().getPixels();

		for (int i = 0; i < weight_mesh.length; i++) {
			weight_mesh[i] = weight_mesh[i] - temp[i];

		}
			
		
		for (int n = 1; n <= num_of_slices; n++) {

			short[] intensity = (short[]) imp.getStack().getProcessor(n).getPixels();
			
				for (int i = 0; i < intensity.length; i++){
					intensity[i] = (short) Math.abs(intensity[i] - (mean[n-1]-mean_global) * weight_mesh[i] / mean_photobleaching_range);
				}
		}

	}
	
	private static void RoiBleachingCorrection(ImagePlus in, Roi roi) {
		
		int image_width = in.getWidth();
		int image_height = in.getHeight();
		int num_of_slices = in.getStackSize();
		double[] mean = new double[num_of_slices];
		double[] time = new double[num_of_slices];
		

		float[] pixels = (float[]) in.getStack().getProcessor(1).convertToFloatProcessor().getPixels();
		double sum = 0;
		double number = 0;
		
		for (float f : pixels) {
			sum += f;
			number++;
		}

		mean[0] = sum / number;
		time[0] = 0;
		double min = mean[0];
		for (int i = 1; i < num_of_slices; i++) {

			pixels = (float[]) in.getStack().getProcessor(i + 1).convertToFloatProcessor().getPixels();
			sum = 0;
			number = 0;
			for (float f : pixels) {
				sum += f;
				number++;
			}

			mean[i] = sum / number;
			time[i] = i + 1.0;
			if (mean[i] < min) min = mean[i];

		}

		for (int i = 0; i < num_of_slices; i++) {

			in.getStack().getProcessor(i + 1).subtract(mean[i] - min);

		}
	}
}
