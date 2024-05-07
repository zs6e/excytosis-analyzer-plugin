package ExocytosisAnalyzer.detection;

import java.awt.Color;

import ij.gui.Plot;

public class Pettitt {
	
	private double[] yData, xData;
	private int T;
	private double S;
	
	public int K;
	public double[] U;
	public double p;
	
	public Pettitt(double[] signal, double[] time)  {
		yData = signal;
		xData = time;
		T = yData.length;
		U = new double[T];
		S = 0;
		U[0] = 0;

	}
	public Pettitt(double[] signal)  {
		yData = signal;
	 
		T = yData.length;
		xData = new double[T];
		for (int i = 0; i < T; i++) {
			xData[i] =i;
		}
		U = new double[T];
		S = 0;
		U[0] = 0;

	}
	
	public int test() {

		
		//for (int j = 1; j <T; j++) {
		//	Ut[0] += Math.signum(yData[0]-yData[j]) ;
		//}
		
		for (int i = 1; i < T-1; i++ ) {
			S = 0;
			for (int j = i+1; j < T; j++) {
				S += Math.signum(yData[i]-yData[j]) ;
			}
			U[i] = U[i-1] + S;
		}
		
		
		double KT = 0;
		
		for (int i = 0; i < T; i++ ) {
			if (Math.abs(U[i]) > KT) {
				KT = Math.abs(U[i]);
				K =i;
			}
		}
		p = 2.0*Math.exp(-6.0*Math.pow(KT, 2)/(Math.pow(T, 3)+Math.pow(T, 2))); 
		// p<0.05
		if ( p< 0.05 )  
			return K;
		else
			return 0;
	}
	public Plot getPlot() {
	
		
		Plot Pt = new Plot("Pettitt","frame","Ut");
		Pt.setColor(Color.RED);
		Pt.add("line", xData, U);
		return Pt;
	}
	
}
