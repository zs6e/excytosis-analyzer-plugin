package ExocytosisAnalyzer.datasets;

import org.apache.commons.math3.fitting.WeightedObservedPoints;

import ExocytosisAnalyzer.detection.VesicleFitter;
import ij.ImageStack;
import ij.measure.CurveFitter;
import ij.process.ImageProcessor;

public class Vesicle {

	public int ref_ID;
	public boolean isValid = true;
	public boolean isLinked = false;
	
	public String property;
	public int x, y, slice, radius;
	public int max_Den, min_Den, int_Den;
	public ImageProcessor vesicleImage;
	public int image_height, image_width;
	public double[] mask;
	public double Rsqr2D = 0;
	public double[] fData;
	public double[] Gauss2DParam;
	public double pixelSize = 1;
	public String pixelSizeUnit = "µM";
	private double[] GaussianParametresHorizontal;
	private double[] GaussianParametresVertical;

	/**
	 * @param isLinked			- Linked (regiseted) in an exocytosis event   
	 * @param x       			- x coordinates
	 * @param y					- y coordinates
	 * @param radius  			- Radius in pixel
	 * @param slice  			- the number of the slice this vesicle belongs to
	 * @param max_Den 			- maximal pixel density
	 * @param int_Den			- integrated pixel density
	 * @param Rsqr2D			- R sqr of fitter
	 * @param fData				- 2DGaussFitter parameters (new)
	 * @param GaussianParametresHorizontal	- linearGaussFitter Horizontal (old version)
	 * @param GaussianParametresVertical	- linearGaussFitter Vertical (old version)
	 */
	public Vesicle() {

	}

	public Vesicle(int aX, int aY, int aRaidus, int aSlice) {
		this.x = aX;
		this.y = aY;
		this.slice = aSlice;
		this.radius = aRaidus;
	}

	public Vesicle(int aX, int aY, int aRadius, int aSlice, ImageStack is) {
		int corr_x = aX;
		int corr_y = aY;
		x = corr_x;
		y = corr_y;
		slice = aSlice;
		radius = aRadius;
		int width = is.getWidth();
		int height = is.getHeight();
		ImageProcessor slice_of_corrent_vesicle = is.getProcessor(slice).duplicate();
		// sometimes localmaxia in lowpass image is different form the original image
		float current_intensity, max_intensity;
		float[] img = (float[]) slice_of_corrent_vesicle.convertToFloatProcessor().getPixels();
		max_intensity = img[corr_y * width + corr_x];
		for (int j = -radius; j <= radius; j++) {
			for (int i = -radius; i <= radius; i++) {
				if ((i * i) + (j * j) <= radius * radius) {
					current_intensity = img[(corr_y + j) * width + (corr_x + i)];
					if (current_intensity > max_intensity) {
						x = corr_x + i;
						y = corr_y + j;
						max_intensity = current_intensity;
					}
				}
			}
		}

		// make sure to corp minimal 13*13 pixel for a vesicle image
		int corp_radius;
		if (radius < 2)
			corp_radius = 2;
		else
			corp_radius = radius + 1;

		// Corp start at:
		int roi_x = x - 2 * corp_radius;
		int roi_y = y - 2 * corp_radius;
		
		if (roi_x < 0)
			roi_x = 0;
		if (roi_y < 0)
			roi_y = 0;
		
		// Corp image size is 4*radius + 1 pixel
		
		int roi_w = 4 * corp_radius + 1;
		int roi_h = 4 * corp_radius + 1;
		if ((roi_x + roi_w) > width) {
			roi_x = width - roi_w;
		}
		if ((roi_y + roi_h) > height) {
			roi_y = height - roi_h;
		}

		slice_of_corrent_vesicle.setRoi(roi_x, roi_y, roi_w, roi_h);

		this.vesicleImage = slice_of_corrent_vesicle.crop();
		float[] pixels = (float[]) vesicleImage.convertToFloatProcessor().getPixels();
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
		this.max_Den = (int) max_value;
		this.min_Den = (int) min_value;
		this.image_height = vesicleImage.getHeight();
		this.image_width = vesicleImage.getWidth();
		int index;
		this.mask = new double[roi_w * roi_h];
		for (int j = -corp_radius * 2; j <= corp_radius * 2; j++) {
			for (int i = -corp_radius * 2; i <= corp_radius * 2; i++) {
				index = (i + corp_radius * 2) * roi_w + (j + corp_radius * 2);
				if ((i * i) + (j * j) <= corp_radius * corp_radius * 4) {
					this.mask[index] = 1;
				} else {
					this.mask[index] = 0;
				}
			}
		}
	}

