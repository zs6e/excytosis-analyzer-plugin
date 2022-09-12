package ExocytosisAnalyzer.detection;

import java.awt.Color;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import ij.gui.Plot;

public class MovingLinearRegretionFinder {

	double[] yData;
	double[] xData;
	int window_size;
	int T;
	double[] slope_diff;

	public MovingLinearRegretionFinder(double[] signal, double[] xData, int window_size) {
		yData = signal;
		this.xData = xData;
		T = yData.length;
		slope_diff = new double[T];

		this.window_size = window_size;
	}

	public void dofit() {
		SimpleRegression r_left = new SimpleRegression(true);
		SimpleRegression r_right = new SimpleRegression(true);
		double slopeL;
		double slopeR;
		if (window_size <2) return;
		for (int i = window_size - 1; i < T - window_size +1; i++) {

			for (int j = 0; j < window_size; j++) {
				r_right.addData(j, yData[i + j]);
				r_left.addData(j, yData[i - window_size + 1 + j]);
			}

			// intercept = r.getIntercept();
			slopeL = r_left.getSlope();
			slopeR = r_right.getSlope();
			r_left.clear();
			r_right.clear();

			slope_diff[i] = slopeL - slopeR; 
		}
	}

	public int findInflection() {
		double max = 0;
		double sum = 0;
		double average = 0;
		int K1 = 0;
		int K2 = 0;
		for (int i = 0; i < T; i++) {
			sum += yData[i];
		}
		average = sum/T;
		for (int i = window_size -1; i < T-window_size +1; i++) {
			if (slope_diff[i] > max && yData[i]> average ) {
				max = slope_diff[i];
				K1 = i;
			}
		}
		
		if (K1 == 0) return 0;
		max = 0;
		for (int i = K1- window_size/2; i < K1+window_size/2; i++) {
			if (yData[i] > max) {
				max = yData[i];
				K2 = i;
			}
		}
		return K2;
	}
	
	public double findInflectionTime() {
		double max = 0;
		double sum = 0;
		double average = 0;
		int K1 = 0;
		int K2 = 0;
		for (int i = 0; i < T; i++) {
			sum += yData[i];
		}
		average = sum/T;
		for (int i = window_size -1; i < T-window_size +1; i++) {
			if (slope_diff[i] > max && yData[i]> average ) {
				max = slope_diff[i];
				K1 = i;
			}
		}
		
		if (K1 == 0) return 0;
		max = 0;
		for (int i = K1- window_size/2; i < K1+window_size/2; i++) {
			if (yData[i] > max) {
				max = yData[i];
				K2 = i;
			}
		}
		return xData[K2];
	}

	public Plot getPlot() {

		Plot Pt = new Plot("Slope_diff", "frame", "Slope_diff");
		Pt.setColor(Color.RED);
		Pt.add("line", xData, slope_diff);
		return Pt;
	}

}
