package ExocytosisAnalyzer.detection;

import java.awt.Color;
import java.util.Collections;
import java.util.Vector;

import ij.gui.Plot;

public class Grubbs {
	//In fact this a S-H-ESD test
	private Vector<Double> data = new Vector<Double>() ;
	private double[] G;
	private double[] y;
	private double[] x;
	private int N, K;
	private double critical;
	
	//Defaut p = 0.05
	public double alpha = 0.05;

	public Grubbs(double[] input) {
		this.y = input;
		this.N = input.length;
		G = new double[N];
		x = new double[N];
		for (int i = 0; i<N ;i++)
			this.data.add(input[i]);
	}

	public boolean test() {
		//G value table 0.05 and 0.01
		double[][] G_critical= {
				{0,0,0,1.153,1.463,1.672,1.822,1.938,2.032,2.11,2.176,2.234,2.285,2.331,2.371,2.409,2.443,2.475,2.501,
						2.532,2.557,2.58,2.603,2.624,2.644,2.663,2.681,2.698,2.714,2.73,2.745,2.759,2.773,2.786,2.799,
						2.811,2.823,2.835,2.846,2.857,2.866,2.877,2.887,2.896,2.905,2.914,2.923,2.931,2.94,2.948,2.956,
						2.943,2.971,2.978,2.986,2.992,3,3.006,3.013,3.019,3.025,3.032,3.037,3.044,3.049,3.055,3.061,
						3.066,3.071,3.076,3.082,3.087,3.092,3.098,3.102,3.107,3.111,3.117,3.121,3.125,3.13,3.134,3.139,
						3.143,3.147,3.151,3.155,3.16,3.163,3.167,3.171,3.174,3.179,3.182,3.186,3.189,3.193,3.196,3.201,
						3.204,3.207,3.21},
				{0,0,0,1.155,1.492,1.749,1.944,2.097,2.22,2.323,2.41,2.485,2.55,2.607,2.659,2.705,2.747,2.785,2.821,
						2.954,2.884,2.912,2.939,2.963,2.987,3.009,3.029,3.049,3.068,3.085,3.103,3.119,3.135,3.15,3.164,
						3.178,3.191,3.204,3.216,3.228,3.24,3.251,3.261,3.271,3.282,3.292,3.302,3.31,3.319,3.329,3.336,
						3.345,3.353,3.361,3.388,3.376,3.383,3.391,3.397,3.405,3.411,3.418,3.424,3.43,3.437,3.442,3.449,
						3.454,3.46,3.466,3.471,3.476,3.482,3.487,3.492,3.496,3.502,3.507,3.511,3.516,3.521,3.525,3.529,
						3.534,3.539,3.543,3.547,3.551,3.555,3.559,3.563,3.567,3.57,3.575,3.579,3.582,3.586,3.589,3.593,
						3.597,3.6,3.65}};
		

		Vector<Double> res =new Vector<Double>();

		//Minimal 3 Nbrs
		if (N < 3) {
			return false;
		}
		/***
		double mean = 0;   
		double mean2 = 0;   
		double var; 

		double sigma;
		for(Double d: data){
			mean+=d;
			mean2+=d*d;
		}
		mean /= (double)N;        
		mean2/= (double)N;
		var =Math.sqrt(mean2-mean*mean);
		***/
		double median = getMedian(data);
		
		for (double d : data) {
			res.add(Math.abs(d - median));
		}

		double MAD = getMedian(res)/0.6745;
		
		K = 0;
		double max_G = 0;
		for (int i = 0; i < N; i++) {
			G[i] = (y[i]-median)/MAD;
			x[i] = i;
			if (max_G < Math.abs(G[i])) {
				max_G = Math.abs(G[i]);
				K = i;
			}
		}
		
		
		int n = N-1;
		if (N>100) n=100;

		if (alpha <= 0.05 && alpha > 0.01) critical = G_critical[0][n];
		else if (alpha <= 0.01) critical = G_critical[1][n];
		else critical = Double.POSITIVE_INFINITY;
		
		if (max_G > critical)
			return true;
		else
			return false;

	}
	private double getMedian(Vector<Double> data) {
		Vector<Double> input = data;
		Collections.sort(input);
		int nbr = input.size();
	    if(nbr % 2 == 1){
	    	return input.get((nbr-1)/2);
	    }else {
	    	return (input.get(nbr/2-1) + input.get(nbr/2) )/2.0;
	    }
	}
	public Plot getPlot() {
		Plot Gplot = new Plot("S-H-ESD","frame","G");
		Gplot.setColor(Color.BLACK);
		Gplot.add("line", x, G);
		Gplot.setColor(Color.RED);
		Gplot.drawLine(x[0], critical, x[N-1], critical);
		Gplot.drawLine(x[0], -critical, x[N-1], -critical);
		return Gplot;
	}
	public int getK() {
		return K;
	}


}