	public void setImage(ImageStack is) {
		int corr_x = this.x;
		int corr_y = this.y;
		int width = is.getWidth();
		int height = is.getHeight();
		ImageProcessor slice_of_corrent_vesicle = is.getProcessor(slice).duplicate();
		// sometimes localmaxia in lowpass image is different form the original image
		float current_intensity, max_intensity;
		float[] img = (float[]) slice_of_corrent_vesicle.convertToFloatProcessor().getPixels();
		max_intensity = img[corr_y * width + corr_x];
		for (int j = -radius; j <= radius; j++) {
			for (int i = -radius; i <= radius; i++) {
				if ((i * i) + (j * j) <= radius * radius) {
					current_intensity = img[(corr_y + j) * width + (corr_x + i)];
					if (current_intensity > max_intensity) {
						this.x = corr_x + i;
						this.y = corr_y + j;
						max_intensity = current_intensity;
					}
				}
			}
		}

		// minimal 13*13 pixel for vesicle images

		int corp_radius;
		if (radius < 2)
			corp_radius = 2;
		else
			corp_radius = radius +1 ;

		int roi_x = x - 2 * corp_radius;
		int roi_y = y - 2 * corp_radius;
		if (roi_x < 0)
			roi_x = 0;
		if (roi_y < 0)
			roi_y = 0;

		int roi_w = 4 * corp_radius + 1;
		int roi_h = 4 * corp_radius + 1;
		if ((roi_x + roi_w) > width) {
			roi_x = width - roi_w;
		}
		if ((roi_y + roi_h) > height) {
			roi_y = height - roi_h;
		}

		slice_of_corrent_vesicle.setRoi(roi_x, roi_y, roi_w, roi_h);

		this.vesicleImage = slice_of_corrent_vesicle.crop();
		float[] pixels = (float[]) vesicleImage.convertToFloatProcessor().getPixels();
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
		this.max_Den = (int) max_value;
		this.min_Den = (int) min_value;
		this.image_height = vesicleImage.getHeight();
		this.image_width = vesicleImage.getWidth();
		int index;
		this.mask = new double[roi_w * roi_h];
		for (int j = -corp_radius * 2; j <= corp_radius * 2; j++) {
			for (int i = -corp_radius * 2; i <= corp_radius * 2; i++) {
				index = (i + corp_radius * 2) * roi_w + (j + corp_radius * 2);
				if ((i * i) + (j * j) <= corp_radius * corp_radius * 4) {
					this.mask[index] = 1;
				} else {
					this.mask[index] = 0;
				}
			}
		}
	}

	public void setImage(ImageProcessor aImageProcessor) {
		int corp_radius;
		if (radius < 2)
			corp_radius = 2;
		else
			corp_radius = radius + 1 ;

		int roi_x = x - 2 * corp_radius;
		int roi_y = y - 2 * corp_radius;
		if (roi_x < 0)
			roi_x = 0;
		if (roi_y < 0)
			roi_y = 0;

		int roi_w = 4 * corp_radius + 1;
		int roi_h = 4 * corp_radius + 1;
		if ((roi_x + roi_w) > aImageProcessor.getWidth()) {
			roi_x = aImageProcessor.getWidth() - roi_w;
		}
		if ((roi_y + roi_h) > aImageProcessor.getHeight()) {
			roi_y = aImageProcessor.getHeight() - roi_h;
		}

		aImageProcessor.setRoi(roi_x, roi_y, roi_w, roi_h);
		this.vesicleImage = aImageProcessor.duplicate().crop();
		float[] pixels = (float[]) vesicleImage.duplicate().convertToFloatProcessor().getPixels();
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
		this.max_Den = (int) max_value;
		this.min_Den = (int) min_value;
		this.image_height = vesicleImage.getHeight();
		this.image_width = vesicleImage.getWidth();
		int index;
		this.mask = new double[roi_w * roi_h];
		for (int j = -corp_radius * 2; j <= corp_radius * 2; j++) {
			for (int i = -corp_radius * 2; i <= corp_radius * 2; i++) {
				index = (i + corp_radius * 2) * roi_w + (j + corp_radius * 2);
				if ((i * i) + (j * j) <= corp_radius * corp_radius * 4) {
					this.mask[index] = 1;
				} else {
					this.mask[index] = 0;
				}
			}
		}
	}

	public boolean isDuplicata(Vesicle aVesicle, int aRadius) {

		if (Math.abs(x - aVesicle.x) <= aRadius && Math.abs(y - aVesicle.y) <= aRadius && slice == aVesicle.slice) {
			return true;
		} else {
			return false;
		}
	}

	public double getDistance(Vesicle aVesicle) {

		return Math.sqrt(Math.abs(x - aVesicle.x) + Math.abs(y - aVesicle.y));

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
		y = aY;
	}

	public int gety() {
		return y;
	}

	public ImageProcessor getImage() {
		return vesicleImage;
	}

	public void setIntDen(int aInt_Den) {
		int_Den = aInt_Den;
	}

