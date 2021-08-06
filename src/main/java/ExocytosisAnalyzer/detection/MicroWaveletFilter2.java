//Modified from ICY(Institut Pasteur). a simplified 2D version. 

package ExocytosisAnalyzer.detection;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class MicroWaveletFilter2 {
	private boolean[] scalesList;
	private int maxScale;
	//private int minScale;
	private int width;
	private int height;
	private double scaleThreshold;

	public MicroWaveletFilter2(ImagePlus imp, boolean[] aScalesList, double aScaleThreshold) {

		scalesList = aScalesList;
		width = imp.getWidth();
		height = imp.getHeight();

		scaleThreshold = aScaleThreshold;

		maxScale = 0;
		// minScale = 0;
		// find the max scale number in scalesList[]
		boolean findMin = false;
		for (int i = 0; i < scalesList.length; i++) {
			if (scalesList[i]) {
				if (!findMin) {
					findMin = true;
		//			minScale = i + 1;
				}
				maxScale = i + 1;
			}
		}

	}

	public float[] filter(ImageProcessor ip) {

		float[] Result = new float[width * height];
		
		if (maxScale == 0)
			return Result;
		// check image dimensions
		int minSize = 5 + (int) (Math.pow(2, maxScale - 1) - 1) * 4;
		if (width < minSize || height < minSize)
			return Result;

		final float[] pixels = (float[]) ip.duplicate().convertToFloatProcessor().getPixels();
		// decompose the image
		float[][] scales = b3WaveletScales2D(pixels, width, height, maxScale);
		float[][] coefficients = b3WaveletCoefficients2D(scales, pixels, maxScale);
		// Apply threshold to coefficients but not last one ( residual )

		for (int i = 0; i < coefficients.length - 1; i++) {
			filter_wat(coefficients[i], i, width * height, maxScale, scalesList, scaleThreshold);
		}
		// fill of 0 the residual image
		float[] c = coefficients[coefficients.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = 0;
		}

		Result = b3SpotConstruction2D(coefficients, maxScale, width * height, scalesList);
		return Result;

	}

	
	//Wavelet scale images for a 2D image (Copy from Institut Pasteur)
	private float[][] b3WaveletScales2D(float[] dataIn, int w, int h, int numScales) {
		
		// step between non zero coefficients of the convolution kernel
		int stepS;
		float[][] resArray = new float[numScales][];
		float[] prevArray = dataIn; // array to filter, original data for the first scale
		float[] currentArray; // filtered array

		// for each scale
		for (int s = 1; s <= numScales; s++) {
			stepS = (int) Math.pow(2, s - 1); // compute the step between non zero coefficients of the convolution
												// kernel = 2^(scale-1)
			// convolve along the x direction and swap dimensions
			currentArray = filterAndSwap2D(prevArray, w, h, stepS);
			// swap current and previous array pointers
			if (s == 1) {
				prevArray = currentArray; // the filtered array becomes the array to filter
				currentArray = new float[w * h]; // allocate memory for the next dimension filtering (preserve original
													// data)
			} else {
				float[] tmp = currentArray;
				currentArray = prevArray; // the filtered array becomes the array to filter
				prevArray = tmp; // the filtered array becomes the array to filter
			}
			// convolve along the y direction and swap dimensions
			currentArray = filterAndSwap2D(prevArray, h, w, stepS);// swap size of dimensions
			// swap current and previous array pointers
			float[] tmp = currentArray;
			currentArray = prevArray;
			prevArray = tmp;

			resArray[s - 1] = new float[w * h]; // allocate memory to store the filtered array
			System.arraycopy(prevArray, 0, resArray[s - 1], 0, w * h);
		}

		return resArray;
	}

	private float[][] b3WaveletCoefficients2D(float[][] scaleCoefficients, float[] originalImage, int numScales) {
		// maxScales wavelet images to store, + one image for the low pass residual
		float[][] waveletCoefficients = new float[numScales + 1][];

		// compute wavelet coefficients as the difference between scale coefficients of
		// subsequent scales
		float[] iterPrev = originalImage;// the finest scale coefficient is the difference between the original image
											// and the first scale.
		float[] iterCurrent;

		int j = 0;
		while (j < numScales) {
			iterCurrent = scaleCoefficients[j];
			float[] wCoefficients = new float[originalImage.length];
			for (int i = 0; i < originalImage.length; i++) {
				wCoefficients[i] = iterPrev[i] - iterCurrent[i];
			}
			waveletCoefficients[j] = wCoefficients;
			iterPrev = iterCurrent;
			j++;
		}
		// residual low pass image is the last wavelet Scale
		waveletCoefficients[numScales] = new float[originalImage.length];
		System.arraycopy(scaleCoefficients[numScales - 1], 0, waveletCoefficients[numScales], 0, originalImage.length);
		return waveletCoefficients;
	}

	/**
	 * filter a 2D image with the B3 spline wavelet scale kernel in the x direction
	 * when using the a trous algorithm, and swap dimensions (Copy from Institut Pasteur)
	 */
	private float[] filterAndSwap2D(float[] arrayIn, int w, int h, int stepS) {
		float[] arrayOut = new float[w * h];
		// B3 spline wavelet configuration
		// the convolution kernel is {1/16, 1/4, 3/8, 1/4, 1/16}
		float w2 = ((float) 1) / 16;
		float w1 = ((float) 1) / 4;
		float w0 = ((float) 3) / 8;

		int w0idx;
		int w1idx1;
		int w2idx1;
		int w1idx2;
		int w2idx2;
		int arrayOutIter;

		int cntX;
		w0idx = 0;

		for (int y = 0; y < h; y++) {// loop over the second dimension
			// manage the left border with mirror symmetry
			arrayOutIter = 0 + y;// output pointer initialization, we swap dimensions at this point
			// w0idx = arrayIn + y*width;
			w1idx1 = w0idx + stepS - 1;
			w2idx1 = w1idx1 + stepS;
			w1idx2 = w0idx + stepS;
			w2idx2 = w1idx2 + stepS;

			cntX = 0;
			while (cntX < stepS) {

				arrayOut[arrayOutIter] = w2 * ((arrayIn[w2idx1]) + (arrayIn[w2idx2]))
						+ w1 * ((arrayIn[w1idx1]) + (arrayIn[w1idx2])) + w0 * (arrayIn[w0idx]);
				w1idx1--;
				w2idx1--;
				w1idx2++;
				w2idx2++;
				w0idx++;
				arrayOutIter += h;
				cntX++;
			}
			w1idx1++;
			while (cntX < 2 * stepS) {

				arrayOut[arrayOutIter] = w2 * ((arrayIn[w2idx1]) + (arrayIn[w2idx2]))
						+ w1 * ((arrayIn[w1idx1]) + (arrayIn[w1idx2])) + w0 * (arrayIn[w0idx]);
				w1idx1++;
				w2idx1--;
				w1idx2++;
				w2idx2++;
				w0idx++;
				arrayOutIter += h;
				cntX++;
			}
			w2idx1++;
			// filter the center area of the image (no border issue)
			while (cntX < w - 2 * stepS) {

				arrayOut[arrayOutIter] = w2 * ((arrayIn[w2idx1]) + (arrayIn[w2idx2]))
						+ w1 * ((arrayIn[w1idx1]) + (arrayIn[w1idx2])) + w0 * (arrayIn[w0idx]);
				w1idx1++;
				w2idx1++;
				w1idx2++;
				w2idx2++;
				w0idx++;
				arrayOutIter += h;
				cntX++;
			}
			w2idx2--;
			// manage the right border with mirror symmetry
			while (cntX < w - stepS) {

				arrayOut[arrayOutIter] = w2 * ((arrayIn[w2idx1]) + (arrayIn[w2idx2]))
						+ w1 * ((arrayIn[w1idx1]) + (arrayIn[w1idx2])) + w0 * (arrayIn[w0idx]);
				w1idx1++;
				w2idx1++;
				w1idx2++;
				w2idx2--;
				w0idx++;
				arrayOutIter += h;
				cntX++;
			}
			w1idx2--;
			while (cntX < w) {

				arrayOut[arrayOutIter] = w2 * ((arrayIn[w2idx1]) + (arrayIn[w2idx2]))
						+ w1 * ((arrayIn[w1idx1]) + (arrayIn[w1idx2])) + w0 * (arrayIn[w0idx]);
				w1idx1++;
				w2idx1++;
				w1idx2--;
				w2idx2--;
				w0idx++;
				arrayOutIter += h;
				cntX++;
			}
		}
		return arrayOut;
	}

	private void filter_wat(float[] data, int depth, int numPixels, int numScales, boolean[] aScalesList,
			double aScaleThreshold) {

		if (!aScalesList[depth]) {
			// if the scale is not selected, fill of 0
			for (int i = 0; i < data.length; i++) {
				data[i] = 0;
			}
			return;
		}

		// Init lambda value
		double lambdac[] = new double[numScales + 2];

		for (int i = 0; i < numScales + 2; i++) {
			// ( 1 << (2*i) ) ) gives 1 , 4 , 16 , 64 , 256 , 1024 ...
			lambdac[i] = Math.sqrt(2 * Math.log(numPixels / (1 << (2 * i))));
		}

		float mean = 0;
		float sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		mean = sum / data.length;

		float a = 0;
		float s;
		for (int i = 0; i < data.length; i++) {
			s = data[i] - mean;
			a = a + Math.abs(s);
		}
		float mad = a / data.length;

		double coeffThr = (lambdac[depth + 1] * mad) * aScaleThreshold;

		for (int i = 0; i < data.length; i++) {
			if (data[i] >= coeffThr) {
				// data[ i ] = 255 ;
			} else {
				data[i] = 0;
			}
		}
	}

	private float[] b3SpotConstruction2D(float[][] inputCoefficients, int numScales, int numPixels,
			boolean[] scaleList) {

		float[] output = new float[numPixels];

		for (int i = 0; i < numPixels; i++) {
			float v = 0;

			for (int j = 0; j < numScales; j++) {
				if (scaleList[j]) {
					v += inputCoefficients[j][i];
				}
			}
			output[i] = v;
		}
		return output;
	}
}
