package ExocytosisAnalyzer.datasets;

import java.io.Serializable;
import java.util.Arrays;
import ij.IJ;
import ij.ImagePlus;

@SuppressWarnings("serial")
public class Parameters implements Serializable {

    
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
	
	public int minSize;
	public int maxSize;

	public double SNR_Vesicle;
	
	public boolean useTheoreticalResolution;
	public double TheoreticalResolution;
	
	public boolean WaveletFilter;
	public boolean lowpass;
	public boolean deltamovie;
	// Tracking parameters

	public int SpatialsearchRadius;
	public int TemporalSearchDepth;
	public int minimalEventSize;
	public boolean showlist;
	
	
	// Secretion parameters

	public boolean useMinimalPointsForFitter;
	public boolean useExpandFrames;
	public boolean fixedExpand;

	public boolean useMaxTauFrames;
	public boolean useMinTauFrames;
	public boolean useMinSNR;
	
	public boolean useMaxSize;
	public boolean useMinSize;
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
	
	public double max_estimated_size;
	public double min_estimated_size;	
	public double MaxDisplacement;
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
		if (pixelUnit == null)
			pixelUnit = "um";
		timeInterval = imp.getCalibration().frameInterval;
		if (timeInterval == 0)
			timeInterval = 1;
		//timeUnit = imp.getCalibration().getTimeUnit();
		//if (timeUnit == null)
			timeUnit = "sec";

		framerate = 1.0/timeInterval;
		// defaut
		minSize = 4;
		maxSize = 8;
		SNR_Vesicle = 3;
		TheoreticalResolution = 0.4;
		useTheoreticalResolution = false;
		WaveletFilter = true;
		lowpass = false;
		deltamovie = false;
		// Calculate wavelet scale
		boolean isEnable = false;
		for (int i = 0; i < 5; i++) {
			if (availableScale[i] >= (minSize - 1) && availableScale[i] <= (maxSize- 1)) {
				scaleList[i] = true;
				isEnable = true;
			}
			else
				scaleList[i] = false;
		}
		if (!isEnable) {
			for (int i = 1; i < 5; i++) {
				if (availableScale[i] >= (minSize - 1)) {
					scaleList[i - 1] = true;
					break;
				}
			}
		}
	}

	// Set wavelet scale
	public void setScaleList(int aMinSize, int aMaxSize) {
		boolean isEnable = false;
		for (int i = 0; i < 5; i++) {
			if (availableScale[i] >= (aMinSize - 1) && availableScale[i] <= (aMaxSize - 1))  {
				scaleList[i] = true;
				isEnable = true;
			}
			else
				scaleList[i] = false;
		}
		if (!isEnable) {
			for (int i = 0; i < 5; i++) {
				if (availableScale[i] >= (aMinSize - 1)){
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
		
		showlist = false;
		if (deltamovie)
			minimalEventSize = 1;
		else
			minimalEventSize = 2;
				
	}

	public void InitialSecretionParameters() {

		
		useMinimalPointsForFitter = true;
		useExpandFrames = true;
		fixedExpand = true;
		
		useMaxTauFrames = false;
		useMinTauFrames = false;
		useMinSNR = true;
		
		useMaxSize = false;
		useMinSize = false;
		useMaxDisplacement = false;
		useMinDecayFitterR2 = true;

		useGrubbs = true;
		useGaussienfitter2DR2 = false;

		minimalPointsForFitter = 5;
		expand_frames_L = 4;
		expand_frames_R = 4;
		
		min_SNR = 3;
		
		max_tau = 20;
		min_tau = 1;
		min_decay_fitter_r2 = 0.80;
		
		max_estimated_size = maxSize;
		min_estimated_size = minSize;
		MaxDisplacement = 3;
		min_gaussienfitter2DR2 = 0.75;
		Grubbs_Alpha = "0.05";

	}

	public void copy(Parameters para) {

		this.path = para.path;
		this.filename = para.filename;
		this.image_width = para.image_width;
		this.image_height = para.image_height;

		// Vesicle parameters

		this.scaleList = para.scaleList;
		this.pixelSize = para.pixelSize;
		this.timeInterval = para.timeInterval;
		this.pixelUnit = para.pixelUnit;
		this.timeUnit = para.timeUnit;
		this.minSize = para.minSize;
		this.maxSize = para.maxSize;
		this.SNR_Vesicle = para.SNR_Vesicle;
		
		this.useTheoreticalResolution = para.useTheoreticalResolution;
		this.TheoreticalResolution = para.TheoreticalResolution;
		
		
		this.WaveletFilter = para.WaveletFilter;
		this.lowpass = para.lowpass;
		//this.deltamovie = para.deltamovie;
		
		// Tracking parameters

		this.SpatialsearchRadius = para.SpatialsearchRadius;
		this.TemporalSearchDepth = para.TemporalSearchDepth;
		this.minimalEventSize = para.minimalEventSize;
		this.showlist = para.showlist;

		// Secretion parameters

		this.useMinimalPointsForFitter = para.useMinimalPointsForFitter;
		this.useExpandFrames = para.useExpandFrames;
		this.fixedExpand = para.fixedExpand;
		
		this.useMaxTauFrames = para.useMaxTauFrames;
		this.useMinTauFrames = para.useMinTauFrames;
		this.useMinSNR = para.useMinSNR;
		
		this.useMaxSize = para.useMaxSize;
		this.useMinSize = para.useMinSize;
		this.useMaxDisplacement = para.useMaxDisplacement;
		this.useMinDecayFitterR2 = para.useMinDecayFitterR2;

		this.useGrubbs = para.useGrubbs;
		this.useGaussienfitter2DR2 = para.useGaussienfitter2DR2;

		this.minimalPointsForFitter = para.minimalPointsForFitter;
		this.expand_frames_L = para.expand_frames_L;
		this.expand_frames_R = para.expand_frames_R;
		
		this.min_SNR = para.min_SNR;
		
		this.max_tau = para.max_tau;
		this.min_tau = para.min_tau;
		
		this.min_decay_fitter_r2 = para.min_decay_fitter_r2;
		
		this.max_estimated_size = para.max_estimated_size;
		this.min_estimated_size = para.min_estimated_size;
		this.MaxDisplacement = para.MaxDisplacement;
		this.min_gaussienfitter2DR2 = para.min_gaussienfitter2DR2;
		this.Grubbs_Alpha = para.Grubbs_Alpha;

	}

}