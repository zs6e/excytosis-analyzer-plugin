package ExocytosisAnalyzer.detection;

import ij.ImagePlus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;


import ExocytosisAnalyzer.datasets.Vesicle;
import ExocytosisAnalyzer.datasets.Parameters;
import ij.ImageStack;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import io.scif.jj2000.j2k.roi.encoder.ROI;

public class LocalMaximaDetector {
	private ImageStack is;
	private ImageStack lowPassStack;
	private Roi roi;
	private int start_x;
	private int start_y;
	private int end_x;
	private int end_y;
	private final int image_width;
	private final int image_height;
	private final int kernel_width;
	private final int spot_radius;
	private final int max_radius;
	private final double threshold_sigma;
	private final boolean mask[];
	// private ImageStack normalized_float_is;
	private Parameters paras;


	public LocalMaximaDetector(ImagePlus imp, Parameters paras) {
		this.paras = paras;
		image_width = imp.getWidth();
		image_height = imp.getHeight();
		
	
		
		spot_radius = paras.minRadius;
		max_radius = paras.maxRadius;
		threshold_sigma = paras.SNR_Vesicle;
		kernel_width = spot_radius * 2 + 1;
		mask = new boolean[kernel_width * kernel_width];
		
		//set a margin
		start_x = max_radius*2;
		start_y = max_radius*2;
		end_x = image_width - max_radius*2;
		end_y = image_height - max_radius*2;
		roi = new Roi(0, 0, image_width, image_height);
		
		int index = 0;
		for (int i = -spot_radius; i <= spot_radius; i++) {
			for (int j = -spot_radius; j <= spot_radius; j++) {
				index = (i + spot_radius) * kernel_width + (j + spot_radius);
				if ((i * i) + (j * j) <= spot_radius * spot_radius) {
					mask[index] = true;
				} else {
					mask[index] = false;
				}
			}
		}

	}
	public void setRoi(Roi aRoi) {
		roi = aRoi;
	}
	public float CalculateThreshold(float[] img) {

		float[] res = new float[img.length];
		float median = ExocytosisAnalyzer.detection.FindMedian.findMedian(img);
		
		for (int i =0; i < img.length ; i++ ) {
			res[i] =  Math.abs(img[i] - median);
		}

		double MAD = ExocytosisAnalyzer.detection.FindMedian.findMedian(res)/0.6745;
		
		return (float) (median+threshold_sigma*MAD) ;
	}

	public Vector<Vesicle> FindLocalMaxima(float[] img, int slice, float threshold) {
		Vector<Vesicle> local_maxima = new Vector<Vesicle>();
		boolean isMax;
		// max_radius*2 to avoid border effect
		
		
		for (int y = start_y; y < end_y; y++) {
			for (int x = start_x; x < end_x ; x++) {
				if (!roi.contains(x, y))
					continue;
				if (img[y * image_width + x] < threshold)
					continue;

				isMax = true;
				// i,j are the kernel coordinates corresponding to x,y
				for (int j = -spot_radius; j <= spot_radius; j++) {
					if (!isMax)
						break;
					for (int i = -spot_radius; i <= spot_radius; i++) {
						if (mask[(j + spot_radius) * kernel_width + (i + spot_radius)]) {
							if (img[y * image_width + x] < img[(y + j) * image_width + (x + i)]) {
								isMax = false;
							}
						}
					}
				}
				if (isMax) {
					Vesicle current_local_maxima = new Vesicle(x, y, spot_radius, slice);
					current_local_maxima.pixelSize = paras.pixelSize;
					current_local_maxima.pixelSizeUnit = paras.pixelUnit;
					current_local_maxima.property = "Automatic";
					local_maxima.addElement(current_local_maxima);
				}
			}
		}
		return local_maxima;
	}
	
	public float[] getNormalizedImage(ImageProcessor ip) {

		// Convert to float and normalizing (0-1) in all frames
		FloatProcessor fp = new FloatProcessor(image_width, image_height);
		float[] normalized_float_image = new float[image_width * image_height];

		fp = ip.duplicate().convertToFloatProcessor();
		float[] pixels = (float[]) fp.getPixels();
		float max_value = pixels[0];
		float min_value = pixels[0];

		for (int i = 1; i < pixels.length; i++) {
			if (pixels[i] > max_value) {
				max_value = pixels[i];
			}
			if (pixels[i] < min_value) {
				min_value = pixels[i];
			}
		}

		// Normalization
		fp.add(-min_value);
		fp.multiply(1 / (max_value - min_value));

		normalized_float_image = (float[]) fp.getPixels();

		return normalized_float_image;
	}

	public float[] getNormalizedImage(float[] pixels) {

		// Convert to float and normalizing (0-1) in all frames
		float[] normalized_float_image = new float[image_width * image_height];

		float max_value = pixels[0];
		float min_value = pixels[0];

		for (int i = 1; i < pixels.length; i++) {
			if (pixels[i] > max_value) {
				max_value = pixels[i];
			}
			if (pixels[i] < min_value) {
				min_value = pixels[i];
			}
		}

		for (int i = 1; i < pixels.length; i++) {
			normalized_float_image[i] = (pixels[i] - min_value) / (max_value - min_value);
		}

		return normalized_float_image;
	}

}
