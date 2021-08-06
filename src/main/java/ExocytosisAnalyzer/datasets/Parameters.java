package ExocytosisAnalyzer.datasets;

import java.io.Serializable;
import java.util.Arrays;
import ij.IJ;
import ij.ImagePlus;

public class Parameters implements Serializable {

	private static final long serialVersionUID = 4608504839289788158L;

	// Global
	public boolean loadCustomParameters = false;
	private final int[] availableScale = { 1, 3, 7, 13, 25 }; // in pixel
	public boolean[] scaleList = {false, false, false, false, false };
	public String path;
	public String filename;
	public int image_width;
	public int image_height;
	
	// Vesicle parameters

	public double pixelSize;
	public double timeInterval;
	public double framerate;
	
	public String pixelUnit;
	public String timeUnit;
	
	public int minRadius;
	public int maxRadius;
	public double MaxDisplacement;
	public double SNR_Vesicle;
	
	public boolean useWavelet = true;
	public boolean showLowpass = false;
	public boolean showlist = false;
	



	
	public boolean WaveletFilter;
	public boolean lowpass;
	
	// Tracking parameters

	public int SpatialsearchRadius;
	public int TemporalSearchDepth;
	public int minimalEventSize;

	// Secretoion parameters

	public boolean useMinimalPointsForFitter;
	public boolean useExpandFrames;

	public boolean useMaxTauFrames;
	public boolean useMinTauFrames;
	public boolean useMinSNR;
	
	public boolean useMaxRadius;
	public boolean useMinRadius;
	public boolean useMaxDisplacement;
	public boolean useMinDecayFitterR2;

	
	public boolean useGaussienfitter2DR2;
	public boolean useGrubbs;

	public int minimalPointsForFitter;
	public int expand_frames_L;
	public int expand_frames_R;
	
	public double min_SNR;
		
	public double max_tau;
	public double min_tau;
	
	public double min_decay_fitter_r2;
	
	public double max_estimated_radius;
	public double min_estimated_radius;	
	
	public double min_gaussienfitter2DR2;
	public String Grubbs_Alpha;

	public Parameters(ImagePlus imp) {
		path = IJ.getDirectory("current");
		filename = imp.getTitle();
	}

	// Vesicles

	public void InitialVesicleParametres(ImagePlus imp) {
		
		Arrays.fill(scaleList, false);
		image_width = imp.getWidth();
		image_height = imp.getHeight();

		pixelSize = imp.getCalibration().pixelWidth;
		if (pixelSize == 0)
			pixelSize = 1;
		pixelUnit = imp.getCalibration().getUnits();
		timeInterval = imp.getCalibration().frameInterval;
		if (timeInterval == 0)
			timeInterval = 1;
		// defaut
		minRadius = 2;
		maxRadius = 3;
		SNR_Vesicle = 3;

		WaveletFilter = true;
		lowpass = false;


		// Calculate wavelet scale
		boolean isEnable = false;
		for (int i = 0; i < 5; i++) {
			if (availableScale[i] >= (minRadius * 2 - 1) && availableScale[i] <= (maxRadius * 2 - 1)) {
				scaleList[i] = true;
				isEnable = true;
			}
		}
		if (!isEnable) {
			for (int i = 1; i < 5; i++) {
				if (availableScale[i] >= (minRadius * 2 - 1)) {
					scaleList[i - 1] = true;
					break;
				}
			}
		}
	}

	// Set wavelet scale
	public void setScaleList(int aMinRadius, int aMaxRadius) {
		boolean isEnable = false;
		for (int i = 0; i < 5; i++) {
			if (availableScale[i] >= aMinRadius * 2 && availableScale[i] <= aMaxRadius * 2) {
				scaleList[i] = true;
				isEnable = true;
			}
		}
		if (!isEnable) {
			for (int i = 0; i < 5; i++) {
				if (availableScale[i] >= aMinRadius * 2) {
					scaleList[i - 1] = true;
					break;
				}
			}
		}
	}

	// Secretion

	public void InitialTrackingParameters() {
		
		SpatialsearchRadius = 2;
		TemporalSearchDepth = 1;
		minimalEventSize = 2;
		showlist = false;

	}

	public void InitialSecretionParameters() {

		
		useMinimalPointsForFitter = true;
		useExpandFrames = true;
		useMinSNR = true;
		useMaxTauFrames = false;
		useMinTauFrames = false;
		useMaxRadius = false;
		useMinRadius = false;
		useMaxDisplacement = false;
		useMinDecayFitterR2 = true;

		useGrubbs = true;
		useGaussienfitter2DR2 = true;

		minimalPointsForFitter = 5;
		expand_frames_L = 4;
		expand_frames_R = 4;
		min_SNR = 3;
		max_tau = 20;
		min_tau = 1;
		max_estimated_radius = maxRadius;
		min_estimated_radius = minRadius;
		MaxDisplacement = 3;
		min_decay_fitter_r2 = 0.90;
		min_gaussienfitter2DR2 = 0.75;
		Grubbs_Alpha = "0.05";

	}

	public void copy(Parameters para) {

		this.path = para.path;
		this.filename = para.filename;

		// Vesicle parameters

		this.WaveletFilter = para.WaveletFilter;
		this.lowpass = para.lowpass;

		this.minRadius = para.minRadius;
		this.maxRadius = para.maxRadius;
		this.SNR_Vesicle = para.SNR_Vesicle;
		this.useWavelet = para.useWavelet;
		this.showLowpass = para.showLowpass;
		this.scaleList = para.scaleList;
		this.pixelSize = para.pixelSize;
		this.timeInterval = para.timeInterval;
		this.pixelUnit = para.pixelUnit;
		this.timeUnit = para.timeUnit;

		// Tracking parameters

		this.SpatialsearchRadius = para.SpatialsearchRadius;
		this.TemporalSearchDepth = para.TemporalSearchDepth;
		this.minimalEventSize = para.minimalEventSize;

		// Secretoion parameters

		this.useMinimalPointsForFitter = para.useMinimalPointsForFitter;
		this.useExpandFrames = para.useExpandFrames;
		this.useMinSNR = para.useMinSNR;
		this.useMaxTauFrames = para.useMaxTauFrames;
		this.useMinTauFrames = para.useMinTauFrames;
		this.useMaxRadius = para.useMaxRadius;
		this.useMinRadius = para.useMinRadius;
		this.useMaxDisplacement = para.useMaxDisplacement;
		this.useMinDecayFitterR2 = para.useMinDecayFitterR2;

		this.useGrubbs = para.useGrubbs;
		this.useGaussienfitter2DR2 = para.useGaussienfitter2DR2;

		this.minimalPointsForFitter = para.minimalPointsForFitter;
		this.expand_frames_L = para.expand_frames_L;
		this.expand_frames_R = para.expand_frames_R;
		this.max_tau = para.max_tau;
		this.min_tau = para.min_tau;
		this.max_estimated_radius = para.max_estimated_radius;
		this.min_estimated_radius = para.min_estimated_radius;
		this.MaxDisplacement = para.MaxDisplacement;
		this.min_decay_fitter_r2 = para.min_decay_fitter_r2;
		this.min_gaussienfitter2DR2 = para.min_gaussienfitter2DR2;
		this.min_SNR = para.min_SNR;
		this.Grubbs_Alpha = para.Grubbs_Alpha;

	}

}