	public int getIntDen() {
		return int_Den;
	}

	public void setMaxDen(int aMax_Den) {
		max_Den = aMax_Den;
	}

	public double getMaxDen() {
		return max_Den;
	}

	public void setMinDen(int aMin_Den) {
		min_Den = aMin_Den;
	}

	public double getMinDen() {
		return min_Den;
	}

	public void setRadius(int aRadius) {
		radius = aRadius;
	}

	public int getRadius() {
		return radius;
	}

	public void setRef(int aID) {
		ref_ID = aID;
	}

	public int getRef() {
		return ref_ID;
	}

	// Estimate Size by Gaussian fitter
	public double EstimateSize1D() {
		if (!this.isValid)
			return 0;
		GaussianParametresHorizontal = this.getHorizontalGaussFitter();
		GaussianParametresVertical = this.getVerticalGaussFitter();
		return (GaussianParametresHorizontal[3] + GaussianParametresVertical[3]) / 2;
	}

	// Estimate peak by Gaussian fitter
	public double EstimateMaxIntensity1D() {
		GaussianParametresHorizontal = this.getHorizontalGaussFitter();
		GaussianParametresVertical = this.getVerticalGaussFitter();
		return (GaussianParametresHorizontal[2] + GaussianParametresVertical[2]) / 2;
	}

	public double[] getVerticalProfile() {
		double[] profile_v;
		profile_v = vesicleImage.getLine((image_width - 1) / 2, 0, (image_width - 1) / 2, image_height - 1);
		return profile_v;
	}

	public double[] getHorizontalProfile() {
		double[] profile_h;
		profile_h = vesicleImage.getLine(0, (image_height - 1) / 2, image_width - 1, (image_height - 1) / 2);
		return profile_h;
	}

	public double[] getVerticalGaussFitter() {
		double[] profile_v = this.getVerticalProfile();
		double[] xdata = new double[image_height];
		for (int i = 0; i < xdata.length; i++) {
			xdata[i] = i;
		}
		double[] initialParams = { min_Den, max_Den, (image_height - 1) / 2, radius };
		CurveFitter gaussianfitterV = new CurveFitter(xdata, profile_v);
		gaussianfitterV.setInitialParameters(initialParams);
		gaussianfitterV.doFit(CurveFitter.GAUSSIAN);
		double[] ParametresV = gaussianfitterV.getParams();
		return ParametresV;

	}

	public double[] getHorizontalGaussFitter() {
		double[] profile_h = this.getHorizontalProfile();
		double[] xdata = new double[image_width];

		for (int i = 0; i < xdata.length; i++) {
			xdata[i] = i;
		}
		double[] initialParams = { min_Den, max_Den, (image_width - 1) / 2, radius };
		CurveFitter gaussianfitterH = new CurveFitter(xdata, profile_h);
		gaussianfitterH.setInitialParameters(initialParams);
		gaussianfitterH.doFit(CurveFitter.GAUSSIAN);
		double[] ParametresH = gaussianfitterH.getParams();
		return ParametresH;
	}

	public double EstimateSize2D() {
		if (!this.isValid)
			return 0;
		if (Rsqr2D != 0) {
			return Gauss2DParam[4];
		} else {
			get2DGaussFitter();
			return Gauss2DParam[4];
		}
	}

	public double EstimateMaxIntensity2D() {
		if (!this.isValid)
			return 0;
		if (Rsqr2D != 0) {
			return Gauss2DParam[1];
		} else {
			get2DGaussFitter();
			return Gauss2DParam[1];
		}
	}

	public double[] get2DFitData() {

		if (Rsqr2D != 0) {
			return fData;
		} else {
			get2DGaussFitter();
			return fData;
		}
	}

	public double get2DFitRsqr() {
		if (!this.isValid)
			return 0;
		if (Rsqr2D != 0) {
			return Rsqr2D;
		} else {
			get2DGaussFitter();
			return Rsqr2D;
		}
	}

	public void get2DGaussFitter() {
		double[] initialGuess = new double[5];
		float[] pixels = (float[]) vesicleImage.duplicate().convertToFloatProcessor().getPixels();
		initialGuess[0] = min_Den;
		initialGuess[1] = max_Den - min_Den;
		initialGuess[2] = (image_width - 1) / 2;
		initialGuess[3] = (image_height - 1) / 2;
		initialGuess[4] = radius;
		WeightedObservedPoints obs = new WeightedObservedPoints();
		for (int i = 0; i < pixels.length; i++) {
			obs.add(mask[i], i, pixels[i]);
		}

		VesicleFitter fitter = new VesicleFitter(image_width, initialGuess, Integer.MAX_VALUE);
		Gauss2DParam = fitter.fit(obs.toList());
		Rsqr2D = fitter.rSquared;
		fData = fitter.fData;
	}
}